# User Story: Funcionalidades CRUD - Visualizar, Editar, Ativar/Desativar

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-06

## Descrição
Implementar as funcionalidades de CRUD completa para realms na interface web, incluindo visualização de detalhes, edição, ativação, desativação com confirmação, e proteção especial do Realm Master.

## Critérios de Aceite
- [X] Botão View (olho) exibe detalhes completos do realm
- [X] Botão Edit (lápis) abre modal preenchido com dados atuais
- [X] Botão Deactivate (X) exibe diálogo de confirmação
- [X] Botão Activate (check) reativa realm inativo
- [X] Realm Master não tem botão de desativação
- [X] Nome do Realm Master não pode ser editado
- [X] Confirmação antes de desativar realm
- [X] Mensagens de sucesso/erro exibidas após operações
- [X] Modal de detalhes mostra métricas (usuários, chaves, roles)
- [X] Botões condicionais baseados em status do realm
- [X] Redirecionamento correto após cada operação

## Tarefas
1. Atualizar tabela com links/botões de ação dinâmicos
2. Criar template de detalhes do realm
3. Implementar JavaScript de confirmação para desativação
4. Implementar JavaScript para abrir modal de edição com dados
5. Configurar proteção do Realm Master nos botões
6. Adicionar template de visualização de detalhes
7. Implementar métricas na página de detalhes
8. Testar todas as operações CRUD
9. Testar proteção do Realm Master
10. Testar mensagens de sucesso/erro

## Instruções de Implementação

### Atualização da Tabela de Ações
**No template `admin/realms/list.html`, atualizar a coluna Ações:**

```html
<td class="text-center">
    <div class="d-flex align-items-center justify-content-center gap-1">

        <!-- Botão View (sempre disponível) -->
        <a th:href="@{/admin/realms/{id}(id=${realm.id})}"
           class="btn btn-default btn-icon btn-sm rounded"
           title="Ver Detalhes">
            <i class="ti ti-eye fs-lg"></i>
        </a>

        <!-- Botão Edit (disponível para todos, mas nome read-only para Master) -->
        <a th:href="@{/admin/realms/{id}/edit(id=${realm.id})}"
           class="btn btn-default btn-icon btn-sm rounded"
           title="Editar">
            <i class="ti ti-edit fs-lg"></i>
        </a>

        <!-- Botão Deactivate (apenas para ativos e não-Master) -->
        <a th:if="${realm.ativo && !realm.master}"
           th:href="@{/admin/realms/{id}/desativar(id=${realm.id})}"
           class="btn btn-default btn-icon btn-sm rounded"
           onclick="return confirmDesativarRealm(this.getAttribute('data-nome'));"
           th:attr="data-nome=${realm.nome}"
           title="Desativar">
            <i class="ti ti-x fs-lg"></i>
        </a>

        <!-- Botão Activate (apenas para inativos e não-Master) -->
        <a th:if="${!realm.ativo && !realm.master}"
           th:href="@{/admin/realms/{id}/ativar(id=${realm.id})}"
           class="btn btn-success btn-icon btn-sm rounded"
           title="Ativar">
            <i class="ti ti-check fs-lg"></i>
        </a>

        <!-- Badge para Realm Master (visual) -->
        <span th:if="${realm.master}"
              class="badge bg-primary bg-opacity-10 text-primary"
              title="Realm Master">
            <i class="ti ti-crown fs-lg"></i>
        </span>

    </div>
</td>
```

### JavaScript de Confirmação
**Adicionar ao fragment `<th:block layout:fragment="javascripts">`:**

```javascript
<!-- Confirmação de Desativação de Realm -->
<script>
/**
 * Exibe diálogo de confirmação antes de desativar realm.
 * @param {string} nome - Nome do realm a ser desativado
 * @returns {boolean} - true se confirmado, false se cancelado
 */
function confirmDesativarRealm(nome) {
    const message = `Tem certeza que deseja desativar o realm "${nome}"?`;

    // Usar SweetAlert2 se disponível (melhor UX)
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'Confirmar Desativação',
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sim, desativar',
            cancelButtonText: 'Cancelar',
            customClass: {
                confirmButton: 'btn btn-danger',
                cancelButton: 'btn btn-primary'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                // Fazer redirect via AJAX ou submeter formulário
                window.location.href = event.target.getAttribute('href');
            }
            return false; // Prevenir clique padrão
        });
        return false;
    }

    // Fallback para confirm() nativo do navegador
    return confirm(message + '\n\nEsta ação tornará o realm e todos os seus usuários inacessíveis.');
}

/**
 * Exibe diálogo de confirmação antes de ativar realm.
 * @param {string} nome - Nome do realm a ser ativado
 * @returns {boolean} - true se confirmado, false se cancelado
 */
function confirmAtivarRealm(nome) {
    const message = `Deseja ativar o realm "${nome}"?`;

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'Confirmar Ativação',
            text: message,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sim, ativar',
            cancelButtonText: 'Cancelar',
            customClass: {
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-danger'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = event.target.getAttribute('href');
            }
            return false;
        });
        return false;
    }

    return confirm(message);
}

/**
 * Prevenção de edição do Realm Master.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Adicionar aviso ao tentar editar Realm Master
    const masterRealmEditButtons = document.querySelectorAll('[data-is-master="true"] .ti-edit');
    masterRealmEditButtons.forEach(button => {
        button.parentElement.addEventListener('click', function(e) {
            const isMaster = this.closest('tr').querySelector('[data-master="true"]');
            if (isMaster) {
                // Permitir edição, mas mostrar warning
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        title: 'Realm Master',
                        text: 'Você está editando o Realm Master. Algumas operações são restritas.',
                        icon: 'warning',
                        confirmButtonColor: '#3085d6',
                        confirmButtonText: 'Entendido'
                    });
                }
            }
        });
    });

    // Atualizar botões quando o status mudar (via AJAX recarregar tabela)
    document.addEventListener('table-reload', function() {
        // Re-inicializar handlers após reload da tabela
        // Implementar conforme necessário
    });
});
</script>
```

### Template de Detalhes do Realm
**Criar template `admin/realms/detail.html`:**

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Detalhes do Realm')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Realms',
                 'Detalhes do Realm: ' + ${realm?.nome}
                )}">
        </div>

        <!-- Flash Messages -->
        <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="ti ti-check me-2"></i>
            <span th:text="${success}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>

        <div th:if="${error}" class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="ti ti-alert-circle me-2"></i>
            <span th:text="${error}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>

        <!-- Realm Master Alert -->
        <div th:if="${realm.master}" class="alert alert-primary mb-3">
            <div class="d-flex align-items-center">
                <i class="ti ti-crown fs-2 me-3"></i>
                <div>
                    <h5 class="alert-heading">Realm Master</h5>
                    <p class="mb-0">Este é o realm principal do sistema. Algumas operações são restritas.</p>
                </div>
            </div>
        </div>

        <!-- Detalhes do Realm -->
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">Informações Básicas</h5>
                        <div class="d-flex gap-2">
                            <a th:href="@{/admin/realms/{id}/edit(id=${realm.id})}"
                               class="btn btn-sm btn-primary">
                                <i class="ti ti-edit me-1"></i>Editar
                            </a>
                            <a th:href="@{/admin/realms}"
                               class="btn btn-sm btn-default">
                                <i class="ti ti-arrow-left me-1"></i>Voltar
                            </a>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-borderless mb-0">
                                <tr>
                                    <th class="fw-medium" style="width: 30%;">Nome</th>
                                    <td>
                                        <span th:text="${realm.nome}" class="fw-semibold"></span>
                                        <span th:if="${realm.master}" class="badge bg-primary ms-2">
                                            <i class="ti ti-crown me-1"></i>Master
                                        </span>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="fw-medium">Descrição</th>
                                    <td th:text="${realm.descricao ?: '-'}"></td>
                                </tr>
                                <tr>
                                    <th class="fw-medium">Status</th>
                                    <td>
                                        <span th:class="${realm.ativo ? 'badge bg-success-subtle text-success badge-label'
                                                        : 'badge bg-danger-subtle text-danger badge-label'}">
                                            <span th:text="${realm.ativo ? 'Ativo' : 'Inativo'}"></span>
                                        </span>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="fw-medium">Empresa ID</th>
                                    <td th:text="${realm.empresaId ?: '-'}"></td>
                                </tr>
                                <tr>
                                    <th class="fw-medium">Criado Em</th>
                                    <td th:text="${#temporals.format(realm.dataCriacao, 'dd/MM/yyyy HH:mm')}"></td>
                                </tr>
                                <tr>
                                    <th class="fw-medium">Última Modificação</th>
                                    <td th:text="${#temporals.format(realm.dataAtualizacao, 'dd/MM/yyyy HH:mm')}"></td>
                                </tr>
                                <tr th:if="${!realm.ativo}">
                                    <th class="fw-medium">Desativado Em</th>
                                    <td th:text="${#temporals.format(realm.dataDesativacao, 'dd/MM/yyyy HH:mm')}"></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Métricas -->
                <div class="card mt-3">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Métricas</h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <div class="card border-light mb-0">
                                    <div class="card-body">
                                        <p class="text-muted mb-1">Total Usuários</p>
                                        <h4 class="mb-0" th:text="${realm.totalUsuarios}">0</h4>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card border-light mb-0">
                                    <div class="card-body">
                                        <p class="text-muted mb-1">Usuários Ativos</p>
                                        <h4 class="mb-0 text-success" th:text="${realm.usuariosAtivos}">0</h4>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card border-light mb-0">
                                    <div class="card-body">
                                        <p class="text-muted mb-1">Usuários Bloqueados</p>
                                        <h4 class="mb-0 text-danger" th:text="${realm.usuariosBloqueados}">0</h4>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card border-light mb-0">
                                    <div class="card-body">
                                        <p class="text-muted mb-1">Chaves Ativas</p>
                                        <h4 class="mb-0 text-warning" th:text="${realm.chavesAtivas}">0</h4>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Roles -->
                <div class="card mt-3">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Roles</h5>
                    </div>
                    <div class="card-body">
                        <div th:if="${realm.roles != null && !realm.roles.empty}">
                            <div class="d-flex flex-wrap gap-2">
                                <span th:each="role : ${realm.roles}"
                                      class="badge bg-info-subtle text-info"
                                      th:text="${role}">ROLE</span>
                            </div>
                        </div>
                        <div th:if="${realm.roles == null || realm.roles.empty}" class="text-muted">
                            Nenhuma role encontrada neste realm.
                        </div>
                    </div>
                </div>

            </div>

            <!-- Sidebar Ações -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Ações Rápidas</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <!-- Editar -->
                            <a th:href="@{/admin/realms/{id}/edit(id=${realm.id})}"
                               class="btn btn-primary">
                                <i class="ti ti-edit me-2"></i>Editar Realm
                            </a>

                            <!-- Ativar/Desativar -->
                            <a th:if="${realm.ativo && !realm.master}"
                               th:href="@{/admin/realms/{id}/desativar(id=${realm.id})}"
                               class="btn btn-danger"
                               onclick="return confirmDesativarRealm('${realm.nome}');">
                                <i class="ti ti-x me-2"></i>Desativar Realm
                            </a>

                            <a th:if="${!realm.ativo && !realm.master}"
                               th:href="@{/admin/realms/{id}/ativar(id=${realm.id})}"
                               class="btn btn-success"
                               onclick="return confirmAtivarRealm('${realm.nome}');">
                                <i class="ti ti-check me-2"></i>Ativar Realm
                            </a>

                            <!-- Listar Usuários -->
                            <a th:href="@{/admin/usuarios?realm=${realm.id}}"
                               class="btn btn-default">
                                <i class="ti ti-users me-2"></i>Listar Usuários
                            </a>

                            <!-- Listar Roles -->
                            <a th:href="@{/admin/roles?realm=${realm.id}}"
                               class="btn btn-default">
                                <i class="ti ti-shield me-2"></i>Listar Roles
                            </a>

                            <!-- Listar Chaves -->
                            <a th:href="@{/admin/chaves?realm=${realm.id}}"
                               class="btn btn-default">
                                <i class="ti ti-key me-2"></i>Listar Chaves
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Status Info -->
                <div class="card mt-3"
                     th:classappend="${realm.ativo ? 'border-success' : 'border-danger'}">
                    <div class="card-body text-center">
                        <i th:class="${realm.ativo ? 'ti ti-circle-check text-success' : 'ti ti-circle-x text-danger'}"
                           class="fs-2"></i>
                        <h5 class="card-title mt-2"
                            th:text="${realm.ativo ? 'Realm Ativo' : 'Realm Inativo'}"></h5>
                        <p class="text-muted mb-0">
                            <span th:text="${realm.ativo ? 'Este realm está operacional' : 'Este realm está desativado'}"></span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

    </th:block>
</body>
</html>
```

## Checklist de Validação
- [ ] Botão View na tabela implementado
- [ ] Botão Edit na tabela implementado
- [ ] Botão Deactivate (condicional) implementado
- [ ] Botão Activate (condicional) implementado
- [ ] Badge de Realm Master implementado
- [ ] Confirmação para desativação funcionando
- [ ] Confirmação para ativação funcionando
- [ ] Realm Master sem botão de desativação
- [ ] Template de detalhes criado
- [ ] Informações básicas exibidas
- [ ] Métricas exibidas (usuários, chaves)
- [ ] Lista de roles exibida
- [ ] Ações rápidas implementadas
- [ ] Status info visual implementado
- [ ] Links para usuários, roles, chaves implementados
- [ ] Mensagens de sucesso/erro funcionando
- [ ] Proteção do Realm Master funcionando

## Anotações
- Botões de ação devem ser condicionais baseados em status e propriedade master
- Usar SweetAlert2 para diálogos de confirmação se disponível (melhor UX)
- Fallback para confirm() nativo se SweetAlert2 não disponível
- Template de detalhes mostra métricas e links relacionados
- Realm Master tem tratamento especial em todo o sistema
- Ações rápidas facilitam navegação para entidades relacionadas

## Dependências
- Story 01 (Template da Lista de Realms) - template base
- Story 04 (Controller API) - endpoints de CRUD
- Story 05 (Modal de Criação/Edição) - formulários
- Epic 13 (Página de Gestão de Usuários) - link para listagem
- Epic 14 (Página de Gestão de Roles) - link para listagem
- Epic 15 (Página de Gestão de Chaves) - link para listagem

## Prioridade
**Alta** - Funcionalidades essenciais para gestão de realms

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
