# Java Enterprise Learning Plugin

Comprehensive learning guidance and knowledge graph for Java JDK 17+ enterprise development.

## Overview

This plugin provides:
- **Structured Learning Path**: 7-phase roadmap from basics to enterprise mastery
- **Knowledge Graph**: Complete map of Java ecosystem
- **Reference Documentation**: Detailed guides for each topic
- **Code Examples**: Ready-to-run examples for key concepts
- **Interactive Commands**: Quick access to learning resources

## Commands

| Command | Description |
|----------|-------------|
| `/java-learn [phase]` | Display learning roadmap and guidance |
| `/java-concept <concept>` | Explain specific Java concept |
| `/java-example <topic>` | Generate code examples |
| `/java-practice [topic]` | Get coding practice problems |
| `/java-check [file]` | Review Java code for improvements |

## Learning Phases

### Phase 1: Core Foundations
- Language basics and OOP
- Exception handling
- Generics and annotations

### Phase 2: Collections Framework
- List, Set, Map implementations
- Stream API
- Performance considerations

### Phase 3: Concurrency
- Threads and Executors
- Synchronization
- Concurrent collections
- CompletableFuture

### Phase 4: IO and NIO
- Traditional IO streams
- NIO.2 API
- Asynchronous IO

### Phase 5: JVM Internals
- Memory management
- Garbage collection
- Class loading
- Performance profiling

### Phase 6: Enterprise Development
- Spring Boot
- Design patterns
- Testing strategies

### Phase 7: Performance Optimization
- Code optimization
- JVM tuning
- GC optimization

## Installation

1. Copy this plugin to your Claude Code plugins directory
2. Restart Claude Code
3. The skill will be automatically discovered

## Usage

The skill is automatically activated when you ask about:
- Learning Java
- Java 17 features
- Java enterprise development
- Java knowledge graph
- Java interview preparation
- Specific Java topics (collections, concurrency, JVM, etc.)

Use commands for quick access to specific resources.

## Reference Files

Detailed documentation is available in the `references/` directory:
- `java-basics.md` - Core language fundamentals
- `java-collections.md` - Collections framework
- `java-concurrency.md` - Concurrency programming
- `java-io-nio.md` - IO and NIO
- `java-17-features.md` - JDK 17+ features
- `jvm-internals.md` - JVM architecture
- `spring-boot.md` - Spring Boot development
- `enterprise-patterns.md` - Design patterns
- `testing.md` - Testing strategies
- `performance-tuning.md` - Performance optimization

## Code Examples

Ready-to-run examples in `examples/`:
- `HelloJava17.java` - Java 17 features demo
- `ConcurrentExample.java` - Concurrency patterns
- `StreamExample.java` - Stream API usage
- `ExceptionHandlingExample.java` - Exception handling
- `DesignPatternsExample.java` - Design patterns

## License

MIT License
