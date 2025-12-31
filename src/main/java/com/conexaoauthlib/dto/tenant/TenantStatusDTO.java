package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de status de Tenant.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TenantStatusDTO statusUpdate = TenantStatusDTO.builder()
 *     .status("INACTIVE")
 *     .build();
 * tenantClient.updateStatus("tenant-id", statusUpdate);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantStatusDTO {

    /**
     * Novo status do tenant.
     * Valores possíveis: ACTIVE, INACTIVE, SUSPENDED
     */
    @NotBlank(message = "Status é obrigatório")
    private String status;

    /**
     * Motivo da alteração de status (opcional).
     */
    @JsonProperty("reason")
    private String reason;
}
