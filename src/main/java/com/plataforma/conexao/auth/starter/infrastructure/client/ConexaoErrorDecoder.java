package com.plataforma.conexao.auth.starter.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plataforma.conexao.auth.starter.dto.response.ErrorResponse;
import com.plataforma.conexao.auth.starter.exception.*;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Error Decoder customizado para o cliente Feign do Conexão Auth.
 *
 * <p>Esta classe converte respostas de erro HTTP do Auth Server em exceções
 * de domínio do SDK, permitindo um tratamento mais estruturado e significativo
 * dos erros.
 *
 * <p>Funcionalidades:
 * <ul>
 *   <li>Lê o corpo da resposta HTTP e converte para {@link ErrorResponse}</li>
 *   <li>Mapeia códigos de status HTTP para exceções apropriadas</li>
 *   <li>Extrai informações de erro para lançar exceções específicas</li>
 *   <li>Logue erros em nível apropriado (WARN para esperados, ERROR para críticas)</li>
 * </ul>
 *
 * <p>Mapeamento de erros:
 * <ul>
 *   <li>400 Bad Request → {@link BusinessException}</li>
 *   <li>401 Unauthorized → {@link InvalidCredentialsException} ou {@link InvalidTokenException}</li>
 *   <li>403 Forbidden → {@link InvalidPermissionException}</li>
 *   <li>404 Not Found → {@link UserNotFoundException} ou {@link JwksNotFoundException}</li>
 *   <li>409 Conflict → {@link UserAlreadyExistsException}</li>
 *   <li>422 Unprocessable Entity → {@link BusinessException}</li>
 *   <li>5xx Server Errors → {@link ConexaoAuthClientException}</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ConexaoErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    /**
     * Construtor padrão.
     *
     * <p>Inicializa o ObjectMapper do Jackson para parsing de respostas JSON.
     */
    public ConexaoErrorDecoder() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Decodifica uma resposta de erro HTTP em uma exceção apropriada.
     *
     * @param methodKey Chave do método Feign (ex: "ConexaoAuthClient#registerUser(RegisterUserRequest)")
     * @param response Resposta HTTP do Auth Server
     * @return Exceção apropriada baseada no status HTTP e conteúdo da resposta
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String responseBody = readResponseBody(response);

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConexaoErrorDecoder.class);
        logger.warn("Erro no Feign Client: Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);

        try {
            // Tentar parsear o corpo da resposta para extrair detalhes do erro
            ErrorResponse errorResponse = parseErrorResponse(responseBody, methodKey);

            // Mapear baseado no status HTTP e tipo de erro
            return mapToException(status, errorResponse, methodKey);

        } catch (Exception e) {
            // Se não conseguir parsear, usar uma exceção genérica de cliente
            logger.error("Erro ao decodificar resposta do Auth Server: Method: {}, Status: {}",
                    methodKey, status, e);
            return new ConexaoAuthClientException(
                    methodKey,
                    status,
                    String.format("Erro ao decodificar resposta: %s", responseBody)
            );
        }
    }

    /**
     * Lê o corpo da resposta HTTP como String.
     *
     * @param response Resposta HTTP
     * @return Corpo da resposta como String, ou null se não for possível ler
     */
    private String readResponseBody(Response response) {
        try {
            if (response.body() == null || response.body().length() == 0) {
                return null;
            }
            return Util.toString(response.body().asReader());
        } catch (IOException e) {
            org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConexaoErrorDecoder.class);
            logger.warn("Erro ao ler corpo da resposta HTTP: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Tenta parsear o corpo da resposta para um DTO de erro.
     *
     * @param responseBody Corpo da resposta JSON
     * @param methodKey Chave do método (para logs)
     * @return ErrorResponse parseado, ou null se não for possível
     */
    private ErrorResponse parseErrorResponse(String responseBody, String methodKey) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (IOException e) {
            org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConexaoErrorDecoder.class);
            logger.debug("Não foi possível parsear corpo da resposta como ErrorResponse: Method: {}, Body: {}",
                    methodKey, responseBody);
            return null;
        }
    }

    /**
     * Mapeia o status HTTP e ErrorResponse para uma exceção apropriada.
     *
     * @param status Código de status HTTP
     * @param errorResponse DTO de erro (pode ser null)
     * @param methodKey Chave do método Feign
     * @return Exceção apropriada
     */
    private Exception mapToException(int status, ErrorResponse errorResponse, String methodKey) {
        String message = errorResponse != null ? errorResponse.getMessage() :
                String.format("Erro HTTP %s ao chamar %s", status, methodKey);

        String errorType = errorResponse != null ? errorResponse.getError() : null;

        // 400 Bad Request
        if (status == 400) {
            if (errorType != null && errorType.contains("PERMISSION")) {
                return new InvalidPermissionException(message);
            }
            if (errorType != null && errorType.contains("REALM")) {
                return new InvalidRealmException(message);
            }
            return new BusinessException(message, 400);
        }

        // 401 Unauthorized
        if (status == 401) {
            if (errorType != null && errorType.contains("TOKEN")) {
                if (errorType.contains("EXPIRED")) {
                    return new ExpiredTokenException(message);
                }
                return new InvalidTokenException(message);
            }
            if (errorType != null && errorType.contains("CREDENTIALS")) {
                return new InvalidCredentialsException(message);
            }
            return new AuthException(message, 401);
        }

        // 403 Forbidden
        if (status == 403) {
            return new InvalidPermissionException(message);
        }

        // 404 Not Found
        if (status == 404) {
            if (errorType != null && errorType.contains("USER")) {
                return new UserNotFoundException(message);
            }
            if (errorType != null && errorType.contains("JWKS")) {
                return new JwksNotFoundException(methodKey);
            }
            if (methodKey != null && methodKey.contains("JwksClient")) {
                return new JwksNotFoundException(methodKey);
            }
            if (methodKey != null && methodKey.contains("findUser")) {
                return new UserNotFoundException(message);
            }
            return new UserNotFoundException(message);
        }

        // 409 Conflict
        if (status == 409) {
            if (errorType != null && errorType.contains("USER")) {
                return new UserAlreadyExistsException("email", "já existente");
            }
            return new UserAlreadyExistsException(message);
        }

        // 422 Unprocessable Entity
        if (status == 422) {
            return new BusinessException(message, 422);
        }

        // 5xx Server Errors
        if (status >= 500) {
            return new ConexaoAuthClientException(methodKey, status, message);
        }

        // Fallback para outros códigos
        return new ConexaoAuthClientException(methodKey, status, message);
    }
}
