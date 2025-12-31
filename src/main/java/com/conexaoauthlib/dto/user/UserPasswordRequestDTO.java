package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para alteração de senha de Usuário.
 *
 * <p>Exige a senha atual para verificação de identidade antes de permitir
 * a alteração. A nova senha deve atender aos requisitos de complexidade.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserPasswordRequestDTO request = UserPasswordRequestDTO.builder()
 *     .currentPassword("SenhaAntiga123!")
 *     .newPassword("NovaSenhaForte456@")
 *     .build();
 * userClient.changePassword("user-id", request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordRequestDTO {

    /**
     * Senha atual do usuário.
     * Necessária para verificação de identidade.
     */
    @NotBlank(message = "Senha atual é obrigatória")
    @JsonProperty("current_password")
    private String currentPassword;

    /**
     * Nova senha do usuário.
     * Deve atender aos requisitos de complexidade:
     * - Mínimo 8 caracteres
     * - Pelo menos 1 letra maiúscula
     * - Pelo menos 1 letra minúscula
     * - Pelo menos 1 número
     * - Pelo menos 1 caractere especial (@$!%*?&)
     */
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
             message = "Nova senha deve conter: maiúscula, minúscula, número e caractere especial")
    @JsonProperty("new_password")
    private String newPassword;
}
