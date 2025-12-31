package com.conexaoauthlib.feign.user;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.role.RoleAssignRequestDTO;
import com.conexaoauthlib.dto.user.UserCreateRequestDTO;
import com.conexaoauthlib.dto.user.UserFilterDTO;
import com.conexaoauthlib.dto.user.UserPasswordRequestDTO;
import com.conexaoauthlib.dto.user.UserResponseDTO;
import com.conexaoauthlib.dto.user.UserStatusDTO;
import com.conexaoauthlib.dto.user.UserUpdateRequestDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Interface Feign Client para operações de Usuário.
 *
 * <p>Fornece métodos declarativos para CRUD completo de usuários,
 * incluindo criação, listagem, consulta, atualização, mudança de senha,
 * desativação e atribuição de roles.</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private UserClient userClient;
 *
 * // Criar usuário
 * UserResponseDTO user = userClient.create(
 *     UserCreateRequestDTO.builder()
 *         .name("João Silva")
 *         .email("joao@empresa.com")
 *         .password("Senha123!")
 *         .tenantId("tenant-123")
 *         .build()
 * );
 *
 * // Listar usuários com paginação
 * PageResponseDTO<UserResponseDTO> page = userClient.list(
 *     UserFilterDTO.builder()
 *         .status("ACTIVE")
 *         .tenantId("tenant-123")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Buscar por ID
 * UserResponseDTO found = userClient.getById("user-id");
 *
 * // Atualizar dados parciais
 * userClient.update(
 *     "user-id",
 *     UserUpdateRequestDTO.builder().name("João Silva Santos").build()
 * );
 *
 * // Atualizar status
 * userClient.updateStatus(
 *     "user-id",
 *     UserStatusDTO.builder().status("SUSPENDED").reason("Inatividade").build()
 * );
 *
 * // Mudar senha
 * userClient.changePassword(
 *     "user-id",
 *     UserPasswordRequestDTO.builder()
 *         .currentPassword("Senha123!")
 *         .newPassword("NovaSenha456@")
 *         .build()
 * );
 *
 * // Atribuir roles
 * userClient.assignRoles(
 *     "user-id",
 *     RoleAssignRequestDTO.builder()
 *         .roleIds(List.of("role-1", "role-2"))
 *         .build()
 * );
 *
 * // Desativar usuário
 * userClient.deactivate("user-id");
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "user",
    url = "${conexao-auth.clients.user.base-url}",
    configuration = {UserClientConfiguration.class}
)
public interface UserClient {

    /**
     * Cria um novo usuário.
     *
     * @param request DTO com dados do usuário
     * @param xTenantId Header de contexto (opcional)
     * @return UserResponseDTO com usuário criado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "user", fallbackMethod = "createFallback")
    @Retry(name = "user")
    UserResponseDTO create(
        @RequestBody UserCreateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Lista usuários com paginação e filtros.
     *
     * @param filter Parâmetros de filtro e paginação
     * @param xTenantId Header de contexto (opcional)
     * @return PageResponseDTO com lista de usuários
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/users")
    @CircuitBreaker(name = "user", fallbackMethod = "listFallback")
    @Retry(name = "user")
    PageResponseDTO<UserResponseDTO> list(
        @ModelAttribute UserFilterDTO filter,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Busca usuário pelo ID.
     *
     * @param id ID do usuário
     * @param xTenantId Header de contexto (opcional)
     * @return UserResponseDTO com dados do usuário
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @GetMapping(value = "/api/users/{id}")
    @CircuitBreaker(name = "user", fallbackMethod = "getFallback")
    @Retry(name = "user")
    UserResponseDTO getById(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza dados parciais do usuário.
     *
     * <p>Campos não presentes no request permanecem inalterados.
     * Para alterar a senha, utilize o método changePassword.</p>
     *
     * @param id ID do usuário
     * @param request DTO com dados a atualizar
     * @param xTenantId Header de contexto (opcional)
     * @return UserResponseDTO com usuário atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PatchMapping(
        value = "/api/users/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "user", fallbackMethod = "updateFallback")
    @Retry(name = "user")
    UserResponseDTO update(
        @PathVariable("id") String id,
        @RequestBody UserUpdateRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atualiza status do usuário.
     *
     * @param id ID do usuário
     * @param request DTO com novo status
     * @param xTenantId Header de contexto (opcional)
     * @return UserResponseDTO com usuário atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PatchMapping(
        value = "/api/users/{id}/status",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "user", fallbackMethod = "updateStatusFallback")
    @Retry(name = "user")
    UserResponseDTO updateStatus(
        @PathVariable("id") String id,
        @RequestBody UserStatusDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Altera senha do usuário.
     *
     * <p>Requer a senha atual para validação de identidade.
     * A nova senha deve atender aos requisitos de complexidade.</p>
     *
     * @param id ID do usuário
     * @param request DTO com senhas (atual e nova)
     * @param xTenantId Header de contexto (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     * @throws UnauthorizedException se a senha atual estiver incorreta
     */
    @PostMapping(
        value = "/api/users/{id}/password",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "user", fallbackMethod = "changePasswordFallback")
    @Retry(name = "user")
    void changePassword(
        @PathVariable("id") String id,
        @RequestBody UserPasswordRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Desativa (soft delete) um usuário.
     *
     * <p>O usuário não é removido permanentemente, apenas marcado como
     * inativo. Tokens ativos do usuário são invalidados.</p>
     *
     * @param id ID do usuário
     * @param xTenantId Header de contexto (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @DeleteMapping(value = "/api/users/{id}")
    @CircuitBreaker(name = "user", fallbackMethod = "deactivateFallback")
    @Retry(name = "user")
    void deactivate(
        @PathVariable("id") String id,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Atribui roles ao usuário.
     *
     * @param id ID do usuário
     * @param request DTO com IDs de roles
     * @param xTenantId Header de contexto (opcional)
     * @return UserResponseDTO com usuário atualizado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/api/users/{id}/roles",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "user", fallbackMethod = "assignRolesFallback")
    @Retry(name = "user")
    UserResponseDTO assignRoles(
        @PathVariable("id") String id,
        @RequestBody RoleAssignRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // Fallback methods

    default UserResponseDTO createFallback(UserCreateRequestDTO request,
                                            String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to create user", t);
    }

    default PageResponseDTO<UserResponseDTO> listFallback(UserFilterDTO filter,
                                                           String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to list users", t);
    }

    default UserResponseDTO getFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to get user", t);
    }

    default UserResponseDTO updateFallback(String id, UserUpdateRequestDTO request,
                                            String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to update user", t);
    }

    default UserResponseDTO updateStatusFallback(String id, UserStatusDTO request,
                                                  String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to update status", t);
    }

    default void changePasswordFallback(String id, UserPasswordRequestDTO request,
                                         String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to change password", t);
    }

    default void deactivateFallback(String id, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to deactivate user", t);
    }

    default UserResponseDTO assignRolesFallback(String id, RoleAssignRequestDTO request,
                                                 String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "User service unavailable - unable to assign roles", t);
    }
}
