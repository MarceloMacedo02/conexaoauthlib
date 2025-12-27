# Epic SDK-3: Token Validator e JWKS

**Epic:** SDK-3 - Token Validator e JWKS
**Status:** Planejado
**Prioridade:** Alta (P0)
**Complexidade:** Alta
**Estimativa:** 2 dias

---

## Descrição

Este Epic implementa o validador de tokens JWT que valida tokens localmente via JWKS (JSON Web Key Set), sem necessidade de chamadas extras ao Auth Server. Inclui cache de chaves públicas para performance.

---

## Funcionalidades Implementadas

1. **Token Validator Interface** - Interface do validador de JWT
2. **Token Validator Implementation** - Implementação com validação RSA
3. **JWKS Cache** - Cache de chaves públicas com TTL configurável
4. **Extração de Claims** - Extração de claims do token JWT
5. **Verificação de Expiração** - Verificação se token está expirado
6. **Modelo TokenClaims** - Record para claims extraídos

---

## Stories do Epic

| # | Story | Prioridade | Estimativa | Status |
|---|-------|-----------|------------|--------|
| SDK-3.1 | Token Validator Interface | Alta (P0) | 0.2 dia | Planejado |
| SDK-3.2 | Token Validator Implementation | Alta (P0) | 1.0 dia | Planejado |
| SDK-3.3 | JWKS Cache com TTL | Alta (P0) | 0.4 dia | Planejado |
| SDK-3.4 | Modelo TokenClaims | Alta (P0) | 0.2 dia | Planejado |
| SDK-3.5 | Verificação de Expiração | Alta (P0) | 0.2 dia | Planejado |

---

## Dependências

- **Epic SDK-1: Estrutura Básica** - ConexaoAuthProperties
- **Epic SDK-2: Feign Client** - JwksClient

---

## Arquitetura do Epic

### Pacote: service

```
com.plataforma.conexao.auth.starter.service/
├── TokenValidator.java               # Interface do validador
└── TokenValidatorImpl.java           # Implementação com JWKS cache
```

### Pacote: model

```
com.plataforma.conexao.auth.starter.model/
└── TokenClaims.java                 # Record para claims JWT
```

### Pacote: exception

```
com.plataforma.conexao.auth.starter.exception/
└── InvalidTokenException.java       # Exceção para token inválido/expirado
```

---

## Componentes Principais

### TokenValidator (Interface)

**Responsabilidade:** Interface do validador de tokens JWT.

**Métodos:**
```java
public interface TokenValidator {

    /**
     * Valida um token JWT e extrai seus claims.
     *
     * @param jwt Token JWT a ser validado
     * @return TokenClaims com os claims extraídos
     * @throws InvalidTokenException se token for inválido ou expirado
     */
    TokenClaims validateToken(String jwt) throws InvalidTokenException;

    /**
     * Extrai claims de um token JWT sem validar a assinatura.
     *
     * @param jwt Token JWT
     * @return TokenClaims com os claims extraídos
     * @throws InvalidTokenException se formato do token for inválido
     */
    TokenClaims extractClaims(String jwt) throws InvalidTokenException;

    /**
     * Força refresh do cache JWKS.
     */
    void refreshJwksCache();
}
```

### TokenValidatorImpl

**Responsabilidade:** Implementação do validador com cache JWKS.

**Dependências:**
- `JwksClient` - Para buscar chaves públicas
- `ConexaoAuthProperties` - Para configuração de TTL

**Funcionalidades:**
- Validação de assinatura RSA usando chaves públicas do JWKS
- Cache de chaves públicas com TTL configurável
- Extração de claims do token
- Verificação de expiração (`exp` claim)

**Lógica de Cache:**
```java
public class TokenValidatorImpl implements TokenValidator {

    private final JwksClient jwksClient;
    private final long jwksCacheTtl;
    private final ConcurrentHashMap<String, RSAPublicKey> publicKeyCache = new ConcurrentHashMap<>();
    private volatile long lastCacheUpdate = 0L;

    @Override
    public TokenClaims validateToken(String jwt) {
        // 1. Extrair kid do header JWT
        String kid = extractKidFromHeader(jwt);

        // 2. Buscar chave pública do cache
        RSAPublicKey publicKey = publicKeyCache.get(kid);

        // 3. Cache miss: buscar do JWKS
        if (publicKey == null || isCacheExpired()) {
            refreshJwksCache();
            publicKey = publicKeyCache.get(kid);
        }

        // 4. Validar assinatura RSA
        JWTClaimsSet claims = validateSignature(jwt, publicKey);

        // 5. Verificar expiração
        if (isExpired(claims)) {
            throw new InvalidTokenException("Token expirado");
        }

        // 6. Retornar claims
        return TokenClaims.fromJWTClaimsSet(claims);
    }

    private void refreshJwksCache() {
        JwksResponse jwks = jwksClient.getJwks();
        jwks.keys().forEach(jwk -> {
            RSAPublicKey publicKey = convertJwkToPublicKey(jwk);
            publicKeyCache.put(jwk.kid(), publicKey);
        });
        lastCacheUpdate = System.currentTimeMillis();
    }

    private boolean isCacheExpired() {
        return System.currentTimeMillis() - lastCacheUpdate > jwksCacheTtl;
    }
}
```

### TokenClaims

**Responsibilidade:** DTO interno para claims JWT extraídos.

**Tipo:** Record (imutável)

**Campos:**
```java
public record TokenClaims(
    String sub,        // Subject (user ID)
    String realm,      // Realm ID
    List<String> roles, // Lista de roles
    String aud,        // Audience
    String iss,        // Issuer
    Long exp,          // Expiration timestamp (seconds)
    Long iat           // Issued at timestamp (seconds)
) {
    /**
     * Verifica se o token está expirado.
     *
     * @return true se expirado, false caso contrário
     */
    public boolean isExpired() {
        return System.currentTimeMillis() / 1000 >= exp;
    }

    /**
     * Converte JWTClaimsSet do Nimbus para TokenClaims.
     */
    public static TokenClaims fromJWTClaimsSet(JWTClaimsSet claimsSet) {
        return new TokenClaims(
            claimsSet.getSubject(),
            claimsSet.getStringClaim("realm"),
            claimsSet.getStringListClaim("roles"),
            claimsSet.getAudience() != null ? claimsSet.getAudience().get(0) : null,
            claimsSet.getIssuer(),
            claimsSet.getExpirationTime() != null ? claimsSet.getExpirationTime().toEpochSecond() : null,
            claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toEpochSecond() : null
        );
    }
}
```

### InvalidTokenException

**Responsibilidade:** Exceção para token inválido ou expirado.

**Extends:** `ConexaoAuthException`

```java
public class InvalidTokenException extends ConexaoAuthException {

    public InvalidTokenException(String message) {
        super(message, 0);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause, 0);
    }
}
```

---

## Critérios de Aceite por Story

### Story SDK-3.1: Token Validator Interface

- [ ] Interface `TokenValidator` criada
- [ ] Método `validateToken()` definido
- [ ] Método `extractClaims()` definido
- [ ] Método `refreshJwksCache()` definido
- [ ] Javadoc completo em todos os métodos

### Story SDK-3.2: Token Validator Implementation

- [ ] Classe `TokenValidatorImpl` implementa `TokenValidator`
- [ ] Validação de assinatura RSA funciona corretamente
- [ ] Chaves públicas são extraídas do JWKS
- [ ] Tokens inválidos lançam `InvalidTokenException`
- [ ] Tokens válidos retornam `TokenClaims`

### Story SDK-3.3: JWKS Cache com TTL

- [ ] Cache de chaves públicas implementado com `ConcurrentHashMap`
- [ ] TTL configurável via `conexao.auth.jwks-cache-ttl`
- [ ] Cache expira após TTL configurado
- [ ] Cache miss dispara refresh do JWKS
- [ ] Cache hit não dispara chamada ao Auth Server
- [ ] Thread-safe para múltiplas requisições

### Story SDK-3.4: Modelo TokenClaims

- [ ] Record `TokenClaims` criado com todos os campos
- [ ] Método `isExpired()` implementado
- [ ] Método estático `fromJWTClaimsSet()` implementado
- [ ] Claims mapeados corretamente do JWT

### Story SDK-3.5: Verificação de Expiração

- [ ] Claim `exp` é verificado em `validateToken()`
- [ ] Token expirado lança `InvalidTokenException`
- [ ] Timestamp convertido corretamente (epoch seconds)
- [ ] Validação usa hora atual do sistema

---

## Exemplo de Uso

### Validação de Token em Security Filter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // Validação local via JWKS (sem chamada ao auth server)
                TokenClaims claims = tokenValidator.validateToken(token);

                // Verificar expiração
                if (claims.isExpired()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
                    return;
                }

                // Criar autenticação Spring Security
                List<SimpleGrantedAuthority> authorities = claims.roles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                claims.sub(),
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (InvalidTokenException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### Extração de Claims (sem validação de assinatura)

```java
@Autowired
private TokenValidator tokenValidator;

// Extrair claims sem validar assinatura
TokenClaims claims = tokenValidator.extractClaims(jwt);
System.out.println("User ID: " + claims.sub());
System.out.println("Realm: " + claims.realm());
System.out.println("Roles: " + claims.roles());
System.out.println("Expired: " + claims.isExpired());
```

---

## Tecnologias Utilizadas

- **Nimbus JOSE+JWT** - Manipulação de tokens JWT
- **JWKS** - Endpoint com chaves públicas RSA
- **ConcurrentHashMap** - Cache thread-safe
- **Spring Cloud OpenFeign** - Cliente HTTP para JWKS

---

## Testes Requeridos

### Testes Unitários

- Teste de validação de token válido
- Teste de validação de token inválido (assinatura incorreta)
- Teste de validação de token expirado
- Teste de cache JWKS (hit e miss)
- Teste de expiração de cache TTL
- Teste de thread-safety do cache
- Teste de extração de claims

### Testes de Integração

- Teste de validação com JWKS real (mock server)
- Teste de refresh do cache
- Teste de performance (validação < 5ms)

### Testes de Performance

- Validação JWT deve ser < 5ms
- Cache JWKS deve ter hit rate > 95% em produção

---

## Pontos de Atenção

1. **Performance:** Validação de token deve ser < 5ms (NFR-PERF-001)
2. **Thread-Safety:** Cache deve ser thread-safe (usar ConcurrentHashMap)
3. **Cache TTL:** TTL padrão de 5 minutos (configurável)
4. **JWT Header:** Extrair `kid` do header para identificar chave pública correta
5. **RSA Validation:** Usar Nimbus JOSE+JWT para validação de assinatura RSA
6. **Expiration Check:** Verificar claim `exp` em epoch seconds
7. **No Network Calls:** Validação local sem chamada ao Auth Server

---

## Próximos Passos

Após conclusão deste Epic:
1. **Epic SDK-4: Auth Service** - Implementar serviços de alto nível
2. **Epic SDK-5: Testes e Documentação** - Implementar testes abrangentes e documentação

---

## Estatísticas do Epic

| Métrica | Quantidade |
|---------|------------|
| **Stories** | 5 |
| **Interfaces** | 1 |
| **Implementações** | 1 |
| **Records** | 1 |
| **Exceções** | 1 |
| **Testes Estimados** | 12-15 |
| **Linhas de Código** | ~200-250 |
