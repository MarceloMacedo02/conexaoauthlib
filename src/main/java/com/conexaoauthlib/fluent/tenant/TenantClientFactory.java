package com.conexaoauthlib.fluent.tenant;

import com.conexaoauthlib.feign.tenant.TenantClient;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Factory para criação de instâncias de TenantClient.
 *
 * <p>Esta classe utiliza o Spring Cloud OpenFeign para criar
 * instâncias dinâmicas de TenantClient com todas as configurações
 * de resiliência e interceptores aplicadas.</p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // A factory é automaticamente inicializada pelo Spring
 * // Use TenantClient para operações fluentes
 *
 * TenantResponseDTO tenant = TenantClient.create()
 *     .name("Empresa X")
 *     .documentNumber("12345678000100")
 *     .product("premium")
 *     .executeWithProducts();
 * }</pre>
 *
 * <h3>Nota sobre uso fora do Spring:</h3>
 * <pre>{@code
 * // Para uso fora do Spring, configure o contexto primeiro
 * TenantClientFactory.setApplicationContext(applicationContext);
 *
 * // Ou crie o cliente diretamente
 * TenantClient client = TenantClientFactory.createTenantClient();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see TenantClient
 * @see com.conexaoauthlib.feign.tenant.TenantClient
 */
public final class TenantClientFactory {

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
        TenantClientFactory.applicationContext = context;
    }

    /**
     * Cria uma nova instância de TenantClient.
     *
     * <p>O cliente criado inclui todas as configurações de:</p>
     * <ul>
     *   <li>Resiliência (CircuitBreaker, Retry)</li>
     *   <li>Interceptores de autenticação</li>
     *   <li>Tratamento de erros</li>
     *   <li>Serialização JSON</li>
     * </ul>
     *
     * @return TenantClient configurado e pronto para uso
     * @throws IllegalStateException se o ApplicationContext não estiver configurado
     */
    public static TenantClient createTenantClient() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                "ApplicationContext não configurado. " +
                "Certifique-se de que a biblioteca está sendo usada em contexto Spring."
            );
        }

        FeignClientBuilder builder = new FeignClientBuilder(applicationContext);
        return builder.forType(TenantClient.class, "tenant").build();
    }
}
