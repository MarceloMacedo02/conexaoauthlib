package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para solicitação de token via Client Credentials Flow.
 *
 * <p>Este DTO é utilizado para obter um token de acesso no fluxo
 * Client Credentials do OAuth2, onde o cliente (aplicação) se autentica
 * diretamente usando client_id e client_secret.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record ClientCredentialsRequest(

    /**
     * Tipo de grant (obrigatoriamente "client_credentials").
     */
    @NotBlank(message = "Grant type é obrigatório")
    @Pattern(regexp = "client_credentials", message = "Grant type deve ser 'client_credentials'")
    String grantType,

    /**
     * Identificador do cliente (client_id).
     */
    @NotBlank(message = "Client ID é obrigatório")
    String clientId,

    /**
     * Segredo do cliente (client_secret).
     */
    @NotBlank(message = "Client Secret é obrigatório")
    String clientSecret,

    /**
     * Escopo do token (opcional, ex: "read write").
     */
    String scope
) {}
