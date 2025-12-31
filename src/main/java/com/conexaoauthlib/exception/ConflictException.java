package com.conexaoauthlib.exception;

/**
 * Exceção para conflito de recursos (HTTP 409).
 *
 * <p>Lançada quando uma operação não pode ser executada
 * devido a um conflito com o estado atual do recurso.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ConflictException extends RuntimeException {

    /**
     * Construtor com mensagem.
     *
     * @param message Mensagem descritiva do erro
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
