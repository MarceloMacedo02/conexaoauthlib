# História 8.5: Bootstrap de Chaves Criptográficas

**Epic:** 8 - Bootstrap do Sistema
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Média

---

## Descrição

Como sistema, preciso criar chaves criptográficas para todos os realms automaticamente no bootstrap para que eu possa assinar e validar tokens JWT imediatamente.

---

## Critérios de Aceite

- [x] Criar par de chaves RSA-2048 para o Realm Master
- [x] Criar par de chaves RSA-2048 para cada realm existente
- [x] Chave pública não criptografada
- [x] Chave privada criptografada com AES-128
- [x] Chaves marcadas como `ATIVA`
- [x] Processo idempotente (só cria se não existir chave ativa)
- [x] Auditoria dos eventos registrada
- [x] Log de criação/ignorada

---

## Regras de Negócio

1. Chaves Criptográficas:
   - RSA-2048 bits
   - Chave pública não criptografada
   - Chave privada criptografada com AES-128
   - Status: `ATIVA`
   - Cada realm tem seu próprio par de chaves

2. Realm Master:
   - Chaves são criadas primeiro para o Realm Master
   - Depois para todos os outros realms existentes

3. Idempotência:
   - Verifica se realm possui chave `ATIVA` antes de criar
   - Se existir, ignora e continua

---

## Requisitos Técnicos

### Service
```java
@Service
@RequiredArgsConstructor
public class ChaveBootstrapServiceImpl implements ChaveBootstrapService {
    
    private final ChaveCriptograficaRepository chaveRepository;
    private final RealmRepository realmRepository;
    private final RsaKeyGenerator rsaGenerator;
    private final AesCriptografiaService aesService;
    private final AuditoriaService auditoriaService;
    
    private final Logger logger = LoggerFactory.getLogger(ChaveBootstrapServiceImpl.class);
    
    @Override
    @Transactional
    public void criarChavesParaTodosRealms() {
        List<Realm> realms = realmRepository.findAll();
        
        for (Realm realm : realms) {
            criarChaveSeNaoExistir(realm);
        }
    }
    
    private void criarChaveSeNaoExistir(Realm realm) {
        if (chaveRepository.existsByRealmIdAndStatus(realm.getId(), StatusChave.ATIVA)) {
            logger.info("Realm {} já possui chave ativa, ignorando criação", realm.getNome());
            return;
        }
        
        logger.info("Criando par de chaves RSA-2048 para realm: {}", realm.getNome());
        
        KeyPair keyPair = rsaGenerator.generateKeyPair();
        
        ChaveCriptografica chave = new ChaveCriptografica();
        chave.setVersao(UUID.randomUUID());
        chave.setRealm(realm);
        chave.setPublicKey(rsaGenerator.publicKeyToString(keyPair.getPublic()));
        chave.setPrivateKey(aesService.encrypt(rsaGenerator.privateKeyToString(keyPair.getPrivate())));
        chave.setStatus(StatusChave.ATIVA);
        chave.setDataCriacao(LocalDateTime.now());
        
        chave = chaveRepository.save(chave);
        
        auditoriaService.registrarEvento(
            TipoEventoAuditoria.GERACAO_CHAVE,
            "Chave criptográfica criada via bootstrap para realm: " + realm.getNome(),
            null, null, realm.getId(), Map.of("versao", chave.getVersao().toString())
        );
        
        logger.info("Par de chaves criado com sucesso para realm: {} (versão: {})", 
            realm.getNome(), chave.getVersao());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean chavesCriadas() {
        List<Realm> realms = realmRepository.findAll();
        
        for (Realm realm : realms) {
            if (!chaveRepository.existsByRealmIdAndStatus(realm.getId(), StatusChave.ATIVA)) {
                return false;
            }
        }
        
        return true;
    }
}
```

### Interface
```java
public interface ChaveBootstrapService {
    void criarChavesParaTodosRealms();
    boolean chavesCriadas();
}
```

---

## Exemplos de Testes

### Teste de Criação de Chaves
```java
@SpringBootTest
public class ChaveBootstrapServiceTest {
    
    @Autowired
    private ChaveBootstrapService bootstrapService;
    
    @Autowired
    private ChaveCriptograficaRepository chaveRepository;
    
    @Autowired
    private RealmRepository realmRepository;
    
    @Test
    dadoRealmMasterCriado_quandoExecutarBootstrap_entaoChaveCriada() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        
        bootstrapService.criarChavesParaTodosRealms();
        
        List<ChaveCriptografica> chaves = chaveRepository.findByRealmId(realmMaster.getId());
        assertThat(chaves).hasSize(1);
        assertThat(chaves.get(0).getStatus()).isEqualTo(StatusChave.ATIVA);
        assertThat(chaves.get(0).getPublicKey()).isNotEmpty();
        assertThat(chaves.get(0).getPrivateKey()).isNotEmpty();
    }
}
```

### Teste de Idempotência
```java
@Test
    dadoChavesCriadas_quandoExecutarBootstrapNovamente_entaoNaoCriaNovasChaves() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        bootstrapService.criarChavesParaTodosRealms();
        
        long countPrimeiraCriacao = chaveRepository.count();
        
        bootstrapService.criarChavesParaTodosRealms();
        
        assertThat(chaveRepository.count()).isEqualTo(countPrimeiraCriacao);
}
```

---

## Dependências

- História 8.1: Configuração de Bootstrap
- História 8.2: Bootstrap de Realm Master
- História 5.1: Gerar Par de Chaves RSA por Realm
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Chave privada criptografada com AES-128
- Idempotência garantida por realm
- Checkstyle: Seguir Google Java Style Guide

---

## QA Results

### Review Date: 2025-12-24

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

**BLOQUEIO CRÍTICO**: Story não implementada - status "Pendente". O sistema não terá chaves criptográficas para assinar tokens JWT.

### Compliance Check

- Coding Standards: ✗ Não aplicável - não implementado
- Project Structure: ✗ Não aplicável - não implementado
- Testing Strategy: ✗ Não aplicável - não implementado
- All ACs Met: ✗ Nenhum critério implementado

### Improvements Checklist

- [ ] **CRÍTICO**: Implementar geração de par de chaves RSA-2048
- [ ] **CRÍTICO**: Implementar criptografia AES-128 para chave privada
- [ ] **CRÍTICO**: Criar chaves para todos os realms existentes
- [ ] **CRÍTICO**: Implementar auditoria GERACAO_CHAVE
- [ ] **CRÍTICO**: Garantir idempotência por realm
- [ ] Criar testes de integração de geração
- [ ] Adicionar validação de versão UUID

### Security Review

❌ **RISCO CRÍTICO**: Tokens JWT não podem ser assinados sem chaves criptográficas. Authorization Server completamente não funcional.

### Performance Considerations

❌ Não aplicável - funcionalidade não implementada.

### Files Modified During Review

Nenhum arquivo modificado durante esta revisão.

### Gate Status

Gate: FAIL → docs/qa/gates/8.5-bootstrap-chaves-criptograficas.yml

### Recommended Status

[✗ Changes Required - Story not implemented]
