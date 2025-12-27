# História 7.4: Job de Limpeza de Eventos Antigos (Retenção)

**Epic:** 7 - Auditoria de Eventos de Segurança
**Status:** Done  
**Prioridade:** Média  
**Estimativa:** 2 dias  
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso limpar eventos de auditoria antigos periodicamente para que eu possa controlar o tamanho da tabela e manter o desempenho do banco de dados.

---

## Critérios de Aceite

- [x] Job agendado para limpeza de eventos antigos
- [x] Configuração de período de retenção via application.properties
- [x] Padrão: 90 dias
- [x] Execução diária (pode ser configurável)
- [x] Log de eventos removidos
- [x] Exclusão em lote para performance

---

## Regras de Negócio

1. Retenção:
   - Eventos mais antigos que o período de retenção são excluídos
   - Padrão: 90 dias
   - Configurável via `auditoria.retencao.dias`

2. Job:
   - Executado diariamente às 00:00
   - Usa `@Scheduled`
   - Exclui eventos em lote

---

## Requisitos Técnicos

### application.properties
```properties
# Configuração de retenção de auditoria
auditoria.retencao.dias=90
auditoria.limpeza.cron=0 0 0 * * ?
auditoria.limpeza.batch.size=1000
```

### Scheduler
```java
@Component
@RequiredArgsConstructor
public class AuditoriaLimpezaScheduler {
    
    private final EventoAuditoriaRepository repository;
    
    @Value("${auditoria.retencao.dias:90}")
    private int diasRetencao;
    
    @Value("${auditoria.limpeza.batch.size:1000}")
    private int batchSize;
    
    private final Logger logger = LoggerFactory.getLogger(AuditoriaLimpezaScheduler.class);
    
    @Scheduled(cron = "${auditoria.limpeza.cron:0 0 0 * * ?}")
    @Transactional
    public void limparEventosAntigos() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(diasRetencao);
        
        logger.info("Iniciando limpeza de eventos de auditoria anteriores a {}", dataLimite);
        
        int totalRemovidos = 0;
        List<UUID> eventosParaRemover;
        
        do {
            eventosParaRemover = repository.findIdsByDataCriacaoBefore(dataLimite, PageRequest.of(0, batchSize));
            
            if (!eventosParaRemover.isEmpty()) {
                repository.deleteAllByIdInBatch(eventosParaRemover);
                totalRemovidos += eventosParaRemover.size();
                logger.info("Removidos {} eventos de auditoria (total: {})", eventosParaRemover.size(), totalRemovidos);
            }
        } while (eventosParaRemover.size() == batchSize);
        
        logger.info("Limpeza de eventos de auditoria concluída. Total removido: {}", totalRemovidos);
    }
}
```

### Repository
```java
@Repository
public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, UUID>, JpaSpecificationExecutor<EventoAuditoria> {
    
    @Query("SELECT e.id FROM EventoAuditoria e WHERE e.dataCriacao < :dataLimite")
    List<UUID> findIdsByDataCriacaoBefore(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM EventoAuditoria e WHERE e.id IN :ids")
    void deleteAllByIdInBatch(@Param("ids") List<UUID> ids);
}
```

---

## Exemplos de Testes

### Teste de Job de Limpeza
```java
@SpringBootTest
public class AuditoriaLimpezaSchedulerTest {
    
    @Autowired
    private AuditoriaLimpezaScheduler scheduler;
    
    @Autowired
    private EventoAuditoriaRepository repository;
    
    @Test
    void dadoEventosAntigos_quandoExecutarLimpeza_entaoEventosRemovidos() {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        
        EventoAuditoria eventoAntigo = new EventoAuditoria();
        eventoAntigo.setTipo(TipoEventoAuditoria.LOGIN);
        eventoAntigo.setDescricao("Login antigo");
        eventoAntigo.setRealm(realm);
        eventoAntigo.setDataCriacao(LocalDateTime.now().minusDays(100));
        repository.save(eventoAntigo);
        
        EventoAuditoria eventoRecente = new EventoAuditoria();
        eventoRecente.setTipo(TipoEventoAuditoria.LOGIN);
        eventoRecente.setDescricao("Login recente");
        eventoRecente.setRealm(realm);
        eventoRecente.setDataCriacao(LocalDateTime.now().minusDays(10));
        repository.save(eventoRecente);
        
        scheduler.limparEventosAntigos();
        
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById(eventoRecente.getId())).isPresent();
        assertThat(repository.findById(eventoAntigo.getId())).isEmpty();
    }
}
```

---

## Dependências

- História 7.1: Modelo de Domínio de Auditoria

---

## Pontos de Atenção

- Exclusão em lote para performance
- Configuração de retenção via properties
- Logs detalhados de execução
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Agent Model Used
Claude-3.5-Sonnet

### Debug Log References
N/A - Arquivos implementados e verificados em 2025-12-24

### Completion Notes List
- ✅ AuditoriaLimpezaScheduler implementado com @Scheduled
- ✅ Configuração de retenção via application.properties (auditoria.retencao.dias)
- ✅ Configuração de cron e batch size
- ✅ Exclusão em lote para performance (batch de 1000 registros)
- ✅ Logs detalhados de execução
- ✅ Método findIdsByDataCriacaoBefore adicionado ao repository

### File List
**Arquivos Criados:**
- `src/main/java/br/com/plataforma/conexaodigital/auditoria/infrastructure/scheduler/AuditoriaLimpezaScheduler.java`
- `src/main/resources/application.yml` (atualizado com configurações de auditoria)

---

## QA Results

*Será preenchido pelo QA Agent após conclusão*