# Java Testing Reference Guide

## Table of Contents

1. [Testing Fundamentals](#testing-fundamentals)
2. [JUnit 5](#junit-5)
3. [Mockito](#mockito)
4. [Integration Testing](#integration-testing)
5. [Test Slices](#test-slices)
6. [Test Containers](#test-containers)
7. [Test Coverage](#test-coverage)
8. [Test Best Practices](#test-best-practices)
9. [Testing Strategies](#testing-strategies)
10. [Performance Testing](#performance-testing)

---

## Testing Fundamentals

### Testing Pyramid

```
         /\
        /  \   E2E Tests (few, slow)
       /____\
      /      \
     /  Integration  \ (medium, moderate)
    /______________\
   /                \
  /     Unit Tests    \ (many, fast)
 /______________________\
```

### Test Types

```java
// 1. Unit Tests - Test individual components in isolation
// - Fast execution
// - Isolated from dependencies
// - Test business logic

// 2. Integration Tests - Test multiple components together
// - Moderate execution time
// - Test component interactions
// - May use real dependencies

// 3. End-to-End Tests - Test entire application
// - Slow execution
// - Test complete workflows
// - Use real infrastructure
```

---

## JUnit 5

### Basic JUnit 5 Tests

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// Test class
class CalculatorTest {

    @BeforeAll
    static void setUpAll() {
        System.out.println("Before all tests");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Before each test");
    }

    @Test
    void testAddition() {
        Calculator calculator = new Calculator();
        int result = calculator.add(2, 3);
        assertEquals(5, result);
    }

    @Test
    void testSubtraction() {
        Calculator calculator = new Calculator();
        int result = calculator.subtract(5, 3);
        assertEquals(2, result);
    }

    @Test
    void testDivisionByZero() {
        Calculator calculator = new Calculator();
        assertThrows(ArithmeticException.class, () -> calculator.divide(10, 0));
    }

    @Test
    @DisplayName("Test multiplication with @DisplayName")
    void testMultiplication() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(3, 4);
        assertEquals(12, result);
    }

    @AfterEach
    void tearDown() {
        System.out.println("After each test");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("After all tests");
    }
}
```

### Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

class AssertionExamples {

    @Test
    void testBasicAssertions() {
        assertEquals(5, 5);
        assertNotEquals(5, 6);
        assertTrue(5 > 3);
        assertFalse(5 < 3);
        assertNull(null);
        assertNotNull("value");
    }

    @Test
    void testArrayAssertions() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
        assertArrayEquals(expected, actual);
    }

    @Test
    void testObjectAssertions() {
        Object obj1 = new Object();
        Object obj2 = obj1;
        assertSame(obj1, obj2);

        Object obj3 = new Object();
        assertNotSame(obj1, obj3);
    }

    @Test
    void testExceptionAssertions() {
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> { throw new IllegalArgumentException("Invalid argument"); }
        );
        assertEquals("Invalid argument", exception.getMessage());
    }

    @Test
    void testTimeoutAssertions() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            // Code that should complete within 1 second
            Thread.sleep(100);
        });
    }

    @Test
    void testGroupedAssertions() {
        assertAll("user properties",
            () -> assertEquals("John", user.getName()),
            () -> assertEquals("john@example.com", user.getEmail()),
            () -> assertEquals(30, user.getAge())
        );
    }

    @Test
    void testMultipleAssertions() {
        // All assertions are checked, even if one fails
        assertAll(() -> assertEquals(5, 5),
                  () -> assertEquals(6, 6),
                  () -> assertEquals(7, 7));
    }
}
```

### Parameterized Tests

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ParameterizedTestExamples {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testWithValueSource(int number) {
        assertTrue(number > 0);
    }

    @ParameterizedTest
    @EnumSource(Season.class)
    void testWithEnumSource(Season season) {
        assertNotNull(season);
    }

    @ParameterizedTest
    @CsvSource({
        "apple, 1",
        "banana, 2",
        "cherry, 3"
    })
    void testWithCsvSource(String fruit, int count) {
        assertNotNull(fruit);
        assertTrue(count > 0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void testWithCsvFileSource(String name, int age, String email) {
        assertNotNull(name);
        assertTrue(age > 0);
        assertNotNull(email);
    }

    @ParameterizedTest
    @MethodSource("provideTestNumbers")
    void testWithMethodSource(int number, int expected) {
        assertEquals(expected, number * 2);
    }

    private static Stream<Arguments> provideTestNumbers() {
        return Stream.of(
            Arguments.of(1, 2),
            Arguments.of(2, 4),
            Arguments.of(3, 6)
        );
    }
}

enum Season { SPRING, SUMMER, AUTUMN, WINTER }
```

### Nested Tests

```java
class NestedTestExamples {

    @Test
    void testTopLevel() {
        System.out.println("Top level test");
    }

    @Nested
    class WhenUserIsLoggedIn {

        @BeforeEach
        void setUp() {
            System.out.println("Login user");
        }

        @Test
        void testUserCanViewProfile() {
            System.out.println("Test view profile");
        }

        @Test
        void testUserCanEditProfile() {
            System.out.println("Test edit profile");
        }
    }

    @Nested
    class WhenUserIsNotLoggedIn {

        @Test
        void testUserCannotViewProfile() {
            System.out.println("Test cannot view profile");
        }
    }
}
```

---

## Mockito

### Basic Mocking

```java
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MockitoExamples {

    @Test
    void testMock() {
        // Create mock
        List<String> mockList = mock(List.class);

        // Define behavior
        when(mockList.get(0)).thenReturn("First");
        when(mockList.get(1)).thenReturn("Second");
        when(mockList.size()).thenReturn(2);

        // Use mock
        assertEquals("First", mockList.get(0));
        assertEquals("Second", mockList.get(1));
        assertEquals(2, mockList.size());
    }

    @Test
    void testVerify() {
        List<String> mockList = mock(List.class);

        mockList.add("One");
        mockList.add("Two");

        // Verify method calls
        verify(mockList).add("One");
        verify(mockList).add("Two");
        verify(mockList, times(2)).add(anyString());
        verify(mockList, never()).clear();
    }
}
```

### Mocking with Annotations

```java
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockitoAnnotationExamples {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        // Setup mock behavior
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call method
        User result = userService.createUser(user);

        // Verify
        assertNotNull(result);
        verify(userRepository).save(user);
        verify(emailService).sendWelcomeEmail(user);
    }

    @Test
    void testCreateUser_EmailFailure() {
        User user = new User();
        user.setEmail("john@example.com");

        // Throw exception
        when(emailService.sendWelcomeEmail(user))
            .thenThrow(new EmailSendException("Failed to send email"));

        // Verify exception handling
        assertThrows(EmailSendException.class, () -> userService.createUser(user));

        verify(emailService).sendWelcomeEmail(user);
        verify(userRepository, never()).save(user);
    }
}
```

### Argument Matchers

```java
class ArgumentMatcherExamples {

    @Test
    void testArgumentMatchers() {
        List<String> mockList = mock(List.class);

        // Any matcher
        when(mockList.get(anyInt())).thenReturn("Any");
        when(mockList.contains(anyString())).thenReturn(true);

        assertEquals("Any", mockList.get(5));
        assertTrue(mockList.contains("anything"));

        // Specific matcher
        when(mockList.contains(eq("specific"))).thenReturn(true);

        assertTrue(mockList.contains("specific"));

        // Verify with matchers
        verify(mockList).get(anyInt());
        verify(mockList).contains(eq("specific"));
    }

    @Test
    void testCustomMatcher() {
        UserRepository userRepository = mock(UserRepository.class);

        // Custom argument matcher
        when(userRepository.save(argThat(user ->
            user.getEmail() != null && user.getEmail().contains("@")
        ))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = new User();
        user.setEmail("valid@example.com");

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser);
    }
}
```

### Spy

```java
class SpyExamples {

    @Test
    void testSpy() {
        List<String> list = new ArrayList<>();
        List<String> spy = spy(list);

        // Real methods are called
        spy.add("One");
        spy.add("Two");

        assertEquals(2, spy.size());

        // Stub specific methods
        when(spy.size()).thenReturn(100);
        assertEquals(100, spy.size());

        // Verify real method was called
        verify(spy).add("One");
        verify(spy).add("Two");
    }

    @Test
    void testPartialMock() {
        RealObject realObject = spy(new RealObject());

        // Stub some methods, use real implementation for others
        when(realObject.expensiveMethod()).thenReturn("Cached result");

        String result = realObject.expensiveMethod();
        assertEquals("Cached result", result);

        // Other methods use real implementation
        realObject.simpleMethod();
    }
}
```

---

## Integration Testing

### Spring Boot Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void testCreateOrder() throws Exception {
        String orderJson = """
            {
                "customerName": "John Doe",
                "items": [
                    {"productId": 1, "quantity": 2},
                    {"productId": 2, "quantity": 1}
                ]
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerName").value("John Doe"));

        // Verify in database
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
    }
}
```

### TestRestTemplate

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestTemplateIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetUser() {
        String url = "http://localhost:" + port + "/api/users/1";

        ResponseEntity<UserResponseDTO> response = restTemplate.getForEntity(
            url,
            UserResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateUser() {
        String url = "http://localhost:" + port + "/api/users";

        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");

        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(
            url,
            userDTO,
            UserResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
```

---

## Test Slices

### @WebMvcTest

```java
@WebMvcTest(UserController.class)
class UserControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "John Doe", "john@example.com");
        when(userService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).getUser(1L);
    }
}
```

### @DataJpaTest

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByEmail("john@example.com");
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }
}
```

### @JsonTest

```java
@JsonTest
class JsonTest {

    @Autowired
    private JacksonTester<UserDTO> json;

    @Test
    void testSerialize() throws JsonProcessingException {
        UserDTO user = new UserDTO(1L, "John Doe", "john@example.com");

        String jsonContent = json.write(user).getJson();

        assertThat(json.parse(jsonContent)).isEqualTo(user);
        assertThat(jsonContent).contains("John Doe");
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        String content = """
            {
                "id": 1,
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        UserDTO user = json.parseObject(content);

        assertEquals("John Doe", user.getName());
    }
}
```

---

## Test Containers

### PostgreSQL Container

```java
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFind() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());

        User found = userRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }
}
```

### Redis Container

```java
@Testcontainers
class RedisCacheTest {

    @Container
    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCache() {
        Cache cache = cacheManager.getCache("users");
        assertNotNull(cache);

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        cache.put(1L, user);

        User cached = cache.get(1L, User.class);
        assertNotNull(cached);
        assertEquals("John Doe", cached.getName());
    }
}
```

---

## Test Coverage

### JaCoCo Configuration

```xml
<!-- pom.xml -->
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Coverage Goals

```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

---

## Test Best Practices

### AAA Pattern

```java
@Test
void testCalculateTotalPrice_AAA_Pattern() {
    // Arrange - Set up test data
    Order order = new Order();
    order.addItem(new Item("Item1", 10.0));
    order.addItem(new Item("Item2", 20.0));

    // Act - Execute method under test
    double total = order.calculateTotal();

    // Assert - Verify expected results
    assertEquals(30.0, total, 0.001);
}
```

### One Assert Per Test

```java
// Good - Single assertion
@Test
void testUserName() {
    User user = new User("John Doe");
    assertEquals("John Doe", user.getName());
}

// Avoid - Multiple assertions
@Test
void testUserMultiple() {
    User user = new User("John Doe", "john@example.com", 30);
    assertEquals("John Doe", user.getName());
    assertEquals("john@example.com", user.getEmail());
    assertEquals(30, user.getAge());
}
```

### Meaningful Test Names

```java
// Good - Descriptive
@Test
void testCalculateTotalPrice_WhenItemsAdded_ReturnsCorrectSum() {
    // Test implementation
}

// Bad - Vague
@Test
void testPrice() {
    // Test implementation
}
```

---

## Summary

This reference covers Java testing fundamentals:

- **Testing Fundamentals**: Testing pyramid and types
- **JUnit 5**: Assertions, parameterized tests, nested tests
- **Mockito**: Mocking, spies, argument matchers
- **Integration Testing**: Spring Boot integration tests
- **Test Slices**: @WebMvcTest, @DataJpaTest, @JsonTest
- **Test Containers**: PostgreSQL, Redis containers
- **Test Coverage**: JaCoCo configuration
- **Test Best Practices**: AAA pattern, one assert per test

Testing is essential for maintaining code quality and preventing regressions. JUnit 5 and Mockito provide powerful tools for writing effective tests.
