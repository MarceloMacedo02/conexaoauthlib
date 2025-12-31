package com.conexaoauthlib.dto.scope;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de um novo Scope.
 *
 * <p>Scopes seguem o padrão "resource:action" e definem permissões granulares
 * para acesso a recursos protegidos.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * ScopeCreateRequestDTO scope = ScopeCreateRequestDTO.builder()
 *     .name("users:read")
 *     .description("Permissão para leitura de dados de usuários")
 *     .resource("users")
 *     .action("read")
 *     .build();
 * }</pre>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeCreateRequestDTO {

    /**
     * Nome do scope (formato: recurso:ação).
     * Exemplo: "users:read", "users:write", "orders:delete"
     *
     * Deve seguir o padrão: letras minúsculas, números e underscore.
     */
    @NotBlank(message = "Nome do scope é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]*$",
             message = "Scope deve seguir o formato 'recurso:acao' (letras minúsculas e underscore)")
    private String name;

    /**
     * Descrição do scope e seu propósito.
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    /**
     * Recurso protegido pelo scope.
     * Exemplo: "users", "products", "orders"
     */
    @NotBlank(message = "Recurso é obrigatório")
    private String resource;

    /**
     * Ação permitida pelo scope.
     * Exemplo: "read", "write", "delete", "all"
     */
    @NotBlank(message = "Ação é obrigatória")
    private String action;
}
