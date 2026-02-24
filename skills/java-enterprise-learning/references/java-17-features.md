# Java 17 Features Reference Guide

## Table of Contents

1. [Overview](#overview)
2. [Records](#records)
3. [Pattern Matching](#pattern-matching)
4. [Sealed Classes](#sealed-classes)
5. [Text Blocks](#text-blocks)
6. [Switch Expressions](#switch-expressions)
7. [Enhanced Pseudo-Random Number Generators](#enhanced-pseudo-random-number-generators)
8. [Foreign Function & Memory API (Preview)](#foreign-function--memory-api-preview)
9. [Vector API (Second Incubator)](#vector-api-second-incubator)
10. [Context-Specific Deserialization Filters](#context-specific-deserialization-filters)
11. [Additional Features](#additional-features)
12. [Migration Guide](#migration-guide)

---

## Overview

### Java 17 LTS Release

Java 17 is a Long-Term Support (LTS) release, following Java 11 LTS. It includes many features that were finalized from previous versions:

- **Records** (JEP 395) - Final in Java 16
- **Pattern Matching for instanceof** (JEP 394) - Final in Java 16
- **Sealed Classes** (JEP 409) - Final in Java 17
- **Text Blocks** (JEP 378) - Final in Java 15
- **Switch Expressions** (JEP 361) - Final in Java 14
- **Enhanced Pseudo-Random Number Generators** (JEP 356) - Final in Java 17

### Version History

| Feature | Introduced | Finalized | JEP |
|---------|-----------|------------|-----|
| Switch Expressions | Java 12 (Preview) | Java 14 | 361 |
| Text Blocks | Java 13 (Preview) | Java 15 | 378 |
| Records | Java 14 (Preview) | Java 16 | 395 |
| Pattern Matching instanceof | Java 14 (Preview) | Java 16 | 394 |
| Sealed Classes | Java 15 (Preview) | Java 17 | 409 |
| Enhanced Random Generators | Java 17 | Java 17 | 356 |

---

## Records

### Record Basics

```java
// Basic record declaration
record Point(int x, int y) {}

// Usage
Point p1 = new Point(3, 4);
System.out.println(p1.x());  // 3
System.out.println(p1.y());  // 4
System.out.println(p1);      // Point[x=3, y=4]

// Record with multiple fields
record Person(String name, int age, String email) {}

Person person = new Person("Alice", 30, "alice@example.com");
System.out.println(person.name());   // Alice
System.out.println(person.age());    // 30
System.out.println(person.email());  // alice@example.com
```

### Record Methods

```java
// Records automatically generate:
// - Constructor (canonical)
// - Accessor methods (not getters)
// - equals()
// - hashCode()
// - toString()

record User(String username, String password) {}

User user1 = new User("alice", "secret123");
User user2 = new User("alice", "secret123");

// Automatic equals
System.out.println(user1.equals(user2));  // true

// Automatic hashCode
System.out.println(user1.hashCode() == user2.hashCode());  // true

// Automatic toString
System.out.println(user1);  // User[username=alice, password=secret123]
```

### Custom Records

```java
// Record with custom constructor
record Rectangle(double width, double height) {
    public Rectangle {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
    }
}

Rectangle rect = new Rectangle(5, 3);
// Rectangle invalid = new Rectangle(-1, 3);  // Throws IllegalArgumentException

// Record with additional methods
record Circle(double radius) {
    public Circle {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
    }

    public double area() {
        return Math.PI * radius * radius;
    }

    public double circumference() {
        return 2 * Math.PI * radius;
    }

    public Circle scaled(double factor) {
        return new Circle(radius * factor);
    }
}

Circle circle = new Circle(5);
System.out.println("Area: " + circle.area());  // 78.5398...
System.out.println("Circumference: " + circle.circumference());  // 31.4159...

Circle scaled = circle.scaled(2);
System.out.println("Scaled radius: " + scaled.radius());  // 10
```

### Compact Constructor

```java
// Compact constructor - can access and validate components
record Email(String address) {
    public Email {
        address = address.toLowerCase().trim();
        if (!address.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + address);
        }
    }
}

Email email = new Email("  ALICE@EXAMPLE.COM  ");
System.out.println(email.address());  // alice@example.com

// Compact constructor for validation
record Age(int value) {
    public Age {
        if (value < 0 || value > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }

    public boolean isAdult() {
        return value >= 18;
    }
}
```

### Record with Static Members

```java
record Currency(String code, String symbol) {
    // Static field
    private static final Map<String, Currency> CURRENCIES = Map.of(
        "USD", new Currency("USD", "$"),
        "EUR", new Currency("EUR", "€"),
        "GBP", new Currency("GBP", "£")
    );

    // Static factory method
    public static Currency of(String code) {
        return CURRENCIES.get(code.toUpperCase());
    }

    // Static method
    public static Set<String> availableCodes() {
        return CURRENCIES.keySet();
    }
}

Currency usd = Currency.of("USD");
System.out.println(usd.symbol());  // $
System.out.println(Currency.availableCodes());  // [USD, EUR, GBP]
```

### Records with Generic Types

```java
// Generic record
record Pair<T, U>(T first, U second) {}

Pair<String, Integer> pair1 = new Pair<>("Age", 30);
Pair<String, Double> pair2 = new Pair<>("Price", 19.99);

// Generic record with bounds
record Container<T extends Number>(T value) {
    public double doubleValue() {
        return value.doubleValue();
    }
}

Container<Integer> intContainer = new Container<>(42);
System.out.println(intContainer.doubleValue());  // 42.0

Container<Double> doubleContainer = new Container<>(3.14);
System.out.println(doubleContainer.doubleValue());  // 3.14
```

### Records with Nested Types

```java
// Nested records
record Address(String street, String city, String country) {}
record Contact(String email, String phone) {}

record Person(String name, Address address, Contact contact) {}

Address address = new Address("123 Main St", "Springfield", "USA");
Contact contact = new Contact("john@example.com", "555-1234");
Person person = new Person("John Doe", address, contact);

System.out.println(person.address().city());  // Springfield
```

### Records in Collections

```java
// Records as map keys
record Key(String namespace, String id) {}

Map<Key, String> map = new HashMap<>();
map.put(new Key("user", "1"), "Alice");
map.put(new Key("user", "2"), "Bob");

System.out.println(map.get(new Key("user", "1")));  // Alice

// Records in sets
Set<Point> points = new HashSet<>();
points.add(new Point(0, 0));
points.add(new Point(1, 1));
points.add(new Point(0, 0));  // Duplicate, not added

System.out.println(points.size());  // 2

// Records in lists
List<Point> pointList = new ArrayList<>();
pointList.add(new Point(3, 4));
pointList.add(new Point(5, 12));
pointList.add(new Point(8, 15));

pointList.forEach(p -> System.out.println(p));
```

---

## Pattern Matching

### Pattern Matching for instanceof

```java
// Traditional instanceof with casting
Object obj = "Hello World";

if (obj instanceof String) {
    String str = (String) obj;
    System.out.println("String length: " + str.length());
}

// Pattern matching (Java 16+)
Object obj = "Hello World";

if (obj instanceof String str) {
    System.out.println("String length: " + str.length());  // str is automatically cast
}

// Works with control flow
Object obj = "Hello";

if (obj instanceof String s && s.length() > 5) {
    System.out.println("Long string: " + s);  // s is in scope
}

// Conditional pattern
Object obj = "123";

if (obj instanceof Number n) {
    System.out.println("Number: " + n.doubleValue());
} else if (obj instanceof String s && s.matches("\\d+")) {
    System.out.println("Numeric string: " + s);
}

// Pattern matching with negation
Object obj = "Hello";

if (!(obj instanceof String s)) {
    System.out.println("Not a string");
} else {
    System.out.println("String: " + s);  // s is in scope
}
```

### Pattern Matching in Switch (Preview in Java 17)

```java
// Pattern matching in switch expressions (Preview feature in Java 17)
Object obj = "Hello";

String result = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Integer: " + i;
    case Long l -> "Long: " + l;
    case Double d -> "Double: " + d;
    case null -> "null value";
    default -> "Unknown type";
};

System.out.println(result);

// Guarded pattern matching
Object obj = 42;

String result = switch (obj) {
    case Integer i when i > 0 -> "Positive integer: " + i;
    case Integer i when i < 0 -> "Negative integer: " + i;
    case Integer i -> "Zero";
    case String s when s.length() > 10 -> "Long string: " + s;
    case String s -> "Short string: " + s;
    default -> "Other: " + obj;
};

// Dominance checking - more specific cases must come first
String result = switch (obj) {
    case Integer i when i > 0 -> "Positive";
    case Integer i -> "Integer";  // This handles all other integers
    case String s -> "String";
    default -> "Other";
};
```

### Deconstructor Patterns (Future Feature)

```java
// Deconstructor patterns for records (future feature)
record Point(int x, int y) {}
record Rectangle(Point topLeft, Point bottomRight) {}

Rectangle rect = new Rectangle(new Point(0, 0), new Point(10, 10));

// Pattern matching on nested records
if (rect instanceof Rectangle(Point(int x1, int y1), Point(int x2, int y2))) {
    System.out.printf("Rectangle from (%d,%d) to (%d,%d)%n", x1, y1, x2, y2);
}
```

---

## Sealed Classes

### Sealed Class Basics

```java
// Define sealed class with permitted subclasses
public sealed class Shape
    permits Circle, Rectangle, Square {
    private final String name;

    public Shape(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double area();
}

// Final subclass (cannot be extended)
public final class Circle extends Shape {
    private final double radius;

    public Circle(double radius) {
        super("Circle");
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    public double getRadius() {
        return radius;
    }
}

// Final subclass
public final class Rectangle extends Shape {
    private final double width;
    private final double height;

    public Rectangle(double width, double height) {
        super("Rectangle");
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}

// Sealed subclass (can be extended further)
public sealed class Square extends Rectangle permits SpecialSquare {
    public Square(double side) {
        super(side, side);
    }

    public double getSide() {
        return getWidth();
    }
}

// Final subclass of sealed subclass
public final class SpecialSquare extends Square {
    private final String color;

    public SpecialSquare(double side, String color) {
        super(side);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
```

### Sealed Interface

```java
// Sealed interface
public sealed interface Vehicle
    permits Car, Motorcycle, Truck {

    String getType();
    int getMaxSpeed();
}

// Final implementations
public final class Car implements Vehicle {
    @Override
    public String getType() {
        return "Car";
    }

    @Override
    public int getMaxSpeed() {
        return 200;
    }
}

public final class Motorcycle implements Vehicle {
    @Override
    public String getType() {
        return "Motorcycle";
    }

    @Override
    public int getMaxSpeed() {
        return 250;
    }
}

public final class Truck implements Vehicle {
    @Override
    public String getType() {
        return "Truck";
    }

    @Override
    public int getMaxSpeed() {
        return 120;
    }
}
```

### Sealed Classes with Pattern Matching

```java
// Sealed class with pattern matching
public sealed class Result permits Success, Failure {
    public sealed static class Success extends Result permits NetworkSuccess {
        private final String message;

        public Success(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public final static class NetworkSuccess extends Success {
        private final int statusCode;

        public NetworkSuccess(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public final static class Failure extends Result {
        private final Exception error;

        public Failure(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return error;
        }
    }
}

// Usage with pattern matching
public void handleResult(Result result) {
    String output = switch (result) {
        case NetworkSuccess ns ->
            String.format("Success: %s (Status: %d)", ns.getMessage(), ns.getStatusCode());
        case Success s -> String.format("Success: %s", s.getMessage());
        case Failure f -> String.format("Failure: %s", f.getError().getMessage());
    };

    System.out.println(output);
}
```

### Sealed Class Rules

```java
// Rules for sealed classes:

// 1. All permitted subclasses must be in the same module or package
package com.example.shapes;

public sealed class Shape permits Circle, Rectangle, Square {
    // All permitted subclasses are in com.example.shapes
}

// 2. Permitted subclasses must use: final, sealed, or non-sealed
public final class Circle extends Shape { }  // Cannot be extended

public sealed class Rectangle extends Shape permits RoundedRectangle { }  // Can be extended

public non-sealed class Square extends Shape { }  // Can be extended without restriction

// 3. Permitted subclass must be accessible
public sealed class Base permits Sub { }
// final class Sub extends Base { }  // OK - same package

// 4. All permitted subclasses must be declared
public sealed class A permits B, C { }
public final class B extends A { }
// public final class D extends A { }  // ERROR - not permitted

// 5. Sealed classes cannot be anonymous or local classes
// var obj = new Object() { };  // Cannot be sealed
```

---

## Text Blocks

### Text Block Basics

```java
// Traditional string concatenation
String json = "{\n" +
    "  \"name\": \"John\",\n" +
    "  \"age\": 30,\n" +
    "  \"email\": \"john@example.com\"\n" +
    "}";

// Text block (Java 15+)
String json = """
    {
      "name": "John",
      "age": 30,
      "email": "john@example.com"
    }
    """;

System.out.println(json);

// SQL query
String query = """
    SELECT id, name, email
    FROM users
    WHERE age > :minAge
    AND status = :status
    ORDER BY name
    """;
```

### Text Block Formatting

```java
// Indentation is stripped to the left margin
String indented = """
        Line 1
        Line 2
        Line 3
    """;

// Trailing spaces can be preserved with \s
String withTrailing = """
    Line 1   \s
    Line 2   \s
    Line 3   \s
    """;

// Escape sequences still work
String withEscapes = """
    Line 1\nLine 2
    Line 3\tTabbed
    Line 4\"quoted\"
    """;

// Prevent line break with backslash
String singleLine = """
    This is all \
    one line \
    because backslash \
    prevents line breaks
    """;
// Result: "This is all one line because backslash prevents line breaks"
```

### Text Block Methods

```java
// stripIndent() - removes incidental indentation
String block = """
        Hello
        World
    """;
String stripped = block.stripIndent();

// translateEscapes() - processes escape sequences
String withEscapes = "Hello\\nWorld\\t!";
String translated = withEscapes.translateEscapes();

// formatted() - string formatting with text blocks
String template = """
    Name: %s
    Age: %d
    Email: %s
    """;
String result = template.formatted("Alice", 30, "alice@example.com");
```

---

## Switch Expressions

### Switch Expressions

```java
// Traditional switch statement
int day = 3;
String dayName;
switch (day) {
    case 1:
        dayName = "Sunday";
        break;
    case 2:
        dayName = "Monday";
        break;
    case 3:
        dayName = "Tuesday";
        break;
    default:
        dayName = "Unknown";
}

// Switch expression (Java 14+)
int day = 3;
String dayName = switch (day) {
    case 1 -> "Sunday";
    case 2 -> "Monday";
    case 3 -> "Tuesday";
    case 4 -> "Wednesday";
    case 5 -> "Thursday";
    case 6 -> "Friday";
    case 7 -> "Saturday";
    default -> "Unknown";
};

// Multi-case labels
String season = switch (month) {
    case 12, 1, 2 -> "Winter";
    case 3, 4, 5 -> "Spring";
    case 6, 7, 8 -> "Summer";
    case 9, 10, 11 -> "Autumn";
    default -> throw new IllegalArgumentException("Invalid month");
};
```

### Switch with Yield

```java
// Complex case logic with yield
String result = switch (status) {
    case SUCCESS -> {
        System.out.println("Operation successful");
        yield "Success";  // Explicit yield
    }
    case FAILURE -> {
        System.err.println("Operation failed");
        yield "Failure";
    }
    case PENDING -> {
        System.out.println("Operation pending");
        yield "Pending";
    }
    default -> {
        throw new IllegalStateException("Unknown status");
    }
};

// Switch with old-style syntax but as expression
String dayType = switch (dayOfWeek) {
    case 1, 7:
        yield "Weekend";  // yield instead of break
    case 2, 3, 4, 5, 6:
        yield "Weekday";
    default:
        throw new IllegalArgumentException("Invalid day");
};
```

### Switch with Pattern Matching

```java
// Switch with pattern matching (Java 17+ - preview feature)
Object obj = "Hello";

String result = switch (obj) {
    case String s when s.length() > 10 -> "Long string: " + s;
    case String s -> "Short string: " + s;
    case Integer i when i > 0 -> "Positive integer: " + i;
    case Integer i -> "Non-positive integer: " + i;
    case Double d -> "Double: " + d;
    case null -> "null value";
    default -> "Unknown type: " + obj.getClass().getSimpleName();
};
```

---

## Enhanced Pseudo-Random Number Generators

### New Random Generator Interface

```java
import java.util.random.RandomGenerator;

// Get random generator
RandomGenerator random = RandomGenerator.getDefault();
System.out.println(random.getClass());  // Usually Xoroshiro128PlusPlus

// Generate various types
int randomInt = random.nextInt();
int boundedInt = random.nextInt(100);  // 0-99
long randomLong = random.nextLong();
float randomFloat = random.nextFloat();
double randomDouble = random.nextDouble();
boolean randomBoolean = random.nextBoolean();

// Generate random value in range
int min = 10, max = 100;
int rangeValue = random.nextInt(min, max);  // 10-99
double rangeDouble = random.nextDouble(min, max);  // 10.0-100.0

// Generate random bytes
byte[] bytes = new byte[16];
random.nextBytes(bytes);
```

### Random Generator Algorithms

```java
import java.util.random.RandomGeneratorFactory;

// List available algorithms
RandomGeneratorFactory.all()
    .forEach(factory -> {
        System.out.println(factory.name() + " - " + factory.group());
    });

// Get specific algorithm
RandomGenerator xoroshiro = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create();
RandomGenerator l32x64 = RandomGeneratorFactory.of("L32X64MixRandom").create();
RandomGenerator xoshiro = RandomGeneratorFactory.of("Xoshiro256PlusPlus").create();

// Algorithm groups:
// - LXM: L32X64MixRandom, L64X128MixRandom, L128X256MixRandom
// - Xoroshiro: Xoroshiro128PlusPlus, Xoroshiro128
// - Xoshiro: Xoshiro256PlusPlus, Xoshiro256
```

### Stream of Random Numbers

```java
// Generate stream of random numbers
RandomGenerator random = RandomGenerator.getDefault();

// Stream of integers
random.ints(100)                    // 100 random integers
     .limit(10)
     .forEach(System.out::println);

// Stream of bounded integers
random.ints(0, 100)                // 0-99
     .limit(10)
     .forEach(System.out::println);

// Stream of doubles
random.doubles(10)                 // 10 random doubles
     .forEach(System.out::println);

// Stream of longs
random.longs(5)                    // 5 random longs
     .forEach(System.out::println);
```

### Seeded Random Generators

```java
// Create reproducible sequences
long seed = 12345;
RandomGenerator seeded = RandomGenerator.of("Xoroshiro128PlusPlus", seed);

// Same seed produces same sequence
RandomGenerator same = RandomGenerator.of("Xoroshiro128PlusPlus", seed);

System.out.println(seeded.nextInt());  // Same value
System.out.println(same.nextInt());   // Same value

// Jump operations for advancing generator state
RandomGenerator random = RandomGenerator.getDefault();
random.jump();         // Advance generator state (equivalent to 2^64 calls)
random.jumpPower(2);    // Advance by 2^(64*2) calls
```

---

## Foreign Function & Memory API (Preview)

### Memory Access

```java
import jdk.incubator.foreign.*;

// Allocate off-heap memory
try (MemorySession session = MemorySession.openConfined()) {
    MemorySegment segment = session.allocate(100);  // 100 bytes

    // Write data
    segment.set(ValueLayout.JAVA_INT, 0, 42);
    segment.set(ValueLayout.JAVA_DOUBLE, 8, 3.14);

    // Read data
    int intValue = segment.get(ValueLayout.JAVA_INT, 0);
    double doubleValue = segment.get(ValueLayout.JAVA_DOUBLE, 8);

    System.out.println("Int: " + intValue);      // 42
    System.out.println("Double: " + doubleValue); // 3.14
}

// Allocate with specific layout
MemoryLayout layout = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("id"),
    ValueLayout.JAVA_DOUBLE.withName("value"),
    ValueLayout.JAVA_BOOLEAN.withName("flag")
);

try (MemorySession session = MemorySession.openConfined()) {
    MemorySegment segment = session.allocate(layout);
}
```

### Native Function Calls

```java
import jdk.incubator.foreign.*;
import java.lang.invoke.MethodHandle;

// Get native library
Linker linker = Linker.nativeLinker();
SymbolLookup stdlib = linker.defaultLookup();

// Get strlen function
MethodHandle strlen = linker.downcallHandle(
    stdlib.lookup("strlen").get(),
    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
);

// Call native function
try (MemorySession session = MemorySession.openConfined()) {
    MemorySegment cString = session.allocateUtf8String("Hello");
    long length = (long) strlen.invokeExact(cString);
    System.out.println("Length: " + length);  // 5
}
```

---

## Vector API (Second Incubator)

### Vector Operations

```java
import jdk.incubator.vector.*;

// Create vector from array
float[] data = {1.0f, 2.0f, 3.0f, 4.0f};
FloatVector a = FloatVector.fromArray(FloatVector.SPECIES_256, data, 0);

// Vector operations
FloatVector b = FloatVector.broadcast(FloatVector.SPECIES_256, 2.0f);
FloatVector c = a.mul(b);  // Element-wise multiplication
FloatVector d = a.add(b);  // Element-wise addition

// Vector reduction
float sum = c.reduceLanes(VectorOperators.ADD);

// Compare vectors
VectorMask<Float> mask = a.compare(VectorOperators.GT, 2.0f);
boolean[] greaterThanTwo = mask.toArray();

// Example: Vectorized array multiplication
float[] array1 = new float[1024];
float[] array2 = new float[1024];
float[] result = new float[1024];

// Fill arrays with random data
Random random = new Random();
for (int i = 0; i < 1024; i++) {
    array1[i] = random.nextFloat();
    array2[i] = random.nextFloat();
}

// Vectorized multiplication
int i = 0;
int upperBound = FloatVector.SPECIES_256.loopBound(1024);
for (; i < upperBound; i += FloatVector.SPECIES_256.length()) {
    FloatVector v1 = FloatVector.fromArray(FloatVector.SPECIES_256, array1, i);
    FloatVector v2 = FloatVector.fromArray(FloatVector.SPECIES_256, array2, i);
    FloatVector vr = v1.mul(v2);
    vr.intoArray(result, i);
}

// Handle remaining elements
for (; i < 1024; i++) {
    result[i] = array1[i] * array2[i];
}
```

---

## Context-Specific Deserialization Filters

### Deserialization Filters

```java
import java.io.*;

// Create filter
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
    "com.example.*;!*"
);

// Set filter for all streams
ObjectInputFilter.Config.setSerialFilter(filter);

// Set filter for specific stream
try (ObjectInputStream ois = new ObjectInputStream(
    new FileInputStream("data.dat"))) {

    ois.setObjectInputFilter(filter);
    Object obj = ois.readObject();

} catch (IOException | ClassNotFoundException e) {
    e.printStackTrace();
}

// Custom filter
ObjectInputFilter customFilter = new ObjectInputFilter() {
    @Override
    public Status checkInput(FilterInfo filterInfo) {
        if (filterInfo.depth() > 10) {
            return Status.REJECTED;  // Reject deeply nested objects
        }
        if (filterInfo.arrayLength() > 1000) {
            return Status.REJECTED;  // Reject large arrays
        }
        return Status.UNDECIDED;
    }
};

ObjectInputFilter.Config.setSerialFilter(customFilter);
```

---

## Additional Features

### Deprecations and Removals

```java
// Removed features:
// - Security Manager is deprecated for removal
// - RMI Activation mechanism removed

// Deprecated features:
// - java.security.acl package deprecated
// - Thread.stop() deprecated
// - Thread.destroy() deprecated

// Use replacements:
// - For RMI Activation: use alternative mechanisms
// - For Thread.stop(): use Thread.interrupt()
```

### Performance Improvements

```java
// ZGC: Concurrent thread stack processing
// - Improved GC performance

// AppCDS: Improved application class data sharing
// - Better startup time

// String deduplication improvements
// - Reduced memory footprint
```

### Language Clarifications

```java
// Strictly floating-point expressions
// - Clearer rules for floating-point operations

// Pattern matching improvements
// - Better error messages for pattern matching
```

---

## Migration Guide

### Upgrading from Java 11

```java
// 1. Update records to final version
// Before (Java 14-15):
record Point(int x, int y) {}  // Preview feature

// After (Java 16+):
record Point(int x, int y) {}  // Final feature

// 2. Update switch expressions to final version
// Before:
int day = 3;
String name = switch (day) {
    case 1: yield "Sunday";  // Preview syntax
    default: yield "Unknown";
};

// After:
String name = switch (day) {
    case 1 -> "Sunday";      // Final syntax
    default -> "Unknown";
};

// 3. Update sealed classes to final version
// Before (Java 15):
sealed class Shape permits Circle, Rectangle {}  // Preview

// After (Java 17):
sealed class Shape permits Circle, Rectangle {}  // Final

// 4. Update text blocks to final version
String text = """
    Multi-line
    string
    """;  // Final in Java 15+

// 5. Update random generators
// Before:
Random random = new Random();

// After:
RandomGenerator generator = RandomGenerator.getDefault();
// Or use specific algorithm:
RandomGenerator xoroshiro = RandomGenerator.of("Xoroshiro128PlusPlus");
```

### Build System Updates

```java
// Maven - Update plugin to support Java 17
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
                <release>17</release>
            </configuration>
        </plugin>
    </plugins>
</build>

// Gradle - Update to support Java 17
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

### Preview Features

```java
// Enable preview features for compilation
// Java compiler:
javac --enable-preview --release 17 MyClass.java

// Java runtime:
java --enable-preview MyClass

// Maven:
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.10.1</version>
    <configuration>
        <release>17</release>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
    </configuration>
</plugin>

// Gradle:
tasks.withType(JavaCompile) {
    options.compilerArgs += '--enable-preview'
}

tasks.withType(Test) {
    jvmArgs += '--enable-preview'
}
```

---

## Summary

This reference covers Java 17 features:

- **Records**: Concise immutable data carriers
- **Pattern Matching**: Type-safe conditional expressions
- **Sealed Classes**: Restricted class hierarchies
- **Text Blocks**: Multi-line string literals
- **Switch Expressions**: Enhanced switch statements
- **Enhanced Random Generators**: New random number API
- **Foreign Function & Memory API**: Native code interop (Preview)
- **Vector API**: SIMD operations (Incubator)
- **Deserialization Filters**: Security improvements

Java 17 is an LTS release bringing significant language enhancements and API improvements, making Java more expressive and efficient while maintaining backward compatibility.
