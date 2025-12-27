# História 3.4: Listar Roles por Realm

**Epic:** 3 - Gestão de Roles
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 2 dias
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero listar roles filtrando por realm para que eu possa visualizar todas as roles disponíveis em um domínio lógico específico.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/roles` retorna lista paginada
- [x] Filtro por `realmId` (obrigatório ou opcional - definir como opcional)
- [x] Filtro por `nome` (busca parcial, case-insensitive)
- [x] Filtro por `padrao` (roles padrão: ADMIN, USER, SERVICE)
- [x] Suporta paginação via parâmetros `page`, `size`, `sort`
- [x] Retornar `200 OK` com página de resultados
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Paginação:
   - Padrão: `page=0`, `size=20`, `sort=nome,asc`
   - Tamanho máximo de página: 100

2. Filtros:
   - `realmId`: opcional (se não informado, lista de todos os realms)
   - `nome`: busca parcial, case-insensitive
   - `padrao`: filtra apenas roles padrão ou apenas não padrão

3. Ordenação:
   - Ordenação padrão: nome ascendente
   - Suporta múltiplos campos de ordenação

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Gestão de Roles", description = "Operações de gestão de roles")
public class RoleController {
    
    @GetMapping
    @Operation(summary = "Listar roles", description = "Lista roles com paginação e filtros opcionais")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de roles retornada com sucesso")
    })
    ResponseEntity<Page<RoleResponse>> listar(
        @RequestParam(required = false) UUID realmId,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) Boolean padrao,
        Pageable pageable
    );
    
    @GetMapping("/realms/{realmId}")
    @Operation(summary = "Listar roles por realm", description = "Lista todas as roles de um realm específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de roles do realm retornada com sucesso")
    })
    ResponseEntity<List<RoleResponse>> listarPorRealm(@PathVariable UUID realmId);
}
```

### JPA Specification
```java
public class RoleSpecification {
    public static Specification<Role> comRealmId(UUID realmId) {
        return (root, query, cb) -> 
            realmId == null ? null : cb.equal(root.get("realm").get("id"), realmId);
    }
    
    public static Specification<Role> comNome(String nome) {
        return (root, query, cb) -> 
            nome == null ? null : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
    
    public static Specification<Role> comPadrao(Boolean padrao) {
        return (root, query, cb) -> 
            padrao == null ? null : cb.equal(root.get("padrao"), padrao);
    }
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public interface RoleService {
    Page<RoleResponse> listar(UUID realmId, String nome, Boolean padrao, Pageable pageable);
    List<RoleResponse> listarPorRealm(UUID realmId);
}
```

---

## Exemplos de Testes

### Teste de Listagem Básica
```java
@Test
void dadoRolesExistentes_quandoListarSemFiltros_entaoRetornaPaginaOrdenada() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    roleRepository.save(new Role("ADMIN", "Administrador", realm, true));
    roleRepository.save(new Role("USER", "Usuário", realm, true));
    roleRepository.save(new Role("GERENTE", "Gerente", realm, false));
    
    Pageable pageable = PageRequest.of(0, 20, Sort.by("nome").ascending());
    ResponseEntity<Page<RoleResponse>> response = controller.listar(null, null, null, pageable);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getContent()).hasSize(3);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("ADMIN");
}
```

### Teste de Filtro por Realm
```java
@Test
void dadoRolesDeRealmsDiferentes_quandoFiltrarPorRealm_entaoRetornaApenasCorrespondentes() {
    Realm realm1 = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Realm realm2 = realmRepository.save(new Realm("empresa-b", StatusRealm.ATIVO));
    roleRepository.save(new Role("ADMIN", "Administrador", realm1, true));
    roleRepository.save(new Role("GERENTE", "Gerente", realm2, false));
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<RoleResponse>> response = controller.listar(realm1.getId(), null, null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("ADMIN");
}
```

### Teste de Listar por Realm (sem paginação)
```java
@Test
void dadoRolesDeRealm_quandoListarPorRealm_entaoRetornaTodas() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    roleRepository.save(new Role("ADMIN", "Administrador", realm, true));
    roleRepository.save(new Role("USER", "Usuário", realm, true));
    roleRepository.save(new Role("GERENTE", "Gerente", realm, false));
    
    ResponseEntity<List<RoleResponse>> response = controller.listarPorRealm(realm.getId());
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(3);
}
```

### Teste de Filtro por Nome
```java
@Test
void dadoRolesExistentes_quandoFiltrarPorNome_entaoRetornaApenasCorrespondentes() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    roleRepository.save(new Role("ADMIN", "Administrador", realm, true));
    roleRepository.save(new Role("USER", "Usuário", realm, true));
    roleRepository.save(new Role("ADMIN_FINANCEIRO", "Admin Financeiro", realm, false));
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<RoleResponse>> response = controller.listar(null, "ADMIN", null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(2);
}
```

---

## Dependências

- História 3.1: Criar Role
- Epic 1: Gestão de Realms

---

## Pontos de Atenção

- Validação de tamanho máximo de página (100)
- `@Transactional(readOnly = true)` para métodos de leitura
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- src/main/java/br/com/plataforma/conexaodigital/role/api/controller/RoleController.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/RoleService.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImpl.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/infrastructure/persistence/RoleSpecification.java (modificado)
- src/test/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImplTest.java (modificado)
- src/test/java/br/com/plataforma/conexaodigital/role/api/controller/RoleControllerIntegrationTest.java (modificado)

### Debug Log References
- RoleSpecification: Class specification criada para filtros dinâmicos
- RoleServiceImpl: Métodos listar() e listarPorRealm() implementados
- RoleController: Endpoints GET /api/v1/roles e GET /api/v1/roles/realms/{realmId} adicionados

### Completion Notes
1. **JPA Specifications**: Implementada classe RoleSpecification com métodos para filtrar por realmId, nome e padrao
2. **Método listar()**: Implementada listagem paginada com filtros opcionais via JPA Specifications
3. **Método listarPorRealm()**: Implementada listagem sem paginação para todas as roles de um realm
4. **Validação**: Validação de tamanho máximo de página (100) adicionada no Controller
5. **Testes de unidade**: 4 testes adicionados para os novos métodos
6. **Testes de integração**: Implementados testes MockMvc para os endpoints de listagem

### Change Log
| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| RoleController.java | Modified | Adicionados endpoints GET /api/v1/roles e GET /api/v1/roles/realms/{realmId} |
| RoleService.java | Modified | Adicionados métodos listar() e listarPorRealm() |
| RoleServiceImpl.java | Modified | Implementações dos métodos listar() e listarPorRealm() com Specifications |
| RoleSpecification.java | Created | Classe de especificações JPA para filtros dinâmicos |
| RoleServiceImplTest.java | Modified | Adicionados testes para os métodos de listagem |
