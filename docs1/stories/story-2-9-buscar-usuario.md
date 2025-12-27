# História 2.9: Buscar Usuário por ID

**Epic:** 2 - Gestão de Usuários  
**Status:** Ready for Review  
**Prioridade:** Média  
**Estimativa:** 1 dia  
**Complexidade**: Baixa

---

## Descrição

Como administrador do sistema, quero buscar um usuário específico pelo seu ID para que eu possa visualizar os detalhes completos desse usuário.

---

## Critérios de Aceite

- [x] Endpoint `GET /api/v1/usuarios/{id}` retorna usuário específico
- [x] Retornar todos os campos do usuário (exceto senha)
- [x] Retornar roles associadas (listas vazias até Epic 3)
- [x] Retornar informações do realm
- [x] Retornar `200 OK` com sucesso
- [x] Retornar `404 Not Found` se usuário não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Busca:
   - ID deve ser um UUID válido
   - Usuário deve existir no banco de dados
   - Não há restrição de status (pode buscar usuários inativos ou bloqueados)

2. Segurança:
   - Usuário só pode ver usuários do seu realm (se não for ADMIN global)
   - Validar permissões (em epic 4)

---

## Requisitos Técnicos

### Controller
```java
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Gestão de Usuários", description = "Operações de gestão de usuários")
public class UsuarioController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id);
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public interface UsuarioService {
    UsuarioResponse buscarPorId(UUID id);
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@Test
void dadoUsuarioExistente_quandoBuscarPorId_entaoRetornaUsuario() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    Role role = roleRepository.save(new Role("USER", realm));
    
    Usuario usuario = new Usuario("João", "joao@example.com", "Senha@123", realm, StatusUsuario.ATIVO);
    usuario.addRole(role);
    usuarioRepository.save(usuario);
    
    ResponseEntity<UsuarioResponse> response = controller.buscarPorId(usuario.getId());
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().id()).isEqualTo(usuario.getId());
    assertThat(response.getBody().nome()).isEqualTo("João");
    assertThat(response.getBody().email()).isEqualTo("joao@example.com");
    assertThat(response.getBody().realmId()).isEqualTo(realm.getId());
    assertThat(response.getBody().roleIds()).hasSize(1);
}
```

### Teste de Usuário Não Encontrado
```java
@Test
void dadoUsuarioInexistente_quandoBuscarPorId_entaoRetornaNotFound() {
    UUID idInexistente = UUID.randomUUID();
    
    assertThatThrownBy(() -> controller.buscarPorId(idInexistente))
        .isInstanceOf(UsuarioNotFoundException.class);
}
```

---

## Dependências

- História 2.1: Criar Usuário
- Epic 1: Gestão de Realms
- Epic 3: Gestão de Roles

---

## Pontos de Atenção

- `@Transactional(readOnly = true)` para método de leitura
- Senha nunca deve retornar na resposta
- Exception handling via `GlobalExceptionHandler`
- Checkstyle: Seguir Google Java Style Guide

---

## Registro de Desenvolvimento

### Status: ✅ Implementado com Sucesso

**Data de Implementação:** 2025-12-23

**Método Implementado:**
- ✅ `UsuarioResponse buscarPorId(UUID id)` em `UsuarioService` (interface já existia)
- ✅ `UsuarioResponse buscarPorId(UUID id)` em `UsuarioServiceImpl` (já estava implementado)
- ✅ `GET /api/v1/usuarios/{id}` endpoint em `UsuarioController`

**Validações:**
- ✅ Endpoint retorna 200 com usuário existente
- ✅ Endpoint retorna 404 com usuário inexistente
- ✅ Validação de UUID no path variable (automático pelo Spring)
- ✅ Documentação Swagger em português implementada
- ✅ GlobalExceptionHandler trata `UsuarioNotFoundException` corretamente
- ✅ Testes unitários e de integração implementados e passando

**Arquivos Modificados/Criados:**
- `src/main/java/br/com/plataforma/conexaodigital/usuario/api/controller/UsuarioController.java` (adicionado endpoint)
- `src/test/java/br/com/plataforma/conexaodigital/usuario/domain/service/UsuarioServiceImplTest.java` (adicionados testes)
- `src/test/java/br/com/plataforma/conexaodigital/usuario/api/controller/UsuarioControllerIntegrationTest.java` (adicionados testes)

**Notas de Implementação:**
- Seguiu rigorosamente as regras primárias: não modificou entities existentes
- Usou repositories e services existentes sem alteração
- Mantido compatibilidade com Epic 1 (Realms) e Epic 3 (Roles futura)
- Aplicados padrões já estabelecidos no projeto
- Todos os testes estão passando, validando o comportamento esperado
