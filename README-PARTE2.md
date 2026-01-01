# ConexãoAuthLib - Guia Prático

## 1. COMO EXPORTAR (Publicar no GitHub Packages)

### Passo 1: Gere um token no GitHub
```
https://github.com/settings/tokens
```
- New Token (classic)
- Scopes: `repo`, `write:packages`, `delete:packages`
- Copie o token (ghp_xxxx...)

### Passo 2: Configure o Maven
Arquivo: `C:\Users\marce\.m2\settings.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <server>
            <id>github</id>
            <username>MarceloMacedo02</username>
            <password>SEU_TOKEN_AQUI</password>
        </server>
    </servers>
</settings>
```

### Passo 3: Execute o comando
```bash
cd E:\projeto\conexaoauthlib
mvn clean deploy -DskipTests
```

**Pronto!** A biblioteca estará em:
https://github.com/MarceloMacedo02/conexaoauthlib/packages

---

## 2. COMO COMPILAR A BIBLIOTECA

### Compilar e gerar JAR
```bash
cd E:\projeto\conexaoauthlib
mvn clean package -DskipTests
```

**Resultado:** JAR em `target/conexaoauthlib-1.0.0-SNAPSHOT.jar`

### Instalar no repositório local (para testes)
```bash
cd E:\projeto\conexaoauthlib
mvn clean install -DskipTests
```

---

## 3. COMO CONSUMIR EM OUTRA APLICAÇÃO

### Passo 1: Configure o Maven
Arquivo: `C:\Users\SEU_USUARIO\.m2\settings.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <server>
            <id>github</id>
            <username>SEU_USUARIO_GITHUB</username>
            <password>SEU_TOKEN_GITHUB</password>
        </server>
    </servers>
</settings>
```

### Passo 2: Adicione a dependência no pom.xml
```xml
<dependency>
    <groupId>com.conexaoauthlib</groupId>
    <artifactId>conexaoauthlib</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Passo 3: Atualize as dependências
```bash
cd C:\caminho\do\seu\projeto
mvn dependency:resolve
```

### Passo 4: Exemplo de uso
```java
import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;

// Obter token OAuth2
TokenResponseDTO token = AuthClient.clientCredentials("client-id", "client-secret")
    .scope("read write")
    .tenantId("tenant-123")
    .execute();

System.out.println("Token: " + token.getAccessToken());
```

---

## 4. CONFIGURAÇÃO DO APPLICATION.YML
```yaml
conexao-auth:
  default:
    base-url: "http://localhost:8080"
    connect-timeout: 5000
    read-timeout: 10000
```

---

## INFORMAÇÕES DO PACOTE
| Campo | Valor |
|-------|-------|
| GroupId | com.conexaoauthlib |
| ArtifactId | conexaoauthlib |
| Versão SNAPSHOT | 1.0.0-SNAPSHOT |
| Repositório | https://maven.pkg.github.com/MarceloMacedo02/conexaoauthlib |

---

## PROBLEMAS COMUNS

| Erro | Solução |
|------|---------|
| 401 Unauthorized | Token inválido. Gere um novo token no GitHub |
| Não encontra dependência | Execute `mvn dependency:resolve` |
| Dependência com `!` | Versão SNAPSHOT precisa ser baixada do GitHub Packages |

---

## COMANDOS RÁPIDOS

| Ação | Comando |
|------|---------|
| Publicar | `mvn clean deploy -DskipTests` |
| Compilar | `mvn clean package -DskipTests` |
| Instalar local | `mvn clean install -DskipTests` |
| Baixar dependências | `mvn dependency:resolve` |
| Ver dependências | `mvn dependency:tree` |

---

**Atualizado**: Janeiro 2026
