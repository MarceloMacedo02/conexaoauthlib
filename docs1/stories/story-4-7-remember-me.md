# História 4.7: Remember-me Persistente

**Epic:** 4 - Autenticação OAuth 2.0
**Status:** Em Progresso
**Prioridade:** Média
**Estimativa:** 3 dias
**Complexidade**: Média

---

## Dev Agent Record

### Tasks
- [x] Criar RefreshTokenCookieHandler
- [x] Criar RememberMeSettings
- [x] Atualizar LoginController com suporte a remember-me
- [x] Atualizar template login.html com checkbox remember-me
- [x] Atualizar TokenService para suportar remember-me
- [x] Escrever testes unitários (RefreshTokenCookieHandlerTest, LoginControllerTest)
- [x] Corrigir mocks em TokenServiceTest para suportar RememberMeSettings
- [x] Validar compilação e testes
- [ ] Integrar RefreshTokenCookieHandler no fluxo de autenticação (requer integração com Spring Security)
- [ ] Validar critérios de aceite com testes de integração E2E

### Debug Log
- 2025-12-23 18:51: Compilação realizada com sucesso
- 2025-12-23 18:54: Testes unitários passando (RefreshTokenCookieHandlerTest: 4, LoginControllerTest: 7)
- 2025-12-23 19:00: Todos os testes passando (26 testes: 4 RefreshTokenCookieHandlerTest, 7 LoginControllerTest, 15 TokenServiceTest)

### File List
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/web/handler/RefreshTokenCookieHandler.java`
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/config/RememberMeSettings.java`
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/api/controller/LoginController.java` (modificado)
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/TokenService.java` (modificado)
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/service/impl/TokenServiceImpl.java` (modificado)
- `src/main/resources/templates/login.html` (modificado)
- `src/test/java/br/com/plataforma/conexaodigital/oauth2/web/handler/RefreshTokenCookieHandlerTest.java`
- `src/test/java/br/com/plataforma/conexaodigital/oauth2/api/controller/LoginControllerTest.java`
- `src/test/java/br/com/plataforma/conexaodigital/oauth2/domain/service/TokenServiceTest.java` (modificado)

### Change Log
- 2025-12-23: Criado RefreshTokenCookieHandler para gerenciar cookie HTTP-only/Secure
- 2025-12-23: Criado RememberMeSettings para configuração de TTL baseado em remember-me
- 2025-12-23: Atualizado LoginController com parâmetro rememberMe e auditoria
- 2025-12-23: Atualizado login.html com checkbox "Lembrar-me" e estilos CSS
- 2025-12-23: Atualizado TokenService com sobrecarga para remember-me
- 2025-12-23: Atualizado TokenServiceImpl para usar TTL dinâmico baseado em remember-me
- 2025-12-23: Criados testes unitários para RefreshTokenCookieHandler e LoginController
- 2025-12-23: Corrigidos mocks em TokenServiceTest (adicionados JwtEncoder mock, RememberMeSettings mock)

---

---

## Descrição

Como usuário, quero manter minha sessão ativa por um período prolongado (remember-me) para que eu não precise me autenticar toda vez que fechar o navegador.

---

## Critérios de Aceite

- [x] Checkbox "Lembrar-me" na tela de login
- [x] Quando marcado, emite refresh token com prazo estendido (90 dias)
- [x] Refresh token remember-me expira em 90 dias
- [x] Refresh token remember-me pode ser usado para obter novos access tokens
- [x] Auditoria dos eventos deve ser registrada (tipo: LOGIN_REMEMBER_ME)
- [ ] Retornar `200 OK` com tokens (requer integração com fluxo authorization_code)
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Remember-me:
   - Opcional (checkbox na tela de login)
   - Quando marcado, refresh token expira em 90 dias
   - Quando não marcado, refresh token expira em 30 dias (padrão)

2. Refresh Token:
   - Mesmo refresh token pode ser usado múltiplas vezes
   - Rotação de refresh token (novo refresh token a cada refresh)
   - Remember-me persistente entre sessões do navegador

3. Cookie:
   - Refresh token pode ser armazenado em cookie HTTP-only
   - Cookie tem mesma validade do refresh token

---

## Requisitos Técnicos

### Configuração de Remember-me
```java
@Bean
public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        .refreshTokenTimeToLive(Duration.ofDays(30))
        .reuseRefreshTokens(false)
        .build();
}

public TokenSettings rememberMeTokenSettings() {
    return TokenSettings.builder()
        .refreshTokenTimeToLive(Duration.ofDays(90))
        .reuseRefreshTokens(false)
        .build();
}
```

### Login Controller com Remember-me
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
    public String login(@RequestParam String email, 
                       @RequestParam String senha,
                       @RequestParam(required = false) boolean rememberMe,
                       HttpServletResponse response) {
        
        Usuario usuario = usuarioService.autenticar(email, senha);
        
        TokenSettings tokenSettings = rememberMe ? rememberMeTokenSettings() : tokenSettings();
        
        auditoriaService.registrarEvento(
            rememberMe ? TipoEventoAuditoria.LOGIN_REMEMBER_ME : TipoEventoAuditoria.LOGIN,
            "Login realizado: " + usuario.getEmail()
        );
        
        return "redirect:/oauth2/authorize?remember_me=" + rememberMe;
    }
}
```

### Cookie de Refresh Token
```java
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieHandler {
    
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, boolean rememberMe) {
        int maxAge = rememberMe ? 90 * 24 * 60 * 60 : 30 * 24 * 60 * 60;
        
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        
        response.addCookie(cookie);
    }
    
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        
        response.addCookie(cookie);
    }
}
```

---

## Exemplos de Testes

### Teste de Remember-me Ativado
```java
@SpringBootTest
@AutoConfigureMockMvc
public class RememberMeTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    dadoRememberMeAtivo_quandoLogin_entaoRefreshTokenExpiraEm90Dias() throws Exception {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
        usuarioRepository.save(usuario);
        
        mockMvc.perform(post("/login")
                .param("email", "joao@example.com")
                .param("senha", "Senha@123")
                .param("rememberMe", "true"))
            .andExpect(status().is3xxRedirection());
        
        String refreshToken = obterRefreshTokenDoCookie(response);
        
        Instant expiresAt = jwtDecoder.decode(refreshToken).getExpiresAt();
        Duration validity = Duration.between(Instant.now(), expiresAt);
        
        assertThat(validity.toDays()).isGreaterThanOrEqualTo(89);
    }
}
```

---

## Dependências

- História 4.2: Fluxo Authorization Code
- História 4.4: Fluxo Refresh Token
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Cookie de refresh token deve ser HTTP-only e Secure
- Registrar auditoria de login com remember-me
- Validar expiração de refresh token remember-me
- Checkstyle: Seguir Google Java Style Guide
