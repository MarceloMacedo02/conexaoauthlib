# ConexãoAuthLib

[![Java Version](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/spring--cloud-2023.0.0-brightgreen)](https://spring.io/projects/spring-cloud)
[![Resilience4j](https://img.shields.io/badge/resilience4j-2.2.0-blue)](https://resilience4j.readme.io/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

**ConexãoAuthLib** é uma biblioteca Java 21 que oferece clientes Feign declarativos para APIs de autenticação OAuth2 e Identity. A biblioteca expõe uma API Fluent Builder intuitiva que abstrai completamente o transporte HTTP, enquanto garante resiliência robusta através de Resilience4j.

Esta biblioteca foi projetada para simplificar a integração com servidores OAuth2 e Identity, eliminando a necessidade de escrever código boilerplate para comunicação HTTP, tratamento de erros e padrões de resiliência. Com uma interface fluente e type-safe, você pode se concentrar na lógica de negócio enquanto a biblioteca cuida de toda a complexidade de comunicação com os serviços de autenticação.

---

## 📋 Índice

1. [Introdução](#1-introdução)
2. [Recursos Principais](#2-recursos-principais)
3. [Instalação](#3-instalação)
4. [Guia de Início Rápido](#4-guia-de-início-rápido)
5. [Configuração](#5-configuração)
6. [API OAuth2](#6-api-oauth2)
7. [API Tenant](#7-api-tenant)
8. [API Client](#8-api-client)
9. [API User](#9-api-user)
10. [API Role e Scope](#10-api-role-e-scope)
11. [Configuração de Resiliência](#11-configuração-de-resiliência)
12. [Tratamento de Erros](#12-tratamento-de-erros)
13. [Multi-Tenancy](#13-multi-tenancy)
14. [Monitoramento e Métricas](#14-monitoramento-e-métricas)
15. [Testes](#15-testes)
16. [Contribuição](#16-contribuição)
17. [Licença](#17-licença)

---

## 1. Introdução

ConexãoAuthLibresolve um problema comum em aplicações modernas: a integração complexa com serviços de autenticação OAuth2 e Identity. Historicamente, desenvolvedores precisavam escrever código repetitivo para realizar requisições HTTP, implementar circuit breakers manualmente, lidar com retries e tratar uma variedade de erros específicos de autenticação. Esta biblioteca elimina toda essa complexidade através de uma API unificada e bem projetada.

A biblioteca é construída sobre tecnologias consolidadas no ecossistema Java: Spring Cloud OpenFeign para comunicação HTTP declarativa, Resilience4j para padrões de resiliência, e Spring Boot 3.2+ para integração automática. Essa combinação garante não apenas facilidade de uso, mas também desempenho otimizado e confiabilidade comprovada em ambientes de produção.

O design da API foi cuidadosamente pensado para oferecer uma experiência de desenvolvimento agradável. Utilizando o padrão Fluent Builder, cada operação pode ser configurada de forma encadeada e legível, com autocompletion completo em IDEs modernas. Todos os DTOs (Data Transfer Objects) são tipados, garantindo segurança em tempo de compilação e reduzindo erros durante o desenvolvimento.

### 1.1 Quando usar ConexãoAuthLib

Esta biblioteca é ideal para aplicações que precisam integrar-se com servidores OAuth2 ou Identity para qualquer um dos seguintes propósitos: obtenção de tokens de acesso para autenticação de APIs, gerenciamento de tenants multi-tenant, registro e administração de clientes OAuth2, gerenciamento de usuários e suas permissões, e implementação de controles de acesso baseados em roles e scopes. Se sua aplicação precisa de qualquer uma dessas funcionalidades, ConexãoAuthLib pode acelerar significativamente seu desenvolvimento.

---

## 2. Recursos Principais

A biblioteca oferece um conjunto abrangente de recursos projetados para atender às necessidades mais comuns de integração com sistemas de autenticação. Cada recurso foi implementado com foco em usabilidade, performance e confiabilidade.

A **API Fluent Builder** representa o coração da experiência do desenvolvedor. Através de uma interface fluente e intuitiva, você pode construir requisições complexas de forma legível e concisa. O encadeamento de métodos permite configurar todos os aspectos de uma operação em uma única linha de código, enquanto o sistema de tipos garante que apenas configurações válidas sejam aceitas em tempo de compilação.

O suporte completo a **todos os fluxos OAuth2** inclui Client Credentials para autenticação máquina-à-máquina, Password Grant para obtenção de tokens em nome de usuários específicos (com as devidas considerações de segurança), Refresh Token para renovação automática de tokens expirados, além de operações de introspecção e revogação de tokens. Cada fluxo foi implementado seguindo rigorosamente as especificações RFC 6749 e RFC 7009.

A **resiliência integrada** através de Resilience4j oferece proteção contra falhas em cascata e degradação gradual de serviço. Circuit breakers previnem que sua aplicação continue tentando acessar serviços temporariamente indisponíveis, enquanto retries inteligentes com exponential backoff aumentam a probabilidade de sucesso em situações de instabilidade transitória. Todas essas proteções são configuráveis por cliente, permitindo ajustes finos conforme as necessidades específicas de cada integração.

O **suporte nativo a multi-tenancy** é essencial para aplicações SaaS e arquiteturas modernas. A biblioteca gerencia automaticamente o header X-Tenant-Id em todas as requisições, permitindo que você opere múltiplos tenants em um único cliente HTTP. Isso simplifica significativamente a implementação de sistemas multi-tenant, eliminando a necessidade de gerenciar headers manualmente ou criar instâncias separadas de clientes para cada tenant.

---

## 3. Instalação

### 3.1 Pré-requisitos

Antes de adicionar ConexãoAuthLib ao seu projeto, certifique-se de que seu ambiente atende aos seguintes requisitos mínimos. A biblioteca foi desenvolvida utilizando as últimas funcionalidades do Java 21, portanto, é essencial ter o JDK 21 ou superior instalado. Para projetos Spring Boot, a versão 3.2.0 ou superior é obrigatória devido à dependência de recursos específicos do Spring Framework 6.x. Maven 3.8+ ou Gradle 8+ são necessários para gerenciar as dependências corretamente.

### 3.2 Maven

Para projetos Maven, adicione a seguinte dependência ao seu arquivo `pom.xml`. A configuração completa inclui todas as dependências transitivas necessárias, não sendo preciso adicionar explicitamente Spring Cloud OpenFeign ou Resilience4j.

```xml
<dependencies>
    <dependency>
        <groupId>com.conexaoauthlib</groupId>
        <artifactId>conexaoauthlib</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### 3.3 Gradle

Para projetos Gradle, adicione a dependência ao seu arquivo `build.gradle` ou `build.gradle.kts`. O plugin Spring Boot gerencia automaticamente as versões das dependências transitivas.

```groovy
dependencies {
    implementation 'com.conexaoauthlib:conexaoauthlib:1.0.0'
}
```

```kotlin
dependencies {
    implementation("com.conexaoauthlib:conexaoauthlib:1.0.0")
}
```

### 3.4 Spring Boot Auto-configuration

Uma vez adicionada a dependência, a biblioteca configura-se automaticamente através do mecanismo de auto-configuration do Spring Boot. Basta adicionar a dependência ao seu classpath e as configurações padrão serão aplicadas. Você pode personalizar qualquer configuração através do arquivo `application.yml` conforme descrito na próxima seção.

---

## 4. Guia de Início Rápido

Este guia apresenta um exemplo completo de como utilizar a biblioteca para os cenários mais comuns. O exemplo demonstra a obtenção de um token OAuth2, que é a operação fundamental para qualquer integração com serviços de autenticação.

### 4.1 Exemplo Completo Minimal

O código abaixo demonstra como obter um token de acesso utilizando o fluxo Client Credentials, que é adequado para autenticação de serviços e aplicações. Este fluxo não envolve usuários finais, sendo ideal para comunicação entre sistemas.

```java
package com.exemplo;

import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;

public class QuickStartExample {
    
    public static void main(String[] args) {
        // Exemplo 1: Client Credentials Grant (para serviços/APIs)
        // Este fluxo é usado quando a aplicação precisa de um token
        // em nome próprio, sem representar um usuário específico
        
        try {
            TokenResponseDTO token = AuthClient.clientCredentials("meu-servico", "senha-secreta")
                .scope("read write")
                .tenantId("tenant-empresa-x")
                .execute();
            
            System.out.println("Token de Acesso: " + token.getAccessToken());
            System.out.println("Tipo do Token: " + token.getTokenType());
            System.out.println("Expira em: " + token.getExpiresIn() + " segundos");
            System.out.println("Refresh Token: " + token.getRefreshToken());
            
        } catch (Exception e) {
            System.err.println("Erro ao obter token: " + e.getMessage());
        }
    }
}
```

### 4.2 Diferença entre Fluxos OAuth2

É fundamental entender a diferença entre os fluxos disponíveis para escolher o mais adequado para cada situação. O **Client Credentials Grant** é utilizado quando sua aplicação precisa de um token para si mesma, sem representar um usuário final. Este é o fluxo típico para APIs de serviço para serviço, microsserviços comunicando-se entre si, e scripts de automação. O token obtido pertence à aplicação, não a um usuário específico.

O **Password Grant** (Resource Owner Password Credentials) é utilizado quando você precisa de um token em nome de um usuário específico. Este fluxo requer que o usuário forneça suas credenciais (usuário e senha) diretamente à aplicação. Devido a considerações de segurança, este fluxo é recomendado apenas para aplicações de primeira-party onde o usuário confia completamente na aplicação com suas credenciais. Como o token pertence ao usuário, ele terá as permissões e roles atribuídas àquele usuário específico.

### 4.3 Exemplo com Password Grant

O código abaixo demonstra como obter um token para um usuário específico utilizando o fluxo Password Grant. Note que, diferentemente do Client Credentials, você deve fornecer as credenciais do usuário além das credenciais do cliente.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;

// Exemplo: Obter token para um usuário específico
TokenResponseDTO userToken = AuthClient.password("joao.silva@empresa.com", "senha-do-usuario123")
    .clientCredentials("meu-servico", "senha-secreta-do-servico")
    .scope("read write profile")
    .tenantId("tenant-empresa-x")
    .execute();

System.out.println("Token do Usuário: " + userToken.getAccessToken());
System.out.println("Token pode ser usado para acessar recursos em nome do usuário João Silva");
```

### 4.4 Registro de Tenant

Para aplicações multi-tenant, o primeiro passo é criar os tenants que utilizarão seu sistema. O código abaixo demonstra como registrar um novo tenant com produtos associados.

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

// Criar tenant com produtos
TenantResponseDTO tenant = TenantClient.create()
    .name("Empresa Exemplo Ltda")
    .documentNumber("12345678000100")
    .product("premium")
    .product("analytics")
    .product("support")
    .executeWithProducts();

System.out.println("Tenant criado com sucesso!");
System.out.println("  ID: " + tenant.getId());
System.out.println("  Nome: " + tenant.getName());
System.out.println("  Status: " + tenant.getStatus());
System.out.println("  Produtos: " + tenant.getProducts());
```

---

## 5. Configuração

### 5.1 Configuração Básica

A configuração da biblioteca é feita inteiramente através do arquivo `application.yml` do Spring Boot. A estrutura de configuração é organizada por cliente, permitindo que cada serviço OAuth2/Identity tenha suas próprias definições de URL, timeouts e comportamento de resiliência.

```yaml
conexao-auth:
  default:
    base-url: "http://localhost:8080"
    connect-timeout: 5000
    read-timeout: 10000
    max-retries: 3
    resilience:
      enabled: true
  
  clients:
    oauth2:
      name: oauth2
      base-url: "${conexao-auth.default.base-url}/oauth2"
    tenant:
      name: tenant
      base-url: "${conexao-auth.default.base-url}/api/tenants"
    client:
      name: client
      base-url: "${conexao-auth.default.base-url}/api/clients"
    user:
      name: user
      base-url: "${conexao-auth.default.base-url}/api/users"
    role:
      name: role
      base-url: "${conexao-auth.default.base-url}/api/roles"
    scope:
      name: scope
      base-url: "${conexao-auth.default.base-url}/api/scopes"
```

### 5.2 Opções de Configuração por Cliente

Cada cliente pode ser configurado independentemente com as seguintes opções. O parâmetro `base-url` define a URL base do serviço, aceita tanto strings fixas quanto expressões SpEL. Os timeouts de conexão e leitura são especificados em milissegundos, permitindo ajuste fino para diferentes cenários de rede. A habilitação de resiliência pode ser desativada por cliente em situações específicas onde você não deseja os overheads de circuit breaker e retry.

### 5.3 Configuração de Resiliência

As configurações de resiliência são gerenciadas pelo Resilience4j através de seu arquivo de configuração dedicado. Por conveniência, a biblioteca inclui um arquivo `resilience4j.yml` com configurações padrão sensíveis que funcionam bem para a maioria dos casos de uso. Você pode sobrescrever essas configurações no seu `application.yml`.

```yaml
# application.yml - Sobrescrevendo configurações de resiliência
resilience4j:
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      oauth2:
        failureRateThreshold: 30
        waitDurationInOpenState: 60s
      tenant:
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
  
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 500ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
    instances:
      oauth2:
        maxAttempts: 5
        waitDuration: 1s
```

---

## 6. API OAuth2

A API OAuth2 é o núcleo da biblioteca, fornecendo métodos para todos os fluxos de autenticação OAuth2. Cada fluxo é exposto através de métodos factory na classe `AuthClient` que retornam builders configuráveis.

### 6.1 Client Credentials Grant

O fluxo Client Credentials é o mais simples e direto, utilizado para autenticação de máquina para máquina. Não envolve usuário final, sendo ideal para comunicação entre serviços, APIs backend, e processos automatizados.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.exception.InvalidClientException;

public class OAuth2Examples {
    
    public static void clientCredentialsBasic() {
        // Exemplo básico - apenas credenciais e escopos
        TokenResponseDTO token = AuthClient.clientCredentials("api-client", "super-secreto")
            .scope("read write")
            .execute();
        
        System.out.println("Access Token: " + token.getAccessToken());
    }
    
    public static void clientCredentialsWithTenant() {
        // Exemplo com tenant - para sistemas multi-tenant
        TokenResponseDTO token = AuthClient.clientCredentials("api-client", "super-secreto")
            .scope("read write admin")
            .tenantId("tenant-abc-123")
            .execute();
        
        System.out.println("Token para tenant específico: " + token.getAccessToken());
    }
    
    public static void clientCredentialsWithErrorHandling() {
        // Exemplo com tratamento de erros
        try {
            TokenResponseDTO token = AuthClient.clientCredentials("cliente-invalido", "senha-errada")
                .scope("read")
                .execute();
            
            System.out.println("Token: " + token.getAccessToken());
            
        } catch (InvalidClientException e) {
            System.err.println("Erro de autenticação: " + e.getMessage());
            System.err.println("Código do erro: " + e.getErrorCode());
            // Ações de recuperação: notificar admin, usar credenciais alternativas, etc.
        }
    }
}
```

### 6.2 Password Grant (Resource Owner Password Credentials)

O fluxo Password Grant permite obter tokens em nome de usuários específicos. Este fluxo requer que o usuário confie sua senha à aplicação, portanto, deve ser usado apenas em situações apropriadas, tipicamente aplicações de primeira-party onde o usuário é o proprietário da aplicação.

**⚠️ Importante:** O Password Grant é considerado menos seguro que outros fluxos OAuth2. Considere usar Authorization Code Grant ou PKCE para aplicações que lidam com usuários finais. O Password Grant é mais adequado para migrações legadas ou situações específicas onde outros fluxos não são viáveis.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;

public class PasswordGrantExamples {
    
    public static void passwordGrantBasic() {
        // Obter token para um usuário específico
        // O token terá as permissões do USUÁRIO, não apenas do cliente
        TokenResponseDTO userToken = AuthClient.password("joao@empresa.com", "senha-usuario123")
            .clientCredentials("minha-app", "segredo-app")
            .scope("profile read write")
            .tenantId("tenant-xyz")
            .execute();
        
        System.out.println("Token do usuário: " + userToken.getAccessToken());
        System.out.println("Este token pertence ao usuário João e tem suas permissões");
    }
    
    public static void passwordGrantMinimal() {
        // Versão mínima - apenas o necessário
        TokenResponseDTO token = AuthClient.password("email@usuario.com", "senha")
            .clientCredentials("client-id", "client-secret")
            .execute();
        
        System.out.println("Token: " + token.getAccessToken());
    }
}
```

### 6.3 Refresh Token

Quando um token de acesso expira, ao invés de solicitar novas credenciais ao usuário, você pode usar o refresh token para obter um novo access token. O refresh token tem validade maior e pode ser usado para manter sessões ativas sem interrupção.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.exception.InvalidGrantException;

public class RefreshTokenExamples {
    
    public static void refreshAccessToken() {
        // Assume que você armazenou o refresh token anteriormente
        String storedRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        
        // Usar refresh token para obter novo access token
        TokenResponseDTO newToken = AuthClient.refreshToken(storedRefreshToken)
            .clientCredentials("api-client", "api-secret")
            .execute();
        
        System.out.println("Novo Access Token: " + newToken.getAccessToken());
        System.out.println("Novo Refresh Token: " + newToken.getRefreshToken());
        
        // Importante: store o novo refresh token, pois pode ter sido rotacionado
    }
    
    public static void tokenRefreshFlow() {
        // Exemplo de fluxo completo de gerenciamento de token
        // 1. Obter token inicial
        TokenResponseDTO initialToken = AuthClient.clientCredentials("api-client", "api-secret")
            .scope("read write")
            .execute();
        
        // 2. Armazenar refresh token com segurança
        String refreshToken = initialToken.getRefreshToken();
        
        // 3. Verificar se token expirou (em produção, verificar antes de cada requisição)
        // Quando expirado...
        if (refreshToken != null) {
            try {
                TokenResponseDTO renewedToken = AuthClient.refreshToken(refreshToken)
                    .clientCredentials("api-client", "api-secret")
                    .execute();
                
                // 4. Atualizar refresh token armazenado
                refreshToken = renewedToken.getRefreshToken();
                System.out.println("Token renovado com sucesso");
                
            } catch (InvalidGrantException e) {
                // Refresh token expirado ou revogado - usuário precisa fazer login novamente
                System.err.println("Refresh token expirado, reautenticação necessária");
            }
        }
    }
}
```

### 6.4 Introspecção de Token

A API de introspecção permite verificar a validade e os detalhes de um token. Isso é útil para validação de tokens em APIs deResource Server, auditorias de segurança, e debugging de problemas de autenticação.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.IntrospectResponseDTO;

public class IntrospectExamples {
    
    public static void validateToken() {
        String accessToken = "token-a-ser-validado";
        
        IntrospectResponseDTO info = AuthClient.introspect(accessToken)
            .tenantId("tenant-123")
            .execute();
        
        if (info.getActive()) {
            System.out.println("✓ Token válido");
            System.out.println("  Subject (usuário): " + info.getSub());
            System.out.println("  Client ID: " + info.getClientId());
            System.out.println("  Scopes: " + info.getScopes());
            System.out.println("  Expira em (timestamp): " + info.getExp());
            System.out.println("  Issuer: " + info.getIss());
        } else {
            System.out.println("✗ Token inválido ou expirado");
            System.out.println("  Motivo: " + (info.getActive() == false ? "expirado ou revogado" : "desconhecido"));
        }
    }
    
    public static void tokenDetails() {
        // Obter detalhes completos do token
        IntrospectResponseDTO info = AuthClient.introspect("access-token")
            .execute();
        
        // Campos disponíveis na resposta
        System.out.println("Token ativo: " + info.getActive());
        System.out.println("Subject: " + info.getSub());
        System.out.println("Client ID: " + info.getClientId());
        System.out.println("Escopos: " + info.getScopes());
        System.out.println("Issuer: " + info.getIss());
        System.out.println("Audience: " + info.getAud());
        System.out.println("Expira em: " + info.getExp());
        System.out.println("Issued at: " + info.getIat());
        System.out.println("Scopes como array: " + info.getScope());
    }
}
```

### 6.5 Revogação de Token

A revogação permite invalidar um token antes de sua expiração natural. Isso é essencial para logout de usuários, revogação de acessos comprometidos, e implementação de políticas de segurança.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;

public class RevokeExamples {
    
    public static void revokeAccessToken() {
        // Revogar um access token
        String accessToken = "token-a-ser-revogado";
        
        AuthClient.revoke(accessToken)
            .tenantId("tenant-123")
            .execute();
        
        System.out.println("Token revogado com sucesso");
        // O token agora é inválido e não pode mais ser usado
    }
    
    public static void revokeOnLogout() {
        // Exemplo de fluxo de logout completo
        String accessToken = getStoredAccessToken();
        String refreshToken = getStoredRefreshToken();
        
        // Revogar ambos os tokens
        AuthClient.revoke(accessToken).execute();
        
        if (refreshToken != null) {
            AuthClient.revoke(refreshToken).execute();
        }
        
        // Limpar tokens armazenados localmente
        clearStoredTokens();
        System.out.println("Logout realizado com sucesso");
    }
    
    private static String getStoredAccessToken() {
        // Implementação depends de seu storage
        return null;
    }
    
    private static String getStoredRefreshToken() {
        // Implementação depends de seu storage
        return null;
    }
    
    private static void clearStoredTokens() {
        // Implementação depends de seu storage
    }
}
```

---

## 7. API Tenant

A API Tenant permite gerenciar tenants em sistemas multi-tenant. Cada tenant representa uma organização ou cliente separado que utiliza o sistema de forma isolada.

### 7.1 Criar Tenant

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

public class TenantExamples {
    
    public static void createSimpleTenant() {
        // Criar tenant sem produtos
        TenantResponseDTO tenant = TenantClient.create()
            .name("Empresa Exemplo Ltda")
            .documentNumber("12345678000100")
            .execute();
        
        System.out.println("Tenant criado: " + tenant.getName());
        System.out.println("ID: " + tenant.getId());
        System.out.println("Status: " + tenant.getStatus());
    }
    
    public static void createTenantWithProducts() {
        // Criar tenant com produtos associados
        TenantResponseDTO tenant = TenantClient.create()
            .name("Corporação ABC")
            .documentNumber("98765432000100")
            .product("premium")
            .product("analytics")
            .product("support")
            .product("integrations")
            .executeWithProducts();
        
        System.out.println("Tenant criado com produtos:");
        System.out.println("  Nome: " + tenant.getName());
        System.out.println("  Produtos: " + tenant.getProducts());
    }
}
```

### 7.2 Listar e Buscar Tenants

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

public class TenantListExamples {
    
    public static void listTenantsWithFilters() {
        // Listar tenants com filtros
        PageResponseDTO<TenantResponseDTO> page = TenantClient.list()
            .status("ACTIVE")
            .name("Empresa")
            .page(0)
            .size(20)
            .execute();
        
        System.out.println("Total de tenants: " + page.getTotalElements());
        System.out.println("Página: " + (page.getNumber() + 1) + " de " + page.getTotalPages());
        
        page.getContent().forEach(tenant -> 
            System.out.println("  - " + tenant.getName() + " (" + tenant.getStatus() + ")")
        );
    }
    
    public static void findByDocument() {
        // Buscar tenant pelo documento (CNPJ/CPF)
        TenantResponseDTO tenant = TenantClient.findByDocument("12345678000100")
            .execute();
        
        System.out.println("Tenant encontrado: " + tenant.getName());
    }
    
    public static void findById() {
        // Buscar tenant pelo ID
        TenantResponseDTO tenant = TenantClient.get("tenant-id-123")
            .execute();
        
        System.out.println("Tenant: " + tenant.getName());
        System.out.println("Documento: " + tenant.getDocumentNumber());
        System.out.println("Status: " + tenant.getStatus());
        System.out.println("Produtos: " + tenant.getProducts());
    }
}
```

### 7.3 Atualizar Status de Tenant

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

public class TenantStatusExamples {
    
    public static void suspendTenant() {
        // Suspender tenant
        TenantResponseDTO suspended = TenantClient.updateStatus("tenant-id")
            .status("SUSPENDED")
            .reason("Pagamento pendente")
            .execute();
        
        System.out.println("Tenant suspenso: " + suspended.getStatus());
    }
    
    public static void activateTenant() {
        // Reativar tenant
        TenantResponseDTO activated = TenantClient.updateStatus("tenant-id")
            .status("ACTIVE")
            .reason("Pagamento confirmado")
            .execute();
        
        System.out.println("Tenant reativado: " + activated.getStatus());
    }
    
    public static void deactivateTenant() {
        // Desativar tenant (soft delete)
        TenantResponseDTO deactivated = TenantClient.updateStatus("tenant-id")
            .status("INACTIVE")
            .reason("Solicitação do cliente")
            .execute();
        
        System.out.println("Tenant desativado: " + deactivated.getStatus());
    }
}
```

### 7.4 Gerenciar Produtos do Tenant

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantProductDTO;

public class TenantProductsExamples {
    
    public static void addProducts() {
        // Adicionar produtos ao tenant
        TenantResponseDTO updated = TenantClient.addProducts("tenant-id")
            .product("new-premium-feature")
            .product("enterprise-support")
            .execute();
        
        System.out.println("Produtos adicionados: " + updated.getProducts());
    }
    
    public static void removeProducts() {
        // Remover produtos do tenant
        TenantResponseDTO updated = TenantClient.removeProducts("tenant-id")
            .product("analytics")
            .execute();
        
        System.out.println("Produtos restantes: " + updated.getProducts());
    }
    
    public static void listCurrentProducts() {
        // Obter tenant com produtos atuais
        TenantResponseDTO tenant = TenantClient.get("tenant-id")
            .execute();
        
        System.out.println("Produtos do tenant " + tenant.getName() + ":");
        for (String product : tenant.getProducts()) {
            System.out.println("  - " + product);
        }
    }
}
```

### 7.5 Excluir Tenant

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;

public class TenantDeleteExamples {
    
    public static void deleteTenant() {
        // Excluir tenant (hard delete)
        TenantClient.delete("tenant-id")
            .execute();
        
        System.out.println("Tenant excluído permanentemente");
    }
}
```

---

## 8. API Client

A API Client permite registrar e gerenciar clientes OAuth2. Cada cliente representa uma aplicação que pode solicitar tokens de acesso.

### 8.1 Registrar Client

```java
import com.conexaoauthlib.fluent.client.ClientClient;
import com.conexaoauthlib.dto.client.ClientResponseDTO;

public class ClientExamples {
    
    public static void registerClientWithSecret() {
        // Registrar cliente com segredo fornecido
        ClientResponseDTO client = ClientClient.register()
            .clientId("meu-servico-api")
            .clientSecret("segredo-seguro-gerado-previamente")
            .name("Minha API de Serviço")
            .tenantId("tenant-123")
            .grantType("client_credentials")
            .grantType("refresh_token")
            .scopes("read", "write", "admin")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(86400)
            .execute();
        
        System.out.println("Client registrado:");
        System.out.println("  ID: " + client.getId());
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Status: " + client.getStatus());
        System.out.println("  Grant Types: " + client.getGrantTypes());
        System.out.println("  Scopes: " + client.getScopes());
        
        // ⚠️ ARMAZENAR O CLIENT SECRET COM SEGURANÇA!
        // Não será possível recuperá-lo depois!
    }
    
    public static void registerClientAutoSecret() {
        // Registrar cliente - segredo gerado automaticamente
        ClientResponseDTO client = ClientClient.register()
            .clientId("servico-autonomo")
            .name("Serviço Autônomo")
            .tenantId("tenant-123")
            .grantType("client_credentials")
            .scopes("read", "write")
            .execute();
        
        System.out.println("Client registrado:");
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Client Secret: " + client.getClientSecret());
        
        // ⚠️ GUARDE O SEGREDO IMEDIATAMENTE!
        // Não será mostrado novamente!
    }
}
```

### 8.2 Rotacionar Segredo

A rotação regular de segredos é uma prática de segurança recomendada. Quando você rotaciona o segredo, o segredo antigo permanece válido até sua expiração, dando tempo para que todos os serviços sejam atualizados.

```java
import com.conexaoauthlib.fluent.client.ClientClient;
import com.conexaoauthlib.dto.client.ClientSecretResponseDTO;

public class ClientSecretExamples {
    
    public static void rotateSecret() {
        // Rotacionar segredo do cliente
        ClientSecretResponseDTO result = ClientClient.rotateSecret("client-id")
            .execute();
        
        System.out.println("Novo segredo gerado:");
        System.out.println("  Client ID: " + result.getClientId());
        System.out.println("  Novo Segredo: " + result.getNewSecret());
        System.out.println("  Expira em: " + result.getExpiresAt());
        
        // ⚠️ O segredo antigo ainda é válido até expiresAt!
        // Atualize todos os serviços que usam o segredo antigo!
    }
    
    public static void secretRotationProcess() {
        // Processo completo de rotação de segredo
        String clientId = "client-id";
        
        // 1. Gerar novo segredo
        ClientSecretResponseDTO newSecret = ClientClient.rotateSecret(clientId)
            .execute();
        
        // 2. Atualizar serviços gradualmente
        // Durante o período de transição, ambos os segredos são aceitos
        
        System.out.println("Novo segredo: " + newSecret.getNewSecret());
        System.out.println("Segredo antigo expira em: " + newSecret.getExpiresAt());
        System.out.println("Atualize todos os serviços durante este período!");
    }
}
```

### 8.3 Listar e Buscar Clients

```java
import com.conexaoauthlib.fluent.client.ClientClient;
import com.conexaoauthlib.dto.client.ClientResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

public class ClientListExamples {
    
    public static void listClients() {
        // Listar clients do tenant
        PageResponseDTO<ClientResponseDTO> page = ClientClient.list()
            .tenantId("tenant-123")
            .status("ACTIVE")
            .page(0)
            .size(20)
            .execute();
        
        System.out.println("Total de clients: " + page.getTotalElements());
        page.getContent().forEach(client ->
            System.out.println("  - " + client.getClientId() + " (" + client.getStatus() + ")")
        );
    }
    
    public static void findClientById() {
        // Buscar client pelo ID interno
        ClientResponseDTO client = ClientClient.getById("client-id")
            .execute();
        
        System.out.println("Client encontrado:");
        System.out.println("  ID: " + client.getId());
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Nome: " + client.getName());
        System.out.println("  Scopes: " + client.getScopes());
        System.out.println("  Status: " + client.getStatus());
    }
}
```

### 8.4 Atualizar Status de Client

```java
import com.conexaoauthlib.fluent.client.ClientClient;
import com.conexaoauthlib.dto.client.ClientResponseDTO;

public class ClientStatusExamples {
    
    public static void suspendClient() {
        // Suspender client
        ClientResponseDTO suspended = ClientClient.updateStatus("client-id")
            .status("SUSPENDED")
            .execute();
        
        System.out.println("Client suspenso: " + suspended.getStatus());
    }
    
    public static void activateClient() {
        // Reativar client
        ClientResponseDTO activated = ClientClient.updateStatus("client-id")
            .status("ACTIVE")
            .execute();
        
        System.out.println("Client ativado: " + activated.getClientId());
    }
}
```

### 8.5 Excluir Client

```java
import com.conexaoauthlib.fluent.client.ClientClient;

public class ClientDeleteExamples {
    
    public static void deleteClient() {
        // Excluir client
        ClientClient.delete("client-id")
            .execute();
        
        System.out.println("Client excluído com sucesso!");
    }
}
```

---

## 9. API User

A API User permite gerenciar usuários dentro de tenants específicos.

### 9.1 Registrar Usuário

```java
import com.conexaoauthlib.fluent.user.UserClient;
import com.conexaoauthlib.dto.user.UserResponseDTO;

public class UserExamples {
    
    public static void registerUser() {
        // Registrar novo usuário
        UserResponseDTO user = UserClient.register()
            .name("João Silva Santos")
            .email("joao.silva@empresa.com")
            .password("Senha123!@#")
            .tenantId("tenant-123")
            .execute();
        
        System.out.println("Usuário criado:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Nome: " + user.getName());
        System.out.println("  Email: " + user.getEmail());
        System.out.println("  Status: " + user.getStatus());
    }
}
```

### 9.2 Listar e Buscar Usuários

```java
import com.conexaoauthlib.fluent.user.UserClient;
import com.conexaoauthlib.dto.user.UserResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

public class UserListExamples {
    
    public static void listUsers() {
        // Listar usuários do tenant
        PageResponseDTO<UserResponseDTO> page = UserClient.list()
            .tenantId("tenant-123")
            .status("ACTIVE")
            .page(0)
            .size(20)
            .execute();
        
        System.out.println("Total de usuários: " + page.getTotalElements());
        page.getContent().forEach(user ->
            System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")")
        );
    }
    
    public static void findById() {
        // Buscar usuário pelo ID
        UserResponseDTO user = UserClient.get("user-id")
            .execute();
        
        System.out.println("Usuário encontrado:");
        System.out.println("  Nome: " + user.getName());
        System.out.println("  Email: " + user.getEmail());
        System.out.println("  Status: " + user.getStatus());
    }
    
    public static void findByEmail() {
        // Buscar usuário pelo email (via listagem com filtro)
        PageResponseDTO<UserResponseDTO> results = UserClient.list()
            .email("joao.silva@empresa.com")
            .tenantId("tenant-123")
            .execute();
        
        if (!results.getContent().isEmpty()) {
            UserResponseDTO user = results.getContent().get(0);
            System.out.println("Usuário encontrado: " + user.getName());
        } else {
            System.out.println("Usuário não encontrado");
        }
    }
}
```

### 9.3 Atualizar Dados do Usuário

```java
import com.conexaoauthlib.fluent.user.UserClient;
import com.conexaoauthlib.dto.user.UserResponseDTO;

public class UserUpdateExamples {
    
    public static void updateUser() {
        // Atualizar dados do usuário
        UserResponseDTO updated = UserClient.update("user-id")
            .name("João Silva Santos Filho")
            .email("joao.santos@empresa.com")
            .execute();
        
        System.out.println("Usuário atualizado:");
        System.out.println("  Nome: " + updated.getName());
        System.out.println("  Email: " + updated.getEmail());
    }
}
```

### 9.4 Alterar Senha

```java
import com.conexaoauthlib.fluent.user.UserClient;

public class PasswordExamples {
    
    public static void changePassword() {
        // Alterar senha do usuário
        UserClient.changePassword("user-id")
            .currentPassword("SenhaAntiga123!")
            .newPassword("NovaSenha456!@#")
            .execute();
        
        System.out.println("Senha alterada com sucesso!");
    }
}
```

### 9.5 Ativar e Desativar Usuário

```java
import com.conexaoauthlib.fluent.user.UserClient;
import com.conexaoauthlib.dto.user.UserResponseDTO;

public class UserStatusExamples {
    
    public static void deactivateUser() {
        // Desativar usuário
        UserClient.deactivate("user-id")
            .execute();
        
        System.out.println("Usuário desativado!");
    }
    
    public static void activateUser() {
        // Reativar usuário
        UserResponseDTO activated = UserClient.updateStatus("user-id")
            .status("ACTIVE")
            .execute();
        
        System.out.println("Usuário reativado: " + activated.getName());
    }
}
```

### 9.6 Excluir Usuário

```java
import com.conexaoauthlib.fluent.user.UserClient;

public class UserDeleteExamples {
    
    public static void deleteUser() {
        // Excluir usuário
        UserClient.delete("user-id")
            .execute();
        
        System.out.println("Usuário excluído com sucesso!");
    }
}
```

---

## 10. API Role e Scope

A API Role e Scope permite gerenciar o sistema de autorização baseado em permissões granulares.

### 10.1 Criar Scopes

Scopes representam permissões granulares que podem ser atribuídas a roles. Cada scope define uma ação específica sobre um recurso.

```java
import com.conexaoauthlib.fluent.scope.ScopeClient;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;

public class ScopeExamples {
    
    public static void createScopes() {
        // Criar scope para leitura
        ScopeResponseDTO readUsers = ScopeClient.create()
            .name("users:read")
            .description("Permissão para leitura de dados de usuários")
            .resource("users")
            .action("read")
            .execute();
        
        // Criar scope para escrita
        ScopeResponseDTO writeUsers = ScopeClient.create()
            .name("users:write")
            .description("Permissão para criação e edição de usuários")
            .resource("users")
            .action("write")
            .execute();
        
        // Criar scope para deleção
        ScopeResponseDTO deleteUsers = ScopeClient.create()
            .name("users:delete")
            .description("Permissão para exclusão de usuários")
            .resource("users")
            .action("delete")
            .execute();
        
        System.out.println("Scopes criados:");
        System.out.println("  " + readUsers.getName() + ": " + readUsers.getId());
        System.out.println("  " + writeUsers.getName() + ": " + writeUsers.getId());
        System.out.println("  " + deleteUsers.getName() + ": " + deleteUsers.getId());
    }
}
```

### 10.2 Listar Scopes

```java
import com.conexaoauthlib.fluent.scope.ScopeClient;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;

import java.util.List;

public class ScopeListExamples {
    
    public static void listScopesByResource() {
        // Listar scopes de um recurso específico
        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .resource("users")
            .execute();
        
        System.out.println("Scopes de 'users':");
        scopes.forEach(scope ->
            System.out.println("  - " + scope.getName() + ": " + scope.getDescription())
        );
    }
    
    public static void listAllScopes() {
        // Listar todos os scopes
        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .execute();
        
        System.out.println("Total de scopes: " + scopes.size());
        scopes.forEach(scope ->
            System.out.println("  - " + scope.getName() + " (" + scope.getResource() + ":" + scope.getAction() + ")")
        );
    }
    
    public static void findScopeById() {
        // Buscar scope pelo ID
        ScopeResponseDTO scope = ScopeClient.getById("scope-id")
            .execute();
        
        System.out.println("Scope encontrado:");
        System.out.println("  Nome: " + scope.getName());
        System.out.println("  Descrição: " + scope.getDescription());
        System.out.println("  Recurso: " + scope.getResource());
        System.out.println("  Ação: " + scope.getAction());
    }
}
```

### 10.3 Criar Roles

```java
import com.conexaoauthlib.fluent.role.RoleClient;
import com.conexaoauthlib.dto.role.RoleResponseDTO;
import com.conexaoauthlib.fluent.scope.ScopeClient;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;

import java.util.List;

public class RoleExamples {
    
    public static void createRoleWithScopes() {
        // Primeiro, obter os IDs dos scopes
        List<ScopeResponseDTO> userScopes = ScopeClient.list()
            .resource("users")
            .execute();
        
        List<String> scopeIds = userScopes.stream()
            .map(ScopeResponseDTO::getId)
            .toList();
        
        // Criar role com scopes
        RoleResponseDTO adminRole = RoleClient.create()
            .name("admin")
            .description("Administrador com acesso total aos usuários")
            .tenantId("tenant-123")
            .scopeIds(scopeIds)
            .execute();
        
        System.out.println("Role criada:");
        System.out.println("  Nome: " + adminRole.getName());
        System.out.println("  Scopes: " + adminRole.getScopes());
    }
    
    public static void createSimpleRole() {
        // Criar role sem scopes inicialmente
        RoleResponseDTO viewerRole = RoleClient.create()
            .name("viewer")
            .description("Visualizador somente leitura")
            .tenantId("tenant-123")
            .execute();
        
        System.out.println("Role criada:");
        System.out.println("  ID: " + viewerRole.getId());
        System.out.println("  Nome: " + viewerRole.getName());
    }
}
```

### 10.4 Gerenciar Scopes de Roles

```java
import com.conexaoauthlib.fluent.role.RoleClient;
import com.conexaoauthlib.dto.role.RoleResponseDTO;
import com.conexaoauthlib.fluent.scope.ScopeClient;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;

import java.util.List;

public class RoleScopeManagementExamples {
    
    public static void assignScopesToRole() {
        // Obter IDs dos scopes
        List<ScopeResponseDTO> orderScopes = ScopeClient.list()
            .resource("orders")
            .execute();
        
        List<String> scopeIds = orderScopes.stream()
            .map(ScopeResponseDTO::getId)
            .toList();
        
        // Atribuir scopes à role
        RoleResponseDTO updatedRole = RoleClient.assignScopes("role-id")
            .scopeIds(scopeIds)
            .execute();
        
        System.out.println("Scopes atribuídos à role: " + updatedRole.getName());
        System.out.println("Total de scopes: " + updatedRole.getScopes().size());
    }
    
    public static void removeScopesFromRole() {
        // Remover scopes específicos da role
        List<String> scopeIdsToRemove = List.of("scope-id-1", "scope-id-2");
        
        RoleResponseDTO updatedRole = RoleClient.removeScopes("role-id")
            .scopeIds(scopeIdsToRemove)
            .execute();
        
        System.out.println("Scopes removidos da role: " + updatedRole.getName());
        System.out.println("Scopes restantes: " + updatedRole.getScopes().size());
    }
}
```

### 10.5 Listar Roles

```java
import com.conexaoauthlib.fluent.role.RoleClient;
import com.conexaoauthlib.dto.role.RoleResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

public class RoleListExamples {
    
    public static void listRoles() {
        // Listar roles do tenant
        PageResponseDTO<RoleResponseDTO> page = RoleClient.list()
            .tenantId("tenant-123")
            .execute();
        
        System.out.println("Roles do tenant:");
        page.getContent().forEach(role ->
            System.out.println("  - " + role.getName() + ": " + role.getDescription())
        );
    }
    
    public static void listRolesWithScopes() {
        // Listar roles incluindo seus scopes
        PageResponseDTO<RoleResponseDTO> page = RoleClient.list()
            .tenantId("tenant-123")
            .includeScopes(true)
            .execute();
        
        System.out.println("Roles do tenant (com scopes):");
        page.getContent().forEach(role -> {
            System.out.println("  - " + role.getName() + ": " + role.getDescription());
            System.out.println("    Scopes: " + role.getScopes());
        });
    }
    
    public static void findRoleById() {
        // Buscar role pelo ID
        RoleResponseDTO role = RoleClient.getById("role-id")
            .execute();
        
        System.out.println("Role encontrada:");
        System.out.println("  Nome: " + role.getName());
        System.out.println("  Descrição: " + role.getDescription());
        System.out.println("  Scopes: " + role.getScopes());
    }
}
```

### 10.6 Ativar e Desativar Role

```java
import com.conexaoauthlib.fluent.role.RoleClient;
import com.conexaoauthlib.dto.role.RoleResponseDTO;

public class RoleStatusExamples {
    
    public static void deactivateRole() {
        // Desativar role
        RoleClient.deactivate("role-id")
            .execute();
        
        System.out.println("Role desativada!");
    }
    
    public static void activateRole() {
        // Reativar role
        RoleResponseDTO activated = RoleClient.updateStatus("role-id")
            .status("ACTIVE")
            .execute();
        
        System.out.println("Role ativada: " + activated.getName());
    }
}
```

### 10.7 Excluir Role

```java
import com.conexaoauthlib.fluent.role.RoleClient;

public class RoleDeleteExamples {
    
    public static void deleteRole() {
        // Excluir role
        RoleClient.delete("role-id")
            .execute();
        
        System.out.println("Role excluída com sucesso!");
    }
}
```

---

## 11. Configuração de Resiliência

### 11.1 Arquitetura de Resiliência

ConexãoAuthLib utiliza Resilience4j para implementar padrões de resiliência em três camadas: Circuit Breaker para prevenção de falhas em cascata, Retry para tratamento de falhas transitórias, e Bulkhead para isolamento de recursos. Cada cliente pode ter configurações independentes, permitindo ajuste fino conforme as características de cada serviço.

O Circuit Breaker impede que sua aplicação continue fazendo requisições para um serviço que está falhando repetidamente. Após um número configurado de falhas, o circuit breaker "abre" e para de enviar requisições por um período configurado, dando tempo ao serviço para se recuperar. Depois, entra em estado "half-open" para testar se o serviço se recuperou, e finalmente "fecha" novamente quando as requisições começam a ter sucesso.

### 11.2 Configuração de Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        # Taxa de falha (%) para abrir o circuit breaker
        failureRateThreshold: 50
        # Tempo em estado OPEN antes de tentar HALF_OPEN
        waitDurationInOpenState: 30s
        # Número de chamadas permitidas em estado HALF_OPEN
        permittedNumberOfCallsInHalfOpenState: 3
        # Tamanho da janela deslizante para cálculo de taxa de falha
        slidingWindowSize: 10
        # Número mínimo de chamadas antes de calcular taxa
        minimumNumberOfCalls: 5
        # Exceções que contam como falhas
        recordExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.reactive.function.client.WebClientResponseException
    
    instances:
      # Configuração específica para OAuth2 - mais restritivo
      oauth2:
        failureRateThreshold: 30
        waitDurationInOpenState: 60s
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
      
      # Configuração específica para Tenant - moderada
      tenant:
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
```

### 11.3 Configuração de Retry

```yaml
resilience4j:
  retry:
    configs:
      default:
        # Número máximo de tentativas
        maxAttempts: 3
        # Tempo entre tentativas
        waitDuration: 500ms
        # Habilitar exponential backoff
        enableExponentialBackoff: true
        # Multiplicador do exponential backoff
        exponentialBackoffMultiplier: 2
        # Tempo máximo entre retries
        maxWaitDuration: 5s
        # Exceções que acionam retry
        retryExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - java.net.SocketTimeoutException
    
    instances:
      oauth2:
        maxAttempts: 5
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
```

### 11.4 Configuração de Bulkhead

```yaml
resilience4j:
  bulkhead:
    configs:
      default:
        # Número máximo de chamadas concorrentes
        maxConcurrentCalls: 20
        # Tempo máximo de espera por uma permissão
        maxWaitDuration: 500ms
    
    instances:
      oauth2:
        maxConcurrentCalls: 10
        maxWaitDuration: 500ms
```

---

## 12. Tratamento de Erros

### 12.1 Hierarquia de Exceções

ConexãoAuthLib define uma hierarquia de exceções bem estruturada para facilitar o tratamento granular de erros. A classe base `OAuth2Exception` estende `RuntimeException` e adiciona campos para `errorCode` e `httpStatus`, permitindo identificação precisa do tipo de erro.

```java
package com.conexaoauthlib.exception;

/**
 * Exceção base para erros específicos do OAuth2.
 *
 * <p>Esta é a classe pai para todas as exceções relacionadas a operações OAuth2,
 * incluindo erros de autenticação, grants inválidos e problemas de servidor.</p>
 */
public class OAuth2Exception extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public OAuth2Exception(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}
```

### 12.2 Tipos de Exceções

| Exceção | HTTP Status | Descrição | Quando Ocorre |
|---------|-------------|-----------|---------------|
| `OAuth2Exception` | - | Exceção base para todos os erros OAuth2 | Erros genéricos de autenticação |
| `InvalidClientException` | 401 | Cliente OAuth2 inválido | Client ID ou secret incorretos |
| `InvalidGrantException` | 400 | Grant inválido | Grant type não suportado ou parâmetros inválidos |
| `UnauthorizedException` | 401 | Não autorizado | Token ausente, expirado ou insuficiente |
| `ResourceNotFoundException` | 404 | Recurso não encontrado | Tenant, client, user ou role inexistente |
| `ConflictException` | 409 | Conflito de recursos | Recurso já existe ou operação conflituosa |
| `CircuitBreakerOpenException` | 503 | Circuit breaker aberto | Serviço temporariamente indisponível |
| `ServerException` | 500 | Erro interno do servidor | Erro inesperado no servidor |
| `InvalidOperationException` | 400 | Operação inválida | Operação não permitida no estado atual |

### 12.3 Exemplo de Tratamento de Erros

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.exception.*;

public class ErrorHandlingExamples {
    
    public static void comprehensiveErrorHandling() {
        try {
            TokenResponseDTO token = AuthClient.clientCredentials("client-id", "secret")
                .scope("read")
                .execute();
            
            System.out.println("Token obtido: " + token.getAccessToken());
            
        } catch (InvalidClientException e) {
            // Credenciais do cliente inválidas
            System.err.println("❌ Erro de autenticação do cliente");
            System.err.println("   Mensagem: " + e.getMessage());
            System.err.println("   Código: " + e.getErrorCode());
            // Ações: verificar credenciais, notificar admin
            
        } catch (InvalidGrantException e) {
            // Grant inválido
            System.err.println("❌ Grant inválido");
            System.err.println("   Mensagem: " + e.getMessage());
            System.err.println("   Status: " + e.getHttpStatus());
            // Ações: verificar parâmetros do grant
            
        } catch (UnauthorizedException e) {
            // Não autorizado
            System.err.println("❌ Não autorizado");
            System.err.println("   Mensagem: " + e.getMessage());
            // Ações: obter novo token, verificar permissões
            
        } catch (ResourceNotFoundException e) {
            // Recurso não encontrado
            System.err.println("❌ Recurso não encontrado");
            System.err.println("   Mensagem: " + e.getMessage());
            // Ações: verificar se o recurso existe
            
        } catch (ConflictException e) {
            // Conflito
            System.err.println("❌ Conflito");
            System.err.println("   Mensagem: " + e.getMessage());
            // Ações: resolver conflito, verificar estado atual
            
        } catch (CircuitBreakerOpenException e) {
            // Circuit breaker aberto
            System.err.println("⚠️ Serviço temporariamente indisponível");
            System.err.println("   Mensagem: " + e.getMessage());
            // Ações: implementar fallback, aguardar recuperação
            
        } catch (ServerException e) {
            // Erro de servidor
            System.err.println("❌ Erro interno do servidor");
            System.err.println("   Mensagem: " + e.getMessage());
            // Ações: logar para análise, possivelmente notificar
            
        } catch (OAuth2Exception e) {
            // Outros erros OAuth2
            System.err.println("❌ Erro OAuth2: " + e.getMessage());
            System.err.println("   Código: " + e.getErrorCode());
            System.err.println("   Status: " + e.getHttpStatus());
        }
    }
    
    public static void fallbackPattern() {
        // Exemplo de padrão fallback com circuit breaker
        try {
            TokenResponseDTO token = AuthClient.clientCredentials("client", "secret")
                .scope("read")
                .execute();
            
            useToken(token);
            
        } catch (CircuitBreakerOpenException e) {
            // Fallback: usar cache ou valor padrão
            System.out.println("⚠️ Usando token em cache (circuito aberto)");
            TokenResponseDTO cachedToken = getCachedToken();
            if (cachedToken != null) {
                useToken(cachedToken);
            } else {
                handleServiceUnavailable();
            }
        }
    }
    
    private static void useToken(TokenResponseDTO token) {
        // Implementação do uso do token
    }
    
    private static TokenResponseDTO getCachedToken() {
        // Implementação do cache
        return null;
    }
    
    private static void handleServiceUnavailable() {
        // Implementação de tratamento de indisponibilidade
    }
}
```

### 12.4 Boas Práticas de Tratamento de Erros

Ao implementar tratamento de erros, considere as seguintes práticas para garantir resiliência e experiência do usuário. Primeiro, sempre logue os erros com contexto suficiente para debugging, incluindo IDs de correlação quando disponíveis. Segundo, implemente circuit breakers adequadamente para evitar sobrecarga em serviços já degradados. Terceiro, forneça feedback claro ao usuário quando apropriado, sem expor detalhes internos de implementação. Por fim, implemente mecanismos de retry apenas para erros transitórios, não para erros de validação ou autorização.

---

## 13. Multi-Tenancy

### 13.1 Como Funciona

O suporte a multi-tenancy em ConexãoAuthLib é implementado através do header HTTP `X-Tenant-Id`. Este header é automaticamente adicionado a todas as requisições quando especificado no builder, permitindo que você gerencie múltiplos tenants usando a mesma instância de cliente.

```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

public class MultiTenantExamples {
    
    public static void operationsForDifferentTenants() {
        // Operações para Tenant A
        String tenantAId = "tenant-empresa-a";
        TokenResponseDTO tokenA = AuthClient.clientCredentials("client-a", "secret-a")
            .scope("read write")
            .tenantId(tenantAId)
            .execute();
        
        System.out.println("Token para Tenant A: " + tokenA.getAccessToken());
        
        // Operações para Tenant B
        String tenantBId = "tenant-empresa-b";
        TokenResponseDTO tokenB = AuthClient.clientCredentials("client-b", "secret-b")
            .scope("read")
            .tenantId(tenantBId)
            .execute();
        
        System.out.println("Token para Tenant B: " + tokenB.getAccessToken());
        
        // Listar tenants
        System.out.println("\nListando tenants:");
        
        TenantResponseDTO tenantA = TenantClient.get("tenant-id-a")
            .execute();
        System.out.println("  Tenant A: " + tenantA.getName());
        
        TenantResponseDTO tenantB = TenantClient.get("tenant-id-b")
            .execute();
        System.out.println("  Tenant B: " + tenantB.getName());
    }
    
    public static void tenantScopedClients() {
        // Criar clients específicos por tenant
        String tenantId = "tenant-123";
        
        // Registrar client para este tenant
        var client = com.conexaoauthlib.fluent.client.ClientClient.register()
            .clientId("service-for-tenant")
            .clientSecret("secret")
            .name("Serviço do Tenant")
            .tenantId(tenantId)
            .grantType("client_credentials")
            .scope("read", "write")
            .execute();
        
        System.out.println("Client registrado para tenant " + tenantId + ": " + client.getClientId());
    }
}
```

### 13.2 Gerenciamento Centralizado de Tenant

```java
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TenantManagementExamples {
    
    public static void manageMultipleTenants() {
        // Criar múltiplos tenants
        String[][] tenantData = {
            {"Empresa A", "11111111000100"},
            {"Empresa B", "22222222000100"},
            {"Empresa C", "33333333000100"}
        };
        
        Map<String, TenantResponseDTO> tenants = new HashMap<>();
        
        for (String[] data : tenantData) {
            TenantResponseDTO tenant = TenantClient.create()
                .name(data[0])
                .documentNumber(data[1])
                .execute();
            
            tenants.put(tenant.getId(), tenant);
            System.out.println("Tenant criado: " + tenant.getName() + " (ID: " + tenant.getId() + ")");
        }
        
        // Listar todos os tenants
        System.out.println("\nTotal de tenants: " + tenants.size());
        
        // Atualizar status de tenants específicos
        tenants.keySet().forEach(tenantId -> 
            System.out.println("  - " + tenants.get(tenantId).getName() + ": " + tenants.get(tenantId).getStatus())
        );
    }
}
```

---

## 14. Monitoramento e Métricas

### 14.1 Métricas do Circuit Breaker

A biblioteca expõe métricas do Resilience4j que podem ser coletadas e visualizadas através de ferramentas como Prometheus e Grafana.

```java
import com.conexaoauthlib.resilience.ResilienceStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class MonitoringExamples {
    
    @Autowired
    private ResilienceStatus resilienceStatus;
    
    public void checkCircuitBreakerHealth() {
        // Verificar saúde de todos os circuit breakers
        boolean allHealthy = resilienceStatus.isAllHealthy();
        
        System.out.println("Todos os Circuit Breakers saudáveis: " + allHealthy);
        
        // Obter status de cada circuit breaker
        Map<String, String> status = resilienceStatus.getStatus();
        status.forEach((name, state) ->
            System.out.println("  " + name + ": " + state)
        );
    }
    
    public void checkSpecificCircuitBreaker() {
        // Verificar circuit breaker específico
        String oauth2State = resilienceStatus.getState("oauth2");
        System.out.println("Circuit Breaker OAuth2: " + oauth2State);
        
        // Obter métricas
        Long failedCalls = resilienceStatus.getFailedCalls("oauth2");
        Long successfulCalls = resilienceStatus.getSuccessfulCalls("oauth2");
        
        System.out.println("Chamadas bem-sucedidas: " + successfulCalls);
        System.out.println("Chamadas falhadas: " + failedCalls);
    }
    
    public void getStateCounts() {
        // Obter contagem de circuit breakers por estado
        Map<String, Integer> counts = resilienceStatus.getStateCounts();
        
        System.out.println("Estado dos Circuit Breakers:");
        System.out.println("  CLOSED: " + counts.get("CLOSED"));
        System.out.println("  OPEN: " + counts.get("OPEN"));
        System.out.println("  HALF_OPEN: " + counts.get("HALF_OPEN"));
    }
}
```

### 14.2 Configuração do Actuator

Para expor métricas via Spring Boot Actuator, adicione a dependência do Micrometer e configure os endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
```

### 14.3 Métricas Disponíveis

As seguintes métricas são automaticamente coletadas e expostas:

| Métrica | Descrição |
|---------|-----------|
| `resilience4j_circuitbreaker_calls` | Número de chamadas ao circuit breaker, filtrado por resultado |
| `resilience4j_circuitbreaker_state` | Estado atual do circuit breaker (0=CLOSED, 1=OPEN, 2=HALF_OPEN) |
| `resilience4j_retry_calls` | Número de tentativas de retry, filtrado por resultado |
| `resilience4j_bulkhead_available_concurrent_calls` | Chamadas concorrentes disponíveis |
| `resilience4j_bircuitbreaker_failure_rate` | Taxa de falha do circuit breaker |

---

## 15. Testes

### 15.1 Testes Unitários com Mocks

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.feign.oauth2.OAuth2Client;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {
    
    @Mock
    private OAuth2Client oauth2Client;
    
    @InjectMocks
    private AuthClient authClient;
    
    @Test
    void shouldGetTokenWithClientCredentials() {
        // Given
        TokenResponseDTO expectedResponse = new TokenResponseDTO();
        expectedResponse.setAccessToken("access-token-123");
        expectedResponse.setTokenType("Bearer");
        expectedResponse.setExpiresIn(3600);
        
        when(oauth2Client.getToken(any())).thenReturn(expectedResponse);
        
        // When
        TokenResponseDTO result = AuthClient.clientCredentials("client", "secret")
            .scope("read")
            .execute();
        
        // Then
        assertNotNull(result);
        assertEquals("access-token-123", result.getAccessToken());
        assertEquals("Bearer", result.getTokenType());
        verify(oauth2Client, times(1)).getToken(any());
    }
    
    @Test
    void shouldThrowExceptionOnInvalidClient() {
        // Given
        when(oauth2Client.getToken(any()))
            .thenThrow(new InvalidClientException("Invalid client credentials", "invalid_client", 401));
        
        // When & Then
        assertThrows(InvalidClientException.class, () -> 
            AuthClient.clientCredentials("invalid", "invalid")
                .execute()
        );
    }
}
```

### 15.2 Testes de Integração

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;

@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest {
    
    @Autowired
    private AuthClient authClient;
    
    @Autowired
    private TenantClient tenantClient;
    
    @Test
    void shouldCreateTenantAndGetToken() {
        // Criar tenant
        TenantResponseDTO tenant = TenantClient.create()
            .name("Test Company")
            .documentNumber("12345678000100")
            .execute();
        
        assertNotNull(tenant.getId());
        assertEquals("Test Company", tenant.getName());
        
        // Obter token para o tenant
        TokenResponseDTO token = AuthClient.clientCredentials("test-client", "test-secret")
            .scope("read")
            .tenantId(tenant.getId())
            .execute();
        
        assertNotNull(token.getAccessToken());
        assertEquals("Bearer", token.getTokenType());
    }
}
```

### 15.3 Teste de Resiliência

```java
import org.junit.jupiter.api.Test;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import static org.junit.jupiter.api.Assertions.*;

import com.conexaoauthlib.resilience.ResilienceStatus;

class CircuitBreakerTest {
    
    @Test
    void circuitBreakerShouldOpenAfterFailures() {
        // Given
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = registry.circuitBreaker("test");
        ResilienceStatus status = new ResilienceStatus(registry);
        
        // Initially closed
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        
        // Simulate failures
        for (int i = 0; i < 5; i++) {
            circuitBreaker.onError(100, new RuntimeException("Error"));
        }
        
        // Should be open
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        
        // Check status
        assertFalse(status.isAllHealthy());
        assertEquals("OPEN", status.getState("test"));
    }
}
```

---

## 16. Contribuição

Contribuições são bem-vindas! Por favor, leia o arquivo [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes sobre nosso código de conduta e o processo para enviar pull requests.

### 16.1 Configuração do Ambiente de Desenvolvimento

```bash
# Pré-requisitos
- Java 21+
- Maven 3.8+
- Git

# Clonar o repositório
git clone https://github.com/conexaoauthlib/conexaoauthlib.git
cd conexaoauthlib

# Instalar dependências
mvn clean install -DskipTests

# Executar testes
mvn test

# Verificar formatação
mvn spotless:check

# Aplicar formatação
mvn spotless:apply
```

### 16.2 Diretrizes de Contribuição

Para contribuir com o projeto, siga estas etapas: primeiro, faça um fork do repositório e clone seu fork localmente. Segundo, crie uma branch para sua feature ou correção. Terceiro, faça suas alterações, garantindo que todos os testes passem e a formatação esteja correta. Quarto, commit suas mudanças seguindo o padrão Conventional Commits. Quinto, push sua branch e abra um Pull Request.

---

## 17. Licença

Este projeto está licenciado sob a Licença MIT - consulte o arquivo [LICENSE](LICENSE) para obter detalhes.

```
MIT License

Copyright (c) 2024 ConexãoAuthLib

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📞 Suporte

- **Documentação**: [README.md](README.md)
- **Issues**: [GitHub Issues](https://github.com/conexaoauthlib/conexaoauthlib/issues)
- **Contribuições**: [CONTRIBUTING.md](CONTRIBUTING.md)
- **Código de Conduta**: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

---

**Desenvolvido com ❤️ pela Equipe ConexãoAuthLib**
