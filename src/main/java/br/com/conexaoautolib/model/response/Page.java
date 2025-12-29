package br.com.conexaoautolib.model.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * DTO de resposta para dados paginados.
 * Implementação simples de paginação para evitar dependência do Spring Data.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Value
@Builder
public class Page<T> {

    /**
     * Conteúdo da página atual.
     */
    List<T> content;

    /**
     * Número da página atual (0-based).
     */
    int number;

    /**
     * Tamanho da página.
     */
    int size;

    /**
     * Total de elementos disponíveis.
     */
    long totalElements;

    /**
     * Total de páginas disponíveis.
     */
    int totalPages;

    /**
     * Indica se existe página anterior.
     */
    boolean first;

    /**
     * Indica se existe próxima página.
     */
    boolean last;

    /**
     * Indica se é a última página.
     */
    boolean empty;
}