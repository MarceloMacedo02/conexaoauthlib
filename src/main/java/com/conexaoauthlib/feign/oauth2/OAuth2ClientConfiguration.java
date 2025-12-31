package com.conexaoauthlib.feign.oauth2;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.OAuth2ErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para OAuth2 Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente OAuth2.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class OAuth2ClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros OAuth2.
     *
     * @return OAuth2ErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder oauth2ErrorDecoder() {
        return new OAuth2ErrorDecoder();
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
    public Logger.Level oauth2FeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
