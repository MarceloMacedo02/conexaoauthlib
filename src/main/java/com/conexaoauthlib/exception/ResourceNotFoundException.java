package com.conexaoauthlib.exception;

/**
 * Exceção para recurso não encontrado (HTTP 404).
 *
 * <p>Lançada quando uma operação tenta acessar um recurso
 * que não existe ou foi removido.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem.
     *
     * @param message Mensagem descritiva do erro
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
