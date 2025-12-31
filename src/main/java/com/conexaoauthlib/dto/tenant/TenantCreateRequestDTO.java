package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para criação de um novo Tenant.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TenantCreateRequestDTO request = TenantCreateRequestDTO.builder()
 *     .name("Empresa Exemplo Ltda")
 *     .documentNumber("12345678901")
 *     .products(List.of("MOD_RH", "MOD_FIN"))
 *     .build();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateRequestDTO {

    /**
     * Nome do tenant/organização.
     */
    @NotBlank(message = "Nome do tenant é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String name;

    /**
     * Número do documento (CPF ou CNPJ).
     */
    @NotBlank(message = "Documento é obrigatório")
    @Size(min = 11, max = 18, message = "Documento deve ter entre 11 e 18 caracteres")
    @DocumentValidator(message = "Documento deve ser um CPF ou CNPJ válido")
    @JsonProperty("document_number")
    private String documentNumber;

    /**
     * Lista de códigos de produtos a serem atribuídos ao tenant.
     */
    @Builder.Default
    private List<String> products = new ArrayList<>();

    /**
     * ID do tenant pai (para estruturas hierárquicas).
     */
    @JsonProperty("parent_tenant_id")
    private String parentTenantId;
}
