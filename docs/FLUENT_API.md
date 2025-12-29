# API Fluente do Conexão Auth

## 📖 Visão Geral

A API Fluente fornece uma maneira intuitiva e encadeada de configurar e obter tokens de autenticação OAuth2 do Conexão Auth.

## 🚀 Uso Básico

### 1. Obter Token com Credenciais de Cliente (Client Credentials)

```java
import com.plataforma.conexao.auth.starter.api.ConexaoAuth;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;

TokenResponse token = ConexaoAuth
    .clientId("conexaoauth-client")
    .clientSecret("conexaoauth-secret")
    .realm("my-realm")
    .grantType("client_credentials")
    .execute();

System.out.println("Access Token: " + token.accessToken());
System.out.println("Expires In: " + token.expiresIn() + " segundos");
```

### 2. Obter Token com Credenciais de Usuário (Password Grant)

```java
TokenResponse token = ConexaoAuth
    .clientId("conexaoauth-client")
    .clientSecret("conexaoauth-secret")
    .realm("my-realm")
    .username("admin@example.com")
    .password("Admin@123")
    .grantType("password")
    .execute();

System.out.println("Access Token: " + token.accessToken());
System.out.println("Refresh Token: " + token.refreshToken());
```

### 3. Atualizar Token (Refresh Token)

```java
TokenResponse newToken = ConexaoAuth
    .clientId("conexaoauth-client")
    .clientSecret("conexaoauth-secret")
    .realm("my-realm")
    .refreshToken("old-refresh-token")
    .grantType("refresh_token")
    .execute();

System.out.println("New Access Token: " + newToken.accessToken());
```

### 4. Fluxo de Código de Autorização (Authorization Code)

```java
TokenResponse token = ConexaoAuth
    .clientId("conexaoauth-client")
    .clientSecret("conexaoauth-secret")
    .realm("my-realm")
    .code("authorization-code-from-redirect")
    .redirectUri("https://myapp.com/callback")
    .grantType("authorization_code")
    .execute();
```

### 5. Com Escopo (Scope) Personalizado

```java
TokenResponse token = ConexaoAuth
    .clientId("conexaoauth-client")
    .clientSecret("conexaoauth-secret")
    .realm("my-realm")
    .grantType("client_credentials")
    .scope("read write admin")
    .execute();
```

### 6. Fluxo PKCE (Authorization Code com PKCE)

```java
TokenResponse token = ConexaoAuth
    .clientId("conexaoauth-client")
    .realm("my-realm")
    .code("authorization-code")
    .codeVerifier("pkce-verifier-string")
    .redirectUri("https://myapp.com/callback")
    .grantType("authorization_code")
    .execute();
```

## 📋 Métodos Disponíveis

### Métodos de Entrada

| Método | Descrição | Obrigatório |
|--------|-----------|-------------|
| `ConexaoAuth.builder()` | Inicia uma nova requisição vazia | - |
| `ConexaoAuth.clientId(id)` | Inicia com Client ID preenchido | - |
| `ConexaoAuth.realm(name)` | Inicia com Realm preenchido | - |

### Métodos de Configuração

| Método | Descrição | Quando Usar |
|--------|-----------|------------|
| `grantType(type)` | Tipo de grant OAuth2 | Sempre obrigatório |
| `clientId(id)` | ID do cliente OAuth2 | Sempre obrigatório |
| `clientSecret(secret)` | Secret do cliente OAuth2 | Sempre obrigatório |
| `realm(name)` | Nome do realm no Auth Server | Recomendado |
| `username(user)` | Nome de usuário/e-mail | `password` grant |
| `password(pass)` | Senha do usuário | `password` grant |
| `refreshToken(token)` | Refresh token | `refresh_token` grant |
| `code(code)` | Código de autorização | `authorization_code` grant |
| `redirectUri(uri)` | URI de redirecionamento | `authorization_code` grant |
| `codeVerifier(verifier)` | Verificador PKCE | `authorization_code` + PKCE |
| `scope(scope)` | Escopo da requisição | Opcional |

### Método de Execução

| Método | Descrição |
|--------|-----------|
| `execute()` | Executa a requisição e retorna o token |

## 🎯 Tipos de Grant Suportados

### 1. client_credentials
Usado quando a aplicação (client) precisa de acesso à API sem contexto de usuário.

**Parâmetros obrigatórios:**
- `grantType` = `"client_credentials"`
- `clientId`
- `clientSecret`

**Parâmetros opcionais:**
- `realm`
- `scope`

### 2. password
Usado para autenticação direta com credenciais de usuário (não recomendado para clientes públicos).

**Parâmetros obrigatórios:**
- `grantType` = `"password"`
- `clientId`
- `clientSecret`
- `username`
- `password`

**Parâmetros opcionais:**
- `realm`
- `scope`

### 3. refresh_token
Usado para obter um novo access token usando um refresh token válido.

**Parâmetros obrigatórios:**
- `grantType` = `"refresh_token"`
- `clientId`
- `clientSecret`
- `refreshToken`

**Parâmetros opcionais:**
- `realm`
- `scope`

### 4. authorization_code
Usado no fluxo de código de autorização OAuth2 padrão.

**Parâmetros obrigatórios:**
- `grantType` = `"authorization_code"`
- `clientId`
- `code`
- `redirectUri`

**Parâmetros opcionais:**
- `clientSecret` (opcional para clientes públicos)
- `realm`
- `scope`
- `codeVerifier` (para PKCE)

## ⚠️ Validações

A API valida automaticamente os parâmetros obrigatórios antes de executar a requisição:

```java
// Lança IllegalArgumentException se grantType não for fornecido
TokenResponse token = ConexaoAuth
    .clientId("my-client")
    .clientSecret("my-secret")
    .execute();  // ❌ Erro: grantType é obrigatório

// Lança IllegalArgumentException se parâmetros específicos do grant faltarem
TokenResponse token = ConexaoAuth
    .clientId("my-client")
    .clientSecret("my-secret")
    .grantType("password")
    .execute();  // ❌ Erro: username e password são obrigatórios para grant_type=password
```

## 📦 Retorno (TokenResponse)

O método `execute()` retorna um objeto `TokenResponse` com os seguintes campos:

```java
public record TokenResponse(
    String accessToken,     // Token de acesso JWT
    String tokenType,       // Tipo do token (ex: "Bearer")
    Long expiresIn,         // Tempo de expiração em segundos
    String refreshToken,     // Refresh token (se aplicável)
    String scope           // Escopo concedido
)
```

**Exemplo de uso:**

```java
TokenResponse token = ConexaoAuth
    .clientId("my-client")
    .clientSecret("my-secret")
    .grantType("client_credentials")
    .execute();

// Usar o token em requisições
String authHeader = "Bearer " + token.accessToken();

// Calcular expiração
long expiresAt = System.currentTimeMillis() + (token.expiresIn() * 1000);

// Guardar refresh token (se fornecido)
if (token.refreshToken() != null) {
    System.out.println("Refresh Token: " + token.refreshToken());
}
```

## 🔐 Tratamento de Erros

A API lança exceções em caso de erro:

```java
try {
    TokenResponse token = ConexaoAuth
        .clientId("invalid-client")
        .clientSecret("invalid-secret")
        .grantType("client_credentials")
        .execute();
} catch (IllegalArgumentException e) {
    // Erro de validação de parâmetros
    System.err.println("Erro de validação: " + e.getMessage());
} catch (RuntimeException e) {
    // Erro na comunicação com o Auth Server
    System.err.println("Erro de autenticação: " + e.getMessage());
}
```

## 🎨 Boas Práticas

### 1. Armazenar Tokens Seguramente
```java
// ❌ NÃO: Armazenar em log
System.out.println(token.accessToken());

// ✅ SIM: Armazenar em cache com expiração
tokenCache.put("access_token", token.accessToken(), token.expiresIn());
```

### 2. Usar Refresh Tokens
```java
TokenResponse token = ConexaoAuth
    .clientId("my-client")
    .clientSecret("my-secret")
    .grantType("password")
    .username("user@example.com")
    .password("password")
    .execute();

// Armazenar refresh token para uso posterior
String savedRefreshToken = token.refreshToken();

// Quando o access token expirar, usar o refresh token
TokenResponse newToken = ConexaoAuth
    .clientId("my-client")
    .clientSecret("my-secret")
    .refreshToken(savedRefreshToken)
    .grantType("refresh_token")
    .execute();
```

### 3. Usar Scopes Apropriados
```java
// ❌ NÃO: Solicitar mais escopos do que necessário
.scope("read write delete admin full_access")

// ✅ SIM: Solicitar apenas os escopos necessários
.scope("read write")
```

## 📚 Exemplos Completos

### Exemplo 1: Cliente REST API

```java
@Service
public class ApiService {

    private String accessToken;
    private long tokenExpiresAt;

    @Scheduled(fixedRate = 300000) // A cada 5 minutos
    public void refreshTokenIfNeeded() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpiresAt) {
            TokenResponse token = ConexaoAuth
                .clientId("api-client")
                .clientSecret("api-secret")
                .grantType("client_credentials")
                .scope("read write")
                .execute();

            this.accessToken = token.accessToken();
            this.tokenExpiresAt = System.currentTimeMillis() + (token.expiresIn() * 1000);
        }
    }

    public UserResponse getUser(String userId) {
        refreshTokenIfNeeded();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserResponse> response = restTemplate.exchange(
            "/api/v1/users/" + userId,
            HttpMethod.GET,
            entity,
            UserResponse.class
        );

        return response.getBody();
    }
}
```

### Exemplo 2: Autenticação de Usuário

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse token = ConexaoAuth
                .clientId("web-client")
                .clientSecret("web-secret")
                .username(request.getUsername())
                .password(request.getPassword())
                .grantType("password")
                .execute();

            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Credenciais inválidas");
        }
    }
}
```

## 🔗 Documentação Adicional

- [README Principal](../README.md)
- [Documentação do OAuth2](https://oauth.net/2/)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)
