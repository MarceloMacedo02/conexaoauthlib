package com.conexaoauthlib.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO resumido para referência a um Scope.
 *
 * <p>Usado em listas e respostas onde detalhes completos não são necessários,
 * como em respostas de Role ou listas de seleção.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * List<ScopeSummaryDTO> scopes = role.getScopes();
 * for (ScopeSummaryDTO scope : scopes) {
 *     System.out.println(scope.getName() + ": " + scope.getDescription());
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
public class ScopeSummaryDTO {

    /**
     * Identificador único do scope.
     */
    private String id;

    /**
     * Nome do scope (formato: recurso:ação).
     */
    private String name;

    /**
     * Descrição do scope.
     */
    private String description;

    /**
     * Recurso protegido pelo scope.
     */
    @JsonProperty("resource")
    private String resource;

    /**
     * Ação permitida pelo scope.
     */
    @JsonProperty("action")
    private String action;
}
