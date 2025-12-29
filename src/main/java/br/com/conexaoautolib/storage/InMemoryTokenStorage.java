package br.com.conexaoautolib.storage;

import br.com.conexaoautolib.model.response.TokenResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação em memória de TokenStorage.
 * 
 * <p>Armazena tokens em um ConcurrentHashMap para thread-safety.
 * Ideal para desenvolvimento, testes e aplicações de instância única.</p>
 * 
 * <p><strong>Atenção:</strong> Tokens são perdidos quando a aplicação é reiniciada.
 * Para produção, considere implementações persistentes como Redis ou database.</p>
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class InMemoryTokenStorage implements TokenStorage {

    private final Map<String, TokenWrapper> tokenStore = new ConcurrentHashMap<>();

    @Override
    public void store(String key, TokenResponse tokenResponse) {
        if (key == null || tokenResponse == null) {
            throw new IllegalArgumentException("Key e TokenResponse não podem ser nulos");
        }
        
        tokenStore.put(key, new TokenWrapper(tokenResponse, LocalDateTime.now()));
    }

    @Override
    public Optional<TokenResponse> retrieve(String key) {
        if (key == null) {
            return Optional.empty();
        }
        
        TokenWrapper wrapper = tokenStore.get(key);
        return wrapper != null ? Optional.of(wrapper.tokenResponse) : Optional.empty();
    }

    @Override
    public Optional<TokenResponse> retrieveIfValid(String key) {
        if (key == null) {
            return Optional.empty();
        }
        
        TokenWrapper wrapper = tokenStore.get(key);
        if (wrapper == null) {
            return Optional.empty();
        }
        
        // Remove token expirado e retorna empty
        if (isExpired(wrapper.tokenResponse)) {
            tokenStore.remove(key);
            return Optional.empty();
        }
        
        return Optional.of(wrapper.tokenResponse);
    }

    @Override
    public boolean isValid(String key) {
        if (key == null) {
            return false;
        }
        
        TokenWrapper wrapper = tokenStore.get(key);
        if (wrapper == null) {
            return false;
        }
        
        // Remove token expirado
        if (isExpired(wrapper.tokenResponse)) {
            tokenStore.remove(key);
            return false;
        }
        
        return true;
    }

    @Override
    public void invalidate(String key) {
        if (key != null) {
            tokenStore.remove(key);
        }
    }

    @Override
    public void clear() {
        tokenStore.clear();
    }

    @Override
    public int size() {
        // Remove tokens expirados antes de contar
        cleanupExpired();
        return tokenStore.size();
    }

    @Override
    public boolean isEmpty() {
        // Remove tokens expirados antes de verificar
        cleanupExpired();
        return tokenStore.isEmpty();
    }

    /**
     * Limpa tokens expirados do storage.
     * Método otimizado para ser chamado periodicamente.
     */
    public void cleanupExpired() {
        tokenStore.entrySet().removeIf(entry -> isExpired(entry.getValue().tokenResponse));
    }

    /**
     * Wrapper interno para armazenar token junto com timestamp de armazenamento.
     */
    private static class TokenWrapper {
        private final TokenResponse tokenResponse;
        private final LocalDateTime storedAt;

        public TokenWrapper(TokenResponse tokenResponse, LocalDateTime storedAt) {
            this.tokenResponse = tokenResponse;
            this.storedAt = storedAt;
        }

        public TokenResponse getTokenResponse() {
            return tokenResponse;
        }

        public LocalDateTime getStoredAt() {
            return storedAt;
        }
    }

    /**
     * Retorna uma visão imutável dos tokens armazenados para debug.
     * 
     * @return Map com cópia dos tokens (redatados para segurança)
     */
    public Map<String, String> getDebugInfo() {
        Map<String, String> debugInfo = new ConcurrentHashMap<>();
        tokenStore.forEach((key, wrapper) -> {
            debugInfo.put(key, wrapper.tokenResponse.toString());
        });
        return Collections.unmodifiableMap(debugInfo);
    }

    /**
     * Obtém timestamp de armazenamento de um token.
     * 
     * @param key Chave do token
     * @return Optional com timestamp se encontrado, empty caso contrário
     */
    public Optional<LocalDateTime> getStoredAt(String key) {
        if (key == null) {
            return Optional.empty();
        }
        
        TokenWrapper wrapper = tokenStore.get(key);
        return wrapper != null ? Optional.of(wrapper.getStoredAt()) : Optional.empty();
    }
}