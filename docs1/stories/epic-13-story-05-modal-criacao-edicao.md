# User Story: Modal de Criação/Edição (Thymeleaf)

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-05

## Descrição
Criar o modal Bootstrap 5 para criação e edição de usuários (`addUsuarioModal`) com formulário HTML, campos validados, botões de ação, multi-select de roles e tratamento de erros de validação. O modal deve ser reutilizável para criação e edição, preenchendo dados automaticamente em modo de edição.

## Critérios de Aceite
- [ ] Modal Bootstrap 5 criado no template de listagem
- [ ] Formulário HTML com todos os campos implementados
- [ ] Campo Nome (required, 3-100 chars) validado
- [ ] Campo Email (required, validação de formato) implementado
- [ ] Campo CPF (opcional, validação de formato de 11 dígitos) implementado
- [ ] Campo Realm (select dropdown, filtered by logged-in user realm or admin sees all realms) implementado
- [ ] Campo Status (select: Ativo/Bloqueado) implementado
- [ ] Campo Roles (multi-select para associar múltiplas roles) implementado
- [ ] Botão "Salvar" com ícone implementado
- [ ] Botão "Cancelar" implementado
- [ ] Validações visuais com Bootstrap classes
- [ ] Mensagens de erro exibidas abaixo dos campos
- [ ] CSRF token incluído no formulário
- [ ] Título do modal dinâmico (Criar vs Editar)
- [ ] Modal fecha automaticamente em sucesso
- [ ] Campos preenchidos em modo de edição
- [ ] Password field (readonly, masked) para edição

## Tarefas
1. Criar estrutura do modal Bootstrap 5
2. Adicionar campos do formulário com Thymeleaf bindings
3. Configurar validações visuais (`has-error`, `invalid-feedback`)
4. Implementar campo Realm como select dropdown
5. Implementar campo Status como select dropdown
6. Implementar campo Roles como multi-select (Select2 ou similar)
7. Implementar campo CPF com máscara de 11 dígitos
8. Implementar lógica de preenchimento em modo de edição
9. Adicionar CSRF token
10. Configurar botões de ação
11. Implementar JavaScript para manipulação do modal
12. Adicionar tratamento de erros de validação
13. Implementar AJAX para carregar realms disponíveis
14. Implementar AJAX para carregar roles por realm selecionado
15. Testar criação de novo usuário
16. Testar edição de usuário existente

## Instruções de Implementação

### Modal HTML
**Adicionar ao final do template `admin/usuarios/list.html` (após o fechamento do card):**

```html
<!-- Modal de Criação/Edição de Usuário -->
<div class="modal fade" id="addUsuarioModal" tabindex="-1"
     aria-labelledby="addUsuarioModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addUsuarioModalLabel">
                    <span th:if="${editMode}" th:text="${pageTitle}">Editar Usuário</span>
                    <span th:unless="${editMode}">Novo Usuário</span>
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <!-- Formulário com Thymeleaf -->
            <form th:action="@{/admin/usuarios}"
                  th:object="${usuarioForm}"
                  th:method="${editMode ? 'put' : 'post'}"
                  id="usuarioForm"
                  class="needs-validation"
                  novalidate>

                <!-- Campo oculto para ID (edição) -->
                <input type="hidden" th:field="*{id}" th:if="${editMode}" />

                <div class="modal-body">
                    <div class="row g-3">

                        <!-- Nome do Usuário -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('nome')} ? 'has-error' : ''">
                            <label class="form-label" for="nome">
                                Nome <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   class="form-control"
                                   th:classappend="${#fields.hasErrors('nome')} ? 'is-invalid' : ''"
                                   id="nome"
                                   th:field="*{nome}"
                                   placeholder="Ex: João Silva"
                                   required
                                   minlength="3"
                                   maxlength="100" />

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('nome')}"
                                 th:errors="*{nome}"></div>
                        </div>

                        <!-- Email -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('email')} ? 'has-error' : ''">
                            <label class="form-label" for="email">
                                Email <span class="text-danger">*</span>
                            </label>
                            <input type="email"
                                   class="form-control"
                                   th:classappend="${#fields.hasErrors('email')} ? 'is-invalid' : ''"
                                   id="email"
                                   th:field="*{email}"
                                   placeholder="Ex: joao.silva@empresa.com.br"
                                   required
                                   maxlength="255" />

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('email')}"
                                 th:errors="*{email}"></div>
                        </div>

                        <!-- CPF -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('cpf')} ? 'has-error' : ''">
                            <label class="form-label" for="cpf">CPF (Opcional)</label>
                            <input type="text"
                                   class="form-control cpf-mask"
                                   th:classappend="${#fields.hasErrors('cpf')} ? 'is-invalid' : ''"
                                   id="cpf"
                                   th:field="*{cpf}"
                                   placeholder="Ex: 12345678901"
                                   maxlength="11"
                                   pattern="^\\d{11}$" />

                            <div class="form-text text-muted">
                                Deve conter exatamente 11 dígitos numéricos.
                            </div>

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('cpf')}"
                                 th:errors="*{cpf}"></div>
                        </div>

                        <!-- Realm -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('realmId')} ? 'has-error' : ''">
                            <label class="form-label" for="realmId">
                                Realm <span class="text-danger">*</span>
                            </label>
                            <select class="form-select realm-select"
                                    th:classappend="${#fields.hasErrors('realmId')} ? 'is-invalid' : ''"
                                    id="realmId"
                                    th:field="*{realmId}"
                                    required>
                                <option value="" disabled selected>Selecione um realm</option>
                                <option th:each="realm : ${realms}"
                                        th:value="${realm.id}"
                                        th:text="${realm.nome}">
                                    Realm
                                </option>
                            </select>

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('realmId')}"
                                 th:errors="*{realmId}"></div>
                        </div>

                        <!-- Status -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('ativo')} ? 'has-error' : ''">
                            <label class="form-label" for="ativo">
                                Status <span class="text-danger">*</span>
                            </label>
                            <select class="form-select"
                                    th:classappend="${#fields.hasErrors('ativo')} ? 'is-invalid' : ''"
                                    id="ativo"
                                    th:field="*{ativo}"
                                    required>
                                <option th:value="true"
                                        th:selected="${usuarioForm?.ativo == true}">
                                    Ativo
                                </option>
                                <option th:value="false"
                                        th:selected="${usuarioForm?.ativo == false}">
                                    Bloqueado
                                </option>
                            </select>

                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('ativo')}"
                                 th:errors="*{ativo}"></div>
                        </div>

                        <!-- Roles (Multi-select) -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('rolesIds')} ? 'has-error' : ''">
                            <label class="form-label" for="rolesIds">
                                Roles <span class="text-danger">*</span>
                            </label>
                            <select class="form-select role-multi-select"
                                    th:classappend="${#fields.hasErrors('rolesIds')} ? 'is-invalid' : ''"
                                    id="rolesIds"
                                    th:field="*{rolesIds}"
                                    multiple
                                    required>
                                <!-- Opções carregadas via AJAX -->
                            </select>

                            <div class="form-text text-muted">
                                Selecione pelo menos uma role.
                            </div>

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('rolesIds')}"
                                 th:errors="*{rolesIds}"></div>
                        </div>

                        <!-- Alerta para modo de edição -->
                        <div class="col-md-12" th:if="${editMode}">
                            <div class="alert alert-warning d-flex align-items-center">
                                <i class="ti ti-alert-triangle fs-4 me-3"></i>
                                <div>
                                    <h6 class="alert-heading mb-1">Modo de Edição</h6>
                                    <p class="mb-0 fs-sm">
                                        Alterações de email e CPF requerem reconfirmação.
                                        Senha não pode ser alterada neste modal (use reset de senha).
                                    </p>
                                </div>
                            </div>
                        </div>

                    </div> <!-- end row -->
                </div> <!-- end modal-body -->

                <div class="modal-footer">
                    <button type="button" class="btn btn-light"
                            data-bs-dismiss="modal">
                        <i class="ti ti-x me-2"></i>Cancelar
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="ti ti-save me-2"></i>
                        <span th:text="${editMode ? 'Salvar' : 'Criar'}">Criar</span>
                    </button>
                </div> <!-- end modal-footer -->

            </form>
        </div>
    </div>
</div>
```

### JavaScript para Modal
**Adicionar ao fragment `<th:block layout:fragment="javascripts">`:**

```javascript
<!-- Usuario Form JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function() {

    // Máscara de CPF (11 dígitos)
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function() {
            this.value = this.value.replace(/\D/g, ''); // Remove não numéricos
            if (this.value.length > 11) {
                this.value = this.value.substring(0, 11);
            }
        });
    }

    // Multi-select de Roles (usando Select2 se disponível)
    const roleSelect = document.getElementById('rolesIds');
    const realmSelect = document.getElementById('realmId');

    // Carregar realms disponíveis
    function loadRealms() {
        fetch('/api/v1/admin/realms')
            .then(response => response.json())
            .then(realms => {
                realmSelect.innerHTML = '<option value="" disabled selected>Selecione um realm</option>';
                realms.forEach(realm => {
                    const option = document.createElement('option');
                    option.value = realm.id;
                    option.textContent = realm.nome;
                    realmSelect.appendChild(option);
                });
            });
    }

    // Carregar roles por realm
    function loadRoles(realmId) {
        if (!realmId) {
            roleSelect.innerHTML = '<option value="" disabled selected>Selecione um realm primeiro</option>';
            return;
        }

        fetch(`/admin/usuarios/api/roles/${realmId}`)
            .then(response => response.json())
            .then(roles => {
                roleSelect.innerHTML = '';
                roles.forEach(role => {
                    const option = document.createElement('option');
                    option.value = role.id;
                    option.textContent = role.nome;
                    roleSelect.appendChild(option);
                });
            });
    }

    // Event listener para mudança de realm
    if (realmSelect) {
        realmSelect.addEventListener('change', function() {
            loadRoles(this.value);
        });
    }

    // Inicializar realms ao carregar a página
    if (realmSelect) {
        loadRealms();
    }

    // Auto-focus no campo nome ao abrir modal
    const addUsuarioModal = document.getElementById('addUsuarioModal');
    if (addUsuarioModal) {
        addUsuarioModal.addEventListener('shown.bs.modal', function() {
            const nomeInput = document.getElementById('nome');
            if (nomeInput) {
                nomeInput.focus();
            }
        });
    }

    // Validar formulário antes de submeter
    const usuarioForm = document.getElementById('usuarioForm');
    if (usuarioForm) {
        usuarioForm.addEventListener('submit', function(event) {
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            // Validar que pelo menos uma role está selecionada
            const selectedRoles = Array.from(roleSelect.selectedOptions);
            if (selectedRoles.length === 0) {
                event.preventDefault();
                roleSelect.classList.add('is-invalid');

                // Mostrar feedback se não existir
                let feedback = roleSelect.parentElement.querySelector('.invalid-feedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback d-block';
                    feedback.textContent = 'Pelo menos uma role deve ser selecionada';
                    roleSelect.parentElement.appendChild(feedback);
                }
            } else {
                roleSelect.classList.remove('is-invalid');
            }

            this.classList.add('was-validated');
        }, false);
    }

});
</script>
```

### CSS Adicional (se necessário)
**Adicionar ao template ou CSS separado:**

```css
/* Estilos para multi-select de roles */
.role-multi-select {
    min-height: 120px;
}

/* Feedback de validação */
.is-invalid {
    border-color: #dc3545 !important;
}

.invalid-feedback {
    display: none;
    font-size: 0.875rem;
    color: #dc3545;
    margin-top: 0.25rem;
}

.is-invalid + .invalid-feedback,
.invalid-feedback.d-block {
    display: block;
}

/* Alertas no modal */
.alert {
    margin-bottom: 0;
}
```

## Checklist de Validação
- [ ] Modal Bootstrap 5 criado no template
- [ ] Campo Nome implementado com validações
- [ ] Campo Email implementado com validação de email
- [ ] Campo CPF implementado com máscara de 11 dígitos
- [ ] Campo Realm implementado como select dropdown
- [ ] Campo Status (select) implementado
- [ ] Campo Roles (multi-select) implementado
- [ ] Validations visuais (`is-invalid`, `invalid-feedback`)
- [ ] Mensagens de erro exibidas com Thymeleaf
- [ ] CSRF token incluído no formulário
- [ ] Botões Submit e Cancel implementados
- [ ] Título do modal dinâmico (Criar vs Editar)
- [ ] Campos preenchidos em modo de edição
- [ ] JavaScript para máscara de CPF implementado
- [ ] JavaScript para carregar realms via AJAX implementado
- [ ] JavaScript para carregar roles por realm implementado
- [ ] JavaScript para validação em tempo real implementado
- [ ] JavaScript para auto-focus no campo nome implementado
- [ ] Validação HTML5 configurada (required, pattern, maxlength)
- [ ] Multi-select de roles funcional
- [ ] Alerta informativo em modo de edição

## Anotações
- Modal usa `th:method` dinâmico para POST (criação) ou PUT (edição)
- Realm é carregado via AJAX do endpoint `/api/v1/admin/realms`
- Roles são carregadas via AJAX do endpoint `/admin/usuarios/api/roles/{realmId}`
- CPF é opcional, mas se preenchido deve ter 11 dígitos
- Multi-select de roles permite selecionar múltiplas roles (ADMIN, USER, SERVICE)
- Validations HTML5 + Thymeleaf para validação server-side
- CSRF token é obrigatório para segurança
- JavaScript deve melhorar UX (máscaras, validação em tempo real, AJAX)
- Feedback visual imediato para erros de validação
- Senha não é alterada no modal de edição (usar reset administrativo de senha - Story 07)

## Dependências
- Story 01 (Template da Lista de Usuários) - template base
- Story 02 (DTOs de Usuário) - UsuarioForm
- Story 03 (Backend Service Layer) - validações no servidor
- Story 04 (Controller API) - endpoints POST/PUT e API de roles
- Epic 3 (Gestão de Roles) - para dados de roles
- Epic 1 (Gestão de Realms) - para dados de realms

## Prioridade
**Alta** - Modal necessário para criação/edição de usuários

## Estimativa
- Implementação: 4 horas
- Testes: 1 hora
- Total: 5 horas
