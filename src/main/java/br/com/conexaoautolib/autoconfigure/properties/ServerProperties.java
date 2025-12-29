package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.NotBlank;

import java.time.Duration;

/**
 * Propriedades de configuração do servidor ConexãoAuth.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ServerProperties {
    
    /**
     * URL base do servidor ConexãoAuth.
     */
    @NotBlank(message = "URL do servidor não pode ser vazia")
    private String url = "http://localhost:8080";
    
    /**
     * Versão da API a ser utilizada.
     */
    @NotBlank(message = "Versão da API não pode ser vazia")
    private String apiVersion = "v1";
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}