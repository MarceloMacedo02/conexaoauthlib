/**
 * Provides Feign Client interfaces for Role management operations.
 *
 * <p>This package contains the Role Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the Role API.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.role.RoleClient} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.role.RoleClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Role CRUD operations (create, read, update, delete)</li>
 *   <li>Paginated listing with filters</li>
 *   <li>Status updates</li>
 *   <li>Scope assignment and removal</li>
 *   <li>Cross-tenant queries via X-Tenant-Id header</li>
 *   <li>CircuitBreaker and Retry integration</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * RoleClient client = ...; // Injected via Spring
 *
 * // Create role
 * RoleResponseDTO role = client.create(
 *     RoleCreateRequestDTO.builder()
 *         .name("ADMIN")
 *         .description("Administrator role")
 *         .build()
 * );
 *
 * // List roles with pagination
 * PageResponseDTO<RoleResponseDTO> page = client.list(
 *     RoleFilterDTO.builder()
 *         .status("ACTIVE")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Get by ID
 * RoleResponseDTO found = client.getById("role-id");
 *
 * // Update status
 * client.updateStatus(
 *     "role-id",
 *     RoleStatusDTO.builder().status("INACTIVE").build()
 * );
 *
 * // Assign scopes to role
 * client.assignScopes(
 *     "role-id",
 *     ScopeAssignRequestDTO.builder()
 *         .scopeIds(List.of("scope-read", "scope-write"))
 *         .build()
 * );
 *
 * // Remove scopes from role
 * client.removeScopes(
 *     "role-id",
 *     List.of("scope-read")
 * );
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.role;
