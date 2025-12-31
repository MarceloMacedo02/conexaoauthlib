/**
 * API Fluent Builder para operações de Client OAuth2.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de gerenciamento de clients M2M, incluindo
 * registro, consulta, rotação de segredos e gestão de status.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Registro de novos clients</li>
 *   <li>Listagem paginada com filtros</li>
 *   <li>Consulta por ID interno</li>
 *   <li>Consulta por clientId público</li>
 *   <li>Rotação de segredos</li>
 *   <li>Atualização de status</li>
 *   <li>Remoção de clients</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Registro de client
 * ClientResponseDTO client = ClientClient.register()
 *     .clientId("my-service")
 *     .clientSecret("initial-secret")
 *     .name("My Service")
 *     .tenantId("tenant-123")
 *     .grantType("client_credentials")
 *     .scope("read", "write")
 *     .execute();
 *
 * // Rotação de segredo
 * ClientSecretResponseDTO secret = ClientClient.rotateSecret("client-id")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Atualização de status
 * ClientResponseDTO updated = ClientClient.updateStatus("client-id")
 *     .status("SUSPENDED")
 *     .tenant("tenant-123")
 *     .execute();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.client.ClientClient
 * @see com.conexaoauthlib.fluent.client.ClientClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.client;

import io.micrometer.core.lang.NonNullApi;
