package br.com.conexaoautolib.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/**
 * DTO para requisição de token OAuth2.
 * Segue padrão RFC 6749 para troca de credenciais por tokens.
 * 
 * @param grantType Tipo de concessão OAuth2 (client_credentials, password, refresh_token)
 * @param clientId Identificador do cliente OAuth2
 * @param clientSecret Chave secreta do cliente OAuth2
 * @param username Nome de usuário (opcional, para grant password)
 * @param password Senha do usuário (opcional, para grant password)
 * @param refreshToken Token de refresh (opcional, para grant refresh_token)
 * @param scope Escopos solicitados (opcional)
 * @param realm Identificador do realm multi-tenant
 */
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public class TokenRequest {

    @NotBlank(message = "Tipo de concessão é obrigatório")
    @JsonProperty("grant_type")
    private String grantType;

    @NotBlank(message = "ID do cliente é obrigatório")
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank(message = "Secret do cliente é obrigatório")
    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @NotBlank(message = "Realm é obrigatório")
    private String realm;

    // Getters and Setters
    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public String getRealm() { return realm; }
    public void setRealm(String realm) { this.realm = realm; }

    // Builder method
    public static TokenRequestBuilder builder() {
        return new TokenRequestBuilder();
    }

    public static class TokenRequestBuilder {
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private String refreshToken;
        private String scope;
        private String realm;

        public TokenRequestBuilder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public TokenRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public TokenRequestBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public TokenRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TokenRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public TokenRequestBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenRequestBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public TokenRequestBuilder realm(String realm) {
            this.realm = realm;
            return this;
        }

        public TokenRequest build() {
            TokenRequest request = new TokenRequest();
            request.grantType = this.grantType;
            request.clientId = this.clientId;
            request.clientSecret = this.clientSecret;
            request.username = this.username;
            request.password = this.password;
            request.refreshToken = this.refreshToken;
            request.scope = this.scope;
            request.realm = this.realm;
            return request;
        }
    }

    /**
     * Sobrescrita de toString() para evitar exposição de credenciais sensíveis.
     * Redata campos que contêm informações de autenticação.
     * 
     * @return Representação string segura sem dados sensíveis
     */
    @Override
    public String toString() {
        return "TokenRequest{" +
                "grantType='" + grantType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='[REDACTED]" +
                ", username='" + username + '\'' +
                ", password='[REDACTED]" +
                ", refreshToken='[REDACTED]" +
                ", scope='" + scope + '\'' +
                ", realm='" + realm + '\'' +
                '}';
    }
}