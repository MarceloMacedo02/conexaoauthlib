package br.com.conexaoautolib.model.request.factories;

import br.com.conexaoautolib.model.request.TokenRequest;

import java.util.UUID;

/**
 * Factory para criação de dados de teste para TokenRequest.
 * Fornece métodos estáticos para criar instâncias válidas e inválidas.
 */
public class TokenRequestFactory {

    private static final String VALID_CLIENT_ID = "test-client-id";
    private static final String VALID_CLIENT_SECRET = "test-client-secret";
    private static final String VALID_USERNAME = "test@example.com";
    private static final String VALID_PASSWORD = "test-password";
    private static final String VALID_REFRESH_TOKEN = "test-refresh-token";
    private static final String VALID_SCOPE = "read write";
    private static final String VALID_REALM = UUID.randomUUID().toString();

    /**
     * Cria um TokenRequest válido para grant_type client_credentials.
     * 
     * @return TokenRequest válido
     */
    public static TokenRequest createValidClientCredentialsRequest() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .scope(VALID_SCOPE)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest válido para grant_type password.
     * 
     * @return TokenRequest válido
     */
    public static TokenRequest createValidPasswordRequest() {
        return TokenRequest.builder()
                .grantType("password")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .scope(VALID_SCOPE)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest válido para grant_type refresh_token.
     * 
     * @return TokenRequest válido
     */
    public static TokenRequest createValidRefreshTokenRequest() {
        return TokenRequest.builder()
                .grantType("refresh_token")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest com grantType nulo (inválido).
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithNullGrantType() {
        return TokenRequest.builder()
                .grantType(null)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest com clientId nulo (inválido).
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithNullClientId() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(null)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest com clientSecret nulo (inválido).
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithNullClientSecret() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(null)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest com realm nulo (inválido).
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithNullRealm() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm(null)
                .build();
    }

    /**
     * Cria um TokenRequest com grantType inválido.
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithInvalidGrantType() {
        return TokenRequest.builder()
                .grantType("invalid_grant")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest com realm em formato UUID inválido.
     * 
     * @return TokenRequest inválido
     */
    public static TokenRequest createWithInvalidRealmFormat() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm("invalid-uuid-format")
                .build();
    }

    /**
     * Cria um TokenRequest com todos os campos preenchidos (modo completo).
     * 
     * @return TokenRequest completo
     */
    public static TokenRequest createFullTokenRequest() {
        return TokenRequest.builder()
                .grantType("password")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .realm(VALID_REALM)
                .build();
    }

    /**
     * Cria um TokenRequest mínimo (apenas campos obrigatórios).
     * 
     * @return TokenRequest mínimo
     */
    public static TokenRequest createMinimalTokenRequest() {
        return TokenRequest.builder()
                .grantType("client_credentials")
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .realm(VALID_REALM)
                .build();
    }
}