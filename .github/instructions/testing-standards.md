# Testing Standards & Coverage Instructions

## Scope
Applies to all test files under `src/test/java` and test resources.

## Testing Philosophy

All functionality must be covered with automated tests. The test pyramid follows this pattern:
- **Unit Tests** (70%): Fast, isolated, focus on single methods
- **Integration Tests** (20%): Test components with Spring Boot context
- **Acceptance Tests** (10%): End-to-end API testing with real scenarios

## Unit Testing Standards

### Test Framework: JUnit 5 + Mockito + AssertJ

### Structure: Given-When-Then

Every test method follows the Given-When-Then pattern with clear section comments:

```java
/**
 * Unit test for {@link DogService}.
 */
@ExtendWith(MockitoExtension.class)
class DogServiceTest {
    @Mock
    private DogRepository dogRepository;
    
    @InjectMocks
    private DogService dogService;

    @Test
    void should_find_dog_by_id_when_exists() {
        // Given
        long dogId = 1L;
        var expectedDog = Dog.builder().id(dogId).name("Rex").build();
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(expectedDog));
        
        // When
        var result = dogService.findById(dogId);
        
        // Then
        assertThat(result).isPresent().contains(expectedDog);
        verify(dogRepository).findById(dogId);
        verifyNoMoreInteractions(dogRepository);
    }

    @Test
    void should_return_empty_when_dog_not_found() {
        // Given
        long dogId = 999L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());
        
        // When
        var result = dogService.findById(dogId);
        
        // Then
        assertThat(result).isEmpty();
        verify(dogRepository).findById(dogId);
    }
}
```

### Naming Convention: `should_` prefix

Use descriptive names that specify behavior:
- ✅ `should_create_user_with_valid_credentials`
- ✅ `should_throw_exception_when_email_already_exists`
- ✅ `should_update_dog_height_successfully`
- ❌ `testUserCreation`
- ❌ `test1`

### Mock vs Real Objects

**Use mocks for:**
- External services (databases, APIs, message queues)
- Complex dependencies
- To isolate the class under test

**Use real objects for:**
- Simple value types (String, Integer, LocalDate)
- DTOs and domain objects
- Builder patterns

```java
@Test
void should_create_dog_when_all_fields_valid() {
    // Given - Real objects are OK for simple value types
    var createRequest = DogRequest.builder()
        .name("Buddy")
        .height(DogHeight.LARGE)
        .weight(DogWeight.MEDIUM)
        .build();
    
    var expectedDog = Dog.builder()
        .name("Buddy")
        .height(DogHeight.LARGE)
        .weight(DogWeight.MEDIUM)
        .build();
    
    when(dogRepository.save(any(Dog.class))).thenReturn(expectedDog);
    
    // When
    var result = dogService.createDog(createRequest);
    
    // Then
    assertThat(result.getName()).isEqualTo("Buddy");
}
```

### Verification Strategy

**For order-critical interactions:**
```java
@Test
void should_authenticate_then_log_activity() {
    // Given
    var credentials = new Credentials("user", "pass");
    var expectedUser = new User("user");
    when(authenticator.authenticate(credentials)).thenReturn(expectedUser);
    
    // When
    authService.authenticateAndLog(credentials);
    
    // Then - Verify order matters
    InOrder inOrder = inOrder(authenticator, logger);
    inOrder.verify(authenticator).authenticate(credentials);
    inOrder.verify(logger).logActivity(anyString());
}
```

**For regular interactions:**
```java
// Verify specific method was called
verify(dogRepository).findById(1L);

// Verify no additional interactions
verifyNoMoreInteractions(dogRepository);

// Verify number of invocations
verify(dogRepository, times(2)).save(any());
```

### AssertJ Assertions

Use fluent assertion API:

```java
// Collections
assertThat(dogs).hasSize(3).contains(dog1, dog2).allMatch(d -> d.getHeight() != null);

// Strings
assertThat(name).isNotBlank().startsWith("Rex").endsWith("Jr");

// Exceptions
assertThatThrownBy(() -> userService.deleteAdmin())
    .isInstanceOf(UnauthorizedException.class)
    .hasMessage("Cannot delete admin user");

// Optional
assertThat(optionalDog).isPresent().hasValue(expectedDog);
assertThat(optionalUser).isEmpty();

// Custom assertions
assertThat(user).as("User validation")
    .hasFieldOrPropertyWithValue("active", true)
    .hasFieldOrPropertyWithValue("role", Role.ADMIN);
```

## Coverage Requirements

### Target Metrics
- **Overall Coverage**: Minimum 80% for new code
- **Line Coverage**: Track all executable lines
- **Branch Coverage**: All if/else paths tested
- **Method Coverage**: Every public method tested

### Coverage Exceptions
Coverage can be less strict for:
- Configuration classes (AppConfig, SecurityConfig)
- Model/Entity classes (auto-generated getters/setters)
- Exceptions and error handlers (integration tests may cover these better)
- Spring Data repositories (interfaces - they're Spring-generated)

Mark these with `@lombok.Generated` or `// Coverage exclude` comments.

### Running Coverage Reports

```bash
# Run tests with coverage
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

## Integration Testing with Spring Boot Test

### Database Testing

```java
@SpringBootTest
@ActiveProfiles("test")
class DogRepositoryIntegrationTest {
    @Autowired
    private DogRepository dogRepository;
    
    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.clear();
    }

    @Test
    @Transactional
    void should_find_dogs_by_breed_group() {
        // Given
        var dog1 = Dog.builder().name("Rex").breedGroup(SPORTING).build();
        var dog2 = Dog.builder().name("Max").breedGroup(WORKING).build();
        em.persistAndFlush(dog1);
        em.persistAndFlush(dog2);
        
        // When
        var results = dogRepository.findByBreedGroup(SPORTING);
        
        // Then
        assertThat(results).hasSize(1).contains(dog1);
    }
}
```

### Security Testing

```java
@SpringBootTest
@WebMvcTest(DogController.class)
@DisplayName("DogController Security Tests")
class DogControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_deny_access_when_not_authenticated() throws Exception {
        mockMvc.perform(get("/api/dogs"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void should_allow_access_with_valid_token() throws Exception {
        mockMvc.perform(get("/api/dogs"))
            .andExpect(status().isOk());
    }
}
```

## Acceptance Testing

### Gherkin Feature Files

Store in `src/test/resources/features/`:

```gherkin
Feature: Dog Management
  As a user
  I want to manage my dog profile
  So that I can keep track of my dog's information

  Scenario: Create a new dog profile
    Given I am authenticated as "john"
    When I create a dog with name "Buddy" and height "LARGE"
    Then the dog should be saved successfully
    And I should receive a 201 Created response

  Scenario: Prevent duplicate dog names
    Given a dog named "Rex" already exists
    When I try to create another dog named "Rex"
    Then I should receive a 409 Conflict response
    And the error message should contain "Dog name already exists"
```

### RestAssured Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DogApiAcceptanceTest {
    @LocalServerPort
    private int port;

    private RequestSpecification requestSpec;

    @BeforeEach
    void setUp() {
        requestSpec = RestAssured.given()
            .port(port)
            .header("Authorization", "Bearer " + getValidToken());
    }

    @Test
    void should_create_dog_successfully() {
        var createRequest = DogRequest.builder()
            .name("Buddy")
            .height(DogHeight.LARGE)
            .build();

        requestSpec.body(createRequest)
            .post("/api/dogs")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo("Buddy"));
    }
}
```

## Test Data & Fixtures

### Use TestFixtures or Test Builders

```java
// ✅ Good: Clear test data
var dog = DogTestFixture.aBigDog()
    .withName("Rex")
    .withBreedGroup(SPORTING)
    .build();

// ✅ Also good: ObjectMother pattern
var admin = TestUsers.anAdminUser().build();
var normalUser = TestUsers.aNormalUser().build();
```

## Test Code Quality

- Keep test methods small and focused (test one behavior per test)
- Avoid test interdependencies
- Use descriptive assertion messages
- No business logic in tests
- Extract common setup into @BeforeEach methods
- Use parameterized tests for multiple similar cases

```java
@ParameterizedTest(name = "Height {0} should be valid")
@ValueSource(strings = {"SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE"})
void should_accept_valid_heights(String heightValue) {
    var height = DogHeight.valueOf(heightValue);
    assertThat(height).isNotNull();
}
```

