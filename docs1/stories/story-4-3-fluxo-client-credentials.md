# História 4.3: Fluxo Client Credentials

**Epic:** 4 - Autenticação OAuth 2.0  
**Status:** Pendente  
**Prioridade:** Alta  
**Estimativa:** 4 dias  
**Complexidade**: Média

---

## Descrição

Como microserviço, quero me autenticar usando o fluxo Client Credentials para que eu possa obter um access token e acessar recursos protegidos em nome do próprio serviço (service-to-service).

---

## Critérios de Aceite

- [ ] Endpoint `POST /oauth2/token` recebe client credentials
- [ ] Gera JWT com claims: sub, realm, roles (SERVICE), empresaId, tenentId, exp, iat, jti
- [ ] Valida client ID e client secret
- [ ] Auditoria dos eventos deve ser registrada (tipo: AUTENTICACAO_CLIENT, EMISSAO_TOKEN)
- [ ] Retornar `200 OK` com access token
- [ ] Retornar `401 Unauthorized` se client inválido
- [ ] Retornar `400 Bad Request` se grant_type inválido
- [ ] Documentação Swagger em português

---

## Regras de Negócio

1. Client Credentials Flow:
   - Usado para service-to-service authentication
   - Não há usuário envolvido
   - Client representa um serviço

2. JWT Claims:
   - `sub`: Client ID
   - `realm`: Realm associado ao client
   - `roles`: Lista contendo apenas `SERVICE`
   - `empresaId`: EmpresaId associada ao client (se existir)
   - `tenentId`: TenentId associada ao client (se existir)
   - `exp`: Timestamp de expiração
   - `iat`: Timestamp de emissão
   - `jti`: ID único do token
   - `iss`: Emissor (configurável)

3. Prazos:
   - Access token: expira em 1 hora
   - Refresh token: NÃO é emitido (não há refresh token para client credentials)

---

## Requisitos Técnicos

### Entidade Oauth2Client
```java
@Entity
@Table(name = "oauth2_client")
public class Oauth2Client {
    @Id
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String clientId;
    
    @Column(nullable = false)
    private String clientSecret;
    
    @ManyToOne
    @JoinColumn(name = "realm_id")
    private Realm realm;
    
    @Column
    private String empresaId;
    
    @Column
    private String tenentId;
    
    @ElementCollection
    @CollectionTable(name = "oauth2_client_scope", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "scope")
    private Set<String> scopes = new HashSet<>();
}
```

### Repository
```java
@Repository
public interface Oauth2ClientRepository extends JpaRepository<Oauth2Client, UUID> {
    Optional<Oauth2Client> findByClientId(String clientId);
}
```

### Custom Token Generator para Client Credentials
```java
@Component
@RequiredArgsConstructor
public class ClientCredentialsJwtGenerator implements OAuth2TokenCustomizer<JwtEncodingContext> {
    
    private final Oauth2ClientRepository clientRepository;
    private final AuditoriaService auditoriaService;
    
    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) && 
            AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
            
            Oauth2Client client = clientRepository.findByClientId(context.getAuthorizedClient().getClientId())
                .orElseThrow();
            
            context.getClaims().claim("sub", client.getClientId());
            context.getClaims().claim("realm", client.getRealm().getId().toString());
            context.getClaims().claim("roles", List.of("SERVICE"));
            context.getClaims().claim("empresaId", client.getEmpresaId());
            context.getClaims().claim("tenentId", client.getTenentId());
            
            auditoriaService.registrarEvento(TipoEventoAuditoria.AUTENTICACAO_CLIENT, 
                "Client autenticado: " + client.getClientId());
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
public class ClientCredentialsFlowTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void dadoClientValido_quandoFlujoClientCredentials_entaoRetornaAccessToken() throws Exception {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        
        Oauth2Client client = new Oauth2Client();
        client.setClientId("service-client");
        client.setClientSecret(passwordEncoder.encode("secret"));
        client.setRealm(realm);
        clientRepository.save(client);
        
        mockMvc.perform(post("/oauth2/token")
                .param("grant_type", "client_credentials")
                .with(httpBasic("service-client", "secret")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.token_type").value("Bearer"))
            .andExpect(jsonPath("$.expires_in").isNotEmpty());
    }
}
```

### Teste de Client Inválido
```java
@Test
void dadoClientInvalido_quandoFlujoClientCredentials_entaoRetornaUnauthorized() throws Exception {
    mockMvc.perform(post("/oauth2/token")
            .param("grant_type", "client_credentials")
            .with(httpBasic("invalid-client", "secret")))
        .andExpect(status().isUnauthorized());
}
```

---

## Dependências

- História 4.1: Configurar Authorization Server Spring Security
- Epic 1: Gestão de Realms
- Epic 5: Gestão de Chaves Criptográficas
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Não emitir refresh token para client credentials
- Validar client secret com BCrypt
- Registrar auditoria de autenticação de client
- Checkstyle: Seguir Google Java Style Guide
