/**
 * Módulo de Configuração de Resiliência.
 *
 * <p>Este módulo fornece configurações de CircuitBreaker e Retry
 * para todos os clientes Feign da biblioteca, garantindo resiliência
 * e tolerância a falhas nas comunicações com serviços externos.</p>
 *
 * <h3>Componentes principais:</h3>
 * <ul>
 *   <li>ResilienceConfiguration - Configuração centralizada de CircuitBreaker e Retry</li>
 *   <li>ResilienceMetrics - Métricas Prometheus para monitoramento</li>
 *   <li>ResilienceHealthIndicator - Health check para status dos CircuitBreakers</li>
 *   <li>ResilienceProperties - Propriedades configuráveis via application.yml</li>
 * </ul>
 *
 * <h3>Configuração:</h3>
 * <p>As configurações podem ser sobrescritas via application.yml:</p>
 * <pre>{@code
 * conexao-auth:
 *   resilience:
 *     circuit-breaker:
 *       oauth2:
 *         failure-rate-threshold: 30
 *         wait-duration-in-open-state: 30s
 *     retry:
 *       oauth2:
 *         max-attempts: 5
 *         wait-duration: 1s
 * }</pre>
 *
 * <h3>Métricas Prometheus:</h3>
 * <ul>
 *   <li>resilience4j.circuitbreaker.state - Estado do CircuitBreaker</li>
 *   <li>resilience4j.circuitbreaker.failed.calls - Chamadas falhas</li>
 *   <li>resilience4j.circuitbreaker.successful.calls - Chamadas bem-sucedidas</li>
 *   <li>resilience4j.circuitbreaker.not.permitted.calls - Chamadas rejeitadas</li>
 * </ul>
 *
 * @since 1.0.0
 * @see com.conexaoauthlib.resilience.ResilienceConfiguration
 * @see com.conexaoauthlib.resilience.ResilienceMetrics
 * @see com.conexaoauthlib.resilience.ResilienceHealthIndicator
 */
@NonNullApi
package com.conexaoauthlib.resilience;

import io.micrometer.core.lang.NonNullApi;
