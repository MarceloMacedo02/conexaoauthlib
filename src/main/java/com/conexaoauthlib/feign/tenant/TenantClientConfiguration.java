package com.conexaoauthlib.feign.tenant;

import com.conexaoauthlib.feign.config.FeignClientInterceptor;
import com.conexaoauthlib.feign.error.TenantErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração para Tenant Feign Client.
 *
 * <p>Define interceptors, error decoder e nível de logging
 * específico para o cliente de Tenant.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Configuration
public class TenantClientConfiguration {

    /**
     * Error decoder customizado para tratamento de erros de Tenant.
     *
     * @return TenantErrorDecoder configurado
     */
    @Bean
    public ErrorDecoder tenantErrorDecoder() {
        return new TenantErrorDecoder();
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
    public Logger.Level tenantFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
