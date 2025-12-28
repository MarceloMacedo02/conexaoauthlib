package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção base para erros de integração com serviços externos.
 *
 * <p>Esta classe representa erros relacionados à comunicação com
 * serviços externos, incluindo:
 * <ul>
 *   <li>Auth Server Conexão Auth</li>
 *   <li>Endpoints JWKS</li>
 *   <li>Serviços de terceiros</li>
 * </ul>
 *
 * <p>Subclasses comuns:
 * <ul>
 *   <li>{@link ConexaoAuthClientException} - Erros no cliente Feign</li>
 *   <li>{@link JwksNotFoundException} - JWKS não encontrado</li>
 *   <li>{@link KeyNotFoundException} - Chave pública não encontrada</li>
 * </ul>
 *
 * <p>Esta exceção geralmente resulta em respostas HTTP 502 (Bad Gateway),
 * 503 (Service Unavailable) ou 504 (Gateway Timeout).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class IntegrationException extends ConexaoAuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public IntegrationException(String message) {
        super(message, 502);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public IntegrationException(String message, Throwable cause) {
        super(message, cause, 502);
    }

    /**
     * Construtor com mensagem e status HTTP customizável.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param statusCode Status HTTP code (ex: 502, 503, 504)
     */
    public IntegrationException(String message, int statusCode) {
        super(message, statusCode);
    }
}
