package com.conexaoauthlib.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para parâmetros de filtragem e paginação de Clients.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientFilterDTO filter = ClientFilterDTO.builder()
 *     .tenantId("tenant-123")
 *     .status("ACTIVE")
 *     .page(0)
 *     .size(20)
 *     .sort("name,asc")
 *     .build();
 * PageResponseDTO<ClientResponseDTO> clients = clientClient.list(filter);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientFilterDTO {

    /**
     * Filtrar por tenant específico.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Filtrar por status.
     */
    private String status;

    /**
     * Filtrar por client ID (contém).
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Filtrar por nome (contém).
     */
    private String name;

    /**
     * Filtrar por grant type específico.
     */
    @JsonProperty("grant_type")
    private String grantType;

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
        if (tenantId != null && !tenantId.isBlank()) {
            params.add("tenant_id=" + tenantId);
        }
        if (status != null && !status.isBlank()) {
            params.add("status=" + status);
        }
        if (clientId != null && !clientId.isBlank()) {
            params.add("client_id=" + clientId);
        }
        if (name != null && !name.isBlank()) {
            params.add("name=" + name);
        }
        if (grantType != null && !grantType.isBlank()) {
            params.add("grant_type=" + grantType);
        }
        params.add("page=" + page);
        params.add("size=" + size);
        params.add("sort=" + sort);
        return params;
    }
}
