package br.com.conexaoautolib.interceptor;

import br.com.conexaoautolib.autoconfigure.properties.ClientProperties;
import br.com.conexaoautolib.model.response.TokenResponse;
import br.com.conexaoautolib.storage.TokenStorage;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

/**
 * Interceptor Feign para injeção automática de tokens OAuth2.
 * 
 * <p>Este interceptor automaticamente adiciona o header Authorization com
 * Bearer token em todas as requisições Feign configuradas para usar
 * o ConexãoAuthLib.</p>
 * 
 * <p>Busca tokens no TokenStorage e os injeta se válidos.
 * Se não houver token válido, não modifica a requisição.</p>
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class TokenInjectionInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TokenInjectionInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenStorage tokenStorage;
    private final ClientProperties clientProperties;

    /**
     * Constrói um novo TokenInjectionInterceptor.
     * 
     * @param tokenStorage Storage para busca de tokens
     * @param clientProperties Propriedades do cliente para geração de chave
     */
    public TokenInjectionInterceptor(TokenStorage tokenStorage, ClientProperties clientProperties) {
        this.tokenStorage = tokenStorage;
        this.clientProperties = clientProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            // Gera chave para busca do token
            String key = TokenStorage.generateKey(clientProperties.getClientId(), clientProperties.getRealm());
            
            // Busca token válido no storage
            Optional<TokenResponse> tokenOpt = tokenStorage.retrieveIfValid(key);
            
            if (tokenOpt.isPresent()) {
                TokenResponse token = tokenOpt.get();
                String headerValue = BEARER_PREFIX + token.getAccessToken();
                
                // Adiciona header Authorization se não existir ou estiver vazio
                if (!hasAuthorizationHeader(template)) {
                    template.header(AUTHORIZATION_HEADER, headerValue);
                    log.debug("Token injetado com sucesso para o cliente: {}", clientProperties.getClientId());
                } else {
                    log.debug("Header Authorization já presente, não sobrescrevendo");
                }
            } else {
                log.debug("Nenhum token válido encontrado para o cliente: {}", clientProperties.getClientId());
            }
            
        } catch (Exception e) {
            log.error("Erro ao injetar token na requisição", e);
            // Não propaga exceção para não quebrar a requisição
        }
    }

    /**
     * Verifica se já existe um header Authorization na requisição.
     * 
     * @param template Template da requisição Feign
     * @return true se header Authorization estiver presente
     */
    private boolean hasAuthorizationHeader(RequestTemplate template) {
        Collection<String> authHeaders = template.headers().get(AUTHORIZATION_HEADER);
        return authHeaders != null && !authHeaders.isEmpty() && StringUtils.hasText(authHeaders.iterator().next());
    }

    /**
     * Remove header Authorization existente (opcional).
     * 
     * @param template Template da requisição Feign
     */
    public void removeExistingAuthorization(RequestTemplate template) {
        template.removeHeader(AUTHORIZATION_HEADER);
        log.debug("Header Authorization removido");
    }

    /**
     * Força a injenção de um token específico, ignorando o storage.
     * Método útil para casos de override manual.
     * 
     * @param template Template da requisição Feign
     * @param token Token a ser injetado
     */
    public void forceInjectToken(RequestTemplate template, String token) {
        if (StringUtils.hasText(token)) {
            template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
            log.debug("Token forçado injetado com sucesso");
        }
    }
}