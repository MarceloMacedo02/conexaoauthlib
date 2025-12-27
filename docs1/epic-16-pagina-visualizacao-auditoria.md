# Epic 16 - Página de Visualização de Auditoria (Thymeleaf)

## Status
**Estado:** 🔲 Planejado
**Iniciada em:** 2025-12-25
**Responsável:** BMad Team

## Objetivo

Implementar a página completa de visualização de eventos de auditoria do sistema, com filtros avançados, visualização detalhada, coloração por severidade e exportação em CSV.

---

## Histórias da Epic

### Story 01: Template de Tabela de Eventos
**Arquivo:** [epic-16-story-01](./stories/epic-16-story-01-template-tabela-eventos.md)
**Status:** ✅ Concluído (já existia no repositório)

---

### Story 02: DTOs de Eventos Auditoria (Java Records)
**Arquivo:** [epic-16-story-02](./stories/epic-16-story-02-dtos-eventos-auditoria.md)
**Status:** 🔲 Planejado

Criar DTOs (Data Transfer Objects) para representação de eventos de auditoria nas camadas de controller e view.

---

### Story 03: Backend Service Layer
**Arquivo:** [epic-16-story-03](./stories/epic-16-story-03-backend-service-layer.md)
**Status:** 🔲 Planejado

Implementar a camada de service para gerenciamento de eventos de auditoria.

---

### Story 04: Controller API
**Arquivo:** [epic-16-story-04](./stories/epic-16-story-04-controller-api.md)
**Status:** 🔲 Planejado

Criar o controller REST para endpoints de consulta de auditoria.

---

### Story 05: Filtros Avançados
**Arquivo:** [epic-16-story-05](./stories/epic-16-story-05-filtros-avancados.md)
**Status:** 🔲 Planejado

Implementar filtros avançados de eventos de auditoria.

---

### Story 06: Modal de Detalhes do Evento
**Arquivo:** [epic-16-story-06](./stories/epic-16-story-06-modal-detalhes-evento.md)
**Status:** 🔲 Planejado

Implementar modal de detalhes do evento com JSON completo.

---

### Story 07: Coloração por Severidade
**Arquivo:** [epic-16-story-07](./stories/epic-16-story-07-coloracao-severidade.md)
**Status:** 🔲 Planejado

Implementar sistema de coloração por severidade de eventos.

---

### Story 08: Exportação CSV + Ajustes Finais
**Arquivo:** [epic-16-story-08](./stories/epic-16-story-08-exportacao-csv-ajustes-finais.md)
**Status:** 🔲 Planejado

Implementar funcionalidade de exportação em CSV e ajustes finais da página.

---

## Dependências

- ✅ Epic 11 - Dashboard Principal (tabela de eventos recentes como referência)
- ✅ Epic 7 - Serviço de Registro de Eventos (backend já implementado)
- ✅ Epic 6.8 - Visualização de Auditoria (menu lateral)

---

## Requisitos do Escopo (conforme escopo.md)

### Auditoria - Visualização de Eventos

**Visualização de eventos por:**
- ✅ Realm
- ✅ Tipo
- ✅ Período (data início, data fim)
- ✅ Severidade

**Funcionalidades:**
- ✅ Paginação de eventos (20 por página)
- ✅ Modal de detalhes do evento com JSON completo
- ✅ Badges de cor por tipo de evento (TI icons)
- ✅ Badges de cor por severidade (SUCCESS, FAILED, WARNING)
- ✅ Formatação de data em PT-BR (dd/MM/yyyy HH:mm)
- ✅ Tempo relativo (ex: "5 minutos atrás")
- ✅ Exportação em CSV dos eventos filtrados
- ✅ Ordenação por data (padrão: mais recentes primeiro)

---

## Decisões Arquiteturais

### ADR 16.1: Estrutura de Filtros

**Decisão:** Implementar filtros em DTO dedicado (AuditoriaFilter) com validação separada.

### ADR 16.2: Coloração por Severidade

**Decisão:** Implementar lógica de coloração via método estático no DTO usando switch expression.

### ADR 16.3: Exportação CSV

**Decisão:** Implementar exportação via endpoint REST com Content-Type: text/csv.

---

## Critérios de Aceitação da Epic

- [ ] Todas as 8 histórias concluídas
- [ ] Tabela de eventos com paginação funcionando
- [ ] Filtros por realm, tipo, período e severidade operando
- [ ] Modal de detalhes exibindo JSON completo do evento
- [ ] Badges coloridos por tipo e severidade
- [ ] Exportação CSV gerando arquivo válido
- [ ] Página responsiva em dispositivos móveis
- [ ] Testes manuais de aceitação passando

---

## Links Relacionados

- [Escopo do Projeto](../escopo.md)
- [Epic 7 - Serviço de Registro de Eventos](../epic-7-modelo-dominio-auditoria.md)
- [Epic 11 - Dashboard Principal](../epic-11-dashboard-principal-metricas.md)
- [Epic 6.8 - Visualização de Auditoria](../epic-6-8-visualizacao-auditoria.md)

---

**Última Atualização:** 2025-12-25
**Responsável:** BMad Team
