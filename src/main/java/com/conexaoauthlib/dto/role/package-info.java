/**
 * Provides Data Transfer Objects (DTOs) for Role management operations.
 *
 * <p>This package contains request and response DTOs for role CRUD operations,
 * including creation, retrieval, listing with pagination, status updates,
 * and scope assignments.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.role.RoleCreateRequestDTO} - Role creation request</li>
 *   <li>{@link com.conexaoauthlib.dto.role.RoleResponseDTO} - Role response with details</li>
 *   <li>{@link com.conexaoauthlib.dto.role.RoleStatusDTO} - Status update request</li>
 *   <li>{@link com.conexaoauthlib.dto.role.RoleFilterDTO} - Pagination and filter parameters</li>
 *   <li>{@link com.conexaoauthlib.dto.role.RoleAssignRequestDTO} - Role assignment request</li>
 * </ul>
 *
 * <p>Validation:</p>
 * <ul>
 *   <li>Role name must be 3-100 characters with alphanumeric and underscore</li>
 *   <li>Description must be up to 500 characters</li>
 *   <li>Tenant ID is required for all operations</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * RoleCreateRequestDTO role = RoleCreateRequestDTO.builder()
 *     .name("admin")
 *     .description("Administrador com acesso total")
 *     .tenantId("tenant-123")
 *     .scopeIds(List.of("users:read", "users:write"))
 *     .build();
 *
 * RoleResponseDTO response = roleClient.create(role);
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.role;
