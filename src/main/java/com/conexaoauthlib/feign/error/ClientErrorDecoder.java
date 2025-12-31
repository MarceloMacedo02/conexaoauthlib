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
 * Error decoder customizado para Client Client.
 *
 * <p>Converte respostas de erro HTTP em exceções específicas do domínio,
 * facilitando o tratamento de erros pelos consumidores da biblioteca.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ClientErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = Logger.getLogger(ClientErrorDecoder.class.getName());

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        String body = readBody(response);

        return switch (status) {
            case 404 -> {
                LOGGER.warning("Client not found: " + body);
                yield new ResourceNotFoundException("Client não encontrado: " + body);
            }
            case 409 -> {
                LOGGER.warning("Client conflict: " + body);
                yield new ConflictException("Client já existe ou operação em conflito: " + body);
            }
            case 400 -> {
                LOGGER.warning("Client bad request: " + body);
                yield new InvalidOperationException("Operação inválida: " + body);
            }
            case 403 -> {
                LOGGER.warning("Client forbidden: " + body);
                yield new SecurityException("Acesso negado ao recurso: " + body);
            }
            case 503 -> {
                LOGGER.severe("Client service unavailable");
                yield new CircuitBreakerOpenException("Client service unavailable");
            }
            case 500, 502, 504 -> {
                LOGGER.severe("Client server error: " + body);
                yield new ServerException("Erro interno do servidor: " + body);
            }
            default -> {
                LOGGER.warning("Client unknown error: HTTP " + status + " - " + body);
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
