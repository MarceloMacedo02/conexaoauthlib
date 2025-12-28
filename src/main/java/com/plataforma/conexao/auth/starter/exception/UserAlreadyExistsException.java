package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando tentamos criar um usuário que já existe no sistema.
 *
 * <p>Esta exceção é utilizada quando há violação de unicidade em campos
 * que devem ser únicos, como:
 * <ul>
 *   <li>Email do usuário</li>
 *   <li>CPF do usuário</li>
 *   <li>Username</li>
 * </ul>
 *
 * <p>Esta exceção resulta em resposta HTTP 409 (Conflict), indicando que
 * a operação não pode ser realizada pois causaria um estado inconsistente.
 *
 * <p><strong>Nota de Segurança:</strong> Por segurança, a mensagem de erro
 * não deve revelar detalhes sobre usuários existentes, apenas indicar que
 * o recurso já existe.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserAlreadyExistsException extends BusinessException {

    /**
     * Tipo do identificador que já existe (email, cpf, username).
     */
    private final String identifierType;

    /**
     * Valor do identificador que já existe.
     */
    private final String identifierValue;

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public UserAlreadyExistsException(String message) {
        super(message, 409);
        this.identifierType = null;
        this.identifierValue = null;
    }

    /**
     * Construtor com tipo e valor do identificador.
     *
     * @param identifierType Tipo do identificador (email, cpf, username)
     * @param identifierValue Valor do identificador que já existe
     */
    public UserAlreadyExistsException(String identifierType, String identifierValue) {
        super(String.format("Usuário já existe com %s informado", identifierType), 409);
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
    }

    /**
     * Construtor com tipo e valor do identificador, e causa raiz.
     *
     * @param identifierType Tipo do identificador (email, cpf, username)
     * @param identifierValue Valor do identificador que já existe
     * @param cause Causa raiz da exceção
     */
    public UserAlreadyExistsException(String identifierType, String identifierValue, Throwable cause) {
        super(String.format("Usuário já existe com %s informado", identifierType), cause, 409);
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
    }

    /**
     * Obtém o tipo do identificador que já existe.
     *
     * @return Tipo do identificador (email, cpf, username), ou null se não foi especificado
     */
    public String getIdentifierType() {
        return identifierType;
    }

    /**
     * Obtém o valor do identificador que já existe.
     *
     * @return Valor do identificador, ou null se não foi especificado
     */
    public String getIdentifierValue() {
        return identifierValue;
    }
}
