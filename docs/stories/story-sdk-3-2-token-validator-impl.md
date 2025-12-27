# Story SDK-3.2: Token Validator Implementation

**Epic:** SDK-3 - Token Validator e JWKS  
**Story:** SDK-3.2  
**Status:** Planejado  
**Prioridade:** Alta (P0)  
**Estimativa:** 1.0 dia  
**Complexidade:** Alta

## Descrição

Implementar a classe `TokenValidatorImpl` para validar tokens JWT localmente via JWKS.

## Critérios de Aceite

- [ ] Classe `TokenValidatorImpl` implementada
- [ ] Implementa interface `TokenValidator`
- [ ] Valida assinatura JWT via JWKS
- [ ] Verifica expiração do token
- [ ] Cache JWKS com TTL configurável
- [ ] Performance < 5ms para tokens cacheados
- [ ] Javadoc completo em Português

## Requisitos Técnicos

### Classe TokenValidatorImpl

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/service/TokenValidatorImpl.java`

**Principais métodos:**
1. `validateToken(String jwt)`: Valida assinatura, expiração e retorna claims
2. `extractClaims(String jwt)`: Extrai claims sem validar assinatura
3. `refreshJwksCache()`: Força atualização do cache JWKS

**Dependências:**
- `JwksClient` para buscar JWKS
- `ConexaoAuthProperties` para configuração de TTL
- `com.nimbusds:jwt` para parse/validação JWT

**Cache JWKS:**
- Cache em memória com TTL configurável (conexao.auth.jwks-cache-ttl)
- Padrão: 5 minutos (300000ms)
- Busca JWKS apenas em cache miss

## Exemplos de Testes

```java
@Test
@DisplayName("Dado token válido, quando validar, então claims retornados")
void dadoTokenValido_quandoValidar_entaoClaimsRetornados() {
    // Implementação
}
```

## Dependências

- SDK-3.1: Token Validator Interface
- SDK-2.2: Feign Client - JwksClient
- SDK-2.5: DTOs de Response

## Pontos de Atenção

1. **Performance:** Validação < 5ms (sem chamada de rede)
2. **Cache JWKS:** TTL configurável, padrão 5 minutos
3. **Validação Assinatura:** RS256 com chave pública do JWKS
4. **Verificação Expiração:** Token expirados devem ser rejeitados
5. **Thread-Safe:** Cache thread-safe (ConcurrentHashMap)
6. **Nimbus JWT:** Usar biblioteca com.nimbusds:jwt para parse/validação
