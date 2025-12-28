package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um realm é inválido ou inexistente.
 *
 * <p>Esta exceção é utilizada quando operações são tentadas em um realm
 * que:
 * <ul>
 *   <li>Não existe no sistema</li>
 *   <li>Está desativado ou inativo</li>
 *   <li>Não tem permissão para acessar</li>
 *   <li>ID do realm não é válido (formato incorreto, caracteres inválidos)</li>
 * </ul>
 *
 * <p>Realms representam isolamentos lógicos dentro do sistema de
 * autenticação, permitindo multi-tenancy. Cada realm tem seus próprios
 * usuários, roles e configurações.
 *
 * <p>Esta exceção resulta em resposta HTTP 400 (Bad Request) ou 404 (Not Found).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidRealmException extends BusinessException {

    /**
     * ID do realm que é inválido ou inexistente.
     */
    private final String realmId;

    /**
     * Construtor com ID do realm.
     *
     * @param realmId ID do realm que é inválido ou inexistente
     */
    public InvalidRealmException(String realmId) {
        super(String.format("Realm inválido ou inexistente: %s", realmId), 404);
        this.realmId = realmId;
    }

    /**
     * Construtor com ID do realm e causa raiz.
     *
     * @param realmId ID do realm que é inválido ou inexistente
     * @param cause Causa raiz da exceção
     */
    public InvalidRealmException(String realmId, Throwable cause) {
        super(String.format("Realm inválido ou inexistente: %s", realmId), cause, 404);
        this.realmId = realmId;
    }

    /**
     * Obtém o ID do realm que é inválido ou inexistente.
     *
     * @return ID do realm, ou null se não foi especificado
     */
    public String getRealmId() {
        return realmId;
    }
}
