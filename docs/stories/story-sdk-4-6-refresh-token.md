# Story SDK-4.6: Refresh Token

**Epic:** SDK-4 - Auth Service  
**Story:** SDK-4.6  
**Status:** Planejado  
**Prioridade:** Baixa (P2)  
**Estimativa:** 0.1 dia  
**Complexidade:** Baixa

## Descrição

Implementar método `refreshToken()` em `ConexaoAuthServiceImpl`.

## Critérios de Aceite

- [ ] Método `refreshToken()` implementado
- [ ] Chama endpoint de refresh token do Auth Server
- [ ] Trata exceções do Feign (Unauthorized, Server)
- [ ] Logs em Português

## Requisitos Técnicos

```java
public TokenResponse refreshToken(String refreshToken) {
    log.info("Atualizando token via refresh token");
    // Nota: Implementação depende do endpoint específico do Auth Server
    // Este é um exemplo conceitual
    throw new UnsupportedOperationException("Endpoint de refresh token será definido pelo Auth Server");
}
```

## Pontos de Atenção

1. **Endpoint Auth Server:** Implementação depende de endpoint específico
2. **Segurança:** Nunca expor refresh token em logs
3. **Exceções:** Tratar Unauthorized (refresh inválido/expirado)
4. **Logs:** Logs em Português sem dados sensíveis

## Dependências

- SDK-4.1: Auth Service Interface
- SDK-2.5: DTOs de Response (TokenResponse)
