package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.Min;

import java.time.Duration;

/**
 * Propriedades de configuração de circuit breaker.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class CircuitBreakerProperties {
    
    /**
     * Habilita/desabilita circuit breaker.
     */
    private boolean enabled = false;
    
    /**
     * Limite de falhas para abrir o circuito.
     */
    @Min(value = 1, message = "Limite de falhas deve ser pelo menos 1")
    private int failureThreshold = 5;
    
    /**
     * Timeout em segundos para tentativas.
     */
    private Duration timeout = Duration.ofSeconds(30);
    
    /**
     * Máximo de chamadas em estado half-open.
     */
    @Min(value = 1, message = "Máximo em half-open deve ser pelo menos 1")
    private int halfOpenMaxCalls = 3;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }
    
    public Duration getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
    
    public int getHalfOpenMaxCalls() {
        return halfOpenMaxCalls;
    }
    
    public void setHalfOpenMaxCalls(int halfOpenMaxCalls) {
        this.halfOpenMaxCalls = halfOpenMaxCalls;
    }
}