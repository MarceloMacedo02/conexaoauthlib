package com.conexaoauthlib.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de status de Client OAuth2.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientStatusDTO statusUpdate = ClientStatusDTO.builder()
 *     .status("SUSPENDED")
 *     .build();
 * clientClient.updateStatus("client-id", statusUpdate);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatusDTO {

    /**
     * Novo status do cliente.
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
