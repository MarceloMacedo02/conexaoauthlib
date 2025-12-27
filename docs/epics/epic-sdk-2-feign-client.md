# Epic SDK-2: Feign Client e Error Decoder

**Epic:** SDK-2 - Feign Client e Error Decoder
**Status:** Planejado
**Prioridade:** Alta (P0)
**Complexidade:** Média
**Estimativa:** 1 dia

---

## Descrição

Este Epic implementa os clientes Feign para comunicação HTTP com o Auth Server e o Error Decoder customizado que traduz códigos HTTP em exceções de negócio significativas.

---

## Funcionalidades Implementadas

1. **Feign Client - ConexaoAuthClient** - Interface declarativa para endpoints do Auth Server
2. **Feign Client - JwksClient** - Interface declarativa para endpoint JWKS
3. **Error Decoder** - Traduz códigos HTTP em exceções da SDK
4. **DTOs de Request** - Records para requests Feign
5. **DTOs de Response** - Records para responses Feign

---

## Stories do Epic

| # | Story | Prioridade | Estimativa | Status |
|---|-------|-----------|------------|--------|
| SDK-2.1 | Feign Client - ConexaoAuthClient | Alta (P0) | 0.25 dia | Planejado |
| SDK-2.2 | Feign Client - JwksClient | Alta (P0) | 0.15 dia | Planejado |
| SDK-2.3 | Error Decoder Customizado | Alta (P0) | 0.25 dia | Planejado |
| SDK-2.4 | DTOs de Request | Alta (P0) | 0.15 dia | Planejado |
| SDK-2.5 | DTOs de Response | Alta (P0) | 0.2 dia | Planejado |

---

## Dependências

- **Epic SDK-1: Estrutura Básica** - ConexaoAuthProperties e Feign Configuration

---

## Arquitetura do Epic

### Pacote: client

```
com.plataforma.conexao.auth.starter.client/
├── ConexaoAuthClient.java          # Feign Client principal
└── JwksClient.java                # Feign Client para JWKS
```

### Pacote: decoder

```
com.plataforma.conexao.auth.starter.decoder/
└── ConexaoAuthErrorDecoder.java    # Error Decoder customizado
```

### Pacote: dto/request

```
com.plataforma.conexao.auth.starter.dto.request/
├── RegisterUserRequest.java        # DTO para registro de usuário
├── ClientCredentialsRequest.java    # DTO para Client Credentials
└── FindUserByCpfRequest.java      # DTO para busca por CPF
```

### Pacote: dto/response

```
com.plataforma.conexao.auth.starter.dto.response/
├── UserResponse.java               # DTO de resposta de usuário
├── TokenResponse.java             # DTO de resposta de token
└── JwksResponse.java             # DTO de resposta JWKS
```

---

## Componentes Principais

### ConexaoAuthClient

**Responsabilidade:** Interface Feign para comunicação com Auth Server.

**Anotações:**
- `@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")`

**Métodos:**
```java
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface ConexaoAuthClient {

    @PostMapping("/api/v1/usuarios")
    UserResponse registerUser(@RequestBody RegisterUserRequest request);

    @GetMapping("/api/v1/usuarios/cpf/{cpf}")
    UserResponse findUserByCpf(@PathVariable("cpf") String cpf);

    @PostMapping("/oauth2/token")
    TokenResponse clientCredentials(@RequestBody ClientCredentialsRequest request);
}
```

### JwksClient

**Responsabilidade:** Interface Feign para buscar JWKS do Auth Server.

**Anotações:**
- `@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")`

**Métodos:**
```java
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface JwksClient {

    @GetMapping("/.well-known/jwks.json")
    JwksResponse getJwks();
}
```

### ConexaoAuthErrorDecoder

**Responsabilidade:** Traduz códigos HTTP em exceções da SDK.

**Implements:** `feign.codec.ErrorDecoder`

**Lógica de Tradução:**
```java
public class ConexaoAuthErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 401:
                return new UnauthorizedException("Não autorizado");
            case 403:
                return new ForbiddenException("Acesso proibido");
            case 404:
                return new ResourceNotFoundException("Recurso não encontrado");
            case 409:
                return new ConflictException("Conflito de dados");
            default:
                return new ServerException("Erro no servidor", response.status());
        }
    }
}
```

### DTOs de Request

#### RegisterUserRequest
```java
public record RegisterUserRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha,

    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos")
    String cpf,

    @NotBlank(message = "Realm ID é obrigatório")
    String realmId,

    List<String> roleIds,
    String empresaId,
    String tenantId
) {}
```

#### ClientCredentialsRequest
```java
public record ClientCredentialsRequest(
    String grantType,
    String clientId,
    String clientSecret,
    String scope
) {}
```

#### FindUserByCpfRequest
```java
public record FindUserByCpfRequest(
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos")
    String cpf
) {}
```

### DTOs de Response

#### UserResponse
```java
public record UserResponse(
    String id,
    String nome,
    String email,
    String cpf,
    String realmId,
    String realmNome,
    List<String> roles,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao
) {}
```

#### TokenResponse
```java
public record TokenResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    Long expiresIn,

    String scope
) {}
```

#### JwksResponse
```java
public record JwksResponse(
    List<Jwk> keys
) {
    public record Jwk(
        String kid,    // Key ID
        String kty,    // Key Type (RSA)
        String alg,    // Algorithm (RS256)
        String use,    // Use (sig)
        String n,      // Modulus (base64url)
        String e       // Exponent (base64url)
    ) {}
}
```

---

## Critérios de Aceite por Story

### Story SDK-2.1: Feign Client - ConexaoAuthClient

- [ ] Interface `ConexaoAuthClient` criada com anotação @FeignClient
- [ ] URL configurada via `${conexao.auth.base-url}`
- [ ] Método `registerUser()` configurado com @PostMapping
- [ ] Método `findUserByCpf()` configurado com @GetMapping
- [ ] Método `clientCredentials()` configurado com @PostMapping
- [ ] Headers automáticos configurados (X-Client-Id, X-Realm-Id)
- [ ] Error Decoder injetado via auto-configuração

### Story SDK-2.2: Feign Client - JwksClient

- [ ] Interface `JwksClient` criada com anotação @FeignClient
- [ ] Método `getJwks()` configurado com @GetMapping para `/.well-known/jwks.json`
- [ ] Jackson deserializa JwksResponse corretamente

### Story SDK-2.3: Error Decoder Customizado

- [ ] Classe `ConexaoAuthErrorDecoder` implementa `ErrorDecoder`
- [ ] 401 → `UnauthorizedException`
- [ ] 403 → `ForbiddenException`
- [ ] 404 → `ResourceNotFoundException`
- [ ] 409 → `ConflictException`
- [ ] 500+ → `ServerException`
- [ ] Stack trace original preservada para debugging
- [ ] Mensagens de erro em Português

### Story SDK-2.4: DTOs de Request

- [ ] `RegisterUserRequest` criado como record com validações
- [ ] `ClientCredentialsRequest` criado como record
- [ ] `FindUserByCpfRequest` criado como record com validações
- [ ] Validações Jakarta Bean Validation funcionam
- [ ] @JsonProperty usado para mapeamento correto

### Story SDK-2.5: DTOs de Response

- [ ] `UserResponse` criado como record com todos os campos
- [ ] `TokenResponse` criado como record
- [ ] `JwksResponse` criado como record com Jwk aninhado
- [ ] @JsonProperty usado para snake_case do Auth Server
- [ ] Jackson deserializa JSON corretamente

---

## Exemplo de Uso

### Chamada via ConexaoAuthClient

```java
@Autowired
private ConexaoAuthClient conexaoAuthClient;

// Registrar usuário
RegisterUserRequest request = new RegisterUserRequest(
    "João Silva",
    "joao@example.com",
    "Senha@123",
    "12345678901",
    "master",
    List.of("USER"),
    null,
    null
);
UserResponse user = conexaoAuthClient.registerUser(request);
```

### Chamada via JwksClient

```java
@Autowired
private JwksClient jwksClient;

// Buscar chaves públicas JWKS
JwksResponse jwks = jwksClient.getJwks();
List<JwksResponse.Jwk> keys = jwks.keys();
```

---

## Tecnologias Utilizadas

- **Spring Cloud OpenFeign** - Cliente HTTP declarativo
- **OkHttp** - Cliente HTTP subjacente
- **Jackson** - Serialização/Deserialização JSON
- **Jakarta Validation** - Validação de DTOs
- **Lombok** - Redução de boilerplate

---

## Testes Requeridos

### Testes Unitários

- Teste de Error Decoder para cada código HTTP
- Teste de validação de DTOs de Request
- Teste de serialização/desserialização de DTOs

### Testes de Integração

- Teste de chamada HTTP via ConexaoAuthClient (mock server)
- Teste de chamada HTTP via JwksClient (mock server)
- Teste de erro HTTP traduzido para exceção correta

---

## Pontos de Atenção

1. **Headers Automáticos:** Configurar headers X-Client-Id e X-Realm-Id em todas as requisições
2. **Error Decoder:** Garantir que todas as exceções preservem stack trace original
3. **Jackson Mapeamento:** Usar @JsonProperty para snake_case do Auth Server
4. **Timeouts:** Configurar connection-timeout e read-timeout via ConexaoAuthProperties
5. **Retry Logic:** Configurar 3 tentativas com exponential backoff

---

## Próximos Passos

Após conclusão deste Epic:
1. **Epic SDK-3: Token Validator** - Implementar validação JWT e cache JWKS
2. **Epic SDK-4: Auth Service** - Implementar serviços de alto nível
3. **Epic SDK-5: Testes e Documentação** - Implementar testes abrangentes e documentação

---

## Estatísticas do Epic

| Métrica | Quantidade |
|---------|------------|
| **Stories** | 5 |
| **Interfaces Feign** | 2 |
| **DTOs** | 6 |
| **Classes Java** | 1 |
| **Testes Estimados** | 10-12 |
| **Linhas de Código** | ~250-300 |
