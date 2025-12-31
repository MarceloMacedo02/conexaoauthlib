package com.conexaoauthlib.feign.error;

import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import com.conexaoauthlib.exception.ConflictException;
import com.conexaoauthlib.exception.ResourceNotFoundException;
import com.conexaoauthlib.exception.ServerException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Error decoder customizado para Tenant Client.
 *
 * <p>Converte respostas de erro HTTP em exceções específicas do domínio,
 * facilitando o tratamento de erros pelos consumidores da biblioteca.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class TenantErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = Logger.getLogger(TenantErrorDecoder.class.getName());

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        String body = readBody(response);

        return switch (status) {
            case 404 -> {
                LOGGER.warning("Tenant not found: " + body);
                yield new ResourceNotFoundException("Tenant não encontrado");
            }
            case 409 -> {
                LOGGER.warning("Tenant conflict: " + body);
                yield new ConflictException("Operação em conflito com estado atual: " + body);
            }
            case 400 -> {
                LOGGER.warning("Tenant bad request: " + body);
                yield new IllegalArgumentException("Requisição inválida: " + body);
            }
            case 503 -> {
                LOGGER.severe("Tenant service unavailable");
                yield new CircuitBreakerOpenException("Tenant service unavailable");
            }
            case 500, 502, 504 -> {
                LOGGER.severe("Tenant server error: " + body);
                yield new ServerException("Erro interno do servidor: " + body);
            }
            default -> {
                LOGGER.warning("Tenant unknown error: HTTP " + status + " - " + body);
                yield new RuntimeException("Erro HTTP: " + status + " - " + body);
            }
        };
    }

    private String readBody(Response response) {
        if (response.body() == null) {
            return "";
        }

        try {
            InputStream inputStream = response.body().asInputStream();
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.severe("Failed to read error body: " + e.getMessage());
            return "";
        }
    }
}
