package com.conexaoauthlib.examples;

import com.conexaoauthlib.fluent.oauth2.AuthClient;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.dto.oauth2.IntrospectResponseDTO;
import com.conexaoauthlib.exception.InvalidClientException;

/**
 * Exemplos de uso da API OAuth2.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class OAuth2TokenExample {

    /**
     * Exemplo de Client Credentials Grant.
     */
    public static void clientCredentialsExample() {
        System.out.println("=== Client Credentials Grant ===");

        try {
            TokenResponseDTO token = AuthClient.clientCredentials("my-service", "secret123")
                .scope("read write")
                .execute();

            System.out.println("Access Token: " + token.getAccessToken());
            System.out.println("Token Type: " + token.getTokenType());
            System.out.println("Expires In: " + token.getExpiresIn() + " seconds");
            System.out.println("Refresh Token: " + token.getRefreshToken());

        } catch (InvalidClientException e) {
            System.err.println("Erro: Credenciais inválidas");
        }
    }

    /**
     * Exemplo de Password Grant.
     */
    public static void passwordGrantExample() {
        System.out.println("\n=== Password Grant ===");

        TokenResponseDTO token = AuthClient.password("user@example.com", "userPassword123")
            .clientCredentials("my-service", "secret123")
            .scope("read write")
            .tenantId("tenant-123")
            .execute();

        System.out.println("Access Token: " + token.getAccessToken());
    }

    /**
     * Exemplo de Refresh Token.
     */
    public static void refreshTokenExample() {
        System.out.println("\n=== Refresh Token ===");

        String refreshToken = "stored-refresh-token";

        TokenResponseDTO newToken = AuthClient.refreshToken(refreshToken)
            .clientCredentials("my-service", "secret123")
            .execute();

        System.out.println("Novo Access Token: " + newToken.getAccessToken());
        System.out.println("Novo Refresh Token: " + newToken.getRefreshToken());
    }

    /**
     * Exemplo de Introspect.
     */
    public static void introspectExample() {
        System.out.println("\n=== Token Introspection ===");

        String accessToken = "token-to-check";

        IntrospectResponseDTO info = AuthClient.introspect(accessToken)
            .execute();

        if (info.getActive()) {
            System.out.println("Token válido!");
            System.out.println("Subject: " + info.getSub());
            System.out.println("Client ID: " + info.getClientId());
            System.out.println("Scopes: " + info.getScopes());
            System.out.println("Expires At: " + info.getExp());
        } else {
            System.out.println("Token expirado ou revogado");
        }
    }

    /**
     * Exemplo de Revoke Token.
     */
    public static void revokeExample() {
        System.out.println("\n=== Revoke Token ===");

        String accessToken = "token-to-revoke";

        AuthClient.revoke(accessToken)
            .execute();

        System.out.println("Token revogado com sucesso!");
    }

    /**
     * Exemplo completo de fluxo de autenticação.
     */
    public static void fullAuthFlow() {
        System.out.println("\n=== Fluxo Completo de Autenticação ===");

        // 1. Obter token
        TokenResponseDTO token = AuthClient.clientCredentials("my-service", "secret123")
            .scope("read write")
            .execute();

        System.out.println("Token obtido: " + token.getAccessToken());

        // 2. Usar o token para chamar API...

        // 3. Verificar se token ainda é válido
        IntrospectResponseDTO info = AuthClient.introspect(token.getAccessToken())
            .execute();

        if (!info.getActive() && token.getRefreshToken() != null) {
            // 4. Se expirou, fazer refresh
            TokenResponseDTO newToken = AuthClient.refreshToken(token.getRefreshToken())
                .clientCredentials("my-service", "secret123")
                .execute();

            System.out.println("Token renovado: " + newToken.getAccessToken());
        }
    }

    public static void main(String[] args) {
        clientCredentialsExample();
        passwordGrantExample();
        refreshTokenExample();
        introspectExample();
        revokeExample();
        fullAuthFlow();
    }
}
