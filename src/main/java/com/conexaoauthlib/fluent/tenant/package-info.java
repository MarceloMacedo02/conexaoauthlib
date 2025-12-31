/**
 * API Fluent Builder para operações de Tenant.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de gerenciamento de tenants, incluindo criação,
 * listagem, consulta, atualização de status e gerenciamento de produtos.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Criação de tenants com ou sem produtos</li>
 *   <li>Listagem paginada com filtros</li>
 *   <li>Consulta por ID</li>
 *   <li>Consulta por documento (CPF/CNPJ)</li>
 *   <li>Atualização de status</li>
 *   <li>Gerenciamento de produtos</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Criação com produtos
 * TenantResponseDTO tenant = TenantClient.create()
 *     .name("Empresa X")
 *     .documentNumber("12345678000100")
 *     .product("premium")
 *     .product("analytics")
 *     .executeWithProducts();
 *
 * // Listagem com filtros
 * PageResponseDTO<TenantResponseDTO> tenants = TenantClient.list()
 *     .name("Empresa")
 *     .status("ACTIVE")
 *     .page(0)
 *     .size(20)
 *     .execute();
 *
 * // Busca por documento
 * TenantResponseDTO tenant = TenantClient.findByDocument("12345678000100")
 *     .execute();
 *
 * // Atualização de status
 * TenantResponseDTO updated = TenantClient.updateStatus("tenant-id")
 *     .status("SUSPENDED")
 *     .reason("Payment overdue")
 *     .execute();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.tenant.TenantClient
 * @see com.conexaoauthlib.fluent.tenant.TenantClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.tenant;

import io.micrometer.core.lang.NonNullApi;
