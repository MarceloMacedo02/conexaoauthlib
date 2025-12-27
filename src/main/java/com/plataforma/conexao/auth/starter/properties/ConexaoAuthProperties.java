package com.plataforma.conexao.auth.starter.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propriedades de configuração do Conexão Auth Starter.
 * Configuráveis via application.yml com prefixo conexao.auth.
 *
 * <p>Exemplo de configuração:
 * <pre>
 * conexao:
 *   auth:
 *     enabled: true
 *     base-url: https://auth.example.com
 *     client-id: meu-client-id
 *     client-secret: meu-client-secret
 *     realm-id: master
 *     connection-timeout: 5000
 *     read-timeout: 10000
 *     jwks-cache-ttl: 300000
 * </pre>
 *
 * <p><b>NOTA:</b> Esta classe usa Java 21 records (sem Lombok) seguindo o padrão do projeto.
 * Spring Boot 3.x suporta configuration properties com records.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "conexao.auth")
public record ConexaoAuthProperties(
    /**
     * Habilita ou desabilita o Starter SDK.
     * <p>Padrão: false</p>
     */
    @NotNull(message = "conexao.auth.enabled é obrigatório")
    Boolean enabled,

    /**
     * URL base do Auth Server.
     * <p>Exemplos: https://auth.example.com ou http://conexao-auth:8080</p>
     */
    @NotBlank(message = "conexao.auth.base-url é obrigatório")
    String baseUrl,

    /**
     * Client ID OAuth2 para autenticação da aplicação.
     */
    @NotBlank(message = "conexao.auth.client-id é obrigatório")
    String clientId,

    /**
     * Client Secret OAuth2 para autenticação da aplicação.
     * <p><b>Atenção:</b> Nunca exponha este valor em logs.</p>
     */
    @NotBlank(message = "conexao.auth.client-secret é obrigatório")
    String clientSecret,

    /**
     * ID do Realm padrão a ser usado nas operações.
     */
    @NotBlank(message = "conexao.auth.realm-id é obrigatório")
    String realmId,

    /**
     * Timeout de conexão em milissegundos.
     * <p>Padrão: 5000ms (5 segundos)</p>
     */
    @Positive(message = "conexao.auth.connection-timeout deve ser positivo")
    Integer connectionTimeout,

    /**
     * Timeout de leitura em milissegundos.
     * <p>Padrão: 10000ms (10 segundos)</p>
     */
    @Positive(message = "conexao.auth.read-timeout deve ser positivo")
    Integer readTimeout,

    /**
     * TTL (Time To Live) do cache JWKS em milissegundos.
     * <p>Padrão: 300000ms (5 minutos)</p>
     */
    @Positive(message = "conexao.auth.jwks-cache-ttl deve ser positivo")
    Long jwksCacheTtl
) {
    /**
     * Construtor padrão com valores padrão.
     * <p>Spring Boot 3.x requer construtor padrão para configuration properties com records.</p>
     */
    public ConexaoAuthProperties {
        if (enabled == null) {
            enabled = false;
        }
        if (connectionTimeout == null) {
            connectionTimeout = 5000;
        }
        if (readTimeout == null) {
            readTimeout = 10000;
        }
        if (jwksCacheTtl == null) {
            jwksCacheTtl = 300000L;
        }
    }
}
