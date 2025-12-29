# ConexãoAuthLib Coding Standards

## General Guidelines
- **Language**: Java 21 LTS with modern features
- **Style**: Google Java Style Guide enforced via Checkstyle
- **Build Tool**: Maven with Checkstyle, SpotBugs, PMD plugins
- **Editor**: EditorConfig for consistent formatting

## Naming Conventions
| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | TokenClient, ConexaoAuthProperties |
| Methods | camelCase | gerar(), filtrar(), execute() |
| Constants | UPPER_SNAKE_CASE | DEFAULT_TIMEOUT, MAX_RETRY_ATTEMPTS |
| Packages | lowercase | br.com.conexaoautolib.client |
| Variables | camelCase | userId, tokenResponse |

## Critical Rules (from Architecture.md)
1. **Never log credentials or tokens**: Use redaction in all logging statements
2. **All public APIs must use fluent pattern**: Method chaining with builder pattern
3. **Feign clients must use configuration class**: Centralized timeout and retry settings
4. **Token storage must be thread-safe**: Use concurrent collections for in-memory implementation
5. **All DTOs must be immutable**: Use @Value or Lombok's @Value with @Builder
6. **External configuration validation**: Use @Validated on properties classes

## Lombok Usage
- **DTOs**: @Data, @Builder, @AllArgsConstructor, @NoArgsConstructor
- **Immutable Objects**: @Value, @Builder
- **Dependency Injection**: @RequiredArgsConstructor
- **Logging**: @Slf4j

## Validation
- **Input Validation**: Jakarta Validation 3.x annotations (@NotBlank, @NotNull, etc.)
- **Custom Messages**: ValidationMessages.properties for Portuguese messages
- **Validation Location**: API boundary (DTOs and request objects)

## Exception Handling
- **Base Exception**: ConexaoAuthException extending RuntimeException
- **Specific Exceptions**: Domain-specific subclasses (InvalidCredentialsException, etc.)
- **Error Mapping**: HTTP status codes to domain exceptions via ErrorDecoder
- **Serialization**: Exceptions must be serializable

## Testing Standards
- **Framework**: JUnit 5.10+, Mockito 5.x
- **Pattern**: AAA (Arrange, Act, Assert)
- **Coverage**: >80% for all public methods
- **Test Data**: Factory pattern (TokenRequestFactory, TokenResponseFactory)
- **Mocking**: Mock all external dependencies (Feign clients, storage)

## Security Requirements
- **No Hardcoded Secrets**: Access via configuration only
- **Token Redaction**: Never log tokens or credentials
- **HTTPS Required**: Production environments must use HTTPS/TLS 1.3

## Documentation
- **JavaDoc**: Required for all public APIs
- **Comments**: Portuguese for business logic, English for technical
- **README**: Comprehensive installation and usage examples