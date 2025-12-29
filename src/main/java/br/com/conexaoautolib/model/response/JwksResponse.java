package br.com.conexaoautolib.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta JWKS (JSON Web Key Set).
 * Segue padrão RFC 7517 para chaves públicas de verificação JWT.
 * 
 * @param keys Lista de chaves públicas JSON Web Key
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwksResponse {
    
    @JsonProperty("keys")
    private List<JsonWebKey> keys;
}