package br.com.conexaoautolib.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de erro OAuth2.
 * Segue padrão RFC 6749 para respostas de erro do Authorization Server.
 * 
 * @author ConexãoAuthLib
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuth2ErrorResponse {
    
    /**
     * Código do erro OAuth2 conforme RFC 6749.
     * 
     * Valores comuns: invalid_client, invalid_grant, access_denied, 
     * unsupported_grant_type, invalid_scope
     */
    @JsonProperty("error")
    private String error;
    
    /**
     * Descrição humana do erro (opcional).
     * Fornece contexto adicional sobre o erro ocorrido.
     */
    @JsonProperty("error_description")
    private String errorDescription;
    
    /**
     * URI da página web com informações sobre o erro (opcional).
     * Usada para fornecer contexto adicional ao desenvolvedor.
     */
    @JsonProperty("error_uri")
    private String errorUri;
    
    /**
     * Verifica se o erro é de credenciais inválidas.
     * 
     * @return true para invalid_client ou invalid_grant
     */
    public boolean isCredentialsError() {
        return "invalid_client".equals(error) || 
               "invalid_grant".equals(error);
    }
    
    /**
     * Verifica se o erro é de acesso negado.
     * 
     * @return true para access_denied
     */
    public boolean isAccessDenied() {
        return "access_denied".equals(error);
    }
    
    /**
     * Verifica se o erro é de grant type não suportado.
     * 
     * @return true para unsupported_grant_type
     */
    public boolean isUnsupportedGrantType() {
        return "unsupported_grant_type".equals(error);
    }
    
    /**
     * Verifica se o erro é de escopo inválido.
     * 
     * @return true para invalid_scope
     */
    public boolean isInvalidScope() {
        return "invalid_scope".equals(error);
    }
    
    /**
     * Obtém mensagem de erro formatada para logs.
     * 
     * @return mensagem formatada com código e descrição
     */
    public String getFormattedMessage() {
        if (errorDescription != null && !errorDescription.trim().isEmpty()) {
            return String.format("[%s] %s", error, errorDescription);
        }
        return error;
    }
}