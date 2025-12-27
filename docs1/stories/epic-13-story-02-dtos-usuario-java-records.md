# User Story: DTOs de Usuário (Java Records)

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-02

## Descrição
Criar os DTOs (Data Transfer Objects) como Java Records para transferência de dados entre a camada de controller e service para gestão de usuários. Incluir validações Jakarta Bean Validation e mensagens de erro em `messages.properties`.

## Critérios de Aceite
- [X] Record `UsuarioListResponse` criado com todos os campos necessários para listagem
- [X] Record `UsuarioForm` criado com validações para criação/edição
- [X] Record `UsuarioDetailResponse` criado com campos completos para visualização
- [X] Jakarta Bean Validation annotations aplicadas corretamente
- [X] Mensagens de validação adicionadas em `messages.properties`
- [X] Métodos auxiliares de conversão implementados (se necessário)
- [X] Imutabilidade garantida (records são imutáveis por padrão)

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.requests`
2. Criar package `br.com.plataforma.conexaodigital.admin.api.responses`
3. Criar record `UsuarioListResponse.java` para listagem
4. Criar record `UsuarioDetailResponse.java` para detalhes
5. Criar record `UsuarioForm.java` para formulários de criação/edição
6. Adicionar mensagens de validação em `src/main/resources/messages.properties`
7. Implementar métodos de mapeamento (mapper) de entidade para DTOs

## Instruções de Implementação

### Package Structure
```
br.com.plataforma.conexaodigital.admin.api
├── controller
│   └── AdminUsuarioController.java
├── requests
│   └── UsuarioForm.java
└── responses
    ├── UsuarioListResponse.java
    └── UsuarioDetailResponse.java
```

### Record: UsuarioListResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/UsuarioListResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de listagem de usuários na página administrativa.
 * Contém os campos essenciais para exibição na tabela.
 */
public record UsuarioListResponse(
    String id,
    String nome,
    String email,
    String cpf,
    String realmNome,
    String realmId,
    List<String> roles,
    String status,
    String iniciais, // iniciais para avatar
    String avatarColor, // cor do avatar (gerada aleatoriamente)
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     * @param usuario entidade Usuario
     * @param realmNome nome do realm do usuário
     * @param roles lista de nomes de roles do usuário
     * @return instância de UsuarioListResponse
     */
    public static UsuarioListResponse from(
        br.com.plataforma.conexaodigital.gestausuario.domain.model.Usuario usuario,
        String realmNome,
        String realmId,
        List<String> roles
    ) {
        // Gerar iniciais
        String iniciais = generateIniciais(usuario.getNome());

        // Gerar cor do avatar (baseada no nome)
        String avatarColor = generateAvatarColor(usuario.getNome());

        return new UsuarioListResponse(
            usuario.getId().toString(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            realmNome,
            realmId,
            roles,
            usuario.getStatus() == br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO ?
                "Ativo" : "Bloqueado",
            iniciais,
            avatarColor,
            usuario.getDataCriacao(),
            usuario.getDataUltimaAtualizacao()
        );
    }

    /**
     * Gera as iniciais do usuário para o avatar.
     */
    private static String generateIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "U";
        }

        String[] partes = nome.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
        } else if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }

        return "U";
    }

    /**
     * Gera uma cor baseada no nome do usuário (determinística).
     */
    private static String generateAvatarColor(String nome) {
        if (nome == null || nome.isBlank()) {
            return "bg-secondary";
        }

        // Hash simples do nome
        int hash = nome.hashCode();
        int index = Math.abs(hash) % 10;

        // Cores disponíveis (Bootstrap colors)
        String[] colors = {
            "bg-primary", "bg-success", "bg-info", "bg-warning",
            "bg-danger", "bg-secondary", "bg-dark", "bg-purple",
            "bg-pink", "bg-teal"
        };

        return colors[index];
    }
}
```

### Record: UsuarioDetailResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/UsuarioDetailResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta detalhada de um usuário.
 * Contém todos os campos da entidade mais informações relacionadas.
 */
public record UsuarioDetailResponse(
    String id,
    String nome,
    String email,
    String cpf,
    String realmNome,
    String realmId,
    String empresaId,
    String tenantId,
    List<String> roles,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao,
    LocalDateTime dataBloqueio,
    LocalDateTime dataReativacao,
    String motivoBloqueio,
    String senhaAlteradaEm // data da última alteração de senha (ou nulo)
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     */
    public static UsuarioDetailResponse from(
        br.com.plataforma.conexaodigital.gestausuario.domain.model.Usuario usuario,
        String realmNome,
        String realmId,
        List<String> roles
    ) {
        return new UsuarioDetailResponse(
            usuario.getId().toString(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            realmNome,
            realmId,
            usuario.getEmpresaId(),
            usuario.getTenantId(),
            roles,
            usuario.getStatus() == br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO ?
                "Ativo" : "Bloqueado",
            usuario.getDataCriacao(),
            usuario.getDataUltimaAtualizacao(),
            usuario.getDataBloqueio(),
            usuario.getDataReativacao(),
            usuario.getMotivoBloqueio(),
            usuario.getDataUltimaAlteracaoSenha() != null ?
                usuario.getDataUltimaAlteracaoSenha().toString() : null
        );
    }
}
```

### Record: UsuarioForm
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/requests/UsuarioForm.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO para formulário de criação/edição de usuário.
 * Contém validações Jakarta Bean Validation.
 */
public record UsuarioForm(
    String id,

    @NotBlank(message = "{usuario.nome.obrigatorio}")
    @Size(min = 3, max = 100, message = "{usuario.nome.tamanho}")
    String nome,

    @NotBlank(message = "{usuario.email.obrigatorio}")
    @Email(message = "{usuario.email.invalido}")
    @Size(max = 255, message = "{usuario.email.tamanho}")
    String email,

    @Pattern(regexp = "^\\d{11}$|^[0-9]*$", message = "{usuario.cpf.formato}")
    String cpf,

    @NotNull(message = "{usuario.realm.obrigatorio}")
    String realmId,

    @NotNull(message = "{usuario.status.obrigatorio}")
    Boolean ativo,

    List<String> rolesIds
) {
    /**
     * Cria instância vazia para novo usuário.
     */
    public UsuarioForm() {
        this(
            null,
            null,
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

    /**
     * Valida se o CPF está preenchido corretamente.
     * CPF é opcional, mas se preenchido deve ter 11 dígitos.
     */
    public boolean isCpfValido() {
        if (cpf == null || cpf.isBlank()) {
            return true; // CPF opcional
        }
        return cpf.matches("^\\d{11}$");
    }
}
```

### Messages Properties
**Localização:** `src/main/resources/messages.properties`

```properties
# Usuario Form Validations
usuario.nome.obrigatorio=Nome do usuário é obrigatório
usuario.nome.tamanho=Nome deve ter entre 3 e 100 caracteres
usuario.email.obrigatorio=Email é obrigatório
usuario.email.invalido=Email deve ser válido
usuario.email.tamanho=Email deve ter no máximo 255 caracteres
usuario.cpf.formato=CPF deve conter exatamente 11 dígitos
usuario.realm.obrigatorio=Realm é obrigatório
usuario.status.obrigatorio=Status é obrigatório
usuario.roles.obrigatorio=Pelo menos uma role deve ser selecionada

# Usuario Messages
usuario.ja.existe=Email já cadastrado no sistema
usuario.nao.encontrado=Usuário não encontrado
usuario.master.nao.pode.bloquear=Usuário administrador do Realm Master não pode ser bloqueado
usuario.senha.nao.igual=Nova senha não pode ser igual à senha atual
```

### Mapper Utility (opcional)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/mapper/UsuarioMapper.java`

```java
package br.com.plataforma.conexaodigital.admin.api.mapper;

import br.com.plataforma.conexaodigital.admin.api.requests.UsuarioForm;
import br.com.plataforma.conexaodigital.admin.api.responses.UsuarioListResponse;
import br.com.plataforma.conexaodigital.gestausuario.domain.model.Usuario;
import br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario;

import java.util.List;

/**
 * Utilitário de mapeamento entre entidade Usuario e DTOs.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
        // Utilitário estático
    }

    /**
     * Converte UsuarioForm para entidade Usuario (nova ou edição).
     */
    public static Usuario toEntity(UsuarioForm form) {
        if (form == null) {
            return null;
        }

        Usuario usuario;
        if (form.isEdit()) {
            usuario = new Usuario();
            usuario.setId(java.util.UUID.fromString(form.id()));
        } else {
            usuario = new Usuario();
            usuario.setDataCriacao(java.time.LocalDateTime.now());
        }

        usuario.setNome(form.nome());
        usuario.setEmail(form.email());
        usuario.setCpf(form.cpf());
        usuario.setStatus(form.ativo() ? StatusUsuario.ATIVO : StatusUsuario.BLOQUEADO);
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        return usuario;
    }

    /**
     * Converte entidade Usuario para UsuarioForm (edição).
     */
    public static UsuarioForm toForm(Usuario usuario, String realmId, List<String> rolesIds) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioForm(
            usuario.getId().toString(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            realmId,
            usuario.getStatus() == StatusUsuario.ATIVO,
            rolesIds
        );
    }
}
```

### Testes Unitários
**Localização:** `src/test/java/br/com/plataforma/conexaodigital/admin/api/requests/UsuarioFormTest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioFormTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testUsuarioFormValid() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "joao.silva@empresa.com.br",
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        Set<ConstraintViolation<UsuarioForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsuarioFormNomeObrigatorio() {
        UsuarioForm form = new UsuarioForm(
            null,
            "", // vazio
            "joao.silva@empresa.com.br",
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        Set<ConstraintViolation<UsuarioForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    void testUsuarioFormNomeTamanhoMinimo() {
        UsuarioForm form = new UsuarioForm(
            null,
            "Jo", // menos de 3
            "joao.silva@empresa.com.br",
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        Set<ConstraintViolation<UsuarioForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUsuarioFormEmailInvalido() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "email-invalido", // formato inválido
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        Set<ConstraintViolation<UsuarioForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("válido")));
    }

    @Test
    void testUsuarioFormCpfFormatoInvalido() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "joao.silva@empresa.com.br",
            "123", // menos de 11 dígitos
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        Set<ConstraintViolation<UsuarioForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUsuarioFormCpfValido() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "joao.silva@empresa.com.br",
            "12345678901", // CPF válido
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        assertTrue(form.isCpfValido());
    }

    @Test
    void testUsuarioFormCpfOpcional() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "joao.silva@empresa.com.br",
            null, // CPF opcional
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        assertTrue(form.isCpfValido());
    }

    @Test
    void testUsuarioFormEditMode() {
        UsuarioForm form = new UsuarioForm(
            "uuid-123",
            "João Silva",
            "joao.silva@empresa.com.br",
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        assertTrue(form.isEdit());
    }

    @Test
    void testUsuarioFormCreateMode() {
        UsuarioForm form = new UsuarioForm(
            null,
            "João Silva",
            "joao.silva@empresa.com.br",
            "12345678901",
            "realm-id-1",
            true,
            List.of("role-id-1")
        );

        assertFalse(form.isEdit());
    }
}
```

## Checklist de Validação
- [ ] Package `api/responses` criado
- [ ] Package `api/requests` criado
- [ ] Record `UsuarioListResponse` criado com todos os campos
- [ ] Record `UsuarioDetailResponse` criado com todos os campos
- [ ] Record `UsuarioForm` criado com validações Jakarta Bean Validation
- [ ] Mensagens de validação adicionadas em `messages.properties`
- [ ] Método `from()` implementado em `UsuarioListResponse`
- [ ] Método `from()` implementado em `UsuarioDetailResponse`
- [ ] Métodos auxiliares em `UsuarioForm` (construtor vazio, isEdit, isCpfValido)
- [ ] Método `generateIniciais()` implementado em `UsuarioListResponse`
- [ ] Método `generateAvatarColor()` implementado em `UsuarioListResponse`
- [ ] Classe `UsuarioMapper` criada (opcional)
- [ ] Testes unitários para `UsuarioForm` criados
- [ ] Todos os testes passam

## Anotações
- Records são imutáveis por padrão, garantindo thread-safety
- CPF é opcional, mas se preenchido deve ter exatamente 11 dígitos
- Iniciais são geradas automaticamente para exibir no avatar
- Cor do avatar é gerada deterministicamente baseada no nome (hash)
- Status usa Boolean para simplificar a UI (true = ATIVO, false = BLOQUEADO)
- Email deve ser único no sistema (validação no backend)
- Métodos de fábrica estáticos facilitam a conversão de entidade para DTO
- Testes de validação devem cobrir todos os constraints

## Dependências
- Epic 2 (Gestão de Usuários) - entidade Usuario já existe
- Epic 1 (Gestão de Realms) - para associação com realm
- Epic 3 (Gestão de Roles) - para associação com roles
- Epic 9 (Configuração) - Jakarta Bean Validation configurado

## Prioridade
**Alta** - DTOs necessários para todas as outras histórias

## Estimativa
- Implementação: 2.5 horas
- Testes: 1.5 horas
- Total: 4 horas
