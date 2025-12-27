# User Story: Backend Service Layer (ChaveService)

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-03

## Descrição
Criar a camada de serviço `AdminChaveService` com métodos para operações de gestão de chaves criptográficas, incluindo listagem de chaves ativas, histórico de rotações, rotação manual e consulta de próxima rotação automática.

## Critérios de Aceite
- [ ] Classe `AdminChaveService` criada com todos os métodos necessários
- [ ] Método `listarChavesAtivas(realmId)` implementado
- [ ] Método `listarHistoricoRotacoes(realmId)` implementado
- [ ] Método `rotacionarChaveManual(realmId, motivo, usuario)` implementado
- [ ] Método `obterProximaRotacaoAutomatica()` implementado
- [ ] Método `buscarChavePorRealm(realmId)` implementado
- [ ] Transações gerenciadas corretamente (@Transactional)
- [ ] Auditoria de eventos registrada
- [ ] Integração com serviço de geração de chaves
- [ ] Integração com serviço de rotação automática

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.domain.service`
2. Criar classe `AdminChaveService.java`
3. Implementar método de listagem de chaves ativas
4. Implementar método de listagem de histórico
5. Implementar método de rotação manual
6. Implementar método de consulta de próxima rotação
7. Implementar método de busca de chave por realm
8. Adicionar validações de negócio
9. Integrar com serviço de auditoria
10. Integrar com serviço de geração de chaves

## Instruções de Implementação

### Service Class
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/AdminChaveService.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.responses.ChaveAtivaResponse;
import br.com.plataforma.conexaodigital.admin.api.requests.RotacaoRequest;
import br.com.plataforma.conexaodigital.admin.api.responses.RotacaoHistoricoResponse;
import br.com.plataforma.conexaodigital.gestachave.domain.model.ChaveCriptografica;
import br.com.plataforma.conexaodigital.gestachave.domain.model.RotacaoChave;
import br.com.plataforma.conexaodigital.gestachave.domain.repository.ChaveCriptograficaRepository;
import br.com.plataforma.conexaodigital.gestachave.domain.repository.RotacaoChaveRepository;
import br.com.plataforma.conexaodigital.gestachave.service.ChaveService;
import br.com.plataforma.conexaodigital.auditoria.domain.service.AuditoriaService;
import br.com.plataforma.conexaodigital.gestarealm.domain.model.Realm;
import br.com.plataforma.conexaodigital.gestarealm.domain.repository.RealmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service para operações administrativas de chaves criptográficas.
 */
@Service
public class AdminChaveService {

    private final ChaveCriptograficaRepository chaveRepository;
    private final RotacaoChaveRepository rotacaoRepository;
    private final RealmRepository realmRepository;
    private final ChaveService chaveService;
    private final AuditoriaService auditoriaService;

    public AdminChaveService(
        ChaveCriptograficaRepository chaveRepository,
        RotacaoChaveRepository rotacaoRepository,
        RealmRepository realmRepository,
        ChaveService chaveService,
        AuditoriaService auditoriaService
    ) {
        this.chaveRepository = chaveRepository;
        this.rotacaoRepository = rotacaoRepository;
        this.realmRepository = realmRepository;
        this.chaveService = chaveService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Lista todas as chaves ativas no sistema.
     *
     * @param realmId ID do realm (opcional)
     * @return lista de chaves ativas
     */
    @Transactional(readOnly = true)
    public List<ChaveAtivaResponse> listarChavesAtivas(String realmId) {
        List<ChaveCriptografica> chaves;

        if (realmId == null || "All".equals(realmId)) {
            // Buscar todas as chaves
            chaves = chaveRepository.findAll();
        } else {
            // Buscar chaves por realm
            chaves = chaveRepository.findByRealmId(UUID.fromString(realmId));
        }

        // Mapear para DTO
        return chaves.stream()
            .map(chave -> {
                Realm realm = chave.getRealm();
                String realmNome = realm != null ? realm.getNome() : "N/A";
                String realmIdStr = realm != null ? realm.getId().toString() : null;
                return ChaveAtivaResponse.from(chave, realmNome, realmIdStr);
            })
            .toList();
    }

    /**
     * Lista histórico de rotações de chaves.
     *
     * @param realmId ID do realm (opcional)
     * @return lista de histórico de rotações
     */
    @Transactional(readOnly = true)
    public List<RotacaoHistoricoResponse> listarHistoricoRotacoes(String realmId) {
        List<RotacaoChave> rotacoes;

        if (realmId == null || "All".equals(realmId)) {
            // Buscar todas as rotações (ordenadas por data descendente)
            rotacoes = rotacaoRepository.findAllByOrderByDataRotacaoDesc();
        } else {
            // Buscar rotações por realm
            rotacoes = rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(
                UUID.fromString(realmId)
            );
        }

        // Mapear para DTO
        return rotacoes.stream()
            .map(rotacao -> {
                Realm realm = rotacao.getRealm();
                String realmNome = realm != null ? realm.getNome() : "N/A";
                String realmIdStr = realm != null ? realm.getId().toString() : null;
                return RotacaoHistoricoResponse.from(rotacao, realmNome, realmIdStr);
            })
            .toList();
    }

    /**
     * Executa rotação manual de chave para um realm.
     *
     * @param request solicitação de rotação
     * @param usuarioId ID do usuário solicitante
     * @return chave ativa após rotação
     */
    @Transactional
    public ChaveAtivaResponse rotacionarChaveManual(
        RotacaoRequest request,
        UUID usuarioId
    ) {
        // Validar realm
        Realm realm = realmRepository.findById(UUID.fromString(request.realmId()))
            .orElseThrow(() -> new IllegalArgumentException("Realm não encontrado"));

        // Buscar chave atual do realm
        ChaveCriptografica chaveAtual = chaveRepository.findAtivaByRealmId(realm.getId())
            .orElseThrow(() -> new IllegalArgumentException("Chave ativa não encontrada para este realm"));

        // Verificar se já existe rotação em andamento
        boolean rotacaoEmAndamento = rotacaoRepository.existsByRealmIdAndStatus(
            realm.getId(),
            br.com.plataforma.conexaodigital.gestachave.domain.model.StatusRotacao.EM_ANDAMENTO
        );

        if (rotacaoEmAndamento) {
            throw new IllegalArgumentException("Rotação já está em andamento para este realm");
        }

        // Rotacionar chave usando serviço existente
        ChaveCriptografica novaChave = chaveService.rotacionarChave(
            realm.getId(),
            request.motivo(),
            usuarioId
        );

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioId,
            "ROTACAO_MANUAL",
            "Rotação manual de chave: Realm " + realm.getNome() +
            ", Versão: " + novaChave.getKid() +
            (request.hasMotivo() ? ", Motivo: " + request.motivo() : "")
        );

        // Retornar nova chave
        return ChaveAtivaResponse.from(
            novaChave,
            realm.getNome(),
            realm.getId().toString()
        );
    }

    /**
     * Busca a próxima data de rotação automática (dia 1 do próximo mês).
     *
     * @return data da próxima rotação automática
     */
    @Transactional(readOnly = true)
    public LocalDateTime obterProximaRotacaoAutomatica() {
        LocalDateTime agora = LocalDateTime.now();
        return agora
            .plusMonths(1)
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    }

    /**
     * Busca chave ativa de um realm específico.
     *
     * @param realmId ID do realm
     * @return chave ativa
     */
    @Transactional(readOnly = true)
    public ChaveAtivaResponse buscarChavePorRealm(UUID realmId) {
        Realm realm = realmRepository.findById(realmId)
            .orElseThrow(() -> new IllegalArgumentException("Realm não encontrado"));

        ChaveCriptografica chave = chaveRepository.findAtivaByRealmId(realmId)
            .orElseThrow(() -> new IllegalArgumentException("Chave ativa não encontrada para este realm"));

        return ChaveAtivaResponse.from(
            chave,
            realm.getNome(),
            realm.getId().toString()
        );
    }

    /**
     * Calcula dias restantes até a próxima rotação automática.
     *
     * @return dias restantes
     */
    @Transactional(readOnly = true)
    public int calcularDiasParaProximaRotacao() {
        LocalDateTime proximaRotacao = obterProximaRotacaoAutomatica();
        LocalDateTime agora = LocalDateTime.now();

        return (int) java.time.temporal.ChronoUnit.DAYS.between(agora, proximaRotacao);
    }
}
```

### Repository Methods (caso não existam)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/gestachave/domain/repository/RotacaoChaveRepository.java`

Adicionar método ao repository:

```java
/**
 * Busca rotações por realm ordenadas por data descendente.
 */
List<RotacaoChave> findByRealmIdOrderByDataRotacaoDesc(UUID realmId);

/**
 * Busca todas as rotações ordenadas por data descendente.
 */
List<RotacaoChave> findAllByOrderByDataRotacaoDesc();

/**
 * Verifica se existe rotação em andamento para um realm.
 */
@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
       "FROM RotacaoChave r WHERE r.realm.id = :realmId AND r.status = :status")
boolean existsByRealmIdAndStatus(
    @Param("realmId") UUID realmId,
    @Param("status") StatusRotacao status
);
```

## Checklist de Validação
- [ ] Classe `AdminChaveService` criada
- [ ] Método `listarChavesAtivas(realmId)` implementado
- [ ] Método `listarHistoricoRotacoes(realmId)` implementado
- [ ] Método `rotacionarChaveManual(request, usuarioId)` implementado
- [ ] Método `obterProximaRotacaoAutomatica()` implementado
- [ ] Método `buscarChavePorRealm(realmId)` implementado
- [ ] Método `calcularDiasParaProximaRotacao()` implementado
- [ ] Anotação `@Transactional` aplicada nos métodos de escrita
- [ ] Anotação `@Transactional(readOnly = true)` nos métodos de leitura
- [ ] Auditoria de eventos registrada em rotação manual
- [ ] Validação de realm em rotação manual
- [ ] Validação de rotação em andamento
- [ ] Integração com `ChaveService` existente
- [ ] Repository methods adicionados (se necessário)
- [ ] DTOs mapeados corretamente

## Anotações
- Todos os métodos de escrita devem ser transacionais
- Auditoria deve registrar rotações manuais com motivo
- Rotações automáticas são executadas por job agendado (Epic 5)
- Motivo é opcional, mas deve ser registrado se informado
- Histórico de rotações ordenado por data descendente (mais recente primeiro)
- Próxima rotação automática sempre dia 1 do próximo mês às 00:00
- Rotação manual verifica se já existe rotação em andamento
- Grace period é gerenciado pelo serviço de chaves (Epic 5)

## Dependências
- Epic 1 (Gestão de Realms) - para dados de realms
- Epic 5 (Gestão de Chaves) - entidades e serviços já existem
- Epic 7 (Auditoria) - para registro de eventos
- Story 02 (DTOs de Chave) - DTOs necessários

## Prioridade
**Alta** - Service layer necessário para controller

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
