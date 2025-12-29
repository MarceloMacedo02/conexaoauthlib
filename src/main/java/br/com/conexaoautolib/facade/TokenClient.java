package br.com.conexaoautolib.facade;

import br.com.conexaoautolib.client.ConexaoAuthOAuth2Client;
import br.com.conexaoautolib.exception.ConexaoAuthException;
import br.com.conexaoautolib.exception.InvalidCredentialsException;
import br.com.conexaoautolib.model.request.TokenRequest;
import br.com.conexaoautolib.model.response.TokenResponse;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * API fluente para operações de token OAuth2.
 * 
 * Fornece uma interface intuitiva com method chaining para configuração
 * e execução de requisições de token no servidor ConexãoAuth.
 * 
 * @author ConexãoAuthLib
 * @version 1.0.0
 */
public class TokenClient {

    private final ConexaoAuthOAuth2Client oAuth2Client;
    
    public TokenClient(ConexaoAuthOAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }

    /**
     * Cria um novo builder para configuração de token.
     * 
     * @param oAuth2Client Client OAuth2 para comunicação
     * @return Nova instância de TokenBuilder
     */
    public static TokenBuilder gerar(ConexaoAuthOAuth2Client oAuth2Client) {
        return new TokenBuilder(oAuth2Client);
    }

    /**
     * Builder para configuração e execução de requisições OAuth2.
     * Implementa padrão Builder com method chaining para configuração
     * intuitiva dos parâmetros.
     */
    public static class TokenBuilder {

        private final ConexaoAuthOAuth2Client oAuth2Client;

        // Parâmetros OAuth2 obrigatórios
        private String clientId;
        private String clientSecret;
        private String realm;

        // Parâmetros opcionais por grant type
        private String username;
        private String password;
        private String refreshToken;
        private String scope;

        // Grant type padrão
        private String grantType = "client_credentials";

        /**
         * Construtor privado que recebe o cliente OAuth2.
         * 
         * @param oAuth2Client Cliente OAuth2 para comunicação
         */
        private TokenBuilder(ConexaoAuthOAuth2Client oAuth2Client) {
            this.oAuth2Client = oAuth2Client;
        }

        /**
         * Configura o ID do cliente OAuth2.
         * 
         * @param clientId ID do cliente
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Configura o secret do cliente OAuth2.
         * 
         * @param secret Secret do cliente
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder secret(String secret) {
            this.clientSecret = secret;
            return this;
        }

        /**
         * Configura o nome de usuário (grant type password).
         * Automaticamente define o grant type como "password".
         * 
         * @param username Nome de usuário
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder usuario(String usuario) {
            this.username = usuario;
            this.grantType = "password";
            return this;
        }

        /**
         * Configura a senha do usuário (grant type password).
         * 
         * @param senha Senha do usuário
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder senha(String senha) {
            this.password = senha;
            this.grantType = "password";
            return this;
        }

        /**
         * Configura o realm multi-tenant.
         * 
         * @param realm Identificador do realm
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder realm(String realm) {
            this.realm = realm;
            return this;
        }

        /**
         * Configura o escopo de acesso solicitado.
         * 
         * @param scope Escopo do acesso (opcional)
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * Configura o token de refresh (grant type refresh_token).
         * Automaticamente define o grant type como "refresh_token".
         * 
         * @param refreshToken Token de refresh
         * @return Builder atualizado para method chaining
         */
        public TokenBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            this.grantType = "refresh_token";
            return this;
        }

        /**
         * Executa a requisição OAuth2 e retorna o token.
         * 
         * @return TokenResponse com o token de acesso
         * @throws ConexaoAuthException em caso de erro
         */
        public TokenResponse execute() {
            try {
                // Validar parâmetros obrigatórios
                validateRequiredParameters();

                // Construir dados de formulário diretamente
                Map<String, String> formData = buildFormData();

                // Executar requisição HTTP e retornar resposta
                TokenResponse response = oAuth2Client.emitirToken(formData);
                return response;

            } catch (ConexaoAuthException e) {
                throw new ConexaoAuthException(
                    "Erro ao emitir token OAuth2: " + e.getMessage(),
                    e
                );
            }
        }

        /**
         * Valida parâmetros obrigatórios antes da execução.
         * 
         * @throws IllegalArgumentException se algum parâmetro obrigatório estiver faltando
         */
        private void validateRequiredParameters() {
            if (clientId == null || clientId.trim().isEmpty()) {
                throw new IllegalArgumentException("clientId é obrigatório");
            }
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("clientSecret é obrigatório");
            }
            if (realm == null || realm.trim().isEmpty()) {
                throw new IllegalArgumentException("realm é obrigatório");
            }

            // Valida consistência do grant type com parâmetros fornecidos
            if ("password".equals(grantType)) {
                if (username == null || username.trim().isEmpty()) {
                    throw new IllegalArgumentException("usuário é obrigatório para grant type password");
                }
                if (password == null || password.trim().isEmpty()) {
                    throw new IllegalArgumentException("senha é obrigatória para grant type password");
                }
            }

            if ("refresh_token".equals(grantType)) {
                if (refreshToken == null || refreshToken.trim().isEmpty()) {
                    throw new IllegalArgumentException("refreshToken é obrigatório para grant type refresh_token");
                }
            }
        }

        /**
         * Constrói Map<String, String> para dados de formulário HTTP.
         * 
         * @return Map com dados formatados como application/x-www-form-urlencoded
         */
        private Map<String, String> buildFormData() {
            Map<String, String> formData = new java.util.HashMap<>();
            formData.put("grant_type", grantType);
            formData.put("client_id", clientId);
            formData.put("client_secret", clientSecret);
            formData.put("realm", realm);
            
            if (username != null) {
                formData.put("username", username);
            }
            if (password != null) {
                formData.put("password", password);
            }
            if (refreshToken != null) {
                formData.put("refresh_token", refreshToken);
            }
            if (scope != null) {
                formData.put("scope", scope);
            }
            
            return formData;
        }
    }
}