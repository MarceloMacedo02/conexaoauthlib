package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Duration;

/**
 * Propriedades de configuração de retry.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class RetryProperties {
    
    /**
     * Habilita/desabilita lógica de retry.
     */
    private boolean enabled = true;
    
    /**
     * Número máximo de tentativas de retry.
     */
    @Min(value = 1, message = "Máximo de tentativas deve ser pelo menos 1")
    @Max(value = 10, message = "Máximo de tentativas não deve exceder 10")
    private int maxAttempts = 3;
    
    /**
     * Intervalo inicial entre tentativas.
     */
    private Duration initialInterval = Duration.ofMillis(1000);
    
    /**
     * Multiplicador para backoff exponencial.
     */
    @Min(value = 1, message = "Multiplicador deve ser pelo menos 1.0")
    @Max(value = 10, message = "Multiplicador não deve exceder 10.0")
    private double multiplier = 2.0;
    
    /**
     * Intervalo máximo entre tentativas.
     */
    private Duration maxInterval = Duration.ofSeconds(10);
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    public Duration getInitialInterval() {
        return initialInterval;
    }
    
    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    
    public Duration getMaxInterval() {
        return maxInterval;
    }
    
    public void setMaxInterval(Duration maxInterval) {
        this.maxInterval = maxInterval;
    }
}