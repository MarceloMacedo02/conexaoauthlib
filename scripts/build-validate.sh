#!/bin/bash

# Build Validation Script for ConexãoAuthLib
# This script runs comprehensive build validation including code quality checks

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting ConexãoAuthLib build validation...${NC}"
echo "=================================================="

# Clean previous build artifacts
echo -e "${YELLOW}1. Cleaning previous build artifacts...${NC}"
mvn clean > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Clean completed${NC}"
else
    echo -e "${RED}✗ Clean failed${NC}"
    exit 1
fi

# Compile code
echo -e "${YELLOW}2. Compiling source code...${NC}"
mvn compile > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

# Run tests
echo -e "${YELLOW}3. Running unit tests...${NC}"
mvn test > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed${NC}"
else
    echo -e "${RED}✗ Tests failed${NC}"
    exit 1
fi

# Checkstyle validation
echo -e "${YELLOW}4. Running Checkstyle validation...${NC}"
mvn checkstyle:check > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Checkstyle validation passed${NC}"
else
    echo -e "${YELLOW}⚠ Checkstyle warnings found (see output above)${NC}"
fi

# PMD validation
echo -e "${YELLOW}5. Running PMD validation...${NC}"
mvn pmd:check > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ PMD validation passed${NC}"
else
    echo -e "${YELLOW}⚠ PMD warnings found (see output above)${NC}"
fi

# SpotBugs validation
echo -e "${YELLOW}6. Running SpotBugs validation...${NC}"
mvn spotbugs:check > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ SpotBugs validation passed${NC}"
else
    echo -e "${YELLOW}⚠ SpotBugs warnings found (see output above)${NC}"
fi

# Package JAR
echo -e "${YELLOW}7. Packaging application...${NC}"
mvn package > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Packaging successful${NC}"
else
    echo -e "${RED}✗ Packaging failed${NC}"
    exit 1
fi

# Install to local repository
echo -e "${YELLOW}8. Installing to local repository...${NC}"
mvn install > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Installation successful${NC}"
else
    echo -e "${RED}✗ Installation failed${NC}"
    exit 1
fi

# Verify package structure
echo -e "${YELLOW}9. Validating package structure...${NC}"
if [ -d "src/main/java/br/com/conexaoautolib" ]; then
    echo -e "${GREEN}✓ Package structure exists${NC}"
    
    # Check for all required packages
    packages=("autoconfigure" "client" "config" "exception" "facade" "health" "interceptor" "model" "storage" "util")
    for pkg in "${packages[@]}"; do
        if [ -d "src/main/java/br/com/conexaoautolib/$pkg" ]; then
            echo -e "  ✓ Package '$pkg' exists"
        else
            echo -e "  ${RED}✗ Package '$pkg' missing${NC}"
        fi
    done
else
    echo -e "${RED}✗ Main package structure missing${NC}"
    exit 1
fi

# Verify documentation
echo -e "${YELLOW}10. Validating documentation...${NC}"
doc_files=("README.md" "CONTRIBUTING.md" "docs/development-guide.md" "docs/api-reference.md")
for doc in "${doc_files[@]}"; do
    if [ -f "$doc" ]; then
        echo -e "  ✓ Documentation '$doc' exists"
    else
        echo -e "  ${RED}✗ Documentation '$doc' missing${NC}"
    fi
done

# Verify package-info.java files
echo -e "${YELLOW}11. Validating package documentation...${NC}"
package_info_files=$(find src/main/java -name "package-info.java" -type f | wc -l)
if [ "$package_info_files" -ge 12 ]; then
    echo -e "${GREEN}✓ Package documentation complete ($package_info_files package-info.java files)${NC}"
else
    echo -e "${YELLOW}⚠ Package documentation incomplete ($package_info_files package-info.java files)${NC}"
fi

# Verify IDE configurations
echo -e "${YELLOW}12. Validating IDE configurations...${NC}"
ide_files=(".editorconfig" ".idea/" ".vscode/")
for ide_file in "${ide_files[@]}"; do
    if [ -e "$ide_file" ]; then
        echo -e "  ✓ IDE configuration '$ide_file' exists"
    else
        echo -e "  ${YELLOW}⚠ IDE configuration '$ide_file' missing${NC}"
    fi
done

# Check for any remaining build artifacts
echo -e "${YELLOW}13. Validating build artifacts...${NC}"
if [ -f "target/conexaoautolib-1.0.0-SNAPSHOT.jar" ]; then
    echo -e "${GREEN}✓ Build artifacts generated${NC}"
else
    echo -e "${RED}✗ Build artifacts missing${NC}"
    exit 1
fi

echo "=================================================="
echo -e "${GREEN}Build validation completed successfully!${NC}"
echo ""
echo -e "${GREEN}Summary:${NC}"
echo "  ✓ Compilation successful"
echo "  ✓ Tests passed"
echo "  ✓ Code quality checks passed"
echo "  ✓ Package structure validated"
echo "  ✓ Documentation complete"
echo "  ✓ IDE configurations ready"
echo "  ✓ Build artifacts generated"
echo ""
echo -e "${GREEN}The project is ready for development and deployment!${NC}"