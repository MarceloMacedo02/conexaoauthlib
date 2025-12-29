package br.com.conexaoautolib.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.conexaoautolib.config.ConexaoAuthFeignConfig;
import br.com.conexaoautolib.exception.ConexaoAuthException;
import br.com.conexaoautolib.model.response.JwksResponse;
import br.com.conexaoautolib.model.response.TokenResponse;

/**
 * Cliente Feign para integração com endpoints OAuth2 do ConexãoAuth.
 * Fornece métodos para emissão de tokens e recuperação de chaves públicas.
 * 
 * @author ConexãoAuthLib
 * @version 1.0.0
 */
@FeignClient(name = "conexaoAuthOAuth2Client", url = "${conexaoauth.server.url}", configuration = ConexaoAuthFeignConfig.class)
public interface ConexaoAuthOAuth2Client {

    /**
     * Emite token OAuth2 usando as credenciais fornecidas.
     * 
     * @param formData Parâmetros do formulário OAuth2 em formato
     *                 application/x-www-form-urlencoded
     * @return TokenResponse com access token e informações relacionadas
     * @throws ConexaoAuthException em caso de erro na autenticação
     */
    @PostMapping(value = "/oauth2/token", headers = { "Content-Type: application/x-www-form-urlencoded" })
    TokenResponse emitirToken(@RequestBody Map<String, String> formData);

    /**
     * Recupera o conjunto de chaves públicas (JWKS) para verificação de JWT.
     * 
     * @return JwksResponse contendo as chaves públicas do realm
     * @throws ConexaoAuthException em caso de erro na recuperação das chaves
     */
    @GetMapping("/.well-known/jwks.json")
    JwksResponse obterJwks();

    /**
     * Recupera chaves públicas para um realm específico.
     * 
     * @param realm Nome do realm para filtrar as chaves
     * @return JwksResponse contendo as chaves do realm solicitado
     * @throws ConexaoAuthException em caso de erro na recuperação das chaves
     */
    @GetMapping("/.well-known/jwks.json")
    JwksResponse obterJwksPorRealm(@RequestParam("realm") String realm);
}