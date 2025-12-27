package com.plataforma.conexao.auth.starter.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plataforma.conexao.auth.starter.exception.ConflictException;
import com.plataforma.conexao.auth.starter.exception.ConexaoAuthException;
import com.plataforma.conexao.auth.starter.exception.ForbiddenException;
import com.plataforma.conexao.auth.starter.exception.ResourceNotFoundException;
import com.plataforma.conexao.auth.starter.exception.ServerException;
import com.plataforma.conexao.auth.starter.exception.UnauthorizedException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Decoder de erros customizado para Feign Client.
 *
 * <p>Este decoder converte respostas de erro HTTP do Auth Server em exceções
 * específicas do SDK, facilitando o tratamento de erros pelos clientes.
 *
 * <p>Mapeamento de códigos de status:
 * <ul>
 *   <li>400 Bad Request → {@link ConexaoAuthException} (validação)</li>
 *   <li>401 Unauthorized → {@link UnauthorizedException}</li>
 *   <li>403 Forbidden → {@link ForbiddenException}</li>
 *   <li>404 Not Found → {@link ResourceNotFoundException}</li>
 *   <li>409 Conflict → {@link ConflictException}</li>
 *   <li>500+ Server Error → {@link ServerException}</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConexaoAuthErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(ConexaoAuthErrorDecoder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String errorMessage = extractErrorMessage(response);

        log.error("Erro na chamada Feign: methodKey={}, status={}, message={}",
                methodKey, status, errorMessage);

        // Mapeia códigos de status para exceções específicas
        if (status == 400) {
            return new ConexaoAuthException("Requisição inválida: " + errorMessage, status);
        }
        if (status == 401) {
            return new UnauthorizedException("Não autorizado: " + errorMessage);
        }
        if (status == 403) {
            return new ForbiddenException("Acesso proibido: " + errorMessage);
        }
        if (status == 404) {
            return new ResourceNotFoundException("Recurso não encontrado: " + errorMessage);
        }
        if (status == 409) {
            return new ConflictException("Conflito de dados: " + errorMessage);
        }
        if (status == 422) {
            return new ConexaoAuthException("Erro de validação: " + errorMessage, status);
        }
        if (status >= 500 && status < 600) {
            return new ServerException("Erro interno no servidor: " + errorMessage, status);
        }

        // Erro desconhecido
        return new ConexaoAuthException(
                String.format("Erro desconhecido (status %d): %s", status, errorMessage),
                status);
    }

    /**
     * Extrai a mensagem de erro do corpo da resposta.
     *
     * <p>Tenta ler o corpo da resposta como JSON e extrair a mensagem.
     * Se não for possível, retorna uma mensagem genérica.
     *
     * @param response Resposta do Feign
     * @return Mensagem de erro extraída
     */
    private String extractErrorMessage(Response response) {
        try {
            if (response.body() == null) {
                return "Sem corpo na resposta";
            }

            String responseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));

            if (responseBody == null || responseBody.isEmpty()) {
                return "Corpo da resposta vazio";
            }

            // Tenta fazer parse do JSON para extrair a mensagem
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);
                Object messageObj = errorMap.get("message");

                if (messageObj != null) {
                    String message = messageObj.toString();
                    if (message != null && !message.isEmpty()) {
                        return message;
                    }
                }

                // Tenta outros campos comuns de erro
                messageObj = errorMap.get("error");
                if (messageObj != null) {
                    return messageObj.toString();
                }

                messageObj = errorMap.get("error_description");
                if (messageObj != null) {
                    return messageObj.toString();
                }

            } catch (IOException e) {
                log.debug("Não foi possível fazer parse do corpo da resposta como JSON", e);
            }

            // Se não conseguir extrair mensagem específica, retorna o corpo completo (limitado)
            return responseBody.length() > 200
                    ? responseBody.substring(0, 200) + "..."
                    : responseBody;

        } catch (IOException e) {
            log.error("Erro ao ler corpo da resposta", e);
            return "Erro ao ler corpo da resposta";
        }
    }
}
