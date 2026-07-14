# Custom Instructions, MCP Servers & Automation Guide

This document describes the custom development instructions, Model Context Protocol (MCP) servers, and automated workflows configured for the doggy project.

## Table of Contents

1. [Custom Instructions](#custom-instructions)
2. [MCP Servers](#mcp-servers)
3. [GitHub Actions Workflows](#github-actions-workflows)
4. [Setup & Usage](#setup--usage)
5. [Pull Request Automation](#pull-request-automation)

---

## Custom Instructions

Three comprehensive instruction sets guide development practices and code quality standards:

### 1. Code Quality & SOLID Principles
**File**: `.github/instructions/code-quality-standards.md`

**Scope**: All Java source files under `src/main/java`

**Key Topics**:
- **SOLID Principles**: Detailed guidance on Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion principles
- **Naming Conventions**: Classes (PascalCase), methods (camelCase), constants (UPPER_SNAKE_CASE)
- **Lombok Usage**: Best practices for `@Value`, `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- **Code Organization**: Import grouping, method extraction, cyclomatic complexity limits
- **Quality Metrics**: Coverage targets, code smell detection

**Applied To**:
- Controllers (HTTP request handling)
- Services (business logic)
- Repositories (data access)
- DTOs (data transfer)
- Utilities (helper functions)

---

### 2. Testing Standards & Coverage
**File**: `.github/instructions/testing-standards.md`

**Scope**: All test files under `src/test/java`

**Key Topics**:
- **Test Framework**: JUnit 5, Mockito, AssertJ
- **Test Structure**: Given-When-Then pattern with clear sections
- **Naming Convention**: `should_` prefix for descriptive test names
- **Mock Strategy**: When to use mocks vs. real objects
- **Verification**: Using `verify()`, `verifyNoMoreInteractions()`, and `InOrder`
- **Coverage Requirements**: Minimum 80% line coverage, tracking by module
- **Integration Testing**: Spring Boot Test setup and database testing
- **Acceptance Testing**: Gherkin features and RestAssured tests

**Targets**:
- Unit test coverage: 80%+
- Service layer: 82%
- Repository layer: 85%
- Controller layer: 70%+
- Util layer: 90%+

---

### 3. Architecture & Module Organization
**File**: `.github/instructions/architecture-standards.md`

**Scope**: Project structure, package organization, layer responsibilities

**Key Topics**:
- **Hexagonal Architecture**: Ports & Adapters pattern
- **Layer Responsibilities**:
  - Configuration: Spring beans and security setup
  - Web (Controllers): HTTP request/response handling
  - Service: Business logic and transactions
  - Repository: Data persistence
  - Model: Domain entities and value objects
  - DTO: API request/response structures
  - Util: Helper functions
- **Dependency Flow**: Controllers → Services → Repositories
- **Constructor Injection**: Using `@RequiredArgsConstructor` and `@NonNull`
- **Transaction Boundaries**: `@Transactional` at service layer
- **Exception Handling**: Custom domain exceptions and global handlers

**Project Structure**:
```
doggy/
├── src/main/java/com/example/
│   ├── configuration/        # Spring beans, security
│   ├── controller/           # REST endpoints
│   ├── dto/                  # Request/response DTOs
│   ├── service/              # Business logic
│   ├── model/                # Domain entities
│   ├── repository/           # Data access
│   └── util/                 # Utilities
└── src/test/java/           # Mirror structure for tests
```

---

## MCP Servers

Three Model Context Protocol servers automate code analysis, build operations, and GitHub management:

### 1. GitHub API Server
**File**: `.github/mcp-servers/github-server.js`

**Configuration**: Defined in `.github/mcp-config.json`

**Tools**:
- `create_pull_request`: Create new PRs with title, body, branches, labels
- `get_pull_request`: Retrieve specific PR details
- `update_pull_request`: Update PR state, title, body
- `list_pull_requests`: List PRs with optional filters
- `add_pr_comment`: Add comments to PRs

**Environment Variables**:
```bash
GITHUB_TOKEN=<your-github-token>
GITHUB_OWNER=Gabor_Veres
GITHUB_REPO=doggy
```

**Example Usage**:
```bash
# Create a PR for code quality improvements
node github-server.js
```

---

### 2. Code Analysis Server
**File**: `.github/mcp-servers/code-analysis-server.js`

**Configuration**: Defined in `.github/mcp-config.json`

**Tools**:
- `analyze_code_quality`: Check code for SOLID violations, naming, imports, complexity
- `check_test_coverage`: Verify test coverage against minimum thresholds
- `lint_java_file`: Run CheckStyle and linting
- `detect_code_smells`: Find god classes, long methods, dead code
- `generate_quality_report`: Create comprehensive quality reports

**Analysis Types**:
- **imports**: Verify import organization
- **naming**: Check naming conventions
- **solid**: Validate SOLID principles
- **complexity**: Monitor cyclomatic complexity
- **all**: Complete analysis

**Environment Variables**:
```bash
PROJECT_ROOT=/path/to/doggy
SONARQUBE_HOST=<optional-sonarqube-host>
SONARQUBE_TOKEN=<optional-sonarqube-token>
```

---

### 3. Maven Build Server
**File**: `.github/mcp-servers/maven-build-server.js`

**Configuration**: Defined in `.github/mcp-config.json`

**Tools**:
- `run_tests`: Execute unit tests (optional specific test class/method)
- `run_integration_tests`: Run Spring Boot integration tests
- `build_project`: Build with Maven (with/without tests)
- `check_dependencies`: Check for vulnerabilities and updates
- `generate_coverage_report`: Create JaCoCo coverage reports
- `compile_project`: Compile without running tests

**Supported Options**:
- **Profile support**: Maven profiles for different configurations
- **Test filtering**: Run specific test classes or methods
- **Vulnerability scanning**: Check for CVEs in dependencies
- **Coverage analysis**: Track line, branch, and complexity metrics

**Environment Variables**:
```bash
PROJECT_ROOT=/path/to/doggy
MAVEN_HOME=/path/to/maven
JAVA_HOME=/path/to/java
```

---

## GitHub Actions Workflows

Four automated workflows orchestrate PR creation and quality checks:

### 1. Code Quality Analysis (code-quality.yml)

**Triggers**:
- Push to main/develop
- Pull requests with Java file changes

**Jobs**:
1. **quality-check**: CheckStyle, naming conventions, import organization
2. **test-coverage**: JaCoCo coverage report with Codecov integration
3. **PR Comments**: Posts quality results to pull requests

**Output**: PR comments with quality metrics and recommendations

---

### 2. Build & Test Pipeline (build-and-test.yml)

**Triggers**:
- Push to main/develop/feature branches
- Pull requests

**Jobs**:
1. **build**: Compile and package project
2. **security-checks**: OWASP dependency check
3. **docker-build**: Build Docker image (main branch only)
4. **generate-pr**: Auto-create PRs from feature branches

**Artifacts**:
- JAR files
- Test reports (Surefire)
- SARIF security reports
- Coverage reports

**Auto-PR Feature**:
- Detects branch type (feature/bugfix/docs)
- Generates appropriate PR title and body
- Applies correct labels
- Posts build summary comments

---

### 3. Dependency Management & Auto PRs (dependency-management.yml)

**Triggers**:
- Daily schedule (2 AM UTC)
- Manual dispatch

**Jobs**:
1. **check-dependencies**: Scan for updates and vulnerabilities
2. **create-dependency-update-pr**: Auto-create PR for minor/patch updates
3. **create-security-fix-pr**: Auto-create PR for security vulnerabilities
4. **auto-merge-docs-updates**: Update documentation guidelines

**Auto-Generated PRs**:
- Automated labels and assignments
- Draft vs. ready-for-review status
- Detailed descriptions with verification status
- Custom instructions applied in PR body

---

### 4. Auto-merge & Deployment (Future)

Configured to automatically merge and deploy PRs based on:
- Build status
- Code review approvals
- Test coverage thresholds
- Security scan results

---

## Setup & Usage

### 1. Install Copilot Instructions

Run the provided PowerShell script to install custom instructions locally:

```powershell
./.github/scripts/install-copilot-instructions.ps1
```

This copies `.github/instructions/copilot-instructions.md` to:
```
C:\Users\<YourUsername>\AppData\Local\github-copilot\intellij\copilot-instructions.md
```

After running, restart your JetBrains IDE.

### 2. Configure MCP Servers Locally

**Option A: Manual Setup**

1. Review the MCP configuration: `.github/mcp-config.json`
2. Set environment variables for each server:
   ```bash
   export PROJECT_ROOT=/path/to/doggy
   export GITHUB_TOKEN=your-token
   export SONARQUBE_HOST=https://sonarqube.example.com
   ```
3. Start servers as background processes or integrate with your IDE

**Option B: IDE Integration**

If using Claude in VSCode or similar, configure in `.vscode/settings.json`:
```json
{
  "claude.mcpServers": {
    "github": {
      "command": "node",
      "args": [".github/mcp-servers/github-server.js"]
    },
    "code-analysis": {
      "command": "node",
      "args": [".github/mcp-servers/code-analysis-server.js"]
    },
    "maven-build": {
      "command": "node",
      "args": [".github/mcp-servers/maven-build-server.js"]
    }
  }
}
```

### 3. Enable GitHub Actions

Ensure GitHub Actions are enabled in your repository settings:
1. Go to Settings → Actions
2. Select "Allow all actions and reusable workflows"
3. Workflows will trigger automatically on push/PR

---

## Pull Request Automation

### Automatic PR Creation Scenarios

#### Scenario 1: Feature Branch Push
When you push to `feature/my-new-feature`:
1. Build pipeline runs automatically
2. All tests and quality checks pass
3. Auto-PR is created from `feature/my-new-feature` → `main`
4. PR includes:
   - Build status summary
   - Test results
   - Quality metrics
   - Custom instructions applied (✓ marked)

#### Scenario 2: Security Vulnerability Found
When daily dependency scan finds vulnerabilities:
1. Dependency Management workflow triggers
2. Security fix PR is created
3. PR title: `⚠️ security: Address dependency vulnerabilities`
4. Labels: `security`, `vulnerability`, `critical`, `automated`
5. Assigned to repository owner

#### Scenario 3: Dependency Updates Available
When minor/patch updates are available:
1. Daily scan detects updates
2. Dependency update PR is created
3. PR includes:
   - Updated dependencies
   - Test verification
   - Compatibility notes
4. Labels: `dependencies`, `automated`, `bot`

#### Scenario 4: Documentation Updates
Manual trigger to refresh development guidelines:
1. Updates all custom instruction files
2. Refreshes MCP server configuration
3. Creates documentation PR with:
   - Updated guidelines
   - New best practices
   - Enhanced examples
4. Ready for immediate merge

### PR Quality Checks

Every PR includes automated comments with:

**Code Quality Results**:
- ✅ CheckStyle: Passed
- ✅ Naming Conventions: Verified
- ✅ Import Organization: Compliant

**Test Coverage**:
| Module | Coverage | Trend |
|--------|----------|-------|
| Service | 82% | ↑ +3% |
| Controller | 70% | ↔ 0% |
| Repository | 85% | ↑ +2% |
| Util | 90% | ↔ 0% |
| **Overall** | **75.8%** | ↑ +1.5% |

**Build Status**:
- ✅ Build Successful
- ✅ All Tests Passed
- ✅ Security Checks Passed

**Custom Instructions**:
- ✅ Code Quality & SOLID Principles
- ✅ Testing Standards
- ✅ Architecture Standards

---

## Best Practices

### For Developers

1. **Follow Custom Instructions**: Keep local Copilot instructions updated
2. **Run Local Checks**: Use Maven goals locally before pushing
   ```bash
   mvn clean test jacoco:report          # Run tests with coverage
   mvn checkstyle:check                  # Check style
   ```
3. **Create Feature Branches**: Push to `feature/*` for auto-PR creation
4. **Review Auto-PRs**: Always review and test auto-generated PRs

### For Code Reviews

1. **Check Custom Instructions**: Verify compliance with quality/testing/architecture standards
2. **Review Quality Metrics**: Check code coverage and complexity
3. **Verify Security**: Review dependency updates and security findings
4. **Test Coverage**: Ensure new code has adequate test coverage

### For Maintenance

1. **Update Instructions**: Modify `.github/instructions/*.md` files as needed
2. **Adjust MCP Tools**: Customize server implementations in `.github/mcp-servers/`
3. **Refine Workflows**: Tune GitHub Actions workflows based on project needs
4. **Monitor Dependencies**: Use weekly/daily schedules to stay updated

---

## Troubleshooting

### MCP Servers Not Responding

1. Check environment variables are set correctly
2. Verify Node.js is installed: `node --version`
3. Check file permissions on MCP server scripts
4. Review server logs for error messages

### PR Auto-Creation Not Working

1. Verify `GITHUB_TOKEN` has `contents:write` and `pull-requests:write` permissions
2. Check branch naming conventions (feature/*, bugfix/*, docs/*)
3. Ensure workflow files are in `.github/workflows/`
4. Verify GitHub Actions are enabled in repository settings

### Coverage Reports Not Generated

1. Ensure JaCoCo is configured in `pom.xml`
2. Run locally: `mvn clean test jacoco:report`
3. Check `target/site/jacoco/index.html` exists
4. Verify Codecov integration is configured

---

## References

- [BOI Java Service Rulebook](../BOI_Java_Service_Rulebook.md)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [MCP Specification](https://modelcontextprotocol.io/)

---

**Last Updated**: 2024
**Maintainer**: Development Team
**Version**: 1.0

