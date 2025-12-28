package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um usuário não é encontrado no sistema.
 *
 * <p>Esta exceção é utilizada quando tentativas de buscar ou operar sobre
 * um usuário que não existe, como:
 * <ul>
 *   <li>Busca por CPF, email ou ID inexistente</li>
 *   <li>Tentativa de atualizar usuário deletado</li>
 *   <li>Usuário foi removido do sistema</li>
 * </ul>
 *
 * <p>Esta exceção resulta em resposta HTTP 404 (Not Found).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserNotFoundException extends AuthException {

    /**
     * Identificador do usuário que não foi encontrado.
     */
    private final String userId;

    /**
     * Tipo do identificador (ex: "cpf", "email", "id").
     */
    private final String identifierType;

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public UserNotFoundException(String message) {
        super(message, 404);
        this.userId = null;
        this.identifierType = null;
    }

    /**
     * Construtor com identificador do usuário.
     *
     * @param identifier Identificador do usuário (CPF, email ou ID)
     * @param identifierType Tipo do identificador ("cpf", "email", "id")
     */
    public UserNotFoundException(String identifier, String identifierType) {
        super(String.format("Usuário não encontrado com %s: %s", identifierType, identifier), 404);
        this.userId = identifier;
        this.identifierType = identifierType;
    }

    /**
     * Construtor padrão.
     */
    public UserNotFoundException() {
        super("Usuário não encontrado");
        this.userId = null;
        this.identifierType = null;
    }

    /**
     * Obtém o identificador do usuário que não foi encontrado.
     *
     * @return Identificador do usuário, ou null se não foi especificado
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Obtém o tipo do identificador do usuário.
     *
     * @return Tipo do identificador (cpf, email, id), ou null se não foi especificado
     */
    public String getIdentifierType() {
        return identifierType;
    }
}
