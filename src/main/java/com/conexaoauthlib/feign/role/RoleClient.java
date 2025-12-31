package com.conexaoauthlib.feign.role;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.role.*;
import com.conexaoauthlib.dto.scope.ScopeAssignRequestDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface Feign Client para operações de Role.
 *
 * <p>Fornece métodos declarativos para CRUD completo de roles,
 * incluindo criação, listagem paginada, consulta individual,
 * atualização de status, gerenciamento de scopes e remoção.</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private RoleClient roleClient;
 *
 * // Criar role
 * RoleResponseDTO role = roleClient.create(
 *     RoleCreateRequestDTO.builder()
 *         .name("ADMIN")
 *         .description("Administrador do sistema")
 *         .build()
 * );
 *
 * // Listar roles com paginação
 * PageResponseDTO<RoleResponseDTO> page = roleClient.list(
 *     RoleFilterDTO.builder()
 *         .status("ACTIVE")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Buscar por ID
 * RoleResponseDTO found = roleClient.getById("role-id");
 *
 * // Atualizar status
 * roleClient.updateStatus(
 *     "role-id",
 *     RoleStatusDTO.builder().status("INACTIVE").reason("Deprecated").build()
 * );
 *
 * // Atribuir scopes a role
 * roleClient.assignScopes(
 *     "role-id",
 *     ScopeAssignRequestDTO.builder()
 *         .scopeIds(List.of("scope-read", "scope-write"))
 *         .build()
 * );
 *
 * // Remover scopes de role
 * roleClient.removeScopes(
 *     "role-id",
 *     List.of("scope-read")
 * );
 *
 * // Deletar role
 * roleClient.delete("role-id");
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "role",
    url = "${conexao-auth.clients.role.base-url}",
    configuration = {RoleClientConfiguration.class}
)
public interface RoleClient {

    /**
     * Cria uma nova role.
     *
     * @param request DTO com dados da role
     * @param xTenantId Header de contexto (opcional, para operações cross-tenant)
     * @return RoleResponseDTO com role criada
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/roles",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "role", fallbackMethod = "createFallback")
    @Retry(name = "role")
    RoleResponseDTO create(
        @RequestBody RoleCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Lista roles com paginação e filtros.
     *
     * @param filter Parâmetros de filtro e paginação
     * @param xTenantId Header de contexto (opcional)
     * @return PageResponseDTO com lista de roles
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/roles")
    @CircuitBreaker(name = "role", fallbackMethod = "listFallback")
    @Retry(name = "role")
    PageResponseDTO<RoleResponseDTO> list(
        @ModelAttribute RoleFilterDTO filter,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca role pelo ID.
     *
     * @param id ID da role
     * @param xTenantId Header de contexto (opcional)
     * @return RoleResponseDTO com dados da role
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/roles/{id}")
    @CircuitBreaker(name = "role", fallbackMethod = "getFallback")
    @Retry(name = "role")
    RoleResponseDTO getById(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza status da role.
     *
     * @param id ID da role
     * @param request DTO com novo status
     * @param xTenantId Header de contexto (opcional)
     * @return RoleResponseDTO com role atualizada
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PatchMapping(
        value = "/api/roles/{id}/status",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "role", fallbackMethod = "updateStatusFallback")
    @Retry(name = "role")
    RoleResponseDTO updateStatus(
        @PathVariable("id") String id,
        @RequestBody RoleStatusDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atribui scopes a uma role.
     *
     * @param id ID da role
     * @param request DTO com IDs de scopes a atribuir
     * @param xTenantId Header de contexto (opcional)
     * @return RoleResponseDTO com role atualizada
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/roles/{id}/scopes",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "role", fallbackMethod = "assignScopesFallback")
    @Retry(name = "role")
    RoleResponseDTO assignScopes(
        @PathVariable("id") String id,
        @RequestBody ScopeAssignRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Remove scopes de uma role.
     *
     * @param id ID da role
     * @param scopeIds IDs dos scopes a remover
     * @param xTenantId Header de contexto (opcional)
     * @return RoleResponseDTO com role atualizada
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/roles/{id}/scopes")
    @CircuitBreaker(name = "role", fallbackMethod = "removeScopesFallback")
    @Retry(name = "role")
    RoleResponseDTO removeScopes(
        @PathVariable("id") String id,
        @RequestParam("scope_ids") List<String> scopeIds,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Remove (desativa) uma role.
     *
     * @param id ID da role
     * @param xTenantId Header de contexto (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/roles/{id}")
    @CircuitBreaker(name = "role", fallbackMethod = "deleteFallback")
    @Retry(name = "role")
    void delete(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // Fallback methods

    default RoleResponseDTO createFallback(RoleCreateRequestDTO request,
                                            String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to create role", t);
    }

    default PageResponseDTO<RoleResponseDTO> listFallback(RoleFilterDTO filter,
                                                           String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to list roles", t);
    }

    default RoleResponseDTO getFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to get role", t);
    }

    default RoleResponseDTO updateStatusFallback(String id, RoleStatusDTO request,
                                                  String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to update status", t);
    }

    default RoleResponseDTO assignScopesFallback(String id, ScopeAssignRequestDTO request,
                                                  String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to assign scopes", t);
    }

    default RoleResponseDTO removeScopesFallback(String id, List<String> scopeIds,
                                                  String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to remove scopes", t);
    }

    default void deleteFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Role service unavailable - unable to delete role", t);
    }
}
