# JVM Internals Reference Guide

## Table of Contents

1. [JVM Architecture](#jvm-architecture)
2. [Memory Management](#memory-management)
3. [Garbage Collection](#garbage-collection)
4. [Class Loading](#class-loading)
5. [Bytecode and Execution](#bytecode-and-execution)
6. [JIT Compilation](#jit-compilation)
7. [Performance Monitoring](#performance-monitoring)
8. [GC Tuning](#gc-tuning)
9. [JVM Options](#jvm-options)
10. [Performance Analysis](#performance-analysis)

---

## JVM Architecture

### JVM Components

```
┌─────────────────────────────────────────┐
│         Class Loader Subsystem          │
├─────────────────────────────────────────┤
│         Runtime Data Areas              │
│  ┌──────────┐  ┌──────────┐  ┌──────┐ │
│  │   Heap   │  │  Stack   │  │ PC   │ │
│  │          │  │  (per    │  │      │ │
│  │          │  │  thread) │  │      │ │
│  └──────────┘  └──────────┘  └──────┘ │
│  ┌──────────┐  ┌──────────┐  ┌──────┐ │
│  │ Method   │  │ Native   │  │ Code │ │
│  │ Area     │  │ Method   │  │ Cache│ │
│  └──────────┘  └──────────┘  └──────┘ │
├─────────────────────────────────────────┤
│      Execution Engine                   │
│  ┌──────────┐  ┌──────────┐            │
│  │  JIT     │  │ Interpreter            │
│  └──────────┘  └──────────┘            │
├─────────────────────────────────────────┤
│      Native Method Interface (JNI)      │
├─────────────────────────────────────────┤
│         Native Libraries                │
└─────────────────────────────────────────┘
```

### JVM Architecture Components

```java
// Class Loader Subsystem
// - Bootstrap Class Loader
// - Extension/Platform Class Loader
// - Application Class Loader

// Runtime Data Areas
// - Heap (shared, stores objects)
// - Stack (per thread, stores method calls)
// - PC Register (per thread, stores current instruction)
// - Method Area (shared, stores class data)
// - Native Method Stack (per thread, native methods)
// - Code Cache (JIT compiled code)

// Execution Engine
// - Interpreter (executes bytecode)
// - JIT Compiler (compiles hot code to native)
// - Garbage Collector (manages memory)

// JNI
// - Interface between Java and native code
```

---

## Memory Management

### Heap Memory

```java
// Heap structure
/*
┌─────────────────────────────────┐
│         Young Generation         │
│  ┌──────────┬──────────┐        │
│  │   Eden   │  Survivor│        │
│  │          │  (S0/S1) │        │
│  └──────────┴──────────┘        │
├─────────────────────────────────┤
│         Old Generation          │
│  (Tenured Space)                │
├─────────────────────────────────┤
│      Metaspace (PermGen)        │
│  (Class metadata)               │
└─────────────────────────────────┘
*/

// Default heap sizes (Java 11+)
// - Xms: Initial heap size = 1/64 physical memory (min 256MB)
// - Xmx: Maximum heap size = 1/4 physical memory (min 512MB)

// Memory allocation example
public class MemoryAllocation {
    public static void main(String[] args) {
        // Object allocation on heap
        MyClass obj1 = new MyClass();
        MyClass obj2 = new MyClass();

        // Large object allocation
        byte[] largeArray = new byte[10 * 1024 * 1024];  // 10MB

        // String pool (special area)
        String s1 = "Hello";
        String s2 = "Hello";  // Same object in string pool

        // String.intern() moves to string pool
        String s3 = new String("World").intern();
    }
}

class MyClass {
    private int field1;
    private String field2;
    // Instance fields stored in heap
}
```

### Stack Memory

```java
// Stack memory per thread
public class StackExample {
    // Method frames on stack
    public void method1() {
        int local1 = 10;
        method2();
        // After method2 returns, its frame is popped
    }

    public void method2() {
        int local2 = 20;
        method3();
    }

    public void method3() {
        int local3 = 30;
        // Stack trace shows all method calls
    }

    // Stack overflow with deep recursion
    public void recursiveMethod(int depth) {
        if (depth == 0) return;
        recursiveMethod(depth - 1);  // Each call adds a frame
        // Too deep recursion causes StackOverflowError
    }
}

// Stack frame contains:
// - Local variables
// - Operand stack
// - Reference to constant pool
// - Return address
```

### Metaspace

```java
// Metaspace (Java 8+) - stores class metadata
// - Class definitions
// - Method bytecode
// - Constant pool
// - Field and method metadata

// Metaspace sizing
// -XX:MetaspaceSize=256m
// -XX:MaxMetaspaceSize=512m
// -XX:CompressedClassSpaceSize=256m

// Class metadata usage
public class ClassMetadata {
    // Class loaded into Metaspace
    private static final int CONSTANT = 42;
    private int instanceField;
    private static int staticField;

    // Methods stored in Metaspace
    public void instanceMethod() {}
    public static void staticMethod() {}
}

// Monitoring Metaspace
// - jcmd <pid> GC.class_stats
// - jstat -gcutil <pid>
```

### Memory Regions

```java
// Java Memory Model (JMM)
/*
┌─────────────────────────────────┐
│           Heap                   │
│  ┌─────────────────────────┐    │
│  │    Young Gen            │    │
│  │  ┌──────┐ ┌──────┐      │    │
│  │  │ Eden │ │ S0   │ S1   │    │
│  │  └──────┘ └──────┘      │    │
│  └─────────────────────────┘    │
│  ┌─────────────────────────┐    │
│  │    Old Gen              │    │
│  └─────────────────────────┘    │
├─────────────────────────────────┤
│        Metaspace                 │
│  ┌─────────────────────────┐    │
│  │ Class Metadata          │    │
│  └─────────────────────────┘    │
├─────────────────────────────────┤
│         Stack                   │
│  Thread1: Frame1, Frame2, ...    │
│  Thread2: Frame1, Frame2, ...    │
└─────────────────────────────────┘
*/

// Memory allocation example
public class MemoryRegions {
    public static void main(String[] args) {
        // Heap allocation
        Object heapObject = new Object();

        // Stack allocation
        int stackVariable = 42;
        methodCall();

        // String pool (special heap area)
        String stringPool = "String Pool";
    }

    private static void methodCall() {
        // Local variables on stack
        int localVar = 10;
        Object heapRef = new Object();
    }
}
```

---

## Garbage Collection

### GC Algorithms

```java
// Serial GC (single-threaded, small applications)
// -XX:+UseSerialGC

// Parallel GC (multi-threaded, throughput-focused)
// -XX:+UseParallelGC

// G1 GC (low pause time, large heaps)
// -XX:+UseG1GC

// ZGC (ultra-low pause time, large heaps) - Java 11+
// -XX:+UnlockExperimentalVMOptions -XX:+UseZGC

// Shenandoah GC (low pause time) - Java 12+
// -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC
```

### Generational GC

```java
// Generational GC concept
/*
Young Generation (frequent collections)
├── Eden Space (new objects)
├── Survivor Space S0 (surviving objects)
└── Survivor Space S1 (surviving objects)

Old Generation (infrequent collections)
└── Tenured Space (long-lived objects)
*/

// Object lifecycle
public class ObjectLifecycle {
    public static void main(String[] args) {
        // 1. Object created in Eden
        MyObject obj1 = new MyObject();

        // 2. After first GC (survives) -> S0
        obj1 = new MyObject();  // Old obj1 might survive to S0

        // 3. After multiple GCs -> S1
        // After many GCs -> Old Generation

        // 4. When Old Gen is full -> Full GC
    }
}

// GC triggers
// - Young GC: Eden is full
// - Old GC: Old generation is full
// - Full GC: Entire heap needs collection
```

### G1 GC

```java
// G1 (Garbage First) GC
// - Divides heap into equal-sized regions
// - Predictable pause times
// - Concurrent marking phase

// G1 GC options
-XX:+UseG1GC                          // Enable G1
-XX:MaxGCPauseMillis=200             // Target pause time (ms)
-XX:G1HeapRegionSize=16m             // Region size
-XX:InitiatingHeapOccupancyPercent=45 // Start marking at 45% heap usage

// G1 example
/*
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ E │ E │ E │ O │ O │ H │ H │ H │  (E=Eden, O=Old, H=Humongous)
├───┼───┼───┼───┼───┼───┼───┼───┤
│ E │ E │ S │ O │ O │ H │ H │ H │  (S=Survivor)
├───┼───┼───┼───┼───┼───┼───┼───┤
│ E │ E │ S │ O │ O │ O │ O │ O │
└───┴───┴───┴───┴───┴───┴───┴───┘
*/

// Monitoring G1
jcmd <pid> GC.heap_info
jcmd <pid> GC.region_stats
```

### ZGC

```java
// ZGC (Z Garbage Collector) - Java 11+
// - Ultra-low pause times (<10ms)
// - Scales to multi-terabyte heaps
// - Concurrent most phases

// ZGC options (Java 17+)
-XX:+UseZGC                          // Enable ZGC
-XX:ZCollectionInterval=5             // GC interval (seconds)
-XX:ZAllocationSpikeTolerance=5      // Allocation spike tolerance

// ZGC phases
// 1. Stop-the-world (STW) Mark Start
// 2. Concurrent Mark
// 3. Concurrent Relocate
// 4. Stop-the-world Relocate Start

// ZGC example for large heap
java -XX:+UseZGC -Xms32g -Xmx32g MyApp
```

### Shenandoah GC

```java
// Shenandoah GC - Java 12+
// - Ultra-low pause times
// - Forwarding pointers
// - Broader platform support than ZGC

// Shenandoah options
-XX:+UseShenandoahGC                 // Enable Shenandoah
-XX:ShenandoahGCHeuristics=compact   // Heuristic: compact, aggressive, etc.

// Monitoring Shenandoah
jcmd <pid> GC.heap_info
```

### GC Logs

```java
// Enable GC logging (Java 9+)
-Xlog:gc*
-Xlog:gc=*:file=gc.log:time,uptime,level,tags
-Xlog:gc+heap=debug:file=gc-heap.log

// GC log example
[0.023s][info][gc] GC(0) Pause Young (Allocation Failure) 8M->2M(16M) 2.913ms
// [time][level][tag] GC(id) Collection type Used->Used(Committed) pauseTime

// GC log rotation
-Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=10M

// GC logging options (Java 8)
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-Xloggc:gc.log
```

### Object Finalization

```java
// finalize() method (deprecated)
public class FinalizationExample {
    @Override
    protected void finalize() throws Throwable {
        try {
            // Cleanup resources
            System.out.println("Finalizing object");
        } finally {
            super.finalize();
        }
    }
}

// Try-with-resources (preferred)
public class ResourceExample implements AutoCloseable {
    private final Resource resource;

    public ResourceExample() {
        this.resource = new Resource();
    }

    @Override
    public void close() {
        resource.cleanup();
    }

    public static void main(String[] args) {
        try (ResourceExample example = new ResourceExample()) {
            // Use resource
        }  // Automatically closed
    }
}
```

---

## Class Loading

### Class Loader Hierarchy

```java
// Class loader delegation model
/*
Bootstrap Class Loader (native)
    ↑
Platform Class Loader
    ↑
Application Class Loader
    ↑
Custom Class Loaders
*/

// Bootstrap class loader
// - Loads core Java classes (java.*, javax.*)
// - Implemented in native code
// - No parent

// Platform class loader
// - Loads extension/platform classes
// - Java 9+: Extension class loader renamed

// Application class loader
// - Loads application classes
// - Also called system class loader

// Custom class loader
public class CustomClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name)
        throws ClassNotFoundException {

        // Custom class loading logic
        byte[] classBytes = loadClassBytes(name);
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] loadClassBytes(String name) {
        // Load class bytes from file, network, etc.
        return new byte[0];
    }
}
```

### Class Loading Process

```java
// 1. Loading
// - Find binary representation of class
// - Create Class object

// 2. Linking
// - Verification: Check bytecode validity
// - Preparation: Allocate memory for static fields
// - Resolution: Symbolic references to direct references

// 3. Initialization
// - Execute static initializers
// - Initialize static fields

public class ClassLoadingExample {
    static {
        System.out.println("Static initializer called");
    }

    private static final int CONSTANT = 42;
    private static int initialized;

    static {
        initialized = 100;
    }
}

// Class loading example
public class LoadClass {
    public static void main(String[] args) throws Exception {
        // Load class by name
        Class<?> clazz = Class.forName("java.util.ArrayList");

        // Get class loader
        ClassLoader loader = clazz.getClassLoader();

        // Get system class loader
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

        // Get platform class loader
        ClassLoader platformLoader = ClassLoader.getPlatformClassLoader();

        // Bootstrap class loader returns null
        ClassLoader bootstrapLoader = String.class.getClassLoader();
        System.out.println(bootstrapLoader);  // null
    }
}
```

### Reflection

```java
// Reflection example
public class ReflectionExample {
    public static void main(String[] args) throws Exception {
        // Get class object
        Class<?> clazz = MyClass.class;

        // Get constructors
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println("Constructor: " + constructor);
        }

        // Get methods
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            System.out.println("Method: " + method.getName());
        }

        // Get fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("Field: " + field.getName());
        }

        // Create instance
        MyClass instance = (MyClass) clazz.getDeclaredConstructor().newInstance();

        // Invoke method
        Method method = clazz.getMethod("myMethod", String.class);
        Object result = method.invoke(instance, "Hello");

        // Get/set field
        Field field = clazz.getDeclaredField("privateField");
        field.setAccessible(true);
        field.set(instance, "New Value");
    }
}

class MyClass {
    private String privateField;

    public MyClass() {}

    public void myMethod(String param) {
        System.out.println("Called with: " + param);
    }
}
```

---

## Bytecode and Execution

### Bytecode Instructions

```java
// Simple Java class
public class BytecodeExample {
    public int add(int a, int b) {
        return a + b;
    }
}

// Equivalent bytecode (simplified)
/*
Method add(int, int):int
0:  iload_1        // Load int from local variable 1 (a)
1:  iload_2        // Load int from local variable 2 (b)
2:  iadd           // Add two ints
3:  ireturn        // Return int
*/

// Bytecode viewer
javap -c BytecodeExample

// Detailed bytecode
javap -c -v BytecodeExample
```

### Method Invocation

```java
// Method invocation bytecodes
// invokevirtual: Virtual method dispatch
// invokeinterface: Interface method dispatch
// invokespecial: Constructor, private, super methods
// invokestatic: Static methods
// invokedynamic: Dynamic method invocation

public class MethodInvocation {
    // Static method - invokestatic
    public static void staticMethod() {
        System.out.println("Static method");
    }

    // Private method - invokespecial
    private void privateMethod() {
        System.out.println("Private method");
    }

    // Instance method - invokevirtual
    public void instanceMethod() {
        System.out.println("Instance method");
    }
}

interface MyInterface {
    void interfaceMethod();  // invokeinterface
}
```

### Stack-Based Execution

```java
// Stack-based virtual machine
public class StackExecution {
    public int calculate() {
        int a = 10;
        int b = 20;
        int c = a + b;
        return c * 2;
    }
}

// Stack operations
/*
1. Load 10 -> stack
   Stack: [10]

2. Store to local variable a
   Stack: []

3. Load 20 -> stack
   Stack: [20]

4. Store to local variable b
   Stack: []

5. Load a -> stack
   Stack: [10]

6. Load b -> stack
   Stack: [10, 20]

7. Add -> stack
   Stack: [30]

8. Store to local variable c
   Stack: []

9. Load c -> stack
   Stack: [30]

10. Load 2 -> stack
    Stack: [30, 2]

11. Multiply -> stack
    Stack: [60]

12. Return
*/
```

---

## JIT Compilation

### JIT Compilation Levels

```java
// JIT (Just-In-Time) Compiler
// - Interprets bytecode initially
// - Compiles hot methods to native code
// - Improves performance over time

// Compilation levels
// Level 0: Interpreter
// Level 1: C1 compiler (client compiler, fast compilation)
// Level 2: C1 compiler with profiling
// Level 3: C1 compiler with full profiling
// Level 4: C2 compiler (server compiler, optimized code)

// JIT compilation options
-XX:+TieredCompilation              // Enable tiered compilation (default)
-XX:CompileThreshold=10000           // Compile after 10000 calls
-XX:InitialCodeCacheSize=16m         // Initial code cache size
-XX:ReservedCodeCacheSize=256m        // Reserved code cache size
```

### C1 and C2 Compilers

```java
// C1 Compiler (Client)
// - Fast compilation
// - Minimal optimization
// - Good for short-lived applications

// C2 Compiler (Server)
// - Slow compilation
// - Aggressive optimization
// - Better for long-running applications

// Compiler selection
-XX:+UseSerialGC                     // Use C1 only
-XX:+UseParallelGC                   // Use C2 only
-XX:+TieredCompilation               // Use both (default)
```

### Inlining

```java
// Method inlining - replacing method calls with method body
public class InliningExample {
    public int add(int a, int b) {
        return a + b;  // Small method, likely inlined
    }

    public int calculate() {
        return add(10, 20);  // Might be inlined to: return 10 + 20;
    }
}

// Inlining options
-XX:MaxInlineSize=35                 // Max method size for inlining
-XX:FreqInlineSize=325                // Max size for frequently called methods
-XX:InlineSmallCode=1000              // Inlining threshold

// Preventing inlining
private synchronized void notInlineable() {
    // Complex method, unlikely to be inlined
}
```

### Escape Analysis

```java
// Escape analysis - determine object scope
public class EscapeAnalysisExample {
    public void method() {
        // Object does not escape
        MyObject obj = new MyObject();
        obj.method();
        // Can be allocated on stack
    }

    public MyObject createAndReturn() {
        // Object escapes method
        MyObject obj = new MyObject();
        return obj;  // Must be allocated on heap
    }
}

// Escape analysis options
-XX:+DoEscapeAnalysis                // Enable (default)
-XX:+EliminateAllocations             // Enable stack allocation
```

---

## Performance Monitoring

### JVM Tools

```java
// JVM monitoring tools

// 1. jps - Java process status
jps
jps -l  // With full package names
jps -v  // With JVM arguments

// 2. jstat - JVM statistics
jstat -gcutil <pid> 1000 5  // GC stats every 1 second, 5 times

// 3. jmap - Memory map
jmap -heap <pid>          // Heap configuration
jmap -histo:live <pid>    // Live object histogram
jmap -dump:format=b,file=heap.bin <pid>  // Heap dump

// 4. jstack - Stack trace
jstack <pid>               // Thread dump
jstack -l <pid>           // With locks

// 5. jcmd - JVM command
jcmd <pid> help           // List commands
jcmd <pid> GC.heap_info   // GC information
jcmd <pid> Thread.print   // Thread dump
jcmd <pid> VM.flags       // VM flags

// 6. jconsole - JVM monitoring GUI
jconsole

// 7. jvisualvm - All-in-one tool
jvisualvm
```

### JVM Monitoring API

```java
// MXBean - Management Extensions API
public class MonitoringExample {
    public static void main(String[] args) {
        // Memory MXBean
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        System.out.println("Heap used: " + heapUsage.getUsed() + " bytes");

        // Memory Pool MXBeans
        List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : memoryPools) {
            System.out.println("Pool: " + pool.getName() +
                ", Used: " + pool.getUsage().getUsed());
        }

        // Runtime MXBean
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM name: " + runtimeMXBean.getVmName());
        System.out.println("JVM version: " + runtimeMXBean.getVmVersion());

        // Thread MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println("Thread count: " + threadMXBean.getThreadCount());

        // Compilation MXBean
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        System.out.println("Compilation time: " +
            compilationMXBean.getTotalCompilationTime() + " ms");

        // GC MXBeans
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
            System.out.println("GC: " + gcMXBean.getName() +
                ", Collections: " + gcMXBean.getCollectionCount());
        }
    }
}
```

### Thread Dump Analysis

```java
// Thread dump contains:
// - Thread ID, name, state
// - Stack trace
// - Lock information
// - Blocked/Waiting threads

// Generating thread dump
jstack <pid> > thread-dump.txt

// Thread states
// - NEW: Not yet started
// - RUNNABLE: Executing in JVM
// - BLOCKED: Waiting for monitor lock
// - WAITING: Waiting indefinitely
// - TIMED_WAITING: Waiting with timeout
// - TERMINATED: Exited

// Deadlock detection
jstack <pid> | grep -A 5 "Found one Java-level deadlock"
```

### Heap Dump Analysis

```java
// Heap dump analysis tools

// 1. VisualVM
// - Load heap dump
// - Analyze object sizes
// - Find memory leaks

// 2. Eclipse MAT (Memory Analyzer Tool)
// - Detailed heap analysis
// - Leak suspects report
// - Dominator tree

// 3. JProfiler
// - Commercial tool
// - Real-time profiling
// - CPU and memory profiling

// Heap dump example
jmap -dump:format=b,file=heap.bin <pid>

// Analyzing heap dump
jhat heap.bin  // HTTP server for analysis
// Open http://localhost:7000 in browser
```

---

## GC Tuning

### GC Tuning Strategy

```java
// 1. Determine heap size
// - Start with 50-80% of available memory
// - Leave room for OS and other processes

// 2. Choose GC algorithm
// - Small heap (<4GB): Serial GC or Parallel GC
// - Medium heap (4-16GB): G1 GC or Parallel GC
// - Large heap (>16GB): G1 GC, ZGC, or Shenandoah

// 3. Tune GC parameters
// - Adjust young generation size
// - Set pause time goals
// - Adjust survivor ratios

// Example tunings

// Small application (Serial GC)
java -XX:+UseSerialGC -Xms512m -Xmx512m MyApp

// Medium application (Parallel GC)
java -XX:+UseParallelGC -Xms2g -Xmx2g -XX:ParallelGCThreads=4 MyApp

// Large application (G1 GC)
java -XX:+UseG1GC -Xms8g -Xmx8g -XX:MaxGCPauseMillis=200 MyApp

// Very large application (ZGC)
java -XX:+UseZGC -Xms32g -Xmx32g MyApp
```

### Young Generation Tuning

```java
// Young generation tuning
-XX:NewRatio=2                     // Young:Old = 1:2 (default 1:2)
-XX:SurvivorRatio=8                // Eden:S0:S1 = 8:1:1 (default 8:1:1)
-XX:MaxTenuringThreshold=15        // Max promotions to old (default 15)

// Example tuning
java -XX:NewRatio=3 -XX:SurvivorRatio=10 MyApp
// Young:Old = 1:3, Eden:S0:S1 = 10:1:1
```

### GC Performance Metrics

```java
// GC performance metrics to monitor:
// - GC pause time
// - GC frequency
// - Heap usage
// - CPU usage during GC
// - Promotion rate (young -> old)

// Calculating metrics
// - Throughput = Application time / Total time
// - Pause time = GC pause duration
// - Allocation rate = Bytes allocated per second

// GC log analysis example
/*
GC(0) Pause Young (Allocation Failure) 8M->2M(16M) 2.913ms
GC(1) Pause Young (Allocation Failure) 10M->3M(16M) 3.245ms
GC(2) Pause Full (Allocation Failure) 12M->8M(16M) 45.678ms
*/

// Interpretation:
// - Young GC: Frequent, short pauses
// - Full GC: Infrequent, long pauses (problematic)
```

---

## JVM Options

### Memory Options

```java
// Heap size options
-Xms512m                              // Initial heap size
-Xmx2g                                 // Maximum heap size
-XX:NewSize=256m                      // Initial young generation size
-XX:MaxNewSize=512m                   // Maximum young generation size
-XX:MetaspaceSize=256m                // Initial metaspace size
-XX:MaxMetaspaceSize=512m             // Maximum metaspace size

// Example configuration
java -Xms1g -Xmx4g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m MyApp
```

### GC Options

```java
// GC selection
-XX:+UseSerialGC                      // Serial GC
-XX:+UseParallelGC                    // Parallel GC
-XX:+UseG1GC                         // G1 GC
-XX:+UseZGC                          // ZGC
-XX:+UseShenandoahGC                  // Shenandoah GC

// GC tuning
-XX:MaxGCPauseMillis=200             // Target pause time (G1)
-XX:G1HeapRegionSize=16m             // G1 region size
-XX:ParallelGCThreads=4              // Parallel GC threads
-XX:ConcGCThreads=2                  // Concurrent GC threads

// GC logging
-Xlog:gc*                            // Enable GC logging
-Xlog:gc=*:file=gc.log               // Log to file
-Xlog:gc+heap=debug                  // Detailed heap logging
```

### Performance Options

```java
// JIT compilation options
-XX:+TieredCompilation                // Enable tiered compilation
-XX:CompileThreshold=10000           // Compilation threshold
-XX:InlineSmallCode=1000             // Inlining threshold

// Performance tuning
-XX:+UseCompressedOops               // Compressed object pointers (default)
-XX:+UseStringDeduplication          // String deduplication (G1 only)
-XX:+UseCompressedClassPointers      // Compressed class pointers

// Thread options
-XX:ParallelGCThreads=4              // GC thread count
-XX:ConcGCThreads=2                  // Concurrent GC threads
-XX:ActiveProcessorCount=4           // Number of CPUs to use
```

### Debugging Options

```java
// Debugging options
-verbose:class                        // Class loading info
-verbose:gc                           // GC logging
-verbose:jni                          // JNI logging
-Xcheck:jni                           // JNI checks
-XX:+PrintGCDetails                   // Detailed GC info (Java 8)
-XX:+PrintGCApplicationStoppedTime   // GC pause time

// Error handling
-XX:+HeapDumpOnOutOfMemoryError       // Dump heap on OOME
-XX:HeapDumpPath=/path/to/dump.hprof  // Heap dump location
-XX:OnOutOfMemoryError="kill -9 %p"   // Command on OOME

// Crash options
-XX:ErrorFile=/path/to/error.log     // Error log location
-XX:+ShowMessageBoxOnError           // Show dialog on error
```

---

## Performance Analysis

### Performance Profiling

```java
// Profiling tools

// 1. Java Flight Recorder (JFR) - Commercial feature (free in OpenJDK)
-XX:+FlightRecorder                  // Enable JFR
-XX:StartFlightRecording=duration=60s,filename=recording.jfr

// 2. YourKit - Commercial profiler
// - CPU profiling
// - Memory profiling
// - Thread profiling

// 3. JProfiler - Commercial profiler
// - Real-time profiling
// - JDBC/Spring/Hibernate support

// 4. Java Mission Control (JMC)
// - JVM monitoring
// - JFR recording and analysis
// - Thread analysis

// Using JFR
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=app.jfr MyApp

// Analyzing JFR recording
jmc  // Open Java Mission Control, load JFR file
```

### CPU Profiling

```java
// CPU profiling identifies:
// - Hot methods
// - Call trees
// - CPU time distribution

// CPU profiling with JFR
-XX:+FlightRecorder
-XX:StartFlightRecording=duration=60s,filename=cpu.jfr,settings=profile

// Using async-profiler (Linux)
./profiler.sh -d 30 -f cpu.svg <pid>
// Generates flame graph of CPU usage
```

### Memory Profiling

```java
// Memory profiling identifies:
// - Memory leaks
// - Object allocation rates
// - Memory distribution

// Memory profiling with JFR
-XX:+FlightRecorder
-XX:StartFlightRecording=duration=60s,filename=memory.jfr,settings=memory

// Heap dump analysis
jmap -dump:format=b,file=heap.bin <pid>

// Using VisualVM for memory profiling
// - Load heap dump
// - View histogram
// - Find dominator tree
// - Track allocations
```

### Thread Profiling

```java
// Thread profiling identifies:
// - Thread states
// - Deadlocks
// - Contention
// - Thread dumps

// Thread dump analysis
jstack <pid> > thread-dump.txt

// Using JFR for thread profiling
-XX:+FlightRecorder
-XX:StartFlightRecording=duration=60s,filename=thread.jfr,settings=thread

// Monitoring thread states
ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
long[] threadIds = threadMXBean.getAllThreadIds();
for (long threadId : threadIds) {
    ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
    System.out.println("Thread: " + threadInfo.getThreadName() +
        ", State: " + threadInfo.getThreadState());
}
```

---

## Summary

This reference covers JVM internals:

- **JVM Architecture**: Components and data areas
- **Memory Management**: Heap, stack, metaspace
- **Garbage Collection**: Algorithms and tuning
- **Class Loading**: Class loaders and reflection
- **Bytecode and Execution**: Stack-based VM
- **JIT Compilation**: C1/C2 compilers and optimization
- **Performance Monitoring**: JVM tools and MXBeans
- **GC Tuning**: Strategies and parameters
- **JVM Options**: Memory, GC, performance options
- **Performance Analysis**: Profiling and debugging

Understanding JVM internals is crucial for optimizing Java applications, troubleshooting performance issues, and making informed decisions about JVM configuration and tuning.
