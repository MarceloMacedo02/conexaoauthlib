/**
 * Provides Feign Client interfaces for Scope management operations.
 *
 * <p>This package contains the Scope Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the Scope API.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.scope.ScopeClient} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.scope.ScopeClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Scope CRUD operations (create, read, update, delete)</li>
 *   <li>Listing all available scopes</li>
 *   <li>Filtered listing by resource or action</li>
 *   <li>Cross-tenant queries via X-Tenant-Id header</li>
 *   <li>CircuitBreaker and Retry integration</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ScopeClient client = ...; // Injected via Spring
 *
 * // Create scope
 * ScopeResponseDTO scope = client.create(
 *     ScopeCreateRequestDTO.builder()
 *         .code("users:read")
 *         .name("Read Users")
 *         .description("Permission to read user data")
 *         .resource("users")
 *         .action("read")
 *         .build()
 * );
 *
 * // List all scopes
 * List<ScopeResponseDTO> scopes = client.list(null);
 *
 * // List scopes with filter
 * List<ScopeResponseDTO> filtered = client.list(
 *     ScopeFilterDTO.builder()
 *         .resource("users")
 *         .build()
 * );
 *
 * // Get by ID
 * ScopeResponseDTO found = client.getById("scope-id");
 *
 * // Update scope
 * scopeClient.update(
 *     "scope-id",
 *     ScopeCreateRequestDTO.builder()
 *         .code("users:read")
 *         .name("Read Users (Updated)")
 *         .description("Updated permission description")
 *         .resource("users")
 *         .action("read")
 *         .build()
 * );
 *
 * // Delete scope
 * client.delete("scope-id");
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.scope;
