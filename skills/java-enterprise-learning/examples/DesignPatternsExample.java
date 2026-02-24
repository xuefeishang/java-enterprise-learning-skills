/**
 * DesignPatternsExample.java
 *
 * Demonstrates common design patterns in Java:
 * - Singleton Pattern
 * - Factory Method Pattern
 * - Builder Pattern
 * - Strategy Pattern
 * - Observer Pattern
 * - Decorator Pattern
 * - Adapter Pattern
 * - Template Method Pattern
 */

import java.util.*;
import java.util.function.*;

public class DesignPatternsExample {

    public static void main(String[] args) {
        demonstrateSingleton();
        demonstrateFactoryMethod();
        demonstrateBuilder();
        demonstrateStrategy();
        demonstrateObserver();
        demonstrateDecorator();
        demonstrateAdapter();
        demonstrateTemplateMethod();
    }

    // Singleton Pattern
    private static void demonstrateSingleton() {
        System.out.println("=== Singleton Pattern ===");

        // Get singleton instance
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        System.out.println("Instance 1 hash: " + instance1.hashCode());
        System.out.println("Instance 2 hash: " + instance2.hashCode());
        System.out.println("Same instance: " + (instance1 == instance2));

        instance1.connect();
        instance1.query("SELECT * FROM users");
        instance1.disconnect();

        System.out.println();
    }

    // Factory Method Pattern
    private static void demonstrateFactoryMethod() {
        System.out.println("=== Factory Method Pattern ===");

        // Create shapes using factory
        Shape circle = ShapeFactory.createShape("CIRCLE");
        Shape rectangle = ShapeFactory.createShape("RECTANGLE");
        Shape triangle = ShapeFactory.createShape("TRIANGLE");

        circle.draw();
        rectangle.draw();
        triangle.draw();

        System.out.println();
    }

    // Builder Pattern
    private static void demonstrateBuilder() {
        System.out.println("=== Builder Pattern ===");

        // Using builder
        Computer computer = Computer.Builder()
            .cpu("Intel i9")
            .ram("32GB")
            .storage("1TB SSD")
            .gpu("RTX 3080")
            .build();

        System.out.println("Computer built: " + computer);

        // Minimal configuration
        Computer basicComputer = Computer.Builder()
            .cpu("Intel i5")
            .ram("16GB")
            .build();

        System.out.println("Basic computer: " + basicComputer);

        System.out.println();
    }

    // Strategy Pattern
    private static void demonstrateStrategy() {
        System.out.println("=== Strategy Pattern ===");

        PaymentContext paymentContext = new PaymentContext();

        // Credit card payment
        paymentContext.setPaymentStrategy(new CreditCardPayment("John Doe", "1234-5678-9012-3456"));
        paymentContext.processPayment(100.0);

        // PayPal payment
        paymentContext.setPaymentStrategy(new PayPalPayment("john@example.com"));
        paymentContext.processPayment(50.0);

        // Cash payment
        paymentContext.setPaymentStrategy(new CashPayment());
        paymentContext.processPayment(25.0);

        System.out.println();
    }

    // Observer Pattern
    private static void demonstrateObserver() {
        System.out.println("=== Observer Pattern ===");

        NewsAgency agency = new NewsAgency();

        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");

        agency.registerObserver(cnn);
        agency.registerObserver(bbc);

        agency.setNews("Breaking: New Java version released!");
        agency.setNews("Update: Java 21 released with virtual threads");

        agency.removeObserver(bbc);
        agency.setNews("CNN exclusive: Java performance improvements");

        System.out.println();
    }

    // Decorator Pattern
    private static void demonstrateDecorator() {
        System.out.println("=== Decorator Pattern ===");

        Notifier notifier = new EmailNotifier();

        // Add SMS notification
        notifier = new SMSDecorator(notifier);
        notifier.send("Hello World!");

        // Add Slack notification
        notifier = new SlackDecorator(notifier);
        notifier.send("Important notification!");

        System.out.println();
    }

    // Adapter Pattern
    private static void demonstrateAdapter() {
        System.out.println("=== Adapter Pattern ===");

        // Target interface
        MediaPlayer player = new AudioPlayer();

        player.play("mp3", "song.mp3");

        // Using adapter for unsupported formats
        player.play("vlc", "movie.vlc");
        player.play("mp4", "video.mp4");

        System.out.println();
    }

    // Template Method Pattern
    private static void demonstrateTemplateMethod() {
        System.out.println("=== Template Method Pattern ===");

        DataProcessor csvProcessor = new CSVDataProcessor();
        DataProcessor jsonProcessor = new JSONDataProcessor();

        System.out.println("Processing CSV:");
        csvProcessor.process();

        System.out.println("\nProcessing JSON:");
        jsonProcessor.process();

        System.out.println();
    }
}

// ==================== Singleton Pattern ====================

class DatabaseConnection {
    private static DatabaseConnection instance;

    private boolean connected;

    private DatabaseConnection() {
        this.connected = false;
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void connect() {
        if (!connected) {
            System.out.println("Connecting to database...");
            connected = true;
        } else {
            System.out.println("Already connected");
        }
    }

    public void disconnect() {
        if (connected) {
            System.out.println("Disconnecting from database...");
            connected = false;
        }
    }

    public void query(String sql) {
        if (connected) {
            System.out.println("Executing query: " + sql);
        } else {
            System.out.println("Not connected!");
        }
    }

    @Override
    public String toString() {
        return "DatabaseConnection{connected=" + connected + "}";
    }
}

// ==================== Factory Method Pattern ====================

interface Shape {
    void draw();
}

class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a circle");
    }
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a rectangle");
    }
}

class Triangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing a triangle");
    }
}

class ShapeFactory {
    public static Shape createShape(String type) {
        switch (type.toUpperCase()) {
            case "CIRCLE":
                return new Circle();
            case "RECTANGLE":
                return new Rectangle();
            case "TRIANGLE":
                return new Triangle();
            default:
                throw new IllegalArgumentException("Unknown shape type: " + type);
        }
    }
}

// ==================== Builder Pattern ====================

class Computer {
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

    public static Builder Builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Computer{" +
            "cpu='" + cpu + '\'' +
            ", ram='" + ram + '\'' +
            ", storage='" + storage + '\'' +
            ", gpu='" + gpu + '\'' +
            '}';
    }

    public static class Builder {
        private String cpu = "Unknown";
        private String ram = "Unknown";
        private String storage = "Unknown";
        private String gpu = "Unknown";

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

// ==================== Strategy Pattern ====================

interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    private final String name;
    private final String cardNumber;

    public CreditCardPayment(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f using credit card %s (%s)%n",
            amount, cardNumber, name);
    }
}

class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f using PayPal account %s%n",
            amount, email);
    }
}

class CashPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f in cash%n", amount);
    }
}

class PaymentContext {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(double amount) {
        paymentStrategy.pay(amount);
    }
}

// ==================== Observer Pattern ====================

interface Observer {
    void update(String news);
}

interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

class NewsAgency implements Subject {
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

class NewsChannel implements Observer {
    private final String name;

    public NewsChannel(String name) {
        this.name = name;
    }

    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}

// ==================== Decorator Pattern ====================

interface Notifier {
    void send(String message);
}

class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending email: " + message);
    }
}

abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;

    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }

    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}

class SMSDecorator extends NotifierDecorator {
    public SMSDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS: " + message);
    }
}

class SlackDecorator extends NotifierDecorator {
    public SlackDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Slack message: " + message);
    }
}

// ==================== Adapter Pattern ====================

interface MediaPlayer {
    void play(String audioType, String fileName);
}

interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
        // Do nothing
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Do nothing
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMediaPlayer;

    public MediaAdapter(String audioType) {
        if ("vlc".equalsIgnoreCase(audioType)) {
            advancedMediaPlayer = new VlcPlayer();
        } else if ("mp4".equalsIgnoreCase(audioType)) {
            advancedMediaPlayer = new Mp4Player();
        }
    }

    @Override
    public void play(String audioType, String fileName) {
        if ("vlc".equalsIgnoreCase(audioType)) {
            advancedMediaPlayer.playVlc(fileName);
        } else if ("mp4".equalsIgnoreCase(audioType)) {
            advancedMediaPlayer.playMp4(fileName);
        }
    }
}

class AudioPlayer implements MediaPlayer {
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

// ==================== Template Method Pattern ====================

abstract class DataProcessor {
    // Template method
    public final void process() {
        loadData();
        validateData();
        transformData();
        saveData();
    }

    // Steps implemented by subclasses
    protected abstract void loadData();
    protected abstract void validateData();
    protected abstract void transformData();
    protected abstract void saveData();
}

class CSVDataProcessor extends DataProcessor {
    @Override
    protected void loadData() {
        System.out.println("Loading data from CSV file");
    }

    @Override
    protected void validateData() {
        System.out.println("Validating CSV data structure");
    }

    @Override
    protected void transformData() {
        System.out.println("Transforming CSV data");
    }

    @Override
    protected void saveData() {
        System.out.println("Saving processed CSV data");
    }
}

class JSONDataProcessor extends DataProcessor {
    @Override
    protected void loadData() {
        System.out.println("Loading data from JSON file");
    }

    @Override
    protected void validateData() {
        System.out.println("Validating JSON schema");
    }

    @Override
    protected void transformData() {
        System.out.println("Transforming JSON data");
    }

    @Override
    protected void saveData() {
        System.out.println("Saving processed JSON data");
    }
}
