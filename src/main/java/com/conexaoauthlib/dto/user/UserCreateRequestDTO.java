package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de um novo Usuário.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserCreateRequestDTO user = UserCreateRequestDTO.builder()
 *     .name("João da Silva")
 *     .email("joao.silva@empresa.com")
 *     .password("SenhaForte123!")
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
public class UserCreateRequestDTO {

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String name;

    /**
     * Email do usuário.
     * Deve ser único dentro do tenant.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    /**
     * Senha do usuário.
     * Deve atender aos requisitos de complexidade:
     * - Mínimo 8 caracteres
     * - Pelo menos 1 letra maiúscula
     * - Pelo menos 1 letra minúscula
     * - Pelo menos 1 número
     * - Pelo menos 1 caractere especial (@$!%*?&)
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
             message = "Senha deve conter: maiúscula, minúscula, número e caractere especial")
    private String password;

    /**
     * Identificador do tenant ao qual o usuário pertence.
     */
    @NotBlank(message = "Tenant ID é obrigatório")
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * ID da role padrão a ser atribuída ao usuário.
     */
    @JsonProperty("default_role_id")
    private String defaultRoleId;

    /**
     * ID do cliente (M2M) associado ao usuário, se aplicável.
     */
    @JsonProperty("client_id")
    private String clientId;
}
