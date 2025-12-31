package com.conexaoauthlib.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para criação de um novo Client OAuth2 (M2M).
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientCreateRequestDTO client = ClientCreateRequestDTO.builder()
 *     .clientId("my-service")
 *     .clientSecret("super-secret-key-12345")
 *     .name("My Service Client")
 *     .tenantId("tenant-123")
 *     .grantTypes(List.of("client_credentials", "refresh_token"))
 *     .scopes(List.of("read", "write"))
 *     .accessTokenValiditySeconds(3600)
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
public class ClientCreateRequestDTO {

    /**
     * Identificador único do cliente.
     * Deve ser único dentro do tenant.
     */
    @NotBlank(message = "Client ID é obrigatório")
    @Size(min = 5, max = 100, message = "Client ID deve ter entre 5 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$",
             message = "Client ID deve conter apenas letras, números, hífen e underscore")
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Segredo do cliente para autenticação.
     * Deve ser forte (mínimo 16 caracteres).
     */
    @NotBlank(message = "Client secret é obrigatório")
    @Size(min = 16, max = 200, message = "Client secret deve ter entre 16 e 200 caracteres")
    @JsonProperty("client_secret")
    private String clientSecret;

    /**
     * Nome amigável do cliente.
     */
    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String name;

    /**
     * Identificador do tenant ao qual o cliente pertence.
     */
    @NotBlank(message = "Tenant ID é obrigatório")
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Tipos de grant permitidos para este cliente.
     * Valores válidos: client_credentials, password, refresh_token
     */
    @NotEmpty(message = "Ao menos um grant type é obrigatório")
    @JsonProperty("grant_types")
    private List<String> grantTypes;

    /**
     * Escopos padrão atribuídos ao cliente.
     */
    @Builder.Default
    private List<String> scopes = new ArrayList<>();

    /**
     * Tempo de validade do access token em segundos.
     * Se null, usa o valor padrão do servidor.
     */
    @JsonProperty("access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    /**
     * Tempo de validade do refresh token em segundos.
     * Se null, usa o valor padrão do servidor.
     */
    @JsonProperty("refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

    /**
     * Descrição opcional do cliente.
     */
    private String description;

    /**
     * Redirect URIs (para grant type authorization_code).
     */
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;
}
