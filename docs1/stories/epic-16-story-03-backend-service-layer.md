# User Story: Backend Service Layer (AuditoriaService)

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-03

## Descrição
Criar a camada de serviço `AdminAuditoriaService` com métodos para operações de consulta de eventos de auditoria, incluindo listagem com filtros avançados, busca por ID, e preparação para exportação CSV.

## Critérios de Aceite
- [ ] Classe `AdminAuditoriaService` criada com todos os métodos necessários
- [ ] Método `buscarEventos()` implementado com filtros
- [ ] Método `buscarPorId()` implementado
- [ ] Método `buscarParaExportar()` implementado
- [ ] Método `contarEventosPorTipo()` implementado
- [ ] Filtros suportados: realmId, tipoEvento, usuario, dataInicial, dataFinal, busca
- [ ] Paginação configurada corretamente
- [ ] Ordenação configurável (padrão: timestamp DESC)
- [ ] Transações gerenciadas corretamente (@Transactional)
- [ ] Consultas otimizadas (indexes)
- [ ] Integração com repositório existente

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.domain.service`
2. Criar classe `AdminAuditoriaService.java`
3. Implementar método de busca com filtros
4. Implementar método de busca por ID
5. Implementar método de busca para exportação
6. Implementar método de contagem por tipo
7. Criar DTOs de filtros (se necessário)
8. Adicionar suporte a paginação
9. Adicionar ordenação
10. Otimizar consultas com índices

## Instruções de Implementação

### Service Class
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/AdminAuditoriaService.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.EventoAuditoriaResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.EventoDetalheResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.TipoEventoAuditoria;
import br.com.plataforma.conexaodigital.auditoria.domain.model.EventoAuditoria;
import br.com.plataforma.conexaodigital.auditoria.domain.repository.EventoAuditoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service para operações administrativas de auditoria.
 */
@Service
public class AdminAuditoriaService {

    private final EventoAuditoriaRepository eventoRepository;

    public AdminAuditoriaService(EventoAuditoriaRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Busca eventos de auditoria com filtros avançados.
     *
     * @param pageable configuração de paginação
     * @param realmId filtro por realm (opcional)
     * @param tipoEvento filtro por tipo de evento (opcional)
     * @param usuario filtro por usuário (opcional)
     * @param dataInicial filtro por data inicial (opcional)
     * @param dataFinal filtro por data final (opcional)
     * @param busca busca textual (opcional)
     * @return página de eventos
     */
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaResponse> buscarEventos(
        Pageable pageable,
        String realmId,
        String tipoEvento,
        String usuario,
        LocalDateTime dataInicial,
        LocalDateTime dataFinal,
        String busca
    ) {
        // Configurar ordenação padrão (timestamp descendente)
        if (pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "timestamp")
            );
        }

        // Converter tipo de evento string para enum
        TipoEventoAuditoria tipoEnum = null;
        if (tipoEvento != null && !tipoEvento.isBlank() && !"All".equals(tipoEvento)) {
            try {
                tipoEnum = TipoEventoAuditoria.valueOf(tipoEvento);
            } catch (IllegalArgumentException e) {
                // Tipo inválido, ignorar
            }
        }

        // Buscar eventos com filtros
        Page<EventoAuditoria> eventos = eventoRepository.buscarComFiltros(
            pageable,
            realmId,
            tipoEnum,
            usuario,
            dataInicial,
            dataFinal,
            busca
        );

        // Mapear para DTO
        return eventos.map(evento -> {
            String realmNome = evento.getRealm() != null ? evento.getRealm().getNome() : "N/A";
            return EventoAuditoriaResponse.from(evento, realmNome);
        });
    }

    /**
     * Busca evento por ID com detalhes completos.
     *
     * @param id ID do evento
     * @return detalhes do evento
     * @throws IllegalArgumentException se evento não for encontrado
     */
    @Transactional(readOnly = true)
    public EventoDetalheResponse buscarPorId(UUID id) {
        EventoAuditoria evento = eventoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        String realmNome = evento.getRealm() != null ? evento.getRealm().getNome() : "N/A";
        String realmId = evento.getRealm() != null ? evento.getRealm().getId().toString() : null;

        return EventoDetalheResponse.from(evento, realmNome, realmId);
    }

    /**
     * Busca eventos para exportação (sem paginação).
     *
     * @param realmId filtro por realm (opcional)
     * @param tipoEvento filtro por tipo de evento (opcional)
     * @param dataInicial filtro por data inicial (opcional)
     * @param dataFinal filtro por data final (opcional)
     * @return lista de eventos para exportação
     */
    @Transactional(readOnly = true)
    public List<EventoAuditoriaResponse> buscarParaExportar(
        String realmId,
        String tipoEvento,
        LocalDateTime dataInicial,
        LocalDateTime dataFinal
    ) {
        // Converter tipo de evento
        TipoEventoAuditoria tipoEnum = null;
        if (tipoEvento != null && !tipoEvento.isBlank() && !"All".equals(tipoEvento)) {
            try {
                tipoEnum = TipoEventoAuditoria.valueOf(tipoEvento);
            } catch (IllegalArgumentException e) {
                // Tipo inválido, ignorar
            }
        }

        // Buscar todos os eventos (sem paginação, limit 10.000)
        Pageable pageable = PageRequest.of(0, 10000, Sort.by(Sort.Direction.DESC, "timestamp"));

        List<EventoAuditoria> eventos = eventoRepository.buscarComFiltros(
            pageable,
            realmId,
            tipoEnum,
            null, // sem filtro de usuário
            dataInicial,
            dataFinal,
            null  // sem busca textual
        ).getContent();

        // Mapear para DTO
        return eventos.stream()
            .map(evento -> {
                String realmNome = evento.getRealm() != null ? evento.getRealm().getNome() : "N/A";
                return EventoAuditoriaResponse.from(evento, realmNome);
            })
            .toList();
    }

    /**
     * Conta eventos por tipo para um realm.
     *
     * @param realmId ID do realm (opcional)
     * @return mapa com contagem por tipo
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> contarEventosPorTipo(String realmId) {
        List<Object[]> resultados;

        if (realmId == null || "All".equals(realmId)) {
            // Contar todos os eventos por tipo
            resultados = eventoRepository.contarPorTipoTodos();
        } else {
            // Contar eventos por tipo para um realm específico
            resultados = eventoRepository.contarPorTipoRealm(UUID.fromString(realmId));
        }

        // Converter para mapa
        java.util.Map<String, Long> contagem = new java.util.LinkedHashMap<>();
        for (Object[] resultado : resultados) {
            TipoEventoAuditoria tipo = (TipoEventoAuditoria) resultado[0];
            Long quantidade = (Long) resultado[1];
            contagem.put(tipo.name(), quantidade);
        }

        return contagem;
    }

    /**
     * Busca eventos de segurança crítica.
     *
     * @param realmId ID do realm (opcional)
     * @param dias número de dias atrás (padrão: 7)
     * @return lista de eventos críticos
     */
    @Transactional(readOnly = true)
    public List<EventoAuditoriaResponse> buscarEventosCriticos(
        String realmId,
        Integer dias
    ) {
        // Calcular data limite (padrão: 7 dias atrás)
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(dias != null ? dias : 7);

        List<TipoEventoAuditoria> tiposCriticos = List.of(
            TipoEventoAuditoria.TENTATIVA_BRUTE_FORCE,
            TipoEventoAuditoria.ACESSO_NEGADO
        );

        List<EventoAuditoria> eventos;

        if (realmId == null || "All".equals(realmId)) {
            eventos = eventoRepository.buscarCriticosUltimosDias(
                tiposCriticos, dataLimite
            );
        } else {
            eventos = eventoRepository.buscarCriticosUltimosDiasRealm(
                tiposCriticos, dataLimite, UUID.fromString(realmId)
            );
        }

        // Mapear para DTO
        return eventos.stream()
            .map(evento -> {
                String realmNome = evento.getRealm() != null ? evento.getRealm().getNome() : "N/A";
                return EventoAuditoriaResponse.from(evento, realmNome);
            })
            .toList();
    }
}
```

### Repository Methods (caso não existam)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/auditoria/domain/repository/EventoAuditoriaRepository.java`

```java
package br.com.plataforma.conexaodigital.auditoria.domain.repository;

import br.com.plataforma.conexaodigital.admin.api.responses.TipoEventoAuditoria;
import br.com.plataforma.conexaodigital.auditoria.domain.model.EventoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, UUID> {

    /**
     * Busca eventos com filtros opcionais.
     */
    @Query("SELECT e FROM EventoAuditoria e " +
           "WHERE (:realmId IS NULL OR e.realm.id = :realmId) " +
           "AND (:tipoEvento IS NULL OR e.tipo = :tipoEvento) " +
           "AND (:usuario IS NULL OR LOWER(e.usuario) LIKE LOWER(CONCAT('%', :usuario, '%'))) " +
           "AND (:dataInicial IS NULL OR e.timestamp >= :dataInicial) " +
           "AND (:dataFinal IS NULL OR e.timestamp <= :dataFinal) " +
           "AND (:busca IS NULL OR LOWER(e.detalhes) LIKE LOWER(CONCAT('%', :busca, '%')) " +
           "OR LOWER(e.usuario) LIKE LOWER(CONCAT('%', :busca, '%')) " +
           "OR LOWER(e.ipOrigem) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<EventoAuditoria> buscarComFiltros(
        Pageable pageable,
        @Param("realmId") String realmId,
        @Param("tipoEvento") TipoEventoAuditoria tipoEvento,
        @Param("usuario") String usuario,
        @Param("dataInicial") LocalDateTime dataInicial,
        @Param("dataFinal") LocalDateTime dataFinal,
        @Param("busca") String busca
    );

    /**
     * Conta eventos por tipo (todos os realms).
     */
    @Query("SELECT e.tipo, COUNT(e) FROM EventoAuditoria e GROUP BY e.tipo")
    List<Object[]> contarPorTipoTodos();

    /**
     * Conta eventos por tipo (por realm).
     */
    @Query("SELECT e.tipo, COUNT(e) FROM EventoAuditoria e " +
           "WHERE e.realm.id = :realmId GROUP BY e.tipo")
    List<Object[]> contarPorTipoRealm(@Param("realmId") UUID realmId);

    /**
     * Busca eventos críticos dos últimos X dias.
     */
    @Query("SELECT e FROM EventoAuditoria e " +
           "WHERE e.tipo IN :tiposCriticos " +
           "AND e.timestamp >= :dataLimite")
    List<EventoAuditoria> buscarCriticosUltimosDias(
        @Param("tiposCriticos") List<TipoEventoAuditoria> tiposCriticos,
        @Param("dataLimite") LocalDateTime dataLimite
    );

    /**
     * Busca eventos críticos dos últimos X dias por realm.
     */
    @Query("SELECT e FROM EventoAuditoria e " +
           "WHERE e.realm.id = :realmId " +
           "AND e.tipo IN :tiposCriticos " +
           "AND e.timestamp >= :dataLimite")
    List<EventoAuditoria> buscarCriticosUltimosDiasRealm(
        @Param("realmId") UUID realmId,
        @Param("tiposCriticos") List<TipoEventoAuditoria> tiposCriticos,
        @Param("dataLimite") LocalDateTime dataLimite
    );
}
```

## Checklist de Validação
- [ ] Classe `AdminAuditoriaService` criada
- [ ] Método `buscarEventos()` implementado com todos os filtros
- [ ] Método `buscarPorId()` implementado
- [ ] Método `buscarParaExportar()` implementado
- [ ] Método `contarEventosPorTipo()` implementado
- [ ] Método `buscarEventosCriticos()` implementado
- [ ] Filtro por realmId funcionando
- [ ] Filtro por tipoEvento funcionando
- [ ] Filtro por usuário funcionando
- [ ] Filtro por período (dataInicial, dataFinal) funcionando
- [ ] Busca textual (detalhes, usuário, IP) funcionando
- [ ] Paginação configurada corretamente
- [ ] Ordenação padrão (timestamp DESC)
- [ ] Anotação `@Transactional` aplicada nos métodos de leitura
- [ ] Repository methods adicionados/verificados
- [ ] DTOs mapeados corretamente
- [ ] Consultas otimizadas

## Anotações
- Todos os métodos de leitura são transacionais (@Transactional(readOnly = true))
- Ordenação padrão por timestamp descendente (mais recente primeiro)
- Conversão segura de string para enum (trata IllegalArgumentException)
- Filtros são opcionais (null ou "All" ignora o filtro)
- Busca textual pesquisa em detalhes, usuário e IP origem
- Exportação limitada a 10.000 registros (evitar memory issues)
- Eventos críticos incluem TENTATIVA_BRUTE_FORCE e ACESSO_NEGADO
- Realm nome é obtido da entidade (não da query principal)
- Data limite para eventos críticos é 7 dias atrás por padrão

## Dependências
- Epic 7 (Auditoria) - entidade e repository já existem
- Epic 1 (Gestão de Realms) - para associação com realm
- Story 02 (DTOs de Auditoria) - DTOs necessários

## Prioridade
**Alta** - Service layer necessário para controller

## Estimativa
- Implementação: 4.5 horas
- Testes: 2.5 horas
- Total: 7 horas

### 2. Criar Exceção

```java
package br.com.plataforma.conexaodigital.admin.exceptions;

/**
 * Exceção customizada para auditoria.
 */
public class AuditoriaException extends RuntimeException {
    public AuditoriaException(String message) {
        super(message);
    }

    public AuditoriaException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## Validações

- [ ] Service criado no pacote correto
- [ ] Todos os métodos implementados
- [ ] Injeção de dependências funcionando
- [ ] Tratamento de erros apropriado

## Próximos Passos

1. Implementar Story 04: Controller API (atualizar endpoint GET para filtros)
2. Implementar Story 05: Filtros Avançados

---

**Estado:** 🔲 Planejado
**Responsável:** BMad Team
