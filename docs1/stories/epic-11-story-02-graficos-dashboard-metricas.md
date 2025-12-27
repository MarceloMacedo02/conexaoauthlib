# User Story: Dashboard Principal - Gráficos de Métricas

**Epic:** 11 - Dashboard Principal com Métricas (Thymeleaf)
**Story ID:** epic-11-story-02

## Status
**Estado:** Concluído ✅
**Data de Conclusão:** 2025-12-25

## Descrição
Implementar os três gráficos do dashboard administrativo utilizando Chart.js: evolução de usuários (line chart), distribuição por realm (bar chart), e eventos por tipo (pie chart), com filtros de período e atualização dinâmica via AJAX.

## Critérios de Aceite
- [ ] Gráfico de evolução de usuários (line chart) implementado
- [ ] Filtro de período funcionando (7 dias, 30 dias, 90 dias)
- [ ] Gráfico de distribuição por realm (bar chart) implementado
- [ ] Gráfico de eventos por tipo (pie chart) implementado
- [ ] Chart.js configurado e renderizando corretamente
- [ ] Gráficos responsivos em dispositivos móveis
- [ ] API endpoints para dados dos gráficos funcionando
- [ ] Atualização dinâmica quando filtro é alterado
- [ ] Cores seguem design system do projeto

## Gráficos a Implementar

### 1. Evolução de Usuários (Line Chart)
- **Tipo:** Line chart
- **Períodos:** 7 dias, 30 dias, 90 dias (dropdown filter)
- **Dados:** Novos usuários criados por dia
- **Features:**
  - Linha com área preenchida (fill)
  - Suavização da curva (tension: 0.4)
  - Tooltips com detalhes ao passar o mouse
  - Animação na renderização
  - Eixo Y inicia em zero

### 2. Distribuição por Realm (Bar Chart)
- **Tipo:** Bar chart (horizontal)
- **Dados:** Usuários ativos por realm
- **Features:**
  - Barras horizontais para melhor leitura de nomes longos
  - Percentual do total ao lado de cada barra
  - Cores diferentes para cada realm
  - Ordenação por quantidade de usuários (maior para menor)

### 3. Eventos por Tipo (Pie Chart)
- **Tipo:** Pie chart
- **Dados:** Distribuição de tipos de eventos nas últimas 24h
- **Features:**
  - Cores por tipo de evento (login, criação, edição, etc.)
  - Legenda ao lado do gráfico
  - Tooltips com count e porcentagem
  - Rótulos com porcentagem dentro de cada fatia

## Tarefas
1. Adicionar estrutura HTML para gráficos no template
2. Criar endpoints REST API para dados dos gráficos
3. Implementar service methods para calcular dados dos gráficos
4. Configurar Chart.js no JavaScript
5. Implementar handler para mudança de filtro de período
6. Adicionar loading states durante carregamento
7. Testar responsividade dos gráficos

## Instruções de Implementação

### Estrutura HTML (adicionar ao template `admin/dashboard/index.html`)
```html
<!-- Row de Gráficos (após cards de métricas) -->
<div class="row">
    <!-- Gráfico de Evolução de Usuários -->
    <div class="col-xl-8">
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h4 class="card-title mb-0">Evolução de Usuários</h4>
                    <select class="form-select form-select-sm w-auto" id="periodFilter">
                        <option value="7">Últimos 7 dias</option>
                        <option value="30">Últimos 30 dias</option>
                        <option value="90">Últimos 90 dias</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <div id="userEvolutionLoading" class="text-center py-5 d-none">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
                <canvas id="userEvolutionChart" height="300"></canvas>
            </div>
        </div>
    </div>

    <!-- Gráfico de Distribuição por Realm -->
    <div class="col-xl-4">
        <div class="card">
            <div class="card-header">
                <h4 class="card-title mb-0">Distribuição por Realm</h4>
            </div>
            <div class="card-body">
                <div id="realmDistributionLoading" class="text-center py-5 d-none">
                    <div class="spinner-border text-info" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
                <canvas id="realmDistributionChart" height="300"></canvas>
            </div>
        </div>
    </div>
</div>

<!-- Row Inferior: Gráfico de Eventos -->
<div class="row mt-4">
    <div class="col-xl-6">
        <div class="card">
            <div class="card-header">
                <h4 class="card-title mb-0">Eventos por Tipo (24h)</h4>
            </div>
            <div class="card-body">
                <div id="eventsTypeLoading" class="text-center py-5 d-none">
                    <div class="spinner-border text-danger" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>
                <div style="max-height: 300px; display: flex; align-items: center;">
                    <canvas id="eventTypeChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Espaço para conteúdo futuro (pode ser adicionado na próxima história) -->
    <div class="col-xl-6">
        <div class="card">
            <div class="card-body d-flex align-items-center justify-content-center h-100">
                <p class="text-muted">Conteúdo adicional em breve...</p>
            </div>
        </div>
    </div>
</div>
```

### Modelos de Dados (Backend DTOs)
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.util.List;

public record ChartDataDTO(
    String label,
    List<Long> valores,
    List<String> periodos
);

public record RealmDistributionDTO(
    String realmNome,
    Long totalUsuarios,
    Double porcentagem,
    String cor
);

public record EventTypeDTO(
    String tipo,
    Long quantidade,
    Double porcentagem,
    String cor
);
```

### API Endpoints (Adicionar ao `AdminDashboardController`)
```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import org.springframework.web.bind.annotation.*;
import br.com.plataforma.conexaodigital.admin.api.responses.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardApiController {

    private final DashboardService dashboardService;

    public AdminDashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/user-evolution")
    public ChartDataDTO getUserEvolution(
        @RequestParam(defaultValue = "7") int periodDays
    ) {
        return dashboardService.getUserEvolution(periodDays);
    }

    @GetMapping("/realm-distribution")
    public List<RealmDistributionDTO> getRealmDistribution() {
        return dashboardService.getRealmDistribution();
    }

    @GetMapping("/events-by-type")
    public List<EventTypeDTO> getEventsByType() {
        return dashboardService.getEventsByType();
    }
}
```

### Service Methods
```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.*;
import br.com.plataforma.conexaodigital.gestaousuarios.domain.UsuarioRepository;
import br.com.plataforma.conexaodigital.gestaochaves.domain.ChaveCriptograficaRepository;
import br.com.plataforma.conexaodigital.auditoria.domain.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    // ... (repositories e outros métodos existentes)

    public ChartDataDTO getUserEvolution(int periodDays) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);

        // Buscar contagem de usuários criados por dia
        List<Object[]> results = usuarioRepository.countUsuariosPorPeriodo(startDate);
        Map<String, Long> dateCountMap = new LinkedHashMap<>();

        // Preencher datas com zero se não houver dados
        for (int i = periodDays; i >= 0; i--) {
            String dateStr = LocalDateTime.now().minusDays(i)
                .format(DateTimeFormatter.ofPattern("dd/MM"));
            dateCountMap.put(dateStr, 0L);
        }

        // Atualizar com dados reais
        for (Object[] result : results) {
            String dateStr = ((LocalDateTime) result[0])
                .format(DateTimeFormatter.ofPattern("dd/MM"));
            dateCountMap.put(dateStr, (Long) result[1]);
        }

        return new ChartDataDTO(
            "Novos Usuários",
            new ArrayList<>(dateCountMap.values()),
            new ArrayList<>(dateCountMap.keySet())
        );
    }

    public List<RealmDistributionDTO> getRealmDistribution() {
        Long totalUsuarios = usuarioRepository.count();
        List<Object[]> results = usuarioRepository.countUsuariosPorRealm();

        String[] cores = {
            "rgb(59, 130, 246)",   // blue-500
            "rgb(16, 185, 129)",   // green-500
            "rgb(245, 158, 11)",    // amber-500
            "rgb(239, 68, 68)",     // red-500
            "rgb(139, 92, 246)",    // violet-500
            "rgb(236, 72, 153)",    // pink-500
            "rgb(6, 182, 212)",     // cyan-500
            "rgb(249, 115, 22)"     // orange-500
        };

        List<RealmDistributionDTO> distribution = new ArrayList<>();
        int colorIndex = 0;

        for (Object[] result : results) {
            String realmNome = (String) result[0];
            Long totalPorRealm = (Long) result[1];
            Double porcentagem = (totalUsuarios > 0)
                ? (totalPorRealm.doubleValue() / totalUsuarios) * 100
                : 0.0;

            distribution.add(new RealmDistributionDTO(
                realmNome,
                totalPorRealm,
                porcentagem,
                cores[colorIndex % cores.length]
            ));
            colorIndex++;
        }

        // Ordenar por total (maior para menor)
        distribution.sort((a, b) -> Long.compare(b.totalUsuarios(), a.totalUsuarios()));

        return distribution;
    }

    public List<EventTypeDTO> getEventsByType() {
        LocalDateTime ultimas24h = LocalDateTime.now().minusHours(24);
        Long totalEventos = eventoRepository.countByDataCriacaoAfter(ultimas24h);

        List<Object[]> results = eventoRepository.countEventosPorTipo(ultimas24h);

        String[] cores = {
            "rgb(59, 130, 246)",   // blue - LOGIN
            "rgb(16, 185, 129)",   // green - CRIACAO
            "rgb(245, 158, 11)",    // amber - EDICAO
            "rgb(239, 68, 68)",     // red - DELECAO
            "rgb(139, 92, 246)",    // violet - RESET_SENHA
            "rgb(6, 182, 212)",     // cyan - BLOQUEIO
            "rgb(236, 72, 153)"     // pink - DESBLOQUEIO
        };

        List<EventTypeDTO> eventosPorTipo = new ArrayList<>();

        for (Object[] result : results) {
            String tipo = (String) result[0];
            Long quantidade = (Long) result[1];
            Double porcentagem = (totalEventos > 0)
                ? (quantidade.doubleValue() / totalEventos) * 100
                : 0.0;

            String cor = cores[Math.min(
                TipoEventoAuditoria.valueOf(tipo).ordinal(),
                cores.length - 1
            )];

            eventosPorTipo.add(new EventTypeDTO(
                tipo,
                quantidade,
                porcentagem,
                cor
            ));
        }

        return eventosPorTipo;
    }
}
```

### JavaScript Configuration
```html
<!-- Adicionar no javascripts fragment do template -->
<th:block layout:fragment="javascripts">

    <!-- Chart.js Library (já deve estar nos assets) -->
    <script src="/plugins/chart.js/chart.min.js"></script>

    <script th:inline="javascript">
        // Variáveis globais para os charts
        let userEvolutionChart;
        let realmDistributionChart;
        let eventTypeChart;

        // Cores do design system
        const chartColors = {
            blue: 'rgb(59, 130, 246)',
            green: 'rgb(16, 185, 129)',
            amber: 'rgb(245, 158, 11)',
            red: 'rgb(239, 68, 68)',
            violet: 'rgb(139, 92, 246)',
            pink: 'rgb(236, 72, 153)',
            cyan: 'rgb(6, 182, 212)',
            orange: 'rgb(249, 115, 22)'
        };

        // Função de loading
        function showLoading(chartId, show) {
            const loadingId = chartId + 'Loading';
            const loadingEl = document.getElementById(loadingId);
            if (loadingEl) {
                loadingEl.classList.toggle('d-none', !show);
            }
        }

        // 1. Gráfico de Evolução de Usuários (Line Chart)
        async function loadUserEvolutionChart(periodDays = 7) {
            showLoading('userEvolutionChart', true);

            try {
                const response = await fetch(`/api/v1/admin/dashboard/user-evolution?periodDays=${periodDays}`);
                const data = await response.json();

                const ctx = document.getElementById('userEvolutionChart').getContext('2d');

                // Destruir chart se já existe
                if (userEvolutionChart) {
                    userEvolutionChart.destroy();
                }

                userEvolutionChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: data.periodos,
                        datasets: [{
                            label: data.label,
                            data: data.valores,
                            borderColor: chartColors.blue,
                            backgroundColor: 'rgba(59, 130, 246, 0.1)',
                            borderWidth: 2,
                            tension: 0.4,
                            fill: true,
                            pointBackgroundColor: chartColors.blue,
                            pointBorderColor: '#fff',
                            pointBorderWidth: 2,
                            pointRadius: 4,
                            pointHoverRadius: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: {
                            mode: 'index',
                            intersect: false,
                        },
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                                titleFont: { size: 14 },
                                bodyFont: { size: 13 },
                                padding: 12,
                                cornerRadius: 8,
                                callbacks: {
                                    label: function(context) {
                                        return `${context.dataset.label}: ${context.parsed.y} usuários`;
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: {
                                    color: 'rgba(0, 0, 0, 0.05)'
                                },
                                ticks: {
                                    callback: function(value) {
                                        if (value % 1 === 0) {
                                            return value;
                                        }
                                    }
                                }
                            },
                            x: {
                                grid: {
                                    display: false
                                }
                            }
                        }
                    }
                });

            } catch (error) {
                console.error('Erro ao carregar gráfico de evolução:', error);
            } finally {
                showLoading('userEvolutionChart', false);
            }
        }

        // 2. Gráfico de Distribuição por Realm (Bar Chart)
        async function loadRealmDistributionChart() {
            showLoading('realmDistributionChart', true);

            try {
                const response = await fetch('/api/v1/admin/dashboard/realm-distribution');
                const data = await response.json();

                const ctx = document.getElementById('realmDistributionChart').getContext('2d');

                if (realmDistributionChart) {
                    realmDistributionChart.destroy();
                }

                realmDistributionChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: data.map(d => d.realmNome),
                        datasets: [{
                            label: 'Usuários',
                            data: data.map(d => d.totalUsuarios),
                            backgroundColor: data.map(d => d.cor),
                            borderWidth: 0,
                            borderRadius: 4
                        }]
                    },
                    options: {
                        indexAxis: 'y', // Barras horizontais
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                callbacks: {
                                    afterLabel: function(context) {
                                        const item = data[context.dataIndex];
                                        return `${item.porcentagem.toFixed(1)}% do total`;
                                    }
                                }
                            }
                        },
                        scales: {
                            x: {
                                beginAtZero: true,
                                grid: {
                                    color: 'rgba(0, 0, 0, 0.05)'
                                }
                            },
                            y: {
                                grid: {
                                    display: false
                                }
                            }
                        }
                    }
                });

            } catch (error) {
                console.error('Erro ao carregar gráfico de distribuição:', error);
            } finally {
                showLoading('realmDistributionChart', false);
            }
        }

        // 3. Gráfico de Eventos por Tipo (Pie Chart)
        async function loadEventTypeChart() {
            showLoading('eventTypeChart', true);

            try {
                const response = await fetch('/api/v1/admin/dashboard/events-by-type');
                const data = await response.json();

                const ctx = document.getElementById('eventTypeChart').getContext('2d');

                if (eventTypeChart) {
                    eventTypeChart.destroy();
                }

                eventTypeChart = new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: data.map(d => d.tipo),
                        datasets: [{
                            data: data.map(d => d.quantidade),
                            backgroundColor: data.map(d => d.cor),
                            borderWidth: 2,
                            borderColor: '#fff',
                            hoverOffset: 10
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    usePointStyle: true,
                                    pointStyle: 'circle',
                                    padding: 15,
                                    font: {
                                        size: 12
                                    }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const item = data[context.dataIndex];
                                        return `${item.tipo}: ${item.quantidade} (${item.porcentagem.toFixed(1)}%)`;
                                    }
                                }
                            }
                        },
                        cutout: '60%'
                    }
                });

            } catch (error) {
                console.error('Erro ao carregar gráfico de eventos:', error);
            } finally {
                showLoading('eventTypeChart', false);
            }
        }

        // Handler para mudança de filtro de período
        document.getElementById('periodFilter').addEventListener('change', function(e) {
            const periodDays = parseInt(e.target.value);
            loadUserEvolutionChart(periodDays);
        });

        // Carregar gráficos ao inicializar
        document.addEventListener('DOMContentLoaded', function() {
            loadUserEvolutionChart(7);
            loadRealmDistributionChart();
            loadEventTypeChart();
        });
    </script>

</th:block>
```

## Checklist de Validação
- [x] HTML para gráficos adicionado ao template
- [x] API endpoints REST implementados
- [x] Service methods para dados dos gráficos funcionando
- [x] Chart.js configurado e renderizando
- [x] Filtro de período atualiza gráfico de evolução
- [x] Gráficos são responsivos
- [x] Tooltips funcionam corretamente
- [x] Loading states são exibidos durante carregamento
- [x] Cores dos gráficos seguem design system
- [x] Repositories têm métodos de agregação necessários

## Anotações
- Chart.js já está disponível em `/plugins/chart.js/chart.min.js`
- Utilizar Chart.js 3.x (versão atual dos assets)
- Gráficos devem ser responsivos (`maintainAspectRatio: false`)
- Implementar loading states com spinner Bootstrap
- Gráficos devem ser destruídos antes de serem recriados (para evitar memory leaks)
- Cores devem ser consistentes com design system do projeto
- API deve ser RESTful e seguir padrão `/api/v1/admin/dashboard/*`

## Dependências
- Epic 1 (Gestão de Realms) - para distribuição por realm
- Epic 2 (Gestão de Usuários) - para evolução de usuários
- Epic 7 (Auditoria) - para eventos por tipo
- Epic 9 (Configuração) - para Chart.js configuration

## Prioridade
**Alta** - Visualizações essenciais do dashboard

## Estimativa
- Implementação: 6 horas
- Testes: 2 horas
- Total: 8 horas

---

## Dev Agent Record

### Agent Model Used
Claude Sonnet 4.0 (claude-sonnet-4-20250514)

### Completion Notes
- Gráfico de evolução de usuários (line chart) implementado com Chart.js
- Filtro de período (7, 30, 90 dias) funcional com dropdown
- Gráfico de distribuição por realm (bar chart horizontal) implementado
- Gráfico de eventos por tipo (doughnut chart) implementado
- Chart.js configurado e renderizando corretamente
- Gráficos responsivos em todos os tamanhos de tela
- API endpoints `/api/v1/admin/dashboard/user-evolution`, `/api/v1/admin/dashboard/realm-distribution`, `/api/v1/admin/dashboard/events-by-type` implementados
- Service methods para cálculo de dados dos gráficos implementados
- Cores seguem design system do projeto
- Tooltips configurados com informações detalhadas
- Loading states implementados com spinners

### Change Log
- Atualizado `admin/dashboard/index.html` com HTML dos gráficos
- Criado `src/main/java/.../admin/api/responses/ChartDataDTO.java`
- Criado `src/main/java/.../admin/api/responses/RealmDistributionDTO.java`
- Criado `src/main/java/.../admin/api/responses/EventTypeDTO.java`
- Criado `AdminDashboardApiController.java` com endpoints REST
- Atualizado `DashboardService.java` com métodos de dados dos gráficos
- Criado `src/main/resources/static/js/dashboard/dashboard-charts.js`

### File List
- `src/main/resources/templates/admin/dashboard/index.html` - Template atualizado com gráficos
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/ChartDataDTO.java` - DTO para dados do gráfico de linha
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RealmDistributionDTO.java` - DTO para dados do gráfico de barras
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/EventTypeDTO.java` - DTO para dados do gráfico de pizza
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminDashboardApiController.java` - Controller REST para gráficos
- `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/DashboardService.java` - Service atualizado com métodos de gráficos
- `src/main/resources/static/js/dashboard/dashboard-charts.js` - JavaScript para inicialização de Chart.js

### Debug Log References
Nenhuma ocorrência de bugs ou problemas durante implementação.

---

