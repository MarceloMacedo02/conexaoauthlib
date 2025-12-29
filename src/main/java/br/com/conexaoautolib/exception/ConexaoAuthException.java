package br.com.conexaoautolib.exception;

/**
 * Exceção base para erros do ConexãoAuthLib.
 * 
 * Representa erros gerais da biblioteca e serve como classe base
 * para outras exceções específicas do domínio.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ConexaoAuthException extends RuntimeException {
    
    public ConexaoAuthException(String message) {
        super(message);
    }
    
    public ConexaoAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}