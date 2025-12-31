/**
 * API Fluent Builder para operações OAuth2.
 *
 * <p>Este módulo fornece uma interface fluente e intuitiva para
 * todas as operações de autenticação OAuth2, eliminando a necessidade
 * de trabalhar diretamente com DTOs e chamadas Feign.</p>
 *
 * <h3>Operações suportadas:</h3>
 * <ul>
 *   <li>Client Credentials Grant - Para autenticação M2M</li>
 *   <li>Password Grant - Para usuários com credenciais</li>
 *   <li>Refresh Token - Renovação de tokens</li>
 *   <li>Introspect - Verificação de validade de tokens</li>
 *   <li>Revoke - Revogação de tokens</li>
 * </ul>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // Client Credentials
 * TokenResponseDTO token = AuthClient.clientCredentials("client-id", "secret")
 *     .scope("read write")
 *     .execute();
 *
 * // Password Grant
 * TokenResponseDTO token = AuthClient.password("user@example.com", "password123")
 *     .clientCredentials("client-id", "secret")
 *     .scope("read write admin")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Refresh Token
 * TokenResponseDTO token = AuthClient.refreshToken("refresh-token")
 *     .clientCredentials("client-id", "secret")
 *     .execute();
 *
 * // Introspect
 * IntrospectResponseDTO info = AuthClient.introspect(accessToken)
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * // Revoke
 * AuthClient.revoke(accessToken)
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.fluent.oauth2.AuthClient
 * @see com.conexaoauthlib.fluent.oauth2.OAuth2ClientFactory
 */
@NonNullApi
package com.conexaoauthlib.fluent.oauth2;

import io.micrometer.core.lang.NonNullApi;
