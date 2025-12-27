package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há erro interno no servidor (500+ Server Error).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class ServerException extends ConexaoAuthException {

    /**
     * Construtor com mensagem e código de status HTTP.
     *
     * @param message Mensagem de erro em Português
     * @param statusCode Código de status HTTP (500+)
     */
    public ServerException(String message, int statusCode) {
        super(message, statusCode);
    }
}