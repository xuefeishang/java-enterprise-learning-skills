---
name: java-example
description: Generate code examples for specific Java topics
arguments:
  - name: topic
    description: Topic for code example (e.g., "stream", "concurrent", "spring-rest")
    type: string
    required: true
  - name: type
    description: Example type (simple/practical/advanced)
    type: string
    required: false
    default: practical
---

# Java Code Example Generator

Generate ready-to-use Java code examples for various topics.

## Usage

```
/java-example <topic> [type]
```

## Topics

### Core Language
- `string` - String operations and optimization
- `exception` - Exception handling patterns
- `generic` - Generic type usage
- `lambda` - Lambda expressions
- `optional` - Optional usage

### Collections
- `list` - List operations
- `set` - Set operations
- `map` - Map operations
- `stream` - Stream API examples
- `collector` - Custom collectors

### Concurrency
- `thread` - Thread creation and management
- `executor` - ExecutorService usage
- `lock` - Lock mechanisms
- `atomic` - Atomic operations
- `completable` - CompletableFuture patterns

### IO/NIO
- `file` - File operations
- `stream-io` - IO streams
- `nio` - NIO.2 operations
- `async-io` - Asynchronous IO

### Spring
- `controller` - REST controller
- `service` - Service layer
- `repository` - JPA repository
- `dto` - DTO pattern
- `config` - Configuration

### Java 17+
- `record` - Record usage
- `text-block` - Text blocks
- `switch-exp` - Switch expressions
- `pattern-match` - Pattern matching

## Types

- `simple` - Basic examples for understanding concepts
- `practical` - Real-world usage examples (default)
- `advanced` - Advanced patterns and techniques

## Examples

```
/java-example stream practical          # Practical stream example
/java-example concurrent advanced       # Advanced concurrency patterns
/java-example spring-rest practical    # Spring REST API example
/java-example record simple            # Basic record example
```
