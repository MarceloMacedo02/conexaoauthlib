/**
 * Provides Feign Client interfaces for OAuth2 Client (M2M) management operations.
 *
 * <p>This package contains the Client Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the Client API.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.client.ClientClient} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.client.ClientClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Client CRUD operations (create, read, update, delete)</li>
 *   <li>Paginated listing with filters</li>
 *   <li>Status updates</li>
 *   <li>Secret rotation (dual-validity period)</li>
 *   <li>Cross-tenant queries via X-Tenant-Id header</li>
 *   <li>CircuitBreaker and Retry integration</li>
 * </ul>
 *
 * <p>Security Considerations:</p>
 * <ul>
 *   <li>Secret rotation maintains old secret valid temporarily</li>
 *   <li>Client secret is never exposed in responses</li>
 *   <li>Client deletion invalidates active tokens</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ClientClient client = ...; // Injected via Spring
 *
 * // Create client
 * ClientResponseDTO created = client.create(
 *     ClientCreateRequestDTO.builder()
 *         .clientId("my-service")
 *         .clientSecret("secure-secret-12345")
 *         .name("My Service")
 *         .tenantId("tenant-123")
 *         .grantTypes(List.of("client_credentials"))
 *         .build()
 * );
 *
 * // List clients
 * PageResponseDTO<ClientResponseDTO> page = client.list(
 *     ClientFilterDTO.builder()
 *         .status("ACTIVE")
 *         .tenantId("tenant-123")
 *         .build()
 * );
 *
 * // Rotate secret
 * ClientSecretResponseDTO secret = client.regenerateSecret("client-id");
 * // Store newSecret immediately!
 *
 * // Update status
 * client.updateStatus(
 *     "client-id",
 *     ClientStatusDTO.builder().status("SUSPENDED").build()
 * );
 *
 * // Delete client
 * client.delete("client-id");
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.client;
