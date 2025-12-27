package com.plataforma.conexao.auth.starter.dto.response;

import java.util.List;

/**
 * DTO de resposta para token OAuth2.
 *
 * <p>
 * Este DTO representa a resposta padrão de um endpoint de token OAuth2,
 * contendo o access token, tipo de token, tempo de expiração, refresh token
 * (opcional) e escopo de permissões.
 *
 * <p>
 * Conforme especificação RFC 6749 (OAuth 2.0 Token):
 * <ul>
 * <li>accessToken: Token de acesso JWT</li>
 * <li>tokenType: Tipo do token (geralmente "Bearer")</li>
 * <li>expiresIn: Tempo de expiração em segundos Unix timestamp</li>
 * <li>scope: Escopo do token (opcional)</li>
 * <li>refreshToken: Token de atualização (opcional)</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record TokenResponse(

        /**
         * Token de acesso JWT.
         *
         * <p>
         * Este é o token retornado pelo endpoint /oauth2/token
         * </p>
         */
        String accessToken,

        /**
         * Tipo do token (geralmente "Bearer").
         *
         * <p>
         * Pode ser "Bearer", "Basic", "MAC", etc.
         * </p>
         */
        String tokenType,

        /**
         * Tempo de expiração em segundos Unix timestamp.
         *
         * <p>
         * Indica quando o token expira. Ex: 1735684800L representa
         * segundos desde 1 de janeiro de 1970 (Unix timestamp.
         * </p>
         */
        Long expiresIn,

        /**
         * Token de atualização (opcional).
         *
         * <p>
         * Usado para renovar o token quando access token expirar.
         * </p>
         */
        String refreshToken,

        /**
         * Escopo do token (opcional).
         *
         * <p>
         * Lista de permissões do token separada por espaços.
         * </p>
         */
        List<String> scope) {
}
