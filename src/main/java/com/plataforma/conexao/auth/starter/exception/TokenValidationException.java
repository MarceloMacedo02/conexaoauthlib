package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando há um erro na validação de token.
 *
 * <p>Esta exceção é utilizada para erros genéricos de validação que não
 * se enquadram nas categorias mais específicas como {@link InvalidTokenException}
 * ou {@link ExpiredTokenException}.
 *
 * <p>Exemplos de uso:
 * <ul>
 *   <li>Erro ao buscar chaves JWKS</li>
 *   <li>Algoritmo de assinatura não suportado</li>
 *   <li>Erro na verificação de claims</li>
 *   <li>Token emitido por um issuer diferente do esperado</li>
 * </ul>
 *
 * <p>Esta exceção resulta em resposta HTTP 401 (Unauthorized) por padrão.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenValidationException extends AuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public TokenValidationException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor padrão com mensagem genérica.
     */
    public TokenValidationException() {
        super("Erro na validação do token JWT");
    }
}
