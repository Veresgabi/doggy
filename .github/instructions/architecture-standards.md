# Architecture & Module Organization Instructions

## Scope
Applies to project structure, package organization, and module dependencies in the doggy project.

## Hexagonal Architecture (Ports & Adapters)

The doggy service follows hexagonal architecture principles with clear separation of concerns:

```
doggy/
├── src/main/java/com/example/
│   ├── configuration/           # Configuration beans, security, filters
│   │   ├── AppConfig.java
│   │   ├── SecurityConfig.java
│   │   └── WebMvcConfig.java
│   ├── controller/              # Web Adapters (REST endpoints)
│   │   ├── AuthController.java
│   │   ├── DogController.java
│   │   └── UserController.java
│   ├── dto/                     # Data Transfer Objects
│   │   ├── AuthRequest.java
│   │   ├── DogResponse.java
│   │   └── AbstractResponse.java
│   ├── service/                 # Business Logic (Domain Layer)
│   │   ├── DogService.java
│   │   ├── DogServiceImpl.java
│   │   ├── UserService.java
│   │   └── TokenProvider.java
│   ├── model/                   # Domain Entities
│   │   ├── Dog.java
│   │   ├── User.java
│   │   └── Authority.java
│   ├── repository/              # Data Persistence (Adapter)
│   │   ├── DogRepository.java
│   │   ├── DogRepositoryCustom.java
│   │   ├── DogRepositoryCustomImpl.java
│   │   └── UserRepository.java
│   └── util/                    # Utility Classes
│       ├── SecurityCipher.java
│       └── CookieUtil.java
└── src/test/java/              # Test mirror structure
```

## Layer Responsibilities

### Configuration Layer
**Location:** `configuration/`

**Responsibility:** Spring configuration, security setup, bean definitions

**Rules:**
- Should NOT contain business logic
- Define beans, security filters, and autoconfiguration
- Use `@Configuration` annotation
- Document non-obvious bean setup

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // Security filter setup
    }
}
```

### Web Layer (Downstream Integration)
**Location:** `controller/`

**Responsibility:** Handle HTTP requests/responses, validate input, delegate to services

**Rules:**
- Should NOT contain business logic
- Should NOT access repositories directly
- Must validate request parameters
- Should use DTOs for request/response bodies
- Use appropriate HTTP status codes
- Document all endpoints with comments or Swagger

```java
@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {
    private final DogService dogService;

    @GetMapping("/{id}")
    public ResponseEntity<DogResponse> getDogById(@PathVariable Long id) {
        var dog = dogService.findById(id);
        return dog.map(d -> ResponseEntity.ok(toResponse(d)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DogResponse> createDog(@Valid @RequestBody CreateDogRequest request) {
        var dog = dogService.createDog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(dog));
    }
}
```

### Service Layer (Domain/Business Logic)
**Location:** `service/`

**Responsibility:** Implement business rules, orchestrate operations, manage transactions

**Rules:**
- Contains all business logic
- Should be stateless
- Uses dependency injection for repositories and other services
- Defines transaction boundaries with `@Transactional`
- Returns domain objects (not DTOs)
- May throw domain exceptions

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DogServiceImpl implements DogService {
    private final DogRepository dogRepository;
    private final DogImageService dogImageService;

    @Transactional
    public Dog createDog(CreateDogRequest request) {
        // Validate business rules
        if (dogRepository.existsByName(request.getName())) {
            throw new DogAlreadyExistsException("Dog name already exists");
        }

        // Create and save
        var dog = Dog.builder()
            .name(request.getName())
            .height(request.getHeight())
            .build();

        var savedDog = dogRepository.save(dog);
        log.info("Created new dog with id: {}", savedDog.getId());
        return savedDog;
    }

    @Transactional(readOnly = true)
    public Optional<Dog> findById(Long id) {
        return dogRepository.findById(id);
    }
}
```

### Data Persistence Layer (Adapter)
**Location:** `repository/`

**Responsibility:** Database access, queries, persistence operations

**Rules:**
- Should NOT contain business logic
- Implement `CrudRepository` or custom interfaces
- Prefix custom implementations with `Custom` (e.g., `DogRepositoryCustom`)
- Implement `DogRepositoryCustom` in `DogRepositoryCustomImpl`
- Use named queries or QueryDSL for complex queries
- Return domain objects, not DTOs
- Use Hungarian-style prefixes for local variables (see copilot-instructions.md)

```java
// Interface definition
public interface DogRepository extends JpaRepository<Dog, Long> {
    Optional<Dog> findByName(String name);
    boolean existsByName(String name);
    List<Dog> findByBreedGroup(BreedGroup breedGroup);
}

// Custom repository for complex queries
public interface DogRepositoryCustom {
    List<Dog> findByFilters(DogFilterCriteria criteria);
}

// Implementation with Hungarian-style naming
@Repository
public class DogRepositoryCustomImpl implements DogRepositoryCustom {
    private final EntityManager entityManager;

    public List<Dog> findByFilters(DogFilterCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dog> cq = cb.createQuery(Dog.class);
        Root<Dog> rootDog = cq.from(Dog.class);

        List<Predicate> lstPredicates = new ArrayList<>();
        
        if (Objects.nonNull(criteria.getHeight())) {
            Predicate predHeight = cb.equal(rootDog.get("height"), criteria.getHeight());
            lstPredicates.add(predHeight);
        }

        cq.where(cb.and(lstPredicates.toArray(new Predicate[0])));
        return entityManager.createQuery(cq).getResultList();
    }
}
```

### Model Layer (Domain Entities)
**Location:** `model/`

**Responsibility:** Domain entity definitions, value objects, enums

**Rules:**
- Use `@Entity` for JPA entities
- Use `@Value` annotation for immutable value types
- Use `@Data` or `@Value` with Lombok
- Add `@Builder` for complex construction
- Add `@NonNull` for required fields
- Keep entities simple; business logic goes in services
- Define meaningful enums for constrained values

```java
@Entity
@Table(name = "dogs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private DogHeight height;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}

// Value objects should be immutable
@Value
public class DogHeight {
    @NonNull
    private String value; // SMALL, MEDIUM, LARGE, etc.

    public static DogHeight SMALL = new DogHeight("SMALL");
    public static DogHeight LARGE = new DogHeight("LARGE");
}
```

### DTO Layer (Data Transfer)
**Location:** `dto/`

**Responsibility:** Define structures for API request/response bodies

**Rules:**
- Use `@Value` annotation (immutable)
- Add `@Builder` for convenience
- Add `@Valid` for validation rules in DTOs
- Include proper documentation
- Separate request DTOs from response DTOs
- Use `AbstractResponse` base class for consistency

```java
@Value
@Builder
public class CreateDogRequest {
    @NonNull
    @NotBlank
    String name;

    @NonNull
    DogHeight height;

    @NonNull
    DogWeight weight;
}

@Value
@Builder
public class DogResponse {
    Long id;
    String name;
    DogHeight height;
    DogWeight weight;
    LocalDateTime createdAt;
}
```

### Utility Layer
**Location:** `util/`

**Responsibility:** Helper functions, encryption, formatting, etc.

**Rules:**
- Small, focused utility classes
- No dependency on Spring
- No business logic
- Static methods only (unless stateful like a cipher)
- Well-documented behavior

## Dependency Flow

Valid dependencies between layers:

```
Controllers
    ↓
Services
    ↓
Repositories
    ↓
Database
```

**Invalid dependencies:**
- ❌ Controllers → Repositories (skip service layer)
- ❌ Services → Controllers (circular)
- ❌ DTOs → Services (DTOs are for transfer only)

## Package Structure Best Practices

### Organize by Feature (Recommended for larger projects)
```
com/example/
├── user/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
├── dog/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
└── shared/
    ├── configuration/
    ├── util/
    └── dto/
```

### Current Project Structure (Layer-based)
Current doggy project uses layer-based organization, which is acceptable for smaller projects. The current structure is fine.

## Dependency Injection

**Required:** Use constructor injection with `@RequiredArgsConstructor`

```java
// ✅ Good: Constructor injection
@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    public Dog createDog(CreateDogRequest req) {
        // Use fields
    }
}

// ❌ Bad: Field injection
@Service
public class DogService {
    @Autowired
    private DogRepository dogRepository;
}

// ❌ Bad: Setter injection
@Service
public class DogService {
    private DogRepository dogRepository;
    
    @Autowired
    public void setDogRepository(DogRepository repo) {
        this.dogRepository = repo;
    }
}
```

## Transaction Management

### Use `@Transactional` at Service Layer

```java
// ✅ Good: Transactional at service boundary
@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;

    @Transactional
    public Dog createDog(CreateDogRequest req) {
        // Multiple repository calls in one transaction
        var dog = dogRepository.save(mapToDog(req));
        updateUserDogCount(dog.getOwnerId());
        return dog;
    }

    @Transactional(readOnly = true)
    public Optional<Dog> findById(Long id) {
        return dogRepository.findById(id);
    }
}
```

### Avoid `@Transactional` on Controllers

```java
// ❌ Bad: @Transactional on controller
@RestController
@Transactional  // Don't do this!
public class DogController {
    // ...
}
```

## Error Handling & Custom Exceptions

### Define Custom Exceptions in Domain

```java
// Base exception for domain errors
public abstract class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}

// Specific domain exceptions
public class DogAlreadyExistsException extends DomainException {
    public DogAlreadyExistsException(String dogName) {
        super("Dog with name '" + dogName + "' already exists");
    }
}

public class DogNotFound extends DomainException {
    public DogNotFound(Long dogId) {
        super("Dog with ID " + dogId + " not found");
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        log.warn("Domain exception: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse("An unexpected error occurred"));
    }
}
```

## Import Organization

All classes should follow import organization rules (see code-quality-standards.md):

```java
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Dog;
import com.example.repository.DogRepository;
```

