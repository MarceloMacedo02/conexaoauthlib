# Guia Rápido - ConexãoAuthLib

## 1. PUBLICAR (EXPORTAR) A BIBLIOTECA

### Pré-requisito: Gerar token no GitHub
1. Acesse: https://github.com/settings/tokens
2. Clique em **Generate new token (classic)**
3. Marque: `repo`, `write:packages`, `delete:packages`
4. Clique em **Generate**
5. **Copie o token** (ghp_xxxxx...)

### Configurar Maven
Arquivo: `C:\Users\marce\.m2\settings.xml`
```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>MarceloMacedo02</username>
      <password>SEU_TOKEN_AQUI</password>
    </server>
  </servers>
</settings>
```

### Publicar no GitHub Packages
```bash
cd E:\projeto\conexaoauthlib
mvn clean deploy -DskipTests
```

**Pronto!** Acesse: https://github.com/MarceloMacedo02/conexaoauthlib/packages

---

## 2. COMPILAR A BIBLIOTECA

```bash
cd E:\projeto\conexaoauthlib
mvn clean package -DskipTests
```

**Resultado:** `target/conexaoauthlib-1.0.0-SNAPSHOT.jar`

---

## 3. CONSUMIR EM OUTRA APLICAÇÃO

### 3.1. Configurar token no Maven
Arquivo: `C:\Users\SEU_USUARIO\.m2\settings.xml`
```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>SEU_USUARIO</username>
      <password>SEU_TOKEN</password>
    </server>
  </servers>
</settings>
```

### 3.2. Adicionar dependência no pom.xml
```xml
<dependency>
    <groupId>com.conexaoauthlib</groupId>
    <artifactId>conexaoauthlib</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 3.3. Baixar dependências
```bash
cd C:\caminho\do\projeto
mvn dependency:resolve
```

### 3.4. Exemplo de uso completo
```java
package com.exemplo;

import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Aplicacao {
    public static void main(String[] args) {
        SpringApplication.run(Aplicacao.class, args);
    }
    
    @Bean
    public CommandLineRunner testar() {
        return args -> {
            // Obter token OAuth2
            TokenResponseDTO token = AuthClient.clientCredentials("client-id", "client-secret")
                .scope("read write")
                .tenantId("tenant-123")
                .execute();
            
            System.out.println("Token: " + token.getAccessToken());
            System.out.println("Tipo: " + token.getTokenType());
            System.out.println("Expira em: " + token.getExpiresIn() + " segundos");
        };
    }
}
```

### 3.5. Configuração application.yml
```yaml
server:
  port: 8080

conexao-auth:
  default:
    base-url: "http://localhost:8080"
    connect-timeout: 5000
    read-timeout: 10000
```

---

## 4. COMANDOS ÚTEIS

| O que fazer | Comando |
|-------------|---------|
| Publicar no GitHub | `mvn clean deploy -DskipTests` |
| Gerar JAR | `mvn clean package -DskipTests` |
| Instalar local | `mvn clean install -DskipTests` |
| Baixar dependências | `mvn dependency:resolve` |
| Ver dependências | `mvn dependency:tree` |

---

## 5. INFORMAÇÕES DO PACOTE

| Campo | Valor |
|-------|-------|
| GroupId | com.conexaoauthlib |
| ArtifactId | conexaoauthlib |
| Versão | 1.0.0-SNAPSHOT |
| Repositório | https://maven.pkg.github.com/MarceloMacedo02/conexaoauthlib |

---

## 6. ERROS COMUNS E SOLUÇÕES

| Erro | Solução |
|------|---------|
| `401 Unauthorized` | Token expirado. Gere um novo token no GitHub |
| `Could not resolve dependency` | Execute `mvn dependency:resolve` |
| Erro de compilação | Verifique se o Java é 21+ |

---

**Versão**: 1.0.0-SNAPSHOT  
**Atualizado**: Janeiro 2026
