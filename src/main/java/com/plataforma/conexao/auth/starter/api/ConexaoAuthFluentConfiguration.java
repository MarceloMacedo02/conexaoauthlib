package com.plataforma.conexao.auth.starter.api;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuração para a API Fluente do Conexão Auth.
 *
 * <p>Esta classe injeta o ApplicationContext no ConexaoAuthRequest
 * para permitir a recuperação de beans Spring necessários para
 * executar as requisições de autenticação.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Configuration
public class ConexaoAuthFluentConfiguration {

    private final ApplicationContext applicationContext;

    /**
     * Construtor com injeção de dependências.
     *
     * @param applicationContext Contexto da aplicação Spring
     */
    public ConexaoAuthFluentConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Configura o ApplicationContext no ConexaoAuthRequest após
     * a inicialização do contexto Spring.
     */
    @PostConstruct
    public void configureFluentApi() {
        ConexaoAuthRequest.setApplicationContext(applicationContext);
    }
}
