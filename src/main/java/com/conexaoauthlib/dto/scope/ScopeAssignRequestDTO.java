package com.conexaoauthlib.dto.scope;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para atribuição de scopes a uma role.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ScopeAssignRequestDTO request = ScopeAssignRequestDTO.builder()
 *     .scopeIds(List.of("scope-1", "scope-2", "scope-3"))
 *     .build();
 * roleClient.assignScopes("role-id", request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeAssignRequestDTO {

    /**
     * Lista de IDs de scopes a serem atribuídos.
     */
    @NotEmpty(message = "Pelo menos um scope deve ser informado")
    @JsonProperty("scope_ids")
    private List<String> scopeIds;

    /**
     * Se true, substitui todos os scopes existentes.
     * Se false, adiciona aos scopes existentes.
     */
    @JsonProperty("replace_existing")
    @Builder.Default
    private Boolean replaceExisting = false;
}
