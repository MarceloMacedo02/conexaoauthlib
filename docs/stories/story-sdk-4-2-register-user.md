# Story SDK-4.2: Registro de Usuário

**Epic:** SDK-4 - Auth Service  
**Story:** SDK-4.2  
**Status:** Planejado  
**Prioridade:** Alta (P1)  
**Estimativa:** 0.25 dia  
**Complexidade:** Baixa

## Descrição

Implementar método `registerUser()` em `ConexaoAuthServiceImpl`.

## Critérios de Aceite

- [ ] Método `registerUser()` implementado
- [ ] Chama `ConexaoAuthClient.registerUser()`
- [ ] Trata exceções do Feign (Unauthorized, Conflict, Server)
- [ ] Logs em Português

## Requisitos Técnicos

```java
public UserResponse registerUser(RegisterUserRequest request) {
    log.info("Registrando usuário: {}", request.email());
    try {
        return conexaoAuthClient.registerUser(request);
    } catch (UnauthorizedException e) {
        log.error("Credenciais inválidas: {}", e.getMessage());
        throw e;
    } catch (ConflictException e) {
        log.error("Conflito de dados: {}", e.getMessage());
        throw e;
    }
}
```

## Pontos de Atenção

1. **Logs:** Logs em Português (não expor senhas)
2. **Tratamento de Exceções:** Preservar exceções do Feign
3. **Validação Request:** Feign valida request automaticamente

## Dependências

- SDK-4.1: Auth Service Interface
- SDK-2.1: Feign Client - ConexaoAuthClient
- SDK-2.4: DTOs de Request
