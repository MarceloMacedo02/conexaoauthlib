package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há erros no cliente Feign de comunicação com o Auth Server.
 *
 * <p>Esta exceção é utilizada quando há falhas na comunicação HTTP com o
 * Conexão Auth Server, incluindo:
 * <ul>
 *   <li>Timeout de conexão ou leitura</li>
 *   <li>Erro de parse de JSON</li>
 *   <li>Resposta HTTP não esperada</li>
 *   <li>Erros de rede (DNS, conexão recusada)</li>
 * </ul>
 *
 * <p>Esta exceção geralmente resulta em respostas HTTP 502 (Bad Gateway) ou
 * 503 (Service Unavailable), indicando problemas de comunicação entre o SDK
 * e o Auth Server.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConexaoAuthClientException extends IntegrationException {

    /**
     * URL do endpoint que falhou.
     */
    private final String endpointUrl;

    /**
     * Código de status HTTP recebido (se aplicável).
     */
    private final Integer httpStatus;

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public ConexaoAuthClientException(String message) {
        super(message, 502);
        this.endpointUrl = null;
        this.httpStatus = null;
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public ConexaoAuthClientException(String message, Throwable cause) {
        super(message, cause);
        this.endpointUrl = null;
        this.httpStatus = null;
    }

    /**
     * Construtor com endpoint URL e status HTTP.
     *
     * @param endpointUrl URL do endpoint que falhou
     * @param httpStatus Código de status HTTP recebido
     * @param message Mensagem descritiva do erro em Português
     */
    public ConexaoAuthClientException(String endpointUrl, Integer httpStatus, String message) {
        super(message, 502);
        this.endpointUrl = endpointUrl;
        this.httpStatus = httpStatus;
    }

    /**
     * Obtém a URL do endpoint que falhou.
     *
     * @return URL do endpoint, ou null se não foi especificado
     */
    public String getEndpointUrl() {
        return endpointUrl;
    }

    /**
     * Obtém o código de status HTTP recebido.
     *
     * @return Código de status HTTP, ou null se não foi especificado
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }
}
