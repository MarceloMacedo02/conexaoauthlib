# Índice de Epics - Dashboard Administrativo (Thymeleaf)

## Visão Geral

Este documento organiza os epics criados especificamente para implementação das páginas do dashboard administrativo, utilizando **Thymeleaf** como template engine e **Bootstrap 5** para estilização, seguindo o padrão do template `ui.txt`.

## Epics por Categoria

### 📋 Autenticação
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 10 | Página de Login (Thymeleaf) | `epic-10-pagina-login-thymeleaf.md` | Alta | 6h |

### 📊 Dashboard
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 11 | Dashboard Principal com Métricas (Thymeleaf) | `epic-11-dashboard-principal-metricas.md` | Alta | 12h |

### 👥 Gestão de Realms
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 12 | Página de Gestão de Realms (Thymeleaf) | `epic-12-pagina-gestao-realms.md` | Alta | 14h |

### 👥 Gestão de Usuários
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 13 | Página de Gestão de Usuários (Thymeleaf) | `epic-13-pagina-gestao-usuarios.md` | Alta | 20h |

### 🔑 Gestão de Roles
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 14 | Página de Gestão de Roles (Thymeleaf) | `epic-14-pagina-gestao-roles.md` | Alta | 12h |

### 🔐 Gestão de Chaves Criptográficas
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 15 | Página de Gestão de Chaves Criptográficas (Thymeleaf) | `epic-15-pagina-gestao-chaves.md` | Alta | 14h |

### 📝 Auditoria
| Epic | Título | Arquivo | Prioridade | Estimativa |
|------|--------|----------|------------|------------|
| Epic 16 | Página de Visualização de Auditoria (Thymeleaf) | `epic-16-pagina-visualizacao-auditoria.md` | Média | 16h |

## Resumo de Esforço

| Categoria | Total Horas |
|-----------|-------------|
| Autenticação | 6h |
| Dashboard | 12h |
| Gestão de Realms | 14h |
| Gestão de Usuários | 20h |
| Gestão de Roles | 12h |
| Gestão de Chaves | 14h |
| Auditoria | 16h |
| **TOTAL** | **110h** |

## Estrutura de Templates

Os epics criam os seguintes templates na estrutura padrão:

```
src/main/resources/templates/admin/
├── login.html                          (Epic 10)
├── dashboard/
│   └── index.html                     (Epic 11)
├── realms/
│   ├── list.html                       (Epic 12)
│   └── form.html
├── usuarios/
│   ├── list.html                       (Epic 13)
│   └── form.html
├── roles/
│   ├── list.html                       (Epic 14)
│   └── form.html
├── chaves/
│   ├── list.html                       (Epic 15)
│   └── historico.html
└── auditoria/
    └── list.html                       (Epic 16)
```

## Tecnologia e Frameworks

### Utilizados nos Epics
- **Template Engine:** Thymeleaf 3.x
- **CSS Framework:** Bootstrap 5
- **Icons:** Tabler Icons (ti-*)
- **Tables:** DataTables.js (já disponível)
- **Date Picker:** Flatpickr (já disponível)
- **Charts:** Chart.js (já disponível)
- **JavaScript:** Vanilla JS + Bootstrap 5

### Layouts
- **`layouts/base`** - Usado para Login (sem sidebar)
- **`layouts/vertical`** - Usado para todas as páginas administrativas (com sidebar)

## Dependências entre Epics

```
Epic 10 (Login)
    └─> Epic 11 (Dashboard)
        └─> Epic 12 (Realms) ──> Epic 14 (Roles) ──> Epic 13 (Usuários)
                                              │
                                              └─> Epic 15 (Chaves)
                                              └─> Epic 16 (Auditoria)
```

## Ordem de Implementação Sugerida (Epics de UI)

1. **Epic 10** (Login) - Acesso básico ao sistema
2. **Epic 11** (Dashboard) - Página principal após login
3. **Epic 12** (Realms) - Base para domínio
4. **Epic 14** (Roles) - Depende de Realms
5. **Epic 13** (Usuários) - Depende de Realms e Roles
6. **Epic 15** (Chaves) - Depende de Realms
7. **Epic 16** (Auditoria) - Depende de todos os anteriores

## Critérios de Qualidade Aplicáveis a Todos os Epics

### UI/UX
- [ ] Design responsivo (mobile-first)
- [ ] Acessibilidade WCAG 2.1 AA
- [ ] Validações client-side e server-side
- [ ] Feedback visual em todas as ações (loading states)
- [ ] Mensagens de erro/sucesso em toasts
- [ ] Ícones significativos e alinhados
- [ ] Cores seguindo o design system

### Técnico
- [ ] Validação com Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`, `@Email`)
- [ ] CSRF token em todos os formulários POST
- [ ] Paginação server-side (não trazer tudo de uma vez)
- [ ] Filtros com otimização de queries
- [ ] Loading states durante requisições AJAX
- [ ] Tratamento de erros consistente
- [ ] Comentários em português

### Performance
- [ ] Tempo de carregamento inicial < 2 segundos
- [ ] API endpoints respondem em < 500ms (listagens)
- [ ] Modais abrem em < 200ms
- [ ] Auto-refresh de dashboard não degrada performance
- [ ] Paginação rápida e fluida
- [ ] Operações de CRUD completas em < 1 segundo

## Padrões de UI Seguindo ui.txt

### Estrutura de Tabela (referência: API Keys template)
```html
<div data-table data-table-rows-per-page="8" class="card">
    <div class="card-header border-light justify-content-between">
        <!-- Filtros e Botões -->
    </div>

    <div class="table-responsive">
        <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
            <thead class="bg-light bg-opacity-25thead-sm">
                <!-- Headers -->
            </thead>
            <tbody>
                <!-- Linhas de dados -->
            </tbody>
        </table>
    </div>

    <div class="card-footer border-0">
        <!-- Paginação -->
    </div>
</div>
```

### Estrutura de Modal
```html
<div class="modal fade" id="modalId" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Título</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <!-- Conteúdo do formulário -->
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
        </div>
    </div>
</div>
```

## Integração com Epics de Backend

| Epic UI | Epic Backend Correspondente | Descrição |
|----------|------------------------|------------|
| Epic 10 | Epic 2 (Gestão de Usuários) | Validação de credenciais |
| Epic 11 | Todos (1, 2, 3, 5, 7) | Métricas agregadas |
| Epic 12 | Epic 1 (Gestão de Realms) | CRUD de realms |
| Epic 13 | Epic 2 (Gestão de Usuários) | CRUD de usuários |
| Epic 14 | Epic 3 (Gestão de Roles) | CRUD de roles |
| Epic 15 | Epic 5 (Gestão de Chaves) | Visualização + rotação manual |
| Epic 16 | Epic 7 (Auditoria) | Consulta de eventos |

## Notas Importantes

### Escopo
- **INCLUÍDO:** Todas as páginas administrativas especificadas no escopo.md
- **EXCLUÍDO:** Funcionalidades de IAM corporativo pesado (conforme escopo.md)
- **INCLUÍDO:** Validações, filtros, paginação, modais
- **EXCLUÍDO:** Widgets complexos, dashboards personalizáveis

### Consistência
- Todos os epics seguem o mesmo padrão de estrutura
- Mesmo estilo de validação e feedback
- Mesma abordagem de modais e toasts
- Consistência na nomenclatura de classes CSS
- Mesmo padrão de API endpoints

### Futuras Melhorias (Fora do Escopo Atual)
- Implementação de WebSocket para atualizações em tempo real
- Dashboard personalizável (drag & drop de widgets)
- Exportação de dados em PDF e Excel
- Gráficos mais avançados (heatmaps, treemaps)
- Detecção de anomalias em auditoria com ML
- Internacionalização (i18n) completa

## Referências

- **PRD:** `docs/prd.md` - Especificação funcional do sistema
- **Escopo:** `escopo.md` - Definição de what's in/out of scope
- **Templates UI:** `src/main/resources/templates/ui.txt` - Padrões de UI a seguir
- **Arquitetura:** `docs/architecture.md` - Estrutura técnica e design patterns
- **Epics Backend:** `docs/EPICS.md` - Epics de backend já existentes

---

**Última Atualização:** 24 de Dezembro de 2025
**Versão:** 1.0
**Responsável:** BMad Scrum Master (Bob) 🏃
