# História 1.5: Listar Realms com Paginação e Filtros

**Epic:** 1 - Gestão de Realms  
**Status:** Concluído  
**Prioridade:** Alta  
**Estimativa:** 3 dias  
**Complexidade:** Média

---

## Descrição

Como administrador do sistema, quero listar realms com paginação e filtros para que eu possa navegar e encontrar realms específicos de forma eficiente.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/realms` retorna lista paginada
- [x] Suporta paginação via parâmetros `page`, `size`, `sort`
- [x] Filtro por `nome` (busca parcial, case-insensitive)
- [x] Filtro por `status` (ATIVO ou INATIVO)
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
   - Filtro de nome usa `LIKE` (busca parcial)
   - Filtro de período usa intervalo fechado [inicio, fim]

3. Ordenação:
   - Ordenação padrão: nome ascendente
   - Suporta múltiplos campos de ordenação

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/realms")
@Tag(name = "Gestão de Realms", description = "Operações de gestão de realms")
public class RealmController {
    
    @GetMapping
    @Operation(summary = "Listar realms", description = "Lista realms com paginação e filtros opcionais")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de realms retornada com sucesso")
    })
    ResponseEntity<Page<RealmResponse>> listar(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) StatusRealm status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacaoInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacaoFim,
        Pageable pageable
    );
}
```

### JPA Specification
```java
public class RealmSpecification {
    public static Specification<Realm> comNome(String nome) {
        return (root, query, cb) -> 
            nome == null ? null : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
    
    public static Specification<Realm> comStatus(StatusRealm status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
    
    public static Specification<Realm> comDataCriacaoEntre(LocalDateTime inicio, LocalDateTime fim) {
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
public interface RealmService {
    Page<RealmResponse> listar(String nome, StatusRealm status, LocalDateTime dataCriacaoInicio, LocalDateTime dataCriacaoFim, Pageable pageable);
}
```

---

## Dev Agent Record

**Agent Model Used:** dev  
**Implementation Date:** 2025-12-23  

### File List
- **New Files:**
  - `src/main/java/br/com/plataforma/conexaodigital/realm/infrastructure/persistence/RealmSpecification.java`
- **Modified Files:**
  - `src/main/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmService.java`
  - `src/main/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmServiceImpl.java`
  - `src/main/java/br/com/plataforma/conexaodigital/realm/api/controller/RealmController.java`
  - `src/test/java/br/com/plataforma/conexaodigital/realm/domain/service/RealmServiceImplTest.java`
  - `src/test/java/br/com/plataforma/conexaodigital/realm/api/controller/RealmControllerIntegrationTest.java`
- **Documentation:**
  - `docs/stories/story-1-5-listar-realms.md`

### Change Log
- Added JPA Specification class for dynamic filtering
- Implemented listar method in service layer with read-only transaction
- Added GET endpoint with parameter validation and OpenAPI documentation
- Comprehensive test coverage with 5 unit tests and 6 integration tests
- All acceptance criteria validated and passing

### Completion Notes
- Implementation follows Clean Architecture principles
- Uses Spring Data JPA Specifications for flexible filtering
- Proper pagination with Spring Data Pageable
- Case-insensitive LIKE search for name filter
- Date range filtering with LocalDateTime
- Full test coverage including edge cases
- No regressions in existing functionality

---

## Exemplos de Testes

### Teste de Listagem Básica
```java
@Test
void dadoRealmsExistentes_quandoListarSemFiltros_entaoRetornaPaginaOrdenada() {
    realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    realmRepository.save(new Realm("empresa-b", StatusRealm.ATIVO));
    
    Pageable pageable = PageRequest.of(0, 20, Sort.by("nome").ascending());
    ResponseEntity<Page<RealmResponse>> response = controller.listar(null, null, null, null, pageable);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("empresa-a");
}
```

### Teste de Filtro por Nome
```java
@Test
void dadoRealmsExistentes_quandoFiltrarPorNome_entaoRetornaApenasCorrespondentes() {
    realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    realmRepository.save(new Realm("empresa-b", StatusRealm.ATIVO));
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<RealmResponse>> response = controller.listar("empresa-a", null, null, null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).nome()).isEqualTo("empresa-a");
}
```

### Teste de Filtro por Status
```java
@Test
void dadoRealmsComStatusDiferentes_quandoFiltrarPorStatus_entaoRetornaApenasCorrespondentes() {
    realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    realmRepository.save(new Realm("empresa-b", StatusRealm.INATIVO));
    
    Pageable pageable = PageRequest.of(0, 20);
    ResponseEntity<Page<RealmResponse>> response = controller.listar(null, StatusRealm.ATIVO, null, null, pageable);
    
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).status()).isEqualTo(StatusRealm.ATIVO);
}
```

---

## Dependências

- História 1.1: Criar Realm
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Validação de tamanho máximo de página (100)
- JPA Specifications para filtros complexos
- `@Transactional(readOnly = true)` para métodos de leitura
- Checkstyle: Seguir Google Java Style Guide

---

## QA Results

### Review Date: 2025-12-23

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

The implementation follows Clean Architecture principles with proper separation of concerns. JPA Specifications are used effectively for flexible filtering, and the read-only transaction is correctly applied to the listing method. Code is well-documented with Javadoc and follows Spring Boot best practices.

### Refactoring Performed

None required - implementation is already well-structured.

### Compliance Check

- Coding Standards: ✓ Follows Spring Boot and Java conventions
- Project Structure: ✓ Clean Architecture maintained with proper layering
- Testing Strategy: ✓ Comprehensive unit and integration tests covering all scenarios
- All ACs Met: ✓ All 8 acceptance criteria fully implemented and tested

### Improvements Checklist

- [x] Comprehensive test coverage with edge cases
- [x] Proper error handling and validation
- [x] Clean Architecture adherence
- [x] OpenAPI documentation in Portuguese
- [x] Case-insensitive LIKE search implementation
- [x] Date range filtering with proper null handling

### Security Review

No security concerns identified. Realm listing is a basic read operation with no sensitive data exposure risks. Input validation is properly handled through Spring's parameter binding and JPA Specifications.

### Performance Considerations

Implementation uses pagination effectively to prevent large result sets. JPA Specifications allow for efficient database-level filtering. Read-only transaction optimizes performance for list operations.

### Files Modified During Review

None

### Gate Status

Gate: PASS → docs/qa/gates/1.5-listar-realms.yml
Risk profile: docs/qa/assessments/1.5-risk-20251223.md
NFR assessment: docs/qa/assessments/1.5-nfr-20251223.md

### Recommended Status

✓ Ready for Done
