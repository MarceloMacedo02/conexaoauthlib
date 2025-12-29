package br.com.conexaoautolib.autoconfigure;

import br.com.conexaoautolib.autoconfigure.properties.*;
import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de configuração de propriedades para ConexãoAuthLib.
 * 
 * Mapeia todas as propriedades prefixadas com "conexaoauth" do application.yml
 * para esta classe, permitindo configuração externa do comportamento da biblioteca.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "conexaoauth")
@Validated
public class ConexaoAuthProperties {
    
    /**
     * Habilita/desabilita a biblioteca ConexãoAuthLib.
     */
    private boolean enabled = true;
    
    /**
     * Configurações do servidor ConexãoAuth.
     */
    @Valid
    private ServerProperties server = new ServerProperties();
    
    /**
     * Configurações de autenticação padrão.
     */
    @Valid
    private AuthProperties auth = new AuthProperties();
    
    /**
     * Configurações de timeout.
     */
    @Valid
    private TimeoutProperties timeout = new TimeoutProperties();
    
    /**
     * Configurações de retry.
     */
    @Valid
    private RetryProperties retry = new RetryProperties();
    
    /**
     * Configurações de logging.
     */
    @Valid
    private LoggingProperties logging = new LoggingProperties();
    
    /**
     * Configurações de armazenamento de token.
     */
    @Valid
    private TokenStorageProperties tokenStorage = new TokenStorageProperties();
    
    /**
     * Configurações de circuit breaker.
     */
    @Valid
    private CircuitBreakerProperties circuitBreaker = new CircuitBreakerProperties();
    
    /**
     * Configurações de health check.
     */
    @Valid
    private HealthProperties health = new HealthProperties();
    
    /**
     * Configurações de clientes específicos (por nome).
     */
    @Valid
    private Map<String, ClientProperties> clients = new HashMap<>();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public ServerProperties getServer() {
        return server;
    }
    
    public void setServer(ServerProperties server) {
        this.server = server;
    }
    
    public AuthProperties getAuth() {
        return auth;
    }
    
    public void setAuth(AuthProperties auth) {
        this.auth = auth;
    }
    
    public TimeoutProperties getTimeout() {
        return timeout;
    }
    
    public void setTimeout(TimeoutProperties timeout) {
        this.timeout = timeout;
    }
    
    public RetryProperties getRetry() {
        return retry;
    }
    
    public void setRetry(RetryProperties retry) {
        this.retry = retry;
    }
    
    public LoggingProperties getLogging() {
        return logging;
    }
    
    public void setLogging(LoggingProperties logging) {
        this.logging = logging;
    }
    
    public TokenStorageProperties getTokenStorage() {
        return tokenStorage;
    }
    
    public void setTokenStorage(TokenStorageProperties tokenStorage) {
        this.tokenStorage = tokenStorage;
    }
    
    public CircuitBreakerProperties getCircuitBreaker() {
        return circuitBreaker;
    }
    
    public void setCircuitBreaker(CircuitBreakerProperties circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }
    
    public Map<String, ClientProperties> getClients() {
        return clients;
    }
    
    public void setClients(Map<String, ClientProperties> clients) {
        this.clients = clients;
    }
    
    public HealthProperties getHealth() {
        return health;
    }
    
    public void setHealth(HealthProperties health) {
        this.health = health;
    }
}