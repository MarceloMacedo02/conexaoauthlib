package com.plataforma.conexao.auth.starter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para registro de novo usuário via API do Auth Server.
 *
 * <p>Este DTO contém os dados necessários para criar um novo usuário no sistema
 * de autenticação, incluindo informações pessoais, credenciais e associações
 * com realm, roles, empresa e tenant.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record RegisterUserRequest(

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    /**
     * Email do usuário (usado para login).
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    String email,

    /**
     * Senha do usuário.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    String senha,

    /**
     * CPF do usuário (formato: apenas números).
     */
    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 dígitos")
    String cpf,

    /**
     * ID do realm ao qual o usuário pertence.
     */
    @NotBlank(message = "Realm ID é obrigatório")
    String realmId,

    /**
     * Lista de IDs de roles a serem atribuídas ao usuário.
     */
    @NotNull(message = "Lista de roles não pode ser nula")
    List<@NotBlank(message = "Role ID não pode estar em branco") String> roleIds,

    /**
     * ID da empresa (opcional, para multitenancy).
     */
    String empresaId,

    /**
     * ID do tenant (opcional, para multitenancy).
     */
    String tenantId
) {}
