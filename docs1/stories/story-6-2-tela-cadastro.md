# História 6.2: Tela de Cadastro

**Epic:** 6 - Dashboard Administrativo (Thymeleaf)  
**Status:** Ready for Review  
**Prioridade:** Média  
**Estimativa:** 3 dias  
**Complexidade**: Média

---

## Descrição

Como usuário do sistema, quero acessar uma tela de cadastro para que eu possa criar minha conta de acesso.

---

## Critérios de Aceite

- [ ] Tela de cadastro em `/admin/cadastro`
- [ ] Formulário com campos: nome, email, senha, confirmação de senha
- [ ] Validação de todos os campos obrigatórios
- [ ] Validação de email válido
- [ ] Validação de senha mínimo 8 caracteres
- [ ] Validação de confirmação de senha igual a senha
- [ ] Realm definido automaticamente (Realm Master ou específico)
- [ ] Mensagem de sucesso/erro
- [ ] Redirect para tela de login após cadastro
- [ ] Design responsivo usando Bootstrap 5

---

## Requisitos Técnicos

### Template Thymeleaf
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro - Admin Dashboard</title>
    <link th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-4">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <h3 class="text-center mb-4">Cadastro</h3>
                        
                        <div th:if="${error}" class="alert alert-danger">
                            <span th:text="${error}">Erro ao cadastrar</span>
                        </div>
                        
                        <div th:if="${success}" class="alert alert-success">
                            <span th:text="${success}">Cadastro realizado com sucesso</span>
                        </div>
                        
                        <form th:action="@{/admin/cadastro}" method="post" th:object="${cadastroForm}">
                            <div class="mb-3">
                                <label for="nome" class="form-label">Nome</label>
                                <input type="text" class="form-control" id="nome" th:field="*{nome}" required>
                                <div class="text-danger" th:if="${#fields.hasErrors('nome')}" th:errors="*{nome}"></div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" th:field="*{email}" required>
                                <div class="text-danger" th:if="${#fields.hasErrors('email')}" th:errors="*{email}"></div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="senha" class="form-label">Senha</label>
                                <input type="password" class="form-control" id="senha" th:field="*{senha}" required>
                                <div class="text-danger" th:if="${#fields.hasErrors('senha')}" th:errors="*{senha}"></div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmarSenha" class="form-label">Confirmar Senha</label>
                                <input type="password" class="form-control" id="confirmarSenha" th:field="*{confirmarSenha}" required>
                                <div class="text-danger" th:if="${#fields.hasErrors('confirmarSenha')}" th:errors="*{confirmarSenha}"></div>
                            </div>
                            
                            <button type="submit" class="btn btn-success w-100">Cadastrar</button>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a th:href="@{/admin/login}">Já tem conta? Faça login</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>
</body>
</html>
```

### DTO
```java
public record CadastroForm(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha,
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    String confirmarSenha
) {
    @AssertTrue(message = "Senhas não conferem")
    public boolean isSenhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }
}
```

### Controller
```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UsuarioService usuarioService;
    private final RealmRepository realmRepository;
    
    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("cadastroForm", new CadastroForm("", "", "", ""));
        return "admin/cadastro";
    }
    
    @PostMapping("/cadastro")
    public String cadastro(@Valid @ModelAttribute CadastroForm form, 
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/cadastro";
        }
        
        try {
            Realm realm = realmRepository.findByNome("master")
                .orElseThrow(() -> new RealmNotFoundException("master"));
            
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                form.nome(),
                form.email(),
                form.senha(),
                realm.getId(),
                List.of(roleRepository.findByNomeAndRealmId("USER", realm.getId()).getId()),
                null,
                null
            );
            
            usuarioService.criar(request);
            
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso");
            return "redirect:/admin/login";
        } catch (EmailJaExisteException e) {
            redirectAttributes.addFlashAttribute("error", "Email já cadastrado");
            return "redirect:/admin/cadastro";
        }
    }
}
```

---

## Exemplos de Testes

### Teste de Tela de Cadastro Renderizada
```java
@SpringBootTest
@AutoConfigureMockMvc
public class CadastroControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void quandoAcessarTelaCadastro_entaoRetornaOk() throws Exception {
        mockMvc.perform(get("/admin/cadastro"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/cadastro"))
            .andExpect(model().attributeExists("cadastroForm"));
    }
}
```

---

## Dependências

- História 2.1: Criar Usuário
- Epic 1: Gestão de Realms
- Epic 3: Gestão de Roles

---

## Pontos de Atenção

- Validação de senha e confirmação
- Role padrão USER para cadastros
- Realm Master ou realm específico
- Design responsivo com Bootstrap 5
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Implementado Por:
- James (dev) em 2025-12-23

### Status
- Status: Ready for Review

### Tasks / Subtasks Checkboxes
- [x] Tela de cadastro em `/admin/cadastro`
- [x] Formulário com campos: nome, email, senha, confirmação de senha
- [x] Validação de todos os campos obrigatórios
- [x] Validação de email válido
- [x] Validação de senha mínimo 8 caracteres
- [x] Validação de confirmação de senha igual a senha
- [x] Realm definido automaticamente (Realm Master ou específico)
- [x] Mensagem de sucesso/erro
- [x] Redirect para tela de login após cadastro
- [x] Design responsivo usando Bootstrap 5

### File List
### Novos Arquivos:
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/dto/CadastroForm.java`
- `src/main/resources/templates/admin/cadastro.html`

### Arquivos Modificados:
- `src/main/java/br/com/plataforma/conexaodigital/admin/api/controller/AdminController.java` (endpoint cadastro)

### Arquivos Deletados:
- Nenhum

### Debug Log References
- Nenhum erro durante compilação ou testes

### Completion Notes List
- Implementado tela de cadastro com Bootstrap 5 em `/admin/cadastro`
- Criado CadastroForm DTO com validações (@NotBlank, @Email, @Size, @AssertTrue)
- Implementado endpoint POST `/admin/cadastro` no AdminController
- Lógica de cadastro cria usuário no realm master com role USER padrão
- Mensagens de flash para sucesso/erro configuradas
- Redirect para `/admin/login` após cadastro bem-sucedido
- Template Thymeleaf criado com feedback visual de erros e sucesso

### Change Log
- 2025-12-23: Criado CadastroForm DTO com validações
- 2025-12-23: Implementado endpoint de cadastro no AdminController
- 2025-12-23: Criado template cadastro.html com Bootstrap 5
- 2025-12-23: Configurada lógica de cadastro usando realm master e role padrão USER

---

## QA Results

### Review Date: 2025-12-24

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

A tela de cadastro está bem implementada com validações robustas usando Bean Validation (@NotBlank, @Email, @Size, @AssertTrue). O fluxo de cadastro cria usuário corretamente no realm master com role USER padrão. O design responsivo com Bootstrap 5 está adequado.

### Compliance Check

- Coding Standards: ✓ Segue Google Java Style Guide
- Project Structure: ✓ Estrutura MVC correta
- Testing Strategy: ⚠️ Testes unitários presentes mas falta E2E
- All ACs Met: ✓ Todos os critérios implementados

### Improvements Checklist

- [x] Verificado formulário completo com validações
- [x] Validada criação de usuário com role USER
- [x] Confirmado tratamento de email duplicado
- [x] Verificada senha criptografada
- [ ] **CRÍTICO**: Ativar proteção CSRF no SecurityConfig
- [ ] **IMPORTANTE**: Implementar testes E2E de fluxo de cadastro
- [ ] Adicionar verificação de email em tempo real
- [ ] Implementar reCAPTCHA para proteção contra bots

### Security Review

⚠️ **VULNERABILIDADE CRÍTICA IDENTIFICADA**: Formulário de cadastro sem proteção CSRF. Isso permite ataques de Cross-Site Request Forgery onde um atacante pode criar contas em nome de usuários autenticados.

**Recomendação Imediata:**
```java
// Em SecurityConfig, habilitar CSRF:
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/**") // se necessário
)
```

### Performance Considerations

✅ Performance do processo de cadastro é adequada. Validações são eficientes e o processo de criação de usuário está otimizado.

### Files Modified During Review

Nenhum arquivo modificado durante esta revisão.

### Gate Status

Gate: CONCERNS → docs/qa/gates/6.2-tela-cadastro.yml

### Recommended Status

[✗ Changes Required - See unchecked items above]
