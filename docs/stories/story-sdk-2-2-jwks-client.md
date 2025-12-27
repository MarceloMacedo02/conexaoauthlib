# Story SDK-2.2: Feign Client - JwksClient

**Epic:** SDK-2 - Feign Client e Error Decoder
**Story:** SDK-2.2
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.15 dia
**Complexidade:** Baixa

---

## Descrição

Implementar a interface Feign Client `JwksClient` para buscar o conjunto de chaves públicas (JWKS - JSON Web Key Set) do Auth Server. Esta interface será usada pelo `TokenValidator` para validar tokens JWT localmente.

---

## Critérios de Aceite

- [ ] Interface `JwksClient` criada com endpoint JWKS
- [ ] Anotação `@FeignClient` configurada corretamente
- [ ] Endpoint `GET /.well-known/jwks.json` definido
- [ ] Interface usa DTO `JwksResponse` para resposta
- [ ] Javadoc completo em Português
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Endpoint JWKS:** Endpoint padrão RFC 7517 para JWKS é `/.well-known/jwks.json`
2. **URL Dinâmica:** URL base do Auth Server configurada via `conexao.auth.base-url`
3. **Cache:** Respostas serão cacheadas pelo TokenValidator (Story SDK-3.3)
4. **Error Decoder:** Erros HTTP são traduzidos para exceções da SDK

---

## Requisitos Técnicos

### Interface JwksClient

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/client/JwksClient.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.client;

import com.plataforma.conexao.auth.starter.dto.response.JwksResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Interface Feign Client para buscar JWKS (JSON Web Key Set) do Auth Server.
 *
 * <p>Esta interface define o endpoint JWKS do Auth Server de forma declarativa.
 * O Feign gera a implementação automaticamente em tempo de execução.
 *
 * <p><b>Finalidade:</b>
 * O JWKS contém as chaves públicas RSA usadas para validar a assinatura
 * dos tokens JWT emitidos pelo Auth Server. Com o JWKS em cache local,
 * é possível validar tokens sem fazer chamadas ao Auth Server.
 *
 * <p><b>Configuração:</b>
 * <ul>
 *   <li>URL base: ${conexao.auth.base-url} (configurado via application.yml)</li>
 *   <li>Endpoint: /.well-known/jwks.json (RFC 7517)</li>
 *   <li>Erros HTTP são traduzidos para exceções da SDK pelo ConexaoAuthErrorDecoder</li>
 * </ul>
 *
 * <p><b>Response JWKS:</b>
 * <pre>
 * {
 *   "keys": [
 *     {
 *       "kid": "key-1",
 *       "kty": "RSA",
 *       "alg": "RS256",
 *       "use": "sig",
 *       "n": "base64url-encoded-modulus",
 *       "e": "AQAB"
 *     },
 *     ...
 *   ]
 * }
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @see com.plataforma.conexao.auth.starter.service.TokenValidator
 * @see com.plataforma.conexao.auth.starter.dto.response.JwksResponse
 */
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface JwksClient {

    /**
     * Busca o conjunto de chaves públicas (JWKS) do Auth Server.
     *
     * <p>Este endpoint retorna o JWKS contendo as chaves públicas RSA usadas
     * para validar a assinatura dos tokens JWT emitidos pelo Auth Server.
     *
     * <p><b>Endpoint:</b> GET /.well-known/jwks.json
     *
     * <p><b>Finalidade:</b>
     * Com o JWKS em cache local, é possível validar tokens JWT sem fazer
     * chamadas ao Auth Server. Isso reduz latência e aumenta resiliência.
     *
     * <p><b>Cache:</b>
     * A resposta deste endpoint deve ser cacheada pelo TokenValidator com
     * um TTL configurável (conexao.auth.jwks-cache-ttl). O padrão é 5 minutos.
     *
     * <p><b>Status HTTP:</b>
     * <ul>
     *   <li>200 OK - JWKS retornado com sucesso</li>
     *   <li>404 Not Found - Endpoint JWKS não disponível</li>
     *   <li>500 Server Error - Erro interno do servidor</li>
     * </ul>
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>ResourceNotFoundException - Endpoint JWKS não disponível (404)</li>
     *   <li>ServerException - Erro interno do servidor (500+)</li>
     * </ul>
     *
     * <p><b>Formato JWKS (RFC 7517):</b>
     * <pre>
     * {
     *   "keys": [
     *     {
     *       "kid": "key-1",              // Key ID (identificador único da chave)
     *       "kty": "RSA",                // Key Type (RSA)
     *       "alg": "RS256",              // Algorithm (RS256)
     *       "use": "sig",                // Use (sig = signature)
     *       "n": "base64url-modulus",    // Modulus (parte da chave pública)
     *       "e": "AQAB"                  // Exponent (parte da chave pública)
     *     },
     *     ...
     *   ]
     * }
     * </pre>
     *
     * <p><b>Uso no TokenValidator:</b>
     * <pre>
     * // 1. Extrair kid (key id) do header do token JWT
     * String kid = extractKidFromToken(jwt);
     *
     * // 2. Buscar JWKS (cacheado ou fetch novo)
     * JwksResponse jwks = jwksClient.getJwks();
     *
     * // 3. Buscar chave pública por kid
     * Jwk publicKey = jwks.keys().stream()
     *     .filter(key -> key.kid().equals(kid))
     *     .findFirst()
     *     .orElseThrow(() -> new InvalidTokenException("Chave não encontrada"));
     *
     * // 4. Validar assinatura do token com a chave pública
     * boolean valid = validateSignature(jwt, publicKey);
     * </pre>
     *
     * @return DTO com conjunto de chaves públicas (JWKS)
     * @throws ResourceNotFoundException Se endpoint JWKS não disponível (404)
     * @throws ServerException Se erro interno do servidor (500+)
     */
    @GetMapping("/.well-known/jwks.json")
    JwksResponse getJwks();
}
```

---

## Exemplos de Testes

### Teste Unitário com Feign Mock

```java
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static feign.mock.RequestKeyBuilder.request;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários de JwksClient usando Feign Mock.
 */
@DisplayName("Testes Unitários - JwksClient")
class JwksClientUnitTest {

    private JwksClient client;
    private MockClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = new MockClient();
        client = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .client(mockClient)
                .target(new MockTarget<>(JwksClient.class));
    }

    @Test
    @DisplayName("Dado JWKS no mock, quando buscar JWKS, então JWKS retornado")
    void dadoJwksNoMock_quandoBuscarJwks_entaoJwksRetornado() {
        // Arrange
        JwksResponse expectedResponse = new JwksResponse(
            List.of(
                new JwksResponse.Jwk(
                    "key-1",
                    "RSA",
                    "RS256",
                    "sig",
                    "base64url-encoded-modulus",
                    "AQAB"
                ),
                new JwksResponse.Jwk(
                    "key-2",
                    "RSA",
                    "RS256",
                    "sig",
                    "base64url-encoded-modulus-2",
                    "AQAB"
                )
            )
        );

        mockClient.ok(request(HttpMethod.GET, "/.well-known/jwks.json"), expectedResponse);

        // Act
        JwksResponse actualResponse = client.getJwks();

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.keys()).hasSize(2);
        assertThat(actualResponse.keys().get(0).kid()).isEqualTo("key-1");
        assertThat(actualResponse.keys().get(0).kty()).isEqualTo("RSA");
        assertThat(actualResponse.keys().get(0).alg()).isEqualTo("RS256");
    }

    @Test
    @DisplayName("Dado JWKS com 1 chave no mock, quando buscar JWKS, então 1 chave retornada")
    void dadoJwksCom1ChaveNoMock_quandoBuscarJwks_entao1ChaveRetornada() {
        // Arrange
        JwksResponse expectedResponse = new JwksResponse(
            List.of(
                new JwksResponse.Jwk(
                    "key-1",
                    "RSA",
                    "RS256",
                    "sig",
                    "base64url-encoded-modulus",
                    "AQAB"
                )
            )
        );

        mockClient.ok(request(HttpMethod.GET, "/.well-known/jwks.json"), expectedResponse);

        // Act
        JwksResponse actualResponse = client.getJwks();

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.keys()).hasSize(1);
        assertThat(actualResponse.keys().get(0).kid()).isEqualTo("key-1");
    }

    @Test
    @DisplayName("Dado JWKS vazio no mock, quando buscar JWKS, então lista vazia retornada")
    void dadoJwksVazioNoMock_quandoBuscarJwks_entaoListaVaziaRetornada() {
        // Arrange
        JwksResponse expectedResponse = new JwksResponse(List.of());

        mockClient.ok(request(HttpMethod.GET, "/.well-known/jwks.json"), expectedResponse);

        // Act
        JwksResponse actualResponse = client.getJwks();

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.keys()).isEmpty();
    }
}
```

### Teste de Integração (Exemplo Conceitual)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JwksClientIntegrationTest {

    @Autowired
    private JwksClient jwksClient;

    @Test
    @DisplayName("Dado Auth Server disponível, quando buscar JWKS, então JWKS retornado")
    @Disabled("Este teste requer Auth Server em execução")
    void dadoAuthServerDisponivel_quandoBuscarJwks_entaoJwksRetornado() {
        // Act
        JwksResponse response = jwksClient.getJwks();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.keys()).isNotEmpty();
        assertThat(response.keys().get(0).kid()).isNotBlank();
        assertThat(response.keys().get(0).kty()).isEqualTo("RSA");
        assertThat(response.keys().get(0).alg()).isEqualTo("RS256");
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal
- **Story SDK-1.2:** Configuration Properties (define `conexao.auth.base-url`)
- **Story SDK-1.3:** Feign Configuration (configura encoder/decoder/OkHttp)
- **Story SDK-2.5:** DTOs de Response (expande `JwksResponse`)
- **Story SDK-3.2:** Token Validator Implementation (usará este client para buscar JWKS)

---

## Pontos de Atenção

1. **Endpoint JWKS RFC 7517:** Endpoint padrão é `/.well-known/jwks.json`
2. **URL Dinâmica:** URL base configurada via `${conexao.auth.base-url}`
3. **Cache:** Resposta será cacheada pelo TokenValidator (Story SDK-3.3)
4. **Error Decoder:** Erros HTTP traduzidos para exceções da SDK
5. **Javadoc Completo:** Documentar formato JWKS, campos, uso no TokenValidator
6. **Type-Safe:** Tipo de retorno tipado (`JwksResponse`)
7. **Spring Cloud OpenFeign:** Usar anotações `@FeignClient`, `@GetMapping`
8. **Performance:** JWKS deve ser cacheado para evitar chamadas frequentes ao Auth Server

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Created Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/client/JwksClient.java`

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/client/JwksClientUnitTest.java`
- `src/test/java/com/plataforma/conexao/auth/starter/client/JwksClientIntegrationTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-2: Feign Client e Error Decoder](./epic-sdk-2-feign-client.md)
- **Story Anterior:** [Story SDK-2.1: Feign Client - ConexaoAuthClient](./story-sdk-2-1-conexao-auth-client.md)
- **Story Seguinte:** [Story SDK-2.3: Error Decoder Customizado](./story-sdk-2-3-error-decoder.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
- **RFC 7517:** JSON Web Key (JWK) Specification (https://tools.ietf.org/html/rfc7517)
