package com.plataforma.conexao.auth.starter.config;

import com.plataforma.conexao.auth.starter.cache.JwksCache;
import com.plataforma.conexao.auth.starter.client.ConexaoAuthClient;
import com.plataforma.conexao.auth.starter.client.JwksClient;
import com.plataforma.conexao.auth.starter.decoder.ConexaoAuthErrorDecoder;
import com.plataforma.conexao.auth.starter.properties.ConexaoAuthProperties;
import com.plataforma.conexao.auth.starter.service.ConexaoAuthService;
import com.plataforma.conexao.auth.starter.service.ConexaoAuthServiceImpl;
import com.plataforma.conexao.auth.starter.service.JwtTokenValidator;
import com.plataforma.conexao.auth.starter.service.TokenValidator;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Auto-Configuration do Conexão Auth Starter SDK.
 * Registra automaticamente beans se a propriedade conexao.auth.enabled=true.
 *
 * <p>Esta configuração cria beans para:
 * <ul>
 *   <li>{@link ConexaoAuthClient} - Cliente Feign para API principal</li>
 *   <li>{@link JwksClient} - Cliente Feign para JWKS</li>
 *   <li>{@link JwksCache} - Cache de chaves públicas</li>
 *   <li>{@link TokenValidator} - Validador de tokens JWT (completo)</li>
 *   <li>{@link ConexaoAuthService} - Serviço de autenticação (completo)</li>
 *   <li>{@link ConexaoAuthErrorDecoder} - Decoder de erros customizado</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "conexao.auth", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(ConexaoAuthProperties.class)
@Import(FeignConfiguration.class)
public class ConexaoAuthAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ConexaoAuthAutoConfiguration.class);

    private final ConexaoAuthProperties properties;

    public ConexaoAuthAutoConfiguration(ConexaoAuthProperties properties) {
        this.properties = properties;
    }

    /**
     * Cria bean de Feign Client para comunicação com Auth Server.
     *
     * <p>Este bean cria uma implementação real do {@link ConexaoAuthClient}
     * usando a biblioteca Feign para comunicação HTTP declarativa.
     *
     * <p>Configurações:
     * <ul>
     *   <li>URL base: configurada em {@code conexao.auth.baseUrl}</li>
     *   <li>HTTP Client: OkHttp (melhor performance)</li>
     *   <li>Encoder/Decoder: Jackson</li>
     *   <li>Error Decoder: Customizado para mapear códigos HTTP</li>
     * </ul>
     *
     * @param errorDecoder Decoder de erros customizado
     * @return Instância de ConexaoAuthClient
     */
    @Bean
    public ConexaoAuthClient conexaoAuthClient(ConexaoAuthErrorDecoder errorDecoder) {
        String baseUrl = properties.baseUrl();

        log.info("Configurando ConexaoAuthClient para URL: {}", baseUrl);

        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder)
                .target(ConexaoAuthClient.class, baseUrl);
    }

    /**
     * Cria bean de Feign Client para obter chaves JWKS.
     *
     * <p>Este bean cria uma implementação real do {@link JwksClient}
     * usando a biblioteca Feign para buscar chaves públicas do Auth Server.
     *
     * <p>Configurações:
     * <ul>
     *   <li>URL base: configurada em {@code conexao.auth.baseUrl}</li>
     *   <li>HTTP Client: OkHttp</li>
     *   <li>Encoder/Decoder: Jackson</li>
     * </ul>
     *
     * @return Instância de JwksClient
     */
    @Bean
    public JwksClient jwksClient() {
        String baseUrl = properties.baseUrl();

        log.info("Configurando JwksClient para URL: {}", baseUrl);

        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(JwksClient.class, baseUrl);
    }

    /**
     * Cria bean de JWKS Cache com TTL configurável.
     *
     * <p>Este cache armazena chaves públicas RSA para evitar requisições
     * frequentes ao endpoint JWKS.
     *
     * @param jwksClient Feign Client para JWKS
     * @return Instância de JwksCache
     */
    @Bean
    public JwksCache jwksCache(JwksClient jwksClient) {
        long cacheTtl = properties.jwksCacheTtl();

        log.info("Configurando JwksCache com TTL: {}ms", cacheTtl);

        return new JwksCache(jwksClient, cacheTtl);
    }

    /**
     * Cria bean de Token Validator.
     *
     * <p>Este bean cria uma implementação completa usando a biblioteca
     * JJWT para validar tokens JWT assinados com RSA256.
     *
     * <p>Configurações:
     * <ul>
     *   <li>Cache: Usa JwksCache para obter chaves públicas</li>
     *   <li>Algoritmo: RS256 (RSA SHA-256)</li>
     *   <li>Validações: Assinatura, expiração, nbf, emissor</li>
     * </ul>
     *
     * @param jwksCache Cache de chaves públicas
     * @return Instância de TokenValidator
     */
    @Bean
    public TokenValidator tokenValidator(JwksCache jwksCache) {
        log.info("Configurando JwtTokenValidator completo");

        return new JwtTokenValidator(jwksCache);
    }

    /**
     * Cria bean de Auth Service completo.
     *
     * <p>Este bean cria uma implementação completa usando:
     * <ul>
     *   <li>ConexaoAuthClient para comunicação HTTP</li>
     *   <li>TokenValidator para validação de JWT</li>
     *   <li>Client ID/Secret da configuração</li>
     * </ul>
     *
     * <p>Operações disponíveis:
     * <ul>
     *   <li>Registro de usuários</li>
     *   <li>Busca de usuários por CPF</li>
     *   <li>Validação de permissões</li>
     *   <li>Obtenção de tokens via Client Credentials</li>
     * </ul>
     *
     * @param conexaoAuthClient Feign Client para comunicação
     * @param tokenValidator Validador de tokens
     * @return Instância de ConexaoAuthService
     */
    @Bean
    public ConexaoAuthService conexaoAuthService(ConexaoAuthClient conexaoAuthClient,
                                                TokenValidator tokenValidator,
                                                ConexaoAuthProperties properties) {
        String clientId = properties.clientId();
        String clientSecret = properties.clientSecret();
        log.info("Configurando ConexaoAuthService completo (client-id: {})", clientId);
        log.debug("Client secret configurado: {}", clientSecret != null && !clientSecret.isEmpty() ? "***" : "nulo");

        return new ConexaoAuthServiceImpl(
                conexaoAuthClient,
                tokenValidator,
                properties
        );
    }

    /**
     * Cria bean de Error Decoder customizado.
     *
     * <p>Este bean cria uma implementação completa que mapeia códigos de status
     * HTTP para exceções específicas do SDK.
     *
     * @return Instância de ConexaoAuthErrorDecoder
     */
    @Bean
    public ConexaoAuthErrorDecoder conexaoAuthErrorDecoder() {
        log.info("Configurando ConexaoAuthErrorDecoder");
        return new ConexaoAuthErrorDecoder();
    }
}
