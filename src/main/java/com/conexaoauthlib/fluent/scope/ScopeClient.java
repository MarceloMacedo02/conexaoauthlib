package com.conexaoauthlib.fluent.scope;

import com.conexaoauthlib.dto.scope.ScopeCreateRequestDTO;
import com.conexaoauthlib.dto.scope.ScopeFilterDTO;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entry point fluente para operações de Scope.
 * Fornece uma API intuitiva para gerenciamento de scopes.
 *
 * <h3>Exemplo de criação:</h3>
 * <pre>{@code
 * ScopeResponseDTO scope = ScopeClient.create()
 *     .name("users:read")
 *     .description("Permissão para leitura de usuários")
 *     .resource("users")
 *     .action("read")
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de listagem:</h3>
 * <pre>{@code
 * List<ScopeResponseDTO> scopes = ScopeClient.list()
 *     .resource("users")
 *     .execute();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class ScopeClient {

    private ScopeClient() {
        // Utility class - não instanciar
    }

    // ==================== Create ====================

    /**
     * Inicia criação de um novo scope.
     *
     * @return ScopeCreateBuilder para configuração
     */
    public static ScopeCreateBuilder create() {
        return new ScopeCreateBuilder();
    }

    /**
     * Builder para criação de scope.
     */
    public static final class ScopeCreateBuilder {
        private String name;
        private String description;
        private String resource;
        private String action;
        private String contextTenantId;

        /**
         * Define o nome do scope (formato: resource:action).
         *
         * @param name Nome do scope
         * @return this builder
         */
        public ScopeCreateBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define a descrição do scope.
         *
         * @param description Descrição
         * @return this builder
         */
        public ScopeCreateBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Define o recurso do scope.
         *
         * @param resource Recurso (ex: "users", "products")
         * @return this builder
         */
        public ScopeCreateBuilder resource(String resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Define a ação do scope.
         *
         * @param action Ação (ex: "read", "write", "delete")
         * @return this builder
         */
        public ScopeCreateBuilder action(String action) {
            this.action = action;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ScopeCreateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a criação do scope.
         *
         * <p>Se o nome não for fornecido mas resource e action forem,
         * o nome será construído automaticamente como "resource:action".</p>
         *
         * @return ScopeResponseDTO com scope criado
         */
        public ScopeResponseDTO execute() {
            // Se nome não fornecido, constrói a partir de resource:action
            String scopeName = name;
            if (scopeName == null && resource != null && action != null) {
                scopeName = resource + ":" + action;
            }

            ScopeCreateRequestDTO request = ScopeCreateRequestDTO.builder()
                .name(scopeName)
                .description(description)
                .resource(resource)
                .action(action)
                .build();

            com.conexaoauthlib.feign.scope.ScopeClient feignClient =
                ScopeClientFactory.createScopeClient();
            return feignClient.create(request, contextTenantId);
        }
    }

    // ==================== List ====================

    /**
     * Inicia listagem de scopes.
     *
     * @return ScopeListBuilder para configuração
     */
    public static ScopeListBuilder list() {
        return new ScopeListBuilder();
    }

    /**
     * Builder para listagem de scopes.
     */
    public static final class ScopeListBuilder {
        private String resource;
        private String action;
        private String name;
        private String contextTenantId;

        /**
         * Filtra pelo recurso do scope.
         *
         * @param resource Recurso
         * @return this builder
         */
        public ScopeListBuilder resource(String resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Filtra pela ação do scope.
         *
         * @param action Ação
         * @return this builder
         */
        public ScopeListBuilder action(String action) {
            this.action = action;
            return this;
        }

        /**
         * Filtra pelo nome do scope.
         *
         * @param name Nome ou parte do nome
         * @return this builder
         */
        public ScopeListBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ScopeListBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a listagem com os filtros configurados.
         *
         * @return List com scopes
         */
        public List<ScopeResponseDTO> execute() {
            ScopeFilterDTO filter = ScopeFilterDTO.builder()
                .resource(resource)
                .action(action)
                .name(name)
                .build();

            com.conexaoauthlib.feign.scope.ScopeClient feignClient =
                ScopeClientFactory.createScopeClient();
            return feignClient.list(filter, contextTenantId);
        }

        /**
         * Executa e retorna como lista de nomes de scopes.
         *
         * @return List<String> com nomes dos scopes
         */
        public List<String> executeAsNames() {
            return execute().stream()
                .map(ScopeResponseDTO::getName)
                .collect(Collectors.toList());
        }
    }

    // ==================== Get ====================

    /**
     * Inicia busca de scope por ID.
     *
     * @param scopeId ID do scope
     * @return ScopeGetBuilder para operações adicionais
     */
    public static ScopeGetBuilder get(String scopeId) {
        return new ScopeGetBuilder(scopeId);
    }

    /**
     * Builder para operações após GET de scope.
     */
    public static final class ScopeGetBuilder {
        private final String scopeId;
        private String contextTenantId;

        ScopeGetBuilder(String scopeId) {
            this.scopeId = scopeId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public ScopeGetBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a busca do scope.
         *
         * @return ScopeResponseDTO com dados do scope
         */
        public ScopeResponseDTO execute() {
            com.conexaoauthlib.feign.scope.ScopeClient feignClient =
                ScopeClientFactory.createScopeClient();
            return feignClient.getById(scopeId, contextTenantId);
        }

        /**
         * Atualiza o scope.
         *
         * @param description Nova descrição
         * @return ScopeResponseDTO atualizado
         */
        public ScopeResponseDTO update(String description) {
            com.conexaoauthlib.feign.scope.ScopeClient feignClient =
                ScopeClientFactory.createScopeClient();

            ScopeResponseDTO current = execute();

            ScopeCreateRequestDTO request = ScopeCreateRequestDTO.builder()
                .name(current.getName())
                .description(description)
                .resource(current.getResource())
                .action(current.getAction())
                .build();

            return feignClient.update(scopeId, request, contextTenantId);
        }

        /**
         * Remove o scope.
         */
        public void delete() {
            com.conexaoauthlib.feign.scope.ScopeClient feignClient =
                ScopeClientFactory.createScopeClient();
            feignClient.delete(scopeId, contextTenantId);
        }
    }
}
