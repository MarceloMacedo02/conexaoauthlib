# História 8.4: Bootstrap de Usuário Administrador

**Epic:** 8 - Bootstrap do Sistema
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Média

---

## Descrição

Como sistema, preciso criar um usuário administrador global automaticamente no bootstrap para que eu tenha acesso administrativo inicial ao sistema.

---

## Critérios de Aceite

- [x] Criar usuário administrador com credenciais via variáveis de ambiente
- [x] Usuário associado ao Realm Master
- [x] Usuário associado à role `ADMIN`
- [x] Status do usuário: `ATIVO`
- [x] Processo idempotente (só cria se não existir)
- [x] Auditoria do evento registrada
- [x] Validação de credenciais obrigatórias

---

## Regras de Negócio

1. Usuário Administrador:
   - Nome, email e senha obtidos de variáveis de ambiente
   - Associado ao Realm Master
   - Associado à role `ADMIN`
   - Status: `ATIVO`
   - Pode ser editado posteriormente

2. Variáveis de Ambiente:
   - `BOOTSTRAP_ADMIN_USERNAME` (obrigatório)
   - `BOOTSTRAP_ADMIN_EMAIL` (obrigatório)
   - `BOOTSTRAP_ADMIN_PASSWORD` (obrigatório)

3. Idempotência:
   - Verifica se usuário com email existe antes de criar
   - Se existir, ignora e continua

---

## Requisitos Técnicos

### application.properties
```properties
# Configuração de Usuário Administrador
bootstrap.admin.username=Admin
bootstrap.admin.email=admin@conexaoauth.com
bootstrap.admin.password=Admin@123
```

### Service
```java
@Service
@RequiredArgsConstructor
public class UsuarioBootstrapServiceImpl implements UsuarioBootstrapService {
    
    private final UsuarioRepository repository;
    private final RealmBootstrapService realmBootstrapService;
    private final RoleBootstrapService roleBootstrapService;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    
    @Value("${bootstrap.admin.username:Admin}")
    private String adminUsername;
    
    @Value("${bootstrap.admin.email:admin@conexaoauth.com}")
    private String adminEmail;
    
    @Value("${bootstrap.admin.password}")
    private String adminPassword;
    
    private final Logger logger = LoggerFactory.getLogger(UsuarioBootstrapServiceImpl.class);
    
    @Override
    @Transactional
    public void criarUsuarioAdmin() {
        validarCredenciais();
        
        if (repository.existsByEmailIgnoreCase(adminEmail)) {
            logger.info("Usuário administrador já existe (email: {}), ignorando criação", adminEmail);
            return;
        }
        
        logger.info("Criando usuário administrador: {} ({})", adminUsername, adminEmail);
        
        Realm realmMaster = realmBootstrapService.obterRealmMaster();
        
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setNome(adminUsername);
        usuarioAdmin.setEmail(adminEmail);
        usuarioAdmin.setSenha(passwordEncoder.encode(adminPassword));
        usuarioAdmin.setRealm(realmMaster);
        usuarioAdmin.setStatus(StatusUsuario.ATIVO);
        
        usuarioAdmin = repository.save(usuarioAdmin);
        
        // Associar role ADMIN
        Role roleAdmin = roleBootstrapService.obterRole("ADMIN", realmMaster.getId());
        usuarioAdmin.addRole(roleAdmin);
        usuarioAdmin = repository.save(usuarioAdmin);
        
        auditoriaService.registrarEvento(
            TipoEventoAuditoria.BOOTSTRAP_USUARIO_ADMIN,
            "Usuário administrador criado via bootstrap: " + usuarioAdmin.getEmail(),
            usuarioAdmin.getId(),
            usuarioAdmin.getEmail(),
            realmMaster.getId(),
            null
        );
        
        logger.info("Usuário administrador criado com sucesso: {}", usuarioAdmin.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean usuarioAdminCriado() {
        return repository.existsByEmailIgnoreCase(adminEmail);
    }
    
    private void validarCredenciais() {
        if (StringUtils.isBlank(adminPassword)) {
            throw new BootstrapException("Senha do usuário administrador não foi configurada (bootstrap.admin.password)");
        }
        
        if (adminPassword.length() < 8) {
            throw new BootstrapException("Senha do usuário administrador deve ter no mínimo 8 caracteres");
        }
    }
}
```

### Interface
```java
public interface UsuarioBootstrapService {
    void criarUsuarioAdmin();
    boolean usuarioAdminCriado();
}
```

---

## Exemplos de Testes

### Teste de Criação do Usuário Administrador
```java
@SpringBootTest
public class UsuarioBootstrapServiceTest {
    
    @Autowired
    private UsuarioBootstrapService bootstrapService;
    
    @Autowired
    private UsuarioRepository repository;
    
    @Autowired
    private RealmRepository realmRepository;
    
    @Test
    dadoRealmMasterERolesCriadas_quandoExecutarBootstrap_entaoUsuarioAdminCriado() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        Role roleAdmin = roleRepository.save(new Role("ADMIN", "Admin", realmMaster, true));
        
        bootstrapService.criarUsuarioAdmin();
        
        Usuario usuarioAdmin = repository.findByEmailIgnoreCase("admin@conexaoauth.com").orElseThrow();
        assertThat(usuarioAdmin.getNome()).isEqualTo("Admin");
        assertThat(usuarioAdmin.getStatus()).isEqualTo(StatusUsuario.ATIVO);
        assertThat(usuarioAdmin.getRealm().getId()).isEqualTo(realmMaster.getId());
        assertThat(usuarioAdmin.getRoles()).contains(roleAdmin);
    }
}
```

### Teste de Idempotência
```java
@Test
    dadoUsuarioAdminCriado_quandoExecutarBootstrapNovamente_entaoNaoCriaNovoUsuario() {
        Realm realmMaster = realmRepository.save(new Realm("master", StatusRealm.ATIVO));
        Role roleAdmin = roleRepository.save(new Role("ADMIN", "Admin", realmMaster, true));
        
        bootstrapService.criarUsuarioAdmin();
        
        UUID idPrimeiraCriacao = repository.findByEmailIgnoreCase("admin@conexaoauth.com")
            .orElseThrow().getId();
        
        bootstrapService.criarUsuarioAdmin();
        
        Usuario usuarioSegundaExecucao = repository.findByEmailIgnoreCase("admin@conexaoauth.com")
            .orElseThrow();
        assertThat(usuarioSegundaExecucao.getId()).isEqualTo(idPrimeiraCriacao);
        
        assertThat(repository.count()).isEqualTo(1);
}
```

---

## Dependências

- História 8.1: Configuração de Bootstrap
- História 8.2: Bootstrap de Realm Master
- História 8.3: Bootstrap de Roles Padrão
- História 2.1: Criar Usuário
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Validação de credenciais obrigatórias
- Senha criptografada com BCrypt
- Idempotência garantida por email
- Checkstyle: Seguir Google Java Style Guide

---

## QA Results

### Review Date: 2025-12-24

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

**BLOQUEIO CRÍTICO**: Story não implementada - status "Pendente". O sistema não terá acesso administrativo inicial sem esta funcionalidade crítica.

### Compliance Check

- Coding Standards: ✗ Não aplicável - não implementado
- Project Structure: ✗ Não aplicável - não implementado
- Testing Strategy: ✗ Não aplicável - não implementado
- All ACs Met: ✗ Nenhum critério implementado

### Improvements Checklist

- [ ] **CRÍTICO**: Implementar BootstrapServiceImpl.criarUsuarioAdmin()
- [ ] **CRÍTICO**: Criar validação de credenciais de admin
- [ ] **CRÍTICO**: Implementar criptografia BCrypt para senha
- [ ] **CRÍTICO**: Associar usuário ao realm master e role ADMIN
- [ ] **CRÍTICO**: Implementar auditoria BOOTSTRAP_USUARIO_ADMIN
- [ ] Criar testes de integração
- [ ] Adicionar validação de força de senha

### Security Review

❌ **RISCO CRÍTICO**: Sem acesso administrativo inicial ao sistema. Impossível realizar configurações pós-instalação.

### Performance Considerations

❌ Não aplicável - funcionalidade não implementada.

### Files Modified During Review

Nenhum arquivo modificado durante esta revisão.

### Gate Status

Gate: FAIL → docs/qa/gates/8.4-bootstrap-usuario-admin.yml

### Recommended Status

[✗ Changes Required - Story not implemented]
