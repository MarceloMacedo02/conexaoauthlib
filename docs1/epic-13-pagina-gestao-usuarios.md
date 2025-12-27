# Epic 13: Página de Gestão de Usuários (Thymeleaf)

## Descrição
Implementar página de gestão de usuários com listagem paginada, filtros avançados por realm/empresa/tenentId, formulário de criação/edição, ações de bloquear/desbloquear/reset de senha, utilizando Thymeleaf como template engine e Bootstrap 5 para estilização, seguindo o padrão do template API Keys do ui.txt.

## Objetivos
- Listar todos os usuários com paginação e filtros
- Criar novos usuários com validação completa
- Editar usuários existentes
- Bloquear/desbloquear usuários
- Resetar senhas administrativamente
- Filtrar usuários por realm, empresaId, tenentId, status
- Associar múltiplas roles ao usuário

## Critérios de Aceite
- [ ] Tabela listando todos os usuários
- [ ] Filtros por realm, empresaId, tenentId, status, email
- [ ] Paginação configurável (5, 10, 15, 20 por página)
- [ ] Ordenação por nome, email, data de criação, status
- [ ] Busca textual de usuários
- [ ] Modal de criação de usuário
- [ ] Modal de edição de usuário
- [ ] Modal de associação de roles
- [ ] Ação de bloquear usuário (com confirmação)
- [ ] Ação de desbloquear usuário
- [ ] Ação de reset de senha administrativo
- [ ] Exibição de roles do usuário (badges)
- [ ] Validação de email único
- [ ] Mensagens de sucesso/erro em toasts

## Requisitos Funcionais
- Listar usuários com paginação
- Filtrar por realm, empresaId, tenentId, status, email
- Criar novo usuário (email, senha, nome, realm, roles)
- Editar usuário existente
- Bloquear usuário (alterar status para BLOQUEADO)
- Desbloquear usuário (alterar status para ATIVO)
- Reset administrativo de senha (gerar senha provisória)
- Associar múltiplas roles ao usuário
- Visualizar roles associadas ao usuário
- Validar unicidade de email
- Busca textual em tempo real

## Requisitos Técnicos
- **Template:** `src/main/resources/templates/admin/usuarios/list.html`
- **Template Formulário:** `src/main/resources/templates/admin/usuarios/form.html`
- **Layout:** `layouts/vertical` (com sidebar)
- **Endpoint:** `GET /admin/usuarios` (listagem)
- **Endpoint:** `GET /admin/usuarios/{id}` (edição)
- **Endpoint:** `POST /admin/usuarios` (criação)
- **Endpoint:** `PUT /admin/usuarios/{id}` (atualização)
- **Endpoint:** `PATCH /admin/usuarios/{id}/bloquear`
- **Endpoint:** `PATCH /admin/usuarios/{id}/desbloquear`
- **Endpoint:** `POST /admin/usuarios/{id}/reset-senha`
- **CSS Framework:** Bootstrap 5 (já disponível)
- **Icons:** Tabler Icons (ti-*)
- **Table:** DataTables.js (já disponível)

## Componentes de UI (baseado em ui.txt - API Keys template)

### Estrutura da Página
```html
<div class="row">
    <div class="col-12">
        <div data-table data-table-rows-per-page="8" class="card">
            <!-- Card Header com Filtros e Botões -->
            <div class="card-header border-light justify-content-between">
                <div class="d-flex gap-2">
                    <div class="app-search">
                        <input data-table-search type="text" class="form-control"
                               placeholder="Buscar usuários...">
                        <i data-lucide="search" class="app-search-icon text-muted"></i>
                    </div>
                    <button type="button" class="btn btn-primary btn-icon"
                            data-bs-toggle="modal"
                            data-bs-target="#addUsuarioModal">
                        <i class="ti ti-user-plus fs-lg"></i>
                    </button>
                    <button data-table-delete-selected class="btn btn-danger d-none">
                        <i class="ti ti-trash fs-lg"></i>
                    </button>
                </div>

                <div class="d-flex align-items-center gap-2">
                    <span class="me-2 fw-semibold">Filtrar Por:</span>

                    <!-- Realm Filter -->
                    <div class="app-search">
                        <select data-table-filter="realm"
                                class="form-select form-control my-1 my-md-0">
                            <option value="All">Realm</option>
                            <!-- Opções carregadas dinamicamente -->
                        </select>
                        <i data-lucide="building" class="app-search-icon text-muted"></i>
                    </div>

                    <!-- Empresa ID Filter -->
                    <div class="app-search">
                        <select data-table-filter="empresaId"
                                class="form-select form-control my-1 my-md-0">
                            <option value="All">Empresa ID</option>
                            <!-- Opções carregadas dinamicamente -->
                        </select>
                        <i data-lucide="building-2" class="app-search-icon text-muted"></i>
                    </div>

                    <!-- Tenent ID Filter -->
                    <div class="app-search">
                        <select data-table-filter="tenentId"
                                class="form-select form-control my-1 my-md-0">
                            <option value="All">Tenent ID</option>
                            <!-- Opções carregadas dinamicamente -->
                        </select>
                        <i data-lucide="building-store" class="app-search-icon text-muted"></i>
                    </div>

                    <!-- Status Filter -->
                    <div class="app-search">
                        <select data-table-filter="status"
                                class="form-select form-control my-1 my-md-0">
                            <option value="All">Status</option>
                            <option value="ATIVO">Ativo</option>
                            <option value="BLOQUEADO">Bloqueado</option>
                        </select>
                        <i data-lucide="circle-check" class="app-search-icon text-muted"></i>
                    </div>

                    <!-- Records Per Page -->
                    <div>
                        <select data-table-set-rows-per-page
                                class="form-select form-control my-1 my-md-0">
                            <option value="5">5</option>
                            <option value="10">10</option>
                            <option value="15">15</option>
                            <option value="20">20</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Tabela de Usuários -->
            <div class="table-responsive">
                <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                    <thead class="bg-light bg-opacity-25 thead-sm">
                        <tr class="text-uppercase fs-xxs">
                            <th scope="col" style="width: 1%;">
                                <input class="form-check-input form-check-input-light fs-14 mt-0"
                                       data-table-select-all type="checkbox" value="option">
                            </th>
                            <th data-table-sort>Nome</th>
                            <th data-table-sort>Email</th>
                            <th>Realm</th>
                            <th>Empresa ID</th>
                            <th>Roles</th>
                            <th data-table-sort data-column="status">Status</th>
                            <th data-table-sort>Criado Em</th>
                            <th class="text-center">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="usuario : ${usuarios}">
                            <td>
                                <input class="form-check-input form-check-input-light fs-14 mt-0"
                                       type="checkbox" value="option">
                            </td>
                            <td class="fw-medium">
                                <div class="d-flex align-items-center gap-2">
                                    <div class="avatar avatar-xs">
                                        <span class="avatar-title text-bg-primary fw-bold rounded-circle">
                                            <span th:text="${#strings.substring(usuario.nome, 0, 1)}"></span>
                                        </span>
                                    </div>
                                    <span th:text="${usuario.nome}"></span>
                                </div>
                            </td>
                            <td th:text="${usuario.email}"></td>
                            <td th:text="${usuario.realm.nome}"></td>
                            <td th:text="${usuario.empresaId}"></td>
                            <td>
                                <div class="d-flex flex-wrap gap-1">
                                    <span th:each="role : ${usuario.roles}"
                                          class="badge bg-primary-subtle text-primary fs-xxs"
                                          th:text="${role.nome}">
                                    </span>
                                </div>
                            </td>
                            <td>
                                <span th:class="${usuario.status == 'ATIVO' ? 'badge bg-success-subtle text-success badge-label'
                                                       : 'badge bg-danger-subtle text-danger badge-label'}">
                                    <span th:text="${usuario.status.descricao}"></span>
                                </span>
                            </td>
                            <td th:text="${usuario.dataCriacao}"></td>
                            <td>
                                <div class="d-flex align-items-center justify-content-center gap-1">
                                    <a th:href="@{/admin/usuarios/{id}(id=${usuario.id})}"
                                       class="btn btn-default btn-icon btn-sm rounded">
                                        <i class="ti ti-eye fs-lg"></i>
                                    </a>
                                    <a th:href="@{/admin/usuarios/{id}/edit(id=${usuario.id})}"
                                       class="btn btn-default btn-icon btn-sm rounded">
                                        <i class="ti ti-edit fs-lg"></i>
                                    </a>
                                    <a th:if="${usuario.status == 'ATIVO'}"
                                       th:href="@{/admin/usuarios/{id}/bloquear(id=${usuario.id})}"
                                       class="btn btn-warning btn-icon btn-sm rounded"
                                       data-confirm="Deseja realmente bloquear este usuário?">
                                        <i class="ti ti-lock fs-lg"></i>
                                    </a>
                                    <a th:if="${usuario.status == 'BLOQUEADO'}"
                                       th:href="@{/admin/usuarios/{id}/desbloquear(id=${usuario.id})}"
                                       class="btn btn-success btn-icon btn-sm rounded">
                                        <i class="ti ti-lock-open fs-lg"></i>
                                    </a>
                                    <a th:href="@{/admin/usuarios/{id}/reset-senha(id=${usuario.id})}"
                                       class="btn btn-info btn-icon btn-sm rounded"
                                       data-confirm="Deseja resetar a senha deste usuário?">
                                        <i class="ti ti-key fs-lg"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Paginação -->
            <div class="card-footer border-0">
                <div class="d-flex justify-content-between align-items-center">
                    <div data-table-pagination-info="usuarios"></div>
                    <div data-table-pagination></div>
                </div>
            </div>
        </div>
    </div>
</div>
```

### Modal de Criação/Edição
```html
<div class="modal fade" id="addUsuarioModal" tabindex="-1"
     aria-labelledby="addUsuarioModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addUsuarioModalLabel">
                    <span th:text="${editMode ? 'Editar Usuário' : 'Novo Usuário'}"></span>
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form th:action="@{/admin/usuarios}" method="post"
                  th:object="${usuarioForm}">
                <input type="hidden" th:field="*{id}" />

                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Nome Completo</label>
                            <input type="text" class="form-control"
                                   th:field="*{nome}"
                                   placeholder="Nome completo"
                                   required />
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('nome')}"
                                 th:errors="*{nome}"></div>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control"
                                   th:field="*{email}"
                                   placeholder="email@exemplo.com"
                                   required />
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('email')}"
                                 th:errors="*{email}"></div>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">Senha</label>
                            <input type="password" class="form-control"
                                   th:field="*{senha}"
                                   placeholder="Minímo 8 caracteres"
                                   th:disabled="${editMode}" />
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('senha')}"
                                 th:errors="*{senha}"></div>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">Confirmar Senha</label>
                            <input type="password" class="form-control"
                                   name="confirmarSenha"
                                   placeholder="Repita a senha"
                                   th:disabled="${editMode}" />
                        </div>

                        <div class="col-md-4">
                            <label class="form-label">Realm</label>
                            <select class="form-select" th:field="*{realmId}" required>
                                <option value="">Selecione...</option>
                                <option th:each="realm : ${realms}"
                                        th:value="${realm.id}"
                                        th:text="${realm.nome}">
                                </option>
                            </select>
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('realmId')}"
                                 th:errors="*{realmId}"></div>
                        </div>

                        <div class="col-md-4">
                            <label class="form-label">Empresa ID</label>
                            <input type="text" class="form-control"
                                   th:field="*{empresaId}"
                                   placeholder="ID da empresa" />
                        </div>

                        <div class="col-md-4">
                            <label class="form-label">Tenent ID</label>
                            <input type="text" class="form-control"
                                   th:field="*{tenentId}"
                                   placeholder="ID do tenant" />
                        </div>

                        <div class="col-md-12">
                            <label class="form-label">Roles</label>
                            <select class="form-select"
                                    th:field="*{roleIds}"
                                    multiple>
                                <option th:each="role : ${roles}"
                                        th:value="${role.id}"
                                        th:text="${role.nome}">
                                </option>
                            </select>
                            <small class="text-muted">
                                Segure Ctrl/Cmd para selecionar múltiplas roles
                            </small>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-light"
                            data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="ti ti-save me-2"></i>
                        <span th:text="${editMode ? 'Salvar' : 'Criar'}"></span>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
```

### Modal de Reset de Senha
```html
<div class="modal fade" id="resetSenhaModal" tabindex="-1"
     aria-labelledby="resetSenhaModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="resetSenhaModalLabel">
                    Reset de Senha Administrativo
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <div class="alert alert-info mb-3">
                    <i class="ti ti-info-circle me-2"></i>
                    Uma nova senha será gerada e enviada por e-mail para o usuário.
                </div>

                <div class="mb-3">
                    <label class="form-label">Usuário</label>
                    <input type="text" class="form-control"
                           id="resetUsuarioNome" readonly />
                </div>

                <div class="mb-3">
                    <label class="form-label">Nova Senha Gerada</label>
                    <div class="input-group">
                        <input type="text" class="form-control"
                               id="novaSenhaGerada" readonly />
                        <button type="button" class="btn btn-outline-secondary"
                                onclick="copiarSenha()">
                            <i class="ti ti-copy"></i>
                        </button>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-light"
                        data-bs-dismiss="modal">Fechar</button>
                <button type="button" class="btn btn-primary"
                        onclick="confirmarResetSenha()">
                    <i class="ti ti-send me-2"></i>
                    Enviar por E-mail
                </button>
            </div>
        </div>
    </div>
</div>
```

### Modelos de Dados
```java
public record UsuarioForm(
    String id,

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    String senha,

    @NotBlank(message = "Realm é obrigatório")
    String realmId,

    @Size(max = 100, message = "Empresa ID deve ter no máximo 100 caracteres")
    String empresaId,

    @Size(max = 100, message = "Tenent ID deve ter no máximo 100 caracteres")
    String tenentId,

    List<String> roleIds
);

public record UsuarioListResponse(
    String id,
    String nome,
    String email,
    String realmNome,
    String empresaId,
    List<String> roles,
    StatusUsuario status,
    LocalDateTime dataCriacao
);
```

## API Endpoints

### Controller
```java
@GetMapping("/admin/usuarios")
public String listUsuarios(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) String email,
    @RequestParam(required = false) String realmId,
    @RequestParam(required = false) String empresaId,
    @RequestParam(required = false) String tenentId,
    @RequestParam(required = false) String status,
    Model model
) {
    Page<UsuarioListResponse> usuarios = usuarioService.listarUsuarios(
        page, size, nome, email, realmId, empresaId, tenentId, status
    );
    model.addAttribute("usuarios", usuarios.getContent());
    model.addAttribute("realms", realmService.listarTodos());
    model.addAttribute("roles", roleService.listarTodos());
    model.addAttribute("page", usuarios.getNumber());
    model.addAttribute("totalPages", usuarios.getTotalPages());
    model.addAttribute("totalElements", usuarios.getTotalElements());
    return "admin/usuarios/list";
}

@GetMapping("/admin/usuarios/novo")
public String novoUsuario(Model model) {
    model.addAttribute("usuarioForm", new UsuarioForm());
    model.addAttribute("editMode", false);
    model.addAttribute("realms", realmService.listarTodos());
    model.addAttribute("roles", roleService.listarTodos());
    return "admin/usuarios/form";
}

@PostMapping("/admin/usuarios")
public String criarUsuario(
    @Valid @ModelAttribute UsuarioForm form,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
) {
    if (result.hasErrors()) {
        model.addAttribute("usuarioForm", form);
        model.addAttribute("editMode", false);
        model.addAttribute("realms", realmService.listarTodos());
        model.addAttribute("roles", roleService.listarTodos());
        return "admin/usuarios/form";
    }

    try {
        usuarioService.criarUsuario(form);
        redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso!");
        return "redirect:/admin/usuarios";
    } catch (UsuarioJaExisteException e) {
        result.rejectValue("email", null, e.getMessage());
        return "admin/usuarios/form";
    }
}

@PutMapping("/admin/usuarios/{id}")
public String atualizarUsuario(
    @PathVariable String id,
    @Valid @ModelAttribute UsuarioForm form,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
) {
    if (result.hasErrors()) {
        model.addAttribute("usuarioForm", form);
        model.addAttribute("editMode", true);
        model.addAttribute("realms", realmService.listarTodos());
        model.addAttribute("roles", roleService.listarTodos());
        return "admin/usuarios/form";
    }

    form.setId(id);
    usuarioService.atualizarUsuario(form);
    redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
    return "redirect:/admin/usuarios";
}

@PatchMapping("/admin/usuarios/{id}/bloquear")
public String bloquearUsuario(
    @PathVariable String id,
    RedirectAttributes redirectAttributes
) {
    usuarioService.bloquearUsuario(id);
    redirectAttributes.addFlashAttribute("success", "Usuário bloqueado com sucesso!");
    return "redirect:/admin/usuarios";
}

@PatchMapping("/admin/usuarios/{id}/desbloquear")
public String desbloquearUsuario(
    @PathVariable String id,
    RedirectAttributes redirectAttributes
) {
    usuarioService.desbloquearUsuario(id);
    redirectAttributes.addFlashAttribute("success", "Usuário desbloqueado com sucesso!");
    return "redirect:/admin/usuarios";
}

@PostMapping("/admin/usuarios/{id}/reset-senha")
public String resetarSenhaAdmin(
    @PathVariable String id,
    RedirectAttributes redirectAttributes
) {
    String novaSenha = usuarioService.gerarNovaSenha(id);
    // Enviar e-mail com nova senha
    emailService.enviarSenhaResetEmail(id, novaSenha);
    redirectAttributes.addFlashAttribute("success", "Senha resetada com sucesso! E-mail enviado.");
    return "redirect:/admin/usuarios";
}
```

## JavaScript
```javascript
// Confirmação antes de bloquear/resetar
document.querySelectorAll('[data-confirm]').forEach(element => {
    element.addEventListener('click', function(e) {
        const message = this.getAttribute('data-confirm');
        if (!confirm(message)) {
            e.preventDefault();
        }
    });
});

// Copiar senha gerada
function copiarSenha() {
    const senhaInput = document.getElementById('novaSenhaGerada');
    senhaInput.select();
    document.execCommand('copy');
    alert('Senha copiada para a área de transferência!');
}

// Confirmação de reset de senha
function confirmarResetSenha() {
    if (confirm('Deseja enviar a nova senha por e-mail para o usuário?')) {
        document.getElementById('resetSenhaForm').submit();
    }
}

// Toast notifications
window.addEventListener('load', function() {
    const successMessage = document.querySelector('.alert-success');
    const errorMessage = document.querySelector('.alert-danger');

    if (successMessage) {
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 5000);
    }

    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.display = 'none';
        }, 5000);
    }
});
```

## Validações
### Server-side
- Nome não vazio e entre 3 e 200 caracteres
- Email válido e único
- Senha não vazia e mínimo 8 caracteres (apenas criação)
- Realm obrigatório
- Confirmação de senha (apenas criação)
- Empresa ID máximo 100 caracteres
- Tenent ID máximo 100 caracteres
- Ao menos uma role selecionada

### Client-side (JavaScript)
- Nome não vazio
- Email formato válido
- Senha e confirmação coincidem (apenas criação)
- Realm selecionado
- Ao menos uma role selecionada

## Integrações
- Epic 1 (Gestão de Realms) - para seleção de realm
- Epic 2 (Gestão de Usuários) - backend já implementado
- Epic 3 (Gestão de Roles) - para seleção de roles
- Epic 7 (Auditoria) - para registro de alterações
- Epic 2 (Recuperação de Senha) - para envio de e-mail

## Arquivos a Criar/Modificar
```
src/main/resources/templates/admin/usuarios/
├── list.html
└── form.html

src/main/java/br/com/plataforma/conexaodigital/admin/api/
├── controller/
│   └── AdminUsuarioController.java
└── requests/
    └── UsuarioForm.java
```

## Testes
### Testes de Aceitação
- [ ] Lista de usuários é exibida corretamente
- [ ] Filtros funcionam (nome, email, realm, empresaId, tenentId, status)
- [ ] Paginação funciona corretamente
- [ ] Busca textual retorna resultados corretos
- [ ] Novo usuário pode ser criado
- [ ] Usuário existente pode ser editado
- [ ] Usuário ativo pode ser bloqueado
- [ ] Usuário bloqueado pode ser desbloqueado
- [ ] Senha pode ser resetada administrativamente
- [ ] Múltiplas roles podem ser associadas
- [ ] Validação de email único funciona
- [ ] Mensagens de sucesso/erro são exibidas
- [ ] Página é responsiva em dispositivos móveis

### Testes de UI
- [ ] Tabela está alinhada e formatada corretamente
- [ ] Badges de status e roles têm cores apropriadas
- [ ] Avatares com iniciais são exibidos corretamente
- [ ] Ícones de ações estão visíveis
- [ ] Modal de criação/edição abre corretamente
- [ ] Formulário tem validações visuais
- [ ] Botões têm feedback de clique
- [ ] Ordenação funciona
- [ ] Seleção múltipla de roles funciona

## Performance
- Carregamento da página < 2 segundos
- Filtros aplicados em < 500ms
- Modal de criação abre em < 200ms
- Operação de CRUD completa em < 1 segundo

## Dependências
- Epic 1 (Gestão de Realms) - para seleção de realm
- Epic 2 (Gestão de Usuários) - backend já implementado
- Epic 3 (Gestão de Roles) - para seleção de roles
- Epic 7 (Auditoria) - para registro de eventos

## Prioridade
**Alta** - Página essencial para gestão do sistema

## Estimativa
- Implementação: 14 horas
- Testes: 6 horas
- Total: 20 horas

## Notas
- Utilizar layout `layouts/vertical`
- DataTables.js já está disponível nos assets
- Avatar com inicial é gerado automaticamente
- Reset de senha gera nova senha e envia por e-mail
- Validação de email único é obrigatória no servidor
- Implementar toast notifications para feedback
- Considerar permitir edição inline (future enhancement)
- Adicionar confirmação adicional para bloquear usuários com sessões ativas
- Senha resetada deve ser enviada por e-mail (não exibida no frontend)
