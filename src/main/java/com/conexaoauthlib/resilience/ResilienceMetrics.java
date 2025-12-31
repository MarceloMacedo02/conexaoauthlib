package com.conexaoauthlib.resilience;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuração de métricas Prometheus para resiliência.
 * Expõe métricas de CircuitBreaker para monitoramento via Prometheus.
 *
 * <h3>Métricas expostas:</h3>
 * <ul>
 *   <li>resilience4j.circuitbreaker.state - Estado do CircuitBreaker</li>
 *   <li>resilience4j.circuitbreaker.failed.calls - Número de chamadas falhas</li>
 *   <li>resilience4j.circuitbreaker.successful.calls - Chamadas bem-sucedidas</li>
 * </ul>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class ResilienceMetrics {

    // Cache de CircuitBreakers por nome para uso nos gauges
    private final ConcurrentHashMap<String, io.github.resilience4j.circuitbreaker.CircuitBreaker> circuitBreakers =
        new ConcurrentHashMap<>();

    /**
     * Armazena referência aos CircuitBreakers para métricas.
     *
     * @param registry Registry de CircuitBreakers
     */
    @Bean
    public void registerCircuitBreakers(
            io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry registry) {

        circuitBreakers.clear();
        registry.getAllCircuitBreakers().forEach(cb -> {
            circuitBreakers.put(cb.getName(), cb);
        });
    }

    /**
     * Adiciona gauges específicos para dashboard de monitoramento.
     *
     * @param meterRegistry Registro de métricas
     */
    @Bean
    public void registerCircuitBreakerGauges(MeterRegistry meterRegistry) {

        circuitBreakers.forEach((name, circuitBreaker) -> {
            // Estado do CircuitBreaker (0: CLOSED, 1: OPEN, 2: HALF_OPEN)
            Gauge.builder("resilience4j.circuitbreaker.state",
                         circuitBreaker,
                         cb -> cb.getState().ordinal())
                .tag("circuitbreaker", name)
                .description("Estado do CircuitBreaker (0=CLOSED, 1=OPEN, 2=HALF_OPEN)")
                .register(meterRegistry);

            // Número de falhas
            Gauge.builder("resilience4j.circuitbreaker.failed.calls",
                         circuitBreaker,
                         cb -> cb.getMetrics().getNumberOfFailedCalls())
                .tag("circuitbreaker", name)
                .description("Número de chamadas que falharam")
                .register(meterRegistry);

            // Número de chamadas bem-sucedidas
            Gauge.builder("resilience4j.circuitbreaker.successful.calls",
                         circuitBreaker,
                         cb -> cb.getMetrics().getNumberOfSuccessfulCalls())
                .tag("circuitbreaker", name)
                .description("Número de chamadas bem-sucedidas")
                .register(meterRegistry);

            // Número de chamadas não permitidas
            Gauge.builder("resilience4j.circuitbreaker.not.permitted.calls",
                         circuitBreaker,
                         cb -> cb.getMetrics().getNumberOfNotPermittedCalls())
                .tag("circuitbreaker", name)
                .description("Número de chamadas não permitidas")
                .register(meterRegistry);
        });
    }
}
