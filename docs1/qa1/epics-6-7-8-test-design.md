# 🧪 Test Design Comprehensive - Epics 6, 7 e 8

**Data da Análise:** 2025-12-24  
**Analista:** Quinn - Test Architect & Quality Advisor  
**Estratégia:** Pyramid Testing (Unit → Integration → E2E)

---

## 🎯 Estratégia Geral de Testes

### Pirâmide de Testes
```
        /\
       /  \  ← E2E Tests (10%)
      /____\
     /      \
    /        \ ← Integration Tests (20%)
   /__________\
  /            \
 /              \ ← Unit Tests (70%)
/________________\
```

### Níveis de Teste

1. **Unit Tests (70%)**
   - Testes isolados de classes e métodos
   - Mock de dependências externas
   - Cobertura mínima: 80%

2. **Integration Tests (20%)**
   - Testes de componentes e APIs
   - Banco de dados em memória (H2)
   - Integração entre múltiplos serviços

3. **E2E Tests (10%)**
   - Testes completos de usuário
   - Browser automation (Selenium/Playwright)
   - Fluxos críticos de negócio

---

## 📋 Epic 6 - Dashboard Administrativo

### Test Strategy
**Foco:** UI/UX, segurança, usabilidade

#### 6.1 - Tela de Login

**Unit Tests:**
```gherkin
Cenário: Validação de campos obrigatórios
  Dado formulário de login com campos vazios
  Quando submit é acionado
  Então erros de validação são exibidos

Cenário: Formato de email inválido
  Dado email "email-invalido" no formulário
  Quando submit é acionado
  Então mensagem "Email inválido" é exibida
```

**Integration Tests:**
```gherkin
Cenário: Login com credenciais corretas
  Dado usuário existente com email/senha válidos
  Quando login é submetido via POST /admin/login
  Então redirecionado para /admin/dashboard
  E sessão é criada

Cenário: Login com credenciais inválidas
  Dado usuário com senha incorreta
  Quando login é submetido
  Então mensagem "Credenciais inválidas" exibida
  E permanece na página de login
```

**E2E Tests:**
```gherkin
Cenário: Fluxo completo de login e acesso ao dashboard
  Dado usuário na página de login
  Quando preenche credenciais válidas e clica em "Entrar"
  Então dashboard é carregado com métricas
  E menu de navegação está visível
  E informações do usuário aparecem no header
```

#### 6.2 - Tela de Cadastro

**Unit Tests:**
```gherkin
Cenário: Validação de força de senha
  Dado senha "123" no formulário
  Quando validação é executada
  Então erro "Senha deve ter no mínimo 8 caracteres"

Cenário: Confirmação de senha não confere
  Dado senha "Senha@123" e confirmação "Senha@456"
  Quando validação é executada
  Então erro "Senhas não conferem"
```

**Integration Tests:**
```gherkin
Cenário: Cadastro com email duplicado
  Dado usuário existente com email "test@example.com"
  Quando novo cadastro com mesmo email é submetido
  Então mensagem "Email já cadastrado" é retornada
  E usuário não é criado

Cenário: Cadastro com sucesso
  Dados dados válidos de novo usuário
  Quando cadastro é submetido via POST /admin/cadastro
  Então usuário é criado no realm master
  E role USER é associada
  E redirecionado para página de login
```

**E2E Tests:**
```gherkin
Cenário: Fluxo completo de cadastro e login
  Dado usuário na página de cadastro
  Quando preenche formulário válido e submete
  Então redirecionado para login com mensagem de sucesso
  E pode fazer login com novas credenciais
  E dashboard é acessível
```

#### 6.3 - Dashboard Principal

**Unit Tests:**
```gherkin
Cenário: Agregação de métricas
  Dado múltiplos realms, usuários, roles, chaves
  Quando DashboardService.obterDashboard() é chamado
  Então totais corretos são retornados
  E labels para gráficos são gerados

Cenário: Cálculo de usuários por status
  Dado usuários com diferentes status
  Quando métricas são calculadas
  Então contagem por status está correta
```

**Integration Tests:**
```gherkin
Cenário: Endpoint de dashboard
  Dado usuário autenticado como ADMIN
  Quando GET /admin/dashboard é acessado
  Então dashboard.html é renderizado
  E modelo contém DashboardResponse
  E todas as métricas são populadas
```

**E2E Tests:**
```gherkin
Cenário: Visualização completa do dashboard
  Dado usuário admin logado no dashboard
  Quando página é carregada
  Então cards com métricas são exibidos
  E gráficos são renderizados
  E navegação lateral funciona
  E eventos recentes aparecem na tabela
```

---

## 🔒 Epic 7 - Auditoria de Eventos

### Test Strategy
**Foco:** integridade de dados, performance, segurança

#### 7.1 - Modelo de Domínio

**Unit Tests:**
```gherkin
Cenário: Criação de evento de auditoria
  Dado dados válidos de evento
  Quando EventoAuditoria é criado
  Então todos os campos são populados
  E dataCriacao é gerada automaticamente

Cenário: Serialização de detalhes em JSON
  Dado mapa com detalhes {"key": "value"}
  Quando detalhes são serializados
  Então string JSON válida é gerada
```

**Integration Tests:**
```gherkin
Cenário: Persistência de evento
  Dado EventoAuditoria criado
  Quando salvo no repository
  Então evento é persistido com ID
  E índices são criados corretamente
```

#### 7.2 - Serviço de Registro

**Unit Tests:**
```gherkin
Cenário: Captura automática de IP
  Dado request com header X-Forwarded-For
  Quando evento é registrado
  Então IP correto é capturado

Cenário: Tratamento de erro no registro
  Dado exceção ao registrar evento
  Quando registrarEvento() é chamado
  Então exceção é capturada e logada
  E transação principal não é afetada
```

**Integration Tests:**
```gherkin
Cenário: Registro de evento completo
  Dados usuário, realm e detalhes
  Quando evento é registrado
  Então todos os campos são salvos
  E auditoria é registrada no banco
  E IP/User-Agent são capturados
```

#### 7.3 - Consulta com Filtros

**Unit Tests:**
```gherkin
Cenário: Especificação por realm
  Dado realmId específico
  Quando Specification é construída
  Então WHERE clause correta é gerada

Cenário: Especificação por período
  Dados dataInicio e dataFim
  Quando Specification é construída
  Então BETWEEN clause é gerada
```

**Integration Tests:**
```gherkin
Cenário: Listagem com múltiplos filtros
  Dados eventos com diferentes tipos e datas
  Quando GET /api/v1/auditoria/eventos com filtros
  Então apenas eventos filtrados retornam
  E paginação funciona corretamente
  E ordenação por data descendente é aplicada
```

---

## 🚀 Epic 8 - Bootstrap do Sistema

### Test Strategy
**Foco:** idempotência, configuração, recuperação

#### 8.1 - Configuração de Bootstrap

**Unit Tests:**
```gherkin
Cenário: Desabilitação via properties
  Dado bootstrap.habilitado=false
  Quando aplicação inicia
  Então BootstrapService não é executado

Cenário: Configuração de credenciais
  Dado variáveis de ambiente configuradas
  Quando BootstrapService inicia
  Então credenciais são lidas corretamente
```

**Integration Tests:**
```gherkin
Cenário: Execução automática na inicialização
  Dado aplicação sendo iniciada
  Quando ApplicationReadyEvent é disparado
  Então BootstrapService.executarBootstrap() é chamado
```

#### 8.2 - Bootstrap Realm Master

**Unit Tests:**
```gherkin
Cenário: Idempotência de realm master
  Dado realm master já existe
  Quando criarRealmMaster() é chamado novamente
  Então novo realm não é criado
  E log informativo é gerado

Cenário: Criação bem-sucedida
  Dado sistema sem realm master
  Quando criarRealmMaster() é chamado
  Então realm é criado com nome "master"
  E status ATIVO é definido
  E auditoria BOOTSTRAP_REALM_MASTER é registrada
```

#### 8.3 - Bootstrap Roles Padrão

**Unit Tests:**
```gherkin
Cenário: Criação das 3 roles padrão
  Dado realm master existente
  Quando criarRolesPadrao() é chamado
  Então roles ADMIN, USER, SERVICE são criadas
  E todas estão marcadas como padrao=true
  E auditoria BOOTSTRAP_ROLES é registrada

Cenário: Idempotência individual por role
  Dado role ADMIN já existe
  Quando bootstrap é executado
  Então role ADMIN não é recriada
  E outras roles são verificadas individualmente
```

---

## 🛠️ Testes Cross-Epic

### Integração entre Epics

**Bootstrap → Dashboard:**
```gherkin
Cenário: Dashboard pós-bootstrap
  Dado bootstrap executado com sucesso
  Quando dashboard é acessado
  Então métricas mostram dados do bootstrap
  E realm master aparece nas estatísticas
  E usuário admin aparece nos contadores
```

**Auditoria → Bootstrap:**
```gherkin
Cenário: Eventos de bootstrap na auditoria
  Dado bootstrap executado
  Quando eventos são consultados
  Então BOOTSTRAP_* events aparecem
  E detalhes corretos são registrados
```

**Dashboard → Auditoria:**
```gherkin
Cenário: Eventos recentes no dashboard
  Dado múltiplos eventos de auditoria
  Quando dashboard é carregado
  Então últimos 10 eventos aparecem
  E ordenação por data está correta
```

---

## 🔧 Ferramentas e Configuração

### Stack de Testes
```yaml
unit_tests:
  framework: JUnit 5
  mocking: Mockito
  assertions: AssertJ
  coverage: JaCoCo (target: 80%)

integration_tests:
  framework: Spring Boot Test
  database: H2 (testcontainers para CI)
  rest: MockMvc
  fixtures: TestContainers

e2e_tests:
  framework: Playwright
  browsers: [Chrome, Firefox]
  reporting: Allure
  ci: GitHub Actions
```

### Configuração de Testes
```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public AuditoriaService auditoriaServiceMock() {
        return Mockito.mock(AuditoriaService.class);
    }
    
    @Bean
    @Profile("test")
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```

### Test Data Management
```java
@Component
public class TestDataFactory {
    
    public static Usuario criarUsuarioValido() {
        return Usuario.builder()
            .nome("Test User")
            .email("test@example.com")
            .senha("Password@123")
            .status(StatusUsuario.ATIVO)
            .build();
    }
    
    public static Realm criarRealmMaster() {
        return Realm.builder()
            .nome("master")
            .status(StatusRealm.ATIVO)
            .build();
    }
}
```

---

## 📊 Métricas e Relatórios

### Cobertura de Testes
- **Target Mínimo:** 80% line coverage
- **Target Ideal:** 90% line coverage
- **Branch Coverage:** 75% mínimo

### Qualidade de Testes
- **Testes Documentados:** Com Given-When-Then
- **Testes Independentes:** Sem dependências entre si
- **Testes Rápidos:** < 2s por teste unitário
- **Testes Reprodutíveis:** Mesmo resultado em diferentes execuções

### Relatórios Automatizados
```yaml
reports:
  unit: target/surefire-reports/
  integration: target/failsafe-reports/
  coverage: target/site/jacoco/
  e2e: allure-results/

ci_integration:
  - Rodar testes em cada PR
  - Bloquear merge se coverage < 80%
  - Gerar badge de coverage
  - Publicar relatórios em artifacts
```

---

## 🎯 Priorização de Testes

### P0 - Críticos (Blockers)
- Testes de bootstrap completo
- Testes de segurança (CSRF, XSS)
- Testes de criação de usuário admin

### P1 - Altos (Must Have)
- Testes de login/cadastro
- Testes de auditoria
- Testes de dashboard básico

### P2 - Médios (Should Have)
- Testes de UI avançada
- Testes de performance
- Testes de cross-browser

### P3 - Baixos (Nice to Have)
- Testes de acessibilidade
- Testes de responsividade avançada
- Testes de usabilidade

---

## 🏁 Conclusão

Esta estratégia de testes fornece cobertura abrangente para os três epics, com foco especial em:

1. **Segurança:** Validação de CSRF, XSS, e proteção de dados
2. **Funcionalidade:** Todos os fluxos críticos testados
3. **Performance:** Identificação de gargalos e memory leaks
4. **Confiabilidade:** Bootstrap robusto e recuperação de erros

A implementação desta estratégia garantirá qualidade e confiança no sistema antes da produção.