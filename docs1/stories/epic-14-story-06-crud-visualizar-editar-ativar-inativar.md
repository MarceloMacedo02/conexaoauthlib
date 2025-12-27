# User Story: Funcionalidades CRUD - Visualizar, Editar, Ativar/Inativar

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-06

## Descrição
Implementar as funcionalidades de CRUD completas para roles, incluindo visualização de detalhes, edição de roles existentes, ativação/inativação de roles e remoção de roles sem usuários associados. Integrar com as ações da tabela da listagem.

## Critérios de Aceite
- [ ] Ação de visualizar detalhes da role funcionando
- [ ] Ação de editar role funcionando
- [ ] Ação de ativar role funcionando
- [ ] Ação de inativar role funcionando
- [ ] Ação de remover role funcionando (sem usuários)
- [ ] Botão de remover desabilitado para roles com usuários
- [ ] Botão de remover desabilitado para roles padrão
- [ ] Modal de confirmação de remoção
- [ ] Atualização da lista após cada ação
- [ ] Mensagens de sucesso/erro em toasts
- [ ] Animações de carregamento

## Tarefas
1. Implementar ação de visualizar detalhes
2. Implementar ação de editar (redirecionar para formulário)
3. Implementar ação de ativar/inativar
4. Implementar ação de remover com confirmação
5. Desabilitar ações conforme contexto (usuários, padrão)
6. Adicionar modais de confirmação
7. Implementar toasts de feedback
8. Adicionar JavaScript para manipulação de ações

## Instruções de Implementação

### Atualizar Template de Listagem
**Adicionar modal de confirmação em `admin/roles/list.html`:**

```html
<!-- Modal de Confirmação de Remoção -->
<div class="modal fade" id="confirmarRemocaoModal" tabindex="-1"
     aria-labelledby="confirmarRemocaoModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-danger" id="confirmarRemocaoModalLabel">
                    <i class="ti ti-trash me-2"></i>
                    Confirmar Remoção
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="alert alert-warning mb-3">
                    <i class="ti ti-alert-triangle me-2"></i>
                    Esta ação não pode ser desfeita!
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Nome da Role</label>
                    <input type="text" class="form-control"
                           id="confirmarRoleNome" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Total de Usuários</label>
                    <input type="text" class="form-control"
                           id="confirmarRoleUsuarios" readonly />
                </div>

                <div id="alertaRoleEmUso" class="alert alert-danger d-none">
                    <i class="ti ti-lock me-2"></i>
                    Esta role possui usuários associados e não pode ser removida.
                    Primeiro remova a role de todos os usuários.
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger"
                        id="confirmarRemoverBtn">
                    <i class="ti ti-trash me-2"></i>
                    Remover Role
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Modal de Detalhes da Role -->
<div class="modal fade" id="detalhesRoleModal" tabindex="-1"
     aria-labelledby="detalhesRoleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="detalhesRoleModalLabel">
                    <i class="ti ti-shield me-2"></i>
                    Detalhes da Role
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body" id="detalhesRoleContent">
                <!-- Conteúdo carregado via AJAX -->
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">Fechar</button>
                <a href="#" class="btn btn-primary" id="btnEditarRoleModal">
                    <i class="ti ti-edit me-2"></i>
                    Editar Role
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Modal de Ativação/Inativação -->
<div class="modal fade" id="alterarStatusModal" tabindex="-1"
     aria-labelledby="alterarStatusModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="alterarStatusModalLabel">
                    <i class="ti ti-power me-2"></i>
                    Alterar Status
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div id="alertaAtivar" class="alert alert-success d-none">
                    <i class="ti ti-check me-2"></i>
                    Você está ativando a role. Usuários poderão utilizá-la novamente.
                </div>

                <div id="alertaInativar" class="alert alert-warning d-none">
                    <i class="ti ti-alert-triangle me-2"></i>
                    Você está inativando a role. Usuários não poderão mais utilizá-la.
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Nome da Role</label>
                    <input type="text" class="form-control"
                           id="alterarStatusRoleNome" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Novo Status</label>
                    <input type="text" class="form-control"
                           id="alterarStatusNovo" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label">Motivo (Opcional)</label>
                    <textarea class="form-control"
                              id="alterarStatusMotivo"
                              rows="3"
                              placeholder="Descreva o motivo desta alteração..."></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary"
                        id="confirmarAlterarStatusBtn">
                    <i class="ti ti-check me-2"></i>
                    Confirmar
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Toast de Sucesso -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
    <div id="toastSucesso" class="toast align-items-center text-white bg-success border-0"
         role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body">
                <i class="ti ti-check-circle me-2"></i>
                <span id="toastSucessoMensagem">Operação realizada com sucesso!</span>
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>

<!-- Toast de Erro -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
    <div id="toastErro" class="toast align-items-center text-white bg-danger border-0"
         role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body">
                <i class="ti ti-x-circle me-2"></i>
                <span id="toastErroMensagem">Erro ao realizar operação!</span>
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>
```

### JavaScript para Ações CRUD
**Adicionar em `src/main/resources/static/js/pages/roles.js`:**

```javascript
// Variáveis globais
let roleAtual = null;

// Visualizar detalhes da role
function visualizarRole(id) {
    fetch(`/admin/roles/${id}`)
        .then(response => response.json())
        .then(data => {
            renderizarDetalhes(data);
            const modal = new bootstrap.Modal(document.getElementById('detalhesRoleModal'));
            modal.show();

            // Configurar botão de edição
            document.getElementById('btnEditarRoleModal').href =
                `/admin/roles/${id}/editar`;
        })
        .catch(error => {
            mostrarErro('Erro ao carregar detalhes da role');
            console.error(error);
        });
}

// Renderizar detalhes da role
function renderizarDetalhes(role) {
    const content = document.getElementById('detalhesRoleContent');

    const padraoBadge = role.padrao
        ? '<span class="badge bg-primary ms-2"><i class="ti ti-star me-1"></i>Padrão</span>'
        : '';

    const statusBadge = role.ativa
        ? '<span class="badge bg-success-subtle text-success badge-label">Ativa</span>'
        : '<span class="badge bg-danger-subtle text-danger badge-label">Inativa</span>';

    const usuariosList = role.usuariosNomes.length > 0
        ? role.usuariosNomes.map(nome =>
            `<span class="badge bg-info me-1">${nome}</span>`
          ).join('')
        : '<span class="text-muted">Nenhum usuário</span>';

    content.innerHTML = `
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold">Nome</label>
                <div class="fs-5 fw-medium">
                    ${role.nome}${padraoBadge}
                </div>
            </div>

            <div class="col-md-6">
                <label class="form-label fw-semibold">Status</label>
                <div>${statusBadge}</div>
            </div>

            <div class="col-md-12">
                <label class="form-label fw-semibold">Descrição</label>
                <div class="alert alert-light border">
                    ${role.descricao || '-'}
                </div>
            </div>

            <div class="col-md-6">
                <label class="form-label fw-semibold">Realm</label>
                <div><span class="badge bg-secondary badge-label">${role.realmNome}</span></div>
            </div>

            <div class="col-md-6">
                <label class="form-label fw-semibold">Total de Usuários</label>
                <div class="fs-4 fw-bold text-primary">${role.totalUsuarios}</div>
            </div>

            <div class="col-md-12">
                <label class="form-label fw-semibold">Usuários com esta Role</label>
                <div class="mb-0">${usuariosList}</div>
            </div>

            <div class="col-md-6">
                <label class="form-label fw-semibold">Criada Em</label>
                <div>${formatarDataHora(role.dataCriacao)}</div>
            </div>

            <div class="col-md-6">
                <label class="form-label fw-semibold">Atualizada Em</label>
                <div>${formatarDataHora(role.dataUltimaAtualizacao)}</div>
            </div>
        </div>
    `;
}

// Confirmar remoção de role
function confirmarRemocao(id, nome, totalUsuarios) {
    roleAtual = { id, nome, totalUsuarios };

    document.getElementById('confirmarRoleNome').value = nome;
    document.getElementById('confirmarRoleUsuarios').value = totalUsuarios;

    const alertaEmUso = document.getElementById('alertaRoleEmUso');
    const btnRemover = document.getElementById('confirmarRemoverBtn');

    if (totalUsuarios > 0) {
        alertaEmUso.classList.remove('d-none');
        btnRemover.disabled = true;
        btnRemover.classList.add('disabled');
    } else {
        alertaEmUso.classList.add('d-none');
        btnRemover.disabled = false;
        btnRemover.classList.remove('disabled');
    }

    const modal = new bootstrap.Modal(document.getElementById('confirmarRemocaoModal'));
    modal.show();
}

// Confirmar e executar remoção
document.getElementById('confirmarRemoverBtn').addEventListener('click', function() {
    if (!roleAtual || roleAtual.totalUsuarios > 0) {
        return;
    }

    fetch(`/admin/roles/${roleAtual.id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': getCsrfToken()
        }
    })
    .then(response => {
        if (response.redirected) {
            window.location.href = response.url;
        }
    })
    .catch(error => {
        mostrarErro('Erro ao remover role');
        console.error(error);
    })
    .finally(() => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('confirmarRemocaoModal'));
        modal.hide();
    });
});

// Alterar status da role
function alterarStatus(id, nome, ativa) {
    roleAtual = { id, nome, ativa };

    document.getElementById('alterarStatusRoleNome').value = nome;
    document.getElementById('alterarStatusNovo').value = ativa ? 'Inativar' : 'Ativar';

    const alertaAtivar = document.getElementById('alertaAtivar');
    const alertaInativar = document.getElementById('alertaInativar');

    if (ativa) {
        alertaInativar.classList.remove('d-none');
        alertaAtivar.classList.add('d-none');
    } else {
        alertaAtivar.classList.remove('d-none');
        alertaInativar.classList.add('d-none');
    }

    document.getElementById('alterarStatusMotivo').value = '';

    const modal = new bootstrap.Modal(document.getElementById('alterarStatusModal'));
    modal.show();
}

// Confirmar e executar alteração de status
document.getElementById('confirmarAlterarStatusBtn').addEventListener('click', function() {
    if (!roleAtual) {
        return;
    }

    const novoStatus = !roleAtual.ativa;
    const motivo = document.getElementById('alterarStatusMotivo').value;

    fetch(`/admin/roles/${roleAtual.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': getCsrfToken()
        },
        body: JSON.stringify({
            id: roleAtual.id,
            nome: roleAtual.nome,
            ativa: novoStatus,
            motivo: motivo
        })
    })
    .then(response => response.json())
    .then(data => {
        mostrarSucesso('Status da role alterado com sucesso!');
        setTimeout(() => window.location.reload(), 1000);
    })
    .catch(error => {
        mostrarErro('Erro ao alterar status da role');
        console.error(error);
    })
    .finally(() => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('alterarStatusModal'));
        modal.hide();
    });
});

// Utilitários
function formatarDataHora(dataIso) {
    const data = new Date(dataIso);
    return data.toLocaleString('pt-BR', {
        dateStyle: 'short',
        timeStyle: 'short'
    });
}

function getCsrfToken() {
    const meta = document.querySelector('meta[name="_csrf"]');
    return meta ? meta.getAttribute('content') : '';
}

function mostrarSucesso(mensagem) {
    const toast = document.getElementById('toastSucesso');
    document.getElementById('toastSucessoMensagem').textContent = mensagem;

    const bsToast = new bootstrap.Toast(toast, { delay: 5000 });
    bsToast.show();
}

function mostrarErro(mensagem) {
    const toast = document.getElementById('toastErro');
    document.getElementById('toastErroMensagem').textContent = mensagem;

    const bsToast = new bootstrap.Toast(toast, { delay: 5000 });
    bsToast.show();
}
```

### Atualizar Tabela com Links de Ação
**Atualizar botões de ação na tabela:**

```html
<!-- Ações da Role -->
<td>
    <div class="d-flex align-items-center justify-content-center gap-1">
        <a href="#" class="btn btn-default btn-icon btn-sm rounded"
           onclick="visualizarRole('${role.id}')"
           title="Visualizar Detalhes">
            <i class="ti ti-eye fs-lg"></i>
        </a>
        <a th:href="@{/admin/roles/{id}/editar(id=${role.id})}"
           class="btn btn-default btn-icon btn-sm rounded"
           title="Editar">
            <i class="ti ti-edit fs-lg"></i>
        </a>
        <button th:if="${role.podeRemover()}"
                class="btn btn-danger btn-icon btn-sm rounded"
                onclick="confirmarRemocao('${role.id}', '${role.nome}', ${role.totalUsuarios})"
                title="Remover">
            <i class="ti ti-trash fs-lg"></i>
        </button>
        <button th:if="${!role.podeRemover()}"
                class="btn btn-secondary btn-icon btn-sm rounded disabled"
                title="${role.totalUsuarios > 0 ? 'Role possui usuários associados' : 'Role padrão não pode ser removida'}"
                disabled>
            <i class="ti ti-lock fs-lg"></i>
        </button>
        <button th:if="${role.ativa}"
                class="btn btn-warning btn-icon btn-sm rounded"
                onclick="alterarStatus('${role.id}', '${role.nome}', true)"
                title="Inativar">
            <i class="ti ti-power fs-lg"></i>
        </button>
        <button th:if="${!role.ativa}"
                class="btn btn-success btn-icon btn-sm rounded"
                onclick="alterarStatus('${role.id}', '${role.nome}', false)"
                title="Ativar">
            <i class="ti ti-power fs-lg"></i>
        </button>
    </div>
</td>
```

## Checklist de Validação
- [X] Modal de confirmação de remoção implementado
- [X] Modal de detalhes implementado
- [X] Modal de alteração de status implementado
- [X] Toasts de sucesso/erro implementados
- [X] Ação visualizar funcionando
- [X] Ação editar funcionando
- [X] Ação remover funcionando (sem usuários)
- [X] Ação remover desabilitada (com usuários ou padrão)
- [X] Ação ativar/inativar funcionando
- [X] Mensagens de sucesso exibidas corretamente
- [X] Mensagens de erro exibidas corretamente
- [X] Atualização da lista após cada ação
- [X] CSRF token incluído nas requisições AJAX

## Anotações
- Botão de remover deve estar desabilitado para roles com usuários
- Roles padrão não podem ser removidas (sempre desabilitado)
- Alteração de status requer confirmação
- Motivo opcional para alteração de status
- Toasts aparecem no canto inferior direito
- Modais são fechados automaticamente após confirmação
- Lista é recarregada após operações bem-sucedidas

## Dependências
- Epic 14 Story 01 - Template base (list.html)
- Epic 14 Story 04 - Controller (AdminRoleController)
- Epic 14 Story 02 - DTOs (RoleListResponse, RoleDetailResponse)

## Prioridade
**Alta** - Funcionalidades essenciais de gestão

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
