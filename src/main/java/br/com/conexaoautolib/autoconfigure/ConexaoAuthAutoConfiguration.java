package br.com.conexaoautolib.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.conexaoautolib.autoconfigure.properties.HealthProperties;
import br.com.conexaoautolib.health.ConexaoAuthHealthIndicator;
import br.com.conexaoautolib.health.MetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Classe de autoconfiguração principal para ConexãoAuthLib.
 * 
 * Esta classe é responsável por configurar todos os beans necessários
 * para o funcionamento da biblioteca quando presente no classpath.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ConexaoAuthProperties.class)
@ConditionalOnProperty(prefix = "conexaoauth", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConexaoAuthAutoConfiguration {

    /**
     * Configura o bean de HealthProperties para health checks.
     */
    @Bean
    @ConditionalOnMissingBean
    public HealthProperties healthProperties() {
        return new HealthProperties();
    }

    /**
     * Configura o MetricsCollector quando Micrometer está disponível.
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry.class)
    public MetricsCollector metricsCollector(MeterRegistry meterRegistry) {
        return new MetricsCollector(meterRegistry);
    }

    /**
     * Configura o HealthIndicator para ConexãoAuthLib.
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
    @ConditionalOnEnabledHealthIndicator("conexaoauth")
    @ConditionalOnMissingBean
    public ConexaoAuthHealthIndicator conexaoAuthHealthIndicator(
            HealthProperties healthProperties,
            ConexaoAuthProperties conexaoAuthProperties) {
        return new ConexaoAuthHealthIndicator(healthProperties, conexaoAuthProperties.getServer());
    }

    // TODO: Implementar configuração de beans nas próximas stories
    // - TokenStorage (@ConditionalOnMissingBean)
    // - ConexaoAuthFeignConfig (@ConditionalOnMissingBean)
    // - TokenInjectionInterceptor (@ConditionalOnMissingBean)
    // - LoggingInterceptor (@ConditionalOnMissingBean)
    // - ConexaoAuthErrorDecoder (@ConditionalOnMissingBean)
}