package com.conexaoauthlib.fluent.oauth2;

import com.conexaoauthlib.dto.oauth2.IntrospectRequestDTO;
import com.conexaoauthlib.dto.oauth2.IntrospectResponseDTO;
import com.conexaoauthlib.dto.oauth2.RevokeRequestDTO;
import com.conexaoauthlib.dto.oauth2.TokenRequestDTO;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.feign.oauth2.OAuth2Client;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point fluente para operações OAuth2.
 * Fornece uma API intuitiva para obtenção e gerenciamento de tokens.
 *
 * <h3>Exemplo de uso - Client Credentials:</h3>
 * <pre>{@code
 * TokenResponseDTO token = AuthClient.clientCredentials("client-id", "secret")
 *     .scope("read write")
 *     .execute();
 *
 * String accessToken = token.getAccessToken();
 * }</pre>
 *
 * <h3>Exemplo de uso - Password Grant:</h3>
 * <pre>{@code
 * TokenResponseDTO token = AuthClient.password("user@example.com", "password123")
 *     .clientId("client-id")
 *     .clientSecret("secret")
 *     .scope("read write admin")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de uso - Refresh Token:</h3>
 * <pre>{@code
 * TokenResponseDTO token = AuthClient.refreshToken("refresh-token")
 *     .clientId("client-id")
 *     .clientSecret("secret")
 *     .execute();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class AuthClient {

    private AuthClient() {
        // Utility class - não instanciar
    }

    // ==================== Client Credentials ====================

    /**
     * Inicia fluxo Client Credentials Grant.
     *
     * @param clientId Identificador do cliente
     * @param clientSecret Segredo do cliente
     * @return ClientCredentialsBuilder para configuração adicional
     */
    public static ClientCredentialsBuilder clientCredentials(String clientId, String clientSecret) {
        return new ClientCredentialsBuilder(clientId, clientSecret);
    }

    /**
     * Builder para fluxo Client Credentials Grant.
     */
    public static final class ClientCredentialsBuilder {
        private final String clientId;
        private final String clientSecret;
        private String scope;
        private String tenantId;

        ClientCredentialsBuilder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        /**
         * Define os escopos solicitados.
         *
         * @param scope Escopos separados por espaço
         * @return this builder
         */
        public ClientCredentialsBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public ClientCredentialsBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a requisição e obtém o token.
         *
         * @return TokenResponseDTO com access token
         */
        public TokenResponseDTO execute() {
            TokenRequestDTO request = TokenRequestDTO.builder()
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope(scope)
                .build();

            OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();

            if (tenantId != null) {
                return client.getToken(request, tenantId);
            }
            return client.getToken(request);
        }

        /**
         * Executa a requisição de forma assíncrona.
         *
         * @return CompletableFuture com TokenResponseDTO
         */
        public CompletableFuture<TokenResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== Password Grant ====================

    /**
     * Inicia fluxo Password Grant.
     *
     * @param username Nome de usuário
     * @param password Senha
     * @return PasswordBuilder para configuração adicional
     */
    public static PasswordBuilder password(String username, String password) {
        return new PasswordBuilder(username, password);
    }

    /**
     * Builder para fluxo Password Grant.
     */
    public static final class PasswordBuilder {
        private final String username;
        private final String password;
        private String clientId;
        private String clientSecret;
        private String scope;
        private String tenantId;

        PasswordBuilder(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Define as credenciais do cliente OAuth2.
         * Necessário para validação do cliente.
         *
         * @param clientId ID do cliente
         * @param clientSecret Segredo do cliente
         * @return this builder
         */
        public PasswordBuilder clientCredentials(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Define os escopos solicitados.
         *
         * @param scope Escopos separados por espaço
         * @return this builder
         */
        public PasswordBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public PasswordBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a requisição e obtém o token.
         *
         * @return TokenResponseDTO com access token
         */
        public TokenResponseDTO execute() {
            TokenRequestDTO request = TokenRequestDTO.builder()
                .grantType("password")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .scope(scope)
                .build();

            OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();

            if (tenantId != null) {
                return client.getToken(request, tenantId);
            }
            return client.getToken(request);
        }

        /**
         * Executa a requisição de forma assíncrona.
         *
         * @return CompletableFuture com TokenResponseDTO
         */
        public CompletableFuture<TokenResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== Refresh Token ====================

    /**
     * Inicia fluxo Refresh Token Grant.
     *
     * @param refreshToken Refresh token válido
     * @return RefreshTokenBuilder para configuração adicional
     */
    public static RefreshTokenBuilder refreshToken(String refreshToken) {
        return new RefreshTokenBuilder(refreshToken);
    }

    /**
     * Builder para fluxo Refresh Token Grant.
     */
    public static final class RefreshTokenBuilder {
        private final String refreshToken;
        private String clientId;
        private String clientSecret;
        private String scope;
        private String tenantId;

        RefreshTokenBuilder(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        /**
         * Define as credenciais do cliente OAuth2.
         *
         * @param clientId ID do cliente
         * @param clientSecret Segredo do cliente
         * @return this builder
         */
        public RefreshTokenBuilder clientCredentials(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Define escopos opcionais para o novo token.
         * Se null, mantém os escopos originais.
         *
         * @param scope Escopos separados por espaço
         * @return this builder
         */
        public RefreshTokenBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public RefreshTokenBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a requisição e obtém novo token.
         *
         * @return TokenResponseDTO com novo access token
         */
        public TokenResponseDTO execute() {
            TokenRequestDTO request = TokenRequestDTO.builder()
                .grantType("refresh_token")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(refreshToken)
                .scope(scope)
                .build();

            OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();

            if (tenantId != null) {
                return client.getToken(request, tenantId);
            }
            return client.getToken(request);
        }

        /**
         * Executa a requisição de forma assíncrona.
         *
         * @return CompletableFuture com TokenResponseDTO
         */
        public CompletableFuture<TokenResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== Introspect ====================

    /**
     * Inicia operação de introspecção de token.
     *
     * @param token Token a ser analisado
     * @return IntrospectBuilder para configuração adicional
     */
    public static IntrospectBuilder introspect(String token) {
        return new IntrospectBuilder(token);
    }

    /**
     * Builder para operação de introspecção.
     */
    public static final class IntrospectBuilder {
        private final String token;
        private String tenantId;

        IntrospectBuilder(String token) {
            this.token = token;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public IntrospectBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a introspecção.
         *
         * @return IntrospectResponseDTO com status e claims do token
         */
        public IntrospectResponseDTO execute() {
            IntrospectRequestDTO request = IntrospectRequestDTO.builder()
                .token(token)
                .build();

            OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();
            return client.introspect(request, tenantId);
        }

        /**
         * Executa a introspecção de forma assíncrona.
         *
         * @return CompletableFuture com IntrospectResponseDTO
         */
        public CompletableFuture<IntrospectResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== Revoke ====================

    /**
     * Inicia operação de revogação de token.
     *
     * @param token Token a ser revogado
     * @return RevokeBuilder para configuração adicional
     */
    public static RevokeBuilder revoke(String token) {
        return new RevokeBuilder(token);
    }

    /**
     * Builder para operação de revogação.
     */
    public static final class RevokeBuilder {
        private final String token;
        private String tenantId;

        RevokeBuilder(String token) {
            this.token = token;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public RevokeBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a revogação.
         */
        public void execute() {
            RevokeRequestDTO request = RevokeRequestDTO.builder()
                .token(token)
                .build();

            OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();
            client.revoke(request, tenantId);
        }

        /**
         * Executa a revogação de forma assíncrona.
         *
         * @return CompletableFuture void
         */
        public CompletableFuture<Void> executeAsync() {
            return CompletableFuture.runAsync(this::execute);
        }
    }
}
