package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção base para erros do Conexão Auth SDK.
 *
 * <p>Esta hierarquia será expandida na Story SDK-2.3.
 * Por enquanto, esta é uma classe stub para permitir a compilação.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConexaoAuthException extends RuntimeException {

    private final int statusCode;

    public ConexaoAuthException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ConexaoAuthException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
