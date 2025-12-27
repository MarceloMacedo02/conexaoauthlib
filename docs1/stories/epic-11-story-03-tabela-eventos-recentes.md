# User Story: Dashboard Principal - Tabela de Eventos Recentes

**Epic:** 11 - Dashboard Principal com Métricas (Thymeleaf)
**Story ID:** epic-11-story-03

## Status
**Estado:** Concluído ✅
**Data de Conclusão:** 2025-12-25

## Descrição
Implementar a tabela de eventos de auditoria mais recentes (últimos 10 eventos) no dashboard principal, com colunas de tipo, usuário, realm, detalhes, data e status, usando o padrão de tabelas do ui.txt.

## Critérios de Aceite
- [ ] Tabela de eventos recentes implementada
- [ ] Exibe os últimos 10 eventos de auditoria
- [ ] Colunas: Tipo, Usuário, Realm, Detalhes, Data, Status
- [ ] Badges de cor por tipo de evento
- [ ] Badges de cor por status (SUCCESS, FAILED, WARNING)
- [ ] Link "Ver Todos" para página completa de auditoria
- [ ] Tabela responsiva em dispositivos móveis
- [ ] Dados populados via Thymeleaf do service

## Tarefas
1. Criar método no `DashboardService` para buscar eventos recentes
2. Criar DTO `EventoAuditoriaRecenteDTO`
3. Adicionar tabela ao template do dashboard
4. Implementar estilos de badges para tipos e status
5. Adicionar link para página completa de auditoria
6. Configurar responsividade da tabela

## Instruções de Implementação

### 1. DTO para Eventos Recentes
```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

public record EventoAuditoriaRecenteDTO(
    String id,
    TipoEventoAuditoria tipo,
    String usuario,
    String realm,
    String detalhes,
    String ipAddress,
    LocalDateTime dataCriacao,
    String status
) {

    /**
     * Obter classe CSS para o badge de tipo
     */
    public String getTipoBadgeClass() {
        return switch (tipo) {
            case LOGIN, LOGIN_REMEMBER_ME -> "badge bg-success-subtle text-success badge-label";
            case LOGIN_FALHADO, CREDENCIAIS_EXPIRADAS -> "badge bg-danger-subtle text-danger badge-label";
            case CRIACAO -> "badge bg-primary-subtle text-primary badge-label";
            case EDICAO -> "badge bg-info-subtle text-info badge-label";
            case DELECAO, BLOQUEIO -> "badge bg-warning-subtle text-warning badge-label";
            case DESBLOQUEIO -> "badge bg-success-subtle text-success badge-label";
            case RESET_SENHA, RECUPERACAO_SENHA -> "badge bg-info-subtle text-info badge-label";
            case ACESSO_NEGADO -> "badge bg-danger-subtle text-danger badge-label";
            case LOGOUT -> "badge bg-secondary-subtle text-secondary badge-label";
            default -> "badge bg-light-subtle text-dark badge-label";
        };
    }

    /**
     * Obter ícone Tabler para o tipo
     */
    public String getTipoIcon() {
        return switch (tipo) {
            case LOGIN, LOGIN_REMEMBER_ME -> "ti ti-login";
            case LOGIN_FALHADO -> "ti ti-login-x";
            case CRIACAO -> "ti ti-plus";
            case EDICAO -> "ti ti-edit";
            case DELECAO -> "ti ti-trash";
            case BLOQUEIO -> "ti ti-lock";
            case DESBLOQUEIO -> "ti ti-lock-open";
            case RESET_SENHA, RECUPERACAO_SENHA -> "ti ti-key";
            case ACESSO_NEGADO -> "ti ti-shield-x";
            case LOGOUT -> "ti ti-logout";
            default -> "ti ti-info-circle";
        };
    }

    /**
     * Obter classe CSS para o badge de status
     */
    public String getStatusBadgeClass() {
        return switch (status) {
            case "SUCCESS" -> "badge bg-success-subtle text-success badge-label";
            case "FAILED" -> "badge bg-danger-subtle text-danger badge-label";
            case "WARNING" -> "badge bg-warning-subtle text-warning badge-label";
            default -> "badge bg-secondary-subtle text-secondary badge-label";
        };
    }

    /**
     * Formatar data para exibição (PT-BR)
     */
    public String getDataFormatada() {
        return dataCriacao.format(java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obter tempo relativo (ex: "5 minutos atrás")
     */
    public String getTempoRelativo() {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        java.time.Duration duracao = java.time.Duration.between(dataCriacao, agora);

        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();
        long dias = duracao.toDays();

        if (minutos < 1) {
            return "Agora";
        } else if (minutos < 60) {
            return minutos + " minuto" + (minutos == 1 ? "" : "s") + " atrás";
        } else if (horas < 24) {
            return horas + " hora" + (horas == 1 ? "" : "s") + " atrás";
        } else if (dias < 7) {
            return dias + " dia" + (dias == 1 ? "" : "s") + " atrás";
        } else {
            return getDataFormatada();
        }
    }
}
```

### 2. Update DashboardService
```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.*;
import br.com.plataforma.conexaodigital.auditoria.domain.*;
import br.com.plataforma.conexaodigital.auditoria.domain.repository.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EventoAuditoriaRepository eventoRepository;

    // ... construtor e outros métodos ...

    /**
     * Obter os 10 eventos mais recentes de auditoria
     */
    public List<EventoAuditoriaRecenteDTO> getEventosRecentes() {
        List<EventoAuditoria> eventos = eventoRepository
            .findAllByOrderByDataCriacaoDesc()
            .stream()
            .limit(10)
            .collect(Collectors.toList());

        return eventos.stream()
            .map(evento -> new EventoAuditoriaRecenteDTO(
                evento.getId().toString(),
                evento.getTipo(),
                evento.getUsuario(),
                evento.getRealm() != null ? evento.getRealm().getNome() : "N/A",
                evento.getDetalhes(),
                evento.getIpAddress(),
                evento.getDataCriacao(),
                evento.getStatus()
            ))
            .collect(Collectors.toList());
    }
}
```

### 3. Repository Update
```java
package br.com.plataforma.conexaodigital.auditoria.domain.repository;

import br.com.plataforma.conexaodigital.auditoria.domain.EventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {

    /**
     * Buscar todos os eventos ordenados por data (mais recentes primeiro)
     */
    List<EventoAuditoria> findAllByOrderByDataCriacaoDesc();

    // ... outros métodos existentes ...
}
```

### 4. Update Controller
```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import br.com.plataforma.conexaodigital.admin.api.responses.*;
import br.com.plataforma.conexaodigital.admin.domain.service.DashboardService;

@Controller
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // Carregar métricas
        DashboardMetrics metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("metrics", metrics);

        // Carregar eventos recentes
        List<EventoAuditoriaRecenteDTO> eventosRecentes =
            dashboardService.getEventosRecentes();
        model.addAttribute("eventosRecentes", eventosRecentes);

        return "admin/dashboard/index";
    }
}
```

### 5. HTML Template - Tabela de Eventos Recentes
```html
<!-- Adicionar ao template admin/dashboard/index.html após os gráficos -->

<!-- Tabela de Eventos Recentes -->
<div class="row mt-4">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h4 class="card-title mb-0">
                        <i class="ti ti-shield me-2"></i>
                        Eventos de Auditoria Recentes
                    </h4>
                    <a th:href="@{/admin/auditoria}" class="btn btn-sm btn-primary">
                        Ver Todos <i class="ti ti-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-centered table-hover mb-0">
                        <thead class="bg-light bg-opacity-25">
                            <tr class="text-uppercase fs-xxs">
                                <th>Tipo</th>
                                <th>Usuário</th>
                                <th>Realm</th>
                                <th>Detalhes</th>
                                <th>Data</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr th:each="evento : ${eventosRecentes}">
                                <!-- Tipo do Evento -->
                                <td>
                                    <span th:class="${evento.tipoBadgeClass}">
                                        <i th:class="${evento.tipoIcon}" class="me-1"></i>
                                        <span th:text="${evento.tipo}">LOGIN</span>
                                    </span>
                                </td>

                                <!-- Usuário -->
                                <td>
                                    <span th:text="${evento.usuario}" class="fw-medium">
                                        joao.silva@example.com
                                    </span>
                                </td>

                                <!-- Realm -->
                                <td>
                                    <span th:text="${evento.realm}" class="text-muted fs-sm">
                                        Master
                                    </span>
                                </td>

                                <!-- Detalhes -->
                                <td>
                                    <span th:text="${evento.detalhes}" class="text-muted fs-sm">
                                        Login realizado com sucesso
                                    </span>
                                </td>

                                <!-- Data -->
                                <td>
                                    <div class="d-flex flex-column">
                                        <span th:text="${evento.dataFormatada}" class="fw-medium">
                                            24/12/2025 14:30
                                        </span>
                                        <span th:text="${evento.tempoRelativo}" class="text-muted fs-xxs">
                                            5 minutos atrás
                                        </span>
                                    </div>
                                </td>

                                <!-- Status -->
                                <td>
                                    <span th:class="${evento.statusBadgeClass}" class="fs-xs">
                                        <span th:text="${evento.status}">SUCCESS</span>
                                    </span>
                                </td>
                            </tr>

                            <!-- Se não houver eventos -->
                            <tr th:if="${#lists.isEmpty(eventosRecentes)}">
                                <td colspan="6" class="text-center py-4">
                                    <div class="text-muted">
                                        <i class="ti ti-inbox fs-32 mb-2 d-block"></i>
                                        <p class="mb-0">Nenhum evento de auditoria registrado</p>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Card Footer com link -->
            <div class="card-footer border-0 bg-light-subtle">
                <div class="text-center">
                    <a th:href="@{/admin/auditoria}" class="link-primary text-decoration-none">
                        Ver todos os eventos de auditoria
                        <i class="ti ti-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
```

### 6. Estilos CSS Adicionais
```css
/* Adicionar ao CSS principal ou arquivo específico */

/* Tabela de eventos recentes */
.table-centered th,
.table-centered td {
    vertical-align: middle;
}

/* Badges de tipo de evento */
.badge-label {
    font-weight: 500;
    font-size: 0.75rem;
    padding: 0.375rem 0.5rem;
    border-radius: 0.25rem;
    letter-spacing: 0.025em;
}

/* Hover effect na tabela */
.table-hover tbody tr:hover {
    background-color: rgba(0, 0, 0, 0.02);
}

/* Tempo relativo em fonte menor */
.fs-xxs {
    font-size: 0.7rem;
}

/* Badges com ícones */
.badge i {
    font-size: 0.875rem;
}
```

### 7. Enum de Tipos de Evento
```java
package br.com.plataforma.conexaodigital.auditoria.domain;

public enum TipoEventoAuditoria {
    LOGIN("Login"),
    LOGIN_FALHADO("Login Falhado"),
    LOGIN_REMEMBER_ME("Login (Remember-Me)"),
    LOGOUT("Logout"),
    CRIACAO("Criação"),
    EDICAO("Edição"),
    DELECAO("Deleção"),
    BLOQUEIO("Bloqueio"),
    DESBLOQUEIO("Desbloqueio"),
    RESET_SENHA("Reset de Senha"),
    RECUPERACAO_SENHA("Recuperação de Senha"),
    ACESSO_NEGADO("Acesso Negado"),
    CREDENCIAIS_EXPIRADAS("Credenciais Expiradas"),
    REMEMBER_ME_EXPIRADO("Remember-Me Expirado");

    private final String descricao;

    TipoEventoAuditoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
```

## Exemplo de Dados Populados (Mock)

```html
<!-- Exemplo de como a tabela ficaria com dados reais -->
<tbody>
    <tr>
        <td>
            <span class="badge bg-success-subtle text-success badge-label">
                <i class="ti ti-login me-1"></i>
                LOGIN
            </span>
        </td>
        <td>
            <span class="fw-medium">joao.silva@example.com</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Master</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Login realizado com sucesso</span>
        </td>
        <td>
            <div class="d-flex flex-column">
                <span class="fw-medium">24/12/2025 14:30</span>
                <span class="text-muted fs-xxs">5 minutos atrás</span>
            </div>
        </td>
        <td>
            <span class="badge bg-success-subtle text-success badge-label fs-xs">SUCCESS</span>
        </td>
    </tr>

    <tr>
        <td>
            <span class="badge bg-danger-subtle text-danger badge-label">
                <i class="ti ti-login-x me-1"></i>
                LOGIN_FALHADO
            </span>
        </td>
        <td>
            <span class="fw-medium">maria.santos@example.com</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Empresa A</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Credenciais inválidas</span>
        </td>
        <td>
            <div class="d-flex flex-column">
                <span class="fw-medium">24/12/2025 14:25</span>
                <span class="text-muted fs-xxs">10 minutos atrás</span>
            </div>
        </td>
        <td>
            <span class="badge bg-danger-subtle text-danger badge-label fs-xs">FAILED</span>
        </td>
    </tr>

    <tr>
        <td>
            <span class="badge bg-primary-subtle text-primary badge-label">
                <i class="ti ti-plus me-1"></i>
                CRIACAO
            </span>
        </td>
        <td>
            <span class="fw-medium">admin@conexaoauth.com</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Master</span>
        </td>
        <td>
            <span class="text-muted fs-sm">Usuário joao.silva criado</span>
        </td>
        <td>
            <div class="d-flex flex-column">
                <span class="fw-medium">24/12/2025 13:45</span>
                <span class="text-muted fs-xxs">50 minutos atrás</span>
            </div>
        </td>
        <td>
            <span class="badge bg-success-subtle text-success badge-label fs-xs">SUCCESS</span>
        </td>
    </tr>
</tbody>
```

## Checklist de Validação
- [x] DTO `EventoAuditoriaRecenteDTO` criado
- [x] Service method `getEventosRecentes()` implementado
- [x] Tabela exibe até 10 eventos recentes
- [x] Colunas corretas (Tipo, Usuário, Realm, Detalhes, Data, Status)
- [x] Badges de cor por tipo funcionam
- [x] Badges de cor por status funcionam
- [x] Ícones Tabler por tipo funcionam
- [x] Tempo relativo é calculado corretamente
- [x] Link "Ver Todos" redireciona para `/admin/auditoria`
- [x] Tabela é responsiva em dispositivos móveis
- [x] Mensagem "Nenhum evento" é exibida quando vazio

## Notas
- Tabela segue padrão do template API Keys do `ui.txt`
- Badges usam classes `bg-*-subtle text-* badge-label` do Bootstrap 5
- Ícones Tabler: `ti ti-login`, `ti ti-plus`, `ti ti-edit`, etc.
- Tempo relativo é calculado em Java para performance
- Link "Ver Todos" aponta para `/admin/auditoria` (Epic 16)
- Dados são ordenados por data (mais recentes primeiro)
- Página de auditoria será implementada em Epic 16

## Prioridade
**Média** - Visualização importante de auditoria no dashboard

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas

---

## Dev Agent Record

### Agent Model Used
Claude Sonnet 4.0 (claude-sonnet-4-20250514)

### Completion Notes
- Tabela de eventos recentes implementada com 10 últimos eventos
- Colunas: Tipo, Usuário, Realm, Detalhes, Data, Status
- DTO `EventoAuditoriaRecenteDTO` criado com métodos auxiliares para badging
- Badges de cor por tipo de evento implementados (success, danger, warning, info, primary)
- Badges de cor por status implementados (SUCCESS, FAILED, WARNING)
- Ícones Tabler por tipo funcionando corretamente
- Tempo relativo calculado em Java (Agora, 5 minutos atrás, etc.)
- Link "Ver Todos" redirecionando para `/admin/auditoria`
- Tabela responsiva com table-responsive wrapper
- Mensagem "Nenhum evento" exibida quando vazia
- `DashboardService.getEventosRecentes()` implementado
- `EventoAuditoriaRepository.findAllByOrderByDataCriacaoDesc()` implementado

### Change Log
- Atualizado `admin/dashboard/index.html` com tabela de eventos recentes
- Criado `src/main/java/.../admin/api/responses/EventoAuditoriaRecenteDTO.java`
- Atualizado `EventoAuditoriaRepository.java` com método findAllByOrderByDataCriacaoDesc()
- Atualizado `DashboardService.java` com método getEventosRecentes()
- Atualizado `AdminDashboardController.java` passando eventosRecentes para o template

### File List
- `src/main/resources/templates/admin/dashboard/index.html` - Template atualizado com tabela de eventos
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/EventoAuditoriaRecenteDTO.java` - DTO com métodos auxiliares
- `src/main/java/br/com/plataforma/conexaodigital/auditoria/domain/repository/EventoAuditoriaRepository.java` - Repository atualizado
- `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/DashboardService.java` - Service atualizado
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminDashboardController.java` - Controller atualizado

### Debug Log References
Nenhuma ocorrência de bugs ou problemas durante implementação.

---

