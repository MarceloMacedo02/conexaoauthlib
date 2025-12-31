package com.conexaoauthlib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Classe principal da biblioteca ConexãoAuthLib.
 *
 * Esta biblioteca fornece clientes Feign declarativos para a API de autenticação
 * docauth, expondo uma API Fluent Builder intuitiva que abstrai completamente
 * o transporte HTTP enquanto garante resiliência via Resilience4j.
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class ConexaoAuthLib {

    /**
     * Método de inicialização da biblioteca.
     * Pode ser usado para configurações globais antes da primeira chamada.
     */
    public static void initialize() {
        // Configurações globais podem ser adicionadas aqui
    }
}
