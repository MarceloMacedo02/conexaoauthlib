# História 2.8: Listar Usuários com Paginação e Filtros

**Epic:** 2 - Gestão de Usuários  
**Status:** Concluído  
**Prioridade:** Alta  
**Estimativa:** 4 dias  
**Complexidade**: Alta

---

## Descrição

Como administrador do sistema, quero listar usuários com paginação e filtros avançados para que eu possa navegar e encontrar usuários específicos de forma eficiente.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/usuarios` retorna lista paginada
- [x] Suporta paginação via parâmetros `page`, `size`, `sort`
- [x] Filtro por `nome` (busca parcial, case-insensitive)
- [x] Filtro por `email` (busca parcial, case-insensitive)
- [x] Filtro por `realmId`
- [x] Filtro por `empresaId` (exato)
- [x] Filtro por `tenentId` (exato)
- [x] Filtro por `status` (ATIVO, INATIVO, BLOQUEADO)
- [x] Filtro por `roleIds` (usuários com pelo menos uma das roles)
- [x] Filtro por período de criação (`dataCriacaoInicio`, `dataCriacaoFim`)
- [x] Suporta múltiplos filtros simultâneos
- [x] Retornar `200 OK` com página de resultados
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Paginação:
   - Padrão: `page=0`, `size=20`, `sort=nome,asc`
   - Tamanho máximo de página: 100

2. Filtros:
   - Todos os filtros são opcionais
   - Filtro de nome/email usa `LIKE` (busca parcial)
   - Filtro de realmId, empresaId, tenentId usa exato
   - Filtro de roleIds usa `IN` (usuários com qualquer uma das roles)
   - Filtro de período usa intervalo fechado [inicio, fim]

3. Ordenação:
   - Ordenação padrão: nome ascendente
   - Suporta múltiplos campos de ordenação

4. Segurança:
   - Usuário só pode ver usuários do seu realm (se não for ADMIN global)
   - Validar permissões (em epic 4)

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Gestão de Usuários", description = "Operações de gestão de usuários")
public class UsuarioController {
    
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista usuários com paginação e filtros opcionais")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    ResponseEntity<Page<UsuarioResponse>> listar(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) UUID realmId,
        @RequestParam(required = false) String empresaId,
        @RequestParam(required = false) String tenentId,
        @RequestParam(required = false) StatusUsuario status,
        @RequestParam(required = false) List<UUID> roleIds,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacaoInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacaoFim,
        Pageable pageable
    );
}
```

### JPA Specification
```java
public class UsuarioSpecification {
    public static Specification<Usuario> comNome(String nome) {
        return (root, query, cb) -> 
            nome == null ? null : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
    
    public static Specification<Usuario> comEmail(String email) {
        return (root, query, cb) -> 
            email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
    
    public static Specification<Usuario> comRealmId(UUID realmId) {
        return (root, query, cb) -> 
            realmId == null ? null : cb.equal(root.get("realm").get("id"), realmId);
    }
    
    public static Specification<Usuario> comEmpresaId(String empresaId) {
        return (root, query, cb) -> 
            empresaId == null ? null : cb.equal(root.get("empresaId"), empresaId);
    }
    
    public static Specification<Usuario> comTenentId(String tenentId) {
        return (root, query, cb) -> 
            tenentId == null ? null : cb.equal(root.get("tenentId"), tenentId);
    }
    
    public static Specification<Usuario> comStatus(StatusUsuario status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
    
    public static Specification<Usuario> comRoleIds(List<UUID> roleIds) {
        return (root, query, cb) -> {
            if (roleIds == null || roleIds.isEmpty()) return null;
            Join<Usuario, Role> rolesJoin = root.join("roles", JoinType.LEFT);
            return rolesJoin.get("id").in(roleIds);
        };
    }
    
    public static Specification<Usuario> comDataCriacaoEntre(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, cb) -> {
            if (inicio == null && fim == null) return null;
            if (inicio == null) return cb.lessThanOrEqualTo(root.get("dataCriacao"), fim);
            if (fim == null) return cb.greaterThanOrEqualTo(root.get("dataCriacao"), inicio);
            return cb.between(root.get("dataCriacao"), inicio, fim);
        };
    }
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public interface UsuarioService {
    Page<UsuarioResponse> listar(String nome, String email, UUID realmId, String empresaId, String tenentId, 
                                   StatusUsuario status, List<UUID> roleIds, LocalDateTime dataCriacaoInicio, 
                                   LocalDateTime dataCriacaoFim, Pageable pageable);
}
```

---

## Exemplos de Testes

### Teste de Listagem Básica
```java
@Test
void dadoUsuariosExistentes_quandoListarSemFiltros_entaoRetornaPaginaOrdenada() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario1 = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario1.addRole(role);
    usuarioRepository.save(usuario1);
    
    Usuario usuario2 = new Usuario("Maria", "maria@example.com", "Senha@456", realm, StatusUsuario.ATIVO);
    usuario2.addRole(role);
    usuarioRepository.save(usuario2);
    
    Pageable pageable = PageRequest.of(0, 20, Sort.by("nome").ascending());
    ResponseEntity<Page<UsuarioResponse>> response = controller.listar(null, null, null, null, null, null, null, null, null, pageable);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("João");
}
```

### Teste de Filtro por Realm
```java
@Test
void dadoUsuariosDeRealmsDiferentes_quandoFiltrarPorRealm_entaoRetornaApenasCorrespondentes() {
    Realm realm1 = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Realm realm2 = realmRepository.save(new Realm("empresa-b", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm1));
    
    Usuario usuario1 = new Usuario("João", "joao@example.com", "Senha@123", realm1, StatusUsuario.ATIVO);
    usuario1.addRole(role);
    usuarioRepository.save(usuario1);
    
    Usuario usuario2 = new Usuario("Maria", "maria@example.com", "Senha@456", realm2, StatusUsuario.ATIVO);
    usuarioRepository.save(usuario2);
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<UsuarioResponse>> response = controller.listar(null, null, realm1.getId(), null, null, null, null, null, null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).realmId()).isEqualTo(realm1.getId());
}
```

### Teste de Filtro por Roles
```java
@Test
void dadoUsuariosComRolesDiferentes_quandoFiltrarPorRoles_entaoRetornaApenasCorrespondentes() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role roleAdmin = roleRepository.save(new Role("ADMIN", realm));
    Role roleUser = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario1 = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario1.addRole(roleAdmin);
    usuarioRepository.save(usuario1);
    
    Usuario usuario2 = new Usuario("Maria", "maria@example.com", "Senha@456", realm, StatusUsuario.ATIVO);
    usuario2.addRole(roleUser);
    usuarioRepository.save(usuario2);
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<UsuarioResponse>> response = controller.listar(null, null, null, null, null, null, List.of(roleAdmin.getId()), null, null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("João");
}
```

---

## Dependências

- História 2.1: Criar Usuário
- Epic 1: Gestão de Realms
- Epic 3: Gestão de Roles

---

## Pontos de Atenção

- Validação de tamanho máximo de página (100)
- JPA Specifications para filtros complexos
- `@Transactional(readOnly = true)` para métodos de leitura
- Controle de acesso por realm (se não for ADMIN global)
- Checkstyle: Seguir Google Java Style Guide
