# Implement Feature

## Purpose

Implement a new feature or GitHub issue in the Spring Boot application.

## Instructions

1. First analyze the existing project structure.
2. Identify:
    - Controllers
    - Services
    - Repositories
    - Entities
    - DTOs
    - Existing tests

3. Follow existing coding patterns.

4. Implement the requested feature:
    - Use constructor injection.
    - Keep business logic in services.
    - Do not put business logic in controllers.
    - Validate input using Spring validation where appropriate.

5. Add tests:
    - Use JUnit 5.
    - Use Mockito for unit tests.
    - Add integration tests if needed.

6. Before finishing:
    - Run Maven tests.
    - Fix compilation errors.
    - Summarize changed files.

## Output

Provide:
- Implementation summary
- Files changed
- Tests added
- Test execution result