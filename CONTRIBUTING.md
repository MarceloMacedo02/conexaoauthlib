# Guia de Contribuição

Obrigado por seu interesse em contribuir para o ConexãoAuthLib! Este documento fornece diretrizes e instruções para contribuir com o projeto.

## 📋 Índice

- [Código de Conduta](#código-de-conduta)
- [Como Contribuir](#como-contribuir)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Processo de Desenvolvimento](#processo-de-desenvolvimento)
- [Padrões de Código](#padrões-de-código)
- [Testes](#testes)
- [Documentação](#documentação)
- [Commits](#commits)
- [Pull Requests](#pull-requests)

---

## Código de Conducta

Este projeto segue nosso [Código de Conduta](CODE_OF_CONDUCT.md). Ao participar desta comunidade, você concorda em respeitar estes termos.

## Como Contribuir

Existem várias formas de contribuir:

1. **Reportar Bugs** - Encontrou um bug? Abra uma issue
2. **Sugerir Funcionalidades** - Tem uma ideia? Compartilhe conosco
3. **Escrever Documentação** - Melhore a documentação do projeto
4. **Corrigir Bugs** - Resolva issues existentes
5. **Implementar Funcionalidades** - Adicione novos recursos
6. **Revisar Código** - Ajudar a revisar PRs de outros contribuidores

## Configuração do Ambiente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Git

### Configuração

1. **Fork o repositório**

   Clique no botão "Fork" no canto superior direito da página do repositório.

2. **Clone seu fork**

   ```bash
   git clone https://github.com/YOUR-USERNAME/conexaoauthlib.git
   cd conexaoauthlib
   ```

3. **Adicione o repositório original como remote**

   ```bash
   git remote add upstream https://github.com/conexaoauthlib/conexaoauthlib.git
   ```

4. **Instale as dependências**

   ```bash
   mvn clean install -DskipTests
   ```

## Processo de Desenvolvimento

### 1. Sincronize com o repositório principal

```bash
git checkout main
git fetch upstream
git merge upstream/main
```

### 2. Crie uma branch para sua feature/fix

```bash
git checkout -b feature/nova-funcionalidade
# ou para correções
git checkout -b bugfix/descrição-do-bug
```

### 3. Desenvolva sua funcionalidade

- Faça suas alterações
- Execute os testes localmente
- Garanta que o código compila

### 4. Mantenha sua branch atualizada

```bash
git fetch upstream
git rebase upstream/main
```

### 5. Commit suas mudanças

```bash
git add .
git commit -m "Descrição clara das mudanças"
```

### 6. Push para seu fork

```bash
git push origin feature/nova-funcionalidade
```

### 7. Crie um Pull Request

Vá até o GitHub e crie um Pull Request do seu fork para o repositório principal.

## Padrões de Código

### Estilo de Código

Seguimos o [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

### Verificação de Código

Antes de commitar, execute:

```bash
# Verifica formatação
mvn spotless:check

# Aplica formatação automaticamente
mvn spotless:apply

# Verifica código estático
mvn static-analysis:check
```

### Convenções de Nomenclatura

- **Classes**: PascalCase (ex: `OAuth2Client`, `UserService`)
- **Métodos e Variáveis**: camelCase (ex: `getUser()`, `userName`)
- **Constantes**: UPPER_SNAKE_CASE (ex: `MAX_RETRY_COUNT`)
- **Pacotes**: lowercase (ex: `com.conexaoauthlib.dto`)

### Estrutura de Pacotes

```
src/main/java/br/com/plataforma/conexaodigital
├── shared/
│   ├── config/
│   ├── exceptions/
│   ├── security/
│   └── utils/
└── [bounded_context]/
    ├── api/
    ├── application/
    ├── domain/
    └── infrastructure/
```

## Testes

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar teste específico
mvn test -Dtest=UserClientTest

# Executar com cobertura
mvn test jacoco:report
```

### Cobertura Mínima

- **Unit Tests**: 80%
- **Integration Tests**: 60%
- **Overall**: 75%

### Tipos de Testes

1. **Unit Tests**: Testam classes isoladamente
2. **Integration Tests**: Testam integração entre componentes
3. **Contract Tests**: Testam APIs externas (Pact)

### Boas Práticas de Teste

- Cada teste deve testar uma única funcionalidade
- Use nomes descritivos para testes
- Organize testes por classe testada
- Mantenha testes independentes
- Use mocks para dependências externas

## Documentação

### JavaDoc

Todas as classes e métodos públicos devem ter JavaDoc:

```java
/**
 * Classe responsável pelo gerenciamento de tokens OAuth2.
 *
 * <p>Esta classe fornece métodos para obtenção, validação e revocação
 * de tokens de acesso utilizando os fluxos Client Credentials,
 * Password e Refresh Token.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class OAuth2Client {
    /**
     * Obtém um token de acesso utilizando o fluxo Client Credentials.
     *
     * @param clientId Identificador do cliente
     * @param clientSecret Senha do cliente
     * @return TokenResponseDTO contendo o token de acesso
     * @throws InvalidClientException quando as credenciais são inválidas
     */
    public TokenResponseDTO getToken(String clientId, String clientSecret) {
        // implementação
    }
}
```

### Atualização de Documentação

- README.md: Para mudanças que afetam a API pública
- CHANGELOG.md: Para novas features e correções
- docs/: Para documentação técnica detalhada

## Commits

### Formato de Commits

```
<tipo>(<escopo>): <descrição>

[corpo opcional]

[footer opcional]
```

### Tipos de Commits

- **feat**: Nova funcionalidade
- **fix**: Correção de bug
- **docs**: Mudanças na documentação
- **style**: Formatação de código (sem mudança de lógica)
- **refactor**: Refatoração de código
- **test**: Adição ou correção de testes
- **chore**: Tarefas de manutenção

### Exemplos

```
feat(oauth2): adicionar suporte a refresh token

Implementa a funcionalidade de refresh token conforme RFC 6749.
Inclui validação de token expirado e rotação de refresh token.

Closes #123
```

```
fix(client): corrigir validação de client secret

O client secret não estava sendo validado corretamente,
permitindo autenticação com segredos parciais.

Fixes #456
```

## Pull Requests

### Criação de PR

1. Preencha o template de PR
2. Descreva as mudanças realizadas
3. Adicione screenshots para mudanças visuais
4. Liste issues relacionados
5. Marque revisores

### Revisão de PR

- Mantenha um tom respeitoso
- Seja específico e construtivo
- Sugira melhorias em vez de apenas criticar
- Reconheça boas práticas
- Foque no código, não na pessoa

### Checklist do PR

- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Código formatação corretamente
- [ ] Build passa localmente
- [ ] Sem warnings de compilação
- [ ] Commits seguem o padrão
- [ ] Branch está atualizada com main

---

## 📞 Suporte

Se tiver dúvidas:

- Leia a [Documentação](README.md)
- Procure por issues similares
- Abra uma nova issue se necessário
- Responda no Discord da comunidade (se disponível)

Obrigado por contribuir! 🎉
