# 🧪 Comprehensive Test Design - Epic 5 Stories 5.5 & 5.6

**Data:** 2025-12-23  
**Designer:** Quinn - Test Architect & Quality Advisor  
**Framework:** JUnit 5 + Mockito + Spring Test + TestContainers

---

## 📋 Test Strategy Overview

### Níveis de Teste
1. **Unit Tests** (60%) - Testes de unidade de serviços e repositórios
2. **Integration Tests** (25%) - Testes de integração com banco de dados
3. **API Tests** (10%) - Testes de endpoints REST
4. **Performance Tests** (5%) - Testes de carga e stress

### Cobertura Alvo
- **Cobertura de Código:** Mínimo 80%
- **Cobertura de Branch:** Mínimo 75%
- **Cobertura de Critérios de Aceite:** 100%

---

## 🎯 Story 5.5 - Listar Chaves Ativas - Test Design

### 1. Unit Tests - ChaveServiceImpl

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ChaveServiceImpl - Método listar()")
class ChaveServiceImplListarTest {

    @Mock private ChaveCriptograficaRepository chaveRepository;
    @Mock private RealmRepository realmRepository;
    @Mock private RotacaoChaveRepository rotacaoRepository;
    @InjectMocks private ChaveServiceImpl chaveService;
    
    private UUID REALM_ID = UUID.randomUUID();
    private Realm realm;
    private List<ChaveCriptografica> chaves;

    @BeforeEach
    void setUp() {
        realm = new Realm("test-realm", StatusRealm.ATIVO);
        realm.setId(REALM_ID);
        
        chaves = Arrays.asList(
            criarChave(StatusChave.ATIVA, LocalDateTime.now().minusDays(10)),
            criarChave(StatusChave.INATIVA, LocalDateTime.now().minusDays(20)),
            criarChave(StatusChave.EXPIRADA, LocalDateTime.now().minusDays(30))
        );
    }

    @Test
    @DisplayName("Dado realm existente e sem filtro, quando listar, então retornar todas chaves ordenadas")
    void listar_comRealmValidoESemFiltro_deveRetornarTodasChavesOrdenadas() {
        // Given
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(chaveRepository.findByRealmIdOrderByDataCriacaoDesc(REALM_ID))
            .thenReturn(chaves);
        when(rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(any(), any()))
            .thenReturn(Optional.empty());

        // When
        List<ChaveResponse> result = chaveService.listar(REALM_ID, null);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).status()).isEqualTo(StatusChave.ATIVA);
        assertThat(result.get(1).status()).isEqualTo(StatusChave.INATIVA);
        assertThat(result.get(2).status()).isEqualTo(StatusChave.EXPIRADA);
        
        // Verificar ordenação descendente
        assertTrue(result.get(0).dataCriacao().isAfter(result.get(1).dataCriacao()));
        assertTrue(result.get(1).dataCriacao().isAfter(result.get(2).dataCriacao()));
        
        verify(realmRepository).findById(REALM_ID);
        verify(chaveRepository).findByRealmIdOrderByDataCriacaoDesc(REALM_ID);
    }

    @Test
    @DisplayName("Dado realm existente com filtro ATIVA, quando listar, então retornar apenas chaves ativas")
    void listar_comFiltroAtivo_deveRetornarApenasChavesAtivas() {
        // Given
        List<ChaveCriptografica> chavesAtivas = Arrays.asList(chaves.get(0));
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(chaveRepository.findAllByRealmIdAndStatus(REALM_ID, StatusChave.ATIVA))
            .thenReturn(chavesAtivas);
        when(rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(any(), any()))
            .thenReturn(Optional.empty());

        // When
        List<ChaveResponse> result = chaveService.listar(REALM_ID, StatusChave.ATIVA);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(StatusChave.ATIVA);
        
        verify(chaveRepository).findAllByRealmIdAndStatus(REALM_ID, StatusChave.ATIVA);
    }

    @Test
    @DisplayName("Dado realm inexistente, quando listar, então lançar RealmNotFoundException")
    void listar_comRealmInexistente_deveLancarExcecao() {
        // Given
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RealmNotFoundException.class, () -> {
            chaveService.listar(REALM_ID, null);
        });
        
        verify(realmRepository).findById(REALM_ID);
        verifyNoInteractions(chaveRepository);
    }

    @Test
    @DisplayName("Dada chave ativa sem rotação anterior, quando listar, então calcular próxima rotação desde criação")
    void listar_comChaveAtivaSemRotacao_deveCalcularProximaRotacaoDesdeCriacao() {
        // Given
        ChaveCriptografica chaveAtiva = criarChave(StatusChave.ATIVA, LocalDateTime.of(2024, 12, 1, 10, 0));
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(chaveRepository.findByRealmIdOrderByDataCriacaoDesc(REALM_ID))
            .thenReturn(Arrays.asList(chaveAtiva));
        when(rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(any(), any()))
            .thenReturn(Optional.empty());

        // When
        List<ChaveResponse> result = chaveService.listar(REALM_ID, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).proximaRotacao()).contains("2025-01-01T00:00");
    }

    @Test
    @DisplayName("Dada chave inativa com rotação automática, quando listar, então calcular próxima rotação desde última rotação")
    void listar_comChaveInativaComRotacao_deveCalcularProximaRotacaoDesdeUltimaRotacao() {
        // Given
        ChaveCriptografica chaveInativa = criarChave(StatusChave.INATIVA, LocalDateTime.of(2024, 11, 1, 10, 0));
        RotacaoChave ultimaRotacao = new RotacaoChave(realm, UUID.randomUUID(), 
            chaveInativa.getId(), TipoRotacao.AUTOMATICA, "system");
        ultimaRotacao.setDataRotacao(LocalDateTime.of(2024, 12, 1, 0, 0));
        
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(chaveRepository.findByRealmIdOrderByDataCriacaoDesc(REALM_ID))
            .thenReturn(Arrays.asList(chaveInativa));
        when(rotacaoRepository.findTopByRealmAndTipoOrderByDataRotacaoDesc(
            realm, TipoRotacao.AUTOMATICA))
            .thenReturn(Optional.of(ultimaRotacao));

        // When
        List<ChaveResponse> result = chaveService.listar(REALM_ID, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).proximaRotacao()).contains("2025-01-01T00:00");
    }

    @Test
    @DisplayName("Dada chave expirada, quando listar, então próxima rotação deve ser nula")
    void listar_comChaveExpirada_deveRetornarProximaRotacaoNula() {
        // Given
        ChaveCriptografica chaveExpirada = criarChave(StatusChave.EXPIRADA, LocalDateTime.now().minusMonths(2));
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(chaveRepository.findByRealmIdOrderByDataCriacaoDesc(REALM_ID))
            .thenReturn(Arrays.asList(chaveExpirada));

        // When
        List<ChaveResponse> result = chaveService.listar(REALM_ID, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).proximaRotacao()).isNull();
    }

    private ChaveCriptografica criarChave(StatusChave status, LocalDateTime dataCriacao) {
        ChaveCriptografica chave = new ChaveCriptografica(
            UUID.randomUUID(), realm, "public-key", "encrypted-private", status);
        chave.setDataCriacao(dataCriacao);
        return chave;
    }
}
```

### 2. Integration Tests - ChaveController

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@DisplayName("Testes de Integração - ChaveController - Endpoint Listar")
class ChaveControllerListarTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ChaveService chaveService;
    @Autowired private RealmRepository realmRepository;
    
    private Realm realm;
    private String baseUrl;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        baseUrl = "/api/v1/chaves";
        realm = realmRepository.save(new Realm("integration-test", StatusRealm.ATIVO));
    }

    @Test
    @DisplayName("GET /{realmId} com realm válido deve retornar 200 e lista de chaves")
    void listarEndpoint_comRealmValido_deveRetornar200() {
        // Given
        chaveService.gerarChave(realm.getId());
        chaveService.gerarChave(realm.getId());
        
        String url = baseUrl + "/" + realm.getId();

        // When
        ResponseEntity<ChaveResponse[]> response = restTemplate.getForEntity(url, ChaveResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSize(2);
        
        // Verificar que chave privada não está presente
        Arrays.stream(response.getBody()).forEach(chave -> {
            assertThat(chave.publicKey()).isNotBlank();
            // Verificar indiretamente que não há campo privateKey
            assertThat(chave.toString()).doesNotContain("privateKey");
        });
    }

    @Test
    @DisplayName("GET /{realmId}?status=ATIVA deve filtrar por status")
    void listarEndpoint_comFiltroStatus_deveRetornarFiltrado() {
        // Given
        ChaveResponse chave1 = chaveService.gerarChave(realm.getId());
        // Rotacionar para tornar a primeira chave inativa
        chaveService.rotacionar(realm.getId(), TipoRotacao.MANUAL);
        
        String url = baseUrl + "/" + realm.getId() + "?status=ATIVA";

        // When
        ResponseEntity<ChaveResponse[]> response = restTemplate.getForEntity(url, ChaveResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].status()).isEqualTo(StatusChave.ATIVA);
        assertThat(response.getBody()[0].id()).isNotEqualTo(chave1.id());
    }

    @Test
    @DisplayName("GET /{realmId} com realm inexistente deve retornar 404")
    void listarEndpoint_comRealmInexistente_deveRetornar404() {
        // Given
        UUID realmInexistente = UUID.randomUUID();
        String url = baseUrl + "/" + realmInexistente;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
```

---

## 🎯 Story 5.6 - Histórico de Rotações - Test Design

### 1. Unit Tests - RotacaoChaveServiceImpl

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RotacaoChaveServiceImpl - Método historico()")
class RotacaoChaveServiceImplHistoricoTest {

    @Mock private RotacaoChaveRepository rotacaoRepository;
    @Mock private RealmRepository realmRepository;
    @InjectMocks private RotacaoChaveServiceImpl rotacaoService;
    
    private UUID REALM_ID = UUID.randomUUID();
    private Realm realm;
    private List<RotacaoChave> rotacoes;

    @BeforeEach
    void setUp() {
        realm = new Realm("test-realm", StatusRealm.ATIVO);
        realm.setId(REALM_ID);
        
        rotacoes = Arrays.asList(
            criarRotacao(TipoRotacao.MANUAL, LocalDateTime.now().minusDays(10)),
            criarRotacao(TipoRotacao.AUTOMATICA, LocalDateTime.now().minusDays(20)),
            criarRotacao(TipoRotacao.MANUAL, LocalDateTime.now().minusDays(30))
        );
    }

    @Test
    @DisplayName("Dado realm existente sem filtros, quando buscar histórico, então retornar todas rotações ordenadas")
    void historico_comRealmValidoSemFiltros_deveRetornarTodasRotacoesOrdenadas() {
        // Given
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(REALM_ID))
            .thenReturn(rotacoes);

        // When
        List<RotacaoChaveResponse> result = rotacaoService.historico(REALM_ID, null, null, null);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).tipo()).isEqualTo(TipoRotacao.MANUAL); // Mais recente
        assertThat(result.get(1).tipo()).isEqualTo(TipoRotacao.AUTOMATICA);
        assertThat(result.get(2).tipo()).isEqualTo(TipoRotacao.MANUAL);
        
        // Verificar ordenação descendente
        assertTrue(result.get(0).dataRotacao().isAfter(result.get(1).dataRotacao()));
        assertTrue(result.get(1).dataRotacao().isAfter(result.get(2).dataRotacao()));
    }

    @Test
    @DisplayName("Dado filtro por tipo MANUAL, quando buscar histórico, então retornar apenas rotações manuais")
    void historico_comFiltroTipoManual_deveRetornarApenasRotaçõesManuais() {
        // Given
        List<RotacaoChave> rotacoesManuais = Arrays.asList(rotacoes.get(0), rotacoes.get(2));
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(REALM_ID))
            .thenReturn(rotacoes);

        // When
        List<RotacaoChaveResponse> result = rotacaoService.historico(
            REALM_ID, TipoRotacao.MANUAL, null, null);

        // Then
        assertThat(result).hasSize(2);
        result.forEach(r -> assertThat(r.tipo()).isEqualTo(TipoRotacao.MANUAL));
    }

    @Test
    @DisplayName("Dado filtro por período, quando buscar histórico, então retornar rotações no período")
    void historico_comFiltroPeriodo_deveRetornarRotaçõesNoPeriodo() {
        // Given
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(25);
        LocalDateTime dataFim = LocalDateTime.now().minusDays(5);
        
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(REALM_ID))
            .thenReturn(rotacoes);

        // When
        List<RotacaoChaveResponse> result = rotacaoService.historico(
            REALM_ID, null, dataInicio, dataFim);

        // Then
        assertThat(result).hasSize(2); // rotações dos dias 10 e 20
        result.forEach(r -> {
            assertTrue(r.dataRotacao().isAfter(dataInicio));
            assertTrue(r.dataRotacao().isBefore(dataFim));
        });
    }

    @Test
    @DisplayName("Dado filtros combinados, quando buscar histórico, então aplicar todos os filtros")
    void historico_comFiltrosCombinados_deveRetornarRotaçõesFiltradas() {
        // Given
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(35);
        LocalDateTime dataFim = LocalDateTime.now().minusDays(25);
        
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(REALM_ID))
            .thenReturn(rotacoes);

        // When
        List<RotacaoChaveResponse> result = rotacaoService.historico(
            REALM_ID, TipoRotacao.MANUAL, dataInicio, dataFim);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).tipo()).isEqualTo(TipoRotacao.MANUAL);
        assertTrue(result.get(0).dataRotacao().isAfter(dataInicio));
        assertTrue(result.get(0).dataRotacao().isBefore(dataFim));
    }

    @Test
    @DisplayName("Dado realm inexistente, quando buscar histórico, então lançar RealmNotFoundException")
    void historico_comRealmInexistente_deveLancarExcecao() {
        // Given
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RealmNotFoundException.class, () -> {
            rotacaoService.historico(REALM_ID, null, null, null);
        });
        
        verify(realmRepository).findById(REALM_ID);
        verifyNoInteractions(rotacaoRepository);
    }

    @Test
    @DisplayName("Dado intervalo de datas inválido, quando buscar histórico, então tratar como filtro vazio")
    void historico_comIntervaloInvalido_deveTratarComoVazio() {
        // Given
        LocalDateTime dataInicio = LocalDateTime.now();
        LocalDateTime dataFim = dataInicio.minusDays(10); // Invertido
        
        when(realmRepository.findById(REALM_ID)).thenReturn(Optional.of(realm));
        when(rotacaoRepository.findByRealmIdOrderByDataRotacaoDesc(REALM_ID))
            .thenReturn(rotacoes);

        // When
        List<RotacaoChaveResponse> result = rotacaoService.historico(
            REALM_ID, null, dataInicio, dataFim);

        // Then
        assertThat(result).isEmpty(); // Nenhuma rotação satisfaz intervalo invertido
    }

    private RotacaoChave criarRotacao(TipoRotacao tipo, LocalDateTime dataRotacao) {
        RotacaoChave rotacao = new RotacaoChave(
            realm, UUID.randomUUID(), UUID.randomUUID(), tipo, "test-user");
        rotacao.setDataRotacao(dataRotacao);
        return rotacao;
    }
}
```

### 2. Integration Tests - ChaveController (Histórico)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@DisplayName("Testes de Integração - ChaveController - Endpoint Histórico")
class ChaveControllerHistoricoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private RotacaoChaveService rotacaoService;
    @Autowired private ChaveService chaveService;
    @Autowired private RealmRepository realmRepository;
    
    private Realm realm;
    private String baseUrl;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        baseUrl = "/api/v1/chaves";
        realm = realmRepository.save(new Realm("integration-test", StatusRealm.ATIVO));
    }

    @Test
    @DisplayName("GET /{realmId}/historico com rotações deve retornar 200 e histórico ordenado")
    void historicoEndpoint_comRotações_deveRetornar200() {
        // Given
        chaveService.gerarChave(realm.getId());
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.MANUAL);
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.AUTOMATICA);
        
        String url = baseUrl + "/" + realm.getId() + "/historico";

        // When
        ResponseEntity<RotacaoChaveResponse[]> response = restTemplate.getForEntity(
            url, RotacaoChaveResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        
        // Verificar ordenação descendente
        assertThat(response.getBody()[0].tipo()).isEqualTo(TipoRotacao.AUTOMATICA);
        assertThat(response.getBody()[1].tipo()).isEqualTo(TipoRotacao.MANUAL);
    }

    @Test
    @DisplayName("GET /{realmId}/historico?tipo=MANUAL deve filtrar por tipo")
    void historicoEndpoint_comFiltroTipo_deveRetornarFiltrado() {
        // Given
        chaveService.gerarChave(realm.getId());
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.MANUAL);
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.AUTOMATICA);
        
        String url = baseUrl + "/" + realm.getId() + "/historico?tipo=MANUAL";

        // When
        ResponseEntity<RotacaoChaveResponse[]> response = restTemplate.getForEntity(
            url, RotacaoChaveResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].tipo()).isEqualTo(TipoRotacao.MANUAL);
    }

    @Test
    @DisplayName("GET /{realmId}/historico com filtro de data deve funcionar")
    void historicoEndpoint_comFiltroData_deveRetornarNoPeriodo() {
        // Given
        LocalDateTime antes = LocalDateTime.now().minusHours(2);
        chaveService.gerarChave(realm.getId());
        
        // Aguardar um pouco para garantir diferença de tempo
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        LocalDateTime meio = LocalDateTime.now();
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.MANUAL);
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        LocalDateTime depois = LocalDateTime.now();
        rotacaoService.rotacionar(realm.getId(), TipoRotacao.AUTOMATICA);
        
        String url = baseUrl + "/" + realm.getId() + "/historico" +
            "?dataInicio=" + meio.format(DateTimeFormatter.ISO_DATE_TIME) +
            "&dataFim=" + depois.format(DateTimeFormatter.ISO_DATE_TIME);

        // When
        ResponseEntity<RotacaoChaveResponse[]> response = restTemplate.getForEntity(
            url, RotacaoChaveResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1); // Apenas a rotação AUTOMATICA
        assertThat(response.getBody()[0].tipo()).isEqualTo(TipoRotacao.AUTOMATICA);
    }
}
```

---

## ⚡ Performance Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Testes de Performance - Listagem e Histórico")
class PerformanceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("perftestdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ChaveService chaveService;
    @Autowired private RotacaoChaveService rotacaoService;
    @Autowired private RealmRepository realmRepository;
    
    private Realm realmGrande;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "/api/v1/chaves";
        realmGrande = realmRepository.save(new Realm("perf-test", StatusRealm.ATIVO));
        
        // Criar volume significativo de dados para testes
        for (int i = 0; i < 100; i++) {
            chaveService.gerarChave(realmGrande.getId());
            if (i % 5 == 0) {
                rotacaoService.rotacionar(realmGrande.getId(), TipoRotacao.MANUAL);
            }
        }
    }

    @Test
    @DisplayName("Listar chaves deve responder em menos de 500ms com 100 registros")
    void listarChaves_deveResponderRapidamente() {
        // Given
        String url = baseUrl + "/" + realmGrande.getId();

        // When
        long startTime = System.currentTimeMillis();
        ResponseEntity<ChaveResponse[]> response = restTemplate.getForEntity(url, ChaveResponse[].class);
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(endTime - startTime).isLessThan(500); // < 500ms
    }

    @Test
    @DisplayName("Histórico deve responder em menos de 1 segundo com 20 rotações")
    void historico_deveResponderRapidamente() {
        // Given
        String url = baseUrl + "/" + realmGrande.getId() + "/historico";

        // When
        long startTime = System.currentTimeMillis();
        ResponseEntity<RotacaoChaveResponse[]> response = restTemplate.getForEntity(
            url, RotacaoChaveResponse[].class);
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(endTime - startTime).isLessThan(1000); // < 1s
    }
}
```

---

## 📊 Test Data Factory

```java
@Component
public class ChaveTestDataFactory {

    public static Realm criarRealm(String nome) {
        Realm realm = new Realm(nome, StatusRealm.ATIVO);
        realm.setId(UUID.randomUUID());
        return realm;
    }

    public static ChaveCriptografica criarChave(Realm realm, StatusChave status, 
                                              LocalDateTime dataCriacao) {
        ChaveCriptografica chave = new ChaveCriptografica(
            UUID.randomUUID(), realm, 
            "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...\n-----END PUBLIC KEY-----",
            "encrypted_private_key_" + UUID.randomUUID(),
            status
        );
        chave.setDataCriacao(dataCriacao);
        return chave;
    }

    public static RotacaoChave criarRotacao(Realm realm, TipoRotacao tipo, 
                                          LocalDateTime dataRotacao) {
        RotacaoChave rotacao = new RotacaoChave(
            realm, UUID.randomUUID(), UUID.randomUUID(), tipo, "test-user"
        );
        rotacao.setDataRotacao(dataRotacao);
        return rotacao;
    }

    public static List<ChaveCriptografica> criarMultiplasChaves(Realm realm, int quantidade) {
        return IntStream.range(0, quantidade)
            .mapToObj(i -> criarChave(realm, StatusChave.ATIVA, 
                LocalDateTime.now().minusDays(i)))
            .collect(Collectors.toList());
    }
}
```

---

## 🎯 Test Execution Strategy

### 1. Pipeline Integration
```yaml
# .github/workflows/test.yml
name: Test Pipeline
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Unit Tests
        run: ./mvnw test
      
      - name: Run Integration Tests
        run: ./mvnw verify -P integration-tests
      
      - name: Generate Test Report
        run: ./mvnw jacoco:report
      
      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
```

### 2. Coverage Requirements
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 📋 Test Checklist

### Antes do Merge:
- [ ] Todos os unit tests passando
- [ ] Coverage ≥ 80% (instrução) e ≥ 75% (branch)
- [ ] Integration tests com banco de dados
- [ ] Performance tests validando tempos de resposta
- [ ] Security tests verificando não exposição de dados

### Antes do Deploy:
- [ ] Testes em ambiente de staging
- [ ] Load tests com volume de produção
- [ ] Testes de failover e recuperação
- [ ] Validação de logs e métricas
- [ ] Documentação de operação atualizada

Este design de testes deve garantir qualidade e confiança nas funcionalidades implementadas, cobrindo todos os aspectos críticos de segurança, performance e funcionalidade.