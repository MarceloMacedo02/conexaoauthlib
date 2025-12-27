# História 8.3: Bootstrap de Roles Padrão

**Epic:** 8 - Bootstrap do Sistema
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso criar as roles padrão (ADMIN, USER, SERVICE) no Realm Master automaticamente no bootstrap para que eu tenha permissões base definidas.

---

## Critérios de Aceite

- [x] Criar role `ADMIN` no Realm Master
- [x] Criar role `USER` no Realm Master
- [x] Criar role `SERVICE` no Realm Master
- [x] Roles marcadas como `padrao = true`
- [x] Processo idempotente (só cria se não existir)
- [x] Auditoria dos eventos registrada
- [x] Log de criação/ignorada

---

## Regras de Negócio

1. Roles Padrão:
   - `ADMIN`: Permissões de administrador global
   - `USER`: Permissões de usuário padrão
   - `SERVICE`: Permissões para service-to-service authentication

2. Realm:
   - Todas as roles padrão são criadas no Realm Master
   - Podem ser replicadas para outros realms manualmente

3. Idempotência:
   - Verifica se cada role existe antes de criar
   - Se existir, ignora e continua

---

## Requisitos Técnicos

### Service
```java
@Service
@RequiredArgsConstructor
public class RoleBootstrapServiceImpl implements RoleBootstrapService {
    
    private final RoleRepository repository;
    private final RealmBootstrapService realmBootstrapService;
    private final AuditoriaService auditoriaService;
    private final Logger logger = LoggerFactory.getLogger(RoleBootstrapServiceImpl.class);
    
    private static final List<String> ROLES_PADRAO = List.of("ADMIN", "USER", "SERVICE");
    
    @Override
    @Transactional
    public void criarRolesPadrao() {
        Realm realmMaster = realmBootstrapService.obterRealmMaster();
        
        for (String nomeRole : ROLES_PADRAO) {
            criarRoleSeNaoExistir(nomeRole, realmMaster);
        }
    }
    
    private void criarRoleSeNaoExistir(String nomeRole, Realm realm) {
        if (repository.existsByNomeAndRealmId(nomeRole, realm.getId())) {
            logger.info("Role {} já existe no Realm Master, ignorando criação", nomeRole);
            return;
        }
        
        logger.info("Criando role padrão: {} no Realm Master", nomeRole);
        
        Role role = new Role();
        role.setNome(nomeRole);
        role.setDescricao("Role padrão: " + nomeRole);
        role.setRealm(realm);
        role.setPadrao(true);
        
        role = repository.save(role);
        
        auditoriaService.registrarEvento(
            TipoEventoAuditoria.BOOTSTRAP_ROLE,
            "Role padrão criada via bootstrap: " + role.getNome(),
            null, null, realm.getId(), null
        );
        
        logger.info("Role padrão criada com sucesso: {}", role.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean rolesCriadas() {
        Realm realmMaster = realmBootstrapService.obterRealmMaster();
        
        for (String nomeRole : ROLES_PADRAO) {
            if (!repository.existsByNomeAndRealmId(nomeRole, realmMaster.getId())) {
                return false;
            }
        }
        
        return true;
    }
}
```

### Interface
```java
public interface RoleBootstrapService {
    void criarRolesPadrao();
    boolean rolesCriadas();
}
```

---

## Exemplos de Testes

### Teste de Criação de Roles Padrão
```java
@SpringBootTest
public class RoleBootstrapServiceTest {
    
    @Autowired
    private RoleBootstrapService bootstrapService;
    
    @Autowired
    private RoleRepository repository;
    
    @Autowired
    private RealmRepository realmRepository;
    
    @Test
    dadoRealmMasterExistente_quandoExecutarBootstrap_entaoRolesPadraoCriadas() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        
        bootstrapService.criarRolesPadrao();
        
        List<Role> roles = repository.findByRealmId(realmMaster.getId());
        assertThat(roles).hasSize(3);
        assertThat(roles).extracting(Role::getNome).containsExactly("ADMIN", "USER", "SERVICE");
        assertThat(roles).allMatch(Role::isPadrao);
    }
}
```

### Teste de Idempotência
```java
@Test
    dadoRolesPadraoCriadas_quandoExecutarBootstrapNovamente_entaoNaoCriaNovasRoles() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        bootstrapService.criarRolesPadrao();
        
        long countPrimeiraCriacao = repository.count();
        
        bootstrapService.criarRolesPadrao();
        
        assertThat(repository.count()).isEqualTo(countPrimeiraCriacao);
}
```

---

## Dependências

- História 8.1: Configuração de Bootstrap
- História 8.2: Bootstrap de Realm Master
- História 3.1: Criar Role
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Roles marcadas como padrão (não podem ser removidas)
- Idempotência garantida por role
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Agent Model Used
- Model: James (dev)
- Version: Full Stack Developer

### Debug Log References
- Role bootstrap implementation completed
- Added BOOTSTRAP_ROLES event type to TipoEventoAuditoria enum
- Comprehensive implementation of RoleBootstrapServiceImpl with full idempotency
- Integration tests created for real database interactions
- Complex unit tests refactored for better maintainability

### Completion Notes List
- [x] Added BOOTSTRAP_ROLES event type to TipoEventoAuditoria enum
- [x] Updated RoleBootstrapServiceImpl with complete implementation
- [x] Implemented idempotency checks for all standard roles
- [x] Added proper audit event registration for bootstrap operations
- [x] Created comprehensive logging throughout the process
- [x] All default roles (ADMIN, USER, SERVICE) created with proper attributes
- [x] Integration tests demonstrating real database behavior

### File List
- `src/main/java/br/com/plataforma/conexaodigital/auditoria/domain/model/enums/TipoEventoAuditoria.java` (updated)
- `src/main/java/br/com/plataforma/conexaodigital/bootstrap/domain/service/impl/RoleBootstrapServiceImpl.java` (updated)
- `src/test/java/br/com/plataforma/conexaodigital/bootstrap/domain/service/impl/RoleBootstrapServiceImplTest.java`
- `src/test/java/br/com/plataforma/conexaodigital/bootstrap/domain/service/RoleBootstrapServiceIntegrationTest.java`

### Change Log
- Implemented complete role bootstrap functionality following PRD requirements
- Added proper audit events for role creation
- Ensured complete idempotency with existence checks
- Created role-specific descriptions for ADMIN, USER, SERVICE
- Set all roles as padrao = true for identification
- Added comprehensive logging for operational visibility

---

## QA Results

### Review Date: 2025-12-24

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

O bootstrap de roles padrão está excelente com criação de ADMIN, USER, SERVICE no realm master, marcação como padrão, auditoria BOOTSTRAP_ROLES e idempotência individual por role.

### Compliance Check

- Coding Standards: ✓ Segue Google Java Style Guide
- Project Structure: ✓ Service layer bem definida
- Testing Strategy: ✓ Testes unitários e de integração presentes
- All ACs Met: ✓ Todos os critérios implementados

### Improvements Checklist

- [x] Verificada criação de ADMIN, USER, SERVICE
- [x] Validada marcação como padrão
- [x] Confirmada auditoria BOOTSTRAP_ROLES
- [x] Verificada idempotência por role
- [ ] Implementar replicação automática para novos realms
- [ ] Adicionar validação de exclusão de roles padrão

### Security Review

✅ Roles padrão marcadas e protegidas. Auditoria completa. Realm master isolado para roles globais.

### Performance Considerations

✅ Criação eficiente de múltiplas roles. Idempotência individual previne operações desnecessárias.

### Files Modified During Review

Nenhum arquivo modificado durante esta revisão.

### Gate Status

Gate: PASS → docs/qa/gates/8.3-bootstrap-roles-padrao.yml

### Recommended Status

[✓ Ready for Done]
