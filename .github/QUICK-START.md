# Quick Start Guide: Custom Instructions & MCP Setup

## 📋 What's Been Configured

### 3 Custom Instruction Files
1. ✅ **Code Quality & SOLID Principles** (`code-quality-standards.md`)
   - SOLID principles guidance (SRP, OCP, LSP, ISP, DIP)
   - Naming conventions and import organization
   - Lombok best practices
   - Code quality metrics and refactoring signals

2. ✅ **Testing Standards & Coverage** (`testing-standards.md`)
   - JUnit 5, Mockito, AssertJ patterns
   - Given-When-Then test structure
   - Mock vs. real objects strategy
   - Coverage requirements (80%+)
   - Integration and acceptance testing

3. ✅ **Architecture & Module Organization** (`architecture-standards.md`)
   - Hexagonal architecture implementation
   - Layer responsibilities (Controller, Service, Repository, etc.)
   - Dependency injection patterns
   - Transaction management
   - Exception handling

### 3 MCP Servers
1. ✅ **GitHub API Server** (`github-server.js`)
   - PR creation and management
   - Commenting on PRs
   - Listing and updating PRs

2. ✅ **Code Analysis Server** (`code-analysis-server.js`)
   - Code quality analysis
   - Test coverage verification
   - Linting and code smell detection
   - Quality report generation

3. ✅ **Maven Build Server** (`maven-build-server.js`)
   - Unit and integration testing
   - Project building and compilation
   - Dependency vulnerability checking
   - Coverage report generation

### 3 GitHub Actions Workflows
1. ✅ **Code Quality Analysis** (`code-quality.yml`)
   - CheckStyle validation
   - Test coverage analysis
   - PR comments with results

2. ✅ **Build & Test Pipeline** (`build-and-test.yml`)
   - Compile and test
   - Security checks
   - Auto-PR creation from feature branches

3. ✅ **Dependency Management** (`dependency-management.yml`)
   - Daily dependency scanning
   - Automatic update PRs
   - Security vulnerability alerts

---

## 🚀 Getting Started (3 Steps)

### Step 1: Install Copilot Instructions Locally

```powershell
# Run from repository root
./.github/scripts/install-copilot-instructions.ps1
```

This copies the project's custom instructions to your local Copilot config:
- **Location**: `C:\Users\<YourUsername>\AppData\Local\github-copilot\intellij\copilot-instructions.md`
- **Restart**: Restart your JetBrains IDE after running

### Step 2: Configure Environment Variables

Set these for your system:

```bash
# GitHub Integration
export GITHUB_TOKEN=<your-github-personal-access-token>
export GITHUB_OWNER=Gabor_Veres
export GITHUB_REPO=doggy

# Code Analysis
export PROJECT_ROOT=/path/to/doggy
export SONARQUBE_HOST=<optional>
export SONARQUBE_TOKEN=<optional>

# Maven
export MAVEN_HOME=/path/to/maven
export JAVA_HOME=/path/to/java
```

### Step 3: Enable GitHub Actions

1. Go to repository Settings → Actions
2. Select "Allow all actions and reusable workflows"
3. Workflows will trigger automatically on push/PR

---

## 📁 Project Structure

```
doggy/
├── .github/
│   ├── instructions/                    # Custom instruction files
│   │   ├── copilot-instructions.md     # Hungarian naming for repos
│   │   ├── code-quality-standards.md   # SOLID principles & quality
│   │   ├── testing-standards.md        # Testing best practices
│   │   └── architecture-standards.md   # Architecture & layers
│   ├── mcp-servers/                     # MCP server implementations
│   │   ├── github-server.js
│   │   ├── code-analysis-server.js
│   │   └── maven-build-server.js
│   ├── mcp-config.json                 # MCP configuration
│   ├── workflows/                       # GitHub Actions
│   │   ├── code-quality.yml
│   │   ├── build-and-test.yml
│   │   └── dependency-management.yml
│   ├── scripts/
│   │   └── install-copilot-instructions.ps1
│   ├── README.md                        # Full documentation
│   └── QUICK-START.md                   # This file
├── src/main/java/com/example/
│   ├── configuration/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   └── util/
└── pom.xml                              # Maven configuration
```

---

## 💻 Usage Examples

### Example 1: Create a Feature Branch PR

```bash
# Create and push feature branch
git checkout -b feature/add-dog-filtering
# Make your changes
git add .
git commit -m "feat: Add dog filtering by breed"
git push origin feature/add-dog-filtering

# GitHub Actions will:
# 1. Run build & tests
# 2. Check code quality
# 3. Auto-create PR to main with detailed summary
# 4. Post quality metrics as PR comment
```

### Example 2: Security Vulnerability Detected

```
Daily scheduled workflow runs:
1. Scans dependencies for vulnerabilities
2. Finds CVE in log4j-core@2.14.0
3. Auto-creates PR with:
   - Title: ⚠️ security: Address dependency vulnerabilities
   - Labels: security, vulnerability, critical
   - Recommended fix in PR body
4. Assigned to repository owner
```

### Example 3: Test Coverage Check

```
PR posted comment:
## 📊 Code Quality Analysis Results
- CheckStyle: Passed ✅
- Naming: Verified ✅
- SOLID Principles: Verified ✅

## 🧪 Test Coverage Report
- Service: 82% ✅
- Controller: 70% (needs 2 more tests)
- Repository: 85% ✅
- Overall: 75.8%
```

### Example 4: Using MCP Servers

Once configured, you can use MCP servers in your IDE/CLI:

```javascript
// Create a pull request
github.create_pull_request({
  title: "feat: Add new feature",
  body: "Description of changes",
  head: "feature/new-feature",
  base: "main",
  labels: ["enhancement"]
});

// Analyze code quality
code-analysis.analyze_code_quality({
  file_path: "src/main/java/com/example/service/DogService.java",
  check_type: "solid"
});

// Run tests
maven-build.run_tests({
  test_class: "com.example.service.DogServiceTest",
  profile: null
});
```

---

## 📊 Workflow Triggers

### Code Quality Analysis
- ✅ On Push to main/develop
- ✅ On Pull Request with Java changes

### Build & Test Pipeline  
- ✅ On Push to feature/*, main, develop
- ✅ On Pull Request

### Dependency Management
- ✅ Daily at 2 AM UTC (schedule)
- ✅ Manual workflow dispatch

---

## 🎯 Key Features

### Automatic PR Creation
- Feature branches → Auto-PR to main
- Security fixes → Labeled with `security`, `critical`
- Dependency updates → Labeled with `dependencies`, `automated`

### Quality Gates
- Code coverage minimum: 80%
- Cyclomatic complexity: max 10 per method
- CheckStyle validation: All rules pass
- Test pass rate: 100%

### Custom Instructions Applied
Every PR shows this footer:
```
### Custom Instructions Applied:
- ✅ Code Quality & SOLID Principles
- ✅ Testing Standards
- ✅ Architecture Standards
```

### PR Comments
Automatic comments on every PR with:
- Build status
- Test results (unit + integration)
- Coverage metrics by module
- Quality recommendations
- Next steps

---

## 🔧 Customization

### Modify Custom Instructions
Edit the `.md` files in `.github/instructions/`:
- Changes apply to all new code
- Update Copilot instructions: Run install script again
- Affects GitHub Actions: Workflows read these files

### Adjust MCP Tools
Edit `.github/mcp-servers/*.js` files to:
- Change tool behavior
- Add new tools
- Modify response format
- Integrate with external services

### Tune GitHub Actions
Edit `.github/workflows/*.yml` to:
- Change trigger conditions
- Adjust quality thresholds
- Add new jobs
- Modify PR labels and assignments

---

## ✅ Verification Checklist

- [ ] Custom instructions installed locally
- [ ] Environment variables configured
- [ ] GitHub Actions enabled
- [ ] MCP servers configured
- [ ] Test run: `mvn clean test`
- [ ] First feature branch pushed
- [ ] Auto-PR created successfully
- [ ] Quality metrics posted to PR
- [ ] All tests passing
- [ ] Ready to merge!

---

## 📞 Support

For issues or questions:

1. **Check the full documentation**: `.github/README.md`
2. **Review instruction files**: `.github/instructions/`
3. **Check workflow logs**: GitHub Actions tab in repository
4. **Consult troubleshooting**: `.github/README.md#troubleshooting`

---

## 🎓 Learning Resources

- **SOLID Principles**: See `code-quality-standards.md`
- **Testing Patterns**: See `testing-standards.md`
- **Architecture Guide**: See `architecture-standards.md`
- **BOI Java Rulebook**: Referenced in project root

---

**Status**: ✅ Ready to Use
**Last Updated**: 2024
**Version**: 1.0

