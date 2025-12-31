package com.conexaoauthlib.feign.scope;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.ScopeErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para Scope Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente de Scope.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class ScopeClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros de Scope.
     *
     * @return ScopeErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder scopeErrorDecoder() {
        return new ScopeErrorDecoder();
    }

    /**
     * Interceptor para adicionar headers padrão.
     *
     * @return FeignClientInterceptor configurado
     */
    @Bean
    public FeignClientInterceptor feignClientInterceptor() {
        return new FeignClientInterceptor();
    }

    /**
     * Nível de logging para debug de requisições.
     *
     * @return Logger.Level BASIC
     */
    @Bean
    public Logger.Level scopeFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
