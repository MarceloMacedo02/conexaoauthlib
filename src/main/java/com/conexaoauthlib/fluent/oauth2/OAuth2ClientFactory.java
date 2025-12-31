package com.conexaoauthlib.fluent.oauth2;

import com.conexaoauthlib.feign.oauth2.OAuth2Client;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Factory para criação de instâncias de OAuth2Client.
 * Gerencia o ciclo de vida dos clientes Feign.
 *
 * <p>Esta classe utiliza o Spring Cloud OpenFeign para criar
 * instâncias dinâmicas de OAuth2Client com todas as configurações
 * de resiliência e interceptores aplicadas.</p>
 *
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * // A factory é automaticamente inicializada pelo Spring
 * // Use AuthClient para operações fluentes
 *
 * TokenResponseDTO token = AuthClient.clientCredentials("client-id", "secret")
 *     .scope("read write")
 *     .execute();
 * }</pre>
 *
 * <h3>Nota sobre uso fora do Spring:</h3>
 * <pre>{@code
 * // Para uso fora do Spring, configure o contexto primeiro
 * OAuth2ClientFactory.setApplicationContext(applicationContext);
 *
 * // Ou crie o cliente diretamente
 * OAuth2Client client = OAuth2ClientFactory.createOAuth2Client();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 * @see AuthClient
 * @see OAuth2Client
 */
public final class OAuth2ClientFactory {

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
        OAuth2ClientFactory.applicationContext = context;
    }

    /**
     * Cria uma nova instância de OAuth2Client.
     *
     * <p>O cliente criado inclui todas as configurações de:</p>
     * <ul>
     *   <li>Resiliência (CircuitBreaker, Retry)</li>
     *   <li>Interceptores de autenticação</li>
     *   <li>Tratamento de erros</li>
     *   <li>Serialização JSON</li>
     * </ul>
     *
     * @return OAuth2Client configurado e pronto para uso
     * @throws IllegalStateException se o ApplicationContext não estiver configurado
     */
    public static OAuth2Client createOAuth2Client() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                "ApplicationContext não configurado. " +
                "Certifique-se de que a biblioteca está sendo usada em contexto Spring."
            );
        }

        FeignClientBuilder builder = new FeignClientBuilder(applicationContext);
        return builder.forType(OAuth2Client.class, "oauth2").build();
    }
}
