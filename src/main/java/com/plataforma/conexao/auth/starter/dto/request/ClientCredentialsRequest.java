package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
@AllArgsConstructor
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

}