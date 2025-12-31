package com.conexaoauthlib.fluent.role;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.role.RoleCreateRequestDTO;
import com.conexaoauthlib.dto.role.RoleFilterDTO;
import com.conexaoauthlib.dto.role.RoleResponseDTO;
import com.conexaoauthlib.dto.role.RoleStatusDTO;
import com.conexaoauthlib.dto.scope.ScopeAssignRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Entry point fluente para operações de Role.
 * Fornece uma API intuitiva para gerenciamento de roles e permissões.
 *
 * <h3>Exemplo de criação de role com scopes:</h3>
 * <pre>{@code
 * RoleResponseDTO role = RoleClient.create()
 *     .name("admin")
 *     .description("Administrador do sistema")
 *     .tenantId("tenant-123")
 *     .scopeId("scope-1")
 *     .scopeId("scope-2")
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de atribuição de scopes:</h3>
 * <pre>{@code
 * RoleClient.assignScopes("role-id")
 *     .scopeId("scope-1")
 *     .scopeId("scope-2")
 *     .execute();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class RoleClient {

    private RoleClient() {
        // Utility class - não instanciar
    }

    // ==================== Create ====================

    /**
     * Inicia criação de uma nova role.
     *
     * @return RoleCreateBuilder para configuração
     */
    public static RoleCreateBuilder create() {
        return new RoleCreateBuilder();
    }

    /**
     * Builder para criação de role.
     */
    public static final class RoleCreateBuilder {
        private String name;
        private String description;
        private String tenantId;
        private final List<String> scopeIds = new ArrayList<>();
        private String contextTenantId;

        /**
         * Define o nome da role.
         *
         * @param name Nome da role
         * @return this builder
         */
        public RoleCreateBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define a descrição da role.
         *
         * @param description Descrição
         * @return this builder
         */
        public RoleCreateBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Define o tenantId da role.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public RoleCreateBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Adiciona um scope à role durante criação.
         *
         * @param scopeId ID do scope
         * @return this builder
         */
        public RoleCreateBuilder scopeId(String scopeId) {
            this.scopeIds.add(scopeId);
            return this;
        }

        /**
         * Adiciona múltiplos scopes à role durante criação.
         *
         * @param scopeIds IDs dos scopes
         * @return this builder
         */
        public RoleCreateBuilder scopeIds(List<String> scopeIds) {
            this.scopeIds.addAll(scopeIds);
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleCreateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a criação da role.
         *
         * @return RoleResponseDTO com role criada
         */
        public RoleResponseDTO execute() {
            RoleCreateRequestDTO request = RoleCreateRequestDTO.builder()
                .name(name)
                .description(description)
                .tenantId(tenantId)
                .scopeIds(scopeIds.isEmpty() ? null : scopeIds)
                .build();

            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.create(request, contextTenantId);
        }

        /**
         * Executa a criação de forma assíncrona.
         *
         * @return CompletableFuture com RoleResponseDTO
         */
        public CompletableFuture<RoleResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== List ====================

    /**
     * Inicia listagem de roles com filtros.
     *
     * @return RoleListBuilder para configuração
     */
    public static RoleListBuilder list() {
        return new RoleListBuilder();
    }

    /**
     * Builder para listagem de roles.
     */
    public static final class RoleListBuilder {
        private String name;
        private String status;
        private String tenantId;
        private Boolean includeScopes = false;
        private Integer page = 0;
        private Integer size = 20;
        private String sort = "name,asc";
        private String contextTenantId;

        /**
         * Filtra pelo nome da role.
         *
         * @param name Nome ou parte do nome
         * @return this builder
         */
        public RoleListBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Filtra pelo status da role.
         *
         * @param status Status (ACTIVE, INACTIVE)
         * @return this builder
         */
        public RoleListBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Filtra pelo tenantId.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public RoleListBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Define se deve incluir scopes na resposta.
         *
         * @param includeScopes Se true, inclui lista de scopes
         * @return this builder
         */
        public RoleListBuilder includeScopes(Boolean includeScopes) {
            this.includeScopes = includeScopes;
            return this;
        }

        /**
         * Define o número da página.
         *
         * @param page Número da página (inicia em 0)
         * @return this builder
         */
        public RoleListBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        /**
         * Define o tamanho da página.
         *
         * @param size Quantidade de itens por página
         * @return this builder
         */
        public RoleListBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        /**
         * Define a ordenação.
         *
         * @param sort Campo e direção (ex: "name,asc")
         * @return this builder
         */
        public RoleListBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleListBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a listagem com os filtros configurados.
         *
         * @return PageResponseDTO com lista de roles
         */
        public PageResponseDTO<RoleResponseDTO> execute() {
            RoleFilterDTO filter = RoleFilterDTO.builder()
                .name(name)
                .status(status)
                .tenantId(tenantId)
                .includeScopes(includeScopes)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.list(filter, contextTenantId);
        }
    }

    // ==================== Get ====================

    /**
     * Inicia busca de role por ID.
     *
     * @param roleId ID da role
     * @return RoleGetBuilder para operações adicionais
     */
    public static RoleGetBuilder get(String roleId) {
        return new RoleGetBuilder(roleId);
    }

    /**
     * Builder para operações após GET de role.
     */
    public static final class RoleGetBuilder {
        private final String roleId;
        private String contextTenantId;

        RoleGetBuilder(String roleId) {
            this.roleId = roleId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleGetBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a busca da role.
         *
         * @return RoleResponseDTO com dados da role
         */
        public RoleResponseDTO execute() {
            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.getById(roleId, contextTenantId);
        }

        /**
         * Atribui scopes à role.
         *
         * @param scopeIds IDs dos scopes
         * @return RoleResponseDTO atualizado
         */
        public RoleResponseDTO assignScopes(List<String> scopeIds) {
            return RoleClient.assignScopes(roleId)
                .scopeIds(scopeIds)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Remove scopes da role.
         *
         * @param scopeIds IDs dos scopes
         * @return RoleResponseDTO atualizado
         */
        public RoleResponseDTO removeScopes(List<String> scopeIds) {
            return RoleClient.removeScopes(roleId)
                .scopeIds(scopeIds)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Atualiza status da role.
         *
         * @param status Novo status
         * @return RoleResponseDTO atualizado
         */
        public RoleResponseDTO updateStatus(String status) {
            return RoleClient.updateStatus(roleId)
                .status(status)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Desativa a role.
         */
        public void deactivate() {
            RoleClient.deactivate(roleId)
                .tenant(contextTenantId)
                .execute();
        }
    }

    // ==================== Assign Scopes ====================

    /**
     * Inicia atribuição de scopes a uma role.
     *
     * @param roleId ID da role
     * @return RoleScopeAssignBuilder para configuração
     */
    public static RoleScopeAssignBuilder assignScopes(String roleId) {
        return new RoleScopeAssignBuilder(roleId);
    }

    /**
     * Builder para atribuição de scopes.
     */
    public static final class RoleScopeAssignBuilder {
        private final String roleId;
        private final List<String> scopeIds = new ArrayList<>();
        private String contextTenantId;

        RoleScopeAssignBuilder(String roleId) {
            this.roleId = roleId;
        }

        /**
         * Adiciona um scope para atribuição.
         *
         * @param scopeId ID do scope
         * @return this builder
         */
        public RoleScopeAssignBuilder scopeId(String scopeId) {
            this.scopeIds.add(scopeId);
            return this;
        }

        /**
         * Adiciona múltiplos scopes para atribuição.
         *
         * @param scopeIds IDs dos scopes
         * @return this builder
         */
        public RoleScopeAssignBuilder scopeIds(List<String> scopeIds) {
            this.scopeIds.addAll(scopeIds);
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleScopeAssignBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atribuição de scopes.
         *
         * @return RoleResponseDTO com role atualizada
         */
        public RoleResponseDTO execute() {
            ScopeAssignRequestDTO request = ScopeAssignRequestDTO.builder()
                .scopeIds(scopeIds)
                .build();

            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.assignScopes(roleId, request, contextTenantId);
        }
    }

    // ==================== Remove Scopes ====================

    /**
     * Inicia remoção de scopes de uma role.
     *
     * @param roleId ID da role
     * @return RoleScopeRemoveBuilder para configuração
     */
    public static RoleScopeRemoveBuilder removeScopes(String roleId) {
        return new RoleScopeRemoveBuilder(roleId);
    }

    /**
     * Builder para remoção de scopes.
     */
    public static final class RoleScopeRemoveBuilder {
        private final String roleId;
        private final List<String> scopeIds = new ArrayList<>();
        private String contextTenantId;

        RoleScopeRemoveBuilder(String roleId) {
            this.roleId = roleId;
        }

        /**
         * Adiciona um scope para remoção.
         *
         * @param scopeId ID do scope
         * @return this builder
         */
        public RoleScopeRemoveBuilder scopeId(String scopeId) {
            this.scopeIds.add(scopeId);
            return this;
        }

        /**
         * Adiciona múltiplos scopes para remoção.
         *
         * @param scopeIds IDs dos scopes
         * @return this builder
         */
        public RoleScopeRemoveBuilder scopeIds(List<String> scopeIds) {
            this.scopeIds.addAll(scopeIds);
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleScopeRemoveBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a remoção de scopes.
         *
         * @return RoleResponseDTO com role atualizada
         */
        public RoleResponseDTO execute() {
            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.removeScopes(roleId, scopeIds, contextTenantId);
        }
    }

    // ==================== Update Status ====================

    /**
     * Inicia atualização de status de role.
     *
     * @param roleId ID da role
     * @return RoleStatusUpdateBuilder para configuração
     */
    public static RoleStatusUpdateBuilder updateStatus(String roleId) {
        return new RoleStatusUpdateBuilder(roleId);
    }

    /**
     * Builder para atualização de status.
     */
    public static final class RoleStatusUpdateBuilder {
        private final String roleId;
        private String status;
        private String reason;
        private String contextTenantId;

        RoleStatusUpdateBuilder(String roleId) {
            this.roleId = roleId;
        }

        /**
         * Define o novo status da role.
         *
         * @param status Novo status (ACTIVE, INACTIVE)
         * @return this builder
         */
        public RoleStatusUpdateBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Define a razão da mudança de status.
         *
         * @param reason Motivo da alteração
         * @return this builder
         */
        public RoleStatusUpdateBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleStatusUpdateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atualização de status.
         *
         * @return RoleResponseDTO com role atualizada
         */
        public RoleResponseDTO execute() {
            RoleStatusDTO request = RoleStatusDTO.builder()
                .status(status)
                .reason(reason)
                .build();

            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            return feignClient.updateStatus(roleId, request, contextTenantId);
        }
    }

    // ==================== Deactivate ====================

    /**
     * Inicia desativação de role.
     *
     * @param roleId ID da role
     * @return RoleDeactivateBuilder para configuração
     */
    public static RoleDeactivateBuilder deactivate(String roleId) {
        return new RoleDeactivateBuilder(roleId);
    }

    /**
     * Builder para desativação de role.
     */
    public static final class RoleDeactivateBuilder {
        private final String roleId;
        private String contextTenantId;

        RoleDeactivateBuilder(String roleId) {
            this.roleId = roleId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public RoleDeactivateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a desativação da role.
         */
        public void execute() {
            com.conexaoauthlib.feign.role.RoleClient feignClient =
                RoleClientFactory.createRoleClient();
            feignClient.delete(roleId, contextTenantId);
        }
    }
}
