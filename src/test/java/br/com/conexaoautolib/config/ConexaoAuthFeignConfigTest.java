package br.com.conexaoautolib.config;

import br.com.conexaoautolib.autoconfigure.ConexaoAuthProperties;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConexaoAuthFeignConfig Tests")
class ConexaoAuthFeignConfigTest {
    
    @Mock
    private ConexaoAuthProperties properties;
    
    @Mock
    private br.com.conexaoautolib.autoconfigure.properties.TimeoutProperties timeoutProperties;
    
    @Mock
    private br.com.conexaoautolib.autoconfigure.properties.RetryProperties retryProperties;
    
    @Mock
    private br.com.conexaoautolib.autoconfigure.properties.LoggingProperties loggingProperties;
    
    @InjectMocks
    private ConexaoAuthFeignConfig feignConfig;
    
    @BeforeEach
    void setUp() {
        lenient().when(properties.getTimeout()).thenReturn(timeoutProperties);
        lenient().when(properties.getRetry()).thenReturn(retryProperties);
        lenient().when(properties.getLogging()).thenReturn(loggingProperties);
        
        // Configurações padrão
        lenient().when(timeoutProperties.getConnect()).thenReturn(Duration.ofSeconds(5));
        lenient().when(timeoutProperties.getRead()).thenReturn(Duration.ofSeconds(30));
        lenient().when(retryProperties.isEnabled()).thenReturn(true);
        lenient().when(retryProperties.getMaxAttempts()).thenReturn(3);
        lenient().when(retryProperties.getInitialInterval()).thenReturn(Duration.ofMillis(1000));
        lenient().when(retryProperties.getMultiplier()).thenReturn(2.0);
        lenient().when(retryProperties.getMaxInterval()).thenReturn(Duration.ofSeconds(10));
        lenient().when(loggingProperties.isEnabled()).thenReturn(true);
        lenient().when(loggingProperties.getLevel()).thenReturn("BASIC");
    }
    
    @Test
    @DisplayName("Deve criar Request.Options com valores configurados")
    void shouldCreateRequestOptionsWithConfiguredValues() {
        // Act
        Request.Options options = feignConfig.feignRequestOptions();
        
        // Assert
        assertNotNull(options);
        assertEquals(5000, options.connectTimeoutMillis());
        assertEquals(30000, options.readTimeoutMillis());
    }
    
    @Test
    @DisplayName("Deve criar Logger.Level com nível BASIC")
    void shouldCreateLoggerLevelWithBasic() {
        // Act
        Logger.Level level = feignConfig.feignLoggerLevel();
        
        // Assert
        assertNotNull(level);
        assertEquals(Logger.Level.BASIC, level);
    }
    
    @Test
    @DisplayName("Deve criar Logger.Level com nível FULL")
    void shouldCreateLoggerLevelWithFull() {
        // Arrange
        when(loggingProperties.getLevel()).thenReturn("FULL");
        
        // Act
        Logger.Level level = feignConfig.feignLoggerLevel();
        
        // Assert
        assertNotNull(level);
        assertEquals(Logger.Level.FULL, level);
    }
    
    @Test
    @DisplayName("Deve criar Logger.Level padrão quando nível inválido")
    void shouldCreateDefaultLoggerLevelWhenInvalid() {
        // Arrange
        when(loggingProperties.getLevel()).thenReturn("INVALID");
        
        // Act
        Logger.Level level = feignConfig.feignLoggerLevel();
        
        // Assert
        assertNotNull(level);
        assertEquals(Logger.Level.BASIC, level);
    }
    
    @Test
    @DisplayName("Deve criar Retryer com configurações padrão")
    void shouldCreateRetryerWithDefaultConfigurations() {
        // Act
        Retryer retryer = feignConfig.feignRetryer();
        
        // Assert
        assertNotNull(retryer);
        assertInstanceOf(ConexaoAuthRetryer.class, retryer);
        
        // Verificar se o retryer foi configurado corretamente via Reflection
        ConexaoAuthRetryer conexaoAuthRetryer = (ConexaoAuthRetryer) retryer;
        Integer maxAttempts = (Integer) ReflectionTestUtils.getField(conexaoAuthRetryer, "maxAttempts");
        assertEquals(3, maxAttempts);
    }
    
    @Test
    @DisplayName("Deve criar ErrorDecoder personalizado")
    void shouldCreateCustomErrorDecoder() {
        // Act
        ErrorDecoder errorDecoder = feignConfig.feignErrorDecoder();
        
        // Assert
        assertNotNull(errorDecoder);
        assertInstanceOf(ConexaoAuthErrorDecoder.class, errorDecoder);
    }
}