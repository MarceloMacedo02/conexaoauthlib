# Story SDK-4.3: Busca de Usuário por Identificador

**Epic:** SDK-4 - Auth Service  
**Story:** SDK-4.3  
**Status:** Planejado  
**Prioridade:** Média (P2)  
**Estimativa:** 0.2 dia  
**Complexidade:** Baixa

## Descrição

Implementar método `findUserByCpf()` em `ConexaoAuthServiceImpl`.

## Critérios de Aceite

- [ ] Método `findUserByCpf()` implementado
- [ ] Chama `ConexaoAuthClient.findUserByCpf()`
- [ ] Trata exceções do Feign (Unauthorized, NotFound, Server)
- [ ] Logs em Português

## Requisitos Técnicos

```java
public UserResponse findUserByCpf(String cpf) {
    log.debug("Buscando usuário por CPF");
    try {
        return conexaoAuthClient.findUserByCpf(cpf);
    } catch (ResourceNotFoundException e) {
        log.error("Usuário não encontrado: {}", e.getMessage());
        throw e;
    }
}
```

## Pontos de Atenção

1. **Tratamento ResourceNotFoundException:** Usuário não encontrado
2. **Logs:** Logs em Português
3. **CPF Validação:** Validar antes de chamar Feign

## Dependências

- SDK-4.1: Auth Service Interface
- SDK-2.1: Feign Client - ConexaoAuthClient
- SDK-2.5: DTOs de Response
