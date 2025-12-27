# User Story: DTOs de Chave e Rotação (Java Records)

**Epic:** 15 - Página de Gestão de Chaves Criptográficas (Thymeleaf)
**Story ID:** epic-15-story-02

## Descrição
Criar os DTOs (Data Transfer Objects) como Java Records para transferência de dados entre a camada de controller e service para gestão de chaves criptográficas e histórico de rotações.

## Critérios de Aceite
- [ ] Record `ChaveAtivaResponse` criado com todos os campos necessários para listagem
- [ ] Record `RotacaoHistoricoResponse` criado com campos completos para histórico
- [ ] Record `RotacaoRequest` criado para rotação manual
- [ ] Métodos auxiliares de conversão implementados
- [ ] Enum `StatusChave` implementado (ATIVA, EXPIRADA, EXPIRANDO)
- [ ] Enum `StatusRotacao` implementado (CONCLUIDA, EM_ANDAMENTO, FALHOU)
- [ ] Cálculo automático de dias restantes para expiração
- [ ] Imutabilidade garantida (records são imutáveis por padrão)

## Tarefas
1. Criar package `br.com.plataforma.conexaodigital.admin.api.responses`
2. Criar package `br.com.plataforma.conexaodigital.admin.api.requests`
3. Criar record `ChaveAtivaResponse.java` para listagem
4. Criar record `RotacaoHistoricoResponse.java` para histórico
5. Criar record `RotacaoRequest.java` para rotação manual
6. Implementar métodos de mapeamento de entidade para DTOs

## Instruções de Implementação

### Package Structure
```
br.com.plataforma.conexaodigital.admin.api
├── controller
│   └── AdminChaveController.java
├── requests
│   └── RotacaoRequest.java
└── responses
    ├── ChaveAtivaResponse.java
    └── RotacaoHistoricoResponse.java
```

### Record: ChaveAtivaResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/ChaveAtivaResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

/**
 * DTO para resposta de chaves ativas na página administrativa.
 * Contém os campos essenciais para exibição na tabela.
 */
public record ChaveAtivaResponse(
    String id,
    String realmId,
    String realmNome,
    String versao, // kid (Key ID)
    String tipo, // RSA-2048, RSA-4096, etc.
    LocalDateTime dataCriacao,
    LocalDateTime dataExpiracao,
    StatusChave status,
    Integer diasRestantes
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     * @param chave entidade ChaveCriptografica
     * @param realmNome nome do realm da chave
     * @param realmId ID do realm da chave
     * @return instância de ChaveAtivaResponse
     */
    public static ChaveAtivaResponse from(
        br.com.plataforma.conexaodigital.gestachave.domain.model.ChaveCriptografica chave,
        String realmNome,
        String realmId
    ) {
        // Calcular dias restantes
        int diasRestantes = calcularDiasRestantes(chave.getDataExpiracao());

        // Determinar status
        StatusChave status = diasRestantes < 0 ? StatusChave.EXPIRADA :
                              diasRestantes <= 7 ? StatusChave.EXPIRANDO :
                              StatusChave.ATIVA;

        return new ChaveAtivaResponse(
            chave.getId().toString(),
            realmId,
            realmNome,
            chave.getKid(),
            chave.getTipo(),
            chave.getDataCriacao(),
            chave.getDataExpiracao(),
            status,
            diasRestantes
        );
    }

    /**
     * Calcula os dias restantes até a expiração.
     */
    private static int calcularDiasRestantes(LocalDateTime dataExpiracao) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), dataExpiracao
        );
    }

    /**
     * Retorna texto formatado para badge de dias restantes.
     */
    public String getBadgeDiasRestantes() {
        if (diasRestantes < 0) {
            return "Expirada";
        }
        return diasRestantes + " dias";
    }

    /**
     * Retorna classe CSS para badge de dias restantes.
     */
    public String getBadgeDiasClasse() {
        if (diasRestantes < 0) {
            return "bg-danger";
        } else if (diasRestantes <= 7) {
            return "bg-warning";
        }
        return "bg-success";
    }
}

/**
 * Enum para status da chave.
 */
public enum StatusChave {
    ATIVA("Ativa", "bg-success-subtle text-success"),
    EXPIRADA("Expirada", "bg-danger-subtle text-danger"),
    EXPIRANDO("Expirando", "bg-warning-subtle text-warning");

    private final String descricao;
    private final String classeCss;

    StatusChave(String descricao, String classeCss) {
        this.descricao = descricao;
        this.classeCss = classeCss;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClasseCss() {
        return classeCss;
    }
}
```

### Record: RotacaoHistoricoResponse
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/responses/RotacaoHistoricoResponse.java`

```java
package br.com.plataforma.conexaodigital.admin.api.responses;

import java.time.LocalDateTime;

/**
 * DTO para resposta de histórico de rotações de chaves.
 * Contém os campos essenciais para exibição na tabela de histórico.
 */
public record RotacaoHistoricoResponse(
    String id,
    String realmId,
    String realmNome,
    String versaoAntiga, // kid anterior
    String versaoNova, // kid atual
    LocalDateTime dataRotacao,
    String responsavel, // usuário que fez a rotação
    String motivo, // motivo da rotação
    StatusRotacao status
) {
    /**
     * Método estático de fábrica para criar instância a partir da entidade.
     * @param rotacao entidade RotacaoChave
     * @param realmNome nome do realm
     * @param realmId ID do realm
     * @return instância de RotacaoHistoricoResponse
     */
    public static RotacaoHistoricoResponse from(
        br.com.plataforma.conexaodigital.gestachave.domain.model.RotacaoChave rotacao,
        String realmNome,
        String realmId
    ) {
        return new RotacaoHistoricoResponse(
            rotacao.getId().toString(),
            realmId,
            realmNome,
            rotacao.getVersaoAntiga(),
            rotacao.getVersaoNova(),
            rotacao.getDataRotacao(),
            rotacao.getResponsavel(),
            rotacao.getMotivo(),
            rotacao.getStatus()
        );
    }

    /**
     * Retorna texto formatado para data da rotação.
     */
    public String getDataFormatada() {
        return dataRotacao.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm"));
    }

    /**
     * Retorna texto formatado para motivo (ou "-" se vazio).
     */
    public String getMotivoFormatado() {
        return motivo != null && !motivo.isBlank() ? motivo : "-";
    }
}

/**
 * Enum para status da rotação.
 */
public enum StatusRotacao {
    CONCLUIDA("Concluída", "bg-success-subtle text-success"),
    EM_ANDAMENTO("Em Andamento", "bg-warning-subtle text-warning"),
    FALHOU("Falhou", "bg-danger-subtle text-danger");

    private final String descricao;
    private final String classeCss;

    StatusRotacao(String descricao, String classeCss) {
        this.descricao = descricao;
        this.classeCss = classeCss;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClasseCss() {
        return classeCss;
    }
}
```

### Record: RotacaoRequest
**Localização:** `src/main/java/br/com/plataforma/conexaodigital/admin/api/requests/RotacaoRequest.java`

```java
package br.com.plataforma.conexaodigital.admin.api.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitação de rotação manual de chave.
 * Contém validações Jakarta Bean Validation.
 */
public record RotacaoRequest(
    @NotNull(message = "{rotacao.realm.obrigatorio}")
    String realmId,

    String motivo
) {
    /**
     * Valida se motivo não excede tamanho máximo.
     */
    public boolean isMotivoValido() {
        if (motivo == null) {
            return true; // motivo é opcional
        }
        return motivo.length() <= 500;
    }

    /**
     * Retorna true se motivo foi informado.
     */
    public boolean hasMotivo() {
        return motivo != null && !motivo.isBlank();
    }
}
```

### Messages Properties
**Localização:** `src/main/resources/messages.properties`

```properties
# Rotacao Validations
rotacao.realm.obrigatorio=Realm é obrigatório
rotacao.motivo.tamanho=Motivo deve ter no máximo 500 caracteres

# Rotacao Messages
rotacao.em.andamento=Rotação já está em andamento para este realm
rotacao.erro.processamento=Erro ao processar rotação de chave
rotacao.sucesso=Rotação de chave concluída com sucesso
```

## Checklist de Validação
- [ ] Package `api/responses` criado
- [ ] Package `api/requests` criado
- [ ] Record `ChaveAtivaResponse` criado com todos os campos
- [ ] Record `RotacaoHistoricoResponse` criado com todos os campos
- [ ] Record `RotacaoRequest` criado com validações
- [ ] Enum `StatusChave` criado (ATIVA, EXPIRADA, EXPIRANDO)
- [ ] Enum `StatusRotacao` criado (CONCLUIDA, EM_ANDAMENTO, FALHOU)
- [ ] Método `from()` implementado em `ChaveAtivaResponse`
- [ ] Método `from()` implementado em `RotacaoHistoricoResponse`
- [ ] Método `calcularDiasRestantes()` implementado
- [ ] Método `getBadgeDiasRestantes()` implementado
- [ ] Método `getBadgeDiasClasse()` implementado
- [ ] Método `getDataFormatada()` implementado em `RotacaoHistoricoResponse`
- [ ] Método `getMotivoFormatado()` implementado em `RotacaoHistoricoResponse`
- [ ] Métodos auxiliares em `RotacaoRequest` implementados
- [ ] Mensagens de validação adicionadas em `messages.properties`

## Anotações
- Records são imutáveis por padrão, garantindo thread-safety
- Dias restantes calculados automaticamente baseados na data de expiração
- Status da chave determinado automaticamente (ATIVA > 7 dias, EXPIRANDO ≤ 7 dias, EXPIRADA < 0 dias)
- Motivo é opcional, mas se informado deve ter no máximo 500 caracteres
- Responsável pode ser "Sistema" para rotações automáticas
- Métodos de fábrica estáticos facilitam a conversão de entidade para DTO
- Badge classes seguem padrão Bootstrap (bg-success, bg-warning, bg-danger)

## Dependências
- Epic 1 (Gestão de Realms) - para associação com realm
- Epic 5 (Gestão de Chaves) - entidades já existem
- Epic 9 (Configuração) - Jakarta Bean Validation configurado

## Prioridade
**Alta** - DTOs necessários para todas as outras histórias

## Estimativa
- Implementação: 2.5 horas
- Testes: 1.5 horas
- Total: 4 horas

## Status do Epic 15 - Story 02

**Status:** Concluído
**Data de Conclusão:** 25 de Dezembro de 2025
**Porcentagem de Conclusão:** 100%

