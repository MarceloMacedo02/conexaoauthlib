package com.conexaoauthlib.dto.client;

import com.conexaoauthlib.dto.common.RoleSummaryDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta para operações de Client OAuth2.
 *
 * <p>Segurança: O campo clientSecret é ignorado na serialização
 * para prevenir vazamento acidental de credenciais.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientResponseDTO client = clientClient.getById("client-id");
 * String clientId = client.getClientId();
 * String status = client.getStatus();
 * List<String> grantTypes = client.getGrantTypes();
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
public class ClientResponseDTO {

    /**
     * Identificador interno único do cliente.
     */
    private String id;

    /**
     * Identificador público do cliente.
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Nome do cliente.
     */
    private String name;

    /**
     * Descrição do cliente.
     */
    private String description;

    /**
     * Tenant ao qual o cliente pertence.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Status atual do cliente.
     * Valores possíveis: ACTIVE, INACTIVE, SUSPENDED, PENDING_SECRET
     */
    private String status;

    /**
     * Grant types permitidos.
     */
    @JsonProperty("grant_types")
    private List<String> grantTypes;

    /**
     * Escopos atribuídos ao cliente.
     */
    private List<String> scopes;

    /**
     * Tempo de validade do access token em segundos.
     */
    @JsonProperty("access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    /**
     * Tempo de validade do refresh token em segundos.
     */
    @JsonProperty("refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

    /**
     * Data de criação do cliente.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Data da última atualização do cliente.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Roles atribuídas ao cliente.
     */
    private List<RoleSummaryDTO> roles;

    /**
     * Redirect URIs configurados.
     */
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    /**
     * Campo IGNORADO na serialização para prevenir vazamento de credencial.
     * Este campo nunca deve ser exposto em responses.
     */
    @JsonIgnore
    private String clientSecret;
}
