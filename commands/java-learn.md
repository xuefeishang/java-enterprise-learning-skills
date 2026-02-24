---
name: java-learn
description: Display Java learning roadmap and progress guidance
arguments:
  - name: phase
    description: Learning phase (optional, e.g., "basics", "collections", "concurrency")
    type: string
    required: false
---

# Java Learning Roadmap

Display your Java learning journey with structured phases and detailed guidance.

## Usage

```
/java-learn [phase]
```

## Phases

### Phase 1: Core Foundations (basics)
- Language syntax and operators
- OOP principles
- Exception handling
- Generics and annotations

### Phase 2: Collections (collections)
- List, Set, Map implementations
- Choosing the right collection
- Stream API
- Collection best practices

### Phase 3: Concurrency (concurrency)
- Threads and Executors
- Synchronization mechanisms
- Concurrent collections
- CompletableFuture and reactive patterns

### Phase 4: IO & NIO (io-nio)
- Traditional IO streams
- NIO.2 API
- Asynchronous IO
- File system operations

### Phase 5: JVM Internals (jvm)
- Memory management
- Garbage collection
- Class loading
- Performance profiling

### Phase 6: Enterprise (enterprise)
- Spring Boot development
- Design patterns
- Testing strategies
- Microservices

### Phase 7: Performance (performance)
- Code optimization
- JVM tuning
- GC optimization
- Profiling tools

Use `/java-learn <phase>` for detailed guidance on a specific phase.
