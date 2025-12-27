package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando a requisição não está autorizada (401 Unauthorized).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class UnauthorizedException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}