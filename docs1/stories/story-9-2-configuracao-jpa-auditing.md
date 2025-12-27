# História 9.2: Configuração de JPA Auditing

**Epic:** 9 - Configuração e Infraestrutura
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso configurar JPA Auditing para que eu possa rastrear automaticamente quando e por quem as entidades foram criadas/atualizadas.

---

## Critérios de Aceite

- [x] Classe base `BaseEntity` com campos de auditoria
- [x] Configuração `JpaAuditingConfig` habilitando auditoria
- [x] Anotações `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`
- [x] Implementação de `AuditorAware` para capturar usuário atual
- [x] Uso de `@EntityListeners(AuditingEntityListener.class)`
- [x] Configuração de timezone UTC para datas

---

## Regras de Negócio

1. BaseEntity:
   - `id`: UUID gerado automaticamente
   - `dataCriacao`: Timestamp de criação (imutável)
   - `dataAtualizacao`: Timestamp de última atualização
   - `criadoPor`: Usuario que criou o registro

2. Auditoria Automática:
   - Atualização automática de `dataCriacao` e `dataAtualizacao`
   - Captura automática do usuário atual

---

## Requisitos Técnicos

### BaseEntity
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @CreatedBy
    @Column(name = "criado_por", length = 255)
    private String criadoPor;
    
    public UUID getId() {
        return id;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public String getCriadoPor() {
        return criadoPor;
    }
}
```

### JpaAuditingConfig
```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
    
    @Bean
    public AuditorAware<String> auditorAware() {
        return new SecurityAuditorAware();
    }
}
```

### SecurityAuditorAware
```java
@Component
public class SecurityAuditorAware implements AuditorAware<String> {
    
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        
        String username = authentication.getName();
        
        if (username.startsWith("service_")) {
            return Optional.of(username);
        }
        
        return Optional.ofNullable(username);
    }
}
```

### Exemplo de Entidade Usando BaseEntity
```java
@Entity
@Table(name = "realm")
public class Realm extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 50)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRealm status;
    
    // getters e setters
}
```

---

## Exemplos de Testes

### Teste de Auditoria Automática
```java
@SpringBootTest
@ActiveProfiles("test")
public class JpaAuditingTest {
    
    @Autowired
    private RealmRepository realmRepository;
    
    @Test
    @WithMockUser(username = "test-user")
    void dadoCriacaoDeEntidade_quandoSalvar_entaoAuditoriaPreenchida() {
        Realm realm = new Realm();
        realm.setNome("test-realm");
        realm.setStatus(StatusRealm.ATIVO);
        
        Realm salvo = realmRepository.save(realm);
        
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getDataCriacao()).isNotNull();
        assertThat(salvo.getCriadoPor()).isEqualTo("test-user");
    }
}
```

---

## Dependências

- História 9.1: Configuração de Banco de Dados

---

## Pontos de Atenção

- Todos os campos de auditoria em UTC
- Auditoria desativada em testes de performance
- Checkstyle: Seguir Google Java Style Guide
