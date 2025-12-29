package br.com.conexaoautolib.health;

import br.com.conexaoautolib.autoconfigure.properties.HealthProperties;
import br.com.conexaoautolib.autoconfigure.properties.ServerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConexaoAuthHealthIndicatorTest {
    
    @Mock
    private HealthProperties healthProperties;
    
    @Mock
    private ServerProperties serverProperties;
    
    @Mock
    private RestTemplate restTemplate;
    
    private ConexaoAuthHealthIndicator healthIndicator;
    
    @BeforeEach
    void setUp() {
        healthIndicator = new ConexaoAuthHealthIndicator(healthProperties, serverProperties);
        ReflectionTestUtils.setField(healthIndicator, "restTemplate", restTemplate);
    }
    
    @Test
    void whenHealthChecksDisabled_thenReturnUnknownStatus() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(false);
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UNKNOWN, result.getStatus());
        assertEquals("Health checks desabilitados", result.getDetails().get("status"));
    }
    
    @Test
    void whenServerConnectivityCheckSuccess_thenReturnUpStatus() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(true);
        when(healthProperties.isCheckTokenStorage()).thenReturn(true);
        when(healthProperties.getEndpoints()).thenReturn(Arrays.asList("/health"));
        when(healthProperties.getRetryAttempts()).thenReturn(1);
        when(serverProperties.getUrl()).thenReturn("http://test-server");
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body("OK"));
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UP, result.getStatus());
        assertNotNull(result.getDetails().get("serverConnectivity"));
        assertNotNull(result.getDetails().get("tokenStorage"));
        assertEquals("Todos os componentes estão saudáveis", result.getDetails().get("status"));
    }
    
    @Test
    void whenServerConnectivityCheckFails_thenReturnDownStatus() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(true);
        when(healthProperties.isCheckTokenStorage()).thenReturn(true);
        when(healthProperties.getEndpoints()).thenReturn(Arrays.asList("/health"));
        when(healthProperties.getRetryAttempts()).thenReturn(1);
        when(serverProperties.getUrl()).thenReturn("http://test-server");
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.DOWN, result.getStatus());
        assertNotNull(result.getDetails().get("serverConnectivity"));
        assertNotNull(result.getDetails().get("status"));
    }
    
    @Test
    void whenMultipleEndpoints_thenReturnAggregateHealth() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(true);
        when(healthProperties.isCheckTokenStorage()).thenReturn(true);
        when(healthProperties.getEndpoints()).thenReturn(Arrays.asList("/health", "/actuator/health"));
        when(healthProperties.getRetryAttempts()).thenReturn(1);
        when(serverProperties.getUrl()).thenReturn("http://test-server");
        
        when(restTemplate.getForEntity(eq("http://test-server/health"), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body("OK"));
        when(restTemplate.getForEntity(eq("http://test-server/actuator/health"), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body("UP"));
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UP, result.getStatus());
        @SuppressWarnings("unchecked")
        var serverConnectivity = (java.util.Map<String, Object>) result.getDetails().get("serverConnectivity");
        assertTrue(serverConnectivity.containsKey("/health"));
        assertTrue(serverConnectivity.containsKey("/actuator/health"));
    }
    
    @Test
    void whenTokenStorageCheckDisabled_thenReturnUpStatusWithoutTokenStorageDetails() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(true);
        when(healthProperties.isCheckTokenStorage()).thenReturn(false);
        when(healthProperties.getEndpoints()).thenReturn(Arrays.asList("/health"));
        when(healthProperties.getRetryAttempts()).thenReturn(1);
        when(serverProperties.getUrl()).thenReturn("http://test-server");
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body("OK"));
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UP, result.getStatus());
        assertNotNull(result.getDetails().get("serverConnectivity"));
        assertFalse(result.getDetails().containsKey("tokenStorage"));
    }
    
    @Test
    void whenServerConnectivityCheckDisabled_thenReturnUpStatusWithoutServerConnectivityDetails() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(false);
        when(healthProperties.isCheckTokenStorage()).thenReturn(true);
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UP, result.getStatus());
        assertNotNull(result.getDetails().get("tokenStorage"));
        assertFalse(result.getDetails().containsKey("serverConnectivity"));
    }
    
    @Test
    void whenRetryAttemptsSucceedOnSecondTry_thenReturnUpStatus() {
        // Arrange
        when(healthProperties.isEnabled()).thenReturn(true);
        when(healthProperties.isCheckServerConnectivity()).thenReturn(true);
        when(healthProperties.isCheckTokenStorage()).thenReturn(true);
        when(healthProperties.getEndpoints()).thenReturn(Arrays.asList("/health"));
        when(healthProperties.getRetryAttempts()).thenReturn(2);
        when(serverProperties.getUrl()).thenReturn("http://test-server");
        
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection refused"))
                .thenReturn(ResponseEntity.ok().body("OK"));
        
        // Act
        Health result = healthIndicator.health();
        
        // Assert
        assertEquals(Status.UP, result.getStatus());
        @SuppressWarnings("unchecked")
        var serverConnectivity = (java.util.Map<String, Object>) result.getDetails().get("serverConnectivity");
        @SuppressWarnings("unchecked")
        var healthEndpoint = (java.util.Map<String, Object>) serverConnectivity.get("/health");
        assertEquals(true, healthEndpoint.get("healthy"));
        assertEquals(2, healthEndpoint.get("attempts"));
    }
}