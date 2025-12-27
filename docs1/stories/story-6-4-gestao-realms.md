# História 6.4: Gestão de Realms (CRUD)

**Epic:** 6 - Dashboard Administrativo (Thymeleaf)  
**Status:** Ready for Review  
**Prioridade:** Média  
**Estimativa:** 4 dias  
**Complexidade**: Média

---

## Descrição

Como administrador do sistema, quero uma interface para gerenciar realms (criar, editar, desativar) para que eu possa administrar domínios lógicos via dashboard.

---

## Critérios de Aceite

- [x] Lista de realms em `/admin/realms`
- [x] Tabela com: nome, status, data de criação, ações (editar, desativar, reativar)
- [x] Botão para criar novo realm
- [x] Formulário de criação/edição
- [x] Validação de campos
- [x] Mensagens de sucesso/erro
- [x] Paginação na lista
- [x] Filtros por nome e status
- [x] Design responsivo usando Bootstrap 5

---

## Requisitos Técnicos

### Template Lista de Realms
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Realms - Admin Dashboard</title>
    <link th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}" rel="stylesheet">
</head>
<body>
    <nav th:replace="~{admin/fragments/navbar :: navbar}"></nav>
    
    <div class="container-fluid mt-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2>Realms</h2>
            <a th:href="@{/admin/realms/novo}" class="btn btn-primary">
                <i class="bi bi-plus"></i> Novo Realm
            </a>
        </div>
        
        <div class="card">
            <div class="card-body">
                <form th:action="@{/admin/realms}" method="get" class="mb-3">
                    <div class="row">
                        <div class="col-md-4">
                            <input type="text" name="nome" class="form-control" placeholder="Filtrar por nome" th:value="${param.nome}">
                        </div>
                        <div class="col-md-3">
                            <select name="status" class="form-select">
                                <option value="">Todos os status</option>
                                <option value="ATIVO" th:selected="${param.status == 'ATIVO'}">Ativo</option>
                                <option value="INATIVO" th:selected="${param.status == 'INATIVO'}">Inativo</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-secondary">Filtrar</button>
                        </div>
                    </div>
                </form>
                
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Status</th>
                            <th>Data Criação</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="realm : ${realms.content}">
                            <td th:text="${realm.nome}"></td>
                            <td>
                                <span class="badge" 
                                      th:class="'badge ' + (${realm.status} == T(br.com.plataforma.conexaodigital.realm.domain.model.StatusRealm).ATIVO ? 'bg-success' : 'bg-danger')"
                                      th:text="${realm.status}">
                                </span>
                            </td>
                            <td th:text="${realm.dataCriacao}"></td>
                            <td>
                                <a th:href="@{/admin/realms/{id}(id=${realm.id})}" class="btn btn-sm btn-primary">Editar</a>
                                <a th:if="${realm.status == T(br.com.plataforma.conexaodigital.realm.domain.model.StatusRealm).ATIVO}" 
                                   th:href="@{/admin/realms/{id}/desativar(id=${realm.id})}" 
                                   class="btn btn-sm btn-danger">Desativar</a>
                                <a th:if="${realm.status == T(br.com.plataforma.conexaodigital.realm.domain.model.StatusRealm).INATIVO}" 
                                   th:href="@{/admin/realms/{id}/reativar(id=${realm.id})}" 
                                   class="btn btn-sm btn-success">Reativar</a>
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <nav th:if="${realms.totalPages > 1}">
                    <ul class="pagination">
                        <li class="page-item" th:class="${realms.number == 0} ? 'disabled' : ''">
                            <a class="page-link" th:href="@{/admin/realms(page=${realms.number - 1}, nome=${param.nome}, status=${param.status})}">Anterior</a>
                        </li>
                        <li class="page-item" th:each="i : ${#numbers.sequence(0, realms.totalPages - 1)}" 
                            th:class="${realms.number == i} ? 'active' : ''">
                            <a class="page-link" th:href="@{/admin/realms(page=${i}, nome=${param.nome}, status=${param.status})}" th:text="${i + 1}"></a>
                        </li>
                        <li class="page-item" th:class="${realms.number == realms.totalPages - 1} ? 'disabled' : ''">
                            <a class="page-link" th:href="@{/admin/realms(page=${realms.number + 1}, nome=${param.nome}, status=${param.status})}">Próximo</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
    
    <script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>
</body>
</html>
```

### Controller
```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRealmController {
    
    private final RealmService realmService;
    
    @GetMapping("/realms")
    public String listarRealms(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) StatusRealm status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<RealmResponse> realms = realmService.listar(nome, status, pageable);
        
        model.addAttribute("realms", realms);
        return "admin/realms/lista";
    }
    
    @GetMapping("/realms/novo")
    public String novoRealm(Model model) {
        model.addAttribute("realmForm", new RealmForm("", StatusRealm.ATIVO));
        return "admin/realms/form";
    }
    
    @PostMapping("/realms")
    public String criarRealm(@Valid @ModelAttribute RealmForm form, 
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/realms/form";
        }
        
        CriarRealmRequest request = new CriarRealmRequest(form.nome());
        realmService.criar(request);
        
        redirectAttributes.addFlashAttribute("success", "Realm criado com sucesso");
        return "redirect:/admin/realms";
    }
    
    @GetMapping("/realms/{id}")
    public String editarRealm(@PathVariable UUID id, Model model) {
        RealmResponse realm = realmService.buscarPorId(id);
        model.addAttribute("realmForm", new RealmForm(realm.nome(), realm.status()));
        model.addAttribute("realmId", id);
        return "admin/realms/form";
    }
    
    @PostMapping("/realms/{id}")
    public String atualizarRealm(@PathVariable UUID id,
                                  @Valid @ModelAttribute RealmForm form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/realms/form";
        }
        
        AtualizarRealmRequest request = new AtualizarRealmRequest(form.nome(), form.status());
        realmService.atualizar(id, request);
        
        redirectAttributes.addFlashAttribute("success", "Realm atualizado com sucesso");
        return "redirect:/admin/realms";
    }
    
    @PostMapping("/realms/{id}/desativar")
    public String desativarRealm(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        realmService.desativar(id);
        redirectAttributes.addFlashAttribute("success", "Realm desativado com sucesso");
        return "redirect:/admin/realms";
    }
    
    @PostMapping("/realms/{id}/reativar")
    public String reativarRealm(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        realmService.reativar(id);
        redirectAttributes.addFlashAttribute("success", "Realm reativado com sucesso");
        return "redirect:/admin/realms";
    }
}
```

---

## Dependências

- História 1.1: Criar Realm
- História 1.2: Editar Realm
- História 1.3: Desativar Realm
- História 1.4: Reativar Realm
- História 1.5: Listar Realms

---

## Pontos de Atenção

- Paginação e filtros funcionais
- Validação de formulário
- Mensagens flash para feedback
- Design responsivo com Bootstrap 5
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRealmController.java`
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/dto/forms/RealmForm.java`
- `src/main/resources/templates/admin/realms/lista.html`
- `src/main/resources/templates/admin/realms/form.html`
- `src/test/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRealmControllerTest.java`

### Debug Log References
- N/A - Sem erros de depuração durante implementação

### Completion Notes
- Todos os endpoints CRUD implementados com sucesso
- Validação de formulários funcionando corretamente
- Templates Thymeleaf criados com Bootstrap 5
- Paginação e filtros implementados conforme especificação
- Mensagens flash para feedback ao usuário implementadas
- Testes unitários criados para todos os métodos do controller

### Change Log
- [x] Criar controller AdminRealmController
- [x] Criar DTO RealmForm para formulários
- [x] Implementar endpoint de listagem com paginação e filtros
- [x] Implementar endpoint de criação de realm
- [x] Implementar endpoint de edição de realm
- [x] Implementar endpoint de desativação de realm
- [x] Implementar endpoint de reativação de realm
- [x] Criar template de listagem de realms
- [x] Criar template de formulário de realm
- [x] Criar testes unitários para o controller
- [x] Validação de inputs e tratamento de exceções

### Status
Ready for Review
