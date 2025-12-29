package br.com.conexaoautolib.facade;

import br.com.conexaoautolib.client.ConexaoAuthUsuarioClient;
import br.com.conexaoautolib.model.response.Page;
import br.com.conexaoautolib.model.response.UsuarioResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * API fluente para operações de usuários do ConexãoAuth.
 * 
 * Fornece uma interface intuitiva com method chaining para configuração
 * e execução de consultas de usuários no servidor ConexãoAuth.
 * 
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class UsuarioClient {

    private final ConexaoAuthUsuarioClient usuarioClient;
    
    public UsuarioClient(ConexaoAuthUsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    /**
     * Cria um novo builder para consulta de usuários.
     * 
     * @param usuarioClient Client de usuários para comunicação
     * @return Nova instância de UsuarioBuilder
     */
    public static UsuarioBuilder filtrar(ConexaoAuthUsuarioClient usuarioClient) {
        return new UsuarioBuilder(usuarioClient);
    }

    /**
     * Builder para configuração e execução de consultas de usuários.
     * Implementa padrão Builder com method chaining para configuração
     * intuitiva dos filtros e parâmetros.
     */
    public static class UsuarioBuilder {

        private final ConexaoAuthUsuarioClient usuarioClient;

        // Filtros de busca
        private UUID id;
        private String username;
        private String email;
        private String realm;
        private String status;
        private String empresaId;
        private UUID realmId;
        private UUID roleId;
        private String cpfOrCnpj;
        private List<UUID> ids;

        // Parâmetros de paginação
        private int page = 0;
        private int size = 20;

        /**
         * Construtor privado que recebe o client de usuários.
         * 
         * @param usuarioClient Client para comunicação
         */
        private UsuarioBuilder(ConexaoAuthUsuarioClient usuarioClient) {
            this.usuarioClient = usuarioClient;
        }

        /**
         * Filtra por ID único do usuário.
         * 
         * @param id UUID do usuário
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder porId(UUID id) {
            this.id = id;
            return this;
        }

        /**
         * Filtra por username ou email.
         * 
         * @param username Username ou email do usuário
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder porUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Filtra por email específico.
         * 
         * @param email Email do usuário
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder porEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Filtra por nome do realm.
         * 
         * @param realm Nome do realm
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder noRealm(String realm) {
            this.realm = realm;
            return this;
        }

        /**
         * Filtra por status do usuário.
         * 
         * @param status Status do usuário (ATIVO, INATIVO, BLOQUEADO)
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder comStatus(String status) {
            this.status = status;
            return this;
        }

        /**
         * Filtra por ID da empresa.
         * 
         * @param empresaId ID da empresa
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder daEmpresa(String empresaId) {
            this.empresaId = empresaId;
            return this;
        }

        /**
         * Filtra por ID do realm.
         * 
         * @param realmId ID do realm
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder doRealm(UUID realmId) {
            this.realmId = realmId;
            return this;
        }

        /**
         * Filtra por ID de role.
         * 
         * @param roleId ID da role
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder comRole(UUID roleId) {
            this.roleId = roleId;
            return this;
        }

        /**
         * Filtra por CPF ou CNPJ.
         * 
         * @param cpfOrCnpj CPF ou CNPJ
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder porCpfOuCnpj(String cpfOrCnpj) {
            this.cpfOrCnpj = cpfOrCnpj;
            return this;
        }

        /**
         * Filtra por lista de IDs.
         * 
         * @param ids Lista de UUIDs
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder porIds(List<UUID> ids) {
            this.ids = ids;
            return this;
        }

        /**
         * Configura número da página (0-based).
         * 
         * @param page Número da página
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder naPagina(int page) {
            this.page = page;
            return this;
        }

        /**
         * Configura tamanho da página.
         * 
         * @param size Tamanho da página
         * @return Builder atualizado para method chaining
         */
        public UsuarioBuilder comTamanho(int size) {
            this.size = size;
            return this;
        }

        /**
         * Executa a busca e retorna o primeiro resultado encontrado.
         * 
         * @return UsuarioResponse com dados do usuário encontrado
         * @throws IllegalArgumentException se múltiplos critérios forem usados indevidamente
         */
        public UsuarioResponse execute() {
            // Prioridade de busca: ID > Username > Email > CPF/CNPJ > Batch
            if (id != null) {
                return usuarioClient.buscarPorId(id);
            }
            
            if (username != null) {
                return usuarioClient.buscarPorUsername(username);
            }
            
            if (email != null) {
                // Para busca por email, usamos o endpoint de verificação e depois busca por username
                if (usuarioClient.existePorEmail(email)) {
                    return usuarioClient.buscarPorUsername(email);
                }
            }
            
            if (cpfOrCnpj != null) {
                return usuarioClient.buscarPorCpfOrCnpj(cpfOrCnpj);
            }
            
            if (ids != null && !ids.isEmpty()) {
                // Para busca batch, retornamos o primeiro da lista
                return usuarioClient.buscarPorIds(ids).stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário encontrado na lista fornecida"));
            }
            
            throw new IllegalArgumentException("É necessário fornecer pelo menos um critério de busca");
        }

        /**
         * Executa a busca e retorna resultados paginados.
         * 
         * @return Page com usuários encontrados
         * @throws IllegalArgumentException se nenhum critério de listagem for fornecido
         */
        public Page<UsuarioResponse> listar() {
            // Listagem com filtros aplicados
            if (realm != null || status != null) {
                return usuarioClient.listarComFiltros(realm, status, page, size);
            }
            
            if (empresaId != null) {
                return usuarioClient.buscarPorEmpresa(empresaId, page, size);
            }
            
            if (realmId != null) {
                return usuarioClient.buscarPorRealm(realmId, page, size);
            }
            
            if (roleId != null) {
                return usuarioClient.buscarPorRole(roleId, page, size);
            }
            
            // Listagem geral sem filtros
            return usuarioClient.listar(page, size);
        }

        /**
         * Verifica se usuário existe por email.
         * 
         * @return true se usuário existir, false caso contrário
         */
        public boolean existe() {
            if (email == null) {
                throw new IllegalArgumentException("Email é obrigatório para verificação de existência");
            }
            return usuarioClient.existePorEmail(email);
        }

        /**
         * Valida parâmetros para busca individual.
         */
        private void validateSingleCriteria() {
            int criteriaCount = 0;
            
            if (id != null) criteriaCount++;
            if (username != null) criteriaCount++;
            if (email != null) criteriaCount++;
            if (cpfOrCnpj != null) criteriaCount++;
            if (ids != null) criteriaCount++;
            
            if (criteriaCount > 1) {
                throw new IllegalArgumentException("Apenas um critério de busca individual pode ser usado por vez");
            }
        }

        /**
         * Valida parâmetros para listagem.
         */
        private void validateListCriteria() {
            int criteriaCount = 0;
            
            if (realm != null) criteriaCount++;
            if (status != null) criteriaCount++;
            if (empresaId != null) criteriaCount++;
            if (realmId != null) criteriaCount++;
            if (roleId != null) criteriaCount++;
            
            if (criteriaCount > 1) {
                throw new IllegalArgumentException("Apenas um tipo de filtro de listagem pode ser usado por vez");
            }
        }
    }
}