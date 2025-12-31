package com.conexaoauthlib.fluent.client;

import com.conexaoauthlib.dto.client.ClientCreateRequestDTO;
import com.conexaoauthlib.dto.client.ClientFilterDTO;
import com.conexaoauthlib.dto.client.ClientResponseDTO;
import com.conexaoauthlib.dto.client.ClientSecretResponseDTO;
import com.conexaoauthlib.dto.client.ClientStatusDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Entry point fluente para operações de Client OAuth2.
 * Fornece uma API intuitiva para gerenciamento de clients M2M.
 *
 * <h3>Exemplo de registro:</h3>
 * <pre>{@code
 * ClientResponseDTO client = ClientClient.register()
 *     .clientId("my-service")
 *     .clientSecret("initial-secret")
 *     .name("My Service")
 *     .tenantId("tenant-123")
 *     .grantType("client_credentials")
 *     .scope("read", "write")
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de rotação de segredo:</h3>
 * <pre>{@code
 * ClientSecretResponseDTO secret = ClientClient.rotateSecret("client-id")
 *     .tenantId("tenant-123")
 *     .execute();
 *
 * System.out.println("Novo segredo: " + secret.getNewSecret());
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class ClientClient {

    private ClientClient() {
        // Utility class - não instanciar
    }

    // ==================== Register ====================

    /**
     * Inicia registro de um novo client.
     *
     * @return ClientCreateRequestBuilder para configuração
     */
    public static ClientCreateRequestBuilder register() {
        return new ClientCreateRequestBuilder();
    }

    /**
     * Builder para registro de client.
     */
    public static final class ClientCreateRequestBuilder {
        private String clientId;
        private String clientSecret;
        private String name;
        private String tenantId;
        private final List<String> grantTypes = new ArrayList<>();
        private final List<String> scopes = new ArrayList<>();
        private Integer accessTokenValiditySeconds;
        private Integer refreshTokenValiditySeconds;
        private String contextTenantId;

        /**
         * Define o clientId público do client.
         *
         * @param clientId Identificador público do client
         * @return this builder
         */
        public ClientCreateRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Define o segredo inicial do client.
         *
         * @param clientSecret Segredo do client
         * @return this builder
         */
        public ClientCreateRequestBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Define o nome amigável do client.
         *
         * @param name Nome do client
         * @return this builder
         */
        public ClientCreateRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o tenantId do client.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public ClientCreateRequestBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Adiciona um grant type ao client.
         * Pode ser chamado múltiplas vezes.
         *
         * @param grantType Grant type (client_credentials, password, refresh_token)
         * @return this builder
         */
        public ClientCreateRequestBuilder grantType(String grantType) {
            this.grantTypes.add(grantType);
            return this;
        }

        /**
         * Adiciona múltiplos grant types ao client.
         *
         * @param grantTypes Lista de grant types
         * @return this builder
         */
        public ClientCreateRequestBuilder grantTypes(List<String> grantTypes) {
            this.grantTypes.addAll(grantTypes);
            return this;
        }

        /**
         * Adiciona um escopo ao client.
         * Pode ser chamado múltiplas vezes.
         *
         * @param scope Escopo
         * @return this builder
         */
        public ClientCreateRequestBuilder scope(String scope) {
            this.scopes.add(scope);
            return this;
        }

        /**
         * Adiciona múltiplos escopos ao client.
         *
         * @param scopes Escopos
         * @return this builder
         */
        public ClientCreateRequestBuilder scopes(String... scopes) {
            this.scopes.addAll(List.of(scopes));
            return this;
        }

        /**
         * Define a validade do access token em segundos.
         *
         * @param seconds Tempo de validade
         * @return this builder
         */
        public ClientCreateRequestBuilder accessTokenValiditySeconds(Integer seconds) {
            this.accessTokenValiditySeconds = seconds;
            return this;
        }

        /**
         * Define a validade do refresh token em segundos.
         *
         * @param seconds Tempo de validade
         * @return this builder
         */
        public ClientCreateRequestBuilder refreshTokenValiditySeconds(Integer seconds) {
            this.refreshTokenValiditySeconds = seconds;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientCreateRequestBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa o registro do client.
         *
         * @return ClientResponseDTO com client criado
         */
        public ClientResponseDTO execute() {
            ClientCreateRequestDTO request = ClientCreateRequestDTO.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .name(name)
                .tenantId(tenantId)
                .grantTypes(grantTypes.isEmpty() ? null : grantTypes)
                .scopes(scopes.isEmpty() ? null : scopes)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .build();

            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();
            return feignClient.create(request, contextTenantId);
        }

        /**
         * Executa o registro de forma assíncrona.
         *
         * @return CompletableFuture com ClientResponseDTO
         */
        public CompletableFuture<ClientResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== List ====================

    /**
     * Inicia listagem de clients com filtros.
     *
     * @return ClientListRequestBuilder para configuração
     */
    public static ClientListRequestBuilder list() {
        return new ClientListRequestBuilder();
    }

    /**
     * Builder para listagem de clients.
     */
    public static final class ClientListRequestBuilder {
        private String tenantId;
        private String status;
        private String clientId;
        private String name;
        private Integer page = 0;
        private Integer size = 20;
        private String sort = "created_at,desc";
        private String contextTenantId;

        /**
         * Filtra pelo tenantId.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public ClientListRequestBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Filtra pelo status do client.
         *
         * @param status Status (ACTIVE, INACTIVE, SUSPENDED, PENDING_SECRET)
         * @return this builder
         */
        public ClientListRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Filtra pelo clientId público.
         *
         * @param clientId Identificador público do client
         * @return this builder
         */
        public ClientListRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Filtra pelo nome do client.
         *
         * @param name Nome ou parte do nome
         * @return this builder
         */
        public ClientListRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o número da página.
         *
         * @param page Número da página (inicia em 0)
         * @return this builder
         */
        public ClientListRequestBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        /**
         * Define o tamanho da página.
         *
         * @param size Quantidade de itens por página
         * @return this builder
         */
        public ClientListRequestBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        /**
         * Define a ordenação.
         *
         * @param sort Campo e direção (ex: "created_at,desc")
         * @return this builder
         */
        public ClientListRequestBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientListRequestBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a listagem com os filtros configurados.
         *
         * @return PageResponseDTO com lista de clients
         */
        public PageResponseDTO<ClientResponseDTO> execute() {
            ClientFilterDTO filter = ClientFilterDTO.builder()
                .tenantId(tenantId)
                .status(status)
                .clientId(clientId)
                .name(name)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();
            return feignClient.list(filter, contextTenantId);
        }
    }

    // ==================== Get By ID ====================

    /**
     * Inicia busca de client por ID interno.
     *
     * @param id ID interno do client
     * @return ClientGetBuilder para operações adicionais
     */
    public static ClientGetBuilder getById(String id) {
        return new ClientGetBuilder(id, false);
    }

    /**
     * Inicia busca de client por clientId público.
     *
     * @param clientId Identificador público do client
     * @return ClientGetBuilder para operações adicionais
     */
    public static ClientGetBuilder getByClientId(String clientId) {
        return new ClientGetBuilder(clientId, true);
    }

    /**
     * Builder para operações após GET de client.
     */
    public static final class ClientGetBuilder {
        private final String id;
        private final boolean byClientId;
        private String contextTenantId;

        ClientGetBuilder(String id, boolean byClientId) {
            this.id = id;
            this.byClientId = byClientId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientGetBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a busca do client.
         *
         * @return ClientResponseDTO com dados do client
         */
        public ClientResponseDTO execute() {
            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();

            if (byClientId) {
                return feignClient.getByClientId(id, contextTenantId);
            }
            return feignClient.getById(id, contextTenantId);
        }

        /**
         * Atualiza status do client.
         *
         * @param status Novo status
         * @return ClientResponseDTO atualizado
         */
        public ClientResponseDTO updateStatus(String status) {
            return ClientClient.updateStatus(id)
                .status(status)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Rotaciona o segredo do client.
         *
         * @return ClientSecretResponseDTO com novo segredo
         */
        public ClientSecretResponseDTO rotateSecret() {
            return ClientClient.rotateSecret(id)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Remove o client.
         */
        public void delete() {
            ClientClient.delete(id)
                .tenant(contextTenantId)
                .execute();
        }
    }

    // ==================== Rotate Secret ====================

    /**
     * Inicia rotação de segredo de client.
     *
     * @param clientId ID do client (interno ou público)
     * @return ClientSecretRotateBuilder para configuração
     */
    public static ClientSecretRotateBuilder rotateSecret(String clientId) {
        return new ClientSecretRotateBuilder(clientId);
    }

    /**
     * Builder para rotação de segredo.
     */
    public static final class ClientSecretRotateBuilder {
        private final String clientId;
        private String contextTenantId;

        ClientSecretRotateBuilder(String clientId) {
            this.clientId = clientId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientSecretRotateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a rotação de segredo.
         *
         * @return ClientSecretResponseDTO com novo segredo
         */
        public ClientSecretResponseDTO execute() {
            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();
            return feignClient.regenerateSecret(clientId, contextTenantId);
        }
    }

    // ==================== Update Status ====================

    /**
     * Inicia atualização de status de client.
     *
     * @param clientId ID do client
     * @return ClientStatusUpdateBuilder para configuração
     */
    public static ClientStatusUpdateBuilder updateStatus(String clientId) {
        return new ClientStatusUpdateBuilder(clientId);
    }

    /**
     * Builder para atualização de status.
     */
    public static final class ClientStatusUpdateBuilder {
        private final String clientId;
        private String status;
        private String contextTenantId;

        ClientStatusUpdateBuilder(String clientId) {
            this.clientId = clientId;
        }

        /**
         * Define o novo status do client.
         *
         * @param status Novo status (ACTIVE, INACTIVE, SUSPENDED, PENDING_SECRET)
         * @return this builder
         */
        public ClientStatusUpdateBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientStatusUpdateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atualização de status.
         *
         * @return ClientResponseDTO com client atualizado
         */
        public ClientResponseDTO execute() {
            ClientStatusDTO request = ClientStatusDTO.builder()
                .status(status)
                .build();

            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();
            return feignClient.updateStatus(clientId, request, contextTenantId);
        }
    }

    // ==================== Delete ====================

    /**
     * Inicia remoção de client.
     *
     * @param clientId ID do client
     * @return ClientDeleteBuilder para configuração
     */
    public static ClientDeleteBuilder delete(String clientId) {
        return new ClientDeleteBuilder(clientId);
    }

    /**
     * Builder para remoção de client.
     */
    public static final class ClientDeleteBuilder {
        private final String clientId;
        private String contextTenantId;

        ClientDeleteBuilder(String clientId) {
            this.clientId = clientId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ClientDeleteBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a remoção do client.
         */
        public void execute() {
            com.conexaoauthlib.feign.client.ClientClient feignClient =
                ClientClientFactory.createClientClient();
            feignClient.delete(clientId, contextTenantId);
        }
    }
}
