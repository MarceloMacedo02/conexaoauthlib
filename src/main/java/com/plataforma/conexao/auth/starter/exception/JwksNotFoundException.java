package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando o endpoint JWKS não é encontrado ou não está acessível.
 *
 * <p>Esta exceção é utilizada quando há problemas ao buscar o conjunto de chaves
 * públicas (JSON Web Key Set) do Auth Server, incluindo:
 * <ul>
 *   <li>Endpoint /.well-known/jwks.json não encontrado (404)</li>
 *   <li>Endpoint inacessível (timeout, erro de rede)</li>
 *   <li>Resposta inválida do endpoint (não é JSON válido)</li>
 * </ul>
 *
 * <p>O JWKS é essencial para validar tokens JWT localmente, sem necessidade
 * de chamadas ao Auth Server. Quando o JWKS não está disponível, a validação
 * de tokens não pode ser realizada.
 *
 * <p>Esta exceção resulta em resposta HTTP 502 (Bad Gateway) ou 503 (Service Unavailable).
 *
 * <p><strong>Impacto:</strong> Sem JWKS disponível, o SDK não pode validar
 * tokens JWT localmente, o que pode impedir a autenticação de usuários.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class JwksNotFoundException extends IntegrationException {

    /**
     * URL do endpoint JWKS que não foi encontrado.
     */
    private final String jwksUrl;

    /**
     * Construtor com URL do endpoint JWKS.
     *
     * @param jwksUrl URL do endpoint JWKS que não foi encontrado
     */
    public JwksNotFoundException(String jwksUrl) {
        super(String.format("JWKS não encontrado ou inacessível: %s", jwksUrl));
        this.jwksUrl = jwksUrl;
    }

    /**
     * Construtor com URL e causa raiz.
     *
     * @param jwksUrl URL do endpoint JWKS que não foi encontrado
     * @param cause Causa raiz da exceção
     */
    public JwksNotFoundException(String jwksUrl, Throwable cause) {
        super(String.format("Erro ao buscar JWKS em: %s", jwksUrl), cause);
        this.jwksUrl = jwksUrl;
    }

    /**
     * Obtém a URL do endpoint JWKS que não foi encontrado.
     *
     * @return URL do endpoint JWKS, ou null se não foi especificado
     */
    public String getJwksUrl() {
        return jwksUrl;
    }
}
