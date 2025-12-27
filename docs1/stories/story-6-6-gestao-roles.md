# História 6.6: Gestão de Roles (CRUD)

**Epic:** 6 - Dashboard Administrativo (Thymeleaf)  
**Status:** Ready for Review  
**Prioridade:** Média  
**Estimativa:** 3 dias  
**Complexidade**: Média

---

## Descrição

Como administrador do sistema, quero uma interface para gerenciar roles (criar, editar, remover) para que eu possa administrar permissões via dashboard.

---

## Critérios de Aceite

- [x] Lista de roles em `/admin/roles`
- [x] Tabela com: nome, descrição, realm, é padrão, data de criação, ações (editar, remover)
- [x] Botão para criar nova role
- [x] Formulário de criação/edição
- [x] Validação de campos
- [x] Mensagens de sucesso/erro
- [x] Filtro por realm
- [x] Design responsivo usando Bootstrap 5

---

## Requisitos Técnicos

### Controller
```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRoleController {
    
    private final RoleService roleService;
    private final RealmRepository realmRepository;
    
    @GetMapping("/roles")
    public String listarRoles(@RequestParam(required = false) UUID realmId, Model model) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("nome").ascending());
        Page<RoleResponse> roles = realmId != null ? 
            roleService.listar(realmId, null, null, pageable) : 
            roleService.listar(null, null, null, pageable);
        
        model.addAttribute("roles", roles);
        model.addAttribute("realms", realmRepository.findAll());
        model.addAttribute("realmId", realmId);
        return "admin/roles/lista";
    }
    
    @GetMapping("/roles/novo")
    public String novaRole(Model model) {
        model.addAttribute("roleForm", new RoleForm("", "", null));
        model.addAttribute("realms", realmRepository.findAll());
        return "admin/roles/form";
    }
    
    @PostMapping("/roles")
    public String criarRole(@Valid @ModelAttribute RoleForm form,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/roles/form";
        }
        
        CriarRoleRequest request = new CriarRoleRequest(form.nome(), form.descricao(), form.realmId());
        roleService.criar(request);
        
        redirectAttributes.addFlashAttribute("success", "Role criada com sucesso");
        return "redirect:/admin/roles";
    }
    
    @GetMapping("/roles/{id}")
    public String editarRole(@PathVariable UUID id, Model model) {
        RoleResponse role = roleService.buscarPorId(id);
        model.addAttribute("roleForm", new RoleForm(role.nome(), role.descricao(), role.realmId()));
        model.addAttribute("roleId", id);
        model.addAttribute("realms", realmRepository.findAll());
        return "admin/roles/form";
    }
    
    @PostMapping("/roles/{id}")
    public String atualizarRole(@PathVariable UUID id,
                                  @Valid @ModelAttribute RoleForm form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/roles/form";
        }
        
        AtualizarRoleRequest request = new AtualizarRoleRequest(form.descricao());
        roleService.atualizar(id, request);
        
        redirectAttributes.addFlashAttribute("success", "Role atualizada com sucesso");
        return "redirect:/admin/roles";
    }
    
    @PostMapping("/roles/{id}/remover")
    public String removerRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        roleService.remover(id);
        redirectAttributes.addFlashAttribute("success", "Role removida com sucesso");
        return "redirect:/admin/roles";
    }
}
```

---

## Dependências

- História 3.1: Criar Role
- História 3.2: Editar Role
- História 3.3: Remover Role
- História 3.4: Listar Roles

---

## Pontos de Atenção

- Filtro por realm funcional
- Validação de formulário
- Mensagens flash para feedback
- Roles padrão não podem ser removidas
- Design responsivo com Bootstrap 5
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRoleController.java`
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/dto/forms/RoleForm.java`
- `src/main/resources/templates/admin/roles/lista.html`
- `src/main/resources/templates/admin/roles/form.html`
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminApiController.java`
- `src/test/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRoleControllerTest.java`

### Debug Log References
- N/A - Sem erros de depuração durante implementação

### Completion Notes
- Todos os endpoints CRUD implementados com sucesso
- Validação de formulários funcionando corretamente
- Templates Thymeleaf criados com Bootstrap 5
- Filtro por realm implementado conforme especificação
- Proteção contra remoção de roles padrão implementada
- Mensagens flash para feedback ao usuário implementadas
- Testes unitários criados para todos os métodos do controller
- Endpoint API para carregamento dinâmico de roles por realm
- Layout responsivo com navegação lateral e breadcrumb

### Change Log
- [x] Criar controller AdminRoleController
- [x] Criar DTO RoleForm para formulários
- [x] Implementar endpoint de listagem com filtro por realm
- [x] Implementar endpoint de criação de role
- [x] Implementar endpoint de edição de role
- [x] Implementar endpoint de remoção de role
- [x] Criar template de listagem de roles
- [x] Criar template de formulário de role
- [x] Criar endpoint API para suporte (roles por realm)
- [x] Criar testes unitários para o controller
- [x] Validação de inputs e tratamento de exceções
- [x] Proteção contra remoção de roles padrão no template

### Status
Ready for Review
