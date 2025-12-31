/**
 * API Fluent Builder para operações de Role.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de gerenciamento de roles e permissões,
 * incluindo criação, listagem, consulta, atribuição de scopes
 * e gestão de status.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Criação de roles com scopes iniciais</li>
 *   <li>Listagem paginada com filtros</li>
 *   <li>Consulta por ID</li>
 *   <li>Atribuição de scopes</li>
 *   <li>Remoção de scopes</li>
 *   <li>Atualização de status</li>
 *   <li>Desativação de roles</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Criação de role com scopes
 * RoleResponseDTO role = RoleClient.create()
 *     .name("admin")
 *     .description("Administrador do sistema")
 *     .tenantId("tenant-123")
 *     .scopeId("users:read")
 *     .scopeId("users:write")
 *     .scopeId("users:delete")
 *     .execute();
 *
 * // Atribuição de scopes
 * RoleClient.assignScopes("role-id")
 *     .scopeId("orders:read")
 *     .scopeId("orders:write")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Remoção de scopes
 * RoleClient.removeScopes("role-id")
 *     .scopeId("users:delete")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.role.RoleClient
 * @see com.conexaoauthlib.fluent.role.RoleClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.role;

import io.micrometer.core.lang.NonNullApi;
