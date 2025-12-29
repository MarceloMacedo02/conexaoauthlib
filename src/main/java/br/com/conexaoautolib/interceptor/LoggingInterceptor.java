package br.com.conexaoautolib.interceptor;

import feign.RequestInterceptor;

/**
 * Interceptor Feign para logging básico de requisições.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class LoggingInterceptor implements RequestInterceptor {

    @Override
    public void apply(feign.RequestTemplate template) {
        String correlationId = java.util.UUID.randomUUID().toString().substring(0, 8);
        long startTime = System.currentTimeMillis();

        // Adiciona headers de correlação e timing
        template.header("X-Correlation-ID", correlationId);
        template.header("X-Request-Start-Time", String.valueOf(startTime));
    }
}