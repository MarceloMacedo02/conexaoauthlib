# Epic 14: Página de Gestão de Roles (Thymeleaf)

## Descrição
Implementar página de gestão de roles com listagem paginada, filtros por realm, formulário de criação/edição, validação de exclusão de roles em uso, utilizando Thymeleaf como template engine e Bootstrap 5 para estilização, seguindo o padrão do template API Keys do ui.txt.

## Objetivos
- Listar todas as roles com paginação e filtros
- Criar novas roles escopadas por realm
- Editar roles existentes
- Remover roles (com verificação de uso)
- Definir roles padrão (ADMIN, USER, SERVICE)
- Visualizar usuários associados a cada role

## Critérios de Aceite
- [ ] Tabela listando todas as roles
- [ ] Filtros por realm, nome, status
- [ ] Paginação configurável (5, 10, 15, 20 por página)
- [ ] Ordenação por nome, realm, data de criação
- [ ] Busca textual de roles
- [ ] Modal de criação de role
- [ ] Modal de edição de role
- [ ] Ação de remover role (com confirmação)
- [ ] Validação de nome único por realm
- [ ] Alerta ao remover role com usuários associados
- [ ] Indicação visual de roles padrão (ADMIN, USER, SERVICE)
- [ ] Contagem de usuários por role
- [ ] Mensagens de sucesso/erro em toasts

## Requisitos Funcionais
- Listar roles com paginação
- Filtrar por realm, nome, status
- Criar nova role (nome, descrição, realm)
- Editar role existente
- Remover role (verificar usuários associados)
- Validar unicidade de nome por realm
- Visualizar quantos usuários possuem cada role
- Indicar roles padrão com ícone/distintivo
- Busca textual em tempo real

## Requisitos Técnicos
- **Template:** `src/main/resources/templates/admin/roles/list.html`
- **Template Formulário:** `src/main/resources/templates/admin/roles/form.html`
- **Layout:** `layouts/vertical` (com sidebar)
- **Endpoint:** `GET /admin/roles` (listagem)
- **Endpoint:** `GET /admin/roles/{id}` (edição)
- **Endpoint:** `POST /admin/roles` (criação)
- **Endpoint:** `PUT /admin/roles/{id}` (atualização)
- **Endpoint:** `DELETE /admin/roles/{id}` (remoção)
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
                               placeholder="Buscar roles...">
                        <i data-lucide="search" class="app-search-icon text-muted"></i>
                    </div>
                    <button type="button" class="btn btn-primary btn-icon"
                            data-bs-toggle="modal"
                            data-bs-target="#addRoleModal">
                        <i class="ti ti-plus fs-lg"></i>
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

                    <!-- Status Filter -->
                    <div class="app-search">
                        <select data-table-filter="status"
                                class="form-select form-control my-1 my-md-0">
                            <option value="All">Status</option>
                            <option value="ATIVO">Ativo</option>
                            <option value="INATIVO">Inativo</option>
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

            <!-- Tabela de Roles -->
            <div class="table-responsive">
                <table class="table text-nowrap table-custom table-centered table-hover w-100 mb-0">
                    <thead class="bg-light bg-opacity-25 thead-sm">
                        <tr class="text-uppercase fs-xxs">
                            <th scope="col" style="width: 1%;">
                                <input class="form-check-input form-check-input-light fs-14 mt-0"
                                       data-table-select-all type="checkbox" value="option">
                            </th>
                            <th data-table-sort>Nome</th>
                            <th>Realm</th>
                            <th>Descrição</th>
                            <th data-table-sort data-column="status">Status</th>
                            <th data-table-sort>Usuários</th>
                            <th data-table-sort>Criada Em</th>
                            <th class="text-center">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="role : ${roles}">
                            <td>
                                <input class="form-check-input form-check-input-light fs-14 mt-0"
                                       type="checkbox" value="option">
                            </td>
                            <td class="fw-medium">
                                <span th:if="${role.padrao}" class="badge bg-primary me-2">
                                    <i class="ti ti-star me-1"></i>Padrão
                                </span>
                                <span th:text="${role.nome}"></span>
                            </td>
                            <td th:text="${role.realm.nome}"></td>
                            <td th:text="${role.descricao}"></td>
                            <td>
                                <span th:class="${role.ativa ? 'badge bg-success-subtle text-success badge-label'
                                                       : 'badge bg-danger-subtle text-danger badge-label'}">
                                    <span th:text="${role.ativa ? 'Ativa' : 'Inativa'}"></span>
                                </span>
                            </td>
                            <td th:text="${role.totalUsuarios}"></td>
                            <td th:text="${role.dataCriacao}"></td>
                            <td>
                                <div class="d-flex align-items-center justify-content-center gap-1">
                                    <a th:href="@{/admin/roles/{id}(id=${role.id})}"
                                       class="btn btn-default btn-icon btn-sm rounded">
                                        <i class="ti ti-eye fs-lg"></i>
                                    </a>
                                    <a th:href="@{/admin/roles/{id}/edit(id=${role.id})}"
                                       class="btn btn-default btn-icon btn-sm rounded">
                                        <i class="ti ti-edit fs-lg"></i>
                                    </a>
                                    <a th:if="${role.totalUsuarios == 0 && !role.padrao}"
                                       th:href="@{/admin/roles/{id}(id=${role.id})}"
                                       class="btn btn-danger btn-icon btn-sm rounded"
                                       data-confirm="Deseja realmente remover esta role?">
                                        <i class="ti ti-trash fs-lg"></i>
                                    </a>
                                    <a th:if="${role.totalUsuarios > 0}"
                                       class="btn btn-secondary btn-icon btn-sm rounded disabled"
                                       title="Role possui usuários associados">
                                        <i class="ti ti-lock fs-lg"></i>
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
                    <div data-table-pagination-info="roles"></div>
                    <div data-table-pagination></div>
                </div>
            </div>
        </div>
    </div>
</div>
```

### Modal de Criação/Edição
```html
<div class="modal fade" id="addRoleModal" tabindex="-1"
     aria-labelledby="addRoleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addRoleModalLabel">
                    <span th:text="${editMode ? 'Editar Role' : 'Nova Role'}"></span>
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form th:action="@{/admin/roles}" method="post"
                  th:object="${roleForm}">
                <input type="hidden" th:field="*{id}" />

                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-12">
                            <label class="form-label">Nome da Role</label>
                            <input type="text" class="form-control"
                                   th:field="*{nome}"
                                   placeholder="Ex: ADMIN, USER, GERENTE"
                                   required />
                            <div class="invalid-feedback"
                                 th:if="${#fields.hasErrors('nome')}"
                                 th:errors="*{nome}"></div>
                        </div>

                        <div class="col-md-12">
                            <label class="form-label">Descrição</label>
                            <textarea class="form-control"
                                      th:field="*{descricao}"
                                      rows="3"
                                      placeholder="Descrição da role e suas permissões"></textarea>
                        </div>

                        <div class="col-md-12">
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

                        <div class="col-md-6">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input"
                                       th:field="*{padrao}"
                                       id="padrao" />
                                <label class="form-check-label" for="padrao">
                                    Role Padrão (ADMIN, USER, SERVICE)
                                </label>
                            </div>
                            <small class="text-muted">
                                Roles padrão não podem ser removidas
                            </small>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">Status</label>
                            <select class="form-select" th:field="*{ativa}">
                                <option th:value="true" selected>Ativa</option>
                                <option th:value="false">Inativa</option>
                            </select>
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

### Modelos de Dados
```java
public record RoleForm(
    String id,

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    String nome,

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,

    @NotBlank(message = "Realm é obrigatório")
    String realmId,

    Boolean padrao,

    Boolean ativa
);

public record RoleListResponse(
    String id,
    String nome,
    String descricao,
    String realmNome,
    Boolean padrao,
    Boolean ativa,
    Long totalUsuarios,
    LocalDateTime dataCriacao
);
```

## API Endpoints

### Controller
```java
@GetMapping("/admin/roles")
public String listRoles(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) String realmId,
    @RequestParam(required = false) String status,
    Model model
) {
    Page<RoleListResponse> roles = roleService.listarRoles(page, size, nome, realmId, status);
    model.addAttribute("roles", roles.getContent());
    model.addAttribute("realms", realmService.listarTodos());
    model.addAttribute("page", roles.getNumber());
    model.addAttribute("totalPages", roles.getTotalPages());
    model.addAttribute("totalElements", roles.getTotalElements());
    return "admin/roles/list";
}

@GetMapping("/admin/roles/novo")
public String novaRole(Model model) {
    model.addAttribute("roleForm", new RoleForm());
    model.addAttribute("editMode", false);
    model.addAttribute("realms", realmService.listarTodos());
    return "admin/roles/form";
}

@PostMapping("/admin/roles")
public String criarRole(
    @Valid @ModelAttribute RoleForm form,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
) {
    if (result.hasErrors()) {
        model.addAttribute("roleForm", form);
        model.addAttribute("editMode", false);
        model.addAttribute("realms", realmService.listarTodos());
        return "admin/roles/form";
    }

    try {
        roleService.criarRole(form);
        redirectAttributes.addFlashAttribute("success", "Role criada com sucesso!");
        return "redirect:/admin/roles";
    } catch (RoleJaExisteException e) {
        result.rejectValue("nome", null, e.getMessage());
        return "admin/roles/form";
    }
}

@PutMapping("/admin/roles/{id}")
public String atualizarRole(
    @PathVariable String id,
    @Valid @ModelAttribute RoleForm form,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
) {
    if (result.hasErrors()) {
        model.addAttribute("roleForm", form);
        model.addAttribute("editMode", true);
        model.addAttribute("realms", realmService.listarTodos());
        return "admin/roles/form";
    }

    form.setId(id);
    roleService.atualizarRole(form);
    redirectAttributes.addFlashAttribute("success", "Role atualizada com sucesso!");
    return "redirect:/admin/roles";
}

@DeleteMapping("/admin/roles/{id}")
public String removerRole(
    @PathVariable String id,
    RedirectAttributes redirectAttributes
) {
    try {
        roleService.removerRole(id);
        redirectAttributes.addFlashAttribute("success", "Role removida com sucesso!");
    } catch (RoleEmUsoException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/roles";
}
```

## JavaScript
```javascript
// Confirmação antes de remover role
document.querySelectorAll('[data-confirm]').forEach(element => {
    element.addEventListener('click', function(e) {
        const message = this.getAttribute('data-confirm');
        if (!confirm(message)) {
            e.preventDefault();
        }
    });
});

// Toast de sucesso/erro
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

// Desabilitar botão de remover para roles com usuários
document.querySelectorAll('a[title="Role possui usuários associados"]').forEach(el => {
    el.addEventListener('click', function(e) {
        e.preventDefault();
        alert('Esta role possui usuários associados. Não é possível remover.');
    });
});
```

## Validações
### Server-side
- Nome não vazio e entre 3 e 50 caracteres
- Nome único por realm
- Descrição máximo 500 caracteres
- Realm obrigatório
- Role não pode ser removida se tiver usuários associados
- Roles padrão (ADMIN, USER, SERVICE) não podem ser removidas

### Client-side (JavaScript)
- Nome não vazio
- Realm selecionado
- Formato correto (alfanumérico + underscores/hífens)

## Integrações
- Epic 1 (Gestão de Realms) - para seleção de realm
- Epic 2 (Gestão de Usuários) - para verificação de uso
- Epic 3 (Gestão de Roles) - backend já implementado
- Epic 7 (Auditoria) - para registro de alterações

## Arquivos a Criar/Modificar
```
src/main/resources/templates/admin/roles/
├── list.html
└── form.html

src/main/java/br/com/plataforma/conexaodigital/admin/api/
├── controller/
│   └── AdminRoleController.java
└── requests/
    └── RoleForm.java
```

## Testes
### Testes de Aceitação
- [ ] Lista de roles é exibida corretamente
- [ ] Filtros funcionam (nome, realm, status)
- [ ] Paginação funciona corretamente
- [ ] Busca textual retorna resultados corretos
- [ ] Nova role pode ser criada
- [ ] Role existente pode ser editada
- [ ] Role sem usuários pode ser removida
- [ ] Role com usuários não pode ser removida (alerta)
- [ ] Validação de nome único funciona
- [ ] Roles padrão são indicadas visualmente
- [ ] Roles padrão não podem ser removidas
- [ ] Mensagens de sucesso/erro são exibidas
- [ ] Página é responsiva em dispositivos móveis

### Testes de UI
- [ ] Tabela está alinhada e formatada corretamente
- [ ] Badges de status e padrão têm cores apropriadas
- [ ] Ícones de ações estão visíveis
- [ ] Botão de remover desabilitado para roles com usuários
- [ ] Modal de criação/edição abre corretamente
- [ ] Formulário tem validações visuais
- [ ] Botões têm feedback de clique
- [ ] Ordenação funciona

## Performance
- Carregamento da página < 2 segundos
- Filtros aplicados em < 500ms
- Modal de criação abre em < 200ms
- Operação de CRUD completa em < 1 segundo

## Dependências
- Epic 1 (Gestão de Realms) - para seleção de realm
- Epic 2 (Gestão de Usuários) - para verificação de uso
- Epic 3 (Gestão de Roles) - backend já implementado
- Epic 7 (Auditoria) - para registro de eventos

## Prioridade
**Alta** - Página essencial para gestão de RBAC

## Estimativa
- Implementação: 8 horas
- Testes: 4 horas
- Total: 12 horas

## Notas
- Utilizar layout `layouts/vertical`
- DataTables.js já está disponível nos assets
- Roles padrão devem ter tratamento especial (não podem ser removidas)
- Validação de nome único por realm é obrigatória no servidor
- Implementar toast notifications para feedback
- Considerar hierarquia de roles (future enhancement)
- Considerar permissões granulares por role (fora do escopo atual)
