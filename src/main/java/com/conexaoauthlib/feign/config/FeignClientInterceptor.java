package com.conexaoauthlib.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Interceptor para adicionar headers padrão em todas as requisições Feign.
 *
 * <p>Este interceptor garante que todas as requisições tenham os headers
 * necessários para comunicação com o servidor OAuth2.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(FeignClientInterceptor.class.getName());

    @Override
    public void apply(RequestTemplate template) {
        // Content-Type padrão para OAuth2 (pode ser sobrescrito)
        if (template.headers().get(HttpHeaders.CONTENT_TYPE) == null) {
            template.header(HttpHeaders.CONTENT_TYPE,
                "application/x-www-form-urlencoded");
        }

        // Accept padrão
        if (template.headers().get(HttpHeaders.ACCEPT) == null) {
            template.header(HttpHeaders.ACCEPT, "application/json");
        }

        LOGGER.fine("Applied Feign interceptor for URL: " + template.url());
    }
}
