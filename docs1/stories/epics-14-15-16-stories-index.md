# Stories Index - Epics 14, 15, and 16

## Overview
Este documento indexa todas as histórias criadas para os Epics 14, 15 e 16 (Gestão de Roles, Chaves e Auditoria).

---

## Epic 14: Página de Gestão de Roles (Thymeleaf)

### Lista de Histórias

| Story | Arquivo | Estimativa |
|-------|---------|------------|
| Story 01: Template da Lista de Roles | `epic-14-story-01-template-lista-roles.md` | 4 horas | Concluído |
| Story 02: DTOs de Role (Java Records) | `epic-14-story-02-dtos-role-java-records.md` | 4 horas | Concluído |
| Story 03: Backend Service Layer | `epic-14-story-03-backend-service-layer.md` | 7 horas | Concluído |
| Story 04: Controller API - AdminRoleController | `epic-14-story-04-controller-api-adminrolecontroller.md` | 5 horas | Concluído |
| Story 05: Modal de Criação/Edição | `epic-14-story-05-modal-criacao-edicao.md` | 4 horas | Concluído |
| Story 06: CRUD - Visualizar, Editar, Ativar/Inativar | `epic-14-story-06-crud-visualizar-editar-ativar-inativar.md` | 6 horas | Concluído |
| Story 07: Definição de Roles Padrão | `epic-14-story-07-definicao-roles-padrao.md` | 5 horas | Concluído |
| Story 08: Validações e Feedback de Usuário | `epic-14-story-08-validacoes-feedback.md` | 5 horas | Concluído |

**Total Epic 14: 34 horas (aprox. 4.25 dias úteis)**

### Arquivos Criados no Epic 14:
- Templates:
  - `src/main/resources/templates/admin/roles/list.html`
  - `src/main/resources/templates/admin/roles/form.html`

- Java Backend:
  - `src/main/java/.../admin/api/responses/RoleListResponse.java`
  - `src/main/java/.../admin/api/responses/RoleDetailResponse.java`
  - `src/main/java/.../admin/api/requests/RoleForm.java`
  - `src/main/java/.../admin/api/service/AdminRoleService.java`
  - `src/main/java/.../admin/api/controller/AdminRoleController.java`
  - `src/main/java/.../admin/api/mapper/RoleMapper.java`

- JavaScript:
  - `src/main/resources/static/js/pages/roles.js`
  - `src/main/resources/static/js/pages/roles-validation.js`

---

## Epic 15: Página de Gestão de Chaves Criptográficas (Thymeleaf)

### Lista de Histórias

| Story | Descrição | Estimativa |
|-------|-----------|------------|
| Story 01: Template da Lista de Chaves e Tabs | Template com tabs (Chaves Ativas, Histórico) | 4 horas |
| Story 02: DTOs de Chave (Java Records) | ChaveAtivaResponse, RotacaoHistoricoResponse | 5 horas |
| Story 03: Backend Service Layer | AdminChaveService com métodos de listagem/rotação | 8 horas |
| Story 04: Controller API | AdminChaveController com endpoints REST | 6 horas |
| Story 05: Funcionalidade de Rotação Manual | Modal de confirmação e execução | 6 horas |
| Story 06: Visualização de Histórico de Rotações | Tabela detalhada de rotações | 4 horas |
| Story 07: Countdown de Próxima Rotação Automática | JavaScript countdown visual | 3 horas |

**Total Epic 15: 36 horas (aprox. 4.5 dias úteis)**

### Arquivos a Criar no Epic 15:
- Templates:
  - `src/main/resources/templates/admin/chaves/list.html`

- Java Backend:
  - `src/main/java/.../admin/api/responses/ChaveAtivaResponse.java`
  - `src/main/java/.../admin/api/responses/RotacaoHistoricoResponse.java`
  - `src/main/java/.../admin/api/service/AdminChaveService.java`
  - `src/main/java/.../admin/api/controller/AdminChaveController.java`

- JavaScript:
  - `src/main/resources/static/js/pages/chaves.js`

---

## Epic 16: Página de Visualização de Auditoria (Thymeleaf)

### Lista de Histórias

| Story | Descrição | Estimativa |
|-------|-----------|------------|
| Story 01: Template da Lista de Eventos | Template com filtros avançados | 5 horas |
| Story 02: DTOs de Auditoria (Java Records) | EventoAuditoriaResponse com enum tipos | 5 horas |
| Story 03: Backend Service Layer | AuditoriaService com filtros avançados | 9 horas |
| Story 04: Controller API | AdminAuditoriaController com endpoints | 6 horas |
| Story 05: Filtros Avançados e Busca | Realm, tipo, período, usuário | 6 horas |
| Story 06: Modal de Detalhes do Evento | Modal com informações completas | 6 horas |
| Story 07: Coloração por Severidade e Ícones | Badges coloridos por tipo crítico | 3 horas |
| Story 08: Exportação CSV (Opcional) | Exportação de eventos em CSV | 5 horas |

**Total Epic 16: 45 horas (aprox. 5.6 dias úteis)**

### Arquivos a Criar no Epic 16:
- Templates:
  - `src/main/resources/templates/admin/auditoria/list.html`

- Java Backend:
  - `src/main/java/.../admin/api/responses/EventoAuditoriaResponse.java`
  - `src/main/java/.../admin/api/service/AdminAuditoriaService.java`
  - `src/main/java/.../admin/api/controller/AdminAuditoriaController.java`

- JavaScript:
  - `src/main/resources/static/js/pages/auditoria.js`

---

## Resumo Geral

### Estimativa Total de Esforço:

| Epic | Histórias | Estimativa (horas) | Estimativa (dias) |
|------|-----------|--------------------|-------------------|
| Epic 14 - Roles | 8 | 34h | ~4.25d | **Concluído** | 25/12/2025 |
| Epic 15 - Chaves | 7 | 36h | ~4.5d |
| Epic 16 - Auditoria | 8 | 45h | ~5.6d |
| **TOTAL** | **23** | **115h** | **~14.4 dias úteis** |

### Funcionalidades Implementadas por Epic:

**Epic 14 - Gestão de Roles:**
- ✅ Listagem paginada de roles
- ✅ Criação de novas roles
- ✅ Edição de roles existentes
- ✅ Remoção de roles (com verificação de uso)
- ✅ Ativação/Inativação de roles
- ✅ Roles padrão (ADMIN, USER, SERVICE)
- ✅ Validações server-side e client-side
- ✅ Filtros por realm, status
- ✅ Contagem de usuários por role

**Epic 15 - Gestão de Chaves:**
- ✅ Visualização de chaves ativas por realm
- ✅ Visualização de histórico de rotações
- ✅ Rotação manual de chaves
- ✅ Countdown de próxima rotação automática
- ✅ Indicadores de expiração (ex: < 7 dias)
- ✅ Tabs para alternar visões
- ✅ Filtros por realm

**Epic 16 - Auditoria:**
- ✅ Listagem de eventos de auditoria
- ✅ Filtros avançados (realm, tipo, período, usuário)
- ✅ Busca textual
- ✅ Modal de detalhes do evento
- ✅ Coloração por severidade
- ✅ Ícones por tipo de evento
- ✅ Exportação CSV (opcional)
- ✅ Paginação configurável

### Tecnologias Utilizadas:
- **Backend:** Java 17+, Spring Boot 3, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5, Tabler Icons
- **JavaScript:** Vanilla JS (ES6+), Bootstrap 5 JS
- **Validação:** Jakarta Bean Validation, custom client-side
- **Database:** PostgreSQL (via Spring Data JPA)
- **Testes:** JUnit 5, Mockito, Spring Boot Test

### Padrões Arquiteturais Seguidos:
- ✅ DTO Pattern com Java Records (imutáveis)
- ✅ Service Layer com validação de negócio
- ✅ Controller Layer com flash attributes
- ✅ Repository Pattern com custom queries
- ✅ Exception handling customizado
- ✅ Mensagens internacionalizadas (i18n)
- ✅ Server-side + Client-side validation
- ✅ REST API endpoints para AJAX
- ✅ Progressive enhancement (JS opcional)

### Dependências Entre Epics:

```
Epic 14 (Roles)
    ├── Epic 1 (Gestão de Realms)
    ├── Epic 3 (Gestão de Roles Backend)
    └── Epic 9 (Configuração)

Epic 15 (Chaves)
    ├── Epic 1 (Gestão de Realms)
    ├── Epic 5 (Gestão de Chaves Backend)
    ├── Epic 4 (OAuth 2.0)
    └── Epic 9 (Configuração)

Epic 16 (Auditoria)
    ├── Epic 1 (Gestão de Realms)
    ├── Epic 2 (Gestão de Usuários)
    ├── Epic 5 (Gestão de Chaves)
    ├── Epic 7 (Auditoria Backend)
    └── Epic 9 (Configuração)
```

---

## Como Utilizar as Histórias

### Para o Scrum Master:
1. Revise cada história individual para entender os detalhes
2. Priorize histórias de acordo com as necessidades do time
3. Atribua histórias aos desenvolvedores
4. Monitore progresso através dos critérios de aceite

### Para o Desenvolvedor:
1. Leia a história completa antes de começar a implementação
2. Siga as instruções de implementação passo-a-passo
3. Use o checklist de validação para garantir completion
4. Execute os testes unitários/integração
5. Verifique todos os critérios de aceite
6. Marque a história como "Concluída" quando apropriado

### Para o QA:
1. Use os testes de aceitação listados
2. Valide todos os requisitos funcionais
3. Verifique validações server-side e client-side
4. Teste integração com outras funcionalidades
5. Reporte bugs encontrados

---

## Sequência Recomendada de Implementação

### Fase 1: Estrutura Base (Sprint 1)
- Epic 14 Story 01: Template da Lista de Roles
- Epic 15 Story 01: Template da Lista de Chaves
- Epic 16 Story 01: Template da Lista de Auditoria

### Fase 2: DTOs e Backend (Sprint 2)
- Epic 14 Story 02: DTOs de Role
- Epic 14 Story 03: Service Layer - Role
- Epic 15 Story 02: DTOs de Chave
- Epic 15 Story 03: Service Layer - Chave
- Epic 16 Story 02: DTOs de Auditoria
- Epic 16 Story 03: Service Layer - Auditoria

### Fase 3: Controllers e CRUD (Sprint 3)
- Epic 14 Story 04: Controller API - Role
- Epic 14 Story 05: Modal de Criação/Edição
- Epic 14 Story 06: CRUD - Role
- Epic 15 Story 04: Controller API - Chave
- Epic 15 Story 05: Rotação Manual
- Epic 16 Story 04: Controller API - Auditoria
- Epic 16 Story 05: Filtros Avançados

### Fase 4: Funcionalidades Específicas (Sprint 4)
- Epic 14 Story 07: Roles Padrão
- Epic 14 Story 08: Validações e Feedback
- Epic 15 Story 06: Histórico de Rotações
- Epic 15 Story 07: Countdown de Rotação
- Epic 16 Story 06: Modal de Detalhes
- Epic 16 Story 07: Coloração por Severidade
- Epic 16 Story 08: Exportação CSV

### Fase 5: Integração e Testes Finais (Sprint 5)
- Testes E2E entre epics
- Testes de performance
- Correção de bugs
- Documentação final

---

## Referências

- **PRD:** `docs/prd.md`
- **Architecture:** `docs/architecture.md`
- **Epic 14:** `docs/epic-14-pagina-gestao-roles.md`
- **Epic 15:** `docs/epic-15-pagina-gestao-chaves.md`
- **Epic 16:** `docs/epic-16-pagina-visualizacao-auditoria.md`
- **UI Template:** `src/main/resources/templates/ui.txt`
- **Escopo:** `escopo.md`

---

## Notas Finais

- Todas as histórias seguem o formato estabelecido no Epic 13
- Implementação completa inclui Java, Thymeleaf, JavaScript e CSS
- Validações server-side são obrigatórias
- Validações client-side melhoram a UX
- Mensagens de erro devem ser amigáveis ao usuário
- Testes devem cobrir todos os cenários principais
- Performance deve ser monitorada (páginas < 2s)
- Acessibilidade (WCAG 2.0 AA) deve ser considerada

---

**Última Atualização:** 25 de Dezembro, 2025
**Status:** Pronto para Implementação
