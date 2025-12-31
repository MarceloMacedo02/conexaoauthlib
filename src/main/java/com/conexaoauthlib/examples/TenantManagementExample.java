package com.conexaoauthlib.examples;

import com.conexaoauthlib.fluent.tenant.TenantClient;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;
import com.conexaoauthlib.dto.common.PageResponseDTO;

import java.util.List;

/**
 * Exemplos de gerenciamento de Tenants.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class TenantManagementExample {

    /**
     * Exemplo de criação de tenant com produtos.
     */
    public static void createTenantWithProducts() {
        System.out.println("=== Criar Tenant com Produtos ===");

        TenantResponseDTO tenant = TenantClient.create()
            .name("Empresa Teste Ltda")
            .documentNumber("12345678000100")
            .product("premium")
            .product("analytics")
            .product("support")
            .executeWithProducts();

        System.out.println("Tenant criado:");
        System.out.println("  ID: " + tenant.getId());
        System.out.println("  Nome: " + tenant.getName());
        System.out.println("  Status: " + tenant.getStatus());
        System.out.println("  Produtos: " + tenant.getProducts());
    }

    /**
     * Exemplo de criação de tenant simples.
     */
    public static void createSimpleTenant() {
        System.out.println("\n=== Criar Tenant Simples ===");

        TenantResponseDTO tenant = TenantClient.create()
            .name("Empresa Simples Ltda")
            .documentNumber("98765432000100")
            .execute();

        System.out.println("Tenant criado: " + tenant.getName());
        System.out.println("ID: " + tenant.getId());
    }

    /**
     * Exemplo de listagem de tenants com filtros.
     */
    public static void listTenants() {
        System.out.println("\n=== Listar Tenants ===");

        PageResponseDTO<TenantResponseDTO> page = TenantClient.list()
            .status("ACTIVE")
            .name("Empresa")
            .page(0)
            .size(20)
            .execute();

        System.out.println("Total de tenants: " + page.getTotalElements());
        page.getContent().forEach(tenant ->
            System.out.println("  - " + tenant.getName() + " (" + tenant.getStatus() + ")")
        );
    }

    /**
     * Exemplo de busca por documento.
     */
    public static void findByDocument() {
        System.out.println("\n=== Buscar por Documento ===");

        TenantResponseDTO tenant = TenantClient.findByDocument("12345678000100")
            .execute();

        System.out.println("Tenant encontrado: " + tenant.getName());
    }

    /**
     * Exemplo de busca por ID.
     */
    public static void findById() {
        System.out.println("\n=== Buscar por ID ===");

        String tenantId = "tenant-id-123";

        TenantResponseDTO tenant = TenantClient.get(tenantId)
            .execute();

        System.out.println("Tenant encontrado: " + tenant.getName());
        System.out.println("Documento: " + tenant.getDocumentNumber());
    }

    /**
     * Exemplo de atualização de status.
     */
    public static void updateStatus() {
        System.out.println("\n=== Atualizar Status ===");

        TenantResponseDTO updated = TenantClient.updateStatus("tenant-id")
            .status("SUSPENDED")
            .reason("Manutenção do sistema")
            .execute();

        System.out.println("Status atualizado para: " + updated.getStatus());
    }

    /**
     * Exemplo de ativação de tenant.
     */
    public static void activateTenant() {
        System.out.println("\n=== Ativar Tenant ===");

        TenantResponseDTO activated = TenantClient.updateStatus("tenant-id")
            .status("ACTIVE")
            .reason("Finalização da manutenção")
            .execute();

        System.out.println("Tenant ativado: " + activated.getName());
    }

    /**
     * Exemplo de gerenciamento de produtos.
     */
    public static void manageProducts() {
        System.out.println("\n=== Gerenciar Produtos ===");

        String tenantId = "tenant-id";

        // Obter tenant atual
        TenantResponseDTO tenant = TenantClient.get(tenantId)
            .execute();

        System.out.println("Produtos atuais: " + tenant.getProducts());
        System.out.println("Nota: Para gerenciar produtos, use TenantClient.addProducts() ou TenantClient.removeProducts()");
    }

    /**
     * Exemplo de exclusão de tenant.
     */
    public static void deleteTenant() {
        System.out.println("\n=== Excluir Tenant ===");

        TenantClient.delete("tenant-id")
            .execute();

        System.out.println("Tenant excluído com sucesso!");
    }

    public static void main(String[] args) {
        createTenantWithProducts();
        createSimpleTenant();
        listTenants();
        findByDocument();
        findById();
        updateStatus();
        activateTenant();
        manageProducts();
        deleteTenant();
    }
}
