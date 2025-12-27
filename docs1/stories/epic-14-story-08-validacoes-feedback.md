# User Story: Validações e Feedback de Usuário

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-08

## Descrição
Implementar validações server-side e client-side completas para gestão de roles, incluindo validação de nome único por realm, validações de formato de dados, e feedback visual através de toasts e mensagens de erro amigáveis.

## Critérios de Aceite
- [ ] Validações server-side configuradas no RoleForm
- [ ] Validações client-side (JavaScript) implementadas
- [ ] Mensagens de validação em `messages.properties`
- [ ] Toasts de sucesso exibidos após operações bem-sucedidas
- [ ] Toasts de erro exibidos após falhas
- [ ] Feedback visual de carregamento nos botões
- [ ] Mensagens de erro amigáveis para o usuário
- [ ] Validação de nome único por realm
- [ ] Validação de formato (uppercase, underscores)
- [ ] Validação de tamanho (3-50 caracteres)

## Tarefas
1. Revisar todas as validações Jakarta Bean Validation
2. Adicionar validações customizadas se necessário
3. Implementar validações client-side no JavaScript
4. Melhorar mensagens de validação
5. Implementar toasts de feedback
6. Adicionar loading states aos botões
7. Testar todos os cenários de validação

## Instruções de Implementação

### Mensagens de Validação
**Adicionar a `messages.properties`:**

```properties
# Role Form Validations
role.nome.obrigatorio=Nome da role é obrigatório
role.nome.tamanho=Nome deve ter entre 3 e 50 caracteres
role.nome.formato=Nome deve conter apenas letras maiúsculas e underscores (ex: ADMIN, USER_ROLE)
role.nome.unico=Já existe uma role com este nome neste realm
role.descricao.tamanho=Descrição deve ter no máximo 500 caracteres
role.realm.obrigatorio=Realm é obrigatório
role.ativa.obrigatorio=Status é obrigatório
role.padrao.obrigatorio=Indicador de padrão é obrigatório

# Role Operation Messages
role.criada.sucesso=Role '{0}' criada com sucesso!
role.atualizada.sucesso=Role '{0}' atualizada com sucesso!
role.removida.sucesso=Role removida com sucesso!
role.status.alterado.sucesso=Status da role '{0}' alterado com sucesso!

# Role Error Messages
role.ja.existe=Role já existe neste realm
role.nao.encontrada=Role não encontrada
role.em.uso=Role possui usuários associados e não pode ser removida
role.padrao.nao.pode.remover=Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas
role.padrao.nao.pode.alterar=Não é possível alterar atributos de roles padrão
role.nome.uppercase=Nome deve estar em maiúsculas
role.realm.invalido=Realm selecionado não existe ou está inativo
```

### Validações Client-Side (JavaScript)
**Adicionar a `src/main/resources/static/js/pages/roles-validation.js`:**

```javascript
// Validações client-side para formulário de role
(function() {
    'use strict';

    const form = document.querySelector('.needs-validation');
    const nomeInput = document.getElementById('nome');
    const descricaoInput = document.getElementById('descricao');
    const realmSelect = document.getElementById('realmId');

    // Validação de nome em tempo real
    nomeInput.addEventListener('input', function() {
        const valor = this.value;
        const feedback = this.nextElementSibling.nextElementSibling;

        // Converter para uppercase
        this.value = valor.toUpperCase();

        // Validar formato
        if (!validarNomeFormato(this.value)) {
            mostrarErroValidacao(this, 'Nome deve conter apenas letras e underscores');
        } else if (this.value.length < 3) {
            mostrarErroValidacao(this, 'Nome deve ter pelo menos 3 caracteres');
        } else if (this.value.length > 50) {
            mostrarErroValidacao(this, 'Nome deve ter no máximo 50 caracteres');
        } else {
            limparErroValidacao(this);
        }
    });

    // Validação de descrição em tempo real
    descricaoInput.addEventListener('input', function() {
        if (this.value.length > 500) {
            mostrarErroValidacao(this, 'Descrição deve ter no máximo 500 caracteres');
        } else {
            limparErroValidacao(this);
        }
    });

    // Validação de realm
    realmSelect.addEventListener('change', function() {
        if (this.value === '') {
            mostrarErroValidacao(this, 'Selecione um realm');
        } else {
            limparErroValidacao(this);
        }
    });

    // Validação ao submeter formulário
    form.addEventListener('submit', function(event) {
        if (!validarFormulario()) {
            event.preventDefault();
            event.stopPropagation();
            mostrarErro('Corrija os erros no formulário antes de continuar');
        }
    });

    // Validação de formato de nome
    function validarNomeFormato(nome) {
        return /^[A-Z_]+$/.test(nome);
    }

    // Validação completa do formulário
    function validarFormulario() {
        let valido = true;

        // Validar nome
        if (!nomeInput.value || nomeInput.value.trim() === '') {
            mostrarErroValidacao(nomeInput, 'Nome é obrigatório');
            valido = false;
        } else if (!validarNomeFormato(nomeInput.value)) {
            mostrarErroValidacao(nomeInput, 'Nome deve conter apenas letras e underscores');
            valido = false;
        } else if (nomeInput.value.length < 3) {
            mostrarErroValidacao(nomeInput, 'Nome deve ter pelo menos 3 caracteres');
            valido = false;
        }

        // Validar realm
        if (!realmSelect.value || realmSelect.value === '') {
            mostrarErroValidacao(realmSelect, 'Selecione um realm');
            valido = false;
        }

        return valido;
    }

    // Mostrar erro de validação
    function mostrarErroValidacao(input, mensagem) {
        input.classList.add('is-invalid');

        let feedback = input.parentElement.querySelector('.invalid-feedback');
        if (!feedback) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            input.parentElement.appendChild(feedback);
        }
        feedback.textContent = mensagem;
    }

    // Limpar erro de validação
    function limparErroValidacao(input) {
        input.classList.remove('is-invalid');

        const feedback = input.parentElement.querySelector('.invalid-feedback');
        if (feedback) {
            feedback.remove();
        }
    }
})();
```

### Loading States nos Botões
**Adicionar JavaScript para loading states:**

```javascript
// Adicionar a roles.js ou roles-form.js
(function() {
    'use strict';

    const btnSalvar = document.getElementById('btnSalvar');
    const form = document.querySelector('.needs-validation');

    // Loading state ao submeter
    form.addEventListener('submit', function() {
        if (form.checkValidity()) {
            // Desabilitar botão e mostrar loading
            btnSalvar.disabled = true;
            btnSalvar.innerHTML = '<i class="ti ti-spinner ti-spin me-2"></i>Salvando...';
            btnSalvar.classList.add('btn-loading');
        }
    });

    // Restaurar botão em caso de erro
    window.addEventListener('load', function() {
        // Verificar se há mensagem de erro na página
        const errorAlert = document.querySelector('.alert-danger');
        if (errorAlert && btnSalvar) {
            restaurarBotaoSalvar();
        }
    });

    function restaurarBotaoSalvar() {
        if (btnSalvar) {
            btnSalvar.disabled = false;
            btnSalvar.innerHTML = '<i class="ti ti-device-floppy me-2"></i>Salvar';
            btnSalvar.classList.remove('btn-loading');
        }
    }
})();
```

### Validação Server-Side com MessageSource
**Adicionar ao Controller:**

```java
@Autowired
private MessageSource messageSource;

private String getMessage(String key, Object... args) {
    return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
}

// Exemplo de uso ao criar role
try {
    RoleDetailResponse created = adminRoleService.criarRole(form);
    redirectAttributes.addFlashAttribute("success",
        getMessage("role.criada.sucesso", created.nome()));
} catch (RoleJaExisteException e) {
    redirectAttributes.addFlashAttribute("error", e.getMessage());
    // ...
}
```

### Melhorar Mensagens de Erro na UI
**Adicionar alertas de feedback no template:**

```html
<!-- Alertas de Feedback -->
<div th:if="${success}" class="alert alert-success alert-dismissible fade show mb-3"
     role="alert">
    <i class="ti ti-check-circle me-2"></i>
    <span th:text="${success}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"
            aria-label="Close"></button>
</div>

<div th:if="${error}" class="alert alert-danger alert-dismissible fade show mb-3"
     role="alert">
    <i class="ti ti-x-circle me-2"></i>
    <span th:text="${error}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"
            aria-label="Close"></button>
</div>
```

### Validação de Nome Único em Tempo Real
**Adicionar AJAX validation:**

```javascript
// Validação de nome único em tempo real
let debounceTimer;

nomeInput.addEventListener('input', function() {
    clearTimeout(debounceTimer);

    const nome = this.value;

    // Não validar se estiver vazio
    if (!nome || nome.length < 3) {
        return;
    }

    debounceTimer = setTimeout(() => {
        const realmId = document.getElementById('realmId').value;

        fetch(`/api/v1/admin/roles/validar-nome?nome=${nome}&realmId=${realmId}`)
            .then(response => response.json())
            .then(data => {
                if (!data.valido) {
                    mostrarErroValidacao(nomeInput, 'Este nome já está em uso no realm selecionado');
                    nomeInput.classList.add('is-invalid');
                } else {
                    limparErroValidacao(nomeInput);
                }
            })
            .catch(error => {
                console.error('Erro ao validar nome:', error);
            });
    }, 500); // 500ms debounce
});
```

### Endpoint de Validação de Nome Único
**Adicionar ao Controller:**

```java
@GetMapping("/api/v1/admin/roles/validar-nome")
@ResponseBody
public Map<String, Object> validarNomeUnico(
    @RequestParam String nome,
    @RequestParam String realmId
) {
    boolean valido = !roleRepository.existsByNomeAndRealm_Id(
        nome.toUpperCase(),
        UUID.fromString(realmId)
    );

    return Map.of(
        "valido", valido,
        "mensagem", valido ? null : "Nome já existe neste realm"
    );
}
```

## Checklist de Validação
- [X] Validações server-side configuradas no RoleForm
- [X] Validações client-side (JavaScript) implementadas
- [X] Mensagens de validação em `messages.properties`
- [X] Toasts de sucesso exibidos após operações bem-sucedidas
- [X] Toasts de erro exibidos após falhas
- [X] Feedback visual de carregamento nos botões
- [X] Mensagens de erro amigáveis para o usuário
- [X] Validação de nome único por realm
- [X] Validação de formato (uppercase, underscores)
- [X] Validação de tamanho (3-50 caracteres)
- [X] Validações client-side implementadas
- [X] Validação de nome em tempo real (uppercase)
- [X] Validação de tamanho de nome (3-50)
- [X] Validação de formato (A-Z, underscores)
- [X] Validação de nome único (AJAX)
- [X] Loading states nos botões implementados
- [X] Toasts de sucesso configurados
- [X] Toasts de erro configurados
- [X] Alertas de feedback na página implementados
- [X] Debounce implementado para validação AJAX
- [X] Todas as validações testadas

## Anotações
- Validações devem ocorrer tanto no cliente quanto no servidor
- Cliente fornece feedback imediato, servidor garante segurança
- Debounce evita requisições desnecessárias
- Loading states previnem múltiplos submits
- Mensagens devem ser amigáveis e orientadas à ação
- Code coverage deve ser alto para validações

## Dependências
- Epic 14 Story 02 - DTOs (RoleForm)
- Epic 14 Story 03 - Service Layer (AdminRoleService)
- Epic 14 Story 05 - Formulário (form.html)
- Epic 9 (Configuração) - MessageSource configurado

## Prioridade
**Alta** - Validações e feedback são essenciais para UX

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas

## Resumo do Epic 14

Esta história conclui o Epic 14 (Página de Gestão de Roles) com todas as funcionalidades necessárias:

**Histórias do Epic 14:**
1. ✅ epic-14-story-01: Template da Lista de Roles
2. ✅ epic-14-story-02: DTOs de Role (Java Records)
3. ✅ epic-14-story-03: Backend Service Layer
4. ✅ epic-14-story-04: Controller API (AdminRoleController)
5. ✅ epic-14-story-05: Modal de Criação/Edição
6. ✅ epic-14-story-06: CRUD - Visualizar, Editar, Ativar/Inativar
7. ✅ epic-14-story-07: Definição de Roles Padrão
8. ✅ epic-14-story-08: Validações e Feedback de Usuário

**Arquivos Criados:**
- `src/main/resources/templates/admin/roles/list.html`
- `src/main/resources/templates/admin/roles/form.html`
- `src/main/java/.../admin/api/responses/RoleListResponse.java`
- `src/main/java/.../admin/api/responses/RoleDetailResponse.java`
- `src/main/java/.../admin/api/dto/forms/RoleForm.java`
- `src/main/java/.../admin/api/service/AdminRoleService.java`
- `src/main/java/.../admin/api/controller/AdminRoleController.java`
- `src/main/java/.../admin/api/mapper/RoleMapper.java`
- `src/main/resources/static/js/pages/roles.js`
- `src/main/resources/static/js/pages/roles-validation.js`
- `src/main/java/.../role/domain/exception/RoleExceptions.java`

**Funcionalidades Implementadas:**
- Listagem paginada de roles com filtros
- Criação de novas roles
- Edição de roles existentes
- Remoção de roles (com verificação de uso)
- Ativação/inativação de roles
- Gestão de roles padrão (ADMIN, USER, SERVICE)
- Validações server-side e client-side
- Feedback visual com toasts
- Visualização detalhada de roles
- Contagem de usuários por role

**Tempo Total Estimado do Epic 14:**
- Implementação: 22 horas
- Testes: 12 horas
- **Total: 34 horas (aprox. 4.25 dias úteis)**

## Status do Epic 14

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%
