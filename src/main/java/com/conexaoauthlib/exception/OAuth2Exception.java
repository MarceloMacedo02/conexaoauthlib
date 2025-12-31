package com.conexaoauthlib.exception;

/**
 * Exceção base para erros específicos do OAuth2.
 *
 * <p>Esta é a classe pai para todas as exceções relacionadas a operações OAuth2,
 * incluindo erros de autenticação, grants inválidos e problemas de servidor.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class OAuth2Exception extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    /**
     * Construtor principal.
     *
     * @param message Mensagem de erro
     * @param errorCode Código de erro OAuth2
     * @param httpStatus Código HTTP status
     */
    public OAuth2Exception(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Construtor com causa.
     *
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "unknown";
        this.httpStatus = 500;
    }

    /**
     * Construtor completo.
     *
     * @param message Mensagem de erro
     * @param errorCode Código de erro OAuth2
     * @param httpStatus Código HTTP status
     * @param cause Causa da exceção
     */
    public OAuth2Exception(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Retorna o código de erro OAuth2.
     *
     * @return Código de erro
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Retorna o status HTTP.
     *
     * @return Status HTTP
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}
