package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção base para erros de autenticação no Conexão Auth SDK.
 *
 * <p>Esta classe representa erros relacionados a autenticação e autorização,
 * incluindo problemas com tokens JWT, credenciais inválidas e validação de
 * acesso a recursos protegidos.
 *
 * <p>Subclasses comuns:
 * <ul>
 *   <li>{@link InvalidTokenException} - Token JWT inválido ou malformado</li>
 *   <li>{@link ExpiredTokenException} - Token JWT expirado</li>
 *   <li>{@link InvalidCredentialsException} - Credenciais de usuário inválidas</li>
 *   <li>{@link UserNotFoundException} - Usuário não encontrado no sistema</li>
 *   <li>{@link TokenValidationException} - Erro na validação de token</li>
 * </ul>
 *
 * <p>Esta exceção é lançada quando há falha no processo de autenticação ou
 * autorização, geralmente resultando em respostas HTTP 401 (Unauthorized) ou
 * 403 (Forbidden).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class AuthException extends ConexaoAuthException {

    /**
     * Construtor padrão com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro em Português
     */
    public AuthException(String message) {
        super(message, 401);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param cause Causa raiz da exceção
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause, 401);
    }

    /**
     * Construtor com mensagem e status HTTP customizável.
     *
     * @param message Mensagem descritiva do erro em Português
     * @param statusCode Status HTTP code (ex: 401, 403)
     */
    public AuthException(String message, int statusCode) {
        super(message, statusCode);
    }
}
