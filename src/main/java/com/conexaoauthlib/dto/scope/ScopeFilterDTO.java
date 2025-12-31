package com.conexaoauthlib.dto.scope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para parâmetros de filtragem de Scopes.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ScopeFilterDTO filter = ScopeFilterDTO.builder()
 *     .resource("users")
 *     .action("read")
 *     .build();
 * List<ScopeResponseDTO> scopes = scopeClient.list(filter);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeFilterDTO {

    /**
     * Filtrar por recurso.
     */
    private String resource;

    /**
     * Filtrar por ação.
     */
    private String action;

    /**
     * Filtrar por nome (contém).
     */
    private String name;

    /**
     * Filtrar por status.
     */
    private String status;

    /**
     * Converte para lista de parâmetros para query string.
     *
     * @return Lista de parâmetros no formato "key=value"
     */
    public List<String> toQueryParams() {
        List<String> params = new ArrayList<>();
        if (resource != null && !resource.isBlank()) {
            params.add("resource=" + resource);
        }
        if (action != null && !action.isBlank()) {
            params.add("action=" + action);
        }
        if (name != null && !name.isBlank()) {
            params.add("name=" + name);
        }
        if (status != null && !status.isBlank()) {
            params.add("status=" + status);
        }
        return params;
    }
}
