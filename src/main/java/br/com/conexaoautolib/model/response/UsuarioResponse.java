package br.com.conexaoautolib.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para dados de usuário do ConexãoAuth.
 * Contém informações completas do usuário incluindo roles e realm.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Value
@Builder
public class UsuarioResponse {

    @JsonProperty("id")
    UUID id;

    @JsonProperty("nome")
    String nome;

    @JsonProperty("cpfOrCnpj")
    String cpfOrCnpj;

    @JsonProperty("email")
    String email;

    @JsonProperty("realmId")
    UUID realmId;

    @JsonProperty("realmNome")
    String realmNome;

    @JsonProperty("roleIds")
    List<UUID> roleIds;

    @JsonProperty("roleNomes")
    List<String> roleNomes;

    @JsonProperty("empresaId")
    String empresaId;

    @JsonProperty("tenantId")
    String tenantId;

    @JsonProperty("status")
    StatusUsuario status;

    @JsonProperty("realmStatus")
    StatusRealm realmStatus;

    @JsonProperty("dataCriacao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dataCriacao;

    @JsonProperty("dataUltimoLogin")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dataUltimoLogin;

    @JsonProperty("dataAtualizacao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dataAtualizacao;

    /**
     * Enum de status do usuário.
     */
    public enum StatusUsuario {
        ATIVO,
        INATIVO,
        BLOQUEADO
    }

    /**
     * Enum de status do realm.
     */
    public enum StatusRealm {
        ATIVO,
        INATIVO
    }
}