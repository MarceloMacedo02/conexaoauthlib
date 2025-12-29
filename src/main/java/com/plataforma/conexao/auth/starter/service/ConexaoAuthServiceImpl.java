package com.plataforma.conexao.auth.starter.service;

import com.plataforma.conexao.auth.starter.client.ConexaoAuthClient;
import com.plataforma.conexao.auth.starter.dto.request.RegisterUserRequest;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import com.plataforma.conexao.auth.starter.dto.response.UserResponse;
import com.plataforma.conexao.auth.starter.exception.InvalidTokenException;
import com.plataforma.conexao.auth.starter.model.TokenClaims;
import com.plataforma.conexao.auth.starter.properties.ConexaoAuthProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementação do serviço de autenticação Conexão Auth.
 *
 * <p>Esta classe fornece uma abstração de alto nível para interagir com
 * o Auth Server, incluindo:
 * <ul>
 *   <li>Registro de usuários</li>
 *   <li>Busca de usuários</li>
 *   <li>Validação de permissões</li>
 *   <li>Obtenção de tokens (Client Credentials e Refresh Token)</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
@Service
public class ConexaoAuthServiceImpl implements ConexaoAuthService {

    private static final Logger log = LoggerFactory.getLogger(ConexaoAuthServiceImpl.class);

    private final ConexaoAuthClient conexaoAuthClient;
    private final TokenValidator tokenValidator;
    private final String clientId;
    private final String clientSecret;

    /**
     * Construtor do serviço de autenticação.
     *
     * @param conexaoAuthClient Feign client para Auth Server
     * @param tokenValidator Validador de tokens JWT
     * @param properties Propriedades de configuração
     */
    public ConexaoAuthServiceImpl(ConexaoAuthClient conexaoAuthClient,
                                  TokenValidator tokenValidator,
                                  ConexaoAuthProperties properties) {
        this.conexaoAuthClient = conexaoAuthClient;
        this.tokenValidator = tokenValidator;
        this.clientSecret = properties.clientSecret();
        this.clientId = properties.clientId();
        log.info("Inicializando ConexaoAuthServiceImpl");
    }

    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Registrando novo usuário: email={}, cpf={}",
                request.email(), maskCpf(request.cpf()));

        try {
            UserResponse response = conexaoAuthClient.registerUser(request);
            log.info("Usuário registrado com sucesso: id={}", response.id());
            return response;
        } catch (Exception e) {
            log.error("Erro ao registrar usuário: email={}", request.email(), e);
            throw e; // Re-throws as Feign exceptions are already proper
        }
    }

    @Override
    public UserResponse findUserByCpf(String cpf) {
        log.debug("Buscando usuário por CPF: {}", maskCpf(cpf));

        try {
            UserResponse response = conexaoAuthClient.findUserByCpf(cpf);
            log.debug("Usuário encontrado: id={}", response.id());
            return response;
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por CPF: {}", maskCpf(cpf), e);
            throw e; // Re-throws as Feign exceptions are already proper
        }
    }

    @Override
    public UserResponse findUserByUsername(String username) {
        log.debug("Buscando usuário por username: {}", maskUsername(username));

        try {
            UserResponse response = conexaoAuthClient.findUserByUsername(username);
            log.debug("Usuário encontrado: id={}", response.id());
            return response;
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por username: {}", maskUsername(username), e);
            throw e; // Re-throws as Feign exceptions are already proper
        }
    }

    @Override
    public boolean validatePermissions(String token, List<String> requiredPermissions) {
        if (token == null || token.isEmpty()) {
            log.warn("Token JWT nulo ou vazio");
            return false;
        }

        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            log.debug("Nenhuma permissão requerida, considerando válido");
            return true;
        }

        try {
            // Valida o token e extrai os claims
            TokenClaims claims = tokenValidator.validateToken(token);

            log.debug("Validando permissões para usuário: sub={}, roles={}",
                    claims.sub(), claims.roles());

            // Verifica se o usuário tem todas as permissões requeridas
            boolean hasAllPermissions = claims.hasAllRoles(requiredPermissions);

            if (hasAllPermissions) {
                log.debug("Usuário possui todas as permissões requeridas");
            } else {
                log.warn("Usuário não possui todas as permissões requeridas. " +
                        "Required: {}, Has: {}", requiredPermissions, claims.roles());
            }

            return hasAllPermissions;

        } catch (InvalidTokenException e) {
            log.error("Token inválido ao validar permissões", e);
            return false;
        }
    }

    @Override
    public TokenResponse getClientCredentialsToken() {
        log.info("Obtendo token via Client Credentials Flow");

        try {
            // Parâmetros para Client Credentials Flow
            String grantType = "client_credentials";
            String scope = "read write";

            log.debug("Client Credentials Request: grant_type={}, client_id={}",
                    grantType, clientId);

            TokenResponse response = conexaoAuthClient.clientCredentials(
                    grantType,
                    clientId,
                    clientSecret,
                    scope,
                    null,  // code
                    null,  // redirectUri
                    null,  // refreshToken
                    null,  // username
                    null,  // password
                    null   // codeVerifier
            );
            log.info("Token obtido com sucesso. Token Type: {}, Expires In: {}s",
                    response.tokenType(), response.expiresIn());

            return response;

        } catch (Exception e) {
            log.error("Erro ao obter token via Client Credentials Flow", e);
            throw new RuntimeException("Falha ao obter token de acesso", e);
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Atualizando token usando refresh token");

        try {
            // Nota: Como não temos um método específico no ConexaoAuthClient para refresh,
            // vamos usar uma implementação simplificada
            //
            // Em produção, o Auth Server deve ter um endpoint para refresh token
            // que aceita: grant_type=refresh_token, refresh_token=<token>
            
            log.warn("Refresh token endpoint não implementado. Retornando erro.");
            throw new UnsupportedOperationException(
                    "Refresh token flow ainda não implementado no Auth Server");

        } catch (Exception e) {
            log.error("Erro ao atualizar token", e);
            throw new RuntimeException("Falha ao atualizar token de acesso", e);
        }
    }

    /**
     * Mascarar CPF para logs (mantém apenas os 3 primeiros e 2 últimos dígitos).
     *
     * @param cpf CPF completo
     * @return CPF mascarado (ex: 123*****45)
     */
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return "***";
        }
        return cpf.substring(0, 3) + "*****" + cpf.substring(9);
    }

    /**
     * Mascarar username (email) para logs.
     *
     * @param username Username completo
     * @return Username mascarado (ex: joao@*****.com)
     */
    private String maskUsername(String username) {
        if (username == null || !username.contains("@")) {
            return "***";
        }
        String[] parts = username.split("@");
        if (parts[0].length() > 3) {
            return parts[0].substring(0, 3) + "***@" + parts[1];
        }
        return "***@" + parts[1];
    }
}
