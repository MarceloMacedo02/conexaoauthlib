package br.com.conexaoautolib.config;

import feign.RetryableException;
import feign.Retryer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ConexaoAuthRetryer Tests")
class ConexaoAuthRetryerTest {
    
    private ConexaoAuthRetryer retryer;
    private RetryableException retryableException;
    
    @BeforeEach
    void setUp() {
        // Configuração padrão para testes
        retryer = new ConexaoAuthRetryer(3, 1000, 2.0, 10000);
        retryableException = mock(RetryableException.class);
    }
    
    @Test
    @DisplayName("Deve criar retryer com configurações padrão")
    void shouldCreateRetryerWithDefaultConfigurations() {
        // Act
        ConexaoAuthRetryer newRetryer = new ConexaoAuthRetryer(3, 1000, 2.0, 10000);
        
        // Assert
        assertNotNull(newRetryer);
    }
    
    @Test
    @DisplayName("Deve executar retry para tentativas dentro do limite")
    void shouldRetryWhenWithinAttemptLimit() throws InterruptedException {
        // Arrange & Act
        assertDoesNotThrow(() -> {
            retryer.continueOrPropagate(retryableException);
        });
        
        // Segunda tentativa também deve ser permitida
        assertDoesNotThrow(() -> {
            retryer.continueOrPropagate(retryableException);
        });
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando excede número máximo de tentativas")
    void shouldThrowExceptionWhenExceedingMaxAttempts() {
        // Arrange - Executar 2 tentativas (chegando na 3ª)
        assertDoesNotThrow(() -> retryer.continueOrPropagate(retryableException));
        assertDoesNotThrow(() -> retryer.continueOrPropagate(retryableException));
        
        // Act & Assert - Terceira tentativa deve lançar exceção
        assertThrows(RetryableException.class, () -> {
            retryer.continueOrPropagate(retryableException);
        });
    }
    
    @Test
    @DisplayName("Deve clonar retryer com mesmas configurações")
    void shouldCloneRetryerWithSameConfigurations() {
        // Act
        Retryer clonedRetryer = retryer.clone();
        
        // Assert
        assertNotNull(clonedRetryer);
        assertInstanceOf(ConexaoAuthRetryer.class, clonedRetryer);
    }
    
    @Test
    @DisplayName("Deve identificar status que devem ser retry")
    void shouldIdentifyRetryableStatusCodes() {
        // Assert
        assertTrue(ConexaoAuthRetryer.shouldRetry(500));
        assertTrue(ConexaoAuthRetryer.shouldRetry(502));
        assertTrue(ConexaoAuthRetryer.shouldRetry(503));
        assertTrue(ConexaoAuthRetryer.shouldRetry(504));
        assertTrue(ConexaoAuthRetryer.shouldRetry(408));
        
        assertFalse(ConexaoAuthRetryer.shouldRetry(400));
        assertFalse(ConexaoAuthRetryer.shouldRetry(401));
        assertFalse(ConexaoAuthRetryer.shouldRetry(403));
        assertFalse(ConexaoAuthRetryer.shouldRetry(404));
        assertFalse(ConexaoAuthRetryer.shouldRetry(200));
    }
    
    @Test
    @DisplayName("Deve calcular intervalo de backoff exponencial corretamente")
    void shouldCalculateExponentialBackoffCorrectly() throws InterruptedException {
        // Arrange
        long startTime = System.currentTimeMillis();
        ConexaoAuthRetryer testRetryer = new ConexaoAuthRetryer(3, 100, 2.0, 500);
        
        // Act - Primeira tentativa (deve esperar 100ms)
        testRetryer.continueOrPropagate(retryableException);
        long firstAttemptTime = System.currentTimeMillis() - startTime;
        
        // Segunda tentativa (deve esperar 200ms)
        testRetryer.continueOrPropagate(retryableException);
        long secondAttemptTime = System.currentTimeMillis() - startTime;
        
        // Assert
        assertTrue(firstAttemptTime >= 100, "Primeira tentativa deve esperar pelo menos 100ms");
        assertTrue(secondAttemptTime >= 300, "Tempo total deve ser pelo menos 300ms (100+200)");
    }
}