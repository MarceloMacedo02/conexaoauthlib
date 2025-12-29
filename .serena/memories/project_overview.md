# ConexãoAuthLib Project Overview

## Purpose
ConexãoAuthLib is a Spring Boot starter library that provides seamless integration with ConexãoAuth Authorization Server. It offers fluent APIs for OAuth2 token management and user operations with automatic configuration and comprehensive error handling.

## Tech Stack
- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.2+
- **HTTP Client**: Spring Cloud OpenFeign 4.x
- **Serialization**: Jackson 2.15+ with Jakarta Time support
- **Validation**: Jakarta Validation 3.x
- **Code Generation**: Lombok 1.18+
- **Metrics**: Micrometer 1.11+
- **Testing**: JUnit 5.10+, Mockito 5.x, WireMock 3.x
- **Build**: Maven 3.9+
- **Quality**: Checkstyle, SpotBugs, PMD

## Key Features
- 🚀 Zero Configuration via Spring Boot autoconfiguration
- 🔄 Automatic Token Management with caching
- 🎯 Fluent APIs with method chaining
- 🛡️ Enterprise Ready with retry and circuit breaker
- 📊 Health Monitoring via Spring Boot Actuator
- 🧪 Thoroughly Tested with high coverage

## Project Structure
```
conexaoautolib/
├── src/main/java/br/com/conexaoautolib/
│   ├── autoconfigure/          # Spring Boot autoconfiguration
│   ├── client/               # Feign HTTP clients  
│   ├── config/               # Feign configuration and error handling
│   ├── exception/            # Domain exception hierarchy
│   ├── facade/              # Public fluent APIs (TokenClient, UsuarioClient)
│   ├── health/               # Health indicators and metrics
│   ├── interceptor/          # Feign interceptors
│   ├── model/               # DTOs (request/response)
│   ├── storage/              # Token storage abstraction
│   └── util/                # Utility classes
└── src/test/java/           # Comprehensive test suite
```

## Backoffice Integration
The library integrates with ConexãoAuth server endpoints defined in backoffice.txt:
- OAuth2 token endpoints
- User management endpoints  
- Realm management
- Key rotation
- Audit logging

## Current Status
Epic 1 (Foundation) is complete. Now implementing Epic 2: Token Management Core with stories 2.1-2.5:
- 2.1: Token Request/Response DTOs ✅ (Done)
- 2.2: OAuth2 Client Implementation (In Progress)
- 2.3: TokenClient Fluent API (Draft)
- 2.4: Domain Exception Hierarchy (Draft)  
- 2.5: Token Storage Implementation (Draft)