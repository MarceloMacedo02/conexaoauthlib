# Epic 15 - Stories Summary (Página de Gestão de Chaves)

## Overview
Este documento contém o resumo de todas as histórias para o Epic 15 (Página de Gestão de Chaves Criptográficas).

---

## Story 02: DTOs de Chave (Java Records)

### Descrição
Criar DTOs (Data Transfer Objects) como Java Records para transferência de dados entre controller e service para gestão de chaves.

### Records a Criar:

**ChaveAtivaResponse.java**
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record ChaveAtivaResponse(
    String id,
    String realmId,
    String realmNome,
    String versao, // kid
    String tipo, // RSA-2048
    LocalDateTime dataCriacao,
    LocalDateTime dataExpiracao,
    StatusChave status // ATIVA, EXPIRADA, EXPIRANDO
) {
    public static ChaveAtivaResponse from(Chave chave, String realmNome, String realmId) {
        int diasParaExpiracao = (int) java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), chave.getDataExpiracao()
        );

        StatusChave status = diasParaExpiracao < 0 ? StatusChave.EXPIRADA :
                           diasParaExpiracao <= 7 ? StatusChave.EXPIRANDO :
                           StatusChave.ATIVA;

        return new ChaveAtivaResponse(
            chave.getId().toString(),
            realmId,
            realmNome,
            chave.getKid(),
            chave.getTipo(),
            chave.getDataCriacao(),
            chave.getDataExpiracao(),
            status
        );
    }

    public int getDiasRestantes() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), dataExpiracao
        );
    }
}

enum StatusChave {
    ATIVA, EXPIRADA, EXPIRANDO
}
```

**RotacaoHistoricoResponse.java**
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record RotacaoHistoricoResponse(
    String id,
    String realmId,
    String realmNome,
    String versaoAntiga,
    String versaoNova,
    LocalDateTime dataRotacao,
    String responsavel,
    String motivo,
    StatusRotacao status // CONCLUIDA, EM_ANDAMENTO, FALHOU
) {
    public static RotacaoHistoricoResponse from(RotacaoChave rotacao, String realmNome, String realmId) {
        return new RotacaoHistoricoResponse(
            rotacao.getId().toString(),
            realmId,
            realmNome,
            rotacao.getVersaoAntiga(),
            rotacao.getVersaoNova(),
            rotacao.getDataRotacao(),
            rotacao.getResponsavel(),
            rotacao.getMotivo(),
            rotacao.getStatus()
        );
    }
}

enum StatusRotacao {
    CONCLUIDA, EM_ANDAMENTO, FALHOU
}
```

### Estimativa: 3 horas (implementação) + 2 horas (testes) = 5 horas

---

## Story 03: Backend Service Layer - ChaveService

### Descrição
Implementar service layer para gestão de chaves criptográficas com métodos para listar, rotacionar e buscar histórico.

### Métodos a Implementar:
- `listarChavesPorRealm(realmId)` - Retorna chaves ativas
- `listarHistoricoRotacoes(realmId)` - Retorna histórico
- `rotacionarChaveManual(realmId, responsavel, motivo)` - Rotação manual
- `buscarProximaRotacaoAutomatica()` - Data da próxima rotação

### Estimativa: 5 horas (implementação) + 3 horas (testes) = 8 horas

---

## Story 04: Controller API - AdminChaveController

### Descrição
Implementar controller com endpoints para listagem e rotação manual de chaves.

### Endpoints:
```
GET /admin/chaves
GET /api/v1/admin/chaves/{realmId}
GET /api/v1/admin/chaves/historico/{realmId}
POST /api/v1/chaves/{realmId}/rotacionar
```

### Estimativa: 4 horas (implementação) + 2 horas (testes) = 6 horas

---

## Story 05: Funcionalidade de Rotação Manual com Modal

### Descrição
Implementar modal de confirmação para rotação manual de chaves.

### Modal de Rotação:
```html
<div class="modal fade" id="rotacaoModal" tabindex="-1">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-warning">
                    <i class="ti ti-rotate me-2"></i>
                    Confirmar Rotação de Chave
                </h5>
            </div>
            <div class="modal-body">
                <div class="alert alert-warning">
                    <i class="ti ti-alert-triangle me-2"></i>
                    Esta ação vai rotacionar a chave criptográfica deste realm.
                </div>
                <div class="mb-3">
                    <label class="form-label">Realm</label>
                    <input type="text" id="rotacaoRealm" class="form-control" readonly />
                </div>
                <div class="mb-3">
                    <label class="form-label">Versão Atual</label>
                    <input type="text" id="rotacaoVersaoAtual" class="form-control" readonly />
                </div>
                <div class="mb-3">
                    <label class="form-label">Motivo (Opcional)</label>
                    <textarea id="rotacaoMotivo" class="form-control" rows="3"></textarea>
                </div>
                <div class="alert alert-info">
                    <i class="ti ti-info-circle me-2"></i>
                    Tokens existentes continuarão válidos até sua expiração natural.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-warning" id="confirmarRotacaoBtn">
                    <i class="ti ti-rotate me-2"></i>Rotacionar Chave
                </button>
            </div>
        </div>
    </div>
</div>
```

### JavaScript para Rotação:
```javascript
function confirmarRotacao(realmId, versaoAtual) {
    document.getElementById('rotacaoRealm').value = realmId;
    document.getElementById('rotacaoVersaoAtual').value = versaoAtual;
    const modal = new bootstrap.Modal(document.getElementById('rotacaoModal'));
    modal.show();
}

document.getElementById('confirmarRotacaoBtn').addEventListener('click', function() {
    const realmId = document.getElementById('rotacaoRealm').value;
    const motivo = document.getElementById('rotacaoMotivo').value;

    fetch(`/api/v1/chaves/${realmId}/rotacionar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ motivo })
    })
    .then(response => response.json())
    .then(data => {
        mostrarSucesso('Rotação iniciada com sucesso!');
        carregarChavesPorRealm(realmId);
        carregarHistoricoPorRealm(realmId);
    })
    .catch(error => mostrarErro('Erro ao rotacionar chave'));
});
```

### Estimativa: 4 horas (implementação) + 2 horas (testes) = 6 horas

---

## Story 06: Visualização de Histórico de Rotações

### Descrição
Implementar funcionalidade completa de visualização do histórico de rotações de chaves.

### JavaScript para Renderizar Histórico:
```javascript
function renderizarHistorico(historico) {
    const tbody = document.getElementById('historicoTableBody');
    tbody.innerHTML = '';

    historico.forEach(rotacao => {
        const statusBadge = rotacao.status === 'CONCLUIDA'
            ? '<span class="badge bg-success-subtle text-success badge-label">Concluída</span>'
            : '<span class="badge bg-warning-subtle text-warning badge-label">Em Andamento</span>';

        const row = `
            <tr>
                <td class="fw-medium">${rotacao.realmNome}</td>
                <td><code>${rotacao.versaoAntiga}</code></td>
                <td><code>${rotacao.versaoNova}</code></td>
                <td>${formatarDataHora(rotacao.dataRotacao)}</td>
                <td>${rotacao.responsavel}</td>
                <td>${rotacao.motivo || '-'}</td>
                <td>${statusBadge}</td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}
```

### Estimativa: 3 horas (implementação) + 1 hora (testes) = 4 horas

---

## Story 07: Countdown de Próxima Rotação Automática

### Descrição
Implementar countdown visual mostrando dias restantes até a próxima rotação automática (dia 1 do mês).

### JavaScript para Countdown:
```javascript
function atualizarCountdownRotacao() {
    const hoje = new Date();
    const proximaRotacao = new Date();
    proximaRotacao.setMonth(proximaRotacao.getMonth() + 1);
    proximaRotacao.setDate(1);
    proximaRotacao.setHours(0, 0, 0, 0);

    const diff = proximaRotacao - hoje;
    const dias = Math.ceil(diff / (1000 * 60 * 60 * 24));

    const countdownElement = document.getElementById('countdownRotacao');
    if (countdownElement) {
        countdownElement.textContent = dias + ' dias';

        // Alerta visual se faltar menos de 7 dias
        if (dias <= 7) {
            countdownElement.classList.remove('bg-warning');
            countdownElement.classList.add('bg-danger');
        } else {
            countdownElement.classList.remove('bg-danger');
            countdownElement.classList.add('bg-warning');
        }
    }

    // Atualizar texto da próxima rotação
    const dataTexto = proximaRotacao.toLocaleDateString('pt-BR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
    const dataElement = document.getElementById('proximaRotacao');
    if (dataElement) {
        dataElement.textContent = dataTexto;
    }
}

// Atualizar a cada hora
setInterval(atualizarCountdownRotacao, 3600000);
atualizarCountdownRotacao(); // Executar imediatamente
```

### Estimativa: 2 horas (implementação) + 1 hora (testes) = 3 horas

---

## Resumo do Epic 15

**Tempo Total Estimado:**
- Story 01 (Template): 4 horas
- Story 02 (DTOs): 5 horas
- Story 03 (Service): 8 horas
- Story 04 (Controller): 6 horas
- Story 05 (Rotação Manual): 6 horas
- Story 06 (Histórico): 4 horas
- Story 07 (Countdown): 3 horas

**Total: 36 horas (aprox. 4.5 dias úteis)**

**Arquivos a Criar:**
- `src/main/resources/templates/admin/chaves/list.html`
- `src/main/java/.../admin/api/responses/ChaveAtivaResponse.java`
- `src/main/java/.../admin/api/responses/RotacaoHistoricoResponse.java`
- `src/main/java/.../admin/api/service/AdminChaveService.java`
- `src/main/java/.../admin/api/controller/AdminChaveController.java`
- `src/main/resources/static/js/pages/chaves.js`

---

# Epic 16 - Stories Summary (Página de Visualização de Auditoria)

## Overview
Este documento contém o resumo de todas as histórias para o Epic 16 (Página de Visualização de Auditoria).

---

## Story 01: Template da Lista de Eventos de Auditoria

### Descrição
Criar template da página de auditoria com tabela de eventos, filtros avançados e modal de detalhes.

### Estimativa: 4 horas (implementação) + 1 hora (testes) = 5 horas

---

## Story 02: DTOs de Auditoria (Java Records)

### Records a Criar:

**EventoAuditoriaResponse.java**
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record EventoAuditoriaResponse(
    String id,
    TipoEventoAuditoria tipo,
    String descricaoTipo,
    String usuario,
    String realmNome,
    String detalhes,
    String ipOrigem,
    String userAgent,
    LocalDateTime timestamp,
    SeveridadeEvento severidade // CRITICO, WARNING, INFO, SUCCESS
) {
    public static EventoAuditoriaResponse from(EventoAuditoria evento, String realmNome) {
        return new EventoAuditoriaResponse(
            evento.getId().toString(),
            evento.getTipo(),
            getDescricaoTipo(evento.getTipo()),
            evento.getUsuario(),
            realmNome,
            evento.getDetalhes(),
            evento.getIpOrigem(),
            evento.getUserAgent(),
            evento.getTimestamp(),
            getSeveridade(evento.getTipo())
        );
    }

    private static String getDescricaoTipo(TipoEventoAuditoria tipo) {
        // Mapear tipo para descrição amigável
        return switch(tipo) {
            case LOGIN -> "Login";
            case LOGOUT -> "Logout";
            case FALHA_LOGIN -> "Falha de Login";
            case CRIACAO_USUARIO -> "Criação de Usuário";
            case EDICAO_USUARIO -> "Edição de Usuário";
            case REMOCAO_USUARIO -> "Remoção de Usuário";
            case ROTACAO_MANUAL -> "Rotação Manual";
            case ROTACAO_AUTOMATICA -> "Rotação Automática";
            default -> tipo.name();
        };
    }

    private static SeveridadeEvento getSeveridade(TipoEventoAuditoria tipo) {
        return switch(tipo) {
            case TENTATIVA_BRUTE_FORCE -> SeveridadeEvento.CRITICO;
            case ACESSO_NEGADO -> SeveridadeEvento.CRITICO;
            case FALHA_LOGIN -> SeveridadeEvento.WARNING;
            case CRIACAO_USUARIO, EDICAO_USUARIO, REMOCAO_USUARIO,
                 CRIACAO_REALM, EDICAO_REALM, DESATIVACAO_REALM -> SeveridadeEvento.INFO;
            case LOGIN, LOGOUT, ROTACAO_MANUAL, ROTACAO_AUTOMATICA -> SeveridadeEvento.SUCCESS;
            default -> SeveridadeEvento.INFO;
        };
    }
}

enum TipoEventoAuditoria {
    LOGIN, LOGOUT, FALHA_LOGIN, BLOQUEIO_USUARIO, DESBLOQUEIO_USUARIO,
    CRIACAO_USUARIO, EDICAO_USUARIO, REMOCAO_USUARIO, RESET_SENHA, RECUPERACAO_SENHA,
    CRIACAO_REALM, EDICAO_REALM, DESATIVACAO_REALM, ATIVACAO_REALM,
    CRIACAO_ROLE, EDICAO_ROLE, REMOCAO_ROLE,
    ROTACAO_MANUAL, ROTACAO_AUTOMATICA, GERACAO_CHAVE,
    TENTATIVA_BRUTE_FORCE, ACESSO_NEGADO, TOKEN_INVALIDO,
    BOOTSTRAP, ERRO_SISTEMA, CONFIGURACAO_ALTERADA
}

enum SeveridadeEvento {
    CRITICO, WARNING, INFO, SUCCESS
}
```

### Estimativa: 3 horas (implementação) + 2 horas (testes) = 5 horas

---

## Story 03: Backend Service Layer - AuditoriaService

### Descrição
Implementar service layer para consulta de eventos de auditoria com filtros avançados.

### Métodos a Implementar:
- `buscarEventos(page, size, realmId, tipoEvento, usuario, dataInicial, dataFinal, busca)`
- `buscarPorId(id)`
- `buscarParaExportar(realmId, tipoEvento, dataInicial, dataFinal)`

### Estimativa: 6 horas (implementação) + 3 horas (testes) = 9 horas

---

## Story 04: Controller API - AdminAuditoriaController

### Descrição
Implementar controller com endpoints para listagem, detalhes e exportação de eventos.

### Endpoints:
```
GET /admin/auditoria
GET /api/v1/auditoria/eventos
GET /api/v1/auditoria/eventos/{id}
GET /api/v1/auditoria/export
```

### Estimativa: 4 horas (implementação) + 2 horas (testes) = 6 horas

---

## Story 05: Filtros Avançados e Busca

### Descrição
Implementar filtros avançados para consulta de eventos de auditoria.

### Filtros:
- Realm (dropdown)
- Tipo de Evento (dropdown com optgroups)
- Período (data inicial e final - Flatpickr)
- Usuário (text input)
- Busca textual (timestamp, detalhes)

### JavaScript para Filtros:
```javascript
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
    if (busca) params.append('busca', busca);

    carregarEventos(`/api/v1/auditoria/eventos?${params.toString()}`);
}

function limparFiltros() {
    document.getElementById('realmFilter').value = 'All';
    document.getElementById('tipoEventoFilter').value = 'All';
    document.getElementById('dataInicial').value = '';
    document.getElementById('dataFinal').value = '';
    document.getElementById('usuarioFilter').value = '';
    document.querySelector('[data-table-search]').value = '';

    carregarEventos('/api/v1/auditoria/eventos');
}
```

### Estimativa: 4 horas (implementação) + 2 horas (testes) = 6 horas

---

## Story 06: Modal de Detalhes do Evento

### Descrição
Implementar modal para visualização detalhada de um evento de auditoria.

### Modal de Detalhes:
```html
<div class="modal fade" id="detalhesEventoModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="ti ti-file-text me-2"></i>
                    Detalhes do Evento de Auditoria
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="detalhesEventoContent">
                <!-- Conteúdo carregado via AJAX -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-light" data-bs-dismiss="modal">Fechar</button>
            </div>
        </div>
    </div>
</div>
```

### JavaScript para Detalhes:
```javascript
function visualizarDetalhes(eventoId) {
    fetch(`/api/v1/auditoria/eventos/${eventoId}`)
        .then(response => response.json())
        .then(evento => {
            renderizarDetalhes(evento);
            const modal = new bootstrap.Modal(document.getElementById('detalhesEventoModal'));
            modal.show();
        });
}

function renderizarDetalhes(evento) {
    const content = document.getElementById('detalhesEventoContent');

    const badgeClasse = getBadgeClasse(evento.severidade);

    content.innerHTML = `
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold">Tipo</label>
                <div><span class="badge ${badgeClasse} badge-label">
                    <i class="${getIconeTipo(evento.tipo)} me-1"></i>
                    ${evento.descricaoTipo}
                </span></div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Severidade</label>
                <div><span class="badge ${badgeClasse} badge-label">
                    ${evento.severidade}
                </span></div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Usuário</label>
                <div>${evento.usuario || '-'}</div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Realm</label>
                <div>${evento.realmNome || '-'}</div>
            </div>
            <div class="col-md-12">
                <label class="form-label fw-semibold">Detalhes</label>
                <div class="alert alert-light border">${evento.detalhes || '-'}</div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">IP Origem</label>
                <div>${evento.ipOrigem || '-'}</div>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">User Agent</label>
                <div class="text-muted fs-sm" style="word-break: break-all;">${evento.userAgent || '-'}</div>
            </div>
            <div class="col-md-12">
                <label class="form-label fw-semibold">Timestamp</label>
                <div>${formatarDataHora(evento.timestamp)}</div>
            </div>
        </div>
    `;
}

function getBadgeClasse(severidade) {
    switch(severidade) {
        case 'CRITICO': return 'bg-danger-subtle text-danger';
        case 'WARNING': return 'bg-warning-subtle text-warning';
        case 'INFO': return 'bg-info-subtle text-info';
        case 'SUCCESS': return 'bg-success-subtle text-success';
        default: return 'bg-secondary-subtle text-secondary';
    }
}

function getIconeTipo(tipo) {
    const icones = {
        'LOGIN': 'ti ti-login',
        'LOGOUT': 'ti ti-logout',
        'FALHA_LOGIN': 'ti ti-x-circle',
        'CRIACAO_USUARIO': 'ti ti-user-plus',
        'REMOCAO_USUARIO': 'ti ti-user-minus',
        'RESET_SENHA': 'ti ti-key',
        'CRIACAO_REALM': 'ti ti-building',
        'DESATIVACAO_REALM': 'ti ti-building-off',
        'ROTACAO_MANUAL': 'ti ti-rotate',
        'ROTACAO_AUTOMATICA': 'ti ti-rotate-clockwise',
        'TENTATIVA_BRUTE_FORCE': 'ti ti-shield-alert'
    };
    return icones[tipo] || 'ti ti-info-circle';
}
```

### Estimativa: 4 horas (implementação) + 2 horas (testes) = 6 horas

---

## Story 07: Coloração por Severidade e Ícones

### Descrição
Implementar coloração visual diferenciada por tipo de evento crítico.

### CSS para Badges de Severidade:
```css
.badge-critico {
    background-color: rgba(220, 53, 69, 0.1);
    color: #dc3545;
}

.badge-warning {
    background-color: rgba(255, 193, 7, 0.1);
    color: #ffc107;
}

.badge-info {
    background-color: rgba(13, 110, 253, 0.1);
    color: #0d6efd;
}

.badge-success {
    background-color: rgba(25, 135, 84, 0.1);
    color: #198754;
}
```

### Renderização de Tabela com Severidade:
```javascript
function renderizarEventos(eventos) {
    const tbody = document.getElementById('auditoriaTableBody');
    tbody.innerHTML = '';

    eventos.forEach(evento => {
        const badgeClasse = getBadgeClasse(evento.severidade);
        const badgeTexto = getBadgeTexto(evento.severidade);
        const iconeTipo = getIconeTipo(evento.tipo);

        const row = `
            <tr>
                <td>
                    <span class="badge ${badgeClasse} badge-label">
                        <i class="${iconeTipo} me-1"></i>
                        ${evento.descricaoTipo}
                    </span>
                </td>
                <td>${evento.usuario || '-'}</td>
                <td>${evento.realmNome || '-'}</td>
                <td class="text-truncate" style="max-width: 200px;" title="${evento.detalhes}">
                    ${evento.detalhes || '-'}
                </td>
                <td>${evento.ipOrigem || '-'}</td>
                <td>${formatarDataHora(evento.timestamp)}</td>
                <td>
                    <button class="btn btn-default btn-icon btn-sm rounded"
                            onclick="visualizarDetalhes('${evento.id}')"
                            title="Visualizar Detalhes">
                        <i class="ti ti-eye fs-lg"></i>
                    </button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}
```

### Estimativa: 2 horas (implementação) + 1 hora (testes) = 3 horas

---

## Story 08: Exportação CSV (Opcional)

### Descrição
Implementar funcionalidade opcional de exportação de eventos de auditoria em formato CSV.

### JavaScript para Exportação:
```javascript
function exportarCSV() {
    const params = new URLSearchParams();

    const realmId = document.getElementById('realmFilter').value;
    const tipoEvento = document.getElementById('tipoEventoFilter').value;
    const dataInicial = document.getElementById('dataInicial').value;
    const dataFinal = document.getElementById('dataFinal').value;

    if (realmId && realmId !== 'All') params.append('realmId', realmId);
    if (tipoEvento && tipoEvento !== 'All') params.append('tipoEvento', tipoEvento);
    if (dataInicial) params.append('dataInicial', dataInicial);
    if (dataFinal) params.append('dataFinal', dataFinal);

    const url = `/api/v1/auditoria/export?${params.toString()}`;

    // Mostrar loading
    mostrarLoading('Exportando eventos...');

    // Abrir URL em nova aba
    window.open(url, '_blank');

    // Remover loading após 2 segundos
    setTimeout(() => ocultarLoading(), 2000);
}
```

### Controller Endpoint para Exportação:
```java
@GetMapping("/api/v1/auditoria/export")
@ResponseBody
public void exportarCSV(
    HttpServletResponse response,
    @RequestParam(required = false) String realmId,
    @RequestParam(required = false) String tipoEvento,
    @RequestParam(required = false) String dataInicial,
    @RequestParam(required = false) String dataFinal
) {
    List<EventoAuditoriaResponse> eventos = auditoriaService.buscarParaExportar(
        realmId, tipoEvento, dataInicial, dataFinal
    );

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition",
        "attachment; filename=auditoria_" + LocalDate.now() + ".csv");

    try (PrintWriter writer = response.getWriter()) {
        // Header BOM para suporte ao Excel em português
        writer.write('\uFEFF');
        writer.write("Tipo,Usuário,Realm,Detalhes,IP Origem,Data\n");

        for (EventoAuditoriaResponse evento : eventos) {
            writer.append(String.format("%s,%s,%s,%s,%s,%s\n",
                evento.descricaoTipo(),
                evento.usuario(),
                evento.realmNome(),
                evento.detalhes(),
                evento.ipOrigem(),
                evento.timestamp()
            ));
        }
    } catch (Exception e) {
        log.error("Erro ao exportar CSV", e);
    }
}
```

### Estimativa: 3 horas (implementação) + 2 horas (testes) = 5 horas

---

## Resumo do Epic 16

**Tempo Total Estimado:**
- Story 01 (Template): 5 horas
- Story 02 (DTOs): 5 horas
- Story 03 (Service): 9 horas
- Story 04 (Controller): 6 horas
- Story 05 (Filtros): 6 horas
- Story 06 (Detalhes Modal): 6 horas
- Story 07 (Coloração): 3 horas
- Story 08 (Exportação): 5 horas

**Total: 45 horas (aprox. 5.6 dias úteis)**

**Arquivos a Criar:**
- `src/main/resources/templates/admin/auditoria/list.html`
- `src/main/java/.../admin/api/responses/EventoAuditoriaResponse.java`
- `src/main/java/.../admin/api/service/AdminAuditoriaService.java`
- `src/main/java/.../admin/api/controller/AdminAuditoriaController.java`
- `src/main/resources/static/js/pages/auditoria.js`

---

# Resumo Geral - Epics 14, 15 e 16

## Total de Histórias: 23
- **Epic 14**: 8 histórias (34 horas)
- **Epic 15**: 7 histórias (36 horas)
- **Epic 16**: 8 histórias (45 horas)

## Tempo Total Estimado: 115 horas (aprox. 14.4 dias úteis)

## Arquivos a Criar: ~35 arquivos entre templates, controllers, services, DTOs e JavaScript

## Prioridades:
- **Alta**: Todas as histórias (são essenciais para o dashboard administrativo)

## Próximos Passos:
1. Implementar histórias do Epic 14 (Gestão de Roles)
2. Implementar histórias do Epic 15 (Gestão de Chaves)
3. Implementar histórias do Epic 16 (Auditoria)
4. Executar testes de integração entre os epics
5. Executar testes E2E (End-to-End)
