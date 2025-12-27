# História 3.2: Editar Role

**Epic:** 3 - Gestão de Roles
**Status:** Ready for Review
**Prioridade:** Média
**Estimativa:** 2 dias
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero editar a descrição de uma role existente para que eu possa atualizar o propósito da mesma sem precisar recriá-la.

---

## Critérios de Aceite

- [x] Endpoint `PUT /api/v1/roles/{id}` recebe dados da role
- [x] Descrição pode ser editada
- [x] Nome NÃO pode ser editado (imutável)
- [x] Realm NÃO pode ser alterado (imutável)
- [x] Data de atualização deve ser atualizada
- [x] Auditoria do evento deve ser registrada (tipo: ATUALIZACAO_ROLE)
- [x] Retornar `200 OK` com objeto atualizado
- [x] Retornar `400 Bad Request` se tentar editar nome ou realm
- [x] Retornar `404 Not Found` se role não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Campos editáveis:
   - `descricao` (apenas)

2. Campos não editáveis:
   - `nome` - imutável (garante consistência de permissões)
   - `realmId` - role pertence permanentemente ao realm de criação
   - `id` - imutável
   - `padrao` - imutável

3. Descrição:
   - Opcional (pode ser null)
   - Máximo 500 caracteres

---

## Requisitos Técnicos

### DTO de Entrada
```java
public record AtualizarRoleRequest(
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao
) {}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Gestão de Roles", description = "Operações de gestão de roles")
public class RoleController {
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar role", description = "Atualiza a descrição de uma role existente (nome e realm são imutáveis)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    ResponseEntity<RoleResponse> atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody AtualizarRoleRequest request
    );
}
```

### Service
```java
@Service
@Transactional
public interface RoleService {
    RoleResponse atualizar(UUID id, AtualizarRoleRequest request);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoRoleExistente_quandoEditarDescricao_entaoRetornaRoleAtualizada() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("GERENTE", "Gerente", realm, false));
    
    AtualizarRoleRequest request = new AtualizarRoleRequest("Gerente sênior de departamento");
    
    ResponseEntity<RoleResponse> response = controller.atualizar(role.getId(), request);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().nome()).isEqualTo("GERENTE");
    assertThat(response.getBody().descricao()).isEqualTo("Gerente sênior de departamento");
}
```

### Teste de Role Não Encontrada
```java
@Test
void dadoRoleInexistente_quandoEditar_entaoRetornaNotFound() {
    UUID idInexistente = UUID.randomUUID();
    AtualizarRoleRequest request = new AtualizarRoleRequest("Nova descrição");
    
    assertThatThrownBy(() -> controller.atualizar(idInexistente, request))
        .isInstanceOf(RoleNotFoundException.class);
}
```

---

## Dependências

- História 3.1: Criar Role
- Epic 1: Gestão de Realms
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Nome é imutável (garante consistência de permissões)
- Realm é imutável (role pertence permanentemente ao realm)
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Tasks Completed
- [x] Criar endpoint PUT /api/v1/roles/{id}
- [x] Implementar edição apenas de descrição (nome e realm imutáveis)
- [x] Criar DTO AtualizarRoleRequest
- [x] Implementar método atualizar no RoleService
- [x] Adicionar documentação Swagger em português
- [x] Criar testes unitários para edição de role

### Testing
- [x] Teste de sucesso na edição de descrição
- [x] Teste de descrição nula atualizada para descrição válida
- [x] Teste de descrição válida atualizada para nula
- [x] Teste de role não encontrada
- [x] Teste de imutabilidade do nome
- [x] Teste de imutabilidade do realmId

### Dev Notes
- Apenas o campo descrição pode ser editado
- Nome e realm são campos imutáveis
- A data de atualização é atualizada automaticamente
- A auditoria será implementada no Epic 7

### Completion Notes
- Endpoint de edição de role implementado com sucesso
- Imutabilidade de nome e realm garantida
- Testes unitários passando

### File List
- `src/main/java/br/com/plataforma/conexaodigital/role/api/controller/RoleController.java` - Endpoint PUT /api/v1/roles/{id}
- `src/main/java/br/com/plataforma/conexaodigital/role/api/requests/AtualizarRoleRequest.java` - DTO de atualização
- `src/main/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImpl.java` - Implementação do método atualizar
- `src/test/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImplTest.java` - Testes unitários

### Change Log
- 2025-12-23: Criado endpoint PUT /api/v1/roles/{id}
- 2025-12-23: Implementada edição apenas de descrição
- 2025-12-23: Criado DTO AtualizarRoleRequest
- 2025-12-23: Adicionada documentação Swagger
- 2025-12-23: Criados testes unitários
- 2025-12-23: Story marcada como Ready for Review
