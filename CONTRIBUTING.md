# Contributing to ConexãoAuthLib

We welcome contributions to ConexãoAuthLib! This guide will help you get started with contributing to the project.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)
- [Release Process](#release-process)

## Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.9+
- Git
- IDE (IntelliJ IDEA or VS Code recommended)

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:

```bash
git clone https://github.com/your-username/conexaoautolib.git
cd conexaoautolib
```

3. Add upstream remote:

```bash
git remote add upstream https://github.com/original-org/conexaoautolib.git
```

## Development Setup

### 1. Build the Project

```bash
mvn clean install
```

### 2. IDE Configuration

#### IntelliJ IDEA

1. Open the project in IntelliJ
2. Enable annotation processing:
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"
3. Set up code style:
   - Import `checkstyle.xml` from project root
   - Set as project code style

#### VS Code

1. Install Java Extension Pack
2. Install Checkstyle for Java extension
3. Configure settings:

```json
{
  "java.checkstyle.configuration": "${workspaceFolder}/checkstyle.xml",
  "editor.formatOnSave": true
}
```

### 3. Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TokenClientTest

# Run tests with coverage
mvn clean test jacoco:report
```

### 4. Code Quality Checks

```bash
# Run all quality checks
mvn clean verify

# Individual checks
mvn checkstyle:check
mvn spotbugs:check
mvn pmd:check
```

## Code Style

### Java Style Guide

We follow the Google Java Style Guide with some customizations:

#### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `TokenClient`, `UsuarioResponse` |
| Methods | camelCase | `gerarToken()`, `filtrarUsuarios()` |
| Constants | UPPER_SNAKE_CASE | `DEFAULT_TIMEOUT`, `MAX_RETRY_ATTEMPTS` |
| Variables | camelCase | `userId`, `accessToken` |
| Packages | lowercase | `br.com.conexaoautolib.client` |

#### Code Organization

- Package-private for internal implementation
- Public for API
- Private for helper methods
- Static factory methods for builder patterns

#### Documentation

All public APIs must have Javadoc:

```java
/**
 * Generates a new OAuth2 token using client credentials.
 * 
 * @param clientId the OAuth2 client identifier
 * @param secret the OAuth2 client secret
 * @return the generated token response
 * @throws InvalidCredentialsException if authentication fails
 * @throws ConexaoAuthException if token generation fails
 */
public TokenResponse gerarToken(String clientId, String secret) {
    // implementation
}
```

### Commit Message Format

We use conventional commit messages:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

#### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

#### Examples

```
feat(client): add retry logic to OAuth2 client

Implement exponential backoff retry with configurable maximum attempts.
Fix connection timeout issues in high latency environments.

Closes #123
```

```
fix(auth): handle null token response gracefully

Prevent NullPointerException when ConexãoAuth server returns
null response body during token requests.

Fixes #45
```

## Testing

### Test Structure

Follow the standard Maven test structure:

```
src/
├── main/java/
│   └── br/com/conexaoautolib/
└── test/java/
    └── br/com/conexaoautolib/
        ├── client/
        ├── config/
        ├── facade/
        └── integration/
```

### Unit Tests

- Use JUnit 5
- Mock external dependencies with Mockito
- Follow AAA pattern (Arrange, Act, Assert)
- Test both happy path and error conditions

```java
@ExtendWith(MockitoExtension.class)
class TokenClientTest {
    
    @Mock
    private ConexaoAuthOAuth2Client oAuth2Client;
    
    @InjectMocks
    private TokenClient tokenClient;
    
    @Test
    void shouldGenerateTokenWhenCredentialsAreValid() {
        // Arrange
        String clientId = "test-client";
        String secret = "test-secret";
        TokenResponse expectedToken = TokenResponse.builder()
            .accessToken("access-token")
            .tokenType("Bearer")
            .expiresIn(3600)
            .build();
        
        when(oAuth2Client.emitirToken(any()))
            .thenReturn(expectedToken);
        
        // Act
        TokenResponse result = tokenClient.gerar()
            .clientId(clientId)
            .secret(secret)
            .execute();
        
        // Assert
        assertThat(result).isEqualTo(expectedToken);
        verify(oAuth2Client).emitirToken(tokenRequestCaptor.capture());
        
        TokenRequest captured = tokenRequestCaptor.getValue();
        assertThat(captured.getClientId()).isEqualTo(clientId);
        assertThat(captured.getClientSecret()).isEqualTo(secret);
    }
}
```

### Integration Tests

- Use Spring Boot Test
- Test against real (or WireMock) ConexãoAuth server
- Test complete workflows

```java
@SpringBootTest
@TestPropertySource(properties = {
    "conexaoauth.server.url=http://localhost:8089"
})
class TokenClientIntegrationTest {
    
    @Autowired
    private TokenClient tokenClient;
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8089))
        .build();
    
    @Test
    void shouldAuthenticateWithRealServer() {
        // Setup WireMock
        wireMock.stubFor(post(urlEqualTo("/oauth2/token"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "access_token": "test-token",
                        "token_type": "Bearer",
                        "expires_in": 3600
                    }
                    """)));
        
        // Test
        TokenResponse token = tokenClient.gerar()
            .clientId("test-client")
            .secret("test-secret")
            .execute();
        
        // Verify
        assertThat(token.getAccessToken()).isEqualTo("test-token");
        verify(postRequestedFor(urlEqualTo("/oauth2/token")));
    }
}
```

### Coverage Requirements

- Maintain >80% line coverage
- 100% coverage for critical paths (authentication, token validation)
- All public methods must have tests

## Pull Request Process

### 1. Create Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes

- Follow code style guidelines
- Add tests for new functionality
- Update documentation
- Ensure all tests pass

### 3. Run Quality Checks

```bash
mvn clean verify
```

Fix any issues before proceeding.

### 4. Commit Changes

```bash
git add .
git commit -m "feat(client): add new token refresh functionality"
```

### 5. Push and Create PR

```bash
git push origin feature/your-feature-name
```

Create pull request with:

- Clear title and description
- Reference any related issues
- Add screenshots if UI changes
- Link to documentation updates

### PR Checklist

Before submitting PR, ensure:

- [ ] Code follows style guidelines
- [ ] All tests pass (`mvn test`)
- [ ] Code quality checks pass (`mvn verify`)
- [ ] Coverage requirements met
- [ ] Documentation updated
- [ ] Commit messages follow conventional format
- [ ] PR description explains changes
- [ ] Tests added for new functionality

## Review Process

### Code Review

- All PRs require at least one approval
- Focus on code quality, security, and maintainability
- Address reviewer comments promptly
- Update PR based on feedback

### Merge Requirements

- All automated checks pass
- At least one code review approval
- No merge conflicts
- Documentation updated

## Release Process

Releases are managed by project maintainers:

### 1. Version Bump

Update version in `pom.xml`:

```xml
<version>1.1.0</version>
```

### 2. Update Changelog

Update `CHANGELOG.md` with:

- New features
- Bug fixes
- Breaking changes
- Migration guide if needed

### 3. Create Release

```bash
mvn clean deploy -P release
```

### 4. Tag Release

```bash
git tag -a v1.1.0 -m "Release version 1.1.0"
git push origin v1.1.0
```

### 5. Update Documentation

- Update API reference
- Update examples
- Publish to website

## Community Guidelines

### Code of Conduct

Be respectful and inclusive:

- Welcome newcomers
- Provide constructive feedback
- Respect different opinions
- Focus on technical discussions

### Getting Help

- Check existing documentation
- Search existing issues
- Create issue with clear description
- Join discussions for questions

### Reporting Security Issues

For security issues:

- Email: security@example.com
- Do not report in public issues
- Provide detailed vulnerability description
- Allow reasonable time for response

## Project Structure

```
conexaoautolib/
├── src/
│   ├── main/java/br/com/conexaoautolib/
│   │   ├── autoconfigure/
│   │   ├── client/
│   │   ├── config/
│   │   ├── exception/
│   │   ├── facade/
│   │   ├── health/
│   │   ├── model/
│   │   ├── storage/
│   │   ├── interceptor/
│   │   └── util/
│   └── test/java/br/com/conexaoautolib/
├── docs/
│   ├── api-reference.md
│   ├── development-guide.md
│   └── examples/
├── .github/workflows/
├── checkstyle.xml
├── pmd-rules.xml
├── spotbugs-include.xml
└── README.md
```

## Useful Commands

```bash
# Build and test
mvn clean verify

# Generate Javadoc
mvn javadoc:javadoc

# Check dependencies for vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Format code (if using formatter)
mvn com.coveo:fmt-maven-plugin:format

# Generate dependency tree
mvn dependency:tree

# Run specific test with debug
mvn test -Dtest=TokenClientTest -Dmaven.surefire.debug
```

Thank you for contributing to ConexãoAuthLib! 🎉