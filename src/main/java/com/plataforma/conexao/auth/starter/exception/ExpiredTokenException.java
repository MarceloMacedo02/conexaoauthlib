package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um token JWT está expirado.
 *
 * <p>Esta exceção é utilizada especificamente quando o token está válido
 * em formato e assinatura, mas sua expiração (claim {@code exp}) já passou.
 *
 * <p>Diferente de {@link InvalidTokenException}, que é usada para erros
 * estruturais do token, esta exceção indica que o token já foi válido
 * em algum momento, mas não pode mais ser usado.
 *
 * <p>Esta exceção resulta em resposta HTTP 401 (Unauthorized).
 *
 * <p><strong>Nota:</strong> O cliente deve solicitar um novo token
 * via refresh token flow ou reautenticação.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExpiredTokenException extends AuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public ExpiredTokenException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor padrão com mensagem genérica.
     */
    public ExpiredTokenException() {
        super("Token JWT expirado. Por favor, autentique-se novamente.");
    }
}
