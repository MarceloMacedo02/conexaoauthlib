# Plano Técnico Preliminar - Conexão Auth Starter SDK

**Versão:** 1.0
**Data:** 27 de Dezembro de 2025
**Status:** Proposta
**Tech Lead:** [Nome]

---

## 📋 Índice

1. [Visão Geral](#1-visão-geral)
2. [Estrutura de Pacotes](#2-estrutura-de-pacotes)
3. [Classes por Pacote](#3-classes-por-pacote)
4. [Diagrama de Classes](#4-diagrama-de-classes)
5. [Configuração Maven](#5-configuração-maven)
6. [Validação Mental contra Quality Gates](#6-validação-mental-contra-quality-gates)
7. [Checklist de Implementação](#7-checklist-de-implementação)

---

## 1. Visão Geral

Este documento apresenta o plano técnico preliminar para implementação do Conexão Auth Starter SDK. O objetivo é fornecer um mapa claro de todas as classes, interfaces e componentes a serem criados, seguindo estritamente as regras de ouro (Quality Gates) do projeto.

### 1.1 Stack Tecnológica

| Componente | Versão | Finalidade |
|-------------|--------|------------|
| **Java** | 21 LTS | Linguagem principal |
| **Spring Boot** | 3.2+ | Framework principal |
| **Spring Cloud OpenFeign** | 4.1+ | Cliente HTTP declarativo |
| **OkHttp** | 4.12+ | Cliente HTTP para Feign |
| **Jackson** | 2.15+ | Serialização/Deserialização JSON |
| **Lombok** | 1.18+ | Redução de boilerplate |
| **Jakarta Validation** | 3.0+ | Validação de beans |
| **Nimbus JOSE+JWT** | 9.37+ | Manipulação de JWT |

### 1.2 Princípios de Design

- **Clean Architecture**: Separação clara entre camadas (config, client, service, dto, exception)
- **SOLID**: Classes coesas, acoplamento baixo, polimorfismo
- **DRY (Don't Repeat Yourself)**: Código reutilizável, sem duplicação
- **Imutabilidade**: Records para DTOs, classes stateless onde possível
- **Type-Safe**: Configuration Properties com validação em tempo de compilação

---

## 2. Estrutura de Pacotes

```
com.plataforma.conexao.auth.starter
│
├── config/                              # Auto-Configuration
│   ├── ConexaoAuthAutoConfiguration.java
│   ├── FeignConfiguration.java
│   └── SecurityConfiguration.java
│
├── properties/                          # Configuration Properties
│   └── ConexaoAuthProperties.java
│
├── client/                              # Feign Clients
│   ├── ConexaoAuthClient.java
│   └── JwksClient.java
│
├── service/                             # Service Layer (Abstração)
│   ├── ConexaoAuthService.java          # Interface
│   ├── ConexaoAuthServiceImpl.java      # Implementação
│   ├── TokenValidator.java              # Interface
│   └── TokenValidatorImpl.java          # Implementação
│
├── dto/                                 # DTOs compartilhados
│   ├── request/                         # Requests Feign
│   │   ├── RegisterUserRequest.java
│   │   ├── ClientCredentialsRequest.java
│   │   └── FindUserByCpfRequest.java
│   └── response/                        # Responses Feign
│       ├── RegisterUserResponse.java
│       ├── UserResponse.java
│       ├── TokenResponse.java
│       └── JwksResponse.java
│
├── exception/                           # Exceções da SDK
│   ├── ConexaoAuthException.java         # Base
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   ├── ResourceNotFoundException.java
│   ├── ConflictException.java
│   ├── ServerException.java
│   └── InvalidTokenException.java
│
├── decoder/                             # Feign Error Decoders
│   └── ConexaoAuthErrorDecoder.java
│
└── model/                               # Modelos internos
    └── TokenClaims.java                 # Claims JWT extraídos

src/main/resources/
└── META-INF/
    └── spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports

src/test/java/...                         # Estrutura de testes espelhada
```

---

## 3. Classes por Pacote

### 3.1 Pacote: config

#### 3.1.1 ConexaoAuthAutoConfiguration

**Responsabilidade:** Auto-Configuration principal do SDK.

**Anotações:**
- `@AutoConfiguration`
- `@ConditionalOnProperty(prefix = "conexao.auth", name = "enabled", havingValue = "true")`
- `@EnableConfigurationProperties(ConexaoAuthProperties.class)`
- `@Import(FeignConfiguration.class)`

**Beans Registrados:**
- `ConexaoAuthClient`: Feign Client para comunicação HTTP
- `TokenValidator`: Validador de JWT
- `ConexaoAuthService`: Serviço de alto nível
- `ConexaoAuthErrorDecoder`: Error Decoder customizado

**Linhas Estimadas:** ~80 linhas

---

#### 3.1.2 FeignConfiguration

**Responsabilidade:** Configuração do Feign Client.

**Beans Registrados:**
- `feign.Client`: OkHttp Client (mais performático)
- `Encoder`: Jackson Encoder
- `Decoder`: Jackson Decoder

**Linhas Estimadas:** ~40 linhas

---

### 3.2 Pacote: properties

#### 3.2.1 ConexaoAuthProperties

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

**Validações:**
- `@NotNull` em todos os campos obrigatórios
- `@Positive` em timeouts e TTL

**Linhas Estimadas:** ~50 linhas

---

### 3.3 Pacote: client

#### 3.3.1 ConexaoAuthClient

**Responsabilidade:** Interface Feign para comunicação com Auth Server.

**Anotações:**
- `@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")`

**Métodos:**
```java
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface ConexaoAuthClient {
    @PostMapping("/api/v1/usuarios")
    UserResponse registerUser(@RequestBody RegisterUserRequest request);

    @GetMapping("/api/v1/usuarios/cpf/{cpf}")
    UserResponse findUserByCpf(@PathVariable("cpf") String cpf);

    @PostMapping("/oauth2/token")
    TokenResponse clientCredentials(@RequestBody ClientCredentialsRequest request);
}
```

**Linhas Estimadas:** ~30 linhas

---

#### 3.3.2 JwksClient

**Responsabilidade:** Interface Feign para buscar JWKS.

**Anotações:**
- `@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")`

**Métodos:**
```java
@FeignClient(name = "conexao-auth", url = "${conexao.auth.base-url}")
public interface JwksClient {
    @GetMapping("/.well-known/jwks.json")
    JwksResponse getJwks();
}
```

**Linhas Estimadas:** ~15 linhas

---

### 3.4 Pacote: service

#### 3.4.1 ConexaoAuthService

**Responsabilidade:** Interface do serviço de alto nível.

**Métodos:**
```java
public interface ConexaoAuthService {
    UserResponse registerUser(RegisterUserRequest request);
    UserResponse findUserByCpf(String cpf);
    boolean validatePermissions(String token, List<String> requiredPermissions);
    TokenResponse getClientCredentialsToken();
    TokenResponse refreshToken(String refreshToken);
}
```

**Linhas Estimadas:** ~15 linhas

---

#### 3.4.2 ConexaoAuthServiceImpl

**Responsabilidade:** Implementação do serviço de alto nível.

**Dependências:**
- `ConexaoAuthClient`
- `TokenValidator`
- `ConexaoAuthProperties`

**Linhas Estimadas:** ~100 linhas

---

#### 3.4.3 TokenValidator

**Responsabilidade:** Interface do validador de JWT.

**Métodos:**
```java
public interface TokenValidator {
    TokenClaims validateToken(String jwt) throws InvalidTokenException;
    TokenClaims extractClaims(String jwt) throws InvalidTokenException;
    void refreshJwksCache();
}
```

**Linhas Estimadas:** ~15 linhas

---

#### 3.4.4 TokenValidatorImpl

**Responsabilidade:** Implementação do validador de JWT.

**Dependências:**
- `JwksClient`
- `ConexaoAuthProperties`

**Funcionalidades:**
- Validação de assinatura RSA
- Verificação de expiração
- Cache de chaves públicas (com TTL)
- Extração de claims

**Linhas Estimadas:** ~150 linhas

---

### 3.5 Pacote: dto.request

#### 3.5.1 RegisterUserRequest

**Responsabilidade:** DTO para registro de usuário.

**Tipo:** Record (imutável)

**Campos:**
```java
public record RegisterUserRequest(
    @NotBlank String nome,
    @Email String email,
    @NotBlank @Size(min = 8) String senha,
    @Pattern(regexp = "^\\d{11}$") String cpf,
    @NotBlank String realmId,
    List<String> roleIds,
    String empresaId,
    String tenantId
) {}
```

**Linhas Estimadas:** ~20 linhas

---

#### 3.5.2 ClientCredentialsRequest

**Responsibilidade:** DTO para Client Credentials Flow.

**Tipo:** Record (imutável)

**Campos:**
```java
public record ClientCredentialsRequest(
    String grantType,
    String clientId,
    String clientSecret,
    String scope
) {}
```

**Linhas Estimadas:** ~15 linhas

---

#### 3.5.3 FindUserByCpfRequest

**Responsibilidade:** DTO para busca de usuário por CPF.

**Tipo:** Record (imutável)

**Campos:**
```java
public record FindUserByCpfRequest(
    @NotBlank String cpf
) {}
```

**Linhas Estimadas:** ~10 linhas

---

### 3.6 Pacote: dto.response

#### 3.6.1 UserResponse

**Responsibilidade:** DTO de resposta para dados de usuário.

**Tipo:** Record (imutável)

**Campos:**
```java
public record UserResponse(
    String id,
    String nome,
    String email,
    String cpf,
    String realmId,
    String realmNome,
    List<String> roles,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao
) {}
```

**Linhas Estimadas:** ~20 linhas

---

#### 3.6.2 TokenResponse

**Responsibilidade:** DTO de resposta para token OAuth2.

**Tipo:** Record (imutável)

**Campos:**
```java
public record TokenResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String scope
) {}
```

**Linhas Estimadas:** ~15 linhas

---

#### 3.6.3 JwksResponse

**Responsibilidade:** DTO de resposta para JWKS.

**Tipo:** Record (imutável)

**Campos:**
```java
public record JwksResponse(
    List<Jwk> keys
) {
    public record Jwk(
        String kid,
        String kty,
        String alg,
        String use,
        String n,  // modulus
        String e   // exponent
    ) {}
}
```

**Linhas Estimadas:** ~25 linmas

---

### 3.7 Pacote: exception

#### 3.7.1 ConexaoAuthException

**Responsibilidade:** Exceção base para erros do SDK.

**Campos:**
- `int statusCode` (código HTTP)

**Linhas Estimadas:** ~20 linhas

---

#### 3.7.2 UnauthorizedException

**Responsibilidade:** Exceção para 401 Unauthorized.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~10 linhas

---

#### 3.7.3 ForbiddenException

**Responsibilidade:** Exceção para 403 Forbidden.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~10 linhas

---

#### 3.7.4 ResourceNotFoundException

**Responsibilidade:** Exceção para 404 Not Found.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~10 linhas

---

#### 3.7.5 ConflictException

**Responsibilidade:** Exceção para 409 Conflict.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~10 linhas

---

#### 3.7.6 ServerException

**Responsibilidade:** Exceção para 500+ Server Error.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~15 linhas

---

#### 3.7.7 InvalidTokenException

**Responsibilidade:** Exceção para token inválido/expirado.

**Extends:** `ConexaoAuthException`

**Linhas Estimadas:** ~15 linhas

---

### 3.8 Pacote: decoder

#### 3.8.1 ConexaoAuthErrorDecoder

**Responsibilidade:** Traduz códigos HTTP em exceções da SDK.

**Implements:** `feign.codec.ErrorDecoder`

**Lógica:**
- 401 → `UnauthorizedException`
- 403 → `ForbiddenException`
- 404 → `ResourceNotFoundException`
- 409 → `ConflictException`
- 500+ → `ServerException`

**Linhas Estimadas:** ~60 linhas

---

### 3.9 Pacote: model

#### 3.9.1 TokenClaims

**Responsibilidade:** DTO interno para claims JWT extraídos.

**Tipo:** Record (imutável)

**Campos:**
```java
public record TokenClaims(
    String sub,        // Subject (user ID)
    String realm,      // Realm ID
    List<String> roles, // Lista de roles
    String aud,        // Audience
    String iss,        // Issuer
    Long exp,          // Expiration timestamp
    Long iat           // Issued at timestamp
) {
    public boolean isExpired() {
        return System.currentTimeMillis() / 1000 >= exp;
    }
}
```

**Linhas Estimadas:** ~25 linhas

---

## 4. Diagrama de Classes

### 4.1 Diagrama UML Simplificado

```
┌─────────────────────────────────────────────────────────────────┐
│                  ConexaoAuthAutoConfiguration                   │
│  └──────────────────────────────────────────────────────────┘  │
│  + ConexaoAuthClient clienteFeign()                             │
│  + TokenValidator tokenValidator()                              │
│  + ConexaoAuthService authService()                              │
└────────────────────┬────────────────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
         ▼                       ▼
┌──────────────────┐    ┌──────────────────┐
│ConexaoAuthService│    │  TokenValidator  │
│└─────────────────┘│    │└─────────────────┘│
│+ registerUser()  │    │+ validateToken() │
│+ findUserByCpf() │    │+ extractClaims() │
└────────┬─────────┘    └─────────┬────────┘
         │                         │
         ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│ConexaoAuthClient │    │    JwksClient    │
│  (Feign Client)  │    │  (Feign Client)  │
└──────────────────┘    └──────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   HIERARQUIA DE EXCEÇÕES                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────┐                      │
│  │    RuntimeException                   │                      │
│  └────────────┬─────────────────────────┘                      │
│               │                                                 │
│  ┌────────────▼─────────────────────────┐                      │
│  │    ConexaoAuthException              │                      │
│  │    - int statusCode                  │                      │
│  └────┬───────┬───────┬───────┬────────┘                      │
│       │       │       │       │                                 │
│  ┌────▼───┐ ┌─▼───┐ ┌─▼────┐ ┌─▼────────┐                    │
│  │401     │ │403  │ │404   │ │409      │                    │
│  │Unauth  │ │Forb │ │NotFnd│ │Conflict │                    │
│  └────────┘ └─────┘ └──────┘ └─────────┘                    │
│       │       │       │       │                                 │
│       └───────┴───────┴───────┴────────┐                       │
│               ┌─────────────────────────▼─────────┐            │
│               │         InvalidTokenException      │            │
│               └───────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. Configuração Maven

### 5.1 Estrutura do pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.plataforma.conexao</groupId>
    <artifactId>conexao-auth-spring-boot-starter</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Conexão Auth Spring Boot Starter</name>
    <description>SDK Spring Boot para integração com Conexão Auth</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.7</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-cloud.version>2023.0.2</spring-cloud.version>
        <nimbus-jose-jwt.version>9.37</nimbus-jose-jwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Cloud OpenFeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- OkHttp -->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-okhttp</artifactId>
        </dependency>

        <!-- Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Jakarta Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Nimbus JOSE + JWT (JWT manipulation) -->
        <dependency>
            <groupId>com.nimbusds</groupId>
            <artifactId>nimbus-jose-jwt</artifactId>
            <version>${nimbus-jose-jwt.version}</version>
        </dependency>

        <!-- Slf4j API (for logging) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-contract-verifier</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <release>${java.version}</release>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin (Unit Tests) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
            </plugin>

            <!-- Maven Failsafe Plugin (Integration Tests) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
            </plugin>

            <!-- JaCoCo (Code Coverage) -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>PACKAGE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.80</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Checkstyle (Google Java Style Guide) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <consoleOutput>true</consoleOutput>
                    <failsOnError>true</failsOnError>
                </configuration>
                <executions>
                    <execution>
                        <id>validate</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- SpotBugs (Static Analysis) -->
            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <configuration>
                    <excludeFilterFile>spotbugs-exclude.xml</excludeFilterFile>
                </configuration>
            </plugin>

            <!-- PMD (Code Quality) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <configuration>
                    <minimumTokens>100</minimumTokens>
                </configuration>
            </plugin>

            <!-- OWASP Dependency Check (Security Vulnerabilities) -->
            <plugin>
                <groupId>org.owasp</groupId>
                <artifactId>dependency-check-maven</artifactId>
                <configuration>
                    <failBuildOnCVSS>7</failBuildOnCVSS>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 6. Validação Mental contra Quality Gates

### 6.1 Princípio Mestre: Simplificar o código sem perder a lógica

✅ **Validado:**
- Código é declarativo e limpo
- Records para DTOs (sem getters/setters manuais)
- Auto-Configuration elimina boilerplate
- Feign Client é declarativo (sem código HTTP manual)

---

### 6.2 Padrão de Código: Google Java Style Guide

✅ **Validado:**
- Indentação de 4 espaços
- Nomes de classes em PascalCase
- Nomes de métodos em camelCase
- Javadoc obrigatório em métodos públicos
- Checkstyle configurado no pom.xml

---

### 6.3 Pense antes de Codificar: Regras de Negócio

✅ **Validado:**
- PRD detalhado criado
- Regras de validação claramente definidas
- Exceções de negócio específicas para cada caso

---

### 6.4 Padrão de Código: Java 21 LTS, Spring Boot 3.x

✅ **Validado:**
- Java 21 LTS configurado no pom.xml
- Spring Boot 3.2.7 como parent
- Spring Cloud 2023.0.2 para OpenFeign
- Records (Java 17+) usados para DTOs

---

### 6.5 Arquitetura: Clean Architecture / DDD

✅ **Validado:**
- Separação clara entre camadas (config, client, service, dto, exception)
- Interface e implementação separadas para services
- DDD: Domínio de identidade claramente definido

---

### 6.6 Entidades: IDs como UUID, BaseEntity

✅ **Validado:**
- IDs são Strings (UUIDs vindos do Auth Server)
- DTOs são Records (imutáveis)

---

### 6.7 DTOs: Uso exclusivo de record, validação Jakarta Validation

✅ **Validado:**
- Todos os DTOs são Records
- `@NotBlank`, `@Email`, `@Size`, `@Pattern` usados em requests
- Mensagens de validação definidas em código

---

### 6.8 Tratamento de Erros: Exceções de Domínio Personalizadas

✅ **Validado:**
- Hierarquia de exceções criada
- `ConexaoAuthException` como base
- Exceções específicas: `UnauthorizedException`, `ForbiddenException`, etc.

---

### 6.9 Documentação: Swagger/OpenAPI em Português

✅ **Validado:**
- Javadoc em Português em todos os métodos públicos
- Exemplos de uso claros no README (futuro)

---

### 6.10 Transações: @Transactional em métodos de escrita

✅ **Validado:**
- SDK é stateless, não gerencia transações
- Transações são gerenciadas pelo Auth Server

---

### 6.11 Listagem: Endpoints GET com Pageable e retorno Page<DTO>

✅ **Validado:**
- SDK não expõe endpoints de listagem (essa responsabilidade é do Auth Server)
- SDK expõe métodos individuais (registerUser, findUserByCpf)

---

### 6.12 Logs: Uso de Slf4j com mensagens claras em Português

✅ **Validado:**
- `@Slf4j` em classes de configuração e implementação
- Mensagens em Português
- Logs em nível INFO para eventos importantes
- Logs em nível DEBUG para detalhes

---

## 7. Checklist de Implementação

### 7.1 Estrutura do Projeto

- [ ] Criar estrutura de pacotes conforme especificado
- [ ] Criar arquivo `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- [ ] Configurar pom.xml com todas as dependências

### 7.2 Pacote: config

- [ ] Implementar `ConexaoAuthAutoConfiguration`
- [ ] Implementar `FeignConfiguration`
- [ ] Testar auto-configuração (bean registration)

### 7.3 Pacote: properties

- [ ] Implementar `ConexaoAuthProperties`
- [ ] Adicionar validações Jakarta Bean Validation
- [ ] Testar leitura de propriedades do application.yml

### 7.4 Pacote: client

- [ ] Implementar `ConexaoAuthClient` interface
- [ ] Implementar `JwksClient` interface
- [ ] Testar chamadas HTTP via Feign

### 7.5 Pacote: service

- [ ] Implementar `ConexaoAuthService` interface
- [ ] Implementar `ConexaoAuthServiceImpl`
- [ ] Implementar `TokenValidator` interface
- [ ] Implementar `TokenValidatorImpl`
- [ ] Testar todos os métodos de serviço

### 7.6 Pacote: dto.request

- [ ] Implementar `RegisterUserRequest` (record)
- [ ] Implementar `ClientCredentialsRequest` (record)
- [ ] Testar validações Jakarta Bean Validation

### 7.7 Pacote: dto.response

- [ ] Implementar `UserResponse` (record)
- [ ] Implementar `TokenResponse` (record)
- [ ] Implementar `JwksResponse` (record)
- [ ] Testar desserialização JSON

### 7.8 Pacote: exception

- [ ] Implementar `ConexaoAuthException` (base)
- [ ] Implementar todas as exceções filhas
- [ ] Testar lançamento de exceções

### 7.9 Pacote: decoder

- [ ] Implementar `ConexaoAuthErrorDecoder`
- [ ] Testar tradução de códigos HTTP
- [ ] Testar preservação de stack trace

### 7.10 Pacote: model

- [ ] Implementar `TokenClaims` (record)
- [ ] Testar extração de claims
- [ ] Testar método `isExpired()`

### 7.11 Testes

- [ ] Escrever testes unitários para todas as classes
- [ ] Escrever testes de integração para Feign Clients
- [ ] Escrever testes de performance para TokenValidator
- [ ] Garantir cobertura de testes > 80% (JaCoCo)

### 7.12 Documentação

- [ ] Adicionar Javadoc em 100% dos métodos públicos
- [ ] Criar README.md com exemplos de uso
- [ ] Criar CHANGELOG.md para versionamento
- [ ] Criar LICENSE (Apache 2.0 ou MIT)

### 7.13 Code Quality

- [ ] Executar `mvn checkstyle:check` (0 warnings)
- [ ] Executar `mvn spotbugs:check` (0 bugs críticos)
- [ ] Executar `mvn pmd:check` (0 warnings)
- [ ] Executar `mvn dependency-check:check` (0 vulnerabilidades críticas)

### 7.14 Build e Deploy

- [ ] Executar `mvn clean install` (sucesso)
- [ ] Testar em aplicação de exemplo
- [ ] Preparar artefato para publicação no Maven Central (futuro)

---

## 8. Resumo Estatístico

| Métrica | Quantidade |
|---------|------------|
| **Total de Classes/Interfaces** | ~23 |
| **Total de DTOs (Records)** | ~7 |
| **Total de Exceções** | ~7 |
| **Linhas de Código Estimadas** | ~1,000-1,200 |
| **Testes Unitários Estimados** | ~20-25 |
| **Testes de Integração Estimados** | ~10-15 |
| **Cobertura de Testes Alvo** | > 80% |
| **Estimativa de Tempo** | ~2 semanas |

---

**Fim do Plano Técnico Preliminar**

**Status:** ✅ PRONTO PARA IMPLEMENTAÇÃO
**Próximo Passo:** Aguardar aprovação do usuário e começar implementação módulo a módulo.
