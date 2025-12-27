package com.plataforma.conexao.auth.starter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.plataforma.conexao.auth.starter.client.JwksClient;
import com.plataforma.conexao.auth.starter.dto.response.JwksResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cache para chaves públicas JWKS com suporte a TTL e estatísticas.
 *
 * <p>Este cache armazena as chaves públicas RSA do Auth Server para evitar
 * requisições frequentes ao endpoint JWKS. O cache tem TTL configurável
 * e expira automaticamente as entradas antigas.
 *
 * <p>Utiliza a biblioteca Caffeine, que é a implementação de cache
 * recomendada pelo Spring Framework desde a versão 5.1.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class JwksCache {

    private static final Logger log = LoggerFactory.getLogger(JwksCache.class);

    private final JwksClient jwksClient;
    private final long cacheTtlMillis;

    /**
     * Cache interno: Key ID (kid) → PublicKey.
     */
    private final Cache<String, PublicKey> publicKeyCache;

    /**
     * Cache de respostas JWKS completas para reconstrução.
     */
    private final Cache<String, JwksResponse.Jwk> jwkCache;

    /**
     * Construtor do cache JWKS.
     *
     * @param jwksClient Feign client para buscar chaves
     * @param cacheTtlMillis Tempo de vida do cache em milissegundos
     */
    public JwksCache(JwksClient jwksClient, long cacheTtlMillis) {
        this.jwksClient = jwksClient;
        this.cacheTtlMillis = cacheTtlMillis;

        log.info("Inicializando JWKS Cache com TTL: {}ms", cacheTtlMillis);

        // Configuração do cache de chaves públicas
        this.publicKeyCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheTtlMillis, TimeUnit.MILLISECONDS)
                .maximumSize(100) // Máximo de 100 chaves em cache
                .recordStats() // Habilita estatísticas
                .build();

        // Configuração do cache de JWKs completos
        this.jwkCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheTtlMillis, TimeUnit.MILLISECONDS)
                .maximumSize(100)
                .recordStats()
                .build();
    }

    /**
     * Busca uma chave pública pelo Key ID (kid).
     *
     * <p>Se a chave não estiver no cache, faz uma requisição ao Auth Server
     * para atualizar o cache e retorna a chave encontrada.
     *
     * @param kid Key ID (kid) do header JWT
     * @return Chave pública RSA correspondente
     * @throws IllegalStateException se a chave não for encontrada
     */
    public PublicKey getPublicKey(String kid) {
        log.debug("Buscando chave pública com kid: {}", kid);

        // Tenta obter do cache
        PublicKey cachedKey = publicKeyCache.getIfPresent(kid);
        if (cachedKey != null) {
            log.debug("Chave pública encontrada no cache: {}", kid);
            return cachedKey;
        }

        // Cache miss - precisa atualizar
        log.info("Cache miss para kid: {}, atualizando JWKS...", kid);
        refreshJwks();

        // Tenta novamente após atualizar
        cachedKey = publicKeyCache.getIfPresent(kid);
        if (cachedKey == null) {
            throw new IllegalStateException(
                    String.format("Chave pública não encontrada para kid: %s", kid));
        }

        return cachedKey;
    }

    /**
     * Força a atualização do cache JWKS.
     *
     * <p>Faz uma requisição ao endpoint /.well-known/jwks.json e
     * atualiza todas as chaves armazenadas no cache.
     */
    public void refreshJwks() {
        log.info("Atualizando JWKS cache");

        try {
            // Busca JWKS do Auth Server
            JwksResponse jwksResponse = jwksClient.getJwks();

            if (jwksResponse == null || jwksResponse.keys() == null) {
                log.error("Resposta JWKS inválida: keys is null");
                return;
            }

            List<JwksResponse.Jwk> keys = jwksResponse.keys();
            log.info("Recebidas {} chaves do Auth Server", keys.size());

            // Atualiza cache de JWKs
            for (JwksResponse.Jwk jwk : keys) {
                if (jwk.kid() != null) {
                    jwkCache.put(jwk.kid(), jwk);
                    log.debug("Armazenado JWK no cache: kid={}", jwk.kid());
                }
            }

            // Converte e armazena chaves públicas
            List<Map.Entry<String, PublicKey>> publicKeys = keys.stream()
                    .filter(jwk -> jwk.kid() != null)
                    .map(jwk -> {
                        try {
                            PublicKey publicKey = JwkUtils.jwkToPublicKey(jwk);
                            return Map.entry(jwk.kid(), publicKey);
                        } catch (Exception e) {
                            log.error("Erro ao converter JWK para PublicKey: kid={}",
                                    jwk.kid(), e);
                            return null;
                        }
                    })
                    .filter(entry -> entry != null)
                    .toList();

            // Atualiza cache de chaves públicas
            for (Map.Entry<String, PublicKey> entry : publicKeys) {
                publicKeyCache.put(entry.getKey(), entry.getValue());
            }

            log.info("JWKS cache atualizado com sucesso. {} chaves armazenadas.",
                    publicKeys.size());

        } catch (Exception e) {
            log.error("Erro ao atualizar JWKS cache", e);
            throw new RuntimeException("Falha ao atualizar JWKS cache", e);
        }
    }

    /**
     * Limpa todo o cache (remove todas as entradas).
     *
     * <p>Útil para testes ou quando se deseja forçar uma atualização imediata.
     */
    public void clear() {
        log.info("Limpando JWKS cache");
        publicKeyCache.invalidateAll();
        jwkCache.invalidateAll();
    }

    /**
     * Obtém estatísticas do cache.
     *
     * @return Estatísticas de hits, misses, etc.
     */
    public CacheStats getStats() {
        return publicKeyCache.stats();
    }

    /**
     * Obtém o número de entradas no cache.
     *
     * @return Quantidade de chaves armazenadas
     */
    public long size() {
        return publicKeyCache.estimatedSize();
    }

    /**
     * Obtém a TTL configurada.
     *
     * @return TTL em milissegundos
     */
    public long getCacheTtlMillis() {
        return cacheTtlMillis;
    }
}
