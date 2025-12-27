# User Story: Diferenciação Visual por Severidade

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-07

## Descrição
Implementar coloração visual diferenciada por tipo de evento crítico, utilizando badges coloridos, ícones e destaque visual para eventos de segurança crítica.

## Critérios de Aceite
- [ ] Badges de severidade coloridos (CRITICO=vermelho, WARNING=amarelo, INFO=azul, SUCCESS=verde)
- [ ] Ícones específicos por tipo de evento
- [ ] Destaque visual para eventos críticos (linha inteira em vermelho claro)
- [ ] Ícone de alerta para eventos de segurança
- [ ] Indicador visual de "Segurança Crítica" na linha
- [ ] Badges com ícones incorporados
- [ ] Classes CSS consistentes
- [ ] Filtro por severidade (opcional)
- [ ] Tooltip com descrição da severidade
- [ ] Acessibilidade (ARIA labels)

## Tarefas
1. Criar CSS para badges de severidade
2. Criar CSS para destaque de eventos críticos
3. Implementar JavaScript para renderizar badges com severidade
4. Adicionar classes CSS para cada tipo de severidade
5. Implementar destaque visual de linha para eventos críticos
6. Adicionar ícones de alerta para eventos de segurança
7. Testar responsividade e acessibilidade

## Instruções de Implementação

### CSS para Badges de Severidade
**Adicionar a `src/main/resources/static/css/pages/auditoria.css`:**

```css
/* Badges de Severidade */
.badge-critico {
    background-color: rgba(220, 53, 69, 0.1);
    color: #dc3545;
    border: 1px solid rgba(220, 53, 69, 0.3);
}

.badge-warning {
    background-color: rgba(255, 193, 7, 0.1);
    color: #ffc107;
    border: 1px solid rgba(255, 193, 7, 0.3);
}

.badge-info {
    background-color: rgba(13, 110, 253, 0.1);
    color: #0d6efd;
    border: 1px solid rgba(13, 110, 253, 0.3);
}

.badge-success {
    background-color: rgba(25, 135, 84, 0.1);
    color: #198754;
    border: 1px solid rgba(25, 135, 84, 0.3);
}

/* Destaque de eventos críticos na tabela */
.row-evento-critico {
    background-color: rgba(220, 53, 69, 0.05);
    border-left: 4px solid #dc3545;
}

.row-evento-critico:hover {
    background-color: rgba(220, 53, 69, 0.1);
}

/* Badges com ícones */
.badge-com-icone {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.375rem 0.75rem;
    border-radius: 0.375rem;
    font-size: 0.875rem;
    font-weight: 500;
    line-height: 1;
}

/* Ícones de alerta */
.icone-alerta-seguranca {
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0%, 100% {
        opacity: 1;
    }
    50% {
        opacity: 0.5;
    }
}

/* Tooltip de severidade */
[data-severidade]:hover::after {
    content: attr(data-severidade);
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background-color: #000;
    color: #fff;
    padding: 0.25rem 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.75rem;
    white-space: nowrap;
    z-index: 1000;
    margin-bottom: 0.25rem;
}
```

### JavaScript para Renderização com Severidade
**Adicionar a `src/main/resources/static/js/pages/auditoria.js`:**

```javascript
/**
 * Renderiza tabela de eventos com diferenciação por severidade.
 */
function renderizarEventosComSeveridade(eventos) {
    const tbody = document.getElementById('auditoriaTableBody');
    tbody.innerHTML = '';

    if (!eventos || eventos.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="ti ti-eye-off fs-32 mb-2 d-block"></i>
                    <p>Nenhum evento encontrado</p>
                </td>
            </tr>
        `;
        return;
    }

    eventos.forEach(evento => {
        const row = criarLinhaEventoComSeveridade(evento);
        tbody.innerHTML += row;
    });

    // Aplicar DataTables se existir
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#auditoriaTable').DataTable().destroy();
        $('#auditoriaTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/pt-BR.json'
            },
            order: [[5, 'desc']], // Ordenar por timestamp (data) descendente
            pageLength: 10,
            createdRow: function(row, data, index) {
                // Adicionar classe CSS se for evento crítico
                if (data.severidade === 'CRITICO') {
                    $(row).addClass('row-evento-critico');
                }
            }
        });
    }
}

/**
 * Cria linha HTML com diferenciação por severidade.
 */
function criarLinhaEventoComSeveridade(evento) {
    // Obter classes e ícones
    const badgeClasse = getBadgeClasseSeveridade(evento.severidade);
    const badgeSeveridade = getBadgeSeveridadeHtml(evento.severidade);
    const iconeTipo = getIconeTipo(evento.tipo);
    const ehCritico = evento.severidade === 'CRITICO';
    const ehSeguranca = isTipoSeguranca(evento.tipo);

    // Ícone de alerta se for evento crítico
    const iconeAlerta = ehCritico ? '<i class="ti ti-alert-triangle icone-alerta-seguranca me-1 text-danger"></i>' : '';

    return `
        <tr class="${ehCritico ? 'row-evento-critico' : ''}">
            <td>
                ${iconeAlerta}
                <span class="badge ${badgeClasse} badge-label">
                    <i class="${iconeTipo} me-1"></i>
                    ${evento.tipoDescricao}
                </span>
            </td>
            <td>${evento.usuario || '-'}</td>
            <td>${evento.realmNome || '-'}</td>
            <td class="text-truncate" style="max-width: 200px;" title="${evento.detalhes || ''}">
                ${evento.detalhesTruncado}
            </td>
            <td>
                <code class="bg-light">${evento.ipOrigem || '-'}</code>
            </td>
            <td>${evento.dataHoraFormatada}</td>
            <td>
                <div class="d-flex align-items-center justify-content-center gap-1">
                    <button class="btn btn-default btn-icon btn-sm rounded"
                            onclick="visualizarDetalhes('${evento.id}')"
                            title="Visualizar Detalhes"
                            data-severidade="${getDescricaoSeveridade(evento.severidade)}">
                        <i class="ti ti-eye fs-lg"></i>
                    </button>
                </div>
            </td>
        </tr>
    `;
}

/**
 * Verifica se é um tipo de evento de segurança.
 */
function isTipoSeguranca(tipo) {
    const tiposSeguranca = [
        'TENTATIVA_BRUTE_FORCE',
        'ACESSO_NEGADO',
        'TOKEN_INVALIDO',
        'TOKEN_EXPIRADO',
        'FALHA_LOGIN'
    ];

    return tiposSeguranca.includes(tipo);
}

/**
 * Retorna classe CSS para badge de severidade.
 */
function getBadgeClasseSeveridade(severidade) {
    switch(severidade) {
        case 'CRITICO':
            return 'badge-critico';
        case 'WARNING':
            return 'badge-warning';
        case 'INFO':
            return 'badge-info';
        case 'SUCCESS':
            return 'badge-success';
        default:
            return 'bg-secondary-subtle text-secondary';
    }
}

/**
 * Retorna HTML do badge de severidade.
 */
function getBadgeSeveridadeHtml(severidade) {
    const classe = getBadgeClasseSeveridade(severidade);
    const descricao = getDescricaoSeveridade(severidade);
    const icone = getIconeSeveridade(severidade);

    return `
        <span class="badge ${classe} badge-com-icone" title="${descricao}">
            <i class="${icone}"></i>
            ${descricao}
        </span>
    `;
}

/**
 * Retorna descrição amigável da severidade.
 */
function getDescricaoSeveridade(severidade) {
    switch(severidade) {
        case 'CRITICO':
            return 'Crítico - Requer atenção imediata';
        case 'WARNING':
            return 'Aviso - Monitoramento recomendado';
        case 'INFO':
            return 'Informação - Registro normal';
        case 'SUCCESS':
            return 'Sucesso - Operação concluída';
        default:
            return severidade;
    }
}

/**
 * Retorna ícone para severidade.
 */
function getIconeSeveridade(severidade) {
    switch(severidade) {
        case 'CRITICO':
            return 'ti ti-alert-triangle';
        case 'WARNING':
            return 'ti ti-alert-circle';
        case 'INFO':
            return 'ti ti-info-circle';
        case 'SUCCESS':
            return 'ti ti-check-circle';
        default:
            return 'ti ti-help-circle';
    }
}

/**
 * Filtra eventos por severidade (opcional).
 */
function filtrarPorSeveridade(severidade) {
    const params = new URLSearchParams();

    if (severidade && severidade !== 'All') {
        params.append('severidade', severidade);
    }

    const url = '/api/v1/admin/auditoria/eventos';

    if (params.toString()) {
        url += '?' + params.toString();
    }

    fetch(url)
        .then(response => response.json())
        .then(data => renderizarEventosComSeveridade(data))
        .catch(error => mostrarErro('Erro ao filtrar eventos: ' + error.message));
}
```

### CSS Adicional (opcional - pode ser incluído no template)
**No template `admin/auditoria/list.html`, no `<head>`:**

```html
<style>
/* Adicionar suporte para tooltips customizados */
[data-severidade] {
    position: relative;
}

[data-severidade]::after {
    content: attr(data-severidade);
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background-color: #000;
    color: #fff;
    padding: 0.25rem 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.75rem;
    white-space: nowrap;
    z-index: 1000;
    margin-bottom: 0.25rem;
    opacity: 0;
    transition: opacity 0.2s;
}

[data-severidade]:hover::after {
    opacity: 1;
}
</style>
```

### Ajustes na Tabela
**No template `admin/auditoria/list.html`, atualizar tabela:**

```html
<!-- Ajuste de CSS para suporte a classes de linha -->
<style>
.row-evento-critico td {
    padding-top: 0.75rem;
    padding-bottom: 0.75rem;
}
</style>
```

## Checklist de Validação
- [ ] CSS para badges de severidade criado
- [ ] CSS para destaque de eventos críticos criado
- [ ] Classes CSS consistentes (badge-critico, badge-warning, badge-info, badge-success)
- [ ] Animação de pulse implementada para ícones de alerta
- [ ] JavaScript para renderizar badges com severidade implementado
- [ ] Ícones específicos por tipo de evento mapeados
- [ ] Destaque visual de linha para eventos críticos implementado
- [ ] Ícone de alerta para eventos de segurança implementado
- [ ] Tooltip com descrição da severidade implementado
- [ ] Badges de severidade coloridos corretamente
- [ ] Filtro por severidade opcional implementado
- [ ] Acessibilidade com ARIA labels
- [ ] Responsividade mantida
- [ ] Cores acessíveis (contraste suficiente)
- [ ] Classes aplicadas consistentemente
- [ ] Eventos de segurança destacados visualmente

## Anotações
- Badges usam cores semânticas (vermelho=critico, amarelo=warning, azul=info, verde=success)
- Eventos críticos têm destaque na linha inteira (background + borda vermelha)
- Animação de pulse chama atenção para eventos de segurança
- Ícones de alerta pulsantes para críticos
- Tooltip mostra descrição detalhada da severidade
- Classes CSS consistentes e reutilizáveis
- Filtro por severidade é opcional (pode ser implementado em Story futura)
- Tipos de segurança: TENTATIVA_BRUTE_FORCE, ACESSO_NEGADO, TOKEN_INVALIDO, TOKEN_EXPIRADO, FALHA_LOGIN
- Ícones específicos por tipo de evento (login, logout, usuário+, shield, rotate, etc.)
- Tooltip customizado usando CSS pseudo-elemento
- Acessibilidade com descrições detalhadas em tooltips
- Cores com contraste suficiente para acessibilidade

## Dependências
- Story 01 (Template) - template base
- Story 02 (DTOs de Auditoria) - estrutura dos dados
- Story 03 (Service Layer) - dados dos eventos
- Story 04 (Controller API) - endpoints de eventos
- Story 06 (Modal de Detalhes) - integração
- Bootstrap 5 (já incluído no projeto)
- Tabler Icons (já incluído no projeto)

## Prioridade
**Média** - Melhoria importante para experiência visual

## Estimativa
- Implementação: 2 horas
- Testes: 1 hora
- Total: 3 horas
