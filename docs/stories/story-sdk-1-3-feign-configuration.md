# Story SDK-1.3: Feign Configuration

**Epic:** SDK-1 - Estrutura Básica
**Story:** SDK-1.3
**Status:** Concluída ✅
**Prioridade:** Alta (P0)
**Estimativa:** 0.5 dia
**Complexidade:** Baixa

---

## Descrição

Implementar a classe `FeignConfiguration` que configura o cliente HTTP OkHttp e o encoder/decoder Jackson para o Feign Client.

---

## Critérios de Aceite

- [x] Classe `FeignConfiguration` criada com anotação @Configuration
- [x] Bean `feignClient()` criado com OkHttp
- [x] Bean `feignEncoder()` criado com Jackson Encoder
- [x] Bean `feignDecoder()` criado com Jackson Decoder
- [x] Anotação `@ConditionalOnMissingBean` configurada em todos os beans
- [x] Logs informativos na criação de beans
- [x] Beans funcionam corretamente em testes de integração

---

## Regras de Negócio

1. **OkHttp como Cliente HTTP:** OkHttp é mais performático que o cliente HTTP padrão do Feign
2. **Jackson para JSON:** Usar Jackson para serialização/desserialização JSON
3. **Beans Condicionais:** Não criar beans se a aplicação consumidora já os forneceu

---

## Requisitos Técnicos

### Classe FeignConfiguration

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/config/FeignConfiguration.java`

**Código:**
```java
package com.plataforma.conexao.auth.starter.config;

import feign.Client;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Feign Client.
 *
 * <p>Configura OkHttp como cliente HTTP (mais performático que o padrão do Feign)
 * e Jackson para serialização/desserialização JSON.
 *
 * <p>Todos os beans são configurados com @ConditionalOnMissingBean para permitir
 * que a aplicação consumidora possa sobrescrever as configurações padrão se necessário.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class FeignConfiguration {

    /**
     * Configura OkHttp como cliente HTTP para o Feign.
     *
     * <p>OkHttp é mais performático que o cliente HTTP padrão do Feign
     * (Apache HttpClient) devido ao suporte a HTTP/2, connection pooling eficiente
     * e compressão automática.
     *
     * <p>Este bean só é criado se a aplicação não fornecer um bean do tipo feign.Client.
     * Isso permite que a aplicação consumidora pode customizar o cliente HTTP se necessário.
     *
     * @return Cliente HTTP OkHttp
     */
    @Bean
    @ConditionalOnMissingBean(feign.Client.class)
    public Client feignClient() {
        log.info("Configurando OkHttp Client para Feign");
        return new OkHttpClient();
    }

    /**
     * Configura Jackson como encoder para JSON.
     *
     * <p>Jackson é o padrão do ecossistema Spring e é altamente configurável.
     * Este bean só é criado se a aplicação não fornecer um bean do tipo Encoder.
     *
     * @return Encoder Jackson
     */
    @Bean
    @ConditionalOnMissingBean(Encoder.class)
    public Encoder feignEncoder() {
        log.info("Configurando Jackson Encoder para Feign");
        return new JacksonEncoder();
    }

    /**
     * Configura Jackson como decoder para JSON.
     *
     * <p>Este bean só é criado se a aplicação não fornecer um bean do tipo Decoder.
     *
     * @return Decoder Jackson
     */
    @Bean
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder feignDecoder() {
        log.info("Configurando Jackson Decoder para Feign");
        return new JacksonDecoder();
    }
}
```

### Dependências Maven Necessárias

```xml
<!-- OkHttp Client for Feign -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>

<!-- Jackson for JSON serialization/deserialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- Spring Cloud OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

---

## Exemplos de Testes

### Teste de Criação de Beans

```java
@SpringBootTest
class FeignConfigurationTest {

    @Autowired(required = false)
    private feign.Client feignClient;

    @Autowired(required = false)
    private Encoder feignEncoder;

    @Autowired(required = false)
    private Decoder feignDecoder;

    @Test
    void quandoIniciarContexto_entaoBeansCriados() {
        // Beans devem ser criados automaticamente
        assertThat(feignClient).isNotNull().isInstanceOf(OkHttpClient.class);
        assertThat(feignEncoder).isNotNull().isInstanceOf(JacksonEncoder.class);
        assertThat(feignDecoder).isNotNull().isInstanceOf(JacksonDecoder.class);
    }
}
```

### Teste de @ConditionalOnMissingBean (Beans Customizados)

```java
@SpringBootTest
class FeignConfigurationCustomBeanTest {

    @Bean
    public feign.Client customFeignClient() {
        // Aplicação fornece bean customizado
        return new feign.Client.Default(null, null);
    }

    @Autowired(required = false)
    private feign.Client feignClient;

    @Autowired(required = false)
    private Encoder feignEncoder;

    @Autowired(required = false)
    private Decoder feignDecoder;

    @Test
    void dadoBeanCustomizadoFornecido_entaoBeanSdkNaoCriado() {
        // Bean customizado deve ser usado (não o do SDK)
        assertThat(feignClient).isNotNull().isInstanceOf(feign.Client.Default.class);
        assertThat(feignClient).isNotInstanceOf(OkHttpClient.class);
    }

    @Test
    void dadoBeanCustomizadoFornecidoParaClient_entaoBeansEncoderDecoderSaoDoSdk() {
        // Encoder e Decoder devem ser do SDK (não foram sobrescritos)
        assertThat(feignEncoder).isNotNull().isInstanceOf(JacksonEncoder.class);
        assertThat(feignDecoder).isNotNull().isInstanceOf(JacksonDecoder.class);
    }
}
```

### Teste Unitário com Mockito

```java
import feign.Client;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Testes unitários de FeignConfiguration.
 */
@DisplayName("Testes Unitários - FeignConfiguration")
class FeignConfigurationUnitTest {

    @Test
    @DisplayName("Dado contexto com FeignConfiguration, quando iniciar, então beans são criados")
    void dadoContextoComFeignConfiguration_quandoIniciar_entaoBeansCriados() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(FeignConfiguration.class);
        context.refresh();

        Client client = context.getBean(Client.class);
        Encoder encoder = context.getBean(Encoder.class);
        Decoder decoder = context.getBean(Decoder.class);

        assertThat(client).isNotNull().isInstanceOf(OkHttpClient.class);
        assertThat(encoder).isNotNull().isInstanceOf(JacksonEncoder.class);
        assertThat(decoder).isNotNull().isInstanceOf(JacksonDecoder.class);

        context.close();
    }

    @Test
    @DisplayName("Dado bean customizado, quando iniciar contexto, então bean SDK não é criado")
    void dadoBeanCustomizado_quandoIniciarContexto_entaoBeanSdKNaoCriado() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // Registrar bean customizado antes de FeignConfiguration
        context.registerBean("feignClient", Client.class, () -> mock(Client.class));
        context.register(FeignConfiguration.class);
        context.refresh();

        Client client = context.getBean(Client.class);

        // Bean customizado deve ser usado
        assertThat(client).isNotNull().isInstanceOf(Client.class);
        assertThat(client).isNotInstanceOf(OkHttpClient.class);

        context.close();
    }
}
```

### Exemplo de Customização de Beans na Aplicação Consumidora

```java
@Configuration
public class CustomFeignConfiguration {

    /**
     * Sobrescreve o bean feign.Client do SDK.
     *
     * <p>Este exemplo mostra como a aplicação consumidora pode customizar
     * o cliente HTTP Feign com configurações específicas (ex: timeouts customizados,
     * interceptores, logging específico, etc.).
     */
    @Bean
    public feign.Client customFeignClient() {
        // Criar cliente OkHttp customizado com configurações específicas
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        return new feign.okhttp.OkHttpClient(client);
    }

    /**
     * Sobrescreve o bean Encoder do SDK.
     *
     * <p>Este exemplo mostra como a aplicação consumidora pode customizar
     * o encoder Jackson com configurações específicas (ex: formatos de data,
     * manejo de nulls, etc.).
     */
    @Bean
    public Encoder customFeignEncoder() {
        ObjectMapper mapper = new ObjectMapper();

        // Configurações customizadas do ObjectMapper
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return new JacksonEncoder(mapper);
    }

    /**
     * Sobrescreve o bean Decoder do SDK.
     *
     * <p>Este exemplo mostra como a aplicação consumidora pode customizar
     * o decoder Jackson com configurações específicas.
     */
    @Bean
    public Decoder customFeignDecoder() {
        ObjectMapper mapper = new ObjectMapper();

        // Configurações customizadas do ObjectMapper
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return new JacksonDecoder(mapper);
    }
}
```

---

## Dependências

- **Story SDK-1.1:** Auto-Configuration Principal (@Import esta classe)

---

## Pontos de Atenção

1. **OkHttp Performance:** OkHttp é mais performático que o cliente HTTP padrão do Feign
2. **ConditionalOnMissingBean:** Não criar beans se a aplicação já os forneceu
3. **Logging:** Adicionar logs informativos na criação de beans
4. **Jackson:** Usar Jackson para compatibilidade com o Auth Server

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** [To be filled]

### File List

#### Created Source Files (src/main/java/):
- `src/main/java/com/plataforma/conexao/auth/starter/config/FeignConfiguration.java`

#### Created Test Files (src/test/java/):
- `src/test/java/com/plataforma/conexao/auth/starter/config/FeignConfigurationTest.java`

---

## QA Results

### Review Date: [To be filled]

### Reviewed By: [To be filled]

### Code Quality Assessment

Story status is Planejado, not ready for review. The story must be implemented and status changed to "Review" before quality review can be performed.

---

## Links Relacionados

- **Epic:** [Epic SDK-1: Estrutura Básica](./epic-sdk-1-estrutura-basica.md)
- **Story Anterior:** [Story SDK-1.2: Configuration Properties](./story-sdk-1-2-configuration-properties.md)
- **Story Seguinte:** [Story SDK-1.4: Estrutura de Pacotes e Imports](./story-sdk-1-4-estrutura-pacotes-imports.md)
