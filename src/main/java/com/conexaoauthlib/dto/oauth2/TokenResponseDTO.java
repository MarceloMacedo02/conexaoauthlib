package com.conexaoauthlib.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta de token OAuth2.
 * Contém o access token e metadados associados.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TokenResponseDTO response = oauth2Client.createToken(request);
 * String accessToken = response.getAccessToken();
 * Integer expiresIn = response.getExpiresIn();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseDTO {

    /**
     * Access token JWT emitido pelo servidor.
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Tipo do token, tipicamente "Bearer".
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Tempo em segundos até a expiração do token.
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * Refresh token para obtenção de novos access tokens.
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Escopos concedidos, espaço-separados.
     */
    private String scope;

    /**
     * JWT ID único para o token.
     */
    private String jti;

    // Claims adicionais do token (para introspect)

    /**
     * Indica se o token está ativo e válido.
     */
    private Boolean active;

    /**
     * Identificador do cliente que solicitou o token.
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Subject (usuário) do token.
     */
    private String sub;

    /**
     * Audience do token.
     */
    private String aud;

    /**
     * Issuer do token.
     */
    private String iss;

    /**
     * Expiration time (Unix timestamp em segundos).
     */
    private Long exp;

    /**
     * Issued at (Unix timestamp em segundos).
     */
    private Long iat;

    /**
     * Not before (Unix timestamp em segundos).
     */
    private Long nbf;

    /**
     * ID do tenant associado ao token.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * ID do usuário associado ao token.
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * Email do usuário associado ao token.
     */
    private String email;

    /**
     * Escopos concedidos ao token como lista.
     */
    private List<String> scopes;

    /**
     * Papéis atribuídos ao usuário.
     */
    private List<String> roles;

    /**
     * Produtos acessíveis com este token.
     */
    private List<String> products;

    /**
     * Role principal do usuário (claim role único).
     */
    private String role;

    /**
     * Tipo do token (claim token_type).
     */
    @JsonProperty("token_type")
    private String tokenTypeClaim;
}
