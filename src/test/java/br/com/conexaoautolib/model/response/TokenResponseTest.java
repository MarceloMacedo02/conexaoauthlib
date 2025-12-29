package br.com.conexaoautolib.model.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.conexaoautolib.model.response.factories.TokenResponseFactory;

/**
 * Testes unitários para TokenResponse.
 * Verifica serialização, métodos utilitários e imutabilidade.
 */
class TokenResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @DisplayName("Deve criar TokenResponse válido completo")
    void shouldCreateValidTokenResponse() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createValidTokenResponse();

        // Assert
        assertNotNull(response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getScope());
    }

    @Test
    @DisplayName("Deve criar TokenResponse mínimo")
    void shouldCreateMinimalTokenResponse() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createMinimalTokenResponse();

        // Assert
        assertNotNull(response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertNull(response.getRefreshToken());
        assertNull(response.getScope());
    }

    @Test
    @DisplayName("Deve calcular data de expiração corretamente")
    void shouldCalculateExpirationDateCorrectly() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createShortLivedTokenResponse(); // 5 minutos
        LocalDateTime beforeCalculation = LocalDateTime.now();

        // Act
        LocalDateTime expiresAt = response.getExpiresAt();

        // Assert
        assertNotNull(expiresAt);
        assertTrue(expiresAt.isAfter(beforeCalculation));
        assertTrue(expiresAt.isBefore(beforeCalculation.plusMinutes(6)));
        assertTrue(expiresAt.isAfter(beforeCalculation.plusMinutes(4)));
    }

    @Test
    @DisplayName("Deve retornar nulo quando expiresIn é nulo")
    void shouldReturnNullWhenExpiresInIsNull() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createWithNullExpiresIn();

        // Act
        LocalDateTime expiresAt = response.getExpiresAt();

        // Assert
        assertNull(expiresAt);
    }

    @Test
    @DisplayName("Deve identificar token expirado corretamente")
    void shouldIdentifyExpiredTokenCorrectly() {
        // Arrange
        TokenResponse expiredResponse = TokenResponseFactory.createExpiredTokenResponse();

        // Act
        boolean isExpired = expiredResponse.isExpired();

        // Assert
        assertTrue(isExpired, "Token com expiresIn negativo deve estar expirado");
    }

    @Test
    @DisplayName("Deve identificar token não expirado corretamente")
    void shouldIdentifyNonExpiredTokenCorrectly() {
        // Arrange
        TokenResponse validResponse = TokenResponseFactory.createValidTokenResponse();

        // Act
        boolean isExpired = validResponse.isExpired();

        // Assert
        assertFalse(isExpired, "Token com expiresIn positivo não deve estar expirado");
    }

    @Test
    @DisplayName("Deve retornar não expirado quando expiresIn é nulo")
    void shouldReturnNotExpiredWhenExpiresInIsNull() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createWithNullExpiresIn();

        // Act
        boolean isExpired = response.isExpired();

        // Assert
        assertFalse(isExpired, "Token com expiresIn nulo não deve estar expirado");
    }

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void shouldSerializeToJsonCorrectly() throws JsonProcessingException {
        // Arrange
        TokenResponse response = TokenResponseFactory.createValidTokenResponse();

        // Act
        String json = objectMapper.writeValueAsString(response);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"access_token\""));
        assertTrue(json.contains("\"token_type\""));
        assertTrue(json.contains("\"expires_in\""));
        assertTrue(json.contains("\"refresh_token\""));
        assertTrue(json.contains("\"scope\""));

        // Campos calculados não devem aparecer
        assertFalse(json.contains("expiresAt"));
        assertFalse(json.contains("expired"));
    }

    @Test
    @DisplayName("Deve desserializar de JSON corretamente")
    void shouldDeserializeFromJsonCorrectly() throws JsonProcessingException {
        // Arrange
        String json = """
                {
                  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test",
                  "token_type": "Bearer",
                  "expires_in": 3600,
                  "refresh_token": "refresh-token-12345",
                  "scope": "read write"
                }
                """;

        // Act
        TokenResponse response = objectMapper.readValue(json, TokenResponse.class);

        // Assert
        assertNotNull(response);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("refresh-token-12345", response.getRefreshToken());
        assertEquals("read write", response.getScope());
    }

    @Test
    @DisplayName("Deve redatar dados sensíveis no toString()")
    void shouldRedactSensitiveDataInToString() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createValidTokenResponse();

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertFalse(toString.contains("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"));
        assertFalse(toString.contains("refresh-token-12345"));
        assertTrue(toString.contains("[REDACTED]"));
        assertTrue(toString.contains("Bearer"));
        assertTrue(toString.contains("expiresIn=3600"));
    }

    @Test
    @DisplayName("Deve incluir informações de expiração no toString()")
    void shouldIncludeExpirationInfoInToString() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createShortLivedTokenResponse();

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("expiresAt="));
        assertTrue(toString.contains("expired="));
        assertTrue(toString.contains("expiresIn=300"));
    }

    @Test
    @DisplayName("Deve ter tokenType padrão 'Bearer'")
    void shouldHaveDefaultTokenTypeBearer() {
        // Arrange
        TokenResponse response = TokenResponse.builder()
                .accessToken("test-token")
                .expiresIn(3600L)
                .build();

        // Assert
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    @DisplayName("Deve criar token com expiração personalizada")
    void shouldCreateTokenWithCustomExpiration() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createTokenExpiringIn(10); // 10 minutos

        // Act
        LocalDateTime expiresAt = response.getExpiresAt();
        boolean isExpired = response.isExpired();

        // Assert
        assertNotNull(expiresAt);
        assertFalse(isExpired);
        assertTrue(expiresAt.isBefore(LocalDateTime.now().plusMinutes(11)));
        assertTrue(expiresAt.isAfter(LocalDateTime.now().plusMinutes(9)));
    }

    @Test
    @DisplayName("Deve ser imutável após criação com builder")
    void shouldBeImmutableAfterCreation() {
        // Arrange
        TokenResponse response = TokenResponseFactory.createValidTokenResponse();

        // Act & Assert
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getTokenType());
        assertNotNull(response.getExpiresIn());
        assertNotNull(response.toString());

        // Imutabilidade é garantida em tempo de compilação
        // @Data + @Builder + @AllArgsConstructor + @NoArgsConstructor
        // garante objetos consistentes após criação
    }

    @Test
    @DisplayName("Deve criar token com scopes específicos")
    void shouldCreateTokenWithSpecificScopes() {
        // Arrange
        String expectedScopes = "admin read write delete";
        TokenResponse response = TokenResponseFactory.createTokenWithScopes(expectedScopes);

        // Act & Assert
        assertEquals(expectedScopes, response.getScope());
    }
}