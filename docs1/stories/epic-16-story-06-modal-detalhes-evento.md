# User Story: Modal de Detalhes do Evento

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-06

## Descrição
Implementar modal para visualização detalhada de um evento de auditoria, mostrando todas as informações do evento, incluindo metadados (IP, User Agent, Timestamp), detalhes em formato JSON (pretty-print) e funcionalidade de cópia.

## Critérios de Aceite
- [ ] Modal de detalhes implementado
- [ ] Campos exibidos: Tipo, Usuário, Realm, Detalhes, IP Origem, User Agent, Timestamp, Severidade
- [ ] Detalhes JSON exibidos com pretty-print
- [ ] Botão "Copiar JSON" implementado
- [ ] Badges de tipo e severidade coloridos
- [ ] Ícones por tipo de evento
- [ ] Timestamp formatado em português
- [ ] IP Info adicional (rede interna/externa) exibido
- [ ] User Agent truncado com tooltip
- [ ] Modal fecha ao clicar em fechar ou fora
- [ ] Loading state durante carregamento
- [ ] Tratamento de erro para eventos não encontrados

## Tarefas
1. Adicionar modal de detalhes ao template `admin/auditoria/list.html`
2. Implementar layout do modal com Bootstrap 5
3. Implementar campos de exibição (grid de 2 colunas)
4. Implementar pretty-print para JSON
5. Adicionar botão de copiar JSON
6. Implementar JavaScript para carregar detalhes
7. Implementar JavaScript para renderizar modal
8. Adicionar loading state
9. Implementar cópia para clipboard
10. Testar responsividade

## Instruções de Implementação

### Modal de Detalhes
**Adicionar ao final do template `admin/auditoria/list.html`, antes do fechamento do `</body>`:**

```html
<!-- Modal de Detalhes do Evento -->
<div class="modal fade" id="detalhesEventoModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="ti ti-file-text me-2"></i>
                    Detalhes do Evento de Auditoria
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body" id="detalhesEventoContent">
                <!-- Conteúdo carregado via AJAX -->
                <div class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                    <p class="mt-2 mb-0 text-muted">Carregando detalhes...</p>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">
                    <i class="ti ti-x me-2"></i>Fechar
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Toast de Cópia -->
<div class="toast-container position-fixed top-0 end-0 p-3">
    <div id="copiaToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="toast-header bg-success text-white">
            <i class="ti ti-check me-2"></i>
            <strong class="me-auto">Cópia</strong>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">
            Detalhes copiados para a área de transferência!
        </div>
    </div>
</div>
```

### JavaScript para Detalhes
**Adicionar a `src/main/resources/static/js/pages/auditoria.js`:**

```javascript
/**
 * Abre modal de detalhes de um evento.
 *
 * @param eventoId ID do evento
 */
function visualizarDetalhes(eventoId) {
    // Mostrar loading
    const contentElement = document.getElementById('detalhesEventoContent');
    contentElement.innerHTML = `
        <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Carregando...</span>
            </div>
            <p class="mt-2 mb-0 text-muted">Carregando detalhes...</p>
        </div>
    `;

    // Buscar detalhes via AJAX
    fetch('/api/v1/admin/auditoria/eventos/' + eventoId)
        .then(response => response.json())
        .then(data => {
            renderizarDetalhesModal(data);

            // Abrir modal
            const modal = new bootstrap.Modal(document.getElementById('detalhesEventoModal'));
            modal.show();
        })
        .catch(error => {
            contentElement.innerHTML = `
                <div class="alert alert-danger">
                    <i class="ti ti-x-circle me-2"></i>
                    Erro ao carregar detalhes: ${error.message}
                </div>
            `;

            // Abrir modal mesmo com erro
            const modal = new bootstrap.Modal(document.getElementById('detalhesEventoModal'));
            modal.show();
        });
}

/**
 * Renderiza conteúdo do modal de detalhes.
 */
function renderizarDetalhesModal(evento) {
    const content = document.getElementById('detalhesEventoContent');

    const badgeClasse = getBadgeClasse(evento.severidade);
    const badgeSeveridade = getBadgeSeveridade(evento.severidade);
    const iconeTipo = getIconeTipo(evento.tipo);

    // Formatar JSON com pretty-print
    const detalhesJson = formatarJson(evento.detalhes);

    content.innerHTML = `
        <div class="row g-3">
            <!-- Tipo e Severidade -->
            <div class="col-md-6">
                <label class="form-label fw-semibold">Tipo</label>
                <div>
                    <span class="badge ${badgeClasse} badge-label">
                        <i class="${iconeTipo} me-1"></i>
                        ${evento.tipoDescricao}
                    </span>
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Severidade</label>
                <div>
                    <span class="badge ${badgeSeveridade} badge-label">
                        ${evento.severidadeDescricao}
                    </span>
                </div>
            </div>

            <!-- Usuário e Realm -->
            <div class="col-md-6">
                <label class="form-label fw-semibold">Usuário</label>
                <div>
                    ${evento.usuario || '-'}
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Realm</label>
                <div>
                    ${evento.realmNome || '-'}
                </div>
            </div>

            <!-- IP Origem e IP Info -->
            <div class="col-md-6">
                <label class="form-label fw-semibold">IP Origem</label>
                <div class="d-flex align-items-center gap-2">
                    <code>${evento.ipOrigem || '-'}</code>
                    ${evento.ipInfo ?
                        `<span class="badge bg-info-subtle text-info badge-label">
                             ${evento.ipInfo}
                         </span>` : ''}
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Timestamp</label>
                <div>
                    ${evento.timestampFormatada}
                </div>
            </div>

            <!-- User Agent (linha completa) -->
            <div class="col-12">
                <label class="form-label fw-semibold">User Agent</label>
                <div class="text-muted fs-sm" style="word-break: break-all;" title="${evento.userAgent}">
                    ${evento.userAgent || '-'}
                </div>
            </div>

            <!-- Detalhes (linha completa) -->
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <label class="form-label fw-semibold mb-0">Detalhes (JSON)</label>
                    <button type="button" class="btn btn-sm btn-light"
                            onclick="copiarDetalhesJson()"
                            title="Copiar JSON">
                        <i class="ti ti-copy me-1"></i>Copiar
                    </button>
                </div>
                <div class="alert alert-light border" style="max-height: 300px; overflow-y: auto;">
                    <pre class="mb-0"><code>${detalhesJson}</code></pre>
                </div>
            </div>
        </div>
    `;
}

/**
 * Formata JSON com pretty-print.
 */
function formatarJson(json) {
    if (!json || json === '{}') {
        return '<span class="text-muted">Sem detalhes</span>';
    }

    try {
        // Tentar parsear como JSON
        const objeto = typeof json === 'string' ? JSON.parse(json) : json;
        return JSON.stringify(objeto, null, 2);
    } catch (e) {
        // Não é JSON válido, retornar como texto
        return json;
    }
}

/**
 * Copia JSON dos detalhes para clipboard.
 */
function copiarDetalhesJson() {
    // Obter código JSON do elemento pre
    const preElement = document.querySelector('#detalhesEventoContent pre code');
    if (!preElement) return;

    const jsonText = preElement.textContent;

    // Copiar para clipboard
    navigator.clipboard.writeText(jsonText)
        .then(() => {
            // Mostrar toast de sucesso
            const toast = new bootstrap.Toast(document.getElementById('copiaToast'));
            toast.show();
        })
        .catch(err => {
            console.error('Erro ao copiar:', err);
            mostrarErro('Erro ao copiar para a área de transferência');
        });
}

/**
 * Retorna classe CSS para badge de severidade.
 */
function getBadgeClasse(severidade) {
    switch(severidade) {
        case 'CRITICO':
            return 'bg-danger-subtle text-danger';
        case 'WARNING':
            return 'bg-warning-subtle text-warning';
        case 'INFO':
            return 'bg-info-subtle text-info';
        case 'SUCCESS':
            return 'bg-success-subtle text-success';
        default:
            return 'bg-secondary-subtle text-secondary';
    }
}

/**
 * Retorna badge HTML para severidade.
 */
function getBadgeSeveridade(severidade) {
    const classe = getBadgeClasse(severidade);
    return `<span class="badge ${classe} badge-label">${severidade}</span>`;
}

/**
 * Retorna ícone para tipo de evento.
 */
function getIconeTipo(tipo) {
    const icones = {
        'LOGIN': 'ti ti-login',
        'LOGOUT': 'ti ti-logout',
        'FALHA_LOGIN': 'ti ti-x-circle',
        'CRIACAO_USUARIO': 'ti ti-user-plus',
        'EDICAO_USUARIO': 'ti ti-user-edit',
        'REMOCAO_USUARIO': 'ti ti-user-minus',
        'RESET_SENHA': 'ti ti-key',
        'RECUPERACAO_SENHA': 'ti ti-lock',
        'BLOQUEIO_USUARIO': 'ti ti-lock',
        'DESBLOQUEIO_USUARIO': 'ti ti-lock-open',
        'CRIACAO_REALM': 'ti ti-building',
        'EDICAO_REALM': 'ti ti-building',
        'DESATIVACAO_REALM': 'ti ti-building-off',
        'ATIVACAO_REALM': 'ti ti-building',
        'CRIACAO_ROLE': 'ti ti-shield',
        'EDICAO_ROLE': 'ti ti-shield',
        'REMOCAO_ROLE': 'ti ti-shield-off',
        'ROTACAO_MANUAL': 'ti ti-rotate',
        'ROTACAO_AUTOMATICA': 'ti ti-rotate-clockwise',
        'GERACAO_CHAVE': 'ti ti-key',
        'TENTATIVA_BRUTE_FORCE': 'ti ti-shield-alert',
        'ACESSO_NEGADO': 'ti ti-shield-x',
        'TOKEN_INVALIDO': 'ti ti-x',
        'TOKEN_EXPIRADO': 'ti ti-time',
        'BOOTSTRAP': 'ti ti-rocket',
        'ERRO_SISTEMA': 'ti ti-alert-triangle',
        'CONFIGURACAO_ALTERADA': 'ti ti-settings'
    };
    return icones[tipo] || 'ti ti-info-circle';
}
```

### Atualizar Tabela com Botão de Detalhes
**No template `admin/auditoria/list.html`, na tabela de eventos:**

```html
<!-- Coluna de Ações -->
<td>
    <div class="d-flex align-items-center justify-content-center gap-1">
        <button class="btn btn-default btn-icon btn-sm rounded"
                onclick="visualizarDetalhes('${evento.id}')"
                title="Visualizar Detalhes">
            <i class="ti ti-eye fs-lg"></i>
        </button>
    </div>
</td>
```

## Checklist de Validação
- [ ] Modal de detalhes adicionado ao template
- [ ] Modal configurado com Bootstrap 5
- [ ] Campos exibidos: Tipo, Usuário, Realm, Detalhes, IP, Timestamp, Severidade
- [ ] Detalhes JSON exibidos com pretty-print
- [ ] Botão "Copiar JSON" implementado
- [ ] Badges de tipo e severidade coloridos
- [ ] Ícones por tipo de evento mapeados
- [ ] Timestamp formatado em português (dd/MM/yyyy HH:mm:ss)
- [ ] IP Info exibido (Interno/Externo)
- [ ] User Agent exibido com truncamento e tooltip
- [ ] Loading state implementado
- [ ] Modal fecha ao clicar em fechar ou fora
- [ ] Cópia para clipboard funcionando
- [ ] Toast de cópia exibido
- [ ] Tratamento de erro para eventos não encontrados
- [ ] JSON formatado com identação de 2 espaços
- [ ] Responsividade mantida em dispositivos móveis
- [ ] Grid de 2 colunas implementado
- [ ] Alertas de erro visuais

## Anotações
- Modal usa Bootstrap 5 com classes padrão
- JSON é formatado com pretty-print (identação de 2 espaços)
- Cópia usa Clipboard API moderna (navigator.clipboard)
- Toast de cópia posicionado no canto superior direito
- User Agent é truncado visualmente mas completo no tooltip
- IP Info tenta identificar se é rede interna ou externa
- Loading state mostra spinner durante carregamento
- Ícones Tabler mapeados por tipo de evento
- Badges de severidade usam cores apropriadas
- Modal size é "modal-lg" para mais espaço
- Detalhes JSON em área scrollável (max-height: 300px)
- Layout responsivo com grid de 2 colunas
- Timestamp completo com segundos
- Erro na requisição mostra alerta dentro do modal

## Dependências
- Story 01 (Template) - template base
- Story 02 (DTOs de Auditoria) - estrutura dos dados
- Story 03 (Service Layer) - dados dos eventos
- Story 04 (Controller API) - endpoint de detalhes
- Bootstrap 5 (já incluído no projeto)
- Tabler Icons (já incluído no projeto)

## Prioridade
**Alta** - Funcionalidade essencial para detalhamento de eventos

## Estimativa
- Implementação: 3.5 horas
- Testes: 1.5 horas
- Total: 5 horas
