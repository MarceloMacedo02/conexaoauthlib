# História 1.2: Editar Realm

**Epic:** 1 - Gestão de Realms
**Status:** Concluído
**Prioridade:** Média
**Estimativa:** 2 dias
**Complexidade:** Baixa

---

## Descrição

Como administrador do sistema, quero editar o nome de um realm existente para que eu possa ajustar a identificação do domínio lógico conforme necessário.

---

## Critérios de Aceite

- [x] Endpoint `PUT /api/v1/realms/{id}` recebe dados do realm
- [x] Nome pode ser editado (mantendo regras de validação)
- [x] Status pode ser editado
- [x] Data de atualização deve ser atualizada automaticamente
- [x] Auditoria do evento deve ser registrada (tipo: ATUALIZACAO_REALM)
- [x] Retornar `200 OK` com objeto atualizado
- [x] Retornar `400 Bad Request` se dados inválidos
- [x] Retornar `404 Not Found` se realm não existir
- [x] Retornar `409 Conflict` se novo nome já existir em outro realm
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Nome do realm:
   - Mesmas regras de validação da criação
   - Não pode ser igual a nome de outro realm existente

2. Status:
   - Pode ser alternado entre `ATIVO` e `INATIVO`
   - Realm Master não pode ser desativado (validação em epic 8)

3. Campos editáveis:
   - `nome`
   - `status`
   - `id` não pode ser alterado

---

## Requisitos Técnicos

### DTO de Entrada
```java
public record AtualizarRealmRequest(
    @NotBlank(message = "Nome do realm é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-z][a-z0-9_-]*$", message = "Nome deve começar com letra e conter apenas letras, números, hífens e underscores em minúsculas")
    String nome,

    @NotNull(message = "Status é obrigatório")
    StatusRealm status
) {}
```

### DTO de Saída
```java
public record RealmResponse(
    UUID id,
    String nome,
    StatusRealm status,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/realms")
@Tag(name = "Gestão de Realms", description = "Operações de gestão de realms")
public class RealmController {

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar realm", description = "Atualiza nome e status de um realm existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Realm atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Realm não encontrado"),
        @ApiResponse(responseCode = "409", description = "Nome de realm já existe")
    })
    ResponseEntity<RealmResponse> atualizar(
        @PathVariable UUID id,
        @Valid @RequestBody AtualizarRealmRequest request
    );
}
```

### Service
```java
@Service
@Transactional
public interface RealmService {
    RealmResponse atualizar(UUID id, AtualizarRealmRequest request);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoRealmExistente_quandoEditarNome_entaoRetornaRealmAtualizado() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    AtualizarRealmRequest request = new AtualizarRealmRequest("empresa-b", StatusRealm.ATIVO);

    ResponseEntity<RealmResponse> response = controller.atualizar(realm.getId(), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().nome()).isEqualTo("empresa-b");
    assertThat(response.getBody().dataAtualizacao()).isAfter(realm.getDataCriacao());
}
```

### Teste de Realm Não Encontrado
```java
@Test
void dadoRealmInexistente_quandoEditar_entaoRetornaNotFound() {
    UUID idInexistente = UUID.randomUUID();
    AtualizarRealmRequest request = new AtualizarRealmRequest("empresa-x", StatusRealm.ATIVO);

    assertThatThrownBy(() -> controller.atualizar(idInexistente, request))
        .isInstanceOf(RealmNotFoundException.class);
}
```

---

## Dependências

- História 1.1: Criar Realm
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Validação de unicidade deve excluir o próprio realm sendo editado
- JPA Auditing deve atualizar `dataAtualizacao` automaticamente
- Auditoria deve registrar qual campo foi alterado
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Tasks Completed

- [x] Create RealmNotFoundException in domain.exceptions
- [x] Create AtualizarRealmRequest record in api.requests
- [x] Add atualizar method to RealmService interface
- [x] Implement atualizar method in RealmServiceImpl
- [x] Add PUT endpoint to RealmController
- [x] Update GlobalExceptionHandler for RealmNotFoundException
- [x] Write unit tests for atualizar method
- [x] Write integration tests for PUT endpoint
- [x] Run all tests and validations

### Completion Notes

1. **Exception Handling**:
   - Created `RealmNotFoundException` extending `BusinessException`
   - Added handler in `GlobalExceptionHandler` for 404 responses

2. **Request DTO**:
   - `AtualizarRealmRequest` record with `nome` and `status` fields
   - Full Jakarta Bean Validation annotations
   - Portuguese validation messages
   - OpenAPI documentation

3. **Service Layer Updates**:
   - Added `atualizar(UUID id, AtualizarRealmRequest request)` method to `RealmService` interface
   - Implemented in `RealmServiceImpl` with:
     - Validates realm existence
     - Checks for duplicate names (excluding current realm)
     - Updates name and status
     - Normalizes name to lowercase
     - Uses `markAsUpdated()` for timestamp updates

4. **Controller Updates**:
   - Added `PUT /api/v1/realms/{id}` endpoint
   - Path variable for UUID id
   - Full OpenAPI documentation in Portuguese
   - Proper response codes (200, 400, 404, 409)

5. **Testing**:
   - **Unit Tests (RealmServiceImplTest - AtualizarTests)**: 5 tests
     - Update name successfully
     - Update status successfully
     - Update same name without error
     - Realm not found exception
     - Duplicate name in other realm exception
   - **Controller Tests (RealmControllerIntegrationTest - AtualizarRealmTests)**: 6 tests
     - Successful update returns 200
     - Not found returns 404
     - Duplicate name returns 409
     - Status change validation
     - Empty name validation
     - Missing status validation
   - All 31 tests passing (existing criar tests + new atualizar tests)

6. **Key Implementation Details**:
   - Uniqueness validation excludes the current realm being edited
   - Case-insensitive comparison for duplicate names
   - Timestamp automatically updated via `markAsUpdated()` method
   - ID field is immutable (cannot be changed)

### File List

#### Modified Source Files:
- `src/main/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmService.java` (added atualizar method)
- `src/main/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmServiceImpl.java` (added atualizar implementation)
- `src/main/java/br/com/plataforma/conexaodigital/realm/api/controller/RealmController.java` (added PUT endpoint)
- `src/main/java/br/com/plataforma/conexaodigital/shared/exceptions/GlobalExceptionHandler.java` (added RealmNotFoundException handler)

#### New Source Files:
- `src/main/java/br/com/plataforma/conexaodigital/realm/domain/exceptions/RealmNotFoundException.java`
- `src/main/java/br/com/plataforma/conexaodigital/realm/api/requests/AtualizarRealmRequest.java`

#### Modified Test Files:
- `src/test/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmServiceImplTest.java` (added AtualizarTests with 5 tests)
- `src/test/java/br/com/plataforma/conexaodigital/realm/api/controller/RealmControllerIntegrationTest.java` (added AtualizarRealmTests with 6 tests)

### Change Log

**2025-12-23**: Implementation of Story 1.2 - Editar Realm
- Created RealmNotFoundException for proper error handling
- Created AtualizarRealmRequest DTO with validation
- Updated RealmService interface with atualizar method
- Implemented atualizar logic with uniqueness validation
- Added PUT endpoint to RealmController with full documentation
- Updated GlobalExceptionHandler for 404 responses
- Wrote comprehensive unit tests (5 new tests)
- Wrote comprehensive controller tests (6 new tests)
- All 31 tests passing successfully

### Agent Model Used

Anthropic Claude 3.5 Sonnet

### Debug Log References

No critical errors encountered during implementation. All tests passed successfully.

**Notes:**
- Used `when().thenAnswer()` pattern in tests to handle the `markAsUpdated()` method call
- Ensured duplicate name check excludes the current realm being edited
- Both unit tests (service layer) and integration tests (controller layer) written
