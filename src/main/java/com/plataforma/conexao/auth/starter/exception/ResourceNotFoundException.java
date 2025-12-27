package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um recurso não é encontrado (404 Not Found).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends ConexaoAuthException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem de erro em Português
     */
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}