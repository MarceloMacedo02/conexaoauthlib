package com.conexaoauthlib.resilience;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propriedades de configuração de resiliência.
 * Carregadas de application.yml com prefixo "conexao-auth.resilience".
 *
 * <p>Exemplo de configuração em application.yml:</p>
 * <pre>{@code
 * conexao-auth:
 *   resilience:
 *     circuit-breaker:
 *       oauth2:
 *         failure-rate-threshold: 25
 *         wait-duration-in-open-state: 30s
 *         minimum-number-of-calls: 3
 *     retry:
 *       oauth2:
 *         max-attempts: 5
 *         wait-duration: 1s
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "conexao-auth.resilience")
public class ResilienceProperties {

    private CircuitBreakerProperties circuitBreaker = new CircuitBreakerProperties();
    private RetryProperties retry = new RetryProperties();

    @Data
    public static class CircuitBreakerProperties {
        private ClientProperties oauth2 = new ClientProperties();
        private ClientProperties tenant = new ClientProperties();
        private ClientProperties client = new ClientProperties();
        private ClientProperties user = new ClientProperties();
        private ClientProperties role = new ClientProperties();
        private ClientProperties scope = new ClientProperties();
    }

    @Data
    public static class RetryProperties {
        private ClientProperties oauth2 = new ClientProperties();
        private ClientProperties tenant = new ClientProperties();
        private ClientProperties client = new ClientProperties();
        private ClientProperties user = new ClientProperties();
        private ClientProperties role = new ClientProperties();
        private ClientProperties scope = new ClientProperties();
    }

    @Data
    public static class ClientProperties {
        /**
         * Threshold de taxa de falhas para abrir o circuit breaker.
         * Valor padrão: 50 (50%)
         */
        private float failureRateThreshold = 50;

        /**
         * Número máximo de tentativas de retry.
         * Valor padrão: 3
         */
        private int maxAttempts = 3;

        /**
         * Tempo de espera entre retries.
         * Valor padrão: 500ms
         */
        private String waitDuration = "500ms";

        /**
         * Tamanho da janela deslizante.
         * Valor padrão: 10
         */
        private int slidingWindowSize = 10;

        /**
         * Número mínimo de chamadas para calcular taxa de falha.
         * Valor padrão: 5
         */
        private int minimumNumberOfCalls = 5;

        /**
         * Tempo em estado OPEN antes de ir para HALF_OPEN.
         * Valor padrão: 60s
         */
        private String waitDurationInOpenState = "60s";

        /**
         * Número de chamadas permitidas em estado HALF_OPEN.
         * Valor padrão: 3
         */
        private int permittedNumberOfCallsInHalfOpenState = 3;
    }
}
