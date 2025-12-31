/**
 * Provides Feign Client interfaces for Tenant management operations.
 *
 * <p>This package contains the Tenant Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the Tenant API.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.tenant.TenantClient} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.tenant.TenantClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Tenant CRUD operations (create, read, update)</li>
 *   <li>Paginated listing with filters</li>
 *   <li>Status updates</li>
 *   <li>Product management (add/remove)</li>
 *   <li>Cross-tenant queries via X-Tenant-Id header</li>
 *   <li>CircuitBreaker and Retry integration</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * TenantClient client = ...; // Injected via Spring
 *
 * // Create tenant
 * TenantResponseDTO tenant = client.create(
 *     TenantCreateRequestDTO.builder()
 *         .name("Empresa X")
 *         .documentNumber("12345678000100")
 *         .products(List.of("MOD_RH"))
 *         .build()
 * );
 *
 * // List tenants with pagination
 * PageResponseDTO<TenantResponseDTO> page = client.list(
 *     TenantFilterDTO.builder()
 *         .status("ACTIVE")
 *         .page(0)
 *         .size(20)
 *         .build()
 * );
 *
 * // Get by ID
 * TenantResponseDTO found = client.getById("tenant-id");
 *
 * // Update status
 * client.updateStatus(
 *     "tenant-id",
 *     TenantStatusDTO.builder().status("SUSPENDED").build()
 * );
 *
 * // Add products
 * client.addProducts(
 *     "tenant-id",
 *     TenantProductAddRequestDTO.builder()
 *         .productCodes(List.of("MOD_FIN"))
 *         .build()
 * );
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.tenant;
