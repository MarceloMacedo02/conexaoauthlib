# User Story: Auditoria - DTOs de Eventos Auditoria (Java Records)

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-02

## Status
**Estado:** 🔲 Planejado
**Prioridade:** Alta

## Descrição

Criar DTOs (Data Transfer Objects) para representação de eventos de auditoria nas camadas de controller e view, incluindo filtros, paginação e metadados auxiliares para exibição na UI.

## Critérios de Aceitação

- [ ] DTO `AuditoriaListResponse` para paginação de eventos
- [ ] DTO `AuditoriaFilterRequest` para filtros avançados
- [ ] DTO `AuditoriaExportRequest` para exportação CSV
- [ ] DTO `EventoAuditoriaDetailDTO` para modal de detalhes
- [ ] DTO `EventoAuditoriaRowDTO` para linha da tabela
- [ ] DTOs seguem o padrão de records do Java 21
- [ ] Validações Jakarta Bean Validation implementadas
- [ ] Métodos auxiliares para formatação (data, tempo relativo)

## Tarefas

1. Criar record `AuditoriaListResponse` com:
   - Listagem de eventos
   - Informações de paginação
   - Total de registros
2. Criar record `AuditoriaFilterRequest` com:
   - realmId (String, opcional)
   - tipoEvento (String, opcional)
   - severidade (String, opcional)
   - usuario (String, opcional)
   - dataInicio (LocalDateTime, opcional)
   - dataFim (LocalDateTime, opcional)
3. Criar record `EventoAuditoriaRowDTO` com:
   - Todos os campos da entidade
   - Métodos auxiliares: `getTipoIcon()`, `getTipoBadgeClass()`, `getDataFormatada()`, `getTempoRelativo()`
4. Criar record `EventoAuditoriaDetailDTO` com:
   - Campo `detalhesJson` com o JSON completo do evento
   - Todos os campos para exibição no modal

## Instruções de Implementação

### 1. AuditoriaListResponse

```java
package br.com.plataforma.conexaodigital.admin.api.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de listagem de eventos de auditoria.
 */
public record AuditoriaListResponse(
    List<EventoAuditoriaRowDTO> eventos,
    int page,
    int size,
    long total,
    boolean hasNext,
    boolean hasPrevious
) {
    /**
     * Calcula o índice inicial (baseado em 0).
     */
    public int getInicio() {
        return page * size;
    }

    /**
     * Calcula o índice final (baseado em 0).
     */
    public int getFim() {
        return Math.min((page + 1) * size - 1, (int) total - 1);
    }
}
```

### 2. AuditoriaFilterRequest

```java
package br.com.plataforma.conexaodigital.admin.api.dto.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DateTimeFormat;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * DTO para filtros de consulta de eventos de auditoria.
 */
public record AuditoriaFilterRequest(
    String realmId,

    String tipoEvento,

    @Pattern(regexp = "INFO|SUCCESS|WARNING|ERROR", message = "Severidade inválida")
    String severidade,

    String usuario,

    @DateTimeFormat(pattern = "yyyy-MM-dd", message = "Formato de data inválido")
    LocalDateTime dataInicio,

    @DateTimeFormat(pattern = "yyyy-MM-dd", message = "Formato de data inválido")
    LocalDateTime dataFim,

    @AssertTrue(message = "Data final deve ser maior ou igual à data inicial", payload = AuditoriaFilterValidator.class)
    Boolean isPeriodoValido
) {
    public AuditoriaFilterRequest {
        // Validação de período será feita no validator
        this.isPeriodoValido = true;
    }
}
```

### 3. EventoAuditoriaRowDTO

```java
package br.com.plataforma.conexaodigital.admin.api.dto.responses;

import br.com.plataforma.conexaodigital.auditoria.domain.model.enums.TipoEventoAuditoria;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO para representação de um evento de auditoria na tabela.
 */
public record EventoAuditoriaRowDTO(
    String id,
    TipoEventoAuditoria tipo,
    String usuario,
    String realm,
    String ipAddress,
    String detalhes,
    LocalDateTime dataCriacao
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Obtém classe CSS para o badge de tipo.
     */
    public String getTipoBadgeClass() {
        return switch (tipo) {
            case LOGIN, LOGIN_REMEMBER_ME -> "badge bg-success-subtle text-success badge-label";
            case LOGIN_FALHADO, CREDENCIAIS_EXPIRADAS -> "badge bg-danger-subtle text-danger badge-label";
            case CRIACAO -> "badge bg-primary-subtle text-primary badge-label";
            case EDICAO -> "badge bg-info-subtle text-info badge-label";
            case DELECAO, BLOQUEIO -> "badge bg-warning-subtle text-warning badge-label";
            case DESBLOQUEIO, RESET_SENHA, RECUPERACAO_SENHA -> "badge bg-info-subtle text-info badge-label";
            case ACESSO_NEGADO -> "badge bg-danger-subtle text-danger badge-label";
            case LOGOUT -> "badge bg-secondary-subtle text-secondary badge-label";
            default -> "badge bg-light-subtle text-dark badge-label";
        };
    }

    /**
     * Obtém ícone Tabler para o tipo.
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
            case CRIAÇÃO_REALM -> "ti ti-server";
            case EDIÇÃO_REALM -> "ti ti-server-edit";
            case ATIVAÇÃO_REALM, DESATIVAÇÃO_REALM -> "ti ti-server-check";
            case CRIAÇÃO_ROLE, EDIÇÃO_ROLE, EXCLUSÃO_ROLE -> "ti ti-shield";
            case ROTAÇÃO_CHAVE_MANUAL, ROTAÇÃO_CHAVE_AUTOMÁTICA -> "ti ti-rotate";
            case CRIAÇÃO_CHAVE -> "ti ti-key";
            default -> "ti ti-info-circle";
        };
    }

    /**
     * Formata data para exibição (PT-BR).
     */
    public String getDataFormatada() {
        return dataCriacao.format(FORMATTER);
    }

    /**
     * Obtém tempo relativo (ex: "5 minutos atrás").
     */
    public String getTempoRelativo() {
        LocalDateTime agora = LocalDateTime.now();
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

    /**
     * Obtém label de usuário truncado.
     */
    public String getUsuarioTruncado() {
        if (usuario != null && usuario.length() > 30) {
            return usuario.substring(0, 27) + "...";
        }
        return usuario != null ? usuario : "";
    }

    /**
     * Obtém detalhes truncado.
     */
    public String getDetalhesTruncado() {
        if (detalhes != null && detalhes.length() > 50) {
            return detalhes.substring(0, 47) + "...";
        }
        return detalhes != null ? detalhes : "";
    }
}
```

### 4. EventoAuditoriaDetailDTO

```java
package br.com.plataforma.conexaodigital.admin.api.dto.responses;

import br.com.plataforma.conexaodigital.auditoria.domain.model.enums.TipoEventoAuditoria;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * DTO para detalhes completos de um evento de auditoria.
 */
public record EventoAuditoriaDetailDTO(
    String id,
    TipoEventoAuditoria tipo,
    String usuario,
    St

### 5. AuditoriaFilterValidator

```java
package br.com.plataforma.conexaodigital.admin.api.dto.validation;

import br.com.plataforma.conexaodigital.admin.api.dto.requests.AuditoriaFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validador para o filtro de período de auditoria.
 */
public class AuditoriaFilterValidator implements ConstraintValidator<LocalDateTime, AuditoriaFilterRequest> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        AuditoriaFilterRequest request = (AuditoriaFilterRequest) context.getConstraintValidatorContext().getRootBean();

        if (request == null) {
            return true;
        }

        // Se data final não informada, válida
        if (request.dataFim() == null) {
            return true;
        }

        // Se data inicial não informada, data final deve ser válida
        if (request.dataInicio() == null) {
            return true;
        }

        // Data final deve ser maior ou igual à data inicial
        return !request.dataFim().isBefore(request.dataInicio());
    }

    @Override
    public Class<LocalDateTime> getValidationType() {
        return LocalDateTime.class;
    }
}
```

## Validações

- [ ] Todos os DTOs criados no pacote correto
- [ ] Validações Jakarta Bean Validation funcionando
- [ ] Métodos auxiliares testados
- [ ] Formatação de data em PT-BR
- [ ] Tempo relativo calculado corretamente

## Próximos Passos

1. Implementar Story 03: Backend Service Layer
2. Implementar Story 04: Controller API
3. Implementar Story 05: Filtros Avançados

---

**Estado:** 🔲 Planejado
**Responsável:** BMad Team
