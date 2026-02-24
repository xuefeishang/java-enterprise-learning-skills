# Enterprise Design Patterns Reference Guide

## Table of Contents

1. [GoF Patterns in Java](#gof-patterns-in-java)
2. [Enterprise Patterns](#enterprise-patterns)
3. [Data Access Patterns](#data-access-patterns)
4. [Service Layer Patterns](#service-layer-patterns)
5. [Presentation Patterns](#presentation-patterns)
6. [Integration Patterns](#integration-patterns)
7. [Concurrency Patterns](#concurrency-patterns)
8. [Distribution Patterns](#distribution-patterns)
9. [Security Patterns](#security-patterns)
10. [Testing Patterns](#testing-patterns)

---

## GoF Patterns in Java

### Creational Patterns

#### Singleton Pattern

```java
// Eager initialization
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Lazy initialization with double-checked locking
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Enum singleton (best practice)
public enum Singleton {
    INSTANCE;

    public void doSomething() {
        // Singleton functionality
    }
}

// Usage
Singleton singleton = Singleton.INSTANCE;
singleton.doSomething();
```

#### Factory Method Pattern

```java
// Product interface
public interface Transport {
    void deliver();
}

// Concrete products
public class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering by land in a truck.");
    }
}

public class Ship implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering by sea in a ship.");
    }
}

// Creator with factory method
public abstract class Logistics {
    public void planDelivery() {
        Transport transport = createTransport();
        transport.deliver();
    }

    protected abstract Transport createTransport();
}

// Concrete creators
public class RoadLogistics extends Logistics {
    @Override
    protected Transport createTransport() {
        return new Truck();
    }
}

public class SeaLogistics extends Logistics {
    @Override
    protected Transport createTransport() {
        return new Ship();
    }
}

// Usage
Logistics logistics = new RoadLogistics();
logistics.planDelivery();
```

#### Abstract Factory Pattern

```java
// Abstract factory
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factory 1
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

// Concrete factory 2
public class MacOSFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacOSButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacOSCheckbox();
    }
}

// Product interfaces
public interface Button {
    void paint();
}

public interface Checkbox {
    void paint();
}

// Client code
public class Application {
    private final GUIFactory factory;

    public Application(GUIFactory factory) {
        this.factory = factory;
    }

    public void createUI() {
        Button button = factory.createButton();
        Checkbox checkbox = factory.createCheckbox();

        button.paint();
        checkbox.paint();
    }
}

// Usage
GUIFactory factory = new WindowsFactory();
Application app = new Application(factory);
app.createUI();
```

#### Builder Pattern

```java
// Product class
public class Computer {
    private final String cpu;
    private final String ram;
    private final String storage;
    private final String gpu;

    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
    }

    // Getters
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getGpu() { return gpu; }

    // Builder
    public static class Builder {
        private String cpu;
        private String ram;
        private String storage;
        private String gpu;

        public Builder cpu(String cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder ram(String ram) {
            this.ram = ram;
            return this;
        }

        public Builder storage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder gpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        public Computer build() {
            return new Computer(this);
        }
    }
}

// Usage
Computer computer = new Computer.Builder()
    .cpu("Intel i9")
    .ram("32GB")
    .storage("1TB SSD")
    .gpu("RTX 3080")
    .build();

// Record-based builder (Java 16+)
record Computer(String cpu, String ram, String storage, String gpu) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String cpu = "Unknown";
        private String ram = "Unknown";
        private String storage = "Unknown";
        private String gpu = "Unknown";

        Builder cpu(String cpu) {
            this.cpu = cpu;
            return this;
        }

        Builder ram(String ram) {
            this.ram = ram;
            return this;
        }

        Builder storage(String storage) {
            this.storage = storage;
            return this;
        }

        Builder gpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        Computer build() {
            return new Computer(cpu, ram, storage, gpu);
        }
    }
}

// Usage
Computer computer = Computer.builder()
    .cpu("Intel i9")
    .ram("32GB")
    .build();
```

### Structural Patterns

#### Adapter Pattern

```java
// Target interface
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete adaptee
public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
        // Do nothing
    }
}

public class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Do nothing
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

// Adapter
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMusicPlayer;

    public MediaAdapter(String audioType) {
        if ("vlc".equalsIgnoreCase(audioType)) {
            advancedMusicPlayer = new VlcPlayer();
        } else if ("mp4".equalsIgnoreCase(audioType)) {
            advancedMusicPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String audioType, String fileName) {
        if ("vlc".equalsIgnoreCase(audioType)) {
            advancedMusicPlayer.playVlc(fileName);
        } else if ("mp4".equalsIgnoreCase(audioType)) {
            advancedMusicPlayer.playMp4(fileName);
        }
    }
}

// Client
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;

    @Override
    public void play(String audioType, String fileName) {
        if ("mp3".equalsIgnoreCase(audioType)) {
            System.out.println("Playing mp3 file: " + fileName);
        } else if ("vlc".equalsIgnoreCase(audioType) ||
                   "mp4".equalsIgnoreCase(audioType)) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
}
```

#### Decorator Pattern

```java
// Component interface
public interface Notifier {
    void send(String message);
}

// Concrete component
public class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending email: " + message);
    }
}

// Base decorator
public abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;

    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }

    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}

// Concrete decorators
public class SMSDecorator extends NotifierDecorator {
    public SMSDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS: " + message);
    }
}

public class SlackDecorator extends NotifierDecorator {
    public SlackDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Slack message: " + message);
    }
}

// Usage
Notifier notifier = new EmailNotifier();
notifier = new SMSDecorator(notifier);
notifier = new SlackDecorator(notifier);

notifier.send("Hello World!");
// Output:
// Sending email: Hello World!
// Sending SMS: Hello World!
// Sending Slack message: Hello World!
```

#### Facade Pattern

```java
// Complex subsystems
public class CPU {
    public void freeze() { System.out.println("CPU freeze"); }
    public void jump(long position) { System.out.println("CPU jump to " + position); }
    public void execute() { System.out.println("CPU execute"); }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory load " + data.length + " bytes to " + position);
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive read " + size + " bytes from LBA " + lba);
        return new byte[size];
    }
}

// Facade
public class ComputerFacade {
    private final CPU cpu;
    private final Memory memory;
    private final HardDrive hardDrive;

    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    public void start() {
        System.out.println("Starting computer...");
        cpu.freeze();
        memory.load(0x0000, hardDrive.read(0x0000, 1024));
        cpu.jump(0x0000);
        cpu.execute();
        System.out.println("Computer started");
    }
}

// Usage
ComputerFacade computer = new ComputerFacade();
computer.start();
```

### Behavioral Patterns

#### Strategy Pattern

```java
// Strategy interface
public interface PaymentStrategy {
    void pay(int amount);
}

// Concrete strategies
public class CreditCardStrategy implements PaymentStrategy {
    private final String name;
    private final String cardNumber;

    public CreditCardStrategy(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid with credit card " + cardNumber);
    }
}

public class PayPalStrategy implements PaymentStrategy {
    private final String emailId;

    public PayPalStrategy(String email) {
        this.emailId = email;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid using PayPal account " + emailId);
    }
}

// Context
public class ShoppingCart {
    private final List<Item> items;
    private PaymentStrategy paymentStrategy;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void checkout() {
        int amount = calculateTotal();
        paymentStrategy.pay(amount);
    }

    private int calculateTotal() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }
}

// Usage
ShoppingCart cart = new ShoppingCart();
cart.addItem(new Item("Item1", 100));
cart.addItem(new Item("Item2", 200));

cart.setPaymentStrategy(new CreditCardStrategy("John Doe", "1234-5678-9012-3456"));
cart.checkout();

cart.setPaymentStrategy(new PayPalStrategy("john@example.com"));
cart.checkout();
```

#### Observer Pattern

```java
// Subject interface
public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

// Observer interface
public interface Observer {
    void update(String message);
}

// Concrete subject
public class NewsAgency implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private String news;

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }

    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
}

// Concrete observers
public class NewsChannel implements Observer {
    private final String name;

    public NewsChannel(String name) {
        this.name = name;
    }

    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}

// Usage
NewsAgency agency = new NewsAgency();
agency.registerObserver(new NewsChannel("CNN"));
agency.registerObserver(new NewsChannel("BBC"));

agency.setNews("Breaking: New Java version released!");
```

---

## Enterprise Patterns

### DAO Pattern

```java
// DAO Interface
public interface UserDao {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(User user);
    List<User> findByEmail(String email);
}

// DAO Implementation with JDBC
@Repository
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                return ps;
            }, keyHolder);
            user.setId(keyHolder.getKey().longValue());
        } else {
            String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getId());
        }
        return user;
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public List<User> findByEmail(String email) {
        String sql = "SELECT id, name, email FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), email);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
}

// DAO Implementation with JPA
@Repository
public interface JpaUserDao extends JpaRepository<User, Long>, UserDao {
    @Override
    default User findById(Long id) {
        return this.findById(id).orElse(null);
    }

    @Override
    List<User> findByEmail(String email);
}
```

### DTO Pattern

```java
// Entity
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;  // Sensitive data

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // Getters and setters
}

// DTO for API response
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;

    // Constructors, getters, setters
}

// DTO for API request
public class UserCreateDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Getters and setters
}

// Mapper
@Component
public class UserMapper {
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toEntity(UserCreateDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        return users.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
}

// Usage in Service
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }
}
```

### Service Layer Pattern

```java
// Service Interface
public interface UserService {
    UserResponseDTO getUser(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO createUser(UserCreateDTO dto);
    UserResponseDTO updateUser(Long id, UserUpdateDTO dto);
    void deleteUser(Long id);
}

// Service Implementation
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userMapper.toResponseDTOList(userRepository.findAll());
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);

        // Send welcome email
        emailService.sendWelcomeEmail(savedUser);

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
```

---

## Summary

This reference covers enterprise design patterns:

- **GoF Patterns**: Singleton, Factory Method, Abstract Factory, Builder, Adapter, Decorator, Facade, Strategy, Observer
- **Enterprise Patterns**: DAO, DTO, Service Layer
- Additional patterns for enterprise applications

Design patterns provide proven solutions to common problems in software design and are essential for building maintainable, scalable enterprise applications.

For more detailed patterns and implementations, refer to specialized pattern books and resources.
