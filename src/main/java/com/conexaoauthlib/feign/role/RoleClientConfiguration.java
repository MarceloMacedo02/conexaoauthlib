package com.conexaoauthlib.feign.role;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.RoleErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para Role Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente de Role.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class RoleClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros de Role.
     *
     * @return RoleErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder roleErrorDecoder() {
        return new RoleErrorDecoder();
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
    public Logger.Level roleFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
