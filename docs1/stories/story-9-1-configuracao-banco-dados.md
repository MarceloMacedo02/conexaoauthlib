# História 9.1: Configuração de Banco de Dados (PostgreSQL)

**Epic:** 9 - Configuração e Infraestrutura
**Status:** Done  
**Prioridade:** Alta  
**Estimativa:** 2 dias  
**Complexidade**: Baixa

---

## Descrição

Como sistema, preciso configurar a conexão com PostgreSQL para que eu possa persistir dados do microserviço.

---

## Critérios de Aceite

- [x] Dependência do PostgreSQL Driver e Spring Data JPA
- [x] Configuração de datasource em application.properties
- [x] Configuração de Hibernate (dialect, DDL)
- [x] Configuração de pool de conexões (HikariCP)
- [x] Script SQL inicial para criação de tabelas (usando JPA/Hibernate)
- [x] Configuração para ambientes diferentes (dev, test, prod)

---

## Regras de Negócio

1. Connection Pool:
   - Usar HikariCP (padrão do Spring Boot)
   - Configurar tamanho máximo de conexões
   - Configurar timeout de conexão

2. Hibernate:
   - Dialect: PostgreSQL
   - DDL: validate (produção) ou update (desenvolvimento)
   - Show SQL: true (desenvolvimento) / false (produção)

3. Migrações:
    - Usar Hibernate DDL auto-configuration (validate/update)
    - Schema gerado automaticamente via JPA entities
    - Bootstrap para dados iniciais

---

## Requisitos Técnicos

### pom.xml
```xml
<dependencies>
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Flyway (opcional) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
</dependencies>
```

### application.properties
```properties
# Configuração de Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/conexaodigital
spring.datasource.username=conexaodigital
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.validate-on-migrate=true
```

### application-dev.properties
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.flyway.enabled=false
```

### application-test.properties
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

---

## Exemplos de Testes

### Teste de Conexão com Banco de Dados
```java
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseConfigTest {
    
    @Autowired
    private DataSource dataSource;
    
    @Test
    void quandoConfiguracaoValida_entaoConexaoSucesso() {
        assertThat(dataSource).isNotNull();
        
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(1)).isTrue();
        }
    }
}
```

---

## Dependências

- Nenhuma (base para todos os epics)

---

## Pontos de Atenção

- Senhas em variáveis de ambiente em produção
- DDL validate em produção (nunca update)
- Checkstyle: Seguir Google Java Style Guide
