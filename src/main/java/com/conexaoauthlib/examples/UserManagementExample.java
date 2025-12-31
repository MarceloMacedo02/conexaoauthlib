package com.conexaoauthlib.examples;

import com.conexaoauthlib.fluent.user.UserClient;
import com.conexaoauthlib.dto.user.UserResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

/**
 * Exemplos de gerenciamento de Usuários.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class UserManagementExample {

    /**
     * Exemplo de registro de usuário.
     */
    public static void registerUser() {
        System.out.println("=== Registrar Usuário ===");

        UserResponseDTO user = UserClient.register()
            .name("João Silva Santos")
            .email("joao.silva@empresa.com")
            .password("Senha123!@#")
            .tenantId("tenant-123")
            .execute();

        System.out.println("Usuário criado:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Nome: " + user.getName());
        System.out.println("  Email: " + user.getEmail());
        System.out.println("  Status: " + user.getStatus());
    }

    /**
     * Exemplo de listagem de usuários.
     */
    public static void listUsers() {
        System.out.println("\n=== Listar Usuários ===");

        PageResponseDTO<UserResponseDTO> page = UserClient.list()
            .tenantId("tenant-123")
            .status("ACTIVE")
            .page(0)
            .size(20)
            .execute();

        System.out.println("Total de usuários: " + page.getTotalElements());
        page.getContent().forEach(user ->
            System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")")
        );
    }

    /**
     * Exemplo de busca por email.
     */
    public static void findByEmail() {
        System.out.println("\n=== Buscar por Email ===");

        // Busca por email requer usar a API de listagem com filtro
        var users = UserClient.list()
            .email("joao.silva@empresa.com")
            .tenantId("tenant-123")
            .execute();

        if (!users.getContent().isEmpty()) {
            UserResponseDTO user = users.getContent().get(0);
            System.out.println("Usuário encontrado: " + user.getName());
        } else {
            System.out.println("Usuário não encontrado");
        }
    }

    /**
     * Exemplo de busca por ID.
     */
    public static void findById() {
        System.out.println("\n=== Buscar por ID ===");

        UserResponseDTO user = UserClient.get("user-id")
            .execute();

        System.out.println("Usuário encontrado: " + user.getName());
        System.out.println("Email: " + user.getEmail());
    }

    /**
     * Exemplo de mudança de senha.
     */
    public static void changePassword() {
        System.out.println("\n=== Mudar Senha ===");

        UserClient.changePassword("user-id")
            .currentPassword("SenhaAntiga123!")
            .newPassword("NovaSenha456!@#")
            .execute();

        System.out.println("Senha alterada com sucesso!");
    }

    /**
     * Exemplo de atualização de dados.
     */
    public static void updateUser() {
        System.out.println("\n=== Atualizar Usuário ===");

        UserResponseDTO updated = UserClient.update("user-id")
            .name("João Silva Santos Filho")
            .email("joao.santos@empresa.com")
            .execute();

        System.out.println("Usuário atualizado:");
        System.out.println("  Nome: " + updated.getName());
        System.out.println("  Email: " + updated.getEmail());
    }

    /**
     * Exemplo de desativação de usuário.
     */
    public static void deactivateUser() {
        System.out.println("\n=== Desativar Usuário ===");

        UserClient.deactivate("user-id")
            .execute();

        System.out.println("Usuário desativado!");
    }

    /**
     * Exemplo de reativação de usuário.
     */
    public static void activateUser() {
        System.out.println("\n=== Reativar Usuário ===");

        UserResponseDTO activated = UserClient.updateStatus("user-id")
            .status("ACTIVE")
            .execute();

        System.out.println("Usuário reativado: " + activated.getName());
    }

    /**
     * Exemplo de validação de senha.
     */
    public static void validatePassword() {
        System.out.println("\n=== Validar Senha ===");

        System.out.println("Nota: A validação de senha é feita automaticamente durante a autenticação.");
        System.out.println("Use changePassword() para alterar a senha do usuário.");
    }

    /**
     * Exemplo de exclusão de usuário.
     */
    public static void deleteUser() {
        System.out.println("\n=== Excluir Usuário ===");

        UserClient.delete("user-id")
            .execute();

        System.out.println("Usuário excluído com sucesso!");
    }

    public static void main(String[] args) {
        registerUser();
        listUsers();
        findByEmail();
        findById();
        changePassword();
        updateUser();
        deactivateUser();
        activateUser();
        validatePassword();
        deleteUser();
    }
}
