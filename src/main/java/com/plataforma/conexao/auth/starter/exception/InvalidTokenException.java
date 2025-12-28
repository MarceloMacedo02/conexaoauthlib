package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um token JWT é inválido ou malformado.
 *
 * <p>Esta exceção é utilizada quando há problemas estruturais no token,
 * como:
 * <ul>
 *   <li>Token não está no formato JWT esperado</li>
 *   <li>Header ou payload do token são inválidos</li>
 *   <li>Token não pode ser parseado</li>
 *   <li>Assinatura do token é inválida</li>
 * </ul>
 *
 * <p>Para tokens expirados, utilize {@link ExpiredTokenException}.
 *
 * <p>Esta exceção resulta em resposta HTTP 401 (Unauthorized).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidTokenException extends AuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public InvalidTokenException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção (ex: InvalidKeyException, JsonProcessingException)
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor padrão com mensagem genérica.
     */
    public InvalidTokenException() {
        super("Token JWT inválido ou malformado");
    }
}
