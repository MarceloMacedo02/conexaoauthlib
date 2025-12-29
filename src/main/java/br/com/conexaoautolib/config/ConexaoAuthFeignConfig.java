package br.com.conexaoautolib.config;

import br.com.conexaoautolib.autoconfigure.ConexaoAuthProperties;
import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração base do OpenFeign para ConexãoAuthLib.
 * 
 * Fornece configuração centralizada para clientes Feign com timeout,
 * retry, logging e tratamento de erros personalizado.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConexaoAuthFeignConfig {
    
    private final ConexaoAuthProperties properties;
    
    /**
     * Configuração de timeout para requisições Feign.
     * 
     * @return Bean Request.Options com configurações de timeout
     */
    @Bean
    public Request.Options feignRequestOptions() {
        log.debug("Configurando Feign Request.Options com connect timeout: {}ms, read timeout: {}ms",
            properties.getTimeout().getConnect().toMillis(),
            properties.getTimeout().getRead().toMillis());
            
        return new Request.Options(
            (int) properties.getTimeout().getConnect().toMillis(),
            (int) properties.getTimeout().getRead().toMillis()
        );
    }
    
    /**
     * Configuração do nível de logging para clientes Feign.
     * 
     * @return Bean Logger.Level configurado
     */
    @Bean
    @ConditionalOnProperty(name = "conexaoauth.logging.enabled", havingValue = "true", matchIfMissing = true)
    public Logger.Level feignLoggerLevel() {
        String logLevel = properties.getLogging().getLevel().toUpperCase();
        Logger.Level level = switch (logLevel) {
            case "NONE" -> Logger.Level.NONE;
            case "BASIC" -> Logger.Level.BASIC;
            case "HEADERS" -> Logger.Level.HEADERS;
            case "FULL" -> Logger.Level.FULL;
            default -> {
                log.warn("Nível de logging Feign desconhecido: {}, usando BASIC", logLevel);
                yield Logger.Level.BASIC;
            }
        };
        
        log.debug("Configurando Feign Logger.Level: {}", level);
        return level;
    }
    
    /**
     * Configuração de retry para requisições Feign.
     * 
     * @return Bean Retryer configurado com backoff exponencial
     */
    @Bean
    @ConditionalOnProperty(name = "conexaoauth.retry.enabled", havingValue = "true", matchIfMissing = true)
    public Retryer feignRetryer() {
        ConexaoAuthRetryer retryer = new ConexaoAuthRetryer(
            properties.getRetry().getMaxAttempts(),
            properties.getRetry().getInitialInterval().toMillis(),
            properties.getRetry().getMultiplier(),
            properties.getRetry().getMaxInterval().toMillis()
        );
        
        log.debug("Configurando Feign Retryer com maxAttempts: {}, initialInterval: {}ms, multiplier: {}, maxInterval: {}ms",
            properties.getRetry().getMaxAttempts(),
            properties.getRetry().getInitialInterval().toMillis(),
            properties.getRetry().getMultiplier(),
            properties.getRetry().getMaxInterval().toMillis());
            
        return retryer;
    }
    
    /**
     * Configuração de tratamento de erros para requisições Feign.
     * 
     * @return Bean ErrorDecoder personalizado
     */
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        log.debug("Configurando Feign ErrorDecoder personalizado");
        return new ConexaoAuthErrorDecoder();
    }
}