package br.com.conexaoautolib.health;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Coletor de métricas para ConexãoAuthLib.
 * 
 * Responsável por coletar e registrar métricas de operações
 * da biblioteca usando Micrometer para integração com sistemas de
 * monitoramento.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Component
public class MetricsCollector {

    private final MeterRegistry meterRegistry;

    @Autowired
    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Registra emissão de um novo token.
     */
    public void recordTokenIssued(String serverUrl, String clientId) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.token.issued")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra renovação de token.
     */
    public void recordTokenRenewed(String serverUrl, String clientId) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.token.renewed")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra expiração de token.
     */
    public void recordTokenExpired(String serverUrl, String clientId) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.token.expired")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra falha de autenticação.
     */
    public void recordAuthFailure(String serverUrl, String clientId, String errorType) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.auth.failures")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("error_type", errorType)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra busca de usuário.
     */
    public void recordUserLookup(String serverUrl, String clientId) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.usuario.lookup")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra tentativa de retry.
     */
    public void recordRetryAttempt(String serverUrl, String clientId, String operation) {
        // Get counter with tags and increment
        Counter counter = Counter.builder("conexaoauth.retry.attempts")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("operation", operation)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Mede latência de operação de autenticação.
     */
    public <T> T recordAuthLatency(String serverUrl, String clientId, String operation, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = supplier.get();
            Timer timer = Timer.builder("conexaoauth.auth.latency")
                    .tag("server_url", serverUrl)
                    .tag("client_id", clientId)
                    .tag("operation", operation)
                    .tag("success", "true")
                    .register(meterRegistry);
            sample.stop(timer);
            return result;
        } catch (Exception e) {
            Timer timer = Timer.builder("conexaoauth.auth.latency")
                    .tag("server_url", serverUrl)
                    .tag("client_id", clientId)
                    .tag("operation", operation)
                    .tag("success", "false")
                    .register(meterRegistry);
            sample.stop(timer);
            throw e;
        }
    }

    /**
     * Registra latência manual de operação de autenticação.
     */
    public void recordAuthLatency(String serverUrl, String clientId, String operation,
            Duration duration, boolean success) {
        Timer timer = Timer.builder("conexaoauth.auth.latency")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("operation", operation)
                .tag("success", String.valueOf(success))
                .register(meterRegistry);
        timer.record(duration);
    }

    /**
     * Registra contador genérico com tags personalizadas.
     */
    public void recordCounter(String metricName, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags devem estar em pares chave-valor");
        }

        Counter counter = Counter.builder(metricName)
                .tags(tags)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Registra timer genérico com tags personalizadas.
     */
    public void recordTimer(String metricName, Duration duration, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags devem estar em pares chave-valor");
        }

        Timer timer = Timer.builder(metricName)
                .tags(tags)
                .register(meterRegistry);
        timer.record(duration);
    }
}