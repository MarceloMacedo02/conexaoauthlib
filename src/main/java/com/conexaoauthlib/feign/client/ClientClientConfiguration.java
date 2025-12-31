package com.conexaoauthlib.feign.client;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.ClientErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para Client Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente de Client OAuth2.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class ClientClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros de Client.
     *
     * @return ClientErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder clientErrorDecoder() {
        return new ClientErrorDecoder();
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
    public Logger.Level clientFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
