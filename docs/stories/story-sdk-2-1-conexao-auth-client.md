# Story SDK-2.1: Feign Client - ConexaoAuthClient

**Epic:** SDK-2 - Feign Client e Error Decoder
**Story:** SDK-2.1
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.25 dia
**Complexidade:** Baixa

---

## Descrição

Implementar a interface Feign Client `ConexaoAuthClient` para comunicação com os endpoints do Auth Server. Esta story expande o stub criado na Story SDK-1.1.

---

## Critérios de Aceite

- [ ] Interface `ConexaoAuthClient` expandida com endpoints completos
- [ ] Anotação `@FeignClient` configurada corretamente
- [ ] Endpoint `POST /api/v1/usuarios` definido para registro de usuário
- [ ] Endpoint `GET /api/v1/usuarios/cpf/{cpf}` definido para busca por CPF
- [ ] Endpoint `POST /oauth2/token` definido para Client Credentials Flow
- [ ] Interface usa DTOs de request e response corretos
- [ ] Javadoc completo em Português para todos os métodos
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Feign Client Declarativo:** Usar anotações Feign para definição declarativa de endpoints
2. **URL Dinâmica:** URL base do Auth Server configurada via `conexao.auth.base-url`
3. **Tipo de Retorno:** Todos os endpoints retornam DTOs de response tipados
4. **Error Decoder:** Erros HTTP são traduzidos para exceções da SDK pelo `ConexaoAuthErrorDecoder`

---

## Requisitos Técnicos

### Interface ConexaoAuthClient

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/client/ConexaoAuthClient.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.client;

import com.plataforma.conexao.auth.starter.dto.request.ClientCredentialsRequest;
import com.plataforma.conexao.auth.starter.dto.request.RegisterUserRequest;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import com.plataforma.conexao.auth.starter.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Interface Feign Client para comunicação com o Conexão Auth Server.
 *
 * <p>Esta interface define os endpoints do Auth Server de forma declarativa.
 * O Feign gera a implementação automaticamente em tempo de execução.
 *
 * <p><b>Configuração:</b>
 * <ul>
 *   <li>URL base: ${conexao.auth.base-url} (configurado via application.yml)</li>
 *   <li>Headers X-Client-Id e X-Realm-Id são injetados automaticamente pelo FeignClientBuilder</li>
 *   <li>Erros HTTP são traduzidos para exceções da SDK pelo ConexaoAuthErrorDecoder</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @see com.plataforma.conexao.auth.starter.config.ConexaoAuthAutoConfiguration
 */
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface ConexaoAuthClient {

    /**
     * Registra um novo usuário no Auth Server.
     *
     * <p>Este endpoint cria um novo usuário no realm configurado.
     * O Auth Server valida os dados e retorna o usuário criado.
     *
     * <p><b>Endpoint:</b> POST /api/v1/usuarios
     *
     * <p><b>Status HTTP:</b>
     * <ul>
     *   <li>201 Created - Usuário criado com sucesso</li>
     *   <li>400 Bad Request - Dados inválidos (validação falhou)</li>
     *   <li>409 Conflict - Email/CPF já cadastrado</li>
     *   <li>401 Unauthorized - Credenciais inválidas</li>
     *   <li>500 Server Error - Erro interno do servidor</li>
     * </ul>
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>UnauthorizedException - Credenciais inválidas (401)</li>
     *   <li>ConflictException - Email/CPF já cadastrado (409)</li>
     *   <li>ServerException - Erro interno do servidor (500+)</li>
     * </ul>
     *
     * @param request DTO com dados do usuário (nome, email, senha, cpf, realmId, etc.)
     * @return DTO com dados do usuário criado (id, nome, email, cpf, realm, roles, status, etc.)
     * @throws UnauthorizedException Se credenciais inválidas (401)
     * @throws ConflictException Se email/CPF já cadastrado (409)
     * @throws ServerException Se erro interno do servidor (500+)
     */
    @PostMapping("/api/v1/usuarios")
    UserResponse registerUser(@RequestBody RegisterUserRequest request);

    /**
     * Busca usuário por CPF.
     *
     * <p>Este endpoint busca um usuário pelo CPF no realm configurado.
     * O Auth Server retorna o usuário encontrado ou 404 se não existir.
     *
     * <p><b>Endpoint:</b> GET /api/v1/usuarios/cpf/{cpf}
     *
     * <p><b>Status HTTP:</b>
     * <ul>
     *   <li>200 OK - Usuário encontrado</li>
     *   <li>404 Not Found - Usuário não encontrado</li>
     *   <li>401 Unauthorized - Credenciais inválidas</li>
     *   <li>500 Server Error - Erro interno do servidor</li>
     * </ul>
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>ResourceNotFoundException - Usuário não encontrado (404)</li>
     *   <li>UnauthorizedException - Credenciais inválidas (401)</li>
     *   <li>ServerException - Erro interno do servidor (500+)</li>
     * </ul>
     *
     * @param cpf CPF do usuário (11 dígitos, apenas números)
     * @return DTO com dados do usuário encontrado
     * @throws ResourceNotFoundException Se usuário não encontrado (404)
     * @throws UnauthorizedException Se credenciais inválidas (401)
     * @throws ServerException Se erro interno do servidor (500+)
     */
    @GetMapping("/api/v1/usuarios/cpf/{cpf}")
    UserResponse findUserByCpf(@PathVariable("cpf") String cpf);

    /**
     * Solicita token via Client Credentials Flow.
     *
     * <p>Este endpoint solicita um token de acesso usando Client Credentials Flow OAuth2.
     * O Auth Server valida as credenciais do client e retorna um token de acesso.
     *
     * <p><b>Endpoint:</b> POST /oauth2/token
     *
     * <p><b>Request Body:</b>
     * <pre>
     * {
     *   "grant_type": "client_credentials",
     *   "client_id": "meu-client-id",
     *   "client_secret": "meu-client-secret",
     *   "scope": "read write"
     * }
     * </pre>
     *
     * <p><b>Response Body:</b>
     * <pre>
     * {
     *   "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "token_type": "Bearer",
     *   "expires_in": 3600,
     *   "scope": "read write"
     * }
     * </pre>
     *
     * <p><b>Status HTTP:</b>
     * <ul>
     *   <li>200 OK - Token gerado com sucesso</li>
     *   <li>400 Bad Request - Credenciais inválidas</li>
     *   <li>401 Unauthorized - Client ID/Secret inválidos</li>
     *   <li>500 Server Error - Erro interno do servidor</li>
     * </ul>
     *
     * <p><b>Exceções:</b>
     * <ul>
     *   <li>UnauthorizedException - Client ID/Secret inválidos (401)</li>
     *   <li>ServerException - Erro interno do servidor (500+)</li>
     * </ul>
     *
     * @param request DTO com credenciais do client (grantType, clientId, clientSecret, scope)
     * @return DTO com token de acesso (accessToken, tokenType, expiresIn, scope)
     * @throws UnauthorizedException Se Client ID/Secret inválidos (401)
     * @throws ServerException Se erro interno do servidor (500+)
     */
    @PostMapping("/oauth2/token")
    TokenResponse clientCredentials(@RequestBody ClientCredentialsRequest request);
}
```

---

## Exemplos de Testes

### Teste de Integração com MockMvc

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
class ConexaoAuthClientIntegrationTest {

    @Autowired
    private ConexaoAuthClient conexaoAuthClient;

    @MockBean
    private feign.Client feignClient;

    @Test
    @DisplayName("Dado request válido, quando registrar usuário, então usuário criado")
    void dadoRequestValido_quandoRegistrarUsuario_entaoUsuarioCriado() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "joao.silva@example.com",
            "senhaSegura123",
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act & Assert (será implementado com @MockBean em testes de integração reais)
        // Este é um exemplo conceitual
        assertThat(conexaoAuthClient).isNotNull();
    }
}
```

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
 * Testes unitários de ConexaoAuthClient usando Feign Mock.
 */
@DisplayName("Testes Unitários - ConexaoAuthClient")
class ConexaoAuthClientUnitTest {

    private ConexaoAuthClient client;
    private MockClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = new MockClient();
        client = Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .client(mockClient)
                .target(new MockTarget<>(ConexaoAuthClient.class));
    }

    @Test
    @DisplayName("Dado usuário criado no mock, quando buscar por CPF, então usuário retornado")
    void dadoUsuarioCriadoNoMock_quandoBuscarPorCpf_entaoUsuarioRetornado() {
        // Arrange
        String cpf = "12345678901";
        UserResponse expectedResponse = new UserResponse(
            "user-123",
            "João Silva",
            "joao.silva@example.com",
            cpf,
            "master",
            "Master",
            List.of("role-user"),
            "ATIVO",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        mockClient.ok(request(HttpMethod.GET, "/api/v1/usuarios/cpf/" + cpf), expectedResponse);

        // Act
        UserResponse actualResponse = client.findUserByCpf(cpf);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.cpf()).isEqualTo(cpf);
        assertThat(actualResponse.nome()).isEqualTo("João Silva");
        assertThat(actualResponse.email()).isEqualTo("joao.silva@example.com");
    }

    @Test
    @DisplayName("Dado token gerado no mock, quando solicitar token, então token retornado")
    void dadoTokenGeradoNoMock_quandoSolicitarToken_entaoTokenRetornado() {
        // Arrange
        ClientCredentialsRequest request = new ClientCredentialsRequest(
            "client_credentials",
            "test-client",
            "test-secret",
            "read write"
        );

        TokenResponse expectedResponse = new TokenResponse(
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
            "Bearer",
            3600L,
            "read write"
        );

        mockClient.ok(request(HttpMethod.POST, "/oauth2/token"), expectedResponse);

        // Act
        TokenResponse actualResponse = client.clientCredentials(request);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.accessToken()).isNotBlank();
        assertThat(actualResponse.tokenType()).isEqualTo("Bearer");
        assertThat(actualResponse.expiresIn()).isEqualTo(3600L);
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (criou stub desta interface)
- **Story SDK-1.2:** Configuration Properties (define `conexao.auth.base-url`)
- **Story SDK-1.3:** Feign Configuration (configura encoder/decoder/OkHttp)
- **Story SDK-2.4:** DTOs de Request (serão expandidos nesta story)
- **Story SDK-2.5:** DTOs de Response (serão expandidos nesta story)
- **Story SDK-2.3:** Error Decoder Customizado (traduz erros HTTP)

---

## Pontos de Atenção

1. **Feign Client Declarativo:** Usar anotações Feign para definição declarativa de endpoints
2. **URL Dinâmica:** URL base configurada via `${conexao.auth.base-url}`
3. **Headers Automáticos:** Headers `X-Client-Id` e `X-Realm-Id` injetados pelo FeignClientBuilder
4. **Error Decoder:** Erros HTTP traduzidos para exceções da SDK pelo ConexaoAuthErrorDecoder
5. **Javadoc Completo:** Documentar todos os métodos em Português com exemplos de status HTTP e exceções
6. **Type-Safe:** Tipos de retorno tipados (DTOs de response)
7. **Jakarta Validation:** DTOs de request têm anotações de validação
8. **Spring Cloud OpenFeign:** Usar anotações `@FeignClient`, `@GetMapping`, `@PostMapping`

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Updated Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/client/ConexaoAuthClient.java` (expandido)

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/client/ConexaoAuthClientIntegrationTest.java`
- `src/test/java/com/plataforma/conexao/auth/starter/client/ConexaoAuthClientUnitTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-2: Feign Client e Error Decoder](./epic-sdk-2-feign-client.md)
- **Story Anterior:** [Story SDK-1.4: Estrutura de Pacotes e Imports](./story-sdk-1-4-estrutura-pacotes-imports.md)
- **Story Seguinte:** [Story SDK-2.2: Feign Client - JwksClient](./story-sdk-2-2-jwks-client.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
