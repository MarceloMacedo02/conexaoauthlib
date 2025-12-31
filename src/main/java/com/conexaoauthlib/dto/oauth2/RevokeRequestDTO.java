package com.conexaoauthlib.dto.oauth2;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de revogação de token.
 * Utilizado para invalidar tokens antes de sua expiração natural.
 *
 * <p>Segue a especificação RFC 7009.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RevokeRequestDTO request = RevokeRequestDTO.builder()
 *     .token(accessToken)
 *     .build();
 * oauth2Client.revokeToken(request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7009">RFC 7009</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeRequestDTO {

    /**
     * Token a ser revogado.
     * Pode ser um access_token ou refresh_token.
     */
    @NotBlank(message = "Token é obrigatório para revogação")
    private String token;
}
