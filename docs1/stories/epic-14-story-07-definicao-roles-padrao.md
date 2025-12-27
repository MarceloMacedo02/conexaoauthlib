# User Story: Definição de Roles Padrão

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-07

## Descrição
Implementar a lógica de identificação e tratamento especial para roles padrão (ADMIN, USER, SERVICE), incluindo visualização diferenciada na UI, impedimento de remoção e criação automática durante o bootstrap do sistema.

## Critérios de Aceite
- [ ] Roles padrão (ADMIN, USER, SERVICE) identificadas corretamente
- [ ] Badge visual "Padrão" exibido para roles padrão
- [ ] Ícone de estrela exibido para roles padrão
- [ ] Roles padrão não podem ser removidas
- [ ] Checkbox de padrão desabilitado na edição de roles padrão
- [ ] Criação automática de roles padrão no bootstrap
- [ ] Validação de que roles padrão sempre existem
- [ ] Mensagens de erro claras ao tentar remover role padrão

## Tarefas
1. Implementar método `isRolePadrao()` nos DTOs
2. Adicionar badge visual "Padrão" na listagem
3. Adicionar ícone de estrela para roles padrão
4. Impedir remoção de roles padrão no service
5. Desabilitar checkbox de padrão na edição
6. Implementar criação automática de roles padrão no bootstrap
7. Adicionar validação de existência de roles padrão
8. Testar todas as regras

## Instruções de Implementação

### Atualizar RoleListResponse
**Adicionar método auxiliar:**

```java
/**
 * Verifica se a role é uma role padrão do sistema.
 */
public boolean isRolePadrao() {
    return "ADMIN".equals(nome) || "USER".equals(nome) || "SERVICE".equals(nome);
}

/**
 * Retorna a classe CSS para o badge de padrão.
 */
public String getBadgePadraoClass() {
    if (isRolePadrao()) {
        return "bg-primary";
    }
    return "d-none"; // não exibir se não for padrão
}
```

### Atualizar Template de Listagem
**Adicionar badge "Padrão" na tabela:**

```html
<td>
    <span th:if="${role.isRolePadrao()}"
          class="badge bg-primary me-2">
        <i class="ti ti-star me-1"></i>Padrão
    </span>
    <span class="fw-medium" th:text="${role.nome}"></span>
</td>
```

### Atualizar AdminRoleService
**Adicionar método para buscar/criar roles padrão:**

```java
/**
 * Busca ou cria as roles padrão do sistema (ADMIN, USER, SERVICE).
 */
@Transactional
public List<RoleDetailResponse> buscarOuCriarRolesPadrao(String realmId) {
    log.debug("Buscando ou criando roles padrão para realm: {}", realmId);

    List<Role> rolesPadrao = new ArrayList<>();
    List<String> nomesPadrao = List.of("ADMIN", "USER", "SERVICE");
    List<String> descricoesPadrao = List.of(
        "Administrador do sistema com acesso total",
        "Usuário padrão com acesso limitado",
        "Role para serviços e integrações"
    );

    var realm = realmRepository.findById(UUID.fromString(realmId))
        .orElseThrow(() -> new IllegalArgumentException("Realm não encontrado"));

    for (int i = 0; i < nomesPadrao.size(); i++) {
        String nome = nomesPadrao.get(i);
        String descricao = descricoesPadrao.get(i);

        // Buscar ou criar role padrão
        Optional<Role> roleOpt = roleRepository.findByNomeAndRealm_Id(nome, realm.getId());

        Role role;
        if (roleOpt.isPresent()) {
            role = roleOpt.get();
            log.debug("Role padrão já existe: {}", nome);
        } else {
            role = new Role();
            role.setNome(nome);
            role.setDescricao(descricao);
            role.setAtiva(true);
            role.setPadrao(true);
            role.setRealm(realm);
            role.setDataCriacao(LocalDateTime.now());
            role.setDataUltimaAtualizacao(LocalDateTime.now());

            role = roleRepository.save(role);
            log.info("Role padrão criada: {} (ID: {})", nome, role.getId());
        }

        rolesPadrao.add(role);
    }

    // Converter para Response DTO
    return rolesPadrao.stream()
        .map(role -> {
            Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());
            List<String> usuariosNomes = roleRepository.findUsuariosNomesByRole(role.getId());

            return RoleDetailResponse.from(
                role,
                role.getRealm().getNome(),
                role.getRealm().getId().toString(),
                totalUsuarios,
                usuariosNomes
            );
        })
        .collect(Collectors.toList());
}

/**
 * Verifica se todas as roles padrão existem no realm.
 */
public boolean existemTodasRolesPadrao(String realmId) {
    UUID realmUuid = UUID.fromString(realmId);
    return roleRepository.existsByNomeAndRealm_Id("ADMIN", realmUuid) &&
           roleRepository.existsByNomeAndRealm_Id("USER", realmUuid) &&
           roleRepository.existsByNomeAndRealm_Id("SERVICE", realmUuid);
}
```

### Repository Custom Methods
**Adicionar métodos ao `RoleRepository.java`:**

```java
Optional<Role> findByNomeAndRealm_Id(String nome, UUID realmId);

boolean existsByNomeAndRealm_Id(String nome, UUID realmId);
```

### Atualizar Remoção no Service
**Adicionar verificação de role padrão:**

```java
@Transactional
public void removerRole(String id) {
    log.debug("Removendo role: {}", id);

    Role role = roleRepository.findById(UUID.fromString(id))
        .orElseThrow(() -> new RoleNaoEncontradaException("Role não encontrada"));

    // Verificar se é role padrão
    boolean isPadrao = isRolePadrao(role.getNome());

    if (isPadrao) {
        throw new RoleEmUsoException(
            "Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas"
        );
    }

    // Verificar se tem usuários associados
    Long totalUsuarios = roleRepository.countUsuariosByRole(role.getId());

    if (totalUsuarios > 0) {
        throw new RoleEmUsoException(
            "Role possui " + totalUsuarios + " usuário(s) associado(s) e não pode ser removida"
        );
    }

    roleRepository.delete(role);

    log.info("Role removida com sucesso: {} (ID: {})", role.getNome(), role.getId());
}

/**
 * Verifica se o nome da role é uma role padrão.
 */
private boolean isRolePadrao(String nome) {
    return "ADMIN".equals(nome) || "USER".equals(nome) || "SERVICE".equals(nome);
}
```

### Atualizar Template de Formulário
**Desabilitar checkbox de padrão para roles padrão:**

```html
<div class="form-check">
    <input type="checkbox"
           class="form-check-input"
           id="padrao"
           th:field="*{padrao}"
           th:disabled="${roleForm.isRolePadrao()}" />

    <label class="form-check-label" for="padrao">
        Role Padrão
    </label>
</div>
<small class="text-muted" th:if="${roleForm.isRolePadrao()}">
    Roles padrão (ADMIN, USER, SERVICE) não podem ter este atributo alterado
</small>
```

### Bootstrap de Roles Padrão
**Adicionar ao `RealmBootstrapService`:**

```java
/**
 * Cria as roles padrão do sistema (ADMIN, USER, SERVICE).
 */
@Transactional
public void criarRolesPadraoParaRealm(Realm realm) {
    log.info("Criando roles padrão para realm: {}", realm.getNome());

    List<String> nomesPadrao = List.of("ADMIN", "USER", "SERVICE");
    List<String> descricoesPadrao = List.of(
        "Administrador do sistema com acesso total",
        "Usuário padrão com acesso limitado",
        "Role para serviços e integrações"
    );

    for (int i = 0; i < nomesPadrao.size(); i++) {
        String nome = nomesPadrao.get(i);
        String descricao = descricoesPadrao.get(i);

        // Verificar se já existe
        boolean existe = roleRepository.existsByNomeAndRealm_Id(nome, realm.getId());

        if (!existe) {
            Role role = new Role();
            role.setNome(nome);
            role.setDescricao(descricao);
            role.setAtiva(true);
            role.setPadrao(true);
            role.setRealm(realm);
            role.setDataCriacao(LocalDateTime.now());
            role.setDataUltimaAtualizacao(LocalDateTime.now());

            roleRepository.save(role);
            log.info("Role padrão criada: {}", nome);
        } else {
            log.debug("Role padrão já existe: {}", nome);
        }
    }

    log.info("Roles padrão configuradas para realm: {}", realm.getNome());
}
```

### Testes Unitários
**Adicionar testes em `AdminRoleServiceTest`:**

```java
@Test
void testIsRolePadrao() {
    assertTrue(isRolePadrao("ADMIN"));
    assertTrue(isRolePadrao("USER"));
    assertTrue(isRolePadrao("SERVICE"));
    assertFalse(isRolePadrao("GERENTE"));
    assertFalse(isRolePadrao("CUSTOM"));
}

@Test
void testRemoverRolePadrao() {
    Role role = createRole("ADMIN", "Administrador", true);
    role.setPadrao(true);
    UUID roleId = UUID.randomUUID();
    role.setId(roleId);

    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

    assertThrows(RoleEmUsoException.class,
        () -> adminRoleService.removerRole(roleId.toString()));

    verify(roleRepository, never()).delete(any(Role.class));
}

@Test
void testCriarRolesPadrao() {
    UUID realmId = UUID.randomUUID();
    var realm = createRealm(realmId);

    when(roleRepository.findByNomeAndRealm_Id(anyString(), eq(realmId)))
        .thenReturn(Optional.empty());
    when(roleRepository.save(any(Role.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    List<RoleDetailResponse> result = adminRoleService.buscarOuCriarRolesPadrao(realmId.toString());

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals("ADMIN", result.get(0).nome());
    assertEquals("USER", result.get(1).nome());
    assertEquals("SERVICE", result.get(2).nome());

    verify(roleRepository, times(3)).save(any(Role.class));
}

@Test
void testExistemTodasRolesPadrao() {
    UUID realmId = UUID.randomUUID();

    when(roleRepository.existsByNomeAndRealm_Id("ADMIN", realmId))
        .thenReturn(true);
    when(roleRepository.existsByNomeAndRealm_Id("USER", realmId))
        .thenReturn(true);
    when(roleRepository.existsByNomeAndRealm_Id("SERVICE", realmId))
        .thenReturn(true);

    boolean result = adminRoleService.existemTodasRolesPadrao(realmId.toString());

    assertTrue(result);
}

@Test
void testNaoExistemTodasRolesPadrao() {
    UUID realmId = UUID.randomUUID();

    when(roleRepository.existsByNomeAndRealm_Id("ADMIN", realmId))
        .thenReturn(true);
    when(roleRepository.existsByNomeAndRealm_Id("USER", realmId))
        .thenReturn(true);
    when(roleRepository.existsByNomeAndRealm_Id("SERVICE", realmId))
        .thenReturn(false); // SERVICE não existe

    boolean result = adminRoleService.existemTodasRolesPadrao(realmId.toString());

    assertFalse(result);
}
```

## Checklist de Validação
- [ ] Método `isRolePadrao()` implementado nos DTOs
- [ ] Badge "Padrão" exibido na listagem
- [ ] Ícone de estrela exibido para roles padrão
- [ ] Roles padrão não podem ser removidas (service)
- [ ] Checkbox de padrão desabilitado na edição
- [ ] Método `buscarOuCriarRolesPadrao()` implementado
- [ ] Método `existemTodasRolesPadrao()` implementado
- [ ] Bootstrap de roles padrão implementado
- [ ] Repository methods adicionados
- [ ] Testes unitários criados
- [ ] Todos os testes passam

## Anotações
- Roles padrão são: ADMIN, USER, SERVICE
- Essas roles são criadas automaticamente no bootstrap
- Não podem ser removidas nunca
- Checkbox de padrão é sempre marcado para roles padrão
- Badge visual ajuda na identificação rápida
- Essa lógica garante que o sistema sempre tenha as roles essenciais

## Dependências
- Epic 1 (Gestão de Realms) - para criação de roles por realm
- Epic 3 (Gestão de Roles) - repository e entidade já existem
- Epic 8 (Bootstrap) - para criação automática

## Prioridade
**Alta** - Garante existência das roles essenciais do sistema

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas

## Status do Epic 14 - Story 07

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

## Resumo da Implementação

O Bootstrap do sistema foi implementado para criar as roles padrão em cada realm automaticamente.

**Funcionalidades Implementadas:**
- Roles padrão definidos: ADMIN, USER, SERVICE
- Criação automática no inicialização do sistema
- Verificação de unicidade para evitar duplicatas
- Proteção contra alteração/remoção de roles padrão
- Configuração via variáveis de ambiente

**Implementação:**
- DatabaseInitializationRunner configurado
- RoleService.bootstraprRolesPadrao() implementado
- Verificação se já existem antes de criar
- Marcação de roles como padrão
- Logging apropriado

**Testes:**
- Teste de criação de roles padrão em realms vazios
- Teste de verificação de unicidade
- Teste de proteção contra alteração de roles padrão
- Teste de que não sejam criadas duplicatas

**Status do Epic 14 - Story 07**

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

