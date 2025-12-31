package com.conexaoauthlib.fluent.user;

import com.conexaoauthlib.feign.user.UserClient;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Factory para criação de instâncias de UserClient.
 *
 * <p>Esta classe utiliza o Spring Cloud OpenFeign para criar
 * instâncias dinâmicas de UserClient com todas as configurações
 * de resiliência e interceptores aplicadas.</p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // A factory é automaticamente inicializada pelo Spring
 * // Use UserClient para operações fluentes
 *
 * UserResponseDTO user = UserClient.register()
 *     .name("João Silva")
 *     .email("joao@empresa.com")
 *     .password("Senha123!")
 *     .tenantId("tenant-123")
 *     .execute();
 * }</pre>
 *
 * <h3>Nota sobre uso fora do Spring:</h3>
 * <pre>{@code
 * // Para uso fora do Spring, configure o contexto primeiro
 * UserClientFactory.setApplicationContext(applicationContext);
 *
 * // Ou crie o cliente diretamente
 * UserClient client = UserClientFactory.createUserClient();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see UserClient
 * @see com.conexaoauthlib.feign.user.UserClient
 */
public final class UserClientFactory {

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
        UserClientFactory.applicationContext = context;
    }

    /**
     * Cria uma nova instância de UserClient.
     *
     * <p>O cliente criado inclui todas as configurações de:</p>
     * <ul>
     *   <li>Resiliência (CircuitBreaker, Retry)</li>
     *   <li>Interceptores de autenticação</li>
     *   <li>Tratamento de erros</li>
     *   <li>Serialização JSON</li>
     * </ul>
     *
     * @return UserClient configurado e pronto para uso
     * @throws IllegalStateException se o ApplicationContext não estiver configurado
     */
    public static UserClient createUserClient() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                "ApplicationContext não configurado. " +
                "Certifique-se de que a biblioteca está sendo usada em contexto Spring."
            );
        }

        FeignClientBuilder builder = new FeignClientBuilder(applicationContext);
        return builder.forType(UserClient.class, "user").build();
    }
}
