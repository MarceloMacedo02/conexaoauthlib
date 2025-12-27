package com.plataforma.conexao.auth.starter.dto.request;

/**
 * DTO para busca de usuário por CPF.
 *
 * <p>Este DTO usa Java 21 Record com validação Jakarta Bean.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para busca de usuário por CPF.
 *
 * <p>Este DTO contém apenas o CPF do usuário a ser buscado.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record FindUserByCpfRequest(
        /**
         * CPF do usuário.
         *
         * <p>Obrigatório, deve ter 11 dígitos numéricos.
         */
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos numéricos")
        String cpf
) {}