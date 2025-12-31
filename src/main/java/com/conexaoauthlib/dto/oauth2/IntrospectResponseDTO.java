package com.conexaoauthlib.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta de introspecção de token (RFC 7662).
 * Contém todos os claims do token e seu status de validade.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * IntrospectResponseDTO response = oauth2Client.introspectToken(request);
 * if (Boolean.TRUE.equals(response.getActive())) {
 *     String subject = response.getSub();
 *     List<String> scopes = response.getScopes();
 * }
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7662">RFC 7662</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrospectResponseDTO {

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
     * JWT ID único.
     */
    private String jti;

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
     * Escopos concedidos ao token.
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
     * Tipo do token.
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Nome de usuário.
     */
    private String username;

    /**
     * Tipo de grant utilizado para obter o token.
     */
    @JsonProperty("grant_type")
    private String grantType;
}
