package com.conexaoauthlib.exception;

/**
 * Exceção para operações inválidas (HTTP 400).
 *
 * <p>Lançada quando uma operação não pode ser executada
 * devido a regras de negócio ou validações.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Construtor com mensagem.
     *
     * @param message Mensagem descritiva do erro
     */
    public InvalidOperationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
