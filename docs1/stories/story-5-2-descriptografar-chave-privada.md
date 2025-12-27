# História 5.2: Descriptografar Chave Privada

**Epic:** 5 - Gestão de Chaves Criptográficas  
**Status:** Ready for Review  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Média

---

## Descrição

Como sistema, preciso descriptografar chaves privadas armazenadas no banco para que eu possa usá-las para assinar tokens JWT.

---

## Critérios de Aceite

- [x] Descriptografar chave privada com AES-128
- [x] Retornar objeto `PrivateKey`
- [x] Validar integridade da chave descriptografada
- [x] Lançar exceção se descriptografia falhar
- [x] Retornar `200 OK` com chave privada descriptografada
- [x] Retornar `404 Not Found` se chave não existir
- [x] Documentação Swagger em português

---

## Regras de Negócio

1. Descriptografia:
   - Chave privada criptografada com AES-128
   - Segredo AES obtido de variável de ambiente
   - Apenas chaves do mesmo realm podem ser descriptografadas (controle de acesso)

2. Validação:
   - Chave deve ser válida (formato PKCS#8)
   - Algoritmo deve ser RSA com 2048 bits

3. Segurança:
   - Chave privada nunca deve ser exposta via API
   - Apenas usada internamente para assinatura

---

## Requisitos Técnicos

### Chave Privada Descriptografia Service
```java
@Component
@RequiredArgsConstructor
public class ChavePrivadaDescriptografiaService {
    
    private final ChaveCriptograficaRepository repository;
    private final AesCriptografiaService aesService;
    
    public PrivateKey descriptografar(UUID chaveId) {
        ChaveCriptografica chave = repository.findById(chaveId)
            .orElseThrow(() -> new ChaveNotFoundException(chaveId));
        
        String privateKeyEncrypted = chave.getPrivateKey();
        String privateKeyPem = aesService.decrypt(privateKeyEncrypted);
        
        try {
            return converterPrivateKey(privateKeyPem);
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao converter chave privada", e);
        }
    }
    
    private PrivateKey converterPrivateKey(String pem) throws Exception {
        String privateKeyPEM = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        
        return keyFactory.generatePrivate(keySpec);
    }
}
```

### RSA Key Converter
```java
@Component
public class RsaKeyConverter {
    
    public PrivateKey stringToPrivateKey(String privateKeyPem) throws Exception {
        String cleanPem = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(cleanPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        
        return keyFactory.generatePrivate(keySpec);
    }
    
    public PublicKey stringToPublicKey(String publicKeyPem) throws Exception {
        String cleanPem = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(cleanPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        
        return keyFactory.generatePublic(keySpec);
    }
}
```

---

## Exemplos de Testes

### Teste de Sucesso
```java
@SpringBootTest
public class DescriptografarChaveTest {
    
    @Autowired
    private ChavePrivadaDescriptografiaService descriptografiaService;
    
    @Test
    void dadoChaveCriptografada_quandoDescriptografar_entaoRetornaPrivateKey() {
        Realm realm = realmRepository.save(new Realm("empresa-a", StatusRealm.ATIVO));
        ChaveCriptografica chave = chaveService.gerarChaveEntity(realm.getId());
        
        PrivateKey privateKey = descriptografiaService.descriptografar(chave.getId());
        
        assertThat(privateKey).isNotNull();
        assertThat(privateKey.getAlgorithm()).isEqualTo("RSA");
        assertThat(privateKey.getEncoded()).hasSize(256);
    }
}
```

### Teste de Chave Inexistente
```java
@Test
void dadoChaveInexistente_quandoDescriptografar_entaoLancaExcecao() {
    UUID idInexistente = UUID.randomUUID();
    
    assertThatThrownBy(() -> descriptografiaService.descriptografar(idInexistente))
        .isInstanceOf(ChaveNotFoundException.class);
}
```

---

## Dependências

- História 5.1: Gerar Par de Chaves RSA por Realm
- Epic 1: Gestão de Realms

---

## Pontos de Atenção

- Chave privada nunca deve ser exposta via API
- Validar formato da chave descriptografada
- Checkstyle: Seguir Google Java Style Guide

---

## Dev Agent Record

### Agent Model Used
GPT-4 Turbo (OpenAI)

### Debug Log References
- N/A (implementation completed successfully)

### Completion Notes
- Implementada descriptografia de chave privada com AES-128
- Validação de integridade da chave descriptografada
- Retorno de objeto PrivateKey para uso interno
- API interna para operações criptográficas
- Testes unitários para validação de criptografia
- Tratamento de exceções específicas
- Endpoint de validação de integridade

### File List
- src/main/java/br/com/plataforma/conexaodigital/chave/api/controller/ChaveInternalController.java
- src/test/java/br/com/plataforma/conexaodigital/chave/api/controller/ChaveInternalControllerTest.java

### Change Log
- Created: Controller interno para operações de descriptografia
- Created: Endpoint POST /api/v1/chaves/internal/{chaveId}/descriptografar
- Created: Endpoint POST /api/v1/chaves/internal/{chaveId}/validar
- Created: DTOs específicos para respostas internas
- Created: Validação de integridade de chaves descriptografadas
- Created: Testes para controllers internos
- Updated: ChaveService com métodos de descriptografia e validação

### Status
Ready for Review
