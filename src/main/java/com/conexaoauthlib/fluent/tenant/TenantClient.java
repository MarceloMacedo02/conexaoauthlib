package com.conexaoauthlib.fluent.tenant;

import com.conexaoauthlib.dto.common.PageResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantCreateRequestDTO;
import com.conexaoauthlib.dto.tenant.TenantFilterDTO;
import com.conexaoauthlib.dto.tenant.TenantProductAddRequestDTO;
import com.conexaoauthlib.dto.tenant.TenantResponseDTO;
import com.conexaoauthlib.dto.tenant.TenantStatusDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Entry point fluente para operações de Tenant.
 * Fornece uma API intuitiva para CRUD completo de tenants.
 *
 * <h3>Exemplo de criação com produtos:</h3>
 * <pre>{@code
 * TenantResponseDTO tenant = TenantClient.create()
 *     .name("Empresa X")
 *     .documentNumber("12345678000100")
 *     .product("premium")
 *     .product("analytics")
 *     .executeWithProducts();
 * }</pre>
 *
 * <h3>Exemplo de listagem com filtros:</h3>
 * <pre>{@code
 * PageResponseDTO<TenantResponseDTO> tenants = TenantClient.list()
 *     .name("Empresa")
 *     .status("ACTIVE")
 *     .page(0)
 *     .size(20)
 *     .execute();
 * }</pre>
 *
 * <h3>Exemplo de atualização de status:</h3>
 * <pre>{@code
 * TenantResponseDTO updated = TenantClient.updateStatus("tenant-id")
 *     .status("SUSPENDED")
 *     .reason("Payment overdue")
 *     .execute();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public final class TenantClient {

    private TenantClient() {
        // Utility class - não instanciar
    }

    // ==================== Create ====================

    /**
     * Inicia criação de um novo tenant.
     *
     * @return TenantCreateRequestBuilder para configuração
     */
    public static TenantCreateRequestBuilder create() {
        return new TenantCreateRequestBuilder();
    }

    /**
     * Builder para criação de tenant.
     */
    public static final class TenantCreateRequestBuilder {
        private String name;
        private String documentNumber;
        private final List<String> products = new ArrayList<>();
        private String tenantId;

        /**
         * Define o nome do tenant.
         *
         * @param name Nome do tenant
         * @return this builder
         */
        public TenantCreateRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Define o documento (CPF/CNPJ) do tenant.
         *
         * @param documentNumber Número do documento
         * @return this builder
         */
        public TenantCreateRequestBuilder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        /**
         * Adiciona um produto ao tenant.
         * Pode ser chamado múltiplas vezes para múltiplos produtos.
         *
         * @param productCode Código do produto
         * @return this builder
         */
        public TenantCreateRequestBuilder product(String productCode) {
            this.products.add(productCode);
            return this;
        }

        /**
         * Adiciona múltiplos produtos ao tenant.
         *
         * @param productCodes Códigos dos produtos
         * @return this builder
         */
        public TenantCreateRequestBuilder products(List<String> productCodes) {
            this.products.addAll(productCodes);
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public TenantCreateRequestBuilder tenant(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a criação do tenant.
         *
         * @return TenantResponseDTO com tenant criado
         */
        public TenantResponseDTO execute() {
            TenantCreateRequestDTO request = TenantCreateRequestDTO.builder()
                .name(name)
                .documentNumber(documentNumber)
                .products(products.isEmpty() ? null : products)
                .build();

            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.create(request, tenantId);
        }

        /**
         * Executa a criação do tenant e adiciona produtos.
         * Se produtos já existirem, são ignorados (idempotente).
         *
         * <p>Este método é útil para fluxos de onboarding onde um novo tenant
         * deve ser criado e já associado a produtos de assinatura em uma
         * única operação atômica.</p>
         *
         * @return TenantResponseDTO com tenant e produtos
         */
        public TenantResponseDTO executeWithProducts() {
            // Cria o tenant
            TenantResponseDTO tenant = execute();

            // Se há produtos a adicionar, adiciona um por um
            if (!products.isEmpty()) {
                com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                    TenantClientFactory.createTenantClient();

                for (String productCode : products) {
                    try {
                        TenantProductAddRequestDTO productRequest =
                            TenantProductAddRequestDTO.builder()
                                .productCodes(List.of(productCode))
                                .build();

                        feignClient.addProducts(tenant.getId(), productRequest, tenantId);
                    } catch (Exception e) {
                        // Produto pode já existir - continuar com os demais
                    }
                }

                // Retorna tenant atualizado com produtos
                return feignClient.getById(tenant.getId(), tenantId);
            }

            return tenant;
        }

        /**
         * Executa a criação de forma assíncrona.
         *
         * @return CompletableFuture com TenantResponseDTO
         */
        public CompletableFuture<TenantResponseDTO> executeAsync() {
            return CompletableFuture.supplyAsync(this::execute);
        }
    }

    // ==================== List ====================

    /**
     * Inicia listagem de tenants com filtros.
     *
     * @return TenantListRequestBuilder para configuração
     */
    public static TenantListRequestBuilder list() {
        return new TenantListRequestBuilder();
    }

    /**
     * Builder para listagem de tenants.
     */
    public static final class TenantListRequestBuilder {
        private String name;
        private String status;
        private String documentNumber;
        private Integer page = 0;
        private Integer size = 20;
        private String sort = "created_at,desc";
        private String tenantId;

        /**
         * Filtra pelo nome do tenant.
         *
         * @param name Nome ou parte do nome
         * @return this builder
         */
        public TenantListRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Filtra pelo status do tenant.
         *
         * @param status Status (ACTIVE, INACTIVE, SUSPENDED, PENDING)
         * @return this builder
         */
        public TenantListRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Filtra pelo documento do tenant.
         *
         * @param documentNumber Documento (CPF/CNPJ)
         * @return this builder
         */
        public TenantListRequestBuilder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        /**
         * Define o número da página (paginação).
         *
         * @param page Número da página (inicia em 0)
         * @return this builder
         */
        public TenantListRequestBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        /**
         * Define o tamanho da página.
         *
         * @param size Quantidade de itens por página
         * @return this builder
         */
        public TenantListRequestBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        /**
         * Define a ordenação.
         *
         * @param sort Campo e direção (ex: "created_at,desc")
         * @return this builder
         */
        public TenantListRequestBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public TenantListRequestBuilder tenant(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a listagem com os filtros configurados.
         *
         * @return PageResponseDTO com lista de tenants
         */
        public PageResponseDTO<TenantResponseDTO> execute() {
            TenantFilterDTO filter = TenantFilterDTO.builder()
                .name(name)
                .status(status)
                .documentNumber(documentNumber)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.list(filter, tenantId);
        }
    }

    // ==================== Get ====================

    /**
     * Inicia busca de tenant por ID.
     *
     * @param tenantId ID do tenant
     * @return TenantGetBuilder para operações adicionais
     */
    public static TenantGetBuilder get(String tenantId) {
        return new TenantGetBuilder(tenantId);
    }

    /**
     * Builder para operações após GET de tenant.
     */
    public static final class TenantGetBuilder {
        private final String tenantId;
        private String contextTenantId;

        TenantGetBuilder(String tenantId) {
            this.tenantId = tenantId;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public TenantGetBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a busca do tenant por ID.
         *
         * @return TenantResponseDTO com dados do tenant
         */
        public TenantResponseDTO execute() {
            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.getById(tenantId, contextTenantId);
        }

        /**
         * Adiciona produtos ao tenant.
         *
         * @param productCodes Códigos dos produtos
         * @return TenantResponseDTO atualizado
         */
        public TenantResponseDTO addProducts(List<String> productCodes) {
            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();

            TenantProductAddRequestDTO request = TenantProductAddRequestDTO.builder()
                .productCodes(productCodes)
                .build();

            return feignClient.addProducts(tenantId, request, contextTenantId);
        }

        /**
         * Remove produtos do tenant.
         *
         * @param productCodes Códigos dos produtos
         * @return TenantResponseDTO atualizado
         */
        public TenantResponseDTO removeProducts(List<String> productCodes) {
            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.removeProducts(tenantId, productCodes, contextTenantId);
        }
    }

    // ==================== Find by Document ====================

    /**
     * Inicia busca de tenant por documento.
     *
     * @param document Documento (CPF/CNPJ)
     * @return TenantFindBuilder para operações adicionais
     */
    public static TenantFindBuilder findByDocument(String document) {
        return new TenantFindBuilder(document);
    }

    /**
     * Builder para busca por documento.
     */
    public static final class TenantFindBuilder {
        private final String document;
        private String tenantId;

        TenantFindBuilder(String document) {
            this.document = document;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param tenantId ID do tenant
         * @return this builder
         */
        public TenantFindBuilder tenant(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Executa a busca do tenant por documento.
         *
         * @return TenantResponseDTO com dados do tenant
         */
        public TenantResponseDTO execute() {
            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.getByDocument(document, tenantId);
        }
    }

    // ==================== Update Status ====================

    /**
     * Inicia atualização de status de tenant.
     *
     * @param tenantId ID do tenant
     * @return TenantStatusUpdateBuilder para configuração
     */
    public static TenantStatusUpdateBuilder updateStatus(String tenantId) {
        return new TenantStatusUpdateBuilder(tenantId);
    }

    /**
     * Builder para atualização de status.
     */
    public static final class TenantStatusUpdateBuilder {
        private final String tenantId;
        private String status;
        private String reason;
        private String contextTenantId;

        TenantStatusUpdateBuilder(String tenantId) {
            this.tenantId = tenantId;
        }

        /**
         * Define o novo status do tenant.
         *
         * @param status Novo status (ACTIVE, INACTIVE, SUSPENDED, PENDING)
         * @return this builder
         */
        public TenantStatusUpdateBuilder status(String status) {
            this.status = status;
            return this;
        }

        /**
         * Define a razão da mudança de status.
         *
         * @param reason Motivo da alteração
         * @return this builder
         */
        public TenantStatusUpdateBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        /**
         * Define o tenant para contexto multi-tenant.
         *
         * @param contextTenantId ID do tenant de contexto
         * @return this builder
         */
        public TenantStatusUpdateBuilder tenant(String contextTenantId) {
            this.contextTenantId = contextTenantId;
            return this;
        }

        /**
         * Executa a atualização de status.
         *
         * @return TenantResponseDTO com tenant atualizado
         */
        public TenantResponseDTO execute() {
            TenantStatusDTO request = TenantStatusDTO.builder()
                .status(status)
                .reason(reason)
                .build();

            com.conexaoauthlib.feign.tenant.TenantClient feignClient =
                TenantClientFactory.createTenantClient();
            return feignClient.updateStatus(tenantId, request, contextTenantId);
        }
    }
}
