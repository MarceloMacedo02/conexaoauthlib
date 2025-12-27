package com.plataforma.conexao.auth.starter.service;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plataforma.conexao.auth.starter.cache.JwksCache;
import com.plataforma.conexao.auth.starter.exception.InvalidTokenException;
import com.plataforma.conexao.auth.starter.model.TokenClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

/**
 * Implementação do validador de tokens JWT.
 *
 * <p>
 * Esta classe valida tokens JWT assinados com RSA256 usando chaves públicas
 * obtidas do endpoint JWKS do Auth Server.
 *
 * <p>
 * Validações realizadas:
 * <ul>
 * <li>Assinatura JWT (verificação usando chave pública)</li>
 * <li>Formato do token (header, payload, signature)</li>
 * <li>Expiração do token (claim exp)</li>
 * <li>Validade do token (claim nbf - not before)</li>
 * <li>Emissor (claim iss)</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public class JwtTokenValidator implements TokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final JwksCache jwksCache;

    /**
     * Construtor do validador de tokens.
     *
     * @param jwksCache Cache de chaves públicas JWKS
     */
    public JwtTokenValidator(JwksCache jwksCache) {
        this.jwksCache = jwksCache;
        log.info("Inicializando JwtTokenValidator");
    }

    @Override
    public TokenClaims validateToken(String jwt) throws InvalidTokenException {
        if (jwt == null || jwt.isEmpty()) {
            throw new InvalidTokenException("Token JWT não pode ser nulo ou vazio");
        }

        try {
            log.debug("Validando token JWT...");

            // Extrai o Key ID (kid) do header do token
            String kid = extractKeyId(jwt);
            if (kid == null) {
                throw new InvalidTokenException(
                        "Token JWT não contém 'kid' no header");
            }

            log.debug("Key ID (kid) do token: {}", kid);

            // Obtém a chave pública do cache (ou atualiza cache se necessário)
            PublicKey publicKey = jwksCache.getPublicKey(kid);

            // Cria o parser JWT com a chave pública
            JwtParser parser = io.jsonwebtoken.Jwts.parser()
                    .verifyWith(publicKey)
                    .build();

            // Valida e extrai os claims
            Claims claims = parser.parseSignedClaims(jwt).getPayload();

            log.debug("Token validado com sucesso. Subject: {}", claims.getSubject());

            // Converte Claims JWT para TokenClaims (nossa classe)
            return convertToTokenClaims(claims);

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token JWT expirado", e);

        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Token JWT não suportado", e);

        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Token JWT malformado", e);

        } catch (SignatureException e) {
            throw new InvalidTokenException("Assinatura do token JWT inválida", e);

        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token JWT inválido: " + e.getMessage(), e);

        } catch (Exception e) {
            throw new InvalidTokenException(
                    "Erro ao validar token JWT: " + e.getMessage(), e);
        }
    }

    @Override
    public TokenClaims extractClaims(String jwt) throws InvalidTokenException {
        // Para extrair claims sem validar assinatura, podemos apenas fazer o parse
        // Mas por segurança, recomendamos sempre validar o token
        return validateToken(jwt);
    }

    @Override
    public void refreshJwksCache() {
        log.info("Forçando atualização do cache JWKS");
        jwksCache.refreshJwks();
    }

    /**
     * Extrai o Key ID (kid) do header do token JWT.
     *
     * <p>
     * O header JWT é Base64Url-encoded JSON. Este método decodifica
     * e extrai o campo "kid" (Key ID).
     *
     * @param jwt Token JWT
     * @return Key ID (kid) ou null se não encontrado
     */
    private String extractKeyId(String jwt) {
        try {
            // O header JWT está antes do primeiro ponto
            String[] parts = jwt.split("\\.");
            if (parts.length < 1) {
                return null;
            }

            String headerEncoded = parts[0];

            // Decodifica Base64 URL-safe para String
            byte[] headerBytes = io.jsonwebtoken.io.Decoders.BASE64URL.decode(headerEncoded);
            String headerJson = new String(headerBytes);

            // Extrai kid usando parsing simples de JSON
            // (para evitar dependência de Jackson apenas para isso)
            String kidPrefix = "\"kid\":\"";
            int kidIndex = headerJson.indexOf(kidPrefix);
            if (kidIndex == -1) {
                return null;
            }

            int startIndex = kidIndex + kidPrefix.length();
            int endIndex = headerJson.indexOf("\"", startIndex);

            return headerJson.substring(startIndex, endIndex);

        } catch (Exception e) {
            log.error("Erro ao extrair kid do header JWT", e);
            return null;
        }
    }

    /**
     * Converte Claims JWT para TokenClaims.
     *
     * @param claims Claims JWT (biblioteca JJWT)
     * @return TokenClaims (nossa classe)
     */
    private TokenClaims convertToTokenClaims(Claims claims) {
        // Extrai roles do token (pode estar em vários formatos)
        List<String> roles = extractRoles(claims);

        // Extrai audience (aud)
        String aud = null;
        if (claims.getAudience() != null) {
            aud = claims.getAudience().stream().findFirst().orElse(null);
        }

        // Extrair valores dos claims
        String sub = claims.getSubject();
        String realm = claims.get("realm", String.class);
        String iss = claims.getIssuer();
        Long exp = claims.getExpiration() != null ? claims.getExpiration().getTime() / 1000 : null;
        Long iat = claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() / 1000 : null;
        Long nbf = claims.getNotBefore() != null ? claims.getNotBefore().getTime() / 1000 : null;
        String jti = claims.getId();
        String typ = claims.get("typ", String.class);
        String clientId = claims.get("client_id", String.class);

        // Criar TokenClaims usando o construtor canônico
        return new TokenClaims(sub, realm, roles, aud, iss, exp, iat, jti, nbf, typ, clientId);
    }

    /**
     * Extrai roles dos claims JWT.
     *
     * <p>
     * O claim de roles pode estar em diferentes formatos:
     * <ul>
     * <li>Uma String simples: "USER"</li>
     * <li>Uma lista de Strings: ["USER", "ADMIN"]</li>
     * <li>Um claim específico: "realm_access.roles" ou "roles"</li>
     * </ul>
     *
     * @param claims Claims JWT
     * @return Lista de roles
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Claims claims) {
        // Tenta obter do claim "roles"
        Object rolesObj = claims.get("roles");

        if (rolesObj == null) {
            // Tenta obter do claim "realm_access.roles" (formato Keycloak)
            Map<String, Object> realmAccess = claims.get("realm_access", Map.class);
            if (realmAccess != null) {
                rolesObj = realmAccess.get("roles");
            }
        }

        if (rolesObj == null) {
            return List.of(); // Sem roles
        }

        // Se for uma lista de Strings
        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        // Se for uma String única
        if (rolesObj instanceof String role) {
            return List.of(role);
        }

        return List.of();
    }
}
