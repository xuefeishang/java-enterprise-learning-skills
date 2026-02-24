# Java Performance Tuning Reference Guide

## Table of Contents

1. [Code Optimization](#code-optimization)
2. [Memory Optimization](#memory-optimization)
3. [JVM Tuning](#jvm-tuning)
4. [GC Tuning](#gc-tuning)
5. [Concurrency Optimization](#concurrency-optimization)
6. [I/O Optimization](#io-optimization)
7. [Database Optimization](#database-optimization)
8. [Profiling Tools](#profiling-tools)
9. [Performance Patterns](#performance-patterns)
10. [Best Practices](#best-practices)

---

## Code Optimization

### String Handling

```java
// DO: Use StringBuilder for concatenation in loops
public String concatUsingBuilder(List<String> strings) {
    StringBuilder sb = new StringBuilder();
    for (String s : strings) {
        sb.append(s);
    }
    return sb.toString();
}

// DON'T: Use + operator in loops
public String concatBadWay(List<String> strings) {
    String result = "";
    for (String s : strings) {
        result += s;  // Creates new String each iteration
    }
    return result;
}

// For simple concatenation, + is fine (compile-time optimization)
String message = "Hello " + name + ", welcome!";  // Optimized by compiler

// String interning - use sparingly
String s1 = "hello";
String s2 = new String("hello").intern();  // Moves to string pool
// Don't intern random strings - can cause memory issues
```

### Collection Optimization

```java
// Choose appropriate collection type
// ArrayList - random access, frequent appends
List<Integer> list = new ArrayList<>();

// LinkedList - frequent insertions at ends
Deque<String> deque = new LinkedList<>();

// HashSet - fast lookup, no order
Set<String> set = new HashSet<>();

// LinkedHashSet - maintains insertion order
Set<String> orderedSet = new LinkedHashSet<>();

// HashMap - fast key-value lookup
Map<String, Integer> map = new HashMap<>();

// Initial capacity tuning
int expectedSize = 10000;
List<String> list = new ArrayList<>(expectedSize);
Map<String, Integer> map = new HashMap<>(expectedSize * 4 / 3 + 1);  // Load factor 0.75

// Use primitive collections for better performance (Eclipse Collections, fastutil)
IntList intList = new IntArrayList();
LongMap<String> longMap = new LongOpenHashMap<>();
```

### Loop Optimization

```java
// Enhanced for loop - clean and efficient
for (String item : items) {
    process(item);
}

// Traditional loop - when index needed
for (int i = 0; i < items.size(); i++) {
    process(items.get(i));
}

// Iterator - when modifying during iteration
Iterator<String> iterator = items.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    if (shouldRemove(item)) {
        iterator.remove();
    }
}

// Avoid method calls in loop conditions
// Bad
for (int i = 0; i < list.size(); i++) {  // size() called each iteration
    process(list.get(i));
}

// Good
int size = list.size();
for (int i = 0; i < size; i++) {
    process(list.get(i));
}
```

### Stream vs Loop Performance

```java
// Streams - readable, good for complex operations
List<String> result = items.stream()
    .filter(item -> item.length() > 5)
    .map(String::toUpperCase)
    .distinct()
    .collect(Collectors.toList());

// Traditional loops - often faster for simple operations
List<String> result = new ArrayList<>();
for (String item : items) {
    if (item.length() > 5) {
        String upper = item.toUpperCase();
        if (!result.contains(upper)) {
            result.add(upper);
        }
    }
}

// Parallel streams - for CPU-bound operations on large datasets
List<Result> processed = largeData.parallelStream()
    .map(this::heavyComputation)
    .collect(Collectors.toList());

// Note: Parallel streams have overhead, use only for large datasets
```

---

## Memory Optimization

### Object Reuse

```java
// Object pools for frequently created objects
class ObjectPoolExample {
    private final ArrayBlockingQueue<Buffer> pool;

    public ObjectPoolExample(int poolSize) {
        this.pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(new Buffer());
        }
    }

    public Buffer borrowBuffer() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Buffer();
        }
    }

    public void returnBuffer(Buffer buffer) {
        buffer.clear();
        pool.offer(buffer);
    }
}
```

### Immutable Objects

```java
// Records (Java 16+) - immutable by default
record User(String name, String email, int age) {}

// Final class pattern
public final class ImmutableUser {
    private final String name;
    private final String email;

    public ImmutableUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters only, no setters
}

// Benefits:
// - Thread-safe without synchronization
// - Can be shared freely
// - Easier to reason about
```

### Memory Leaks Prevention

```java
// Static collections - common source of leaks
// Bad - grows unbounded
public class CacheBad {
    private static final Map<String, Object> CACHE = new HashMap<>();

    public void addToCache(String key, Object value) {
        CACHE.put(key, value);  // Never removed
    }
}

// Good - use weak references or size limits
public class CacheGood {
    private static final Map<String, Object> CACHE =
        new ConcurrentHashMap<>();

    private static final int MAX_SIZE = 1000;

    public void addToCache(String key, Object value) {
        if (CACHE.size() >= MAX_SIZE) {
            CACHE.clear();  // Simple eviction strategy
        }
        CACHE.put(key, value);
    }
}

// Use WeakHashMap for cache keys
Map<Key, Value> cache = new WeakHashMap<>();

// Use Guava Cache with proper eviction
Cache<String, Object> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();
```

---

## JVM Tuning

### Heap Sizing

```java
// Recommended heap sizing for different scenarios

// Small application (< 2GB heap)
-Xms512m -Xmx1024m

// Medium application (2-8GB heap)
-Xms2g -Xmx4g

// Large application (> 8GB heap)
-Xms8g -Xmx16g

// Rule of thumb:
// - Initial heap (-Xms) should equal max heap (-Xmx) for production
// - Leave memory for OS and other processes
// - Typical allocation: 60-70% of physical memory for heap
```

### Heap Structure Tuning

```bash
# Young generation size
-XX:NewSize=1g                # Initial young generation
-XX:MaxNewSize=2g             # Maximum young generation

# New ratio - ratio of young to old generation
-XX:NewRatio=2                # Young:Old = 1:2 (default)
-XX:NewRatio=3                # Young:Old = 1:3

# Survivor ratio - Eden:S0:S1
-XX:SurvivorRatio=8           # Eden:S0:S1 = 8:1:1 (default)

# Example tuning
-XX:NewRatio=2 -XX:SurvivorRatio=8
# Young:Old = 1:2, Eden:S0:S1 = 8:1:1
```

### Thread Stack Sizing

```bash
# Thread stack size
-Xss256k                      # 256KB per thread (default)

# Calculate total thread memory
# Total thread memory = Number of threads * Stack size
# 1000 threads * 256KB = 256MB

# Reduce stack size for applications with many threads
-Xss128k                      # 128KB per thread
```

---

## GC Tuning

### G1 GC Tuning

```bash
# G1 GC options
-XX:+UseG1GC                          # Enable G1 GC
-XX:MaxGCPauseMillis=200             # Target pause time (ms)
-XX:G1HeapRegionSize=16m             # Region size
-XX:InitiatingHeapOccupancyPercent=45 # Start marking at 45% heap usage
-XX:G1ReservePercent=10              # Reserve 10% of heap

# Example configuration for 4GB heap
-XX:+UseG1GC
-Xms4g -Xmx4g
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:InitiatingHeapOccupancyPercent=45
```

### ZGC Tuning

```bash
# ZGC options (Java 17+)
-XX:+UseZGC                          # Enable ZGC
-XX:ZCollectionInterval=5             # GC interval (seconds)
-XX:ZAllocationSpikeTolerance=5      # Allocation spike tolerance

# Example configuration for 16GB heap
-XX:+UseZGC
-Xms16g -Xmx16g
-XX:ZCollectionInterval=5
```

### GC Logging

```bash
# GC logging (Java 11+)
-Xlog:gc*                            # Enable all GC logging
-Xlog:gc=info:file=gc.log:time,uptime,level,tags
-Xlog:gc+heap=debug:file=gc-heap.log
-Xlog:gc+ref=debug:file=gc-ref.log

# Log rotation
-Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=10M
```

---

## Concurrency Optimization

### Thread Pool Tuning

```java
// CPU-bound tasks
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuPool = Executors.newFixedThreadPool(cores);

// I/O-bound tasks
ExecutorService ioPool = Executors.newFixedThreadPool(cores * 2);

// Mixed workload
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// Custom thread pool
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    cores,                  // Core pool size
    cores * 2,             // Maximum pool size
    60L, TimeUnit.SECONDS,  // Keep alive time
    new LinkedBlockingQueue<>(100),  // Work queue
    new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy
);
```

### Concurrent Collections

```java
// ConcurrentHashMap - for high-concurrency scenarios
ConcurrentMap<String, User> userMap = new ConcurrentHashMap<>();

// Atomic operations
userMap.computeIfAbsent(userId, id -> fetchUser(id));
userMap.computeIfPresent(userId, (id, user) -> updateUser(user));
userMap.merge(userId, userData, (old, newData) -> mergeUserData(old, newData));

// Concurrent queues
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();  // Unbounded
LinkedBlockingQueue<String> boundedQueue = new LinkedBlockingQueue<>(100);  // Bounded
```

### Lock Optimization

```java
// Use ReadWriteLock for read-heavy workloads
private final ReadWriteLock lock = new ReentrantReadWriteLock();

public User getUser(String id) {
    lock.readLock().lock();
    try {
        return userMap.get(id);
    } finally {
        lock.readLock().unlock();
    }
}

public void updateUser(User user) {
    lock.writeLock().lock();
    try {
        userMap.put(user.getId(), user);
    } finally {
        lock.writeLock().unlock();
    }
}

// Use StampedLock for optimistic reads
private final StampedLock lock = new StampedLock();

public User getUserOptimistic(String id) {
    long stamp = lock.tryOptimisticRead();
    User user = userMap.get(id);
    if (!lock.validate(stamp)) {
        stamp = lock.readLock();
        try {
            user = userMap.get(id);
        } finally {
            lock.unlockRead(stamp);
        }
    }
    return user;
}
```

---

## I/O Optimization

### NIO for Large Files

```java
// Traditional IO (slower for large files)
public void copyTraditional(String source, String dest) throws IOException {
    try (InputStream in = new FileInputStream(source);
         OutputStream out = new FileOutputStream(dest)) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}

// NIO with FileChannel (faster for large files)
public void copyNIO(String source, String dest) throws IOException {
    try (FileChannel in = FileChannel.open(Paths.get(source), StandardOpenOption.READ);
         FileChannel out = FileChannel.open(Paths.get(dest),
             StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

        long size = in.size();
        in.transferTo(0, size, out);
    }
}

// Memory-mapped files for random access
public void processRandomAccess(String file) throws IOException {
    try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
         FileChannel channel = raf.getChannel()) {

        MappedByteBuffer buffer = channel.map(
            FileChannel.MapMode.READ_WRITE, 0, channel.size()
        );

        // Random access
        buffer.put(0, (byte) 'H');
        byte b = buffer.get(1);
    }
}
```

### Buffer Pooling

```java
// Reuse buffers to reduce allocation
public class BufferPool {
    private final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<>();
    private final int bufferSize;

    public BufferPool(int bufferSize, int poolSize) {
        this.bufferSize = bufferSize;
        for (int i = 0; i < poolSize; i++) {
            pool.add(ByteBuffer.allocateDirect(bufferSize));
        }
    }

    public ByteBuffer borrowBuffer() {
        ByteBuffer buffer = pool.poll();
        return buffer != null ? buffer : ByteBuffer.allocateDirect(bufferSize);
    }

    public void returnBuffer(ByteBuffer buffer) {
        buffer.clear();
        pool.offer(buffer);
    }
}
```

---

## Database Optimization

### Connection Pooling

```java
// HikariCP - recommended connection pool
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");

        // Connection pool settings
        config.setMaximumPoolSize(20);           // Max connections
        config.setMinimumIdle(5);               // Min idle connections
        config.setIdleTimeout(300000);          // Idle timeout (5 min)
        config.setConnectionTimeout(30000);      // Connection timeout (30 sec)
        config.setMaxLifetime(1800000);         // Max lifetime (30 min)

        return new HikariDataSource(config);
    }
}
```

### Batch Operations

```java
// Batch inserts for better performance
@Transactional
public void batchInsert(List<User> users) {
    jdbcTemplate.batchUpdate(
        "INSERT INTO users (name, email) VALUES (?, ?)",
        new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
}

// Spring Data JPA batch insert
@Transactional
public void batchInsert(List<User> users) {
    for (User user : users) {
        userRepository.save(user);
    }
}
```

### Query Optimization

```java
// Use projections to select only needed fields
public interface UserNameProjection {
    Long getId();
    String getName();
}

@Query("SELECT new com.example.UserNameDTO(u.id, u.name) FROM User u")
List<UserNameDTO> findNamesOnly();

// Use pagination for large result sets
Page<User> users = userRepository.findAll(PageRequest.of(0, 100));

// Use @EntityGraph to avoid N+1 queries
@EntityGraph(attributePaths = {"orders", "addresses"})
User findByIdWithRelations(Long id);
```

---

## Profiling Tools

### JFR (Java Flight Recorder)

```bash
# Enable JFR
-XX:+FlightRecorder
-XX:StartFlightRecording=duration=60s,filename=recording.jfr
-XX:FlightRecorderOptions=settings=profile

# Analyze recording
jfr --print recording.jfr

# Visual analysis
jmc  # Java Mission Control
```

### VisualVM

```java
// Launch VisualVM
jvisualvm

// Or use the embedded profiler
-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
```

### YourKit

```bash
# Launch with YourKit agent
-agentpath:/path/to/libyjpagent.so

# Start recording from UI
- start CPU profiling
- start memory profiling
- analyze results
```

---

## Performance Patterns

### Lazy Initialization

```java
// Traditional lazy initialization
public class LazyExample {
    private ExpensiveObject expensiveObject;

    public ExpensiveObject getExpensiveObject() {
        if (expensiveObject == null) {
            expensiveObject = new ExpensiveObject();
        }
        return expensiveObject;
    }
}

// Thread-safe lazy initialization
public class ThreadSafeLazy {
    private volatile ExpensiveObject expensiveObject;

    public ExpensiveObject getExpensiveObject() {
        ExpensiveObject result = expensiveObject;
        if (result == null) {
            synchronized (this) {
                result = expensiveObject;
                if (result == null) {
                    expensiveObject = result = new ExpensiveObject();
                }
            }
        }
        return result;
    }
}

// Using Supplier (Java 8+)
public class LazySupplier {
    private final Supplier<ExpensiveObject> supplier =
        Suppliers.memoize(ExpensiveObject::new);

    public ExpensiveObject getExpensiveObject() {
        return supplier.get();
    }
}
```

### Object Pool Pattern

```java
public class ObjectPool<T> {
    private final Queue<T> pool;
    private final Supplier<T> supplier;
    private final int maxSize;

    public ObjectPool(Supplier<T> supplier, int maxSize) {
        this.supplier = supplier;
        this.maxSize = maxSize;
        this.pool = new ConcurrentLinkedQueue<>();
    }

    public T borrow() {
        T obj = pool.poll();
        return obj != null ? obj : supplier.get();
    }

    public void release(T obj) {
        if (pool.size() < maxSize) {
            pool.offer(obj);
        }
    }
}

// Usage
ObjectPool<Buffer> bufferPool = new ObjectPool<>(
    Buffer::new,
    100
);

Buffer buffer = bufferPool.borrow();
// Use buffer
bufferPool.release(buffer);
```

---

## Best Practices

### General Guidelines

```java
// 1. Profile before optimizing
// Don't guess where the bottlenecks are

// 2. Measure, don't assume
long start = System.nanoTime();
// code to measure
long duration = System.nanoTime() - start;

// 3. Use appropriate data structures
// Choose based on operations, not defaults

// 4. Avoid premature optimization
// Write clean code first, optimize hotspots later

// 5. Consider algorithm complexity over micro-optimizations
// O(n log n) vs O(n) beats constant factors

// 6. Cache expensive operations
@Cacheable("users")
public User getUser(String id) {
    return userRepository.findById(id);
}

// 7. Use connection pooling
// For database, HTTP, etc.

// 8. Optimize hot code paths
// Focus on frequently executed code
```

### Performance Anti-Patterns

```java
// Avoid these patterns:

// 1. Premature synchronization
synchronized (this) {
    int result = expensiveComputation();  // No need to synchronize
}

// 2. String concatenation in loops
String result = "";
for (String s : list) {
    result += s;  // Creates new String each time
}

// 3. Autoboxing in loops
List<Integer> list = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    list.add(i);  // Autoboxing overhead
}
// Better: use int[] or primitive collections

// 4. Excessive logging in hot paths
if (logger.isDebugEnabled()) {  // Check level first
    logger.debug("Value: " + value);
}

// 5. Using + for SQL concatenation (SQL injection risk)
String query = "SELECT * FROM users WHERE name = '" + name + "'";
// Use prepared statements instead
```

---

## Summary

This reference covers Java performance tuning:

- **Code Optimization**: String handling, collections, loops, streams
- **Memory Optimization**: Object reuse, immutability, leak prevention
- **JVM Tuning**: Heap sizing, thread stack sizing
- **GC Tuning**: G1 GC, ZGC configuration
- **Concurrency Optimization**: Thread pools, concurrent collections, locks
- **I/O Optimization**: NIO, buffer pooling
- **Database Optimization**: Connection pooling, batch operations
- **Profiling Tools**: JFR, VisualVM, YourKit
- **Performance Patterns**: Lazy initialization, object pools
- **Best Practices**: General guidelines and anti-patterns

Performance optimization requires careful measurement and profiling. Focus on optimizing hot code paths and bottlenecks identified through profiling rather than premature optimization.
