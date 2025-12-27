# User Story: Filtros Avançados e Busca

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-05

## Descrição
Implementar filtros avançados para consulta de eventos de auditoria, incluindo filtro por realm, tipo de evento (organizado em optgroups), período (data range), usuário e busca textual.

## Critérios de Aceite
- [ ] Filtro por Realm (dropdown) funcionando
- [ ] Filtro por Tipo de Evento (dropdown com optgroups) funcionando
- [ ] Filtro por Período (data inicial e final) funcionando
- [ ] Filtro por Usuário (text input) funcionando
- [ ] Busca textual (detalhes, usuário, IP) funcionando
- [ ] Botão "Aplicar Filtros" funcionando
- [ ] Botão "Limpar Filtros" funcionando
- [ ] Dropdown de registros por página funcionando
- [ ] Flatpickr configurado para seleção de data
- [ ] Filtros persistem durante navegação de páginas
- [ ] Feedback visual de filtros ativos
- [ ] Optgroups organizados por categoria
- [ ] Responsividade mantida em dispositivos móveis

## Tarefas
1. Implementar JavaScript para aplicar filtros
2. Implementar JavaScript para limpar filtros
3. Implementar persistência de filtros em sessionStorage
4. Adicionar visual de filtros ativos
5. Configurar Flatpickr para data range
6. Implementar debounce na busca textual
7. Testar combinações de filtros
8. Adicionar validações de período

## Instruções de Implementação

### JavaScript para Filtros
**Adicionar a `src/main/resources/static/js/pages/auditoria.js`:**

```javascript
/**
 * Chave para armazenamento de filtros no sessionStorage.
 */
const FILTROS_KEY = 'auditoriaFiltros';

/**
 * Carrega filtros salvos do sessionStorage.
 */
function carregarFiltrosSalvos() {
    const filtrosSalvos = sessionStorage.getItem(FILTROS_KEY);
    if (filtrosSalvos) {
        const filtros = JSON.parse(filtrosSalvos);

        // Restaurar filtros nos campos
        if (filtros.realmId) {
            document.getElementById('realmFilter').value = filtros.realmId;
        }
        if (filtros.tipoEvento) {
            document.getElementById('tipoEventoFilter').value = filtros.tipoEvento;
        }
        if (filtros.dataInicial) {
            document.getElementById('dataInicial').value = filtros.dataInicial;
        }
        if (filtros.dataFinal) {
            document.getElementById('dataFinal').value = filtros.dataFinal;
        }
        if (filtros.usuario) {
            document.getElementById('usuarioFilter').value = filtros.usuario;
        }

        // Aplicar filtros
        aplicarFiltros();
    }
}

/**
 * Salva filtros no sessionStorage.
 */
function salvarFiltros() {
    const filtros = {
        realmId: document.getElementById('realmFilter').value,
        tipoEvento: document.getElementById('tipoEventoFilter').value,
        dataInicial: document.getElementById('dataInicial').value,
        dataFinal: document.getElementById('dataFinal').value,
        usuario: document.getElementById('usuarioFilter').value
    };

    sessionStorage.setItem(FILTROS_KEY, JSON.stringify(filtros));
}

/**
 * Aplica filtros e recarrega eventos.
 */
function aplicarFiltros() {
    const params = new URLSearchParams();

    const realmId = document.getElementById('realmFilter').value;
    const tipoEvento = document.getElementById('tipoEventoFilter').value;
    const dataInicial = document.getElementById('dataInicial').value;
    const dataFinal = document.getElementById('dataFinal').value;
    const usuario = document.getElementById('usuarioFilter').value;
    const busca = document.querySelector('[data-table-search]').value;

    if (realmId && realmId !== 'All') params.append('realmId', realmId);
    if (tipoEvento && tipoEvento !== 'All') params.append('tipoEvento', tipoEvento);
    if (dataInicial) params.append('dataInicial', dataInicial);
    if (dataFinal) params.append('dataFinal', dataFinal);
    if (usuario) params.append('usuario', usuario);
    if (busca && busca.trim()) params.append('busca', busca.trim());

    // Salvar filtros
    salvarFiltros();

    // Atualizar visual de filtros ativos
    atualizarVisualFiltrosAtivos();

    // Carregar eventos
    carregarEventosComFiltros(params);
}

/**
 * Limpa todos os filtros e recarrega eventos.
 */
function limparFiltros() {
    // Limpar campos
    document.getElementById('realmFilter').value = 'All';
    document.getElementById('tipoEventoFilter').value = 'All';
    document.getElementById('dataInicial').value = '';
    document.getElementById('dataFinal').value = '';
    document.getElementById('usuarioFilter').value = '';
    document.querySelector('[data-table-search]').value = '';

    // Limpar sessionStorage
    sessionStorage.removeItem(FILTROS_KEY);

    // Atualizar visual
    atualizarVisualFiltrosAtivos();

    // Carregar todos os eventos
    carregarEventosComFiltros(new URLSearchParams());
}

/**
 * Carrega eventos com filtros.
 */
function carregarEventosComFiltros(params) {
    const url = '/api/v1/admin/auditoria/eventos';

    mostrarLoading('Carregando eventos...');

    const urlCompleta = params.toString() ? url + '?' + params.toString() : url;

    fetch(urlCompleta)
        .then(response => response.json())
        .then(data => {
            ocultarLoading();
            renderizarEventos(data);
        })
        .catch(error => {
            ocultarLoading();
            mostrarErro('Erro ao carregar eventos: ' + error.message);
        });
}

/**
 * Atualiza visual de filtros ativos.
 */
function atualizarVisualFiltrosAtivos() {
    const realmId = document.getElementById('realmFilter').value;
    const tipoEvento = document.getElementById('tipoEventoFilter').value;
    const dataInicial = document.getElementById('dataInicial').value;
    const dataFinal = document.getElementById('dataFinal').value;
    const usuario = document.getElementById('usuarioFilter').value;
    const busca = document.querySelector('[data-table-search]').value.trim();

    const filtrosAtivos = [];

    if (realmId && realmId !== 'All') {
        filtrosAtivos.push('Realm');
    }
    if (tipoEvento && tipoEvento !== 'All') {
        filtrosAtivos.push('Tipo');
    }
    if (dataInicial || dataFinal) {
        filtrosAtivos.push('Período');
    }
    if (usuario && usuario.trim()) {
        filtrosAtivos.push('Usuário');
    }
    if (busca) {
        filtrosAtivos.push('Busca');
    }

    // Exibir filtros ativos
    const filtrosAtivosElement = document.getElementById('filtrosAtivos');
    if (filtrosAtivosElement) {
        if (filtrosAtivos.length > 0) {
            filtrosAtivosElement.innerHTML = `
                <span class="badge bg-info me-1">${filtrosAtivos.join('</span> <span class="badge bg-info me-1">')}</span>
                <button type="button" class="btn btn-sm btn-light ms-2"
                        onclick="limparFiltros()">
                    <i class="ti ti-x me-1"></i>Limpar
                </button>
            `;
        } else {
            filtrosAtivosElement.innerHTML = '';
        }
    }
}

/**
 * Valida período de datas.
 */
function validarPeriodo() {
    const dataInicial = document.getElementById('dataInicial').value;
    const dataFinal = document.getElementById('dataFinal').value;

    if (dataInicial && dataFinal) {
        const dataIni = new Date(dataInicial);
        const dataFim = new Date(dataFinal);

        if (dataIni > dataFim) {
            mostrarAviso('Data inicial deve ser anterior ou igual à data final');
            return false;
        }

        // Validar período máximo (1 ano)
        const umAnoEmMs = 365 * 24 * 60 * 60 * 1000;
        const diferencaEmMs = dataFim - dataIni;

        if (diferencaEmMs > umAnoEmMs) {
            mostrarAviso('Período máximo de 1 ano excedido');
            return false;
        }
    }

    return true;
}

/**
 * Debounce para busca textual (300ms).
 */
let debounceTimer;
function debounceBusca() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        aplicarFiltros();
    }, 300);
}

/**
 * Configurar Flatpickr para seleção de datas.
 */
function configurarFlatpickr() {
    flatpickr('#dataInicial', {
        dateFormat: 'd M, Y',
        maxDate: 'today',
        locale: 'pt',
        onChange: function(selectedDates) {
            if (selectedDates.length > 0) {
                document.getElementById('dataFinal')._flatpickr.set('minDate', selectedDates[0]);
            }
        }
    });

    flatpickr('#dataFinal', {
        dateFormat: 'd M, Y',
        maxDate: 'today',
        locale: 'pt',
        onChange: function(selectedDates) {
            if (selectedDates.length > 0) {
                document.getElementById('dataInicial')._flatpickr.set('maxDate', selectedDates[0]);
            }
        }
    });
}

/**
 * Mostra aviso.
 */
function mostrarAviso(mensagem) {
    const alertContainer = document.getElementById('alertaContainer');
    if (alertContainer) {
        const alertElement = document.createElement('div');
        alertElement.className = 'alert alert-warning alert-dismissible fade show mb-3';
        alertElement.innerHTML = `
            <i class="ti ti-alert-triangle me-2"></i>
            ${mensagem}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        alertContainer.insertBefore(alertElement, alertContainer.firstChild);

        // Remover após 5 segundos
        setTimeout(() => {
            alertElement.remove();
        }, 5000);
    }
}

/**
 * Renderiza eventos na tabela.
 */
function renderizarEventos(page) {
    // Extrair filtros da URL atual
    const urlParams = new URLSearchParams(window.location.search);

    let url = '/api/v1/admin/auditoria/eventos';

    if (urlParams.toString()) {
        url += '?' + urlParams.toString();
    }

    mostrarLoading('Carregando eventos...');

    fetch(url)
        .then(response => response.json())
        .then(data => {
            ocultarLoading();
            atualizarTabelaEventos(data);
        })
        .catch(error => {
            ocultarLoading();
            mostrarErro('Erro ao carregar eventos: ' + error.message);
        });
}
```

### Atualizar Template do Filtro
**No template `admin/auditoria/list.html`, atualizar filtros:**

```html
<!-- Filtros Avançados -->
<div class="d-flex align-items-center gap-2">
    <span class="me-2 fw-semibold">Filtrar Por:</span>

    <!-- Realm Filter -->
    <div class="app-search">
        <select id="realmFilter" class="form-select form-control my-1 my-md-0" onchange="aplicarFiltros()">
            <option value="All">Todos os Realms</option>
            <option value="master">Master Realm</option>
            <option value="empresa-a">Empresa A</option>
            <option value="empresa-b">Empresa B</option>
            <option value="empresa-c">Empresa C</option>
        </select>
        <i data-lucide="building-cog" class="app-search-icon text-muted"></i>
    </div>

    <!-- Tipo de Evento Filter -->
    <div class="app-search">
        <select id="tipoEventoFilter" class="form-select form-control my-1 my-md-0" onchange="aplicarFiltros()">
            <option value="All">Todos os Tipos</option>
            <optgroup label="Autenticação">
                <option value="LOGIN">Login</option>
                <option value="LOGOUT">Logout</option>
                <option value="FALHA_LOGIN">Falha de Login</option>
            </optgroup>
            <optgroup label="Gestão de Usuários">
                <option value="CRIACAO_USUARIO">Criação de Usuário</option>
                <option value="EDICAO_USUARIO">Edição de Usuário</option>
                <option value="REMOCAO_USUARIO">Remoção de Usuário</option>
                <option value="RESET_SENHA">Reset de Senha</option>
            </optgroup>
            <optgroup label="Gestão de Realms">
                <option value="CRIACAO_REALM">Criação de Realm</option>
                <option value="EDICAO_REALM">Edição de Realm</option>
                <option value="DESATIVACAO_REALM">Desativação de Realm</option>
            </optgroup>
            <optgroup label="Gestão de Chaves">
                <option value="ROTACAO_MANUAL">Rotação Manual</option>
                <option value="ROTACAO_AUTOMATICA">Rotação Automática</option>
            </optgroup>
            <optgroup label="Segurança">
                <option value="TENTATIVA_BRUTE_FORCE">Tentativa Brute Force</option>
                <option value="ACESSO_NEGADO">Acesso Negado</option>
            </optgroup>
        </select>
        <i data-lucide="flag" class="app-search-icon text-muted"></i>
    </div>

    <!-- Período Filter (Data Range) -->
    <div class="d-flex gap-1 align-items-center">
        <div>
            <input type="date" id="dataInicial" class="form-control form-control-sm"
                   data-provider="flatpickr" data-date-format="d M, Y"
                   placeholder="Data Inicial" onchange="validarPeriodo(); aplicarFiltros()">
        </div>
        <span class="text-muted">-</span>
        <div>
            <input type="date" id="dataFinal" class="form-control form-control-sm"
                   data-provider="flatpickr" data-date-format="d M, Y"
                   placeholder="Data Final" onchange="validarPeriodo(); aplicarFiltros()">
        </div>
    </div>

    <!-- Usuário Filter -->
    <div class="app-search">
        <input type="text" id="usuarioFilter" class="form-control my-1 my-md-0"
               placeholder="Usuário..." onchange="aplicarFiltros()">
        <i data-lucide="user" class="app-search-icon text-muted"></i>
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

<!-- Container de Filtros Ativos -->
<div id="filtrosAtivos" class="mb-3"></div>

<!-- Alertas Dinâmicos -->
<div id="alertaContainer"></div>
```

### Configurar Eventos de Filtro
**Adicionar ao final do arquivo `auditoria.js`:**

```javascript
/**
 * Inicializa filtros quando o DOM estiver pronto.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Carregar filtros salvos
    carregarFiltrosSalvos();

    // Configurar Flatpickr
    if (typeof flatpickr !== 'undefined') {
        configurarFlatpickr();
    }

    // Configurar debounce na busca textual
    const searchInput = document.querySelector('[data-table-search]');
    if (searchInput) {
        searchInput.addEventListener('input', debounceBusca);
    }

    // Evento de mudança em filtros
    document.getElementById('realmFilter').addEventListener('change', aplicarFiltros);
    document.getElementById('tipoEventoFilter').addEventListener('change', aplicarFiltros);
    document.getElementById('dataInicial').addEventListener('change', validarPeriodo);
    document.getElementById('dataFinal').addEventListener('change', validarPeriodo);
    document.getElementById('usuarioFilter').addEventListener('change', aplicarFiltros);
});
```

## Checklist de Validação
- [ ] Filtro por Realm funcionando
- [ ] Filtro por Tipo de Evento (optgroups) funcionando
- [ ] Filtro por Período (data range) funcionando
- [ ] Filtro por Usuário funcionando
- [ ] Busca textual funcionando
- [ ] Botão "Aplicar Filtros" funcionando
- [ ] Botão "Limpar Filtros" funcionando
- [ ] Filtros persistem em sessionStorage
- [ ] Filtros salvos restaurados ao recarregar página
- [ ] Visual de filtros ativos exibido
- [ ] Validação de período (data inicial ≤ data final)
- [ ] Validação de período máximo (1 ano)
- [ ] Flatpickr configurado corretamente
- [ ] Debounce implementado na busca (300ms)
- [ ] Mínimo e máximo de datas configurados
- [ ] Avisos visuais implementados
- [ ] Responsividade mantida em dispositivos móveis

## Anotações
- Filtros são salvos em sessionStorage para persistência
- Optgroups organizam tipos por categoria (Autenticação, Gestão, Segurança, etc.)
- Flatpickr garante seleção de datas válidas
- Validação de período previne datas inconsistentes
- Período máximo de 1 ano para evitar consultas pesadas
- Debounce evita chamadas excessivas durante digitação
- Visual de filtros ativos mostra quais filtros estão aplicados
- Botão "Limpar" remove todos os filtros e recarrega
- Data máxima configurada para "hoje"
- Locale configurado para português (pt)

## Dependências
- Story 01 (Template) - template já existe
- Story 03 (Service Layer) - service com filtros implementado
- Story 04 (Controller API) - endpoints de filtros implementados
- Flatpickr.js (já incluído no projeto)
- Bootstrap 5 (já incluído no projeto)

## Prioridade
**Alta** - Funcionalidade essencial para consulta de auditoria

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
