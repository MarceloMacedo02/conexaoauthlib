package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de status de Usuário.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserStatusDTO statusUpdate = UserStatusDTO.builder()
 *     .status("SUSPENDED")
 *     .reason("Pagamento pendente")
 *     .build();
 * userClient.updateStatus("user-id", statusUpdate);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {

    /**
     * Novo status do usuário.
     * Valores possíveis: ACTIVE, INACTIVE, SUSPENDED, LOCKED
     */
    @NotBlank(message = "Status é obrigatório")
    private String status;

    /**
     * Motivo da mudança de status (opcional, para auditoria).
     */
    @JsonProperty("reason")
    private String reason;
}
