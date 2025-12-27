# User Story: DTOs de Realm (Java Records)

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-02

## Descrição
Criar os DTOs (Data Transfer Objects) como Java Records para transferência de dados entre a camada de controller e service para gestão de realms. Incluir validações Jakarta Bean Validation e mensagens de erro em `messages.properties`.

## Critérios de Aceite
- [X] Record `RealmListResponse` criado com todos os campos necessários para listagem
- [X] Record `RealmForm` criado com validações para criação/edição
- [X] Record `RealmDetailResponse` criado com campos completos para visualização
- [X] Jakarta Bean Validation annotations aplicadas corretamente
- [X] Mensagens de validação adicionadas em `messages.properties`
- [X] Métodos auxiliares de conversão implementados (se necessário)
- [X] Imutabilidade garantida (records são imutáveis por padrão)

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.responses`
2. Criar record `RealmListResponse.java` para listagem
3. Criar record `RealmDetailResponse.java` para detalhes
4. Criar record `RealmForm.java` para formulários de criação/edição
5. Adicionar mensagens de validação em `src/main/resources/messages.properties`
6. Implementar métodos de mapeamento (mapper) de entidade para DTOs

## Instruções de Implementação

### Package Structure
```
br.com.plataforma.conexaodigital.admin.api
├── controller
│   └── AdminRealmController.java
├── requests
│   └── RealmForm.java
└── responses
    ├── RealmListResponse.java
    └── RealmDetailResponse.java
```

### Record: RealmListResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RealmListResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

/**
 * DTO para resposta de listagem de realms na página administrativa.
 * Contém os campos essenciais para exibição na tabela.
 */
public record RealmListResponse(
    String id,
    String nome,
    String descricao,
    Boolean ativo,
    Boolean master,
    Long totalUsuarios,
    Long chavesAtivas,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     * @param realm entidade Realm
     * @param totalUsuarios total de usuários no realm
     * @param chavesAtivas total de chaves ativas no realm
     * @return instância de RealmListResponse
     */
    public static RealmListResponse from(
        br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm realm,
        Long totalUsuarios,
        Long chavesAtivas
    ) {
        return new RealmListResponse(
            realm.getId().toString(),
            realm.getNome(),
            realm.getDescricao(),
            realm.getStatus() == br.com.plataforma.conexaodigital.gestaorealm.domain.model.StatusRealm.ATIVO,
            realm.isMaster(),
            totalUsuarios,
            chavesAtivas,
            realm.getDataCriacao(),
            realm.getDataAtualizacao()
        );
    }
}
```

### Record: RealmDetailResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RealmDetailResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta detalhada de um realm.
 * Contém todos os campos da entidade mais informações relacionadas.
 */
public record RealmDetailResponse(
    String id,
    String nome,
    String descricao,
    Boolean ativo,
    Boolean master,
    String empresaId,
    Long totalUsuarios,
    Long usuariosAtivos,
    Long usuariosBloqueados,
    Long chavesAtivas,
    Long chavesInativas,
    List<String> roles,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao,
    LocalDateTime dataDesativacao,
    LocalDateTime dataReativacao
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     */
    public static RealmDetailResponse from(
        br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm realm,
        Long totalUsuarios,
        Long usuariosAtivos,
        Long usuariosBloqueados,
        Long chavesAtivas,
        Long chavesInativas,
        List<String> roles
    ) {
        return new RealmDetailResponse(
            realm.getId().toString(),
            realm.getNome(),
            realm.getDescricao(),
            realm.getStatus() == br.com.plataforma.conexaodigital.gestaorealm.domain.model.StatusRealm.ATIVO,
            realm.isMaster(),
            realm.getEmpresaId(),
            totalUsuarios,
            usuariosAtivos,
            usuariosBloqueados,
            chavesAtivas,
            chavesInativas,
            roles,
            realm.getDataCriacao(),
            realm.getDataAtualizacao(),
            realm.getDataDesativacao(),
            realm.getDataReativacao()
        );
    }
}
```

### Record: RealmForm
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/requests/RealmForm.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para formulário de criação/edição de realm.
 * Contém validações Jakarta Bean Validation.
 */
public record RealmForm(
    String id,

    @NotBlank(message = "{realm.nome.obrigatorio}")
    @Size(min = 3, max = 100, message = "{realm.nome.tamanho}")
    @Pattern(regexp = "^[a-z][a-z0-9_-]*$", message = "{realm.nome.formato}")
    String nome,

    @Size(max = 500, message = "{realm.descricao.tamanho}")
    String descricao,

    @NotNull(message = "{realm.status.obrigatorio}")
    Boolean ativo,

    @Size(max = 100, message = "{realm.empresaid.tamanho}")
    String empresaId
) {
    /**
     * Cria instância vazia para novo realm.
     */
    public RealmForm() {
        this(
            null,
            null,
            null,
            true, // padrão: ativo
            null
        );
    }

    /**
     * Retorna true se este form está em modo de edição.
     */
    public boolean isEdit() {
        return id != null && !id.isBlank();
    }
}
```

### Messages Properties
**Localização:** `src/main/resources/messages.properties`

```properties
# Realm Form Validations
realm.nome.obrigatorio=Nome do realm é obrigatório
realm.nome.tamanho=Nome deve ter entre 3 e 100 caracteres
realm.nome.formato=Nome deve começar com letra minúscula e conter apenas letras, números, hífens e underscores
realm.descricao.tamanho=Descrição deve ter no máximo 500 caracteres
realm.status.obrigatorio=Status é obrigatório
realm.empresaid.tamanho=Empresa ID deve ter no máximo 100 caracteres

# Realm Messages
realm.ja.existe=Nome de realm já existe
realm.nao.encontrado=Realm não encontrado
realm.master.nao.pode.desativar=Realm Master não pode ser desativado
realm.master.nao.pode.editar.nome=Nome do Realm Master não pode ser alterado
```

### Mapper Utility (opcional)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/mapper/RealmMapper.java`

```java
package br.com.plataforma.conexaodigital.admin.api.mapper;

import br.com.plataforma.conexaodigital.admin.api.requests.RealmForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmListResponse;
import br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm;
import br.com.plataforma.conexaodigital.gestaorealm.domain.model.StatusRealm;

/**
 * Utilitário de mapeamento entre entidade Realm e DTOs.
 */
public final class RealmMapper {

    private RealmMapper() {
        // Utilitário estático
    }

    /**
     * Converte RealmForm para entidade Realm (nova ou edição).
     */
    public static Realm toEntity(RealmForm form) {
        if (form == null) {
            return null;
        }

        Realm realm;
        if (form.isEdit()) {
            realm = new Realm();
            realm.setId(java.util.UUID.fromString(form.id()));
        } else {
            realm = new Realm();
            realm.setDataCriacao(java.time.LocalDateTime.now());
            realm.setDataAtualizacao(java.time.LocalDateTime.now());
        }

        realm.setNome(form.nome());
        realm.setDescricao(form.descricao());
        realm.setStatus(form.ativo() ? StatusRealm.ATIVO : StatusRealm.INATIVO);
        realm.setEmpresaId(form.empresaId());

        return realm;
    }

    /**
     * Converte entidade Realm para RealmForm (edição).
     */
    public static RealmForm toForm(Realm realm) {
        if (realm == null) {
            return null;
        }

        return new RealmForm(
            realm.getId().toString(),
            realm.getNome(),
            realm.getDescricao(),
            realm.getStatus() == StatusRealm.ATIVO,
            realm.getEmpresaId()
        );
    }
}
```

### Testes Unitários
**Localização:** `src/test/java/br/com/plataforma/conexaodigital/admin/api/requests/RealmFormTest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RealmFormTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testRealmFormValid() {
        RealmForm form = new RealmForm();
        RealmForm validForm = new RealmForm(
            null,
            "empresa-a",
            "Descrição do realm",
            true,
            "COMP-001"
        );

        Set<ConstraintViolation<RealmForm>> violations = validator.validate(validForm);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRealmFormNomeObrigatorio() {
        RealmForm form = new RealmForm(
            null,
            "", // vazio
            "Descrição",
            true,
            "COMP-001"
        );

        Set<ConstraintViolation<RealmForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    void testRealmFormNomeTamanhoMinimo() {
        RealmForm form = new RealmForm(
            null,
            "ab", // menos de 3
            "Descrição",
            true,
            "COMP-001"
        );

        Set<ConstraintViolation<RealmForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRealmFormNomeTamanhoMaximo() {
        RealmForm form = new RealmForm(
            null,
            "a".repeat(101), // mais de 100
            "Descrição",
            true,
            "COMP-001"
        );

        Set<ConstraintViolation<RealmForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRealmFormNomeFormatoInvalido() {
        RealmForm form = new RealmForm(
            null,
            "Nome Invalido", // espaços e maiúsculas
            "Descrição",
            true,
            "COMP-001"
        );

        Set<ConstraintViolation<RealmForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRealmFormEditMode() {
        RealmForm form = new RealmForm(
            "uuid-123",
            "empresa-a",
            "Descrição",
            true,
            "COMP-001"
        );

        assertTrue(form.isEdit());
    }

    @Test
    void testRealmFormCreateMode() {
        RealmForm form = new RealmForm(
            null,
            "empresa-a",
            "Descrição",
            true,
            "COMP-001"
        );

        assertFalse(form.isEdit());
    }
}
```

## Checklist de Validação
- [ ] Package `api/responses` criado
- [ ] Package `api/requests` criado
- [ ] Record `RealmListResponse` criado com todos os campos
- [ ] Record `RealmDetailResponse` criado com todos os campos
- [ ] Record `RealmForm` criado com validações Jakarta Bean Validation
- [ ] Mensagens de validação adicionadas em `messages.properties`
- [ ] Método `from()` implementado em `RealmListResponse`
- [ ] Método `from()` implementado em `RealmDetailResponse`
- [ ] Métodos auxiliares em `RealmForm` (construtor vazio, isEdit)
- [ ] Classe `RealmMapper` criada (opcional)
- [ ] Testes unitários para `RealmForm` criados
- [ ] Todos os testes passam

## Anotações
- Records são imutáveis por padrão, garantindo thread-safety
- Validações de formato de nome seguem o padrão definido no Epic 1: `^[a-z][a-z0-9_-]*$`
- Status usa Boolean para simplificar a UI (true = ATIVO, false = INATIVO)
- O campo `master` indica se é o Realm Master (regra de negócio importante)
- Métodos de fábrica estáticos facilitam a conversão de entidade para DTO
- Testes de validação devem cobrir todos os constraints

## Dependências
- Epic 1 (Gestão de Realms) - entidade Realm já existe
- Epic 9 (Configuração) - Jakarta Bean Validation configurado

## Prioridade
**Alta** - DTOs necessários para todas as outras histórias

## Estimativa
- Implementação: 2 horas
- Testes: 1 hora
- Total: 3 horas
