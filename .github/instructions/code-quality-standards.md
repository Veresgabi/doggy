# Code Quality & SOLID Principles Instructions

## Scope
Applies to all Java source files under `src/main/java` in the doggy project.

## SOLID Principles Guidance

### Single Responsibility Principle (SRP)
- Each class should have **one and only one reason to change**
- Controllers handle HTTP requests only (no business logic)
- Services contain business logic (no data access)
- Repositories handle data access only (no business logic)

Example violations to avoid:
```java
// ❌ Bad: Controller doing business logic
@PostMapping("/dogs")
public DogResponse saveDog(@RequestBody DogRequest req) {
    // Business logic should NOT be here
    Dog dog = new Dog();
    // ... save logic ...
    return mapper.toDogResponse(dog);
}

// ✅ Good: Controller delegates to service
@PostMapping("/dogs")
public DogResponse saveDog(@RequestBody DogRequest req) {
    Dog saved = dogService.create(mapper.toDog(req));
    return mapper.toDogResponse(saved);
}
```

### Open/Closed Principle (OCP)
- Classes should be **open for extension, closed for modification**
- Use interfaces for external dependencies
- Avoid hard-coded implementations

```java
// ✅ Good: Open for extension via interface
public interface DogRepository {
    Dog findById(Long id);
    List<Dog> findAll();
}

// Can be extended for different storage mechanisms
public class JpaDogRepository implements DogRepository { }
public class MongoDogRepository implements DogRepository { }
```

### Liskov Substitution Principle (LSP)
- Subtypes must be substitutable for their base types
- Derived classes should enhance, not restrict, parent behavior

```java
// ✅ Good: Inheritance respects contract
public interface AuthProvider {
    User authenticate(Credentials creds);
}

public class JwtAuthProvider implements AuthProvider {
    @Override
    public User authenticate(Credentials creds) {
        // Proper implementation
    }
}
```

### Interface Segregation Principle (ISP)
- **Many specific interfaces are better than one general-purpose interface**
- Clients should not be forced to depend on methods they don't use

```java
// ❌ Bad: Fat interface
public interface UserService {
    User authenticate(Credentials c);
    void sendEmail(String recipient, String body);
    void logActivity(String action);
    List<User> getAllUsers();
}

// ✅ Good: Segregated interfaces
public interface UserAuthenticator {
    User authenticate(Credentials c);
}

public interface EmailService {
    void sendEmail(String recipient, String body);
}

public interface ActivityLogger {
    void logActivity(String action);
}
```

### Dependency Inversion Principle (DIP)
- High-level modules should NOT depend on low-level modules
- Both should depend on abstractions
- Abstractions should NOT depend on details; details should depend on abstractions

```java
// ❌ Bad: Direct dependency on concrete class
public class DogService {
    private PostgresRepository dogRepo = new PostgresRepository();
}

// ✅ Good: Dependency injection of interface
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
}
```

## Code Quality Checks

### Naming Conventions
- **Classes**: PascalCase (e.g., `DogService`, `AuthController`)
- **Methods**: camelCase, verb-first (e.g., `findDogById()`, `createUser()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_PAGE_SIZE`)
- **Local variables in repositories**: Hungarian-style prefixes (see copilot-instructions.md)

### Imports Organization
Follow this order:
1. Static imports (no wildcards)
2. Java standard library (`java.*`, `javax.*`)
3. Third-party imports (`org.*`, `com.*` excluding BOI)
4. BOI-specific imports (`com.boi.*`)

```java
import static java.util.stream.Collectors.toList;

import java.util.*;
import javax.persistence.*;

import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.model.*;
import com.example.service.*;
```

### Lombok Usage
- Use `@Value` for immutable DTOs and value types
- Use `@Data` for mutable entities and domain objects
- Use `@Builder` for complex object construction
- Use `@RequiredArgsConstructor` + `@NonNull` for dependency injection
- Use `@Slf4j` for logging

```java
// ✅ Correct Lombok usage
@Value
@Builder
public class CreateDogRequest {
    @NonNull
    String name;
    
    @NonNull
    DogHeight height;
    
    @NonNull
    DogWeight weight;
}
```

### Code Organization
- Keep methods under 25 lines when possible
- Group related methods together
- No unnecessary empty lines within methods
- Extract complex logic into helper methods

## Quality Metrics
- **Code coverage**: Minimum 80% for new code
- **Cyclomatic complexity**: Max 10 per method
- **Method length**: Preferred max 25 lines
- **Class length**: Preferred max 300 lines

## Refactoring Signals
Request a PR for refactoring when you notice:
- Methods with more than 3 levels of nesting
- Classes with more than 5 public methods
- Duplicate code patterns appearing 3+ times
- God Classes handling multiple responsibilities
- Long parameter lists (5+ parameters)

