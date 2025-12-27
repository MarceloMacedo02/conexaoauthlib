# Story SDK-3.3: JWKS Cache com TTL

**Epic:** SDK-3 - Token Validator e JWKS  
**Story:** SDK-3.3  
**Status:** Planejado  
**Prioridade:** Alta (P0)  
**Estimativa:** 0.4 dia  
**Complexidade:** Média

## Descrição

Implementar cache JWKS com TTL configurável para performance de validação de tokens.

## Critérios de Aceite

- [ ] Cache JWKS implementado com TTL
- [ ] TTL configurável via `conexao.auth.jwks-cache-ttl`
- [ ] Padrão: 5 minutos (300000ms)
- [ ] Thread-safe (ConcurrentHashMap)
- [ ] Invalidação automática após TTL

## Requisitos Técnicos

### Cache JWKS

**Estrutura:**
```java
// Cache em memória
private final ConcurrentHashMap<String, RSAPublicKey> jwksCache = new ConcurrentHashMap<>();
private volatile long cacheTimestamp = 0;
private final long cacheTtl; // em milissegundos

// Busca JWKS (cacheado ou fetch novo)
public RSAPublicKey getPublicKey(String kid) {
    // 1. Verificar se cache expirou
    if (System.currentTimeMillis() - cacheTimestamp > cacheTtl) {
        refreshJwksCache();
    }
    
    // 2. Buscar chave no cache
    return jwksCache.get(kid);
}
```

## Pontos de Atenção

1. **TTL Configurável:** conexao.auth.jwks-cache-ttl (padrão: 5 minutos)
2. **Thread-Safe:** ConcurrentHashMap para cache
3. **Invalidação:** Cache expira após TTL
4. **Refresh Manual:** Método refreshJwksCache() força atualização
5. **Performance:** Busca O(1) no cache

## Dependências

- SDK-3.2: Token Validator Implementation (usa cache)
- SDK-1.2: Configuration Properties (define TTL)
