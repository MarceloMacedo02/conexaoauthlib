package com.conexaoauthlib.exception;

/**
 * Exceção para erros de grant inválido (HTTP 400).
 *
 * <p>Lançada quando o grant type é inválido, o token expirou,
 * ou os parâmetros da requisição estão incorretos.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class InvalidGrantException extends OAuth2Exception {

    /**
     * Construtor com mensagem e código de erro.
     *
     * @param message Mensagem descritiva do erro
     * @param errorCode Código de erro OAuth2
     */
    public InvalidGrantException(String message, String errorCode) {
        super(message, errorCode, 400);
    }

    /**
     * Construtor com mensagem, código de erro e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param errorCode Código de erro OAuth2
     * @param cause Causa da exceção
     */
    public InvalidGrantException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, 400, cause);
    }
}
