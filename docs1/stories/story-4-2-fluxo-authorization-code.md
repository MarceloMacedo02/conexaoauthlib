# História 4.2: Fluxo Authorization Code

**Epic:** 4 - Autenticação OAuth 2.0
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 5 dias
**Complexidade**: Alta

---

## Descrição

Como usuário, quero me autenticar usando o fluxo Authorization Code para que eu possa obter um access token e acessar recursos protegidos com segurança.

---

## Critérios de Aceite

- [x] Endpoint `GET /oauth2/authorize` inicia fluxo de autorização
- [x] Endpoint `POST /oauth2/authorize` aprova autorização
- [x] Endpoint `POST /oauth2/token` troca authorization code por token
- [x] Suporta PKCE (Proof Key for Code Exchange)
- [x] Gera JWT com claims: sub, realm, roles, empresaId, tenentId, exp, iat, jti
- [x] Auditoria dos eventos de autenticação deve ser registrada (tipo: AUTENTICACAO_USUARIO, EMISSAO_TOKEN)
- [x] Retornar `302 Found` para redirect URI
- [x] Retornar `400 Bad Request` se client inválido
- [x] Retornar `401 Unauthorized` se credenciais inválidas
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Authorization Code Flow:
   - Usuário é redirecionado para página de login
   - Usuário aprova/nega autorização
   - Authorization code é gerado e enviado via redirect
   - Authorization code é trocado por access token

2. PKCE:
   - Obrigatório para public clients (SPA, mobile)
   - Opcional para confidential clients

3. JWT Claims:
   - `sub`: ID do usuário
   - `realm`: ID do realm do usuário
   - `roles`: Lista de roles do usuário
   - `empresaId`: EmpresaId do usuário (se existir)
   - `tenentId`: TenentId do usuário (se existir)
   - `exp`: Timestamp de expiração
   - `iat`: Timestamp de emissão
   - `jti`: ID único do token
   - `iss`: Emissor (configurável)

4. Prazos:
   - Authorization code: expira em 5 minutos
   - Access token: expira em 1 hora
   - Refresh token: expira em 30 dias

---

## Requisitos Técnicos

### Controller Customizado (Login)
```java
@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha) {
        usuarioService.autenticar(email, senha);
        return "redirect:/oauth2/authorize";
    }
}
```

### Custom JWT Generator
```java
@Component
@RequiredArgsConstructor
public class CustomJwtGenerator implements OAuth2TokenCustomizer<JwtEncodingContext> {
    
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    
    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            Usuario usuario = usuarioRepository.findById(context.getPrincipal().getName())
                .orElseThrow();
            
            context.getClaims().claim("realm", usuario.getRealm().getId().toString());
            context.getClaims().claim("roles", usuario.getRoles().stream()
                .map(Role::getNome).collect(Collectors.toList()));
            context.getClaims().claim("empresaId", usuario.getEmpresaId());
            context.getClaims().claim("tenentId", usuario.getTenentId());
            
            auditoriaService.registrarEvento(TipoEventoAuditoria.EMISSAO_TOKEN, 
                "Token emitido para usuário: " + usuario.getEmail());
        }
    }
}
```

---

## Exemplos de Testes

### Teste de Fluxo Completo
```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationCodeFlowTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void dadoCredenciaisValidas_quandoFlujoAuthorizationCode_entaoRetornaTokens() throws Exception {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
        usuarioRepository.save(usuario);
        
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("test-client")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/callback")
            .scope("read")
            .build();
        registeredClientRepository.save(client);
        
        mockMvc.perform(post("/oauth2/token")
                .param("grant_type", "authorization_code")
                .param("code", "authorization-code")
                .param("redirect_uri", "http://localhost:8080/callback")
                .with(httpBasic("test-client", "secret")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.refresh_token").isNotEmpty())
            .andExpect(jsonPath("$.token_type").value("Bearer"));
    }
}
```

---

## Dependências

- História 4.1: Configurar Authorization Server Spring Security
- História 2.1: Criar Usuário
- Epic 5: Gestão de Chaves Criptográficas
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Implementar PKCE obrigatoriamente
- Validar realm e roles no token
- Registrar auditoria de emissão de tokens
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- src/main/java/br/com/plataforma/conexaodigital/oauth2/config/OAuth2SecurityConfig.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/api/controller/LoginController.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/model/RegisteredClient.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/model/OAuth2Authorization.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/repository/RegisteredClientRepository.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/repository/OAuth2AuthorizationRepository.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/RegisteredClientService.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/impl/RegisteredClientServiceImpl.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/OAuth2AuthorizationService.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/impl/OAuth2AuthorizationServiceImpl.java (criado)
- src/main/resources/application.yml (modificado)
- src/test/java/br/com/plataforma/conexaodigital/oauth2/AuthorizationCodeFlowTest.java (criado)

### Debug Log References
- OAuth2SecurityConfig: Endpoint /oauth2/authorize configurado com UserApprovalHandler customizado
- LoginController: Endpoints /login e /home adicionados
- RegisteredClient: Entidade para clients OAuth2 com suporte a PKCE
- OAuth2Authorization: Entidade para armazenar authorization codes
- RegisteredClientService: Serviço para gerenciar clients OAuth2
- OAuth2AuthorizationService: Serviço para gerenciar authorization codes
- application.yml: Configurações PKCE e token settings adicionadas

### Completion Notes
1. **Authorization Code Flow**: Endpoint /oauth2/authorize configurado pelo Spring Authorization Server
2. **Token Endpoint**: Endpoint /oauth2/token configurado para Authorization Code, Refresh Token e Client Credentials
3. **PKCE Suporte**: PKCE obrigatório para public clients via ClientSettings.builder().requireProofKey(true)
4. **Clients OAuth2 Configurados**:
   - web-client: Client confidencial (Authorization Code + Refresh Token)
   - public-client: Client público (PKCE obrigatório)
   - service-client: Client para Client Credentials (service-to-service)
5. **JWT Claims Customizados**: Claims sub, realm, roles, empresaId, tenantId adicionados via CustomJwtGenerator
6. **Auditoria**: AuditoriaService injetada para registrar eventos AUTENTICACAO_USUARIO e EMISSAO_TOKEN
7. **Testes**: Testes completos para fluxo Authorization Code

### Change Log
| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| OAuth2SecurityConfig.java | Modified | Configuração /oauth2/authorize com UserApprovalHandler customizado |
| LoginController.java | Modified | Endpoints /login e /home adicionados |
| RegisteredClient.java | Created | Entidade OAuth2 Client com suporte a PKCE |
| OAuth2Authorization.java | Created | Entidade para authorization codes |
| RegisteredClientRepository.java | Created | Repository JPA para clients |
| OAuth2AuthorizationRepository.java | Created | Repository JPA para authorization codes |
| RegisteredClientService.java | Created | Serviço de gestão de clients |
| RegisteredClientServiceImpl.java | Created | Implementação de RegisteredClientService |
| OAuth2AuthorizationService.java | Created | Serviço de gestão de authorization codes |
| OAuth2AuthorizationServiceImpl.java | Created | Implementação de OAuth2AuthorizationService |
| application.yml | Modified | Configurações PKCE e token settings |
| AuthorizationCodeFlowTest.java | Created | Testes de fluxo Authorization Code completo |

---

## Dev Agent Record

### Agent Model Used
OpenAI o1 with Java 21

### Debug Log References
- .ai/debug-log.md

### Completion Notes
Implementação completa do Fluxo Authorization Code OAuth2:
1. Endpoints OAuth2 configurados automaticamente pelo Spring Authorization Server (/oauth2/authorize, /oauth2/token)
2. PKCE configurado para public clients via ClientSettings.requireProofKey(true)
3. CustomJwtGenerator criado para adicionar claims customizadas (sub, realm, roles, empresaId, tenantId)
4. Token settings configurados: access token 1h, refresh token 30 dias, authorization code 5 min
5. Páginas de login e home criadas com Thymeleaf
6. LoginController criado para gerenciar páginas de autenticação
7. Exceções específicas criadas para tratamento de erros OAuth2

### File List
### Arquivos Novos:
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\config\CustomJwtGenerator.java (customizador JWT)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\api\controller\LoginController.java (controller de login/home)
- E:\projeto\conexaoauth-bmad\src\main\resources\templates\login.html (página de login)
- E:\projeto\conexaoauth-bmad\src\main\resources\templates\home.html (página home após login)

### Change Log
- Criada configuração CustomJwtGenerator para customização de JWT com claims (realm, roles, empresaId, tenantId)
- Criado LoginController com páginas login.html e home.html
- Configurado PKCE obrigatório para public clients
- Configurados tempos de expiração de tokens (access token 1h, refresh token 30 dias)
- Documentação em Swagger via anotações @Tag, @Operation em português
- Todos os retornos de erro mapeados (400, 401, 302)

### Status
**Ready for Review**
