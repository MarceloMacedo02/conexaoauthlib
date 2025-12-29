package br.com.conexaoautolib.config;

import br.com.conexaoautolib.exception.ConexaoAuthException;
import br.com.conexaoautolib.exception.InvalidCredentialsException;
import br.com.conexaoautolib.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementação personalizada de ErrorDecoder para OpenFeign.
 * 
 * Mapeia respostas HTTP para exceções específicas do ConexãoAuthLib,
 * fornecendo mensagens de erro detalhadas em português.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Slf4j
public class ConexaoAuthErrorDecoder implements ErrorDecoder {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = extractErrorMessage(response);
        int status = response.status();
        
        log.warn("Erro na chamada Feign para {}: status={}, message={}", methodKey, status, errorMessage);
        
        // Mapear status HTTP para exceções específicas
        return switch (status) {
            case 401 -> new InvalidCredentialsException(
                String.format("Credenciais inválidas: %s", errorMessage)
            );
            case 404 -> {
                if (errorMessage.toLowerCase().contains("realm")) {
                    yield new UserNotFoundException(
                        String.format("Realm não encontrado: %s", errorMessage)
                    );
                } else {
                    yield new UserNotFoundException(
                        String.format("Usuário não encontrado: %s", errorMessage)
                    );
                }
            }
            default -> {
                if (status >= 500 && status < 600) {
                    yield new ConexaoAuthException(
                        String.format("Erro interno do servidor (%d): %s", status, errorMessage)
                    );
                } else if (status >= 400 && status < 500) {
                    yield new ConexaoAuthException(
                        String.format("Erro de cliente (%d): %s", status, errorMessage)
                    );
                } else {
                    yield new ConexaoAuthException(
                        String.format("Erro na requisição (%d): %s", status, errorMessage)
                    );
                }
            }
        };
    }
    
    /**
     * Extrai mensagem de erro detalhada do corpo da resposta.
     * 
     * @param response resposta HTTP do Feign
     * @return mensagem de erro extraída ou mensagem padrão
     */
    private String extractErrorMessage(Response response) {
        if (response.body() == null) {
            return "Sem corpo na resposta";
        }
        
        try (InputStream bodyStream = response.body().asInputStream()) {
            JsonNode rootNode = objectMapper.readTree(bodyStream);
            
            // Tentar diferentes campos comuns de erro
            if (rootNode.has("message")) {
                return rootNode.get("message").asText();
            } else if (rootNode.has("error")) {
                JsonNode errorNode = rootNode.get("error");
                if (errorNode.isObject() && errorNode.has("message")) {
                    return errorNode.get("message").asText();
                } else {
                    return errorNode.asText();
                }
            } else if (rootNode.has("error_description")) {
                return rootNode.get("error_description").asText();
            } else if (rootNode.has("detail")) {
                return rootNode.get("detail").asText();
            }
            
            // Se não encontrar campos específicos, retornar o conteúdo completo
            return rootNode.toString();
            
        } catch (IOException e) {
            log.debug("Não foi possível parsear o corpo da resposta como JSON", e);
            
            // Se falhar parse JSON, tentar ler como string
            try (InputStream bodyStream = response.body().asInputStream()) {
                return new String(bodyStream.readAllBytes());
            } catch (IOException ex) {
                log.debug("Não foi possível ler o corpo da resposta", ex);
                return "Não foi possível ler o corpo da resposta";
            }
        }
    }
}