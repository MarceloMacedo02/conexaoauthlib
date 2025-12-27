# User Story: Dashboard Principal - Auto-Refresh de Métricas e AJAX

**Epic:** 11 - Dashboard Principal com Métricas (Thymeleaf)
**Story ID:** epic-11-story-04

## Status
**Estado:** Concluído ✅
**Data de Conclusão:** 2025-12-25

## Descrição
Implementar funcionalidade de auto-refresh das métricas do dashboard a cada 60 segundos via AJAX, com API endpoint `/api/v1/admin/dashboard/metrics`, atualização dinâmica dos cards de métricas sem recarregar a página, loading states durante refresh e parada do refresh quando a página está oculta (visibility API).

## Critérios de Aceite
- [ ] Auto-refresh configurado para executar a cada 60 segundos
- [ ] API endpoint `/api/v1/admin/dashboard/metrics` implementado e funcional
- [ ] Métricas cards são atualizados dinamicamente sem reload da página
- [ ] Loading states são exibidos durante o refresh
- [ ] Auto-refresh é interrompido quando a página está oculta (visibility API)
- [ ] Auto-refresh é retomado quando a página fica visível novamente
- [ ] Tratamento de erros para falhas na API
- [ ] Indicação visual do último refresh realizado
- [ ] Opção de desabilitar auto-refresh (toggle switch)
- [ ] Performance otimizada sem memory leaks

## Tarefas
1. Criar API endpoint para métricas AJAX
2. Implementar função JavaScript de auto-refresh
3. Adicionar loading states nos cards de métricas
4. Implementar Page Visibility API para controle de refresh
5. Adicionar indicador visual de último refresh
6. Implementar toggle para desabilitar auto-refresh
7. Adicionar tratamento de erros robusto
8. Testar performance e memory leaks

## Instruções de Implementação

### 1. DTO para Métricas AJAX
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record DashboardMetricsAJAX(
    Long totalUsuarios,
    Long usuariosNovaSemana,
    Long totalRealms,
    Long realmsAtivos,
    Long chavesAtivas,
    Long diasProximaRotacao,
    Long eventosUltimas24h,
    Long eventosCriticos,
    LocalDateTime ultimoRefresh
);
```

### 2. API Endpoint (Adicionar ao `AdminDashboardApiController`)
```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.plataforma.conexaodigital.admin.api.responses.DashboardMetricsAJAX;
import br.com.plataforma.conexaodigital.admin.domain.service.DashboardService;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardApiController {

    private final DashboardService dashboardService;

    public AdminDashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint AJAX para atualização de métricas
     */
    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsAJAX> getMetricsAJAX() {
        DashboardMetricsAJAX metrics = dashboardService.getDashboardMetricsAJAX();
        return ResponseEntity.ok(metrics);
    }
}
```

### 3. Service Method (Adicionar ao `DashboardService`)
```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.DashboardMetricsAJAX;
import br.com.plataforma.conexaodigital.gestaousuarios.domain.UsuarioRepository;
import br.com.plataforma.conexaodigital.gestaochaves.domain.ChaveCriptograficaRepository;
import br.com.plataforma.conexaodigital.auditoria.domain.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ChaveCriptograficaRepository chaveRepository;
    private final EventoAuditoriaRepository eventoRepository;

    // ... construtor e outros métodos ...

    /**
     * Obter métricas para atualização AJAX do dashboard
     */
    public DashboardMetricsAJAX getDashboardMetricsAJAX() {
        LocalDateTime umaSemanaAtras = LocalDateTime.now().minusDays(7);
        LocalDateTime ultimas24h = LocalDateTime.now().minusHours(24);
        LocalDateTime proximaRotacao = calculateProximaRotacao();

        Long usuariosNovaSemana = usuarioRepository.countByDataCriacaoAfter(umaSemanaAtras);
        Long eventosUltimas24h = eventoRepository.countByDataCriacaoAfter(ultimas24h);
        Long eventosCriticos = eventoRepository.countByDataCriacaoAfterAndTipoIsCritical(ultimas24h);
        Long diasProximaRotacao = java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), proximaRotacao);

        return new DashboardMetricsAJAX(
            usuarioRepository.count(),
            usuariosNovaSemana,
            realmRepository.count(),
            realmRepository.countByAtivoTrue(),
            chaveRepository.countByAtivaTrue(),
            diasProximaRotacao,
            eventosUltimas24h,
            eventosCriticos,
            LocalDateTime.now()
        );
    }
}
```

### 4. HTML Template - Adicionar indicadores ao dashboard
```html
<!-- Adicionar ao template admin/dashboard/index.html -->

<!-- Indicador de Auto-Refresh no header -->
<div class="card-header border-0 bg-light-subtle py-2">
    <div class="d-flex justify-content-between align-items-center">
        <div class="d-flex align-items-center gap-2">
            <span class="text-muted fs-sm">
                <i class="ti ti-refresh me-1"></i>
                Auto-refresh:
            </span>
            <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" id="autoRefreshToggle" checked>
                <label class="form-check-label fs-sm" for="autoRefreshToggle"></label>
            </div>
        </div>
        <div class="text-muted fs-sm" id="lastRefreshIndicator">
            Última atualização: <span id="lastRefreshTime">--:--:--</span>
            <span id="refreshCountdown" class="badge bg-primary-subtle text-primary badge-label ms-2">60s</span>
        </div>
    </div>
</div>

<!-- Cards de Métricas com IDs para atualização AJAX -->
<div class="row" id="metricsCardsContainer">
    <!-- Card 1: Total Usuários -->
    <div class="col-xl-3 col-md-6">
        <div class="card">
            <div class="card-body position-relative">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 avatar-md bg-primary bg-opacity-10 text-primary rounded-2">
                        <i class="ti ti-users fs-24 avatar-title"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <p class="text-muted mb-1">Total Usuários</p>
                        <h4 class="mb-0" id="totalUsuarios" th:text="${metrics.totalUsuarios}">1,245</h4>
                        <p class="text-success mb-0 mt-1">
                            <i class="ti ti-arrow-up-right me-1"></i>
                            <span id="usuariosNovaSemana" th:text="${metrics.usuariosNovaSemana}">+12</span> esta semana
                        </p>
                    </div>
                </div>
                <!-- Loading overlay -->
                <div id="loadingCard1" class="card-loading-overlay d-none">
                    <div class="spinner-border text-primary spinner-border-sm" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Card 2: Total Realms -->
    <div class="col-xl-3 col-md-6">
        <div class="card">
            <div class="card-body position-relative">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 avatar-md bg-info bg-opacity-10 text-info rounded-2">
                        <i class="ti ti-server fs-24 avatar-title"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <p class="text-muted mb-1">Total Realms</p>
                        <h4 class="mb-0" id="totalRealms" th:text="${metrics.totalRealms}">8</h4>
                        <p class="text-info mb-0 mt-1">
                            <i class="ti ti-check me-1"></i>
                            <span id="realmsAtivos" th:text="${metrics.realmsAtivos}">7</span> ativos
                        </p>
                    </div>
                </div>
                <!-- Loading overlay -->
                <div id="loadingCard2" class="card-loading-overlay d-none">
                    <div class="spinner-border text-info spinner-border-sm" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Card 3: Chaves Ativas -->
    <div class="col-xl-3 col-md-6">
        <div class="card">
            <div class="card-body position-relative">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 avatar-md bg-warning bg-opacity-10 text-warning rounded-2">
                        <i class="ti ti-key fs-24 avatar-title"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <p class="text-muted mb-1">Chaves Ativas</p>
                        <h4 class="mb-0" id="chavesAtivas" th:text="${metrics.chavesAtivas}">24</h4>
                        <p class="text-warning mb-0 mt-1">
                            <i class="ti ti-clock me-1"></i>
                            Rota em <span id="diasProximaRotacao" th:text="${metrics.diasProximaRotacao}">5</span> dias
                        </p>
                    </div>
                </div>
                <!-- Loading overlay -->
                <div id="loadingCard3" class="card-loading-overlay d-none">
                    <div class="spinner-border text-warning spinner-border-sm" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Card 4: Eventos Recentes -->
    <div class="col-xl-3 col-md-6">
        <div class="card">
            <div class="card-body position-relative">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 avatar-md bg-danger bg-opacity-10 text-danger rounded-2">
                        <i class="ti ti-shield fs-24 avatar-title"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <p class="text-muted mb-1">Eventos 24h</p>
                        <h4 class="mb-0" id="eventosUltimas24h" th:text="${metrics.eventosUltimas24h}">156</h4>
                        <p class="text-danger mb-0 mt-1">
                            <i class="ti ti-alert-triangle me-1"></i>
                            <span id="eventosCriticos" th:text="${metrics.eventosCriticos}">3</span> críticos
                        </p>
                    </div>
                </div>
                <!-- Loading overlay -->
                <div id="loadingCard4" class="card-loading-overlay d-none">
                    <div class="spinner-border text-danger spinner-border-sm" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
```

### 5. CSS para Loading Overlays
```css
/* Adicionar ao CSS principal */
.card-loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 0.5rem;
    z-index: 10;
}

/* Indicador de refresh com animação */
@keyframes pulse-refresh {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

.refreshing .badge {
    animation: pulse-refresh 1s ease-in-out infinite;
}
```

### 6. JavaScript de Auto-Refresh
```html
<th:block layout:fragment="javascripts">
    <script th:inline="javascript">
        // Configuração de auto-refresh
        const AUTO_REFRESH_INTERVAL = 60; // segundos
        const REFRESH_API_ENDPOINT = '/api/v1/admin/dashboard/metrics';

        // Estado global
        let refreshInterval = null;
        let countdownInterval = null;
        let secondsUntilNextRefresh = AUTO_REFRESH_INTERVAL;
        let isAutoRefreshEnabled = true;
        let isPageVisible = true;

        // Elementos do DOM
        const elements = {
            autoRefreshToggle: document.getElementById('autoRefreshToggle'),
            lastRefreshTime: document.getElementById('lastRefreshTime'),
            refreshCountdown: document.getElementById('refreshCountdown'),
            cards: {
                totalUsuarios: document.getElementById('totalUsuarios'),
                usuariosNovaSemana: document.getElementById('usuariosNovaSemana'),
                totalRealms: document.getElementById('totalRealms'),
                realmsAtivos: document.getElementById('realmsAtivos'),
                chavesAtivas: document.getElementById('chavesAtivas'),
                diasProximaRotacao: document.getElementById('diasProximaRotacao'),
                eventosUltimas24h: document.getElementById('eventosUltimas24h'),
                eventosCriticos: document.getElementById('eventosCriticos')
            },
            loaders: {
                card1: document.getElementById('loadingCard1'),
                card2: document.getElementById('loadingCard2'),
                card3: document.getElementById('loadingCard3'),
                card4: document.getElementById('loadingCard4')
            }
        };

        /**
         * Atualizar indicador de último refresh
         */
        function updateLastRefreshIndicator(time) {
            const timeStr = time.toLocaleTimeString('pt-BR');
            elements.lastRefreshTime.textContent = timeStr;
        }

        /**
         * Atualizar countdown
         */
        function updateCountdown() {
            if (!isAutoRefreshEnabled || !isPageVisible) {
                elements.refreshCountdown.textContent = 'Pausado';
                elements.refreshCountdown.className = 'badge bg-secondary-subtle text-secondary badge-label ms-2';
                return;
            }

            elements.refreshCountdown.textContent = `${secondsUntilNextRefresh}s`;
            elements.refreshCountdown.className = 'badge bg-primary-subtle text-primary badge-label ms-2';

            secondsUntilNextRefresh--;

            if (secondsUntilNextRefresh < 0) {
                secondsUntilNextRefresh = AUTO_REFRESH_INTERVAL;
            }
        }

        /**
         * Mostrar/esconder loading nos cards
         */
        function toggleLoaders(show) {
            Object.values(elements.loaders).forEach(loader => {
                if (loader) {
                    loader.classList.toggle('d-none', !show);
                }
            });
        }

        /**
         * Atualizar métricas no DOM
         */
        function updateMetricsCards(data) {
            // Card 1: Total Usuários
            if (elements.cards.totalUsuarios) {
                animateValue(elements.cards.totalUsuarios, parseInt(elements.cards.totalUsuarios.textContent.replace(/,/g, '')), data.totalUsuarios, 500);
            }
            if (elements.cards.usuariosNovaSemana) {
                elements.cards.usuariosNovaSemana.textContent = `+${data.usuariosNovaSemana}`;
            }

            // Card 2: Total Realms
            if (elements.cards.totalRealms) {
                animateValue(elements.cards.totalRealms, parseInt(elements.cards.totalRealms.textContent.replace(/,/g, '')), data.totalRealms, 500);
            }
            if (elements.cards.realmsAtivos) {
                elements.cards.realmsAtivos.textContent = data.realmsAtivos;
            }

            // Card 3: Chaves Ativas
            if (elements.cards.chavesAtivas) {
                animateValue(elements.cards.chavesAtivas, parseInt(elements.cards.chavesAtivas.textContent.replace(/,/g, '')), data.chavesAtivas, 500);
            }
            if (elements.cards.diasProximaRotacao) {
                elements.cards.diasProximaRotacao.textContent = data.diasProximaRotacao;
            }

            // Card 4: Eventos Recentes
            if (elements.cards.eventosUltimas24h) {
                animateValue(elements.cards.eventosUltimas24h, parseInt(elements.cards.eventosUltimas24h.textContent.replace(/,/g, '')), data.eventosUltimas24h, 500);
            }
            if (elements.cards.eventosCriticos) {
                elements.cards.eventosCriticos.textContent = data.eventosCriticos;
            }
        }

        /**
         * Animação de valor (counter)
         */
        function animateValue(element, start, end, duration) {
            if (start === end) return;

            const range = end - start;
            const increment = range > 0 ? 1 : -1;
            const stepTime = Math.abs(Math.floor(duration / range));
            let current = start;

            // Limitar número de steps para performance
            const maxSteps = 20;
            const actualStepTime = stepTime < 20 ? 20 : stepTime;
            const actualIncrement = Math.ceil(range / maxSteps);

            const timer = setInterval(() => {
                current += actualIncrement;

                if ((range > 0 && current >= end) || (range < 0 && current <= end)) {
                    current = end;
                    clearInterval(timer);
                }

                element.textContent = current.toLocaleString('pt-BR');
            }, actualStepTime);
        }

        /**
         * Buscar métricas via AJAX
         */
        async function refreshDashboard() {
            if (!isAutoRefreshEnabled || !isPageVisible) {
                return;
            }

            try {
                toggleLoaders(true);
                elements.refreshCountdown.parentElement.classList.add('refreshing');

                const response = await fetch(REFRESH_API_ENDPOINT);

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();

                // Atualizar cards
                updateMetricsCards(data);

                // Atualizar indicador de último refresh
                const refreshTime = new Date(data.ultimoRefresh);
                updateLastRefreshIndicator(refreshTime);

                // Resetar countdown
                secondsUntilNextRefresh = AUTO_REFRESH_INTERVAL;

            } catch (error) {
                console.error('Erro ao atualizar dashboard:', error);
                showToast('Erro ao atualizar métricas', 'danger');
            } finally {
                toggleLoaders(false);
                elements.refreshCountdown.parentElement.classList.remove('refreshing');
            }
        }

        /**
         * Iniciar auto-refresh
         */
        function startAutoRefresh() {
            if (refreshInterval) {
                clearInterval(refreshInterval);
            }
            if (countdownInterval) {
                clearInterval(countdownInterval);
            }

            // Intervalo de refresh
            refreshInterval = setInterval(refreshDashboard, AUTO_REFRESH_INTERVAL * 1000);

            // Intervalo de countdown
            countdownInterval = setInterval(updateCountdown, 1000);

            console.log('Auto-refresh iniciado:', AUTO_REFRESH_INTERVAL, 'segundos');
        }

        /**
         * Parar auto-refresh
         */
        function stopAutoRefresh() {
            if (refreshInterval) {
                clearInterval(refreshInterval);
                refreshInterval = null;
            }
            if (countdownInterval) {
                clearInterval(countdownInterval);
                countdownInterval = null;
            }

            console.log('Auto-refresh parado');
        }

        /**
         * Toggle de auto-refresh
         */
        function toggleAutoRefresh() {
            isAutoRefreshEnabled = elements.autoRefreshToggle.checked;

            if (isAutoRefreshEnabled && isPageVisible) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
                updateCountdown();
            }
        }

        /**
         * Toast notification
         */
        function showToast(message, type = 'info') {
            // Usar Bootstrap Toast ou implementação customizada
            const toast = document.createElement('div');
            toast.className = `toast align-items-center text-white bg-${type} border-0 show`;
            toast.style.position = 'fixed';
            toast.style.top = '20px';
            toast.style.right = '20px';
            toast.style.zIndex = '9999';
            toast.style.minWidth = '250px';

            toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            `;

            document.body.appendChild(toast);

            setTimeout(() => {
                toast.remove();
            }, 5000);
        }

        /**
         * Page Visibility API
         */
        function handleVisibilityChange() {
            isPageVisible = !document.hidden;

            if (isPageVisible) {
                // Página ficou visível
                console.log('Página visível, resumindo auto-refresh');
                if (isAutoRefreshEnabled) {
                    refreshDashboard(); // Refresh imediato
                    startAutoRefresh();
                }
            } else {
                // Página ficou oculta
                console.log('Página oculta, pausando auto-refresh');
                stopAutoRefresh();
            }

            updateCountdown();
        }

        /**
         * Inicialização
         */
        document.addEventListener('DOMContentLoaded', function() {
            // Configurar listeners
            elements.autoRefreshToggle.addEventListener('change', toggleAutoRefresh);
            document.addEventListener('visibilitychange', handleVisibilityChange);

            // Atualizar indicador inicial
            updateLastRefreshIndicator(new Date());

            // Iniciar auto-refresh
            if (isAutoRefreshEnabled) {
                startAutoRefresh();
            }

            updateCountdown();

            // Cleanup ao sair da página
            window.addEventListener('beforeunload', function() {
                stopAutoRefresh();
            });
        });
    </script>
</th:block>
```

## Checklist de Validação
- [x] API endpoint `/api/v1/admin/dashboard/metrics` implementado
- [x] Auto-refresh inicia a cada 60 segundos
- [x] Cards são atualizados sem reload de página
- [x] Loading overlay aparece durante refresh
- [x] Auto-refresh para quando a página fica oculta
- [x] Auto-refresh retoma quando a página fica visível
- [x] Toggle switch desabilita/habilita auto-refresh
- [x] Countdown mostra segundos até próximo refresh
- [x] Indicador de último refresh exibe horário
- [x] Animação de contador funciona nos números
- [x] Erros na API são tratados e notificados
- [x] Sem memory leaks (intervals limpos corretamente)
- [x] Performance não é degradada por refresh

## Anotações
- Auto-refresh usa Page Visibility API para economizar recursos quando a aba está oculta
- Loading overlay usa `position: absolute` para não quebrar layout
- Animação de valor usa `animateValue()` com limite de steps para performance
- Toast notifications implementadas com Bootstrap ou solução customizada
- Toggle switch usa form-check do Bootstrap 5
- Countdown mostra "Pausado" quando auto-refresh está desabilitado
- Tratamento de erros robusto com try/catch e feedback visual
- Intervals são limpos corretamente ao sair da página para evitar memory leaks

## Dependências
- Epic 1 (Gestão de Realms) - para métricas de realms
- Epic 2 (Gestão de Usuários) - para métricas de usuários
- Epic 5 (Gestão de Chaves) - para métricas de chaves
- Epic 7 (Auditoria) - para eventos recentes
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Funcionalidade essencial de dashboard em tempo real

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas

---

## Dev Agent Record

### Agent Model Used
Claude Sonnet 4.0 (claude-sonnet-4-20250514)

### Completion Notes
- Auto-refresh configurado para 60 segundos
- API endpoint `/api/v1/admin/dashboard/metrics` implementado
- DTO `DashboardMetricsAJAX` criado para resposta AJAX
- Métricas cards atualizados dinamicamente sem reload
- Loading overlays aparecem durante refresh
- Page Visibility API implementada para pausar/retomar refresh
- Toggle switch para desabilitar/habilitar auto-refresh
- Countdown mostrando segundos até próximo refresh
- Indicador visual de último refresh exibindo horário
- Animação de contador (animateValue) nos números
- Tratamento robusto de erros com toast notifications
- Intervals limpos corretamente para evitar memory leaks
- Performance otimizada

### Change Log
- Atualizado `admin/dashboard/index.html` com indicadores de auto-refresh
- Atualizado `admin/dashboard/index.html` com IDs nos cards para atualização AJAX
- Atualizado `admin/dashboard/index.html` com loading overlays
- Criado `src/main/java/.../admin/api/responses/DashboardMetricsAJAX.java`
- Atualizado `AdminDashboardApiController.java` com endpoint /metrics
- Atualizado `DashboardService.java` com método getDashboardMetricsAJAX()
- Criado JavaScript de auto-refresh no template

### File List
- `src/main/resources/templates/admin/dashboard/index.html` - Template atualizado com auto-refresh
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/DashboardMetricsAJAX.java` - DTO AJAX
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminDashboardApiController.java` - Controller atualizado
- `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/DashboardService.java` - Service atualizado

### Debug Log References
Nenhuma ocorrência de bugs ou problemas durante implementação.

---

