/**
 * HelloJava17.java
 *
 * Demonstrates Java 17 features:
 * - Records
 * - Text Blocks
 * - Switch Expressions
 * - Pattern Matching for instanceof
 * - Enhanced Pseudo-Random Number Generators
 * - Sealed Classes
 * - Stream API enhancements
 */

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

// Sealed class hierarchy
sealed interface Shape permits Circle, Rectangle, Square {
    double area();
}

record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

record Rectangle(double width, double height) implements Shape {
    @Override
    public double area() {
        return width * height;
    }
}

record Square(double side) implements Shape {
    @Override
    public double area() {
        return side * side;
    }
}

public class HelloJava17 {

    public static void main(String[] args) {
        demonstrateRecords();
        demonstrateTextBlocks();
        demonstrateSwitchExpressions();
        demonstratePatternMatching();
        demonstrateRandomGenerators();
        demonstrateSealedClasses();
        demonstrateStreamAPI();
    }

    // Records demonstration
    private static void demonstrateRecords() {
        System.out.println("=== Records ===");

        // Creating records
        Point point = new Point(3, 4);
        System.out.println("Point: " + point);

        // Record with validation
        User user = new User("Alice", 30);
        System.out.println("User: " + user);
        System.out.println("Is adult: " + user.isAdult());

        // Record in collections
        List<Point> points = List.of(
            new Point(0, 0),
            new Point(3, 4),
            new Point(5, 12)
        );

        points.forEach(p -> System.out.println("Distance from origin: " +
            distanceFromOrigin(p.x(), p.y())));

        System.out.println();
    }

    // Text Blocks demonstration
    private static void demonstrateTextBlocks() {
        System.out.println("=== Text Blocks ===");

        // Traditional string concatenation
        String jsonTraditional = "{\n" +
            "  \"name\": \"John\",\n" +
            "  \"age\": 30,\n" +
            "  \"email\": \"john@example.com\"\n" +
            "}";

        // Text block
        String jsonTextBlock = """
            {
              "name": "John",
              "age": 30,
              "email": "john@example.com"
            }
            """;

        System.out.println("Traditional JSON:");
        System.out.println(jsonTraditional);
        System.out.println("Text Block JSON:");
        System.out.println(jsonTextBlock);

        // SQL query with text block
        String query = """
            SELECT id, name, email
            FROM users
            WHERE age > :minAge
            AND status = :status
            ORDER BY name
            """;

        System.out.println("SQL Query:");
        System.out.println(query);
        System.out.println();
    }

    // Switch Expressions demonstration
    private static void demonstrateSwitchExpressions() {
        System.out.println("=== Switch Expressions ===");

        // Traditional switch statement
        int day = 3;
        String dayNameTraditional;
        switch (day) {
            case 1:
                dayNameTraditional = "Sunday";
                break;
            case 2:
                dayNameTraditional = "Monday";
                break;
            case 3:
                dayNameTraditional = "Tuesday";
                break;
            default:
                dayNameTraditional = "Unknown";
        }

        // Switch expression
        String dayNameExpression = switch (day) {
            case 1 -> "Sunday";
            case 2 -> "Monday";
            case 3 -> "Tuesday";
            case 4 -> "Wednesday";
            case 5 -> "Thursday";
            case 6 -> "Friday";
            case 7 -> "Saturday";
            default -> "Unknown";
        };

        System.out.println("Traditional: " + dayNameTraditional);
        System.out.println("Expression: " + dayNameExpression);

        // Multi-case labels
        int month = 6;
        String season = switch (month) {
            case 12, 1, 2 -> "Winter";
            case 3, 4, 5 -> "Spring";
            case 6, 7, 8 -> "Summer";
            case 9, 10, 11 -> "Autumn";
            default -> "Invalid month";
        };

        System.out.println("Month " + month + " is in " + season);

        // Switch with yield for complex logic
        String result = switch (day) {
            case 1, 7 -> {
                System.out.println("Weekend!");
                yield "Weekend";
            }
            case 2, 3, 4, 5, 6 -> {
                System.out.println("Weekday");
                yield "Weekday";
            }
            default -> throw new IllegalArgumentException("Invalid day");
        };

        System.out.println("Result: " + result);
        System.out.println();
    }

    // Pattern Matching for instanceof
    private static void demonstratePatternMatching() {
        System.out.println("=== Pattern Matching for instanceof ===");

        // Traditional instanceof
        Object obj = "Hello, World!";
        if (obj instanceof String) {
            String str = (String) obj;
            System.out.println("String length: " + str.length());
        }

        // Pattern matching (Java 16+)
        Object obj2 = "Hello";
        if (obj2 instanceof String s && s.length() > 5) {
            System.out.println("Long string: " + s);
        }

        // Pattern matching with negation
        Object obj3 = 123;
        if (!(obj3 instanceof String s)) {
            System.out.println("Not a string: " + obj3);
        }

        // Pattern matching in switch expressions (Preview feature)
        Object obj4 = "Hello";
        String result = switch (obj4) {
            case String s when s.length() > 10 -> "Long string: " + s;
            case String s -> "Short string: " + s;
            case Integer i -> "Integer: " + i;
            default -> "Unknown type: " + obj4.getClass().getSimpleName();
        };

        System.out.println("Switch pattern result: " + result);
        System.out.println();
    }

    // Enhanced Pseudo-Random Number Generators
    private static void demonstrateRandomGenerators() {
        System.out.println("=== Enhanced Random Generators ===");

        // Get default random generator
        RandomGenerator random = RandomGenerator.getDefault();
        System.out.println("Default generator: " + random.getClass().getName());

        // Generate random values
        int randomInt = random.nextInt(100);
        double randomDouble = random.nextDouble();
        boolean randomBoolean = random.nextBoolean();

        System.out.println("Random int (0-99): " + randomInt);
        System.out.println("Random double: " + randomDouble);
        System.out.println("Random boolean: " + randomBoolean);

        // Generate random value in range
        int rangeValue = random.nextInt(10, 20);
        double rangeDouble = random.nextDouble(1.0, 10.0);

        System.out.println("Random int (10-19): " + rangeValue);
        System.out.println("Random double (1.0-10.0): " + rangeDouble);

        // List available algorithms
        System.out.println("Available random algorithms:");
        RandomGeneratorFactory.all()
            .limit(5)
            .forEach(factory -> System.out.println(
                "  " + factory.name() + " - " + factory.group()
            ));

        // Use specific algorithm
        RandomGenerator xoroshiro = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create();
        System.out.println("Xoroshiro random: " + xoroshiro.nextInt(100));

        // Stream of random numbers
        System.out.println("5 random integers:");
        random.ints(5).forEach(n -> System.out.println("  " + n));

        System.out.println();
    }

    // Sealed Classes demonstration
    private static void demonstrateSealedClasses() {
        System.out.println("=== Sealed Classes ===");

        // Create shapes
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        Shape square = new Square(3);

        System.out.println("Circle area: " + circle.area());
        System.out.println("Rectangle area: " + rectangle.area());
        System.out.println("Square area: " + square.area());

        // Pattern matching on sealed classes
        Shape shape = new Circle(5);
        String description = switch (shape) {
            case Circle c -> "Circle with radius " + c.radius();
            case Rectangle r -> "Rectangle " + r.width() + "x" + r.height();
            case Square s -> "Square with side " + s.side();
        };

        System.out.println("Description: " + description);

        // List of shapes
        List<Shape> shapes = List.of(
            new Circle(5),
            new Rectangle(4, 6),
            new Square(3),
            new Circle(3)
        );

        System.out.println("Shape areas:");
        shapes.forEach(s -> System.out.println("  " + s.area()));

        // Filter circles
        System.out.println("Circles only:");
        shapes.stream()
            .filter(s -> s instanceof Circle)
            .forEach(s -> System.out.println("  " + s));

        System.out.println();
    }

    // Stream API enhancements
    private static void demonstrateStreamAPI() {
        System.out.println("=== Stream API ===");

        List<String> words = List.of(
            "apple", "banana", "cherry", "date", "elderberry"
        );

        // Stream operations
        System.out.println("Words with length > 5:");
        words.stream()
            .filter(w -> w.length() > 5)
            .forEach(w -> System.out.println("  " + w));

        // Collect to list with mapping
        System.out.println("Words and lengths:");
        Map<String, Integer> wordLengths = words.stream()
            .collect(Collectors.toMap(
                w -> w,
                String::length
            ));

        wordLengths.forEach((k, v) ->
            System.out.println("  " + k + ": " + v));

        // Partitioning
        Map<Boolean, List<String>> partitioned = words.stream()
            .collect(Collectors.partitioningBy(w -> w.length() > 5));

        System.out.println("Long words: " + partitioned.get(true));
        System.out.println("Short words: " + partitioned.get(false));

        // Grouping
        Map<Integer, List<String>> byLength = words.stream()
            .collect(Collectors.groupingBy(String::length));

        System.out.println("Grouped by length:");
        byLength.forEach((length, list) ->
            System.out.println("  Length " + length + ": " + list));

        // Reduce
        String concatenated = words.stream()
            .reduce("", (a, b) -> a + ", " + b);

        System.out.println("Concatenated: " + concatenated);

        System.out.println();
    }

    // Helper methods
    private static double distanceFromOrigin(int x, int y) {
        return Math.sqrt(x * x + y * y);
    }
}

// Record examples
record Point(int x, int y) {
    Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative");
        }
    }

    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
}

record User(String name, int age) {
    public User {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
    }

    public boolean isAdult() {
        return age >= 18;
    }

    public String greet() {
        return String.format("Hello, %s! You are %d years old.", name, age);
    }
}
