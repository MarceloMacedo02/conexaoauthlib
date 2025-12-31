package com.conexaoauthlib.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de uma nova Role.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RoleCreateRequestDTO role = RoleCreateRequestDTO.builder()
 *     .name("admin")
 *     .description("Administrador do sistema com acesso total")
 *     .tenantId("tenant-123")
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
public class RoleCreateRequestDTO {

    /**
     * Nome único da role dentro do tenant.
     */
    @NotBlank(message = "Nome da role é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "Nome deve conter apenas letras, números e underscore")
    private String name;

    /**
     * Descrição da role e suas permissões.
     */
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    /**
     * Identificador do tenant ao qual a role pertence.
     */
    @NotBlank(message = "Tenant ID é obrigatório")
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Lista de IDs de scopes a serem atribuídos inicialmente.
     */
    @JsonProperty("scope_ids")
    private java.util.List<String> scopeIds;
}
