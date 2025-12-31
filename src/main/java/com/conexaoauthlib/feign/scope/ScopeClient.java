package com.conexaoauthlib.feign.scope;

import com.conexaoauthlib.dto.scope.ScopeCreateRequestDTO;
import com.conexaoauthlib.dto.scope.ScopeFilterDTO;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface Feign Client para operações de Scope.
 *
 * <p>Fornece métodos declarativos para CRUD de scopes,
 * incluindo criação, listagem, consulta, atualização e remoção.</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private ScopeClient scopeClient;
 *
 * // Criar scope
 * ScopeResponseDTO scope = scopeClient.create(
 *     ScopeCreateRequestDTO.builder()
 *         .code("users:read")
 *         .name("Read Users")
 *         .description("Permite visualizar dados de usuários")
 *         .resource("users")
 *         .action("read")
 *         .build()
 * );
 *
 * // Listar todos os scopes
 * List<ScopeResponseDTO> scopes = scopeClient.list(null);
 *
 * // Listar scopes com filtro
 * List<ScopeResponseDTO> filtered = scopeClient.list(
 *     ScopeFilterDTO.builder()
 *         .resource("users")
 *         .build()
 * );
 *
 * // Buscar por ID
 * ScopeResponseDTO found = scopeClient.getById("scope-id");
 *
 * // Atualizar scope
 * scopeClient.update(
 *     "scope-id",
 *     ScopeCreateRequestDTO.builder()
 *         .code("users:read")
 *         .name("Read Users (Updated)")
 *         .description("Permissão atualizada para ler usuários")
 *         .resource("users")
 *         .action("read")
 *         .build()
 * );
 *
 * // Deletar scope
 * scopeClient.delete("scope-id");
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "scope",
    url = "${conexao-auth.clients.scope.base-url}",
    configuration = {ScopeClientConfiguration.class}
)
public interface ScopeClient {

    /**
     * Cria um novo scope.
     *
     * @param request DTO com dados do scope
     * @param xTenantId Header de contexto (opcional, para operações cross-tenant)
     * @return ScopeResponseDTO com scope criado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/scopes",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "scope", fallbackMethod = "createFallback")
    @Retry(name = "scope")
    ScopeResponseDTO create(
        @RequestBody ScopeCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Lista todos os scopes disponíveis.
     *
     * @param filter Parâmetros de filtro (opcional, pode ser null)
     * @param xTenantId Header de contexto (opcional)
     * @return List com todos os scopes
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/scopes")
    @CircuitBreaker(name = "scope", fallbackMethod = "listFallback")
    @Retry(name = "scope")
    List<ScopeResponseDTO> list(
        @ModelAttribute ScopeFilterDTO filter,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca scope pelo ID.
     *
     * @param id ID do scope
     * @param xTenantId Header de contexto (opcional)
     * @return ScopeResponseDTO com dados do scope
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/scopes/{id}")
    @CircuitBreaker(name = "scope", fallbackMethod = "getFallback")
    @Retry(name = "scope")
    ScopeResponseDTO getById(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza um scope existente.
     *
     * @param id ID do scope
     * @param request DTO com dados atualizados
     * @param xTenantId Header de contexto (opcional)
     * @return ScopeResponseDTO com scope atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PutMapping(
        value = "/api/scopes/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "scope", fallbackMethod = "updateFallback")
    @Retry(name = "scope")
    ScopeResponseDTO update(
        @PathVariable("id") String id,
        @RequestBody ScopeCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Remove um scope.
     *
     * @param id ID do scope
     * @param xTenantId Header de contexto (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/scopes/{id}")
    @CircuitBreaker(name = "scope", fallbackMethod = "deleteFallback")
    @Retry(name = "scope")
    void delete(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // Fallback methods

    default ScopeResponseDTO createFallback(ScopeCreateRequestDTO request,
                                             String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Scope service unavailable - unable to create scope", t);
    }

    default List<ScopeResponseDTO> listFallback(ScopeFilterDTO filter,
                                                 String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Scope service unavailable - unable to list scopes", t);
    }

    default ScopeResponseDTO getFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Scope service unavailable - unable to get scope", t);
    }

    default ScopeResponseDTO updateFallback(String id, ScopeCreateRequestDTO request,
                                             String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Scope service unavailable - unable to update scope", t);
    }

    default void deleteFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Scope service unavailable - unable to delete scope", t);
    }
}
