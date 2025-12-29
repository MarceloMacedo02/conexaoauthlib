package com.plataforma.conexao.auth.starter.api;

import com.plataforma.conexao.auth.starter.client.ConexaoAuthClient;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/**
 * Builder para configuração de requisição de autenticação OAuth2.
 *
 * <p>Esta classe implementa o padrão Builder/Fluent Interface para permitir
 * a configuração encadeada de parâmetros de autenticação.
 *
 * <p>Exemplo de uso:
 * <pre>{@code
 * ConexaoAuthRequest request = ConexaoAuth
 *     .clientId("my-client")
 *     .clientSecret("my-secret")
 *     .realm("my-realm")
 *     .username("user@example.com")
 *     .password("UserPass123!")
 *     .grantType("password");
 *
 * TokenResponse token = request.execute();
 * }</pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConexaoAuthRequest {

    private static final Logger log = LoggerFactory.getLogger(ConexaoAuthRequest.class);

    private static ApplicationContext applicationContext;

    private String grantType;
    private String clientId;
    private String clientSecret;
    private String realm;
    private String username;
    private String password;
    private String refreshToken;
    private String scope;
    private String code;
    private String redirectUri;
    private String codeVerifier;

    /**
     * Construtor padrão para uso com builder estático.
     */
    public ConexaoAuthRequest() {
    }

    /**
     * Define o tipo de grant OAuth2.
     *
     * <p>Valores comuns:
     * <ul>
     *   <li>{@code client_credentials} - Autenticação como aplicação</li>
     *   <li>{@code password} - Autenticação com credenciais de usuário</li>
     *   <li>{@code refresh_token} - Atualização de token</li>
     *   <li>{@code authorization_code} - Fluxo de código de autorização</li>
     * </ul>
     *
     * @param grantType Tipo de grant OAuth2
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest grantType(String grantType) {
        this.grantType = grantType;
        return this;
    }

    /**
     * Define o ID do cliente OAuth2.
     *
     * @param clientId ID do cliente
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Define o secret do cliente OAuth2.
     *
     * @param clientSecret Secret do cliente
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * Define o realm no Auth Server.
     *
     * @param realm Nome do realm
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest realm(String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * Define o nome de usuário (e-mail) para autenticação.
     *
     * <p>Usado no fluxo {@code password}.
     *
     * @param username Nome de usuário (e-mail)
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Define a senha do usuário.
     *
     * <p>Usado no fluxo {@code password}.
     *
     * @param password Senha do usuário
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Define o refresh token.
     *
     * <p>Usado no fluxo {@code refresh_token}.
     *
     * @param refreshToken Refresh token
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * Define o escopo (scope) da requisição.
     *
     * <p>Exemplo: {@code "read write"} ou {@code "openid profile email"}.
     *
     * @param scope Escopo solicitado
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest scope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Define o código de autorização.
     *
     * <p>Usado no fluxo {@code authorization_code}.
     *
     * @param code Código de autorização
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Define a URI de redirecionamento.
     *
     * <p>Usado no fluxo {@code authorization_code}.
     *
     * @param redirectUri URI de redirecionamento
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest redirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    /**
     * Define o verificador PKCE.
     *
     * <p>Usado no fluxo {@code authorization_code} com PKCE.
     *
     * @param codeVerifier Verificador PKCE
     * @return Esta instância para encadeamento
     */
    public ConexaoAuthRequest codeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
        return this;
    }

    /**
     * Executa a requisição de autenticação e retorna o token.
     *
     * <p>Este método valida os parâmetros obrigatórios e faz a chamada
     * ao endpoint {@code /oauth2/token} do Auth Server.
     *
     * @return TokenResponse com o token de acesso e informações adicionais
     * @throws IllegalArgumentException se parâmetros obrigatórios não foram fornecidos
     * @throws RuntimeException se ocorrer erro na comunicação com o Auth Server
     */
    public TokenResponse execute() {
        validateRequiredParameters();

        log.info("Solicitando token via OAuth2: grant_type={}, client_id={}",
                grantType, clientId);

        try {
            ConexaoAuthClient client = getConexaoAuthClient();

            TokenResponse response = client.clientCredentials(
                    grantType,
                    clientId,
                    clientSecret,
                    scope,
                    code,
                    redirectUri,
                    refreshToken,
                    username,
                    password,
                    codeVerifier
            );

            log.info("Token obtido com sucesso. Token Type: {}, Expires In: {}s",
                    response.tokenType(), response.expiresIn());

            return response;

        } catch (Exception e) {
            log.error("Erro ao obter token via OAuth2", e);
            throw new RuntimeException("Falha ao obter token de acesso", e);
        }
    }

    /**
     * Valida os parâmetros obrigatórios antes de executar a requisição.
     *
     * @throws IllegalArgumentException se parâmetros obrigatórios não foram fornecidos
     */
    private void validateRequiredParameters() {
        if (grantType == null || grantType.isBlank()) {
            throw new IllegalArgumentException("grantType é obrigatório");
        }

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId é obrigatório");
        }

        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("clientSecret é obrigatório");
        }

        // Validações específicas por tipo de grant
        switch (grantType) {
            case "password":
                if (username == null || username.isBlank()) {
                    throw new IllegalArgumentException("username é obrigatório para grant_type=password");
                }
                if (password == null || password.isBlank()) {
                    throw new IllegalArgumentException("password é obrigatório para grant_type=password");
                }
                break;

            case "refresh_token":
                if (refreshToken == null || refreshToken.isBlank()) {
                    throw new IllegalArgumentException("refreshToken é obrigatório para grant_type=refresh_token");
                }
                break;

            case "authorization_code":
                if (code == null || code.isBlank()) {
                    throw new IllegalArgumentException("code é obrigatório para grant_type=authorization_code");
                }
                if (redirectUri == null || redirectUri.isBlank()) {
                    throw new IllegalArgumentException("redirectUri é obrigatório para grant_type=authorization_code");
                }
                break;

            default:
                // client_credentials não requer parâmetros adicionais
                break;
        }
    }

    /**
     * Obtém o bean ConexaoAuthClient do contexto Spring.
     *
     * @return Instância de ConexaoAuthClient
     * @throws IllegalStateException se não for possível obter o bean
     */
    private ConexaoAuthClient getConexaoAuthClient() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "ApplicationContext não configurado. Certifique-se de que a biblioteca " +
                    "Conexão Auth foi configurada corretamente no seu projeto Spring Boot."
            );
        }

        try {
            return applicationContext.getBean(ConexaoAuthClient.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não foi possível obter o bean ConexaoAuthClient do contexto Spring. " +
                    "Verifique se a biblioteca foi configurada corretamente.",
                    e
            );
        }
    }

    /**
     * Configura o ApplicationContext para permitir a injeção de beans.
     *
     * <p>Este método é chamado internamente pela Auto-Configuration.
     *
     * @param context Contexto da aplicação Spring
     */
    static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
}
