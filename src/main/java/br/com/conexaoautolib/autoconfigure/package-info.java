/**
 * Spring Boot autoconfiguration for ConexãoAuth library.
 * 
 * <p>This package provides Spring Boot autoconfiguration classes that automatically
 * configure ConexãoAuth library beans based on application properties. The main
 * autoconfiguration class {@code ConexaoAuthAutoConfiguration} sets up all necessary
 * components including HTTP clients, token storage, and facades.</p>
 * 
 * <h3>Configuration Properties:</h3>
 * <ul>
 *   <li>{@code conexaoauth.server.*} - Server endpoint configuration</li>
 *   <li>{@code conexaoauth.client.*} - HTTP client settings</li>
 *   <li>{@code conexaoauth.retry.*} - Retry and circuit breaker configuration</li>
 *   <li>{@code conexaoauth.logging.*} - Logging configuration</li>
 * </ul>
 * 
 * <h3>Automatic Beans:</h3>
 * <ul>
 *   <li>{@link br.com.conexaoautolib.facade.TokenClient}</li>
 *   <li>{@link br.com.conexaoautolib.facade.UsuarioClient}</li>
 *   <li>{@link br.com.conexaoautolib.storage.TokenStorage}</li>
 *   <li>OpenFeign client configurations</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package br.com.conexaoautolib.autoconfigure;