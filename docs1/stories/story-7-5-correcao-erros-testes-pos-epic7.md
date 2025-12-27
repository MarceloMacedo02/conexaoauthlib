# Story 7.5: Correção de Erros de Teste Pós-Epic 7

**Epic:** 7 - Auditoria de Eventos de Segurança  
**Status:** InProgress  
**Prioridade:** Alta  
**Estimativa:** 1 dia  
**Complexidade**: Alta

---

## Descrição

Como desenvolvedor, preciso corrigir os erros de teste que surgiram após a implementação do Epic 7 para que o sistema volte a funcionar corretamente e todos os testes passem.

---

## Critérios de Aceite

- [ ] Todos os endpoints retornam os status HTTP corretos (sem 403 ou 500 inesperados)
- [ ] Todos os testes de controllers passam sem erros de mock/stubbing
- [ ] Todos os testes unitários de criptografia passam
- [ ] Todos os testes de token passam
- [ ] Todos os testes de validação passam
- [ ] A configuração de segurança está correta e não bloqueia endpoints públicos
- [ ] Validação completa com execução de todos os testes

---

## Regras de Negócio

1. **Segurança**: Endpoints existentes devem manter suas permissões originais
2. **Compatibilidade**: Novas funcionalidades de auditoria não devem quebrar funcionalidades existentes
3. **Testes**: Todos os testes devem passar sem modificação nos comportamentos esperados

---

## Requisitos Técnicos

### Problemas Identificados:

#### 1. Erros de Autorização (403 Unauthorized)
- Múltiplos endpoints retornando 403 em vez dos status esperados
- Endpoints afetados: ChaveInternalController, RealmController, UsuarioController, RoleController, TokenRevogacaoController, JwksController

#### 2. Erros de Teste (500 Internal Server Error) 
- AuthorizationCodeFlowTest - endpoints retornando 500 em vez de redireção
- Endpoint JWKS retornando 500 em vez de 200
- Endpoint metadata sem scopes_supported

#### 3. Erros de Mock/Stubs
- AdminRealmControllerTest e AdminUsuarioControllerTest com problemas de stubbing
- Argumentos diferentes entre stubs e chamadas reais
- PotentialStubbingProblem do Mockito

#### 4. Erros de Criptografia
- AesCriptografiaServiceTest não está lançando exceção quando deveria
- RsaKeyGeneratorTest com tamanho de chave incorreto
- RotacaoChaveServiceImplTest com erro de criptografia

#### 5. Erros de Teste de Token
- TokenRevogacaoServiceImplTest com UnnecessaryStubbing
- JwksProviderTest com valores de UUID incorretos

#### 6. Erros de Validação
- ValidarUnicidadeEmailIntegrationTest com mensagem de erro diferente do esperado

---

## Tarefas / Subtasks

- [x] Corrigir configuração de dependências no pom.xml
  - [x] Remover dependência duplicada de spring-boot-starter-security
  - [x] Compilação limpa após correção

- [ ] Verificar e corrigir SecurityConfig
  - [ ] Analisar configuração de segurança atual
  - [ ] Verificar se endpoints públicos estão acessíveis
  - [ ] Ajustar regras para endpoints existentes
  - [ ] Testar acesso aos endpoints críticos

- [ ] Corrigir NoClassDefFoundError em ChaveInternalControllerTest
  - [ ] Investigar dependências de ChaveService no teste
  - [ ] Verificar configuração de @WebMvcTest
  - [ ] Ajustar mocks para carregamento correto

- [ ] Corrigir erro de validação em RotacaoChaveServiceImpl
  - [ ] Investigar problema "Par de chaves gerado é inválido"
  - [ ] Verificar conflito com nova auditoria
  - [ ] Ajustar lógica de geração/validação de chaves

- [ ] Corrigir Testes de Controllers
  - [ ] Corrigir AuthorizationCodeFlowTest
  - [ ] Corrigir JwksControllerTest
  - [ ] Corrigir TokenRevogacaoControllerTest
  - [ ] Verificar endpoints metadata

- [ ] Corrigir Testes Unitários
  - [ ] Corrigir AesCriptografiaServiceTest (já passa)
  - [ ] Corrigir RsaKeyGeneratorTest (já passa)
  - [ ] Corrigir RotacaoChaveServiceImplTest
  - [ ] Corrigir TokenRevogacaoServiceImplTest
  - [ ] Corrigir JwksProviderTest

- [ ] Corrigir Testes de Validação
  - [ ] Corrigir ValidarUnicidadeEmailIntegrationTest
  - [ ] Verificar mensagens de erro

- [ ] Validação Final
  - [ ] Executar suite completa de testes
  - [ ] Verificar cobertura de testes
  - [ ] Validar funcionalidades manualmente se necessário

---

## Dev Notes

### Contexto do Problema:
Após implementação do Epic 7 - Auditoria de Eventos de Segurança, múltiplos testes começaram a falhar. Os problemas identificados são:

1. **Problemas de compilação de testes**: Múltiplos arquivos de teste com imports incorretos e pacotes faltando
2. **NoClassDefFoundError em ChaveService**: Testes não conseguem carregar a classe ChaveService durante execução
3. **Erros de 500 em vez de 403**: Não são problemas de segurança mas sim erros de implementação
4. **Problemas com mocks/stubs**: Diferenças entre parâmetros esperados e reais nos testes

### Principais Problemas Identificados:
- ChaveInternalControllerTest: NoClassDefFoundError para ChaveService
- TokenServiceTest: Import de pacotes inexistentes (api.dto, config, etc)
- ValidarUnicidadeEmailIntegrationTest: Pacotes de dominio faltando
- AesCriptografiaServiceTest: Funciona (passou), então issue é específica

### Arquivos Críticos para Verificar:
- Imports em arquivos de teste (correção de pacotes)
- ChaveInternalControllerTest (dependências do ChaveService)
- TokenServiceTest e outros testes com imports incorretos
- Configuração de classpath e dependências

### Prioridade:
1. **ALTA**: Corrigir problemas de compilação e imports nos testes
2. **MÉDIA**: Corrigir NoClassDefFoundError em controllers específicos
3. **BAIXA**: Ajustar lógicas de testes unitários específicos

---

## Testing

### Estratégia de Teste:
- Executar testes incrementalmente após cada correção principal
- Focar primeiro em testes de integração e controllers
- Validar configuração de segurança com testes de endpoints
- Executar suite completa ao final para garantir regressão

### Padrões a Seguir:
- Manter comportamentos existentes dos testes
- Usar ArgumentMatchers.any() quando apropriado
- Garantir que testes não dependam de implementações específicas
- Manter testes isolados e independentes

---

## Change Log

| Date | Version | Description | Author |
|------|---------|-------------|--------|
| 2025-12-23 | 1.0 | Criação da história para correção de erros pós-Epic 7 | James |

---

## Dev Agent Record

### Agent Model Used
Claude-3.5-Sonnet

### Debug Log References
- Será populado durante a correção dos problemas

### Completion Notes List
- Será populado durante a implementação

### File List
- Será populado durante a implementação

---

## QA Results

*Será preenchido pelo QA Agent após conclusão*