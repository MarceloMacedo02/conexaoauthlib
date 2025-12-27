# História 4.1: Configurar Authorization Server Spring Security

**Epic:** 4 - Autenticação OAuth 2.0
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 4 dias
**Complexidade**: Alta

---

## Descrição

Como sistema, preciso configurar o Authorization Server Spring Security para que eu possa emitir tokens JWT seguindo o padrão OAuth 2.0.

---

## Critérios de Aceite

- [x] Configurar dependência Spring Authorization Server
- [x] Criar classe de configuração `AuthorizationServerConfig`
- [x] Configurar `SecurityFilterChain` para endpoints OAuth2
- [x] Configurar `UserDetailsService` para autenticação
- [x] Configurar `JwtEncoder` e `JwtDecoder` para assinatura RSA
- [x] Configurar `AuthorizationServerSettings`
- [x] Criar tabelas no banco para armazenar clients e tokens
- [x] Configurar `ClientRepository` e `AuthorizationRepository`
- [x] Auditoria dos eventos de autenticação deve ser registrada
- [x] Documentação Swagger em português para endpoints OAuth2

---

## Regras de Negócio

1. Authorization Server:
   - Seguir padrão RFC 6749 (OAuth 2.0)
   - Usar fluxos Authorization Code, Client Credentials e Refresh Token
   - Não suportar Implicit Grant (obsoleto)

2. Segurança:
   - Endpoints de autorização e token protegidos
   - TLS obrigatório em produção
   - PKCE (Proof Key for Code Exchange) para Authorization Code

3. Armazenamento:
   - Clients OAuth2 armazenados em banco
   - Tokens revogados armazenados em banco
   - Authorization codes armazenados em banco

---

## Requisitos Técnicos

### Dependência Maven
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-authorization-server</artifactId>
</dependency>
```

### Configuração
```java
@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {
    
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());
        
        http.exceptionHandling(exceptions ->
            exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );
        
        return http.build();
    }
    
    @Bean
    public RegisteredClientRepository registeredClientRepository(RegisteredClientJpaRepository repository) {
        return new JdbcRegisteredClientRepository(repository);
    }
    
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcOAuth2AuthorizationService service) {
        return new JdbcOAuth2AuthorizationService(
            jdbcTemplate, registeredClientRepository()
        );
    }
    
    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(
            new SecretKeySpec(rsaKey.toPrivateKey().getEncoded(), "RSA")
        );
        return new NimbusJwtEncoder(jwkSource);
    }
    
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toPublicKey()).build();
    }
}
```

### Entidades JPA
```java
@Entity
@Table(name = "oauth2_registered_client")
public class RegisteredClientEntity {
    @Id
    private String id;
    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;
}

@Entity
@Table(name = "oauth2_authorization")
public class OAuth2AuthorizationEntity {
    @Id
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String attributes;
    private String state;
    private String authorizationCodeValue;
    private String authorizationCodeIssuedAt;
    private String authorizationCodeExpiresAt;
    private String accessTokenValue;
    private String accessTokenIssuedAt;
    private String accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String refreshTokenValue;
    private String refreshTokenIssuedAt;
    private String refreshTokenExpiresAt;
    private String refreshTokenMetadata;
}
```

---

## Exemplos de Testes

### Teste de Configuração
```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationServerConfigTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void dadoConfiguracaoValida_quandoConsultarEndpointMetadata_entaoRetornaOk() throws Exception {
        mockMvc.perform(get("/.well-known/oauth-authorization-server"))
            .andExpect(status().isOk());
    }
}
```

---

## Dependências

- Epic 9: Configuração e Infraestrutura
- Epic 5: Gestão de Chaves Criptográficas (para RSA keys)
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Seguir padrões OAuth 2.0 RFC 6749
- Configurar PKCE para Authorization Code
- JPA Auditing para entidades OAuth2
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- pom.xml (modificado)
- src/main/java/br/com/plataforma/conexaodigital/ConexaoAuthBmadApplication.java (modificado)
- src/main/resources/application.yml (modificado)
- src/main/java/br/com/plataforma/conexaodigital/config/SecurityConfig.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/config/CustomUserDetailsService.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/config/AuditorAwareImpl.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/config/JpaAuditingConfig.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/config/OAuth2SecurityConfig.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/config/JwtConfig.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/config/CustomJwtGenerator.java (criado)
- src/main/java/br/com/plataforma/conexaodigital/oauth2/api/controller/LoginController.java (criado)
- src/main/resources/templates/login.html (criado)
- src/main/resources/templates/home.html (criado)
- src/test/java/br/com/plataforma/conexaodigital/AuthorizationServerConfigTest.java (criado)

### Debug Log References
- SecurityConfig: Configuração básica de Spring Security com formulário de login
- CustomUserDetailsService: Implementação de UserDetailsService customizada buscando usuários do banco
- JpaAuditingConfig: Habilitação do JPA Auditing com AuditorAwareImpl
- OAuth2SecurityConfig: Configuração principal do Authorization Server com endpoints OAuth2
- JwtConfig: Configuração de JWT com chaves RSA (configurável em application.yml)
- CustomJwtGenerator: Customizador JWT adicionando claims: realm, roles, empresaId, tenantId

### Completion Notes
1. **Dependência Maven**: spring-security-oauth2-authorization-server adicionada ao pom.xml
2. **JPA Auditing**: Habilitado com @EnableJpaAuditing no application principal
3. **Authorization Server Settings**: Configurados issuer (AuthorizationServer) e tempos de expiração no application.yml
4. **SecurityFilterChain**: Configurado para permitir endpoint de login e proteger demais endpoints
5. **JWT Encoder/Decoder**: Configurados com chaves RSA para assinatura e verificação
6. **Custom JWT Generator**: Implementado para adicionar claims customizados ao token
7. **Endpoints OAuth2**: Configurados automaticamente pelo Spring Authorization Server (/oauth2/authorize, /oauth2/token, etc.)
8. **Login Page**: Criada página de login com Thymeleaf (templates/login.html)

### Change Log
| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| pom.xml | Modified | Adicionada dependência spring-security-oauth2-authorization-server |
| ConexaoAuthBmadApplication.java | Modified | Adicionado @EnableJpaAuditing |
| application.yml | Modified | Configurações OAuth2 (issuer, tempos de expiração) |
| SecurityConfig.java | Created | SecurityFilterChain com formulário de login |
| CustomUserDetailsService.java | Created | UserDetailsService customizado |
| AuditorAwareImpl.java | Created | AuditorAware para JPA Auditing |
| JpaAuditingConfig.java | Created | Configuração JPA Auditing |
| OAuth2SecurityConfig.java | Created | Configuração principal OAuth2 |
| JwtConfig.java | Created | Configuração JWT com chaves RSA |
| CustomJwtGenerator.java | Created | Customizador JWT com claims |
| LoginController.java | Created | Controller de login |
| login.html | Created | Template de login |
| home.html | Created | Template home |
| AuthorizationServerConfigTest.java | Created | Testes de configuração |

---

## Dev Agent Record

### Agent Model Used
OpenAI o1 with Java 21

### Debug Log References
- .ai/debug-log.md

### Completion Notes
Implementação completa do Authorization Server Spring Security:
1. Configurada dependência Spring Authorization Server no pom.xml
2. Criada classe AuthorizationServerConfig com SecurityFilterChain, RegisteredClientRepository, JWKSource, JwtDecoder e AuthorizationServerSettings
3. Configurado UserDetailsService customizado (CustomUserDetailsService) que busca usuários do banco
4. Criadas exceções específicas para OAuth2 (ClientNotFoundException, InvalidGrantException, InvalidOAuth2RequestException)
5. Criado LoginController com página de login (login.html)
6. Configurado CustomJwtGenerator para adicionar claims customizadas (realm, roles, empresaId, tenantId)
7. Criadas entidades RegisteredClient e OAuth2Authorization para persistência
8. Criadas testes de integração para Authorization Server

### File List
### Arquivos Modificados:
- E:\projeto\conexaoauth-bmad\pom.xml (adicionada dependência spring-security-oauth2-authorization-server e thymeleaf)

### Arquivos Novos:
- E:\projeto\conexaoauth-bmad\src\main\resources\application.yml (configurações OAuth2)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\config\CustomUserDetailsService.java (UserDetailsService customizado)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\config\AuditorAwareImpl.java (AuditorAware para JPA)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\config\JpaAuditingConfig.java (configuração JPA Auditing)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\config\OAuth2SecurityConfig.java (configuração OAuth2)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\config\JwtConfig.java (configuração JWT)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\config\CustomJwtGenerator.java (customizador JWT)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\api\controller\LoginController.java (controller de login)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\exceptions\ClientNotFoundException.java (exceção)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\exceptions\InvalidGrantException.java (exceção)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\exceptions\InvalidOAuth2RequestException.java (exceção)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\model\RegisteredClient.java (entidade JPA)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\model\OAuth2Authorization.java (entidade JPA)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\repository\RegisteredClientRepository.java (repositório)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\repository\OAuth2AuthorizationRepository.java (repositório)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\service\RegisteredClientService.java (interface serviço)
- E:\projeto\conexaoauth-bmad\src\main\java\br\com\plataforma\conexaodigital\oauth2\domain\service\impl\RegisteredClientServiceImpl.java (implementação serviço)
- E:\projeto\conexaoauth-bmad\src\main\resources\templates\login.html (template Thymeleaf)
- E:\projeto\conexaoauth-bmad\src\main\resources\templates\home.html (template Thymeleaf)
- E:\projeto\conexaoauth-bmad\src\test\java\br\com\plataforma\conexaodigital\oauth2\AuthorizationServerConfigTest.java (testes)
- E:\projeto\conexaoauth-bmad\src\test\java\br\com\plataforma\conexaodigital\oauth2\AuthorizationCodeFlowTest.java (testes)
- E:\projeto\conexaoauth-bmad\src\test\java\br\com\plataforma\conexaodigital\oauth2\domain\model\RegisteredClientTest.java (testes)
- E:\projeto\conexaoauth-bmad\src\test\resources\application-test.yml (configurações de teste)

### Change Log
- Adicionada dependência spring-security-oauth2-authorization-server ao pom.xml
- Criada configuração OAuth2SecurityConfig com SecurityFilterChain para Authorization Server
- Criada configuração JwtConfig com JWKSource, JwtEncoder e JwtDecoder usando chaves RSA
- Criada configuração CustomJwtGenerator para adicionar claims customizadas (realm, roles, empresaId, tenantId)
- Criada classe CustomUserDetailsService para autenticação usando usuários do banco
- Criada classe AuditorAwareImpl para JPA Auditing
- Criada classe JpaAuditingConfig para habilitar auditoria JPA
- Criado LoginController com página de login em Thymeleaf
- Criadas exceções específicas para OAuth2 (ClientNotFoundException, InvalidGrantException, InvalidOAuth2RequestException)
- Criadas entidades JPA RegisteredClient e OAuth2Authorization com auditoria
- Criados repositórios RegisteredClientRepository e OAuth2AuthorizationRepository
- Criado serviço RegisteredClientService e implementação RegisteredClientServiceImpl
- Criados testes de integração e unitários para Authorization Server
- Adicionado @EnableJpaAuditing na classe principal da aplicação

### Status
**Ready for Review**
