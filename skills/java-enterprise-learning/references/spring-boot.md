# Spring Boot Reference Guide

## Table of Contents

1. [Introduction](#introduction)
2. [Application Structure](#application-structure)
3. [Configuration Management](#configuration-management)
4. [Dependency Injection](#dependency-injection)
5. [Auto-Configuration](#auto-configuration)
6. [REST API Development](#rest-api-development)
7. [Data Access](#data-access)
8. [Security](#security)
9. [Testing](#testing)
10. [Actuator](#actuator)
11. [Profiles](#profiles)
12. [Properties](#properties)

---

## Introduction

### What is Spring Boot?

```java
// Spring Boot makes it easy to create stand-alone, production-grade
// Spring-based Applications that you can "just run".

// Key Features:
// - Create stand-alone Spring applications
// - Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)
// - Provide opinionated 'starter' dependencies to simplify build configuration
// - Automatically configure Spring and 3rd party libraries whenever possible
// - Provide production-ready features such as metrics, health checks and externalized configuration
// - Absolutely no code generation and no requirement for XML configuration
```

### Creating a Spring Boot Application

```java
// pom.xml (Maven)
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

// build.gradle (Gradle)
plugins {
    id 'org.springframework.boot' version '3.2.0'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### Main Application Class

```java
// Spring Boot application
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

// Equivalent annotations
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class DemoApplication {
    // Same as @SpringBootApplication
}
```

### Starters

```java
// Spring Boot Starters - dependency aggregators

// Web development
spring-boot-starter-web
spring-boot-starter-websocket
spring-boot-starter-webflux

// Data access
spring-boot-starter-data-jpa
spring-boot-starter-data-mongodb
spring-boot-starter-data-redis
spring-boot-starter-data-jdbc

// Security
spring-boot-starter-security

// Testing
spring-boot-starter-test

// Production features
spring-boot-starter-actuator

// Validation
spring-boot-starter-validation

// AOP
spring-boot-starter-aop

// Using starter in pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## Application Structure

### Recommended Package Structure

```
com.example.myapp
├── MyApplication.java           // Main application class
├── controller
│   ├── UserController.java     // REST controllers
│   └── ProductController.java
├── service
│   ├── UserService.java        // Business logic
│   └── ProductService.java
├── repository
│   ├── UserRepository.java     // Data access
│   └── ProductRepository.java
├── model
│   ├── User.java               // Domain entities
│   └── Product.java
├── dto
│   ├── UserDTO.java            // Data transfer objects
│   └── UserResponseDTO.java
├── config
│   ├── SecurityConfig.java     // Configuration classes
│   └── DatabaseConfig.java
└── exception
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

### Layered Architecture

```java
// Controller Layer
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }
}

// Service Layer
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponseDTO(user);
    }
}

// Repository Layer
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findActiveUsers();
}
```

---

## Configuration Management

### Application Properties

```properties
# application.properties

# Server configuration
server.port=8080
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging configuration
logging.level.root=INFO
logging.level.com.example.myapp=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Spring Boot Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### YAML Configuration

```yaml
# application.yml

server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    root: INFO
    com.example.myapp: DEBUG

app:
  name: My Application
  version: 1.0.0
  description: Spring Boot Demo Application
```

### Configuration Properties

```java
// Using @ConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String version;
    private String description;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

// Enable configuration properties
@Configuration
@EnableConfigurationProperties(AppConfig.class)
public class AppConfiguration {
    // Configuration classes
}

// Using @Value
@Component
public class MyComponent {
    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${server.port:8080}")
    private int serverPort;
}
```

### Profile-Specific Configuration

```properties
# application.properties (default)
app.name=My Application
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb

# application-dev.properties (development profile)
spring.profiles.active=dev
app.name=My Application (Dev)
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb_dev
logging.level.com.example.myapp=DEBUG

# application-prod.properties (production profile)
app.name=My Application (Production)
spring.datasource.url=jdbc:postgresql://prod-db:5432/mydb
logging.level.com.example.myapp=INFO

# Activate profile via:
# - application.properties: spring.profiles.active=dev
# - Command line: java -jar app.jar --spring.profiles.active=prod
# - Environment variable: SPRING_PROFILES_ACTIVE=dev
```

---

## Dependency Injection

### Constructor Injection (Recommended)

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Constructor injection (Spring 4.3+ doesn't need @Autowired)
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void registerUser(User user) {
        userRepository.save(user);
        emailService.sendWelcomeEmail(user);
    }
}

// Controller with constructor injection
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }
}
```

### Setter Injection

```java
@Service
public class OrderService {
    private InventoryService inventoryService;

    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void processOrder(Order order) {
        inventoryService.checkAvailability(order.getItems());
    }
}
```

### Field Injection

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheService cacheService;

    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
```

### Component Scanning

```java
// Component types
@Component        // Generic component
@Service         // Service layer
@Repository      // Repository layer (includes exception translation)
@Controller       // Controller layer
@RestController  // REST controller (@Controller + @ResponseBody)

// Custom component
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email != null && email.contains("@");
    }
}

// Component scanning
@SpringBootApplication
@ComponentScan(basePackages = "com.example.myapp")
public class DemoApplication {
    // Scans for components in specified package
}
```

### Bean Scopes

```java
// Bean scopes
@Scope("singleton")    // Default - one instance per container
@Scope("prototype")    // New instance per request
@Scope("request")      // One instance per HTTP request
@Scope("session")      // One instance per HTTP session
@Scope("application")  // One instance per ServletContext

// Singleton bean (default)
@Service
@Scope("singleton")
public class ConfigurationService {
    // Single instance shared across application
}

// Prototype bean
@Component
@Scope("prototype")
public class RequestProcessor {
    // New instance each time it's requested
}

// Using @Lazy
@Service
public class UserService {
    private final EmailService emailService;

    @Lazy
    @Autowired
    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

### Conditional Bean Creation

```java
// Conditional beans based on property
@Configuration
public class DatabaseConfig {

    @Bean
    @ConditionalOnProperty(name = "app.database.type", havingValue = "mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/mydb")
            .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.database.type", havingValue = "postgres")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/mydb")
            .build();
    }
}

// Conditional based on class presence
@Configuration
@ConditionalOnClass(name = "org.springframework.amqp.rabbit.connection.ConnectionFactory")
public class RabbitMQConfig {
    // Bean definitions for RabbitMQ
}

// Conditional based on bean presence
@Configuration
@ConditionalOnBean(DataSource.class)
public class JPAConfig {
    // Bean definitions requiring DataSource
}
```

---

## Auto-Configuration

### Understanding Auto-Configuration

```java
// Auto-configuration happens through:
// 1. @EnableAutoConfiguration annotation
// 2. spring.factories file (Spring Boot 2.x) or
//    META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (Spring Boot 3.x)

// Disable specific auto-configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoApplication {
    // Disable database auto-configuration
}

// Using properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Creating Custom Auto-Configuration

```java
// Custom auto-configuration class
@Configuration
@ConditionalOnClass(MyService.class)
@ConditionalOnMissingBean(MyService.class)
@EnableConfigurationProperties(MyServiceProperties.class)
public class MyServiceAutoConfiguration {

    private final MyServiceProperties properties;

    public MyServiceAutoConfiguration(MyServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public MyService myService() {
        return new MyService(properties.getApiKey(), properties.getEndpoint());
    }
}

// Configuration properties
@ConfigurationProperties(prefix = "my.service")
public class MyServiceProperties {
    private String apiKey;
    private String endpoint;

    // Getters and setters
}

// Register auto-configuration (Spring Boot 3.x)
// File: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.example.autoconfigure.MyServiceAutoConfiguration
```

### Conditional Annotations

```java
// Conditional on property
@Bean
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
public FeatureService featureService() {
    return new FeatureService();
}

// Conditional on class
@Bean
@ConditionalOnClass(name = "org.springframework.cache.Cache")
public CacheService cacheService() {
    return new CacheService();
}

// Conditional on bean
@Bean
@ConditionalOnBean(DataSource.class)
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}

// Conditional on missing bean
@Bean
@ConditionalOnMissingBean(UserService.class)
public UserService userService() {
    return new UserService();
}

// Conditional on expression
@Bean
@ConditionalOnExpression("${feature.enabled:false}")
public ExperimentalService experimentalService() {
    return new ExperimentalService();
}
```

---

## REST API Development

### REST Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET all users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    // GET users with pagination
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getUsersPaginated(page, size));
    }

    // POST create user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO created = userService.createUser(userDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    // PUT update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updated);
    }

    // PATCH partial update
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> patchUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        UserResponseDTO patched = userService.patchUser(id, updates);
        return ResponseEntity.ok(patched);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @RequestParam String name,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(userService.searchUsers(name, email));
    }
}
```

### Request Handling

```java
// Request parameters
@GetMapping("/users")
public List<User> getUsers(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    // Handle request parameters
}

// Path variables
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    // Handle path variable
}

// Request body
@PostMapping("/users")
public User createUser(@RequestBody UserDTO userDTO) {
    // Handle request body
}

// Request headers
@GetMapping("/users")
public List<User> getUsers(
        @RequestHeader("Authorization") String auth,
        @RequestHeader(value = "Accept", defaultValue = "application/json") String accept) {
    // Handle request headers
}

// Request cookies
@GetMapping("/users")
public List<User> getUsers(@CookieValue("sessionId") String sessionId) {
    // Handle cookie value
}

// Multiple path variables
@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(
        @PathVariable Long userId,
        @PathVariable Long orderId) {
    // Handle multiple path variables
}
```

### Response Handling

```java
// ResponseEntity for full control
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntity.ok(user);
}

// Custom status codes
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
    User user = userService.save(userDTO);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/api/users/" + user.getId())
        .body(user);
}

// No content response
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
}

// Custom headers
@GetMapping("/users")
public ResponseEntity<List<User>> getUsers() {
    List<User> users = userService.findAll();
    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(users.size()))
        .header("X-Api-Version", "1.0")
        .body(users);
}

// Response body
@GetMapping("/users")
public List<User> getUsers() {
    // Uses @RestController, automatically serialized to JSON
    return userService.findAll();
}
```

### Exception Handling

```java
// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors,
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

// Custom exception
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Error response DTO
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    private long timestamp;

    // Constructors, getters, setters
}
```

### Validation

```java
// Entity with validation annotations
public class UserDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must be at most 120")
    private int age;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    // Getters and setters
}

// Custom validation annotation
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "Invalid password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Custom validator
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null &&
               password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[0-9].*");
    }
}

// Usage
public class UserDTO {
    @ValidPassword
    private String password;
}
```

### Content Negotiation

```java
// Producing different content types
@GetMapping(value = "/users/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE
})
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntity.ok(user);
}

// Consuming different content types
@PostMapping(value = "/users", consumes = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE
})
public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
    User user = userService.save(userDTO);
    return ResponseEntity.ok(user);
}

// Custom content type
@GetMapping(value = "/users/{id}", produces = "application/vnd.myapp.v1+json")
public ResponseEntity<User> getUserV1(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findById(id));
}

@GetMapping(value = "/users/{id}", produces = "application/vnd.myapp.v2+json")
public ResponseEntity<UserResponseDTO> getUserV2(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findResponseDTO(id));
}
```

---

## Data Access

### Spring Data JPA

```java
// Entity
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Many-to-one
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // One-to-many
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // Many-to-many
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Getters and setters
}

// Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Derived query methods
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    List<User> findByAgeGreaterThan(Integer age);

    // Custom JPQL query
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameLike(@Param("name") String name);

    // Native query
    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);

    // Pagination and sorting
    Page<User> findByDepartmentName(String departmentName, Pageable pageable);

    // Modifying query
    @Modifying
    @Query("UPDATE User u SET u.age = :age WHERE u.id = :id")
    void updateAge(@Param("id") Long id, @Param("age") Integer age);
}
```

### Transaction Management

```java
// Service with transactions
@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository,
                        InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public Order createOrder(Order order) {
        // Check inventory
        inventoryService.checkAvailability(order.getItems());

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update inventory
        inventoryService.updateInventory(order.getItems());

        return savedOrder;
    }

    @Transactional
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order createOrderWithNewTransaction(Order order) {
        // Always creates new transaction
        return orderRepository.save(order);
    }
}

// Transaction propagation types
// REQUIRED (default): Use existing or create new
// REQUIRES_NEW: Always create new
// MANDATORY: Must have existing transaction
// SUPPORTS: Use existing, execute non-transactionally if none
// NOT_SUPPORTED: Execute non-transactionally
// NEVER: Execute non-transactionally, throw exception if transaction exists
// NESTED: Execute within nested transaction
```

### Database Migrations

```java
// Using Flyway
@Configuration
public class FlywayConfig {
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();
        flyway.migrate();
        return flyway;
    }
}

// Migration file: V1__create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

// Migration file: V2__create_departments_table.sql
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD COLUMN department_id BIGINT REFERENCES departments(id);
```

---

## Security

### Basic Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### JWT Authentication

```java
// JWT configuration
@Configuration
@EnableWebSecurity
public class JWTSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// JWT filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

// JWT service
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }
}
```

### Method-Level Security

```java
// Enable method-level security
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    // Configuration
}

// Using security annotations
@Service
public class DocumentService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDocument(Long id) {
        // Only admins can delete documents
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public User getUserProfile(Long userId) {
        // Users can only view their own profile
    }

    @PostAuthorize("returnObject.owner == authentication.principal.username")
    public Document getDocument(Long id) {
        // Check after returning
        return documentRepository.findById(id).orElse(null);
    }

    @Secured("ROLE_USER")
    public void editDocument(Long id) {
        // Only users with ROLE_USER
    }

    @PreAuthorize("hasPermission(#document, 'WRITE')")
    public void saveDocument(Document document) {
        // Custom permission check
    }
}
```

---

## Testing

### Unit Testing

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponseDTO result = userService.getUser(1L);

        // Then
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUser(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found");
    }
}
```

### Integration Testing

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createUser_ShouldReturnCreatedStatus() throws Exception {
        String userJson = """
            {
                "name": "Jane Doe",
                "email": "jane@example.com",
                "age": 30
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Jane Doe"));
    }
}
```

### Test Slices

```java
// Web layer test
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        when(userService.getUser(1L))
            .thenReturn(new UserResponseDTO(1L, "John", "john@example.com"));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}

// Data layer test
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}

// Service layer test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        UserDTO userDTO = new UserDTO("John", "john@example.com", 30);
        User user = new User();
        when(userRepository.save(any())).thenReturn(user);

        UserResponseDTO result = userService.createUser(userDTO);

        verify(userRepository).save(any());
    }
}
```

---

## Actuator

### Enabling Actuator

```java
// pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

// application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    info:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

### Custom Health Indicators

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("url", dataSource.getMetaData().getURL())
                    .build();
            }
            return Health.down()
                .withDetail("error", "Connection is not valid")
                .build();
        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}

// Custom info contributor
@Component
public class AppInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app",
            Map.of(
                "name", "My Application",
                "version", "1.0.0",
                "description", "Spring Boot Demo"
            ));
    }
}
```

### Custom Metrics

```java
@Service
public class OrderService {

    private final MeterRegistry meterRegistry;
    private final Counter orderCounter;
    private final Timer orderTimer;

    public OrderService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.orderCounter = Counter.builder("orders.created")
            .description("Number of orders created")
            .tag("type", "online")
            .register(meterRegistry);
        this.orderTimer = Timer.builder("orders.processing.time")
            .description("Time taken to process orders")
            .register(meterRegistry);
    }

    public Order createOrder(Order order) {
        return orderTimer.record(() -> {
            Order savedOrder = processOrder(order);
            orderCounter.increment();
            return savedOrder;
        });
    }
}

// Custom metrics endpoint
@RestController
@RequestMapping("/metrics")
public class CustomMetricsController {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/custom")
    public Map<String, Object> getCustomMetrics() {
        return Map.of(
            "orders.created", meterRegistry.get("orders.created").counter().count(),
            "orders.processing.time", meterRegistry.get("orders.processing.time").timer().mean(TimeUnit.MILLISECONDS)
        );
    }
}
```

---

## Profiles

### Profile Configuration

```properties
# application.properties
spring.profiles.active=dev

# application-dev.properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/dev_db
logging.level.com.example.myapp=DEBUG

# application-prod.properties
server.port=8080
spring.datasource.url=jdbc:postgresql://prod-db:5432/prod_db
logging.level.com.example.myapp=INFO

# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

### Profile-Specific Beans

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/dev_db")
            .build();
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://prod-db:5432/prod_db")
            .build();
    }

    @Bean
    @Profile("!dev")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/default_db")
            .build();
    }
}

// Profile-specific service
@Service
@Profile("dev")
public class DevEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("[DEV] Email to: " + to + ", Subject: " + subject);
    }
}

@Service
@Profile("prod")
public class ProdEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        // Send real email
    }
}
```

### Programmatic Profile Activation

```java
@Component
public class ProfileService {

    @Autowired
    private Environment environment;

    public String getCurrentProfile() {
        return String.join(", ", environment.getActiveProfiles());
    }

    public boolean isDev() {
        return environment.acceptsProfiles("dev");
    }

    public boolean isProd() {
        return environment.acceptsProfiles("prod");
    }
}
```

---

## Summary

This reference covers Spring Boot fundamentals:

- **Introduction**: What is Spring Boot and its key features
- **Application Structure**: Recommended package structure and layered architecture
- **Configuration Management**: Properties, YAML, and configuration properties
- **Dependency Injection**: Constructor, setter, and field injection
- **Auto-Configuration**: Understanding and creating custom auto-configuration
- **REST API Development**: Controllers, request handling, validation
- **Data Access**: Spring Data JPA, transactions, migrations
- **Security**: Basic security, JWT authentication, method-level security
- **Testing**: Unit, integration, and slice testing
- **Actuator**: Health indicators, metrics, custom endpoints
- **Profiles**: Profile-specific configuration and beans
- **Properties**: Application properties and externalized configuration

Spring Boot provides a powerful framework for building production-ready applications with minimal configuration, focusing on convention over configuration and providing sensible defaults.
