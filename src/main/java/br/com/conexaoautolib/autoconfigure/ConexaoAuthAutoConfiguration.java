package br.com.conexaoautolib.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.conexaoautolib.autoconfigure.properties.ClientProperties;
import br.com.conexaoautolib.autoconfigure.properties.ServerProperties;
import br.com.conexaoautolib.client.ConexaoAuthOAuth2Client;
import br.com.conexaoautolib.config.ConexaoAuthErrorDecoder;
import br.com.conexaoautolib.health.ConexaoAuthHealthIndicator;
import br.com.conexaoautolib.health.MetricsCollector;
import br.com.conexaoautolib.interceptor.TokenInjectionInterceptor;
import br.com.conexaoautolib.storage.InMemoryTokenStorage;
import br.com.conexaoautolib.storage.TokenStorage;
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
@EnableFeignClients(basePackages = "br.com.conexaoautolib.client")
@ConditionalOnProperty(prefix = "conexaoauth", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConexaoAuthAutoConfiguration {



    /**
     * Configura o bean ServerProperties para URLs do servidor.
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerProperties serverProperties() {
        return new ServerProperties();
    }

    /**
     * Configura o bean TokenStorage padrão (InMemoryTokenStorage).
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenStorage tokenStorage() {
        return new InMemoryTokenStorage();
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
            ConexaoAuthProperties conexaoAuthProperties,
            TokenStorage tokenStorage) {
        return new ConexaoAuthHealthIndicator(conexaoAuthProperties.getHealth(), conexaoAuthProperties.getServer(), tokenStorage);
    }

    /**
     * Configura o bean TokenInjectionInterceptor para injeção automática de tokens.
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenInjectionInterceptor tokenInjectionInterceptor(
            TokenStorage tokenStorage,
            ConexaoAuthProperties conexaoAuthProperties) {
        // Cria ClientProperties padrão se não existir
        ClientProperties clientProperties = new ClientProperties();
        clientProperties.setClientId("default-client");
        clientProperties.setRealm("default-realm");
        return new TokenInjectionInterceptor(tokenStorage, clientProperties);
    }


}
