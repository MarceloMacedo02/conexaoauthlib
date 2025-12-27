# Conexão Auth Spring Boot Starter v1.0.0

## 🎉 Primeira Release Estável

Primeira versão estável da biblioteca Spring Boot para integração com o serviço Conexão Auth.

### ✨ Funcionalidades

- **Autenticação JWT**: Validação e parsing de tokens JWT
- **Feign Client**: Cliente HTTP para comunicação com o serviço de autenticação
- **Cache Inteligente**: Cache para JWKS com TTL configurável
- **Configuração Flexível**: Suporte a múltiplos realms e configurações
- **Validação Automática**: Validação de claims e assinaturas RSA

### 📦 Artefatos

- ** conexao-auth-spring-boot-starter-1.0.0.jar**: Biblioteca principal
- ** conexao-auth-spring-boot-starter-1.0.0-sources.jar**: Fontes
- ** conexao-auth-spring-boot-starter-1.0.0-javadoc.jar**: Documentação Javadoc

### 🚀 Como Usar

```xml
<dependency>
    <groupId>com.plataforma.conexao</groupId>
    <artifactId>conexao-auth-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 🔧 Configuração

```properties
conexao.auth.url=https://api.conexaodigital.com.br/auth
conexao.auth.realm=seu-realm
conexao.auth.client-id=seu-client-id
```

### 🐛 Correções

- Configuração inicial do projeto
- Documentação completa de uso
- Integração com GitHub Packages

---

**Publicado automaticamente via GitHub Actions**