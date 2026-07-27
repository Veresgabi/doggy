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

### Testing
- Missing tests
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

Alternatively, if you want to review without posting to PR, use:
```
/code-review
```

Do not modify files immediately. Only fix issues if explicitly requested.