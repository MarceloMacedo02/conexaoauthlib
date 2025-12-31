/**
 * Provides Data Transfer Objects (DTOs) for Scope management operations.
 *
 * <p>This package contains request and response DTOs for scope CRUD operations.
 * Scopes define granular permissions following the "resource:action" pattern.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.scope.ScopeCreateRequestDTO} - Scope creation request</li>
 *   <li>{@link com.conexaoauthlib.dto.scope.ScopeResponseDTO} - Scope response with details</li>
 *   <li>{@link com.conexaoauthlib.dto.scope.ScopeFilterDTO} - Filter parameters</li>
 *   <li>{@link com.conexaoauthlib.dto.scope.ScopeAssignRequestDTO} - Scope assignment request</li>
 * </ul>
 *
 * <p>Scope Naming Convention:</p>
 * <ul>
 *   <li>Format: "resource:action" (lowercase letters, numbers, underscore)</li>
 *   <li>Examples: "users:read", "orders:write", "products:delete"</li>
 *   <li>Special: "all" action grants full access to a resource</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ScopeCreateRequestDTO scope = ScopeCreateRequestDTO.builder()
 *     .name("users:read")
 *     .description("Permissão para leitura de dados de usuários")
 *     .resource("users")
 *     .action("read")
 *     .build();
 *
 * ScopeResponseDTO response = scopeClient.create(scope);
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.scope;
