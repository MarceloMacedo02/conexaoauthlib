# História 5.3: Rotação Manual de Chaves

**Epic:** 5 - Gestão de Chaves Criptográficas  
**Status:** Ready for Review  
**Prioridade:** Alta  
**Estimativa:** 3 dias  
**Complexidade**: Média

---

## Descrição

Como administrador do sistema, quero rotacionar manualmente as chaves criptográficas de um realm para que eu possa substituir chaves antigas por novas de forma controlada.

---

## Critérios de Aceite

- [x] Endpoint `POST /api/v1/chaves/{realmId}/rotacionar` inicia rotação
- [x] Gera novo par de chaves RSA-2048
- [x] Marca chave anterior como `INATIVA` (não expira)
- [x] Nova chave marcada como `ATIVA`
- [x] Grace period: chaves antigas ainda validam tokens
- [x] Auditoria dos eventos deve ser registrada (tipo: ROTACAO_CHAVE_MANUAL)
- [x] Retornar `200 OK` com nova chave gerada
- [x] Retornar `404 Not Found` se realm não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Rotação Manual:
   - Apenas uma chave `ATIVA` por realm
   - Chaves anteriores marcadas como `INATIVA`
   - Chaves `INATIVAS` ainda podem validar tokens (grace period)
   - Nova chave é usada para assinar novos tokens

2. Grace Period:
   - Chaves anteriores ainda podem validar tokens
   - Tokens assinados com chave anterior ainda são válidos
   - Tokens anteriores expiram naturalmente

3. Histórico:
   - Chaves são mantidas no banco (não excluídas)
   - Histórico pode ser consultado

---

## Requisitos Técnicos

### Entidade RotacaoChave
```java
@Entity
@Table(name = "rotacao_chave")
public class RotacaoChave {
    @Id
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "realm_id", nullable = false)
    private Realm realm;
    
    @Column(name = "chave_anterior_id")
    private UUID chaveAnteriorId;
    
    @Column(name = "chave_nova_id", nullable = false)
    private UUID chaveNovaId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRotacao tipo;
    
    @Column(name = "data_rotacao", nullable = false)
    private LocalDateTime dataRotacao;
    
    @Column(name = "solicitante")
    private String solicitante;
}
```

### Enum TipoRotacao
```java
public enum TipoRotacao {
    MANUAL,
    AUTOMATICA
}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/chaves")
@RequiredArgsConstructor
public class ChaveController {
    
    private final RotacaoChaveService rotacaoService;
    
    @PostMapping("/{realmId}/rotacionar")
    @Operation(summary = "Rotacionar chaves manualmente", description = "Gera novo par de chaves e inativa a chave anterior")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chaves rotacionadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Realm não encontrado")
    })
    ResponseEntity<ChaveResponse> rotacionar(@PathVariable UUID realmId) {
        ChaveResponse response = rotacaoService.rotacionar(realmId, TipoRotacao.MANUAL);
        return ResponseEntity.ok(response);
    }
}
```

### Service
```java
@Service
@Transactional
@RequiredArgsConstructor
public class RotacaoChaveServiceImpl implements RotacaoChaveService {
    
    private final ChaveCriptograficaRepository chaveRepository;
    private final RotacaoChaveRepository rotacaoRepository;
    private final RealmRepository realmRepository;
    private final RsaKeyGenerator rsaGenerator;
    private final AesCriptografiaService aesService;
    private final AuditoriaService auditoriaService;
    
    @Override
    public ChaveResponse rotacionar(UUID realmId, TipoRotacao tipo) {
        Realm realm = realmRepository.findById(realmId)
            .orElseThrow(() -> new RealmNotFoundException(realmId));
        
        ChaveCriptografica chaveAtual = chaveRepository.findByRealmIdAndStatus(realmId, StatusChave.ATIVA)
            .orElseThrow(() -> new NenhumaChaveAtivaException(realmId));
        
        chaveAtual.setStatus(StatusChave.INATIVA);
        chaveAtual.setDataInativacao(LocalDateTime.now());
        chaveRepository.save(chaveAtual);
        
        ChaveCriptografica novaChave = criarNovaChave(realm);
        novaChave = chaveRepository.save(novaChave);
        
        RotacaoChave rotacao = new RotacaoChave();
        rotacao.setRealm(realm);
        rotacao.setChaveAnteriorId(chaveAtual.getId());
        rotacao.setChaveNovaId(novaChave.getId());
        rotacao.setTipo(tipo);
        rotacao.setDataRotacao(LocalDateTime.now());
        rotacaoRepository.save(rotacao);
        
        auditoriaService.registrarEvento(TipoEventoAuditoria.ROTACAO_CHAVE_MANUAL, 
            "Chaves rotacionadas para realm: " + realm.getNome());
        
        return mapToResponse(novaChave);
    }
    
    private ChaveCriptografica criarNovaChave(Realm realm) {
        KeyPair keyPair = rsaGenerator.generateKeyPair();
        
        ChaveCriptografica chave = new ChaveCriptografica();
        chave.setVersao(UUID.randomUUID());
        chave.setRealm(realm);
        chave.setPublicKey(rsaGenerator.publicKeyToString(keyPair.getPublic()));
        chave.setPrivateKey(aesService.encrypt(rsaGenerator.privateKeyToString(keyPair.getPrivate())));
        chave.setStatus(StatusChave.ATIVA);
        chave.setDataCriacao(LocalDateTime.now());
        
        return chave;
    }
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@SpringBootTest
public class RotacaoManualTest {
    
    @Autowired
    private RotacaoChaveService rotacaoService;
    
    @Test
    dadoChaveAtiva_quandoRotacionarManualmente_entaoNovaChaveAtivaEAnteriorInativa() {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        ChaveCriptografica chaveAntiga = chaveService.gerarChaveEntity(realm.getId());
        
        ChaveResponse response = rotacaoService.rotacionar(realm.getId(), TipoRotacao.MANUAL);
        
        ChaveCriptografica chaveAtualizada = chaveRepository.findById(chaveAntiga.getId()).orElseThrow();
        assertThat(chaveAtualizada.getStatus()).isEqualTo(StatusChave.INATIVA);
        
        ChaveCriptografica novaChave = chaveRepository.findById(response.id()).orElseThrow();
        assertThat(novaChave.getStatus()).isEqualTo(StatusChave.ATIVA);
        assertThat(novaChave.getVersao()).isNotEqualTo(chaveAntiga.getVersao());
    }
}
```

---

## Dependências

- História 5.1: Gerar Par de Chaves RSA por Realm
- Epic 1: Gestão de Realms
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Chave anterior marcada como INATIVA (não expira)
- Grace period: chaves antigas ainda validam tokens
- Registrar auditoria de rotação manual
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Agent Model Used
GPT-4 Turbo (OpenAI)

### Debug Log References
- N/A (implementation completed successfully)

### Completion Notes
- Implementada rotação manual de chaves RSA
- Chave anterior marcada como INATIVA (não expira)
- Nova chave marcada como ATIVA para assinatura
- Grace period mantido para validação de tokens
- Registro completo de eventos de rotação
- API RESTful com múltiplos endpoints para gestão
- Histórico de rotações disponível

### File List
- src/main/java/br/com/plataforma/conexaodigital/chave/domain/service/RotacaoChaveService.java
- src/main/java/br/com/plataforma/conexaodigital/chave/domain/service/impl/RotacaoChaveServiceImpl.java
- src/main/java/br/com/plataforma/conexaodigital/chave/api/controller/RotacaoChaveController.java
- src/test/java/br/com/plataforma/conexaodigital/chave/domain/service/impl/RotacaoChaveServiceImplTest.java
- src/test/java/br/com/plataforma/conexaodigital/chave/api/controller/RotacaoChaveControllerTest.java

### Change Log
- Created: Serviço de rotação de chaves com suporte a MANUAL e AUTOMATICA
- Created: Entidade RotacaoChave para histórico de rotações
- Created: Controller com endpoints para rotação e consulta de histórico
- Created: Endpoint POST /api/v1/chaves/{realmId}/rotacionar para rotação manual
- Created: Endpoints GET para histórico, rotação recente e status
- Created: Validação de regras de negócio (chave ativa existente para manual)
- Created: Testes unitários com cobertura para todos os cenários
- Created: DTOs específicos para respostas de rotação

### Status
Ready for Review
