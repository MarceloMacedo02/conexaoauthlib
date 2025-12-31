package com.conexaoauthlib.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração centralizada de resiliência.
 * Configura CircuitBreaker e Retry para cada cliente da biblioteca.
 *
 * <h3>Configurações por cliente:</h3>
 * <ul>
 *   <li>oauth2 - Mais sensível a falhas</li>
 *   <li>tenant - Configuração padrão</li>
 *   <li>client - Configuração padrão</li>
 *   <li>user - Configuração padrão</li>
 *   <li>role - Retry conservador (2 tentativas)</li>
 *   <li>scope - Retry conservador (2 tentativas)</li>
 * </ul>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class ResilienceConfiguration {

    /**
     * Registry de CircuitBreakers configurado.
     * Utiliza o padrão de configuração do Resilience4j 2.x com Map de configurações.
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        Map<String, CircuitBreakerConfig> configs = new HashMap<>();

        // Configuração padrão (usada como fallback)
        configs.put("default", createDefaultCircuitBreakerConfig());

        // Configurações específicas por cliente
        configs.put("oauth2", createOAuth2CircuitBreakerConfig());
        configs.put("tenant", createTenantCircuitBreakerConfig());
        configs.put("client", createClientCircuitBreakerConfig());
        configs.put("user", createUserCircuitBreakerConfig());
        configs.put("role", createRoleCircuitBreakerConfig());
        configs.put("scope", createScopeCircuitBreakerConfig());

        return CircuitBreakerRegistry.of(configs);
    }

    /**
     * Registry de Retries configurado.
     * Utiliza o padrão de configuração do Resilience4j 2.x com Map de configurações.
     */
    @Bean
    public RetryRegistry retryRegistry() {
        Map<String, RetryConfig> configs = new HashMap<>();

        // Configuração padrão (usada como fallback)
        configs.put("default", createDefaultRetryConfig());

        // Configurações específicas por cliente
        configs.put("oauth2", createOAuth2RetryConfig());
        configs.put("tenant", createTenantRetryConfig());
        configs.put("client", createClientRetryConfig());
        configs.put("user", createUserRetryConfig());
        configs.put("role", createRoleRetryConfig());
        configs.put("scope", createScopeRetryConfig());

        return RetryRegistry.of(configs);
    }

    /**
     * Cria e retorna um CircuitBreaker para o cliente especificado.
     *
     * @param clientName Nome do cliente
     * @return CircuitBreaker configurado
     */
    public CircuitBreaker getCircuitBreaker(String clientName) {
        return circuitBreakerRegistry().circuitBreaker(clientName);
    }

    /**
     * Cria e retorna um Retry para o cliente especificado.
     *
     * @param clientName Nome do cliente
     * @return Retry configurado
     */
    public Retry getRetry(String clientName) {
        return retryRegistry().retry(clientName);
    }

    // ==================== Default Configurations ====================

    private CircuitBreakerConfig createDefaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .recordExceptions(
                java.io.IOException.class,
                java.util.concurrent.TimeoutException.class,
                feign.RetryableException.class
            )
            .build();
    }

    private RetryConfig createDefaultRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== OAuth2 Configuration ====================

    private CircuitBreakerConfig createOAuth2CircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(3)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .failureRateThreshold(30)
            .build();
    }

    private RetryConfig createOAuth2RetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== Tenant Configuration ====================

    private CircuitBreakerConfig createTenantCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .build();
    }

    private RetryConfig createTenantRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== Client Configuration ====================

    private CircuitBreakerConfig createClientCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .build();
    }

    private RetryConfig createClientRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== User Configuration ====================

    private CircuitBreakerConfig createUserCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .build();
    }

    private RetryConfig createUserRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== Role Configuration ====================

    private CircuitBreakerConfig createRoleCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .build();
    }

    private RetryConfig createRoleRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(2)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }

    // ==================== Scope Configuration ====================

    private CircuitBreakerConfig createScopeCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .failureRateThreshold(50)
            .build();
    }

    private RetryConfig createScopeRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(2)
            .waitDuration(Duration.ofMillis(500))
            .build();
    }
}
