/**
 * API Fluent Builder para operações de Usuário.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de gerenciamento de usuários, incluindo
 * registro, consulta, atualização, mudança de senha e desativação.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Registro de novos usuários</li>
 *   <li>Listagem paginada com filtros</li>
 *   <li>Consulta por ID</li>
 *   <li>Atualização parcial de dados</li>
 *   <li>Mudança de senha com validação</li>
 *   <li>Atualização de status</li>
 *   <li>Atribuição de roles</li>
 *   <li>Desativação de usuários</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Registro de usuário
 * UserResponseDTO user = UserClient.register()
 *     .name("João Silva")
 *     .email("joao@empresa.com")
 *     .password("Senha123!")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Mudança de senha
 * UserClient.changePassword("user-id")
 *     .currentPassword("Senha123!")
 *     .newPassword("NovaSenha456!")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Atualização de status
 * UserResponseDTO updated = UserClient.updateStatus("user-id")
 *     .status("SUSPENDED")
 *     .reason("Inatividade prolongada")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.user.UserClient
 * @see com.conexaoauthlib.fluent.user.UserClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.user;

import io.micrometer.core.lang.NonNullApi;
