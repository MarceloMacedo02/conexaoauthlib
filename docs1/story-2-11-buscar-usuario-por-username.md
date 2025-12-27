# História 2.11: Buscar Usuário por Username (Email)

**Epic:** 2 - Gestão de Usuários  
**Status:** Ready for Review  
**Prioridade:** Alta  
**Estimativa:** 1 dia  
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero buscar um usuário específico pelo seu username (e-mail) para que eu possa visualizar os detalhes completos desse usuário usando uma forma alternativa de busca.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/usuarios/username/{username}` retorna usuário específico
- [x] Busca case-insensitive (trata username em minúsculas)
- [x] Retornar todos os campos do usuário (exceto senha)
- [x] Retornar roles associadas
- [x] Retornar informações do realm
- [x] Retornar `200 OK` com sucesso
- [x] Retornar `404 Not Found` se usuário não existir
- [x] Retornar `400 Bad Request` se username for nulo ou vazio
- [x] Documentação Swagger em português
- [x] Validação de entrada (username não nulo/vazio)

---

## Regras de Negócio

1. Busca:
   - Username é tratado como email para busca
   - Busca case-insensitive (converte para lowercase)
   - Username não pode ser nulo ou vazio
   - Usuário deve existir no banco de dados

2. Segurança:
   - Usuário só pode ver usuários do seu realm (se não for ADMIN global)
   - Não expor senha em nenhum caso

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Gestão de Usuários", description = "Operações de gestão de usuários")
public class UsuarioController {
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Buscar usuário por username (e-mail)", description = "Retorna os detalhes completos de um usuário específico pelo seu username (e-mail). Busca é case-insensitive.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Username inválido")
    })
    public ResponseEntity<UsuarioResponse> buscarPorUsername(@PathVariable String username) {
        UsuarioResponse usuarioResponse = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(usuarioResponse);
    }
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public interface UsuarioService {
    UsuarioResponse buscarPorUsername(String username);
}
```

### Service Implementation
```java
@Override
@Transactional(readOnly = true)
public UsuarioResponse buscarPorUsername(String username) {
    // Normalize username (email) to lowercase for case-insensitive search
    String usernameNormalizado = username != null ? username.toLowerCase().trim() : null;
    
    if (usernameNormalizado == null || usernameNormalizado.isEmpty()) {
        throw new IllegalArgumentException("Username não pode ser nulo ou vazio");
    }

    // Find user by email (case-insensitive)
    Usuario usuario = usuarioRepository.findByEmail(usernameNormalizado)
            .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com username: " + username));

    // Map to response with roles
    List<UUID> roleIds = usuario.getRoles().stream()
            .map(Role::getId)
            .collect(Collectors.toList());
    List<String> roleNomes = usuario.getRoles().stream()
            .map(Role::getNome)
            .collect(Collectors.toList());

    return mapearParaResponse(usuario, roleIds, roleNomes);
}
```

### Repository
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailIgnoreCase(@Param("email") String email);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoUsuarioExistente_quandoBuscarPorUsername_entaoRetornaUsuario() {
    String username = "joao@example.com";
    UUID userId = UUID.randomUUID();
    UUID realmId = UUID.randomUUID();
    
    Usuario usuario = new Usuario();
    usuario.setId(userId);
    usuario.setNome("João Silva");
    usuario.setEmail("joao@example.com");
    usuario.setRealmId(realmId);
    usuario.setEmpresaId("EMP001");
    usuario.setTenantId("TEN001");
    usuario.setStatus(StatusUsuario.ATIVO);
    usuario.setRoles(new HashSet<>());
    
    when(usuarioRepository.findByEmail(username.toLowerCase())).thenReturn(Optional.of(usuario));
    
    UsuarioResponse response = usuarioService.buscarPorUsername(username);
    
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(userId);
    assertThat(response.nome()).isEqualTo("João Silva");
    assertThat(response.email()).isEqualTo("joao@example.com");
    assertThat(response.realmId()).isEqualTo(realmId);
    assertThat(response.status()).isEqualTo(StatusUsuario.ATIVO);
    
    verify(usuarioRepository).findByEmail(username.toLowerCase());
}
```

### Teste Case-insensitive
```java
@Test
void dadoUsuarioExistente_quandoBuscarPorUsernameCaseDiferente_entaoRetornaUsuario() {
    String username = "JOAO@EXAMPLE.COM";
    String usernameNormalizado = "joao@example.com";
    UUID userId = UUID.randomUUID();
    
    Usuario usuario = new Usuario();
    usuario.setId(userId);
    usuario.setNome("João Silva");
    usuario.setEmail("joao@example.com");
    usuario.setRealmId(UUID.randomUUID());
    usuario.setStatus(StatusUsuario.ATIVO);
    usuario.setRoles(new HashSet<>());
    
    when(usuarioRepository.findByEmail(usernameNormalizado)).thenReturn(Optional.of(usuario));
    
    UsuarioResponse response = usuarioService.buscarPorUsername(username);
    
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo("joao@example.com");
    
    verify(usuarioRepository).findByEmail(usernameNormalizado);
}
```

### Teste de Usuário Não Encontrado
```java
@Test
void dadoUsuarioInexistente_quandoBuscarPorUsername_entaoRetornaNotFound() {
    String username = "inexistente@example.com";
    
    when(usuarioRepository.findByEmail(username.toLowerCase())).thenReturn(Optional.empty());
    
    assertThatThrownBy(() -> usuarioService.buscarPorUsername(username))
            .isInstanceOf(UsuarioNotFoundException.class)
            .hasMessageContaining("Usuário não encontrado com username: " + username);
    
    verify(usuarioRepository).findByEmail(username.toLowerCase());
}
```

### Teste de Validação de Entrada
```java
@Test
void dadoUsernameNulo_quandoBuscarPorUsername_entaoLancaIllegalArgumentException() {
    assertThatThrownBy(() -> usuarioService.buscarPorUsername(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username não pode ser nulo ou vazio");
}

@Test
void dadoUsernameVazio_quandoBuscarPorUsername_entaoLancaIllegalArgumentException() {
    assertThatThrownBy(() -> usuarioService.buscarPorUsername(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username não pode ser nulo ou vazio");
            
    assertThatThrownBy(() -> usuarioService.buscarPorUsername("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username não pode ser nulo ou vazio");
}
```

### Teste de Integração (Controller)
```java
@Test
void dadoUsuarioExistente_quandoBuscarPorUsername_entaoRetorna200() throws Exception {
    String username = "joao@example.com";
    UUID userId = UUID.randomUUID();
    UUID realmId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    
    UsuarioResponse usuarioResponse = new UsuarioResponse(
            userId,
            "12345678901", // cpfOrCnpj
            "joao@example.com",
            realmId,
            "empresa-a",
            List.of(roleId),
            List.of("USER"),
            "EMP001",
            "TEN001",
            StatusUsuario.ATIVO,
            StatusRealm.ATIVO,
            now,
            now,
            now);
    
    when(usuarioService.buscarPorUsername(username)).thenReturn(usuarioResponse);
    
    mockMvc.perform(get(BASE_URL + "/username/" + username)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.nome").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao@example.com"))
                    .andExpect(jsonPath("$.realmId").value(realmId.toString()))
                    .andExpect(jsonPath("$.realmNome").value("empresa-a"))
                    .andExpect(jsonPath("$.status").value("ATIVO"));
}
```

---

## Dependências

- História 2.1: Criar Usuário
- História 2.9: Buscar Usuário por ID
- Epic 1: Gestão de Realms
- Epic 3: Gestão de Roles

---

## Pontos de Atenção

- `@Transactional(readOnly = true)` para método de leitura
- Normalização case-insensitive via `toLowerCase().trim()`
- Validação de entrada nula/vazia
- Documentação Swagger em português
- Testes unitários e de integração obrigatórios
- Checkstyle: Seguir Google Java Style Guide

---

## Registro de Desenvolvimento

### Status: ✅ Implementado com Sucesso

**Data de Implementação:** 2025-12-27

**Método Implementado:**
- ✅ `UsuarioResponse buscarPorUsername(String username)` em `UsuarioService` (interface)
- ✅ `UsuarioResponse buscarPorUsername(String username)` em `UsuarioServiceImpl` (implementação)
- ✅ `GET /api/v1/usuarios/username/{username}` endpoint em `UsuarioController`

**Validações:**
- ✅ Endpoint retorna 200 com usuário existente
- ✅ Endpoint retorna 404 com usuário inexistente
- ✅ Busca case-insensitive implementada e testada
- ✅ Validação de username nulo/vazio implementada
- ✅ Documentação Swagger em português implementada
- ✅ Testes unitários implementados e validados
- ✅ Testes de integração implementados no controller

**Arquivos Modificados/Criados:**
- `src/main/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioService.java` (adicionado método)
- `src/main/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioServiceImpl.java` (adicionado método)
- `src/main/java/br/com/plataforma/conexaodigital/usuario/api/controller/UsuarioController.java` (adicionado endpoint)
- `src/test/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioServiceBuscarPorUsernameTest.java` (criado testes)
- `src/test/java/br/com/plataforma/conexaodigital/usuario/api/controller/UsuarioControllerIntegrationTest.java` (adicionados testes)

**Notas de Implementação:**
- Seguiu rigorosamente as regras primárias: não modificou entities existentes
- Usou repositories e services existentes sem alteração
- Mantido compatibilidade com Epic 1 (Realms) e Epic 3 (Roles)
- Aplicados padrões já estabelecidos no projeto
- Busca case-insensitive implementada através de normalização para lowercase
- Tratamento robusto de entradas inválidas (nulo e vazio)
- Todos os testes estão passando, validando o comportamento esperado

### Histórico de Mudanças
- 2025-12-27: Implementado método buscarPorUsername no service e controller
- 2025-12-27: Adicionada documentação OpenAPI/Swagger
- 2025-12-27: Criados testes unitários e de integração
- 2025-12-27: Validação case-insensitive implementada
- 2025-12-27: Story marcada como Ready for Review