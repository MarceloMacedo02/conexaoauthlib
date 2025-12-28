package com.plataforma.conexao.auth.starter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representando um erro de validação de campo específico.
 *
 * <p>Esta classe encapsula informações sobre um campo específico que falhou
 * na validação, incluindo o nome do campo, mensagem de erro descritiva e o
 * valor que foi rejeitado.
 *
 * <p>Utilizado em conjunto com {@link ValidationErrorResponse} para fornecer
 * feedback detalhado ao cliente sobre erros de validação.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {

    /**
     * Nome do campo que falhou na validação.
     *
     * <p>Corresponde ao nome do campo no DTO ou objeto validado.
     * Utiliza notação ponto (.) para campos aninhados.
     *
     * <p>Exemplos:
     * <ul>
     *   <li>{@code email} - Campo simples</li>
     *   <li>{@code usuario.endereco.cep} - Campo aninhado</li>
     *   <li>{@code roles[0].nome} - Campo em array/lista</li>
     * </ul>
     */
    private String field;

    /**
     * Mensagem de erro descritiva em Português.
     *
     * <p>Explica por que o campo falhou na validação, geralmente
     * proveniente das mensagens de validação do Jakarta Bean Validation.
     *
     * <p>Exemplos:
     * <ul>
     *   <li>{@code "Email não é válido"}</li>
     *   <li>{@code "Senha deve ter no mínimo 8 caracteres"}</li>
     *   <li>{@code "CPF é obrigatório"}</li>
     * </ul>
     */
    private String message;

    /**
     * Valor que foi rejeitado pela validação.
     *
     * <p>Contém o valor original enviado pelo cliente para o campo,
     * permitindo que o usuário veja o que foi digitado e corrija o problema.
     *
     * <p><strong>Nota de Segurança:</strong> Valores sensíveis como senhas,
     * tokens ou segredos não devem ser incluídos nesta campo. O sistema
     * deve mascarar ou omitir valores sensíveis.
     *
     * <p>Exemplos:
     * <ul>
     *   <li>{@code "joao@exemplo.com"}</li>
     *   <li>{@code "123"}</li>
     *   <li>{@code "*****"} (valor mascarado por segurança)</li>
     * </ul>
     */
    private Object rejectedValue;
}
