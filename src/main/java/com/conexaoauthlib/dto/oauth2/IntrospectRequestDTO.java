package com.conexaoauthlib.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de introspecção de token.
 * Utilizado para verificar a validade e claims de um token.
 *
 * <p>Segue a especificação RFC 7662.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * IntrospectRequestDTO request = IntrospectRequestDTO.builder()
 *     .token("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
 *     .tokenTypeHint("access_token")
 *     .build();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7662">RFC 7662</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequestDTO {

    /**
     * Token a ser analisado.
     */
    @NotBlank(message = "Token é obrigatório para introspecção")
    private String token;

    /**
     * Hint opcional do tipo de token.
     * Ajuda o servidor a otimizar a busca.
     * Valores possíveis: "access_token", "refresh_token"
     */
    @JsonProperty("token_type_hint")
    private String tokenTypeHint;
}
