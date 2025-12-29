/**
 * Exception hierarchy for ConexãoAuth library.
 * 
 * <p>This package defines a hierarchical exception structure for handling
 * various error conditions that may occur during ConexãoAuth operations.
 * All exceptions extend from {@link br.com.conexaoautolib.exception.ConexaoAuthException}
 * for consistent error handling.</p>
 * 
 * <h3>Exception Hierarchy:</h3>
 * <ul>
 *   <li>{@code ConexaoAuthException} - Base exception for all library errors</li>
 *   <li>{@code InvalidCredentialsException} - Authentication failed</li>
 *   <li>{@code UserNotFoundException} - User not found in system</li>
 *   <li>{@code TokenExpiredException} - OAuth2 token has expired</li>
 *   <li>{@code RealmNotFoundException} - Realm not available</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package br.com.conexaoautolib.exception;