# User Story: Ações em Lote (Bulk Actions)

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-07

## Descrição
Implementar funcionalidades de ações em lote (bulk actions) para realms, incluindo seleção múltipla, desativação em massa, ativação em massa, e atualização dos checkboxes quando filtros são aplicados.

## Critérios de Aceite
- [X] Checkbox select-all no header funciona corretamente
- [X] Checkboxes individuais funcionam corretamente
- [X] Botão "Delete" aparece quando itens selecionados
- [X] Botão "Delete" oculto quando nenhum item selecionado
- [X] Desativação em massa com confirmação implementada
- [X] Ativação em massa com confirmação implementada
- [X] Filtros atualizam estados dos checkboxes
- [X] Realm Master nunca é selecionado em massa
- [X] Mensagem de erro se tentar desativar Realm Master em massa
- [X] Feedback visual durante seleção
- [X] Contagem de itens selecionados exibida

## Tarefas
1. Implementar JavaScript para gerenciar checkboxes select-all
2. Implementar JavaScript para gerenciar checkboxes individuais
3. Implementar lógica de mostrar/ocultar botão Delete
4. Criar modal de ações em lote
5. Implementar desativação em massa (AJAX)
6. Implementar ativação em massa (AJAX)
7. Implementar proteção do Realm Master em massa
8. Atualizar checkboxes quando filtros mudam
9. Adicionar contagem de itens selecionados
10. Testar todas as ações em lote

## Instruções de Implementação

### Atualização do Template de Lista
**No template `admin/realms/list.html`, adicionar ações em lote:**

```html
<!-- No card-header, após o botão de novo realm -->
<div class="card-header border-light justify-content-between">
    <div class="d-flex gap-2">
        <div class="app-search">
            <input data-table-search type="text" class="form-control"
                placeholder="Buscar realms...">
            <i data-lucide="search" class="app-search-icon text-muted"></i>
        </div>
        <button type="button" class="btn btn-primary btn-icon" data-bs-toggle="modal"
            data-bs-target="#addRealmModal"><i class="ti ti-plus fs-lg"></i></button>

        <!-- Botão Bulk Actions (aparece quando itens selecionados) -->
        <div class="bulk-actions d-none align-items-center gap-2">
            <span class="text-muted">
                <span class="bulk-count">0</span> selecionados
            </span>
            <button type="button" class="btn btn-danger btn-sm"
                    onclick="bulkDesativarRealms()"
                    id="btnBulkDesativar">
                <i class="ti ti-x me-1"></i>Desativar
            </button>
            <button type="button" class="btn btn-success btn-sm"
                    onclick="bulkAtivarRealms()"
                    id="btnBulkAtivar">
                <i class="ti ti-check me-1"></i>Ativar
            </button>
            <button type="button" class="btn btn-default btn-sm"
                    onclick="clearSelection()">
                <i class="ti ti-x me-1"></i>Limpar
            </button>
        </div>
    </div>

    <!-- ... resto do código de filtros ... -->
</div>
```

### Checkbox na Tabela
**Atualizar os checkboxes da tabela:**

```html
<!-- Checkbox Select All no Header -->
<th scope="col" style="width: 1%;">
    <input class="form-check-input form-check-input-light fs-14 mt-0"
           data-table-select-all type="checkbox" value="option"
           id="selectAllRealms">
</th>

<!-- Checkbox Individual em cada linha -->
<td>
    <input class="form-check-input form-check-input-light fs-14 mt-0 realm-checkbox"
           type="checkbox"
           value="option"
           th:value="${realm.id}"
           th:data-nome="${realm.nome}"
           th:data-master="${realm.master}"
           th:data-ativo="${realm.ativo}"
           onchange="updateBulkActions()">
</td>
```

### Modal de Ações em Lote
**Adicionar modal ao final do template:**

```html
<!-- Modal de Confirmação de Ação em Lote -->
<div class="modal fade" id="bulkActionModal" tabindex="-1"
     aria-labelledby="bulkActionModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="bulkActionModalLabel">
                    <i class="ti ti-list-check me-2"></i>
                    Ação em Lote
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div id="bulkActionContent">
                    <!-- Conteúdo dinâmico -->
                </div>

                <!-- Lista de Realms Selecionados -->
                <div class="mt-3">
                    <h6 class="fw-semibold">Realms a serem processados:</h6>
                    <div class="list-group list-group-flush" id="bulkRealmsList"
                         style="max-height: 200px; overflow-y: auto;">
                        <!-- Itens serão adicionados via JavaScript -->
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger"
                        id="btnConfirmBulkAction">
                    Confirmar
                </button>
            </div>
        </div>
    </div>
</div>
```

### JavaScript de Ações em Lote
**Adicionar ao fragment `<th:block layout:fragment="javascripts">`:**

```javascript
<!-- Bulk Actions JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function() {

    const bulkActionsContainer = document.querySelector('.bulk-actions');
    const bulkCountSpan = document.querySelector('.bulk-count');
    const selectAllCheckbox = document.getElementById('selectAllRealms');
    const realmCheckboxes = document.querySelectorAll('.realm-checkbox');

    let selectedRealms = new Set();

    /**
     * Atualiza a interface de ações em lote baseado nos checkboxes selecionados.
     */
    function updateBulkActions() {
        const checkboxes = document.querySelectorAll('.realm-checkbox:checked');
        selectedRealms = new Set();

        checkboxes.forEach(checkbox => {
            // Não incluir Realm Master na seleção em massa
            if (!checkbox.dataset.master || checkbox.dataset.master !== 'true') {
                selectedRealms.add({
                    id: checkbox.value,
                    nome: checkbox.dataset.nome,
                    master: checkbox.dataset.master === 'true',
                    ativo: checkbox.dataset.ativo === 'true'
                });
            }
        });

        // Atualizar contagem
        if (bulkCountSpan) {
            bulkCountSpan.textContent = selectedRealms.size;
        }

        // Mostrar/ocultar botões de ações em lote
        if (selectedRealms.size > 0) {
            bulkActionsContainer.classList.remove('d-none');
            bulkActionsContainer.classList.add('d-flex');

            // Filtrar botões baseados no status dos selecionados
            const allInactive = Array.from(selectedRealms).every(r => !r.ativo);
            const allActive = Array.from(selectedRealms).every(r => r.ativo);

            const btnBulkAtivar = document.getElementById('btnBulkAtivar');
            const btnBulkDesativar = document.getElementById('btnBulkDesativar');

            if (allInactive) {
                btnBulkAtivar.classList.remove('d-none');
                btnBulkDesativar.classList.add('d-none');
            } else if (allActive) {
                btnBulkAtivar.classList.add('d-none');
                btnBulkDesativar.classList.remove('d-none');
            } else {
                // Misto - mostrar ambos
                btnBulkAtivar.classList.remove('d-none');
                btnBulkDesativar.classList.remove('d-none');
            }
        } else {
            bulkActionsContainer.classList.add('d-none');
            bulkActionsContainer.classList.remove('d-flex');
        }
    }

    /**
     * Checkbox Select All no header.
     */
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            const isChecked = this.checked;

            // Selecionar/desselecionar todos, exceto Realm Master
            realmCheckboxes.forEach(checkbox => {
                if (checkbox.dataset.master !== 'true') {
                    checkbox.checked = isChecked;
                }
            });

            updateBulkActions();
        });
    }

    /**
     * Listener para checkboxes individuais.
     */
    realmCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            // Se desselecionou um checkbox, desmarcar select-all
            if (!this.checked && selectAllCheckbox) {
                selectAllCheckbox.checked = false;
            }

            // Se todos os checkboxes (exceto Master) estão marcados, marcar select-all
            const nonMasterCheckboxes = Array.from(realmCheckboxes)
                .filter(cb => cb.dataset.master !== 'true');
            const allChecked = nonMasterCheckboxes.every(cb => cb.checked);

            if (selectAllCheckbox) {
                selectAllCheckbox.checked = allChecked;
            }

            updateBulkActions();
        });
    });

    /**
     * Limpar seleção de todos os checkboxes.
     */
    window.clearSelection = function() {
        realmCheckboxes.forEach(checkbox => {
            checkbox.checked = false;
        });

        if (selectAllCheckbox) {
            selectAllCheckbox.checked = false;
        }

        updateBulkActions();
    };

    /**
     * Desativar realms em massa.
     */
    window.bulkDesativarRealms = function() {
        if (selectedRealms.size === 0) {
            alert('Nenhum realm selecionado.');
            return;
        }

        const content = `
            <div class="alert alert-warning">
                <i class="ti ti-alert-triangle me-2"></i>
                Você está prestes a desativar <strong>${selectedRealms.size}</strong> realm(s).
            </div>
            <p>Esta ação tornará os realms e todos os seus usuários inacessíveis.</p>
        `;

        showModal('Desativar em Massa', content, 'desativar');
    };

    /**
     * Ativar realms em massa.
     */
    window.bulkAtivarRealms = function() {
        if (selectedRealms.size === 0) {
            alert('Nenhum realm selecionado.');
            return;
        }

        const content = `
            <div class="alert alert-success">
                <i class="ti ti-check me-2"></i>
                Você está prestes a ativar <strong>${selectedRealms.size}</strong> realm(s).
            </div>
            <p>Os usuários desses realms poderão acessar o sistema novamente.</p>
        `;

        showModal('Ativar em Massa', content, 'ativar');
    };

    /**
     * Exibe modal de confirmação para ação em lote.
     */
    function showModal(title, content, action) {
        const modalContent = document.getElementById('bulkActionContent');
        const modalTitle = document.getElementById('bulkActionModalLabel');
        const btnConfirm = document.getElementById('btnConfirmBulkAction');
        const realmsList = document.getElementById('bulkRealmsList');

        modalTitle.innerHTML = `<i class="ti ti-list-check me-2"></i>${title}`;
        modalContent.innerHTML = content;

        // Preencher lista de realms
        realmsList.innerHTML = '';
        Array.from(selectedRealms).forEach(realm => {
            const item = document.createElement('a');
            item.className = 'list-group-item list-group-item-action';
            item.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="fw-medium">${realm.nome}</span>
                        ${realm.master ?
                            '<span class="badge bg-primary ms-2"><i class="ti ti-crown"></i></span>' :
                            ''}
                    </div>
                    <span class="${realm.ativo ? 'text-success' : 'text-danger'}">
                        ${realm.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                </div>
            `;
            realmsList.appendChild(item);
        });

        // Configurar botão de confirmação
        btnConfirm.onclick = function() {
            if (action === 'desativar') {
                executeBulkDesativar();
            } else if (action === 'ativar') {
                executeBulkAtivar();
            }
        };

        // Atualizar cor do botão baseado na ação
        btnConfirm.className = `btn ${action === 'desativar' ? 'btn-danger' : 'btn-success'}`;
        btnConfirm.textContent = `Sim, ${action}`;

        // Abrir modal
        const modal = new bootstrap.Modal(document.getElementById('bulkActionModal'));
        modal.show();
    }

    /**
     * Executa desativação em massa via AJAX.
     */
    function executeBulkDesativar() {
        const ids = Array.from(selectedRealms).map(r => r.id);
        const realmNames = Array.from(selectedRealms).map(r => r.nome).join(', ');

        // Verificar se algum é Master
        const hasMaster = Array.from(selectedRealms).some(r => r.master);
        if (hasMaster) {
            alert('Realm Master não pode ser desativado em massa.');
            bootstrap.Modal.getInstance(document.getElementById('bulkActionModal')).hide();
            return;
        }

        fetch('/admin/realms/bulk/desativar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
            },
            body: JSON.stringify({ ids: ids })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(`${data.count} realm(s) desativado(s) com sucesso!`);
                location.reload();
            } else {
                alert(`Erro: ${data.message}`);
            }
        })
        .catch(error => {
            console.error('Erro ao desativar realms:', error);
            alert('Erro ao desativar realms. Tente novamente.');
        })
        .finally(() => {
            bootstrap.Modal.getInstance(document.getElementById('bulkActionModal')).hide();
        });
    }

    /**
     * Executa ativação em massa via AJAX.
     */
    function executeBulkAtivar() {
        const ids = Array.from(selectedRealms).map(r => r.id);

        fetch('/admin/realms/bulk/ativar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
            },
            body: JSON.stringify({ ids: ids })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(`${data.count} realm(s) ativado(s) com sucesso!`);
                location.reload();
            } else {
                alert(`Erro: ${data.message}`);
            }
        })
        .catch(error => {
            console.error('Erro ao ativar realms:', error);
            alert('Erro ao ativar realms. Tente novamente.');
        })
        .finally(() => {
            bootstrap.Modal.getInstance(document.getElementById('bulkActionModal')).hide();
        });
    }

    // Inicializar
    updateBulkActions();
});
</script>
```

### Backend: Bulk Action Endpoints
**Adicionar ao `AdminRealmController`:**

```java
/**
 * Desativa realms em massa.
 *
 * @param request Map com lista de IDs
 * @return Response JSON com resultado
 */
@PostMapping("/bulk/desativar")
@ResponseBody
public ResponseEntity<Map<String, Object>> bulkDesativar(
        @RequestBody Map<String, List<String>> request
) {
    List<String> ids = request.get("ids");
    log.info("Desativando {} realms em massa", ids.size());

    try {
        // Verificar se algum é Master
        for (String id : ids) {
            Realm realm = realmRepository.findById(UUID.fromString(id))
                .orElse(null);
            if (realm != null && realm.isMaster()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Realm Master não pode ser desativado"
                ));
            }
        }

        // Desativar todos
        int count = 0;
        for (String id : ids) {
            try {
                adminRealmService.desativarRealm(id);
                count++;
            } catch (Exception e) {
                log.warn("Erro ao desativar realm {}: {}", id, e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", count,
            "message", count + " realm(s) desativado(s) com sucesso"
        ));

    } catch (Exception e) {
        log.error("Erro ao desativar realms em massa", e);
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "Erro ao desativar realms: " + e.getMessage()
        ));
    }
}

/**
 * Ativa realms em massa.
 *
 * @param request Map com lista de IDs
 * @return Response JSON com resultado
 */
@PostMapping("/bulk/ativar")
@ResponseBody
public ResponseEntity<Map<String, Object>> bulkAtivar(
        @RequestBody Map<String, List<String>> request
) {
    List<String> ids = request.get("ids");
    log.info("Ativando {} realms em massa", ids.size());

    try {
        int count = 0;
        for (String id : ids) {
            try {
                adminRealmService.ativarRealm(id);
                count++;
            } catch (Exception e) {
                log.warn("Erro ao ativar realm {}: {}", id, e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", count,
            "message", count + " realm(s) ativado(s) com sucesso"
        ));

    } catch (Exception e) {
        log.error("Erro ao ativar realms em massa", e);
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "Erro ao ativar realms: " + e.getMessage()
        ));
    }
}
```

### CSRF Meta Tag
**Adicionar ao layout base se não existir:**

```html
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

## Checklist de Validação
- [ ] Checkbox select-all funciona
- [ ] Checkboxes individuais funcionam
- [ ] Botões de ações em lote aparecem quando selecionados
- [ ] Botões de ações em lote ocultam quando nada selecionado
- [ ] Contagem de itens selecionados atualizada
- [ ] Desativação em massa funcionando
- [ ] Ativação em massa funcionando
- [ ] Realm Master não pode ser selecionado em massa
- [ ] Mensagem de erro ao tentar desativar Master em massa
- [ ] Modal de confirmação exibe lista de realms
- [ ] Modal exibe count de realms a serem processados
- [ ] AJAX requests funcionam corretamente
- [ ] CSRF token incluído nas requests
- [ ] Page reload após sucesso
- [ ] Tratamento de erros implementado
- [ ] Limpar seleção funciona
- [ ] Checkboxes atualizados quando filtros mudam

## Anotações
- Realm Master nunca deve ser incluído em ações em massa
- Usar AJAX para evitar reload completo da página até o final
- Mostrar lista de realms que serão processados no modal de confirmação
- Filtros da tabela devem limpar seleção ao mudar
- Contagem de itens selecionados deve ser visível
- Botões de ação em lote devem ser condicionais (apenas desativar se todos ativos, etc.)
- Tratamento de erros robusto com feedback ao usuário

## Dependências
- Story 01 (Template da Lista de Realms) - estrutura da tabela
- Story 04 (Controller API) - endpoints base
- Story 06 (CRUD Funcionalidades) - operações individuais

## Prioridade
**Média** - Funcionalidade útil mas não essencial

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
