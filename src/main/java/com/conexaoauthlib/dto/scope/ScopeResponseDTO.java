package com.conexaoauthlib.dto.scope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para operações de Scope.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ScopeResponseDTO scope = scopeClient.getById("scope-id");
 * String name = scope.getName();
 * String resource = scope.getResource();
 * String action = scope.getAction();
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
public class ScopeResponseDTO {

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
     * Recurso protegido.
     */
    private String resource;

    /**
     * Ação permitida.
     */
    private String action;

    /**
     * Status do scope.
     */
    private String status;

    /**
     * Data de criação.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Data da última atualização.
     */
    @JsonProperty("updated_at")
    private String updatedAt;
}
