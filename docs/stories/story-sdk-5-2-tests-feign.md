# Story SDK-5.2: Testes Unitários - Feign

**Epic:** SDK-5 - Testes e Documentação  
**Story:** SDK-5.2  
**Status:** Planejado  
**Prioridade:** Alta (P0)  
**Estimativa:** 0.5 dia  
**Complexidade:** Média

## Descrição

Criar testes unitários para Feign Clients.

## Critérios de Aceite

- [ ] Testes para `ConexaoAuthClient` (já criados parcialmente)
- [ ] Testes para `JwksClient` (já criados parcialmente)
- [ ] Cobertura > 80%
- [ ] Testes usando Feign Mock

## Pontos de Atenção

1. **Feign Mock:** Usar `feign.mock.MockClient` para mockar respostas
2. **Cobertura:** Garantir > 80% cobertura
3. **DTOs:** Testar desserialização/serialização de DTOs

## Dependências

- SDK-2.1: Feign Client - ConexaoAuthClient
- SDK-2.2: Feign Client - JwksClient
