# Epic 11: Dashboard Principal com Métricas (Thymeleaf)

## Descrição
Implementar página principal do dashboard administrativo exibindo métricas em tempo real sobre o sistema (usuários, realms, chaves, eventos de auditoria), com cards de resumo e visualizações gráficas, utilizando Thymeleaf como template engine e Bootstrap 5 para estilização.

## Objetivos
- Fornecer visão geral do status do sistema
- Exibir métricas críticas em cards destacados
- Permitir navegação rápida para seções detalhadas
- Atualizar dados dinamicamente via AJAX
- Apresentar visualizações gráficas de tendências

## Critérios de Aceite
- [ ] Cards com métricas principais (total usuários, total realms, chaves ativas, eventos recentes)
- [ ] Gráfico de evolução de usuários por período (7 dias, 30 dias)
- [ ] Gráfico de distribuição de usuários por realm
- [ ] Lista de eventos de auditoria mais recentes
- [ ] Atualização automática dos dados a cada 60 segundos
- [ ] Links rápidos para seções de gestão
- [ ] Design responsivo e acessível
- [ ] Filtros por período para gráficos

## Requisitos Funcionais
- Exibir métricas agregadas do sistema
- Mostrar eventos de auditoria recentes (últimos 10)
- Visualizar tendência de criação de usuários
- Distribuição de usuários por realm
- Status das chaves criptográficas
- Alertas para eventos críticos recentes
- Navegação rápida para páginas detalhadas

## Métricas a Exibir

### Cards de Resumo
1. **Total de Usuários**
   - Usuários ativos por realm
   - Usuários criados na última semana
   - Usuários bloqueados

2. **Total de Realms**
   - Realms ativos
   - Realms desativados
   - Realm Master (indicador visual)

3. **Status de Chaves Criptográficas**
   - Chaves ativas
   - Próxima rotação automática (data)
   - Chaves próximas de expiração (alerta se < 7 dias)

4. **Eventos de Auditoria Recentes**
   - Total de eventos nas últimas 24h
   - Eventos de segurança (login, falhas, etc.)
   - Último evento crítico

### Visualizações Gráficas
1. **Evolução de Usuários**
   - Gráfico de linha (line chart)
   - Período: 7 dias, 30 dias, 90 dias (toggle)
   - Novos usuários por dia

2. **Distribuição por Realm**
   - Gráfico de barras (bar chart)
   - Usuários ativos por realm
   - Percentual do total

3. **Eventos por Tipo**
   - Gráfico de pizza (pie chart)
   - Distribuição de tipos de eventos de auditoria
   - Últimas 24h ou 7 dias

## Requisitos Técnicos
- **Template:** `src/main/resources/templates/admin/dashboard/index.html`
- **Layout:** `layouts/vertical` (com sidebar)
- **Endpoint:** `GET /admin/dashboard` (dados)
- **Endpoint:** `GET /api/v1/admin/dashboard/metrics` (API AJAX)
- **Charts:** Chart.js (já disponível na aplicação)
- **Auto-refresh:** JavaScript `setInterval()` a cada 60s
- **CSS Framework:** Bootstrap 5 (já disponível)
- **Icons:** Tabler Icons (ti-*)

## Componentes de UI

### Estrutura Base (baseado em ui.txt - API Keys template)
```html
<div class="row">
    <!-- Cards de Métricas -->
    <div class="col-xl-3 col-md-6">
        <div class="card">
            <div class="card-body">
                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 avatar-md bg-primary bg-opacity-10 text-primary rounded-2">
                        <i class="ti ti-users fs-24 avatar-title"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <p class="text-muted mb-1">Total Usuários</p>
                        <h4 class="mb-0">1,245</h4>
                        <p class="text-success mb-0 mt-1">
                            <i class="ti ti-arrow-up-right me-1"></i>
                            +12 esta semana
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Mais cards... -->
</div>

<!-- Gráficos -->
<div class="row">
    <div class="col-xl-8">
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h4 class="card-title mb-0">Evolução de Usuários</h4>
                    <select class="form-select form-select-sm" id="periodFilter">
                        <option value="7">Últimos 7 dias</option>
                        <option value="30">Últimos 30 dias</option>
                        <option value="90">Últimos 90 dias</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <canvas id="userEvolutionChart" height="300"></canvas>
            </div>
        </div>
    </div>
    <div class="col-xl-4">
        <div class="card">
            <div class="card-header">
                <h4 class="card-title mb-0">Distribuição por Realm</h4>
            </div>
            <div class="card-body">
                <canvas id="realmDistributionChart" height="300"></canvas>
            </div>
        </div>
    </div>
</div>

<!-- Eventos Recentes -->
<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h4 class="card-title mb-0">Eventos de Auditoria Recentes</h4>
                    <a th:href="@{/admin/auditoria}" class="btn btn-sm btn-primary">
                        Ver Todos <i class="ti ti-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-centered table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Tipo</th>
                                <th>Usuário</th>
                                <th>Realm</th>
                                <th>Data</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Dados populados via Thymeleaf -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
```

### Modelos de Dados
```java
public record DashboardMetrics(
    long totalUsuarios,
    long usuariosAtivos,
    long usuariosBloqueados,
    long usuariosNovaSemana,
    long totalRealms,
    long realmsAtivos,
    long chavesAtivas,
    LocalDateTime proximaRotacao,
    long eventosUltimas24h,
    List<EventoAuditoriaRecenteDTO> eventosRecentes
);

public record ChartDataDTO(
    String label,
    List<Long> valores,
    List<String> periodos
);

public record RealmDistributionDTO(
    String realmNome,
    Long totalUsuarios,
    Double porcentagem
);
```

## API Endpoints

### Controller
```java
@GetMapping("/admin/dashboard")
public String dashboard(Model model) {
    // Carregar dados iniciais
    DashboardMetrics metrics = dashboardService.getMetrics();
    model.addAttribute("metrics", metrics);
    return "admin/dashboard/index";
}

@GetMapping("/api/v1/admin/dashboard/metrics")
@ResponseBody
public DashboardMetrics getMetrics() {
    return dashboardService.getMetrics();
}

@GetMapping("/api/v1/admin/dashboard/user-evolution")
@ResponseBody
public ChartDataDTO getUserEvolution(
    @RequestParam(defaultValue = "7") int periodDays
) {
    return dashboardService.getUserEvolution(periodDays);
}

@GetMapping("/api/v1/admin/dashboard/realm-distribution")
@ResponseBody
public List<RealmDistributionDTO> getRealmDistribution() {
    return dashboardService.getRealmDistribution();
}
```

## JavaScript

### Auto-refresh
```javascript
let refreshInterval;
let isAutoRefreshEnabled = true;

function refreshDashboard() {
    if (!isAutoRefreshEnabled) return;

    fetch('/api/v1/admin/dashboard/metrics')
        .then(response => response.json())
        .then(data => {
            updateMetricsCards(data);
            updateCharts(data);
        });
}

// Auto-refresh a cada 60 segundos
refreshInterval = setInterval(refreshDashboard, 60000);

// Parar refresh se usuário sair da página
document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
        isAutoRefreshEnabled = false;
    } else {
        isAutoRefreshEnabled = true;
        refreshDashboard();
    }
});
```

### Chart.js Configuration
```javascript
// Gráfico de Evolução de Usuários
const userEvolutionCtx = document.getElementById('userEvolutionChart').getContext('2d');
const userEvolutionChart = new Chart(userEvolutionCtx, {
    type: 'line',
    data: {
        labels: [], // Carregado via API
        datasets: [{
            label: 'Novos Usuários',
            data: [],
            borderColor: 'rgb(59, 130, 246)',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            tension: 0.4,
            fill: true
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false }
        },
        scales: {
            y: { beginAtZero: true }
        }
    }
});
```

## Integrações
- Epic 1 (Gestão de Realms) - para métricas de realms
- Epic 2 (Gestão de Usuários) - para métricas de usuários
- Epic 5 (Gestão de Chaves) - para métricas de chaves
- Epic 7 (Auditoria) - para eventos recentes
- Epic 4 (OAuth 2.0) - para métricas de autenticação

## Arquivos a Criar/Modificar
```
src/main/resources/templates/admin/dashboard/
└── index.html

src/main/java/br/com/plataforma/conexaodigital/admin/
├── api/
│   ├── controller/
│   │   └── DashboardController.java
│   └── responses/
│       ├── DashboardMetrics.java
│       ├── ChartDataDTO.java
│       ├── RealmDistributionDTO.java
│       └── EventoAuditoriaRecenteDTO.java
└── domain/
    └── service/
        └── DashboardService.java
```

## Testes
### Testes de Aceitação
- [ ] Dashboard carrega com métricas corretas
- [ ] Gráficos são exibidos corretamente
- [ ] Auto-refresh atualiza dados após 60s
- [ ] Filtro de período funciona nos gráficos
- [ ] Eventos recentes são listados corretamente
- [ ] Links para páginas detalhadas funcionam
- [ ] Página é responsiva em dispositivos móveis
- [ ] Dados são atualizados via AJAX sem reload

### Testes de UI
- [ ] Cards estão alinhados e com cores apropriadas
- [ ] Gráficos são responsivos
- [ ] Ícones são visíveis e significativos
- [ ] Loading states são exibidos durante refresh
- [ ] Cores seguem design system

## Performance
- Tempo de carregamento inicial < 2 segundos
- API de métricas responde em < 500ms
- Gráficos renderizam em < 1 segundo
- Auto-refresh não degrada performance da página

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 2 (Gestão de Usuários) - para dados de usuários
- Epic 5 (Gestão de Chaves) - para dados de chaves
- Epic 7 (Auditoria) - para eventos recentes
- Epic 9 (Configuração) - para configuração de base

## Prioridade
**Alta** - Página principal de operação do sistema

## Estimativa
- Implementação: 8 horas
- Testes: 4 horas
- Total: 12 horas

## Notas
- Utilizar layout `layouts/vertical` (com sidebar)
- Chart.js já está disponível nos assets da aplicação
- Considerar cache de dados no frontend para evitar chamadas excessivas
- Implementar loading states durante refresh
- Fornecer opção de desabilitar auto-refresh
- Gráficos devem ser responsivos e acessíveis
- Considerar WebSocket para atualizações em tempo real (futuro)
