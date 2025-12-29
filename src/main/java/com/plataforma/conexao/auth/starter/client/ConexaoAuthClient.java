package com.plataforma.conexao.auth.starter.client;

import com.plataforma.conexao.auth.starter.dto.request.ClientCredentialsRequest;
import com.plataforma.conexao.auth.starter.dto.request.RegisterUserRequest;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import com.plataforma.conexao.auth.starter.dto.response.UserResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Interface Feign Client para comunicação com o Conexão Auth Server.
 *
 * <p>Este cliente fornece métodos para interagir com as principais APIs do
 * Auth Server, incluindo registro de usuários, busca de usuário e obtenção
 * de tokens via Client Credentials Flow.
 *
 * <p>Utiliza OpenFeign para comunicação HTTP declarativa, com serialização
 * automática de objetos via Jackson.
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public interface ConexaoAuthClient {

    /**
     * Registra um novo usuário no Auth Server.
     *
     * <p>Endpoint: POST /api/v1/usuarios
     *
     * <p>Cria um novo usuário com as informações fornecidas. O usuário será
     * criado no realm especificado e receberá as roles indicadas.
     *
     * @param request DTO com dados do usuário
     * @return Usuário criado com informações completas
     */
    @RequestLine("POST /api/v1/usuarios")
    @Headers({"Content-Type: application/json"})
    UserResponse registerUser(RegisterUserRequest request);

    /**
     * Busca usuário por CPF.
     *
     * <p>Endpoint: GET /api/v1/usuarios/cpf/{cpf}
     *
     * <p>Retorna as informações do usuário correspondente ao CPF fornecido.
     * Caso o usuário não seja encontrado, o Error Decoder lançará uma
     * {@link com.plataforma.conexao.auth.starter.exception.ResourceNotFoundException}.
     *
     * @param cpf CPF do usuário (apenas números, 11 dígitos)
     * @return Usuário encontrado
     */
    @RequestLine("GET /api/v1/usuarios/cpf/{cpf}")
    @Headers({"Accept: application/json"})
    UserResponse findUserByCpf(@Param("cpf") String cpf);

    /**
     * Busca usuário por username (e-mail).
     *
     * <p>Endpoint: GET /api/v1/usuarios/username/{username}
     *
     * <p>Retorna as informações do usuário correspondente ao username (e-mail) fornecido.
     * A busca é case-insensitive. Caso o usuário não seja encontrado, o Error Decoder
     * lançará uma {@link com.plataforma.conexao.auth.starter.exception.ResourceNotFoundException}.
     *
     * @param username Username (e-mail) do usuário
     * @return Usuário encontrado
     */
    @RequestLine("GET /api/v1/usuarios/username/{username}")
    @Headers({"Accept: application/json"})
    UserResponse findUserByUsername(@Param("username") String username);

    /**
     * Solicita token via Client Credentials Flow.
     *
     * <p>Endpoint: POST /oauth2/token
     *
     * <p>Obtém um token de acesso autenticando-se como client (application)
     * usando o fluxo Client Credentials do OAuth2. Este fluxo é utilizado
     * por aplicações que precisam de acesso à API sem contexto de usuário.
     *
     * <p>O request deve ser enviado como form-data (application/x-www-form-urlencoded),
     * não como JSON.
     *
     * @param request DTO com credenciais do client
     * @return Token de acesso com informações de expiração
     */
    @RequestLine("POST /oauth2/token")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: application/json"
    })
    TokenResponse clientCredentials(ClientCredentialsRequest request);
}
