package com.conexaoauthlib.fluent.user;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.role.RoleAssignRequestDTO;
import com.conexaoauthlib.dto.user.UserCreateRequestDTO;
import com.conexaoauthlib.dto.user.UserFilterDTO;
import com.conexaoauthlib.dto.user.UserPasswordRequestDTO;
import com.conexaoauthlib.dto.user.UserResponseDTO;
import com.conexaoauthlib.dto.user.UserStatusDTO;
import com.conexaoauthlib.dto.user.UserUpdateRequestDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Entry point fluente para operações de Usuário.
 * Fornece uma API intuitiva para gerenciamento completo de usuários.
 *
 * <h3>Exemplo de registro:</h3>
 * <pre>{@code
 * UserResponseDTO user = UserClient.register()
 *     .name("João Silva")
 *     .email("joao@empresa.com")
 *     .password("Senha123!")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de mudança de senha:</h3>
 * <pre>{@code
 * UserClient.changePassword("user-id")
 *     .currentPassword("Senha123!")
 *     .newPassword("NovaSenha456!")
 *     .execute();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class UserClient {

    private UserClient() {
        // Utility class - não instanciar
    }

    // ==================== Register ====================

    /**
     * Inicia registro de um novo usuário.
     *
     * @return UserCreateRequestBuilder para configuração
     */
    public static UserCreateRequestBuilder register() {
        return new UserCreateRequestBuilder();
    }

    /**
     * Builder para registro de usuário.
     */
    public static final class UserCreateRequestBuilder {
        private String name;
        private String email;
        private String password;
        private String tenantId;
        private String contextTenantId;

        /**
         * Define o nome do usuário.
         *
         * @param name Nome completo
         * @return this builder
         */
        public UserCreateRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o email do usuário.
         *
         * @param email Email válido
         * @return this builder
         */
        public UserCreateRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Define a senha do usuário.
         *
         * @param password Senha inicial
         * @return this builder
         */
        public UserCreateRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Define o tenantId do usuário.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public UserCreateRequestBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserCreateRequestBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa o registro do usuário.
         *
         * @return UserResponseDTO com usuário criado
         */
        public UserResponseDTO execute() {
            UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .tenantId(tenantId)
                .build();

            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            return feignClient.create(request, contextTenantId);
        }

        /**
         * Executa o registro de forma assíncrona.
         *
         * @return CompletableFuture com UserResponseDTO
         */
        public CompletableFuture<UserResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== List ====================

    /**
     * Inicia listagem de usuários com filtros.
     *
     * @return UserListRequestBuilder para configuração
     */
    public static UserListRequestBuilder list() {
        return new UserListRequestBuilder();
    }

    /**
     * Builder para listagem de usuários.
     */
    public static final class UserListRequestBuilder {
        private String name;
        private String email;
        private String status;
        private String roleId;
        private String tenantId;
        private Integer page = 0;
        private Integer size = 20;
        private String sort = "created_at,desc";
        private String contextTenantId;

        /**
         * Filtra pelo nome do usuário.
         *
         * @param name Nome ou parte do nome
         * @return this builder
         */
        public UserListRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Filtra pelo email do usuário.
         *
         * @param email Email ou parte do email
         * @return this builder
         */
        public UserListRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Filtra pelo status do usuário.
         *
         * @param status Status (ACTIVE, INACTIVE, LOCKED, PENDING_VERIFICATION, SUSPENDED)
         * @return this builder
         */
        public UserListRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Filtra pela role do usuário.
         *
         * @param roleId ID da role
         * @return this builder
         */
        public UserListRequestBuilder roleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        /**
         * Filtra pelo tenantId do usuário.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public UserListRequestBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Define o número da página.
         *
         * @param page Número da página (inicia em 0)
         * @return this builder
         */
        public UserListRequestBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        /**
         * Define o tamanho da página.
         *
         * @param size Quantidade de itens por página
         * @return this builder
         */
        public UserListRequestBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        /**
         * Define a ordenação.
         *
         * @param sort Campo e direção (ex: "created_at,desc")
         * @return this builder
         */
        public UserListRequestBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserListRequestBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a listagem com os filtros configurados.
         *
         * @return PageResponseDTO com lista de usuários
         */
        public PageResponseDTO<UserResponseDTO> execute() {
            UserFilterDTO filter = UserFilterDTO.builder()
                .name(name)
                .email(email)
                .status(status)
                .roleId(roleId)
                .tenantId(tenantId)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            return feignClient.list(filter, contextTenantId);
        }
    }

    // ==================== Get ====================

    /**
     * Inicia busca de usuário por ID.
     *
     * @param userId ID do usuário
     * @return UserGetBuilder para operações adicionais
     */
    public static UserGetBuilder get(String userId) {
        return new UserGetBuilder(userId);
    }

    /**
     * Builder para operações após GET de usuário.
     */
    public static final class UserGetBuilder {
        private final String userId;
        private String contextTenantId;

        UserGetBuilder(String userId) {
            this.userId = userId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserGetBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a busca do usuário.
         *
         * @return UserResponseDTO com dados do usuário
         */
        public UserResponseDTO execute() {
            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            return feignClient.getById(userId, contextTenantId);
        }

        /**
         * Atualiza dados parciais do usuário.
         *
         * @param name Novo nome (opcional)
         * @param email Novo email (opcional)
         * @return UserResponseDTO atualizado
         */
        public UserResponseDTO update(String name, String email) {
            return UserClient.update(userId)
                .name(name)
                .email(email)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Atualiza status do usuário.
         *
         * @param status Novo status
         * @return UserResponseDTO atualizado
         */
        public UserResponseDTO updateStatus(String status) {
            return UserClient.updateStatus(userId)
                .status(status)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Altera a senha do usuário.
         *
         * @param currentPassword Senha atual
         * @param newPassword Nova senha
         */
        public void changePassword(String currentPassword, String newPassword) {
            UserClient.changePassword(userId)
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .tenant(contextTenantId)
                .execute();
        }

        /**
         * Atribui roles ao usuário.
         *
         * @param roleIds IDs das roles
         * @return UserResponseDTO atualizado
         */
        public UserResponseDTO assignRoles(List<String> roleIds) {
            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();

            RoleAssignRequestDTO request = RoleAssignRequestDTO.builder()
                .roleIds(roleIds)
                .build();

            return feignClient.assignRoles(userId, request, contextTenantId);
        }

        /**
         * Desativa o usuário.
         */
        public void deactivate() {
            UserClient.deactivate(userId)
                .tenant(contextTenantId)
                .execute();
        }
    }

    // ==================== Update ====================

    /**
     * Inicia atualização parcial de usuário.
     *
     * @param userId ID do usuário
     * @return UserUpdateBuilder para configuração
     */
    public static UserUpdateBuilder update(String userId) {
        return new UserUpdateBuilder(userId);
    }

    /**
     * Builder para atualização parcial.
     */
    public static final class UserUpdateBuilder {
        private final String userId;
        private String name;
        private String email;
        private String contextTenantId;

        UserUpdateBuilder(String userId) {
            this.userId = userId;
        }

        /**
         * Define o novo nome.
         *
         * @param name Nome
         * @return this builder
         */
        public UserUpdateBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o novo email.
         *
         * @param email Email
         * @return this builder
         */
        public UserUpdateBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserUpdateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atualização parcial.
         *
         * @return UserResponseDTO com usuário atualizado
         */
        public UserResponseDTO execute() {
            UserUpdateRequestDTO request = UserUpdateRequestDTO.builder()
                .name(name)
                .email(email)
                .build();

            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            return feignClient.update(userId, request, contextTenantId);
        }
    }

    // ==================== Change Password ====================

    /**
     * Inicia mudança de senha de usuário.
     *
     * @param userId ID do usuário
     * @return PasswordChangerBuilder para configuração
     */
    public static PasswordChangerBuilder changePassword(String userId) {
        return new PasswordChangerBuilder(userId);
    }

    /**
     * Builder para mudança de senha.
     */
    public static final class PasswordChangerBuilder {
        private final String userId;
        private String currentPassword;
        private String newPassword;
        private String contextTenantId;

        PasswordChangerBuilder(String userId) {
            this.userId = userId;
        }

        /**
         * Define a senha atual.
         *
         * @param currentPassword Senha atual
         * @return this builder
         */
        public PasswordChangerBuilder currentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
            return this;
        }

        /**
         * Define a nova senha.
         *
         * @param newPassword Nova senha
         * @return this builder
         */
        public PasswordChangerBuilder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public PasswordChangerBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a mudança de senha.
         */
        public void execute() {
            UserPasswordRequestDTO request = UserPasswordRequestDTO.builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .build();

            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            feignClient.changePassword(userId, request, contextTenantId);
        }
    }

    // ==================== Update Status ====================

    /**
     * Inicia atualização de status de usuário.
     *
     * @param userId ID do usuário
     * @return UserStatusUpdateBuilder para configuração
     */
    public static UserStatusUpdateBuilder updateStatus(String userId) {
        return new UserStatusUpdateBuilder(userId);
    }

    /**
     * Builder para atualização de status.
     */
    public static final class UserStatusUpdateBuilder {
        private final String userId;
        private String status;
        private String reason;
        private String contextTenantId;

        UserStatusUpdateBuilder(String userId) {
            this.userId = userId;
        }

        /**
         * Define o novo status do usuário.
         *
         * @param status Novo status (ACTIVE, INACTIVE, LOCKED, PENDING_VERIFICATION, SUSPENDED)
         * @return this builder
         */
        public UserStatusUpdateBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Define a razão da mudança de status.
         *
         * @param reason Motivo da alteração
         * @return this builder
         */
        public UserStatusUpdateBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserStatusUpdateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atualização de status.
         *
         * @return UserResponseDTO com usuário atualizado
         */
        public UserResponseDTO execute() {
            UserStatusDTO request = UserStatusDTO.builder()
                .status(status)
                .reason(reason)
                .build();

            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            return feignClient.updateStatus(userId, request, contextTenantId);
        }
    }

    // ==================== Deactivate ====================

    /**
     * Inicia desativação de usuário.
     *
     * @param userId ID do usuário
     * @return UserDeactivateBuilder para configuração
     */
    public static UserDeactivateBuilder deactivate(String userId) {
        return new UserDeactivateBuilder(userId);
    }

    /**
     * Builder para desativação de usuário.
     */
    public static final class UserDeactivateBuilder {
        private final String userId;
        private String contextTenantId;

        UserDeactivateBuilder(String userId) {
            this.userId = userId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public UserDeactivateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a desativação do usuário.
         */
        public void execute() {
            com.conexaoauthlib.feign.user.UserClient feignClient =
                UserClientFactory.createUserClient();
            feignClient.deactivate(userId, contextTenantId);
        }
    }
}
