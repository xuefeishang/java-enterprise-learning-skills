# Java Enterprise Learning Plugin

# Java 企业级学习插件

Comprehensive learning guidance and knowledge graph for Java JDK 17+ enterprise development.

Java JDK 17+ 企业级开发的全面学习指导和知识图谱。

---

## Overview / 概述

This plugin provides:
- **Structured Learning Path**: 7-phase roadmap from basics to enterprise mastery
- **Knowledge Graph**: Complete map of Java ecosystem
- **Reference Documentation**: Detailed guides for each topic
- **Code Examples**: Ready-to-run examples for key concepts
- **Interactive Commands**: Quick access to learning resources

本插件提供：
- **结构化学习路径**：从基础到企业级精通的7阶段路线图
- **知识图谱**：Java生态系统的完整映射
- **参考文档**：每个主题的详细指南
- **代码示例**：关键概念的可运行示例
- **交互式命令**：快速访问学习资源

---

## Commands / 命令

| Command | Description | 命令 | 描述 |
|----------|-------------|------|------|
| `/java-learn [phase]` | Display learning roadmap and guidance | `/java-learn [阶段]` | 显示学习路线图和指导 |
| `/java-concept <concept>` | Explain specific Java concept | `/java-concept <概念>` | 解释特定Java概念 |
| `/java-example <topic>` | Generate code examples | `/java-example <主题>` | 生成代码示例 |
| `/java-practice [topic]` | Get coding practice problems | `/java-practice [主题]` | 获取编程练习题 |
| `/java-check [file]` | Review Java code for improvements | `/java-check [文件]` | 审查Java代码并建议改进 |

---

## Learning Phases / 学习阶段

### Phase 1: Core Foundations / 第一阶段：核心基础

- Language basics and OOP / 语言基础和面向对象
- Exception handling / 异常处理
- Generics and annotations / 泛型和注解

### Phase 2: Collections Framework / 第二阶段：集合框架

- List, Set, Map implementations / List, Set, Map 实现
- Stream API / 流式API
- Performance considerations / 性能考虑

### Phase 3: Concurrency / 第三阶段：并发编程

- Threads and Executors / 线程和执行器
- Synchronization / 同步机制
- Concurrent collections / 并发集合
- CompletableFuture / 异步编程

### Phase 4: IO and NIO / 第四阶段：IO和NIO

- Traditional IO streams / 传统IO流
- NIO.2 API / NIO.2 API
- Asynchronous IO / 异步IO

### Phase 5: JVM Internals / 第五阶段：JVM内部机制

- Memory management / 内存管理
- Garbage collection / 垃圾回收
- Class loading / 类加载
- Performance profiling / 性能分析

### Phase 6: Enterprise Development / 第六阶段：企业开发

- Spring Boot / Spring Boot框架
- Design patterns / 设计模式
- Testing strategies / 测试策略

### Phase 7: Performance Optimization / 第七阶段：性能优化

- Code optimization / 代码优化
- JVM tuning / JVM调优
- GC optimization / GC优化

---

## Installation / 安装

1. Copy this plugin to your Claude Code plugins directory
   / 将此插件复制到 Claude Code 插件目录
2. Restart Claude Code
   / 重启 Claude Code
3. The skill will be automatically discovered
   / 技能将被自动发现

---

## Usage / 使用

The skill is automatically activated when you ask about:

当您询问以下内容时，技能会自动激活：

- Learning Java / 学习Java
- Java 17 features / Java 17 特性
- Java enterprise development / Java 企业开发
- Java knowledge graph / Java 知识图谱
- Java interview preparation / Java 面试准备
- Specific Java topics (collections, concurrency, JVM, etc.)
  / 特定Java主题（集合、并发、JVM等）

Use commands for quick access to specific resources.

使用命令快速访问特定资源。

---

## Reference Files / 参考文档

Detailed documentation is available in `references/` directory:

详细文档位于 `references/` 目录：

| File / 文件 | Description / 描述 |
|-------------|-------------------|
| `java-basics.md` | Core language fundamentals / 核心语言基础 |
| `java-collections.md` | Collections framework / 集合框架 |
| `java-concurrency.md` | Concurrency programming / 并发编程 |
| `java-io-nio.md` | IO and NIO / IO和NIO |
| `java-17-features.md` | JDK 17+ features / JDK 17+ 特性 |
| `jvm-internals.md` | JVM architecture / JVM 架构 |
| `spring-boot.md` | Spring Boot development / Spring Boot 开发 |
| `enterprise-patterns.md` | Design patterns / 设计模式 |
| `testing.md` | Testing strategies / 测试策略 |
| `performance-tuning.md` | Performance optimization / 性能优化 |

---

## Code Examples / 代码示例

Ready-to-run examples in `examples/`:

`examples/` 目录中的可运行示例：

| File / 文件 | Description / 描述 |
|-------------|-------------------|
| `HelloJava17.java` | Java 17 features demo / Java 17 特性演示 |
| `ConcurrentExample.java` | Concurrency patterns / 并发模式 |
| `StreamExample.java` | Stream API usage / Stream API 使用 |
| `ExceptionHandlingExample.java` | Exception handling / 异常处理 |
| `DesignPatternsExample.java` | Design patterns / 设计模式 |

---

## Features / 特性

### Comprehensive Coverage / 全面覆盖

- Java Core: Syntax, OOP, Generics, Lambda
- Collections: List, Set, Map, Stream API
- Concurrency: Threads, Locks, Executors, CompletableFuture
- IO/NIO: Traditional IO, NIO.2, Async IO
- JVM: Memory, GC, Class Loading, JIT
- Enterprise: Spring Boot, Design Patterns, Testing
- Java 17+: Records, Pattern Matching, Sealed Classes, Text Blocks

### Interactive Learning / 交互式学习

- Get explanations for any Java concept
- Generate code examples for specific topics
- Practice with coding problems
- Review code for improvements

---

## License / 许可证

MIT License

---

## Contributing / 贡献

Contributions are welcome! Feel free to submit issues or pull requests.

欢迎贡献！随时提交问题或拉取请求。

---

## Author / 作者

Created with Claude Code for comprehensive Java enterprise learning.

使用 Claude Code 创建，用于全面的Java企业级学习。
