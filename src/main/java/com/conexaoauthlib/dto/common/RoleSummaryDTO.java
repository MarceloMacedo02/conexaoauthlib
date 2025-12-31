package com.conexaoauthlib.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resumo de Role para uso em respostas de outros DTOs.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * List<RoleSummaryDTO> roles = client.getRoles();
 * for (RoleSummaryDTO role : roles) {
 *     System.out.println(role.getCode() + ": " + role.getName());
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
public class RoleSummaryDTO {

    /**
     * Identificador único da role.
     */
    private String id;

    /**
     * Código único da role.
     */
    @JsonProperty("role_code")
    private String code;

    /**
     * Nome da role.
     */
    private String name;

    /**
     * Descrição da role.
     */
    private String description;
}
