# User Story: Auditoria - Controller API (AdminAuditoriaController)

**Epic:** 16 - Página de Visualização de Auditoria (Thymeleaf)
**Story ID:** epic-16-story-04

## Status
**Estado:** 🔲 Planejado
**Prioridade:** Alta

## Descrição

Criar o controller `AdminAuditoriaController` para expor endpoints REST de consulta de eventos de auditoria, com filtros e paginação.

## Critérios de Aceitação

- [ ] Controller `AdminAuditoriaController` criado
- [ ] Endpoint GET `/admin/auditoria` para listagem com filtros
- [ ] Endpoint GET `/admin/auditoria/{id}` para detalhes
- [ ] Endpoint GET `/admin/auditoria/exportar` para CSV
- [ ] Injeção de dependências funcionando
- [ ] Tratamento de erros apropriado

## Tarefas

1. Criar controller `AdminAuditoriaController` em `src/main/java/.../admin/api/controller/`
2. Implementar endpoint GET `/admin/auditoria` com parâmetros:
   - page (int)
   - size (int)
   - realmId (String, opcional)
   - tipoEvento (String, opcional)
   - severidade (String, opcional)
   - usuario (String, opcional)
   - dataInicio (String, opcional)
   - dataFim (String, opcional)
3. Implementar endpoint GET `/admin/auditoria/{id}` para detalhes
4. Implementar endpoint GET `/admin/auditoria/exportar` para CSV
5. Adicionar tratamento de exceções

## Instruções de Implementação

### 1. Criar Controller

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.dto.requests.AuditoriaFilterRequest;
import br.com.plataforma.conexaodigital.admin.api.dto.responses.AuditoriaListResponse;
import br.com.plataforma.conexaodigital.admin.api.dto.responses.EventoAuditoriaDetailDTO;
import br.com.plataforma.conexaodigital.admin.domain.service.AdminAuditoriaService;
import br.com.plataforma.conexaodigital.admin.exceptions.AuditoriaException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller para página de auditoria.
 */
@Controller
@RequestMapping("/admin/auditoria")
@RequiredArgsConstructor
@Slf4j
public class AdminAuditoriaController {

    private final AdminAuditoriaService adminAuditoriaService;

    /**
     * Listar eventos de auditoria.
     */
    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String realmId,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) String severidade,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            Model model) {

        AuditoriaListResponse response = adminAuditoriaService.listarEventos(
                page, size, realmId, tipoEvento, severidade, usuario,
                dataInicio != null ? LocalDateTime.parse(dataInicio) : null,
                dataFim != null ? LocalDateTime.parse(dataFim) : null
        );

        model.addAttribute("eventos", response);

        return "admin/auditoria/lista";
    }

    /**
     * Buscar detalhes de um evento.
     */
    @GetMapping("/{id}")
    public String buscarDetalhes(@PathVariable Long id, Model model) {
        EventoAuditoriaDetailDTO detalhes = adminAuditoriaService.buscarDetalhesEvento(id);
        model.addAttribute("evento", detalhes);
        return "admin/auditoria/detalhes";
    }

    /**
     * Exportar eventos para CSV.
     */
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCSV(
            @RequestParam(required = false) String realmId,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) String severidade,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {

        String csv = adminAuditoriaService.exportarEventosCSV(
                realmId, tipoEvento, severidade, usuario,
                dataInicio != null ? LocalDateTime.parse(dataInicio) : null,
                dataFim != null ? LocalDateTime.parse(dataFim) : null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8").toString());
        headers.setContentDispositionFormData("attachment", "eventos-auditoria.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
