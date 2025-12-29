/**
 * ConexãoAuthLib - Spring Boot starter library for ConexãoAuth server integration.
 * 
 * <p>This library provides seamless integration with ConexãoAuth authorization server
 * through fluent APIs and automatic configuration. It handles OAuth2 token management,
 * user authentication, and provides declarative HTTP clients via OpenFeign.</p>
 * 
 * <h3>Main Features:</h3>
 * <ul>
 *   <li>Fluent API design for intuitive usage</li>
 *   <li>Automatic token management and caching</li>
 *   <li>Declarative HTTP clients with OpenFeign</li>
 *   <li>Spring Boot autoconfiguration</li>
 *   <li>Comprehensive error handling and retry logic</li>
 * </ul>
 * 
 * <h3>Quick Start:</h3>
 * <pre>{@code
 * // Add dependency to pom.xml
 * // Configure conexaoauth.server.url in application.yml
 * 
 * // Use fluent API to get token
 * TokenResponse token = TokenClient.gerar()
 *     .clientId("your-client")
 *     .secret("your-secret")
 *     .execute();
 * 
 * // Use fluent API to query users
 * UsuarioResponse user = UsuarioClient.filtrar()
 *     .porId(userId)
 *     .execute();
 * }</pre>
 * 
 * @since 1.0.0
 */
package br.com.conexaoautolib;