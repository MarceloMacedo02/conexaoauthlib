package com.conexaoauthlib.feign.client;

import com.conexaoauthlib.dto.client.ClientCreateRequestDTO;
import com.conexaoauthlib.dto.client.ClientFilterDTO;
import com.conexaoauthlib.dto.client.ClientResponseDTO;
import com.conexaoauthlib.dto.client.ClientSecretResponseDTO;
import com.conexaoauthlib.dto.client.ClientStatusDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface Feign Client para operações de Client OAuth2.
 *
 * <p>Fornece métodos declarativos para CRUD completo de clients M2M,
 * incluindo criação, listagem, consulta, atualização de status,
 * regeneração de segredos e remoção.</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private ClientClient clientClient;
 *
 * // Criar client
 * ClientResponseDTO client = clientClient.create(
 *     ClientCreateRequestDTO.builder()
 *         .clientId("my-service")
 *         .clientSecret("secret123")
 *         .name("My Service")
 *         .tenantId("tenant-123")
 *         .grantTypes(List.of("client_credentials"))
 *         .scopes(List.of("read", "write"))
 *         .build()
 * );
 *
 * // Listar clients com paginação
 * PageResponseDTO<ClientResponseDTO> page = clientClient.list(
 *     ClientFilterDTO.builder()
 *         .status("ACTIVE")
 *         .tenantId("tenant-123")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Buscar por ID
 * ClientResponseDTO found = clientClient.getById("client-id");
 *
 * // Buscar por clientId público
 * ClientResponseDTO byClientId = clientClient.getByClientId("my-service");
 *
 * // Atualizar status
 * clientClient.updateStatus(
 *     "client-id",
 *     ClientStatusDTO.builder().status("SUSPENDED").reason("Security concern").build()
 * );
 *
 * // Regenerar segredo
 * ClientSecretResponseDTO secret = clientClient.regenerateSecret("client-id");
 * String newSecret = secret.getNewSecret();
 *
 * // Deletar client
 * clientClient.delete("client-id");
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "client",
    url = "${conexao-auth.clients.client.base-url}",
    configuration = {ClientClientConfiguration.class}
)
public interface ClientClient {

    /**
     * Cria um novo client OAuth2.
     *
     * @param request DTO com dados do client
     * @param xTenantId Header de contexto (opcional)
     * @return ClientResponseDTO com client criado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/clients",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "client", fallbackMethod = "createFallback")
    @Retry(name = "client")
    ClientResponseDTO create(
        @RequestBody ClientCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Lista clients com paginação e filtros.
     *
     * @param filter Parâmetros de filtro e paginação
     * @param xTenantId Header de contexto (opcional)
     * @return PageResponseDTO com lista de clients
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/clients")
    @CircuitBreaker(name = "client", fallbackMethod = "listFallback")
    @Retry(name = "client")
    PageResponseDTO<ClientResponseDTO> list(
        @ModelAttribute ClientFilterDTO filter,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca client pelo ID interno.
     *
     * @param id ID interno do client
     * @param xTenantId Header de contexto (opcional)
     * @return ClientResponseDTO com dados do client
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/clients/{id}")
    @CircuitBreaker(name = "client", fallbackMethod = "getFallback")
    @Retry(name = "client")
    ClientResponseDTO getById(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca client pelo clientId público.
     *
     * @param clientId Identificador público do client
     * @param xTenantId Header de contexto (opcional)
     * @return ClientResponseDTO com dados do client
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/clients/by-clientId/{clientId}")
    @CircuitBreaker(name = "client", fallbackMethod = "getByClientIdFallback")
    @Retry(name = "client")
    ClientResponseDTO getByClientId(
        @PathVariable("clientId") String clientId,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza status do client.
     *
     * @param id ID do client
     * @param request DTO com novo status
     * @param xTenantId Header de contexto (opcional)
     * @return ClientResponseDTO com client atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PatchMapping(value = "/api/clients/{id}/status")
    @CircuitBreaker(name = "client", fallbackMethod = "updateStatusFallback")
    @Retry(name = "client")
    ClientResponseDTO updateStatus(
        @PathVariable("id") String id,
        @RequestBody ClientStatusDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Regenera o segredo do client.
     *
     * <p>O segredo anterior permanece válido até expirar,
     * permitindo rotação sem downtime.</p>
     *
     * @param id ID do client
     * @param xTenantId Header de contexto (opcional)
     * @return ClientSecretResponseDTO com novo segredo
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/clients/{id}/regenerate-secret",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "client", fallbackMethod = "regenerateSecretFallback")
    @Retry(name = "client")
    ClientSecretResponseDTO regenerateSecret(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Remove um client.
     *
     * @param id ID do client
     * @param xTenantId Header de contexto (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/clients/{id}")
    @CircuitBreaker(name = "client", fallbackMethod = "deleteFallback")
    @Retry(name = "client")
    void delete(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza um client existente.
     *
     * @param id ID do client
     * @param request DTO com dados atualizados
     * @param xTenantId Header de contexto (opcional)
     * @return ClientResponseDTO com client atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PutMapping(
        value = "/api/clients/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "client", fallbackMethod = "updateFallback")
    @Retry(name = "client")
    ClientResponseDTO update(
        @PathVariable("id") String id,
        @RequestBody ClientCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // Fallback methods

    default ClientResponseDTO createFallback(ClientCreateRequestDTO request,
                                              String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to create client", t);
    }

    default PageResponseDTO<ClientResponseDTO> listFallback(ClientFilterDTO filter,
                                                             String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to list clients", t);
    }

    default ClientResponseDTO getFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to get client", t);
    }

    default ClientResponseDTO getByClientIdFallback(String clientId, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to get client by clientId", t);
    }

    default ClientResponseDTO updateStatusFallback(String id, ClientStatusDTO request,
                                                    String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to update status", t);
    }

    default ClientSecretResponseDTO regenerateSecretFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to regenerate secret", t);
    }

    default void deleteFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to delete client", t);
    }

    default ClientResponseDTO updateFallback(String id, ClientCreateRequestDTO request,
                                              String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "Client service unavailable - unable to update client", t);
    }
}
