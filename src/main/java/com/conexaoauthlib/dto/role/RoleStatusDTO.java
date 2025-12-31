package com.conexaoauthlib.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de status de Role.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RoleStatusDTO statusUpdate = RoleStatusDTO.builder()
 *     .status("INACTIVE")
 *     .reason("Role temporariamente desativada para manutenção")
 *     .build();
 * roleClient.updateStatus("role-id", statusUpdate);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleStatusDTO {

    /**
     * Novo status da role.
     * Valores possíveis: ACTIVE, INACTIVE
     */
    @NotBlank(message = "Status é obrigatório")
    private String status;

    /**
     * Motivo da mudança de status (opcional).
     */
    @JsonProperty("reason")
    private String reason;
}
