package com.plataforma.conexao.auth.starter.client;

import com.plataforma.conexao.auth.starter.dto.response.JwksResponse;
import feign.Headers;
import feign.RequestLine;

/**
 * Interface Feign Client para obter chaves públicas JWKS.
 *
 * <p>Este cliente é responsável por buscar o conjunto de chaves públicas
 * (JWKS - JSON Web Key Set) do Auth Server, que são usadas para validar
 * assinaturas de tokens JWT.
 *
 * <p>Conforme especificação RFC 7517 (JWK) e RFC 7513 (JWK Set).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public interface JwksClient {

    /**
     * Busca o conjunto de chaves públicas JWKS do Auth Server.
     *
     * <p>Endpoint: GET /.well-known/jwks.json
     *
     * <p>Este endpoint retorna todas as chaves públicas ativas que podem ser
     * usadas para validar tokens JWT emitidos pelo Auth Server.
     *
     * @return Resposta contendo a lista de chaves públicas (JWKS)
     */
    @RequestLine("GET /.well-known/jwks.json")
    @Headers({"Accept: application/json"})
    JwksResponse getJwks();
}
