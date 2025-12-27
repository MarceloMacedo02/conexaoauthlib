# User Story: Modal de Criação/Edição (Thymeleaf)

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-05

## Descrição
Criar o modal Bootstrap 5 para criação e edição de realms (`addRealmModal`) com formulário HTML, campos validados, botões de ação e tratamento de erros de validação. O modal deve ser reutilizável para criação e edição, preenchendo dados automaticamente em modo de edição.

## Critérios de Aceite
- [X] Modal Bootstrap 5 criado no template de listagem
- [X] Formulário HTML com todos os campos implementados
- [X] Campo Nome (required, 3-100 chars) validado
- [X] Campo Descrição (opcional, max 500 chars) implementado
- [X] Campo Empresa ID (opcional, max 100 chars) implementado
- [X] Campo Status (select: Ativo/Inativo) implementado
- [X] Realm Master indicator implementado (read-only)
- [X] Validações visuais com Bootstrap classes
- [X] Mensagens de erro exibidas abaixo dos campos
- [X] CSRF token incluído no formulário
- [X] Botões Submit e Cancel implementados
- [X] Título do modal dinâmico (Criar vs Editar)
- [X] Modal fecha automaticamente em sucesso
- [X] Campos preenchidos em modo de edição

## Tarefas
1. Criar estrutura do modal Bootstrap 5
2. Adicionar campos do formulário com Thymeleaf bindings
3. Configurar validações visuais (`has-error`, `invalid-feedback`)
4. Implementar lógica de preenchimento em modo de edição
5. Adicionar CSRF token
6. Configurar botões de ação
7. Implementar JavaScript para manipulação do modal
8. Adicionar tratamento de erros de validação
9. Testar criação de novo realm
10. Testar edição de realm existente

## Instruções de Implementação

### Modal HTML
**Adicionar ao final do template `admin/realms/list.html` (após o fechamento do card):**

```html
<!-- Modal de Criação/Edição de Realm -->
<div class="modal fade" id="addRealmModal" tabindex="-1"
     aria-labelledby="addRealmModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addRealmModalLabel">
                    <span th:if="${editMode}" th:text="${pageTitle}">Editar Realm</span>
                    <span th:unless="${editMode}">Novo Realm</span>
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <!-- Formulário com Thymeleaf -->
            <form th:action="@{/admin/realms}"
                  th:object="${realmForm}"
                  th:method="${editMode ? 'put' : 'post'}"
                  id="realmForm"
                  class="needs-validation"
                  novalidate>

                <!-- Campo oculto para ID (edição) -->
                <input type="hidden" th:field="*{id}" th:if="${editMode}" />

                <div class="modal-body">
                    <div class="row g-3">

                        <!-- Nome do Realm -->
                        <div class="col-md-12"
                             th:classappend="${#fields.hasErrors('nome')} ? 'has-error' : ''">
                            <label class="form-label" for="nome">
                                Nome do Realm <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   class="form-control"
                                   th:classappend="${#fields.hasErrors('nome')} ? 'is-invalid' : ''"
                                   id="nome"
                                   th:field="*{nome}"
                                   placeholder="Ex: empresa-a"
                                   required
                                   pattern="^[a-z][a-z0-9_-]*$"
                                   minlength="3"
                                   maxlength="100"
                                   th:readonly="${isMaster}" />

                            <!-- Mensagem de erro -->
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('nome')}"
                                 th:errors="*{nome}"></div>

                            <!-- Hint de formato -->
                            <div class="form-text text-muted">
                                Deve começar com letra minúscula e conter apenas letras, números, hífens e underscores.
                            </div>

                            <!-- Alerta para Realm Master -->
                            <div th:if="${isMaster}" class="alert alert-warning mt-2">
                                <i class="ti ti-alert-triangle me-2"></i>
                                Nome do Realm Master não pode ser alterado.
                            </div>
                        </div>

                        <!-- Descrição -->
                        <div class="col-md-12"
                             th:classappend="${#fields.hasErrors('descricao')} ? 'has-error' : ''">
                            <label class="form-label" for="descricao">Descrição</label>
                            <textarea class="form-control"
                                      th:classappend="${#fields.hasErrors('descricao')} ? 'is-invalid' : ''"
                                      id="descricao"
                                      th:field="*{descricao}"
                                      rows="3"
                                      placeholder="Descrição opcional do realm"
                                      maxlength="500"></textarea>

                            <div class="form-text">
                                <span th:text="${#strings.length(realmForm?.descricao ?: '')}">0</span>/500 caracteres
                            </div>

                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('descricao')}"
                                 th:errors="*{descricao}"></div>
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
                                        th:selected="${realmForm?.ativo == true}">
                                    Ativo
                                </option>
                                <option th:value="false"
                                        th:selected="${realmForm?.ativo == false}">
                                    Inativo
                                </option>
                            </select>

                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('ativo')}"
                                 th:errors="*{ativo}"></div>
                        </div>

                        <!-- Empresa ID -->
                        <div class="col-md-6"
                             th:classappend="${#fields.hasErrors('empresaId')} ? 'has-error' : ''">
                            <label class="form-label" for="empresaId">Empresa ID (Opcional)</label>
                            <input type="text"
                                   class="form-control"
                                   th:classappend="${#fields.hasErrors('empresaId')} ? 'is-invalid' : ''"
                                   id="empresaId"
                                   th:field="*{empresaId}"
                                   placeholder="ID da empresa no sistema externo"
                                   maxlength="100" />

                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('empresaId')}"
                                 th:errors="*{empresaId}"></div>

                            <div class="form-text text-muted">
                                Identificador opcional para integração com sistemas externos.
                            </div>
                        </div>

                        <!-- Realm Master Indicator (Read-only) -->
                        <div class="col-md-12" th:if="${isMaster}">
                            <div class="alert alert-primary d-flex align-items-center">
                                <i class="ti ti-crown fs-4 me-3"></i>
                                <div>
                                    <h6 class="alert-heading mb-1">Realm Master</h6>
                                    <p class="mb-0 fs-sm">
                                        Este é o realm principal do sistema e não pode ser desativado.
                                        Algumas operações são restritas.
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
<!-- Realm Form JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function() {

    // Auto-ajuste da altura do textarea de descrição
    const descricaoTextarea = document.getElementById('descricao');
    if (descricaoTextarea) {
        descricaoTextarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }

    // Contador de caracteres da descrição
    if (descricaoTextarea) {
        descricaoTextarea.addEventListener('input', function() {
            const currentLength = this.value.length;
            const maxLength = 500;
            const counter = this.parentElement.querySelector('.form-text');
            if (counter) {
                counter.textContent = `${currentLength}/${maxLength} caracteres`;
            }
        });
    }

    // Validação do formato de nome em tempo real
    const nomeInput = document.getElementById('nome');
    if (nomeInput && !nomeInput.readOnly) {
        nomeInput.addEventListener('input', function() {
            const pattern = /^[a-z][a-z0-9_-]*$/;
            if (this.value.length > 0 && !pattern.test(this.value)) {
                this.classList.add('is-invalid');
                // Mostrar feedback se não existir
                let feedback = this.parentElement.querySelector('.invalid-feedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback d-block';
                    feedback.textContent = 'Nome deve começar com letra e conter apenas letras, números, hífens e underscores em minúsculas';
                    this.parentElement.appendChild(feedback);
                }
            } else {
                this.classList.remove('is-invalid');
                const feedback = this.parentElement.querySelector('.invalid-feedback');
                if (feedback && this.value.length >= 3) {
                    feedback.remove();
                }
            }
        });
    }

    // Auto-focus no campo nome ao abrir modal
    const addRealmModal = document.getElementById('addRealmModal');
    if (addRealmModal) {
        addRealmModal.addEventListener('shown.bs.modal', function() {
            const nomeInput = document.getElementById('nome');
            if (nomeInput && !nomeInput.readOnly) {
                nomeInput.focus();
            }
        });
    }

    // Validar formulário antes de submeter
    const realmForm = document.getElementById('realmForm');
    if (realmForm) {
        realmForm.addEventListener('submit', function(event) {
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            this.classList.add('was-validated');
        }, false);
    }

});
</script>
```

### Alternativa: Template Separado para Modal
**Se preferir, criar template separado `admin/realms/form.html`:**

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/vertical}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta(${pageTitle})}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Page Title -->
        <div th:replace="~{partials/page-title :: page-title(
                 'Apps',
                 'Gestão Realms',
                 ${pageTitle}
                )}">
        </div>

        <!-- Flash Messages -->
        <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="ti ti-check me-2"></i>
            <span th:text="${success}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <div th:if="${error}" class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="ti ti-alert-circle me-2"></i>
            <span th:text="${error}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <!-- Form Card -->
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0" th:text="${pageTitle}">Formulário</h5>
                    </div>
                    <div class="card-body">
                        <!-- O mesmo conteúdo do modal acima -->
                        <!-- [Colar o formulário HTML do modal aqui] -->
                    </div>
                </div>
            </div>

            <!-- Sidebar Info -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Informações</h5>
                    </div>
                    <div class="card-body">
                        <div th:if="${isMaster}" class="alert alert-primary">
                            <i class="ti ti-crown me-2"></i>
                            <strong>Realm Master</strong><br>
                            <small>Este é o realm principal do sistema.</small>
                        </div>

                        <h6 class="fw-semibold mb-2">Regras de Nomes</h6>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item px-0 d-flex align-items-start">
                                <i class="ti ti-check text-success me-2 mt-1"></i>
                                <span>Começar com letra minúscula</span>
                            </li>
                            <li class="list-group-item px-0 d-flex align-items-start">
                                <i class="ti ti-check text-success me-2 mt-1"></i>
                                <span>Conter apenas letras, números, hífens e underscores</span>
                            </li>
                            <li class="list-group-item px-0 d-flex align-items-start">
                                <i class="ti ti-check text-success me-2 mt-1"></i>
                                <span>Tamanho entre 3 e 100 caracteres</span>
                            </li>
                        </ul>

                        <div class="d-grid gap-2 mt-4">
                            <a th:href="@{/admin/realms}" class="btn btn-outline-primary">
                                <i class="ti ti-arrow-left me-2"></i>Voltar para Lista
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </th:block>
</body>
</html>
```

## Checklist de Validação
- [ ] Modal Bootstrap 5 criado no template
- [ ] Campo Nome implementado com validações
- [ ] Campo Descrição implementado com textarea
- [ ] Campo Empresa ID implementado
- [ ] Campo Status (select) implementado
- [ ] Realm Master indicator implementado
- [ ] Validations visuais (`is-invalid`, `invalid-feedback`)
- [ ] Mensagens de erro exibidas com Thymeleaf
- [ ] CSRF token incluído no formulário
- [ ] Botões Submit e Cancel implementados
- [ ] Título do modal dinâmico (Criar vs Editar)
- [ ] Campo Nome read-only quando Realm Master
- [ ] Hint de formato de nome exibido
- [ ] Contador de caracteres na descrição
- [ ] JavaScript para auto-focus no campo nome
- [ ] JavaScript para validação em tempo real
- [ ] JavaScript para auto-ajuste de altura do textarea
- [ ] Validação HTML5 configurada (required, pattern, maxlength)

## Anotações
- Modal pode ser inline na página de listagem ou em template separado
- Usar `th:method` dinâmico para POST (criação) ou PUT (edição)
- Realm Master deve ter nome read-only e não pode ser desativado
- Validations HTML5 + Thymeleaf para双重验证
- CSRF token é obrigatório para segurança
- JavaScript deve melhorar UX (auto-focus, validação em tempo real)
- Feedback visual imediato para erros de validação

## Dependências
- Story 01 (Template da Lista de Realms) - template base
- Story 02 (DTOs de Realm) - RealmForm
- Story 03 (Backend Service Layer) - validações no servidor
- Story 04 (Controller API) - endpoints POST/PUT

## Prioridade
**Alta** - Modal necessário para criação/edição de realms

## Estimativa
- Implementação: 3 horas
- Testes: 1 hora
- Total: 4 horas
