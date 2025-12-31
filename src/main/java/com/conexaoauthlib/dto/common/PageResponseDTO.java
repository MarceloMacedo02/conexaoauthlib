package com.conexaoauthlib.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para respostas paginadas.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * PageResponseDTO<TenantResponseDTO> response = tenantClient.list(filter);
 * List<TenantResponseDTO> tenants = response.getContent();
 * int totalPages = response.getTotalPages();
 * long totalElements = response.getTotalElements();
 * }</pre>
 *
 * @param <T> Tipo do conteúdo da página
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponseDTO<T> {

    /**
     * Lista de elementos da página atual.
     */
    private List<T> content;

    /**
     * Número da página atual (0-indexed).
     */
    private Integer page;

    /**
     * Tamanho da página.
     */
    private Integer size;

    /**
     * Total de elementos em todas as páginas.
     */
    @JsonProperty("total_elements")
    private Long totalElements;

    /**
     * Total de páginas.
     */
    @JsonProperty("total_pages")
    private Integer totalPages;

    /**
     * Indica se é a primeira página.
     */
    private Boolean first;

    /**
     * Indica se é a última página.
     */
    private Boolean last;

    /**
     * Indica se há próxima página.
     */
    @JsonProperty("has_next")
    private Boolean hasNext;

    /**
     * Indica se há página anterior.
     */
    @JsonProperty("has_previous")
    private Boolean hasPrevious;
}
