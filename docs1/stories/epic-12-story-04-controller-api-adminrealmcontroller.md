# User Story: Controller API (AdminRealmController)

**Epic:** 12 - Página de Gestão de Realms (Thymeleaf)
**Story ID:** epic-12-story-04

## Descrição
Criar o controller Spring MVC (`AdminRealmController`) que expõe endpoints HTTP para a interface web de gestão de realms. O controller deve lidar com renderização de templates Thymeleaf, bind de formulários, validações e redirect attributes.

## Critérios de Aceite
- [X] Controller `AdminRealmController` criado
- [X] Endpoint `GET /admin/realms` implementado (listagem)
- [X] Endpoint `GET /admin/realms/novo` implementado (form criação)
- [X] Endpoint `GET /admin/realms/{id}/edit` implementado (form edição)
- [X] Endpoint `POST /admin/realms` implementado (criação)
- [X] Endpoint `PUT /admin/realms/{id}` implementado (atualização)
- [X] Endpoint `DELETE /admin/realms/{id}/desativar` implementado
- [X] Endpoint `PUT /admin/realms/{id}/ativar` implementado
- [X] `@ModelAttribute` binding configurado para RealmForm
- [X] Validação com `@Valid` implementada
- [X] Redirect attributes para mensagens de sucesso/erro
- [X] Tratamento de exceções apropriado

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.controller`
2. Criar classe `AdminRealmController`
3. Implementar método GET para listagem com paginação
4. Implementar método GET para formulário de criação
5. Implementar método GET para formulário de edição
6. Implementar método POST para criação de realm
7. Implementar método PUT para atualização de realm
8. Implementar método DELETE para desativação
9. Implementar método PUT para ativação
10. Configurar tratamento de exceções global
11. Adicionar messages de sucesso/erro no template

## Instruções de Implementação

### Controller: AdminRealmController
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminRealmController.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.requests.RealmForm;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RealmListResponse;
import br.com.plataforma.conexaodigital.admin.domain.exceptions.RealmMasterNaoPodeSerEditadoException;
import br.com.plataforma.conexaodigital.admin.domain.service.AdminRealmService;
import br.com.plataforma.conexaodigital.gestaorealm.domain.exceptions.NomeRealmJaExisteException;
import br.com.plataforma.conexaodigital.gestaorealm.domain.exceptions.RealmNotFoundException;
import br.com.plataforma.conexaodigital.gestaorealm.domain.model.Realm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller para página administrativa de gestão de realms.
 * Renderiza templates Thymeleaf e processa formulários.
 */
@Controller
@RequestMapping("/admin/realms")
@RequiredArgsConstructor
@Slf4j
public class AdminRealmController {

    private final AdminRealmService adminRealmService;

    /**
     * Exibe lista de realms com paginação e filtros.
     *
     * @param page número da página (default 0)
     * @param size tamanho da página (default 10)
     * @param nome filtro por nome (opcional)
     * @param status filtro por status (opcional: Ativo/Inativo)
     * @param model model para passar dados ao template
     * @return nome do template da lista
     */
    @GetMapping
    public String listarRealms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status,
            Model model
    ) {
        log.info("Exibindo lista de realms: page={}, size={}, nome={}, status={}",
                 page, size, nome, status);

        // Buscar realms paginados
        Page<RealmListResponse> realmsPage = adminRealmService.listarRealms(page, size, nome, status);

        // Adicionar dados ao model
        model.addAttribute("realms", realmsPage.getContent());
        model.addAttribute("page", realmsPage.getNumber());
        model.addAttribute("size", realmsPage.getSize());
        model.addAttribute("totalPages", realmsPage.getTotalPages());
        model.addAttribute("totalElements", realmsPage.getTotalElements());
        model.addAttribute("hasPrevious", realmsPage.hasPrevious());
        model.addAttribute("hasNext", realmsPage.hasNext());
        model.addAttribute("previousPage", realmsPage.hasPrevious() ? realmsPage.getNumber() - 1 : 0);
        model.addAttribute("nextPage", realmsPage.hasNext() ? realmsPage.getNumber() + 1 : realmsPage.getNumber());

        // Manter filtros para paginação
        model.addAttribute("filterNome", nome);
        model.addAttribute("filterStatus", status);

        return "admin/realms/list";
    }

    /**
     * Exibe formulário para criar novo realm.
     *
     * @param model model para passar dados ao template
     * @return nome do template do formulário
     */
    @GetMapping("/novo")
    public String novoRealm(Model model) {
        log.info("Exibindo formulário de novo realm");

        model.addAttribute("realmForm", new RealmForm());
        model.addAttribute("editMode", false);
        model.addAttribute("pageTitle", "Novo Realm");

        return "admin/realms/form";
    }

    /**
     * Exibe formulário para editar realm existente.
     *
     * @param id UUID do realm
     * @param model model para passar dados ao template
     * @return nome do template do formulário
     */
    @GetMapping("/{id}/edit")
    public String editarRealm(
            @PathVariable String id,
            Model model
    ) {
        log.info("Exibindo formulário de edição do realm: {}", id);

        try {
            // Buscar realm detalhado
            RealmDetailResponse realm = adminRealmService.buscarPorId(id);

            // Criar form preenchido com dados atuais
            RealmForm form = new RealmForm(
                realm.id(),
                realm.nome(),
                realm.descricao(),
                realm.ativo(),
                realm.empresaId()
            );

            // Verificar se é Realm Master
            boolean isMaster = realm.master();

            model.addAttribute("realmForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("pageTitle", "Editar Realm");
            model.addAttribute("isMaster", isMaster);

            return "admin/realms/form";

        } catch (RealmNotFoundException e) {
            model.addAttribute("error", "Realm não encontrado: " + id);
            return "error/404";
        }
    }

    /**
     * Processa submissão do formulário para criar novo realm.
     *
     * @param form dados do formulário
     * @param result resultado da validação
     * @param redirectAttributes atributos para redirect
     * @return redirect para lista ou formulário com erros
     */
    @PostMapping
    public String criarRealm(
            @Valid @ModelAttribute RealmForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Processando criação de realm: {}", form.nome());

        if (result.hasErrors()) {
            log.warn("Erros de validação ao criar realm: {}", result.getAllErrors());
            return "admin/realms/form";
        }

        try {
            Realm realm = adminRealmService.criarRealm(form);
            redirectAttributes.addFlashAttribute("success",
                "Realm \"" + form.nome() + "\" criado com sucesso!");
            return "redirect:/admin/realms";

        } catch (NomeRealmJaExisteException e) {
            result.rejectValue("nome", null, e.getMessage());
            return "admin/realms/form";

        } catch (Exception e) {
            log.error("Erro ao criar realm", e);
            result.reject(null, "Erro ao criar realm: " + e.getMessage());
            return "admin/realms/form";
        }
    }

    /**
     * Processa submissão do formulário para atualizar realm existente.
     *
     * @param id UUID do realm
     * @param form dados do formulário
     * @param result resultado da validação
     * @param redirectAttributes atributos para redirect
     * @return redirect para lista ou formulário com erros
     */
    @PutMapping("/{id}")
    public String atualizarRealm(
            @PathVariable String id,
            @Valid @ModelAttribute RealmForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Processando atualização do realm: {}", id);

        if (result.hasErrors()) {
            log.warn("Erros de validação ao atualizar realm: {}", result.getAllErrors());
            return "admin/realms/form";
        }

        try {
            adminRealmService.atualizarRealm(id, form);
            redirectAttributes.addFlashAttribute("success",
                "Realm \"" + form.nome() + "\" atualizado com sucesso!");
            return "redirect:/admin/realms";

        } catch (RealmNotFoundException e) {
            result.reject(null, e.getMessage());
            return "admin/realms/form";

        } catch (NomeRealmJaExisteException e) {
            result.rejectValue("nome", null, e.getMessage());
            return "admin/realms/form";

        } catch (RealmMasterNaoPodeSerEditadoException e) {
            result.reject(null, e.getMessage());
            return "admin/realms/form";

        } catch (Exception e) {
            log.error("Erro ao atualizar realm", e);
            result.reject(null, "Erro ao atualizar realm: " + e.getMessage());
            return "admin/realms/form";
        }
    }

    /**
     * Desativa um realm (soft delete).
     *
     * @param id UUID do realm
     * @param redirectAttributes atributos para redirect
     * @return redirect para lista
     */
    @DeleteMapping("/{id}/desativar")
    public String desativarRealm(
            @PathVariable String id,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Processando desativação do realm: {}", id);

        try {
            Realm realm = adminRealmService.desativarRealm(id);
            redirectAttributes.addFlashAttribute("success",
                "Realm \"" + realm.getNome() + "\" desativado com sucesso!");
            return "redirect:/admin/realms";

        } catch (RealmNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/realms";

        } catch (RealmMasterNaoPodeSerEditadoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/realms";

        } catch (Exception e) {
            log.error("Erro ao desativar realm", e);
            redirectAttributes.addFlashAttribute("error",
                "Erro ao desativar realm: " + e.getMessage());
            return "redirect:/admin/realms";
        }
    }

    /**
     * Ativa um realm inativo.
     *
     * @param id UUID do realm
     * @param redirectAttributes atributos para redirect
     * @return redirect para lista
     */
    @PutMapping("/{id}/ativar")
    public String ativarRealm(
            @PathVariable String id,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Processando ativação do realm: {}", id);

        try {
            Realm realm = adminRealmService.ativarRealm(id);
            redirectAttributes.addFlashAttribute("success",
                "Realm \"" + realm.getNome() + "\" ativado com sucesso!");
            return "redirect:/admin/realms";

        } catch (RealmNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/realms";

        } catch (Exception e) {
            log.error("Erro ao ativar realm", e);
            redirectAttributes.addFlashAttribute("error",
                "Erro ao ativar realm: " + e.getMessage());
            return "redirect:/admin/realms";
        }
    }

    /**
     * Exibe detalhes completos de um realm.
     *
     * @param id UUID do realm
     * @param model model para passar dados ao template
     * @return nome do template de detalhes
     */
    @GetMapping("/{id}")
    public String verRealm(
            @PathVariable String id,
            Model model
    ) {
        log.info("Exibindo detalhes do realm: {}", id);

        try {
            RealmDetailResponse realm = adminRealmService.buscarPorId(id);
            model.addAttribute("realm", realm);
            return "admin/realms/detail";

        } catch (RealmNotFoundException e) {
            model.addAttribute("error", "Realm não encontrado: " + id);
            return "error/404";
        }
    }

    /**
     * Tratamento global de exceções para este controller.
     *
     * @param exception exceção lançada
     * @param redirectAttributes atributos para redirect
     * @return redirect para lista com mensagem de erro
     */
    @ExceptionHandler({NomeRealmJaExisteException.class,
                     RealmNotFoundException.class,
                     RealmMasterNaoPodeSerEditadoException.class})
    public String handleRealmExceptions(
            Exception exception,
            RedirectAttributes redirectAttributes
    ) {
        log.error("Erro no controller de realms: {}", exception.getMessage());
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/admin/realms";
    }
}
```

### Global Exception Handler (opcional)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/GlobalExceptionHandler.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler global de exceções para controllers administrativos.
 */
@ControllerAdvice(basePackages = "br.com.plataforma.conexaodigital.admin.api.controller")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Erro não tratado", ex);
        redirectAttributes.addFlashAttribute("error",
            "Ocorreu um erro inesperado: " + ex.getMessage());
        return "redirect:/admin/realms";
    }
}
```

### Template Update: Success/Error Messages
**Adicionar ao template `admin/realms/list.html` antes do card:**

```html
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
```

## Checklist de Validação
- [ ] Package `admin/api/controller` criado
- [ ] Classe `AdminRealmController` criada
- [ ] `@RequestMapping("/admin/realms")` configurado
- [ ] Método `GET /` (listarRealms) implementado
- [ ] Método `GET /novo` (novoRealm) implementado
- [ ] Método `GET /{id}/edit` (editarRealm) implementado
- [ ] Método `POST /` (criarRealm) implementado
- [ ] Método `PUT /{id}` (atualizarRealm) implementado
- [ ] Método `DELETE /{id}/desativar` implementado
- [ ] Método `PUT /{id}/ativar` implementado
- [ ] Método `GET /{id}` (verRealm) implementado
- [ ] `@ModelAttribute` configurado para RealmForm
- [ ] `@Valid` annotations aplicadas
- [ ] RedirectAttributes configurados
- [ ] Tratamento de exceções implementado
- [ ] Logs em todos os métodos
- [ ] Testes de integração criados

## Anotações
- Controller deve delegar toda lógica de negócio para Service layer
- Validações devem ser feitas com Jakarta Bean Validation (`@Valid`)
- Mensagens de sucesso/erro devem usar FlashAttributes para persistirem no redirect
- URLs seguem padrão RESTful com prefixo `/admin/realms`
- Páginação e filtros devem ser mantidos nos redirects
- Tratamento de exceções deve capturar erros específicos primeiro, genéricos depois
- Realm Master não pode ser desativado (exceção específica)

## Dependências
- Story 02 (DTOs de Realm) - RealmForm, RealmListResponse
- Story 03 (Backend Service Layer) - AdminRealmService
- Epic 1 (Gestão de Realms) - exceções de domínio

## Prioridade
**Alta** - Controller necessário para interface web funcionar

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas
