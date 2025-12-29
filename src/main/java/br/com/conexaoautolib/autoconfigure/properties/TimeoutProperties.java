package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

/**
 * Propriedades de configuração de timeout.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class TimeoutProperties {
    
    /**
     * Timeout de conexão HTTP.
     */
    @NotNull(message = "Timeout de conexão não pode ser nulo")
    private Duration connect = Duration.ofSeconds(5);
    
    /**
     * Timeout de leitura HTTP.
     */
    @NotNull(message = "Timeout de leitura não pode ser nulo")
    private Duration read = Duration.ofSeconds(30);
    
    public Duration getConnect() {
        return connect;
    }
    
    public void setConnect(Duration connect) {
        this.connect = connect;
    }
    
    public Duration getRead() {
        return read;
    }
    
    public void setRead(Duration read) {
        this.read = read;
    }
}