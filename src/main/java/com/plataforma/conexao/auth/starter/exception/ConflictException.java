package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há conflito de dados (409 Conflict).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ConflictException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ConflictException(String message) {
        super(message, 409);
    }
}