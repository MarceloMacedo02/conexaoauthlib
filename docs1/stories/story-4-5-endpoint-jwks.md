# História 4.5: Endpoint JWKS por Realm

**Epic:** 4 - Autenticação OAuth 2.0
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 3 dias
**Complexidade**: Média

---

## Descrição

Como microserviço, quero acessar as chaves públicas de um realm via endpoint JWKS para que eu possa validar tokens JWT emitidos por esse realm.

---

## Critérios de Aceite

- [x] Endpoint `GET /oauth2/jwks/{realmId}` retorna chaves públicas do realm
- [x] Segue padrão RFC 7517 (JSON Web Key Set)
- [x] Retorna apenas chaves ativas (não expiradas)
- [x] Inclui todas as versões de chaves ativas (grace period)
- [x] Não requer autenticação (endpoint público)
- [x] Retornar `200 OK` com JWKS
- [x] Retornar `404 Not Found` se realm não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. JWKS Endpoint:
   - Público (sem autenticação)
   - Retorna chaves públicas (chaves privadas nunca são expostas)
   - Segue padrão RFC 7517

2. Grace Period:
   - Chaves anteriores ainda podem ser usadas para validação
   - Chaves expiradas não são retornadas
   - Permite migração suave durante rotação de chaves

3. Formato:
   - `keys`: Array de chaves
   - Cada chave contém: `kid`, `kty`, `use`, `n`, `e`, `alg`

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/oauth2/jwks")
@RequiredArgsConstructor
public class JwksController {
    
    private final JwksProvider jwksProvider;
    private final RealmRepository realmRepository;
    
    @GetMapping("/{realmId}")
    @Operation(summary = "Obter JWKS do realm", description = "Retorna as chaves públicas (JWKS) de um realm para validação de tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWKS retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Realm não encontrado")
    })
    ResponseEntity<JsonWebKeySet> obterJwks(@PathVariable UUID realmId) {
        Realm realm = realmRepository.findById(realmId)
            .orElseThrow(() -> new RealmNotFoundException(realmId));
        
        JsonWebKeySet jwks = jwksProvider.obterJwks(realm.getId());
        
        return ResponseEntity.ok(jwks);
    }
}
```

### JwksProvider
```java
@Component
@RequiredArgsConstructor
public class JwksProvider {
    
    private final ChaveCriptograficaRepository chaveRepository;
    
    public JsonWebKeySet obterJwks(UUID realmId) {
        List<ChaveCriptografica> chaves = chaveRepository.findByRealmIdAndStatus(realmId, StatusChave.ATIVA);
        
        Map<String, Object> jwks = new HashMap<>();
        List<Map<String, Object>> keys = new ArrayList<>();
        
        for (ChaveCriptografica chave : chaves) {
            RSAPublicKey publicKey = chave.getPublicKey();
            
            Map<String, Object> jwk = new HashMap<>();
            jwk.put("kid", chave.getVersao().toString());
            jwk.put("kty", "RSA");
            jwk.put("use", "sig");
            jwk.put("alg", "RS256");
            jwk.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray()));
            jwk.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray()));
            
            keys.add(jwk);
        }
        
        jwks.put("keys", keys);
        
        return new JsonWebKeySet(keys);
    }
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@SpringBootTest
@AutoConfigureMockMvc
public class JwksEndpointTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    dadoRealmComChaves_quandoSolicitarJwks_entaoRetornaChavesPublicas() throws Exception {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        
        ChaveCriptografica chave = new ChaveCriptografica(realm, "v1", StatusChave.ATIVA);
        chave.setPublicKey(generateRSAKeyPair().getPublic());
        chaveRepository.save(chave);
        
        mockMvc.perform(get("/oauth2/jwks/" + realm.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keys").isArray())
            .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
            .andExpect(jsonPath("$.keys[0].alg").value("RS256"))
            .andExpect(jsonPath("$.keys[0].use").value("sig"));
    }
}
```

### Teste de Realm Não Encontrado
```java
@Test
void dadoRealmInexistente_quandoSolicitarJwks_entaoRetornaNotFound() throws Exception {
        mockMvc.perform(get("/oauth2/jwks/" + UUID.randomUUID()))
        .andExpect(status().isNotFound());
}
```

---

## Dependências

- Epic 1: Gestão de Realms
- Epic 5: Gestão de Chaves Criptográficas

---

## Pontos de Atenção

- Endpoint público (sem autenticação)
- Nunca expor chaves privadas
- Suportar múltiplas chaves ativas (grace period)
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Agent Model Used
GPT-4 (Claude 3.5 Sonnet)

### File List
#### Novos Arquivos Criados:
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/model/ChaveCriptografica.java` - Entidade para armazenamento de chaves criptográficas
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/model/StatusChave.java` - Enum para status de chave
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/domain/repository/ChaveCriptograficaRepository.java` - Repository para chaves criptográficas
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/api/responses/JwksResponse.java` - DTO de resposta JWKS
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/infrastructure/jwks/JwksProvider.java` - Provider para geração de JWKS
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/api/controller/JwksController.java` - Controller para endpoint JWKS

#### Arquivos Modificados:
- `src/main/java/br/com/plataforma/conexaodigital/oauth2/config/OAuth2SecurityConfig.java` - Adicionado permitAll para /oauth2/jwks/**

#### Arquivos de Teste Criados:
- `src/test/java/br/com/plataforma/conexaodigital/oauth2/api/controller/JwksControllerTest.java`
- `src/test/java/br/com/plataforma/conexaodigital/oauth2/infrastructure/jwks/JwksProviderTest.java`

### Debug Log References
N/A - Não houve problemas significativos durante a implementação.

### Completion Notes
1. Criada a entidade `ChaveCriptografica` com suporte a versionamento, status e expiração de chaves
2. Implementado `JwksProvider` que converte chaves do banco para formato RFC 7517 (JWK)
3. Criado `JwksController` com endpoint público `/oauth2/jwks/{realmId}`
4. Suporte a grace period através do método `findChavesAtivasParaValidacao`
5. Chaves privadas nunca são expostas (apenas chaves públicas em formato PEM/JWK)
6. Documentação Swagger em português incluída
7. Testes unitários criados para controller e provider

### Change Log
- Criado modelo de domínio para chaves criptográficas por realm
- Implementado endpoint JWKS conforme RFC 7517
- Configurado repositório com queries customizadas para validação de chaves
- Adicionado DTO de resposta JwksResponse com JsonWebKey aninhado

### Status
Ready for Review
