package com.conexaoauthlib.examples;

import com.conexaoauthlib.fluent.scope.ScopeClient;
import com.conexaoauthlib.fluent.role.RoleClient;
import com.conexaoauthlib.dto.scope.ScopeResponseDTO;
import com.conexaoauthlib.dto.role.RoleResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

import java.util.List;

/**
 * Exemplos de gerenciamento de Roles e Scopes.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class RoleScopeManagementExample {

    /**
     * Exemplo de criação de scopes.
     */
    public static void createScopes() {
        System.out.println("=== Criar Scopes ===");

        // Criar scope para leitura
        ScopeResponseDTO readUsers = ScopeClient.create()
            .name("users:read")
            .description("Permissão para leitura de usuários")
            .resource("users")
            .action("read")
            .execute();

        // Criar scope para escrita
        ScopeResponseDTO writeUsers = ScopeClient.create()
            .name("users:write")
            .description("Permissão para escrita de usuários")
            .resource("users")
            .action("write")
            .execute();

        // Criar scope para deleção
        ScopeResponseDTO deleteUsers = ScopeClient.create()
            .name("users:delete")
            .description("Permissão para deletar usuários")
            .resource("users")
            .action("delete")
            .execute();

        System.out.println("Scopes criados:");
        System.out.println("  " + readUsers.getName() + ": " + readUsers.getId());
        System.out.println("  " + writeUsers.getName() + ": " + writeUsers.getId());
        System.out.println("  " + deleteUsers.getName() + ": " + deleteUsers.getId());
    }

    /**
     * Exemplo de listagem de scopes.
     */
    public static void listScopes() {
        System.out.println("\n=== Listar Scopes ===");

        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .resource("users")
            .execute();

        System.out.println("Scopes de 'users':");
        scopes.forEach(scope ->
            System.out.println("  - " + scope.getName() + ": " + scope.getDescription())
        );
    }

    /**
     * Exemplo de listagem de todos os scopes.
     */
    public static void listAllScopes() {
        System.out.println("\n=== Listar Todos os Scopes ===");

        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .execute();

        System.out.println("Total de scopes: " + scopes.size());
        scopes.forEach(scope ->
            System.out.println("  - " + scope.getName() + " (" + scope.getResource() + ":" + scope.getAction() + ")")
        );
    }

    /**
     * Exemplo de busca de scope por ID.
     */
    public static void findScopeById() {
        System.out.println("\n=== Buscar Scope por ID ===");

        ScopeResponseDTO scope = ScopeClient.getById("scope-id")
            .execute();

        System.out.println("Scope encontrado:");
        System.out.println("  Nome: " + scope.getName());
        System.out.println("  Descrição: " + scope.getDescription());
        System.out.println("  Recurso: " + scope.getResource());
        System.out.println("  Ação: " + scope.getAction());
    }

    /**
     * Exemplo de criação de role com scopes.
     */
    public static void createRoleWithScopes() {
        System.out.println("\n=== Criar Role com Scopes ===");

        // Primeiro, liste os scopes para obter IDs
        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .resource("users")
            .execute();

        List<String> scopeIds = scopes.stream()
            .map(ScopeResponseDTO::getId)
            .toList();

        // Crie a role com os scopes
        RoleResponseDTO adminRole = RoleClient.create()
            .name("admin")
            .description("Administrador com acesso total aos usuários")
            .tenantId("tenant-123")
            .scopeIds(scopeIds)
            .execute();

        System.out.println("Role criada:");
        System.out.println("  Nome: " + adminRole.getName());
        System.out.println("  Scopes: " + adminRole.getScopes());
    }

    /**
     * Exemplo de criação de role simples.
     */
    public static void createSimpleRole() {
        System.out.println("\n=== Criar Role Simples ===");

        RoleResponseDTO role = RoleClient.create()
            .name("viewer")
            .description("Visualizador somente leitura")
            .tenantId("tenant-123")
            .execute();

        System.out.println("Role criada:");
        System.out.println("  ID: " + role.getId());
        System.out.println("  Nome: " + role.getName());
    }

    /**
     * Exemplo de atribuição de scopes a role existente.
     */
    public static void assignScopesToRole() {
        System.out.println("\n=== Atribuir Scopes a Role ===");

        // Obter IDs dos scopes
        List<ScopeResponseDTO> scopes = ScopeClient.list()
            .resource("orders")
            .execute();

        List<String> scopeIds = scopes.stream()
            .map(ScopeResponseDTO::getId)
            .toList();

        // Atribuir à role
        RoleResponseDTO updatedRole = RoleClient.assignScopes("role-id")
            .scopeIds(scopeIds)
            .execute();

        System.out.println("Scopes atribuídos à role: " + updatedRole.getName());
        System.out.println("Total de scopes: " + updatedRole.getScopes().size());
    }

    /**
     * Exemplo de remoção de scopes de role.
     */
    public static void removeScopesFromRole() {
        System.out.println("\n=== Remover Scopes de Role ===");

        List<String> scopeIdsToRemove = List.of("scope-id-1", "scope-id-2");

        RoleResponseDTO updatedRole = RoleClient.removeScopes("role-id")
            .scopeIds(scopeIdsToRemove)
            .execute();

        System.out.println("Scopes removidos da role: " + updatedRole.getName());
        System.out.println("Scopes restantes: " + updatedRole.getScopes().size());
    }

    /**
     * Exemplo de listagem de roles.
     */
    public static void listRoles() {
        System.out.println("\n=== Listar Roles ===");

        PageResponseDTO<RoleResponseDTO> page = RoleClient.list()
            .tenantId("tenant-123")
            .includeScopes(true)
            .execute();

        System.out.println("Roles do tenant:");
        page.getContent().forEach(role -> {
            System.out.println("  - " + role.getName() + ": " + role.getDescription());
            System.out.println("    Scopes: " + role.getScopes());
        });
    }

    /**
     * Exemplo de busca de role por ID.
     */
    public static void findRoleById() {
        System.out.println("\n=== Buscar Role por ID ===");

        RoleResponseDTO role = RoleClient.getById("role-id")
            .execute();

        System.out.println("Role encontrada:");
        System.out.println("  Nome: " + role.getName());
        System.out.println("  Descrição: " + role.getDescription());
        System.out.println("  Scopes: " + role.getScopes());
    }

    /**
     * Exemplo de desativação de role.
     */
    public static void deactivateRole() {
        System.out.println("\n=== Desativar Role ===");

        RoleClient.deactivate("role-id")
            .execute();

        System.out.println("Role desativada!");
    }

    /**
     * Exemplo de ativação de role.
     */
    public static void activateRole() {
        System.out.println("\n=== Ativar Role ===");

        RoleResponseDTO activated = RoleClient.updateStatus("role-id")
            .status("ACTIVE")
            .execute();

        System.out.println("Role ativada: " + activated.getName());
    }

    /**
     * Exemplo de exclusão de role.
     */
    public static void deleteRole() {
        System.out.println("\n=== Excluir Role ===");

        RoleClient.delete("role-id")
            .execute();

        System.out.println("Role excluída com sucesso!");
    }

    public static void main(String[] args) {
        createScopes();
        listScopes();
        listAllScopes();
        findScopeById();
        createRoleWithScopes();
        createSimpleRole();
        assignScopesToRole();
        removeScopesFromRole();
        listRoles();
        findRoleById();
        deactivateRole();
        activateRole();
        deleteRole();
    }
}
