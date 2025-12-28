package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando as credenciais fornecidas são inválidas.
 *
 * <p>Esta exceção é utilizada quando há falha na autenticação de usuário
 * devido a:
 * <ul>
 *   <li>Email/username não encontrado</li>
 *   <li>Senha incorreta</li>
 *   <li>Client ID ou Client Secret inválidos (para autenticação de aplicação)</li>
 *   <li>Credenciais não correspondem a nenhum usuário/cliente válido</li>
 * </ul>
 *
 * <p><strong>Nota de Segurança:</strong> Por segurança, esta exceção não
 * deve revelar se o usuário existe ou não, apenas que as credenciais são
 * inválidas. Isso previne ataques de enumeração de usuários.
 *
 * <p>Esta exceção resulta em resposta HTTP 401 (Unauthorized).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidCredentialsException extends AuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public InvalidCredentialsException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor padrão com mensagem genérica.
     */
    public InvalidCredentialsException() {
        super("Credenciais inválidas. Verifique seu email/e senha.");
    }
}
