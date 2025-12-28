package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando uma permissão é inválida ou o usuário não tem permissão suficiente.
 *
 * <p>Esta exceção é utilizada quando há problemas relacionados à autorização,
 * incluindo:
 * <ul>
 *   <li>Permissão não existe no sistema</li>
 *   <li>Usuário não possui a permissão requerida</li>
 *   <li>Permissão não está associada ao realm</li>
 *   <li>Formato ou nome da permissão inválido</li>
 * </ul>
 *
 * <p><strong>Diferença de {@link InvalidCredentialsException}:</strong>
 * <ul>
 *   <li>{@code InvalidCredentialsException} - Usuário não conseguiu se autenticar (401)</li>
 *   <li>{@code InvalidPermissionException} - Usuário está autenticado, mas não tem permissão (403)</li>
 * </ul>
 *
 * <p>Esta exceção resulta em resposta HTTP 403 (Forbidden).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidPermissionException extends BusinessException {

    /**
     * Permissão que é inválida ou ausente.
     */
    private final String permission;

    /**
     * Lista de permissões requeridas.
     */
    private final String[] requiredPermissions;

    /**
     * Construtor com permissão específica.
     *
     * @param permission Permissão que é inválida ou ausente
     */
    public InvalidPermissionException(String permission) {
        super(String.format("Permissão inválida ou insuficiente: %s", permission), 403);
        this.permission = permission;
        this.requiredPermissions = null;
    }

    /**
     * Construtor com permissões requeridas.
     *
     * @param requiredPermissions Lista de permissões requeridas
     */
    public InvalidPermissionException(String[] requiredPermissions) {
        super("Permissões insuficientes para realizar a operação", 403);
        this.permission = null;
        this.requiredPermissions = requiredPermissions;
    }

    /**
     * Construtor com permissão e permissões requeridas.
     *
     * @param permission Permissão ausente
     * @param requiredPermissions Lista de todas as permissões requeridas
     */
    public InvalidPermissionException(String permission, String[] requiredPermissions) {
        super(String.format("Permissão ausente: %s. Permissões requeridas: %s",
                permission, String.join(", ", requiredPermissions)), 403);
        this.permission = permission;
        this.requiredPermissions = requiredPermissions;
    }

    /**
     * Obtém a permissão que é inválida ou ausente.
     *
     * @return Permissão, ou null se não foi especificado
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Obtém a lista de permissões requeridas.
     *
     * @return Array de permissões requeridas, ou null se não foi especificado
     */
    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }
}
