package com.conexaoauthlib.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Status do CircuitBreaker para verificação programática.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ResilienceStatus {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienceStatus(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * Retorna o status de todos os CircuitBreakers.
     *
     * @return Map com nome do CircuitBreaker e seu estado
     */
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<>();

        for (CircuitBreaker cb : circuitBreakerRegistry.getAllCircuitBreakers()) {
            status.put(cb.getName(), cb.getState().name());
        }

        return status;
    }

    /**
     * Verifica se todos os CircuitBreakers estão fechados.
     *
     * @return true se todos estão CLOSED
     */
    public boolean isAllHealthy() {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
            .allMatch(cb -> cb.getState() == CircuitBreaker.State.CLOSED);
    }

    /**
     * Retorna o número de CircuitBreakers em cada estado.
     *
     * @return Map com contagem por estado
     */
    public Map<String, Integer> getStateCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("CLOSED", 0);
        counts.put("OPEN", 0);
        counts.put("HALF_OPEN", 0);

        for (CircuitBreaker cb : circuitBreakerRegistry.getAllCircuitBreakers()) {
            String state = cb.getState().name();
            counts.merge(state, 1, Integer::sum);
        }

        return counts;
    }

    /**
     * Verifica se um CircuitBreaker específico está aberto.
     *
     * @param name Nome do CircuitBreaker
     * @return true se está OPEN
     */
    public boolean isOpen(String name) {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
            .filter(cb -> cb.getName().equals(name))
            .findFirst()
            .map(cb -> cb.getState() == CircuitBreaker.State.OPEN)
            .orElse(false);
    }

    /**
     * Retorna o estado de um CircuitBreaker específico.
     *
     * @param name Nome do CircuitBreaker
     * @return Estado do CircuitBreaker ou null
     */
    public String getState(String name) {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
            .filter(cb -> cb.getName().equals(name))
            .findFirst()
            .map(cb -> cb.getState().name())
            .orElse(null);
    }

    /**
     * Retorna o número de falhas de um CircuitBreaker.
     *
     * @param name Nome do CircuitBreaker
     * @return Número de falhas
     */
    public Long getFailedCalls(String name) {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
            .filter(cb -> cb.getName().equals(name))
            .findFirst()
            .map(cb -> Long.valueOf(cb.getMetrics().getNumberOfFailedCalls()))
            .orElse(Long.valueOf(0L));
    }

    /**
     * Retorna o número de chamadas bem-sucedidas.
     *
     * @param name Nome do CircuitBreaker
     * @return Número de chamadas
     */
    public Long getSuccessfulCalls(String name) {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
            .filter(cb -> cb.getName().equals(name))
            .findFirst()
            .map(cb -> Long.valueOf(cb.getMetrics().getNumberOfSuccessfulCalls()))
            .orElse(Long.valueOf(0L));
    }
}
