# User Story: Rotação Manual com Modal de Confirmação

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-05

## Descrição
Implementar modal de confirmação para rotação manual de chaves criptográficas, permitindo ao administrador executar rotação manual com motivo opcional, exibir alertas sobre grace period e fornecer feedback visual de sucesso/erro.

## Critérios de Aceite
- [ ] Modal de rotação manual implementado
- [ ] Formulário com campo de motivo (opcional)
- [ ] Campos de realm e versão da chave (readonly)
- [ ] Alerta de warning sobre grace period
- [ ] Alerta de info sobre tokens existentes continuarem válidos
- [ ] Botão de confirmação com ícone de rotação
- [ ] Botão de cancelar
- [ ] Feedback visual de sucesso (toast)
- [ ] Feedback visual de erro (toast/alert)
- [ ] Loading state durante processamento
- [ ] Modal fecha automaticamente em caso de sucesso
- [ ] Tabelas atualizam após rotação bem-sucedida

## Tarefas
1. Adicionar modal de rotação ao template `admin/chaves/list.html`
2. Implementar formulário com campos readonly e motivo
3. Adicionar alertas informativos (warning e info)
4. Implementar botões de ação (confirmar/cancelar)
5. Criar JavaScript para abrir modal
6. Implementar JavaScript para executar rotação via AJAX
7. Adicionar loading state
8. Implementar feedback visual (toast)
9. Atualizar tabelas após rotação
10. Adicionar validações básicas

## Instruções de Implementação

### Modal de Rotação
**Adicionar ao final do template `admin/chaves/list.html`, antes do fechamento do `</body>`:**

```html
<!-- Modal de Rotação Manual -->
<div class="modal fade" id="rotacaoModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-warning">
                    <i class="ti ti-rotate me-2"></i>
                    Confirmar Rotação de Chave
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <!-- Alerta de Warning -->
                <div class="alert alert-warning">
                    <i class="ti ti-alert-triangle me-2"></i>
                    Esta ação vai rotacionar a chave criptográfica deste realm.
                    Todos os novos tokens emitidos usarão a nova chave.
                </div>

                <!-- Alerta de Info -->
                <div class="alert alert-info">
                    <i class="ti ti-info-circle me-2"></i>
                    Tokens existentes continuarão válidos até sua expiração natural (grace period).
                    Esta operação é <strong>irreversível</strong>.
                </div>

                <!-- Formulário -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Realm</label>
                    <input type="text" id="rotacaoRealm" class="form-control" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Versão Atual (kid)</label>
                    <input type="text" id="rotacaoVersaoAtual" class="form-control" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Motivo (Opcional)</label>
                    <textarea id="rotacaoMotivo" class="form-control" rows="3"
                              placeholder="Informe o motivo da rotação..."></textarea>
                    <small class="text-muted">Máximo 500 caracteres</small>
                </div>

                <!-- Loading State -->
                <div id="rotacaoLoading" class="d-none">
                    <div class="text-center py-3">
                        <div class="spinner-border text-warning" role="status">
                            <span class="visually-hidden">Carregando...</span>
                        </div>
                        <p class="mt-2 mb-0 text-muted">Processando rotação de chave...</p>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">
                    Cancelar
                </button>
                <button type="button" class="btn btn-warning" id="confirmarRotacaoBtn">
                    <i class="ti ti-rotate me-2"></i>
                    Rotacionar Chave
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Toast de Sucesso -->
<div class="toast-container position-fixed top-0 end-0 p-3">
    <div id="sucessoToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="toast-header bg-success text-white">
            <i class="ti ti-check-circle me-2"></i>
            <strong class="me-auto">Sucesso</strong>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">
            <span id="sucessoToastMessage"></span>
        </div>
    </div>

    <div id="erroToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="toast-header bg-danger text-white">
            <i class="ti ti-x-circle me-2"></i>
            <strong class="me-auto">Erro</strong>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">
            <span id="erroToastMessage"></span>
        </div>
    </div>
</div>
```

### JavaScript para Rotação
**Adicionar a `src/main/resources/static/js/pages/chaves.js`:**

```javascript
/**
 * Abre modal de rotação de chave.
 *
 * @param realmId ID do realm
 * @param realmNome Nome do realm
 * @param versaoAtual Versão atual da chave (kid)
 */
function confirmarRotacao(realmId, realmNome, versaoAtual) {
    // Preencher campos do modal
    document.getElementById('rotacaoRealm').value = realmNome;
    document.getElementById('rotacaoVersaoAtual').value = versaoAtual;
    document.getElementById('rotacaoMotivo').value = '';

    // Armazenar realmId no elemento para uso posterior
    document.getElementById('rotacaoModal').setAttribute('data-realm-id', realmId);

    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById('rotacaoModal'));
    modal.show();
}

/**
 * Executa rotação manual de chave.
 */
function executarRotacaoManual() {
    const realmId = document.getElementById('rotacaoModal').getAttribute('data-realm-id');
    const motivo = document.getElementById('rotacaoMotivo').value.trim();

    // Validar motivo (opcional, mas se informado deve ter <= 500 caracteres)
    if (motivo.length > 500) {
        mostrarErro('Motivo deve ter no máximo 500 caracteres');
        return;
    }

    // Mostrar loading
    document.getElementById('rotacaoLoading').classList.remove('d-none');
    document.getElementById('confirmarRotacaoBtn').disabled = true;

    // Executar rotação via AJAX
    fetch('/admin/chaves/api/rotacionar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            realmId: realmId,
            motivo: motivo || null
        })
    })
    .then(response => response.json())
    .then(data => {
        // Ocultar loading
        document.getElementById('rotacaoLoading').classList.add('d-none');
        document.getElementById('confirmarRotacaoBtn').disabled = false;

        if (data.success) {
            // Sucesso
            mostrarSucesso(data.message);

            // Fechar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('rotacaoModal'));
            modal.hide();

            // Atualizar tabelas
            const realmFilter = document.getElementById('realmFilter').value;
            carregarChavesPorFiltro(realmFilter);
            carregarHistoricoPorFiltro(realmFilter);
            carregarProximaRotacao();

        } else {
            // Erro
            mostrarErro(data.message);
        }
    })
    .catch(error => {
        // Ocultar loading
        document.getElementById('rotacaoLoading').classList.add('d-none');
        document.getElementById('confirmarRotacaoBtn').disabled = false;

        // Mostrar erro
        mostrarErro('Erro ao processar rotação de chave: ' + error.message);
    });
}

/**
 * Mostra toast de sucesso.
 */
function mostrarSucesso(mensagem) {
    document.getElementById('sucessoToastMessage').textContent = mensagem;
    const toast = new bootstrap.Toast(document.getElementById('sucessoToast'));
    toast.show();
}

/**
 * Mostra toast de erro.
 */
function mostrarErro(mensagem) {
    document.getElementById('erroToastMessage').textContent = mensagem;
    const toast = new bootstrap.Toast(document.getElementById('erroToast'));
    toast.show();
}

/**
 * Carrega chaves ativas por filtro de realm.
 */
function carregarChavesPorFiltro(realmId) {
    const url = realmId && realmId !== 'All'
        ? '/admin/chaves/api/ativas?realmId=' + realmId
        : '/admin/chaves/api/ativas';

    fetch(url)
        .then(response => response.json())
        .then(data => renderizarChaves(data))
        .catch(error => console.error('Erro ao carregar chaves:', error));
}

/**
 * Carrega histórico de rotações por filtro de realm.
 */
function carregarHistoricoPorFiltro(realmId) {
    const url = realmId && realmId !== 'All'
        ? '/admin/chaves/api/historico?realmId=' + realmId
        : '/admin/chaves/api/historico';

    fetch(url)
        .then(response => response.json())
        .then(data => renderizarHistorico(data))
        .catch(error => console.error('Erro ao carregar histórico:', error));
}

// Configurar evento de clique no botão de confirmar
document.getElementById('confirmarRotacaoBtn').addEventListener('click', executarRotacaoManual);

/**
 * Renderiza tabela de chaves ativas.
 */
function renderizarChaves(chaves) {
    const tbody = document.getElementById('chavesTableBody');
    tbody.innerHTML = '';

    chaves.forEach(chave => {
        const badgeDiasClasse = chave.diasRestantes < 0 ? 'bg-danger' :
                               chave.diasRestantes <= 7 ? 'bg-warning' :
                               'bg-success';

        const row = `
            <tr>
                <td class="fw-medium">${chave.realmNome}</td>
                <td><code>${chave.versao}</code></td>
                <td>${chave.tipo}</td>
                <td>${formatarData(chave.dataCriacao)}</td>
                <td>
                    ${formatarData(chave.dataExpiracao)}
                    <span class="badge ${badgeDiasClasse} ms-2">
                        ${chave.diasRestantes < 0 ? 'Expirada' : chave.diasRestantes + ' dias'}
                    </span>
                </td>
                <td>
                    <span class="badge ${chave.status.classeCss} badge-label">
                        ${chave.status.descricao}
                    </span>
                </td>
                <td>
                    <div class="d-flex align-items-center justify-content-center gap-1">
                        <button class="btn btn-default btn-icon btn-sm rounded"
                                onclick="confirmarRotacao('${chave.realmId}', '${chave.realmNome}', '${chave.versao}')"
                                title="Rotacionar Chave">
                            <i class="ti ti-rotate fs-lg"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

/**
 * Renderiza tabela de histórico de rotações.
 */
function renderizarHistorico(historico) {
    const tbody = document.getElementById('historicoTableBody');
    tbody.innerHTML = '';

    historico.forEach(rotacao => {
        const row = `
            <tr>
                <td class="fw-medium">${rotacao.realmNome}</td>
                <td><code>${rotacao.versaoAntiga}</code></td>
                <td><code>${rotacao.versaoNova}</code></td>
                <td>${rotacao.dataFormatada}</td>
                <td>${rotacao.responsavel}</td>
                <td>${rotacao.motivoFormatado}</td>
                <td>
                    <span class="badge ${rotacao.status.classeCss} badge-label">
                        ${rotacao.status.descricao}
                    </span>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

/**
 * Formata data para exibição.
 */
function formatarData(data) {
    return new Date(data).toLocaleDateString('pt-BR', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
    });
}
```

### Atualizar Botões na Tabela
**No template `admin/chaves/list.html`, atualizar botões de rotação:**

```html
<!-- No tbody de chaves ativas -->
<button class="btn btn-default btn-icon btn-sm rounded"
        onclick="confirmarRotacao('${chave.realmId}', '${chave.realmNome}', '${chave.versao}')"
        title="Rotacionar Chave">
    <i class="ti ti-rotate fs-lg"></i>
</button>
```

## Checklist de Validação
- [ ] Modal de rotação adicionado ao template
- [ ] Formulário com campos readonly (Realm, Versão)
- [ ] Campo de motivo (textarea) implementado
- [ ] Alerta de warning sobre operação irreversível
- [ ] Alerta de info sobre grace period/tokens existentes
- [ ] Botão de confirmar com ícone `ti-rotate`
- [ ] Botão de cancelar funcionando
- [ ] Toast de sucesso implementado
- [ ] Toast de erro implementado
- [ ] Loading state com spinner implementado
- [ ] Modal fecha automaticamente após sucesso
- [ ] Tabelas atualizam após rotação
- [ ] Validação de motivo (<= 500 caracteres)
- [ ] Feedback visual em todos os estados
- [ ] JavaScript configurado corretamente
- [ ] Bootstrap modals e toasts funcionando
- [ ] Responsividade mantida

## Anotações
- Modal usa Bootstrap 5 com classes padrão
- Toasts posicionados no canto superior direito
- Loading state previne cliques múltiplos
- Grace period explicado claramente ao usuário
- Motivo é opcional, mas limitado a 500 caracteres
- Tabelas atualizam automaticamente após rotação bem-sucedida
- RealmId armazenado no elemento do modal (data attribute)
- Formatação de data em português (pt-BR)
- Ícones Tabler aplicados consistentemente

## Dependências
- Story 01 (Template com Tabs) - template base
- Story 02 (DTOs de Chave) - estrutura dos dados
- Story 03 (Service Layer) - lógica de rotação
- Story 04 (Controller API) - endpoint de rotação
- Bootstrap 5 (já incluído no projeto)
- Toasts Bootstrap (já incluído no projeto)

## Prioridade
**Alta** - Funcionalidade essencial para gestão de chaves

## Estimativa
- Implementação: 3.5 horas
- Testes: 1.5 horas
- Total: 5 horas
