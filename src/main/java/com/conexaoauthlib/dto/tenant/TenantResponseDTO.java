package com.conexaoauthlib.dto.tenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta para operações de Tenant.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * TenantResponseDTO tenant = tenantClient.getById("tenant-id");
 * String name = tenant.getName();
 * String status = tenant.getStatus();
 * List<TenantProductDTO> products = tenant.getProducts();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantResponseDTO {

    /**
     * Identificador único do tenant.
     */
    private String id;

    /**
     * Nome do tenant/organização.
     */
    private String name;

    /**
     * Número do documento (CPF ou CNPJ).
     */
    @JsonProperty("document_number")
    private String documentNumber;

    /**
     * Status atual do tenant.
     * Valores possíveis: ACTIVE, INACTIVE, SUSPENDED, PENDING
     */
    private String status;

    /**
     * ID da chave de criptografia utilizada para dados do tenant.
     */
    @JsonProperty("encryption_key_id")
    private String encryptionKeyId;

    /**
     * Data de criação do tenant.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Data da última atualização do tenant.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Lista de produtos associados ao tenant.
     */
    private List<TenantProductDTO> products;

    /**
     * ID do tenant pai (para estruturas hierárquicas).
     */
    @JsonProperty("parent_tenant_id")
    private String parentTenantId;
}
