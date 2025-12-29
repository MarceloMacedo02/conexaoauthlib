package br.com.conexaoautolib.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurações de health check para ConexãoAuthLib.
 * 
 * Permite configurar timeouts, tentativas de retry e endpoints
 * para verificação de saúde dos componentes da biblioteca.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "conexaoauth.health")
public class HealthProperties {
    
    /**
     * Habilita/desabilita os health checks.
     */
    private boolean enabled = true;
    
    /**
     * Timeout para verificações de saúde.
     */
    @NotNull
    private Duration timeout = Duration.ofSeconds(5);
    
    /**
     * Número máximo de tentativas de retry para health checks.
     */
    @Min(0)
    @Max(10)
    private int retryAttempts = 3;
    
    /**
     * Endpoints para testar conectividade com servidor ConexãoAuth.
     */
    @NotNull
    private List<@NotBlank String> endpoints = new ArrayList<>();
    
    /**
     * Verifica saúde do armazenamento de tokens.
     */
    private boolean checkTokenStorage = true;
    
    /**
     * Verifica conectividade com servidor ConexãoAuth.
     */
    private boolean checkServerConnectivity = true;
    
    public HealthProperties() {
        // Endpoints padrão para verificação de saúde
        this.endpoints.add("/health");
        this.endpoints.add("/actuator/health");
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Duration getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
    
    public int getRetryAttempts() {
        return retryAttempts;
    }
    
    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }
    
    public List<String> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
    
    public boolean isCheckTokenStorage() {
        return checkTokenStorage;
    }
    
    public void setCheckTokenStorage(boolean checkTokenStorage) {
        this.checkTokenStorage = checkTokenStorage;
    }
    
    public boolean isCheckServerConnectivity() {
        return checkServerConnectivity;
    }
    
    public void setCheckServerConnectivity(boolean checkServerConnectivity) {
        this.checkServerConnectivity = checkServerConnectivity;
    }
}