package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para parâmetros de filtragem e paginação de Tenants.
 * Utilizado como query parameters em requisições GET.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TenantFilterDTO filter = TenantFilterDTO.builder()
 *     .name("Empresa")
 *     .status("ACTIVE")
 *     .page(0)
 *     .size(20)
 *     .sort("created_at,desc")
 *     .build();
 * PageResponseDTO<TenantResponseDTO> tenants = tenantClient.list(filter);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFilterDTO {

    /**
     * Filtrar por nome (contém).
     */
    private String name;

    /**
     * Filtrar por status.
     */
    private String status;

    /**
     * Filtrar por número de documento (contém).
     */
    @JsonProperty("document_number")
    private String documentNumber;

    /**
     * Número da página (0-indexed).
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Tamanho da página.
     */
    @Builder.Default
    private Integer size = 20;

    /**
     * Campo de ordenação (formato: field,direction).
     * Exemplo: "name,asc" ou "created_at,desc"
     */
    @Builder.Default
    private String sort = "created_at,desc";

    /**
     * Converte para lista de parâmetros para query string.
     *
     * @return Lista de parâmetros no formato "key=value"
     */
    public List<String> toQueryParams() {
        List<String> params = new ArrayList<>();
        if (name != null && !name.isBlank()) {
            params.add("name=" + name);
        }
        if (status != null && !status.isBlank()) {
            params.add("status=" + status);
        }
        if (documentNumber != null && !documentNumber.isBlank()) {
            params.add("document_number=" + documentNumber);
        }
        params.add("page=" + page);
        params.add("size=" + size);
        params.add("sort=" + sort);
        return params;
    }
}
