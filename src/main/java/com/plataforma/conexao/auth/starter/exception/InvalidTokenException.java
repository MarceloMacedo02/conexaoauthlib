package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando um token JWT é inválido ou expirado.
 *
 * <p>Esta exceção será expandida na Story SDK-2.3.
 * Por enquanto, esta é uma classe stub para permitir a compilação.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class InvalidTokenException extends ConexaoAuthException {

    public InvalidTokenException(String message) {
        super(message, 0);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause, 0);
    }
}
