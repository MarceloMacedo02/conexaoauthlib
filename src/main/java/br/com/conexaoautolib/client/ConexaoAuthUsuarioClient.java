package br.com.conexaoautolib.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.conexaoautolib.config.ConexaoAuthFeignConfig;
import br.com.conexaoautolib.model.response.UsuarioResponse;
import br.com.conexaoautolib.model.response.Page;

import java.util.List;
import java.util.UUID;

/**
 * Cliente Feign para integração com endpoints de usuários do ConexãoAuth.
 * Fornece métodos para consulta, busca e listagem de usuários.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@FeignClient(name = "conexaoAuthUsuarioClient", url = "${conexaoauth.server.url}", configuration = ConexaoAuthFeignConfig.class)
public interface ConexaoAuthUsuarioClient {

    /**
     * Busca usuário por ID único.
     * 
     * @param id UUID do usuário a ser consultado
     * @return UsuarioResponse com dados completos do usuário
     */
    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioResponse buscarPorId(@PathVariable("id") UUID id);

    /**
     * Busca usuário por username (email ou nome de usuário).
     * 
     * @param username Username ou email do usuário
     * @return UsuarioResponse com dados completos do usuário
     */
    @GetMapping("/api/v1/usuarios/username/{username}")
    UsuarioResponse buscarPorUsername(@PathVariable("username") String username);

    /**
     * Lista usuários com suporte a paginação e filtros.
     * 
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @return Page com usuários paginados
     */
    @GetMapping("/api/v1/usuarios")
    Page<UsuarioResponse> listar(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Lista usuários com filtros avançados.
     * 
     * @param realm Filtro por realm específico
     * @param status Filtro por status do usuário (ATIVO, INATIVO, BLOQUEADO)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @return Page com usuários filtrados e paginados
     */
    @GetMapping("/api/v1/usuarios")
    Page<UsuarioResponse> listarComFiltros(
            @RequestParam(value = "realm", required = false) String realm,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Busca usuários por empresa.
     * 
     * @param empresaId ID da empresa para filtro
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @return Page com usuários da empresa
     */
    @GetMapping("/api/v1/usuarios/empresa/{empresaId}")
    Page<UsuarioResponse> buscarPorEmpresa(
            @PathVariable("empresaId") String empresaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Busca usuários por realm específico.
     * 
     * @param realmId ID do realm para filtro
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @return Page com usuários do realm
     */
    @GetMapping("/api/v1/usuarios/realm/{realmId}")
    Page<UsuarioResponse> buscarPorRealm(
            @PathVariable("realmId") UUID realmId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Busca usuários por role específica.
     * 
     * @param roleId ID da role para filtro
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @return Page com usuários que possuem a role
     */
    @GetMapping("/api/v1/usuarios/role/{roleId}")
    Page<UsuarioResponse> buscarPorRole(
            @PathVariable("roleId") UUID roleId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Busca múltiplos usuários por lista de IDs.
     * 
     * @param ids Lista de UUIDs dos usuários
     * @return Lista de UsuarioResponse correspondentes
     */
    @GetMapping("/api/v1/usuarios/batch")
    List<UsuarioResponse> buscarPorIds(@RequestParam("ids") List<UUID> ids);

    /**
     * Verifica se usuário existe por email.
     * 
     * @param email Email a ser verificado
     * @return true se usuário existir, false caso contrário
     */
    @GetMapping("/api/v1/usuarios/existe-email")
    boolean existePorEmail(@RequestParam("email") String email);

    /**
     * Busca usuários por CPF ou CNPJ.
     * 
     * @param cpfOrCnpj CPF ou CNPJ para busca
     * @return UsuarioResponse do usuário encontrado
     */
    @GetMapping("/api/v1/usuarios/cpf-cnpj/{cpfOrCnpj}")
    UsuarioResponse buscarPorCpfOrCnpj(@PathVariable("cpfOrCnpj") String cpfOrCnpj);
}