# User Story: Template da Lista de Usuários (Thymeleaf)

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-01

## Descrição
Criar o template da página de gestão de usuários (`src/main/resources/templates/admin/usuarios/list.html`) utilizando Thymeleaf, layout `layouts/vertical`, Bootstrap 5 e Tabler Icons, seguindo os padrões do arquivo `ui.txt` (API Keys template) e os requisitos do PRD para seção "Gestão de Usuários".

## Critérios de Aceite
- [X] Template `admin/usuarios/list.html` criado com estrutura base
- [X] Layout `layouts/vertical` configurado (com sidebar)
- [X] Page Title fragment implementado com breadcrumb "Apps > Gestão Usuários"
- [X] Search input para busca textual de usuários (nome, email)
- [X] Filtro de Realm (dropdown com todos os realms do usuário logado)
- [X] Filtro de status (All, Ativo, Bloqueado) funcionando
- [X] Dropdown de registros por página (5, 10, 15, 20)
- [X] Tabela de usuários com todas as colunas implementadas
- [X] Checkbox de seleção individual e select-all
- [X] Badges de status (Ativo/Bloqueado) com cores apropriadas
- [X] Badges de roles exibidos com cores diferentes por tipo
- [X] Ícones Tabler (`ti-*`) aplicados corretamente
- [X] Paginação controls implementados
- [X] Colunas: Checkbox, Nome, Email, Realm, Roles, Status, Criado Em, Ações
- [X] Página totalmente responsiva (mobile-first)

## Tarefas
1. Criar diretório `templates/admin/usuarios/` se não existir
2. Criar template `list.html` com estrutura base seguindo API Keys template
3. Implementar Page Title fragment com breadcrumb
4. Criar card com header de filtros e busca
5. Implementar tabela de usuários com todas as colunas
6. Adicionar checkbox de seleção e select-all
7. Configurar dropdown de registros por página
8. Adicionar botão de novo usuário com ícone
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
        <th:block th:replace="~{partials/title-meta :: title-meta('Gestão de Usuários')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Usuários',
                 'Gerencie todos os usuários do sistema'
                )}">
        </div>

        <!-- Card com Tabela de Usuários -->
        <div class="row">
            <div class="col-12">
                <div data-table data-table-rows-per-page="8" class="card">
                    <!-- Card Header com Filtros e Botões -->
                    <div class="card-header border-light justify-content-between">
                        <div class="d-flex gap-2">
                            <div class="app-search">
                                <input data-table-search type="text" class="form-control"
                                    placeholder="Buscar usuários por nome ou email...">
                                <i data-lucide="search" class="app-search-icon text-muted"></i>
                            </div>
                            <button type="button" class="btn btn-primary btn-icon" data-bs-toggle="modal"
                                data-bs-target="#addUsuarioModal"><i class="ti ti-plus fs-lg"></i></button>
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
                                    <option value="Ativo">Ativo</option>
                                    <option value="Bloqueado">Bloqueado</option>
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

                    <!-- Tabela de Usuários -->
                    <div class="table-responsive">
                        <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                            <thead class="bg-light bg-opacity-25 thead-sm">
                                <tr class="text-uppercase fs-xxs">
                                    <th scope="col" style="width: 1%;">
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            data-table-select-all type="checkbox" value="option">
                                    </th>
                                    <th data-table-sort>Nome</th>
                                    <th data-table-sort>Email</th>
                                    <th>Realm</th>
                                    <th>Roles</th>
                                    <th data-table-sort="status">Status</th>
                                    <th data-table-sort>Criado Em</th>
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
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-primary text-white">
                                                    JA
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    João Admin
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">joao.admin@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-primary badge-label">Master</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger">ADMIN</span>
                                        <span class="badge bg-info-subtle text-info">USER</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>Dez 15, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Resetar Senha"><i
                                                    class="ti ti-key fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-success text-white">
                                                    MS
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    Maria Silva
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">maria.silva@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-secondary badge-label">Empresa A</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info-subtle text-info">USER</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>Jan 10, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-danger btn-icon btn-sm rounded" title="Bloquear"><i
                                                    class="ti ti-lock fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-warning text-white">
                                                    PC
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    Pedro Costa
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">pedro.costa@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-secondary badge-label">Empresa B</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info-subtle text-info">USER</span>
                                        <span class="badge bg-warning-subtle text-warning">SERVICE</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger badge-label">Bloqueado</span>
                                    </td>
                                    <td>Mar 5, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-success btn-icon btn-sm rounded" title="Ativar"><i
                                                    class="ti ti-lock-open fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-info text-white">
                                                    AO
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    Ana Oliveira
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">ana.oliveira@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-secondary badge-label">Empresa A</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger">ADMIN</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>Apr 22, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-danger btn-icon btn-sm rounded" title="Bloquear"><i
                                                    class="ti ti-lock fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-purple text-white">
                                                    RS
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    Roberto Santos
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">roberto.santos@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-secondary badge-label">Empresa C</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info-subtle text-info">USER</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>May 15, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-danger btn-icon btn-sm rounded" title="Bloquear"><i
                                                    class="ti ti-lock fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-start align-items-center gap-2">
                                            <div class="avatar avatar-xs">
                                                <div class="avatar-title rounded-circle bg-pink text-white">
                                                    CL
                                                </div>
                                            </div>
                                            <div>
                                                <h5 data-sort="nome" class="text-nowrap fw-medium fs-sm mb-0 lh-base">
                                                    Carolina Lima
                                                </h5>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="fw-medium">carolina.lima@empresa.com.br</td>
                                    <td>
                                        <span class="badge bg-secondary badge-label">Empresa B</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info-subtle text-info">USER</span>
                                        <span class="badge bg-warning-subtle text-warning">SERVICE</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>Jul 8, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Visualizar"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded" title="Editar"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-danger btn-icon btn-sm rounded" title="Bloquear"><i
                                                    class="ti ti-lock fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                            </tbody><!-- end table-body -->
                        </table><!-- end table -->
                    </div>

                    <!-- Paginação -->
                    <div class="card-footer border-0">
                        <div class="d-flex justify-content-between align-items-center">
                            <div data-table-pagination-info="usuarios"></div>
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

        <!-- Usuários page js (será implementado nas próximas histórias) -->
    </th:block>
</body>
</html>
```

### Checklist de Validação
- [ ] Template `admin/usuarios/list.html` criado
- [ ] Layout `layouts/vertical` configurado
- [ ] Page Title com breadcrumb "Apps > Gestão Usuários"
- [ ] Search input implementado com ícone
- [ ] Filtro de Realm (dropdown) funcional
- [ ] Filtro de status (All, Ativo, Bloqueado) funcional
- [ ] Dropdown de registros por página (5, 10, 15, 20)
- [ ] Tabela com todas as colunas: Checkbox, Nome, Email, Realm, Roles, Status, Criado Em, Ações
- [ ] Checkbox de seleção individual
- [ ] Checkbox select-all no header
- [ ] Badges de status com cores apropriadas (Ativo=verde, Bloqueado=vermelho)
- [ ] Badges de roles com cores diferentes (ADMIN=vermelho, USER=azul, SERVICE=amarelo)
- [ ] Avatar com iniciais do usuário gerado automaticamente
- [ ] Botões de ação: View (ti-eye), Edit (ti-edit), Block (ti-lock), Unblock (ti-lock-open), Reset Password (ti-key)
- [ ] Paginação footer com info e controls
- [ ] Ícones Tabler carregados corretamente
- [ ] Classes Bootstrap 5 aplicadas corretamente
- [ ] Página responsiva em dispositivos móveis

## Anotações
- Seguir exatamente o padrão do template `ui.txt` (API Keys section)
- Utilizar layout `layouts/vertical` (com sidebar)
- Badges de Realm usar classes `bg-primary` para Master e `bg-secondary` para outros
- Badges de status usar classes `bg-success-subtle text-success` para ativo e `bg-danger-subtle text-danger` para bloqueado
- Badges de roles usar cores diferentes: ADMIN (bg-danger), USER (bg-info), SERVICE (bg-warning)
- Avatar com iniciais gerado automaticamente usando CSS (primeiras letras do nome)
- Dados mockados podem ser usados inicialmente (serão substituídos por dados reais nas próximas histórias)
- Esta história cria apenas a estrutura do template; a funcionalidade completa será implementada nas histórias seguintes

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 2 (Gestão de Usuários) - backend CRUD já implementado
- Epic 3 (Gestão de Roles) - para dados de roles
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base da página de gestão de usuários

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
