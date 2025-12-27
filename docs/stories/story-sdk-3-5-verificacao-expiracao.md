# Story SDK-3.5: Verificação de Expiração

**Epic:** SDK-3 - Token Validator e JWKS  
**Story:** SDK-3.5  
**Status:** Planejado  
**Prioridade:** Alta (P0)  
**Estimativa:** 0.2 dia  
**Complexidade:** Baixa

## Descrição

Implementar verificação de expiração de tokens JWT em `TokenValidatorImpl`.

## Critérios de Aceite

- [ ] Verificação de expiração implementada em `validateToken()`
- [ ] Tokens expirados lançam `InvalidTokenException`
- [ ] Verificação usa `isExpired()` do `TokenClaims`
- [ ] Mensagem de erro em Português

## Requisitos Técnicos

### Verificação de Expiração

**Em `TokenValidatorImpl.validateToken()`:**
```java
// Após extrair claims
if (claims.isExpired()) {
    throw new InvalidTokenException("Token expirado em " + 
        Instant.ofEpochSecond(claims.exp()));
}
```

## Pontos de Atenção

1. **Verificação Obrigatória:** Sempre verificar expiração em `validateToken()`
2. **Mensagem Clara:** Incluir timestamp de expiração na mensagem de erro
3. **InvalidTokenException:** Lançar para tokens expirados
4. **Timestamp Unix:** Converter para formato legível

## Dependências

- SDK-3.2: Token Validator Implementation
- SDK-3.4: Modelo TokenClaims (método isExpired)
