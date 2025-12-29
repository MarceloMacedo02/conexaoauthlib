package br.com.conexaoautolib.exception;

/**
 * Exceção lançada quando um usuário não é encontrado.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class UserNotFoundException extends ConexaoAuthException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}