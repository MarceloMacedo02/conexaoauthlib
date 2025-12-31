package com.conexaoauthlib.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO padronizado para respostas de erro da API OAuth2.
 * Segue o padrão OAuth 2.0 Authorization Framework.
 *
 * <p>Códigos de erro comuns:</p>
 * <ul>
 *   <li>invalid_request - A requisição está faltando um parâmetro obrigatório</li>
 *   <li>invalid_client - Autenticação do cliente falhou</li>
 *   <li>invalid_grant - O grant ou refresh token é inválido</li>
 *   <li>unauthorized_client - Cliente não autorizado para este grant type</li>
 *   <li>unsupported_grant_type - Grant type não suportado pelo servidor</li>
 *   <li>invalid_scope - Escopo inválido, desconhecido ou malformado</li>
 * </ul>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749">RFC 6749</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDTO {

    /**
     * Código de erro OAuth2.
     * Exemplos: invalid_request, invalid_client, invalid_grant,
     * unauthorized_client, unsupported_grant_type, invalid_scope
     */
    private String error;

    /**
     * Descrição legível do erro.
     */
    @JsonProperty("error_description")
    private String errorDescription;

    /**
     * URI de referência com mais informações sobre o erro.
     */
    @JsonProperty("error_uri")
    private String errorUri;

    /**
     * Código HTTP do erro.
     */
    private Integer status;

    /**
     * Path da requisição que causou o erro.
     */
    private String path;

    /**
     * Timestamp da ocorrência do erro.
     */
    private String timestamp;

    /**
     * ID da requisição para correlação de logs.
     */
    @JsonProperty("request_id")
    private String requestId;
}
