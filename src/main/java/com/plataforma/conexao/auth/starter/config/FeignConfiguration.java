package com.plataforma.conexao.auth.starter.config;

import feign.Client;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Feign Client.
 *
 * <p>Configura OkHttp como cliente HTTP (mais performático que o padrão do Feign)
 * e Jackson para serialização/desserialização JSON.
 *
 * <p>Todos os beans são configurados com @ConditionalOnMissingBean para permitir
 * que a aplicação consumidora possa sobrescrever as configurações padrão se necessário.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Configuration
public class FeignConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FeignConfiguration.class);

    /**
     * Configura OkHttp como cliente HTTP para o Feign.
     *
     * <p>OkHttp é mais performático que o cliente HTTP padrão do Feign
     * (Apache HttpClient) devido ao suporte a HTTP/2, connection pooling eficiente
     * e compressão automática.
     *
     * <p>Este bean só é criado se a aplicação não fornecer um bean do tipo feign.Client.
     * Isso permite que a aplicação consumidora possa customizar o cliente HTTP se necessário.
     *
     * @return Cliente HTTP OkHttp
     */
    @Bean
    @ConditionalOnMissingBean(feign.Client.class)
    public Client feignClient() {
        log.info("Configurando OkHttp Client para Feign");
        return new OkHttpClient();
    }

    /**
     * Configura Jackson como encoder para JSON.
     *
     * <p>Jackson é o padrão do ecossistema Spring e é altamente configurável.
     * Este bean só é criado se a aplicação não fornecer um bean do tipo Encoder.
     *
     * @return Encoder Jackson
     */
    @Bean
    @ConditionalOnMissingBean(Encoder.class)
    public Encoder feignEncoder() {
        log.info("Configurando Jackson Encoder para Feign");
        return new JacksonEncoder();
    }

    /**
     * Configura Jackson como decoder para JSON.
     *
     * <p>Este bean só é criado se a aplicação não fornecer um bean do tipo Decoder.
     *
     * @return Decoder Jackson
     */
    @Bean
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder feignDecoder() {
        log.info("Configurando Jackson Decoder para Feign");
        return new JacksonDecoder();
    }
}
