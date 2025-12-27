# Story SDK-3.1: Token Validator Interface

**Epic:** SDK-3 - Token Validator e JWKS
**Story:** SDK-3.1
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.2 dia
**Complexidade:** Baixa

---

## Descrição

Expandir a interface `TokenValidator` criada como stub na Story SDK-1.1, adicionando Javadoc completo em Português.

---

## Critérios de Aceite

- [ ] Interface `TokenValidator` expandida com Javadoc completo
- [ ] Métodos: `validateToken()`, `extractClaims()`, `refreshJwksCache()` definidos
- [ ] Javadoc detalhado em Português para todos os métodos
- [ ] Parâmetros e retornos tipados corretamente
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Validação Local:** Tokens devem ser validados localmente via JWKS (sem chamada ao Auth Server)
2. **Cache de Chaves:** JWKS deve ser cacheado para performance
3. **Extração de Claims:** Claims devem ser extraídos e retornados como `TokenClaims`
4. **Verificação de Expiração:** Tokens expirados devem ser rejeitados

---

## Requisitos Técnicos

### Interface TokenValidator

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/service/TokenValidator.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.service;

import com.plataforma.conexao.auth.starter.exception.InvalidTokenException;
import com.plataforma.conexao.auth.starter.model.TokenClaims;

/**
 * Interface para validação de tokens JWT.
 *
 * <p>Esta interface define os métodos para validar tokens JWT localmente
 * via JWKS (JSON Web Key Set), sem fazer chamadas ao Auth Server.
 *
 * <p><b>Finalidade:</b>
 * Validação local de tokens JWT oferece os seguintes benefícios:
 * <ul>
 *   <li><b>Performance:</b> Validação < 5ms (sem chamada de rede)</li>
 *   <li><b>Resiliência:</b> Funciona mesmo se o Auth Server estiver indisponível</li>
 *   <li><b>Escalabilidade:</b> Reduz carga no Auth Server</li>
 * </ul>
 *
 * <p><b>Como Funciona:</b>
 * <ol>
 *   <li>Extrair kid (key id) do header do token JWT</li>
 *   <li>Buscar JWKS do Auth Server (cacheado com TTL configurável)</li>
 *   <li>Buscar chave pública no JWKS pelo kid</li>
 *   <li>Validar assinatura do token com a chave pública</li>
 *   <li>Verificar expiração do token</li>
 *   <li>Extrair e retornar claims do token</li>
 * </ol>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * // Validação de token em Security Filter
 * try {
 *     TokenClaims claims = tokenValidator.validateToken(jwt);
 *
 *     // Criar autenticação Spring Security
 *     List&lt;SimpleGrantedAuthority&gt; authorities = claims.roles().stream()
 *             .map(SimpleGrantedAuthority::new)
 *             .toList();
 *
 *     UsernamePasswordAuthenticationToken authentication =
 *         new UsernamePasswordAuthenticationToken(
 *             claims.subject(),
 *             null,
 *             authorities
 *         );
 *
 *     SecurityContextHolder.getContext().setAuthentication(authentication);
 *
 * } catch (InvalidTokenException e) {
 *     response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
 * }
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @see com.plataforma.conexao.auth.starter.service.TokenValidatorImpl
 * @see com.plataforma.conexao.auth.starter.model.TokenClaims
 */
public interface TokenValidator {

    /**
     * Valida um token JWT e extrai os claims.
     *
     * <p>Este método valida:
     * <ul>
     *   <li><b>Assinatura:</b> Verifica se o token foi assinado pelo Auth Server</li>
     *   <li><b>Expiração:</b> Verifica se o token não expirou</li>
     *   <li><b>Formato:</b> Verifica se o token está em formato JWT válido</li>
     * </ul>
     *
     * <p><b>Processo de Validação:</b>
     * <ol>
     *   <li>Extrair kid (key id) do header do token JWT</li>
     *   <li>Buscar JWKS do Auth Server (cacheado com TTL configurável)</li>
     *   <li>Buscar chave pública no JWKS pelo kid</li>
     *   <li>Validar assinatura do token com a chave pública (RS256)</li>
     *   <li>Verificar expiração do token (claim exp)</li>
     *   <li>Extrair claims do token (sub, realm, roles, aud, iss, exp, iat)</li>
     * </ol>
     *
     * <p><b>Cache JWKS:</b>
     * O JWKS é cacheado localmente com um TTL configurável
     * (conexao.auth.jwks-cache-ttl). O padrão é 5 minutos (300000ms).
     * Isso evita chamadas frequentes ao endpoint /.well-known/jwks.json.
     *
     * <p><b>Performance:</b>
     * Validação deve ser < 5ms para tokens cacheados (sem chamada de rede).
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>InvalidTokenException - Token inválido ou expirado</li>
     *   <li>InvalidTokenException - Assinatura inválida</li>
     *   <li>InvalidTokenException - Chave não encontrada no JWKS</li>
     *   <li>InvalidTokenException - Formato JWT inválido</li>
     * </ul>
     *
     * <p><b>Claims Retornados:</b>
     * <pre>
     * TokenClaims {
     *     sub: "user-123",              // Subject (user ID)
     *     realm: "master",             // Realm ID
     *     roles: ["role-user", ...],   // Lista de roles
     *     aud: "api-v1",               // Audience
     *     iss: "https://auth.example.com",  // Issuer
     *     exp: 1704067200,             // Expiration timestamp (seconds)
     *     iat: 1704063600              // Issued at timestamp (seconds)
     * }
     * </pre>
     *
     * @param jwt Token JWT (Bearer token sem prefixo "Bearer ")
     * @return Claims extraídos do token (sub, realm, roles, aud, iss, exp, iat)
     * @throws InvalidTokenException Se o token for inválido, expirado ou tiver assinatura inválida
     */
    TokenClaims validateToken(String jwt) throws InvalidTokenException;

    /**
     * Extrai claims de um token JWT sem validar assinatura.
     *
     * <p><b>Atenção:</b> Este método NÃO valida a assinatura do token.
     * Use-o apenas para debugging ou quando você já validou o token
     * anteriormente e precisa extrair claims novamente.
     *
     * <p>Para validação completa (incluindo assinatura e expiração),
     * use {@link #validateToken(String)}.
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>InvalidTokenException - Token em formato inválido</li>
     * </ul>
     *
     * @param jwt Token JWT (Bearer token sem prefixo "Bearer ")
     * @return Claims extraídos do token (sem validação de assinatura/expiração)
     * @throws InvalidTokenException Se o token estiver em formato inválido
     */
    TokenClaims extractClaims(String jwt) throws InvalidTokenException;

    /**
     * Força a atualização do cache JWKS.
     *
     * <p>Este método busca o JWKS atualizado do Auth Server
     * e atualiza o cache local, ignorando o TTL atual.
     *
     * <p><b>Quando Usar:</b>
     * <ul>
     *   <li>Após rotation de chaves no Auth Server</li>
     *   <li>Quando suspeitar de cache desatualizado</li>
     *   <li>Em jobs de manutenção/sincronização</li>
     * </ul>
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>ServerException - Erro ao buscar JWKS do Auth Server</li>
     * </ul>
     *
     * @throws com.plataforma.conexao.auth.starter.exception.ServerException
     *         Se erro ao buscar JWKS do Auth Server
     */
    void refreshJwksCache() throws com.plataforma.conexao.auth.starter.exception.ServerException;
}
```

---

## Exemplos de Testes

### Teste de Interface (Exemplo Conceitual)

```java
import com.plataforma.conexao.auth.starter.model.TokenClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de interface TokenValidator (testes de implementação serão na SDK-3.2).
 */
@DisplayName("Testes de Interface - TokenValidator")
class TokenValidatorInterfaceTest {

    @Test
    @DisplayName("Dada implementação mock, quando validar token, então claims retornados")
    void dadoImplementacaoMock_quandoValidarToken_entaoClaimsRetornados() {
        // Arrange
        TokenValidator mockValidator = new TokenValidator() {
            @Override
            public TokenClaims validateToken(String jwt) {
                return new TokenClaims(
                    "user-123",
                    "master",
                    List.of("role-user", "role-admin"),
                    "api-v1",
                    "https://auth.example.com",
                    1704067200L,
                    1704063600L
                );
            }

            @Override
            public TokenClaims extractClaims(String jwt) {
                throw new UnsupportedOperationException("Não implementado");
            }

            @Override
            public void refreshJwksCache() {
                throw new UnsupportedOperationException("Não implementado");
            }
        };

        // Act
        TokenClaims claims = mockValidator.validateToken("fake-jwt-token");

        // Assert
        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo("user-123");
        assertThat(claims.realm()).isEqualTo("master");
        assertThat(claims.roles()).containsExactlyInAnyOrder("role-user", "role-admin");
        assertThat(claims.aud()).isEqualTo("api-v1");
        assertThat(claims.iss()).isEqualTo("https://auth.example.com");
    }

    @Test
    @DisplayName("Dada implementação mock, quando extrair claims, então claims retornados")
    void dadoImplementacaoMock_quandoExtrairClaims_entaoClaimsRetornados() {
        // Arrange
        TokenValidator mockValidator = new TokenValidator() {
            @Override
            public TokenClaims validateToken(String jwt) {
                throw new UnsupportedOperationException("Não implementado");
            }

            @Override
            public TokenClaims extractClaims(String jwt) {
                return new TokenClaims(
                    "user-456",
                    "test",
                    List.of("role-user"),
                    "api-v1",
                    "https://auth.example.com",
                    1704067200L,
                    1704063600L
                );
            }

            @Override
            public void refreshJwksCache() {
                throw new UnsupportedOperationException("Não implementado");
            }
        };

        // Act
        TokenClaims claims = mockValidator.extractClaims("fake-jwt-token");

        // Assert
        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo("user-456");
        assertThat(claims.realm()).isEqualTo("test");
    }

    @Test
    @DisplayName("Dada implementação mock, quando refresh cache, então sem exceção")
    void dadoImplementacaoMock_quandoRefreshCache_entaoSemExcecao() {
        // Arrange
        TokenValidator mockValidator = new TokenValidator() {
            @Override
            public TokenClaims validateToken(String jwt) {
                throw new UnsupportedOperationException("Não implementado");
            }

            @Override
            public TokenClaims extractClaims(String jwt) {
                throw new UnsupportedOperationException("Não implementado");
            }

            @Override
            public void refreshJwksCache() {
                // Refresh cache (mock)
            }
        };

        // Act & Assert (não deve lançar exceção)
        mockValidator.refreshJwksCache();
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (criou stub desta interface)
- **Story SDK-2.2:** Feign Client - JwksClient (será usado pela implementação)
- **Story SDK-2.5:** DTOs de Response (expande `JwksResponse` usado pela implementação)
- **Story SDK-3.2:** Token Validator Implementation (implementará esta interface)
- **Story SDK-3.4:** Modelo TokenClaims (será expandido nesta story)

---

## Pontos de Atenção

1. **Javadoc Completo:** Documentar todos os métodos com detalhes de validação, cache, exceções
2. **Método validateToken():** Validar assinatura, expiração, formato JWT
3. **Método extractClaims():** Extração sem validação (apenas para debugging)
4. **Método refreshJwksCache():** Forçar atualização do cache JWKS
5. **Claims Retornados:** sub, realm, roles, aud, iss, exp, iat
6. **Cache JWKS:** TTL configurável (conexao.auth.jwks-cache-ttl)
7. **Performance:** Validação < 5ms (sem chamada de rede)
8. **Exceções:** InvalidTokenException para erros de validação

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Updated Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/service/TokenValidator.java` (expandido)

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/service/TokenValidatorInterfaceTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-3: Token Validator e JWKS](./epic-sdk-3-token-validator.md)
- **Story Anterior:** [Story SDK-2.5: DTOs de Response](./story-sdk-2-5-dtos-response.md)
- **Story Seguinte:** [Story SDK-3.2: Token Validator Implementation](./story-sdk-3-2-token-validator-impl.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
- **RFC 7519:** JSON Web Token (JWT) Specification (https://tools.ietf.org/html/rfc7519)
- **RFC 7517:** JSON Web Key (JWK) Specification (https://tools.ietf.org/html/rfc7517)
