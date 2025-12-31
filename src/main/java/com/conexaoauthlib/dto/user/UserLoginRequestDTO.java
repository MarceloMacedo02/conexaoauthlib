package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de login.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserLoginRequestDTO login = UserLoginRequestDTO.builder()
 *     .email("joao.silva@empresa.com")
 *     .password("Senha123!")
 *     .tenantId("tenant-123")
 *     .build();
 * TokenResponseDTO token = authClient.login(login);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDTO {

    /**
     * Email do usuário.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    /**
     * Senha do usuário.
     */
    @NotBlank(message = "Senha é obrigatória")
    private String password;

    /**
     * Tenant ID para multi-tenant login.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Client ID para M2M login (opcional).
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Escopos solicitados (para grant password).
     */
    private String scope;
}
