package com.conexaoauthlib.exception;

/**
 * Exceção para quando o circuit breaker está aberto (HTTP 503).
 *
 * <p>Lançada quando o servidor OAuth2 está temporariamente indisponível
 * e o circuit breaker abriu para evitar sobrecarga.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class CircuitBreakerOpenException extends OAuth2Exception {

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Causa da exceção
     */
    public CircuitBreakerOpenException(String message, Throwable cause) {
        super(message, "circuit_breaker_open", 503, cause);
    }

    /**
     * Construtor com mensagem apenas.
     *
     * @param message Mensagem descritiva do erro
     */
    public CircuitBreakerOpenException(String message) {
        super(message, "circuit_breaker_open", 503);
    }
}
