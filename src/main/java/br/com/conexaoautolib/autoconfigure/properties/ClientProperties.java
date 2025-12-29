package br.com.conexaoautolib.autoconfigure.properties;

import jakarta.validation.constraints.NotBlank;

/**
 * Propriedades de configuração para clientes específicos.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ClientProperties {
    
    /**
     * Client ID OAuth2.
     */
    @NotBlank(message = "Client ID não pode ser vazio")
    private String clientId;
    
    /**
     * Client Secret OAuth2.
     */
    @NotBlank(message = "Client Secret não pode ser vazio")
    private String clientSecret;
    
    /**
     * Realm associado ao cliente.
     */
    @NotBlank(message = "Realm não pode ser vazio")
    private String realm;
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getRealm() {
        return realm;
    }
    
    public void setRealm(String realm) {
        this.realm = realm;
    }
}