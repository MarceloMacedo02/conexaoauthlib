package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando o acesso é proibido (403 Forbidden).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ForbiddenException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ForbiddenException(String message) {
        super(message, 403);
    }
}