package com.plataforma.conexao.auth.starter.model;

import java.time.Instant;

import java.util.List;

/**
 * DTO interno para claims JWT extraídos.
 *
 * <p>Este record representa os claims (declarações) extraídos de um token JWT,
 * incluindo informações do usuário, autoridade (iss), audiência (aud), tempo de expiração
 * e permissões (roles).
 *
 * <p>Conforme especificação RFC 7519 (JWT).
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 */
public record TokenClaims (

    /**
     * Subject (sub) - identificador do usuário ou entidade.
     * <p>Normalmente contém o ID do usuário ou email.</p>
     */
    String sub,

    /**
     * Realm - identificador do realm do usuário.
     * <p>Normalmente contém o ID do realm (ex: master).</p>
     */
    String realm,

    /**
     * Roles/Permissões - lista de roles atribuídas ao usuário.
     * <p>Exemplo: ["USER", "ADMIN"].</p>
     */
    List<String> roles,

    /**
     * Audience (aud) - destinatário pretendido do token.
     * <p>Exemplo: "https://auth.example.com".</p>
     */
    String aud,

    /**
     * Issuer (iss) - emissor do token (URL do Auth Server).
     * <p>Exemplo: "https://auth.example.com".</p>
     */
    String iss,

    /**
     * Expiration Time (exp) - timestamp Unix quando o token expira.
     * <p>Medido em segundos desde 1ª de janeiro de 1970.</p>
     */
    Long exp,

    /**
     * Issued At (iat) - timestamp Unix quando o token foi emitido.
     * <p>Medido em segundos desde 1ª de janeiro de 1970.</p>
     */
    Long iat,

    /**
     * JWT ID (jti) - identificador único do token (opcional).
     * <p>Exemplo: "jti-123".</p>
     */
    String jti,

    /**
     * Not Before (nbf) - timestamp Unix quando o token passa a ser válido (opcional).
     * <p>Se não claim nbf, consideramos válido imediatamente válido.</p>
     */
    Long nbf,

    /**
     * Token Type (typ) - tipo do token (opcional).
     * <p>Exemplo: "Bearer".</p>
     */
    String typ,

    /**
     * Client ID (client_id) - identificador do cliente que solicitou o token.
     * <p>Exemplo: "client-123".</p>
     */
    String clientId
) {
    /**
     * Verifica se o token está expirado.
     *
     * @return true se o token estiver expirado, false caso contrário.
     *
     * <p>Compara o tempo atual (em segundos Unix) com o claim exp.</p>
     */
    public boolean isExpired() {
        long currentTimeSeconds = Instant.now().getEpochSecond();
        return currentTimeSeconds >= exp;
    }

    /**
     * Verifica se o token ainda não é válido (nbf - not before).
     *
     * @return true se o token ainda não for válido, false caso contrário.
     *
     * <p>Se nbf não for null, consideramos válido imediatamente válido.</p>
     */
    public boolean isNotYetValid() {
        if (nbf == null) {
            return false; // Sem claim nbf, consideramos válido
        }
        long currentTimeSeconds = Instant.now().getEpochSecond();
        return currentTimeSeconds < nbf;
    }

    /**
     * Verifica se o token está válido (não expirado e já é válido).
     *
     * @return true se o token estiver válido, false caso contrário.
     *
     * <p>Verifica expiração e nbf.</p>
     */
    public boolean isValid() {
        return !isExpired() && !isNotYetValid();
    }

    /**
     * Verifica se o usuário possui uma role específica.
     *
     * @param role Role a verificar
     * @return true se o usuário possui a role, false caso contrário.
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Verifica se o usuário possui todas as roles especificadas.
     *
     * @param requiredRoles Lista de roles obrigatórias.
     * @return true se o usuário possui todas as roles, false caso contrário.
     */
    public boolean hasAllRoles(List<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        if (roles == null) {
            return false;
        }
        return roles.containsAll(requiredRoles);
    }

    /**
     * Verifica se o usuário possui pelo menos uma das roles especificadas.
     *
     * @param roles Lista de roles (qualquer uma deve estar presente)
     * @return true se o usuário possui pelo menos uma role, false caso contrário.
     */
    public boolean hasAnyRole(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        if (this.roles == null) {
            return false;
        }
        return this.roles.stream().anyMatch(roles::contains);
    }

    /**
     * Obtém o tempo restante de validade do token em segundos.
     *
     * @return Tempo restante em segundos (negativo se expirado).
     *
     * <p>Compara o tempo atual (em segundos Unix) com o claim exp (expiração).</p>
     */
    public long getTimeUntilExpiration() {
        long currentTimeSeconds = Instant.now().getEpochSecond();
        return exp - currentTimeSeconds;
    }
}
