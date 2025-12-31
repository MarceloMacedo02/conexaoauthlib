package com.conexaoauthlib.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta após rotação de segredo do cliente.
 * Contém o novo segredo que deve ser armazenado com segurança.
 *
 * <p>Importante: O novo segredo é retornado uma única vez e deve ser
 * armazenado imediatamente de forma segura.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientSecretResponseDTO secretResponse = clientClient.rotateSecret("client-id");
 * String newSecret = secretResponse.getNewSecret();
 * String expiresAt = secretResponse.getExpiresAt();
 * // Armazenar o novo segredo imediatamente!
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientSecretResponseDTO {

    /**
     * Identificador do cliente.
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * Novo segredo do cliente.
     * DEVE ser armazenado de forma segura imediatamente.
     */
    @JsonProperty("new_secret")
    private String newSecret;

    /**
     * Data e hora de expiração do segredo anterior.
     * Após este momento, o segredo antigo não funcionará mais.
     */
    @JsonProperty("expires_at")
    private String expiresAt;

    /**
     * Indica se é a primeira rotação de segredo.
     */
    @JsonProperty("first_rotation")
    private Boolean firstRotation;

    /**
     * Tipo de segredo (plain, hashed, etc.).
     */
    @JsonProperty("secret_type")
    private String secretType;
}
