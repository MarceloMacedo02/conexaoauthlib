package com.conexaoauthlib.examples;

import com.conexaoauthlib.fluent.client.ClientClient;
import com.conexaoauthlib.dto.client.ClientResponseDTO;
import com.conexaoauthlib.dto.client.ClientSecretResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

import java.util.List;

/**
 * Exemplos de registro e gerenciamento de Clients OAuth2.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class ClientRegistrationExample {

    /**
     * Exemplo de registro de novo client.
     */
    public static void registerClient() {
        System.out.println("=== Registrar Client ===");

        ClientResponseDTO client = ClientClient.register()
            .clientId("my-service-api")
            .clientSecret("generate-secure-secret-here")
            .name("My Service API")
            .tenantId("tenant-123")
            .grantType("client_credentials")
            .grantType("refresh_token")
            .scopes("read", "write", "admin")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(86400)
            .execute();

        System.out.println("Client registrado:");
        System.out.println("  ID: " + client.getId());
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Status: " + client.getStatus());
        System.out.println("  Grant Types: " + client.getGrantTypes());
        System.out.println("  Scopes: " + client.getScopes());

        // IMPORTANTE: Armazene o clientSecret com segurança!
        System.out.println("  Client Secret: [ARMAZENAR COM SEGURANÇA]");
    }

    /**
     * Exemplo de registro com geração automática de segredo.
     */
    public static void registerClientWithAutoSecret() {
        System.out.println("\n=== Registrar Client com Segredo Automático ===");

        ClientResponseDTO client = ClientClient.register()
            .clientId("auto-secret-service")
            .name("Auto Secret Service")
            .tenantId("tenant-123")
            .grantType("client_credentials")
            .scopes("read", "write")
            .execute();

        System.out.println("Client registrado:");
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Client Secret: " + client.getClientSecret());
        System.out.println("  ⚠️  Guarde o segredo agora, não será mostrado novamente!");
    }

    /**
     * Exemplo de rotação de segredo.
     */
    public static void rotateSecret() {
        System.out.println("\n=== Rotacionar Segredo ===");

        ClientSecretResponseDTO secret = ClientClient.rotateSecret("client-id")
            .execute();

        System.out.println("Novo segredo gerado:");
        System.out.println("  Client ID: " + secret.getClientId());
        System.out.println("  Novo Segredo: " + secret.getNewSecret());
        System.out.println("  Expira em: " + secret.getExpiresAt());

        // ATENÇÃO: O segredo antigo ainda é válido até expiresAt
        // Atualize todos os serviços que usam o segredo antigo!
        System.out.println("  ⚠️  Atualize os serviços que usam o segredo antigo!");
    }

    /**
     * Exemplo de listagem de clients.
     */
    public static void listClients() {
        System.out.println("\n=== Listar Clients ===");

        PageResponseDTO<ClientResponseDTO> page = ClientClient.list()
            .tenantId("tenant-123")
            .status("ACTIVE")
            .page(0)
            .size(20)
            .execute();

        System.out.println("Total de clients: " + page.getTotalElements());
        page.getContent().forEach(client ->
            System.out.println("  - " + client.getClientId() + " (" + client.getStatus() + ")")
        );
    }

    /**
     * Exemplo de atualização de status.
     */
    public static void updateStatus() {
        System.out.println("\n=== Atualizar Status ===");

        ClientResponseDTO suspended = ClientClient.updateStatus("client-id")
            .status("SUSPENDED")
            .execute();

        System.out.println("Client suspenso: " + suspended.getStatus());
    }

    /**
     * Exemplo de ativação de client.
     */
    public static void activateClient() {
        System.out.println("\n=== Ativar Client ===");

        ClientResponseDTO activated = ClientClient.updateStatus("client-id")
            .status("ACTIVE")
            .execute();

        System.out.println("Client ativado: " + activated.getClientId());
    }

    /**
     * Exemplo de busca de client por ID.
     */
    public static void findById() {
        System.out.println("\n=== Buscar Client por ID ===");

        ClientResponseDTO client = ClientClient.getById("client-id")
            .execute();

        System.out.println("Client encontrado:");
        System.out.println("  ID: " + client.getId());
        System.out.println("  Client ID: " + client.getClientId());
        System.out.println("  Nome: " + client.getName());
        System.out.println("  Scopes: " + client.getScopes());
    }

    /**
     * Exemplo de atualização de status.
     */
    public static void updateScopes() {
        System.out.println("\n=== Atualizar Scopes ===");

        System.out.println("Nota: Para atualizar scopes, é necessário recriar o client ou usar a API de atualização direta.");
        System.out.println("Scopes originais podem ser visualizados via getById().");
    }

    /**
     * Exemplo de exclusão de client.
     */
    public static void deleteClient() {
        System.out.println("\n=== Excluir Client ===");

        ClientClient.delete("client-id")
            .execute();

        System.out.println("Client excluído com sucesso!");
    }

    /**
     * Exemplo de uso do client para obter token.
     */
    public static void useClientForAuth() {
        System.out.println("\n=== Usar Client para Autenticação ===");

        // Primeiro, registre o client (conforme exemplo anterior)
        ClientResponseDTO client = ClientClient.register()
            .clientId("batch-processor")
            .clientSecret("batch-secret")
            .name("Batch Processor Service")
            .tenantId("tenant-123")
            .grantType("client_credentials")
            .scope("read", "write")
            .execute();

        // Agora use o client para obter token
        var token = com.conexaoauthlib.fluent.oauth2.AuthClient
            .clientCredentials(client.getClientId(), client.getClientSecret())
            .scope("read", "write")
            .execute();

        System.out.println("Token obtido para o serviço: " + token.getAccessToken());
    }

    public static void main(String[] args) {
        registerClient();
        registerClientWithAutoSecret();
        rotateSecret();
        listClients();
        updateStatus();
        activateClient();
        findById();
        updateScopes();
        deleteClient();
        useClientForAuth();
    }
}
