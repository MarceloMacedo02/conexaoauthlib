# User Story: Template da Lista de Realms (Thymeleaf)

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-01

## Descrição
Criar o template da página de gestão de realms (`src/main/resources/templates/admin/realms/list.html`) utilizando Thymeleaf, layout `layouts/vertical`, Bootstrap 5 e Tabler Icons, seguindo os padrões do arquivo `ui.txt` (API Keys template) e os requisitos do PRD para seção "Gestão de Realms".

## Critérios de Aceite
- [X] Template `admin/realms/list.html` criado com estrutura base
- [X] Layout `layouts/vertical` configurado (com sidebar)
- [X] Page Title fragment implementado com breadcrumb "Apps > Gestão Realms"
- [X] Search input para busca textual de realms
- [X] Filtro de status (All, Ativo, Inativo) funcionando
- [X] Dropdown de registros por página (5, 10, 15, 20)
- [X] Tabela de realms com todas as colunas implementadas
- [X] Checkbox de seleção individual e select-all
- [X] Badges de status (Ativo/Inativo) com cores apropriadas
- [X] Ícones Tabler (`ti-*`) aplicados corretamente
- [X] Paginação controls implementados
- [X] Indicador visual de Realm Master (ícone de coroa)
- [X] Colunas: Checkbox, Nome, Status, Usuários, Chaves Ativas, Criado Em, Modificado Em, Ações
- [X] Página totalmente responsiva (mobile-first)

## Tarefas
1. Criar diretório `templates/admin/realms/` se não existir
2. Criar template `list.html` com estrutura base seguindo API Keys template
3. Implementar Page Title fragment com breadcrumb
4. Criar card com header de filtros e busca
5. Implementar tabela de realms com todas as colunas
6. Adicionar checkbox de seleção e select-all
7. Configurar dropdown de registros por página
8. Adicionar botão de novo realm com ícone
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
        <th:block th:replace="~{partials/title-meta :: title-meta('Gestão de Realms')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Realms',
                 'Gerencie todos os realms do sistema'
                )}">
        </div>

        <!-- Card com Tabela de Realms -->
        <div class="row">
            <div class="col-12">
                <div data-table data-table-rows-per-page="8" class="card">
                    <!-- Card Header com Filtros e Botões -->
                    <div class="card-header border-light justify-content-between">
                        <div class="d-flex gap-2">
                            <div class="app-search">
                                <input data-table-search type="text" class="form-control"
                                    placeholder="Buscar realms...">
                                <i data-lucide="search" class="app-search-icon text-muted"></i>
                            </div>
                            <button type="button" class="btn btn-primary btn-icon" data-bs-toggle="modal"
                                data-bs-target="#addRealmModal"><i class="ti ti-plus fs-lg"></i></button>
                            <button data-table-delete-selected class="btn btn-danger d-none">Delete</button>
                        </div>

                        <div class="d-flex align-items-center gap-2">
                            <span class="me-2 fw-semibold">Filtrar Por:</span>

                            <!-- Status Filter -->
                            <div class="app-search">
                                <select data-table-filter="status" class="form-select form-control my-1 my-md-0">
                                    <option value="All">Status</option>
                                    <option value="Ativo">Ativo</option>
                                    <option value="Inativo">Inativo</option>
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

                    <!-- Tabela de Realms -->
                    <div class="table-responsive">
                        <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                            <thead class="bg-light bg-opacity-25 thead-sm">
                                <tr class="text-uppercase fs-xxs">
                                    <th scope="col" style="width: 1%;">
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            data-table-select-all type="checkbox" value="option">
                                    </th>
                                    <th data-table-sort>Nome</th>
                                    <th data-table-sort="status">Status</th>
                                    <th data-table-sort>Usuários</th>
                                    <th data-table-sort>Chaves Ativas</th>
                                    <th data-table-sort>Criado Em</th>
                                    <th data-table-sort>Modificado Em</th>
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
                                    <td class="fw-medium">
                                        <span class="badge bg-primary me-2">
                                            <i class="ti ti-crown me-1"></i>Master
                                        </span>
                                        Master Realm
                                    </td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>5</td>
                                    <td>2</td>
                                    <td>Dez 15, 2025</td>
                                    <td>Dez 23, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td class="fw-medium">Empresa A</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>152</td>
                                    <td>4</td>
                                    <td>Jan 10, 2025</td>
                                    <td>Dez 22, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-x fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td class="fw-medium">Empresa B</td>
                                    <td>
                                        <span class="badge bg-danger-subtle text-danger badge-label">Inativo</span>
                                    </td>
                                    <td>89</td>
                                    <td>0</td>
                                    <td>Mar 5, 2025</td>
                                    <td>Nov 20, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-success btn-icon btn-sm rounded"><i
                                                    class="ti ti-check fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td class="fw-medium">Empresa C</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>34</td>
                                    <td>2</td>
                                    <td>Apr 22, 2025</td>
                                    <td>Dez 21, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-x fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td class="fw-medium">Empresa D</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>267</td>
                                    <td>6</td>
                                    <td>May 15, 2025</td>
                                    <td>Dez 20, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-x fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td>
                                        <input class="form-check-input form-check-input-light fs-14 mt-0"
                                            type="checkbox" value="option">
                                    </td>
                                    <td class="fw-medium">Empresa E</td>
                                    <td>
                                        <span class="badge bg-success-subtle text-success badge-label">Ativo</span>
                                    </td>
                                    <td>78</td>
                                    <td>3</td>
                                    <td>Jul 8, 2025</td>
                                    <td>Dez 19, 2025</td>
                                    <td>
                                        <div class="d-flex align-items-center justify-content-center gap-1">
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-eye fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-edit fs-lg"></i></a>
                                            <a href="#" class="btn btn-default btn-icon btn-sm rounded"><i
                                                    class="ti ti-x fs-lg"></i></a>
                                        </div>
                                    </td>
                                </tr>

                            </tbody><!-- end table-body -->
                        </table><!-- end table -->
                    </div>

                    <!-- Paginação -->
                    <div class="card-footer border-0">
                        <div class="d-flex justify-content-between align-items-center">
                            <div data-table-pagination-info="realms"></div>
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

        <!-- Realms page js (será implementado nas próximas histórias) -->
    </th:block>
</body>
</html>
```

### Checklist de Validação
- [ ] Template `admin/realms/list.html` criado
- [ ] Layout `layouts/vertical` configurado
- [ ] Page Title com breadcrumb "Apps > Gestão Realms"
- [ ] Search input implementado com ícone
- [ ] Filtro de status (All, Ativo, Inativo) funcional
- [ ] Dropdown de registros por página (5, 10, 15, 20)
- [ ] Tabela com todas as colunas: Checkbox, Nome, Status, Usuários, Chaves Ativas, Criado Em, Modificado Em, Ações
- [ ] Checkbox de seleção individual
- [ ] Checkbox select-all no header
- [ ] Badges de status com cores apropriadas (Ativo=verde, Inativo=vermelho)
- [ ] Ícone de coroa para Realm Master
- [ ] Botões de ação: View (ti-eye), Edit (ti-edit), Deactivate (ti-x), Activate (ti-check)
- [ ] Paginação footer com info e controls
- [ ] Ícones Tabler carregados corretamente
- [ ] Classes Bootstrap 5 aplicadas corretamente
- [ ] Página responsiva em dispositivos móveis

## Anotações
- Seguir exatamente o padrão do template `ui.txt` (API Keys section)
- Utilizar layout `layouts/vertical` (com sidebar)
- Realm Master deve ter badge azul com ícone de coroa (`ti ti-crown`)
- Badges de status usar classes `bg-success-subtle text-success` para ativo e `bg-danger-subtle text-danger` para inativo
- Dados mockados podem ser usados inicialmente (serão substituídos por dados reais nas próximas histórias)
- Esta história cria apenas a estrutura do template; a funcionalidade completa será implementada nas histórias seguintes

## Dependências
- Epic 1 (Gestão de Realms) - backend CRUD já implementado
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base da página de gestão de realms

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
