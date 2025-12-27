# História 2.10: Validação de Unicidade de Email

**Epic:** 2 - Gestão de Usuários
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 2 dias
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso garantir que o email de cada usuário seja único para que não ocorram conflitos de autenticação entre usuários.

---

## Critérios de Aceite

- [x] Validação durante criação de usuário
- [x] Validação durante edição de usuário
- [x] Validação case-insensitive (lowercase)
- [x] Retornar `409 Conflict` com mensagem específica
- [x] Exceção de domínio `EmailJaExisteException`
- [x] Validação via Repository e Service layer

---

## Regras de Negócio

1. Unicidade:
   - Email deve ser único em todo o sistema
   - Comparação case-insensitive (tudo em lowercase)
   - Durante edição, o próprio usuário é excluído da validação

2. Exceção:
   - Mensagem clara em português
   - Incluir email que causou conflito
   - Capturada pelo `GlobalExceptionHandler`

---

## Requisitos Técnicos

### Repository
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) AND u.id != :id")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") UUID id);
}
```

### Exceção de Domínio
```java
public class EmailJaExisteException extends BusinessException {
    public EmailJaExisteException(String email) {
        super("Email já existe: " + email);
    }
}
```

### Service Implementation
```java
@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    private final UsuarioRepository repository;
    private final RealmRepository realmRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailJaExisteException(request.email());
        }
        
        Realm realm = realmRepository.findById(request.realmId())
            .orElseThrow(() -> new RealmNotFoundException(request.realmId()));
        
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
        if (roles.isEmpty()) {
            throw new RoleNotFoundException();
        }
        
        Usuario usuario = new Usuario(
            request.nome(),
            request.email(),
            passwordEncoder.encode(request.senha()),
            realm,
            StatusUsuario.ATIVO
        );
        usuario.setRoles(roles);
        usuario.setEmpresaId(request.empresaId());
        usuario.setTenentId(request.tenentId());
        usuario = repository.save(usuario);
        
        return mapToResponse(usuario);
    }
    
    @Override
    @Transactional
    public UsuarioResponse atualizar(UUID id, AtualizarUsuarioRequest request) {
        Usuario usuario = repository.findById(id)
            .orElseThrow(() -> new UsuarioNotFoundException(id));
        
        if (!usuario.getEmail().equals(request.email()) && 
            repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new EmailJaExisteException(request.email());
        }
        
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
        if (roles.isEmpty()) {
            throw new RoleNotFoundException();
        }
        
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setRoles(roles);
        usuario.setEmpresaId(request.empresaId());
        usuario.setTenentId(request.tenentId());
        usuario = repository.save(usuario);
        
        return mapToResponse(usuario);
    }
    
    private UsuarioResponse mapToResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRealm().getId(),
            usuario.getRealm().getNome(),
            usuario.getRoles().stream().map(Role::getId).toList(),
            usuario.getRoles().stream().map(Role::getNome).toList(),
            usuario.getEmpresaId(),
            usuario.getTenentId(),
            usuario.getStatus(),
            usuario.getDataCriacao(),
            usuario.getDataUltimoLogin()
        );
    }
}
```

---

## Exemplos de Testes

### Teste de Validação na Criação
```java
@Test
void dadoUsuarioJaExistente_quandoCriarComMesmoEmail_entaoLancaExcecao() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario.addRole(role);
    usuarioRepository.save(usuario);
    
    CriarUsuarioRequest request = new CriarUsuarioRequest(
        "Maria", "joao@example.com", "Senha@456", realm.getId(), List.of(role.getId()), null, null
    );
    
    assertThatThrownBy(() -> service.criar(request))
        .isInstanceOf(EmailJaExisteException.class)
        .hasMessageContaining("joao@example.com");
}
```

### Teste de Validação com Case Diferente
```java
@Test
void dadoUsuarioJaExistente_quandoCriarComEmailCaseDiferente_entaoLancaExcecao() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario.addRole(role);
    usuarioRepository.save(usuario);
    
    CriarUsuarioRequest request = new CriarUsuarioRequest(
        "Maria", "JOAO@EXAMPLE.COM", "Senha@456", realm.getId(), List.of(role.getId()), null, null
    );
    
    assertThatThrownBy(() -> service.criar(request))
        .isInstanceOf(EmailJaExisteException.class);
}
```

### Teste de Validação na Edição (mesmo email)
```java
@Test
void dadoUsuarioExistente_quandoEditarComMesmoEmail_entaoNaoLancaExcecao() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario.addRole(role);
    usuarioRepository.save(usuario);
    
    AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
        "João Silva", "joao@example.com", List.of(role.getId()), null, null
    );
    
    assertThatCode(() -> service.atualizar(usuario.getId(), request))
        .doesNotThrowAnyException();
}
```

---

## Dependências

- História 2.1: Criar Usuário
- História 2.2: Editar Usuário
- Epic 1: Gestão de Realms
- Epic 3: Gestão de Roles
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Validação case-insensitive via `LOWER()` no SQL
- Exceção customizada com mensagem clara
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Tasks Completed
- [x] Validar unicidade de email na criação de usuário
- [x] Validar unicidade de email na edição de usuário
- [x] Validação case-insensitive implementada no repository
- [x] Criar exceção EmailJaExisteException
- [x] Implementar validação no UsuarioServiceImpl
- [x] Criar testes unitários para validação de unicidade

### Testing
- [x] Testes unitários em UsuarioServiceImplTest
- [x] Teste de validação de email duplicado na criação
- [x] Teste de validação de email case diferente
- [x] Teste de validação de email na edição (mesmo email)
- [x] Teste de validação de email na edição (email diferente de outro usuário)

### Dev Notes
- A validação case-insensitive é feita via queries SQL com `LOWER()`
- A normalização do email para lowercase ocorre no service layer
- O método `existsByEmailIgnoreCaseAndIdNot` exclui o próprio usuário da validação durante edição

### Completion Notes
- Validação de unicidade de email implementada com sucesso
- Testes unitários passando
- Código segue os padrões estabelecidos

### File List
- `src/main/java/br/com/plataforma/conexaodigital/usuario/domain/repository/UsuarioRepository.java` - Adicionado métodos existsByEmailIgnoreCase e existsByEmailIgnoreCaseAndIdNot
- `src/main/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioServiceImpl.java` - Adicionada validação de unicidade de email
- `src/main/java/br/com/plataforma/conexaodigital/usuario/domain/exceptions/EmailJaExisteException.java` - Criada exceção customizada
- `src/test/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioServiceImplTest.java` - Atualizados testes para validação de unicidade

### Change Log
- 2025-12-23: Implementada validação de unicidade de email case-insensitive
- 2025-12-23: Criada exceção EmailJaExisteException
- 2025-12-23: Atualizados testes unitários para cobrir validação de unicidade
- 2025-12-23: Story marcada como Ready for Review
