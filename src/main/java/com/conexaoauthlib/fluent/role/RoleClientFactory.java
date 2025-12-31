package com.conexaoauthlib.fluent.role;

import com.conexaoauthlib.feign.role.RoleClient;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Factory para criação de instâncias de RoleClient.
 *
 * <p>Esta classe utiliza o Spring Cloud OpenFeign para criar
 * instâncias dinâmicas de RoleClient com todas as configurações
 * de resiliência e interceptores aplicadas.</p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // A factory é automaticamente inicializada pelo Spring
 * // Use RoleClient para operações fluentes
 *
 * RoleResponseDTO role = RoleClient.create()
 *     .name("admin")
 *     .description("Administrador do sistema")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * <h3>Nota sobre uso fora do Spring:</h3>
 * <pre>{@code
 * // Para uso fora do Spring, configure o contexto primeiro
 * RoleClientFactory.setApplicationContext(applicationContext);
 *
 * // Ou crie o cliente diretamente
 * RoleClient client = RoleClientFactory.createRoleClient();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see RoleClient
 * @see com.conexaoauthlib.feign.role.RoleClient
 */
public final class RoleClientFactory {

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
        RoleClientFactory.applicationContext = context;
    }

    /**
     * Cria uma nova instância de RoleClient.
     *
     * <p>O cliente criado inclui todas as configurações de:</p>
     * <ul>
     *   <li>Resiliência (CircuitBreaker, Retry)</li>
     *   <li>Interceptores de autenticação</li>
     *   <li>Tratamento de erros</li>
     *   <li>Serialização JSON</li>
     * </ul>
     *
     * @return RoleClient configurado e pronto para uso
     * @throws IllegalStateException se o ApplicationContext não estiver configurado
     */
    public static RoleClient createRoleClient() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                "ApplicationContext não configurado. " +
                "Certifique-se de que a biblioteca está sendo usada em contexto Spring."
            );
        }

        FeignClientBuilder builder = new FeignClientBuilder(applicationContext);
        return builder.forType(RoleClient.class, "role").build();
    }
}
