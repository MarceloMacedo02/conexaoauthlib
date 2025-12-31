package com.conexaoauthlib.dto.user;

import com.conexaoauthlib.dto.common.RoleSummaryDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta para operações de Usuário.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserResponseDTO user = userClient.getById("user-id");
 * String name = user.getName();
 * String email = user.getEmail();
 * String status = user.getStatus();
 * List<RoleSummaryDTO> roles = user.getRoles();
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
public class UserResponseDTO {

    /**
     * Identificador único do usuário.
     */
    private String id;

    /**
     * Nome completo do usuário.
     */
    private String name;

    /**
     * Email do usuário.
     */
    private String email;

    /**
     * Tenant ao qual o usuário pertence.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Status atual do usuário.
     * Valores possíveis: ACTIVE, INACTIVE, LOCKED, PENDING_VERIFICATION, SUSPENDED
     */
    private String status;

    /**
     * Data de criação do usuário.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Data da última atualização do usuário.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Data do último login do usuário.
     */
    @JsonProperty("last_login_at")
    private String lastLoginAt;

    /**
     * Data de expiração da conta (para contas temporárias).
     */
    @JsonProperty("expires_at")
    private String expiresAt;

    /**
     * Roles atribuídas ao usuário.
     */
    private List<RoleSummaryDTO> roles;

    /**
     * Quantidade de tentativas de login falhas.
     */
    @JsonProperty("failed_login_attempts")
    private Integer failedLoginAttempts;

    /**
     * Data do último password change.
     */
    @JsonProperty("password_changed_at")
    private String passwordChangedAt;
}
