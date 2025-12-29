package br.com.conexaoautolib.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para chave JSON Web Key (JWK).
 * Representa uma chave criptográfica no formato padrão RFC 7517.
 * 
 * @author ConexãoAuthLib
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonWebKey {
    
    /**
     * Tipo da chave (ex: "RSA").
     */
    @JsonProperty("kty")
    private String kty;
    
    /**
     * Uso da chave (ex: "sig" para assinatura).
     */
    @JsonProperty("use")
    private String use;
    
    /**
     * Algoritmo (ex: "RS256").
     */
    @JsonProperty("alg")
    private String alg;
    
    /**
     * ID da chave para identificar qual chave foi usada.
     */
    @JsonProperty("kid")
    private String kid;
    
    /**
     * Módulo da chave RSA (em Base64URL sem padding).
     */
    @JsonProperty("n")
    private String n;
    
    /**
     * Exponente da chave RSA (em Base64URL sem padding).
     */
    @JsonProperty("e")
    private String e;
    
    /**
     * Timestamp em que a chave se torna válida (Unix epoch).
     * Campo opcional presente apenas em chaves com restrição de tempo.
     */
    @JsonProperty("nbf")
    private Long nbf;
    
    /**
     * Timestamp em que a chave expira (Unix epoch).
     * Campo opcional presente apenas em chaves com expiração.
     */
    @JsonProperty("exp")
    private Long exp;
    
    /**
     * Verifica se a chave está expirada.
     * 
     * @return true se expirada, false se válida ou sem expiração definida
     */
    public boolean isExpired() {
        if (exp == null) {
            return false; // Sem expiração definida
        }
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime > exp;
    }
    
    /**
     * Verifica se a chave é válida no momento atual.
     * 
     * @return true se válida, false caso contrário
     */
    public boolean isValid() {
        long currentTime = System.currentTimeMillis() / 1000;
        
        // Verificar se não é antes do tempo válido (nBF)
        if (nbf != null && currentTime < nbf) {
            return false;
        }
        
        // Verificar se não expirou
        return !isExpired();
    }
    
    /**
     * Verifica se a chave é do tipo RSA.
     * 
     * @return true se for RSA, false caso contrário
     */
    public boolean isRSA() {
        return "RSA".equalsIgnoreCase(kty);
    }
    
    /**
     * Verifica se a chave é para assinatura.
     * 
     * @return true se for para assinatura, false caso contrário
     */
    public boolean isForSignature() {
        return "sig".equalsIgnoreCase(use);
    }
}