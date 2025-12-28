package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitação de token via Client Credentials Flow.
 *
 * <p>
 * Este DTO é utilizado para obter um token de acesso no fluxo
 * Client Credentials do OAuth2, onde o cliente (aplicação) se autentica
 * diretamente usando client_id e client_secret.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ClientCredentialsRequest {

    /**
     * Tipo de grant: client_credentials, refresh_token, etc.
     */
    @NotBlank(message = "grant_type é obrigatório")
    private String grantType;

    /**
     * Client ID para autenticação do cliente OAuth2.
     */
    @NotBlank(message = "client_id é obrigatório")
    private String clientId;

    /**
     * Client Secret para autenticação do cliente OAuth2.
     */
    @NotBlank(message = "client_secret é obrigatório")
    private String clientSecret;

    /**
     * Código de autorização (para grant_type=authorization_code).
     */
    private String code;

    /**
     * URI de redirecionamento (para grant_type=authorization_code).
     */
    private String redirectUri;

    /**
     * Refresh token (para grant_type=refresh_token).
     */
    private String refreshToken;

    /**
     * Username (para grant_type=password, se suportado).
     */
    private String username;

    /**
     * Password (para grant_type=password, se suportado).
     */
    private String password;

    /**
     * Scope solicitado.
     */
    private String scope;

    /**
     * Verifier PKCE (para clientes públicos com PKCE).
     */
    private String codeVerifier;

    public ClientCredentialsRequest() {
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
}
