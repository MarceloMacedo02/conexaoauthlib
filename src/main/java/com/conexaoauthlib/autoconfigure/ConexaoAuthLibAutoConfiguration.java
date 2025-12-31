package com.conexaoauthlib.autoconfigure;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração automática da biblioteca ConexãoAuthLib.
 * Permite carregamento via spring.factories em projetos dependentes.
 */
@Configuration
@EnableAutoConfiguration
public class ConexaoAuthLibAutoConfiguration {
    // Auto-configuration empty for now - clients configured individually
}
