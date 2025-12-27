# História 9.3: Configuração de Validação Global

**Epic:** 9 - Configuração e Infraestrutura
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 1 dia  
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso configurar validação global com Jakarta Bean Validation para que eu possa validar DTOs de entrada de forma consistente.

---

## Critérios de Aceite

- [x] Dependência de `spring-boot-starter-validation`
- [x] Configuração de validador global
- [x] Validação automática em controllers com `@Valid`
- [x] Exceções de validação capturadas pelo `GlobalExceptionHandler`
- [x] Mensagens de erro em português
- [x] Uso de validações padrão: `@NotNull`, `@Size`, `@Email`, `@Pattern`

---

## Requisitos Técnicos

### pom.xml
```xml
<dependencies>
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### ValidationConfig
```java
@Configuration
public class ValidationConfig {
    
    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
    
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}
```

### Exemplo de DTO com Validações
```java
public record CriarRealmRequest(
    @NotBlank(message = "Nome do realm é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-z][a-z0-9_-]*$", message = "Nome deve começar com letra e conter apenas letras, números, hífens e underscores em minúsculas")
    String nome
) {}
```

### Controller com Validação
```java
@RestController
@RequestMapping("/api/v1/realms")
@RequiredArgsConstructor
public class RealmController {
    
    private final RealmService realmService;
    
    @PostMapping
    @Operation(summary = "Criar realm", description = "Cria um novo realm com status ATIVO por padrão")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Realm criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Nome de realm já existe")
    })
    ResponseEntity<RealmResponse> criar(@Valid @RequestBody CriarRealmRequest request) {
        RealmResponse response = realmService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### GlobalExceptionHandler (parte)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## Exemplos de Testes

### Teste de Validação
```java
@SpringBootTest
@AutoConfigureMockMvc
public class ValidationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    dadoDadosInvalidos_quandoCriarRealm_entaoRetornaErroValidacao() throws Exception {
        String requestJson = "{}";
        
        mockMvc.perform(post("/api/v1/realms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }
}
```

---

## Dependências

- História 9.1: Configuração de Banco de Dados

---

## Pontos de Atenção

- Mensagens de erro em português
- Validação em DTOs e Entidades
- Checkstyle: Seguir Google Java Style Guide
