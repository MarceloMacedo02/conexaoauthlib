package br.com.conexaoautolib.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.conexaoautolib.config.ConexaoAuthFeignConfig;
import br.com.conexaoautolib.model.response.UsuarioResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Cliente Feign para integração com endpoints de realms do ConexãoAuth.
 * Fornece métodos para consulta e gerenciamento de realms.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(name = "conexaoAuthRealmClient", url = "${conexaoauth.server.url}", configuration = ConexaoAuthFeignConfig.class)
public interface ConexaoAuthRealmClient {

    /**
     * Busca informações de um realm específico.
     * 
     * @param realmId ID do realm a ser consultado
     * @return Informações completas do realm
     */
    @GetMapping("/api/v1/realms/{realmId}")
    RealmResponse buscarPorId(@PathVariable("realmId") UUID realmId);

    /**
     * Busca realm por nome único.
     * 
     * @param realmName Nome único do realm
     * @return Informações do realm encontrado
     */
    @GetMapping("/api/v1/realms/nome/{realmName}")
    RealmResponse buscarPorNome(@PathVariable("realmName") String realmName);

    /**
     * Lista todos os realms disponíveis.
     * 
     * @return Lista de todos os realms
     */
    @GetMapping("/api/v1/realms")
    List<RealmResponse> listarTodos();

    /**
     * Lista realms ativos.
     * 
     * @return Lista de realms com status ATIVO
     */
    @GetMapping("/api/v1/realms/ativos")
    List<RealmResponse> listarAtivos();

    /**
     * Lista realms por tenant.
     * 
     * @param tenantId ID do tenant para filtro
     * @return Lista de realms do tenant
     */
    @GetMapping("/api/v1/realms/tenant/{tenantId}")
    List<RealmResponse> listarPorTenant(@PathVariable("tenantId") String tenantId);

    /**
     * Lista usuários de um realm específico.
     * 
     * @param realmId ID do realm
     * @return Lista de usuários do realm
     */
    @GetMapping("/api/v1/realms/{realmId}/usuarios")
    List<UsuarioResponse> listarUsuarios(@PathVariable("realmId") UUID realmId);

    /**
     * Verifica se realm está ativo.
     * 
     * @param realmId ID do realm a ser verificado
     * @return true se realm estiver ativo, false caso contrário
     */
    @GetMapping("/api/v1/realms/{realmId}/ativo")
    boolean estaAtivo(@PathVariable("realmId") UUID realmId);

    /**
     * Conta usuários ativos em um realm.
     * 
     * @param realmId ID do realm
     * @return Número de usuários ativos no realm
     */
    @GetMapping("/api/v1/realms/{realmId}/usuarios/ativos/count")
    long contarUsuariosAtivos(@PathVariable("realmId") UUID realmId);

    /**
     * DTO de resposta para dados do realm.
     */
    class RealmResponse {
        private UUID id;
        private String nome;
        private String displayName;
        private boolean enabled;
        private String description;
        private String tenantId;
        private LocalDateTime dataCriacao;
        private LocalDateTime dataAtualizacao;

        // Constructors, getters and setters
        public RealmResponse() {}

        public RealmResponse(UUID id, String nome, String displayName, boolean enabled, 
                         String description, String tenantId) {
            this.id = id;
            this.nome = nome;
            this.displayName = displayName;
            this.enabled = enabled;
            this.description = description;
            this.tenantId = tenantId;
            this.dataCriacao = LocalDateTime.now();
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }

        public LocalDateTime getDataCriacao() { return dataCriacao; }
        public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

        public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
        public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    }
}