# História 5.4: Rotação Automática (Cron Mensal)

**Epic:** 5 - Gestão de Chaves Criptográficas  
**Status:** Pendente  
**Prioridade:** Alta  
**Estimativa:** 3 dias  
**Complexidade**: Média

---

## Descrição

Como sistema, preciso rotacionar automaticamente as chaves criptográficas mensalmente (dia 1) para que eu possa garantir a segurança das chaves sem intervenção manual.

---

## Critérios de Aceite

- [ ] Cron job executado dia 1 de cada mês
- [ ] Gera novo par de chaves RSA-2048
- [ ] Marca chave anterior como `INATIVA`
- [ ] Nova chave marcada como `ATIVA`
- [ ] Grace period: chaves antigas ainda validam tokens
- [ ] Auditoria dos eventos deve ser registrada (tipo: ROTACAO_CHAVE_AUTOMATICA)
- [ ] Execução idempotente (se já rotacionou no mês, não executa novamente)
- [ ] Logs de execução registrados

---

## Regras de Negócio

1. Rotação Automática:
   - Executada dia 1 de cada mês às 00:00
   - Apenas realms com chaves ativas são rotacionados
   - Regras de rotação iguais à rotação manual

2. Idempotência:
   - Se já houve rotação no mês atual, não executa novamente
   - Verifica última rotação do tipo AUTOMATICA

3. Grace Period:
   - Chaves anteriores ainda podem validar tokens
   - Tokens anteriores expiram naturalmente

---

## Requisitos Técnicos

### Rotacao Chave Scheduler
```java
@Component
@RequiredArgsConstructor
public class RotacaoChaveScheduler {
    
    private final RotacaoChaveService rotacaoService;
    private final RealmRepository realmRepository;
    private final RotacaoChaveRepository rotacaoRepository;
    private final Logger logger = LoggerFactory.getLogger(RotacaoChaveScheduler.class);
    
    @Scheduled(cron = "0 0 0 1 * ?")
    public void rotacionarChavesAutomaticamente() {
        logger.info("Iniciando rotação automática de chaves");
        
        List<Realm> realms = realmRepository.findAll();
        
        for (Realm realm : realms) {
            try {
                if (deveRotacionar(realm)) {
                    rotacaoService.rotacionar(realm.getId(), TipoRotacao.AUTOMATICA);
                    logger.info("Chaves rotacionadas automaticamente para realm: {}", realm.getNome());
                } else {
                    logger.info("Ignorando realm {}: já houve rotação este mês", realm.getNome());
                }
            } catch (Exception e) {
                logger.error("Erro ao rotacionar chaves para realm: {}", realm.getNome(), e);
            }
        }
        
        logger.info("Rotação automática de chaves concluída");
    }
    
    private boolean deveRotacionar(Realm realm) {
        YearMonth mesAtual = YearMonth.now();
        
        Optional<RotacaoChave> ultimaRotacaoAutomatica = 
            rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(realm, TipoRotacao.AUTOMATICA);
        
        if (ultimaRotacaoAutomatica.isEmpty()) {
            return true;
        }
        
        LocalDateTime dataUltimaRotacao = ultimaRotacaoAutomatica.get().getDataRotacao();
        YearMonth mesUltimaRotacao = YearMonth.from(dataUltimaRotacao);
        
        return !mesUltimaRotacao.equals(mesAtual);
    }
}
```

### Repository
```java
@Repository
public interface RotacaoChaveRepository extends JpaRepository<RotacaoChave, UUID> {
    Optional<RotacaoChave> findTopByRealmAndTipoOrderByDataRotacaoDesc(Realm realm, TipoRotacao tipo);
    
    List<RotacaoChave> findByRealmOrderByDataRotacaoDesc(Realm realm);
}
```

---

## Exemplos de Testes

### Teste de Execução do Scheduler
```java
@SpringBootTest
public class RotacaoAutomaticaTest {
    
    @Autowired
    private RotacaoChaveScheduler scheduler;
    
    @Autowired
    private RotacaoChaveRepository rotacaoRepository;
    
    @Test
    dadoRealmComChaveAtiva_quandoExecutarRotaçãoAutomatica_entaoNovaChaveGerada() {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        chaveService.gerarChaveEntity(realm.getId());
        
        scheduler.rotacionarChavesAutomaticamente();
        
        ChaveCriptografica novaChave = chaveRepository.findByRealmIdAndStatus(realm.getId(), StatusChave.ATIVA)
            .orElseThrow();
        
        assertThat(novaChave).isNotNull();
        
        RotacaoChave rotacao = rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(realm, TipoRotacao.AUTOMATICA)
            .orElseThrow();
        assertThat(rotacao).isNotNull();
    }
}
```

### Teste de Idempotência
```java
@Test
    dadoRotaçãoJaExecutadaMesAtual_quandoExecutarRotaçãoAutomatica_entaoNaoRotacionaNovamente() {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        chaveService.gerarChaveEntity(realm.getId());
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.AUTOMATICA);
        
        ChaveCriptografica chaveAntes = chaveRepository.findByRealmIdAndStatus(realm.getId(), StatusChave.ATIVA)
            .orElseThrow();
        
        scheduler.rotacionarChavesAutomaticamente();
        
        ChaveCriptografica chaveDepois = chaveRepository.findByRealmIdAndStatus(realm.getId(), StatusChave.ATIVA)
            .orElseThrow();
        
        assertThat(chaveDepois.getId()).isEqualTo(chaveAntes.getId());
}
```

---

## Dependências

- História 5.1: Gerar Par de Chaves RSA por Realm
- História 5.3: Rotação Manual de Chaves
- Epic 1: Gestão de Realms
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Scheduler idempotente (evita rotações duplicadas no mesmo mês)
- Logs detalhados de execução
- Checkstyle: Seguir Google Java Style Guide
