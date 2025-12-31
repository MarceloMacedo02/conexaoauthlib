/**
 * API Fluent Builder para operações de Scope.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de gerenciamento de scopes (permissões).
 * Scopes seguem o padrão "resource:action" e definem permissões
 * granulares para acesso a recursos protegidos.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Criação de scopes</li>
 *   <li>Listagem com filtros</li>
 *   <li>Consulta por ID</li>
 *   <li>Atualização de descrição</li>
 *   <li>Remoção de scopes</li>
 * </ul>
 *
 * <h3>Convenções de nomenclatura:</h3>
 * <ul>
 *   <li>Scopes usam formato "recurso:acao" (ex: "users:read")</li>
 *   <li>Letras minúsculas e underscore permitidos</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Criação de scope
 * ScopeResponseDTO scope = ScopeClient.create()
 *     .name("users:read")
 *     .description("Permissão para leitura de usuários")
 *     .resource("users")
 *     .action("read")
 *     .execute();
 *
 * // Listagem de scopes
 * List<ScopeResponseDTO> scopes = ScopeClient.list()
 *     .resource("users")
 *     .execute();
 *
 * // Listagem retornando nomes
 * List<String> names = ScopeClient.list()
 *     .resource("users")
 *     .executeAsNames();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.scope.ScopeClient
 * @see com.conexaoauthlib.fluent.scope.ScopeClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.scope;

import io.micrometer.core.lang.NonNullApi;
