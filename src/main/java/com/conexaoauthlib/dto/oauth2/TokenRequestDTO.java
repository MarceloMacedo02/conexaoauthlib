package com.conexaoauthlib.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de token OAuth2.
 * Suporta todos os grant types: client_credentials, password, refresh_token.
 *
 * <p>Exemplo de uso com client_credentials:</p>
 * <pre>{@code
 * TokenRequestDTO request = TokenRequestDTO.builder()
 *     .grantType("client_credentials")
 *     .clientId("my-client-id")
 *     .clientSecret("my-client-secret")
 *     .scope("read write admin")
 *     .build();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDTO {

    /**
     * Tipo de grant solicitado.
     * Valores válidos: "client_credentials", "password", "refresh_token"
     */
    @NotBlank(message = "Grant type é obrigatório")
    @Pattern(regexp = "client_credentials|password|refresh_token",
             message = "Grant type deve ser: client_credentials, password ou refresh_token")
    @JsonProperty("grant_type")
    private String grantType;

    /**
     * Identificador do cliente OAuth2.
     */
    @NotBlank(message = "Client ID é obrigatório")
    @Size(max = 255, message = "Client ID deve ter no máximo 255 caracteres")
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Segredo do cliente OAuth2.
     * Obrigatório para grant_type client_credentials.
     */
    @Size(max = 500, message = "Client secret deve ter no máximo 500 caracteres")
    @JsonProperty("client_secret")
    private String clientSecret;

    /**
     * Nome de usuário para grant password.
     */
    @Size(max = 255, message = "Username deve ter no máximo 255 caracteres")
    private String username;

    /**
     * Senha do usuário para grant password.
     */
    @Size(max = 255, message = "Password deve ter no máximo 255 caracteres")
    private String password;

    /**
     * Refresh token para grant refresh_token.
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Escopos solicitados, espaço-separados.
     * Exemplo: "read write admin"
     */
    @Size(max = 1000, message = "Scope deve ter no máximo 1000 caracteres")
    private String scope;

    /**
     * ID do tenant para operações multi-tenant.
     */
    @JsonProperty("tenant_id")
    private String tenantId;
}
