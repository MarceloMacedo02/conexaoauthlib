package br.com.conexaoautolib.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Implementação personalizada de Retryer para OpenFeign com backoff exponencial.
 * 
 * Implementa lógica de retry baseada nas regras de negócio do ConexãoAuth:
 * - Retry para erros 5xx (server errors) e 408 (request timeout)
 * - Sem retry para erros 4xx (client errors) exceto 408
 * - Backoff exponencial configurável
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Slf4j
public class ConexaoAuthRetryer implements Retryer {
    
    private final int maxAttempts;
    private final long initialInterval;
    private final double multiplier;
    private final long maxInterval;
    
    private int attempt;
    private long currentTimeForNextRetry;
    
    /**
     * Construtor para ConexaoAuthRetryer.
     * 
     * @param maxAttempts número máximo de tentativas
     * @param initialInterval intervalo inicial em milissegundos
     * @param multiplier multiplicador para backoff exponencial
     * @param maxInterval intervalo máximo em milissegundos
     */
    public ConexaoAuthRetryer(int maxAttempts, long initialInterval, double multiplier, long maxInterval) {
        this.maxAttempts = maxAttempts;
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
        this.attempt = 1;
        this.currentTimeForNextRetry = System.currentTimeMillis();
        
        log.debug("ConexaoAuthRetryer criado com maxAttempts: {}, initialInterval: {}ms, multiplier: {}, maxInterval: {}ms",
            maxAttempts, initialInterval, multiplier, maxInterval);
    }
    
    @Override
    public void continueOrPropagate(RetryableException e) {
        // Se excedeu o número máximo de tentativas, propaga a exceção
        if (attempt >= maxAttempts) {
            log.warn("Excedido o número máximo de tentativas ({}) para a requisição", maxAttempts);
            throw e;
        }
        
        // Calcula o intervalo para próxima tentativa usando backoff exponencial
        long nextInterval = calculateNextInterval();
        currentTimeForNextRetry = System.currentTimeMillis() + nextInterval;
        
        attempt++;
        
        log.info("Tentativa {} de {} para a requisição. Próxima tentativa em {}ms", 
            attempt, maxAttempts, nextInterval);
        
        try {
            Thread.sleep(nextInterval);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrompida durante retry", interrupted);
            throw e;
        }
    }
    
    /**
     * Calcula o intervalo para próxima tentativa usando backoff exponencial.
     * 
     * @return intervalo em milissegundos
     */
    private long calculateNextInterval() {
        long interval = (long) (initialInterval * Math.pow(multiplier, attempt - 1));
        return Math.min(interval, maxInterval);
    }
    
    /**
     * Verifica se o status HTTP deve ser retry com base nas regras de negócio.
     * 
     * @param status status HTTP da resposta
     * @return true se deve retry, false caso contrário
     */
    public static boolean shouldRetry(int status) {
        // Retry para erros de servidor (5xx) e timeout (408)
        return status == HttpStatus.REQUEST_TIMEOUT.value() ||
               (status >= HttpStatus.INTERNAL_SERVER_ERROR.value() && status < 600);
    }
    
    @Override
    public Retryer clone() {
        return new ConexaoAuthRetryer(maxAttempts, initialInterval, multiplier, maxInterval);
    }
}