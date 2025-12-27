package com.plataforma.conexao.auth.starter.service;

import com.plataforma.conexao.auth.starter.exception.InvalidTokenException;
import com.plataforma.conexao.auth.starter.model.TokenClaims;

/**
 * Interface para validação de tokens JWT.
 *
 * <p>Esta interface será implementada e configurada detalhadamente na Story SDK-3.2.
 * Por enquanto, esta é uma interface stub para permitir a compilação da Auto-Configuration.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public interface TokenValidator {

    /**
     * Valida um token JWT e extrai os claims.
     *
     * @param jwt Token JWT
     * @return Claims extraídos do token
     * @throws InvalidTokenException Se o token for inválido ou expirado
     */
    TokenClaims validateToken(String jwt) throws InvalidTokenException;

    /**
     * Extrai claims de um token JWT sem validar assinatura.
     *
     * @param jwt Token JWT
     * @return Claims extraídos do token
     * @throws InvalidTokenException Se o token for inválido
     */
    TokenClaims extractClaims(String jwt) throws InvalidTokenException;

    /**
     * Força a atualização do cache JWKS.
     */
    void refreshJwksCache();
}
