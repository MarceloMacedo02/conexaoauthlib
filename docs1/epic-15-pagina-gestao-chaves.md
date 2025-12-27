
## Status
**Estado:** 🔲 Planejado
**Iniciada em:** 2025-12-25
**Responsável:** BMad Team

## Objetivo

Implementar a página completa de gestão de chaves criptográficas, com visualização de chaves ativas e históricas, funcionalidade de rotação manual e visualização da próxima rotação automática.

## Histórias da Epic

| Story | Título | Status |
|--------|---------|--------|
| [Story 01](./stories/epic-15-story-01-template-lista-chaves-tabs.md) | Template de Lista de Chaves (Tabs) | 🔲 Planejado |
| [Story 02](./stories/epic-15-story-02-dtos-chave-rotacao.md) | DTOs Chave e Rotação (Java Records) | 🔲 Planejado |
| [Story 03](./stories/epic-15-story-03-backend-service-layer.md) | Backend Service Layer | 🔲 Planejado |
| [Story 04](./stories/epic-15-story-04-controller-api-adminchavecontroller.md) | Controller API (AdminChaveController) | 🔲 Planejado |
| [Story 05](./stories/epic-15-story-05-rotacao-manual-modal.md) | Rotação Manual Modal | 🔲 Planejado |
| [Story 06](./stories/epic-15-story-06-visualizacao-historico-rotacoes.md) | Visualização Histórico Rotações | 🔲 Planejado |
| [Story 07](./stories/epic-15-story-07-countdown-proxima-rotacao-automatica.md) | Countdown Próxima Rotação Automática | 🔲 Planejado |

---

## Requisitos do Escopo (conforme escopo.md)

### Gestão de Chaves Criptográficas

**Funcionalidades:**

- ✅ Visualizar chaves ativas e históricas
- ✅ Executar **rotação manual** via dashboard
- ✅ Visualizar próxima rotação automática (data/hora)
- ✅ Histórico completo de rotações (quando, quem, chaves)
- ✅ Badges de cor por status (ativa/inativa/pró-rotação)
- ✅ Tabs: "Chaves Ativas" e "Histórico de Rotações"
- ✅ Paginação de chaves (20 por página)
- ✅ Indicador visual de Realm Master (ícone estrela)
- ✅ Ícones Tabler específicos por ação

**Chaves por realm:**
- Armazenamento em banco de dados
- Chave privada criptografada com **AES-128**
- Versionamento obrigatório
- Chave pública exposta via **JWKS por realm**

**Regras de Rotação:**

- Automática: Cron mensal – dia 1
- Manual: Via dashboard
- Grace period: Tokens antigos válidos até expiração
- Auditoria obrigatória

---

## Decisões Arquiteturais

### ADR 15.1: Estrutura de Página com Tabs

**Decisão:** Implementar página com duas tabs: "Chaves Ativas" e "Histórico de Rotações".

**Rationale:**
- Separação clara entre visualização atual e histórico
- Reduz complexidade da interface
- Permite navegação fácil entre contextos

**Consequências:**
- ✅ Tabs Bootstrap 5 configuradas
- ✅ Conteúdo carregado dinamicamente via AJAX
- ✅ Estado das tabs persistido durante navegação

### ADR 15.2: DTOs Específicos para Chaves e Rotações

**Decisão:** Criar DTOs dedicados para representação de chaves e rotações.

**Rationale:**
- Separação clara de responsabilidades
- Validações independentes por domínio
- Reutilização em diferentes contextos

**Consequências:**
- ✅ ChaveListResponse (com paginação, métricas)
- ✅ ChaveDetailResponse (com detalhes e histórico)
- ✅ RotacaoInfoResponse (informações da próxima rotação automática)
- ✅ Métodos auxiliares para formatação de datas

---

## Dependências

- ✅ Epic 11 - Dashboard Principal (como referência de layout)
- ✅ Models de chaves existentes (ChaveCriptografica, RotacaoChave, StatusChave)

---

## Notas de Implementação

### Models Existentes

As seguintes entities já existem no projeto:
- `ChaveCriptografica` - Armazena chave pública/privada
- `RotacaoChave` - Registra cada rotação
- `StatusChave` - Status da chave (ATIVA, INATIVA, PRE_ROTACIONADA, ROTACIONADA)

### Templates Existentes

Verifique se o template `src/main/resources/templates/admin/auditoria/list.html` pode ser adaptado/reutilizado para a página de chaves.

---

## Critérios de Aceitação da Epic

- [ ] Todas as 7 histórias concluídas
- [ ] Página de chaves com tabs funcionando
- [ ] Tab "Chaves Ativas" exibe chaves ativas
- [ ] Tab "Histórico" exibe rotações
- [ ] Rotação manual funcional
- [ ] Countdown de próxima rotação automática visível
- [ ] Histórico de rotações exibindo quem executou e quando
- [ ] Badges de cor por status
- [ ] Paginação de chaves funcionando
- [ ] Realm Master indicado visualmente
- [ ] Ícones Tabler corretos
- [ ] Responsividade testada em dispositivos móveis
- [ ] Testes manuais de aceitação passando

---

## Links Relacionados

- [Escopo do Projeto](../escopo.md)
- [Epic 6.5 - Gestão de Chaves](../epic-6-5-gestao-chaves.md) - Modelos de chaves existentes
- [Epic 16 - Visualização de Auditoria](../epic-16-pagina-visualizacao-auditoria.md) - Template de auditoria pode ser reutilizado

---

**Última Atualização:** 2025-12-25
**Responsável:** BMad Team
