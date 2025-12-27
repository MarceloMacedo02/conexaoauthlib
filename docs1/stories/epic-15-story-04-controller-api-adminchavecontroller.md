# User Story: Controller API (AdminChaveController)

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-04

## Descrição
Criar o `AdminChaveController` com endpoints MVC para a página administrativa de gestão de chaves, incluindo endpoints de listagem, histórico, rotação manual e consulta de próxima rotação automática.

## Critérios de Aceite
- [ ] Classe `AdminChaveController` criada com `@Controller`
- [ ] Endpoint `GET /admin/chaves` implementado (página principal)
- [ ] Endpoint `GET /api/v1/admin/chaves` implementado (listagem de chaves)
- [ ] Endpoint `GET /api/v1/admin/chaves/historico` implementado (histórico)
- [ ] Endpoint `POST /api/v1/admin/chaves/rotacionar` implementado (rotação manual)
- [ ] Endpoint `GET /api/v1/admin/chaves/proxima-rotacao` implementado (próxima rotação)
- [ ] Model binding configurado corretamente
- [ ] Validação de requisições funcionando
- [ ] Tratamento de exceções implementado
- [ ] Respostas JSON formatadas corretamente

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.controller` (se não existir)
2. Criar classe `AdminChaveController.java`
3. Implementar endpoint de página (GET /admin/chaves)
4. Implementar endpoint de listagem (GET /api/v1/admin/chaves)
5. Implementar endpoint de histórico (GET /api/v1/admin/chaves/historico)
6. Implementar endpoint de rotação (POST /api/v1/admin/chaves/rotacionar)
7. Implementar endpoint de próxima rotação (GET /api/v1/admin/chaves/proxima-rotacao)
8. Configurar validações
9. Implementar tratamento de exceções

## Instruções de Implementação

### Controller Class
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminChaveController.java`

```java
package br.com.plataforma.conexaodigital.admin.api.controller;

import br.com.plataforma.conexaodigital.admin.api.requests.RotacaoRequest;
import br.com.plataforma.conexaodigital.admin.api.responses.ChaveAtivaResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.RotacaoHistoricoResponse;
import br.com.plataforma.conexaodigital.admin.domain.service.AdminChaveService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller MVC para página administrativa de gestão de chaves.
 */
@Controller
@RequestMapping("/admin/chaves")
public class AdminChaveController {

    private final AdminChaveService chaveService;

    public AdminChaveController(AdminChaveService chaveService) {
        this.chaveService = chaveService;
    }

    /**
     * Página principal de gestão de chaves.
     *
     * @return template de listagem
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listarChaves() {
        return "admin/chaves/list";
    }

    /**
     * API endpoint para listar chaves ativas.
     *
     * @param realmId ID do realm (opcional)
     * @return lista de chaves ativas
     */
    @GetMapping("/api/ativas")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChaveAtivaResponse>> listarChavesAtivas(
        @RequestParam(required = false) String realmId
    ) {
        List<ChaveAtivaResponse> chaves = chaveService.listarChavesAtivas(realmId);
        return ResponseEntity.ok(chaves);
    }

    /**
     * API endpoint para listar histórico de rotações.
     *
     * @param realmId ID do realm (opcional)
     * @return lista de histórico de rotações
     */
    @GetMapping("/api/historico")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RotacaoHistoricoResponse>> listarHistoricoRotacoes(
        @RequestParam(required = false) String realmId
    ) {
        List<RotacaoHistoricoResponse> historico = chaveService.listarHistoricoRotacoes(realmId);
        return ResponseEntity.ok(historico);
    }

    /**
     * API endpoint para executar rotação manual de chave.
     *
     * @param request solicitação de rotação
     * @param authentication autenticação do usuário atual
     * @return chave ativa após rotação
     */
    @PostMapping("/api/rotacionar")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rotacionarChaveManual(
        @Valid @RequestBody RotacaoRequest request,
        Authentication authentication
    ) {
        try {
            // Obter ID do usuário atual
            UUID usuarioId = UUID.fromString(authentication.getName());

            // Executar rotação
            ChaveAtivaResponse novaChave = chaveService.rotacionarChaveManual(
                request, usuarioId
            );

            // Retornar sucesso
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Rotação de chave iniciada com sucesso!",
                "chave", novaChave
            ));

        } catch (IllegalArgumentException e) {
            // Retornar erro de validação
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            // Retornar erro interno
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Erro ao processar rotação de chave"
            ));
        }
    }

    /**
     * API endpoint para obter próxima rotação automática.
     *
     * @return informações da próxima rotação
     */
    @GetMapping("/api/proxima-rotacao")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obterProximaRotacaoAutomatica() {
        LocalDateTime proximaRotacao = chaveService.obterProximaRotacaoAutomatica();
        int diasRestantes = chaveService.calcularDiasParaProximaRotacao();

        // Formatar data para exibição
        String dataFormatada = proximaRotacao.format(
            java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")
        );

        return ResponseEntity.ok(Map.of(
            "proximaRotacao", dataFormatada,
            "diasRestantes", diasRestantes,
            "data", proximaRotacao
        ));
    }

    /**
     * API endpoint para buscar chave de um realm específico.
     *
     * @param realmId ID do realm
     * @return chave ativa do realm
     */
    @GetMapping("/api/realm/{realmId}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarChavePorRealm(
        @PathVariable String realmId
    ) {
        try {
            ChaveAtivaResponse chave = chaveService.buscarChavePorRealm(
                UUID.fromString(realmId)
            );
            return ResponseEntity.ok(chave);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * API endpoint para obter resumo de chaves.
     *
     * @return resumo estatístico
     */
    @GetMapping("/api/resumo")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obterResumoChaves() {
        List<ChaveAtivaResponse> todasChaves = chaveService.listarChavesAtivas(null);

        long totalChaves = todasChaves.size();
        long chavesAtivas = todasChaves.stream()
            .filter(c -> c.status() == ChaveAtivaResponse.StatusChave.ATIVA)
            .count();
        long chavesExpirando = todasChaves.stream()
            .filter(c -> c.status() == ChaveAtivaResponse.StatusChave.EXPIRANDO)
            .count();
        long chavesExpiradas = todasChaves.stream()
            .filter(c -> c.status() == ChaveAtivaResponse.StatusChave.EXPIRADA)
            .count();

        return ResponseEntity.ok(Map.of(
            "totalChaves", totalChaves,
            "chavesAtivas", chavesAtivas,
            "chavesExpirando", chavesExpirando,
            "chavesExpiradas", chavesExpiradas
        ));
    }
}
```

### Thymeleaf Binding (para Story 01)
**Adicionar ao template `admin/chaves/list.html`:**

```html
<!-- JavaScript para carregar dados -->
<script th:inline="javascript">
function carregarChaves() {
    const realmId = document.getElementById('realmFilter').value;
    fetch('/admin/chaves/api/ativas?realmId=' + realmId)
        .then(response => response.json())
        .then(data => renderizarChaves(data))
        .catch(error => console.error('Erro ao carregar chaves:', error));
}

function carregarHistorico() {
    const realmId = document.getElementById('realmFilter').value;
    fetch('/admin/chaves/api/historico?realmId=' + realmId)
        .then(response => response.json())
        .then(data => renderizarHistorico(data))
        .catch(error => console.error('Erro ao carregar histórico:', error));
}

function carregarProximaRotacao() {
    fetch('/admin/chaves/api/proxima-rotacao')
        .then(response => response.json())
        .then(data => {
            document.getElementById('proximaRotacao').textContent = data.proximaRotacao;
            document.getElementById('countdownRotacao').textContent = data.diasRestantes + ' dias';
        })
        .catch(error => console.error('Erro ao carregar próxima rotação:', error));
}

function rotacionarChaveManual(realmId) {
    const motivo = prompt('Informe o motivo da rotação (opcional):');

    fetch('/admin/chaves/api/rotacionar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            realmId: realmId,
            motivo: motivo
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarSucesso(data.message);
            carregarChaves();
            carregarHistorico();
        } else {
            mostrarErro(data.message);
        }
    })
    .catch(error => mostrarErro('Erro ao rotacionar chave'));
}

// Carregar dados ao inicializar
document.addEventListener('DOMContentLoaded', function() {
    carregarChaves();
    carregarHistorico();
    carregarProximaRotacao();
});
</script>
```

## Checklist de Validação
- [ ] Classe `AdminChaveController` criada com `@Controller`
- [ ] Mapeamento `@RequestMapping("/admin/chaves")` configurado
- [ ] Endpoint `GET /admin/chaves` implementado
- [ ] Endpoint `GET /admin/chaves/api/ativas` implementado
- [ ] Endpoint `GET /admin/chaves/api/historico` implementado
- [ ] Endpoint `POST /admin/chaves/api/rotacionar` implementado
- [ ] Endpoint `GET /admin/chaves/api/proxima-rotacao` implementado
- [ ] Endpoint `GET /admin/chaves/api/realm/{realmId}` implementado
- [ ] Endpoint `GET /admin/chaves/api/resumo` implementado
- [ ] Anotação `@Valid` aplicada em request body
- [ ] `@PreAuthorize` configurado para segurança (ADMIN)
- [ ] Tratamento de exceções com try/catch implementado
- [ ] Respostas JSON formatadas corretamente
- [ ] Model binding configurado
- [ ] Parâmetros opcionais (`@RequestParam(required = false)`)
- [ ] Path variables configuradas (`@PathVariable`)

## Anotações
- Todos os endpoints protegidos com `@PreAuthorize("hasRole('ADMIN')")`
- Endpoints de API retornam JSON (`@ResponseBody`)
- Rotação manual gera nova chave e registra evento de auditoria
- Próxima rotação sempre dia 1 do próximo mês às 00:00
- Endpoint de resumo fornece estatísticas rápidas
- Tratamento de exceções retorna mensagens amigáveis
- Parâmetros de realmId são opcionais (permite filtrar ou listar todos)
- Data formatada em português para exibição

## Dependências
- Story 01 (Template com Tabs) - template já existe
- Story 02 (DTOs de Chave) - DTOs necessários
- Story 03 (Backend Service Layer) - Service layer implementado
- Epic 5 (Gestão de Chaves) - lógica de negócio já existe
- Epic 9 (Configuração) - Spring Security configurado

## Prioridade
**Alta** - Controller necessário para funcionar a página

## Estimativa
- Implementação: 3 horas
- Testes: 2 horas
- Total: 5 horas
