package com.conexaoauthlib.feign.user;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.UserErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para User Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente de User.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class UserClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros de User.
     *
     * @return UserErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder userErrorDecoder() {
        return new UserErrorDecoder();
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
    public Logger.Level userFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
