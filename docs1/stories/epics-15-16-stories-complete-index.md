# Epic 15 & 16 - Stories Complete Index

## Overview
This document provides a complete index of all user stories created for Epic 15 (Gestão de Chaves Criptográficas) and Epic 16 (Visualização de Auditoria).

---

# Epic 15 - Gestão de Chaves Criptográficas

## Summary
**Total Stories:** 7
**Estimated Time:** 36 hours (approx. 4.5 business days)
**Focus:** Cryptographic key management, rotation history, manual rotation, countdown to automatic rotation

## Story List

### Story 01: Template da Lista de Chaves com Tabs
**File:** `epic-15-story-01-template-lista-chaves-tabs.md`
**Description:** Create the base template for key management with Bootstrap 5 tabs (Active Keys, Rotation History)
**Key Features:**
- Tabs navigation between active keys and history
- Table for active keys with realm filter
- Table for rotation history
- Countdown for next automatic rotation
- Responsive design with Bootstrap 5
- Tabler Icons throughout

**Estimated:** 4 hours

### Story 02: DTOs de Chave e Rotação
**File:** `epic-15-story-02-dtos-chave-rotacao.md`
**Description:** Create Java Records for data transfer (ChaveAtivaResponse, RotacaoHistoricoResponse, RotacaoRequest)
**Key Features:**
- ChaveAtivaResponse with automatic status calculation
- RotacaoHistoricoResponse with rotation details
- Enum StatusChave (ATIVA, EXPIRADA, EXPIRANDO)
- Enum StatusRotacao (CONCLUIDA, EM_ANDAMENTO, FALHOU)
- Methods for badge styling and formatted output
- Jakarta Bean Validation

**Estimated:** 4 hours

### Story 03: Backend Service Layer
**File:** `epic-15-story-03-backend-service-layer.md`
**Description:** Create AdminChaveService with methods for key management operations
**Key Features:**
- listarChavesAtivas(realmId) - with realm filtering
- listarHistoricoRotacoes(realmId) - with realm filtering
- rotacionarChaveManual(realmId, motivo, usuario) - manual rotation
- obterProximaRotacaoAutomatica() - next automatic rotation date
- buscarChavePorRealm(realmId) - active key by realm
- Transaction management with @Transactional
- Audit event registration

**Estimated:** 6 hours

### Story 04: Controller API
**File:** `epic-15-story-04-controller-api-adminchavecontroller.md`
**Description:** Create AdminChaveController with endpoints for key management
**Key Features:**
- GET /admin/chaves - main page
- GET /admin/chaves/api/ativas - list active keys
- GET /admin/chaves/api/historico - list rotation history
- POST /admin/chaves/api/rotacionar - manual rotation
- GET /admin/chaves/api/realm/{realmId} - key by realm
- GET /admin/chaves/api/proxima-rotacao - next auto rotation
- GET /admin/chaves/api/resumo - key statistics
- Error handling and validation

**Estimated:** 5 hours

### Story 05: Rotação Manual com Modal de Confirmação
**File:** `epic-15-story-05-rotacao-manual-modal.md`
**Description:** Implement confirmation modal for manual key rotation
**Key Features:**
- Bootstrap modal with rotation form
- Realm and version fields (readonly)
- Motivo field (textarea, optional)
- Warning alerts about grace period
- Loading state during processing
- Success/error toasts
- Table updates after rotation

**Estimated:** 5 hours

### Story 06: Visualização do Histórico de Rotações
**File:** `epic-15-story-06-visualizacao-historico-rotacoes.md`
**Description:** Implement complete rotation history visualization
**Key Features:**
- History table with all columns
- Filters by realm, date range, rotation type
- Status badges (Concluída, Em Andamento, Falhou)
- Icons for rotation type (Manual vs Automatic)
- Date/time formatting in Portuguese
- Pagination with DataTables
- Update after manual rotation

**Estimated:** 4 hours

### Story 07: Countdown para Próxima Rotação Automática
**File:** `epic-15-story-07-countdown-proxima-rotacao-automatica.md`
**Description:** Implement countdown showing days until next automatic rotation (day 1 of next month)
**Key Features:**
- Countdown of days remaining
- Next rotation date in Portuguese
- Auto-update every hour
- Color changes by days (green > 7, yellow ≤ 7, red ≤ 3)
- Warning alert when < 7 days
- Calendar icon display
- Error handling

**Estimated:** 3 hours

---

# Epic 16 - Página de Visualização de Auditoria

## Summary
**Total Stories:** 8
**Estimated Time:** 45 hours (approx. 5.6 business days)
**Focus:** Audit event visualization, advanced filtering, severity indicators, CSV export

## Story List

### Story 01: Template da Tabela de Eventos com Filtros
**File:** `epic-16-story-01-template-tabela-eventos.md`
**Description:** Create base template for audit events page with filters and table
**Key Features:**
- Page title with breadcrumb
- Advanced filters: Realm, Type (with optgroups), Date Range, User
- Search input for free-text search
- Records per page dropdown (5, 10, 15, 20)
- Events table with all columns
- Export CSV button
- Responsive design with Bootstrap 5
- Tabler Icons

**Estimated:** 4.5 hours

### Story 02: DTOs de Auditoria
**File:** `epic-16-story-02-dtos-eventos-auditoria.md`
**Description:** Create Java Records for audit event data transfer
**Key Features:**
- Enum TipoEventoAuditoria (all event types)
- Enum SeveridadeEvento (CRITICO, WARNING, INFO, SUCCESS)
- EventoAuditoriaResponse for list display
- EventoDetalheResponse for detail view
- Automatic severity mapping by event type
- Icon mapping by event type
- Formatted output methods
- JSON pretty-print support

**Estimated:** 5 hours

### Story 03: Backend Service Layer
**File:** `epic-16-story-03-backend-service-layer.md`
**Description:** Create AdminAuditoriaService with comprehensive query methods
**Key Features:**
- buscarEventos() with 6 filters (realm, type, user, dates, search)
- buscarPorId() for event details
- buscarParaExportar() for CSV export
- contarEventosPorTipo() for statistics
- buscarEventosCriticos() for security events
- Pagination support
- Default sorting by timestamp DESC
- Query optimization with indexes

**Estimated:** 7 hours

### Story 04: Controller API
**File:** `epic-16-story-04-controller-api.md`
**Description:** Create AdminAuditoriaController with all endpoints
**Key Features:**
- GET /admin/auditoria - main page
- GET /api/v1/admin/auditoria/eventos - list events
- GET /api/v1/admin/auditoria/eventos/{id} - event details
- GET /api/v1/admin/auditoria/export - CSV export
- GET /api/v1/admin/auditoria/contagem - type statistics
- GET /api/v1/admin/auditoria/criticos - critical events
- GET /api/v1/admin/auditoria/resumo - full statistics
- Error handling and validation

**Estimated:** 5.5 hours

### Story 05: Filtros Avançados
**File:** `epic-16-story-05-filtros-avancados.md`
**Description:** Implement advanced filtering functionality
**Key Features:**
- Realm filter (dropdown)
- Event type filter (dropdown with optgroups)
- Period filter (date range with Flatpickr)
- User filter (text input)
- Free-text search (details, user, IP)
- Apply and Reset filter buttons
- SessionStorage persistence
- Visual indicator of active filters
- Debounce on search input (300ms)
- Period validation (max 1 year)

**Estimated:** 6 hours

### Story 06: Modal de Detalhes do Evento
**File:** `epic-16-story-06-modal-detalhes-evento.md`
**Description:** Implement event details modal with complete information
**Key Features:**
- Modal with all event fields
- Type and severity badges with icons
- IP Info (internal/external identification)
- User Agent display (truncated with tooltip)
- Timestamp in Portuguese format
- Details JSON with pretty-print
- Copy JSON button
- Loading state
- Error handling
- Responsive layout (2-column grid)

**Estimated:** 5 hours

### Story 07: Coloração por Severidade
**File:** `epic-16-story-07-coloracao-severidade.md`
**Description:** Implement visual differentiation by event severity
**Key Features:**
- Severity badges (CRITICO=red, WARNING=yellow, INFO=blue, SUCCESS=green)
- Critical event row highlighting
- Security event alerts
- Pulse animation on security icons
- Custom CSS classes for each severity
- ARIA labels for accessibility
- High contrast for dark mode support
- Tooltips with severity descriptions

**Estimated:** 3 hours

### Story 08: Exportação CSV e Ajustes Finais
**File:** `epic-16-story-08-exportacao-csv-ajustes-finais.md`
**Description:** Implement CSV export and final UX adjustments
**Key Features:**
- Export CSV button respecting all filters
- BOM UTF-8 (Excel Portuguese support)
- Filename with current date (auditoria_YYYY-MM-DD.csv)
- CSV headers: Tipo, Usuário, Realm, Detalhes, IP, Data
- Loading state during export
- Success/error toasts
- Period validation (max 1 year)
- Mobile responsive adjustments
- ARIA labels for accessibility
- Scrollable table on mobile
- Excel and browser testing

**Estimated:** 5 hours

---

# Dependencies Overview

## Prerequisite Epics
- **Epic 1:** Gestão de Realms - needed for realm association
- **Epic 7:** Auditoria - backend already implemented
- **Epic 9:** Configuração - Thymeleaf and validation configured

## Epic 15 Dependencies
- Story 01 → Story 02 → Story 03 → Story 04 → Story 05, 06, 07
- Stories 05, 06, 07 are parallel (can be implemented together)

## Epic 16 Dependencies
- Story 01 → Story 02 → Story 03 → Story 04 → Story 05, 06, 07 → Story 08
- Stories 05, 06, 07 are parallel
- Story 08 depends on all others (final adjustments)

---

# Files Created

## Epic 15 Files (7 files)
1. `epic-15-story-01-template-lista-chaves-tabs.md`
2. `epic-15-story-02-dtos-chave-rotacao.md`
3. `epic-15-story-03-backend-service-layer.md`
4. `epic-15-story-04-controller-api-adminchavecontroller.md`
5. `epic-15-story-05-rotacao-manual-modal.md`
6. `epic-15-story-06-visualizacao-historico-rotacoes.md`
7. `epic-15-story-07-countdown-proxima-rotacao-automatica.md`

## Epic 16 Files (8 files)
1. `epic-16-story-01-template-tabela-eventos.md`
2. `epic-16-story-02-dtos-eventos-auditoria.md`
3. `epic-16-story-03-backend-service-layer.md`
4. `epic-16-story-04-controller-api.md`
5. `epic-16-story-05-filtros-avancados.md`
6. `epic-16-story-06-modal-detalhes-evento.md`
7. `epic-16-story-07-coloracao-severidade.md`
8. `epic-16-story-08-exportacao-csv-ajustes-finais.md`

## Index Files
1. `epics-15-16-stories-index.md` (this file)
2. `epics-15-16-stories-summary.md` (already exists with overview)

---

# Technology Stack

## Frontend
- **Framework:** Thymeleaf
- **CSS:** Bootstrap 5
- **Icons:** Tabler Icons (`ti-*`)
- **Data Tables:** DataTables.js
- **Date Picker:** Flatpickr
- **JavaScript:** ES6+ (fetch API, async/await)

## Backend
- **Framework:** Spring Boot
- **Java Version:** 17+
- **DTOs:** Java Records (immutable)
- **Validation:** Jakarta Bean Validation
- **Pagination:** Spring Data
- **Security:** Spring Security with @PreAuthorize

---

# Total Estimated Time

## Epic 15: Gestão de Chaves
- Story 01: 4 hours
- Story 02: 4 hours
- Story 03: 6 hours
- Story 04: 5 hours
- Story 05: 5 hours
- Story 06: 4 hours
- Story 07: 3 hours
- **Subtotal:** 36 hours

## Epic 16: Visualização de Auditoria
- Story 01: 4.5 hours
- Story 02: 5 hours
- Story 03: 7 hours
- Story 04: 5.5 hours
- Story 05: 6 hours
- Story 06: 5 hours
- Story 07: 3 hours
- Story 08: 5 hours
- **Subtotal:** 45 hours

## Grand Total: 81 hours (approx. 10 business days)

---

# Implementation Notes

## Key Features Implemented

### Epic 15: Key Management
- **Tab Navigation:** Easy switching between Active Keys and History
- **Real-time Countdown:** Auto-updating countdown to next rotation
- **Manual Rotation:** Safe rotation with confirmation and audit trail
- **Status Indicators:** Visual badges for key status (Active, Expiring, Expired)
- **History Tracking:** Complete rotation history with timestamps and reasons
- **Grace Period:** Tokens remain valid after rotation
- **Filtering:** By realm for both tables

### Epic 16: Audit Visualization
- **Advanced Filtering:** 6 different filters (realm, type, period, user, search)
- **Severity Indicators:** Color-coded badges and row highlighting
- **Security Alerts:** Special indicators for critical events
- **CSV Export:** Complete export with all filters respected
- **Event Details:** Detailed modal with JSON pretty-print
- **Persistence:** Filters saved in sessionStorage
- **Accessibility:** ARIA labels and keyboard navigation
- **Responsive Design:** Mobile-first approach

---

# Next Steps

1. **Epic 15 Implementation:**
   - Start with Story 01 (Template)
   - Implement DTOs (Story 02)
   - Build Service Layer (Story 03)
   - Create Controller (Story 04)
   - Implement Parallel Features (Stories 05, 06, 07)

2. **Epic 16 Implementation:**
   - Start with Story 01 (Template)
   - Implement DTOs (Story 02)
   - Build Service Layer (Story 03)
   - Create Controller (Story 04)
   - Implement Parallel Features (Stories 05, 06, 07)
   - Complete with Final Adjustments (Story 08)

3. **Testing:**
   - Unit tests for all DTOs and Services
   - Integration tests for Controllers
   - E2E tests for complete user flows
   - Mobile responsiveness testing
   - Accessibility testing (WCAG compliance)

4. **Documentation:**
   - Update API documentation with new endpoints
   - Create user guide for key rotation
   - Document audit event types and severity levels

---

# Success Criteria

Both epics will be considered complete when:

## Epic 15
- [ ] All stories implemented according to acceptance criteria
- [ ] Manual key rotation works end-to-end
- [ ] Countdown accurately reflects days until next rotation
- [ ] History displays all rotations correctly
- [ ] Grace period is properly explained to users
- [ ] All filters work correctly
- [ ] Mobile responsive design verified
- [ ] All tests passing

## Epic 16
- [ ] All stories implemented according to acceptance criteria
- [ ] Advanced filters work individually and combined
- [ ] CSV export works with all filter combinations
- [ ] Severity badges display correctly
- [ ] Critical events are visually highlighted
- [ ] Event details modal shows all information
- [ ] Filters persist and restore correctly
- [ ] Mobile responsive design verified
- [ ] Accessibility compliance verified
- [ ] All tests passing

---

**Document Version:** 1.0
**Last Updated:** December 25, 2025
**Author:** Bob (Scrum Master)
