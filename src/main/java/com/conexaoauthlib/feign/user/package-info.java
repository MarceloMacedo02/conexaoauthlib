/**
 * Provides Feign Client interfaces for User management operations.
 *
 * <p>This package contains the User Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the User API.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.user.UserClient} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.user.UserClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>User CRUD operations (create, read, update, delete)</li>
 *   <li>Paginated listing with filters</li>
 *   <li>Partial updates (PATCH)</li>
 *   <li>Status updates</li>
 *   <li>Password changes (requires current password)</li>
 *   <li>Role assignments</li>
 *   <li>Cross-tenant queries via X-Tenant-Id header</li>
 *   <li>CircuitBreaker and Retry integration</li>
 * </ul>
 *
 * <p>Security Considerations:</p>
 * <ul>
 *   <li>Password change requires current password validation</li>
 *   <li>Passwords are never exposed in responses</li>
 *   <li>Deactivation invalidates active tokens</li>
 *   <li>Password strength validation is enforced</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * UserClient userClient = ...; // Injected via Spring
 *
 * // Create user
 * UserResponseDTO created = userClient.create(
 *     UserCreateRequestDTO.builder()
 *         .name("João Silva")
 *         .email("joao@empresa.com")
 *         .password("SenhaForte123!")
 *         .tenantId("tenant-123")
 *         .build()
 * );
 *
 * // List users
 * PageResponseDTO<UserResponseDTO> page = userClient.list(
 *     UserFilterDTO.builder()
 *         .status("ACTIVE")
 *         .tenantId("tenant-123")
 *         .build()
 * );
 *
 * // Get user
 * UserResponseDTO user = userClient.getById("user-id");
 *
 * // Update user
 * userClient.update(
 *     "user-id",
 *     UserUpdateRequestDTO.builder().name("João Silva Santos").build()
 * );
 *
 * // Update status
 * userClient.updateStatus(
 *     "user-id",
 *     UserStatusDTO.builder().status("SUSPENDED").build()
 * );
 *
 * // Change password
 * userClient.changePassword(
 *     "user-id",
 *     UserPasswordRequestDTO.builder()
 *         .currentPassword("Senha123!")
 *         .newPassword("NovaSenha456@")
 *         .build()
 * );
 *
 * // Assign roles
 * userClient.assignRoles(
 *     "user-id",
 *     RoleAssignRequestDTO.builder()
 *         .roleIds(List.of("admin", "user"))
 *         .build()
 * );
 *
 * // Deactivate user
 * userClient.deactivate("user-id");
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.user;
