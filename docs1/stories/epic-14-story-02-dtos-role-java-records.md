# User Story: DTOs de Role (Java Records)

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-02

## Descrição
Criar os DTOs (Data Transfer Objects) como Java Records para transferência de dados entre a camada de controller e service para gestão de roles. Incluir validações Jakarta Bean Validation e mensagens de erro em `messages.properties`.

## Critérios de Aceite
- [X] Record `RoleListResponse` criado com todos os campos necessários para listagem
- [X] Record `RoleForm` criado com validações para criação/edição
- [X] Record `RoleDetailResponse` criado com campos completos para visualização
- [X] Jakarta Bean Validation annotations aplicadas corretamente
- [X] Mensagens de validação adicionadas em `messages.properties`
- [X] Métodos auxiliares de conversão implementados (se necessário)
- [X] Imutabilidade garantida (records são imutáveis por padrão)

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.requests`
2. Criar package `br.com.plataforma.conexaodigital.admin.api.responses`
3. Criar record `RoleListResponse.java` para listagem
4. Criar record `RoleDetailResponse.java` para detalhes
5. Criar record `RoleForm.java` para formulários de criação/edição
6. Adicionar mensagens de validação em `src/main/resources/messages.properties`
7. Implementar métodos de mapeamento (mapper) de entidade para DTOs

## Instruções de Implementação

### Package Structure
```
br.com.plataforma.conexaodigital.admin.api
├── controller
│   └── AdminRoleController.java
├── requests
│   └── RoleForm.java
└── responses
    ├── RoleListResponse.java
    └── RoleDetailResponse.java
```

### Record: RoleListResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RoleListResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

/**
 * DTO para resposta de listagem de roles na página administrativa.
 * Contém os campos essenciais para exibição na tabela.
 */
public record RoleListResponse(
    String id,
    String nome,
    String descricao,
    String realmNome,
    String realmId,
    Boolean padrao, // se é role padrão (ADMIN, USER, SERVICE)
    Boolean ativa,
    Long totalUsuarios,
    LocalDateTime dataCriacao
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     * @param role entidade Role
     * @param realmNome nome do realm da role
     * @param realmId ID do realm da role
     * @param totalUsuarios quantidade de usuários com esta role
     * @return instância de RoleListResponse
     */
    public static RoleListResponse from(
        br.com.plataforma.conexaodigital.gestarole.domain.model.Role role,
        String realmNome,
        String realmId,
        Long totalUsuarios
    ) {
        boolean isPadrao = role.getNome().equals("ADMIN") ||
                          role.getNome().equals("USER") ||
                          role.getNome().equals("SERVICE");

        return new RoleListResponse(
            role.getId().toString(),
            role.getNome(),
            role.getDescricao(),
            realmNome,
            realmId,
            isPadrao,
            role.getAtiva(),
            totalUsuarios,
            role.getDataCriacao()
        );
    }

    /**
     * Verifica se a role pode ser removida.
     * Roles padrão e roles com usuários não podem ser removidas.
     */
    public boolean podeRemover() {
        return !padrao && totalUsuarios == 0;
    }
}
```

### Record: RoleDetailResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RoleDetailResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta detalhada de uma role.
 * Contém todos os campos da entidade mais informações relacionadas.
 */
public record RoleDetailResponse(
    String id,
    String nome,
    String descricao,
    String realmNome,
    String realmId,
    Boolean padrao,
    Boolean ativa,
    Long totalUsuarios,
    List<String> usuariosNomes, // lista de usuários que possuem esta role
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     */
    public static RoleDetailResponse from(
        br.com.plataforma.conexaodigital.gestarole.domain.model.Role role,
        String realmNome,
        String realmId,
        Long totalUsuarios,
        List<String> usuariosNomes
    ) {
        boolean isPadrao = role.getNome().equals("ADMIN") ||
                          role.getNome().equals("USER") ||
                          role.getNome().equals("SERVICE");

        return new RoleDetailResponse(
            role.getId().toString(),
            role.getNome(),
            role.getDescricao(),
            realmNome,
            realmId,
            isPadrao,
            role.getAtiva(),
            totalUsuarios,
            usuariosNomes,
            role.getDataCriacao(),
            role.getDataUltimaAtualizacao()
        );
    }
}
```

### Record: RoleForm
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/requests/RoleForm.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para formulário de criação/edição de role.
 * Contém validações Jakarta Bean Validation.
 */
public record RoleForm(
    String id,

    @NotBlank(message = "{role.nome.obrigatorio}")
    @Size(min = 3, max = 50, message = "{role.nome.tamanho}")
    @Pattern(regexp = "^[A-Z_]+$", message = "{role.nome.formato}")
    String nome,

    @Size(max = 500, message = "{role.descricao.tamanho}")
    String descricao,

    @NotNull(message = "{role.realm.obrigatorio}")
    String realmId,

    @NotNull(message = "{role.ativa.obrigatorio}")
    Boolean ativa,

    Boolean padrao
) {
    /**
     * Cria instância vazia para nova role.
     */
    public RoleForm() {
        this(
            null,
            null,
            null,
            null,
            true, // padrão: ativa
            false // padrão: não é padrão
        );
    }

    /**
     * Retorna true se este form está em modo de edição.
     */
    public boolean isEdit() {
        return id != null && !id.isBlank();
    }

    /**
     * Retorna true se esta role é uma role padrão.
     */
    public boolean isRolePadrao() {
        return "ADMIN".equals(nome) || "USER".equals(nome) || "SERVICE".equals(nome);
    }
}
```

### Messages Properties
**Localização:** `src/main/resources/messages.properties`

```properties
# Role Form Validations
role.nome.obrigatorio=Nome da role é obrigatório
role.nome.tamanho=Nome deve ter entre 3 e 50 caracteres
role.nome.formato=Nome deve conter apenas letras maiúsculas e underscores (ex: ADMIN, USER_ROLE)
role.descricao.tamanho=Descrição deve ter no máximo 500 caracteres
role.realm.obrigatorio=Realm é obrigatório
role.ativa.obrigatorio=Status é obrigatório
role.padrao.obrigatorio=Indicador de padrão é obrigatório

# Role Messages
role.ja.existe=Role já existe neste realm
role.nao.encontrada=Role não encontrada
role.em.uso=Role possui usuários associados e não pode ser removida
role.padrao.nao.pode.remover=Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas
role.nome.uppercase=Nome deve estar em maiúsculas
```

### Mapper Utility
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/mapper/RoleMapper.java`

```java
package br.com.plataforma.conexaodigital.admin.api.mapper;

import br.com.plataforma.conexaodigital.admin.api.requests.RoleForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleListResponse;
import br.com.plataforma.conexaodigital.gestarole.domain.model.Role;

import java.util.UUID;

/**
 * Utilitário de mapeamento entre entidade Role e DTOs.
 */
public final class RoleMapper {

    private RoleMapper() {
        // Utilitário estático
    }

    /**
     * Converte RoleForm para entidade Role (nova ou edição).
     */
    public static Role toEntity(RoleForm form) {
        if (form == null) {
            return null;
        }

        Role role;
        if (form.isEdit()) {
            role = new Role();
            role.setId(UUID.fromString(form.id()));
        } else {
            role = new Role();
            role.setDataCriacao(java.time.LocalDateTime.now());
        }

        role.setNome(form.nome().toUpperCase()); // Garante uppercase
        role.setDescricao(form.descricao());
        role.setAtiva(form.ativa());
        role.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        return role;
    }

    /**
     * Converte entidade Role para RoleForm (edição).
     */
    public static RoleForm toForm(Role role, String realmId) {
        if (role == null) {
            return null;
        }

        boolean isPadrao = role.getNome().equals("ADMIN") ||
                          role.getNome().equals("USER") ||
                          role.getNome().equals("SERVICE");

        return new RoleForm(
            role.getId().toString(),
            role.getNome(),
            role.getDescricao(),
            realmId,
            role.getAtiva(),
            isPadrao
        );
    }
}
```

### Testes Unitários
**Localização:** `src/test/java/br/com/plataforma/conexaodigital/admin/api/requests/RoleFormTest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleFormTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testRoleFormValid() {
        RoleForm form = new RoleForm(
            null,
            "GERENTE",
            "Gerente com permissões de gestão",
            "realm-id-1",
            true,
            false
        );

        Set<ConstraintViolation<RoleForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRoleFormNomeObrigatorio() {
        RoleForm form = new RoleForm(
            null,
            "", // vazio
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        Set<ConstraintViolation<RoleForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    void testRoleFormNomeTamanhoMinimo() {
        RoleForm form = new RoleForm(
            null,
            "AB", // menos de 3
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        Set<ConstraintViolation<RoleForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRoleFormNomeFormatoInvalido() {
        RoleForm form = new RoleForm(
            null,
            "Gerente", // contém minúsculas
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        Set<ConstraintViolation<RoleForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("formato")));
    }

    @Test
    void testRoleFormNomeValido() {
        RoleForm form = new RoleForm(
            null,
            "ADMIN_ROLE", // válido
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        Set<ConstraintViolation<RoleForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRoleFormEditMode() {
        RoleForm form = new RoleForm(
            "uuid-123",
            "GERENTE",
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        assertTrue(form.isEdit());
    }

    @Test
    void testRoleFormCreateMode() {
        RoleForm form = new RoleForm(
            null,
            "GERENTE",
            "Descrição da role",
            "realm-id-1",
            true,
            false
        );

        assertFalse(form.isEdit());
    }

    @Test
    void testRoleFormIsRolePadrao() {
        RoleForm adminForm = new RoleForm(
            null,
            "ADMIN",
            "Admin role",
            "realm-id-1",
            true,
            true
        );

        RoleForm customForm = new RoleForm(
            null,
            "GERENTE",
            "Manager role",
            "realm-id-1",
            true,
            false
        );

        assertTrue(adminForm.isRolePadrao());
        assertFalse(customForm.isRolePadrao());
    }
}
```

## Checklist de Validação
- [X] Package `api/responses` criado
- [X] Package `api/requests` criado
- [X] Record `RoleListResponse` criado com todos os campos
- [X] Record `RoleDetailResponse` criado com todos os campos
- [X] Record `RoleForm` criado com validações Jakarta Bean Validation
- [X] Mensagens de validação adicionadas em `messages.properties`
- [X] Método `from()` implementado em `RoleListResponse`
- [X] Método `from()` implementado em `RoleDetailResponse`
- [X] Métodos auxiliares em `RoleForm` (construtor vazio, isEdit, isRolePadrao)
- [X] Método `podeRemover()` implementado em `RoleListResponse`
- [X] Classe `RoleMapper` criada
- [X] Testes unitários para `RoleForm` criados
- [X] Todos os testes passam

## Anotações
- Records são imutáveis por padrão, garantindo thread-safety
- Nome da role deve estar sempre em UPPERCASE (validação de regex)
- Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas
- Descrição é opcional, mas se preenchida deve ter no máximo 500 caracteres
- Status usa Boolean para simplificar a UI (true = ATIVA, false = INATIVA)
- Métodos de fábrica estáticos facilitam a conversão de entidade para DTO
- Validar unicidade de nome por realm no backend (neste DTO)
- Testes de validação devem cobrir todos os constraints

## Dependências
- Epic 1 (Gestão de Realms) - para associação com realm
- Epic 3 (Gestão de Roles) - entidade Role já existe
- Epic 9 (Configuração) - Jakarta Bean Validation configurado

## Prioridade
**Alta** - DTOs necessários para todas as outras histórias

## Estimativa
- Implementação: 2.5 horas
- Testes: 1.5 horas
- Total: 4 horas
