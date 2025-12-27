# User Story: Exportação CSV e Ajustes Finais

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-08

## Descrição
Implementar funcionalidade de exportação de eventos de auditoria em formato CSV, incluindo todos os filtros, loading states, tratamento de erros, e ajustes finais de responsividade e acessibilidade.

## Critérios de Aceite
- [ ] Botão "Exportar CSV" funcionando
- [ ] Exportação respeitando todos os filtros aplicados
- [ ] Arquivo CSV com BOM UTF-8 (suporte Excel português)
- [ ] Nome do arquivo com data atual (auditoria_YYYY-MM-DD.csv)
- [ ] Cabeçalho CSV (Tipo, Usuário, Realm, Detalhes, IP, Data)
- [ ] Loading state durante exportação
- [ ] Feedback visual (toast) após exportação
- [ ] Tratamento de erro se exportação falhar
- [ ] Validação de período máximo (1 ano)
- [ ] Ajustes de responsividade em mobile
- [ ] Melhorias de acessibilidade (ARIA labels, foco)

## Tarefas
1. Implementar JavaScript para exportação CSV
2. Adicionar validação de período antes de exportar
3. Implementar loading state durante exportação
4. Adicionar toast de sucesso/erro
5. Ajustar responsividade da tabela em mobile
6. Adicionar ARIA labels para acessibilidade
7. Testar exportação com diferentes filtros
8. Testar exportação em Excel (BOM UTF-8)
9. Testar em dispositivos móveis
10. Testar com leitor de tela

## Instruções de Implementação

### JavaScript para Exportação CSV
**Atualizar em `src/main/resources/static/js/pages/auditoria.js`:**

```javascript
/**
 * Exporta eventos de auditoria para CSV.
 */
function exportarCSV() {
    // Validar período antes de exportar
    const dataInicial = document.getElementById('dataInicial').value;
    const dataFinal = document.getElementById('dataFinal').value;

    if (dataInicial && dataFinal) {
        const dataIni = new Date(dataInicial);
        const dataFim = new Date(dataFinal);

        if (dataIni > dataFim) {
            mostrarAviso('Data inicial deve ser anterior ou igual à data final');
            return;
        }

        // Validar período máximo (1 ano)
        const umAnoEmMs = 365 * 24 * 60 * 60 * 1000;
        const diferencaEmMs = dataFim - dataIni;

        if (diferencaEmMs > umAnoEmMs) {
            mostrarAviso('Período máximo de 1 ano excedido');
            return;
        }
    }

    // Mostrar loading
    mostrarLoading('Exportando eventos...');

    // Construir URL com filtros
    const params = new URLSearchParams();

    const realmId = document.getElementById('realmFilter').value;
    const tipoEvento = document.getElementById('tipoEventoFilter').value;
    const usuario = document.getElementById('usuarioFilter').value;

    if (realmId && realmId !== 'All') params.append('realmId', realmId);
    if (tipoEvento && tipoEvento !== 'All') params.append('tipoEvento', tipoEvento);
    if (dataInicial) params.append('dataInicial', dataInicial);
    if (dataFinal) params.append('dataFinal', dataFinal);
    if (usuario) params.append('usuario', usuario);

    const url = '/api/v1/admin/auditoria/export';

    if (params.toString()) {
        url += '?' + params.toString();
    }

    // Abrir URL em nova aba
    const novaJanela = window.open(url, '_blank');

    if (novaJanela) {
        // Aguardar 2 segundos e então remover loading
        setTimeout(() => {
            ocultarLoading();
            mostrarSucesso('Arquivo CSV gerado! Verifique sua pasta de downloads.');
        }, 2000);
    } else {
        // Bloqueador de popup impediu abertura
        ocultarLoading();
        mostrarAviso('O download foi bloqueado pelo navegador. ' +
                   'Permita popups para este site e tente novamente.');
    }
}

/**
 * Exporta eventos críticos para CSV.
 */
function exportarEventosCriticos() {
    const realmId = document.getElementById('realmFilter').value;
    const dias = 7; // Últimos 7 dias

    // Mostrar loading
    mostrarLoading('Exportando eventos críticos...');

    // Buscar eventos críticos via API
    fetch('/api/v1/admin/auditoria/criticos?realmId=' + realmId + '&dias=' + dias)
        .then(response => response.json())
        .then(eventos => {
            ocultarLoading();

            if (!eventos || eventos.length === 0) {
                mostrarAviso('Nenhum evento crítico encontrado nos últimos ' + dias + ' dias');
                return;
            }

            // Gerar CSV manualmente
            gerarECriptarCSV(eventos, 'eventos_criticos_' + getHojeData() + '.csv');
            mostrarSucesso('Eventos críticos exportados com sucesso!');
        })
        .catch(error => {
            ocultarLoading();
            mostrarErro('Erro ao exportar eventos críticos: ' + error.message);
        });
}

/**
 * Gera e baixa CSV manualmente.
 */
function gerarECriptarCSV(eventos, nomeArquivo) {
    // Cabeçalho CSV
    const cabecalho = 'Tipo;Usuário;Realm;Detalhes;IP Origem;Data\n';

    // Linhas de dados
    const linhas = eventos.map(evento => {
        const detalhes = escaparCSV(evento.detalhes);
        const usuario = escaparCSV(evento.usuario);
        const realm = escaparCSV(evento.realmNome);
        const ip = escaparCSV(evento.ipOrigem);
        const data = evento.dataHoraFormatada;

        return `${evento.tipo.getDescricao()};${usuario};${realm};${detalhes};${ip};${data}`;
    }).join('\n');

    // Juntar tudo
    const csv = cabecalho + linhas;

    // Criar Blob
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });

    // Criar link de download
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nomeArquivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    // Limpar URL
    URL.revokeObjectURL(url);
}

/**
 * Retorna data de hoje no formato YYYY-MM-DD.
 */
function getHojeData() {
    const agora = new Date();
    const ano = agora.getFullYear();
    const mes = String(agora.getMonth() + 1).padStart(2, '0');
    const dia = String(agora.getDate()).padStart(2, '0');

    return `${ano}-${mes}-${dia}`;
}

/**
 * Escapa valor para CSV.
 */
function escaparCSV(valor) {
    if (valor == null) return '';

    valor = String(valor);

    // Escapar aspas
    valor = valor.replace(/"/g, '""');

    // Escapar vírgula
    valor = valor.replace(/,/g, '');

    // Envolver em aspas
    return '"' + valor + '"';
}
```

### Ajustes de Responsividade
**Adicionar a `src/main/resources/static/css/pages/auditoria.css`:**

```css
/* Ajustes de Responsividade para Mobile */
@media (max-width: 768px) {
    /* Card header com filtros */
    .card-header .d-flex {
        flex-direction: column;
        align-items: stretch;
        gap: 1rem;
    }

    /* Filtros em stack no mobile */
    .card-header .d-flex > div {
        width: 100% !important;
        margin-bottom: 0.5rem;
    }

    /* Botões de filtro em stack */
    .card-header .d-flex > button {
        width: 100%;
        margin-bottom: 0.5rem;
    }

    /* Tabela com scroll horizontal */
    .table-responsive {
        overflow-x: auto;
        -webkit-overflow-scrolling: touch;
    }

    /* Ajustar largura das colunas no mobile */
    #auditoriaTable {
        min-width: 800px;
    }

    /* Colunas de texto truncado */
    #auditoriaTable td:nth-child(4) {
        max-width: 150px;
    }

    /* Botões de ação menores */
    .btn-icon.btn-sm {
        padding: 0.25rem 0.5rem;
    }

    /* Paginação ajustada */
    .dataTables_wrapper .row {
        flex-direction: column;
        gap: 1rem;
    }

    .dataTables_wrapper .row:first-child {
        order: 1; // Paginação primeiro
    }

    .dataTables_wrapper .row:last-child {
        order: 2; // Info de registros depois
    }
}

/* Ajustes para Tablet */
@media (min-width: 769px) and (max-width: 991px) {
    /* Colunas ocultas em tablet */
    #auditoriaTable td:nth-child(4),
    #auditoriaTable th:nth-child(4) {
        display: none;
    }
}

/* Ajustes de Acessibilidade */
.visually-hidden-focusable:not(:focus):not(:focus-within) {
    position: absolute !important;
    width: 1px !important;
    height: 1px !important;
    padding: 0 !important;
    margin: -1px !important;
    overflow: hidden !important;
    clip: rect(0, 0, 0, 0) !important;
    white-space: nowrap !important;
    border: 0 !important;
}

/* Foco visível */
*:focus-visible {
    outline: 2px solid #0d6efd;
    outline-offset: 2px;
}

/* Alto contraste para modo escuro (suporte futuro) */
@media (prefers-contrast: high) {
    .badge {
        border: 2px solid currentColor;
    }
}
```

### Ajustes de Acessibilidade no Template
**Atualizar template `admin/auditoria/list.html`:**

```html
<!-- Ajustes de ARIA labels -->
<th scope="col" data-table-sort>Tipo
    <span class="visually-hidden">Tipo de evento de auditoria</span>
</th>

<th scope="col" data-table-sort>Usuário
    <span class="visually-hidden">Usuário que realizou a ação</span>
</th>

<th scope="col" data-table-sort>Realm
    <span class="visually-hidden">Realm do evento</span>
</th>

<th scope="col" data-table-sort>Detalhes
    <span class="visually-hidden">Detalhes do evento</span>
</th>

<th scope="col" data-table-sort>IP Origem
    <span class="visually-hidden">Endereço IP de origem</span>
</th>

<th scope="col" data-table-sort>Data
    <span class="visually-hidden">Data e hora do evento</span>
</th>

<th scope="col" class="text-center">Ações
    <span class="visually-hidden">Ações disponíveis para o evento</span>
</th>

<!-- Ajustes em botões -->
<button type="button" class="btn btn-primary" onclick="exportarCSV()"
        aria-label="Exportar eventos de auditoria para arquivo CSV"
        title="Exportar CSV">
    <i class="ti ti-download me-2"></i>Exportar CSV
</button>

<!-- Ajustes em inputs de busca -->
<div class="app-search">
    <input data-table-search type="text" class="form-control"
          aria-label="Buscar eventos por detalhes, usuário ou IP"
          placeholder="Buscar eventos...">
    <i data-lucide="search" class="app-search-icon text-muted" aria-hidden="true"></i>
</div>
```

### Loading State Aprimorado
**Adicionar a `auditoria.js`:**

```javascript
/**
 * Mostra loading state com overlay.
 */
function mostrarLoading(mensagem = 'Processando...') {
    // Verificar se já existe
    let loadingOverlay = document.getElementById('loadingOverlay');

    if (!loadingOverlay) {
        // Criar overlay
        loadingOverlay = document.createElement('div');
        loadingOverlay.id = 'loadingOverlay';
        loadingOverlay.className = 'position-fixed top-0 start-0 w-100 h-100 ' +
                               'd-flex justify-content-center align-items-center ' +
                               'bg-dark bg-opacity-25';
        loadingOverlay.style.zIndex = '9999';
        loadingOverlay.setAttribute('role', 'alert');
        loadingOverlay.setAttribute('aria-busy', 'true');
        loadingOverlay.innerHTML = `
            <div class="card p-4 shadow" role="status" aria-live="polite">
                <div class="d-flex align-items-center gap-3">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando</span>
                    </div>
                    <span class="fw-medium">${mensagem}</span>
                </div>
            </div>
        `;
        document.body.appendChild(loadingOverlay);
    }
}

/**
 * Oculta loading state.
 */
function ocultarLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}
```

## Checklist de Validação
- [ ] Botão "Exportar CSV" funcionando
- [ ] Exportação respeitando todos os filtros
- [ ] Arquivo CSV com BOM UTF-8
- [ ] Nome do arquivo com data atual
- [ ] Cabeçalho CSV correto
- [ ] Loading state funcionando
- [ ] Toast de sucesso após exportação
- [ ] Tratamento de erro se falhar
- [ ] Validação de período máximo (1 ano)
- [ ] Ajustes de responsividade em mobile
- [ ] Tabela com scroll horizontal em mobile
- [ ] Paginação ajustada em mobile
- [ ] ARIA labels em todas as colunas
- [ ] Foco visível com outline
- [ ] Suporte a alto contraste
- [ ] Filtros funcionando em mobile
- [ ] Botões com aria-label
- [ ] Teste em Excel (BOM UTF-8)
- [ ] Teste em iOS Safari
- [ ] Teste em Android Chrome

## Anotações
- BOM UTF-8 garante suporte a caracteres especiais em Excel
- Período máximo de 1 ano evita consultas excessivas
- Loading overlay impede interação durante exportação
- Toast posicionado no canto superior direito
- Bloqueador de popup é tratado com aviso ao usuário
- Responsividade usa media queries para breakpoints específicos
- Tabela usa scroll horizontal em telas pequenas
- ARIA labels fornecem contexto para leitores de tela
- Foco visível usa outline para acessibilidade
- Alto contraste é suportado automaticamente
- Validação de período é consistente com filtros
- Nome do arquivo inclui data para organização
- Escapamento de CSV previne problemas com delimitadores

## Dependências
- Story 01 (Template) - template base
- Story 02 (DTOs de Auditoria) - estrutura dos dados
- Story 03 (Service Layer) - serviço de exportação
- Story 04 (Controller API) - endpoint de exportação
- Story 05 (Filtros Avançados) - filtros já implementados
- Story 06 (Modal de Detalhes) - componentes compartilhados
- Story 07 (Coloração) - estilos base já criados
- Bootstrap 5 (já incluído no projeto)
- Tabler Icons (já incluído no projeto)

## Prioridade
**Média** - Funcionalidade útil para relatórios e compliance

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas

## Validações de Período no Service

**Adicionar método `validarPeriodo()` ao `AuditoriaService`:**

```java
/**
 * Valida período de exportação.
 */
private boolean validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
    if (dataInicio == null && dataFim == null) {
        return true; // Período vazio é válido
    }

    if (dataInicio == null) {
        // Data final deve ser válida
        return true;
    }

    if (dataFim == null) {
        // Data inicial deve ser válida
        return true;
    }

    // Data final deve ser maior ou igual à data inicial
    if (!dataFim.isAfter(dataInicio)) {
        return false;
    }

    // Período máximo de 1 ano
    Duration periodo = Duration.between(dataInicio, dataFim);
    long dias = periodo.toDays();
    
    if (dias > 365) {
        return false;
    }

    return true;
}
```

## Validações

- [ ] Método `validarPeriodo()` implementado
- [ ] Lógica de validação correta

## Anotações

- Validação de período é chamada pelo endpoint de exportação
- Período máximo configurável (deve ser constante no código ou propriedade)
- Período vazio (ambos null) é válido
- Data final ≥ data inicial é válida
- Máximo de 365 dias

## Dependências

- Java 8+ Duration API para cálculo de período
- Story 07 (Coloração por Severidade) - DTOs atualizados

## Próximos Passos

Todas as histórias da Epic 16 foram concluídas!

---

**Estado:** 🔲 Planejado
**Responsável:** BMad Team
