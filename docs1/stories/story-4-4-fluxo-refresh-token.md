# História 4.4: Fluxo Refresh Token

**Epic:** 4 - Autenticação OAuth 2.0  
**Status:** Pendente  
**Prioridade:** Alta  
**Estimativa:** 3 dias  
**Complexidade**: Média

---

## Descrição

Como usuário, quero obter um novo access token usando um refresh token para que eu possa manter minha sessão ativa sem precisar me autenticar novamente.

---

## Critérios de Aceite

- [x] Endpoint `POST /oauth2/token` recebe refresh token
- [x] Valida refresh token expirado ou revogado
- [x] Gera novo access token com mesmas claims originais
- [x] Gera novo refresh token (rotação de refresh token)
- [x] Invalida refresh token anterior
- [x] Auditoria dos eventos deve ser registrada (tipo: REFRESH_TOKEN)
- [x] Retornar `200 OK` com novos tokens
- [x] Retornar `401 Unauthorized` se refresh token inválido
- [x] Retornar `400 Bad Request` se refresh token expirado
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Refresh Token Flow:
   - Usado para obter novo access token sem reautenticação
   - Refresh token deve ser válido (não expirado, não revogado)
   - Refresh token anterior é invalidado (rotação)

2. Rotação de Refresh Token:
   - Novo refresh token é gerado a cada refresh
   - Refresh token anterior é invalidado
   - Previne replay attacks

3. JWT Claims:
   - Mesmas claims do access token original
   - `sub`, `realm`, `roles`, `empresaId`, `tenentId`, `exp`, `iat`, `jti`

4. Prazos:
   - Refresh token: expira em 30 dias
   - Refresh tokens anteriores são invalidados

---

## Requisitos Técnicos

### Configuração do Refresh Token
```java
@Bean
public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        .refreshTokenTimeToLive(Duration.ofDays(30))
        .reuseRefreshTokens(false)
        .build();
}
```

### Custom Refresh Token Validator
```java
@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {
    
    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final AuditoriaService auditoriaService;
    
    public void validate(String refreshToken) {
        if (tokenRevogadoRepository.existsByTokenId(jwtDecoder.decode(refreshToken).getId())) {
            throw new InvalidTokenException("Refresh token revogado");
        }
        
        Instant expiresAt = jwtDecoder.decode(refreshToken).getExpiresAt();
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
            throw new InvalidTokenException("Refresh token expirado");
        }
    }
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@SpringBootTest
@AutoConfigureMockMvc
public class RefreshTokenFlowTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void dadoRefreshTokenValido_quandoFlujoRefreshToken_entaoRetornaNovosTokens() throws Exception {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
        usuarioRepository.save(usuario);
        
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("test-client")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .build();
        registeredClientRepository.save(client);
        
        String refreshToken = "valid-refresh-token";
        
        mockMvc.perform(post("/oauth2/token")
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .with(httpBasic("test-client", "secret")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.refresh_token").isNotEmpty())
            .andExpect(jsonPath("$.token_type").value("Bearer"));
    }
}
```

### Teste de Refresh Token Expirado
```java
@Test
void dadoRefreshTokenExpirado_quandoFlujoRefreshToken_entaoRetornaBadRequest() throws Exception {
    String expiredToken = "expired-refresh-token";
    
    mockMvc.perform(post("/oauth2/token")
            .param("grant_type", "refresh_token")
            .param("refresh_token", expiredToken)
            .with(httpBasic("test-client", "secret")))
        .andExpect(status().isBadRequest());
}
```

---

## Dependências

- História 4.2: Fluxo Authorization Code
- História 4.6: Token Revogação
- Epic 5: Gestão de Chaves Criptográficas
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Rotação obrigatória de refresh token
- Registrar auditoria de refresh token
- Validar expiração de refresh token
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Implementado Por:
- opencode (dev) em 2025-12-24

### Status
- Status: Done

### Tasks / Subtasks Checkboxes
- [x] Endpoint `POST /oauth2/token` recebe refresh token
- [x] Valida refresh token expirado ou revogado
- [x] Gera novo access token com mesmas claims originais
- [x] Gera novo refresh token (rotação de refresh token)
- [x] Invalida refresh token anterior
- [x] Auditoria dos eventos deve ser registrada (tipo: REFRESH_TOKEN)
- [x] Retornar `200 OK` com novos tokens
- [x] Retornar `401 Unauthorized` se refresh token inválido
- [x] Retornar `400 Bad Request` se refresh token expirado
- [x] Documentação Swagger em português

### File List
### Arquivos Modificados:
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/impl/TokenServiceImpl.java`

### Arquivos Deletados:
- Nenhum

### Debug Log References
- Nenhum erro durante compilação ou testes

### Completion Notes List
- Fluxo refresh token já implementado em TokenServiceImpl.generateRefreshToken()
- Auditoria real integrada usando AuditoriaService
- Rotação de refresh token implementada (oldRefreshToken marcado como revogado)
- Tipos de eventos de auditoria: REFRESH_TOKEN, EMISSAO_TOKEN, AUTENTICACAO_CLIENT, REVOGACAO_TOKEN

### Change Log
- 2025-12-24: Integrado AuditoriaService em TokenServiceImpl
- 2025-12-24: Auditoria REFRESH_TOKEN registrada ao utilizar refresh token
- 2025-12-24: Auditoria EMISSAO_TOKEN registrada ao emitir access token
- 2025-12-24: Auditoria AUTENTICACAO_CLIENT registrada ao autenticar client
- 2025-12-24: Auditoria REVOGACAO_TOKEN registrada ao revogar tokens
- 2025-12-24: Corrigido import AuthenticationException em GlobalExceptionHandler (org.springframework.security.core.AuthenticationException)

---