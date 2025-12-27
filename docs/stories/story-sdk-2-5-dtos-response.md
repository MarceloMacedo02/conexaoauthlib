# Story SDK-2.5: DTOs de Response

**Epic:** SDK-2 - Feign Client e Error Decoder
**Story:** SDK-2.5
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.2 dia
**Complexidade:** Baixa

---

## Descrição

Expandir os DTOs de Response criados como stubs na Story SDK-1.1, convertendo-os para Java 21 records completos.

---

## Critérios de Aceite

- [ ] DTOs de Response expandidos para Java 21 records
- [ ] Javadoc completo em Português para todos os campos
- [ ] Anotações `@JsonProperty` para campos snake_case
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Records Java 21:** Todos os DTOs devem usar Java 21 records
2. **JsonProperty:** Usar `@JsonProperty` para campos snake_case (ex: `realm_id`)
3. **Imutabilidade:** DTOs são imutáveis por serem records
4. **Tipagem Forte:** Todos os campos devem ter tipos específicos (ex: `LocalDateTime` em vez de `String` para datas)

---

## Requisitos Técnicos

### DTO UserResponse

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/response/UserResponse.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para dados de usuário.
 *
 * <p>Este DTO contém todos os dados de um usuário retornados pelo Auth Server.
 *
 * <p><b>Campos:</b>
 * <table>
 *   <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
 *   <tr><td>id</td><td>String</td><td>ID único do usuário (UUID)</td></tr>
 *   <tr><td>nome</td><td>String</td><td>Nome completo do usuário</td></tr>
 *   <tr><td>email</td><td>String</td><td>Email do usuário</td></tr>
 *   <tr><td>cpf</td><td>String</td><td>CPF do usuário (11 dígitos)</td></tr>
 *   <tr><td>realmId</td><td>String</td><td>ID do Realm do usuário</td></tr>
 *   <tr><td>realmNome</td><td>String</td><td>Nome do Realm do usuário</td></tr>
 *   <tr><td>roles</td><td>List&lt;String&gt;</td><td>Lista de IDs/nomes de roles do usuário</td></tr>
 *   <tr><td>status</td><td>String</td><td>Status do usuário (ATIVO, BLOQUEADO)</td></tr>
 *   <tr><td>dataCriacao</td><td>LocalDateTime</td><td>Data de criação do usuário</td></tr>
 *   <tr><td>dataUltimaAtualizacao</td><td>LocalDateTime</td><td>Data da última atualização</td></tr>
 * </table>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * UserResponse user = conexaoAuthService.findUserByCpf("12345678901");
 *
 * log.info("Usuário: {}", user.nome());
 * log.info("Email: {}", user.email());
 * log.info("Status: {}", user.status());
 * log.info("Roles: {}", user.roles());
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record UserResponse(
    /**
     * ID único do usuário (UUID).
     *
     * <p>Gerado pelo Auth Server na criação do usuário.
     */
    String id,

    /**
     * Nome completo do usuário.
     */
    String nome,

    /**
     * Email do usuário.
     *
     * <p>Único no sistema.
     */
    String email,

    /**
     * CPF do usuário (11 dígitos).
     *
     * <p>Opcional, único no sistema se fornecido.
     */
    String cpf,

    /**
     * ID do Realm do usuário.
     *
     * <p>Correspondente ao campo realm_id no JSON.
     */
    @JsonProperty("realm_id")
    String realmId,

    /**
     * Nome do Realm do usuário.
     *
     * <p>Correspondente ao campo realm_nome no JSON.
     */
    @JsonProperty("realm_nome")
    String realmNome,

    /**
     * Lista de roles do usuário.
     *
     * <p>Lista de IDs ou nomes de roles associadas ao usuário.
     */
    List<String> roles,

    /**
     * Status do usuário.
     *
     * <p>Valores possíveis: ATIVO, BLOQUEADO.
     */
    String status,

    /**
     * Data de criação do usuário.
     *
     * <p>Correspondente ao campo data_criacao no JSON.
     * Formato ISO 8601.
     */
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,

    /**
     * Data da última atualização.
     *
     * <p>Correspondente ao campo data_ultima_atualizacao no JSON.
     * Formato ISO 8601.
     */
    @JsonProperty("data_ultima_atualizacao")
    LocalDateTime dataUltimaAtualizacao
) {}
```

### DTO TokenResponse

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/response/TokenResponse.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resposta para token OAuth2.
 *
 * <p>Este DTO contém o token de acesso e metadados retornados pelo Auth Server
 * em response a uma solicitação de token (ex: Client Credentials Flow).
 *
 * <p><b>Campos:</b>
 * <table>
 *   <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
 *   <tr><td>accessToken</td><td>String</td><td>Access Token JWT</td></tr>
 *   <tr><td>tokenType</td><td>String</td><td>Tipo de token (geralmente "Bearer")</td></tr>
 *   <tr><td>expiresIn</td><td>Long</td><td>Tempo de expiração em segundos</td></tr>
 *   <tr><td>scope</td><td>String</td><td>Escopos concedidos</td></tr>
 * </table>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * ClientCredentialsRequest request = new ClientCredentialsRequest(
 *     "client_credentials",
 *     "meu-client-id",
 *     "meu-client-secret",
 *     "read write"
 * );
 *
 * TokenResponse response = conexaoAuthService.getClientCredentialsToken(request);
 *
 * String token = response.accessToken();
 * Long expiresIn = response.expiresIn(); // em segundos
 *
 * log.info("Token: {} (expira em {} segundos)", token, expiresIn);
 *
 * // Usar token em requisições HTTP
 * headers.add("Authorization", "Bearer " + token);
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record TokenResponse(
    /**
     * Access Token JWT.
     *
     * <p>Token de acesso que pode ser usado para autenticar requisições
     * ao Auth Server e outras APIs protegidas.
     *
     * <p>Formato: JWT (JSON Web Token)
     * <p>Correspondente ao campo access_token no JSON.
     */
    @JsonProperty("access_token")
    String accessToken,

    /**
     * Tipo de token.
     *
     * <p>Geralmente "Bearer".
     * <p>Correspondente ao campo token_type no JSON.
     */
    @JsonProperty("token_type")
    String tokenType,

    /**
     * Tempo de expiração em segundos.
     *
     * <p>Tempo até o token expirar (em segundos).
     * <p>Correspondente ao campo expires_in no JSON.
     */
    @JsonProperty("expires_in")
    Long expiresIn,

    /**
     * Escopos concedidos.
     *
     * <p>Lista de escopos separados por espaço (ex: "read write").
     * <p>Correspondente ao campo scope no JSON.
     */
    @JsonProperty("scope")
    String scope
) {}
```

### DTO JwksResponse

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/response/JwksResponse.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO de resposta para endpoint JWKS (JSON Web Key Set).
 *
 * <p>Este DTO contém o conjunto de chaves públicas RSA usadas pelo Auth Server
 * para assinar tokens JWT.
 *
 * <p><b>Finalidade:</b>
 * Com o JWKS em cache local, é possível validar tokens JWT sem fazer
 * chamadas ao Auth Server. Isso reduz latência e aumenta resiliência.
 *
 * <p><b>Campos:</b>
 * <table>
 *   <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
 *   <tr><td>keys</td><td>List&lt;Jwk&gt;</td><td>Lista de chaves públicas RSA</td></tr>
 * </table>
 *
 * <p><b>Estrutura JWK (JSON Web Key):</b>
 * <table>
 *   <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
 *   <tr><td>kid</td><td>String</td><td>Key ID (identificador único da chave)</td></tr>
 *   <tr><td>kty</td><td>String</td><td>Key Type (RSA)</td></tr>
 *   <tr><td>alg</td><td>String</td><td>Algorithm (RS256)</td></tr>
 *   <tr><td>use</td><td>String</td><td>Use (sig = signature)</td></tr>
 *   <tr><td>n</td><td>String</td><td>Modulus (base64url)</td></tr>
 *   <tr><td>e</td><td>String</td><td>Exponent (base64url)</td></tr>
 * </table>
 *
 * <p><b>Exemplo de Uso:</b>
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
 * @author Conexão Auth Team
 * @version 1.0.0
 * @see com.plataforma.conexao.auth.starter.client.JwksClient
 * @see com.plataforma.conexao.auth.starter.service.TokenValidator
 */
public record JwksResponse(
    /**
     * Lista de chaves públicas RSA.
     *
     * <p> Cada chave pública representa uma chave usada pelo Auth Server
     * para assinar tokens JWT. Pode haver múltiplas chaves para rotation
     * (substituição de chaves sem downtime).
     */
    @JsonProperty("keys")
    List<Jwk> keys
) {
    /**
     * JSON Web Key (JWK) - Chave pública RSA.
     *
     * <p>Representação de uma chave pública RSA no formato JSON Web Key (RFC 7517).
     *
     * <p><b>Campos:</b>
     * <ul>
     *   <li><b>kid</b>: Key ID (identificador único da chave)</li>
     *   <li><b>kty</b>: Key Type (RSA)</li>
     *   <li><b>alg</b>: Algorithm (RS256)</li>
     *   <li><b>use</b>: Use (sig = signature)</li>
     *   <li><b>n</b>: Modulus (base64url-encoded)</li>
     *   <li><b>e</b>: Exponent (base64url-encoded)</li>
     * </ul>
     */
    public record Jwk(
        /**
         * Key ID (identificador único da chave).
         *
         * <p>Usado para identificar qual chave foi usada para assinar um token JWT.
         * O token JWT contém o kid no header.
         */
        String kid,

        /**
         * Key Type.
         *
         * <p>Geralmente "RSA" para chaves RSA.
         */
        String kty,

        /**
         * Algorithm.
         *
         * <p>Geralmente "RS256" para RSA Signature with SHA-256.
         */
        String alg,

        /**
         * Use.
         *
         * <p>Geralmente "sig" (signature).
         */
        String use,

        /**
         * Modulus (base64url-encoded).
         *
         * <p>Parte da chave pública RSA.
         */
        String n,

        /**
         * Exponent (base64url-encoded).
         *
         * <p>Parte da chave pública RSA. Geralmente "AQAB".
         */
        String e
    ) {}
}
```

### DTO RegisterUserResponse

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/response/RegisterUserResponse.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para registro de usuário.
 *
 * <p>Este DTO contém os dados do usuário criado após o registro.
 * É uma especialização de UserResponse com campos adicionais específicos
 * para a resposta de registro.
 *
 * <p><b>Campos:</b>
 * <table>
 *   <tr><th>Campo</th><th>Tipo</th><th>Descrição</th></tr>
 *   <tr><td>id</td><td>String</td><td>ID único do usuário (UUID)</td></tr>
 *   <tr><td>nome</td><td>String</td><td>Nome completo do usuário</td></tr>
 *   <tr><td>email</td><td>String</td><td>Email do usuário</td></tr>
 *   <tr><td>cpf</td><td>String</td><td>CPF do usuário (11 dígitos)</td></tr>
 *   <tr><td>realmId</td><td>String</td><td>ID do Realm do usuário</td></tr>
 *   <tr><td>realmNome</td><td>String</td><td>Nome do Realm do usuário</td></tr>
 *   <tr><td>roles</td><td>List&lt;String&gt;</td><td>Lista de roles do usuário</td></tr>
 *   <tr><td>status</td><td>String</td><td>Status do usuário (ATIVO)</td></tr>
 *   <tr><td>dataCriacao</td><td>LocalDateTime</td><td>Data de criação</td></tr>
 *   <tr><td>dataUltimaAtualizacao</td><td>LocalDateTime</td><td>Data da última atualização</td></tr>
 * </table>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * RegisterUserRequest request = new RegisterUserRequest(
 *     "João Silva",
 *     "joao.silva@example.com",
 *     "senhaSegura123",
 *     "12345678901",
 *     "master",
 *     List.of("role-user"),
 *     null,
 *     null
 * );
 *
 * UserResponse response = conexaoAuthService.registerUser(request);
 *
 * log.info("Usuário criado com ID: {}", response.id());
 * log.info("Nome: {}", response.nome());
 * log.info("Email: {}", response.email());
 * log.info("Status: {}", response.status());
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record RegisterUserResponse(
    /**
     * ID único do usuário (UUID).
     *
     * <p>Gerado pelo Auth Server na criação do usuário.
     */
    String id,

    /**
     * Nome completo do usuário.
     */
    String nome,

    /**
     * Email do usuário.
     *
     * <p>Único no sistema.
     */
    String email,

    /**
     * CPF do usuário (11 dígitos).
     *
     * <p>Opcional, único no sistema se fornecido.
     */
    String cpf,

    /**
     * ID do Realm do usuário.
     *
     * <p>Correspondente ao campo realm_id no JSON.
     */
    @JsonProperty("realm_id")
    String realmId,

    /**
     * Nome do Realm do usuário.
     *
     * <p>Correspondente ao campo realm_nome no JSON.
     */
    @JsonProperty("realm_nome")
    String realmNome,

    /**
     * Lista de roles do usuário.
     *
     * <p>Lista de IDs ou nomes de roles associadas ao usuário.
     */
    List<String> roles,

    /**
     * Status do usuário.
     *
     * <p>Após registro, geralmente ATIVO.
     */
    String status,

    /**
     * Data de criação do usuário.
     *
     * <p>Correspondente ao campo data_criacao no JSON.
     * Formato ISO 8601.
     */
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,

    /**
     * Data da última atualização.
     *
     * <p>Correspondente ao campo data_ultima_atualizacao no JSON.
     * Formato ISO 8601.
     */
    @JsonProperty("data_ultima_atualizacao")
    LocalDateTime dataUltimaAtualizacao
) {}
```

---

## Exemplos de Testes

### Teste de Desserialização - UserResponse

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de desserialização de DTOs de Response.
 */
@DisplayName("Testes de Desserialização - DTOs de Response")
class ResponseDtoDeserializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Dado JSON válido, quando desserializar UserResponse, então DTO criado")
    void dadoJsonValido_quandoDesserializarUserResponse_entaoDtoCriado() throws Exception {
        // Arrange
        String json = """
            {
                "id": "user-123",
                "nome": "João Silva",
                "email": "joao.silva@example.com",
                "cpf": "12345678901",
                "realm_id": "master",
                "realm_nome": "Master",
                "roles": ["role-user", "role-admin"],
                "status": "ATIVO",
                "data_criacao": "2025-01-01T00:00:00",
                "data_ultima_atualizacao": "2025-01-01T00:00:00"
            }
            """;

        // Act
        UserResponse response = objectMapper.readValue(json, UserResponse.class);

        // Assert
        assertThat(response.id()).isEqualTo("user-123");
        assertThat(response.nome()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao.silva@example.com");
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.realmId()).isEqualTo("master");
        assertThat(response.realmNome()).isEqualTo("Master");
        assertThat(response.roles()).containsExactlyInAnyOrder("role-user", "role-admin");
        assertThat(response.status()).isEqualTo("ATIVO");
        assertThat(response.dataCriacao()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
    }

    @Test
    @DisplayName("Dado JSON válido, quando desserializar TokenResponse, então DTO criado")
    void dadoJsonValido_quandoDesserializarTokenResponse_entaoDtoCriado() throws Exception {
        // Arrange
        String json = """
            {
                "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                "token_type": "Bearer",
                "expires_in": 3600,
                "scope": "read write"
            }
            """;

        // Act
        TokenResponse response = objectMapper.readValue(json, TokenResponse.class);

        // Assert
        assertThat(response.accessToken()).startsWith("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        assertThat(response.scope()).isEqualTo("read write");
    }

    @Test
    @DisplayName("Dado JSON válido, quando desserializar JwksResponse, então DTO criado")
    void dadoJsonValido_quandoDesserializarJwksResponse_entaoDtoCriado() throws Exception {
        // Arrange
        String json = """
            {
                "keys": [
                    {
                        "kid": "key-1",
                        "kty": "RSA",
                        "alg": "RS256",
                        "use": "sig",
                        "n": "base64url-encoded-modulus",
                        "e": "AQAB"
                    },
                    {
                        "kid": "key-2",
                        "kty": "RSA",
                        "alg": "RS256",
                        "use": "sig",
                        "n": "base64url-encoded-modulus-2",
                        "e": "AQAB"
                    }
                ]
            }
            """;

        // Act
        JwksResponse response = objectMapper.readValue(json, JwksResponse.class);

        // Assert
        assertThat(response.keys()).hasSize(2);
        assertThat(response.keys().get(0).kid()).isEqualTo("key-1");
        assertThat(response.keys().get(0).kty()).isEqualTo("RSA");
        assertThat(response.keys().get(0).alg()).isEqualTo("RS256");
        assertThat(response.keys().get(1).kid()).isEqualTo("key-2");
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (criou stubs destes DTOs)
- **Story SDK-2.1:** Feign Client - ConexaoAuthClient (usa estes DTOs)
- **Story SDK-2.2:** Feign Client - JwksClient (usa JwksResponse)
- **Story SDK-3.2:** Token Validator Implementation (usa JwksResponse e JwksResponse.Jwk)

---

## Pontos de Atenção

1. **Records Java 21:** Todos os DTOs devem usar Java 21 records
2. **JsonProperty:** Usar `@JsonProperty` para campos snake_case (ex: `realm_id`, `access_token`)
3. **Javadoc Completo:** Documentar todos os campos com descrições detalhadas
4. **Tipagem Forte:** Usar tipos específicos (ex: `LocalDateTime` em vez de `String` para datas)
5. **Jackson:** Configurar ObjectMapper com JavaTimeModule para desserialização correta de LocalDateTime
6. **JWK RFC 7517:** Seguir especificação RFC 7517 para campos do JWK
7. **ISO 8601:** Datas devem ser formatadas em ISO 8601
8. **Imutabilidade:** DTOs são imutáveis por serem records

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Updated Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/dto/response/UserResponse.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/dto/response/TokenResponse.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/dto/response/JwksResponse.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/dto/response/RegisterUserResponse.java` (nova)

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/dto/response/ResponseDtoDeserializationTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-2: Feign Client e Error Decoder](./epic-sdk-2-feign-client.md)
- **Story Anterior:** [Story SDK-2.4: DTOs de Request](./story-sdk-2-4-dtos-request.md)
- **Epic Seguinte:** [Epic SDK-3: Token Validator e JWKS](./epic-sdk-3-token-validator.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
- **RFC 7517:** JSON Web Key (JWK) Specification (https://tools.ietf.org/html/rfc7517)
