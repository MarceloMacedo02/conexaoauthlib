package com.conexaoauthlib.feign.oauth2;

import com.conexaoauthlib.dto.oauth2.IntrospectRequestDTO;
import com.conexaoauthlib.dto.oauth2.IntrospectResponseDTO;
import com.conexaoauthlib.dto.oauth2.RevokeRequestDTO;
import com.conexaoauthlib.dto.oauth2.TokenRequestDTO;
import com.conexaoauthlib.dto.oauth2.TokenResponseDTO;
import com.conexaoauthlib.exception.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Interface Feign Client para operações OAuth2.
 *
 * <p>Fornece métodos declarativos para token, introspecção e revogação.
 * Todos os métodos incluem anotações de resiliência (CircuitBreaker e Retry).</p>
 *
 * <h3>Exemplo de Uso:</h3>
 * <pre>{@code
 * // Injeção via Spring
 * @Autowired
 * private OAuth2Client oauth2Client;
 *
 * // Obtenção de token
 * TokenResponseDTO token = oauth2Client.getToken(
 *     TokenRequestDTO.builder()
 *         .grantType("client_credentials")
 *         .clientId("my-client")
 *         .clientSecret("secret")
 *         .scope("read write")
 *         .build()
 * );
 *
 * String accessToken = token.getAccessToken();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(
    name = "oauth2",
    url = "${conexao-auth.clients.oauth2.base-url}",
    configuration = {OAuth2ClientConfiguration.class}
)
public interface OAuth2Client {

    @PostMapping(
        value = "/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "tokenFallback")
    @Retry(name = "oauth2")
    TokenResponseDTO getToken(@RequestBody TokenRequestDTO request);

    /**
     * Solicita um novo access token com suporte a multi-tenant.
     *
     * @param request DTO com dados do grant
     * @param xTenantId Header de contexto multi-tenant (opcional)
     * @return TokenResponseDTO com access token e metadados
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "tokenFallback")
    @Retry(name = "oauth2")
    TokenResponseDTO getToken(
        @RequestBody TokenRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Introspecciona um token para verificar sua validade e claims.
     *
     * <p>Segue a especificação RFC 7662. Retorna se o token está ativo
     * e quais claims ele contém.</p>
     *
     * @param request DTO com o token a ser analisado
     * @return IntrospectResponseDTO com status e claims do token
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/introspect",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "introspectFallback")
    @Retry(name = "oauth2")
    IntrospectResponseDTO introspect(@RequestBody IntrospectRequestDTO request);

    /**
     * Introspecciona um token com suporte a multi-tenant.
     *
     * @param request DTO com o token a ser analisado
     * @param xTenantId Header de contexto multi-tenant (opcional)
     * @return IntrospectResponseDTO com status e claims do token
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/introspect",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "introspectFallback")
    @Retry(name = "oauth2")
    IntrospectResponseDTO introspect(
        @RequestBody IntrospectRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    /**
     * Revoga um token antes de sua expiração natural.
     *
     * <p>Após a revogação, o token não poderá mais ser utilizado
     * para autenticação. Funciona para access tokens e refresh tokens.</p>
     *
     * @param request DTO com o token a ser revogado
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/revoke",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "revokeFallback")
    @Retry(name = "oauth2")
    void revoke(@RequestBody RevokeRequestDTO request);

    /**
     * Revoga um token com suporte a multi-tenant.
     *
     * @param request DTO com o token a ser revogado
     * @param xTenantId Header de contexto multi-tenant (opcional)
     * @throws CircuitBreakerOpenException se o circuit breaker estiver aberto
     */
    @PostMapping(
        value = "/revoke",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "oauth2", fallbackMethod = "revokeFallback")
    @Retry(name = "oauth2")
    void revoke(
        @RequestBody RevokeRequestDTO request,
        @RequestHeader(value = "X-Tenant-Id", required = false) String xTenantId
    );

    // ==================== Fallback Methods ====================

    /**
     * Fallback para getToken quando o circuit breaker está aberto.
     *
     * @param request Requisição original
     * @param t Exceção original
     */
    default TokenResponseDTO tokenFallback(TokenRequestDTO request, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to obtain token", t);
    }

    /**
     * Fallback para getToken (com tenant) quando o circuit breaker está aberto.
     *
     * @param request Requisição original
     * @param xTenantId Header de contexto
     * @param t Exceção original
     */
    default TokenResponseDTO tokenFallback(TokenRequestDTO request, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to obtain token", t);
    }

    /**
     * Fallback para introspect quando o circuit breaker está aberto.
     *
     * @param request Requisição original
     * @param t Exceção original
     */
    default IntrospectResponseDTO introspectFallback(IntrospectRequestDTO request, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to introspect token", t);
    }

    /**
     * Fallback para introspect (com tenant) quando o circuit breaker está aberto.
     *
     * @param request Requisição original
     * @param xTenantId Header de contexto
     * @param t Exceção original
     */
    default IntrospectResponseDTO introspectFallback(IntrospectRequestDTO request, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to introspect token", t);
    }

    /**
     * Fallback para revoke quando o circuit breaker está aberto.
     *
     * <p>Nota: Falhas de revogação não são críticas, pois o token
     * pode expirar naturalmente.</p>
     *
     * @param request Requisição original
     * @param t Exceção original
     */
    default void revokeFallback(RevokeRequestDTO request, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to revoke token", t);
    }

    /**
     * Fallback para revoke (com tenant) quando o circuit breaker está aberto.
     *
     * @param request Requisição original
     * @param xTenantId Header de contexto
     * @param t Exceção original
     */
    default void revokeFallback(RevokeRequestDTO request, String xTenantId, Throwable t) {
        throw new CircuitBreakerOpenException(
            "OAuth2 service unavailable - unable to revoke token", t);
    }
}
