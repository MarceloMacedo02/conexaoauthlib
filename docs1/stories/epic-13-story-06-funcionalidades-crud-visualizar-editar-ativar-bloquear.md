# User Story: Funcionalidades CRUD - Visualizar, Editar, Ativar/Bloquear

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-06

## Descrição
Implementar as funcionalidades de CRUD de usuários na tabela de listagem, incluindo visualização de detalhes, edição de usuário, ativação, bloqueio com confirmação, e botões de ação na tabela.

## Critérios de Aceite
- [ ] Botão de visualização (ícone ti-eye) funcionando
- [ ] Modal/página de detalhes exibe informações completas do usuário
- [ ] Botão de edição (ícone ti-edit) funcionando
- [ ] Modal de edição preenche todos os campos automaticamente
- [ ] Botão de bloqueio (ícone ti-lock) funcionando
- [ ] Botão de ativação (ícone ti-lock-open) funcionando
- [ ] Modal de confirmação de bloqueio com campo de motivo
- [ ] Modal de confirmação de ativação
- [ ] Botões de ação mudam dinamicamente (block/unblock) baseado no status
- [ ] Toast de feedback exibido após cada ação
- [ ] Tabela atualizada automaticamente após ações
- [ ] Loading states exibidos durante operações AJAX

## Tarefas
1. Criar modal/página de visualização detalhada de usuário
2. Implementar botão de visualização na tabela
3. Implementar botão de edição na tabela (abre modal da Story 05)
4. Implementar modal de confirmação de bloqueio
5. Implementar modal de confirmação de ativação
6. Implementar botões de ação na tabela (View, Edit, Block, Unblock)
7. Implementar JavaScript para carregar dados do usuário no modal de edição
8. Implementar JavaScript para ações de bloqueio/ativação via AJAX
9. Implementar toast notifications para feedback
10. Implementar loading states
11. Testar visualização de detalhes
12. Testar edição de usuário
13. Testar bloqueio de usuário
14. Testar ativação de usuário

## Instruções de Implementação

### Modal de Visualização de Detalhes
**Adicionar ao final do template `admin/usuarios/list.html`:**

```html
<!-- Modal de Visualização de Detalhes do Usuário -->
<div class="modal fade" id="viewUsuarioModal" tabindex="-1"
     aria-labelledby="viewUsuarioModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="viewUsuarioModalLabel">Detalhes do Usuário</h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="row g-3">

                    <!-- Avatar e Nome -->
                    <div class="col-12 text-center mb-3">
                        <div id="viewUsuarioAvatar"
                             class="avatar avatar-lg bg-primary text-white rounded-circle mb-2">
                            JS
                        </div>
                        <h5 id="viewUsuarioNome" class="mb-0">João Silva</h5>
                        <p id="viewUsuarioEmail" class="text-muted mb-0">
                            joao.silva@empresa.com.br
                        </p>
                    </div>

                    <!-- Informações Básicas -->
                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">CPF</label>
                        <p id="viewUsuarioCpf" class="mb-0">123.456.789-01</p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Status</label>
                        <p id="viewUsuarioStatus" class="mb-0">
                            <span class="badge bg-success-subtle text-success">Ativo</span>
                        </p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Realm</label>
                        <p id="viewUsuarioRealm" class="mb-0">Master Realm</p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Empresa ID</label>
                        <p id="viewUsuarioEmpresaId" class="mb-0">COMP-001</p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Tenant ID</label>
                        <p id="viewUsuarioTenantId" class="mb-0">TENANT-001</p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Roles</label>
                        <div id="viewUsuarioRoles" class="mb-0">
                            <span class="badge bg-danger-subtle text-danger me-1">ADMIN</span>
                            <span class="badge bg-info-subtle text-info">USER</span>
                        </div>
                    </div>

                    <!-- Datas -->
                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Criado Em</label>
                        <p id="viewUsuarioDataCriacao" class="mb-0">Dez 15, 2025 10:30</p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Última Atualização</label>
                        <p id="viewUsuarioDataAtualizacao" class="mb-0">Dez 23, 2025 15:45</p>
                    </div>

                    <div class="col-md-6" id="viewUsuarioBloqueioContainer" style="display: none;">
                        <label class="fw-semibold text-muted">Data de Bloqueio</label>
                        <p id="viewUsuarioDataBloqueio" class="mb-0">Dez 20, 2025 09:00</p>
                    </div>

                    <div class="col-md-6" id="viewUsuarioReativacaoContainer" style="display: none;">
                        <label class="fw-semibold text-muted">Data de Reativação</label>
                        <p id="viewUsuarioDataReativacao" class="mb-0">Dez 22, 2025 14:00</p>
                    </div>

                    <div class="col-md-12" id="viewUsuarioMotivoBloqueioContainer" style="display: none;">
                        <label class="fw-semibold text-muted">Motivo do Bloqueio</label>
                        <p id="viewUsuarioMotivoBloqueio" class="mb-0 text-danger">
                            Violação de política de segurança
                        </p>
                    </div>

                    <div class="col-md-6">
                        <label class="fw-semibold text-muted">Senha Alterada Em</label>
                        <p id="viewUsuarioSenhaAlterada" class="mb-0">Nov 10, 2025</p>
                    </div>

                </div> <!-- end row -->
            </div> <!-- end modal-body -->

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">
                    <i class="ti ti-x me-2"></i>Fechar
                </button>
                <button type="button" class="btn btn-primary" id="btnEditFromView">
                    <i class="ti ti-edit me-2"></i>Editar
                </button>
            </div> <!-- end modal-footer -->

        </div>
    </div>
</div>
```

### Modal de Confirmação de Bloqueio
```html
<!-- Modal de Confirmação de Bloqueio -->
<div class="modal fade" id="blockUsuarioModal" tabindex="-1"
     aria-labelledby="blockUsuarioModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-danger" id="blockUsuarioModalLabel">
                    <i class="ti ti-alert-triangle me-2"></i>Confirmar Bloqueio
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="alert alert-warning mb-3">
                    <i class="ti ti-alert-circle me-2"></i>
                    <strong>Atenção!</strong> O usuário bloqueado não conseguirá fazer login no sistema.
                </div>

                <div class="mb-3">
                    <label class="form-label">Usuário:</label>
                    <p id="blockUsuarioNome" class="fw-medium mb-0">João Silva</p>
                    <p id="blockUsuarioEmail" class="text-muted small">joao.silva@empresa.com.br</p>
                </div>

                <div class="mb-0">
                    <label class="form-label">Motivo do Bloqueio <span class="text-danger">*</span></label>
                    <textarea class="form-control"
                              id="blockUsuarioMotivo"
                              rows="3"
                              placeholder="Informe o motivo do bloqueio..."
                              required></textarea>
                </div>
            </div> <!-- end modal-body -->

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">
                    <i class="ti ti-x me-2"></i>Cancelar
                </button>
                <button type="button" class="btn btn-danger" id="btnConfirmBlock">
                    <i class="ti ti-lock me-2"></i>Confirmar Bloqueio
                </button>
            </div> <!-- end modal-footer -->

        </div>
    </div>
</div>
```

### Modal de Confirmação de Ativação
```html
<!-- Modal de Confirmação de Ativação -->
<div class="modal fade" id="activateUsuarioModal" tabindex="-1"
     aria-labelledby="activateUsuarioModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-success" id="activateUsuarioModalLabel">
                    <i class="ti ti-check-circle me-2"></i>Confirmar Ativação
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="alert alert-success mb-3">
                    <i class="ti ti-check me-2"></i>
                    O usuário conseguirá fazer login novamente no sistema.
                </div>

                <div class="mb-0">
                    <label class="form-label">Usuário:</label>
                    <p id="activateUsuarioNome" class="fw-medium mb-0">João Silva</p>
                    <p id="activateUsuarioEmail" class="text-muted small">joao.silva@empresa.com.br</p>
                </div>
            </div> <!-- end modal-body -->

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">
                    <i class="ti ti-x me-2"></i>Cancelar
                </button>
                <button type="button" class="btn btn-success" id="btnConfirmActivate">
                    <i class="ti ti-lock-open me-2"></i>Confirmar Ativação
                </button>
            </div> <!-- end modal-footer -->

        </div>
    </div>
</div>
```

### Botões de Ação na Tabela
**Atualizar coluna de ações na tabela:**

```html
<td>
    <div class="d-flex align-items-center justify-content-center gap-1">
        <a href="#" class="btn btn-default btn-icon btn-sm rounded"
           data-action="view"
           th:data-usuario-id="${usuario.id}"
           title="Visualizar">
            <i class="ti ti-eye fs-lg"></i>
        </a>
        <a href="#" class="btn btn-default btn-icon btn-sm rounded"
           data-action="edit"
           th:data-usuario-id="${usuario.id}"
           title="Editar">
            <i class="ti ti-edit fs-lg"></i>
        </a>
        <a href="#" class="btn btn-default btn-icon btn-sm rounded"
           data-action="block"
           th:data-usuario-id="${usuario.id}"
           th:if="${usuario.status == 'Ativo'}"
           title="Bloquear">
            <i class="ti ti-lock fs-lg"></i>
        </a>
        <a href="#" class="btn btn-success btn-icon btn-sm rounded"
           data-action="activate"
           th:data-usuario-id="${usuario.id}"
           th:if="${usuario.status == 'Bloqueado'}"
           title="Ativar">
            <i class="ti ti-lock-open fs-lg"></i>
        </a>
    </div>
</td>
```

### JavaScript para Ações CRUD
**Adicionar ao fragment `<th:block layout:fragment="javascripts">`:**

```javascript
<!-- Usuario CRUD Actions JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function() {

    // Variáveis globais
    let currentUsuarioId = null;

    // Inicializar Bootstrap modals
    const viewUsuarioModal = new bootstrap.Modal(document.getElementById('viewUsuarioModal'));
    const blockUsuarioModal = new bootstrap.Modal(document.getElementById('blockUsuarioModal'));
    const activateUsuarioModal = new bootstrap.Modal(document.getElementById('activateUsuarioModal'));

    // Botões de ação na tabela
    document.addEventListener('click', function(e) {
        const actionBtn = e.target.closest('[data-action]');
        if (!actionBtn) return;

        const action = actionBtn.dataset.action;
        const usuarioId = actionBtn.dataset.usuarioId;

        if (!action || !usuarioId) return;

        switch(action) {
            case 'view':
                carregarVisualizacao(usuarioId);
                break;
            case 'edit':
                carregarEdicao(usuarioId);
                break;
            case 'block':
                abrirModalBloqueio(usuarioId);
                break;
            case 'activate':
                abrirModalAtivacao(usuarioId);
                break;
        }
    });

    // Carregar visualização de detalhes
    function carregarVisualizacao(usuarioId) {
        currentUsuarioId = usuarioId;

        fetch(`/admin/usuarios/${usuarioId}`)
            .then(response => response.json())
            .then(usuario => {
                // Preencher dados no modal
                document.getElementById('viewUsuarioNome').textContent = usuario.nome;
                document.getElementById('viewUsuarioEmail').textContent = usuario.email;
                document.getElementById('viewUsuarioCpf').textContent = formatarCPF(usuario.cpf);
                document.getElementById('viewUsuarioRealm').textContent = usuario.realmNome;
                document.getElementById('viewUsuarioEmpresaId').textContent = usuario.empresaId || '-';
                document.getElementById('viewUsuarioTenantId').textContent = usuario.tenantId || '-';

                // Status badge
                const statusHtml = usuario.status === 'Ativo' ?
                    '<span class="badge bg-success-subtle text-success">Ativo</span>' :
                    '<span class="badge bg-danger-subtle text-danger">Bloqueado</span>';
                document.getElementById('viewUsuarioStatus').innerHTML = statusHtml;

                // Roles badges
                const rolesHtml = usuario.roles.map(role =>
                    getRoleBadgeHtml(role)
                ).join(' ');
                document.getElementById('viewUsuarioRoles').innerHTML = rolesHtml;

                // Avatar
                document.getElementById('viewUsuarioAvatar').textContent = usuario.iniciais;
                document.getElementById('viewUsuarioAvatar').className =
                    `avatar avatar-lg ${usuario.avatarColor} text-white rounded-circle mb-2`;

                // Datas
                document.getElementById('viewUsuarioDataCriacao').textContent =
                    formatarDataHora(usuario.dataCriacao);
                document.getElementById('viewUsuarioDataAtualizacao').textContent =
                    formatarDataHora(usuario.dataUltimaAtualizacao);
                document.getElementById('viewUsuarioSenhaAlterada').textContent =
                    usuario.senhaAlteradaEm || '-';

                // Bloqueio/Reativação
                const bloqueioContainer = document.getElementById('viewUsuarioBloqueioContainer');
                const reativacaoContainer = document.getElementById('viewUsuarioReativacaoContainer');
                const motivoContainer = document.getElementById('viewUsuarioMotivoBloqueioContainer');

                if (usuario.dataBloqueio) {
                    bloqueioContainer.style.display = 'block';
                    document.getElementById('viewUsuarioDataBloqueio').textContent =
                        formatarDataHora(usuario.dataBloqueio);
                } else {
                    bloqueioContainer.style.display = 'none';
                }

                if (usuario.dataReativacao) {
                    reativacaoContainer.style.display = 'block';
                    document.getElementById('viewUsuarioDataReativacao').textContent =
                        formatarDataHora(usuario.dataReativacao);
                } else {
                    reativacaoContainer.style.display = 'none';
                }

                if (usuario.motivoBloqueio) {
                    motivoContainer.style.display = 'block';
                    document.getElementById('viewUsuarioMotivoBloqueio').textContent =
                        usuario.motivoBloqueio;
                } else {
                    motivoContainer.style.display = 'none';
                }

                // Botão de edição
                document.getElementById('btnEditFromView').onclick = function() {
                    viewUsuarioModal.hide();
                    carregarEdicao(usuarioId);
                };

                // Abrir modal
                viewUsuarioModal.show();
            })
            .catch(error => {
                mostrarToast('error', 'Erro ao carregar detalhes do usuário');
                console.error(error);
            });
    }

    // Carregar edição (reutiliza modal da Story 05)
    function carregarEdicao(usuarioId) {
        // Redirecionar para página de edição (ou abrir modal via AJAX)
        window.location.href = `/admin/usuarios/${usuarioId}/edit`;
    }

    // Abrir modal de bloqueio
    function abrirModalBloqueio(usuarioId) {
        currentUsuarioId = usuarioId;

        // Buscar dados básicos do usuário
        fetch(`/admin/usuarios/${usuarioId}`)
            .then(response => response.json())
            .then(usuario => {
                document.getElementById('blockUsuarioNome').textContent = usuario.nome;
                document.getElementById('blockUsuarioEmail').textContent = usuario.email;
                document.getElementById('blockUsuarioMotivo').value = '';

                blockUsuarioModal.show();
            })
            .catch(error => {
                mostrarToast('error', 'Erro ao carregar usuário');
                console.error(error);
            });
    }

    // Confirmar bloqueio
    document.getElementById('btnConfirmBlock').addEventListener('click', function() {
        const motivo = document.getElementById('blockUsuarioMotivo').value.trim();

        if (!motivo) {
            alert('Por favor, informe o motivo do bloqueio');
            return;
        }

        fetch(`/admin/usuarios/${currentUsuarioId}/bloquear`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `motivo=${encodeURIComponent(motivo)}`
        })
        .then(response => response.json())
        .then(data => {
            blockUsuarioModal.hide();
            mostrarToast('success', 'Usuário bloqueado com sucesso!');
            location.reload(); // Recarregar página para atualizar tabela
        })
        .catch(error => {
            mostrarToast('error', 'Erro ao bloquear usuário');
            console.error(error);
        });
    });

    // Abrir modal de ativação
    function abrirModalAtivacao(usuarioId) {
        currentUsuarioId = usuarioId;

        // Buscar dados básicos do usuário
        fetch(`/admin/usuarios/${usuarioId}`)
            .then(response => response.json())
            .then(usuario => {
                document.getElementById('activateUsuarioNome').textContent = usuario.nome;
                document.getElementById('activateUsuarioEmail').textContent = usuario.email;

                activateUsuarioModal.show();
            })
            .catch(error => {
                mostrarToast('error', 'Erro ao carregar usuário');
                console.error(error);
            });
    }

    // Confirmar ativação
    document.getElementById('btnConfirmActivate').addEventListener('click', function() {
        fetch(`/admin/usuarios/${currentUsuarioId}/ativar`, {
            method: 'PUT'
        })
        .then(response => response.json())
        .then(data => {
            activateUsuarioModal.hide();
            mostrarToast('success', 'Usuário ativado com sucesso!');
            location.reload(); // Recarregar página para atualizar tabela
        })
        .catch(error => {
            mostrarToast('error', 'Erro ao ativar usuário');
            console.error(error);
        });
    });

    // Utilitários
    function formatarCPF(cpf) {
        if (!cpf) return '-';
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }

    function formatarDataHora(dataStr) {
        if (!dataStr) return '-';
        const data = new Date(dataStr);
        return data.toLocaleString('pt-BR');
    }

    function getRoleBadgeHtml(role) {
        const roleColors = {
            'ADMIN': 'bg-danger-subtle text-danger',
            'USER': 'bg-info-subtle text-info',
            'SERVICE': 'bg-warning-subtle text-warning'
        };
        const colorClass = roleColors[role] || 'bg-secondary-subtle text-secondary';
        return `<span class="badge ${colorClass}">${role}</span>`;
    }

    function mostrarToast(tipo, mensagem) {
        // Implementar toast notification (usar Bootstrap Toasts)
        // Placeholder - implementação real depende do sistema de toasts do projeto
        alert(`${tipo.toUpperCase()}: ${mensagem}`);
    }

});
</script>
```

## Checklist de Validação
- [ ] Modal de visualização criado
- [ ] Modal de bloqueio criado
- [ ] Modal de ativação criado
- [ ] Botão de visualização implementado
- [ ] Botão de edição implementado
- [ ] Botão de bloqueio implementado (mostra só para usuários ativos)
- [ ] Botão de ativação implementado (mostra só para usuários bloqueados)
- [ ] Dados do usuário carregados no modal de visualização
- [ ] Modal de bloqueio exibe nome e email do usuário
- [ ] Campo de motivo de bloqueio implementado
- [ ] Modal de ativação exibe nome e email do usuário
- [ ] JavaScript para carregar visualização via AJAX implementado
- [ ] JavaScript para bloqueio via AJAX implementado
- [ ] JavaScript para ativação via AJAX implementado
- [ ] Toast de feedback implementado
- [ ] Loading states implementados
- [ ] Tabela atualizada após ações
- [ ] Formatação de CPF implementada
- [ ] Formatação de data/hora implementada
- [ ] Badges de roles coloridos
- [ ] Avatar com iniciais e cor

## Anotações
- Botões de ação usam atributos `data-action` e `data-usuario-id` para identificação
- Modal de edição redireciona para página `/admin/usuarios/{id}/edit` (ou pode usar modal via AJAX)
- Bloqueio requer motivo obrigatório (auditoria)
- Ativação não requer motivo
- Status do botão muda dinamicamente (lock vs unlock)
- Visualização exibe todas as informações do usuário
- CPF é formatado no padrão brasileiro (XXX.XXX.XXX-XX)
- Datas são formatadas em português brasileiro
- Roles exibidos como badges coloridos
- Toasts devem usar Bootstrap Toasts ou sistema existente no projeto
- Após ação bem-sucedida, recarregar página para atualizar tabela

## Dependências
- Story 01 (Template da Lista de Usuários) - tabela base
- Story 03 (Backend Service Layer) - métodos de bloqueio/ativação
- Story 04 (Controller API) - endpoints PUT de bloqueio/ativação
- Story 05 (Modal de Criação/Edição) - modal de edição

## Prioridade
**Alta** - Funcionalidades essenciais de gestão de usuários

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
