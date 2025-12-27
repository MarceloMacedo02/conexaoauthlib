package com.plataforma.conexao.auth.starter.service;

import com.plataforma.conexao.auth.starter.dto.request.RegisterUserRequest;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import com.plataforma.conexao.auth.starter.dto.response.UserResponse;

import java.util.List;

/**
 * Interface de serviço de alto nível para integração com Conexão Auth.
 *
 * <p>Esta interface será implementada e configurada detalhadamente na Story SDK-4.2.
 * Por enquanto, esta é uma interface stub para permitir a compilação da Auto-Configuration.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public interface ConexaoAuthService {

    /**
     * Registra um novo usuário.
     *
     * @param request DTO com dados do usuário
     * @return Usuário criado
     */
    UserResponse registerUser(RegisterUserRequest request);

    /**
     * Busca usuário por CPF.
     *
     * @param cpf CPF do usuário
     * @return Usuário encontrado
     */
    UserResponse findUserByCpf(String cpf);

    /**
     * Busca usuário por username (e-mail).
     *
     * @param username Username (e-mail) do usuário
     * @return Usuário encontrado
     */
    UserResponse findUserByUsername(String username);

    /**
     * Valida permissões de um usuário.
     *
     * @param token Token JWT do usuário
     * @param requiredPermissions Lista de permissões requeridas
     * @return true se usuário possui todas as permissões, false caso contrário
     */
    boolean validatePermissions(String token, List<String> requiredPermissions);

    /**
     * Obtém token via Client Credentials Flow.
     *
     * @return Token de acesso
     */
    TokenResponse getClientCredentialsToken();

    /**
     * Atualiza token usando refresh token.
     *
     * @param refreshToken Refresh token
     * @return Novo token de acesso
     */
    TokenResponse refreshToken(String refreshToken);
}
