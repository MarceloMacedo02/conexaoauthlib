package br.com.conexaoautolib;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ConexãoAuthLib with Actuator and Micrometer.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
class ConexaoAuthLibIntegrationTest {

    @Test
    void testMetricsCollectorWithMeterRegistry() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                br.com.conexaoautolib.autoconfigure.ConexaoAuthAutoConfiguration.class))
            .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
            .run(context -> {
                assertThat(context).hasSingleBean(br.com.conexaoautolib.health.MetricsCollector.class);
                assertThat(context).hasBean(br.com.conexaoautolib.autoconfigure.properties.HealthProperties.class);
            });
    }

    @Test
    void testFullConfiguration() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                br.com.conexaoautolib.autoconfigure.ConexaoAuthAutoConfiguration.class))
            .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
            .withPropertyValues(
                "conexaoauth.health.enabled=true",
                "conexaoauth.health.timeout=5s",
                "conexaoauth.health.retry-attempts=3"
            )
            .run(context -> {
                assertThat(context).hasSingleBean("metricsCollector");
                assertThat(context).hasSingleBean("healthProperties");
                
                // Test configuration values
                var healthProperties = context.getBean("healthProperties", br.com.conexaoautolib.autoconfigure.properties.HealthProperties.class);
                assertThat(healthProperties.isEnabled()).isTrue();
                assertThat(healthProperties.getTimeout().getSeconds()).isEqualTo(5);
                assertThat(healthProperties.getRetryAttempts()).isEqualTo(3);
            });
    }

    @Test
    void testConfigurationWithoutMeterRegistry() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                br.com.conexaoautolib.autoconfigure.ConexaoAuthAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean("metricsCollector");
                assertThat(context).hasSingleBean("healthProperties");
            });
    }
}