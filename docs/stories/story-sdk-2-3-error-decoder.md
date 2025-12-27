# Story SDK-2.3: Error Decoder Customizado

**Epic:** SDK-2 - Feign Client e Error Decoder
**Story:** SDK-2.3
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.25 dia
**Complexidade:** Média

---

## Descrição

Implementar o `ConexaoAuthErrorDecoder` para traduzir códigos de erro HTTP em exceções significativas da SDK. Esta story expande o stub criado na Story SDK-1.1 e implementa a hierarquia completa de exceções.

---

## Critérios de Aceite

- [ ] Classe `ConexaoAuthErrorDecoder` implementada corretamente
- [ ] Implementa interface `feign.codec.ErrorDecoder`
- [ ] Traduz códigos HTTP (401, 403, 404, 409, 500+) para exceções da SDK
- [ ] Exceções criadas: `UnauthorizedException`, `ForbiddenException`, `ResourceNotFoundException`, `ConflictException`, `ServerException`
- [ ] Mensagens de erro em Português
- [ ] Stack trace preservada para debugging
- [ ] Javadoc completo em Português
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Tradução de Erros:** Códigos HTTP devem ser traduzidos para exceções de domínio significativas
2. **Mensagens em Português:** Mensagens de erro devem ser claras e em Português
3. **Preservação de Stack Trace:** Stack trace original deve ser preservada para debugging
4. **Hierarquia de Exceções:** Todas as exceções estendem `ConexaoAuthException`

---

## Requisitos Técnicos

### Hierarquia de Exceções

```
RuntimeException
    └─ ConexaoAuthException (base)
        ├─ UnauthorizedException (401)
        ├─ ForbiddenException (403)
        ├─ ResourceNotFoundException (404)
        ├─ ConflictException (409)
        ├─ InvalidTokenException (token inválido/expirado)
        └─ ServerException (500+)
```

### Classe ConexaoAuthException (Base)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/ConexaoAuthException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção base para erros do Conexão Auth SDK.
 *
 * <p>Todas as exceções específicas do SDK devem estender esta classe.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConexaoAuthException extends RuntimeException {

    /**
     * Código de status HTTP associado à exceção.
     *
     * <p>Este código ajuda na identificação do tipo de erro e na
     * criação de respostas HTTP apropriadas na aplicação consumidora.
     */
    private final int statusCode;

    /**
     * Construtor com mensagem e código de status HTTP.
     *
     * @param message Mensagem de erro em Português
     * @param statusCode Código de status HTTP
     */
    public ConexaoAuthException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Construtor com mensagem, causa e código de status HTTP.
     *
     * <p>Este construtor é útil para wrapping de exceções de lower level
     * (ex: IOException, SSLException, etc.) em exceções de domínio.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     * @param statusCode Código de status HTTP
     */
    public ConexaoAuthException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Retorna o código de status HTTP associado à exceção.
     *
     * @return Código de status HTTP
     */
    public int getStatusCode() {
        return statusCode;
    }
}
```

### Classe UnauthorizedException (401)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/UnauthorizedException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando a requisição não está autorizada (401 Unauthorized).
 *
 * <p><b>Causas Comuns:</b>
 * <ul>
 *   <li>Client ID ou Client Secret inválidos</li>
 *   <li>Token JWT expirado ou inválido</li>
 *   <li>Credenciais de usuário incorretas</li>
 *   <li>Header Authorization ausente ou malformado</li>
 * </ul>
 *
 * <p><b>Ação Recomendada:</b>
 * Verifique as credenciais configuradas (conexao.auth.client-id e conexao.auth.client-secret)
 * e certifique-se de que o token JWT é válido e não expirou.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class UnauthorizedException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, 401);
    }
}
```

### Classe ForbiddenException (403)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/ForbiddenException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando o acesso é proibido (403 Forbidden).
 *
 * <p><b>Causas Comuns:</b>
 * <ul>
 *   <li>Usuário autenticado mas sem permissão para acessar o recurso</li>
 *   <li>Client não tem autorização para a operação solicitada</li>
 *   <li>Realm ou role não permite a operação</li>
 * </ul>
 *
 * <p><b>Ação Recomendada:</b>
 * Verifique se o usuário/cliente tem as permissões necessárias para a operação.
 * Consulte o método {@code validatePermissions()} do {@code ConexaoAuthService}.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ForbiddenException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ForbiddenException(String message) {
        super(message, 403);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, 403);
    }
}
```

### Classe ResourceNotFoundException (404)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/ResourceNotFoundException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um recurso não é encontrado (404 Not Found).
 *
 * <p><b>Causas Comuns:</b>
 * <ul>
 *   <li>Usuário não encontrado pelo CPF fornecido</li>
 *   <li>Realm não encontrado</li>
 *   <li>Role não encontrada</li>
 *   <li>Endpoint inexistente</li>
 * </ul>
 *
 * <p><b>Ação Recomendada:</b>
 * Verifique se o identificador fornecido (CPF, ID do realm, etc.) está correto
 * e se o recurso existe no Auth Server.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, 404);
    }
}
```

### Classe ConflictException (409)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/ConflictException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há conflito de dados (409 Conflict).
 *
 * <p><b>Causas Comuns:</b>
 * <ul>
 *   <li>Email já cadastrado no sistema</li>
 *   <li>CPF já cadastrado no sistema</li>
 *   <li>Username já existe</li>
 *   <li>Versão de resource desatualizada (concurrency conflict)</li>
 * </ul>
 *
 * <p><b>Ação Recomendada:</b>
 * Verifique se o email/CPF já está cadastrado. Se for um caso de atualização,
 * verifique se a versão do resource está atualizada.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConflictException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ConflictException(String message) {
        super(message, 409);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause, 409);
    }
}
```

### Classe ServerException (500+)

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/exception/ServerException.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há erro interno no servidor (500+ Server Error).
 *
 * <p><b>Causas Comuns:</b>
 * <ul>
 *   <li>Erro interno no Auth Server (500)</li>
 *   <li>Banco de dados indisponível (503)</li>
 *   <li>Timeout de comunicação (504)</li>
 *   <li>Erro de configuração do servidor</li>
 * </ul>
 *
 * <p><b>Ação Recomendada:</b>
 * Verifique os logs do Auth Server e a conectividade de rede.
 * Se o erro persistir, entre em contato com o suporte.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ServerException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro e código de status HTTP.
     *
     * @param message Mensagem de erro em Português
     * @param statusCode Código de status HTTP (500+)
     */
    public ServerException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * Construtor com mensagem, causa e código de status HTTP.
     *
     * @param message Mensagem de erro em Português
     * @param cause Causa raiz do erro
     * @param statusCode Código de status HTTP (500+)
     */
    public ServerException(String message, Throwable cause, int statusCode) {
        super(message, cause, statusCode);
    }
}
```

### Classe ConexaoAuthErrorDecoder

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/decoder/ConexaoAuthErrorDecoder.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.decoder;

import com.plataforma.conexao.auth.starter.exception.*;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Decoder de erros customizado para Feign Client.
 *
 * <p>Esta classe traduz códigos de erro HTTP em exceções significativas da SDK,
 * permitindo que a aplicação consumidora trate erros de forma granular.
 *
 * <p><b>Mapping de Códigos HTTP para Exceções:</b>
 * <table>
 *   <tr><th>Código HTTP</th><th>Exceção</th><th>Descrição</th></tr>
 *   <tr><td>401</td><td>UnauthorizedException</td><td>Credenciais inválidas ou token expirado</td></tr>
 *   <tr><td>403</td><td>ForbiddenException</td><td>Acesso proibido (sem permissão)</td></tr>
 *   <tr><td>404</td><td>ResourceNotFoundException</td><td>Recurso não encontrado</td></tr>
 *   <tr><td>409</td><td>ConflictException</td><td>Conflito de dados (email/CPF duplicado)</td></tr>
 *   <tr><td>500+</td><td>ServerException</td><td>Erro interno do servidor</td></tr>
 * </table>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * try {
 *     UserResponse user = conexaoAuthService.findUserByCpf("12345678901");
 * } catch (UnauthorizedException e) {
 *     // Tratar credenciais inválidas
 *     log.error("Credenciais inválidas: {}", e.getMessage());
 * } catch (ResourceNotFoundException e) {
 *     // Tratar usuário não encontrado
 *     log.error("Usuário não encontrado: {}", e.getMessage());
 * } catch (ServerException e) {
 *     // Tratar erro de servidor
 *     log.error("Erro do Auth Server: {} (HTTP {})", e.getMessage(), e.getStatusCode());
 * }
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Slf4j
public class ConexaoAuthErrorDecoder implements ErrorDecoder {

    /**
     * Traduz resposta de erro HTTP em exceção da SDK.
     *
     * <p>Este método é chamado pelo Feign sempre que uma requisição retorna
     * um código de status HTTP indicando erro (4xx ou 5xx).
     *
     * @param methodKey Nome do método Feign (ex: ConexaoAuthClient#registerUser)
     * @param response Resposta HTTP do servidor
     * @return Exceção da SDK apropriada para o código de status HTTP
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        // Extrair corpo da resposta se disponível
        String responseBody = extractResponseBody(response);

        // Log do erro com contexto
        log.error("Erro na requisição '{}' - Status: {}, Body: {}",
                methodKey, status, responseBody);

        // Traduzir código de status HTTP para exceção apropriada
        return switch (status) {
            case 401 -> new UnauthorizedException(
                    buildErrorMessage("Não autorizado", methodKey, responseBody));
            case 403 -> new ForbiddenException(
                    buildErrorMessage("Acesso proibido", methodKey, responseBody));
            case 404 -> new ResourceNotFoundException(
                    buildErrorMessage("Recurso não encontrado", methodKey, responseBody));
            case 409 -> new ConflictException(
                    buildErrorMessage("Conflito de dados", methodKey, responseBody));
            default -> {
                // Para erros 5xx ou outros códigos não tratados
                if (status >= 500) {
                    yield new ServerException(
                            buildErrorMessage("Erro interno do servidor", methodKey, responseBody),
                            status);
                } else {
                    yield new ServerException(
                            buildErrorMessage("Erro HTTP não tratado", methodKey, responseBody),
                            status);
                }
            }
        };
    }

    /**
     * Extrai corpo da resposta HTTP.
     *
     * @param response Resposta HTTP
     * @return Corpo da resposta como string, ou null se não disponível
     */
    private String extractResponseBody(Response response) {
        try {
            if (response.body() != null) {
                return Util.toString(response.body().asReader());
            }
        } catch (IOException e) {
            log.warn("Erro ao extrair corpo da resposta: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Constrói mensagem de erro detalhada.
     *
     * @param errorType Tipo de erro
     * @param methodKey Nome do método Feign
     * @param responseBody Corpo da resposta HTTP
     * @return Mensagem de erro detalhada
     */
    private String buildErrorMessage(String errorType, String methodKey, String responseBody) {
        StringBuilder message = new StringBuilder();
        message.append(errorType).append(" ao chamar método '").append(methodKey).append("'");

        if (responseBody != null && !responseBody.isBlank()) {
            message.append(". Detalhes: ").append(responseBody);
        }

        return message.toString();
    }
}
```

---

## Exemplos de Testes

### Teste de Tradução de 401 Unauthorized

```java
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes unitários de ConexaoAuthErrorDecoder.
 */
@DisplayName("Testes Unitários - ConexaoAuthErrorDecoder")
class ConexaoAuthErrorDecoderUnitTest {

    private ConexaoAuthErrorDecoder decoder;
    private Request request;

    @BeforeEach
    void setUp() {
        decoder = new ConexaoAuthErrorDecoder();
        request = Request.create(
            Request.HttpMethod.POST,
            "/api/v1/usuarios",
            Collections.emptyMap(),
            null,
            StandardCharsets.UTF_8,
            null
        );
    }

    @Test
    @DisplayName("Dado status 401, quando decodificar, então UnauthorizedException lançada")
    void dadoStatus401_quandoDecodificar_entaoUnauthorizedExceptionLancada() {
        // Arrange
        String responseBody = "{\"error\":\"unauthorized\",\"error_description\":\"Invalid credentials\"}";
        Response response = Response.builder()
                .status(401)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#registerUser", response);

        assertThat(exception)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Não autorizado")
                .hasMessageContaining("Invalid credentials");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Dado status 403, quando decodificar, então ForbiddenException lançada")
    void dadoStatus403_quandoDecodificar_entaoForbiddenExceptionLancada() {
        // Arrange
        String responseBody = "{\"error\":\"forbidden\",\"error_description\":\"Insufficient permissions\"}";
        Response response = Response.builder()
                .status(403)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#findUserByCpf", response);

        assertThat(exception)
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Acesso proibido")
                .hasMessageContaining("Insufficient permissions");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Dado status 404, quando decodificar, então ResourceNotFoundException lançada")
    void dadoStatus404_quandoDecodificar_entaoResourceNotFoundExceptionLancada() {
        // Arrange
        String responseBody = "{\"error\":\"not_found\",\"error_description\":\"User not found\"}";
        Response response = Response.builder()
                .status(404)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#findUserByCpf", response);

        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Recurso não encontrado")
                .hasMessageContaining("User not found");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Dado status 409, quando decodificar, então ConflictException lançada")
    void dadoStatus409_quandoDecodificar_entaoConflictExceptionLancada() {
        // Arrange
        String responseBody = "{\"error\":\"conflict\",\"error_description\":\"Email already exists\"}";
        Response response = Response.builder()
                .status(409)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#registerUser", response);

        assertThat(exception)
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Conflito de dados")
                .hasMessageContaining("Email already exists");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Dado status 500, quando decodificar, então ServerException lançada")
    void dadoStatus500_quandoDecodificar_entaoServerExceptionLancada() {
        // Arrange
        String responseBody = "{\"error\":\"internal_server_error\"}";
        Response response = Response.builder()
                .status(500)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#registerUser", response);

        assertThat(exception)
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Erro interno do servidor");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("Dado status 503, quando decodificar, então ServerException lançada")
    void dadoStatus503_quandoDecodificar_entaoServerExceptionLancada() {
        // Arrange
        Response response = Response.builder()
                .status(503)
                .request(request)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("JwksClient#getJwks", response);

        assertThat(exception)
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Erro interno do servidor");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(503);
    }

    @Test
    @DisplayName("Dado status 418, quando decodificar, então ServerException lançada (código não tratado)")
    void dadoStatus418_quandoDecodificar_entaoServerExceptionLancada() {
        // Arrange
        Response response = Response.builder()
                .status(418)
                .request(request)
                .build();

        // Act & Assert
        Exception exception = decoder.decode("ConexaoAuthClient#registerUser", response);

        assertThat(exception)
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Erro HTTP não tratado");
        assertThat(((ConexaoAuthException) exception).getStatusCode()).isEqualTo(418);
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (criou stub do ErrorDecoder)
- **Story SDK-2.1:** Feign Client - ConexaoAuthClient (usará este decoder)
- **Story SDK-2.2:** Feign Client - JwksClient (usará este decoder)

---

## Pontos de Atenção

1. **Mapping Correto de Códigos HTTP:** 401 → Unauthorized, 403 → Forbidden, etc.
2. **Mensagens em Português:** Todas as mensagens de erro devem ser claras e em Português
3. **Preservação de Stack Trace:** Stack trace original deve ser preservada para debugging
4. **Logging:** Logar erros com contexto (methodKey, status, responseBody)
5. **Extrair Response Body:** Tentar extrair corpo da resposta para detalhes adicionais
6. **Javadoc Completo:** Documentar todas as exceções com causas comuns e ações recomendadas
7. **Switch Expression:** Usar switch expression (Java 21) para mapping de códigos HTTP
8. **Tratamento de Códigos Desconhecidos:** Códigos não tratados devem retornar ServerException

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Created/Updated Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/exception/ConexaoAuthException.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/exception/UnauthorizedException.java` (nova)
- `src/main/java/com/plataforma/conexao/auth/starter/exception/ForbiddenException.java` (nova)
- `src/main/java/com/plataforma/conexao/auth/starter/exception/ResourceNotFoundException.java` (nova)
- `src/main/java/com/plataforma/conexao/auth/starter/exception/ConflictException.java` (nova)
- `src/main/java/com/plataforma/conexao/auth/starter/exception/ServerException.java` (nova)
- `src/main/java/com/plataforma/conexao/auth/starter/decoder/ConexaoAuthErrorDecoder.java` (expandido)

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/decoder/ConexaoAuthErrorDecoderUnitTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-2: Feign Client e Error Decoder](./epic-sdk-2-feign-client.md)
- **Story Anterior:** [Story SDK-2.2: Feign Client - JwksClient](./story-sdk-2-2-jwks-client.md)
- **Story Seguinte:** [Story SDK-2.4: DTOs de Request](./story-sdk-2-4-dtos-request.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
