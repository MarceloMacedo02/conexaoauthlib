package br.com.conexaoautolib.autoconfigure;

import br.com.conexaoautolib.autoconfigure.properties.HealthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para ConexaoAuthAutoConfiguration.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
class ConexaoAuthAutoConfigurationTest {

    @Test
    void testAutoConfigurationEnabledByDefault() {
        new ApplicationContextRunner()
            .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                ConexaoAuthAutoConfiguration.class))
            .run(context -> {
                assertThat(context).hasSingleBean(ConexaoAuthProperties.class);
                assertThat(context).hasSingleBean(HealthProperties.class);
                // MetricsCollector should not be created without MeterRegistry
                assertThat(context).doesNotHaveBean("metricsCollector");
            });
    }

    @Test
    void testAutoConfigurationDisabledWhenPropertyFalse() {
        new ApplicationContextRunner()
            .withPropertyValues("conexaoauth.enabled=false")
            .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                ConexaoAuthAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean(ConexaoAuthProperties.class);
                assertThat(context).doesNotHaveBean(HealthProperties.class);
            });
    }

    @Test
    void testAutoConfigurationEnabledWhenPropertyTrue() {
        new ApplicationContextRunner()
            .withPropertyValues("conexaoauth.enabled=true")
            .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                ConexaoAuthAutoConfiguration.class))
            .run(context -> {
                assertThat(context).hasSingleBean(ConexaoAuthProperties.class);
                assertThat(context).hasSingleBean(HealthProperties.class);
                // MetricsCollector should not be created without MeterRegistry
                assertThat(context).doesNotHaveBean("metricsCollector");
            });
    }
}