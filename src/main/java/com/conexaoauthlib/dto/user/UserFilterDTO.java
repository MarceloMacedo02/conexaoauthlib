package com.conexaoauthlib.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para parâmetros de filtragem e paginação de Usuários.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * UserFilterDTO filter = UserFilterDTO.builder()
 *     .name("João")
 *     .status("ACTIVE")
 *     .tenantId("tenant-123")
 *     .page(0)
 *     .size(20)
 *     .sort("name,asc")
 *     .build();
 * PageResponseDTO<UserResponseDTO> users = userClient.list(filter);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

    /**
     * Filtrar por nome (contém).
     */
    private String name;

    /**
     * Filtrar por email (contém).
     */
    private String email;

    /**
     * Filtrar por status.
     */
    private String status;

    /**
     * Filtrar por role específica.
     */
    @JsonProperty("role_id")
    private String roleId;

    /**
     * Filtrar por tenant específico.
     */
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * Filtrar por client ID específico.
     */
    @JsonProperty("client_id")
    private String clientId;

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
        if (email != null && !email.isBlank()) {
            params.add("email=" + email);
        }
        if (status != null && !status.isBlank()) {
            params.add("status=" + status);
        }
        if (roleId != null && !roleId.isBlank()) {
            params.add("role_id=" + roleId);
        }
        if (tenantId != null && !tenantId.isBlank()) {
            params.add("tenant_id=" + tenantId);
        }
        if (clientId != null && !clientId.isBlank()) {
            params.add("client_id=" + clientId);
        }
        params.add("page=" + page);
        params.add("size=" + size);
        params.add("sort=" + sort);
        return params;
    }
}
