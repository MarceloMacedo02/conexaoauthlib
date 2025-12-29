/**
 * Configuration classes for OpenFeign and HTTP client setup.
 * 
 * <p>This package contains configuration classes that define how OpenFeign
 * clients should behave, including error handling, retry logic, timeout settings,
 * and request/response interceptors. These configurations ensure consistent
 * behavior across all HTTP client implementations.</p>
 * 
 * <h3>Key Components:</h3>
 * <ul>
 *   <li>{@link br.com.conexaoautolib.config.ConexaoAuthFeignConfig} - Main Feign configuration</li>
 *   <li>{@link br.com.conexaoautolib.config.ConexaoAuthErrorDecoder} - Error handling</li>
 *   <li>{@link br.com.conexaoautolib.config.ConexaoAuthRetryer} - Custom retry logic</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package br.com.conexaoautolib.config;