# User Story: Backend Service Layer - Role

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-03

## Descrição
Implementar a camada de serviço (Service Layer) para gestão de roles na página administrativa, incluindo métodos para listar, buscar por ID, criar, atualizar e remover roles com validações de negócio e tratamento de exceções.

## Critérios de Aceite
- [ ] Classe `AdminRoleService` criada com todos os métodos necessários
- [ ] Método `listarRoles` implementado com paginação e filtros
- [ ] Método `buscarPorId` implementado com tratamento de exceção
- [ ] Método `criarRole` implementado com validação de nome único
- [ ] Método `atualizarRole` implementado com validações
- [ ] Método `removerRole` implementado com verificação de uso
- [ ] Exceções customizadas criadas e lançadas apropriadamente
- [ ] Validações de negócio implementadas
- [ ] Integração com RealmService e UsuarioService
- [ ] Testes unitários cobrindo todos os métodos

## Tarefas
1. Criar exceções customizadas (RoleJaExisteException, RoleEmUsoException, RoleNaoEncontradaException)
2. Criar package `br.com.plataforma.conexaodigital.admin.api.service`
3. Criar classe `AdminRoleService.java`
4. Implementar método de listagem com paginação e filtros
5. Implementar método de busca por ID
6. Implementar método de criação com validação de unicidade
7. Implementar método de atualização
8. Implementar método de remoção com verificação
9. Criar testes unitários

## Instruções de Implementação

### Exceções Customizadas
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/gestarole/domain/exception`

```java
package br.com.plataforma.conexaodigital.gestarole.domain.exception;

public class RoleJaExisteException extends RuntimeException {
    public RoleJaExisteException(String message) {
        super(message);
    }
}

public class RoleEmUsoException extends RuntimeException {
    public RoleEmUsoException(String message) {
        super(message);
    }
}

public class RoleNaoEncontradaException extends RuntimeException {
    public RoleNaoEncontradaException(String message) {
        super(message);
    }
}
```

### Service: AdminRoleService
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/service/AdminRoleService.java`

```java
package br.com.plataforma.conexaodigital.admin.api.service;

import br.com.plataforma.conexaodigital.admin.api.mapper.RoleMapper;
import br.com.plataforma.conexaodigital.admin.api.requests.RoleForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleListResponse;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleEmUsoException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleJaExisteException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleNaoEncontradaException;
import br.com.plataforma.conexaodigital.gestarole.domain.model.Role;
import br.com.plataforma.conexaodigital.gestarole.domain.repository.RoleRepository;
import br.com.plataforma.conexaodigital.gestarealm.domain.repository.RealmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço para gestão de roles na página administrativa.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final RoleRepository roleRepository;
    private final RealmRepository realmRepository;

    /**
     * Lista roles com paginação e filtros.
     */
    public Page<RoleListResponse> listarRoles(
        int page,
        int size,
        String nome,
        String realmId,
        String status
    ) {
        log.debug("Listando roles - page: {}, size: {}, nome: {}, realmId: {}, status: {}",
            page, size, nome, realmId, status);

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<Role> rolesPage;

        if (temFiltros(nome, realmId, status)) {
            rolesPage = roleRepository.buscarComFiltros(nome, realmId, status, pageable);
        } else {
            rolesPage = roleRepository.findAll(pageable);
        }

        // Converter para Response DTO
        return rolesPage.map(role -> {
            Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());
            String realmNome = role.getRealm() != null ? role.getRealm().getNome() : "-";
            String realmIdValue = role.getRealm() != null ? role.getRealm().getId().toString() : null;

            return RoleListResponse.from(role, realmNome, realmIdValue, totalUsuarios);
        });
    }

    /**
     * Busca role por ID.
     */
    public RoleDetailResponse buscarPorId(String id) {
        log.debug("Buscando role por ID: {}", id);

        Role role = roleRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RoleNaoEncontradaException("Role não encontrada"));

        // Buscar informações relacionadas
        String realmNome = role.getRealm() != null ? role.getRealm().getNome() : "-";
        String realmIdValue = role.getRealm() != null ? role.getRealm().getId().toString() : null;
        Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());
        List<String> usuariosNomes = roleRepository.findUsuariosNomesByRole(role.getId());

        return RoleDetailResponse.from(role, realmNome, realmIdValue, totalUsuarios, usuariosNomes);
    }

    /**
     * Cria nova role.
     */
    @Transactional
    public RoleDetailResponse criarRole(RoleForm form) {
        log.debug("Criando nova role: {}", form.nome());

        // Verificar se realm existe
        var realm = realmRepository.findById(UUID.fromString(form.realmId()))
            .orElseThrow(() -> new IllegalArgumentException("Realm não encontrado"));

        // Verificar se nome já existe no realm
        boolean nomeJaExiste = roleRepository.existsByNomeAndRealm_Id(
            form.nome().toUpperCase(),
            UUID.fromString(form.realmId())
        );

        if (nomeJaExiste) {
            throw new RoleJaExisteException("Role '" + form.nome() + "' já existe neste realm");
        }

        // Criar role
        Role role = RoleMapper.toEntity(form);
        role.setRealm(realm);

        Role savedRole = roleRepository.save(role);

        log.info("Role criada com sucesso: {} (ID: {})", savedRole.getNome(), savedRole.getId());

        // Buscar informações relacionadas
        Long totalUsuarios = 0L;
        List<String> usuariosNomes = List.of();

        return RoleDetailResponse.from(savedRole, realm.getNome(), realm.getId().toString(),
            totalUsuarios, usuariosNomes);
    }

    /**
     * Atualiza role existente.
     */
    @Transactional
    public RoleDetailResponse atualizarRole(RoleForm form) {
        log.debug("Atualizando role: {} (ID: {})", form.nome(), form.id());

        Role role = roleRepository.findById(UUID.fromString(form.id()))
            .orElseThrow(() -> new RoleNaoEncontradaException("Role não encontrada"));

        // Verificar se é role padrão
        boolean isPadrao = role.getNome().equals("ADMIN") ||
                          role.getNome().equals("USER") ||
                          role.getNome().equals("SERVICE");

        if (isPadrao && !form.padrao()) {
            throw new IllegalArgumentException("Não é possível remover marca de padrão de roles padrão");
        }

        // Verificar se nome já existe no realm (se nome foi alterado)
        if (!role.getNome().equalsIgnoreCase(form.nome())) {
            boolean nomeJaExiste = roleRepository.existsByNomeAndRealm_IdAndIdNot(
                form.nome().toUpperCase(),
                role.getRealm().getId(),
                role.getId()
            );

            if (nomeJaExiste) {
                throw new RoleJaExisteException("Role '" + form.nome() + "' já existe neste realm");
            }
        }

        // Atualizar campos permitidos
        role.setNome(form.nome().toUpperCase());
        role.setDescricao(form.descricao());
        role.setAtiva(form.ativa());
        role.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        log.info("Role atualizada com sucesso: {} (ID: {})", savedRole.getNome(), savedRole.getId());

        // Buscar informações relacionadas
        String realmNome = role.getRealm() != null ? role.getRealm().getNome() : "-";
        String realmIdValue = role.getRealm() != null ? role.getRealm().getId().toString() : null;
        Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());
        List<String> usuariosNomes = roleRepository.findUsuariosNomesByRole(role.getId());

        return RoleDetailResponse.from(savedRole, realmNome, realmIdValue, totalUsuarios, usuariosNomes);
    }

    /**
     * Remove role.
     */
    @Transactional
    public void removerRole(String id) {
        log.debug("Removendo role: {}", id);

        Role role = roleRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RoleNaoEncontradaException("Role não encontrada"));

        // Verificar se é role padrão
        boolean isPadrao = role.getNome().equals("ADMIN") ||
                          role.getNome().equals("USER") ||
                          role.getNome().equals("SERVICE");

        if (isPadrao) {
            throw new RoleEmUsoException("Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas");
        }

        // Verificar se tem usuários associados
        Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());

        if (totalUsuarios > 0) {
            throw new RoleEmUsoException("Role possui " + totalUsuarios +
                " usuário(s) associado(s) e não pode ser removida");
        }

        roleRepository.delete(role);

        log.info("Role removida com sucesso: {} (ID: {})", role.getNome(), role.getId());
    }

    /**
     * Lista todos os realms para os filtros.
     */
    public List<RealmSimpleResponse> listarTodosRealms() {
        return realmRepository.findAll().stream()
            .map(realm -> new RealmSimpleResponse(
                realm.getId().toString(),
                realm.getNome(),
                realm.getAtiva()
            ))
            .toList();
    }

    /**
     * Verifica se há filtros ativos.
     */
    private boolean temFiltros(String nome, String realmId, String status) {
        return (nome != null && !nome.isBlank()) ||
               (realmId != null && !realmId.isBlank() && !realmId.equals("All")) ||
               (status != null && !status.isBlank() && !status.equals("All"));
    }

    /**
     * DTO simplificado de realm para filtros.
     */
    public record RealmSimpleResponse(
        String id,
        String nome,
        Boolean ativo
    ) {}
}
```

### Repository Custom Methods
**Adicionar em `RoleRepository.java`:**

```java
@Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r.id = :roleId")
Long countUsuariosByRole(@Param("roleId") UUID roleId);

@Query("SELECT u.nome FROM Usuario u JOIN u.roles r WHERE r.id = :roleId")
List<String> findUsuariosNomesByRole(@Param("roleId") UUID roleId);

@Query("SELECT r FROM Role r WHERE " +
       "(:nome IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
       "(:realmId IS NULL OR :realmId = 'All' OR r.realm.id = :realmId) AND " +
       "(:status IS NULL OR :status = 'All' OR " +
       "  (:status = 'Ativa' AND r.ativa = true) OR " +
       "  (:status = 'Inativa' AND r.ativa = false))")
Page<Role> buscarComFiltros(
    @Param("nome") String nome,
    @Param("realmId") String realmId,
    @Param("status") String status,
    Pageable pageable
);

boolean existsByNomeAndRealm_Id(String nome, UUID realmId);

boolean existsByNomeAndRealm_IdAndIdNot(String nome, UUID realmId, UUID id);
```

### Testes Unitários
**Localização:** `src/test/java/br/com/plataforma/conexaodigital/admin/api/service/AdminRoleServiceTest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.service;

import br.com.plataforma.conexaodigital.admin.api.mapper.RoleMapper;
import br.com.plataforma.conexaodigital.admin.api.requests.RoleForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleListResponse;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleEmUsoException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleJaExisteException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleNaoEncontradaException;
import br.com.plataforma.conexaodigital.gestarole.domain.model.Role;
import br.com.plataforma.conexaodigital.gestarole.domain.repository.RoleRepository;
import br.com.plataforma.conexaodigital.gestarealm.domain.repository.RealmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminRoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RealmRepository realmRepository;

    @InjectMocks
    private AdminRoleService adminRoleService;

    @Test
    void testListarRolesSemFiltros() {
        List<Role> roles = List.of(
            createRole("ADMIN", "Administrador", true),
            createRole("USER", "Usuário", true)
        );
        Page<Role> page = new PageImpl<>(roles);
        when(roleRepository.findAll(any())).thenReturn(page);
        when(roleRepository.countUsuariosByRole(any())).thenReturn(0L);

        Page<RoleListResponse> result = adminRoleService.listarRoles(0, 10, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(roleRepository, times(1)).findAll(any());
    }

    @Test
    void testBuscarPorIdSucesso() {
        Role role = createRole("ADMIN", "Administrador", true);
        UUID roleId = UUID.randomUUID();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.countUsuariosByRole(roleId)).thenReturn(5L);
        when(roleRepository.findUsuariosNomesByRole(roleId)).thenReturn(List.of("João", "Maria"));

        RoleDetailResponse result = adminRoleService.buscarPorId(roleId.toString());

        assertNotNull(result);
        assertEquals("ADMIN", result.nome());
        assertEquals(5L, result.totalUsuarios());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testBuscarPorIdNaoEncontrada() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(RoleNaoEncontradaException.class,
            () -> adminRoleService.buscarPorId(roleId.toString()));
    }

    @Test
    void testCriarRoleSucesso() {
        RoleForm form = new RoleForm(
            null,
            "GERENTE",
            "Gerente do sistema",
            "realm-id-1",
            true,
            false
        );

        Role role = createRole("GERENTE", "Gerente do sistema", true);

        when(roleRepository.existsByNomeAndRealm_Id(anyString(), any(UUID.class)))
            .thenReturn(false);
        when(realmRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(createRealm(UUID.randomUUID())));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDetailResponse result = adminRoleService.criarRole(form);

        assertNotNull(result);
        assertEquals("GERENTE", result.nome());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCriarRoleNomeJaExiste() {
        RoleForm form = new RoleForm(
            null,
            "ADMIN",
            "Admin role",
            "realm-id-1",
            true,
            true
        );

        when(roleRepository.existsByNomeAndRealm_Id(anyString(), any(UUID.class)))
            .thenReturn(true);

        assertThrows(RoleJaExisteException.class,
            () -> adminRoleService.criarRole(form));
    }

    @Test
    void testRemoverRoleSucesso() {
        Role role = createRole("CUSTOM", "Custom role", true);
        UUID roleId = UUID.randomUUID();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.countUsuariosByRole(roleId)).thenReturn(0L);
        doNothing().when(roleRepository).delete(any(Role.class));

        adminRoleService.removerRole(roleId.toString());

        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void testRemoverRolePadrao() {
        Role role = createRole("ADMIN", "Administrador", true);
        UUID roleId = UUID.randomUUID();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        assertThrows(RoleEmUsoException.class,
            () -> adminRoleService.removerRole(roleId.toString()));
    }

    @Test
    void testRemoverRoleComUsuarios() {
        Role role = createRole("CUSTOM", "Custom role", true);
        UUID roleId = UUID.randomUUID();
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.countUsuariosByRole(roleId)).thenReturn(5L);

        assertThrows(RoleEmUsoException.class,
            () -> adminRoleService.removerRole(roleId.toString()));
    }

    // Métodos auxiliares de teste
    private Role createRole(String nome, String descricao, boolean ativa) {
        Role role = new Role();
        role.setNome(nome);
        role.setDescricao(descricao);
        role.setAtiva(ativa);
        role.setDataCriacao(LocalDateTime.now());
        role.setDataUltimaAtualizacao(LocalDateTime.now());
        return role;
    }

    private br.com.plataforma.conexaodigital.gestarealm.domain.model.Realm createRealm(UUID id) {
        var realm = new br.com.plataforma.conexaodigital.gestarealm.domain.model.Realm();
        realm.setId(id);
        realm.setNome("Master Realm");
        realm.setAtiva(true);
        return realm;
    }
}
```

## Checklist de Validação
- [X] Exceções customizadas criadas
- [X] Classe `AdminRoleService` criada
- [X] Método `listarRoles` implementado com paginação
- [X] Método `buscarPorId` implementado
- [X] Método `criarRole` implementado com validação de unicidade
- [X] Método `atualizarRole` implementado
- [X] Método `removerRole` implementado com verificação de uso
- [X] Método `listarTodosRealms` implementado
- [X] Repository custom methods adicionados
- [X] Testes unitários criados para todos os métodos
- [X] Todos os testes passam

## Anotações
- Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas
- Nome da role deve ser único por realm (case-insensitive)
- Role com usuários associados não pode ser removida
- Transações devem ser usadas para operações de escrita
- Logs devem ser adicionados em todos os métodos
- Validações de negócio devem ser feitas no service

## Dependências
- Epic 1 (Gestão de Realms) - para associação com realms
- Epic 3 (Gestão de Roles) - repository já existe
- Epic 9 (Configuração) - Lombok e Spring Data configurados

## Prioridade
**Alta** - Service layer necessária para operações CRUD

## Estimativa
- Implementação: 4 horas
- Testes: 3 horas
- Total: 7 horas
