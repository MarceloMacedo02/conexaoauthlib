package br.com.conexaoautolib.health;

import br.com.conexaoautolib.autoconfigure.properties.HealthProperties;
import br.com.conexaoautolib.autoconfigure.properties.ServerProperties;
import br.com.conexaoautolib.model.response.TokenResponse;
import br.com.conexaoautolib.storage.TokenStorage;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Health Indicator para ConexãoAuthLib.
 * 
 * Verifica a saúde dos principais componentes da biblioteca incluindo
 * conectividade com servidor e funcionalidade do armazenamento de tokens.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Component
public class ConexaoAuthHealthIndicator implements HealthIndicator {
    
    private final HealthProperties healthProperties;
    private final ServerProperties serverProperties;
    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage;
    
    public ConexaoAuthHealthIndicator(HealthProperties healthProperties,
                                    ServerProperties serverProperties,
                                    TokenStorage tokenStorage) {
        this.healthProperties = healthProperties;
        this.serverProperties = serverProperties;
        this.tokenStorage = tokenStorage;
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public Health health() {
        if (!healthProperties.isEnabled()) {
            return Health.unknown()
                    .withDetail("status", "Health checks desabilitados")
                    .build();
        }
        
        Map<String, Object> details = new HashMap<>();
        boolean allHealthy = true;
        Instant startTime = Instant.now();
        
        try {
            // Verificar conectividade com servidor
            if (healthProperties.isCheckServerConnectivity()) {
                boolean serverHealthy = checkServerConnectivity(details);
                allHealthy = allHealthy && serverHealthy;
            }
            
            // Verificar armazenamento de tokens (placeholder para implementação futura)
            if (healthProperties.isCheckTokenStorage()) {
                boolean storageHealthy = checkTokenStorage(details);
                allHealthy = allHealthy && storageHealthy;
            }
            
            // Adicionar informações de tempo de resposta
            Duration responseTime = Duration.between(startTime, Instant.now());
            details.put("responseTime", responseTime.toMillis() + "ms");
            details.put("timestamp", Instant.now().toString());
            
            if (allHealthy) {
                details.put("status", "Todos os componentes estão saudáveis");
                return Health.up().withDetails(details).build();
            } else {
                details.put("status", "Alguns componentes apresentam falhas");
                return Health.down().withDetails(details).build();
            }
            
        } catch (Exception e) {
            details.put("status", "Falha na verificação de saúde");
            details.put("error", e.getMessage());
            return Health.down().withDetails(details).build();
        }
    }
    
    /**
     * Verifica conectividade com o servidor ConexãoAuth.
     */
    private boolean checkServerConnectivity(Map<String, Object> details) {
        Map<String, Object> serverInfo = new HashMap<>();
        boolean allEndpointsHealthy = true;
        
        for (String endpoint : healthProperties.getEndpoints()) {
            String fullUrl = serverProperties.getUrl() + endpoint;
            boolean isHealthy = false;
            String error = null;
            long responseTime = 0;
            
            Instant startTime = Instant.now();
            
            for (int attempt = 1; attempt <= healthProperties.getRetryAttempts(); attempt++) {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
                    responseTime = Duration.between(startTime, Instant.now()).toMillis();
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        isHealthy = true;
                        break;
                    } else {
                        error = "HTTP " + response.getStatusCode().value();
                    }
                    
                } catch (ResourceAccessException e) {
                    error = "Servidor inacessível: " + e.getMessage();
                } catch (Exception e) {
                    error = "Erro inesperado: " + e.getMessage();
                }
                
                if (attempt < healthProperties.getRetryAttempts()) {
                    try {
                        Thread.sleep(1000); // Esperar 1 segundo entre tentativas
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            Map<String, Object> endpointInfo = new HashMap<>();
            endpointInfo.put("healthy", isHealthy);
            endpointInfo.put("responseTime", responseTime + "ms");
            endpointInfo.put("attempts", healthProperties.getRetryAttempts());
            if (error != null) {
                endpointInfo.put("error", error);
            }
            
            serverInfo.put(endpoint, endpointInfo);
            allEndpointsHealthy = allEndpointsHealthy && isHealthy;
        }
        
        details.put("serverConnectivity", serverInfo);
        return allEndpointsHealthy;
    }
    
    /**
     * Verifica funcionalidade do armazenamento de tokens.
     * Implementação completa que testa operações básicas do TokenStorage.
     */
    private boolean checkTokenStorage(Map<String, Object> details) {
        Map<String, Object> tokenStorageInfo = new HashMap<>();
        boolean isHealthy = true;
        
        try {
            if (tokenStorage == null) {
                tokenStorageInfo.put("status", "DOWN");
                tokenStorageInfo.put("details", "TokenStorage não está configurado");
                isHealthy = false;
            } else {
                // Teste de escrita/leitura do storage
                String testKey = "health-check-test";
                TokenResponse testToken = createTestTokenResponse();
                
                // Testa armazenamento
                tokenStorage.store(testKey, testToken);
                
                // Testa recuperação
                Optional<TokenResponse> retrieved = tokenStorage.retrieve(testKey);
                if (!retrieved.isPresent()) {
                    tokenStorageInfo.put("storeRetrieve", "FAIL");
                    isHealthy = false;
                } else {
                    tokenStorageInfo.put("storeRetrieve", "PASS");
                }
                
                // Testa validação
                boolean isValid = tokenStorage.isValid(testKey);
                tokenStorageInfo.put("validation", isValid ? "PASS" : "FAIL");
                if (!isValid) {
                    isHealthy = false;
                }
                
                // Testa limpeza
                tokenStorage.invalidate(testKey);
                Optional<TokenResponse> afterCleanup = tokenStorage.retrieve(testKey);
                if (afterCleanup.isPresent()) {
                    tokenStorageInfo.put("cleanup", "FAIL");
                    isHealthy = false;
                } else {
                    tokenStorageInfo.put("cleanup", "PASS");
                }
                
                // Informações gerais do storage
                tokenStorageInfo.put("type", tokenStorage.getClass().getSimpleName());
                tokenStorageInfo.put("size", tokenStorage.size());
                tokenStorageInfo.put("empty", tokenStorage.isEmpty());
                
                if (isHealthy) {
                    tokenStorageInfo.put("status", "UP");
                    tokenStorageInfo.put("details", "Todas as operações de storage funcionam corretamente");
                } else {
                    tokenStorageInfo.put("status", "DOWN");
                    tokenStorageInfo.put("details", "Falha nas operações de armazenamento de tokens");
                }
            }
        } catch (Exception e) {
            tokenStorageInfo.put("status", "DOWN");
            tokenStorageInfo.put("details", "Erro ao verificar armazenamento: " + e.getMessage());
            tokenStorageInfo.put("error", e.getClass().getSimpleName());
            isHealthy = false;
        }
        
        details.put("tokenStorage", tokenStorageInfo);
        return isHealthy;
    }
    
    /**
     * Cria um token de teste para verificação do storage.
     */
    private TokenResponse createTestTokenResponse() {
        return TokenResponse.builder()
                .accessToken("test-access-token-health-check")
                .tokenType("Bearer")
                .expiresIn(300L)
                .refreshToken("test-refresh-token-health-check")
                .scope("health-check")
                .build();
    }
}