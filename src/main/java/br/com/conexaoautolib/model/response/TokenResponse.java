package br.com.conexaoautolib.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de token OAuth2.
 * Segue padrão RFC 6749 para resposta de autenticação.
 * 
 * @param accessToken Token de acesso JWT
 * @param tokenType Tipo do token (sempre "Bearer")
 * @param expiresIn Tempo de expiração em segundos
 * @param refreshToken Token de refresh (opcional)
 * @param scope Escopos concedidos (opcional)
 */
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    // Builder method
    public static TokenResponseBuilder builder() {
        return new TokenResponseBuilder();
    }

    public static class TokenResponseBuilder {
        private String accessToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private String refreshToken;
        private String scope;

        public TokenResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public TokenResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public TokenResponseBuilder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public TokenResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenResponseBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public TokenResponse build() {
            TokenResponse response = new TokenResponse();
            response.accessToken = this.accessToken;
            response.tokenType = this.tokenType;
            response.expiresIn = this.expiresIn;
            response.refreshToken = this.refreshToken;
            response.scope = this.scope;
            return response;
        }
    }

    /**
     * Calcula a data/hora de expiração do token baseado no expiresIn.
     * 
     * @return LocalDateTime com a data/hora de expiração, ou null se expiresIn for nulo
     */
    @JsonIgnore
    public LocalDateTime getExpiresAt() {
        if (expiresIn == null) {
            return null;
        }
        return LocalDateTime.now().plusSeconds(expiresIn);
    }

    /**
     * Verifica se o token está expirado.
     * 
     * @return true se o token estiver expirado, false caso contrário
     */
    @JsonIgnore
    public boolean isExpired() {
        if (expiresIn == null) {
            return false;
        }
        return getExpiresAt().isBefore(LocalDateTime.now());
    }

    /**
     * Sobrescrita de toString() para evitar exposição de tokens sensíveis.
     * Redata campos que contêm tokens de autenticação.
     * 
     * @return Representação string segura sem dados sensíveis
     */
    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='[REDACTED]" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='[REDACTED]" +
                ", scope='" + scope + '\'' +
                ", expiresAt=" + (getExpiresAt() != null ? getExpiresAt().toString() : "null") +
                ", expired=" + isExpired() +
                '}';
    }
}