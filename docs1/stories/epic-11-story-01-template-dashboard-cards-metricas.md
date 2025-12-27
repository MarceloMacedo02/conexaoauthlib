# User Story: Dashboard Principal - Estrutura do Template e Cards de Métricas

**Epic:** 11 - Dashboard Principal com Métricas (Thymeleaf)
**Story ID:** epic-11-story-01

## Status
**Estado:** Concluído ✅
**Data de Conclusão:** 2025-12-25

## Descrição
Criar o template base do dashboard administrativo (`src/main/resources/templates/admin/dashboard/index.html`) com a estrutura de cards de métricas, layout `layouts/vertical`, Bootstrap 5 e Tabler Icons, seguindo os padrões do arquivo `ui.txt` (API Keys template) e os requisitos do PRD para seção "Dashboard Administrativo".

## Status
**Status:** Done ✅
**Completion Date:** 2025-12-25

## Critérios de Aceite
- [x] Template `admin/dashboard/index.html` criado com estrutura básica
- [x] Layout `layouts/vertical` configurado (com sidebar)
- [x] Page Title fragment implementado com breadcrumb "Apps > Dashboard"
- [x] 4 cards de métricas principais implementados (Total Usuários, Total Realms, Chaves Ativas, Eventos Recentes)
- [x] Cards com ícones, números, tendências e descrições
- [x] Thymeleaf fragments para conteúdo dinâmico configurados
- [x] Classes Bootstrap 5 aplicadas corretamente
- [x] Página responsiva e acessível

## Métricas dos Cards
### Card 1: Total de Usuários
- Ícone: `ti ti-users` (cor primary)
- Número principal: total de usuários
- Tendência: usuários criados na última semana (flecha verde/vermelha)
- Descrição: "Usuários ativos no sistema"

### Card 2: Total de Realms
- Ícone: `ti ti-server` (cor info)
- Número principal: total de realms
- Tendência: realms ativos vs inativos
- Descrição: "Realms configurados"

### Card 3: Chaves Criptográficas
- Ícone: `ti ti-key` (cor warning)
- Número principal: chaves ativas
- Tendência: próxima rotação automática em X dias
- Descrição: "Chaves ativas por realm"

### Card 4: Eventos de Auditoria
- Ícone: `ti ti-shield` (cor danger)
- Número principal: eventos nas últimas 24h
- Tendência: eventos de segurança (vermelho se houver críticos)
- Descrição: "Eventos recentes"

## Tarefas
1. Criar diretório `templates/admin/dashboard/` se não existir
2. Criar template `index.html` com estrutura base
3. Implementar Page Title fragment com breadcrumb
4. Criar 4 cards de métricas com layout Bootstrap
5. Configurar Thymeleaf para passar dados dos cards
6. Adicionar ícones Tabler apropriados
7. Implementar responsividade mobile-first

## Instruções de Implementação

### Estrutura do Template
```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Dashboard')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title (Breadcrumb) -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Dashboard',
                 'Visão geral do sistema em tempo real'
                )}">
        </div>

        <!-- Cards de Métricas -->
        <div class="row">
            <!-- Card 1: Total Usuários -->
            <div class="col-xl-3 col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0 avatar-md bg-primary bg-opacity-10 text-primary rounded-2">
                                <i class="ti ti-users fs-24 avatar-title"></i>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <p class="text-muted mb-1">Total Usuários</p>
                                <h4 class="mb-0" th:text="${metrics.totalUsuarios}">1,245</h4>
                                <p class="text-success mb-0 mt-1">
                                    <i class="ti ti-arrow-up-right me-1"></i>
                                    <span th:text="${metrics.usuariosNovaSemana}">+12</span> esta semana
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Card 2: Total Realms -->
            <div class="col-xl-3 col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0 avatar-md bg-info bg-opacity-10 text-info rounded-2">
                                <i class="ti ti-server fs-24 avatar-title"></i>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <p class="text-muted mb-1">Total Realms</p>
                                <h4 class="mb-0" th:text="${metrics.totalRealms}">8</h4>
                                <p class="text-info mb-0 mt-1">
                                    <i class="ti ti-check me-1"></i>
                                    <span th:text="${metrics.realmsAtivos}">7</span> ativos
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Card 3: Chaves Ativas -->
            <div class="col-xl-3 col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0 avatar-md bg-warning bg-opacity-10 text-warning rounded-2">
                                <i class="ti ti-key fs-24 avatar-title"></i>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <p class="text-muted mb-1">Chaves Ativas</p>
                                <h4 class="mb-0" th:text="${metrics.chavesAtivas}">24</h4>
                                <p class="text-warning mb-0 mt-1">
                                    <i class="ti ti-clock me-1"></i>
                                    Rota em <span th:text="${metrics.diasProximaRotacao}">5</span> dias
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Card 4: Eventos Recentes -->
            <div class="col-xl-3 col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0 avatar-md bg-danger bg-opacity-10 text-danger rounded-2">
                                <i class="ti ti-shield fs-24 avatar-title"></i>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <p class="text-muted mb-1">Eventos 24h</p>
                                <h4 class="mb-0" th:text="${metrics.eventosUltimas24h}">156</h4>
                                <p class="text-danger mb-0 mt-1">
                                    <i class="ti ti-alert-triangle me-1"></i>
                                    <span th:text="${metrics.eventosCriticos}">3</span> críticos
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Conteúdo adicional será implementado nas próximas histórias -->

    </th:block>

    <th:block layout:fragment="javascripts">
        <!-- JavaScript para cards e funcionalidades básicas -->
    </th:block>
</body>
</html>
```

### Modelos de Dados (Backend DTO)
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record DashboardMetrics(
    Long totalUsuarios,
    Long usuariosAtivos,
    Long usuariosBloqueados,
    Long usuariosNovaSemana,

    Long totalRealms,
    Long realmsAtivos,

    Long chavesAtivas,
    Long diasProximaRotacao,

    Long eventosUltimas24h,
    Long eventosCriticos
);
```

### Controller Methods
```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import br.com.plataforma.conexaodigital.admin.domain.service.DashboardService;

@Controller
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // Carregar métricas principais
        DashboardMetrics metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("metrics", metrics);
        return "admin/dashboard/index";
    }
}
```

### Service Method
```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.DashboardMetrics;
import br.com.plataforma.conexaodigital.gestaousuarios.domain.UsuarioRepository;
import br.com.plataforma.conexaodigital.gestaorealm.domain.RealmRepository;
import br.com.plataforma.conexaodigital.gestaochaves.domain.ChaveCriptograficaRepository;
import br.com.plataforma.conexaodigital.auditoria.domain.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final RealmRepository realmRepository;
    private final ChaveCriptograficaRepository chaveRepository;
    private final EventoAuditoriaRepository eventoRepository;

    // Constructor com todos os repositories

    public DashboardMetrics getDashboardMetrics() {
        LocalDateTime umaSemanaAtras = LocalDateTime.now().minusDays(7);
        LocalDateTime ultimas24h = LocalDateTime.now().minusHours(24);
        LocalDateTime proximaRotacao = calculateProximaRotacao();

        Long usuariosNovaSemana = usuarioRepository.countByDataCriacaoAfter(umaSemanaAtras);
        Long eventosUltimas24h = eventoRepository.countByDataCriacaoAfter(ultimas24h);
        Long eventosCriticos = eventoRepository.countByDataCriacaoAfterAndTipoIsCritical(ultimas24h);
        Long diasProximaRotacao = java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), proximaRotacao);

        return new DashboardMetrics(
            usuarioRepository.count(),
            usuarioRepository.countByAtivoTrue(),
            usuarioRepository.countByBloqueadoTrue(),
            usuariosNovaSemana,

            realmRepository.count(),
            realmRepository.countByAtivoTrue(),

            chaveRepository.countByAtivaTrue(),
            diasProximaRotacao,

            eventosUltimas24h,
            eventosCriticos
        );
    }

    private LocalDateTime calculateProximaRotacao() {
        // Retorna dia 1 do próximo mês
        return LocalDateTime.now().plusMonths(1)
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
    }
}
```

## Checklist de Validação
- [x] Template `admin/dashboard/index.html` criado
- [x] Layout `layouts/vertical` configurado
- [x] 4 cards de métricas implementados
- [x] Thymeleaf variables `${metrics.*}` renderizam dados
- [x] Ícones Tabler carregados corretamente
- [x] Cards responsivos em dispositivos móveis
- [x] Cores dos cards seguem design system (primary, info, warning, danger)
- [x] Service method `getDashboardMetrics()` implementado
- [x] Controller method `dashboard()` retorna view com dados

## Anotações
- Seguir exatamente o padrão do template `ui.txt` (API Keys section)
- Utilizar layout `layouts/vertical` (com sidebar) - diferente da página de login que usa `layouts/base`
- Cards devem usar classes `avatar-md bg-*-bg-opacity-10 text-* rounded-2` para ícones
- Tendências com flechas (`ti ti-arrow-up-right`, `ti ti-arrow-down-right`)
- Dados mockados podem ser usados inicialmente (serão substituídos por dados reais do service)
- Esta história cria apenas a estrutura de cards; gráficos e tabelas serão nas histórias seguintes

## Dependências
- Epic 1 (Gestão de Realms) - para `RealmRepository`
- Epic 2 (Gestão de Usuários) - para `UsuarioRepository`
- Epic 5 (Gestão de Chaves) - para `ChaveCriptograficaRepository`
- Epic 7 (Auditoria) - para `EventoAuditoriaRepository`
- Epic 9 (Configuração) - para configuração de Thymeleaf

## Prioridade
**Alta** - Estrutura base do dashboard principal

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas

---

## Dev Agent Record

### Agent Model Used
Claude Sonnet 4.0 (claude-sonnet-4-20250514)

### Completion Notes
- Template `admin/dashboard/index.html` criado com estrutura completa
- Layout `layouts/vertical` configurado com sidebar
- Page Title fragment implementado com breadcrumb "Apps > Dashboard"
- 4 cards de métricas implementados com ícones Tabler, valores e tendências
- Thymeleaf variables configuradas para renderização dinâmica
- Classes Bootstrap 5 aplicadas corretamente
- Responsividade implementada com Bootstrap grid
- DTO `DashboardMetrics` criado para encapsular dados dos cards
- `DashboardService.getDashboardMetrics()` implementado
- `AdminDashboardController.dashboard()` configurado para renderizar view com dados

### Change Log
- Criado `src/main/resources/templates/admin/dashboard/index.html`
- Criado `src/main/java/.../admin/api/responses/DashboardMetrics.java`
- Criado `src/main/java/.../admin/domain/service/DashboardService.java`
- Criado `src/main/java/.../admin/api/controller/AdminDashboardController.java`

### File List
- `src/main/resources/templates/admin/dashboard/index.html` - Template do dashboard com cards de métricas
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/DashboardMetrics.java` - DTO com métricas dos cards
- `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/DashboardService.java` - Service com método getDashboardMetrics()
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminDashboardController.java` - Controller para renderizar dashboard

### Debug Log References
Nenhuma ocorrência de bugs ou problemas durante implementação.

---

