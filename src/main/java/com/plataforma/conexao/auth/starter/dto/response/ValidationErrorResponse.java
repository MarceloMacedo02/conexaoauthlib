package com.plataforma.conexao.auth.starter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta de erro de validação.
 *
 * <p>Esta classe estende {@link ErrorResponse} para incluir informações
 * específicas sobre erros de validação de campos, retornados quando há
 * falha na validação de DTOs com {@code @Valid} ou {@code @Validated}.
 *
 * <p>Útil para fornecer feedback detalhado ao cliente sobre quais campos
 * específicos falharam na validação e quais foram os motivos.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {

    /**
     * Lista de erros de campo individuais.
     *
     * <p>Cada entrada representa um campo específico que falhou na validação,
     * contendo o nome do campo, mensagem de erro e valor rejeitado.
     *
     * <p>Exemplo de uso:
     * <pre>{@code
     * List<FieldError> errors = Arrays.asList(
     *     new FieldError("email", "Email já está em uso", "joao@exemplo.com"),
     *     new FieldError("senha", "Senha deve ter no mínimo 8 caracteres", "12345")
     * );
     * }</pre>
     */
    private List<FieldError> fieldErrors;
}
