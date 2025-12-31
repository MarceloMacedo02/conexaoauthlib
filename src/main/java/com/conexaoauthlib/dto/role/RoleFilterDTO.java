package com.conexaoauthlib.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para parâmetros de filtragem e paginação de Roles.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * RoleFilterDTO filter = RoleFilterDTO.builder()
 *     .name("admin")
 *     .status("ACTIVE")
 *     .tenantId("tenant-123")
 *     .includeScopes(true)
 *     .page(0)
 *     .size(20)
 *     .sort("name,asc")
 *     .build();
 * PageResponseDTO<RoleResponseDTO> roles = roleClient.list(filter);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleFilterDTO {

    /**
     * Filtrar por nome (contém).
     */
    private String name;

    /**
     * Filtrar por status.
     */
    private String status;

    /**
     * Filtrar por tenant específico.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Incluir scopes na resposta.
     */
    @JsonProperty("include_scopes")
    @Builder.Default
    private Boolean includeScopes = false;

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
    private String sort = "name,asc";

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
        if (tenantId != null && !tenantId.isBlank()) {
            params.add("tenant_id=" + tenantId);
        }
        if (includeScopes != null) {
            params.add("include_scopes=" + includeScopes);
        }
        params.add("page=" + page);
        params.add("size=" + size);
        params.add("sort=" + sort);
        return params;
    }
}
