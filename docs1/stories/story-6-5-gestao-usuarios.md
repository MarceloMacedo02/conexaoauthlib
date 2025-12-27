# História 6.5: Gestão de Usuários (CRUD + Reset Senha)

**Epic:** 6 - Dashboard Administrativo (Thymeleaf)  
**Status:** Ready for Review  
**Prioridade:** Alta  
**Estimativa:** 5 dias  
**Complexidade**: Alta

---

## Descrição

Como administrador do sistema, quero uma interface para gerenciar usuários (criar, editar, bloquear, desbloquear, resetar senha) para que eu possa administrar contas de acesso via dashboard.

---

## Critérios de Aceite

- [x] Lista de usuários em `/admin/usuarios`
- [x] Tabela com: nome, email, realm, roles, status, data de criação, ações
- [x] Botão para criar novo usuário
- [x] Formulário de criação/edição com seleção de realm e roles
- [x] Botão para resetar senha
- [x] Botão para bloquear/desbloquear usuário
- [x] Validação de campos
- [x] Mensagens de sucesso/erro
- [x] Paginação na lista
- [x] Filtros por nome, email, realm, status
- [x] Design responsivo usando Bootstrap 5

---

## Requisitos Técnicos

### Controller
```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUsuarioController {
    
    private final UsuarioService usuarioService;
    private final RealmRepository realmRepository;
    private final RoleRepository roleRepository;
    
    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID realmId,
            @RequestParam(required = false) StatusUsuario status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<UsuarioResponse> usuarios = usuarioService.listar(nome, email, realmId, null, null, status, null, null, null, pageable);
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("realms", realmRepository.findAll());
        return "admin/usuarios/lista";
    }
    
    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm("", "", "", null, List.of(), null, null));
        model.addAttribute("realms", realmRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/usuarios/form";
    }
    
    @PostMapping("/usuarios")
    public String criarUsuario(@Valid @ModelAttribute UsuarioForm form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/usuarios/form";
        }
        
        CriarUsuarioRequest request = new CriarUsuarioRequest(
            form.nome(), form.email(), form.senha(), form.realmId(),
            form.roleIds(), form.empresaId(), form.tenentId()
        );
        
        usuarioService.criar(request);
        redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso");
        return "redirect:/admin/usuarios";
    }
    
    @GetMapping("/usuarios/{id}")
    public String editarUsuario(@PathVariable UUID id, Model model) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuarioForm", new UsuarioForm(
            usuario.nome(), usuario.email(), "", usuario.realmId(),
            usuario.roleIds(), usuario.empresaId(), usuario.tenentId()
        ));
        model.addAttribute("usuarioId", id);
        model.addAttribute("realms", realmRepository.findAll());
        model.addAttribute("roles", roleRepository.findByRealmId(usuario.realmId()));
        return "admin/usuarios/form";
    }
    
    @PostMapping("/usuarios/{id}")
    public String atualizarUsuario(@PathVariable UUID id,
                                     @Valid @ModelAttribute UsuarioForm form,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/usuarios/form";
        }
        
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(
            form.nome(), form.email(), form.roleIds(), form.empresaId(), form.tenentId()
        );
        
        usuarioService.atualizar(id, request);
        redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso");
        return "redirect:/admin/usuarios";
    }
    
    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        usuarioService.bloquear(id);
        redirectAttributes.addFlashAttribute("success", "Usuário bloqueado com sucesso");
        return "redirect:/admin/usuarios";
    }
    
    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        usuarioService.desbloquear(id);
        redirectAttributes.addFlashAttribute("success", "Usuário desbloqueado com sucesso");
        return "redirect:/admin/usuarios";
    }
    
    @PostMapping("/usuarios/{id}/reset-senha")
    public String resetarSenha(@PathVariable UUID id,
                                @RequestParam String novaSenha,
                                RedirectAttributes redirectAttributes) {
        ResetSenhaAdminRequest request = new ResetSenhaAdminRequest(novaSenha);
        usuarioService.resetSenhaAdmin(id, request);
        redirectAttributes.addFlashAttribute("success", "Senha resetada com sucesso");
        return "redirect:/admin/usuarios";
    }
}
```

---

## Dependências

- História 2.1: Criar Usuário
- História 2.2: Editar Usuário
- História 2.3: Bloquear Usuário
- História 2.4: Desbloquear Usuário
- História 2.5: Reset Administrativo de Senha
- História 2.8: Listar Usuários

---

## Pontos de Atenção

- Paginação e filtros funcionais
- Seleção múltipla de roles
- Modal para reset de senha
- Mensagens flash para feedback
- Design responsivo com Bootstrap 5
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### File List
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminUsuarioController.java`
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/dto/forms/UsuarioForm.java`
- `src/main/resources/templates/admin/usuarios/lista.html`
- `src/main/resources/templates/admin/usuarios/form.html`
- `src/main/resources/templates/admin/usuarios/reset-senha.html`
- `src/test/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminUsuarioControllerTest.java`

### Debug Log References
- N/A - Sem erros de depuração durante implementação

### Completion Notes
- Todos os endpoints CRUD implementados com sucesso
- Funcionalidade de reset de senha administrativo implementada
- Funcionalidades de bloqueio/desbloqueio implementadas
- Validação de formulários funcionando corretamente
- Templates Thymeleaf criados com Bootstrap 5
- Paginação e filtros implementados conforme especificação
- Mensagens flash para feedback ao usuário implementadas
- Testes unitários criados para todos os métodos do controller
- Formulário de reset de senha com confirmação de senhas

### Change Log
- [x] Criar controller AdminUsuarioController
- [x] Criar DTO UsuarioForm para formulários
- [x] Implementar endpoint de listagem com paginação e filtros
- [x] Implementar endpoint de criação de usuário
- [x] Implementar endpoint de edição de usuário
- [x] Implementar endpoint de bloqueio de usuário
- [x] Implementar endpoint de desbloqueio de usuário
- [x] Implementar endpoint de reset de senha administrativo
- [x] Criar template de listagem de usuários
- [x] Criar template de formulário de usuário
- [x] Criar template de reset de senha
- [x] Criar testes unitários para o controller
- [x] Validação de inputs e tratamento de exceções

### Status
Ready for Review
