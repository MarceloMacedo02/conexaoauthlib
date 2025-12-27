# User Story: Associação de Roles (Multi-select)

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-08

## Descrição
Implementar a funcionalidade de associação de múltiplas roles a um usuário através de multi-select dropdown, incluindo CRUD de roles separado, visualização de roles por realm no detalhes do usuário e atualização dinâmica de opções baseada no realm selecionado.

## Critérios de Aceite
- [ ] Campo de Roles implementado como multi-select dropdown
- [ ] Multi-select permite selecionar múltiplas roles
- [ ] Roles são filtradas por realm selecionado
- [ ] Roles disponíveis são carregadas via AJAX
- [ ] Visualização de roles no detalhes do usuário (modal de view)
- [ ] CRUD de roles separado (Epic 14 - referenciado)
- [ ] Badges de roles coloridos por tipo
- [ ] Validação de pelo menos 1 role selecionada
- [ ] Atualização dinâmica de opções ao mudar realm
- [ ] Exibição de nome da role e descrição no multi-select
- [ ] Remoção de role da seleção via click

## Tarefas
1. Implementar campo de multi-select no modal de criação/edição
2. Configurar biblioteca Select2 ou similar para multi-select
3. Implementar carregamento de roles via AJAX por realm
4. Implementar atualização dinâmica de opções ao mudar realm
5. Implementar visualização de roles no modal de detalhes (Story 06)
6. Implementar validação de pelo menos 1 role selecionada
7. Implementar CRUD de roles (Epic 14 - referenciado, não implementado aqui)
8. Adicionar botão para gerenciar roles (link para Epic 14)
9. Testar seleção de múltiplas roles
10. Testar filtro por realm
11. Testar atualização de opções dinâmica
12. Testar visualização de roles no detalhes

## Instruções de Implementação

### Campo de Multi-select de Roles (no Modal de Criação/Edição)
**Atualizar campo no modal da Story 05:**

```html
<!-- Campo Roles (Multi-select com Select2) -->
<div class="col-md-6"
     th:classappend="${#fields.hasErrors('rolesIds')} ? 'has-error' : ''">
    <label class="form-label" for="rolesIds">
        Roles <span class="text-danger">*</span>
    </label>

    <!-- Select2 Multi-select -->
    <select class="form-select select2-multi-roles"
            th:classappend="${#fields.hasErrors('rolesIds')} ? 'is-invalid' : ''"
            id="rolesIds"
            th:field="*{rolesIds}"
            multiple
            required
            style="width: 100%;">
        <!-- Opções carregadas via AJAX -->
    </select>

    <div class="d-flex justify-content-between align-items-center mt-2">
        <div class="form-text text-muted mb-0">
            Selecione pelo menos uma role.
        </div>
        <a th:href="@{/admin/roles}" class="text-primary fs-sm"
           target="_blank" title="Gerenciar Roles (nova aba)">
            <i class="ti ti-settings me-1"></i>Gerenciar Roles
        </a>
    </div>

    <!-- Mensagem de erro -->
    <div class="invalid-feedback"
         th:if="${#fields.hasErrors('rolesIds')}"
         th:errors="*{rolesIds}"></div>
</div>
```

### Visualização de Roles no Modal de Detalhes (Story 06)
**Atualizar seção no modal de visualização:**

```html
<!-- Roles no Modal de Visualização -->
<div class="col-md-12">
    <label class="fw-semibold text-muted">Roles</label>
    <div id="viewUsuarioRoles" class="mb-0">
        <div class="row g-2">
            <div th:each="role : ${usuario.roles}" class="col-md-6">
                <div class="card p-2 mb-0 border-light">
                    <div class="d-flex align-items-center gap-2">
                        <span th:class="|badge me-auto| + ${getRoleBadgeClass(role.nome)}"
                              th:text="${role.nome}">
                            ADMIN
                        </span>
                        <span class="text-muted small flex-grow-1 text-truncate"
                              th:text="${role.descricao ?: 'Sem descrição'}"
                              title="${role.descricao}">
                            Role com permissões completas
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Helper para classes de badges (no head do template) -->
<script th:inline="javascript">
/*<![CDATA[*/
function getRoleBadgeClass(roleName) {
    const roleColors = {
        'ADMIN': 'bg-danger-subtle text-danger',
        'USER': 'bg-info-subtle text-info',
        'SERVICE': 'bg-warning-subtle text-warning'
    };
    return roleColors[roleName] || 'bg-secondary-subtle text-secondary';
}
/*]]>*/
</script>
```

### JavaScript para Multi-select de Roles com Select2
**Adicionar ao fragment `<th:block layout:fragment="javascripts>`:**

```javascript
<!-- Select2 Multi-select for Roles -->
<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css"
      rel="stylesheet" />
<link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css"
      rel="stylesheet" />

<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/i18n/pt-BR.js"></script>

<script>
document.addEventListener('DOMContentLoaded', function() {

    // Inicializar Select2 no campo de roles
    $('#rolesIds').select2({
        theme: 'bootstrap-5',
        language: 'pt-BR',
        placeholder: 'Selecione as roles...',
        allowClear: false,
        closeOnSelect: false,
        width: '100%',
        ajax: {
            url: function(params) {
                const realmId = $('#realmId').val();
                if (!realmId) {
                    return null;
                }
                return `/admin/usuarios/api/roles/${realmId}`;
            },
            dataType: 'json',
            delay: 250,
            data: function(params) {
                return {
                    q: params.term || '', // termo de busca
                    page: params.page || 1
                };
            },
            processResults: function(data, params) {
                // Format response para Select2
                return {
                    results: data.map(role => ({
                        id: role.id,
                        text: role.nome,
                        description: role.descricao
                    })),
                    pagination: {
                        more: false // Sem paginação no lado do cliente
                    }
                };
            },
            cache: true
        },
        templateResult: function(role) {
            if (!role.loading) {
                const badgeClass = getRoleBadgeClass(role.text);

                return $(`
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge ${badgeClass}">${role.text}</span>
                        <span class="text-muted small flex-grow-1">
                            ${role.description || 'Sem descrição'}
                        </span>
                    </div>
                `);
            }
            return role.text;
        },
        templateSelection: function(role) {
            const badgeClass = getRoleBadgeClass(role.text);
            return $(`<span class="badge ${badgeClass}">${role.text}</span>`);
        },
        minimumInputLength: 0 // Não requer digitação para mostrar opções
    });

    // Atualizar options quando realm mudar
    $('#realmId').on('change', function() {
        const realmId = $(this).val();
        const rolesSelect = $('#rolesIds');

        // Limpar seleção atual
        rolesSelect.val(null).trigger('change');

        if (!realmId) {
            // Se não selecionou realm, limpar options
            rolesSelect.empty().trigger('change');
            return;
        }

        // Carregar roles do realm selecionado
        rolesSelect.data('ajax').url = `/admin/usuarios/api/roles/${realmId}`;
        rolesSelect.trigger('change.select2');
    });

    // Validação: pelo menos 1 role selecionada
    const usuarioForm = document.getElementById('usuarioForm');
    if (usuarioForm) {
        usuarioForm.addEventListener('submit', function(event) {
            const selectedRoles = $('#rolesIds').val();
            if (!selectedRoles || selectedRoles.length === 0) {
                event.preventDefault();
                event.stopPropagation();

                alert('Por favor, selecione pelo menos uma role para o usuário.');
                return false;
            }
        });
    }

    // Helper para classes de badges
    function getRoleBadgeClass(roleName) {
        const roleColors = {
            'ADMIN': 'bg-danger-subtle text-danger',
            'USER': 'bg-info-subtle text-info',
            'SERVICE': 'bg-warning-subtle text-warning'
        };
        return roleColors[roleName] || 'bg-secondary-subtle text-secondary';
    }

});
</script>
```

### Validação Lado do Servidor
**Adicionar validação no DTO `UsuarioForm` (Story 02):**

```java
/**
 * Valida se pelo menos uma role foi selecionada.
 */
public boolean isRolesSelecionadas() {
    return rolesIds != null && !rolesIds.isEmpty();
}
```

**Adicionar validator customizado:**

```java
package br.com.plataforma.conexaodigital.admin.api.validation;

import br.com.plataforma.conexaodigital.admin.api.requests.UsuarioForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RolesSelecionadasValidator
    implements ConstraintValidator<RolesSelecionadas, UsuarioForm> {

    @Override
    public boolean isValid(UsuarioForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        return form.isRolesSelecionadas();
    }
}
```

**Adicionar anotação customizada:**

```java
package br.com.plataforma.conexaodigital.admin.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RolesSelecionadasValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesSelecionadas {
    String message() default "{usuario.roles.obrigatorio}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Aplicar anotação no DTO:**

```java
@RolesSelecionadas
public record UsuarioForm(
    // ... outros campos ...
    List<String> rolesIds
) {
    // ... métodos ...
}
```

### Visualização de Roles no Modal de Edição (Pre-fill)
**JavaScript para carregar roles selecionadas ao abrir modal de edição:**

```javascript
// Carregar usuário existente no modal de edição (já implementado na Story 05)
function carregarEdicao(usuarioId) {
    // Buscar dados do usuário
    fetch(`/admin/usuarios/${usuarioId}`)
        .then(response => response.json())
        .then(usuario => {
            // Preencher campos básicos
            document.getElementById('nome').value = usuario.nome;
            document.getElementById('email').value = usuario.email;
            document.getElementById('cpf').value = usuario.cpf;
            document.getElementById('realmId').value = usuario.realmId;
            document.getElementById('ativo').value =
                usuario.status === 'Ativo' ? 'true' : 'false';

            // Carregar roles do realm e selecionar as do usuário
            const realmId = usuario.realmId;

            // Limpar seleção atual
            $('#rolesIds').val(null).trigger('change');

            // Carregar roles do realm
            fetch(`/admin/usuarios/api/roles/${realmId}`)
                .then(response => response.json())
                .then(roles => {
                    // Atualizar options do select
                    $('#rolesIds').empty();
                    roles.forEach(role => {
                        const option = new Option(
                            role.nome,
                            role.id,
                            false,
                            usuario.roles.includes(role.nome)
                        );
                        $('#rolesIds').append(option);
                    });

                    // Selecionar roles do usuário
                    const selectedRolesIds = roles
                        .filter(role => usuario.roles.includes(role.nome))
                        .map(role => role.id);

                    $('#rolesIds').val(selectedRolesIds).trigger('change');
                });

            // Mostrar modal
            addUsuarioModal.show();
        })
        .catch(error => {
            mostrarToast('error', 'Erro ao carregar dados do usuário');
            console.error(error);
        });
}
```

### Visualização de Roles na Tabela (Story 01)
**Atualizar coluna de roles na tabela:**

```html
<td>
    <div class="d-flex flex-wrap gap-1">
        <th:block th:each="role : ${usuario.roles}">
            <span th:class="|badge badge-label| + ${getRoleBadgeClass(role)}"
                  th:text="${role}">
                ADMIN
            </span>
        </th:block>
    </div>
</td>
```

## Checklist de Validação
- [ ] Campo de roles implementado como multi-select
- [ ] Biblioteca Select2 configurada
- [ ] Multi-select permite selecionar múltiplas roles
- [ ] Roles são carregadas via AJAX por realm
- [ ] Atualização dinâmica de options ao mudar realm
- [ ] Template de exibição de role com nome e descrição
- [ ] Template de seleção de role com badge colorido
- [ ] Validação de pelo menos 1 role selecionada
- [ ] Validação lado do servidor implementada
- [ ] Visualização de roles no modal de detalhes
- [ ] Visualização de roles com cards informativos
- [ ] Badges de roles coloridos por tipo
- [ ] Link para gerenciar roles (Epic 14) implementado
- [ ] Pre-fill de roles no modal de edição
- [ ] Roles são filtradas por realm
- [ ] Busca de roles funciona
- [ ] Remoção de role da seleção funciona
- [ ] Exibição de roles na tabela com badges

## Anotações
- Multi-select usa biblioteca Select2 (já disponível no projeto)
- Roles são filtradas dinamicamente por realm selecionado
- Template de exibição mostra nome e descrição da role
- Badges de roles usam cores diferentes (ADMIN=vermelho, USER=azul, SERVICE=amarelo)
- CRUD de roles é implementado separadamente no Epic 14
- Link "Gerenciar Roles" abre nova aba Epic 14
- Validação server-side garante que pelo menos 1 role está selecionada
- Em modo de edição, roles existentes são pre-selecionadas automaticamente
- AJAX cache melhora performance de carregamento de roles
- Busca de roles permite filtrar por nome
- Visualização de cards no modal de detalhes fornece mais contexto

## Dependências
- Story 01 (Template da Lista de Usuários) - coluna de roles
- Story 02 (DTOs de Usuário) - UsuarioForm com rolesIds
- Story 03 (Backend Service Layer) - método listarRolesDisponiveis
- Story 04 (Controller API) - endpoint API de roles
- Story 05 (Modal de Criação/Edição) - campo de roles
- Story 06 (Funcionalidades CRUD) - visualização no detalhes
- Epic 3 (Gestão de Roles) - dados de roles
- Epic 14 (Página de Gestão de Roles) - CRUD de roles (referenciado)

## Prioridade
**Média** - Funcionalidade importante mas não crítica

## Estimativa
- Implementação: 3 horas
- Testes: 1.5 horas
- Total: 4.5 horas
