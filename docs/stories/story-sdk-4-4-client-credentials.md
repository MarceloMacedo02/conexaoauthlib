# Story SDK-4.4: Client Credentials Flow

**Epic:** SDK-4 - Auth Service  
**Story:** SDK-4.4  
**Status:** Planejado  
**Prioridade:** Alta (P1)  
**Estimativa:** 0.2 dia  
**Complexidade:** Baixa

## Descrição

Implementar método `getClientCredentialsToken()` em `ConexaoAuthServiceImpl`.

## Critérios de Aceite

- [ ] Método `getClientCredentialsToken()` implementado
- [ ] Chama `ConexaoAuthClient.clientCredentials()`
- [ ] Usa credenciais de `ConexaoAuthProperties`
- [ ] Trata exceções do Feign (Unauthorized, Server)
- [ ] Logs em Português (não expor clientSecret)

## Requisitos Técnicos

```java
public TokenResponse getClientCredentialsToken() {
    log.info("Solicitando token via Client Credentials Flow");
    ClientCredentialsRequest request = new ClientCredentialsRequest(
        "client_credentials",
        properties.clientId(),
        properties.clientSecret(),
        null  // scope opcional
    );
    try {
        TokenResponse response = conexaoAuthClient.clientCredentials(request);
        log.info("Token obtido com sucesso, expira em {} segundos", response.expiresIn());
        return response;
    } catch (UnauthorizedException e) {
        log.error("Credenciais inválidas: {}", e.getMessage());
        throw e;
    }
}
```

## Pontos de Atenção

1. **Segurança:** Nunca expor clientSecret em logs
2. **Credenciais:** Usar `ConexaoAuthProperties.clientId()` e `clientSecret()`
3. **Logs:** Logs informativos sem dados sensíveis

## Dependências

- SDK-4.1: Auth Service Interface
- SDK-2.1: Feign Client - ConexaoAuthClient
- SDK-1.2: Configuration Properties (clientId, clientSecret)
- SDK-2.5: DTOs de Response (TokenResponse)
