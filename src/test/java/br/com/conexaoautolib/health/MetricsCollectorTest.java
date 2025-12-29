package br.com.conexaoautolib.health;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricsCollectorTest {
    
    private MeterRegistry meterRegistry;
    private MetricsCollector metricsCollector;
    
    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsCollector = new MetricsCollector(meterRegistry);
    }
    
    @Test
    void recordTokenIssued_shouldRegisterAndIncrementCounter() {
        // Arrange
        String serverUrl = "http://test-server";
        String clientId = "test-client";
        
        // Act
        metricsCollector.recordTokenIssued(serverUrl, clientId);
        
        // Assert
        Counter counter = meterRegistry.find("conexaoauth.token.issued")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count(), 0.001);
    }
    
    @Test
    void recordTokenRenewed_shouldRegisterAndIncrementCounter() {
        // Arrange
        String serverUrl = "http://test-server";
        String clientId = "test-client";
        
        // Act
        metricsCollector.recordTokenRenewed(serverUrl, clientId);
        
        // Assert
        Counter counter = meterRegistry.find("conexaoauth.token.renewed")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count(), 0.001);
    }
    
    @Test
    void recordAuthFailure_shouldRegisterAndIncrementCounter() {
        // Arrange
        String serverUrl = "http://test-server";
        String clientId = "test-client";
        String errorType = "INVALID_CREDENTIALS";
        
        // Act
        metricsCollector.recordAuthFailure(serverUrl, clientId, errorType);
        
        // Assert
        Counter counter = meterRegistry.find("conexaoauth.auth.failures")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("error_type", errorType)
                .counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count(), 0.001);
    }
    
    @Test
    void recordAuthLatency_withDuration_shouldRegisterAndRecordTimer() {
        // Arrange
        String serverUrl = "http://test-server";
        String clientId = "test-client";
        String operation = "AUTHENTICATION";
        Duration duration = Duration.ofMillis(100);
        boolean success = true;
        
        // Act
        metricsCollector.recordAuthLatency(serverUrl, clientId, operation, duration, success);
        
        // Assert
        Timer timer = meterRegistry.find("conexaoauth.auth.latency")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("operation", operation)
                .tag("success", "true")
                .timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
        assertEquals(100.0, timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS), 0.001);
    }
    
    @Test
    void recordCounter_withValidTags_shouldRegisterAndIncrementCounter() {
        // Arrange
        String metricName = "custom.metric";
        String[] tags = {"tag1", "value1", "tag2", "value2"};
        
        // Act
        metricsCollector.recordCounter(metricName, tags);
        
        // Assert
        Counter counter = meterRegistry.find(metricName)
                .tag("tag1", "value1")
                .tag("tag2", "value2")
                .counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count(), 0.001);
    }
    
    @Test
    void recordCounter_withInvalidTags_shouldThrowException() {
        // Arrange
        String metricName = "custom.metric";
        String[] tags = {"tag1", "value1", "tag2"}; // Odd number of tags
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            metricsCollector.recordCounter(metricName, tags);
        });
    }
    
    @Test
    void recordTimer_withValidTags_shouldRegisterAndRecordTimer() {
        // Arrange
        String metricName = "custom.timer";
        Duration duration = Duration.ofMillis(200);
        String[] tags = {"tag1", "value1", "tag2", "value2"};
        
        // Act
        metricsCollector.recordTimer(metricName, duration, tags);
        
        // Assert
        Timer timer = meterRegistry.find(metricName)
                .tag("tag1", "value1")
                .tag("tag2", "value2")
                .timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
        assertEquals(200.0, timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS), 0.001);
    }
    
    @Test
    void recordAuthLatency_withSupplier_shouldRecordTimer() {
        // Arrange
        String serverUrl = "http://test-server";
        String clientId = "test-client";
        String operation = "AUTHENTICATION";
        Supplier<String> supplier = () -> "success";
        
        // Act
        String result = metricsCollector.recordAuthLatency(serverUrl, clientId, operation, supplier);
        
        // Assert
        assertEquals("success", result);
        
        Timer timer = meterRegistry.find("conexaoauth.auth.latency")
                .tag("server_url", serverUrl)
                .tag("client_id", clientId)
                .tag("operation", operation)
                .tag("success", "true")
                .timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
    }
}