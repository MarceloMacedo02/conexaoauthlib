package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização parcial de Usuário.
 *
 * <p>Campos não definidos permanecem inalterados. Para alterar a senha,
 * utilize {@link UserPasswordRequestDTO}.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserUpdateRequestDTO update = UserUpdateRequestDTO.builder()
 *     .name("João Silva Santos")
 *     .build();
 * userClient.update("user-id", update);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {

    /**
     * Novo nome do usuário (opcional).
     */
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String name;

    /**
     * Novo email do usuário (opcional).
     */
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    /**
     * Novo status do usuário (opcional).
     */
    private String status;

    /**
     * Flag para forçar mudança de senha no próximo login.
     */
    @JsonProperty("force_password_change")
    private Boolean forcePasswordChange;
}
