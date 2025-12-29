package br.com.conexaoautolib.exception;

/**
 * Exceção lançada quando credenciais inválidas são fornecidas.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class InvalidCredentialsException extends ConexaoAuthException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}