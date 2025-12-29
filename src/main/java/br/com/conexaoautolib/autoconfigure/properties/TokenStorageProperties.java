package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.Min;
import java.time.Duration;

/**
 * Propriedades de configuração de armazenamento de token.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class TokenStorageProperties {
    
    /**
     * Tipo de armazenamento de token (memoria, redis, etc.).
     */
    private String type = "memory";
    
    /**
     * TTL (Time To Live) do token em cache.
     */
    private Duration ttl = Duration.ofMinutes(55); // 55 minutos padrão
    
    /**
     * Janela de refresh para evitar expiração.
     */
    private Duration refreshWindow = Duration.ofMinutes(5); // 5 minutos padrão
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Duration getTtl() {
        return ttl;
    }
    
    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }
    
    public Duration getRefreshWindow() {
        return refreshWindow;
    }
    
    public void setRefreshWindow(Duration refreshWindow) {
        this.refreshWindow = refreshWindow;
    }
}