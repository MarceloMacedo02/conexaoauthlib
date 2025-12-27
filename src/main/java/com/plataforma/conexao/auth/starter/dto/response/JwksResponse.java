package com.plataforma.conexao.auth.starter.dto.response;

import java.util.List;

/**
 * DTO de resposta para endpoint JWKS (JSON Web Key Set).
 *
 * <p>Este DTO representa o conjunto de chaves públicas usadas para validar
 * assinaturas de tokens JWT. O endpoint JWKS é especificado pelo RFC 7517.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record JwksResponse(

    /**
     * Lista de chaves públicas.
     */
    List<Jwk> keys
) {
    /**
     * JSON Web Key (chave pública RSA).
     *
     * <p>Contém os parâmetros necessários para validar a assinatura de tokens
     * JWT usando o algoritmo RS256 (RSA SHA-256).
     */
    public record Jwk(

        /**
         * Key ID - identificador único da chave.
         * Usado para encontrar a chave correta no token JWT (header "kid").
         */
        String kid,

        /**
         * Key Type - tipo de chave (ex: "RSA").
         */
        String kty,

        /**
         * Algorithm - algoritmo de assinatura (ex: "RS256").
         */
        String alg,

        /**
         * Key Use - uso da chave (ex: "sig" para assinatura).
         */
        String use,

        /**
         * Modulus - componente do par de chaves RSA (codificado em Base64 URL).
         */
        String n,

        /**
         * Exponent - componente do par de chaves RSA (codificado em Base64 URL).
         */
        String e
    ) {}
}
