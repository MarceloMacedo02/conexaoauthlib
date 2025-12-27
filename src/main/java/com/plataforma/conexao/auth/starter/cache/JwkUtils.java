package com.plataforma.conexao.auth.starter.cache;

import com.plataforma.conexao.auth.starter.dto.response.JwksResponse;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * Utilitários para conversão de JWK para PublicKey RSA.
 *
 * <p>Converte objetos JWK (JSON Web Key) do formato JSON para
 * instâncias de java.security.PublicKey compatíveis com RSA.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public final class JwkUtils {

    private JwkUtils() {
        // Utilitário - impede instanciação
    }

    /**
     * Converte um JWK para PublicKey RSA.
     *
     * <p>O JWK contém os parâmetros da chave pública RSA (modulus e exponent)
     * codificados em Base64 URL-safe. Este método decodifica esses valores
     * e constrói uma RSAPublicKeySpec.
     *
     * @param jwk Objeto JWK com parâmetros RSA
     * @return Chave pública RSA
     * @throws Exception se houver erro na conversão
     */
    public static PublicKey jwkToPublicKey(JwksResponse.Jwk jwk) throws Exception {
        // Decodifica o modulus (n) e exponent (e) de Base64 URL-safe
        BigInteger modulus = base64UrlDecode(jwk.n());
        BigInteger exponent = base64UrlDecode(jwk.e());

        // Cria especificação da chave pública RSA
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

        // Obtém KeyFactory e cria a chave pública
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Decodifica uma string Base64 URL-safe.
     *
     * <p>A codificação Base64 URL-safe usa '-' e '_' em vez de '+' e '/',
     * e não inclui padding ('='). Este método normaliza para Base64 padrão
     * antes de decodificar.
     *
     * @param base64Url String codificada em Base64 URL-safe
     * @return Array de bytes decodificado
     */
    private static BigInteger base64UrlDecode(String base64Url) {
        // Converte Base64 URL-safe para Base64 padrão
        String base64 = base64Url
                .replace('-', '+')
                .replace('_', '/');

        // Adiciona padding se necessário (múltiplo de 4)
        while (base64.length() % 4 != 0) {
            base64 += '=';
        }

        // Decodifica para BigInteger
        byte[] decoded = Base64.getDecoder().decode(base64);
        return new BigInteger(1, decoded);
    }
}
