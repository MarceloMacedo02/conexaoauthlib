# Epic SDK-1: Estrutura Básica do Starter

**Epic:** SDK-1 - Estrutura Básica
**Status:** Planejado
**Prioridade:** Alta (P0)
**Complexidade:** Média
**Estimativa:** 2 dias

---

## Descrição

Este Epic implementa a estrutura básica do Spring Boot Starter SDK, incluindo auto-configuração, propriedades de configuração e a estrutura de pacotes do projeto. É o fundamento sobre o qual todos os outros componentes serão construídos.

---

## Funcionalidades Implementadas

1. **Auto-Configuration** - Configuração automática do SDK via Spring Boot 3+
2. **Configuration Properties** - Propriedades configuráveis via application.yml
3. **Feign Configuration** - Configuração do cliente HTTP declarativo
4. **Estrutura de Pacotes** - Organização completa de pacotes do SDK
5. **Registro de Beans** - Beans registrados automaticamente quando habilitado

---

## Stories do Epic

| # | Story | Prioridade | Estimativa | Status |
|---|-------|-----------|------------|--------|
| SDK-1.1 | Auto-Configuration Principal | Alta (P0) | 0.5 dia | Planejado |
| SDK-1.2 | Configuration Properties | Alta (P0) | 0.5 dia | Planejado |
| SDK-1.3 | Feign Configuration | Alta (P0) | 0.5 dia | Planejado |
| SDK-1.4 | Estrutura de Pacotes e Imports | Média (P1) | 0.5 dia | Planejado |

---

## Dependências

Nenhuma - Este é o primeiro epic e não depende de outros épicos.

---

## Arquitetura do Epic

### Pacote: config

```
com.plataforma.conexao.auth.starter.config/
├── ConexaoAuthAutoConfiguration.java    # Auto-configuration principal
└── FeignConfiguration.java              # Configuração do Feign Client
```

### Pacote: properties

```
com.plataforma.conexao.auth.starter.properties/
└── ConexaoAuthProperties.java           # Propriedades configuráveis
```

### Arquivo de Configuração Spring Boot

```
src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## Componentes Principais

### ConexaoAuthAutoConfiguration

**Responsabilidade:** Classe principal de auto-configuração do SDK.

**Anotações:**
- `@AutoConfiguration`
- `@ConditionalOnProperty(prefix = "conexao.auth", name = "enabled", havingValue = "true")`
- `@EnableConfigurationProperties(ConexaoAuthProperties.class)`
- `@Import(FeignConfiguration.class)`

**Beans Registrados:**
- `conexaoAuthClient()` - Feign Client para comunicação HTTP
- `tokenValidator()` - Validador de JWT (depende de Feign Client)
- `conexaoAuthService()` - Serviço de alto nível (depende de Feign Client e Token Validator)
- `conexaoAuthErrorDecoder()` - Error Decoder customizado

### ConexaoAuthProperties

**Responsabilidade:** Propriedades de configuração do SDK.

**Anotações:**
- `@ConfigurationProperties(prefix = "conexao.auth")`
- `@Validated`

**Campos:**
```java
public class ConexaoAuthProperties {
    private Boolean enabled = false;
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String realmId;
    private Integer connectionTimeout = 5000;
    private Integer readTimeout = 10000;
    private Long jwksCacheTtl = 300000L;
}
```

### FeignConfiguration

**Responsabilidade:** Configuração do Feign Client.

**Beans Registrados:**
- `feignClient()` - OkHttp Client (mais performático)
- `feignEncoder()` - Jackson Encoder
- `feignDecoder()` - Jackson Decoder

---

## Critérios de Aceite por Story

### Story SDK-1.1: Auto-Configuration Principal

- [ ] Classe `ConexaoAuthAutoConfiguration` criada com anotações corretas
- [ ] Auto-configuração só ativa se `conexao.auth.enabled=true`
- [ ] Beans são registrados automaticamente pelo Spring Boot
- [ ] Logs informativos na inicialização
- [ ] Beans dependem de `ConexaoAuthProperties` configurado

### Story SDK-1.2: Configuration Properties

- [ ] Classe `ConexaoAuthProperties` criada com anotações corretas
- [ ] Propriedades são lidas do application.yml
- [ ] Validações Jakarta Bean Validation funcionam
- [ ] Valores padrão definidos para propriedades opcionais
- [ ] Exceções lançadas se propriedades obrigatórias faltam

### Story SDK-1.3: Feign Configuration

- [ ] Classe `FeignConfiguration` criada com anotações corretas
- [ ] OkHttp Client configurado como cliente HTTP padrão
- [ ] Jackson configurado para encoder/decoder JSON
- [ ] Beans registrados corretamente no contexto Spring

### Story SDK-1.4: Estrutura de Pacotes e Imports

- [ ] Estrutura de pacotes criada conforme plano técnico
- [ ] Arquivo de imports Spring Boot criado
- [ ] Padrão de nomenclatura seguido
- [ ] Documentação Javadoc em classes públicas
- [ ] Checkstyle não reporta erros

---

## Exemplo de Configuração

### application.yml

```yaml
conexao:
  auth:
    # Habilita o Starter SDK
    enabled: true

    # URL base do Auth Server
    base-url: https://auth.example.com

    # Credenciais OAuth2
    client-id: meu-client-id
    client-secret: meu-client-secret

    # ID do Realm padrão
    realm-id: master

    # Timeouts em milissegundos
    connection-timeout: 5000
    read-timeout: 10000

    # TTL do cache JWKS em milissegundos
    jwks-cache-ttl: 300000
```

---

## Tecnologias Utilizadas

- **Java 21** - Linguagem principal
- **Spring Boot 3.2+** - Framework de auto-configuração
- **Spring Cloud OpenFeign** - Cliente HTTP declarativo
- **OkHttp** - Cliente HTTP
- **Jackson** - Serialização/Deserialização JSON
- **Lombok** - Redução de boilerplate
- **Jakarta Validation** - Validação de beans

---

## Testes Requeridos

### Testes Unitários

- Teste de auto-configuração desabilitada
- Teste de auto-configuração habilitada
- Teste de leitura de propriedades válidas
- Teste de validação de propriedades inválidas
- Teste de valores padrão

### Testes de Integração

- Teste de contexto Spring com SDK habilitado
- Teste de contexto Spring com SDK desabilitado
- Teste de beans registrados corretamente

---

## Pontos de Atenção

1. **ConditionalOnProperty:** Garantir que o SDK só inicializa se `conexao.auth.enabled=true`
2. **Validação de Propriedades:** Usar Jakarta Bean Validation para validação em tempo de inicialização
3. **OkHttp Performance:** OkHttp é mais performático que o cliente HTTP padrão do Feign
4. **Logs Adicionais:** Adicionar logs informativos para debugging sem expor credenciais
5. **Segurança:** Client secret nunca deve aparecer em logs

---

## Próximos Passos

Após conclusão deste Epic:
1. **Epic SDK-2: Feign Client** - Implementar interfaces Feign e Error Decoder
2. **Epic SDK-3: Token Validator** - Implementar validação JWT e JWKS cache
3. **Epic SDK-4: Auth Service** - Implementar serviços de alto nível
4. **Epic SDK-5: Testes e Documentação** - Implementar testes abrangentes e documentação

---

## Estatísticas do Epic

| Métrica | Quantidade |
|---------|------------|
| **Stories** | 4 |
| **Classes Java** | 3 |
| **Arquivos de Configuração** | 1 |
| **Testes Estimados** | 8-10 |
| **Linhas de Código** | ~150-200 |
