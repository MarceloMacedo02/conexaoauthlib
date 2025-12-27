# User Story: Reset Administrativo de Senha

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-07

## Descrição
Implementar a funcionalidade de reset administrativo de senha com geração de código numérico de 6 dígitos via e-mail, página de validação do código recebido e modal de confirmação de reset, seguindo o padrão da página de login (sem sidebar).

## Critérios de Aceite
- [ ] Endpoint `POST /admin/usuarios/reset-senha/{id}` implementado
- [ ] Geração de código numérico de 6 dígitos via e-mail
- [ ] Modal de confirmação de reset implementado
- [ ] Modal exibe nome e email do usuário
- [ ] Página de validação do código implementada
- [ ] Template seguindo padrão da página de login (sem sidebar)
- [ ] Formulário de validação de código implementado
- [ ] Novo campo de senha implementado
- [ ] Confirmação de senha implementada
- [ ] Validações visuais implementadas
- [ ] Feedback de sucesso/erro implementado
- [ ] Código expira em 15 minutos
- [ ] Token de reset é único por usuário
- [ ] Auditoria de reset registrada

## Tarefas
1. Implementar endpoint de reset administrativo de senha (já criado na Story 03)
2. Implementar envio de email com código (integração com serviço de email)
3. Criar modal de confirmação de reset na listagem
4. Criar página de validação do código (templates/admin/reset-password-validate.html)
5. Implementar template com layout base (sem sidebar)
6. Implementar formulário de validação de código
7. Implementar formulário de nova senha
8. Adicionar validações visuais
9. Implementar botão de reset senha na tabela de usuários
10. Implementar JavaScript para ação de reset via AJAX
11. Testar geração de código e envio de email
12. Testar validação de código
13. Testar definição de nova senha
14. Testar expiração de código

## Instruções de Implementação

### Modal de Confirmação de Reset
**Adicionar ao template `admin/usuarios/list.html`:**

```html
<!-- Modal de Confirmação de Reset de Senha -->
<div class="modal fade" id="resetSenhaModal" tabindex="-1"
     aria-labelledby="resetSenhaModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-warning" id="resetSenhaModalLabel">
                    <i class="ti ti-key me-2"></i>Reset Administrativo de Senha
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="alert alert-warning mb-3">
                    <i class="ti ti-info-circle me-2"></i>
                    <strong>Informação:</strong> Um código de 6 dígitos será gerado
                    e enviado para o e-mail do usuário. O código expira em 15 minutos.
                </div>

                <div class="mb-3">
                    <label class="form-label">Usuário:</label>
                    <p id="resetSenhaUsuarioNome" class="fw-medium mb-0">João Silva</p>
                    <p id="resetSenhaUsuarioEmail" class="text-muted small">
                        joao.silva@empresa.com.br
                    </p>
                </div>

                <div class="alert alert-info mb-0">
                    <i class="ti ti-mail me-2"></i>
                    <strong>Processo:</strong>
                    <ol class="mb-0 mt-2 ps-3">
                        <li>Código de 6 dígitos enviado para o e-mail</li>
                        <li>Usuário acessa link de validação</li>
                        <li>Usuário insere código e nova senha</li>
                        <li>Senha atualizada com sucesso</li>
                    </ol>
                </div>
            </div> <!-- end modal-body -->

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">
                    <i class="ti ti-x me-2"></i>Cancelar
                </button>
                <button type="button" class="btn btn-warning" id="btnConfirmResetSenha">
                    <i class="ti ti-send me-2"></i>Enviar Código
                </button>
            </div> <!-- end modal-footer -->

        </div>
    </div>
</div>
```

### Página de Validação de Código
**Criar template `src/main/resources/templates/admin/reset-password-validate.html`:**

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layouts/base}">
<head>
    <th:block layout:fragment="title-meta">
        <th:block th:replace="~{partials/title-meta :: title-meta('Validar Reset de Senha')}" />
    </th:block>
</head>
<body>
    <th:block layout:fragment="content">

        <!-- Estrutura de autenticação (sem sidebar) -->
        <div class="auth-box overflow-hidden align-items-center d-flex">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-xxl-4 col-md-6 col-sm-8">
                        <div class="card p-4">

                            <!-- Header -->
                            <div class="text-center mb-4">
                                <div class="avatar avatar-lg bg-primary text-white rounded-circle mx-auto mb-2">
                                    <i class="ti ti-key fs-4 avatar-title"></i>
                                </div>
                                <h4 class="mb-1">Validar Reset de Senha</h4>
                                <p class="text-muted">
                                    Insira o código de 6 dígitos enviado para seu e-mail
                                </p>
                            </div>

                            <!-- Flash Messages -->
                            <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="ti ti-check me-2"></i>
                                <span th:text="${success}"></span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>

                            <div th:if="${error}" class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="ti ti-alert-circle me-2"></i>
                                <span th:text="${error}"></span>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>

                            <!-- Formulário de Validação -->
                            <form th:action="@{/admin/reset-password/validate}"
                                  th:object="${resetPasswordForm}"
                                  method="post"
                                  id="resetPasswordValidateForm"
                                  class="needs-validation"
                                  novalidate>

                                <!-- Campo oculto: Token/ID do Usuário -->
                                <input type="hidden" th:field="*{usuarioId}" />
                                <input type="hidden" th:field="*{token}" />

                                <!-- Código de 6 Dígitos -->
                                <div class="mb-3"
                                     th:classappend="${#fields.hasErrors('codigo')} ? 'has-error' : ''">
                                    <label class="form-label" for="codigo">
                                        Código de Reset <span class="text-danger">*</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="text"
                                               class="form-control text-center fs-18 fw-bold"
                                               th:classappend="${#fields.hasErrors('codigo')} ? 'is-invalid' : ''"
                                               id="codigo"
                                               th:field="*{codigo}"
                                               placeholder="Ex: 123456"
                                               required
                                               pattern="^\\d{6}$"
                                               maxlength="6"
                                               style="letter-spacing: 0.5em;" />
                                        <span class="input-group-text">
                                            <i class="ti ti-key text-muted"></i>
                                        </span>
                                    </div>

                                    <!-- Mensagem de erro -->
                                    <div class="invalid-feedback"
                                         th:if="${#fields.hasErrors('codigo')}"
                                         th:errors="*{codigo}"></div>

                                    <div class="form-text text-muted">
                                        Insira o código de 6 dígitos enviado para seu e-mail.
                                    </div>
                                </div>

                                <!-- Nova Senha -->
                                <div class="mb-3"
                                     th:classappend="${#fields.hasErrors('novaSenha')} ? 'has-error' : ''">
                                    <label class="form-label" for="novaSenha">
                                        Nova Senha <span class="text-danger">*</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="password"
                                               class="form-control"
                                               th:classappend="${#fields.hasErrors('novaSenha')} ? 'is-invalid' : ''"
                                               id="novaSenha"
                                               th:field="*{novaSenha}"
                                               placeholder="Nova senha"
                                               required
                                               minlength="8"
                                               maxlength="100" />
                                        <button type="button" class="btn btn-default"
                                                onclick="togglePasswordVisibility('novaSenha')">
                                            <i class="ti ti-eye" id="iconeNovaSenha"></i>
                                        </button>
                                    </div>

                                    <!-- Mensagem de erro -->
                                    <div class="invalid-feedback"
                                         th:if="${#fields.hasErrors('novaSenha')}"
                                         th:errors="*{novaSenha}"></div>
                                </div>

                                <!-- Confirmação de Senha -->
                                <div class="mb-3"
                                     th:classappend="${#fields.hasErrors('confirmarSenha')} ? 'has-error' : ''">
                                    <label class="form-label" for="confirmarSenha">
                                        Confirmar Senha <span class="text-danger">*</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="password"
                                               class="form-control"
                                               th:classappend="${#fields.hasErrors('confirmarSenha')} ? 'is-invalid' : ''"
                                               id="confirmarSenha"
                                               th:field="*{confirmarSenha}"
                                               placeholder="Confirme a nova senha"
                                               required
                                               minlength="8"
                                               maxlength="100" />
                                        <button type="button" class="btn btn-default"
                                                onclick="togglePasswordVisibility('confirmarSenha')">
                                            <i class="ti ti-eye" id="iconeConfirmarSenha"></i>
                                        </button>
                                    </div>

                                    <!-- Mensagem de erro -->
                                    <div class="invalid-feedback"
                                         th:if="${#fields.hasErrors('confirmarSenha')}"
                                         th:errors="*{confirmarSenha}"></div>
                                </div>

                                <!-- Requisitos da Senha -->
                                <div class="alert alert-light mb-3">
                                    <h6 class="alert-heading mb-2 fs-sm">Requisitos da senha:</h6>
                                    <ul class="mb-0 ps-3 fs-sm">
                                        <li>Mínimo de 8 caracteres</li>
                                        <li>Máximo de 100 caracteres</li>
                                        <li>Pelo menos 1 letra maiúscula</li>
                                        <li>Pelo menos 1 letra minúscula</li>
                                        <li>Pelo menos 1 número</li>
                                        <li>Pelo menos 1 caractere especial</li>
                                    </ul>
                                </div>

                                <!-- Botão de Enviar -->
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary btn-lg">
                                        <i class="ti ti-check me-2"></i>Validar e Atualizar Senha
                                    </button>
                                </div>

                                <!-- Link para Login -->
                                <div class="text-center mt-3">
                                    <a th:href="@{/login}" class="link-primary">
                                        <i class="ti ti-arrow-left me-1"></i>Voltar para Login
                                    </a>
                                </div>

                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>

    </th:block>

    <th:block layout:fragment="javascripts">

        <!-- Reset Password Validate JavaScript -->
        <script>
        document.addEventListener('DOMContentLoaded', function() {

            // Alternar visibilidade da senha
            window.togglePasswordVisibility = function(fieldId) {
                const input = document.getElementById(fieldId);
                const icone = document.getElementById('icone' + fieldId.charAt(0).toUpperCase() + fieldId.slice(1));

                if (input.type === 'password') {
                    input.type = 'text';
                    icone.classList.remove('ti-eye');
                    icone.classList.add('ti-eye-off');
                } else {
                    input.type = 'password';
                    icone.classList.remove('ti-eye-off');
                    icone.classList.add('ti-eye');
                }
            };

            // Validação em tempo real da senha
            const novaSenha = document.getElementById('novaSenha');
            const confirmarSenha = document.getElementById('confirmarSenha');
            const form = document.getElementById('resetPasswordValidateForm');

            form.addEventListener('submit', function(event) {
                // Validar se senhas conferem
                if (novaSenha.value !== confirmarSenha.value) {
                    event.preventDefault();
                    event.stopPropagation();

                    alert('As senhas não conferem. Por favor, digite a mesma senha em ambos os campos.');
                    return;
                }

                // Validar requisitos da senha
                const senha = novaSenha.value;
                const requisitos = [
                    senha.length >= 8 && senha.length <= 100,
                    /[A-Z]/.test(senha),
                    /[a-z]/.test(senha),
                    /[0-9]/.test(senha),
                    /[^A-Za-z0-9]/.test(senha)
                ];

                if (!requisitos.every(r => r)) {
                    event.preventDefault();
                    event.stopPropagation();

                    alert('A senha não atende todos os requisitos. Por favor, revise os requisitos listados.');
                    return;
                }

                if (!this.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }

                this.classList.add('was-validated');
            }, false);

            // Auto-focus no campo código ao carregar a página
            const codigoInput = document.getElementById('codigo');
            if (codigoInput) {
                codigoInput.focus();
            }

        });
        </script>

    </th:block>
</body>
</html>
```

### DTO para Reset de Senha
**Criar record em `src/main/java/br/com/plataforma/conexaodigital/admin/api/requests/ResetPasswordForm.java`:**

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para formulário de validação de reset de senha.
 */
public record ResetPasswordForm(
    @NotBlank(message = "{reset.usuario.id.obrigatorio}")
    String usuarioId,

    @NotBlank(message = "{reset.token.obrigatorio}")
    String token,

    @NotBlank(message = "{reset.codigo.obrigatorio}")
    @Pattern(regexp = "^\\d{6}$", message = "{reset.codigo.formato}")
    String codigo,

    @NotBlank(message = "{reset.nova.senha.obrigatoria}")
    @Size(min = 8, max = 100, message = "{reset.nova.senha.tamanho}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
             message = "{reset.nova.senha.complexidade}")
    String novaSenha,

    @NotBlank(message = "{reset.confirmar.senha.obrigatoria}")
    String confirmarSenha
) {
    /**
     * Valida se as senhas conferem.
     */
    public boolean isSenhasConferem() {
        return novaSenha != null && novaSenha.equals(confirmarSenha);
    }
}
```

### Controller Endpoint de Validação
**Adicionar ao `AdminUsuarioController`:**

```java
/**
 * Página de validação de código de reset de senha.
 *
 * @param token token de reset (encriptado ou hash)
 * @param model Spring Model
 * @return template de validação
 */
@GetMapping("/reset-password/validate")
public String validarResetSenha(
    @RequestParam String token,
    Model model
) {
    // Decodificar token e extrair usuarioId
    UUID usuarioId = decodificarTokenReset(token);

    // Criar form vazio
    ResetPasswordForm form = new ResetPasswordForm(
        usuarioId.toString(),
        token,
        null,
        null,
        null
    );

    model.addAttribute("resetPasswordForm", form);
    return "admin/reset-password-validate";
}

/**
 * Processa validação de código e atualização de senha.
 *
 * @param form formulário de reset
 * @param result resultado da validação
 * @param redirectAttributes atributos de redirect
 * @return redirect para login
 */
@PostMapping("/reset-password/validate")
public String processarResetSenha(
    @Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm form,
    BindingResult result,
    RedirectAttributes redirectAttributes
) {
    // Validar se as senhas conferem
    if (!form.isSenhasConferem()) {
        result.rejectValue("confirmarSenha", "senha.nao.confere",
                         "As senhas não conferem");
    }

    if (result.hasErrors()) {
        redirectAttributes.addFlashAttribute("error", "Erro na validação do formulário");
        return "admin/reset-password-validate";
    }

    try {
        // Validar código e atualizar senha (método deve existir no service)
        usuarioService.validarCodigoResetSenha(
            UUID.fromString(form.usuarioId()),
            form.token(),
            form.codigo(),
            form.novaSenha()
        );

        redirectAttributes.addFlashAttribute(
            "success",
            "Senha atualizada com sucesso! Você pode fazer login agora."
        );

        return "redirect:/login";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "admin/reset-password-validate";
    }
}
```

### JavaScript para Botão de Reset na Tabela
**Atualizar botão na tabela (Story 06):**

```html
<a href="#" class="btn btn-default btn-icon btn-sm rounded"
   data-action="reset-password"
   th:data-usuario-id="${usuario.id}"
   title="Reset Administrativo de Senha">
    <i class="ti ti-key fs-lg"></i>
</a>
```

**JavaScript para ação de reset:**

```javascript
// Event listener para botão de reset senha
case 'reset-password':
    abrirModalResetSenha(usuarioId);
    break;

// Abrir modal de reset de senha
function abrirModalResetSenha(usuarioId) {
    currentUsuarioId = usuarioId;

    // Buscar dados básicos do usuário
    fetch(`/admin/usuarios/${usuarioId}`)
        .then(response => response.json())
        .then(usuario => {
            document.getElementById('resetSenhaUsuarioNome').textContent = usuario.nome;
            document.getElementById('resetSenhaUsuarioEmail').textContent = usuario.email;

            resetSenhaModal.show();
        })
        .catch(error => {
            mostrarToast('error', 'Erro ao carregar usuário');
            console.error(error);
        });
}

// Confirmar reset de senha
document.getElementById('btnConfirmResetSenha').addEventListener('click', function() {
    fetch(`/admin/usuarios/reset-senha/${currentUsuarioId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        resetSenhaModal.hide();
        mostrarToast('success', 'Código de reset enviado para o e-mail do usuário!');
    })
    .catch(error => {
        mostrarToast('error', 'Erro ao enviar código de reset');
        console.error(error);
    });
});
```

## Checklist de Validação
- [ ] Modal de confirmação de reset implementado
- [ ] Modal exibe nome e email do usuário
- [ ] Botão de reset senha implementado na tabela
- [ ] Endpoint POST `/admin/usuarios/reset-senha/{id}` implementado
- [ ] Página de validação de código criada
- [ ] Template usa layout base (sem sidebar)
- [ ] Formulário de validação de código implementado
- [ ] Campo de código (6 dígitos) implementado
- [ ] Campo de nova senha implementado
- [ ] Campo de confirmação de senha implementado
- [ ] Validações visuais (`is-invalid`, `invalid-feedback`)
- [ ] Validação de formato de código (6 dígitos)
- [ ] Validação de requisitos da senha implementada
- [ ] Validação de senhas conferem implementada
- [ ] Botão de alternar visibilidade da senha implementado
- [ ] Link para voltar ao login implementado
- [ ] JavaScript para ação de reset implementado
- [ ] Envio de email com código implementado
- [ ] Código expira em 15 minutos
- [ ] Auditoria de reset registrada
- [ ] Feedback de sucesso/erro implementado

## Anotações
- Reset administrativo segue o padrão do Epic 2 (Reset de senha)
- Código de 6 dígitos enviado via email ( Story 2-7, 2-8 )
- Página de validação usa layout base (sem sidebar) seguindo padrão de login
- Validação de senha inclui requisitos de complexidade
- Senha deve ter: 8+ chars, maiúscula, minúscula, número, especial
- Token de reset pode ser encriptado ou hash do código
- Expiração do código: 15 minutos (configurável)
- Email com link de validação é enviado ao usuário
- Após validação bem-sucedida, usuário é redirecionado para login
- Auditoria registra o reset administrativo e a validação do código

## Dependências
- Story 03 (Backend Service Layer) - método resetSenhaAdministrativo já existe
- Story 04 (Controller API) - endpoint de reset já existe
- Epic 2 (Gestão de Usuários) - lógica de reset já existe (Stories 2-6, 2-7, 2-8)
- Story 06 (CRUD Funcionalidades) - botão na tabela
- Epic 9 (Configuração) - serviço de email configurado

## Prioridade
**Média** - Funcionalidade importante mas não crítica

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
