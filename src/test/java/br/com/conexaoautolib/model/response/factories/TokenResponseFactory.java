package br.com.conexaoautolib.model.response.factories;

import br.com.conexaoautolib.model.response.TokenResponse;

import java.time.LocalDateTime;

/**
 * Factory para criação de dados de teste para TokenResponse.
 * Fornece métodos estáticos para criar instâncias válidas e inválidas.
 */
public class TokenResponseFactory {

    private static final String VALID_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private static final String VALID_REFRESH_TOKEN = "refresh-token-12345";
    private static final String VALID_SCOPE = "read write";
    private static final Long DEFAULT_EXPIRES_IN = 3600L;
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";

    /**
     * Cria um TokenResponse válido completo.
     * 
     * @return TokenResponse válido completo
     */
    public static TokenResponse createValidTokenResponse() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(DEFAULT_EXPIRES_IN)
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse mínimo (apenas campos obrigatórios).
     * 
     * @return TokenResponse mínimo
     */
    public static TokenResponse createMinimalTokenResponse() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(DEFAULT_EXPIRES_IN)
                .build();
    }

    /**
     * Cria um TokenResponse expirado.
     * 
     * @return TokenResponse expirado
     */
    public static TokenResponse createExpiredTokenResponse() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(-1L) // Expirado
                .build();
    }

    /**
     * Cria um TokenResponse com accessToken nulo (inválido).
     * 
     * @return TokenResponse inválido
     */
    public static TokenResponse createWithNullAccessToken() {
        return TokenResponse.builder()
                .accessToken(null)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(DEFAULT_EXPIRES_IN)
                .build();
    }

    /**
     * Cria um TokenResponse com expiresIn nulo.
     * 
     * @return TokenResponse sem expiração
     */
    public static TokenResponse createWithNullExpiresIn() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(null)
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse sem refreshToken.
     * 
     * @return TokenResponse sem refresh token
     */
    public static TokenResponse createWithoutRefreshToken() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(DEFAULT_EXPIRES_IN)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse com tempo de expiração longo.
     * 
     * @return TokenResponse com expiração longa
     */
    public static TokenResponse createLongLivedTokenResponse() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(86400L) // 24 horas
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse com tempo de expiração curto.
     * 
     * @return TokenResponse com expiração curta
     */
    public static TokenResponse createShortLivedTokenResponse() {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(300L) // 5 minutos
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse para teste de expiração futura.
     * 
     * @param minutesFromNow Minutos a partir de agora para expirar
     * @return TokenResponse que expira em X minutos
     */
    public static TokenResponse createTokenExpiringIn(int minutesFromNow) {
        long expiresIn = (long) minutesFromNow * 60;
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN + "-" + minutesFromNow)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(expiresIn)
                .refreshToken(VALID_REFRESH_TOKEN + "-" + minutesFromNow)
                .scope(VALID_SCOPE)
                .build();
    }

    /**
     * Cria um TokenResponse com scopes específicos.
     * 
     * @param scopes Escopos a serem incluídos
     * @return TokenResponse com scopes personalizados
     */
    public static TokenResponse createTokenWithScopes(String scopes) {
        return TokenResponse.builder()
                .accessToken(VALID_ACCESS_TOKEN)
                .tokenType(DEFAULT_TOKEN_TYPE)
                .expiresIn(DEFAULT_EXPIRES_IN)
                .refreshToken(VALID_REFRESH_TOKEN)
                .scope(scopes)
                .build();
    }
}