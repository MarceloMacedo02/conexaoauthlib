# User Story: Controller API - AdminRoleController

**Epic:** 14 - Página de Gestão de Roles (Thymeleaf)
**Story ID:** epic-14-story-04

## Descrição
Implementar o controller `AdminRoleController` com endpoints para gestão de roles, incluindo listagem, criação, edição, remoção e busca por ID. O controller deve integrar com o service layer e retornar templates Thymeleaf ou JSON conforme necessário.

## Critérios de Aceite
- [ ] Classe `AdminRoleController` criada com todas as rotas
- [ ] Endpoint `GET /admin/roles` implementado (listagem)
- [ ] Endpoint `GET /admin/roles/{id}` implementado (detalhes/edição)
- [ ] Endpoint `POST /admin/roles` implementado (criação)
- [ ] Endpoint `PUT /admin/roles/{id}` implementado (atualização)
- [ ] Endpoint `DELETE /admin/roles/{id}` implementado (remoção)
- [ ] Validação de formulário configurada
- [ ] Flash attributes para mensagens de sucesso/erro
- [ ] Tratamento de exceções apropriado
- [ ] Testes de integração criados

## Tarefas
1. Criar classe `AdminRoleController.java`
2. Implementar endpoint de listagem com paginação e filtros
3. Implementar endpoint de criação (GET para form, POST para salvar)
4. Implementar endpoint de edição (GET para form, PUT para salvar)
5. Implementar endpoint de remoção
6. Implementar endpoint para buscar por ID (JSON)
7. Configurar flash attributes para mensagens
8. Criar testes de integração

## Instruções de Implementação

### Controller: AdminRoleController
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRoleController.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.requests.RoleForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleListResponse;
import br.com.plataforma.conexaodigital.admin.api.service.AdminRoleApiService;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleEmUsoException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleJaExisteException;
import br.com.plataforma.conexaodigital.gestarole.domain.exception.RoleNaoEncontradaException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller para gestão de roles na página administrativa.
 */
@Slf4j
@Controller
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleApiService adminRoleService;

    /**
     * Listagem de roles.
     */
    @GetMapping
    public String listarRoles(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String realmId,
        @RequestParam(required = false) String status,
        Model model
    ) {
        log.debug("GET /admin/roles - page: {}, size: {}, nome: {}, realmId: {}, status: {}",
            page, size, nome, realmId, status);

        Page<RoleListResponse> rolesPage = adminRoleService.listarRoles(
            page, size, nome, realmId, status
        );

        List<AdminRoleApiService.RealmSimpleResponse> realms = adminRoleService.listarTodosRealms();

        model.addAttribute("roles", rolesPage.getContent());
        model.addAttribute("page", rolesPage.getNumber());
        model.addAttribute("totalPages", rolesPage.getTotalPages());
        model.addAttribute("totalElements", rolesPage.getTotalElements());
        model.addAttribute("realms", realms);

        // Manter filtros no model para re-aplicação
        model.addAttribute("nomeFilter", nome);
        model.addAttribute("realmIdFilter", realmId);
        model.addAttribute("statusFilter", status);

        return "admin/roles/list";
    }

    /**
     * Formulário de criação de nova role.
     */
    @GetMapping("/novo")
    public String novaRole(Model model) {
        log.debug("GET /admin/roles/novo");

        model.addAttribute("roleForm", new RoleForm());
        model.addAttribute("editMode", false);
        model.addAttribute("realms", adminRoleService.listarTodosRealms());

        return "admin/roles/form";
    }

    /**
     * Salvar nova role.
     */
    @PostMapping
    public String salvarNovaRole(
        @Valid @ModelAttribute RoleForm form,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        log.debug("POST /admin/roles - Criando role: {}", form.nome());

        if (result.hasErrors()) {
            log.warn("Erros de validação ao criar role: {}", result.getAllErrors());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", false);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        }

        try {
            RoleDetailResponse created = adminRoleService.criarRole(form);
            redirectAttributes.addFlashAttribute("success", "Role '" + created.nome() +
                "' criada com sucesso!");
            log.info("Role criada com sucesso: {}", created.nome());
            return "redirect:/admin/roles";
        } catch (RoleJaExisteException e) {
            log.warn("Role já existe: {}", e.getMessage());
            result.reject("nome", null, e.getMessage());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", false);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar role: {}", e.getMessage());
            result.reject(null, e.getMessage());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", false);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        }
    }

    /**
     * Formulário de edição de role.
     */
    @GetMapping("/{id}/editar")
    public String editarRole(
        @PathVariable String id,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        log.debug("GET /admin/roles/{}/editar", id);

        try {
            RoleDetailResponse role = adminRoleService.buscarPorId(id);

            RoleForm form = new RoleForm(
                role.id(),
                role.nome(),
                role.descricao(),
                role.realmId(),
                role.ativa(),
                role.padrao()
            );

            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());

            return "admin/roles/form";
        } catch (RoleNaoEncontradaException e) {
            log.error("Role não encontrada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Role não encontrada");
            return "redirect:/admin/roles";
        }
    }

    /**
     * Atualizar role existente.
     */
    @PutMapping("/{id}")
    public String atualizarRole(
        @PathVariable String id,
        @Valid @ModelAttribute RoleForm form,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        log.debug("PUT /admin/roles/{} - Atualizando role: {}", id, form.nome());

        form.setId(id);

        if (result.hasErrors()) {
            log.warn("Erros de validação ao atualizar role: {}", result.getAllErrors());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        }

        try {
            RoleDetailResponse updated = adminRoleService.atualizarRole(form);
            redirectAttributes.addFlashAttribute("success", "Role '" + updated.nome() +
                "' atualizada com sucesso!");
            log.info("Role atualizada com sucesso: {}", updated.nome());
            return "redirect:/admin/roles";
        } catch (RoleJaExisteException e) {
            log.warn("Role já existe: {}", e.getMessage());
            result.reject("nome", null, e.getMessage());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        } catch (RoleNaoEncontradaException e) {
            log.error("Role não encontrada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Role não encontrada");
            return "redirect:/admin/roles";
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar role: {}", e.getMessage());
            result.reject(null, e.getMessage());
            model.addAttribute("roleForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("realms", adminRoleService.listarTodosRealms());
            return "admin/roles/form";
        }
    }

    /**
     * Remover role.
     */
    @DeleteMapping("/{id}")
    public String removerRole(
        @PathVariable String id,
        RedirectAttributes redirectAttributes
    ) {
        log.debug("DELETE /admin/roles/{}", id);

        try {
            adminRoleService.removerRole(id);
            redirectAttributes.addFlashAttribute("success", "Role removida com sucesso!");
            log.info("Role removida com sucesso: {}", id);
            return "redirect:/admin/roles";
        } catch (RoleNaoEncontradaException e) {
            log.error("Role não encontrada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Role não encontrada");
            return "redirect:/admin/roles";
        } catch (RoleEmUsoException e) {
            log.warn("Role em uso: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/roles";
        }
    }

    /**
     * Buscar role por ID (JSON para AJAX).
     */
    @GetMapping("/{id}")
    @ResponseBody
    public RoleDetailResponse buscarPorIdJson(@PathVariable String id) {
        log.debug("GET /admin/roles/{} (JSON)", id);

        return adminRoleService.buscarPorId(id);
    }
}
```

### Testes de Integração
**Localização:** `src/test/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRoleControllerTest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.requests.RoleForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RoleListResponse;
import br.com.plataforma.conexaodigital.admin.api.service.AdminRoleApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminRoleController.class)
class AdminRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminRoleService adminRoleService;

    @Test
    void testListarRoles() throws Exception {
        List<RoleListResponse> roles = List.of(
            new RoleListResponse("1", "ADMIN", "Admin role", "Master", "r1",
                true, true, 5L, LocalDateTime.now())
        );
        Page<RoleListResponse> page = new PageImpl<>(roles);

        when(adminRoleService.listarRoles(anyInt(), anyInt(), any(), any(), any()))
            .thenReturn(page);
        when(adminRoleService.listarTodosRealms())
            .thenReturn(List.of());

        mockMvc.perform(get("/admin/roles"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/roles/list"))
            .andExpect(model().attributeExists("roles", "page", "totalPages"));
    }

    @Test
    void testNovaRole() throws Exception {
        when(adminRoleService.listarTodosRealms())
            .thenReturn(List.of());

        mockMvc.perform(get("/admin/roles/novo"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/roles/form"))
            .andExpect(model().attributeExists("roleForm", "editMode"))
            .andExpect(model().attribute("editMode", false));
    }

    @Test
    void testSalvarNovaRoleSucesso() throws Exception {
        RoleDetailResponse created = new RoleDetailResponse(
            "1", "ADMIN", "Admin", "Master", "r1",
            true, true, 0L, List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(adminRoleService.criarRole(any(RoleForm.class)))
            .thenReturn(created);

        mockMvc.perform(post("/admin/roles")
                .param("nome", "ADMIN")
                .param("descricao", "Admin role")
                .param("realmId", "r1")
                .param("ativa", "true")
                .param("padrao", "true")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/roles"))
            .andExpect(flash().attributeExists("success"));
    }

    @Test
    void testSalvarNovaRoleValidacao() throws Exception {
        when(adminRoleService.listarTodosRealms())
            .thenReturn(List.of());

        mockMvc.perform(post("/admin/roles")
                .param("nome", "") // inválido
                .param("descricao", "Admin role")
                .param("realmId", "r1")
                .param("ativa", "true")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/roles/form"))
            .andExpect(model().attributeHasErrors("roleForm"));
    }

    @Test
    void testEditarRole() throws Exception {
        RoleDetailResponse role = new RoleDetailResponse(
            "1", "ADMIN", "Admin", "Master", "r1",
            true, true, 5L, List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(adminRoleService.buscarPorId("1"))
            .thenReturn(role);
        when(adminRoleService.listarTodosRealms())
            .thenReturn(List.of());

        mockMvc.perform(get("/admin/roles/1/editar"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/roles/form"))
            .andExpect(model().attribute("editMode", true));
    }

    @Test
    void testRemoverRole() throws Exception {
        doNothing().when(adminRoleService).removerRole("1");

        mockMvc.perform(delete("/admin/roles/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/roles"))
            .andExpect(flash().attributeExists("success"));

        verify(adminRoleService, times(1)).removerRole("1");
    }

    @Test
    void testRemoverRoleEmUso() throws Exception {
        doThrow(new RuntimeException("Role em uso"))
            .when(adminRoleService).removerRole("1");

        mockMvc.perform(delete("/admin/roles/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/roles"))
            .andExpect(flash().attributeExists("error"));
    }
}
```

## Checklist de Validação
- [ ] Classe `AdminRoleController` criada
- [ ] Endpoint `GET /admin/roles` implementado com paginação
- [ ] Endpoint `GET /admin/roles/novo` implementado
- [ ] Endpoint `POST /admin/roles` implementado
- [ ] Endpoint `GET /admin/roles/{id}/editar` implementado
- [ ] Endpoint `PUT /admin/roles/{id}` implementado
- [ ] Endpoint `DELETE /admin/roles/{id}` implementado
- [ ] Endpoint `GET /admin/roles/{id}` (JSON) implementado
- [ ] Validação de formulário configurada
- [ ] Flash attributes para mensagens implementados
- [ ] Tratamento de exceções implementado
- [ ] Testes de integração criados
- [ ] Todos os testes passam

## Anotações
- Usar `@Valid` para validação automática de formulários
- Flash attributes para mensagens que persistem após redirect
- Tratamento de exceções deve ser feito no controller para retornar mensagens amigáveis
- Logs devem ser adicionados em todos os endpoints
- CSRF deve ser habilitado para segurança

## Dependências
- Epic 14 Story 02 - DTOs (RoleForm, RoleListResponse, RoleDetailResponse)
- Epic 14 Story 03 - Service Layer (AdminRoleService)
- Epic 14 Story 01 - Template HTML (list.html, form.html)

## Prioridade
**Alta** - Controller necessário para integração frontend/backend

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas

## Status do Epic 14 - Story 04

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

## Resumo da Implementação

O Controller `AdminRoleController` foi implementado com todos os endpoints necessários:

**Endpoints Implementados:**
- `GET /admin/roles` - Listagem de roles com paginação e filtros
- `GET /admin/roles/novo` - Formulário para nova role
- `GET /admin/roles/{id}/editar` - Formulário para edição
- `POST /admin/roles` - Criação de nova role
- `POST /admin/roles/{id}` - Atualização de role existente
- `POST /admin/roles/{id}/delete` - Remoção de role
- `POST /admin/roles/{id}/ativar` - Ativação de role
- `POST /admin/roles/{id}/inativar` - Inativação de role
- `POST /admin/roles/bulk/remover` - Remoção em lote
- `POST /admin/roles/bulk/ativar` - Ativação em lote
- `GET /api/v1/admin/roles/{id}` - Buscar role (JSON)
- `GET /api/v1/admin/roles/validar-nome` - Validar nome único

**Funcionalidades:**
- Integração com AdminRoleService
- Validação de formulários com @Valid
- Flash attributes para mensagens de sucesso/erro
- Tratamento de exceções
- Logs de debug em todos os métodos
- Validações de negócio delegadas ao service

**Status do Epic 14 - Story 04**

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

