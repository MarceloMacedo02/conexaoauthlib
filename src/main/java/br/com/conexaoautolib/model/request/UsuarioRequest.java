package br.com.conexaoautolib.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisições relacionadas a usuários do ConexãoAuth.
 * Contém dados para criação, atualização e consulta de usuários.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Value
@Builder
public class UsuarioRequest {

    @JsonProperty("nome")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @JsonProperty("email")
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    @JsonProperty("cpfOrCnpj")
    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(min = 11, max = 14, message = "CPF/CNPJ deve ter entre 11 e 14 caracteres")
    private String cpfOrCnpj;

    @JsonProperty("senha")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    private String senha;

    @JsonProperty("confirmarSenha")
    private String confirmarSenha;

    @JsonProperty("realmId")
    private String realmId;

    @JsonProperty("empresaId")
    @Size(max = 50, message = "ID da empresa deve ter no máximo 50 caracteres")
    private String empresaId;

    @JsonProperty("tenantId")
    @Size(max = 50, message = "Tenant ID deve ter no máximo 50 caracteres")
    private String tenantId;

    @JsonProperty("roleIds")
    private java.util.List<String> roleIds;

    @JsonProperty("status")
    private String status = "ATIVO"; // Default: ATIVO

    @JsonProperty("telefone")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @JsonProperty("dataNascimento")
    private String dataNascimento;

    @JsonProperty("endereco")
    private EnderecoRequest endereco;

    /**
     * DTO para dados de endereço do usuário.
     */
    @Value
    @Builder
    public static class EnderecoRequest {

        @JsonProperty("logradouro")
        @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
        private String logradouro;

        @JsonProperty("numero")
        @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
        private String numero;

        @JsonProperty("complemento")
        @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
        private String complemento;

        @JsonProperty("bairro")
        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        private String bairro;

        @JsonProperty("cidade")
        @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
        private String cidade;

        @JsonProperty("estado")
        @Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
        private String estado;

        @JsonProperty("cep")
        @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
        private String cep;

        @JsonProperty("pais")
        @Size(max = 50, message = "País deve ter no máximo 50 caracteres")
        private String pais;
    }

    /**
     * Status possíveis para o usuário.
     */
    public enum StatusUsuario {
        ATIVO,
        INATIVO,
        BLOQUEADO,
        PENDENTE_VALIDACAO,
        PENDENTE_APROVACAO,
        CANCELADO
    }
}