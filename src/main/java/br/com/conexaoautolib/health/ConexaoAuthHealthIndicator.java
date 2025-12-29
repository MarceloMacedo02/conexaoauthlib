package br.com.conexaoautolib.health;

import br.com.conexaoautolib.autoconfigure.properties.HealthProperties;
import br.com.conexaoautolib.autoconfigure.properties.ServerProperties;
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
    
    public ConexaoAuthHealthIndicator(HealthProperties healthProperties,
                                    ServerProperties serverProperties) {
        this.healthProperties = healthProperties;
        this.serverProperties = serverProperties;
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
     * Placeholder implementação - será expandida na Story 2.5.
     */
    private boolean checkTokenStorage(Map<String, Object> details) {
        Map<String, Object> tokenStorageInfo = new HashMap<>();
        boolean isHealthy = false;
        
        try {
            // Placeholder para verificação real do armazenamento
            // Por enquanto, verificamos se o sistema pode acessar o sistema de arquivos
            String tempDir = System.getProperty("java.io.tmpdir");
            if (tempDir != null && java.nio.file.Files.isWritable(java.nio.file.Paths.get(tempDir))) {
                tokenStorageInfo.put("status", "UP");
                tokenStorageInfo.put("details", "Sistema de armazenamento acessível");
                isHealthy = true;
            } else {
                tokenStorageInfo.put("status", "DOWN");
                tokenStorageInfo.put("details", "Sistema de armazenamento inacessível");
                isHealthy = false;
            }
        } catch (Exception e) {
            tokenStorageInfo.put("status", "DOWN");
            tokenStorageInfo.put("details", "Erro ao verificar armazenamento: " + e.getMessage());
            isHealthy = false;
        }
        
        details.put("tokenStorage", tokenStorageInfo);
        return isHealthy;
    }
}