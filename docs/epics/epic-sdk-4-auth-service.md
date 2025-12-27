# Epic SDK-4: Auth Service - Serviços de Alto Nível

**Epic:** SDK-4 - Auth Service
**Status:** Planejado
**Prioridade:** Alta (P1)
**Complexidade:** Média
**Estimativa:** 1 dia

---

## Descrição

Este Epic implementa os serviços de alto nível expostos pela aplicação consumidora. O Auth Service abstrai a complexidade dos Feign Clients e fornece métodos intuitivos para operações de identidade.

---

## Funcionalidades Implementadas

1. **Auth Service Interface** - Interface do serviço de alto nível
2. **Auth Service Implementation** - Implementação com lógica de negócio
3. **Registro de Usuário** - Método `registerUser()`
4. **Busca de Usuário por Identificador** - Método `findUserByIdentifier()`
5. **Client Credentials Flow** - Método `getClientCredentialsToken()`
6. **Validação de Permissões** - Método `validatePermissions()`
7. **Refresh Token** - Método `refreshToken()` (opcional)

---

## Stories do Epic

| # | Story | Prioridade | Estimativa | Status |
|---|-------|-----------|------------|--------|
| SDK-4.1 | Auth Service Interface | Alta (P1) | 0.1 dia | Planejado |
| SDK-4.2 | Registro de Usuário | Alta (P1) | 0.25 dia | Planejado |
| SDK-4.3 | Busca de Usuário por Identificador | Média (P2) | 0.2 dia | Planejado |
| SDK-4.4 | Client Credentials Flow | Alta (P1) | 0.2 dia | Planejado |
| SDK-4.5 | Validação de Permissões | Média (P2) | 0.15 dia | Planejado |
| SDK-4.6 | Refresh Token | Baixa (P2) | 0.1 dia | Planejado |

---

## Dependências

- **Epic SDK-1: Estrutura Básica** - ConexaoAuthProperties
- **Epic SDK-2: Feign Client** - ConexaoAuthClient
- **Epic SDK-3: Token Validator** - TokenValidator

---

## Arquitetura do Epic

### Pacote: service

```
com.plataforma.conexao.auth.starter.service/
├── ConexaoAuthService.java         # Interface do serviço
└── ConexaoAuthServiceImpl.java     # Implementação
```

---

## Componentes Principais

### ConexaoAuthService (Interface)

**Responsibilidade:** Interface do serviço de alto nível para a aplicação.

**Métodos:**
```java
public interface ConexaoAuthService {

    /**
     * Registra um novo usuário no Conexão Auth.
     *
     * @param request DTO com dados do usuário
     * @return UserResponse com dados do usuário criado
     * @throws ConflictException se email já existir
     * @throws ResourceNotFoundException se realm não existir
     */
    UserResponse registerUser(RegisterUserRequest request);

    /**
     * Busca usuário por identificador (CPF/CNPJ ou email).
     *
     * @param identifier CPF (11 dígitos), CNPJ (14 dígitos) ou email
     * @return UserResponse com dados do usuário encontrado
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    UserResponse findUserByIdentifier(String identifier);

    /**
     * Valida se um token possui as permissões requeridas.
     *
     * @param token Token JWT
     * @param requiredPermissions Lista de permissões requeridas
     * @return true se possui todas as permissões, false caso contrário
     * @throws InvalidTokenException se token for inválido ou expirado
     */
    boolean validatePermissions(String token, List<String> requiredPermissions);

    /**
     * Obtém token para autenticação serviço-a-serviço via Client Credentials Flow.
     *
     * @return TokenResponse com access_token
     * @throws UnauthorizedException se credenciais forem inválidas
     */
    TokenResponse getClientCredentialsToken();

    /**
     * Renova um access token usando um refresh token.
     *
     * @param refreshToken Refresh token válido
     * @return TokenResponse com novo access_token
     * @throws UnauthorizedException se refresh token for inválido
     */
    TokenResponse refreshToken(String refreshToken);
}
```

### ConexaoAuthServiceImpl

**Responsibilidade:** Implementação do serviço de alto nível.

**Dependências:**
- `ConexaoAuthClient` - Para chamadas ao Auth Server
- `TokenValidator` - Para validação de tokens
- `ConexaoAuthProperties` - Para configurações

**Lógica de Implementação:**
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class ConexaoAuthServiceImpl implements ConexaoAuthService {

    private final ConexaoAuthClient conexaoAuthClient;
    private final TokenValidator tokenValidator;
    private final ConexaoAuthProperties properties;

    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Registrando novo usuário: {}", request.email());

        // Validação de email já pode ser feita aqui
        // ou delegada para o Auth Server (será tratado via 409 Conflict)

        UserResponse user = conexaoAuthClient.registerUser(request);
        log.info("Usuário registrado com sucesso: ID={}", user.id());
        return user;
    }

    @Override
    public UserResponse findUserByIdentifier(String identifier) {
        log.info("Buscando usuário por identificador: {}", identifier);

        // 1. Tentar busca por CPF
        if (identifier.matches("^\\d{11}$")) {
            try {
                return conexaoAuthClient.findUserByCpf(identifier);
            } catch (ResourceNotFoundException e) {
                log.debug("Usuário não encontrado por CPF, tentando por email");
            }
        }

        // 2. Tentar busca por CNPJ (14 dígitos)
        if (identifier.matches("^\\d{14}$")) {
            try {
                // Implementar endpoint de busca por CNPJ quando disponível
                // return conexaoAuthClient.findUserByCnpj(identifier);
                throw new UnsupportedOperationException("Busca por CNPJ ainda não implementada");
            } catch (ResourceNotFoundException e) {
                log.debug("Usuário não encontrado por CNPJ, tentando por email");
            }
        }

        // 3. Tentar busca por email
        if (identifier.contains("@")) {
            // Implementar endpoint de busca por email quando disponível
            // return conexaoAuthClient.findUserByEmail(identifier);
            throw new UnsupportedOperationException("Busca por email ainda não implementada");
        }

        throw new ResourceNotFoundException("Identificador inválido: " + identifier);
    }

    @Override
    public boolean validatePermissions(String token, List<String> requiredPermissions) {
        log.debug("Validando permissões do token");

        TokenClaims claims = tokenValidator.validateToken(token);

        // Verificar se o usuário possui todas as permissões requeridas
        List<String> userRoles = claims.roles();
        boolean hasAllPermissions = userRoles.containsAll(requiredPermissions);

        log.debug("Permissões requeridas: {}, Permissões do usuário: {}, Tem todas: {}",
                requiredPermissions, userRoles, hasAllPermissions);

        return hasAllPermissions;
    }

    @Override
    public TokenResponse getClientCredentialsToken() {
        log.info("Obtendo token via Client Credentials Flow");

        ClientCredentialsRequest request = new ClientCredentialsRequest(
            "client_credentials",
            properties.getClientId(),
            properties.getClientSecret(),
            null // scope opcional
        );

        TokenResponse token = conexaoAuthClient.clientCredentials(request);
        log.info("Token obtido com sucesso: expires_in={}s", token.expiresIn());
        return token;
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Renovando token via Refresh Token Flow");

        // Implementar endpoint de refresh token quando disponível no Auth Server
        throw new UnsupportedOperationException("Refresh Token Flow ainda não implementado");
    }
}
```

---

## Critérios de Aceite por Story

### Story SDK-4.1: Auth Service Interface

- [ ] Interface `ConexaoAuthService` criada
- [ ] Método `registerUser()` definido
- [ ] Método `findUserByIdentifier()` definido
- [ ] Método `validatePermissions()` definido
- [ ] Método `getClientCredentialsToken()` definido
- [ ] Método `refreshToken()` definido
- [ ] Javadoc completo em todos os métodos

### Story SDK-4.2: Registro de Usuário

- [ ] Método `registerUser()` implementado
- [ ] Chama `conexaoAuthClient.registerUser()`
- [ ] Logs informativos em sucesso e erro
- [ ] Lança `ConflictException` se email já existir (409)
- [ ] Lança `ResourceNotFoundException` se realm não existir (404)
- [ ] Retorna `UserResponse` com dados do usuário criado

### Story SDK-4.3: Busca de Usuário por Identificador

- [ ] Método `findUserByIdentifier()` implementado
- [ ] Suporta CPF (11 dígitos)
- [ ] Suporta CNPJ (14 dígitos)
- [ ] Suporta email (contém @)
- [ ] Fallback para busca por email se CPF/CNPJ não encontrado
- [ ] Lança `ResourceNotFoundException` se usuário não encontrado
- [ ] Valida formato do identificador antes de buscar

### Story SDK-4.4: Client Credentials Flow

- [ ] Método `getClientCredentialsToken()` implementado
- [ ] Chama `conexaoAuthClient.clientCredentials()`
- [ ] Usa `client_credentials` como grant type
- [ ] Usa `clientId` e `clientSecret` das properties
- [ ] Lança `UnauthorizedException` se credenciais inválidas (401)
- [ ] Retorna `TokenResponse` com access_token

### Story SDK-4.5: Validação de Permissões

- [ ] Método `validatePermissions()` implementado
- [ ] Valida token via `tokenValidator.validateToken()`
- [ ] Extrai lista de roles do token
- [ ] Verifica se possui todas as permissões requeridas
- [ ] Retorna true se possui todas, false caso contrário
- [ ] Lança `InvalidTokenException` se token for inválido ou expirado

### Story SDK-4.6: Refresh Token

- [ ] Método `refreshToken()` definido (placeholder)
- [ ] Documenta que será implementado quando endpoint existir no Auth Server
- [ ] Lança `UnsupportedOperationException` por enquanto

---

## Exemplo de Uso

### Registro de Usuário

```java
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final ConexaoAuthService conexaoAuthService;

    @PostMapping("/registrar")
    public UserResponse registrarUsuario(@RequestBody RegisterUserRequest request) {
        return conexaoAuthService.registerUser(request);
    }
}
```

### Busca de Usuário por CPF

```java
@GetMapping("/cpf/{cpf}")
public UserResponse buscarPorCpf(@PathVariable String cpf) {
    return conexaoAuthService.findUserByIdentifier(cpf);
}
```

### Validação de Permissões

```java
@PostMapping("/restrito")
public ResponseEntity<String> acessoRestrito(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer "

    List<String> requiredPermissions = List.of("ADMIN", "USER");

    if (!conexaoAuthService.validatePermissions(token, requiredPermissions)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok("Acesso concedido");
}
```

### Client Credentials Flow

```java
@Scheduled(fixedRate = 3600000) // A cada hora
public void refreshTokenScheduler() {
    TokenResponse token = conexaoAuthService.getClientCredentialsToken();
    // Armazenar token para uso em chamadas serviço-a-serviço
    cache.put("service-token", token.accessToken());
}
```

---

## Tecnologias Utilizadas

- **Spring Framework** - @Service, @RequiredArgsConstructor
- **Lombok** - Redução de boilerplate
- **Slf4j** - Logging

---

## Testes Requeridos

### Testes Unitários

- Teste de registro de usuário com sucesso
- Teste de registro de usuário com email duplicado
- Teste de registro de usuário com realm inexistente
- Teste de busca por CPF encontrado
- Teste de busca por CPF não encontrado (fallback email)
- Teste de busca por email encontrado
- Teste de busca por identificador inválido
- Teste de validação de permissões com sucesso
- Teste de validação de permissões sem permissão
- Teste de client credentials com credenciais válidas
- Teste de client credentials com credenciais inválidas

### Testes de Integração

- Teste de fluxo completo de registro de usuário
- Teste de fluxo completo de busca por identificador
- Teste de fluxo completo de client credentials

---

## Pontos de Atenção

1. **Fallback Logic:** Implementar fallback para busca por email quando CPF/CNPJ não encontrado
2. **Regex Validation:** Validar formato de CPF (11 dígitos) e CNPJ (14 dígitos)
3. **Permissions Check:** Roles do token são usadas como permissões (simplificado)
4. **Client Credentials:** Credenciais vêm das properties (não expostas em logs)
5. **Logging:** Adicionar logs informativos sem expor dados sensíveis
6. **Error Propagation:** Exceções do Feign Client são propagadas corretamente

---

## Próximos Passos

Após conclusão deste Epic:
1. **Epic SDK-5: Testes e Documentação** - Implementar testes abrangentes e documentação

---

## Estatísticas do Epic

| Métrica | Quantidade |
|---------|------------|
| **Stories** | 6 |
| **Interfaces** | 1 |
| **Implementações** | 1 |
| **Testes Estimados** | 11-13 |
| **Linhas de Código** | ~150-200 |
