package com.conexaoauthlib.feign.error;

import com.conexaoauthlib.dto.oauth2.ErrorResponseDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import com.conexaoauthlib.exception.InvalidClientException;
import com.conexaoauthlib.exception.InvalidGrantException;
import com.conexaoauthlib.exception.OAuth2Exception;
import com.conexaoauthlib.exception.ServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Error decoder customizado para tratamento de erros OAuth2.
 *
 * <p>Converte respostas de erro HTTP em exceções específicas do domínio,
 * facilitando o tratamento de erros pelos consumidores da biblioteca.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class OAuth2ErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = Logger.getLogger(OAuth2ErrorDecoder.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        try {
            String body = readBody(response);
            ErrorResponseDTO error = parseErrorResponse(body);
            return mapToException(status, error, body);

        } catch (IOException e) {
            LOGGER.severe("Failed to parse OAuth2 error response: " + e.getMessage());
            return new ServerException("Failed to process error response", e);
        }
    }

    private String readBody(Response response) throws IOException {
        if (response.body() == null) {
            return "";
        }
        InputStream inputStream = response.body().asInputStream();
        byte[] bytes = inputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private ErrorResponseDTO parseErrorResponse(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, ErrorResponseDTO.class);
        } catch (IOException e) {
            LOGGER.fine("Failed to parse error response as ErrorResponseDTO: " + body);
            return null;
        }
    }

    private OAuth2Exception mapToException(int status, ErrorResponseDTO error, String rawBody) {
        String errorCode = getErrorField(error, "error", "unknown_error");
        String errorDesc = getErrorField(error, "errorDescription", getDefaultMessageForStatus(status));

        return switch (status) {
            case 400 -> {
                LOGGER.warning("OAuth2 invalid grant: " + errorCode + " - " + errorDesc);
                yield new InvalidGrantException(errorDesc, errorCode);
            }
            case 401 -> {
                LOGGER.warning("OAuth2 invalid client: " + errorCode + " - " + errorDesc);
                yield new InvalidClientException(errorDesc, errorCode);
            }
            case 503 -> {
                LOGGER.severe("OAuth2 circuit breaker open: " + errorDesc);
                yield new CircuitBreakerOpenException(errorDesc);
            }
            case 500, 502, 504 -> {
                LOGGER.severe("OAuth2 server error: " + errorCode + " - " + errorDesc);
                yield new ServerException(errorDesc);
            }
            default -> {
                LOGGER.warning("OAuth2 unknown error: " + errorCode + " - " + errorDesc + " (HTTP " + status + ")");
                yield new OAuth2Exception(errorDesc, errorCode, status);
            }
        };
    }

    private String getErrorField(ErrorResponseDTO error, String fieldName, String defaultValue) {
        if (error == null) {
            return defaultValue;
        }
        try {
            Field field = ErrorResponseDTO.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(error);
            return value != null ? value.toString() : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getDefaultMessageForStatus(int status) {
        return switch (status) {
            case 400 -> "Invalid grant or request parameters";
            case 401 -> "Invalid client credentials";
            case 403 -> "Access forbidden";
            case 404 -> "Resource not found";
            case 500 -> "Internal server error";
            case 502 -> "Bad gateway";
            case 503 -> "Service unavailable";
            case 504 -> "Gateway timeout";
            default -> "Unknown error";
        };
    }
}
