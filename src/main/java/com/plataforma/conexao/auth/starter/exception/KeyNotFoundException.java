package com.plataforma.conexao.auth.starter.exception;

/**
 * Exceção lançada quando uma chave pública não é encontrada no JWKS.
 *
 * <p>Esta exceção é utilizada quando o token JWT contém um {@code kid}
 * (Key ID) que não corresponde a nenhuma chave pública no JWKS. Isso pode
 * indicar:
 * <ul>
 *   <li>Chave foi rotacionada no Auth Server, mas o cache não foi atualizado</li>
 *   <li>Token foi assinado com uma chave antiga</li>
 *   <li>Token foi modificado ou corrompido</li>
 *   <li>Inconsistência entre Auth Server e SDK</li>
 * </ul>
 *
 * <p>Esta exceção resulta em resposta HTTP 401 (Unauthorized), pois indica
 * que o token não pode ser validado com as chaves disponíveis.
 *
 * <p><strong>Impacto:</strong> Sem a chave pública correspondente, o SDK
 * não pode validar a assinatura do token. O cliente pode tentar:
 * <ul>
 *   <li>Forçar refresh do cache JWKS</li>
 *   <li>Obter um novo token do Auth Server</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class KeyNotFoundException extends IntegrationException {

    /**
     * Key ID (kid) não encontrado no JWKS.
     */
    private final String keyId;

    /**
     * Construtor com Key ID.
     *
     * @param keyId Key ID (kid) não encontrado no JWKS
     */
    public KeyNotFoundException(String keyId) {
        super(String.format("Chave pública não encontrada para kid: %s", keyId));
        this.keyId = keyId;
    }

    /**
     * Construtor com Key ID e causa raiz.
     *
     * @param keyId Key ID (kid) não encontrado no JWKS
     * @param cause Causa raiz da exceção
     */
    public KeyNotFoundException(String keyId, Throwable cause) {
        super(String.format("Chave pública não encontrada para kid: %s", keyId), cause);
        this.keyId = keyId;
    }

    /**
     * Obtém o Key ID (kid) não encontrado.
     *
     * @return Key ID (kid), ou null se não foi especificado
     */
    public String getKeyId() {
        return keyId;
    }
}
