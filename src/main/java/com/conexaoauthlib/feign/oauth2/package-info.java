/**
 * Provides Feign Client interfaces for OAuth2 authentication operations.
 *
 * <p>This package contains the main OAuth2 Feign client interface and its
 * configuration, enabling type-safe HTTP communication with the OAuth2 server.</p>
 *
 * <p>Key Components:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.feign.oauth2.OAuth2Client} - Main Feign client interface</li>
 *   <li>{@link com.conexaoauthlib.feign.oauth2.OAuth2ClientConfiguration} - Client configuration</li>
 * </ul>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Token creation (client_credentials, password, refresh_token)</li>
 *   <li>Token introspection (RFC 7662)</li>
 *   <li>Token revocation (RFC 7009)</li>
 *   <li>CircuitBreaker integration for resilience</li>
 *   <li>Retry mechanism for transient failures</li>
 *   <li>Custom error handling</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * OAuth2Client client = ...; // Injected via Spring
 *
 * // Get token
 * TokenResponseDTO token = client.getToken(
 *     TokenRequestDTO.builder()
 *         .grantType("client_credentials")
 *         .clientId("my-client")
 *         .clientSecret("secret")
 *         .build()
 * );
 *
 * // Introspect token
 * IntrospectResponseDTO info = client.introspect(
 *     IntrospectRequestDTO.builder()
 *         .token(token.getAccessToken())
 *         .build()
 * );
 *
 * // Revoke token
 * client.revoke(
 *     RevokeRequestDTO.builder()
 *         .token(token.getAccessToken())
 *         .build()
 * );
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.feign.oauth2;
