package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.NotBlank;

/**
 * Propriedades de configuração de autenticação.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class AuthProperties {
    
    /**
     * Client ID OAuth2 padrão.
     */
    private String defaultClientId;
    
    /**
     * Client Secret OAuth2 padrão.
     */
    private String defaultClientSecret;
    
    /**
     * Realm padrão para autenticação.
     */
    @NotBlank(message = "Realm padrão não pode ser vazio")
    private String defaultRealm = "master";
    
    public String getDefaultClientId() {
        return defaultClientId;
    }
    
    public void setDefaultClientId(String defaultClientId) {
        this.defaultClientId = defaultClientId;
    }
    
    public String getDefaultClientSecret() {
        return defaultClientSecret;
    }
    
    public void setDefaultClientSecret(String defaultClientSecret) {
        this.defaultClientSecret = defaultClientSecret;
    }
    
    public String getDefaultRealm() {
        return defaultRealm;
    }
    
    public void setDefaultRealm(String defaultRealm) {
        this.defaultRealm = defaultRealm;
    }
}