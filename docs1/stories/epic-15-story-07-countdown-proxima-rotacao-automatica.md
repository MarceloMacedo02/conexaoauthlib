# User Story: Countdown para Próxima Rotação Automática

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-07

## Descrição
Implementar countdown visual mostrando dias restantes até a próxima rotação automática de chaves (dia 1 do próximo mês), com atualização automática a cada hora e alertas visuais quando faltar menos de 7 dias.

## Critérios de Aceite
- [ ] Countdown de dias restantes exibido
- [ ] Próxima data de rotação exibida em formato legível
- [ ] Atualização automática a cada hora
- [ ] Badge de aviso quando faltar < 7 dias (cor amarela)
- [ ] Badge de urgência quando faltar ≤ 3 dias (cor vermelha)
- [ ] Badge normal quando > 7 dias (cor verde/azul)
- [ ] Alerta visual com ícone de alerta quando próximo
- [ ] Ícone de calendário exibido
- [ ] Cálculo correto de dias (dia 1 do próximo mês)
- [ ] Formatação em português (pt-BR)
- [ ] Estado de loading durante cálculo
- [ ] Erro tratado se falhar ao buscar data

## Tarefas
1. Implementar JavaScript para calcular próxima rotação
2. Implementar função de countdown
3. Implementar atualização automática (setInterval)
4. Adicionar lógica de mudança de cor por dias restantes
5. Implementar formatação de data em português
6. Adicionar ícones visuais
7. Implementar loading state
8. Implementar tratamento de erros
9. Testar atualização automática
10. Testar transição de cores (7 dias, 3 dias)

## Instruções de Implementação

### JavaScript para Countdown
**Adicionar a `src/main/resources/static/js/pages/chaves.js`:**

```javascript
/**
 * Intervalo para atualização do countdown (1 hora em ms)
 */
const COUNTDOWN_UPDATE_INTERVAL = 3600000;

/**
 * Carrega informações da próxima rotação automática.
 */
function carregarProximaRotacao() {
    mostrarLoading('Calculando próxima rotação...');

    fetch('/admin/chaves/api/proxima-rotacao')
        .then(response => response.json())
        .then(data => {
            ocultarLoading();
            atualizarDisplayRotacao(data);
        })
        .catch(error => {
            ocultarLoading();
            console.error('Erro ao carregar próxima rotação:', error);

            // Exibir erro no display
            const countdownElement = document.getElementById('countdownRotacao');
            const proximaRotacaoElement = document.getElementById('proximaRotacao');

            if (countdownElement) {
                countdownElement.textContent = 'Erro';
                countdownElement.className = 'badge bg-danger';
            }

            if (proximaRotacaoElement) {
                proximaRotacaoElement.textContent = 'Erro ao carregar';
            }
        });
}

/**
 * Atualiza o display de countdown e próxima rotação.
 */
function atualizarDisplayRotacao(data) {
    const diasRestantes = data.diasRestantes;
    const dataFormatada = data.proximaRotacao;

    // Atualizar texto da próxima rotação
    const proximaRotacaoElement = document.getElementById('proximaRotacao');
    if (proximaRotacaoElement) {
        proximaRotacaoElement.textContent = dataFormatada;
    }

    // Atualizar badge de countdown
    atualizarBadgeCountdown(diasRestantes);

    // Verificar se deve exibir alerta
    if (diasRestantes <= 7) {
        exibirAlertaProximaRotacao(diasRestantes);
    }
}

/**
 * Atualiza badge de countdown com cor apropriada.
 */
function atualizarBadgeCountdown(diasRestantes) {
    const countdownElement = document.getElementById('countdownRotacao');

    if (!countdownElement) return;

    // Atualizar texto
    countdownElement.textContent = diasRestantes + ' dias';

    // Remover classes anteriores
    countdownElement.classList.remove('bg-success', 'bg-warning', 'bg-danger', 'bg-info');

    // Adicionar classe baseada em dias restantes
    if (diasRestantes <= 3) {
        // Urgente: vermelho
        countdownElement.classList.add('bg-danger');
    } else if (diasRestantes <= 7) {
        // Aviso: amarelo
        countdownElement.classList.add('bg-warning');
    } else if (diasRestantes > 15) {
        // Muito tempo: azul
        countdownElement.classList.add('bg-info');
    } else {
        // Normal: verde
        countdownElement.classList.add('bg-success');
    }
}

/**
 * Exibe alerta visual quando próximo da rotação.
 */
function exibirAlertaProximaRotacao(diasRestantes) {
    const alertContainer = document.getElementById('alertaProximaRotacao');

    if (!alertContainer) {
        // Criar container de alerta se não existir
        const container = document.createElement('div');
        container.id = 'alertaProximaRotacao';
        container.className = 'alert alert-warning alert-dismissible fade show mt-3';
        container.setAttribute('role', 'alert');

        let mensagem = '';
        if (diasRestantes <= 3) {
            mensagem = '<i class="ti ti-alert-triangle me-2"></i>' +
                      '<strong>Atenção crítica:</strong> Rotação automática em ' + diasRestantes + ' dias! ' +
                      'Considere rotacionar manualmente se necessário.';
        } else {
            mensagem = '<i class="ti ti-info-circle me-2"></i>' +
                      '<strong>Atenção:</strong> Rotação automática em ' + diasRestantes + ' dias.';
        }

        container.innerHTML = `
            ${mensagem}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        // Inserir após o alerta de próxima rotação
        const alertaRotacao = document.querySelector('.alert-info');
        if (alertaRotacao && alertaRotacao.parentNode) {
            alertaRotacao.parentNode.insertBefore(container, alertaRotacao.nextSibling);
        }
    }
}

/**
 * Inicia atualização automática do countdown.
 */
function iniciarAtualizacaoAutomatica() {
    // Calcular imediatamente
    carregarProximaRotacao();

    // Atualizar a cada hora
    setInterval(carregarProximaRotacao, COUNTDOWN_UPDATE_INTERVAL);

    console.log('Atualização automática de countdown iniciada (intervalo: 1 hora)');
}

/**
 * Para atualização automática (opcional).
 */
function pararAtualizacaoAutomatica() {
    clearInterval(countdownInterval);
    console.log('Atualização automática de countdown parada');
}

/**
 * Calcula manualmente dias restantes (fallback).
 */
function calcularDiasRestantesManual() {
    const agora = new Date();
    const proximaRotacao = new Date();

    // Definir para dia 1 do próximo mês
    proximaRotacao.setMonth(proximaRotacao.getMonth() + 1);
    proximaRotacao.setDate(1);
    proximaRotacao.setHours(0, 0, 0, 0);

    // Calcular diferença em dias
    const diff = proximaRotacao - agora;
    const dias = Math.ceil(diff / (1000 * 60 * 60 * 24));

    return dias;
}

/**
 * Formata data da próxima rotação para exibição.
 */
function formatarProximaRotacao(data) {
    if (!data) return '-';

    const dataObj = new Date(data);

    return dataObj.toLocaleDateString('pt-BR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    }) + ' às 00:00';
}
```

### Atualizar Template
**No template `admin/chaves/list.html`, na tab de Chaves Ativas:**

```html
<!-- Filtros e Alertas -->
<div class="row mb-3">
    <div class="col-md-6">
        <label class="form-label">Selecionar Realm</label>
        <select id="realmFilter" class="form-select">
            <option value="All">Todos os Realms</option>
            <option value="master">Master Realm</option>
            <option value="empresa-a">Empresa A</option>
            <option value="empresa-b">Empresa B</option>
        </select>
    </div>
    <div class="col-md-6">
        <label class="form-label">Próxima Rotação Automática</label>
        <div class="alert alert-info mb-0">
            <i class="ti ti-calendar me-2"></i>
            <span id="proximaRotacao">Carregando...</span>
            <span class="badge ms-2" id="countdownRotacao">
                -- dias
            </span>
        </div>
    </div>
</div>

<!-- Container para alertas dinâmicos -->
<div id="alertContainer"></div>
```

### Inicializar Countdown
**Adicionar ao final do arquivo `chaves.js`:**

```javascript
/**
 * Inicializa countdown quando o DOM estiver pronto.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Iniciar atualização automática
    iniciarAtualizacaoAutomatica();

    // Carregar chaves e histórico
    const realmFilter = document.getElementById('realmFilter');
    if (realmFilter) {
        carregarChavesPorFiltro(realmFilter.value);
        carregarHistoricoPorFiltro(realmFilter.value);
    }

    // Evento de mudança de filtro
    if (realmFilter) {
        realmFilter.addEventListener('change', function() {
            carregarChavesPorFiltro(this.value);
            carregarHistoricoPorFiltro(this.value);
            carregarProximaRotacao(); // Recalcular countdown
        });
    }
});
```

### Funções Auxiliares de Loading
**Adicionar a `chaves.js`:**

```javascript
/**
 * Mostra loading state.
 */
function mostrarLoading(mensagem = 'Carregando...') {
    // Verificar se já existe loading overlay
    let loadingOverlay = document.getElementById('loadingOverlay');

    if (!loadingOverlay) {
        // Criar overlay
        loadingOverlay = document.createElement('div');
        loadingOverlay.id = 'loadingOverlay';
        loadingOverlay.className = 'position-fixed top-0 start-0 w-100 h-100 ' +
                               'd-flex justify-content-center align-items-center ' +
                               'bg-dark bg-opacity-25';
        loadingOverlay.style.zIndex = '9999';
        loadingOverlay.innerHTML = `
            <div class="card p-4 shadow">
                <div class="d-flex align-items-center gap-3">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
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
- [ ] Countdown exibindo dias restantes
- [ ] Próxima rotação exibida em formato legível
- [ ] Atualização automática funcionando (a cada hora)
- [ ] Badge verde quando > 7 dias
- [ ] Badge amarelo quando ≤ 7 dias e > 3 dias
- [ ] Badge vermelho quando ≤ 3 dias
- [ ] Alerta exibido quando ≤ 7 dias
- [ ] Alerta de atenção crítica exibido quando ≤ 3 dias
- [ ] Ícone de calendário exibido
- [ ] Cálculo correto de dias (dia 1 do próximo mês)
- [ ] Formatação em português (pt-BR)
- [ ] Loading state funcionando
- [ ] Erro tratado se falhar ao buscar data
- [ ] Atualização ao mudar filtro de realm
- [ ] Atualização após nova rotação manual
- [ ] Responsividade mantida
- [ ] Acessibilidade (ARIA labels, etc.)

## Anotações
- Countdown atualiza automaticamente a cada hora (3600000ms)
- Cores mudam dinamicamente baseado em dias restantes
- Alerta exibido automaticamente quando próximo da rotação
- Data formatada em português brasileiro (ex: "1 de janeiro de 2026")
- Próxima rotação sempre dia 1 do próximo mês às 00:00
- Loading overlay impede interação durante carregamento
- Alertas podem ser fechados com botão X
- Recálculo ao mudar filtro de realm
- Integração com rotação manual (atualiza após sucesso)

## Dependências
- Story 01 (Template com Tabs) - template base
- Story 03 (Service Layer) - cálculo de próxima rotação
- Story 04 (Controller API) - endpoint de próxima rotação
- Story 05 (Rotação Manual) - atualização após rotação
- Bootstrap 5 (já incluído no projeto)
- Tabler Icons (já incluído no projeto)

## Prioridade
**Média** - Melhoria importante para gestão proativa de chaves

## Estimativa
- Implementação: 2 horas
- Testes: 1 hora
- Total: 3 horas
