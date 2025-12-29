package br.com.conexaoautolib.storage;

import br.com.conexaoautolib.model.response.TokenResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Interface para armazenamento e gerenciamento de tokens OAuth2.
 * 
 * <p>Abstrai a estratégia de cache de tokens, permitindo diferentes implementações
 * como memória, Redis, database, etc. Implementações devem ser thread-safe.</p>
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public interface TokenStorage {

    /**
     * Armazena um token response com base na chave identificadora.
     * 
     * @param key Chave única que identifica o token (ex: client_id:realm)
     * @param tokenResponse Resposta do token com access token e metadados
     */
    void store(String key, TokenResponse tokenResponse);

    /**
     * Recupera um token armazenado pela chave.
     * 
     * @param key Chave única do token
     * @return Optional com o token response se encontrado e válido, empty caso contrário
     */
    Optional<TokenResponse> retrieve(String key);

    /**
     * Recupera um token armazenado apenas se não estiver expirado.
     * 
     * @param key Chave única do token
     * @return Optional com o token response se encontrado e não expirado, empty caso contrário
     */
    Optional<TokenResponse> retrieveIfValid(String key);

    /**
     * Verifica se um token existe e é válido.
     * 
     * @param key Chave única do token
     * @return true se token existe e não está expirado
     */
    boolean isValid(String key);

    /**
     * Invalida/remove um token armazenado.
     * 
     * @param key Chave única do token a ser removido
     */
    void invalidate(String key);

    /**
     * Limpa todos os tokens armazenados.
     */
    void clear();

    /**
     * Retorna a quantidade de tokens armazenados.
     * 
     * @return número de tokens no storage
     */
    int size();

    /**
     * Verifica se o storage está vazio.
     * 
     * @return true se não houver tokens armazenados
     */
    boolean isEmpty();

    /**
     * Verifica se um token está expirado com base na data atual.
     * 
     * @param tokenResponse Token response a ser verificado
     * @return true se token estiver expirado ou próximo de expirar (dentro da janela de buffer)
     */
    default boolean isExpired(TokenResponse tokenResponse) {
        if (tokenResponse == null) {
            return true;
        }
        
        // Usa o método isExpired() próprio do TokenResponse que já inclui lógica de expiração
        return tokenResponse.isExpired();
    }

    /**
     * Gera chave padrão para storage baseado em client_id e realm.
     * 
     * @param clientId ID do cliente OAuth2
     * @param realm Realm multi-tenant
     * @return Chave única no formato "client_id:realm"
     */
    static String generateKey(String clientId, String realm) {
        return String.format("%s:%s", clientId, realm);
    }
}