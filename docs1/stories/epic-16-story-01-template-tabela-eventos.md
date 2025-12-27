# User Story: Template da Tabela de Eventos de Auditoria

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-01

## Descrição
Criar o template da página de auditoria (`src/main/resources/templates/admin/auditoria/list.html`) utilizando Thymeleaf, layout `layouts/vertical`, Bootstrap 5, seguindo os padrões do arquivo `ui.txt` (API Keys template) e os requisitos do PRD para seção "Auditoria".

## Critérios de Aceite
- [ ] Template `admin/auditoria/list.html` criado com estrutura base
- [ ] Layout `layouts/vertical` configurado (com sidebar)
- [ ] Page Title fragment implementado com breadcrumb "Apps > Auditoria"
- [ ] Search input para busca textual de eventos (detalhes, usuário, IP)
- [ ] Filtro de Realm (dropdown com todos os realms)
- [ ] Filtro de Tipo de Evento (dropdown com optgroups)
- [ ] Filtro de Período (data inicial e final)
- [ ] Filtro de Usuário (text input)
- [ ] Dropdown de registros por página (5, 10, 15, 20)
- [ ] Tabela de eventos com todas as colunas implementadas
- [ ] Badges de severidade (CRITICO, WARNING, INFO, SUCCESS)
- [ ] Badges de tipo de evento com ícones
- [ ] Ícones Tabler (`ti-*`) aplicados corretamente
- [ ] Paginação controls implementados
- [ ] Colunas: Tipo, Usuário, Realm, Detalhes, IP Origem, Data, Ações
- [ ] Botão de exportar CSV
- [ ] Página totalmente responsiva (mobile-first)

## Tarefas
1. Criar diretório `templates/admin/auditoria/` se não existir
2. Criar template `list.html` com estrutura base seguindo API Keys template
3. Implementar Page Title fragment com breadcrumb
4. Criar card com header de filtros e busca
5. Implementar filtros avançados (Realm, Tipo, Período, Usuário)
6. Implementar tabela de eventos com todas as colunas
7. Adicionar botão de exportar CSV
8. Configurar dropdown de registros por página
9. Implementar paginação footer
10. Configurar Thymeleaf para dados da tabela (mock inicial)

## Instruções de Implementação

### Template: admin/auditoria/list.html
**Localização:** `src/main/resources/templates/admin/auditoria/list.html`

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Auditoria')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Auditoria',
                 'Visualize todos os eventos de segurança do sistema'
                )}">
        </div>

        <!-- Card com Tabela de Eventos de Auditoria -->
        <div class="row">
            <div class="col-12">
                <div data-table data-table-rows-per-page="10" class="card">
                    <!-- Card Header com Filtros e Botões -->
                    <div class="card-header border-light justify-content-between">
                        <div class="d-flex gap-2">
                            <div class="app-search">
                                <input data-table-search type="text" class="form-control"
                                    placeholder="Buscar eventos...">
                                <i data-lucide="search" class="app-search-icon text-muted"></i>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="exportarCSV()">
                                <i class="ti ti-download me-2"></i>Exportar CSV
                            </button>
                        </div>

                        <div class="d-flex align-items-center gap-2">
                            <span class="me-2 fw-semibold">Filtrar Por:</span>

                            <!-- Realm Filter -->
                            <div class="app-search">
                                <select id="realmFilter" class="form-select form-control my-1 my-md-0">
                                    <option value="All">Todos os Realms</option>
                                    <option value="master">Master Realm</option>
                                    <option value="empresa-a">Empresa A</option>
                                    <option value="empresa-b">Empresa B</option>
                                    <option value="empresa-c">Empresa C</option>
                                </select>
                                <i data-lucide="building-cog" class="app-search-icon text-muted"></i>
                            </div>

                            <!-- Tipo de Evento Filter -->
                            <div class="app-search">
                                <select id="tipoEventoFilter" class="form-select form-control my-1 my-md-0">
                                    <option value="All">Todos os Tipos</option>
                                    <optgroup label="Autenticação">
                                        <option value="LOGIN">Login</option>
                                        <option value="LOGOUT">Logout</option>
                                        <option value="FALHA_LOGIN">Falha de Login</option>
                                    </optgroup>
                                    <optgroup label="Gestão de Usuários">
                                        <option value="CRIACAO_USUARIO">Criação de Usuário</option>
                                        <option value="EDICAO_USUARIO">Edição de Usuário</option>
                                        <option value="REMOCAO_USUARIO">Remoção de Usuário</option>
                                        <option value="RESET_SENHA">Reset de Senha</option>
                                    </optgroup>
                                    <optgroup label="Gestão de Realms">
                                        <option value="CRIACAO_REALM">Criação de Realm</option>
                                        <option value="EDICAO_REALM">Edição de Realm</option>
                                        <option value="DESATIVACAO_REALM">Desativação de Realm</option>
                                    </optgroup>
                                    <optgroup label="Gestão de Chaves">
                                        <option value="ROTACAO_MANUAL">Rotação Manual</option>
                                        <option value="ROTACAO_AUTOMATICA">Rotação Automática</option>
                                    </optgroup>
                                    <optgroup label="Segurança">
                                        <option value="TENTATIVA_BRUTE_FORCE">Tentativa Brute Force</option>
                                        <option value="ACESSO_NEGADO">Acesso Negado</option>
                                    </optgroup>
                                </select>
                                <i data-lucide="flag" class="app-search-icon text-muted"></i>
                            </div>

                            <!-- Período Filter (Data Range) -->
                            <div class="d-flex gap-1 align-items-center">
                                <div>
                                    <input type="date" id="dataInicial" class="form-control form-control-sm"
                                           data-provider="flatpickr" data-date-format="d M, Y"
                                           placeholder="Data Inicial">
                                </div>
                                <span class="text-muted">-</span>
                                <div>
                                    <input type="date" id="dataFinal" class="form-control form-control-sm"
                                           data-provider="flatpickr" data-date-format="d M, Y"
                                           placeholder="Data Final">
                                </div>
                            </div>

                            <!-- Usuário Filter -->
                            <div class="app-search">
                                <input type="text" id="usuarioFilter" class="form-control my-1 my-md-0"
                                       placeholder="Usuário...">
                                <i data-lucide="user" class="app-search-icon text-muted"></i>
                            </div>

                            <!-- Records Per Page -->
                            <div>
                                <select data-table-set-rows-per-page class="form-select form-control my-1 my-md-0">
                                    <option value="5">5</option>
                                    <option value="10">10</option>
                                    <option value="15">15</option>
                                    <option value="20">20</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <!-- Tabela de Eventos de Auditoria -->
                    <div class="table-responsive">
                        <table id="auditoriaTable" class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                            <thead class="bg-light bg-opacity-25thead-sm">
                                <tr class="text-uppercase fs-xxs">
                                    <th data-table-sort>Tipo</th>
                                    <th data-table-sort>Usuário</th>
                                    <th data-table-sort>Realm</th>
                                    <th data-table-sort>Detalhes</th>
                                    <th data-table-sort>IP Origem</th>
                                    <th data-table-sort>Data</th>
                                    <th class="text-center">Ações</th>
                                </tr>
                            </thead><!-- end table-head -->
                            <tbody id="auditoriaTableBody">
                                <!-- Dados mockados iniciais -->
                                <tr>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">
                                            <i class="ti ti-login me-1"></i>Login
                                        </span>
                                    </td>
                                    <td>joao.admin@empresa.com</td>
                                    <td>Master Realm</td>
                                    <td class="text-truncate" style="max-width: 200px;">Login bem-sucedido</td>
                                    <td>192.168.1.100</td>
                                    <td>Dez 25, 2025 10:30</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <button class="btn btn-default btn-icon btn-sm rounded"
                                                    onclick="visualizarDetalhes('evento-1')"
                                                    title="Visualizar Detalhes">
                                                <i class="ti ti-eye fs-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger badge-label">
                                            <i class="ti ti-x-circle me-1"></i>Falha de Login
                                        </span>
                                    </td>
                                    <td>maria.silva@empresa.com</td>
                                    <td>Empresa A</td>
                                    <td class="text-truncate" style="max-width: 200px;">Senha incorreta</td>
                                    <td>192.168.1.101</td>
                                    <td>Dez 25, 2025 10:28</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <button class="btn btn-default btn-icon btn-sm rounded"
                                                    onclick="visualizarDetalhes('evento-2')"
                                                    title="Visualizar Detalhes">
                                                <i class="ti ti-eye fs-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <span class="badge bg-info-subtle text-info badge-label">
                                            <i class="ti ti-user-plus me-1"></i>Criação de Usuário
                                        </span>
                                    </td>
                                    <td>admin@plataforma.com</td>
                                    <td>Master Realm</td>
                                    <td class="text-truncate" style="max-width: 200px;">Usuário criado: novo.usuario@empresa.com</td>
                                    <td>192.168.1.50</td>
                                    <td>Dez 25, 2025 09:15</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <button class="btn btn-default btn-icon btn-sm rounded"
                                                    onclick="visualizarDetalhes('evento-3')"
                                                    title="Visualizar Detalhes">
                                                <i class="ti ti-eye fs-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <span class="badge bg-warning-subtle text-warning badge-label">
                                            <i class="ti ti-rotate me-1"></i>Rotação Manual
                                        </span>
                                    </td>
                                    <td>joao.admin@empresa.com</td>
                                    <td>Empresa A</td>
                                    <td class="text-truncate" style="max-width: 200px;">Chave rotacionada por segurança</td>
                                    <td>192.168.1.100</td>
                                    <td>Dez 24, 2025 14:45</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <button class="btn btn-default btn-icon btn-sm rounded"
                                                    onclick="visualizarDetalhes('evento-4')"
                                                    title="Visualizar Detalhes">
                                                <i class="ti ti-eye fs-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger badge-label">
                                            <i class="ti ti-shield-alert me-1"></i>Tentativa Brute Force
                                        </span>
                                    </td>
                                    <td>unknown@attacker.com</td>
                                    <td>Master Realm</td>
                                    <td class="text-truncate" style="max-width: 200px;">Múltiplas tentativas de login bloqueadas</td>
                                    <td>45.33.22.11</td>
                                    <td>Dez 24, 2025 11:22</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <button class="btn btn-default btn-icon btn-sm rounded"
                                                    onclick="visualizarDetalhes('evento-5')"
                                                    title="Visualizar Detalhes">
                                                <i class="ti ti-eye fs-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                            </tbody><!-- end table-body -->
                        </table><!-- end table -->
                    </div>

                    <!-- Paginação -->
                    <div class="card-footer border-0">
                        <div class="d-flex justify-content-between align-items-center">
                            <div data-table-pagination-info="auditoria"></div>
                            <div data-table-pagination></div>
                        </div>
                    </div>
                </div>
            </div><!-- end col -->
        </div><!-- end row -->

        <!-- Modal de Detalhes do Evento será implementado na Story 06 -->

    </th:block>

    <th:block layout:fragment="javascripts">
        <!-- Custom table -->
        <script src="/js/pages/custom-table.js"></script>

        <!-- Auditoria page js (será implementado nas próximas histórias) -->
        <script src="/js/pages/auditoria.js"></script>
    </th:block>
</body>
</html>
```

## Checklist de Validação
- [ ] Template `admin/auditoria/list.html` criado
- [ ] Layout `layouts/vertical` configurado
- [ ] Page Title com breadcrumb "Apps > Auditoria"
- [ ] Search input implementado com ícone
- [ ] Filtro de Realm (dropdown) funcional
- [ ] Filtro de Tipo de Evento (com optgroups) funcional
- [ ] Filtro de Período (data inicial e final) funcional
- [ ] Filtro de Usuário (text input) funcional
- [ ] Dropdown de registros por página (5, 10, 15, 20)
- [ ] Tabela com todas as colunas: Tipo, Usuário, Realm, Detalhes, IP Origem, Data, Ações
- [ ] Badges de tipo de evento com ícones
- [ ] Badges de severidade com cores diferentes
- [ ] Botão de exportar CSV
- [ ] Paginação footer com info e controls
- [ ] Ícones Tabler carregados corretamente
- [ ] Classes Bootstrap 5 aplicadas corretamente
- [ ] Página responsiva em dispositivos móveis
- [ ] Optgroups para tipos de evento organizados

## Anotações
- Seguir exatamente o padrão do template `ui.txt` (API Keys section)
- Utilizar layout `layouts/vertical` (com sidebar)
- Badges de tipo usam ícones Tabler específicos por categoria
- Tipos de evento organizados em optgroups (Autenticação, Gestão de Usuários, etc.)
- Filtro de período usa Flatpickr para seleção de data
- Botão de exportar CSV será implementado na Story 08
- Modal de detalhes será implementado na Story 06
- Dados mockados podem ser usados inicialmente
- Esta história cria apenas a estrutura do template; a funcionalidade completa será implementada nas histórias seguintes

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 7 (Auditoria) - backend já implementado
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base da página de auditoria

## Estimativa
- Implementação: 3.5 horas
- Testes: 1 hora
- Total: 4.5 horas
