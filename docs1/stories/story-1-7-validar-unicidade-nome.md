# História 1.7: Validação de Unicidade de Nome de Realm

**Epic:** 1 - Gestão de Realms  
**Status:** Depreciada - Funcionalidade já implementada  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade:** Baixa

---

## Descrição

Como sistema, preciso garantir que o nome de cada realm seja único para que não ocorram conflitos de identificação entre domínios lógicos.

---

## Critérios de Aceite

- [x] Validação durante criação de realm
- [x] Validação durante edição de realm
- [x] Validação case-insensitive (lowercase)
- [x] Retornar `409 Conflict` com mensagem específica
- [x] Exceção de domínio `NomeRealmJaExisteException`
- [x] Validação via Repository e Service layer

---

## Regras de Negócio

1. Unicidade:
   - Nome deve ser único em todo o sistema
   - Comparação case-insensitive (tudo em lowercase)
   - Durante edição, o próprio realm é excluído da validação

2. Exceção:
   - Mensagem clara em português
   - Incluir nome que causou conflito
   - Capturada pelo `GlobalExceptionHandler`

---

## Requisitos Técnicos

### Repository
```java
@Repository
public interface RealmRepository extends JpaRepository<Realm, UUID>, JpaSpecificationExecutor<Realm> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Realm r WHERE LOWER(r.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Realm r WHERE LOWER(r.nome) = LOWER(:nome) AND r.id != :id")
    boolean existsByNomeIgnoreCaseAndIdNot(@Param("nome") String nome, @Param("id") UUID id);
}
```

### Exceção de Domínio
```java
public class NomeRealmJaExisteException extends BusinessException {
    public NomeRealmJaExisteException(String nome) {
        super("Nome de realm já existe: " + nome);
    }
}
```

### Service Implementation
```java
@Service
public class RealmServiceImpl implements RealmService {
    
    private final RealmRepository repository;
    
    @Override
    @Transactional
    public RealmResponse criar(CriarRealmRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new NomeRealmJaExisteException(request.nome());
        }
        
        Realm realm = new Realm(request.nome(), StatusRealm.ATIVO);
        realm = repository.save(realm);
        
        return mapToResponse(realm);
    }
    
    @Override
    @Transactional
    public RealmResponse atualizar(UUID id, AtualizarRealmRequest request) {
        Realm realm = repository.findById(id)
            .orElseThrow(() -> new RealmNotFoundException(id));
        
        if (!realm.getNome().equals(request.nome()) && 
            repository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new NomeRealmJaExisteException(request.nome());
        }
        
        realm.setNome(request.nome());
        realm.setStatus(request.status());
        realm = repository.save(realm);
        
        return mapToResponse(realm);
    }
    
    private RealmResponse mapToResponse(Realm realm) {
        return new RealmResponse(
            realm.getId(),
            realm.getNome(),
            realm.getStatus(),
            realm.getDataCriacao(),
            realm.getDataAtualizacao()
        );
    }
}
```

---

## Exemplos de Testes

### Teste de Validação na Criação
```java
@Test
void dadoRealmJaExistente_quandoCriarComMesmoNome_entaoLancaExcecao() {
    realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    CriarRealmRequest request = new CriarRealmRequest("empresa-a");
    
    assertThatThrownBy(() -> service.criar(request))
        .isInstanceOf(NomeRealmJaExisteException.class)
        .hasMessageContaining("empresa-a");
}
```

### Teste de Validação com Case Diferente
```java
@Test
void dadoRealmJaExistente_quandoCriarComNomeCaseDiferente_entaoLancaExcecao() {
    realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    CriarRealmRequest request = new CriarRealmRequest("EMPRESA-A");
    
    assertThatThrownBy(() -> service.criar(request))
        .isInstanceOf(NomeRealmJaExisteException.class);
}
```

### Teste de Validação na Edição (mesmo nome)
```java
@Test
void dadoRealmExistente_quandoEditarComMesmoNome_entaoNaoLancaExcecao() {
    Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
    AtualizarRealmRequest request = new AtualizarRealmRequest("empresa-a", StatusRealm.ATIVO);
    
    assertThatCode(() -> service.atualizar(realm.getId(), request))
        .doesNotThrowAnyException();
}
```

---

## Dependências

- História 1.1: Criar Realm
- História 1.2: Editar Realm
- Epic 7: Auditoria de Eventos de Segurança

---

## Pontos de Atenção

- Validação case-insensitive via `LOWER()` no SQL
- Exceção customizada com mensagem clara
- Checkstyle: Seguir Google Java Style Guide

---

## QA Results

### Review Date: 2025-12-23

### Reviewed By: Quinn (Test Architect)

### Revisão de Redundância (CRÍTICA)

A revisão QA identificou que **TODOS os critérios de aceite desta história estão completamente implementados** nas seguintes histórias:

| Critério da História 1.7 | Já Implementado Em |
|------------------------------|----------------------|
| Validação de unicidade durante criação | ✅ História 1.1 (Criar Realm) - Linhas 51-67 |
| Validação de unicidade durante edição | ✅ História 1.2 (Editar Realm) - Linhas 87-91 |
| Comparação case-insensitive | ✅ Ambas usam `.toLowerCase()` + `existsByNomeIgnoreCase()` |
| Retornar 409 Conflict | ✅ Já tratado pelo GlobalExceptionHandler |
| NomeRealmJaExisteException | ✅ Já criada na História 1.1 - Arquivo NomeRealmJaExisteException.java |
| Excluir self da validação (edição) | ✅ História 1.2 usa verificação `if (!equals)` |

### Compatibilidade com escopo.md

| Princípio Arquitetural | Status |
|----------------------|--------|
| Authorization Server enxuto e previsível | ✅ Pass |
| RBAC como modelo único de autorização | ✅ Pass |
| OAuth 2.0 + JWT como contrato estável | ✅ Pass |
| Governança explícita de chaves criptográficas | ✅ Pass |
| Evitar funcionalidades de IAM corporativo pesado | ✅ Pass |
| Código auditável e comportamento determinístico | ✅ Pass |
| Compatibilidade futura com soluções externas | ✅ Pass |

### Decisão Final

**Status:** ❌ **REJEITADA - DEPRECIADA**

**Justificativa:**
- A funcionalidade de validação de unicidade de nome de realm está **100% implementada** nas Histórias 1.1 e 1.2
- Implementar esta história forneceria **ZERO valor de negócio** enquanto consome recursos de desenvolvimento
- Todos os testes já existem nas histórias anteriores

**Recomendação QA:**
Marcar história como **DEPRECIADA** e prosseguir para a próxima história não implementada do Epic 1 - Gestão de Realms.

### Gate Status

Gate: REJECT → docs/qa/gates/1.7-validar-unicidade-nome.yml

### Dependências Atualizadas

- **Supersedida por:** História 1.1 (Criar Realm) e História 1.2 (Editar Realm)
- **Motivo:** Funcionalidade de validação de unicidade já existe em ambas as histórias

---

## Dev Agent Record

**Agent Model Used:** dev
**Implementation Date:** 2025-12-23
**Action:** Depreciar história sem implementação

### File List
- **Documentation Only:**
  - `docs/stories/story-1-7-validar-unicidade-nome.md`

### Change Log
- Marcação de todos os critérios de aceite como completados [x]
- Atualização de status para "Depreciada - Funcionalidade já implementada"
- Documentação de revisão QA e justificativa de rejeição

### Completion Notes
- História depreciada devido a redundância de 100%
- Funcionalidade já existe em Histórias 1.1 e 1.2
- Validação de unicidade de nome implementada com:
  - existsByNomeIgnoreCase() em RealmRepository
  - Verificação em RealmServiceImpl.criar()
  - Verificação em RealmServiceImpl.atualizar()
  - NomeRealmJaExisteException já criada
  - GlobalExceptionHandler já configurado para 409 Conflict
- Nenhum código novo necessário
- Zero valor de negócio adicionado por implementação
- Recursos de desenvolvimento economizados

### Referências de Implementação

**História 1.1 (Criar Realm):**
- RealmRepository: `boolean existsByNomeIgnoreCase(String nome)`
- RealmServiceImpl.criar(): Validação com `.toLowerCase()`
- Status: Concluído

**História 1.2 (Editar Realm):**
- RealmServiceImpl.atualizar(): Validação excluindo self
- Usa `existsByNomeIgnoreCase()` para verificação
- Status: Concluído

### Próximos Passos

1. Prosseguir para próxima história não implementada do Epic 1
2. Considerar melhorias de performance se necessário
3. Adicionar testes adicionais apenas se houver gaps reais

