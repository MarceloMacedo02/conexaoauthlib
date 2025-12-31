package com.conexaoauthlib.feign.error;

import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import com.conexaoauthlib.exception.ConflictException;
import com.conexaoauthlib.exception.InvalidOperationException;
import com.conexaoauthlib.exception.ResourceNotFoundException;
import com.conexaoauthlib.exception.ServerException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Error decoder customizado para Scope Client.
 *
 * <p>Converte respostas de erro HTTP em exceções específicas do domínio,
 * facilitando o tratamento de erros pelos consumidores da biblioteca.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ScopeErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = Logger.getLogger(ScopeErrorDecoder.class.getName());

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        String body = readBody(response);

        return switch (status) {
            case 404 -> {
                LOGGER.warning("Scope not found: " + body);
                yield new ResourceNotFoundException("Scope não encontrado: " + body);
            }
            case 409 -> {
                LOGGER.warning("Scope conflict: " + body);
                yield new ConflictException("Scope em uso por roles ativas: " + body);
            }
            case 400 -> {
                LOGGER.warning("Scope bad request: " + body);
                yield new InvalidOperationException("Dados inválidos: " + body);
            }
            case 403 -> {
                LOGGER.warning("Scope forbidden: " + body);
                yield new SecurityException("Acesso negado ao recurso: " + body);
            }
            case 503 -> {
                LOGGER.severe("Scope service unavailable");
                yield new CircuitBreakerOpenException("Scope service unavailable");
            }
            case 500, 502, 504 -> {
                LOGGER.severe("Scope server error: " + body);
                yield new ServerException("Erro interno do servidor: " + body);
            }
            default -> {
                LOGGER.warning("Scope unknown error: HTTP " + status + " - " + body);
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
