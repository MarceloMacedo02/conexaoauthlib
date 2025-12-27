# User Story: Backend Service Layer

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-03

## Descrição
Criar a camada de serviço administrativo (`AdminRealmService`) que fornece métodos para listagem, criação, edição, ativação, desativação e validação de realms, incluindo métricas adicionais como contagem de usuários e chaves por realm.

## Critérios de Aceite
- [X] Interface `AdminRealmService` criada
- [X] Implementação `AdminRealmServiceImpl` criada
- [X] Método `listarRealms(page, size, nome, status)` implementado com paginação
- [X] Método `buscarPorId(id)` implementado para obter detalhes
- [X] Método `criarRealm(form)` implementado com validações
- [X] Método `atualizarRealm(form)` implementado com validações
- [X] Método `ativarRealm(id)` implementado para reativação
- [X] Método `desativarRealm(id)` implementado para desativação
- [X] Método `validarUnicidadeNome(nome)` implementado
- [X] Proteção do Realm Master implementada (não pode ser desativado/nome alterado)
- [X] Métricas de usuários e chaves por realm calculadas
- [X] Tratamento de exceções apropriado

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.domain.service`
2. Criar interface `AdminRealmService`
3. Criar classe `AdminRealmServiceImpl`
4. Implementar métodos de listagem com paginação e filtros
5. Implementar métodos de criação e atualização
6. Implementar métodos de ativação e desativação
7. Implementar métodos auxiliares (validação de unicidade)
8. Injetar repositories necessários (RealmRepository, UsuarioRepository, ChaveRepository)
9. Implementar proteção do Realm Master
10. Criar exceções específicas (RealmMasterNaoPodeSerEditadoException)

## Instruções de Implementação

### Package Structure
```
br.com.plataforma.conexaodigital.admin.domain
├── service
│   ├── AdminRealmService.java (interface)
│   └── AdminRealmServiceImpl.java (implementação)
└── exceptions
    └── RealmMasterNaoPodeSerEditadoException.java
```

### Interface: AdminRealmService
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/AdminRealmService.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.requests.RealmForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmListResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Serviço administrativo para gestão de realms.
 * Fornece métodos para CRUD e métricas para a interface web.
 */
public interface AdminRealmService {

    /**
     * Lista realms com paginação e filtros opcionais.
     * @param page número da página (0-indexed)
     * @param size tamanho da página
     * @param nome filtro por nome (busca parcial, opcional)
     * @param status filtro por status (ATIVO/INATIVO, opcional)
     * @return página de RealmListResponse com métricas
     */
    Page<RealmListResponse> listarRealms(int page, int size, String nome, String status);

    /**
     * Busca detalhes completos de um realm por ID.
     * @param id UUID do realm
     * @return RealmDetailResponse com todas as informações
     */
    RealmDetailResponse buscarPorId(String id);

    /**
     * Cria um novo realm.
     * @param form dados do novo realm
     * @return Realm criado
     */
    br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm criarRealm(RealmForm form);

    /**
     * Atualiza um realm existente.
     * @param id UUID do realm
     * @param form dados atualizados
     * @return Realm atualizado
     */
    br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm atualizarRealm(String id, RealmForm form);

    /**
     * Ativa um realm inativo.
     * @param id UUID do realm
     * @return Realm ativado
     */
    br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm ativarRealm(String id);

    /**
     * Desativa um realm (soft delete).
     * @param id UUID do realm
     * @return Realm desativado
     */
    br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm desativarRealm(String id);

    /**
     * Valida unicidade de nome (ignorando próprio ID se edição).
     * @param nome nome a validar
     * @param id ID do realm sendo editado (null para criação)
     * @throws RealmJaExisteException se nome já existe
     */
    void validarUnicidadeNome(String nome, String id);

    /**
     * Conta total de usuários por realm.
     * @param realmId UUID do realm
     * @return total de usuários
     */
    Long contarUsuariosPorRealm(String realmId);

    /**
     * Conta usuários ativos por realm.
     * @param realmId UUID do realm
     * @return total de usuários ativos
     */

    /**
     * Conta usuários bloqueados por realm.
     * @param realmId UUID do realm
     * @return total de usuários bloqueados
     */
    Long contarUsuariosBloqueadosPorRealm(String realmId);

    /**
     * Conta chaves ativas por realm.
     * @param realmId UUID do realm
     * @return total de chaves ativas
     */
    Long contarChavesAtivasPorRealm(String realmId);

    /**
     * Conta chaves inativas por realm.
     * @param realmId UUID do realm
     * @return total de chaves inativas
     */
    Long contarChavesInativasPorRealm(String realmId);

    /**
     * Lista nomes de roles por realm.
     * @param realmId UUID do realm
     * @return lista de nomes de roles
     */
    List<String> listarRolesPorRealm(String realmId);
}
```

### Implementação: AdminRealmServiceImpl
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/AdminRealmServiceImpl.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.requests.RealmForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmListResponse;
import br.com.plataforma.conexaodigital.admin.domain.exceptions.RealmMasterNaoPodeSerEditadoException;
import br.com.plataforma.conexaodigital.gestaorealm.domain.exceptions.NomeRealmJaExisteException;
import br.com.plataforma.conexaodigital.gestaorealm.domain.exceptions.RealmNotFoundException;
import br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm;
import br.com.plataforma.conexaodigital.gestaorealm.domain.model.StatusRealm;
import br.com.plataforma.conexaodigital.gestaorealm.domain.repository.RealmRepository;
import br.com.plataforma.conexaodigital.gestaousuarios.domain.repository.UsuarioRepository;
import br.com.plataforma.conexaodigital.gestaochaves.domain.repository.ChaveCriptograficaRepository;
import br.com.plataforma.conexaodigital.gestaroles.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRealmServiceImpl implements AdminRealmService {

    private final RealmRepository realmRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChaveCriptograficaRepository chaveRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<RealmListResponse> listarRealms(int page, int size, String nome, String status) {
        log.info("Listando realms: page={}, size={}, nome={}, status={}", page, size, nome, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());

        // Filtrar por nome e status se fornecidos
        Page<Realm> realmsPage;
        if (nome != null && !nome.isBlank() && status != null && !status.isBlank()) {
            realmsPage = realmRepository.findByNomeContainingIgnoreCaseAndStatus(
                nome,
                Status.valueOf(status.toUpperCase()),
                pageable
            );
        } else if (nome != null && !nome.isBlank()) {
            realmsPage = realmRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (status != null && !status.isBlank()) {
            realmsPage = realmRepository.findByStatus(StatusRealm.valueOf(status.toUpperCase()), pageable);
        } else {
            realmsPage = realmRepository.findAll(pageable);
        }

        // Mapear para DTOs com métricas
        List<RealmListResponse> responses = realmsPage.getContent().stream()
            .map(realm -> {
                Long totalUsuarios = contarUsuariosPorRealm(realm.getId().toString());
                Long chavesAtivas = contarChavesAtivasPorRealm(realm.getId().toString());
                return RealmListResponse.from(realm, totalUsuarios, chavesAtivas);
            })
            .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, realmsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public RealmDetailResponse buscarPorId(String id) {
        log.info("Buscando realm por ID: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        Long totalUsuarios = contarUsuariosPorRealm(id);
        Long usuariosAtivos = contarUsuariosAtivosPorRealm(id);
        Long usuariosBloqueados = contarUsuariosBloqueadosPorRealm(id);
        Long chavesAtivas = contarChavesAtivasPorRealm(id);
        Long chavesInativas = contarChavesInativasPorRealm(id);
        List<String> roles = listarRolesPorRealm(id);

        return RealmDetailResponse.from(
            realm, totalUsuarios, usuariosAtivos, usuariosBloqueados,
            chavesAtivas, chavesInativas, roles
        );
    }

    @Override
    @Transactional
    public Realm criarRealm(RealmForm form) {
        log.info("Criando novo realm: {}", form.nome());

        // Validar unicidade de nome
        validarUnicidadeNome(form.nome(), null);

        // Criar nova entidade
        Realm realm = new Realm();
        realm.setNome(form.nome());
        realm.setDescricao(form.descricao());
        realm.setStatus(form.ativo() ? StatusRealm.ATIVO : StatusRealm.INATIVO);
        realm.setEmpresaId(form.empresaId());
        realm.setDataCriacao(java.time.LocalDateTime.now());
        realm.setDataAtualizacao(java.time.LocalDateTime.now());

        Realm savedRealm = realmRepository.save(realm);
        log.info("Realm criado com sucesso: {}", savedRealm.getId());

        return savedRealm;
    }

    @Override
    @Transactional
    public Realm atualizarRealm(String id, RealmForm form) {
        log.info("Atualizando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        // Proteção do Realm Master
        if (realm.isMaster()) {
            // Verificar se tentou alterar o nome
            if (!realm.getNome().equals(form.nome())) {
                throw new RealmMasterNaoPodeSerEditadoException("Nome do Realm Master não pode ser alterado");
            }
        }

        // Validar unicidade de nome (ignorando próprio ID)
        validarUnicidadeNome(form.nome(), id);

        realm.setNome(form.nome());
        realm.setDescricao(form.descricao());
        realm.setStatus(form.ativo() ? StatusRealm.ATIVO : StatusRealm.INATIVO);
        realm.setEmpresaId(form.empresaId());
        realm.setDataAtualizacao(java.time.LocalDateTime.now());

        Realm updatedRealm = realmRepository.save(realm);
        log.info("Realm atualizado com sucesso: {}", updatedRealm.getId());

        return updatedRealm;
    }

    @Override
    @Transactional
    public Realm ativarRealm(String id) {
        log.info("Ativando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        if (realm.getStatus() == StatusRealm.ATIVO) {
            log.warn("Realm já está ativo: {}", id);
            return realm;
        }

        realm.setStatus(StatusRealm.ATIVO);
        realm.setDataReativacao(java.time.LocalDateTime.now());
        realm.setDataAtualizacao(java.time.LocalDateTime.now());

        Realm activatedRealm = realmRepository.save(realm);
        log.info("Realm ativado com sucesso: {}", activatedRealm.getId());

        return activatedRealm;
    }

    @Override
    @Transactional
    public Realm desativarRealm(String id) {
        log.info("Desativando realm: {}", id);

        Realm realm = realmRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RealmNotFoundException("Realm não encontrado: " + id));

        // Proteção do Realm Master
        if (realm.isMaster()) {
            throw new RealmMasterNaoPodeSerEditadoException("Realm Master não pode ser desativado");
        }

        if (realm.getStatus() == StatusRealm.INATIVO) {
            log.warn("Realm já está inativo: {}", id);
            return realm;
        }

        realm.setStatus(StatusRealm.INATIVO);
        realm.setDataDesativacao(java.time.LocalDateTime.now());
        realm.setDataAtualizacao(java.time.LocalDateTime.now());

        Realm deactivatedRealm = realmRepository.save(realm);
        log.info("Realm desativado com sucesso: {}", deactivatedRealm.getId());

        return deactivatedRealm;
    }

    @Override
    public void validarUnicidadeNome(String nome, String id) {
        // Verificar se já existe realm com mesmo nome
        realmRepository.findByNome(nome).ifPresent(realm -> {
            // Se estiver editando, permitir manter o mesmo nome
            if (id == null || !realm.getId().toString().equals(id)) {
                throw new NomeRealmJaExisteException("Nome de realm já existe: " + nome);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarUsuariosPorRealm(String realmId) {
        return usuarioRepository.countByRealmId(UUID.fromString(realmId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarUsuariosAtivosPorRealm(String realmId) {
        return usuarioRepository.countByRealmIdAndAtivoTrue(UUID.fromString(realmId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarUsuariosBloqueadosPorRealm(String realmId) {
        return usuarioRepository.countByRealmIdAndBloqueadoTrue(UUID.fromString(realmId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarChavesAtivasPorRealm(String realmId) {
        return chaveRepository.countByRealmIdAndAtivaTrue(UUID.fromString(realmId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarChavesInativasPorRealm(String realmId) {
        return chaveRepository.countByRealmIdAndAtivaFalse(UUID.fromString(realmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarRolesPorRealm(String realmId) {
        return roleRepository.findByRealmId(UUID.fromString(realmId))
            .stream()
            .map(role -> role.getNome())
            .collect(Collectors.toList());
    }
}
```

### Exceção: RealmMasterNaoPodeSerEditadoException
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/exceptions/RealmMasterNaoPodeSerEditadoException.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.exceptions;

/**
 * Exceção lançada quando tentativa de editar Realm Master.
 */
public class RealmMasterNaoPodeSerEditadoException extends RuntimeException {

    public RealmMasterNaoPodeSerEditadoException(String message) {
        super(message);
    }

    public RealmMasterNaoPodeSerEditadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Repository Methods Necessários
Adicionar os seguintes métodos aos repositories existentes:

**UsuarioRepository:**
```java
Long countByRealmId(UUID realmId);
Long countByRealmIdAndAtivoTrue(UUID realmId);
Long countByRealmIdAndBloqueadoTrue(UUID realmId);
```

**ChaveCriptograficaRepository:**
```java
Long countByRealmIdAndAtivaTrue(UUID realmId);
Long countByRealmIdAndAtivaFalse(UUID realmId);
```

**RoleRepository:**
```java
List<Role> findByRealmId(UUID realmId);
```

## Checklist de Validação
- [ ] Package `admin/domain/service` criado
- [ ] Package `admin/domain/exceptions` criado
- [ ] Interface `AdminRealmService` criada
- [ ] Classe `AdminRealmServiceImpl` criada
- [ ] Método `listarRealms` implementado com paginação e filtros
- [ ] Método `buscarPorId` implementado
- [ ] Método `criarRealm` implementado
- [ ] Método `atualizarRealm` implementado
- [ ] Método `ativarRealm` implementado
- [ ] Método `desativarRealm` implementado
- [ ] Método `validarUnicidadeNome` implementado
- [ ] Métodos de contagem implementados (usuários, chaves)
- [ ] Método `listarRolesPorRealm` implementado
- [ ] Proteção do Realm Master implementada
- [ ] Exceção `RealmMasterNaoPodeSerEditadoException` criada
- [ ] Repository methods adicionados
- [ ] Logging em todos os métodos
- [ ] `@Transactional` annotations aplicadas
- [ ] Testes unitários criados

## Anotações
- Service layer deve ser testável e independente da camada de controller
- Todas as operações de escrita devem ser transacionais (`@Transactional`)
- Logs devem incluir informações relevantes para debugging
- Proteção do Realm Master é crítica para integridade do sistema
- Métricas são calculadas on-demand (pode ser cacheado futuramente se necessário)
- Métodos de contagem usam repositories específicos para performance

## Dependências
- Epic 1 (Gestão de Realms) - RealmRepository, Realm entidade
- Epic 2 (Gestão de Usuários) - UsuarioRepository
- Epic 3 (Gestão de Roles) - RoleRepository
- Epic 5 (Gestão de Chaves) - ChaveCriptograficaRepository

## Prioridade
**Alta** - Serviço necessário para controller funcionar

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
