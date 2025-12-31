package com.conexaoauthlib.exception;

/**
 * Exceção para erros de cliente inválido (HTTP 401).
 *
 * <p>Lançada quando o client_id ou client_secret são inválidos,
 * ou quando o cliente não está autorizado para o grant type solicitado.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class InvalidClientException extends OAuth2Exception {

    /**
     * Construtor com mensagem e código de erro.
     *
     * @param message Mensagem descritiva do erro
     * @param errorCode Código de erro OAuth2
     */
    public InvalidClientException(String message, String errorCode) {
        super(message, errorCode, 401);
    }

    /**
     * Construtor com mensagem, código de erro e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param errorCode Código de erro OAuth2
     * @param cause Causa da exceção
     */
    public InvalidClientException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, 401, cause);
    }
}
