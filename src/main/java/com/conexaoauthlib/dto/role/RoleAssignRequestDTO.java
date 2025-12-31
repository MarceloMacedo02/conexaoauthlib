package com.conexaoauthlib.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para atribuição de roles a um usuário ou client.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RoleAssignRequestDTO request = RoleAssignRequestDTO.builder()
 *     .roleIds(List.of("role-1", "role-2", "role-3"))
 *     .build();
 * userClient.assignRoles("user-id", request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignRequestDTO {

    /**
     * Lista de IDs de roles a serem atribuídas.
     */
    @NotEmpty(message = "Pelo menos uma role deve ser informada")
    @JsonProperty("role_ids")
    private List<String> roleIds;
}
