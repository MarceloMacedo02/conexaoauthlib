package com.conexaoauthlib.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para regeneração de segredo do cliente.
 *
 * <p>Se newSecret for null, o servidor gerará automaticamente um segredo seguro.
 * Se fornecido, o segredo deve seguir as políticas de complexidade do servidor.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ClientSecretRequestDTO request = ClientSecretRequestDTO.builder()
 *     .newSecret("meu-novo-segredo-seguro-123")
 *     .build();
 * ClientSecretResponseDTO response = clientClient.regenerateSecret("client-id", request);
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSecretRequestDTO {

    /**
     * Novo segredo do cliente.
     * Se null, o servidor gerará automaticamente.
     * Se fornecido, deve ter entre 16 e 200 caracteres.
     */
    @Size(min = 16, max = 200, message = "Client secret deve ter entre 16 e 200 caracteres")
    @JsonProperty("new_secret")
    private String newSecret;
}
