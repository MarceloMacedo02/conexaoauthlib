package com.conexaoauthlib.feign.tenant;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantCreateRequestDTO;
import com.conexaoauthlib.dto.tenant.TenantFilterDTO;
import com.conexaoauthlib.dto.tenant.TenantProductAddRequestDTO;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantStatusDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface Feign Client para operações de Tenant.
 *
 * <p>Fornece métodos declarativos para CRUD completo de tenants,
 * incluindo criação, listagem paginada, consulta individual,
 * atualização de status e gerenciamento de produtos.</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private TenantClient tenantClient;
 *
 * // Criar tenant
 * TenantResponseDTO tenant = tenantClient.create(
 *     TenantCreateRequestDTO.builder()
 *         .name("Empresa X")
 *         .documentNumber("12345678000100")
 *         .products(List.of("MOD_RH"))
 *         .build()
 * );
 *
 * // Listar tenants com paginação
 * PageResponseDTO<TenantResponseDTO> page = tenantClient.list(
 *     TenantFilterDTO.builder()
 *         .status("ACTIVE")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Buscar por ID
 * TenantResponseDTO found = tenantClient.getById("tenant-id");
 *
 * // Atualizar status
 * tenantClient.updateStatus(
 *     "tenant-id",
 *     TenantStatusDTO.builder().status("SUSPENDED").reason("Payment overdue").build()
 * );
 *
 * // Adicionar produtos
 * tenantClient.addProducts(
 *     "tenant-id",
 *     TenantProductAddRequestDTO.builder()
 *         .productCodes(List.of("MOD_FIN", "MOD_CRM"))
 *         .build()
 * );
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "tenant",
    url = "${conexao-auth.clients.tenant.base-url}",
    configuration = {TenantClientConfiguration.class}
)
public interface TenantClient {

    /**
     * Cria um novo tenant.
     *
     * @param request DTO com dados do tenant
     * @param xTenantId Header de contexto (opcional, para operações cross-tenant)
     * @return TenantResponseDTO com tenant criado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/tenants",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "tenant", fallbackMethod = "createFallback")
    @Retry(name = "tenant")
    TenantResponseDTO create(
        @RequestBody TenantCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Lista tenants com paginação e filtros.
     *
     * @param filter Parâmetros de filtro e paginação
     * @param xTenantId Header de contexto (opcional)
     * @return PageResponseDTO com lista de tenants
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/tenants")
    @CircuitBreaker(name = "tenant", fallbackMethod = "listFallback")
    @Retry(name = "tenant")
    PageResponseDTO<TenantResponseDTO> list(
        @ModelAttribute TenantFilterDTO filter,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca tenant pelo ID.
     *
     * @param id ID do tenant
     * @param xTenantId Header de contexto (opcional)
     * @return TenantResponseDTO com dados do tenant
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/tenants/{id}")
    @CircuitBreaker(name = "tenant", fallbackMethod = "getFallback")
    @Retry(name = "tenant")
    TenantResponseDTO getById(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca tenant pelo documento (CPF/CNPJ).
     *
     * @param document Número do documento (apenas dígitos)
     * @param xTenantId Header de contexto (opcional)
     * @return TenantResponseDTO com dados do tenant
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/tenants/by-document/{document}")
    @CircuitBreaker(name = "tenant", fallbackMethod = "getByDocumentFallback")
    @Retry(name = "tenant")
    TenantResponseDTO getByDocument(
        @PathVariable("document") String document,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza status do tenant.
     *
     * @param id ID do tenant
     * @param request DTO com novo status
     * @param xTenantId Header de contexto (opcional)
     * @return TenantResponseDTO com tenant atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PatchMapping(value = "/api/tenants/{id}/status")
    @CircuitBreaker(name = "tenant", fallbackMethod = "updateStatusFallback")
    @Retry(name = "tenant")
    TenantResponseDTO updateStatus(
        @PathVariable("id") String id,
        @RequestBody TenantStatusDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Adiciona produtos ao tenant.
     *
     * @param id ID do tenant
     * @param request DTO com códigos de produtos
     * @param xTenantId Header de contexto (opcional)
     * @return TenantResponseDTO com tenant atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/tenants/{id}/products",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "tenant", fallbackMethod = "addProductsFallback")
    @Retry(name = "tenant")
    TenantResponseDTO addProducts(
        @PathVariable("id") String id,
        @RequestBody TenantProductAddRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Remove produtos do tenant.
     *
     * @param id ID do tenant
     * @param productCodes Códigos dos produtos a remover
     * @param xTenantId Header de contexto (opcional)
     * @return TenantResponseDTO com tenant atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/tenants/{id}/products")
    @CircuitBreaker(name = "tenant", fallbackMethod = "removeProductsFallback")
    @Retry(name = "tenant")
    TenantResponseDTO removeProducts(
        @PathVariable("id") String id,
        @RequestParam("codes") List<String> productCodes,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // Fallback methods

    default TenantResponseDTO createFallback(TenantCreateRequestDTO request,
                                              String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to create tenant", t);
    }

    default PageResponseDTO<TenantResponseDTO> listFallback(TenantFilterDTO filter,
                                                             String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to list tenants", t);
    }

    default TenantResponseDTO getFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to get tenant", t);
    }

    default TenantResponseDTO getByDocumentFallback(String document, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to get tenant by document", t);
    }

    default TenantResponseDTO updateStatusFallback(String id, TenantStatusDTO request,
                                                    String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to update status", t);
    }

    default TenantResponseDTO addProductsFallback(String id, TenantProductAddRequestDTO request,
                                                    String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to add products", t);
    }

    default TenantResponseDTO removeProductsFallback(String id, List<String> productCodes,
                                                      String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Tenant service unavailable - unable to remove products", t);
    }
}
