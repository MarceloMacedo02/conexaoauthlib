package com.plataforma.conexao.auth.starter.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO de resposta de erro padronizado para APIs REST.
 *
 * <p>Esta classe representa a estrutura de erro retornada pela API em caso de exceções.
 * Segue as melhores práticas de RESTful APIs e fornece informações detalhadas
 * sobre o erro ocorrido, facilitando o debug e tratamento pelo cliente.
 *
 * <p>A estrutura inclui:
 * <ul>
 *   <li>Timestamp da ocorrência do erro</li>
 *   <li>Status HTTP</li>
 *   <li>Tipo/classificação do erro</li>
 *   <li>Mensagem descritiva</li>
 *   <li>Path da requisição que causou o erro</li>
 *   <li>Detalhes adicionais (opcional)</li>
 * </ul>
 *
 * @author Conexão Auth Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp da ocorrência do erro em UTC (ISO 8601).
     *
     * <p>Representa o momento exato em que o erro ocorreu, útil para
     * correlação de logs e debugging.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    /**
     * Status HTTP code da resposta de erro.
     *
     * <p>Códigos comuns:
     * <ul>
     *   <li>400 - Bad Request (erro de validação, dados inválidos)</li>
     *   <li>401 - Unauthorized (credenciais inválidas, token inválido)</li>
     *   <li>403 - Forbidden (permissões insuficientes)</li>
     *   <li>404 - Not Found (recurso não encontrado)</li>
     *   <li>409 - Conflict (recurso já existe)</li>
     *   <li>422 - Unprocessable Entity (erro de negócio)</li>
     *   <li>500 - Internal Server Error (erro inesperado)</li>
     * </ul>
     */
    private int status;

    /**
     * Tipo/classificação do erro.
     *
     * <p>Um identificador curto e descritivo do tipo de erro que ocorreu.
     * Útil para tratamento programático no cliente.
     *
     * <p>Exemplos:
     * <ul>
     *   <li>{@code NOT_FOUND} - Recurso não encontrado</li>
     *   <li>{@code VALIDATION_ERROR} - Erro de validação de dados</li>
     *   <li>{@code INVALID_TOKEN} - Token JWT inválido ou expirado</li>
     *   <li>{@code FORBIDDEN} - Acesso não autorizado</li>
     *   <li>{@code INTERNAL_ERROR} - Erro interno do servidor</li>
     * </ul>
     */
    private String error;

    /**
     * Mensagem descritiva detalhada do erro em Português.
     *
     * <p>Fornece contexto humano-legível sobre o erro, útil para
     * exibição em interfaces de usuário ou logs de debugging.
     */
    private String message;

    /**
     * URI da requisição que causou o erro.
     *
     * <p>Identifica qual endpoint foi solicitado quando o erro ocorreu,
     * útil para traceabilidade e debugging.
     */
    private String path;

    /**
     * Mapa de detalhes adicionais sobre o erro (opcional).
     *
     * <p>Contém informações complementares que podem variar de acordo
     * com o tipo de erro específico. Por exemplo:
     * <ul>
     *   <li>Em erros de validação: lista de campos inválidos</li>
     *   <li>Em erros de negócio: códigos de erro específicos</li>
     *   <li>Em erros de integração: detalhes sobre o serviço externo</li>
     * </ul>
     */
    private Map<String, Object> details;
}
