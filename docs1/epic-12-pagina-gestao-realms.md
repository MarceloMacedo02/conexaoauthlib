# Epic 12 - Página de Gestão de Realms (Thymeleaf)

## Status
**Estado:** 🔲 Planejado
**Iniciada em:** 2025-12-25
**Responsável:** BMad Team

## Objetivo

Implementar a página de gestão de realms, incluindo a criação de novos DTOs, services e controllers necessários, **reutilizando templates e componentes existentes** (`layouts/vertical`, `partials/page-title`, `partials/modal`, etc.), seguindo o padrão do escopo.md.

## Contexto

**IMPORTANTE:** A página de gestão de realms **já existe parcialmente** no projeto (`src/main/resources/templates/admin/realms/list.html`). Esta Epic foca na **extensão e padronização**:
- Criar DTOs específicos para realms (separados de DTOs de auditoria)
- Criar novos services e controllers seguindo o padrão arquitetural
- Implementar endpoints REST para integração com templates existentes
- Não recriar templates já existentes

---

## Histórias da Epic

| Story | Título | Status |
|--------|---------|--------|
| [Story 01](./stories/epic-12-story-01-dtos-realm-java-records.md) | DTOs Realm (Java Records) | 🔲 Planejado |
| [Story 02](./stories/epic-12-story-02-backend-service-layer.md) | Backend Service Layer | 🔲 Planejado |
| [Story 03](./stories/epic-12-story-03-controller-api-adminrealmcontroller.md) | Controller API (AdminRealmController) | 🔲 Planejado |
| [Story 04](./stories/epic-12-story-04-formulario-criacao-edicao.md) | Formulário Criação/Edição | 🔲 Planejado |
| [Story 05](./stories/epic-12-story-05-modal-criacao-edicao.md) | Modal Criação/Edição | 🔲 Planejado |
| [Story 06](./stories/epic-12-story-06-acoes-bulk-realms.md) | Ações em Lote (Bulk Actions) | 🔲 Planejado |
| [Story 07](./stories/epic-12-story-07-ajustes-finais.md) | Ajustes Finais | 🔲 Planejado |

---

## Requisitos do Escopo (conforme escopo.md)

### Gestão de Realms

**Funcionalidades:**
- ✅ Criar novos realms
- ✅ Editar realms existentes
- ✅ Ativar/desativar realms (soft delete)
- ✅ Visualizar status e métricas básicas
- ✅ Validar unicidade de nome de realm
- ✅ Listagem com paginação
- ✅ Filtros avançados (nome, status)
- ✅ Busca textual
- ✅ Ações em lote (bulk actions)
- ✅ Indicação visual de Realm Master (ícone especial)

**Regras de Realm:**
- Todo usuário pertence a **um único realm**
- Roles são **escopadas por realm**
- Tokens JWT devem conter a claim `realm`
- Cada realm possui:
  - Seu próprio **par de chaves**
  - Seu próprio **JWKS**
- Deve existir um **Realm Master** para administração global

---

## Decisões Arquiteturais

### ADR 12.1: Extensão de Arquitetura

**Decisão:** Criar novos packages para gestão de realms (`admin.domain`, `admin.api.dto`, `admin.api.controller`, `admin.domain.exceptions`) seguindo o padrão arquitetural estabelecido.

**Rationale:**
- Separação clara de responsabilidades por pacote
- Reutilização de estruturas existentes (service patterns, exception handling)
- Manutenibilidade consistente com o restante do projeto

**Consequências:**
- ✅ Código organizado e modular
- ✅ DTOs de realms independentes de DTOs de auditoria
- ✅ Services especializados para cada domínio
- ✅ Controllers focados em uma responsabilidade específica

### ADR 12.2: DTOs Separados

**Decisão:** Não usar DTOs de auditoria para gestão de realms. Criar DTOs específicos (`RealmForm`, `RealmListResponse`, `RealmDetailResponse`).

**Rationale:**
- Responsabilidade Única (Single Responsibility)
- Validações específicas para realm (ex: nome único)
- Métodos auxiliares específicos para realm (ex: statusAtivo)

**Consequências:**
- ✅ Separação clara entre domínios
- ✅ Validações específicas por domínio
- ✅ Manutenibilidade simplificada

### ADR 12.3: Reutilização de Templates

**Decisão:** Reutilizar templates e componentes existentes, não recriar estrutura.

**Rationale:**
- Consistência visual com o restante do dashboard
- Evitar duplicação de código
- Aproveitar experiência de usuário já estabelecida

**Consequências:**
- ✅ Menos código novo a ser mantido
- ✅ Experiência consistente para usuário
- ✅ Implementação mais rápida

---

## Dependências

- ✅ Epic 11 - Dashboard Principal (referência de layout)
- ✅ Epic 6.4 - Gestão de Realms (template parcial existente)
- ✅ Models existentes: `Realm`, `StatusRealm`
- ✅ Templates e fragments: `layouts/vertical`, `partials/page-title`, `partials/modal`

---

## Estrutura de Arquivos

```
src/main/java/br/com/plataforma/conexaodigital/admin/
├── api/
│   ├── controller/
│   │   └── AdminRealmController.java                      ← Story 03
│   ├── dto/
│   │   ├── requests/
│   │   │   └── RealmForm.java                           ← Story 01
│   │   └── responses/
│   │       ├── RealmListResponse.java                ← Story 01
│   │       └── RealmDetailResponse.java            ← Story 01
│   └── domain/
│       ├── service/
│       │   └── AdminRealmService.java                  ← Story 02
│       └── exceptions/
│           └── RealmExceptions.java                  ← Story 01

src/main/resources/templates/admin/realms/
├── list.html           ← Reutilizar template existente (Story 03)
└── form.html           ← Reutilizar modal template existente (Story 05)

templates/
├── layouts/vertical.html      ← Reutilizar
└── partials/
    ├── page-title.html     ← Reutilizar
    └── modal.html          ← Reutilizar
```

---

## Notas de Implementação

### 1. Story 01 - DTOs Realm (Java Records)

**Arquivo:** `src/main/java/.../admin/api/dto/requests/RealmForm.java`

**Conteúdo:**
```java
package br.com.plataforma.conexaodigital.admin.api.dto.requests;

import jakarta.validation.constraints.*;
import br.com.plataforma.conexaodigital.realm.domain.model.StatusRealm;

/**
 * Formulário para criação/edição de realm.
 */
public record RealmForm(
    String id,  // Null para criação

    @NotBlank(message = "{realm.nome.obrigatorio}")
    @Size(min = 3, max = 100, message = "{realm.nome.tamanho}")
    String nome,

    @Size(max = 500, message = "{realm.descricao.tamanho}")
    String descricao,

    StatusRealm status,

    String empresaId,  // Opcional

    String tenantId  // Opcional
) {
    /**
     * Verifica se o realm está ativo.
     */
    public boolean isAtivo() {
        return status == StatusRealm.ATIVO;
    }
}
```

**Arquivo:** `src/main/java/.../admin/api/dto/responses/RealmListResponse.java`

**Conteúdo:**
```java
package br.com.plataforma.conexaodigital.admin.api.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para listagem de realms.
 */
public record RealmListResponse(
    List<RealmListResponse> realms,
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

**Arquivo:** `src/main/java/.../admin/api/dto/responses/RealmListResponse.java`

**Conteúdo:**
```java
package br.com.plataforma.conexaodigital.admin.api.dto.responses;

import java.time.LocalDateTime;

/**
 * DTO para linha da tabela de realms.
 */
public record RealmListResponse(
    String id,
    String nome,
    String descricao,
    String empresaId,
    String tenantId,
    String status,
    boolean master,
    long totalUsuarios,
    int totalRoles,
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao,
    boolean isAtivo
) {
    /**
     * Retorna classe CSS para status.
     */
    public String getStatusBadgeClass() {
        return switch (status) {
            case ATIVO -> "badge bg-success-subtle text-success badge-label";
            case INATIVO -> "badge bg-danger-subtle text-danger badge-label";
            default -> "badge bg-secondary-subtle text-secondary badge-label";
        };
    }

    /**
     * Formata data para exibição.
     */
    public String getDataFormatada() {
        return dataCriacao.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
```

### 2. Story 02 - Backend Service Layer

**Arquivo:** `src/main/java/.../admin/domain/service/AdminRealmService.java`

**Conteúdo:**
```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.dto.requests.RealmForm;
import br.com.plataforma.conexaodigital.admin.api.dto.responses.RealmListResponse;
import br.com.plataforma.conexaodigital.admin.api.dto.responses.RealmListResponse;
import br.com.plataforma.conexaodigital.admin.domain.exceptions.RealmExceptions;
import br.com.plataforma.conexaodigital.realm.domain.model.Realm;
import br.com.plataforma.conexaodigital.realm.domain.model.StatusRealm;
import br.com.plataforma.conexaodigital.realm.domain.repository.RealmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gestão de realms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRealmService {

    private final RealmRepository realmRepository;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    /**
     * Listar realms com paginação e filtros.
     */
    @Transactional(readOnly = true)
    public RealmListResponse listarRealms(int page, int size, String nome, String status) {
        log.debug("Listando realms: page={}, size={}, nome={}, status={}", page, size, nome, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());

        Page<Realm> realmsPage;

        // Aplicar filtros
        if (nome != null && !nome.isBlank()) {
            if (status != null && !status.isBlank()) {
                realmsPage = realmRepository.findByNomeContainingIgnoreCaseAndStatus(
                    nome, StatusRealm.valueOf(status.toUpperCase()), pageable);
            } else {
                realmsPage = realmRepository.findByNomeContainingIgnoreCase(nome, pageable);
            }
        } else if (status != null && !status.isBlank()) {
            realmsPage = realmRepository.findByStatus(StatusRealm.valueOf(status.toUpperCase()), pageable);
        } else {
            realmsPage = realmRepository.findAll(pageable);
        }

        // Mapear para DTOs
        List<RealmListResponse> realmsDto = realmsPage.getContent().stream()
                .map(this::mapearParaListDto)
                .collect(Collectors.toList());

        return new RealmListResponse(
                realmsDto,
                page,
                size,
                realmsPage.getTotalElements(),
                realmsPage.hasNext(),
                page > 0
        );
    }

    /**
     * Mapear entidade para DTO de listagem.
     */
    private RealmListResponse mapearParaListDto(Realm realm) {
        return new RealmListResponse(
                realm.getId().toString(),
                realm.getNome(),
                realm.getDescricao(),
                realm.getEmpresaId(),
                realm.getTenantId(),
                realm.getStatus() != null ? realm.getStatus().name() : null,
                realm.getMaster() != null && realm.getMaster(),
                usuarioRepository.countByRealmIdAndStatusAtivo(realm.getId()),
                (int) roleRepository.countByRealmId(realm.getId()),
                realm.getDataCriacao(),
                realm.getDataUltimaAtualizacao(),
                realm.getStatus() == StatusRealm.ATIVO
        );
    }

    /**
     * Criar novo realm.
     */
    @Transactional
    public Realm criarRealm(RealmForm form) {
        log.info("Criando novo realm: {}", form.nome());

        validarUnicidadeNome(form.nome(), null);

        Realm realm = new Realm();
        realm.setNome(form.nome().toUpperCase()); // Garante uppercase
        realm.setDescricao(form.descricao());
        realm.setStatus(form.status());
        realm.setEmpresaId(form.empresaId());
        realm.setTenantId(form.tenantId());
        realm.setMaster(false);
        realm.setDataCriacao(LocalDateTime.now());
        realm.setDataAtualizacao(LocalDateTime.now());

        Realm savedRealm = realmRepository.save(realm);
        log.info("Realm criado com sucesso: {}", savedRealm.getId());

        return savedRealm;
    }

    /**
     * Atualizar realm existente.
     */
    @Transactional
    public Realm atualizarRealm(String id, RealmForm form) {
        log.info("Atualizando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        // Validar alterações no Realm Master
        if (realm.getMaster() != null && realm.getMaster()) {
            if (!realm.getNome().equals("Master")) {
                throw new RealmMasterNaoPodeSerEditadoException("Nome do Realm Master não pode ser alterado");
            }
        }

        validarUnicidadeNome(form.nome(), id);

        realm.setNome(form.nome().toUpperCase());
        realm.setDescricao(form.descricao());
        realm.setEmpresaId(form.empresaId());
        realm.setTenantId(form.tenantId());
        realm.setDataAtualizacao(LocalDateTime.now());

        Realm updatedRealm = realmRepository.save(realm);
        log.info("Realm atualizado com sucesso: {}", updatedRealm.getId());

        return updatedRealm;
    }

    /**
     * Ativar realm.
     */
    @Transactional
    public Realm ativarRealm(String id) {
        log.info("Ativando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        if (realm.isAtivo()) {
            log.warn("Realm já está ativo: {}", id);
            return realm;
        }

        realm.setStatus(StatusRealm.ATIVO);
        realm.setDataReativacao(LocalDateTime.now());
        realm.setDataAtualizacao(LocalDateTime.now());

        Realm activatedRealm = realmRepository.save(realm);
        log.info("Realm ativado com sucesso: {}", activatedRealm.getId());

        return activatedRealm;
    }

    /**
     * Desativar realm (soft delete).
     */
    @Transactional
    public Realm desativarRealm(String id) {
        log.info("Desativando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        if (realm.getMaster() != null && realm.getMaster()) {
            throw new RealmMasterNaoPodeSerEditadoException("Realm Master não pode ser desativado");
        }

        if (realm.isInativo()) {
            log.warn("Realm já está inativo: {}", id);
            return realm;
        }

        realm.setStatus(StatusRealm.INATIVO);
        realm.setDataDesativacao(LocalDateTime.now());
        realm.setDataAtualizacao(LocalDateTime.now());

        Realm deactivatedRealm = realmRepository.save(realm);
        log.info("Realm desativado com sucesso: {}", deactivatedRealm.getId());

        return deactivatedRealm;
    }

    /**
     * Valida unicidade de nome de realm.
     */
    private void validarUnicidadeNome(String nome, String id) {
        realmRepository.findByNomeIgnoreCase(nome).ifPresent(realm -> {
            // Excluir o próprio realm se estiver editando
            if (id != null && !realm.getId().toString().equals(id)) {
                throw new NomeRealmJaExisteException("Nome de realm já existe: " + nome);
            }
        });
    }

    /**
     * Mapear status para String.
     */
    private String statusToString(StatusRealm status) {
        return status != null ? status.name() : null;
    }
}
```

---

## Critérios de Aceitação da Epic

- [ ] Todos os 7 stories implementados
- [ ] DTOs Realm criados (Form, ListResponse, ListRow, DetailResponse)
- [ ] Service Layer implementado com todos os métodos CRUD
- [ ] Controller API implementado com endpoints REST
- [ ] Formulário de criação/edição funcionando
- [ ] Modal de criação/edição funcionando
- [ ] Ações em lote (ativar/desativar)
- [ ] Validação de unicidade de nome
- [ ] Proteção de Realm Master (não pode ser editado/desativado)
- [ ] Reutilização de templates existentes
- [ ] Estrutura de arquivos consistente com arquitetura

---

## Links Relacionados

- [Escopo do Projeto](../escopo.md)
- [Epic 11 - Dashboard Principal](../epic-11-dashboard-principal-metricas.md)
- [Epic 6.4 - Gestão de Realms](../epic-6-4-gestao-realms.md)
- [Models existentes: Realm, StatusRealm]

---

**Última Atualização:** 2025-12-25
**Responsável:** BMad Team
