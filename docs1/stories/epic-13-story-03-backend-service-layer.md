# User Story: Backend Service Layer

**Epic:** 13 - Página de Gestão de Usuários (Thymeleaf)
**Story ID:** epic-13-story-03

## Descrição
Criar a camada de serviço `UsuarioService` com métodos para operações CRUD de usuários na página administrativa, incluindo validações de unicidade de email, associação de roles, bloqueio/reativamento e reset administrativo de senha.

## Critérios de Aceite
- [ ] Classe `UsuarioService` criada com todos os métodos necessários
- [ ] Método `listarUsuarios()` implementado com paginação e filtros
- [ ] Método `buscarPorId()` implementado
- [ ] Método `criarUsuario()` implementado com validações
- [ ] Método `atualizarUsuario()` implementado com validações
- [ ] Método `ativarUsuario()` implementado
- [ ] Método `bloquearUsuario()` implementado com motivo
- [ ] Método `resetSenhaAdministrativo()` implementado com geração de código
- [ ] Método `listarRolesDisponiveis()` implementado
- [ ] Método `associarRoles()` implementado
- [ ] Método `validarEmailUnico()` implementado
- [ ] Transações gerenciadas corretamente (@Transactional)
- [ ] Auditoria de eventos registrada

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.domain.service`
2. Criar classe `UsuarioService.java`
3. Implementar método de listagem com paginação
4. Implementar método de busca por ID
5. Implementar método de criação com validações
6. Implementar método de atualização com validações
7. Implementar método de ativação de usuário
8. Implementar método de bloqueio de usuário
9. Implementar método de reset administrativo de senha
10. Implementar método de listagem de roles por realm
11. Implementar método de associação de roles
12. Adicionar validação de unicidade de email
13. Integrar com serviço de auditoria

## Instruções de Implementação

### Service Class
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/domain/service/UsuarioService.java`

```java
package br.com.plataforma.conexaodigital.admin.domain.service;

import br.com.plataforma.conexaodigital.admin.api.mapper.UsuarioMapper;
import br.com.plataforma.conexaodigital.admin.api.requests.UsuarioForm;
import br.com.plataforma.conexaodigital.admin.api.responses.UsuarioDetailResponse;
import br.com.plataforma.conexaodigital.admin.api.responses.UsuarioListResponse;
import br.com.plataforma.conexaodigital.gestaorole.domain.model.Role;
import br.com.plataforma.conexaodigital.gestaorole.domain.repository.RoleRepository;
import br.com.plataforma.conexaodigital.gestausuario.domain.model.Usuario;
import br.com.plataforma.conexaodigital.gestausuario.domain.repository.UsuarioRepository;
import br.com.plataforma.conexaodigital.auditoria.domain.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para operações administrativas de usuários.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        RoleRepository roleRepository,
        AuditoriaService auditoriaService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Lista usuários com paginação e filtros.
     *
     * @param pageable configuração de paginação
     * @param nome filtro por nome (opcional)
     * @param status filtro por status (opcional: ATIVO, BLOQUEADO)
     * @param realmId filtro por realm (opcional)
     * @return página de usuários
     */
    @Transactional(readOnly = true)
    public Page<UsuarioListResponse> listarUsuarios(
        Pageable pageable,
        String nome,
        String status,
        String realmId
    ) {
        // Converter string de status para enum
        br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario statusEnum = null;
        if ("Ativo".equalsIgnoreCase(status)) {
            statusEnum = br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO;
        } else if ("Bloqueado".equalsIgnoreCase(status)) {
            statusEnum = br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.BLOQUEADO;
        }

        // Buscar usuários com filtros (método deve existir no repository)
        Page<Usuario> usuarios = usuarioRepository.buscarComFiltros(
            pageable, nome, statusEnum, realmId
        );

        // Mapear para DTO
        return usuarios.map(usuario -> {
            // Buscar roles do usuário
            List<String> roleNames = usuario.getRoles().stream()
                .map(Role::getNome)
                .collect(Collectors.toList());

            // Buscar nome do realm (assumindo que usuário tem realm)
            String realmNome = usuario.getRealm() != null ? usuario.getRealm().getNome() : "N/A";
            String realmIdStr = usuario.getRealm() != null ? usuario.getRealm().getId().toString() : null;

            return UsuarioListResponse.from(usuario, realmNome, realmIdStr, roleNames);
        });
    }

    /**
     * Busca usuário por ID com detalhes completos.
     *
     * @param id ID do usuário
     * @return detalhes do usuário
     * @throws IllegalArgumentException se usuário não for encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioDetailResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Buscar roles do usuário
        List<String> roleNames = usuario.getRoles().stream()
            .map(Role::getNome)
            .collect(Collectors.toList());

        // Buscar nome do realm
        String realmNome = usuario.getRealm() != null ? usuario.getRealm().getNome() : "N/A";
        String realmIdStr = usuario.getRealm() != null ? usuario.getRealm().getId().toString() : null;

        return UsuarioDetailResponse.from(usuario, realmNome, realmIdStr, roleNames);
    }

    /**
     * Cria um novo usuário.
     *
     * @param form formulário de criação
     * @param usuarioIdCriador ID do usuário que está criando
     * @return ID do usuário criado
     */
    @Transactional
    public String criarUsuario(UsuarioForm form, UUID usuarioIdCriador) {
        // Validar email único
        validarEmailUnico(null, form.email());

        // Converter form para entidade
        Usuario usuario = UsuarioMapper.toEntity(form);

        // Buscar roles selecionadas
        List<Role> roles = buscarRolesPorIds(form.rolesIds());
        usuario.setRoles(roles);

        // Salvar usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdCriador,
            "USUARIO_CRIADO",
            "Usuário criado: " + usuarioSalvo.getEmail()
        );

        return usuarioSalvo.getId().toString();
    }

    /**
     * Atualiza um usuário existente.
     *
     * @param id ID do usuário
     * @param form formulário de edição
     * @param usuarioIdEditor ID do usuário que está editando
     */
    @Transactional
    public void atualizarUsuario(UUID id, UsuarioForm form, UUID usuarioIdEditor) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Validar email único (excluindo o próprio usuário)
        validarEmailUnico(id, form.email());

        // Atualizar campos
        usuario.setNome(form.nome());
        usuario.setEmail(form.email());
        usuario.setCpf(form.cpf());
        usuario.setStatus(form.ativo() ?
            br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO :
            br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.BLOQUEADO
        );
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        // Atualizar roles
        List<Role> roles = buscarRolesPorIds(form.rolesIds());
        usuario.setRoles(roles);

        // Salvar usuário
        usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdEditor,
            "USUARIO_EDITADO",
            "Usuário editado: " + usuario.getEmail()
        );
    }

    /**
     * Ativa um usuário bloqueado.
     *
     * @param id ID do usuário
     * @param usuarioIdOperador ID do usuário que está ativando
     */
    @Transactional
    public void ativarUsuario(UUID id, UUID usuarioIdOperador) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (usuario.getStatus() == br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO) {
            throw new IllegalArgumentException("Usuário já está ativo");
        }

        // Ativar usuário
        usuario.setStatus(br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.ATIVO);
        usuario.setDataReativacao(java.time.LocalDateTime.now());
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());
        usuario.setMotivoBloqueio(null);

        usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdOperador,
            "USUARIO_ATIVADO",
            "Usuário ativado: " + usuario.getEmail()
        );
    }

    /**
     * Bloqueia um usuário ativo.
     *
     * @param id ID do usuário
     * @param motivo motivo do bloqueio
     * @param usuarioIdOperador ID do usuário que está bloqueando
     */
    @Transactional
    public void bloquearUsuario(UUID id, String motivo, UUID usuarioIdOperador) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (usuario.getStatus() == br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.BLOQUEADO) {
            throw new IllegalArgumentException("Usuário já está bloqueado");
        }

        // Verificar se não é usuário admin do Realm Master (regra de negócio)
        if (usuario.isAdminMaster()) {
            throw new IllegalArgumentException("Usuário administrador do Realm Master não pode ser bloqueado");
        }

        // Bloquear usuário
        usuario.setStatus(br.com.plataforma.conexaodigital.gestausuario.domain.model.StatusUsuario.BLOQUEADO);
        usuario.setDataBloqueio(java.time.LocalDateTime.now());
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());
        usuario.setMotivoBloqueio(motivo);

        usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdOperador,
            "USUARIO_BLOQUEADO",
            "Usuário bloqueado: " + usuario.getEmail() + " - Motivo: " + motivo
        );
    }

    /**
     * Realiza reset administrativo de senha com geração de código de 6 dígitos.
     *
     * @param id ID do usuário
     * @param usuarioIdOperador ID do usuário que está fazendo o reset
     * @return código de reset gerado
     */
    @Transactional
    public String resetSenhaAdministrativo(UUID id, UUID usuarioIdOperador) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Gerar código numérico de 6 dígitos
        String codigoReset = gerarCodigoReset();

        // Armazenar hash do código (implementação depende de Epic 2)
        usuario.setCodigoResetSenha(codigoReset);
        usuario.setCodigoResetSenhaExpiracao(
            java.time.LocalDateTime.now().plusMinutes(15) // Expira em 15 minutos
        );
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdOperador,
            "SENHA_RESET_ADMINISTRATIVO",
            "Reset administrativo de senha solicitado: " + usuario.getEmail()
        );

        // TODO: Enviar email com código (Story 07 - Reset Administrativo de Senha)

        return codigoReset;
    }

    /**
     * Lista roles disponíveis para um realm.
     *
     * @param realmId ID do realm
     * @return lista de roles
     */
    @Transactional(readOnly = true)
    public List<UsuarioService.RoleDTO> listarRolesDisponiveis(UUID realmId) {
        List<Role> roles = roleRepository.findByRealmId(realmId);

        return roles.stream()
            .map(role -> new RoleDTO(
                role.getId().toString(),
                role.getNome(),
                role.getDescricao()
            ))
            .collect(Collectors.toList());
    }

    /**
     * Associa roles a um usuário.
     *
     * @param usuarioId ID do usuário
     * @param rolesIds lista de IDs de roles
     * @param usuarioIdOperador ID do usuário que está associando
     */
    @Transactional
    public void associarRoles(UUID usuarioId, List<String> rolesIds, UUID usuarioIdOperador) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Buscar roles
        List<Role> roles = buscarRolesPorIds(rolesIds);

        // Associar roles
        usuario.setRoles(roles);
        usuario.setDataUltimaAtualizacao(java.time.LocalDateTime.now());

        usuarioRepository.save(usuario);

        // Registrar evento de auditoria
        auditoriaService.registrarEvento(
            usuarioIdOperador,
            "USUARIO_ROLES_ATUALIZADOS",
            "Roles do usuário atualizadas: " + usuario.getEmail()
        );
    }

    /**
     * Valida se email é único no sistema.
     *
     * @param usuarioId ID do usuário (para edição, excluir ele da verificação)
     * @param email email a validar
     * @throws IllegalArgumentException se email já existe
     */
    @Transactional(readOnly = true)
    public void validarEmailUnico(UUID usuarioId, String email) {
        boolean emailExiste = usuarioRepository.existsByEmailAndIdNot(email, usuarioId);
        if (emailExiste) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }
    }

    /**
     * Busca roles por lista de IDs.
     */
    private List<Role> buscarRolesPorIds(List<String> rolesIds) {
        if (rolesIds == null || rolesIds.isEmpty()) {
            return List.of();
        }

        return rolesIds.stream()
            .map(id -> roleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada: " + id)))
            .collect(Collectors.toList());
    }

    /**
     * Gera código numérico de 6 dígitos para reset de senha.
     */
    private String gerarCodigoReset() {
        java.util.Random random = new java.util.Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * DTO simplificado para listagem de roles.
     */
    public record RoleDTO(String id, String nome, String descricao) {}
}
```

### Repository Methods (caso não existam)
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/gestausuario/domain/repository/UsuarioRepository.java`

Adicionar método ao repository:

```java
/**
 * Busca usuários com filtros opcionais.
 */
@Query("SELECT u FROM Usuario u " +
       "WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
       "AND (:status IS NULL OR u.status = :status) " +
       "AND (:realmId IS NULL OR u.realm.id = :realmId)")
Page<Usuario> buscarComFiltros(
    Pageable pageable,
    @Param("nome") String nome,
    @Param("status") StatusUsuario status,
    @Param("realmId") String realmId
);

/**
 * Verifica se email existe para outro usuário.
 */
@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
       "FROM Usuario u WHERE u.email = :email AND (:id IS NULL OR u.id != :id)")
boolean existsByEmailAndIdNot(
    @Param("email") String email,
    @Param("id") UUID id
);
```

## Checklist de Validação
- [ ] Classe `UsuarioService` criada
- [ ] Método `listarUsuarios()` implementado com paginação
- [ ] Método `buscarPorId()` implementado
- [ ] Método `criarUsuario()` implementado com validações
- [ ] Método `atualizarUsuario()` implementado com validações
- [ ] Método `ativarUsuario()` implementado
- [ ] Método `bloquearUsuario()` implementado com motivo
- [ ] Método `resetSenhaAdministrativo()` implementado com geração de código
- [ ] Método `listarRolesDisponiveis()` implementado
- [ ] Método `associarRoles()` implementado
- [ ] Método `validarEmailUnico()` implementado
- [ ] Método `buscarRolesPorIds()` auxiliar implementado
- [ ] Método `gerarCodigoReset()` auxiliar implementado
- [ ] Anotação `@Transactional` aplicada nos métodos de escrita
- [ ] Anotação `@Transactional(readOnly = true)` nos métodos de leitura
- [ ] Auditoria de eventos registrada em todas as operações
- [ ] Repositório com métodos necessários atualizado
- [ ] DTO interno `RoleDTO` criado

## Anotações
- Todos os métodos de escrita devem ser transacionais
- Auditoria deve registrar todas as operações sensíveis
- Email deve ser único no sistema (validação antes de criar/atualizar)
- Reset administrativo de senha gera código de 6 dígitos (Epic 2)
- Usuário administrador do Realm Master não pode ser bloqueado
- Roles devem ser do mesmo realm do usuário
- Código de reset expira em 15 minutos
- Envio de email será implementado na Story 07 (Reset Administrativo de Senha)

## Dependências
- Epic 2 (Gestão de Usuários) - entidade Usuario já existe
- Epic 3 (Gestão de Roles) - para associação de roles
- Epic 7 (Auditoria) - para registro de eventos
- Story 02 (DTOs de Usuário) - DTOs necessários

## Prioridade
**Alta** - Service layer necessário para controller

## Estimativa
- Implementação: 4 horas
- Testes: 2 horas
- Total: 6 horas
