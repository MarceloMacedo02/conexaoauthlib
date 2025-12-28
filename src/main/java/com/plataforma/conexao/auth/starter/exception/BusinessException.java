package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção base para erros de negócio no Conexão Auth SDK.
 *
 * <p>Esta classe representa erros relacionados à validação de regras de
 * negócio, incluindo:
 * <ul>
 *   <li>Validação de realms</li>
 *   <li>Verificação de duplicidade de usuários</li>
 *   <li>Validação de permissões</li>
 *   <li>Outras regras de negócio do domínio</li>
 * </ul>
 *
 * <p>Subclasses comuns:
 * <ul>
 *   <li>{@link InvalidRealmException} - Realm inválido ou inexistente</li>
 *   <li>{@link UserAlreadyExistsException} - Usuário já existe no sistema</li>
 *   <li>{@link InvalidPermissionException} - Permissão inválida ou insuficiente</li>
 * </ul>
 *
 * <p>Esta exceção geralmente resulta em respostas HTTP 400 (Bad Request),
 * 409 (Conflict) ou 422 (Unprocessable Entity), dependendo da natureza
 * da violação de regra de negócio.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class BusinessException extends ConexaoAuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public BusinessException(String message) {
        super(message, 422);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause, 422);
    }

    /**
     * Construtor com mensagem e status HTTP customizável.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param statusCode Status HTTP code (ex: 400, 409, 422)
     */
    public BusinessException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * Construtor com mensagem, causa raiz e status HTTP.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     * @param statusCode Status HTTP code
     */
    public BusinessException(String message, Throwable cause, int statusCode) {
        super(message, cause, statusCode);
    }
}
