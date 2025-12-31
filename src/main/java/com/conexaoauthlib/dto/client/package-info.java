/**
 * Provides Data Transfer Objects (DTOs) for OAuth2 Client (M2M) management operations.
 *
 * <p>This package contains request and response DTOs for client CRUD operations,
 * including creation, retrieval, listing with pagination, status updates,
 * and secret rotation.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientCreateRequestDTO} - Client creation request</li>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientResponseDTO} - Client response with details</li>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientStatusDTO} - Status update request</li>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientFilterDTO} - Pagination and filter parameters</li>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientSecretRequestDTO} - Secret regeneration request</li>
 *   <li>{@link com.conexaoauthlib.dto.client.ClientSecretResponseDTO} - Secret rotation response</li>
 * </ul>
 *
 * <p>Security:</p>
 * <ul>
 *   <li>ClientSecret is marked with @JsonIgnore to prevent accidental exposure</li>
 *   <li>Client ID validation uses alphanumeric pattern with _ and -</li>
 *   <li>Client secret minimum length is 16 characters</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ClientCreateRequestDTO client = ClientCreateRequestDTO.builder()
 *     .clientId("my-service")
 *     .clientSecret("super-secret-key-12345")
 *     .name("My Service Client")
 *     .tenantId("tenant-123")
 *     .grantTypes(List.of("client_credentials"))
 *     .build();
 *
 * ClientResponseDTO response = clientClient.create(client);
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.client;
