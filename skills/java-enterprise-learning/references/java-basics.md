# Java Basics Reference Guide

## Table of Contents

1. [Language Fundamentals](#language-fundamentals)
2. [Data Types](#data-types)
3. [Operators](#operators)
4. [Control Flow](#control-flow)
5. [Object-Oriented Programming](#object-oriented-programming)
6. [Exception Handling](#exception-handling)
7. [Generics](#generics)
8. [Annotations](#annotations)
9. [Lambda Expressions](#lambda-expressions)
10. [Best Practices](#best-practices)

---

## Language Fundamentals

### Java Program Structure

Every Java program consists of one or more classes. The entry point is the `main` method:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### Class Declaration Syntax

```java
// Basic class
public class MyClass {
    // Fields
    // Constructors
    // Methods
}

// Abstract class
public abstract class AbstractClass {
    public abstract void abstractMethod();
}

// Final class (cannot be inherited)
public final class FinalClass {
    // Implementation
}

// Generic class
public class GenericClass<T> {
    private T value;
}
```

---

## Data Types

### Primitive Types

Java has 8 primitive types:

| Type | Size | Range | Default Value | Wrapper Class |
|------|------|-------|---------------|---------------|
| `byte` | 8-bit | -128 to 127 | 0 | Byte |
| `short` | 16-bit | -32,768 to 32,767 | 0 | Short |
| `int` | 32-bit | -2³¹ to 2³¹-1 | 0 | Integer |
| `long` | 64-bit | -2⁶³ to 2⁶³-1 | 0L | Long |
| `float` | 32-bit | IEEE 754 | 0.0f | Float |
| `double` | 64-bit | IEEE 754 | 0.0d | Double |
| `char` | 16-bit | '\u0000' to '\uffff' | '\u0000' | Character |
| `boolean` | 1-bit | true, false | false | Boolean |

### Primitive Type Usage

```java
// Integer literals
int decimal = 100;
int binary = 0b1010;      // Binary literal
int octal = 012;          // Octal literal
int hex = 0xFF;           // Hexadecimal literal
long bigNumber = 100_000_000L;  // Underscores for readability

// Floating-point literals
float f1 = 3.14f;
double d1 = 3.14;
double d2 = 3.14d;
double scientific = 1.2e-5;

// Character literals
char letter = 'A';
char unicode = '\u0041';  // Same as 'A'
char escape = '\n';       // Newline

// Boolean literals
boolean flag = true;
boolean isActive = false;
```

### Reference Types

```java
// String (immutable)
String name = "Java";

// Array
int[] numbers = {1, 2, 3, 4, 5};
String[] names = new String[5];

// Class references
Object obj = new Object();
MyClass instance = new MyClass();

// Interface references
Runnable runnable = () -> System.out.println("Hello");
```

### Type Conversion

```java
// Widening (implicit)
int i = 100;
long l = i;           // int to long
double d = i;         // int to double

// Narrowing (explicit, requires cast)
long big = 100L;
int small = (int) big;    // May lose data

// Wrapper conversion (autoboxing/unboxing)
Integer boxed = 100;      // Autoboxing
int unboxed = boxed;      // Unboxing

// String conversion
String str = String.valueOf(123);
int num = Integer.parseInt("123");

// Number conversion
double dval = 3.14;
int ival = (int) dval;     // Truncates decimal
long lval = Math.round(dval);  // Rounds
```

---

## Operators

### Arithmetic Operators

```java
int a = 10, b = 3;

int sum = a + b;        // 13
int diff = a - b;       // 7
int product = a * b;    // 30
int quotient = a / b;   // 3 (integer division)
int remainder = a % b;  // 1

// Compound assignment
a += 5;     // a = a + 5
a -= 3;     // a = a - 3
a *= 2;     // a = a * 2
a /= 4;     // a = a / 4
a %= 2;     // a = a % 2

// Increment/Decrement
int x = 5;
int y = ++x;  // Prefix: x becomes 6, y is 6
int z = x++;  // Postfix: z is 6, then x becomes 7
```

### Comparison Operators

```java
int a = 10, b = 20;

boolean eq = (a == b);      // false
boolean neq = (a != b);     // true
boolean gt = (a > b);       // false
boolean lt = (a < b);       // true
boolean gte = (a >= b);     // false
boolean lte = (a <= b);     // true

// Object comparison
String s1 = new String("hello");
String s2 = new String("hello");
boolean sameRef = (s1 == s2);        // false (different references)
boolean sameValue = s1.equals(s2);  // true (same content)
```

### Logical Operators

```java
boolean a = true, b = false;

boolean and = a && b;      // false (short-circuit)
boolean or = a || b;       // true (short-circuit)
boolean not = !a;          // false

// Non-short-circuit operators
boolean and2 = a & b;      // Both sides evaluated
boolean or2 = a | b;       // Both sides evaluated

// XOR
boolean xor = a ^ b;       // true (different values)

// Ternary operator
int max = (a > b) ? a : b;
String message = (score >= 60) ? "Pass" : "Fail";
```

### Bitwise Operators

```java
int a = 5;    // 0101
int b = 3;    // 0011

int and = a & b;      // 0001 = 1
int or = a | b;       // 0111 = 7
int xor = a ^ b;      // 0110 = 6
int not = ~a;         // Inverts all bits

int leftShift = a << 1;    // 1010 = 10
int rightShift = a >> 1;   // 0010 = 2 (arithmetic)
int unsignedRightShift = a >>> 1;  // 0010 = 2 (logical)

// Bit manipulation patterns
// Check if bit is set
boolean isSet = (a & (1 << n)) != 0;

// Set a bit
a = a | (1 << n);

// Clear a bit
a = a & ~(1 << n);

// Toggle a bit
a = a ^ (1 << n);
```

### instanceof Operator

```java
Object obj = "Hello World";

if (obj instanceof String) {
    String str = (String) obj;  // Safe to cast
    System.out.println(str.toUpperCase());
}

// Pattern matching (JDK 16+)
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());  // s is automatically cast
}
```

---

## Control Flow

### If-Else Statements

```java
// Basic if-else
int score = 85;

if (score >= 90) {
    System.out.println("A");
} else if (score >= 80) {
    System.out.println("B");
} else if (score >= 70) {
    System.out.println("C");
} else {
    System.out.println("F");
}

// Ternary operator for simple cases
String grade = (score >= 90) ? "A" : (score >= 80) ? "B" : "C";
```

### Switch Statements (Traditional)

```java
int dayOfWeek = 3;
String dayName;

switch (dayOfWeek) {
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

// Fall-through example
switch (season) {
    case "Spring":
        System.out.println("Mild weather");
        // Fall through
    case "Summer":
        System.out.println("Warm weather");
        break;
    case "Autumn":
        System.out.println("Cool weather");
        break;
    case "Winter":
        System.out.println("Cold weather");
        break;
}
```

### Switch Expressions (JDK 14+)

```java
// Arrow syntax with no fall-through
String dayName = switch (dayOfWeek) {
    case 1 -> "Sunday";
    case 2 -> "Monday";
    case 3 -> "Tuesday";
    case 4 -> "Wednesday";
    case 5 -> "Thursday";
    case 6 -> "Friday";
    case 7 -> "Saturday";
    default -> throw new IllegalArgumentException("Invalid day");
};

// Multi-case labels
String season = switch (month) {
    case 12, 1, 2 -> "Winter";
    case 3, 4, 5 -> "Spring";
    case 6, 7, 8 -> "Summer";
    case 9, 10, 11 -> "Autumn";
    default -> throw new IllegalArgumentException("Invalid month");
};

// Complex case with code block
String dayType = switch (dayOfWeek) {
    case 1, 7 -> {
        System.out.println("Weekend!");
        yield "Weekend";
    }
    case 2, 3, 4, 5, 6 -> {
        System.out.println("Weekday");
        yield "Weekday";
    }
    default -> throw new IllegalArgumentException();
};
```

### For Loops

```java
// Traditional for loop
for (int i = 0; i < 10; i++) {
    System.out.println("Index: " + i);
}

// Multiple initialization and increment
for (int i = 0, j = 10; i < j; i++, j--) {
    System.out.println("i=" + i + ", j=" + j);
}

// Enhanced for loop (for-each)
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

// Iterating over collections
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
for (String name : names) {
    System.out.println(name);
}

// Labeled break/continue
outer:
for (int i = 0; i < 5; i++) {
    for (int j = 0; j < 5; j++) {
        if (i == 2 && j == 2) {
            break outer;  // Break outer loop
        }
        System.out.println(i + "," + j);
    }
}
```

### While Loops

```java
// While loop
int i = 0;
while (i < 10) {
    System.out.println(i);
    i++;
}

// Do-while loop (executes at least once)
int j = 0;
do {
    System.out.println(j);
    j++;
} while (j < 10);

// Infinite loop with break
while (true) {
    String input = readInput();
    if (input.equals("quit")) {
        break;
    }
    process(input);
}
```

### ForEach with Iterable

```java
// Using Iterable.forEach with lambda
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(name -> System.out.println(name));

// Using method reference
names.forEach(System.out::println);

// With index workaround (Java 21+)
List<String> list = List.of("A", "B", "C");
IntStream.range(0, list.size())
    .forEach(i -> System.out.println(i + ": " + list.get(i)));
```

---

## Object-Oriented Programming

### Classes and Objects

```java
public class Person {
    // Fields (instance variables)
    private String name;
    private int age;

    // Static field (class variable)
    private static int count = 0;

    // Static block (executed when class is loaded)
    static {
        System.out.println("Person class loaded");
    }

    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }

    // Instance method
    public void introduce() {
        System.out.println("Hi, I'm " + name + ", " + age + " years old");
    }

    // Static method (cannot use 'this')
    public static int getCount() {
        return count;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // toString method
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

// Usage
Person person1 = new Person("Alice", 30);
Person person2 = new Person("Bob", 25);

person1.introduce();
System.out.println("Total persons: " + Person.getCount());
```

### Inheritance

```java
// Base class (parent)
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " is eating");
    }

    public void sleep() {
        System.out.println(name + " is sleeping");
    }

    // Method to be overridden
    public void makeSound() {
        System.out.println(name + " makes a sound");
    }
}

// Derived class (child)
public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);  // Call parent constructor
        this.breed = breed;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " barks: Woof!");
    }

    // New method specific to Dog
    public void fetch() {
        System.out.println(name + " is fetching the ball");
    }
}

// Another derived class
public class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(name + " meows: Meow!");
    }

    public void climb() {
        System.out.println(name + " is climbing");
    }
}

// Usage
Animal dog = new Dog("Buddy", "Golden Retriever");
Animal cat = new Cat("Whiskers");

dog.eat();        // Inherited method
dog.makeSound();  // Overridden method
cat.makeSound();  // Different implementation
```

### Abstract Classes

```java
// Abstract class (cannot be instantiated)
public abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    // Abstract method (must be implemented by subclasses)
    public abstract double getArea();

    // Abstract method
    public abstract double getPerimeter();

    // Concrete method (can be used as-is or overridden)
    public void displayInfo() {
        System.out.println("Shape color: " + color);
    }
}

// Concrete subclass
public class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double getArea() {
        return width * height;
    }

    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
}

// Another concrete subclass
public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}

// Usage
Shape shape1 = new Rectangle("red", 5, 3);
Shape shape2 = new Circle("blue", 4);

shape1.displayInfo();
System.out.println("Area: " + shape1.getArea());
shape2.displayInfo();
System.out.println("Area: " + shape2.getArea());
```

### Interfaces

```java
// Interface definition
public interface Drawable {
    // Constant (public static final by default)
    String DEFAULT_COLOR = "black";

    // Abstract method (public abstract by default)
    void draw();

    // Default method (implementation provided)
    default void drawWithColor(String color) {
        System.out.println("Drawing with color: " + color);
        draw();
    }

    // Static method
    static void printInfo() {
        System.out.println("Drawable interface");
    }
}

// Another interface
public interface Resizable {
    void resize(double factor);
}

// Class implementing multiple interfaces
public class Circle implements Drawable, Resizable {
    private double radius;
    private String color;

    public Circle(double radius, String color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw() {
        System.out.println("Drawing circle with radius " + radius);
    }

    @Override
    public void resize(double factor) {
        radius *= factor;
        System.out.println("Resized to radius: " + radius);
    }

    // Override default method
    @Override
    public void drawWithColor(String color) {
        System.out.println("Drawing " + this.color + " circle with " + color + " ink");
        draw();
    }
}

// Usage
Circle circle = new Circle(5, "red");
circle.draw();
circle.resize(1.5);
circle.drawWithColor("blue");
Drawable.printInfo();
```

### Nested Classes

```java
public class OuterClass {
    private int outerField = 10;

    // Static nested class
    public static class StaticNestedClass {
        private static int staticField = 20;

        public void display() {
            // Cannot access instance members of outer class
            System.out.println("Static nested: " + staticField);
        }

        public static void staticMethod() {
            System.out.println("Static method in static nested class");
        }
    }

    // Inner (non-static nested) class
    public class InnerClass {
        private int innerField = 30;

        public void display() {
            // Can access instance members of outer class
            System.out.println("Outer: " + outerField);
            System.out.println("Inner: " + innerField);
        }

        public void accessOuterMethod() {
            outerMethod();
        }
    }

    // Method-local inner class
    public void methodWithLocalClass() {
        int localVar = 40;  // Must be effectively final

        class LocalClass {
            public void display() {
                System.out.println("Local var: " + localVar);
                System.out.println("Outer field: " + outerField);
            }
        }

        LocalClass local = new LocalClass();
        local.display();
    }

    // Anonymous inner class
    public Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("Running from anonymous class");
            }
        };
    }

    public void outerMethod() {
        System.out.println("Outer method called");
    }
}

// Usage
OuterClass outer = new OuterClass();

// Static nested class
OuterClass.StaticNestedClass staticNested = new OuterClass.StaticNestedClass();
staticNested.display();
OuterClass.StaticNestedClass.staticMethod();

// Inner class (requires outer instance)
OuterClass.InnerClass inner = outer.new InnerClass();
inner.display();

// Method-local class (used within method)
outer.methodWithLocalClass();

// Anonymous class
Runnable runnable = outer.createRunnable();
runnable.run();
```

### Enums

```java
// Simple enum
public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}

// Enum with fields and methods
public enum Season {
    SPRING("Mild", 15),
    SUMMER("Hot", 30),
    AUTUMN("Cool", 15),
    WINTER("Cold", 0);

    private final String description;
    private final int averageTemp;

    Season(String description, int averageTemp) {
        this.description = description;
        this.averageTemp = averageTemp;
    }

    public String getDescription() {
        return description;
    }

    public int getAverageTemp() {
        return averageTemp;
    }

    @Override
    public String toString() {
        return name() + " (" + description + ", " + averageTemp + "C)";
    }
}

// Enum with abstract method
public enum Operation {
    ADD {
        @Override
        public double apply(double a, double b) {
            return a + b;
        }
    },
    SUBTRACT {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    MULTIPLY {
        @Override
        public double apply(double a, double b) {
            return a * b;
        }
    },
    DIVIDE {
        @Override
        public double apply(double a, double b) {
            return a / b;
        }
    };

    public abstract double apply(double a, double b);
}

// Usage
Day today = Day.MONDAY;
System.out.println("Today is " + today);

Season current = Season.SUMMER;
System.out.println(current);
System.out.println("Average temp: " + current.getAverageTemp());

// Enum values
for (Day day : Day.values()) {
    System.out.println(day);
}

// Enum switch
Day weekend = Day.SATURDAY;
switch (weekend) {
    case SATURDAY, SUNDAY:
        System.out.println("Weekend!");
        break;
    default:
        System.out.println("Weekday");
}

// Enum with abstract method
double result = Operation.MULTIPLY.apply(5, 3);
System.out.println("5 * 3 = " + result);
```

---

## Exception Handling

### Exception Hierarchy

```
Throwable
├── Error (System errors, should not be caught)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
└── Exception
    ├── RuntimeException (Unchecked exceptions)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   ├── IndexOutOfBoundsException
    │   └── ...
    └── Checked exceptions (must be caught or declared)
        ├── IOException
        ├── SQLException
        └── ...
```

### Try-Catch-Finally

```java
// Basic try-catch
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
}

// Multiple catch blocks
try {
    String input = readFromNetwork();
    int number = Integer.parseInt(input);
    processNumber(number);
} catch (NumberFormatException e) {
    System.out.println("Invalid number format");
} catch (IOException e) {
    System.out.println("Network error: " + e.getMessage());
}

// Multi-catch (Java 7+)
try {
    // Code that may throw multiple exceptions
    Files.delete(path);
} catch (IOException | SecurityException e) {
    System.out.println("Error: " + e.getMessage());
}

// Finally block (always executes)
FileReader reader = null;
try {
    reader = new FileReader("file.txt");
    // Read file
} catch (FileNotFoundException e) {
    System.out.println("File not found");
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader");
        }
    }
}

// Try-with-resources (Java 7+)
try (FileReader reader = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(reader)) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}
```

### Custom Exceptions

```java
// Custom checked exception
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Custom unchecked exception
public class BusinessException extends RuntimeException {
    private String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

// Using custom exceptions
public class Validator {
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new BusinessException("INVALID_AGE", "Age must be between 0 and 150");
        }
    }
}

// Usage
try {
    Validator.validateEmail("invalid-email");
} catch (ValidationException e) {
    System.out.println("Validation failed: " + e.getMessage());
}

try {
    Validator.validateAge(200);
} catch (BusinessException e) {
    System.out.println("Error code: " + e.getErrorCode());
    System.out.println("Message: " + e.getMessage());
}
```

### Exception Best Practices

```java
// DO: Catch specific exceptions
try {
    processFile();
} catch (FileNotFoundException e) {
    // Handle file not found
} catch (IOException e) {
    // Handle other IO errors
}

// DON'T: Catch generic Exception
try {
    processFile();
} catch (Exception e) {
    // Too generic, may hide bugs
}

// DO: Preserve original exception (exception chaining)
try {
    loadData();
} catch (IOException e) {
    throw new DataLoadException("Failed to load data", e);
}

// DO: Provide meaningful messages
throw new IllegalArgumentException("Age must be positive, got: " + age);

// DO: Clean up resources in finally
Connection conn = null;
try {
    conn = getConnection();
    // Use connection
} catch (SQLException e) {
    log.error("Database error", e);
    throw new DataAccessException(e);
} finally {
    if (conn != null) {
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("Failed to close connection", e);
        }
    }
}

// BETTER: Use try-with-resources
try (Connection conn = getConnection()) {
    // Use connection
} catch (SQLException e) {
    log.error("Database error", e);
    throw new DataAccessException(e);
}
```

---

## Generics

### Generic Classes

```java
// Generic class with type parameter T
public class Box<T> {
    private T content;

    public Box(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Box containing: " + content;
    }
}

// Multiple type parameters
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Usage
Box<String> stringBox = new Box<>("Hello");
Box<Integer> integerBox = new Box<>(42);

System.out.println(stringBox);  // Box containing: Hello
System.out.println(integerBox); // Box containing: 42

Pair<String, Integer> pair = new Pair<>("Age", 30);
System.out.println(pair.getKey() + ": " + pair.getValue());
```

### Bounded Type Parameters

```java
// Upper bounded type parameter
public class NumberBox<T extends Number> {
    private T number;

    public NumberBox(T number) {
        this.number = number;
    }

    public double doubleValue() {
        return number.doubleValue();
    }

    public int intValue() {
        return number.intValue();
    }
}

// Multiple bounds
public class ComparableBox<T extends Number & Comparable<T>> {
    private T value;

    public ComparableBox(T value) {
        this.value = value;
    }

    public T max(T other) {
        return value.compareTo(other) > 0 ? value : other;
    }
}

// Usage
NumberBox<Integer> intBox = new NumberBox<>(42);
NumberBox<Double> doubleBox = new NumberBox<>(3.14);

System.out.println(intBox.doubleValue());  // 42.0
System.out.println(doubleBox.intValue());  // 3
```

### Generic Methods

```java
public class Util {
    // Generic method
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // Generic method with bounds
    public static <T extends Comparable<T>> T max(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        T max = array[0];
        for (T item : array) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    // Wildcard method
    public static void printList(List<?> list) {
        for (Object item : list) {
            System.out.println(item);
        }
    }

    // Upper bounded wildcard
    public static double sum(List<? extends Number> list) {
        double sum = 0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }

    // Lower bounded wildcard
    public static void addNumbers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
    }
}

// Usage
String[] names = {"Alice", "Bob", "Charlie"};
Util.swap(names, 0, 2);

Integer[] numbers = {5, 2, 8, 1, 9};
Integer max = Util.max(numbers);  // 9

List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5);
double sum = Util.sum(intList);  // 15.0
```

### Wildcards

```java
// Upper bounded wildcard (? extends T)
public class UpperBoundedExample {
    // Can read as T, cannot write
    public static void process(List<? extends Number> list) {
        // OK: Reading
        Number num = list.get(0);

        // ERROR: Cannot add
        // list.add(42);  // Compile error
        // list.add(3.14);  // Compile error
    }

    public static double sum(List<? extends Number> list) {
        return list.stream()
            .mapToDouble(Number::doubleValue)
            .sum();
    }
}

// Lower bounded wildcard (? super T)
public class LowerBoundedExample {
    // Can write T, reads as Object
    public static void fillList(List<? super Integer> list) {
        // OK: Writing
        list.add(10);
        list.add(20);

        // Can only read as Object
        Object obj = list.get(0);
    }
}

// Unbounded wildcard (?)
public class UnboundedExample {
    // Can only read as Object, cannot write (except null)
    public static void printAll(List<?> list) {
        for (Object item : list) {
            System.out.println(item);
        }
    }

    public static boolean isEmpty(List<?> list) {
        return list.isEmpty();
    }
}

// Usage
List<Integer> integers = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);

UpperBoundedExample.process(integers);
UpperBoundedExample.process(doubles);

List<Number> numbers = new ArrayList<>();
LowerBoundedExample.fillList(numbers);
```

### Type Erasure

```java
// Generics are erased at compile time
public class ErasureExample<T> {
    private T value;

    public T getValue() {
        return value;
    }

    // After type erasure:
    // private Object value;
    // public Object getValue() { return value; }

    // Cannot use type parameters in static contexts
    // private static T staticField;  // ERROR
    // public static T staticMethod() { return null; }  // ERROR

    // Cannot create generic arrays
    // private T[] array = new T[10];  // ERROR

    // Workaround for creating generic arrays
    @SuppressWarnings("unchecked")
    public T[] createArray(int size) {
        return (T[]) new Object[size];  // Unchecked cast
    }

    // Cannot overload based on generic type
    // public void process(List<Integer> list) {}  // ERROR
    // public void process(List<String> list) {}   // ERROR - same signature
}
```

---

## Annotations

### Built-in Annotations

```java
// @Override - indicates method overrides super method
@Override
public String toString() {
    return "MyObject";
}

// @Deprecated - marks as deprecated
@Deprecated
public void oldMethod() {
    // Do not use this anymore
}

@SuppressWarnings("unchecked")  // Suppress warnings
public void process() {
    List list = new ArrayList();  // Raw type warning suppressed
    list.add("Hello");
}

// @SafeVarargs - safe varargs usage
@SafeVarargs
public static <T> List<T> asList(T... elements) {
    return Arrays.asList(elements);
}

// @FunctionalInterface - interface with single abstract method
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}
```

### Custom Annotations

```java
// Element annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Author {
    String name();
    String date() default "2024-01-01";
}

// Annotation with array
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public interface Permissions {
    String[] value();
}

// Annotation with enum
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public enum LogLevel {
    DEBUG, INFO, WARN, ERROR
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogField {
    LogLevel level() default LogLevel.INFO;
    String format() default "%s";
}

// Annotation usage
@Author(name = "John Doe", date = "2024-02-24")
public class MyClass {
    @LogField(level = LogLevel.DEBUG, format = "Value: %s")
    private String value;

    @Permissions({"read", "write"})
    public void process() {
        // Method implementation
    }
}
```

### Processing Annotations

```java
// Reading annotations at runtime
public class AnnotationProcessor {
    public static void processClass(Class<?> clazz) {
        // Check for class annotation
        if (clazz.isAnnotationPresent(Author.class)) {
            Author author = clazz.getAnnotation(Author.class);
            System.out.println("Author: " + author.name());
            System.out.println("Date: " + author.date());
        }

        // Process fields
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(LogField.class)) {
                LogField logField = field.getAnnotation(LogField.class);
                System.out.println("Field " + field.getName() +
                    " logged at " + logField.level());
            }
        }

        // Process methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Permissions.class)) {
                Permissions permissions = method.getAnnotation(Permissions.class);
                System.out.println("Method " + method.getName() +
                    " permissions: " + Arrays.toString(permissions.value()));
            }
        }
    }
}

// Usage
AnnotationProcessor.processClass(MyClass.class);
```

---

## Lambda Expressions

### Lambda Basics

```java
// Functional interface
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// Lambda expressions
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;
Calculator power = (a, b) -> (int) Math.pow(a, b);

// Using lambdas
System.out.println(add.calculate(5, 3));        // 8
System.out.println(multiply.calculate(5, 3));   // 15
System.out.println(power.calculate(2, 10));     // 1024

// Lambda with block
Calculator divide = (a, b) -> {
    if (b == 0) {
        throw new IllegalArgumentException("Cannot divide by zero");
    }
    return a / b;
};

// Method reference
Calculator addRef = Integer::sum;  // Equivalent to (a, b) -> a + b
```

### Common Functional Interfaces

```java
// Predicate - boolean test
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate();

System.out.println(isEven.test(4));   // true
System.out.println(isEven.test(3));   // false

// Consumer - action with no return value
Consumer<String> printer = s -> System.out.println(s);
Consumer<List<String>> printerList = list ->
    list.forEach(System.out::println);

printer.accept("Hello");

List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
printerList.accept(names);

// Function - transform value
Function<String, Integer> length = String::length;
Function<Integer, String> toString = Object::toString;
Function<String, String> toUpper = String::toUpperCase;

Function<String, String> pipeline = toUpper.andThen(length).andThen(Object::toString);
System.out.println(pipeline.apply("hello"));  // "5"

// Supplier - provide value
Supplier<Double> random = Math::random;
Supplier<String> timestamp = () -> new Date().toString();
Supplier<List<String>> emptyList = ArrayList::new;

System.out.println(random.get());
System.out.println(timestamp.get());

// BiFunction - two inputs, one output
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
BiFunction<String, String, Boolean> equals = String::equals;

System.out.println(add.apply(5, 3));               // 8
System.out.println(equals.apply("A", "A"));       // true
```

### Lambda with Collections

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");

// Filter with Predicate
List<String> longNames = names.stream()
    .filter(name -> name.length() > 3)
    .collect(Collectors.toList());

// Map with Function
List<Integer> lengths = names.stream()
    .map(String::length)
    .collect(Collectors.toList());

// Sorted with Comparator
List<String> sorted = names.stream()
    .sorted(Comparator.comparingInt(String::length))
    .collect(Collectors.toList());

// Reduce
Optional<String> longest = names.stream()
    .reduce((a, b) -> a.length() > b.length() ? a : b);

// Collect with grouping
Map<Integer, List<String>> byLength = names.stream()
    .collect(Collectors.groupingBy(String::length));

// Remove elements with lambda
names.removeIf(name -> name.startsWith("A"));

// Replace elements
names.replaceAll(String::toUpperCase);

// Sort with lambda
names.sort(Comparator.reverseOrder());

// Iterate with lambda
names.forEach(System.out::println);
```

### Variable Capture

```java
int factor = 10;

// Lambda captures factor (must be effectively final)
Function<Integer, Integer> multiplier = n -> n * factor;

System.out.println(multiplier.apply(5));  // 50

// This works because factor is effectively final
// factor = 20;  // ERROR: Would break lambda

// Capturing instance variables
class Multiplier {
    private int factor = 10;

    public Function<Integer, Integer> getMultiplier() {
        return n -> n * factor;  // Can capture instance variable
    }

    public void setFactor(int factor) {
        this.factor = factor;  // This is OK
    }
}
```

---

## Best Practices

### Code Style

```java
// DO: Use meaningful names
public class UserValidator {
    private static final int MAX_AGE = 150;

    public boolean isValid(User user) {
        return user != null
            && user.getAge() > 0
            && user.getAge() <= MAX_AGE;
    }
}

// DON'T: Use single-letter names (except loop variables)
// Bad: class A { void m() { int a = 1; } }

// DO: Use constants instead of magic numbers
public static final double PI = 3.14159265359;
public static final int MAX_RETRIES = 3;

// DON'T: Embed magic numbers
// circumference = 2 * 3.14159 * radius;  // Bad
```

### Access Modifiers

```java
public class MyClass {
    // Private - most restrictive
    private int privateField;

    // Package-private (default) - only within same package
    int packageField;

    // Protected - within package and subclasses
    protected int protectedField;

    // Public - accessible everywhere
    public int publicField;

    // Principle of least privilege
    private String internalData;

    // Provide controlled access
    public String getInternalData() {
        return internalData;
    }

    public void setInternalData(String data) {
        if (isValid(data)) {
            this.internalData = data;
        }
    }

    private boolean isValid(String data) {
        return data != null && !data.isEmpty();
    }
}
```

### Immutability

```java
// Immutable class design
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;  // Defensive copy needed

    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = new ArrayList<>(hobbies);  // Defensive copy
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);  // Defensive copy
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePerson that = (ImmutablePerson) o;
        return age == that.age &&
            Objects.equals(name, that.name) &&
            Objects.equals(hobbies, that.hobbies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, hobbies);
    }
}
```

### Null Safety

```java
public class NullSafety {
    // Use Optional for potentially null return values
    public Optional<User> findUserById(String id) {
        User user = userRepository.findById(id);
        return Optional.ofNullable(user);
    }

    // Use Optional.orElse() for default value
    public String getUserName(String id) {
        return findUserById(id)
            .map(User::getName)
            .orElse("Unknown User");
    }

    // Use Optional.orElseThrow() for required values
    public User getRequiredUser(String id) {
        return findUserById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    // Use Objects.requireNonNull for parameters
    public void process(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        // Process user
    }

    // Use @Nullable and @NonNull annotations
    public @Nullable String findEmail(@NonNull String userId) {
        // May return null
        return emailRepository.findByUserId(userId);
    }
}
```

### Method Design

```java
// DO: Keep methods small and focused
public class UserService {
    // Good: Single responsibility
    public void sendWelcomeEmail(User user) {
        Email email = createWelcomeEmail(user);
        emailService.send(email);
    }

    // Bad: Doing too much
    // public void registerAndSendEmailAndLogAnd...() { }

    // DO: Use method overloading for similar operations
    public User createUser(String name, String email) {
        return createUser(name, email, 0);
    }

    public User createUser(String name, String email, int age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }

    // DO: Use varargs for optional parameters
    public void log(String format, Object... args) {
        System.out.printf(format, args);
    }

    // DO: Use builder pattern for many parameters
    public User user() {
        return User.builder()
            .name("John")
            .email("john@example.com")
            .age(30)
            .active(true)
            .build();
    }
}
```

### Equals and hashCode Contract

```java
public final class UserId {
    private final String value;

    public UserId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        // 1. Check reference equality
        if (this == o) return true;

        // 2. Check for null and class equality
        if (o == null || getClass() != o.getClass()) return false;

        // 3. Cast and compare significant fields
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        // Must use same fields as equals
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{" + value + "}";
    }
}

// When overriding equals and hashCode:
// 1. equals() must be reflexive: x.equals(x) is true
// 2. equals() must be symmetric: x.equals(y) == y.equals(x)
// 3. equals() must be transitive: x.equals(y) && y.equals(z) => x.equals(z)
// 4. equals() must be consistent: multiple calls return same result
// 5. equals() returns false for null
// 6. hashCode() must be consistent: same object produces same hash
// 7. equals() objects must have equal hashCode()
```

### Resource Management

```java
// DO: Use try-with-resources
public class ResourceManager {
    public void processFile(String path) throws IOException {
        try (InputStream in = Files.newInputStream(Paths.get(path));
             BufferedInputStream bis = new BufferedInputStream(in);
             BufferedReader reader = new BufferedReader(new InputStreamReader(bis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        }  // Resources automatically closed
    }

    // Custom resource implementation
    public class CustomResource implements AutoCloseable {
        private Connection connection;

        public CustomResource() {
            this.connection = establishConnection();
        }

        @Override
        public void close() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Failed to close connection", e);
                }
            }
        }
    }

    public void useCustomResource() {
        try (CustomResource resource = new CustomResource()) {
            // Use resource
        }  // Automatically closed
    }
}
```

---

## Summary

This reference covers the fundamental concepts of Java programming:

- **Language Fundamentals**: Class structure, syntax basics
- **Data Types**: Primitives, reference types, conversions
- **Operators**: Arithmetic, comparison, logical, bitwise
- **Control Flow**: If-else, switch, loops
- **OOP**: Classes, inheritance, abstract classes, interfaces, enums, nested classes
- **Exception Handling**: Try-catch-finally, custom exceptions
- **Generics**: Generic classes, methods, wildcards, type erasure
- **Annotations**: Built-in and custom annotations
- **Lambda Expressions**: Functional programming in Java
- **Best Practices**: Code style, access modifiers, immutability, null safety

For more advanced topics, refer to the other reference documents in this learning path.
