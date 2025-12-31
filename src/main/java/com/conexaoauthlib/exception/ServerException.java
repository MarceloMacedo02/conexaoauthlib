package com.conexaoauthlib.exception;

/**
 * Exceção para erros de servidor (HTTP 500).
 *
 * <p>Lançada quando ocorre um erro interno no servidor OAuth2
 * que não pode ser tratado especificamente.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ServerException extends OAuth2Exception {

    /**
     * Construtor com mensagem.
     *
     * @param message Mensagem descritiva do erro
     */
    public ServerException(String message) {
        super(message, "server_error", 500);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public ServerException(String message, Throwable cause) {
        super(message, "server_error", 500, cause);
    }
}
