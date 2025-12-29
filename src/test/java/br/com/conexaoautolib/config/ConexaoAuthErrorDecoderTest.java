package br.com.conexaoautolib.config;

import br.com.conexaoautolib.exception.ConexaoAuthException;
import br.com.conexaoautolib.exception.InvalidCredentialsException;
import br.com.conexaoautolib.exception.UserNotFoundException;
import feign.Request;
import feign.Response;
import feign.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConexaoAuthErrorDecoder Tests")
class ConexaoAuthErrorDecoderTest {
    
    private ConexaoAuthErrorDecoder errorDecoder;
    
    @BeforeEach
    void setUp() {
        errorDecoder = new ConexaoAuthErrorDecoder();
    }
    
    @Test
    @DisplayName("Deve mapear HTTP 401 para InvalidCredentialsException")
    void shouldMap401ToInvalidCredentialsException() {
        // Arrange
        Response response = createResponse(401, "Unauthorized", "{\"message\":\"Invalid credentials\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(InvalidCredentialsException.class, exception);
        assertTrue(exception.getMessage().contains("Credenciais inválidas"));
        assertTrue(exception.getMessage().contains("Invalid credentials"));
    }
    
    @Test
    @DisplayName("Deve mapear HTTP 404 para UserNotFoundException quando usuário")
    void shouldMap404ToUserNotFoundExceptionForUser() {
        // Arrange
        Response response = createResponse(404, "Not Found", "{\"message\":\"User not found\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(UserNotFoundException.class, exception);
        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }
    
    @Test
    @DisplayName("Deve mapear HTTP 404 para UserNotFoundException quando realm")
    void shouldMap404ToUserNotFoundExceptionForRealm() {
        // Arrange
        Response response = createResponse(404, "Not Found", "{\"message\":\"Realm not found\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(UserNotFoundException.class, exception);
        assertTrue(exception.getMessage().contains("Realm não encontrado"));
    }
    
    @Test
    @DisplayName("Deve mapear HTTP 500 para ConexaoAuthException")
    void shouldMap500ToConexaoAuthException() {
        // Arrange
        Response response = createResponse(500, "Internal Server Error", "{\"message\":\"Database connection failed\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(ConexaoAuthException.class, exception);
        assertTrue(exception.getMessage().contains("Erro interno do servidor (500)"));
        assertTrue(exception.getMessage().contains("Database connection failed"));
    }
    
    @Test
    @DisplayName("Deve mapear HTTP 400 para ConexaoAuthException")
    void shouldMap400ToConexaoAuthException() {
        // Arrange
        Response response = createResponse(400, "Bad Request", "{\"message\":\"Invalid request format\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(ConexaoAuthException.class, exception);
        assertTrue(exception.getMessage().contains("Erro de cliente (400)"));
        assertTrue(exception.getMessage().contains("Invalid request format"));
    }
    
    @Test
    @DisplayName("Deve extrair mensagem de campo 'message'")
    void shouldExtractMessageFromMessageField() {
        // Arrange
        Response response = createResponse(401, "Unauthorized", "{\"message\":\"Custom error message\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertTrue(exception.getMessage().contains("Custom error message"));
    }
    
    @Test
    @DisplayName("Deve extrair mensagem de campo 'error_description'")
    void shouldExtractMessageFromErrorDescriptionField() {
        // Arrange
        Response response = createResponse(401, "Unauthorized", "{\"error_description\":\"OAuth error\"}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertTrue(exception.getMessage().contains("OAuth error"));
    }
    
    @Test
    @DisplayName("Deve extrair mensagem de campo 'error' quando é objeto")
    void shouldExtractMessageFromErrorObjectField() {
        // Arrange
        Response response = createResponse(401, "Unauthorized", "{\"error\":{\"message\":\"Nested error message\"}}");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertTrue(exception.getMessage().contains("Nested error message"));
    }
    
    @Test
    @DisplayName("Deve lidar com resposta sem corpo")
    void shouldHandleResponseWithoutBody() {
        // Arrange
        Response response = Response.builder()
            .status(500)
            .reason("Internal Server Error")
            .request(Request.create(Request.HttpMethod.GET, "http://test.com", Collections.emptyMap(), null, Util.UTF_8))
            .build();
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(ConexaoAuthException.class, exception);
        assertTrue(exception.getMessage().contains("Sem corpo na resposta"));
    }
    
    @Test
    @DisplayName("Deve lidar com corpo não JSON")
    void shouldHandleNonJsonResponseBody() {
        // Arrange
        Response response = createResponse(500, "Internal Server Error", "Plain text error message");
        
        // Act
        Exception exception = errorDecoder.decode("testMethod", response);
        
        // Assert
        assertInstanceOf(ConexaoAuthException.class, exception);
        assertTrue(exception.getMessage().contains("Plain text error message"));
    }
    
    private Response createResponse(int status, String reason, String body) {
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        
        return Response.builder()
            .status(status)
            .reason(reason)
            .headers(headers)
            .body(body, StandardCharsets.UTF_8)
            .request(Request.create(Request.HttpMethod.GET, "http://test.com", Collections.emptyMap(), null, Util.UTF_8))
            .build();
    }
}