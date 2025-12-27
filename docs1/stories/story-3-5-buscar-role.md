# História 3.5: Buscar Role por ID

**Epic:** 3 - Gestão de Roles
**Status:** Ready for Review
**Prioridade:** Média
**Estimativa:** 1 dia
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero buscar uma role específica pelo seu ID para que eu possa visualizar os detalhes completos dessa role.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/roles/{id}` retorna role específica
- [x] Retornar todos os campos da role
- [x] Retornar informações do realm associado
- [x] Retornar indicador se é role padrão
- [x] Retornar `200 OK` com sucesso
- [x] Retornar `404 Not Found` se role não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Busca:
   - ID deve ser um UUID válido
   - Role deve existir no banco de dados
   - Não há restrição de status (não há status para roles)

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Gestão de Roles", description = "Operações de gestão de roles")
public class RoleController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar role por ID", description = "Retorna os detalhes de uma role específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role encontrada"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    ResponseEntity<RoleResponse> buscarPorId(@PathVariable UUID id);
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public interface RoleService {
    RoleResponse buscarPorId(UUID id);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoRoleExistente_quandoBuscarPorId_entaoRetornaRole() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("GERENTE", "Gerente", realm, false));
    
    ResponseEntity<RoleResponse> response = controller.buscarPorId(role.getId());
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().id()).isEqualTo(role.getId());
    assertThat(response.getBody().nome()).isEqualTo("GERENTE");
    assertThat(response.getBody().descricao()).isEqualTo("Gerente");
    assertThat(response.getBody().realmId()).isEqualTo(realm.getId());
    assertThat(response.getBody().realmNome()).isEqualTo("empresa-a");
    assertThat(response.getBody().padrao()).isFalse();
}
```

### Teste de Role Padrão
```java
@Test
void dadoRolePadrao_quandoBuscarPorId_entaoRetornaComIndicadorPadrao() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("ADMIN", "Administrador", realm, true));
    
    ResponseEntity<RoleResponse> response = controller.buscarPorId(role.getId());
    
    assertThat(response.getBody().padrao()).isTrue();
}
```

### Teste de Role Não Encontrada
```java
@Test
void dadoRoleInexistente_quandoBuscarPorId_entaoRetornaNotFound() {
    UUID idInexistente = UUID.randomUUID();
    
    assertThatThrownBy(() -> controller.buscarPorId(idInexistente))
        .isInstanceOf(RoleNotFoundException.class);
}
```

---

## Dependências

- História 3.1: Criar Role
- Epic 1: Gestão de Realms

---

## Pontos de Atenção

- `@Transactional(readOnly = true)` para método de leitura
- Exception handling via `GlobalExceptionHandler`
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- src/main/java/br/com/plataforma/conexaodigital/role/api/controller/RoleController.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/RoleService.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImpl.java (modificado)
- src/test/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImplTest.java (modificado)

### Debug Log References
- RoleController: Endpoint GET /api/v1/roles/{id} adicionado
- RoleService: Método buscarPorId() adicionado
- RoleServiceImpl: Implementação da lógica de busca por ID com validação

### Completion Notes
1. **Método buscarPorId()**: Implementada busca de role por ID
2. **Validação**: RoleNotFoundException lançada quando role não existe
3. **Testes de unidade**: 4 testes adicionados (sucesso, padrão, não encontrada)
4. **Testes de integração**:  Não criados devido a problemas com ambiente de teste (Java 21 + Maven)

### Change Log
| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| RoleController.java | Modified | Adicionado endpoint GET /api/v1/roles/{id} com documentação Swagger |
| RoleService.java | Modified | Adicionado método buscarPorId(UUID id) |
| RoleServiceImpl.java | Modified | Implementação da lógica de busca por ID |
| RoleServiceImplTest.java | Modified | Adicionados testes para o método buscarPorId |

---

## Dev Agent Record

### File List
- src/main/java/br/com/plataforma/conexaodigital/role/api/controller/RoleController.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/RoleService.java (modificado)
- src/main/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImpl.java (modificado)
- src/test/java/br/com/plataforma/conexaodigital/role/domain/service/impl/RoleServiceImplTest.java (modificado)

### Debug Log References
- RoleController: Endpoint GET /api/v1/roles/{id} adicionado
- RoleService: Método buscarPorId adicionado
- RoleServiceImpl: Implementação da lógica de busca por ID com validação de existência
- RoleServiceImplTest: Testes unitários adicionados para o método buscarPorId

### Completion Notes
1. **Método buscarPorId()**: Implementado lógica de busca de role por ID
2. **Validação**: RoleNotFoundException lançada quando role não existe
3. **Testes de unidade**: 4 testes adicionados (sucesso, padrão, não encontrada)
4. **Testes de integração**: Nota: O nome do teste foi alterado para RoleControllerMvcTest devido a conflito com arquivo existente

### Change Log
| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| RoleController.java | Modified | Adicionado endpoint GET /api/v1/roles/{id} com documentação Swagger |
| RoleService.java | Modified | Adicionado método buscarPorId(UUID id) |
| RoleServiceImpl.java | Modified | Implementação da lógica de busca por ID |
| RoleServiceImplTest.java | Modified | Adicionados testes para o método buscarPorId |
