# Story SDK-1.2: Configuration Properties

**Epic:** SDK-1 - Estrutura Básica
**Story:** SDK-1.2
**Status:** Concluída ✅
**Prioridade:** Alta (P0)
**Estimativa:** 0.5 dia
**Complexidade:** Baixa

---

## Descrição

Implementar a classe `ConexaoAuthProperties` que define todas as propriedades configuráveis do SDK via `application.yml`, com validações Jakarta Bean Validation.

---

## Critérios de Aceite

- [x] Classe `ConexaoAuthProperties` criada com anotações corretas
- [x] Anotação `@ConfigurationProperties(prefix = "conexao.auth")` configurada
- [x] Anotação `@Validated` configurada
- [x] Propriedades são lidas corretamente do application.yml
- [x] Validações Jakarta Bean Validation funcionam (@NotBlank, @NotNull, @Positive)
- [x] Valores padrão definidos para propriedades opcionais
- [x] Exceções lançadas se propriedades obrigatórias faltam
- [x] Mensagens de erro em Português

---

## Regras de Negócio

1. **Propriedades Obrigatórias:** `enabled`, `base-url`, `client-id`, `client-secret`, `realm-id`
2. **Propriedades Opcionais:** `connection-timeout`, `read-timeout`, `jwks-cache-ttl`
3. **Valores Padrão:** Propriedades opcionais devem ter valores padrão sensatos
4. **Validação em Tempo de Inicialização:** Validação deve falhar se propriedades obrigatórias faltam
5. **Valores Positivos:** Timeouts e TTL devem ser positivos

---

## Requisitos Técnicos

### Classe ConexaoAuthProperties

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthProperties.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propriedades de configuração do Conexão Auth Starter.
 * Configuráveis via application.yml com prefixo conexao.auth.
 *
 * <p>Exemplo de configuração:
 * <pre>
 * conexao:
 *   auth:
 *     enabled: true
 *     base-url: https://auth.example.com
 *     client-id: meu-client-id
 *     client-secret: meu-client-secret
 *     realm-id: master
 *     connection-timeout: 5000
 *     read-timeout: 10000
 *     jwks-cache-ttl: 300000
 * </pre>
 *
 * <p><b>NOTA:</b> Esta classe usa Java 21 records (sem Lombok) seguindo o padrão do projeto.
 * Spring Boot 3.x suporta configuration properties com records.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "conexao.auth")
public record ConexaoAuthProperties(
    /**
     * Habilita ou desabilita o Starter SDK.
     * <p>Padrão: false</p>
     */
    @NotNull(message = "conexao.auth.enabled é obrigatório")
    Boolean enabled,

    /**
     * URL base do Auth Server.
     * <p>Exemplos: https://auth.example.com ou http://conexao-auth:8080</p>
     */
    @NotBlank(message = "conexao.auth.base-url é obrigatório")
    String baseUrl,

    /**
     * Client ID OAuth2 para autenticação da aplicação.
     */
    @NotBlank(message = "conexao.auth.client-id é obrigatório")
    String clientId,

    /**
     * Client Secret OAuth2 para autenticação da aplicação.
     * <p><b>Atenção:</b> Nunca exponha este valor em logs.</p>
     */
    @NotBlank(message = "conexao.auth.client-secret é obrigatório")
    String clientSecret,

    /**
     * ID do Realm padrão a ser usado nas operações.
     */
    @NotBlank(message = "conexao.auth.realm-id é obrigatório")
    String realmId,

    /**
     * Timeout de conexão em milissegundos.
     * <p>Padrão: 5000ms (5 segundos)</p>
     */
    @Positive(message = "conexao.auth.connection-timeout deve ser positivo")
    Integer connectionTimeout,

    /**
     * Timeout de leitura em milissegundos.
     * <p>Padrão: 10000ms (10 segundos)</p>
     */
    @Positive(message = "conexao.auth.read-timeout deve ser positivo")
    Integer readTimeout,

    /**
     * TTL (Time To Live) do cache JWKS em milissegundos.
     * <p>Padrão: 300000ms (5 minutos)</p>
     */
    @Positive(message = "conexao.auth.jwks-cache-ttl deve ser positivo")
    Long jwksCacheTtl
) {
    /**
     * Construtor padrão com valores padrão.
     * <p>Spring Boot 3.x requer construtor padrão para configuration properties com records.</p>
     */
    public ConexaoAuthProperties {
        if (enabled == null) {
            enabled = false;
        }
        if (connectionTimeout == null) {
            connectionTimeout = 5000;
        }
        if (readTimeout == null) {
            readTimeout = 10000;
        }
        if (jwksCacheTtl == null) {
            jwksCacheTtl = 300000L;
        }
    }
}
```

---

## Exemplos de Testes

### Teste Unitário Direto com @Valid

```java
package com.plataforma.conexao.auth.starter.properties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários diretos de ConexaoAuthProperties com @Valid.
 */
@DisplayName("Testes Unitários - ConexaoAuthProperties")
class ConexaoAuthPropertiesUnitTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Dadas propriedades válidas, quando validado, então não há violações")
    void dadoPropriedadesValidas_quandoValidado_entaoSemViolacoes() {
        ConexaoAuthProperties properties = new ConexaoAuthProperties(
            true,
            "http://localhost:8080",
            "test-client",
            "test-secret",
            "master",
            5000,
            10000,
            300000L
        );

        Set<ConstraintViolation<ConexaoAuthProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Dada base URL em branco, quando validado, então há violação @NotBlank")
    void dadaBaseUrlEmBranco_quandoValidado_entaoViolacaoNotBlank() {
        ConexaoAuthProperties properties = new ConexaoAuthProperties(
            true,
            "",
            "test-client",
            "test-secret",
            "master",
            5000,
            10000,
            300000L
        );

        Set<ConstraintViolation<ConexaoAuthProperties>> violations = validator.validate(properties);

        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("conexao.auth.base-url é obrigatório"));
    }

    @Test
    @DisplayName("Dada connection timeout negativo, quando validado, então há violação @Positive")
    void dadoConnectionTimeoutNegativo_quandoValidado_entaoViolacaoPositive() {
        ConexaoAuthProperties properties = new ConexaoAuthProperties(
            true,
            "http://localhost:8080",
            "test-client",
            "test-secret",
            "master",
            -100,
            10000,
            300000L
        );

        Set<ConstraintViolation<ConexaoAuthProperties>> violations = validator.validate(properties);

        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("conexao.auth.connection-timeout deve ser positivo"));
    }

    @Test
    @DisplayName("Dado enabled nulo, quando validado, então há violação @NotNull")
    void dadoEnabledNulo_quandoValidado_entaoViolacaoNotNull() {
        ConexaoAuthProperties properties = new ConexaoAuthProperties(
            null,
            "http://localhost:8080",
            "test-client",
            "test-secret",
            "master",
            5000,
            10000,
            300000L
        );

        Set<ConstraintViolation<ConexaoAuthProperties>> violations = validator.validate(properties);

        assertThat(violations)
            .hasSize(1)
            .anyMatch(v -> v.getMessage().contains("conexao.auth.enabled é obrigatório"));
    }
}
```

### Teste de Leitura de Propriedades Válidas (Integração)

```java
@SpringBootTest(properties = {
    "conexao.auth.enabled=true",
    "conexao.auth.base-url=http://localhost:8080",
    "conexao.auth.client-id=test-client",
    "conexao.auth.client-secret=test-secret",
    "conexao.auth.realm-id=master"
})
class ConexaoAuthPropertiesValidTest {

    @Autowired
    private ConexaoAuthProperties properties;

    @Test
    void dadasPropriedadesValidas_quandoLer_entaoValoresLidosCorretamente() {
        assertThat(properties.enabled()).isTrue();
        assertThat(properties.baseUrl()).isEqualTo("http://localhost:8080");
        assertThat(properties.clientId()).isEqualTo("test-client");
        assertThat(properties.clientSecret()).isEqualTo("test-secret");
        assertThat(properties.realmId()).isEqualTo("master");
        assertThat(properties.connectionTimeout()).isEqualTo(5000);
        assertThat(properties.readTimeout()).isEqualTo(10000);
        assertThat(properties.jwksCacheTtl()).isEqualTo(300000L);
    }
}
```

### Teste de Validação de Propriedade em Branco (Integração)

```java
@SpringBootTest(properties = {
    "conexao.auth.enabled=true",
    "conexao.auth.base-url=",
    "conexao.auth.client-id=test-client",
    "conexao.auth.client-secret=test-secret",
    "conexao.auth.realm-id=master"
})
class ConexaoAuthPropertiesBlankTest {

    @Test
    void dadaBaseUrlEmBranco_quandoIniciarContexto_entaoFalha() {
        assertThatThrownBy(() -> new SpringApplicationBuilder(ConexaoAuthPropertiesBlankTest.class)
                .run())
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
```

### Teste de Validação de Timeout Negativo (Integração)

```java
@SpringBootTest(properties = {
    "conexao.auth.enabled=true",
    "conexao.auth.base-url=http://localhost:8080",
    "conexao.auth.client-id=test-client",
    "conexao.auth.client-secret=test-secret",
    "conexao.auth.realm-id=master",
    "conexao.auth.connection-timeout=-1"
})
class ConexaoAuthPropertiesNegativeTimeoutTest {

    @Test
    void dadoTimeoutNegativo_quandoIniciarContexto_entaoFalha() {
        assertThatThrownBy(() -> new SpringApplicationBuilder(ConexaoAuthPropertiesNegativeTimeoutTest.class)
                .run())
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
```

### Teste de Valores Padrão (Integração)

```java
@SpringBootTest(properties = {
    "conexao.auth.enabled=true",
    "conexao.auth.base-url=http://localhost:8080",
    "conexao.auth.client-id=test-client",
    "conexao.auth.client-secret=test-secret",
    "conexao.auth.realm-id=master"
})
class ConexaoAuthPropertiesDefaultsTest {

    @Autowired
    private ConexaoAuthProperties properties;

    @Test
    void dadasPropriedadesMinimas_quandoLer_entaoValoresPadraoAplicados() {
        assertThat(properties.connectionTimeout()).isEqualTo(5000);
        assertThat(properties.readTimeout()).isEqualTo(10000);
        assertThat(properties.jwksCacheTtl()).isEqualTo(300000L);
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (depende desta classe)

---

## Pontos de Atenção

1. **Validação em Tempo de Inicialização:** Deve falhar se propriedades obrigatórias faltam
2. **Mensagens de Erro em Português:** Todas as mensagens de validação devem ser em Português
3. **Valores Padrão Sensatos:** Timeouts de 5s/10s e TTL de 5 minutos são valores razoáveis
4. **Client Secret em Logs:** Nunca expor client secret em logs (marcar no Javadoc)
5. **Jakarta Validation:** Usar anotações `@NotBlank`, `@NotNull`, `@Positive`
6. **Java 21 Records:** Usar record (sem Lombok) seguindo padrão do projeto
7. **Construtor Compact:** Usar compact constructor para definir valores padrão
8. **Spring Boot 3.x:** Records são suportados como Configuration Properties desde Spring Boot 3.0

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Created Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthProperties.java`

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthPropertiesValidTest.java`
- `src/test/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthPropertiesBlankTest.java`
- `src/test/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthPropertiesNegativeTimeoutTest.java`
- `src/test/java/com/plataforma/conexao/auth/starter/properties/ConexaoAuthPropertiesDefaultsTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-1: Estrutura Básica](./epic-sdk-1-estrutura-basica.md)
- **Story Anterior:** [Story SDK-1.1: Auto-Configuration Principal](./story-sdk-1-1-auto-configuration.md)
- **Story Seguinte:** [Story SDK-1.3: Feign Configuration](./story-sdk-1-3-feign-configuration.md)
- **Documentação de Arquitetura:** [Arquitetura do Starter SDK](../architecture/starter-sdk-arquitetura.md)
