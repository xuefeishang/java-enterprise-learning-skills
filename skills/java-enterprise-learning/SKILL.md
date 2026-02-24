---
name: java-enterprise-learning
description: This skill should be used when the user asks to "learn Java", "study Java 17", "Java enterprise development", "Java knowledge graph", "Java learning roadmap", "Java interview preparation", "understand Java collections", "Java concurrency", "JVM internals", "Spring Boot", "Java performance tuning", or "enterprise Java best practices". Provides comprehensive Java JDK 17+ learning guidance and enterprise development knowledge.
version: 1.0.0
---

# Java JDK 17+ Enterprise Learning Guide

This skill provides comprehensive learning guidance for Java JDK 17+ enterprise development. It covers core language features, enterprise frameworks, performance optimization, and practical development patterns.

## Purpose

Systematically learn Java from basics to enterprise-level mastery. Build a complete knowledge map covering JDK 17+ features, concurrency, JVM internals, Spring ecosystem, and production-ready development practices.

## Learning Path

### Phase 1: Core Foundations

Master Java fundamentals before advancing:

1. **Language Basics**
   - Syntax, data types, operators
   - Control flow and exception handling
   - Object-oriented programming principles
   - References: `references/java-basics.md`

2. **Collections Framework**
   - List, Set, Map interfaces and implementations
   - Choosing the right collection type
   - Performance considerations
   - References: `references/java-collections.md`

3. **Exception Handling**
   - Checked vs unchecked exceptions
   - Try-with-resources
   - Custom exception design

### Phase 2: JDK 17+ New Features

Focus on modern Java capabilities:

1. **Language Enhancements**
   - Records for immutable data
   - Pattern Matching
   - Sealed classes for type hierarchy control
   - Text Blocks
   - Switch Expressions

2. **API Improvements**
   - Stream API enhancements
   - New Random Generator interface
   - Enhanced Pseudo-Random Number Generators
   - References: `references/java-17-features.md`

### Phase 3: Concurrency and Parallelism

Understand thread-safe programming:

1. **Threads and Executors**
   - Thread lifecycle and management
   - ExecutorService and thread pools
   - Future and CompletableFuture

2. **Synchronization**
   - Synchronized blocks and methods
   - Lock interfaces and ReentrantLock
   - ReadWriteLock and StampedLock

3. **Concurrent Collections**
   - ConcurrentHashMap usage
   - Concurrent queues and deques
   - Atomic classes

4. **Advanced Patterns**
   - Fork/Join framework
   - Parallel Streams
   - References: `references/java-concurrency.md`

### Phase 4: IO and NIO

Handle data operations efficiently:

1. **Traditional IO**
   - InputStream/OutputStream hierarchies
   - Reader/Writer for text processing
   - Buffering strategies

2. **NIO.2 API**
   - Path and Files API
   - Asynchronous file operations
   - File system watchers
   - References: `references/java-io-nio.md`

### Phase 5: JVM Internals

Understand how Java runs:

1. **Memory Model**
   - Heap vs Stack memory
   - Garbage collection algorithms
   - Generational GC

2. **Class Loading**
   - Classloader hierarchy
   - Custom classloaders
   - OSGi and modular systems

3. **Performance Profiling**
   - JVM monitoring tools
   - GC log analysis
   - HotSpot optimization
   - References: `references/jvm-internals.md`

### Phase 6: Enterprise Development

Build production applications:

1. **Spring Boot**
   - Application structure
   - Dependency injection
   - Configuration management
   - REST API development
   - References: `references/spring-boot.md`

2. **Design Patterns**
   - GoF patterns in Java
   - Enterprise patterns (DAO, DTO, Service Layer)
   - References: `references/enterprise-patterns.md`

3. **Testing**
   - Unit testing with JUnit 5
   - Integration testing
   - Mock testing with Mockito
   - References: `references/testing.md`

### Phase 7: Performance and Optimization

Write high-performance code:

1. **Code Optimization**
   - String handling best practices
   - Collection selection strategies
   - Stream vs loop performance

2. **JVM Tuning**
   - Heap sizing strategies
   - GC algorithm selection
   - JIT compilation tuning
   - References: `references/performance-tuning.md`

## Common Development Techniques

### Code Patterns

**Immutable Objects:**
```java
// Use records (JDK 14+)
record Point(int x, int y) {}

// Or final class pattern
final class ImmutableUser {
    private final String name;
    private final int age;
    // constructor and getters only
}
```

**Builder Pattern:**
```java
// Use Lombok @Builder or implement manually
public final class User {
    private final String name;
    private final int age;

    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private int age;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

### Error Handling Best Practices

```java
// Prefer specific exceptions
try {
    process();
} catch (IOException e) {
    log.error("Failed to process file", e);
    throw new ProcessingException("File processing failed", e);
}

// Use try-with-resources
try (var stream = Files.newInputStream(path);
     var reader = new BufferedReader(new InputStreamReader(stream))) {
    // process
}
```

### Stream API Usage

```java
// Complex data processing
List<String> result = items.stream()
    .filter(item -> item.isValid())
    .map(Item::getName)
    .distinct()
    .sorted(Comparator.comparing(String::length))
    .collect(Collectors.toList());

// Parallel processing for CPU-bound tasks
List<Result> processed = data.parallelStream()
    .map(this::heavyComputation)
    .collect(Collectors.toList());
```

## Knowledge Graph Structure

```
Java JDK 17+
├── Core Language
│   ├── Syntax & Operators
│   ├── OOP (Classes, Interfaces, Abstract)
│   ├── Enums & Records
│   ├── Exception Handling
│   └── Generics & Type System
├── Collections Framework
│   ├── List (ArrayList, LinkedList)
│   ├── Set (HashSet, TreeSet, LinkedHashSet)
│   ├── Map (HashMap, TreeMap, LinkedHashMap)
│   └── Queue (PriorityQueue, ArrayDeque)
├── Concurrency
│   ├── Threads & Executors
│   ├── Synchronization
│   ├── Concurrent Collections
│   ├── Locks & Atomic
│   └── Fork/Join & Parallel Streams
├── IO/NIO
│   ├── Traditional IO Streams
│   ├── NIO.2 Files API
│   ├── Buffer & Channels
│   └── Asynchronous IO
├── JVM Internals
│   ├── Memory Management (Heap, Stack, GC)
│   ├── Class Loading
│   ├── Bytecode & JIT
│   └── Performance Monitoring
├── JDK 17+ Features
│   ├── Records
│   ├── Pattern Matching
│   ├── Sealed Classes
│   ├── Text Blocks
│   ├── Switch Expressions
│   └── Enhanced APIs
├── Enterprise Development
│   ├── Spring Boot
│   ├── Design Patterns
│   ├── Testing (JUnit, Mockito)
│   └── Build Tools (Maven, Gradle)
└── Performance & Optimization
    ├── Code Optimization
    ├── JVM Tuning
    ├── GC Tuning
    └── Profiling Tools
```

## Quick Reference

### Selecting Collection Types

| Use Case | Recommended Type | Why |
|----------|------------------|-----|
| Frequent access by index | ArrayList | O(1) access |
| Frequent insertions at ends | LinkedList | O(1) insertion |
| Unique elements, order doesn't matter | HashSet | O(1) lookup |
| Unique elements, sorted | TreeSet | O(log n) operations |
| Key-value pairs | HashMap | O(1) lookup |
| Key-value pairs, sorted | TreeMap | O(log n) operations |
| Thread-safe key-value | ConcurrentHashMap | Lock-free reads |

### Choosing Executor Types

| Scenario | Executor Type | Configuration |
|----------|---------------|--------------|
| CPU-bound tasks | FixedThreadPool | Runtime.availableProcessors() |
| I/O-bound tasks | CachedThreadPool | Unlimited growing pool |
| Scheduled tasks | ScheduledThreadPool | Fixed size pool |
| Mixed workload | WorkStealingPool | ForkJoinPool |

## Additional Resources

### Reference Files

For detailed knowledge and implementation details, consult:

- **`references/java-basics.md`** - Core language fundamentals
- **`references/java-collections.md`** - Collections framework deep dive
- **`references/java-concurrency.md`** - Concurrency programming patterns
- **`references/java-io-nio.md`** - IO and NIO.2 comprehensive guide
- **`references/java-17-features.md`** - JDK 17+ new features详解
- **`references/jvm-internals.md`** - JVM architecture and internals
- **`references/spring-boot.md`** - Spring Boot enterprise development
- **`references/enterprise-patterns.md`** - Enterprise design patterns
- **`references/testing.md`** - Testing strategies and frameworks
- **`references/performance-tuning.md`** - Performance optimization techniques

### Example Files

Working examples in `examples/`:
- **`examples/HelloJava17.java`** - Java 17 features demo
- **`examples/ConcurrentExample.java`** - Concurrency examples
- **`examples/StreamExample.java`** - Stream API examples
- **`examples/ExceptionHandlingExample.java`** - Exception handling examples
- **`examples/DesignPatternsExample.java`** - Design patterns examples
