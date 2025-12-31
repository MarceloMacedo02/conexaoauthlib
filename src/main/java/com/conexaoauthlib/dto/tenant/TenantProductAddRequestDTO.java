package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para adição de produtos a um tenant.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TenantProductAddRequestDTO request = TenantProductAddRequestDTO.builder()
 *     .productCodes(List.of("MOD_RH", "MOD_FIN"))
 *     .build();
 * tenantClient.addProducts("tenant-id", request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantProductAddRequestDTO {

    /**
     * Lista de códigos de produtos a serem adicionados.
     */
    @NotEmpty(message = "Pelo menos um código de produto é obrigatório")
    @JsonProperty("product_codes")
    private List<String> productCodes;
}
