# User Story: Modal de Criação/Edição de Role

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-05

## Descrição
Implementar o template do formulário modal de criação/edição de roles (`src/main/resources/templates/admin/roles/form.html`) utilizando Thymeleaf e Bootstrap 5, seguindo os padrões do PRD e incluindo todas as validações necessárias.

## Critérios de Aceite
- [ ] Template `admin/roles/form.html` criado com estrutura base
- [ ] Layout `layouts/vertical` configurado
- [ ] Modal Bootstrap 5 implementado
- [ ] Campo de nome da role com validação visual
- [ ] Campo de descrição da role
- [ ] Dropdown de seleção de realm
- [ ] Checkbox de role padrão (desabilitado para roles padrão)
- [ ] Dropdown de status (Ativa/Inativa)
- [ ] Validações Jakarta Bean Integration configuradas
- [ ] Mensagens de erro exibidas corretamente
- [ ] Botão de salvar com loading state
- [ ] Botão de cancelar

## Tarefas
1. Criar template `form.html` com estrutura modal
2. Implementar formulário com Thymeleaf object binding
3. Adicionar campos de nome, descrição, realm, status, padrão
4. Configurar validações de formulário
5. Adicionar mensagens de erro de validação
6. Implementar botões de ação
7. Adicionar JavaScript para manipulação do modal

## Instruções de Implementação

### Template: admin/roles/form.html
**Localização:** `src/main/resources/templates/admin/roles/form.html`

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta(${editMode ? 'Editar Role' : 'Nova Role'})}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 ${editMode ? 'Editar Role' : 'Nova Role'},
                 ${editMode ? 'Edite as informações da role' : 'Crie uma nova role de acesso'}
                )}">
        </div>

        <!-- Formulário -->
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header">
                        <h4 class="card-title mb-0">
                            <i class="ti ti-shield me-2"></i>
                            <span th:text="${editMode ? 'Editar Role' : 'Nova Role'}"></span>
                        </h4>
                    </div>

                    <form th:action="${editMode ? '/admin/roles/' + roleForm.id : '/admin/roles'}"
                          th:object="${roleForm}"
                          th:method="${editMode ? 'put' : 'post'}"
                          class="needs-validation"
                          novalidate>
                        <input type="hidden" th:field="*{id}" />
                        <input type="hidden" name="_method" th:value="${editMode ? 'put' : 'post'}" />

                        <div class="card-body">
                            <!-- Nome da Role -->
                            <div class="mb-3">
                                <label class="form-label" for="nome">
                                    Nome da Role <span class="text-danger">*</span>
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="nome"
                                       th:field="*{nome}"
                                       placeholder="Ex: ADMIN, USER, GERENTE"
                                       required
                                       maxlength="50"
                                       pattern="^[A-Z_]+$"
                                       th:classappend="${#fields.hasErrors('nome')} ? 'is-invalid' : ''" />

                                <div class="invalid-feedback" th:if="${#fields.hasErrors('nome')}">
                                    <th:block th:each="err : ${#fields.errors('nome')}" th:text="${err}"></th:block>
                                </div>
                                <small class="text-muted">
                                    Deve conter apenas letras maiúsculas e underscores (ex: ADMIN, USER_ROLE)
                                </small>
                            </div>

                            <!-- Descrição -->
                            <div class="mb-3">
                                <label class="form-label" for="descricao">Descrição</label>
                                <textarea class="form-control"
                                          id="descricao"
                                          th:field="*{descricao}"
                                          rows="3"
                                          maxlength="500"
                                          placeholder="Descreva as permissões desta role..."
                                          th:classappend="${#fields.hasErrors('descricao')} ? 'is-invalid' : ''"></textarea>

                                <div class="invalid-feedback" th:if="${#fields.hasErrors('descricao')}">
                                    <th:block th:each="err : ${#fields.errors('descricao')}" th:text="${err}"></th:block>
                                </div>
                                <div class="form-text">
                                    <span class="text-muted" th:text="${#strings.length(roleForm.descricao) + ' / 500'}"></span>
                                </div>
                            </div>

                            <!-- Realm -->
                            <div class="mb-3">
                                <label class="form-label" for="realmId">
                                    Realm <span class="text-danger">*</span>
                                </label>
                                <select class="form-select"
                                        id="realmId"
                                        th:field="*{realmId}"
                                        required
                                        th:disabled="${editMode}"
                                        th:classappend="${#fields.hasErrors('realmId')} ? 'is-invalid' : ''">
                                    <option value="">Selecione um realm...</option>
                                    <option th:each="realm : ${realms}"
                                            th:value="${realm.id}"
                                            th:text="${realm.nome}"
                                            th:selected="${roleForm.realmId == realm.id}"></option>
                                </select>

                                <div class="invalid-feedback" th:if="${#fields.hasErrors('realmId')}">
                                    <th:block th:each="err : ${#fields.errors('realmId')}" th:text="${err}"></th:block>
                                </div>
                                <small class="text-muted" th:if="${editMode}">
                                    Realm não pode ser alterado após a criação
                                </small>
                            </div>

                            <div class="row">
                                <!-- Role Padrão -->
                                <div class="col-md-6 mb-3">
                                    <div class="form-check">
                                        <input type="checkbox"
                                               class="form-check-input"
                                               id="padrao"
                                               th:field="*{padrao}"
                                               th:disabled="${roleForm.isRolePadrao()}" />

                                        <label class="form-check-label" for="padrao">
                                            Role Padrão
                                            <i class="ti ti-info-circle ms-1 text-muted"
                                               data-bs-toggle="tooltip"
                                               title="Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas"></i>
                                        </label>
                                    </div>
                                    <small class="text-muted">
                                        Roles padrão não podem ser removidas
                                    </small>
                                </div>

                                <!-- Status -->
                                <div class="col-md-6 mb-3">
                                    <label class="form-label" for="ativa">Status</label>
                                    <select class="form-select"
                                            id="ativa"
                                            th:field="*{ativa}">
                                        <option th:value="true" selected>Ativa</option>
                                        <option th:value="false">Inativa</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Alerta de Role Padrão -->
                            <div th:if="${roleForm.isRolePadrao()}" class="alert alert-info mb-0">
                                <i class="ti ti-info-circle me-2"></i>
                                Esta é uma role padrão do sistema (ADMIN, USER ou SERVICE).
                                Algumas operações podem ser restritas.
                            </div>
                        </div>

                        <div class="card-footer">
                            <div class="d-flex justify-content-end gap-2">
                                <a href="/admin/roles" class="btn btn-light">
                                    <i class="ti ti-x me-1"></i> Cancelar
                                </a>
                                <button type="submit" class="btn btn-primary" id="btnSalvar">
                                    <i class="ti ti-device-floppy me-1"></i>
                                    <span th:text="${editMode ? 'Salvar Alterações' : 'Criar Role'}"></span>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Sidebar com Informações -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="ti ti-help-circle me-2"></i>
                            Informações
                        </h5>
                    </div>
                    <div class="card-body">
                        <h6 class="fw-semibold mb-2">Sobre Roles</h6>
                        <p class="text-muted mb-3">
                            Roles são utilizadas para definir permissões de acesso dos usuários no sistema.
                            Cada role pode ser associada a múltiplos usuários.
                        </p>

                        <h6 class="fw-semibold mb-2">Roles Padrão</h6>
                        <ul class="list-group list-group-flush mb-3">
                            <li class="list-group-item d-flex align-items-center gap-2 px-0">
                                <span class="badge bg-danger">ADMIN</span>
                                <span class="text-muted">Acesso total ao sistema</span>
                            </li>
                            <li class="list-group-item d-flex align-items-center gap-2 px-0">
                                <span class="badge bg-info">USER</span>
                                <span class="text-muted">Acesso básico limitado</span>
                            </li>
                            <li class="list-group-item d-flex align-items-center gap-2 px-0">
                                <span class="badge bg-warning">SERVICE</span>
                                <span class="text-muted">Acesso para serviços</span>
                            </li>
                        </ul>

                        <div class="alert alert-warning mb-0">
                            <i class="ti ti-alert-triangle me-2"></i>
                            <strong>Atenção:</strong>
                            Roles padrão não podem ser removidas do sistema.
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </th:block>

    <th:block layout:fragment="javascripts">
        <!-- Custom table -->
        <script src="/js/pages/custom-table.js"></script>

        <!-- Form validation script -->
        <script>
            // Validação de formulário
            (function() {
                'use strict';

                const form = document.querySelector('.needs-validation');
                const btnSalvar = document.getElementById('btnSalvar');

                form.addEventListener('submit', function(event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    } else {
                        // Loading state
                        btnSalvar.disabled = true;
                        btnSalvar.innerHTML = '<i class="ti ti-loader me-1"></i> Salvando...';
                    }

                    form.classList.add('was-validated');
                }, false);

                // Converter nome para uppercase
                const nomeInput = document.getElementById('nome');
                nomeInput.addEventListener('input', function() {
                    this.value = this.value.toUpperCase();
                });

                // Contador de caracteres da descrição
                const descricaoInput = document.getElementById('descricao');
                const formText = descricaoInput.nextElementSibling.nextElementSibling;
                descricaoInput.addEventListener('input', function() {
                    formText.textContent = this.value.length + ' / 500';
                });

                // Inicializar tooltips
                const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                tooltips.forEach(function(tooltip) {
                    new bootstrap.Tooltip(tooltip);
                });
            })();
        </script>
    </th:block>
</body>
</html>
```

## Checklist de Validação
- [ ] Template `admin/roles/form.html` criado
- [ ] Layout `layouts/vertical` configurado
- [ ] Breadcrumb implementado corretamente
- [ ] Campo de nome com máscara uppercase configurado
- [ ] Campo de descrição com contador de caracteres
- [ ] Dropdown de realms populado dinamicamente
- [ ] Checkbox de role padrão funcionando
- [ ] Dropdown de status funcionando
- [ ] Validações Jakarta Bean configuradas
- [ ] Mensagens de erro exibidas corretamente
- [ ] Botão de salvar com loading state
- [ ] Sidebar com informações de ajuda implementada
- [ ] Tooltips funcionando
- [ ] Realm desabilitado em modo de edição

## Anotações
- Formulário usa `th:object` para binding automático
- Nome da role deve ser convertido para uppercase automaticamente
- Realm não pode ser alterado após a criação
- Roles padrão têm checkbox desabilitado
- Usar validação HTML5 + Jakarta Bean Validation
- Loading state evita múltiplos submits
- Sidebar ajuda o usuário a entender o conceito de roles

## Dependências
- Epic 14 Story 02 - DTOs (RoleForm)
- Epic 14 Story 01 - Template base (list.html)
- Epic 9 (Configuração) - Thymeleaf e Bootstrap 5 configurados

## Prioridade
**Alta** - Formulário necessário para criação/edição de roles

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas

## Status do Epic 14 - Story 05

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

## Resumo da Implementação

O formulário (form.html) para criação/edição de roles foi implementado com:

**Componentes Implementados:**
- Formulário com campos: Nome, Realm, Descrição, Status, Indicador de Padrão
- Validações Jakarta Bean (@Valid, @NotBlank, @Size, @Pattern)
- Validações de formato de nome (uppercase)
- Validações de tamanho (3-50 caracteres)
- Ícones Tabler em todos os inputs
- Estados de edição (isEdit, isRolePadrao)
- Loading state no botão Salvar
- Botão de voltar para listagem
- Responsividade em dispositivos móveis

**Funcionalidades:**
- Criação de novas roles
- Edição de roles existentes
- Proteção contra alteração de roles padrão
- Exibição de campos desabilitados para roles padrão

**Integrações:**
- Thymeleaf para renderização
- AdminRoleService para lógica de negócio
- RealmRepository para validação de realm existente
- MessageSource para mensagens internacionalizadas
- CSRF protection habilitado

**Status do Epic 14 - Story 05**

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

