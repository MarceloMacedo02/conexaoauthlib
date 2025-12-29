package com.plataforma.conexao.auth.starter.client;

import com.plataforma.conexao.auth.starter.dto.request.RegisterUserRequest;
import com.plataforma.conexao.auth.starter.dto.response.TokenResponse;
import com.plataforma.conexao.auth.starter.dto.response.UserResponse;

import feign.Body;
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
     * <p>
     * Endpoint: POST /oauth2/token
     *
     * <p>
     * Obtém um token de acesso autenticando-se como client (application)
     * usando o fluxo Client Credentials do OAuth2. Este fluxo é utilizado
     * por aplicações que precisam de acesso à API sem contexto de usuário.
     *
     * <p>
     * O request é enviado como form-data (application/x-www-form-urlencoded)
     * usando o @Body template do Feign para garantir a codificação correta.
     * Isso resolve o problema do StrictHttpFirewall que rejeita requisições
     * enviadas como JSON quando o Content-Type é application/x-www-form-urlencoded.
     *
     * <p>Parâmetros obrigatórios:
     * <ul>
     *   <li>grantType - Tipo de grant (ex: "client_credentials")</li>
     *   <li>clientId - ID do cliente OAuth2</li>
     *   <li>clientSecret - Secret do cliente OAuth2</li>
     * </ul>
     *
     * <p>Parâmetros opcionais (passar null para omitir):
     * <ul>
     *   <li>scope - Escopos solicitados (ex: "read write")</li>
     *   <li>code - Código de autorização (para authorization_code)</li>
     *   <li>redirectUri - URI de redirecionamento</li>
     *   <li>refreshToken - Token de atualização</li>
     *   <li>username - Usuário (para password grant)</li>
     *   <li>password - Senha (para password grant)</li>
     *   <li>codeVerifier - Verificador PKCE</li>
     * </ul>
     *
     * @param grantType Tipo de grant (obrigatório)
     * @param clientId ID do cliente (obrigatório)
     * @param clientSecret Secret do cliente (obrigatório)
     * @param scope Escopos solicitados (opcional)
     * @param code Código de autorização (opcional)
     * @param redirectUri URI de redirecionamento (opcional)
     * @param refreshToken Token de atualização (opcional)
     * @param username Usuário (opcional)
     * @param password Senha (opcional)
     * @param codeVerifier Verificador PKCE (opcional)
     * @return Token de acesso com informações de expiração
     */
    @RequestLine("POST /oauth2/token")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: application/json"
    })
    @Body("grant_type={grantType}&client_id={clientId}&client_secret={clientSecret}{scope}{code}{redirectUri}{refreshToken}{username}{password}{codeVerifier}")
    TokenResponse clientCredentials(
            @Param("grantType") String grantType,
            @Param("clientId") String clientId,
            @Param("clientSecret") String clientSecret,
            @Param("scope") String scope,
            @Param("code") String code,
            @Param("redirectUri") String redirectUri,
            @Param("refreshToken") String refreshToken,
            @Param("username") String username,
            @Param("password") String password,
            @Param("codeVerifier") String codeVerifier);
}
