package com.conexaoauthlib.exception;

/**
 * Exceção para operações não autorizadas (HTTP 401).
 *
 * <p>Lançada quando uma operação requer autenticação ou autorização
 * que não foi fornecida ou é inválida.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Construtor com mensagem.
     *
     * @param message Mensagem descritiva do erro
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
