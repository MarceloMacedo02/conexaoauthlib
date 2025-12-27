# User Story: Visualização do Histórico de Rotações

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-06

## Descrição
Implementar funcionalidade completa de visualização do histórico de rotações de chaves, incluindo tabela com todas as rotações, filtros por realm, paginação e detalhes de cada rotação.

## Critérios de Aceite
- [ ] Tabela de histórico exibindo todas as rotações
- [ ] Colunas: Realm, Versão Antiga, Versão Nova, Data, Responsável, Motivo, Status
- [ ] Filtro por realm funcionando
- [ ] Badges de status coloridas (Concluída, Em Andamento, Falhou)
- [ ] Ícones por tipo de rotação (Manual vs Automática)
- [ ] Formatação de data/hora em português
- [ ] Responsável exibe nome ou "Sistema" para rotações automáticas
- [ ] Motivo exibido ou "-" se não informado
- [ ] Ordenação por data (mais recente primeiro)
- [ ] Paginação implementada
- [ ] Atualização automática após nova rotação

## Tarefas
1. Implementar JavaScript para renderizar tabela de histórico
2. Implementar função para carregar histórico por realm
3. Adicionar ícones diferenciados por tipo de rotação
4. Implementar formatação de data/hora
5. Adicionar badging de status
6. Implementar paginação (se necessário)
7. Implementar atualização após nova rotação
8. Adicionar filtros avançados (data range, tipo)
9. Testar responsividade

## Instruções de Implementação

### JavaScript para Histórico
**Adicionar a `src/main/resources/static/js/pages/chaves.js`:**

```javascript
/**
 * Carrega histórico de rotações por filtro de realm.
 */
function carregarHistoricoPorFiltro(realmId) {
    const url = realmId && realmId !== 'All'
        ? '/admin/chaves/api/historico?realmId=' + realmId
        : '/admin/chaves/api/historico';

    mostrarLoading('Carregando histórico...');

    fetch(url)
        .then(response => response.json())
        .then(data => {
            ocultarLoading();
            renderizarHistorico(data);
        })
        .catch(error => {
            ocultarLoading();
            mostrarErro('Erro ao carregar histórico: ' + error.message);
        });
}

/**
 * Renderiza tabela de histórico de rotações.
 */
function renderizarHistorico(historico) {
    const tbody = document.getElementById('historicoTableBody');

    if (!historico || historico.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="ti ti-history fs-32 mb-2 d-block"></i>
                    <p>Nenhuma rotação encontrada</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = '';

    historico.forEach(rotacao => {
        const row = criarLinhaHistorico(rotacao);
        tbody.innerHTML += row;
    });

    // Aplicar DataTables se existir
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#historicoTable').DataTable().destroy();
        $('#historicoTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/pt-BR.json'
            },
            order: [[3, 'desc']], // Ordenar por data descendente
            pageLength: 10
        });
    }
}

/**
 * Cria linha HTML para histórico de rotação.
 */
function criarLinhaHistorico(rotacao) {
    // Determinar ícone por tipo de rotação
    const iconeTipo = rotacao.motivo && rotacao.motivo.toLowerCase().includes('automática')
        ? 'ti-rotate-clockwise' // Rotação automática
        : 'ti-rotate'; // Rotação manual

    // Determinar badge de status
    const statusBadge = getStatusBadge(rotacao.status);

    // Formatar data/hora
    const dataFormatada = formatarDataHora(rotacao.dataRotacao);

    // Formatar responsável
    const responsavel = rotacao.responsavel || 'Sistema';

    // Formatar motivo
    const motivo = rotacao.motivo || '-';

    return `
        <tr>
            <td class="fw-medium">${rotacao.realmNome}</td>
            <td><code class="bg-light">${rotacao.versaoAntiga}</code></td>
            <td><code class="bg-light">${rotacao.versaoNova}</code></td>
            <td>
                <div class="d-flex align-items-center gap-2">
                    <i class="ti ${iconeTipo} text-muted"></i>
                    ${dataFormatada}
                </div>
            </td>
            <td>
                ${responsavel === 'Sistema'
                    ? '<span class="badge bg-info-subtle text-info"><i class="ti ti-cog me-1"></i>Sistema</span>'
                    : responsavel}
            </td>
            <td class="text-truncate" style="max-width: 200px;" title="${motivo}">
                ${motivo}
            </td>
            <td>
                ${statusBadge}
            </td>
        </tr>
    `;
}

/**
 * Retorna badge HTML para status de rotação.
 */
function getStatusBadge(status) {
    switch(status) {
        case 'CONCLUIDA':
            return '<span class="badge bg-success-subtle text-success badge-label">' +
                   '<i class="ti ti-check me-1"></i>Concluída</span>';

        case 'EM_ANDAMENTO':
            return '<span class="badge bg-warning-subtle text-warning badge-label">' +
                   '<i class="ti ti-loader me-1"></i>Em Andamento</span>';

        case 'FALHOU':
            return '<span class="badge bg-danger-subtle text-danger badge-label">' +
                   '<i class="ti ti-x me-1"></i>Falhou</span>';

        default:
            return '<span class="badge bg-secondary-subtle text-secondary badge-label">' +
                   status + '</span>';
    }
}

/**
 * Formata data e hora para exibição.
 */
function formatarDataHora(data) {
    if (!data) return '-';

    const dataObj = new Date(data);

    return dataObj.toLocaleString('pt-BR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Atualiza histórico após nova rotação.
 */
function atualizarHistoricoAposRotacao() {
    const realmFilter = document.getElementById('realmFilter').value;
    carregarHistoricoPorFiltro(realmFilter);

    // Mostrar toast de sucesso
    mostrarSucesso('Histórico atualizado com nova rotação');
}

/**
 * Implementar filtros avançados para histórico (opcional).
 */
function filtrarHistoricoAvancado() {
    const dataInicial = document.getElementById('historicoDataInicial')?.value;
    const dataFinal = document.getElementById('historicoDataFinal')?.value;
    const tipoRotacao = document.getElementById('tipoRotacaoFilter')?.value;

    let url = '/admin/chaves/api/historico';
    const params = new URLSearchParams();

    const realmId = document.getElementById('realmFilter').value;
    if (realmId && realmId !== 'All') params.append('realmId', realmId);
    if (dataInicial) params.append('dataInicial', dataInicial);
    if (dataFinal) params.append('dataFinal', dataFinal);
    if (tipoRotacao && tipoRotacao !== 'All') params.append('tipo', tipoRotacao);

    if (params.toString()) {
        url += '?' + params.toString();
    }

    mostrarLoading('Filtrando histórico...');

    fetch(url)
        .then(response => response.json())
        .then(data => {
            ocultarLoading();
            renderizarHistorico(data);
        })
        .catch(error => {
            ocultarLoading();
            mostrarErro('Erro ao filtrar histórico: ' + error.message);
        });
}

/**
 * Limpa filtros de histórico.
 */
function limparFiltrosHistorico() {
    const dataInicial = document.getElementById('historicoDataInicial');
    const dataFinal = document.getElementById('historicoDataFinal');
    const tipoRotacao = document.getElementById('tipoRotacaoFilter');

    if (dataInicial) dataInicial.value = '';
    if (dataFinal) dataFinal.value = '';
    if (tipoRotacao) tipoRotacao.value = 'All';

    carregarHistoricoPorFiltro('All');
}
```

### Atualizar Template do Histórico
**No template `admin/chaves/list.html`, atualizar tab de histórico:**

```html
<!-- Tab: Histórico de Rotações -->
<div class="tab-pane fade" id="historicoRotacoes" role="tabpanel">
    <!-- Filtros Avançados (opcional) -->
    <div class="row mb-3">
        <div class="col-md-4">
            <label class="form-label">Data Inicial</label>
            <input type="date" id="historicoDataInicial" class="form-control"
                   data-provider="flatpickr" data-date-format="d M, Y">
        </div>
        <div class="col-md-4">
            <label class="form-label">Data Final</label>
            <input type="date" id="historicoDataFinal" class="form-control"
                   data-provider="flatpickr" data-date-format="d M, Y">
        </div>
        <div class="col-md-4">
            <label class="form-label">Tipo</label>
            <select id="tipoRotacaoFilter" class="form-select">
                <option value="All">Todos os Tipos</option>
                <option value="manual">Rotação Manual</option>
                <option value="automatica">Rotação Automática</option>
            </select>
        </div>
    </div>

    <!-- Botões de Filtro -->
    <div class="row mb-3">
        <div class="col-12">
            <button type="button" class="btn btn-primary me-2" onclick="filtrarHistoricoAvancado()">
                <i class="ti ti-filter me-2"></i>Filtrar
            </button>
            <button type="button" class="btn btn-light" onclick="limparFiltrosHistorico()">
                <i class="ti ti-x me-2"></i>Limpar Filtros
            </button>
        </div>
    </div>

    <!-- Tabela de Histórico -->
    <div class="table-responsive">
        <table id="historicoTable" class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
            <thead class="bg-light bg-opacity-25thead-sm">
                <tr class="text-uppercase fs-xxs">
                    <th data-table-sort>Realm</th>
                    <th data-table-sort>Versão Antiga</th>
                    <th data-table-sort>Versão Nova</th>
                    <th data-table-sort>Data</th>
                    <th data-table-sort>Responsável</th>
                    <th data-table-sort>Motivo</th>
                    <th data-table-sort data-column="status">Status</th>
                </tr>
            </thead>
            <tbody id="historicoTableBody">
                <!-- Conteúdo carregado via JavaScript -->
            </tbody>
        </table>
    </div>
</div>
```

### Atualizar Após Rotação
**Na função `executarRotacaoManual` do Story 05, adicionar:**

```javascript
function executarRotacaoManual() {
    // ... código existente ...

    .then(data => {
        if (data.success) {
            // Sucesso
            mostrarSucesso(data.message);

            // Fechar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('rotacaoModal'));
            modal.hide();

            // Atualizar tabelas (incluindo histórico)
            const realmFilter = document.getElementById('realmFilter').value;
            carregarChavesPorFiltro(realmFilter);
            carregarHistoricoPorFiltro(realmFilter); // Atualiza histórico
            carregarProximaRotacao();
        } else {
            mostrarErro(data.message);
        }
    })
    // ... resto do código ...
}
```

## Checklist de Validação
- [ ] Tabela de histórico renderizada corretamente
- [ ] Todas as colunas exibidas (Realm, Versão Antiga, Versão Nova, Data, Responsável, Motivo, Status)
- [ ] Filtro por realm funcionando
- [ ] Badges de status coloridas (Concluída=verde, Em Andamento=amarelo, Falhou=vermelho)
- [ ] Ícones diferenciados por tipo (Manual vs Automática)
- [ ] Data/hora formatada em português
- [ ] Responsável exibe "Sistema" para rotações automáticas
- [ ] Motivo exibido ou "-" se não informado
- [ ] Ordenação por data descendente
- [ ] Paginação com DataTables funcionando
- [ ] Atualização automática após nova rotação
- [ ] Filtros avançados (data range, tipo) funcionando
- [ ] Loading state funcionando
- [ ] Estado vazio exibido quando não há histórico
- [ ] Responsividade mantida em dispositivos móveis
- [ ] Acessibilidade (ARIA labels, etc.)

## Anotações
- Histórico ordenado por data descendente (mais recente primeiro)
- Ícone `ti-rotate-clockwise` para rotações automáticas
- Ícone `ti-rotate` para rotações manuais
- Badge de status com ícone e cor
- Data/hora formatada em português brasileiro (pt-BR)
- Filtros avançados são opcionais (data range, tipo)
- DataTables configurado com localização pt-BR
- Estado vazio exibe mensagem amigável
- Loading state feedback visual ao usuário
- Atualização automática após nova rotação

## Dependências
- Story 01 (Template com Tabs) - template base
- Story 02 (DTOs de Chave) - estrutura dos dados
- Story 03 (Service Layer) - lógica de histórico
- Story 04 (Controller API) - endpoint de histórico
- Story 05 (Rotação Manual) - integração
- DataTables.js (já incluído no projeto)
- Bootstrap 5 (já incluído no projeto)

## Prioridade
**Alta** - Funcionalidade essencial para auditoria de rotações

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
