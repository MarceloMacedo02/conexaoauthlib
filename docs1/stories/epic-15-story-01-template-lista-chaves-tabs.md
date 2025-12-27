# User Story: Template da Lista de Chaves com Tabs (Chaves Ativas e Histórico)

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-01

## Descrição
Criar o template da página de gestão de chaves criptográficas (`src/main/resources/templates/admin/chaves/list.html`) utilizando Thymeleaf, layout `layouts/vertical`, Bootstrap 5 e Tabs para alternar entre "Chaves Ativas" e "Histórico de Rotações", seguindo os padrões do arquivo `ui.txt`.

## Critérios de Aceite
- [ ] Template `admin/chaves/list.html` criado com estrutura base
- [ ] Layout `layouts/vertical` configurado (com sidebar)
- [ ] Page Title fragment implementado com breadcrumb "Apps > Gestão Chaves"
- [ ] Tabs Bootstrap 5 implementados (Chaves Ativas, Histórico)
- [ ] Tab de Chaves Ativas com tabela de chaves
- [ ] Tab de Histórico com tabela de rotações
- [ ] Filtro de Realm na tab de chaves ativas
- [ ] Alerta de próxima rotação automática exibido
- [ ] Countdown de dias para próxima rotação
- [ ] Ícones Tabler (`ti-*`) aplicados corretamente
- [ ] Tabela de chaves com colunas: Realm, Versão, Tipo, Criada Em, Expira Em, Status, Ações
- [ ] Tabela de histórico com colunas: Realm, Versão Antiga, Versão Nova, Data, Responsável, Motivo, Status
- [ ] Página totalmente responsiva

## Tarefas
1. Criar diretório `templates/admin/chaves/` se não existir
2. Criar template `list.html` com estrutura base
3. Implementar Page Title fragment com breadcrumb
4. Criar estrutura de tabs Bootstrap 5
5. Implementar tab de Chaves Ativas com tabela
6. Implementar tab de Histórico com tabela
7. Adicionar filtro de realm
8. Implementar alerta de próxima rotação automática
9. Implementar countdown de dias
10. Configurar JavaScript para navegação de tabs

## Instruções de Implementação

### Template: admin/chaves/list.html
**Localização:** `src/main/resources/templates/admin/chaves/list.html`

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Gestão de Chaves')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Chaves',
                 'Gerencie as chaves criptográficas e rotações'
                )}">
        </div>

        <!-- Tabs e Tabelas -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <!-- Tabs Header -->
                    <div class="card-header border-0">
                        <ul class="nav nav-tabs nav-bordered card-header-tabs" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active"
                                   data-bs-toggle="tab"
                                   data-bs-target="#chavesAtivas"
                                   role="tab">
                                    <i class="ti ti-key me-2"></i>
                                    Chaves Ativas
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link"
                                   data-bs-toggle="tab"
                                   data-bs-target="#historicoRotacoes"
                                   role="tab">
                                    <i class="ti ti-history me-2"></i>
                                    Histórico de Rotações
                                </a>
                            </li>
                        </ul>
                    </div>

                    <div class="card-body">
                        <!-- Tab: Chaves Ativas -->
                        <div class="tab-pane fade show active" id="chavesAtivas" role="tabpanel">
                            <!-- Filtros e Alertas -->
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Selecionar Realm</label>
                                    <select id="realmFilter" class="form-select">
                                        <option value="All">Todos os Realms</option>
                                        <option value="master">Master Realm</option>
                                        <option value="empresa-a">Empresa A</option>
                                        <option value="empresa-b">Empresa B</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Próxima Rotação Automática</label>
                                    <div class="alert alert-info mb-0">
                                        <i class="ti ti-calendar me-2"></i>
                                        <span id="proximaRotacao">Dia 1 do próximo mês</span>
                                        <span class="badge bg-warning ms-2">
                                            <span id="countdownRotacao">-- dias</span>
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <!-- Tabela de Chaves Ativas -->
                            <div class="table-responsive">
                                <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                                    <thead class="bg-light bg-opacity-25thead-sm">
                                        <tr class="text-uppercase fs-xxs">
                                            <th data-table-sort>Realm</th>
                                            <th data-table-sort>Versão (kid)</th>
                                            <th data-table-sort>Tipo</th>
                                            <th data-table-sort>Criada Em</th>
                                            <th data-table-sort>Expira Em</th>
                                            <th data-table-sort data-column="status">Status</th>
                                            <th class="text-center">Ações</th>
                                        </tr>
                                    </thead>
                                    <tbody id="chavesTableBody">
                                        <!-- Dados mockados iniciais -->
                                        <tr>
                                            <td class="fw-medium">Master Realm</td>
                                            <td><code>key-2025-01</code></td>
                                            <td>RSA-2048</td>
                                            <td>Dez 01, 2025</td>
                                            <td>
                                                Jan 01, 2026
                                                <span class="badge bg-success ms-2">30 dias</span>
                                            </td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center justify-content-center gap-1">
                                                    <button class="btn btn-default btn-icon btn-sm rounded"
                                                            onclick="confirmarRotacao('master', 'key-2025-01')"
                                                            title="Rotacionar Chave">
                                                        <i class="ti ti-rotate fs-lg"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="fw-medium">Empresa A</td>
                                            <td><code>key-2025-02</code></td>
                                            <td>RSA-2048</td>
                                            <td>Nov 15, 2025</td>
                                            <td>
                                                Jan 15, 2026
                                                <span class="badge bg-success ms-2">50 dias</span>
                                            </td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center justify-content-center gap-1">
                                                    <button class="btn btn-default btn-icon btn-sm rounded"
                                                            onclick="confirmarRotacao('empresa-a', 'key-2025-02')"
                                                            title="Rotacionar Chave">
                                                        <i class="ti ti-rotate fs-lg"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="fw-medium">Empresa B</td>
                                            <td><code>key-2024-12</code></td>
                                            <td>RSA-2048</td>
                                            <td>Out 01, 2025</td>
                                            <td>
                                                Dez 01, 2025
                                                <span class="badge bg-warning ms-2">6 dias</span>
                                            </td>
                                            <td>
                                                <span class="badge bg-warning-subtle text-warning badge-label">Expirando</span>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center justify-content-center gap-1">
                                                    <button class="btn btn-warning btn-icon btn-sm rounded"
                                                            onclick="confirmarRotacao('empresa-b', 'key-2024-12')"
                                                            title="Rotacionar Chave Urgente">
                                                        <i class="ti ti-rotate fs-lg"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Tab: Histórico de Rotações -->
                        <div class="tab-pane fade" id="historicoRotacoes" role="tabpanel">
                            <div class="table-responsive">
                                <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                                    <thead class="bg-light bg-opacity-25thead-sm">
                                        <tr class="text-uppercase fs-xxs">
                                            <th data-table-sort>Realm</th>
                                            <th data-table-sort>Versão Antiga</th>
                                            <th data-table-sort>Versão Nova</th>
                                            <th data-table-sort>Data</th>
                                            <th data-table-sort>Responsável</th>
                                            <th data-table-sort>Motivo</th>
                                            <th data-table-sort data-column="status">Status</th>
                                        </tr>
                                    </thead>
                                    <tbody id="historicoTableBody">
                                        <!-- Dados mockados iniciais -->
                                        <tr>
                                            <td class="fw-medium">Master Realm</td>
                                            <td><code>key-2024-11</code></td>
                                            <td><code>key-2025-01</code></td>
                                            <td>Dez 01, 2025 10:00</td>
                                            <td>admin@plataforma.com</td>
                                            <td>Rotação mensal automática</td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Concluída</span>
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="fw-medium">Empresa A</td>
                                            <td><code>key-2024-10</code></td>
                                            <td><code>key-2025-02</code></td>
                                            <td>Nov 15, 2025 14:30</td>
                                            <td>joao.admin@empresa.com</td>
                                            <td>Rotação manual por segurança</td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Concluída</span>
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="fw-medium">Master Realm</td>
                                            <td><code>key-2024-10</code></td>
                                            <td><code>key-2024-11</code></td>
                                            <td>Nov 01, 2025 00:00</td>
                                            <td>Sistema</td>
                                            <td>Rotação mensal automática</td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Concluída</span>
                                            </td>
                                        </tr>

                                        <tr>
                                            <td class="fw-medium">Empresa B</td>
                                            <td><code>key-2024-08</code></td>
                                            <td><code>key-2024-12</code></td>
                                            <td>Out 01, 2025 09:15</td>
                                            <td>maria.gestor@empresa.com</td>
                                            <td>-</td>
                                            <td>
                                                <span class="badge bg-success-subtle text-success badge-label">Concluída</span>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- O conteúdo do modal de rotação será implementado na Story 04 -->

    </th:block>

    <th:block layout:fragment="javascripts">
        <!-- Custom table -->
        <script src="/js/pages/custom-table.js"></script>

        <!-- Chaves page js (será implementado nas próximas histórias) -->
        <script src="/js/pages/chaves.js"></script>
    </th:block>
</body>
</html>
```

### JavaScript Básico
**Adicionar a `src/main/resources/static/js/pages/chaves.js`:**

```javascript
// Carregar chaves ao mudar filtro de realm
document.getElementById('realmFilter').addEventListener('change', function() {
    const realmId = this.value;
    if (realmId === 'All') {
        carregarTodasChaves();
    } else {
        carregarChavesPorRealm(realmId);
        carregarHistoricoPorRealm(realmId);
    }
});

// Calcular countdown para próxima rotação automática
function atualizarCountdownRotacao() {
    const hoje = new Date();
    const proximaRotacao = new Date();
    proximaRotacao.setMonth(proximaRotacao.getMonth() + 1);
    proximaRotacao.setDate(1);
    proximaRotacao.setHours(0, 0, 0, 0);

    const diff = proximaRotacao - hoje;
    const dias = Math.ceil(diff / (1000 * 60 * 60 * 24));

    document.getElementById('countdownRotacao').textContent = dias + ' dias';
}

// Atualizar countdown a cada hora
setInterval(atualizarCountdownRotacao, 3600000);
atualizarCountdownRotacao(); // Executar imediatamente
```

## Checklist de Validação
- [ ] Template `admin/chaves/list.html` criado
- [ ] Layout `layouts/vertical` configurado
- [ ] Page Title com breadcrumb "Apps > Gestão Chaves"
- [ ] Tabs Bootstrap 5 funcionando
- [ ] Tab Chaves Ativas exibida
- [ ] Tab Histórico de Rotações exibida
- [ ] Filtro de Realm implementado
- [ ] Alerta de próxima rotação automática visível
- [ ] Countdown de dias funcionando
- [ ] Tabela de chaves com todas as colunas
- [ ] Tabela de histórico com todas as colunas
- [ ] Ícones Tabler carregados corretamente
- [ ] Página responsiva em dispositivos móveis

## Anotações
- Tabs permitem fácil navegação entre chaves ativas e histórico
- Countdown atualiza automaticamente a cada hora
- Badges de status com cores: Ativa (verde), Expirando (amarelo), Expirada (vermelho)
- Código da versão (kid) exibido em tag `<code>`
- Dados mockados podem ser usados inicialmente
- Esta história cria apenas a estrutura do template

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 5 (Gestão de Chaves) - backend já implementado
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base da página de gestão de chaves

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
