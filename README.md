# Conexão Auth Spring Boot Starter

Biblioteca Spring Boot para integração com o serviço de autenticação Conexão Auth.

## 🚀 Instalação

### 1. Configurar Repositório GitHub Packages

Adicione ao seu `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/MarceloMacedo02/conexaoauthlib</url>
    </repository>
</repositories>
```

### 2. Configurar Autenticação

Configure no seu `settings.xml` ou variáveis de ambiente:

```xml
<servers>
    <server>
        <id>github</id>
        <username>${env.GITHUB_USERNAME}</username>
        <password>${env.GITHUB_TOKEN}</password>
    </server>
</servers>
```

### 3. Adicionar Dependência

```xml
<dependency>
    <groupId>com.plataforma.conexao</groupId>
    <artifactId>conexao-auth-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## ⚙️ Configuração

Adicione as propriedades no seu `application.properties` ou `application.yml`:

```properties
# Configuração do serviço de autenticação
conexao.auth.url=https://api.conexaodigital.com.br/auth
conexao.auth.realm=seu-realm
conexao.auth.client-id=seu-client-id
conexao.auth.client-secret=seu-client-secret

# Configuração de cache (opcional)
conexao.auth.cache.jwks-ttl=3600
conexao.auth.cache.max-size=1000
```

## 🔧 Uso

### Autenticação via Feign Client

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ConexaoAuthClient authClient;
    
    public boolean validateToken(String token) {
        return authClient.validateToken(token);
    }
}
```

### Validação de JWT

```java
@RestController
@RequestMapping("/api")
public class ApiController {
    
    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint(
            @RequestHeader("Authorization") String authorization) {
        
        String token = authorization.replace("Bearer ", "");
        
        if (tokenValidator.isValid(token)) {
            return ResponseEntity.ok("Acesso autorizado");
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
```

## 📦 Publicação

Esta biblioteca é publicada automaticamente no GitHub Packages quando:
- Uma nova release é criada no GitHub
- O workflow é executado manualmente via Actions

## 🧪 Desenvolvimento

### Build e Testes

```bash
# Compilar e rodar testes
mvn clean test

# Gerar JAR com sources e javadoc
mvn clean package

# Publicar localmente para teste
mvn clean install
```

### Dependências Principais

- Spring Boot 3.2.7
- Spring Cloud OpenFeign
- Jackson (JSON)
- JJWT (JWT Processing)
- Bouncy Castle (Criptografia)
- Caffeine (Cache)

## 📄 Licença

MIT License - Veja o arquivo LICENSE para detalhes.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma feature branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para o branch (`git push origin feature/nova-funcionalidade`)
5. Crie um Pull Request

## 📞 Suporte

Para suporte e dúvidas, entre em contato através das issues do GitHub.