# User Story: Controller API (AdminUsuarioController)

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-04

## Descrição
Criar o `AdminUsuarioController` com endpoints MVC para a página administrativa de gestão de usuários, incluindo endpoints de listagem, criação, edição, ativação, bloqueio, reset administrativo de senha e associação de roles.

## Critérios de Aceite
- [ ] Classe `AdminUsuarioController` criada com `@Controller`
- [ ] Endpoint `GET /admin/usuarios` implementado (listagem)
- [ ] Endpoint `GET /admin/usuarios/novo` implementado (form de criação)
- [ ] Endpoint `GET /admin/usuarios/{id}/edit` implementado (form de edição)
- [ ] Endpoint `GET /admin/usuarios/{id}` implementado (visualização detalhada)
- [ ] Endpoint `POST /admin/usuarios` implementado (criação)
- [ ] Endpoint `PUT /admin/usuarios/{id}` implementado (atualização)
- [ ] Endpoint `POST /admin/usuarios/reset-senha/{id}` implementado (reset administrativo)
- [ ] Endpoint `PUT /admin/usuarios/{id}/bloquear` implementado (bloqueio)
- [ ] Endpoint `PUT /admin/usuarios/{id}/ativar` implementado (reativação)
- [ ] Endpoint `GET /api/v1/admin/usuarios/roles/{realmId}` implementado (listagem de roles)
- [ ] Model binding configurado corretamente
- [ ] Validação de formulários funcionando
- [ ] Flash messages (success/error) implementadas
- [ ] Redirect attributes configurados

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.controller` (se não existir)
2. Criar classe `AdminUsuarioController.java`
3. Implementar endpoint de listagem (GET /admin/usuarios)
4. Implementar endpoint de formulário de criação (GET /admin/usuarios/novo)
5. Implementar endpoint de formulário de edição (GET /admin/usuarios/{id}/edit)
6. Implementar endpoint de visualização detalhada (GET /admin/usuarios/{id})
7. Implementar endpoint de criação (POST /admin/usuarios)
8. Implementar endpoint de atualização (PUT /admin/usuarios/{id})
9. Implementar endpoint de reset administrativo de senha (POST /admin/usuarios/reset-senha/{id})
10. Implementar endpoint de bloqueio (PUT /admin/usuarios/{id}/bloquear)
11. Implementar endpoint de ativação (PUT /admin/usuarios/{id}/ativar)
12. Implementar endpoint de listagem de roles (GET /api/v1/admin/usuarios/roles/{realmId})
13. Configurar Model binding para formulários
14. Implementar flash messages para feedback

## Instruções de Implementação

### Controller Class
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminUsuarioController.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.requests.UsuarioForm;
import br.com.plataforma.conexaodigital.admin.api.responses.UsuarioDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.UsuarioListResponse;
import br.com.plataforma.conexaodigital.admin.domain.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

/**
 * Controller MVC para página administrativa de gestão de usuários.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Página de listagem de usuários.
     *
     * @param page número da página (padrão: 0)
     * @param size tamanho da página (padrão: 10)
     * @param nome filtro por nome (opcional)
     * @param status filtro por status (opcional: Ativo, Bloqueado)
     * @param realmId filtro por realm (opcional)
     * @param model Spring Model
     * @return template de listagem
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarUsuarios(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String realmId,
        Model model
    ) {
        // Configurar paginação
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("dataCriacao").descending()
        );

        // Buscar usuários
        Page<UsuarioListResponse> usuarios = usuarioService.listarUsuarios(
            pageable, nome, status, realmId
        );

        // Adicionar ao model
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("nome", nome);
        model.addAttribute("status", status);
        model.addAttribute("realmId", realmId);

        return "admin/usuarios/list";
    }

    /**
     * Página de visualização detalhada de usuário.
     *
     * @param id ID do usuário
     * @param model Spring Model
     * @return template de detalhes
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String visualizarUsuario(
        @PathVariable UUID id,
        Model model
    ) {
        UsuarioDetailResponse usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        return "admin/usuarios/detail";
    }

    /**
     * Página de formulário de criação de usuário.
     *
     * @param model Spring Model
     * @return template de formulário
     */
    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoUsuarioForm(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        model.addAttribute("pageTitle", "Novo Usuário");
        model.addAttribute("editMode", false);

        return "admin/usuarios/form";
    }

    /**
     * Página de formulário de edição de usuário.
     *
     * @param id ID do usuário
     * @param model Spring Model
     * @return template de formulário
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarUsuarioForm(
        @PathVariable UUID id,
        Model model
    ) {
        UsuarioDetailResponse usuario = usuarioService.buscarPorId(id);

        // Criar form com dados do usuário
        UsuarioForm form = new UsuarioForm(
            usuario.id(),
            usuario.nome(),
            usuario.email(),
            usuario.cpf(),
            usuario.realmId(),
            "Ativo".equals(usuario.status()),
            null // roles serão carregados no form
        );

        model.addAttribute("usuarioForm", form);
        model.addAttribute("pageTitle", "Editar Usuário");
        model.addAttribute("editMode", true);
        model.addAttribute("usuarioId", id);

        return "admin/usuarios/form";
    }

    /**
     * Processa criação de novo usuário.
     *
     * @param form formulário de criação
     * @param result resultado da validação
     * @param redirectAttributes atributos de redirect
     * @param authentication autenticação do usuário atual
     * @return redirect para listagem ou form em caso de erro
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criarUsuario(
        @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {
        // Validações adicionais
        if (!form.isCpfValido()) {
            result.rejectValue("cpf", "cpf.invalido", "CPF deve conter 11 dígitos");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro na validação do formulário");
            return "admin/usuarios/form";
        }

        try {
            // Obter ID do usuário atual
            UUID usuarioIdCriador = UUID.fromString(authentication.getName());

            // Criar usuário
            String usuarioId = usuarioService.criarUsuario(form, usuarioIdCriador);

            redirectAttributes.addFlashAttribute(
                "success",
                "Usuário criado com sucesso!"
            );

            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/usuarios/form";
        }
    }

    /**
     * Processa atualização de usuário existente.
     *
     * @param id ID do usuário
     * @param form formulário de edição
     * @param result resultado da validação
     * @param redirectAttributes atributos de redirect
     * @param authentication autenticação do usuário atual
     * @return redirect para listagem ou form em caso de erro
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarUsuario(
        @PathVariable UUID id,
        @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {
        // Validações adicionais
        if (!form.isCpfValido()) {
            result.rejectValue("cpf", "cpf.invalido", "CPF deve conter 11 dígitos");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro na validação do formulário");
            return "admin/usuarios/form";
        }

        try {
            // Obter ID do usuário atual
            UUID usuarioIdEditor = UUID.fromString(authentication.getName());

            // Atualizar usuário
            usuarioService.atualizarUsuario(id, form, usuarioIdEditor);

            redirectAttributes.addFlashAttribute(
                "success",
                "Usuário atualizado com sucesso!"
            );

            return "redirect:/admin/usuarios";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/usuarios/form";
        }
    }

    /**
     * Ativa um usuário bloqueado.
     *
     * @param id ID do usuário
     * @param redirectAttributes atributos de redirect
     * @param authentication autenticação do usuário atual
     * @return redirect para listagem
     */
    @PutMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public String ativarUsuario(
        @PathVariable UUID id,
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {
        try {
            UUID usuarioIdOperador = UUID.fromString(authentication.getName());

            usuarioService.ativarUsuario(id, usuarioIdOperador);

            redirectAttributes.addFlashAttribute(
                "success",
                "Usuário ativado com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Bloqueia um usuário ativo.
     *
     * @param id ID do usuário
     * @param motivo motivo do bloqueio
     * @param redirectAttributes atributos de redirect
     * @param authentication autenticação do usuário atual
     * @return redirect para listagem
     */
    @PutMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public String bloquearUsuario(
        @PathVariable UUID id,
        @RequestParam String motivo,
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {
        try {
            UUID usuarioIdOperador = UUID.fromString(authentication.getName());

            usuarioService.bloquearUsuario(id, motivo, usuarioIdOperador);

            redirectAttributes.addFlashAttribute(
                "success",
                "Usuário bloqueado com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Realiza reset administrativo de senha.
     *
     * @param id ID do usuário
     * @param redirectAttributes atributos de redirect
     * @param authentication autenticação do usuário atual
     * @return redirect para listagem
     */
    @PostMapping("/reset-senha/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetSenhaAdministrativo(
        @PathVariable UUID id,
        RedirectAttributes redirectAttributes,
        Authentication authentication
    ) {
        try {
            UUID usuarioIdOperador = UUID.fromString(authentication.getName());

            String codigoReset = usuarioService.resetSenhaAdministrativo(
                id, usuarioIdOperador
            );

            redirectAttributes.addFlashAttribute(
                "success",
                "Reset de senha solicitado com sucesso! Código: " + codigoReset +
                " (expira em 15 minutos)"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * API endpoint para listar roles disponíveis por realm.
     *
     * @param realmId ID do realm
     * @return lista de roles como JSON
     */
    @GetMapping("/api/roles/{realmId}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public Object listarRolesPorRealm(
        @PathVariable String realmId
    ) {
        return usuarioService.listarRolesDisponiveis(
            UUID.fromString(realmId)
        );
    }
}
```

### Thymeleaf Form Binding (Template `admin/usuarios/form.html`)
**Adicionar ao template do formulário (se criar template separado):**

```html
<form th:action="${editMode ? @{/admin/usuarios/{id}(id=${usuarioId})} : @{/admin/usuarios}}"
      th:object="${usuarioForm}"
      th:method="${editMode ? 'put' : 'post'}"
      id="usuarioForm"
      class="needs-validation"
      novalidate>

    <!-- Campo oculto para ID (edição) -->
    <input type="hidden" th:field="*{id}" th:if="${editMode}" />

    <!-- ... resto do formulário será implementado na Story 05 ... -->

</form>
```

## Checklist de Validação
- [ ] Classe `AdminUsuarioController` criada com `@Controller`
- [ ] Mapeamento `@RequestMapping("/admin/usuarios")` configurado
- [ ] Endpoint `GET /admin/usuarios` implementado com paginação e filtros
- [ ] Endpoint `GET /admin/usuarios/{id}` implementado
- [ ] Endpoint `GET /admin/usuarios/novo` implementado
- [ ] Endpoint `GET /admin/usuarios/{id}/edit` implementado
- [ ] Endpoint `POST /admin/usuarios` implementado com validação
- [ ] Endpoint `PUT /admin/usuarios/{id}` implementado com validação
- [ ] Endpoint `PUT /admin/usuarios/{id}/ativar` implementado
- [ ] Endpoint `PUT /admin/usuarios/{id}/bloquear` implementado
- [ ] Endpoint `POST /admin/usuarios/reset-senha/{id}` implementado
- [ ] Endpoint `GET /admin/usuarios/api/roles/{realmId}` implementado
- [ ] Anotação `@Valid` aplicada em formulários
- [ ] `BindingResult` configurado para capturar erros de validação
- [ ] Flash messages (success/error) implementadas
- [ ] `RedirectAttributes` configurado para mensagens
- [ ] `@PreAuthorize` configurado para segurança (ADMIN)
- [ ] Model binding com `@ModelAttribute` configurado
- [ ] Tratamento de exceções com try/catch implementado
- [ ] Validação de CPF implementada no controller
- [ ] Página de formulário separada ou modal implementado

## Anotações
- Todos os endpoints protegidos com `@PreAuthorize("hasRole('ADMIN')")`
- Flash messages permitem exibir feedback após redirect
- Validação de CPF é feita no controller (opcional no formulário)
- Reset administrativo gera código de 6 dígitos (Story 07)
- Endpoint de API para roles permite preencher multi-select via AJAX
- Página de formulário pode ser separada (form.html) ou modal na listagem
- Erros de validação redirecionam de volta para o formulário
- Mensagens de sucesso redirecionam para a listagem

## Dependências
- Story 02 (DTOs de Usuário) - DTOs necessários
- Story 03 (Backend Service Layer) - Service layer implementado
- Epic 2 (Gestão de Usuários) - lógica de negócio já existe
- Epic 9 (Configuração) - Spring Security configurado

## Prioridade
**Alta** - Controller necessário para funcionar a página

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas
