package com.plataforma.conexao.auth.starter.api;

import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;

/**
 * API Fluente para o Conexão Auth.
 *
 * <p>Fornece uma interface encadeada para configuração e obtenção de tokens
 * de autenticação OAuth2 de forma intuitiva e legível.
 *
 * <p>Exemplo de uso:
 * <pre>{@code
 * // Obter token com credenciais de usuário (password grant)
 * TokenResponse token = ConexaoAuth
 *     .clientId("my-client")
 *     .clientSecret("my-secret")
 *     .realm("my-realm")
 *     .username("user@example.com")
 *     .password("UserPass123!")
 *     .grantType("password")
 *     .execute();
 *
 * // Obter token via client credentials
 * TokenResponse token = ConexaoAuth
 *     .clientId("my-client")
 *     .clientSecret("my-secret")
 *     .realm("my-realm")
 *     .grantType("client_credentials")
 *     .execute();
 *
 * // Atualizar token
 * TokenResponse token = ConexaoAuth
 *     .clientId("my-client")
 *     .clientSecret("my-secret")
 *     .realm("my-realm")
 *     .refreshToken("old-refresh-token")
 *     .grantType("refresh_token")
 *     .execute();
 * }</pre>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public final class ConexaoAuth {

    private ConexaoAuth() {
        // Classe utilitária - não pode ser instanciada
    }

    /**
     * Inicia uma nova requisição de autenticação.
     *
     * <p>Use este método para começar a encadear os parâmetros de autenticação.
     *
     * @return Builder para configuração da requisição
     */
    public static ConexaoAuthRequest builder() {
        return new ConexaoAuthRequest();
    }

    /**
     * Inicia uma nova requisição de autenticação com Client ID preenchido.
     *
     * @param clientId ID do cliente OAuth2
     * @return Builder para configuração da requisição
     */
    public static ConexaoAuthRequest clientId(String clientId) {
        return builder().clientId(clientId);
    }

    /**
     * Inicia uma nova requisição de autenticação com Realm preenchido.
     *
     * @param realm Nome do realm no Auth Server
     * @return Builder para configuração da requisição
     */
    public static ConexaoAuthRequest realm(String realm) {
        return builder().realm(realm);
    }
}
