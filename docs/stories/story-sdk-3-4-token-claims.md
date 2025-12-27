# Story SDK-3.4: Modelo TokenClaims

**Epic:** SDK-3 - Token Validator e JWKS  
**Story:** SDK-3.4  
**Status:** Planejado  
**Prioridade:** Alta (P0)  
**Estimativa:** 0.2 dia  
**Complexidade:** Baixa

## Descrição

Expandir o DTO `TokenClaims` criado como stub na Story SDK-1.1, convertendo para Java 21 record.

## Critérios de Aceite

- [ ] Record `TokenClaims` expandido
- [ ] Campos: sub, realm, roles, aud, iss, exp, iat
- [ ] Método `isExpired()` implementado
- [ ] Javadoc completo em Português

## Requisitos Técnicos

### Record TokenClaims

**Localização:** `src/main/java/com/plataforma/conexao/auth/starter/model/TokenClaims.java`

**Código:**
```java
public record TokenClaims(
    String sub,        // Subject (user ID)
    String realm,      // Realm ID
    List<String> roles, // Lista de roles
    String aud,        // Audience
    String iss,        // Issuer
    Long exp,          // Expiration timestamp (seconds)
    Long iat           // Issued at timestamp (seconds)
) {
    public boolean isExpired() {
        return System.currentTimeMillis() / 1000 >= exp;
    }
}
```

## Pontos de Atenção

1. **Record Java 21:** Usar record (sem Lombok)
2. **Timestamps:** exp e iat em segundos (Unix timestamp)
3. **Método isExpired():** Verifica se token expirou
4. **Javadoc Completo:** Documentar todos os campos

## Dependências

- SDK-3.2: Token Validator Implementation (usa este DTO)
