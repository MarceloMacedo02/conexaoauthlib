package com.plataforma.conexao.auth.starter.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para dados de usuário.
 *
 * <p>Este DTO contém as informações de um usuário retornadas pelo Auth Server,
 * incluindo dados pessoais, informações do realm, roles associadas e metadados
 * de auditoria.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record UserResponse(

    /**
     * Identificador único do usuário (UUID).
     */
    String id,

    /**
     * Nome completo do usuário.
     */
    String nome,

    /**
     * Email do usuário.
     */
    String email,

    /**
     * CPF do usuário.
     */
    String cpf,

    /**
     * ID do realm ao qual o usuário pertence.
     */
    String realmId,

    /**
     * Nome do realm ao qual o usuário pertence.
     */
    String realmNome,

    /**
     * Lista de roles atribuídas ao usuário.
     */
    List<String> roles,

    /**
     * Status do usuário (ATIVO, BLOQUEADO, INATIVO).
     */
    String status,

    /**
     * Data e hora de criação do usuário.
     */
    LocalDateTime dataCriacao,

    /**
     * Data e hora da última atualização.
     */
    LocalDateTime dataUltimaAtualizacao
) {}
