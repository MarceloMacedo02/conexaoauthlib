package com.conexaoauthlib.dto.role;

import com.conexaoauthlib.dto.common.ScopeSummaryDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta para operações de Role.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RoleResponseDTO role = roleClient.getById("role-id");
 * String name = role.getName();
 * String status = role.getStatus();
 * List<ScopeSummaryDTO> scopes = role.getScopes();
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
public class RoleResponseDTO {

    /**
     * Identificador único da role.
     */
    private String id;

    /**
     * Nome da role.
     */
    private String name;

    /**
     * Descrição da role.
     */
    private String description;

    /**
     * Status atual da role.
     * Valores possíveis: ACTIVE, INACTIVE
     */
    private String status;

    /**
     * Tenant ao qual a role pertence.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Data de criação da role.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Data da última atualização da role.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Scopes atribuídos à role.
     */
    private List<ScopeSummaryDTO> scopes;

    /**
     * Número de usuários com esta role.
     */
    @JsonProperty("user_count")
    private Integer userCount;

    /**
     * Número de clientes com esta role.
     */
    @JsonProperty("client_count")
    private Integer clientCount;
}
