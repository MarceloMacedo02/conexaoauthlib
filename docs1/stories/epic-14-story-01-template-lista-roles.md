# User Story: Template da Lista de Roles (Thymeleaf)

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-01

## Descrição
Criar o template da página de gestão de roles (`src/main/resources/templates/admin/roles/list.html`) utilizando Thymeleaf, layout `layouts/vertical`, Bootstrap 5 e Tabler Icons, seguindo os padrões do arquivo `ui.txt` (API Keys template) e os requisitos do PRD para seção "Gestão de Roles".

## Critérios de Aceite
- [X] Template `admin/roles/list.html` criado com estrutura base
- [X] Layout `layouts/vertical` configurado (com sidebar)
- [X] Page Title fragment implementado com breadcrumb "Apps > Gestão Roles"
- [X] Search input para busca textual de roles (nome, descrição)
- [X] Filtro de Realm (dropdown com todos os realms do usuário logado)
- [X] Filtro de status (All, Ativa, Inativa) funcionando
- [X] Dropdown de registros por página (5, 10, 15, 20)
- [X] Tabela de roles com todas as colunas implementadas
- [X] Checkbox de seleção individual e select-all
- [X] Badges de status (Ativa/Inativa) com cores apropriadas
- [X] Badges de padrão (ADMIN, USER, SERVICE) exibidos com ícone
- [X] Ícones Tabler (`ti-*`) aplicados corretamente
- [X] Paginação controls implementados
- [X] Colunas: Checkbox, Nome, Realm, Descrição, Status, Usuários, Criada Em, Ações
- [X] Página totalmente responsiva (mobile-first)

## Tarefas
1. Criar diretório `templates/admin/roles/` se não existir
2. Criar template `list.html` com estrutura base seguindo API Keys template
3. Implementar Page Title fragment com breadcrumb
4. Criar card com header de filtros e busca
5. Implementar tabela de roles com todas as colunas
6. Adicionar checkbox de seleção e select-all
7. Configurar dropdown de registros por página
8. Adicionar botão de nova role com ícone
9. Implementar paginação footer
10. Configurar Thymeleaf para dados da tabela (mock inicial)

## Instruções de Implementação

### Estrutura do Template
```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Gestão de Roles')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Roles',
                 'Gerencie as roles de acesso do sistema'
                )}">
        </div>

        <!-- Card com Tabela de Roles -->
        <div class="row">
            <div class="col-12">
                <div data-table data-table-rows-per-page="8" class="card">
                    <!-- Card Header com Filtros e Botões -->
                    <div class="card-header border-light justify-content-between">
                        <div class="d-flex gap-2">
                            <div class="app-search">
                                <input data-table-search type="text" class="form-control"
                                    placeholder="Buscar roles por nome ou descrição...">
                                <i data-lucide="search" class="app-search-icon text-muted"></i>
                            </div>
                            <button type="button" class="btn btn-primary btn-icon" data-bs-toggle="modal"
                                data-bs-target="#addRoleModal"><i class="ti ti-plus fs-lg"></i></button>
                            <button data-table-delete-selected class="btn btn-danger d-none">Delete</button>
                        </div>

                        <div class="d-flex align-items-center gap-2">
                            <span class="me-2 fw-semibold">Filtrar Por:</span>

                            <!-- Realm Filter -->
                            <div class="app-search">
                                <select data-table-filter="realm" class="form-select form-control my-1 my-md-0">
                                    <option value="All">Todos os Realms</option>
                                    <option value="master">Master Realm</option>
                                    <option value="empresa-a">Empresa A</option>
                                    <option value="empresa-b">Empresa B</option>
                                    <option value="empresa-c">Empresa C</option>
                                </select>
                                <i data-lucide="building-cog" class="app-search-icon text-muted"></i>
                            </div>

                            <!-- Status Filter -->
                            <div class="app-search">
                                <select data-table-filter="status" class="form-select form-control my-1 my-md-0">
                                    <option value="All">Status</option>
                                    <option value="Ativa">Ativa</option>
                                    <option value="Inativa">Inativa</option>
                                </select>
                                <i data-lucide="circle-check" class="app-search-icon text-muted"></i>
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

                    <!-- Tabela de Roles -->
                    <div class="table-responsive">
                        <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                            <thead class="bg-light bg-opacity-25thead-sm">
                                <tr class="text-uppercase fs-xxs">
                                    <th scope="col" style="width: 1%;">
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            data-table-select-all type="checkbox" value="option">
                                    </th>
                                    <th data-table-sort>Nome</th>
                                    <th data-table-sort>Realm</th>
                                    <th>Descrição</th>
                                    <th data-table-sort="status">Status</th>
                                    <th data-table-sort>Usuários</th>
                                    <th data-table-sort>Criada Em</th>
                                    <th class="text-center">Ações</th>
                                </tr>
                            </thead><!-- end table-head -->
                            <tbody>
                                <!-- Dados mockados iniciais -->
                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="badge bg-primary me-2">
                                            <i class="ti ti-star me-1"></i>Padrão
                                        </span>
                                        <span class="fw-medium">ADMIN</span>
                                    </td>
                                    <td>Master Realm</td>
                                    <td>Administrador do sistema com acesso total</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                    </td>
                                    <td>3</td>
                                    <td>Dez 15, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="badge bg-primary me-2">
                                            <i class="ti ti-star me-1"></i>Padrão
                                        </span>
                                        <span class="fw-medium">USER</span>
                                    </td>
                                    <td>Master Realm</td>
                                    <td>Usuário padrão com acesso limitado</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                    </td>
                                    <td>25</td>
                                    <td>Dez 15, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="badge bg-primary me-2">
                                            <i class="ti ti-star me-1"></i>Padrão
                                        </span>
                                        <span class="fw-medium">SERVICE</span>
                                    </td>
                                    <td>Master Realm</td>
                                    <td>Role para serviços e integrações</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                    </td>
                                    <td>5</td>
                                    <td>Dez 15, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="fw-medium">GERENTE</span>
                                    </td>
                                    <td>Empresa A</td>
                                    <td>Gerente com permissões de gestão</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                    </td>
                                    <td>8</td>
                                    <td>Jan 10, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Remover"><i
                                                    class="ti ti-trash fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="fw-medium">ANALISTA</span>
                                    </td>
                                    <td>Empresa A</td>
                                    <td>Analista com acesso apenas leitura</td>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger badge-label">Inativa</span>
                                    </td>
                                    <td>0</td>
                                    <td>Mar 5, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Remover"><i
                                                    class="ti ti-trash fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <span class="fw-medium">DESENVOLVEDOR</span>
                                    </td>
                                    <td>Empresa B</td>
                                    <td>Desenvolvedor com acesso técnico</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativa</span>
                                    </td>
                                    <td>12</td>
                                    <td>Apr 22, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Remover"><i
                                                    class="ti ti-trash fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                            </tbody><!-- end table-body -->
                        </table><!-- end table -->
                    </div>

                    <!-- Paginação -->
                    <div class="card-footer border-0">
                        <div class="d-flex justify-content-between align-items-center">
                            <div data-table-pagination-info="roles"></div>
                            <div data-table-pagination></div>
                        </div>
                    </div>
                </div>
            </div><!-- end col -->
        </div><!-- end row -->

        <!-- O conteúdo do modal será implementado na Story 05 -->

    </th:block>

    <th:block layout:fragment="javascripts">
        <!-- Custom table -->
        <script src="/js/pages/custom-table.js"></script>

        <!-- Roles page js (será implementado nas próximas histórias) -->
    </th:block>
</body>
</html>
```

### Checklist de Validação
- [X] Template `admin/roles/list.html` criado
- [X] Layout `layouts/vertical` configurado
- [X] Page Title com breadcrumb "Apps > Gestão Roles"
- [X] Search input implementado com ícone
- [X] Filtro de Realm (dropdown) funcional
- [X] Filtro de status (All, Ativa, Inativa) funcional
- [X] Dropdown de registros por página (5, 10, 15, 20)
- [X] Tabela com todas as colunas: Checkbox, Nome, Realm, Descrição, Status, Usuários, Criada Em, Ações
- [X] Checkbox de seleção individual
- [X] Checkbox select-all no header
- [X] Badges de status com cores apropriadas (Ativa=verde, Inativa=vermelho)
- [X] Badges de padrão com ícone (ADMIN, USER, SERVICE)
- [X] Botões de ação: View (ti-eye), Edit (ti-edit), Delete (ti-trash)
- [X] Paginação footer com info e controls
- [X] Ícones Tabler carregados corretamente
- [X] Classes Bootstrap 5 aplicadas corretamente
- [X] Página responsiva em dispositivos móveis

## Anotações
- Seguir exatamente o padrão do template `ui.txt` (API Keys section)
- Utilizar layout `layouts/vertical` (com sidebar)
- Badges de Realm usar classes `bg-primary` para Master e `bg-secondary` para outros
- Badges de status usar classes `bg-success-subtle text-success` para ativo e `bg-danger-subtle text-danger` para inativo
- Badges de padrão usar classe `bg-primary` com ícone `ti ti-star`
- Dados mockados podem ser usados inicialmente (serão substituídos por dados reais nas próximas histórias)
- Esta história cria apenas a estrutura do template; a funcionalidade completa será implementada nas histórias seguintes

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 3 (Gestão de Roles) - backend CRUD já implementado
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base da página de gestão de roles

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
