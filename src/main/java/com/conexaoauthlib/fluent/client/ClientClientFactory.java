package com.conexaoauthlib.fluent.client;

import com.conexaoauthlib.feign.client.ClientClient;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Factory para criação de instâncias de ClientClient.
 *
 * <p>Esta classe utiliza o Spring Cloud OpenFeign para criar
 * instâncias dinâmicas de ClientClient com todas as configurações
 * de resiliência e interceptores aplicadas.</p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // A factory é automaticamente inicializada pelo Spring
 * // Use ClientClient para operações fluentes
 *
 * ClientResponseDTO client = ClientClient.register()
 *     .clientId("my-service")
 *     .clientSecret("initial-secret")
 *     .name("My Service")
 *     .execute();
 * }</pre>
 *
 * <h3>Nota sobre uso fora do Spring:</h3>
 * <pre>{@code
 * // Para uso fora do Spring, configure o contexto primeiro
 * ClientClientFactory.setApplicationContext(applicationContext);
 *
 * // Ou crie o cliente diretamente
 * ClientClient client = ClientClientFactory.createClientClient();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see ClientClient
 * @see com.conexaoauthlib.feign.client.ClientClient
 */
public final class ClientClientFactory {

    private static ApplicationContext applicationContext;

    /**
     * Injeta o ApplicationContext (chamado automaticamente pelo Spring).
     *
     * <p>Este método é destinado a ser chamado pelo container Spring
     * durante a inicialização da biblioteca.</p>
     *
     * @param context ApplicationContext do Spring
     */
    public static void setApplicationContext(ApplicationContext context) {
        ClientClientFactory.applicationContext = context;
    }

    /**
     * Cria uma nova instância de ClientClient.
     *
     * <p>O cliente criado inclui todas as configurações de:</p>
     * <ul>
     *   <li>Resiliência (CircuitBreaker, Retry)</li>
     *   <li>Interceptores de autenticação</li>
     *   <li>Tratamento de erros</li>
     *   <li>Serialização JSON</li>
     * </ul>
     *
     * @return ClientClient configurado e pronto para uso
     * @throws IllegalStateException se o ApplicationContext não estiver configurado
     */
    public static ClientClient createClientClient() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                "ApplicationContext não configurado. " +
                "Certifique-se de que a biblioteca está sendo usada em contexto Spring."
            );
        }

        FeignClientBuilder builder = new FeignClientBuilder(applicationContext);
        return builder.forType(ClientClient.class, "client").build();
    }
}
