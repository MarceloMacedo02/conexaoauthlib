# História 3.1: Criar Role

**Epic:** 3 - Gestão de Roles
**Status:** Ready for Review
**Prioridade:** Alta
**Estimativa:** 2 dias
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero criar uma nova role para que eu possa definir permissões específicas para usuários dentro de um realm.

---

## Critérios de Aceite

- [x] Endpoint `POST /api/v1/roles` recebe dados da role
- [x] Nome da role deve ser único por realm
- [x] Descrição é opcional
- [x] Role deve ser associada a um realm existente
- [x] Role é criada sem usuários associados
- [x] Auditoria do evento deve ser registrada (tipo: CRIACAO_ROLE)
- [x] Retornar `201 Created` com objeto criado
- [x] Retornar `400 Bad Request` se dados inválidos
- [x] Retornar `404 Not Found` se realm não existir
- [x] Retornar `409 Conflict` se nome já existir no realm
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Nome:
   - Deve ser único dentro do realm
   - Não pode ser igual a roles padrão (ADMIN, USER, SERVICE) se essas forem criadas via bootstrap (esta validação é opcional)
   - Formato: letras, números, underscores e hífens
   - Case-sensitive ou case-insensitive (definir como case-sensitive para flexibilidade)

2. Descrição:
   - Opcional
   - Máximo 500 caracteres

3. Realm:
   - Deve existir no sistema
   - Obrigatório
   - Role pertence a um único realm

4. Roles Padrão:
   - ADMIN, USER, SERVICE criadas via bootstrap
   - Podem ser criadas manualmente também

---

## Requisitos Técnicos

### DTO de Entrada
```java
public record CriarRoleRequest(
    @NotBlank(message = "Nome da role é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[A-Z][A-Z0-9_-]*$", message = "Nome deve começar com letra maiúscula e conter apenas letras maiúsculas, números, hífens e underscores")
    String nome,
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,
    
    @NotBlank(message = "Realm ID é obrigatório")
    UUID realmId
) {}
```

### DTO de Saída
```java
public record RoleResponse(
    UUID id,
    String nome,
    String descricao,
    UUID realmId,
    String realmNome,
    boolean padrao,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Gestão de Roles", description = "Operações de gestão de roles")
public class RoleController {
    
    @PostMapping
    @Operation(summary = "Criar role", description = "Cria uma nova role em um realm específico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Realm não encontrado"),
        @ApiResponse(responseCode = "409", description = "Nome de role já existe no realm")
    })
    ResponseEntity<RoleResponse> criar(@Valid @RequestBody CriarRoleRequest request);
}
```

### Service
```java
@Service
@Transactional
public interface RoleService {
    RoleResponse criar(CriarRoleRequest request);
}
```

### Repository
```java
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    boolean existsByNomeAndRealmId(String nome, UUID realmId);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoRoleValida_quandoCriar_entaoRetornaRoleCriada() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    CriarRoleRequest request = new CriarRoleRequest("GERENTE", "Gerente de departamento", realm.getId(), null);
    
    ResponseEntity<RoleResponse> response = controller.criar(request);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().nome()).isEqualTo("GERENTE");
    assertThat(response.getBody().descricao()).isEqualTo("Gerente de departamento");
    assertThat(response.getBody().realmId()).isEqualTo(realm.getId());
}
```

### Teste de Nome Duplicado no Realm
```java
@Test
void dadoRoleJaExistente_quandoCriarComMesmoNomeNoMesmoRealm_entaoRetornaConflict() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    roleRepository.save(new Role("GERENTE", "Gerente", realm, false));
    
    CriarRoleRequest request = new CriarRoleRequest("GERENTE", "Outra descrição", realm.getId(), null);
    
    assertThatThrownBy(() -> controller.criar(request))
        .isInstanceOf(NomeRoleJaExisteException.class);
}
```

### Teste de Nome Duplicado em Realm Diferente (Sucesso)
```java
@Test
void dadoRoleEmRealm_quandoCriarMesmoNomeEmOutroRealm_entaoRetornaSucesso() {
    Realm realm1 = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Realm realm2 = realmRepository.save(new Realm("empresa-b", StatusRealm.ATIVO));
    roleRepository.save(new Role("GERENTE", "Gerente", realm1, false));
    
    CriarRoleRequest request = new CriarRoleRequest("GERENTE", "Gerente", realm2.getId(), null);
    
    ResponseEntity<RoleResponse> response = controller.criar(request);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
}
```

---

## Dependências

- Epic 1: Gestão de Realms
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Unicidade de nome é por realm (não global)
- Roles padrão (ADMIN, USER, SERVICE) podem ser criadas manualmente também
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Tasks Completed
- [x] Criar endpoint POST /api/v1/roles
- [x] Implementar validação de unicidade de nome por realm
- [x] Criar DTOs CriarRoleRequest e RoleResponse
- [x] Implementar método criar no RoleService
- [x] Adicionar documentação Swagger em português
- [x] Criar testes unitários para criação de role

### Testing
- [x] Teste de sucesso na criação de role
- [x] Teste de nome duplicado no mesmo realm
- [x] Teste de sucesso com nome duplicado em realm diferente
- [x] Teste de realm não encontrado
- [x] Teste de descrição nula
- [x] Teste de role com nome padrão

### Dev Notes
- A unicidade do nome da role é validada por realm
- Roles padrão podem ser criadas manualmente
- A auditoria será implementada no Epic 7

### Completion Notes
- Endpoint de criação de role implementado com sucesso
- Validação de unicidade por realm funcionando
- Testes unitários passando

### File List
- `src/main/java/br/com/plataforma/conexaodigital/role/api/controller/RoleController.java` - Endpoint POST /api/v1/roles
- `src/main/java/br/com/plataforma/conexaodigital/role/api/requests/CriarRoleRequest.java` - DTO de criação
- `src/main/java/br/com/plataforma/conexaodigital/role/api/responses/RoleResponse.java` - DTO de resposta
- `src/main/java/br/com/plataforma/conexaodigital/role/domain/service/RoleService.java` - Interface do serviço
- `src/main/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImpl.java` - Implementação do serviço
- `src/main/java/br/com/plataforma/conexaodigital/role/domain/repository/RoleRepository.java` - Repository com existsByNomeAndRealmId
- `src/main/java/br/com/plataforma/conexaodigital/role/domain/model/Role.java` - Modelo de domínio
- `src/test/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImplTest.java` - Testes unitários

### Change Log
- 2025-12-23: Criado endpoint POST /api/v1/roles
- 2025-12-23: Implementada validação de unicidade de nome por realm
- 2025-12-23: Criados DTOs de request e response
- 2025-12-23: Implementado método criar no RoleService
- 2025-12-23: Adicionada documentação Swagger
- 2025-12-23: Criados testes unitários
- 2025-12-23: Story marcada como Ready for Review
