package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representando um produto associado a um tenant.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * List<TenantProductDTO> products = tenant.getProducts();
 * for (TenantProductDTO product : products) {
 *     System.out.println(product.getCode() + ": " + product.getName());
 * }
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantProductDTO {

    /**
     * Identificador único do produto.
     */
    private String id;

    /**
     * Código único do produto.
     */
    private String code;

    /**
     * Nome do produto.
     */
    private String name;

    /**
     * Status do produto.
     */
    private String status;

    /**
     * Data de ativação do produto para este tenant.
     */
    @JsonProperty("activated_at")
    private String activatedAt;

    /**
     * Data de expiração do produto para este tenant.
     */
    @JsonProperty("expires_at")
    private String expiresAt;
}
