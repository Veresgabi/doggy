---
name: Review Code
description: >
  Audits the active code diff for quality, style guidelines, console logs, and test coverage.
  Use when the user says "review my changes", "audit code", or "check my diff".
disable-model-invocation: false
---

# Review Code

## Purpose

Review current code changes before creating a Pull Request. Only the changes contained by the PR should be checked.

## Review checklist

Analyze:

### Java quality
- Naming conventions
- Code readability
- Exception handling
- Null handling
- SOLID principles

### Spring Boot practices
- Dependency injection
- Controller responsibilities
- Transaction handling
- Configuration management

### Testing (CRITICAL - CLAUDE.md line 13 requires JUnit 5 tests for new code)
- **Missing tests for new methods/classes**: Every new public method must have corresponding JUnit 5 tests
- **Missing tests for modified methods**: Check if existing tests cover the changes
- Use Grep to find test files (e.g., search for "Test.java" files matching the changed class name)
- Flag any new method that lacks test coverage as a CLAUDE.md convention violation
- Incorrect mocking
- Missing edge cases

### Security
Check for:
- Hardcoded secrets
- Unsafe input handling
- Authentication problems

## Instructions

Run the built-in code-review skill with the --comment flag to automatically post findings to the PR:

```
/code-review --comment
```

This will:
1. Analyze the current diff for bugs and code quality issues
2. Automatically post inline comments on the PR at the specific lines
3. Provide findings here in the conversation as well

**IMPORTANT - Test Coverage Check:**
After running code-review, ALWAYS verify test coverage for new/modified code:
1. Use Grep to find test files for changed classes (e.g., if DogRepositoryCustomImpl changed, search for DogRepositoryCustomImplTest)
2. Read the test file and check if new methods have corresponding tests
3. If tests are missing, post a comment citing CLAUDE.md line 13: "Write JUnit 5 tests for new code"
4. This is a CONVENTION violation and must be flagged

Alternatively, if you want to review without posting to PR, use:
```
/code-review
```

Do not modify files immediately. Only fix issues if explicitly requested.