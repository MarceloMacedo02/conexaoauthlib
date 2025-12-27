# História 9.5: Configuração de Tratamento Global de Exceções

**Epic:** 9 - Configuração e Infraestrutura
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Média

---

## Descrição

Como sistema, preciso configurar tratamento global de exceções para que eu possa retornar respostas de erro consistentes e amigáveis ao cliente.

---

## Critérios de Aceite

- [ ] Classe `GlobalExceptionHandler` com `@RestControllerAdvice`
- [ ] Tratamento de `MethodArgumentNotValidException` (validação)
- [ ] Tratamento de `EntityNotFoundException` (entidade não encontrada)
- [ ] Tratamento de `BusinessException` (exceções de negócio)
- [ ] Tratamento de `AuthenticationException` (autenticação)
- [ ] Tratamento de `AccessDeniedException` (autorização)
- [ ] Tratamento de exceções genéricas
- [ ] Resposta padronizada com código HTTP e mensagem em português

---

## Requisitos Técnicos

### ErrorResponse
```java
public record ErrorResponse(
    int status,
    String mensagem,
    List<String> erros,
    LocalDateTime timestamp,
    String caminho
) {}
```

### Exceções de Domínio
```java
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String id, String entidade) {
        super(String.format("%s não encontrado com id: %s", entidade, id));
    }
}

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
```

### GlobalExceptionHandler
```java
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final HttpServletRequest request;
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            List.of(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            List.of(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de validação",
            erros,
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Autenticação falhou: " + ex.getMessage(),
            List.of(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Acesso negado: " + ex.getMessage(),
            List.of(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            List.of(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## Exemplos de Testes

### Teste de Tratamento de Exceção
```java
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    dadoEntidadeNaoEncontrada_quandoBuscar_entaoRetornaNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/realms/" + UUID.randomUUID()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.mensagem").exists());
    }
}
```

---

## Dependências

- História 9.3: Configuração de Validação Global

---

## Pontos de Atenção

- Mensagens em português
- Nunca expor stack trace ao cliente
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Tasks Completed

- [x] Classe `GlobalExceptionHandler` com `@RestControllerAdvice`
- [x] Tratamento de `MethodArgumentNotValidException` (validação)
- [x] Tratamento de `EntityNotFoundException` (entidade não encontrada)
- [x] Tratamento de `BusinessException` (exceções de negócio)
- [x] Tratamento de `AuthenticationException` (autenticação)
- [x] Tratamento de `AccessDeniedException` (autorização)
- [x] Tratamento de exceções genéricas
- [x] Resposta padronizada com código HTTP e mensagem em português

### Files Modified

- `src/main/java/br/com/plataforma/conexaodigital/shared/exceptions/GlobalExceptionHandler.java` - Added HttpServletRequest injection, new exception handlers, updated ErrorResponse with path field
- `src/main/java/br/com/plataforma/conexaodigital/shared/exceptions/BusinessException.java` - Already exists

### Files Created

- `src/main/java/br/com/plataforma/conexaodigital/shared/exceptions/EntityNotFoundException.java` - EntityNotFoundException for common not found scenarios
- `src/test/java/br/com/plataforma/conexaodigital/shared/exceptions/GlobalExceptionHandlerTest.java` - Unit tests for exception handlers

### Tests Implemented

- GlobalExceptionHandlerTest.java with 4 tests covering:
  - BusinessException handling
  - EntityNotFoundException handling
  - AccessDeniedException handling
  - Generic exception handling

### Debug Log References

- All tests passing

### Completion Notes

- GlobalExceptionHandler successfully enhanced with:
  - HttpServletRequest injection to include request path in error responses
  - AuthenticationException handler (401)
  - AccessDeniedException handler (403)
  - Updated ErrorResponse to include path field
  - EntityNotFoundException created for common use
- All exception handlers now include request path in responses
- Messages in Portuguese as required
- Stack traces never exposed to client

### Change Log

- 2025-12-24: Enhanced global exception handling with HttpServletRequest injection, new handlers for AuthenticationException and AccessDeniedException, updated ErrorResponse with path field
