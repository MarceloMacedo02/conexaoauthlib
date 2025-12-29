# ConexãoAuthLib Development Commands

## Build and Test Commands
```bash
# Clean compile and run all tests
mvn clean test

# Clean compile with full validation (Checkstyle, SpotBugs, PMD)
mvn clean verify

# Package JAR with sources and javadoc
mvn clean package

# Install to local repository for testing
mvn clean install

# Skip tests during development (use sparingly)
mvn clean compile -DskipTests
```

## Code Quality Commands
```bash
# Run Checkstyle validation only
mvn checkstyle:check

# Run SpotBugs static analysis
mvn spotbugs:check

# Run PMD code analysis
mvn pmd:check

# Run all quality checks
mvn clean verify
```

## Git Commands (Windows)
```bash
# Check current status
git status

# Stage all changes
git add .

# Stage specific files
git add "src/main/java/br/com/conexaoautolib/model/request/TokenRequest.java"

# Commit changes
git commit -m "feat: implement TokenRequest DTO with validation"

# Push to remote
git push origin main

# Create new branch
git checkout -b feature/token-client-api

# Switch branches
git checkout main
```

## File Operations (Windows)
```bash
# List directory contents
dir

# List files recursively
dir /s

# Find files by pattern
dir /s *.java

# Create directory
mkdir src\test\java\br\com\conexaoautolib\exception

# Remove directory (recursive)
rmdir /s /q target
```

## Development Workflow Commands
```bash
# Run specific test class
mvn test -Dtest=TokenRequestTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Generate test coverage report
mvn jacoco:report

# Update Maven dependencies
mvn versions:display-dependency-updates

# Check for newer versions of parent
mvn versions:display-parent-updates
```

## IDE Commands
```bash
# Refresh Maven project (if using IDE)
mvn dependency:resolve

# Generate IDE project files
mvn eclipse:eclipse    # For Eclipse
mvn idea:idea          # For IntelliJ

# Clean project and rebuild
mvn clean compile
```

## Troubleshooting Commands
```bash
# Clean Maven repository (corrupted dependencies)
rmdir /s /q %USERPROFILE%\.m2\repository

# Force update SNAPSHOT dependencies
mvn clean install -U

# Compile with debug information
mvn compile -X

# Check Maven version
mvn --version

# Check Java version
java -version
```

## Spring Boot Specific Commands
```bash
# Run Spring Boot application (if applicable)
mvn spring-boot:run

# Build executable JAR
mvn clean package spring-boot:repackage

# Check Spring Boot version
mvn dependency:tree | grep spring-boot
```

## Quality Gates
Before committing code, ensure:
```bash
# All tests pass
mvn test

# Code quality checks pass
mvn verify

# No compilation errors
mvn compile

# Coverage meets requirements (>80%)
mvn jacoco:report
```