/**
 * Provides Data Transfer Objects (DTOs) for Tenant management operations.
 *
 * <p>This package contains request and response DTOs for tenant CRUD operations,
 * including creation, retrieval, listing with pagination, status updates,
 * and product subscriptions.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantCreateRequestDTO} - Tenant creation request</li>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantResponseDTO} - Tenant response with details</li>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantStatusDTO} - Status update request</li>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantFilterDTO} - Pagination and filter parameters</li>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantProductDTO} - Product associated with tenant</li>
 *   <li>{@link com.conexaoauthlib.dto.tenant.TenantProductAddRequestDTO} - Product addition request</li>
 * </ul>
 *
 * <p>Validation:</p>
 * <ul>
 *   <li>DocumentValidator validates CPF (11 digits) and CNPJ (14 digits)</li>
 *   <li>Bean Validation annotations ensure data integrity</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * TenantCreateRequestDTO request = TenantCreateRequestDTO.builder()
 *     .name("Empresa Exemplo Ltda")
 *     .documentNumber("12345678901")
 *     .products(List.of("MOD_RH"))
 *     .build();
 *
 * TenantResponseDTO response = tenantClient.create(request);
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.tenant;
