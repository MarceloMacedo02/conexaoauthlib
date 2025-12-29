package br.com.conexaoautolib.autoconfigure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.context.TestPropertySource;

import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes para ConexaoAuthProperties.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
class ConexaoAuthPropertiesTest {

    @Test
    void testDefaultValues() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
            .withPropertyValues("conexaoauth.enabled=true")
            .withPropertyValues("conexaoauth.server.url=http://test.example.com")
            .withPropertyValues("conexaoauth.auth.default-realm=test-realm")
            .withPropertyValues("conexaoauth.timeout.connect=5s")
            .withPropertyValues("conexaoauth.retry.max-attempts=3")
            .withPropertyValues("conexaoauth.logging.enabled=true")
            .withUserConfiguration(ConexaoAuthAutoConfiguration.class);

        runner.run(context -> {
            ConexaoAuthProperties props = context.getBean(ConexaoAuthProperties.class);
            assertThat(props.isEnabled()).isTrue();
            assertThat(props.getServer().getUrl()).isEqualTo("http://test.example.com");
            assertThat(props.getAuth().getDefaultRealm()).isEqualTo("test-realm");
            assertThat(props.getTimeout().getConnect().getSeconds()).isEqualTo(5);
            assertThat(props.getRetry().getMaxAttempts()).isEqualTo(3);
            assertThat(props.getLogging().isEnabled()).isTrue();
        });
    }

    @Test
    void testClientPropertiesMap() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ConexaoAuthAutoConfiguration.class)
            .withPropertyValues("conexaoauth.server.url=http://test-server");

        runner.run(context -> {
            ConexaoAuthProperties props = context.getBean(ConexaoAuthProperties.class);
            assertThat(props.getClients()).isNotNull();
            assertThat(props.getClients()).isEmpty();
        });
    }
}