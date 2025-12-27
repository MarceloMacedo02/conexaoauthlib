# Story SDK-2.4: DTOs de Request

**Epic:** SDK-2 - Feign Client e Error Decoder
**Story:** SDK-2.4
**Status:** Planejado
**Prioridade:** Alta (P0)
**Estimativa:** 0.15 dia
**Complexidade:** Baixa

---

## Descrição

Expandir os DTOs de Request criados como stubs na Story SDK-1.1, convertendo-os para Java 21 records com validações Jakarta Bean Validation completas.

---

## Critérios de Aceite

- [ ] DTOs de Request convertidos para Java 21 records
- [ ] Anotações de validação Jakarta Bean Validation adicionadas (@NotBlank, @Email, @Size, @Pattern)
- [ ] Mensagens de validação em Português
- [ ] Javadoc completo em Português para todos os campos
- [ ] Checkstyle não reporta erros

---

## Regras de Negócio

1. **Records Java 21:** Todos os DTOs devem usar Java 21 records (sem Lombok)
2. **Validação Jakarta Bean Validation:** Usar anotações `@NotBlank`, `@Email`, `@Size`, `@Pattern`
3. **Mensagens em Português:** Mensagens de validação devem ser claras e em Português
4. **Imutabilidade:** DTOs são imutáveis por serem records

---

## Requisitos Técnicos

### DTO RegisterUserRequest

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/request/RegisterUserRequest.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para registro de novo usuário via API do Auth Server.
 *
 * <p>Este DTO contém todos os dados necessários para criar um novo usuário
 * no Conexão Auth Server.
 *
 * <p><b>Validações:</b>
 * <ul>
 *   <li>Nome: Obrigatório, entre 3 e 100 caracteres</li>
 *   <li>Email: Obrigatório, formato válido, único no sistema</li>
 *   <li>Senha: Obrigatória, mínimo 8 caracteres</li>
 *   <li>CPF: Opcional, formato 11 dígitos numéricos, único no sistema se fornecido</li>
 *   <li>Realm ID: Obrigatório</li>
 *   <li>Role IDs: Opcional, lista de IDs de roles a serem associadas</li>
 * </ul>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * RegisterUserRequest request = new RegisterUserRequest(
 *     "João Silva",                          // nome
 *     "joao.silva@example.com",             // email
 *     "senhaSegura123",                     // senha
 *     "12345678901",                         // cpf (opcional)
 *     "master",                              // realmId
 *     List.of("role-user"),                  // roleIds (opcional)
 *     null,                                  // empresaId (opcional)
 *     null                                   // tenantId (opcional)
 * );
 *
 * UserResponse response = conexaoAuthService.registerUser(request);
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record RegisterUserRequest(
    /**
     * Nome completo do usuário.
     *
     * <p>Obrigatório, entre 3 e 100 caracteres.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    /**
     * Email do usuário.
     *
     * <p>Obrigatório, deve ser um email válido e único no sistema.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    String email,

    /**
     * Senha do usuário.
     *
     * <p>Obrigatória, mínimo 8 caracteres.
     * <b>Atenção:</b> Nunca exponha este valor em logs.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha,

    /**
     * CPF do usuário (opcional).
     *
     * <p>Se fornecido, deve ter 11 dígitos numéricos e ser único no sistema.
     */
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve ter 11 dígitos numéricos")
    String cpf,

    /**
     * ID do Realm onde o usuário será criado.
     *
     * <p>Obrigatório.
     */
    @NotBlank(message = "Realm ID é obrigatório")
    String realmId,

    /**
     * Lista de IDs de roles a serem associadas ao usuário (opcional).
     *
     * <p>Se não fornecido, o usuário será criado sem roles adicionais
     * (apenas roles padrão do sistema).
     */
    List<String> roleIds,

    /**
     * ID da empresa (opcional, para integração externa).
     *
     * <p>Usado para integração com sistemas de terceiros.
     */
    String empresaId,

    /**
     * ID do tenant (opcional, para multi-tenancy).
     *
     * <p>Usado em sistemas multi-tenant.
     */
    String tenantId
) {}
```

### DTO ClientCredentialsRequest

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/request/ClientCredentialsRequest.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitação de token via Client Credentials Flow.
 *
 * <p>Este DTO contém as credenciais necessárias para solicitar um token
 * de acesso usando OAuth 2.0 Client Credentials Flow.
 *
 * <p><b>OAuth 2.0 Client Credentials Flow:</b>
 * <pre>
 * POST /oauth2/token
 * Content-Type: application/x-www-form-urlencoded
 *
 * grant_type=client_credentials
 * &client_id=meu-client-id
 * &client_secret=meu-client-secret
 * &scope=read write
 * </pre>
 *
 * <p><b>Validações:</b>
 * <ul>
 *   <li>Grant Type: Obrigatório, deve ser "client_credentials"</li>
 *   <li>Client ID: Obrigatório</li>
 *   <li>Client Secret: Obrigatório</li>
 *   <li>Scope: Opcional, escopos solicitados (ex: "read write")</li>
 * </ul>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * ClientCredentialsRequest request = new ClientCredentialsRequest(
 *     "client_credentials",      // grantType
 *     "meu-client-id",           // clientId
 *     "meu-client-secret",       // clientSecret
 *     "read write"               // scope (opcional)
 * );
 *
 * TokenResponse response = conexaoAuthService.getClientCredentialsToken(request);
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record ClientCredentialsRequest(
    /**
     * Grant type OAuth2.
     *
     * <p>Para Client Credentials Flow, deve ser "client_credentials".
     */
    @NotBlank(message = "Grant type é obrigatório")
    String grantType,

    /**
     * Client ID OAuth2.
     *
     * <p>Identificador único do client no Auth Server.
     * Configurado via application.yml (conexao.auth.client-id).
     * <b>Atenção:</b> Nunca exponha este valor em logs.
     */
    @NotBlank(message = "Client ID é obrigatório")
    String clientId,

    /**
     * Client Secret OAuth2.
     *
     * <p>Segredo do client no Auth Server.
     * Configurado via application.yml (conexao.auth.client-secret).
     * <b>Atenção:</b> Nunca exponha este valor em logs.
     */
    @NotBlank(message = "Client Secret é obrigatório")
    String clientSecret,

    /**
     * Escopos solicitados (opcional).
     *
     * <p>Lista de escopos separados por espaço (ex: "read write").
     * Se não fornecido, o Auth Server retornará os escopos padrão do client.
     */
    String scope
) {}
```

### DTO FindUserByCpfRequest

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/dto/request/FindUserByCpfRequest.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para busca de usuário por CPF.
 *
 * <p>Este DTO contém o CPF do usuário a ser buscado.
 *
 * <p><b>Validações:</b>
 * <ul>
 *   <li>CPF: Obrigatório, formato 11 dígitos numéricos</li>
 * </ul>
 *
 * <p><b>Exemplo de Uso:</b>
 * <pre>
 * FindUserByCpfRequest request = new FindUserByCpfRequest("12345678901");
 *
 * UserResponse response = conexaoAuthService.findUserByCpf(request.cpf());
 * </pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record FindUserByCpfRequest(
    /**
     * CPF do usuário.
     *
     * <p>Obrigatório, deve ter 11 dígitos numéricos.
     */
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve ter 11 dígitos numéricos")
    String cpf
) {}
```

---

## Exemplos de Testes

### Teste de Validação - RegisterUserRequest

```java
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de validação de DTOs de Request.
 */
@DisplayName("Testes de Validação - DTOs de Request")
class RequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Dado RegisterUserRequest válido, quando validado, então sem violações")
    void dadoRegisterUserRequestValido_quandoValidado_entaoSemViolacoes() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "joao.silva@example.com",
            "senhaSegura123",
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Dado nome em branco, quando validado, então violação @NotBlank")
    void dadoNomeEmBranco_quandoValidado_entaoViolacaoNotBlank() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "",
            "joao.silva@example.com",
            "senhaSegura123",
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Nome é obrigatório"));
    }

    @Test
    @DisplayName("Dado nome muito curto, quando validado, então violação @Size")
    void dadoNomeMuitoCurto_quandoValidado_entaoViolacaoSize() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "Jo",  // 2 caracteres (mínimo: 3)
            "joao.silva@example.com",
            "senhaSegura123",
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Nome deve ter entre 3 e 100 caracteres"));
    }

    @Test
    @DisplayName("Dado email inválido, quando validado, então violação @Email")
    void dadoEmailInvalido_quandoValidado_entaoViolacaoEmail() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "email-invalido",  // Não é um email válido
            "senhaSegura123",
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Email inválido"));
    }

    @Test
    @DisplayName("Dada senha curta, quando validado, então violação @Size")
    void dadaSenhaCurta_quandoValidado_entaoViolacaoSize() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "joao.silva@example.com",
            "123",  // 3 caracteres (mínimo: 8)
            "12345678901",
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Senha deve ter no mínimo 8 caracteres"));
    }

    @Test
    @DisplayName("Dado CPF inválido, quando validado, então violação @Pattern")
    void dadoCpfInvalido_quandoValidado_entaoViolacaoPattern() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "joao.silva@example.com",
            "senhaSegura123",
            "1234567890",  // 10 dígitos (inválido, deve ser 11)
            "master",
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("CPF deve ter 11 dígitos numéricos"));
    }

    @Test
    @DisplayName("Dado realmId em branco, quando validado, então violação @NotBlank")
    void dadoRealmIdEmBranco_quandoValidado_entaoViolacaoNotBlank() {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest(
            "João Silva",
            "joao.silva@example.com",
            "senhaSegura123",
            "12345678901",
            "",  // Em branco
            List.of("role-user"),
            null,
            null
        );

        // Act
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Realm ID é obrigatório"));
    }

    @Test
    @DisplayName("Dado ClientCredentialsRequest válido, quando validado, então sem violações")
    void dadoClientCredentialsRequestValido_quandoValidado_entaoSemViolacoes() {
        // Arrange
        ClientCredentialsRequest request = new ClientCredentialsRequest(
            "client_credentials",
            "meu-client-id",
            "meu-client-secret",
            "read write"
        );

        // Act
        Set<ConstraintViolation<ClientCredentialsRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Dado grantType em branco, quando validado, então violação @NotBlank")
    void dadoGrantTypeEmBranco_quandoValidado_entaoViolacaoNotBlank() {
        // Arrange
        ClientCredentialsRequest request = new ClientCredentialsRequest(
            "",  // Em branco
            "meu-client-id",
            "meu-client-secret",
            "read write"
        );

        // Act
        Set<ConstraintViolation<ClientCredentialsRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("Grant type é obrigatório"));
    }

    @Test
    @DisplayName("Dado FindUserByCpfRequest válido, quando validado, então sem violações")
    void dadoFindUserByCpfRequestValido_quandoValidado_entaoSemViolacoes() {
        // Arrange
        FindUserByCpfRequest request = new FindUserByCpfRequest("12345678901");

        // Act
        Set<ConstraintViolation<FindUserByCpfRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Dado CPF inválido, quando validado, então violação @Pattern")
    void dadoCpfInvalidoNoFindUserByCpfRequest_quandoValidado_entaoViolacaoPattern() {
        // Arrange
        FindUserByCpfRequest request = new FindUserByCpfRequest("1234567890");

        // Act
        Set<ConstraintViolation<FindUserByCpfRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("CPF deve ter 11 dígitos numéricos"));
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (criou stubs destes DTOs)
- **Story SDK-2.1:** Feign Client - ConexaoAuthClient (usa estes DTOs)
- **Story SDK-2.3:** Error Decoder Customizado (trata erros de validação)

---

## Pontos de Atenção

1. **Records Java 21:** Todos os DTOs devem usar Java 21 records (sem Lombok)
2. **Validação Jakarta Bean Validation:** Usar anotações `@NotBlank`, `@Email`, `@Size`, `@Pattern`
3. **Mensagens em Português:** Mensagens de validação devem ser claras e em Português
4. **Javadoc Completo:** Documentar todos os campos com descrições e exemplos de uso
5. **Imutabilidade:** DTOs são imutáveis por serem records
6. **Segurança:** Marcar campos sensíveis (senha, clientSecret) com atenção no Javadoc
7. **Validação de CPF:** Usar regex `^\\d{11}$` para validar 11 dígitos numéricos
8. **Validação de Email:** Usar anotação `@Email` para validar formato de email

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Updated Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/dto/request/RegisterUserRequest.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/dto/request/ClientCredentialsRequest.java` (expandido)
- `src/main/java/com/plataforma/conexao/auth/starter/dto/request/FindUserByCpfRequest.java` (expandido)

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/dto/request/RequestDtoValidationTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-2: Feign Client e Error Decoder](./epic-sdk-2-feign-client.md)
- **Story Anterior:** [Story SDK-2.3: Error Decoder Customizado](./story-sdk-2-3-error-decoder.md)
- **Story Seguinte:** [Story SDK-2.5: DTOs de Response](./story-sdk-2-5-dtos-response.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
