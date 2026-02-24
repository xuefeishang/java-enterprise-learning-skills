---
name: java-concept
description: Explain a Java concept with examples and best practices
arguments:
  - name: concept
    description: Java concept to explain (e.g., "stream", "concurrent", "jvm-gc")
    type: string
    required: true
  - name: detail
    description: Detail level (basic/advanced)
    type: string
    required: false
    default: basic
---

# Java Concept Explainer

Get detailed explanations of Java concepts with practical examples.

## Usage

```
/java-concept <concept> [detail]
```

## Common Concepts

**Core Language:**
- `interface` - Java interfaces and default methods
- `abstract` - Abstract classes vs interfaces
- `generic` - Generics and type safety
- `lambda` - Lambda expressions and functional interfaces
- `stream` - Stream API operations

**Collections:**
- `list` - ArrayList vs LinkedList
- `set` - HashSet vs TreeSet
- `map` - HashMap vs TreeMap
- `concurrent-map` - ConcurrentHashMap usage

**Concurrency:**
- `thread` - Thread basics and lifecycle
- `executor` - ExecutorService and thread pools
- `lock` - Synchronized vs Lock
- `atomic` - Atomic classes
- `future` - Future and CompletableFuture

**JVM:**
- `memory` - JVM memory model
- `gc` - Garbage collection
- `classloader` - Class loading mechanism
- `jit` - Just-In-Time compilation

**Spring:**
- `bean` - Spring Bean lifecycle
- `aop` - Aspect Oriented Programming
- `transaction` - Transaction management
- `rest` - REST API development

**Java 17+:**
- `record` - Records for immutable data
- `sealed` - Sealed classes
- `pattern-match` - Pattern matching
- `text-block` - Text blocks
- `switch-exp` - Switch expressions

## Examples

```
/java-concept stream              # Basic stream explanation
/java-concept stream advanced    # Advanced stream features
/java-concept concurrent-map      # ConcurrentHashMap usage
/java-concept record             # Java 17 records
```
