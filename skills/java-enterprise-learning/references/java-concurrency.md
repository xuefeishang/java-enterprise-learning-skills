# Java Concurrency Reference Guide

## Table of Contents

1. [Threads and Runnables](#threads-and-runnables)
2. [Thread Lifecycle](#thread-lifecycle)
3. [Thread Synchronization](#thread-synchronization)
4. [Lock Interface](#lock-interface)
5. [Concurrent Collections](#concurrent-collections)
6. [Atomic Classes](#atomic-classes)
7. [Executor Framework](#executor-framework)
8. [CompletableFuture](#completablefuture)
9. [Fork/Join Framework](#forkjoin-framework)
10. [Advanced Patterns](#advanced-patterns)
11. [Best Practices](#best-practices)

---

## Threads and Runnables

### Creating Threads

```java
// 1. Extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + getName());
    }
}

// Usage
MyThread thread = new MyThread();
thread.start();  // Don't call run() directly!

// 2. Implementing Runnable (preferred)
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running: " + Thread.currentThread().getName());
    }
}

// Usage
Thread thread = new Thread(new MyRunnable());
thread.start();

// 3. Lambda expression (modern approach)
Thread thread = new Thread(() -> {
    System.out.println("Lambda thread running");
});
thread.start();

// 4. Anonymous inner class
Thread thread = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Anonymous runnable");
    }
});
thread.start();
```

### Callable and Future

```java
// Callable returns a result and can throw exceptions
Callable<String> task = () -> {
    Thread.sleep(1000);
    return "Task completed";
};

// Execute with ExecutorService
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<String> future = executor.submit(task);

try {
    // get() blocks until result is ready
    String result = future.get();
    System.out.println(result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
} finally {
    executor.shutdown();
}

// Non-blocking with isDone()
while (!future.isDone()) {
    System.out.println("Waiting...");
    Thread.sleep(100);
}

// Timeout
try {
    String result = future.get(2, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);  // Interrupt if running
}

// Multiple Callables
List<Callable<String>> tasks = List.of(
    () -> "Task 1",
    () -> "Task 2",
    () -> "Task 3"
);

// invokeAll - waits for all to complete
List<Future<String>> futures = executor.invokeAll(tasks);

// invokeAny - returns first completed result
String result = executor.invokeAny(tasks);
```

### Thread Naming and Priority

```java
// Thread naming
Thread namedThread = new Thread(() -> {
    System.out.println("Thread name: " + Thread.currentThread().getName());
}, "MyWorkerThread");
namedThread.start();

// Thread priority (1-10, default 5)
Thread highPriority = new Thread(() -> {}, "HighPriority");
highPriority.setPriority(Thread.MAX_PRIORITY);  // 10

Thread lowPriority = new Thread(() -> {}, "LowPriority");
lowPriority.setPriority(Thread.MIN_PRIORITY);  // 1

// Daemon threads (exit when main thread exits)
Thread daemonThread = new Thread(() -> {
    while (true) {
        try {
            Thread.sleep(1000);
            System.out.println("Daemon running");
        } catch (InterruptedException e) {
            break;
        }
    }
});
daemonThread.setDaemon(true);
daemonThread.start();

// Thread groups
ThreadGroup group = new ThreadGroup("MyThreadGroup");
Thread t1 = new Thread(group, "Thread 1");
Thread t2 = new Thread(group, "Thread 2");

// Thread information
Thread current = Thread.currentThread();
System.out.println("Name: " + current.getName());
System.out.println("ID: " + current.getId());
System.out.println("Priority: " + current.getPriority());
System.out.println("Is alive: " + current.isAlive());
System.out.println("Is daemon: " + current.isDaemon());
System.out.println("State: " + current.getState());
```

---

## Thread Lifecycle

### Thread States

```
NEW
 │
 │ start()
 ▼
RUNNABLE
 │
 │ waiting/blocking
 ▼
BLOCKED/WAITING/TIMED_WAITING
 │
 │
 ▼
TERMINATED
```

```java
// Demonstrate thread states
class StateDemo {
    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                // Waiting state
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("State: " + thread.getState());  // NEW

        thread.start();
        System.out.println("State: " + thread.getState());  // RUNNABLE

        Thread.sleep(100);
        System.out.println("State: " + thread.getState());  // TIMED_WAITING

        thread.join();
        System.out.println("State: " + thread.getState());  // TERMINATED
    }
}

// Thread.State enum values
enum ThreadState {
    NEW,           // Thread created but not started
    RUNNABLE,      // Running or ready to run
    BLOCKED,       // Waiting for monitor lock
    WAITING,       // Waiting indefinitely
    TIMED_WAITING, // Waiting with timeout
    TERMINATED     // Thread has exited
}
```

### Thread Interruption

```java
class InterruptionDemo {
    public static void main(String[] args) {
        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Sleep is interruptible
                    Thread.sleep(1000);
                    System.out.println("Working...");
                } catch (InterruptedException e) {
                    // Restore interrupt status
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted!");
                    break;
                }
            }
            System.out.println("Cleaning up...");
        });

        worker.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        worker.interrupt();  // Request interruption
    }
}

// Poll-based interruption
Thread thread = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        // Do work
        if (shouldStop()) {
            Thread.currentThread().interrupt();
        }
    }
});

// Interruptible operations
// - Thread.sleep()
// - Thread.join()
// - Object.wait()
// - Blocking I/O operations
// - Future.get()

// Non-interruptible operations
// - synchronized blocks/methods
// - I/O on standard streams
// - Math operations
```

### Thread Join

```java
// Join - wait for thread to complete
Thread t1 = new Thread(() -> {
    try {
        Thread.sleep(2000);
        System.out.println("Thread 1 complete");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

Thread t2 = new Thread(() -> {
    try {
        Thread.sleep(1000);
        System.out.println("Thread 2 complete");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

t1.start();
t2.start();

// Wait for both to complete
t1.join();
t2.join();

System.out.println("All threads complete");

// Join with timeout
t1.join(1000);  // Wait up to 1 second
if (t1.isAlive()) {
    System.out.println("Thread 1 still running");
}

// Sequential execution
Thread t1 = new Thread(() -> doTask1());
Thread t2 = new Thread(() -> doTask2());
Thread t3 = new Thread(() -> doTask3());

t1.start();
t1.join();  // Wait for t1
t2.start();
t2.join();  // Wait for t2
t3.start();
t3.join();  // Wait for t3
```

---

## Thread Synchronization

### Synchronized Methods

```java
class Counter {
    private int count = 0;

    // Synchronized instance method - locks on 'this'
    public synchronized void increment() {
        count++;
    }

    // Synchronized instance method
    public synchronized int getCount() {
        return count;
    }
}

// All synchronized methods on the same object are mutually exclusive
Counter counter = new Counter();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 10000; i++) {
        counter.increment();
    }
});
Thread t2 = new Thread(() -> {
    for (int i = 0; i < 10000; i++) {
        counter.increment();
    }
});

t1.start();
t2.start();
t1.join();
t2.join();

System.out.println("Count: " + counter.getCount());  // 20000
```

### Synchronized Blocks

```java
class BankAccount {
    private double balance;
    private final Object lock = new Object();  // Explicit lock object

    public void deposit(double amount) {
        // Synchronized block on specific object
        synchronized (lock) {
            double newBalance = balance + amount;
            // Simulate some work
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            balance = newBalance;
        }
    }

    public void withdraw(double amount) {
        synchronized (lock) {
            if (balance >= amount) {
                balance -= amount;
            }
        }
    }

    public double getBalance() {
        // Read-only can be synchronized too if needed
        synchronized (lock) {
            return balance;
        }
    }
}

// Synchronized on this
public synchronized void method1() {
    // Equivalent to
    // synchronized (this) { ... }
}

public void method2() {
    synchronized (this) {
        // Code here
    }
}

// Synchronized on class object (static methods)
class StaticSync {
    private static int count = 0;

    public static synchronized void increment() {
        count++;
    }

    // Equivalent to
    public static void increment2() {
        synchronized (StaticSync.class) {
            count++;
        }
    }
}
```

### Wait and Notify

```java
class ProducerConsumer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int maxSize = 10;
    private final Object lock = new Object();

    public void produce(int item) throws InterruptedException {
        synchronized (lock) {
            while (queue.size() == maxSize) {
                lock.wait();  // Release lock and wait
            }
            queue.add(item);
            System.out.println("Produced: " + item);
            lock.notifyAll();  // Wake up all waiting threads
        }
    }

    public int consume() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                lock.wait();  // Release lock and wait
            }
            int item = queue.remove();
            System.out.println("Consumed: " + item);
            lock.notifyAll();  // Wake up all waiting threads
            return item;
        }
    }
}

// Usage example
class Producer implements Runnable {
    private final ProducerConsumer pc;
    public Producer(ProducerConsumer pc) { this.pc = pc; }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 20; i++) {
                pc.produce(i);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final ProducerConsumer pc;
    public Consumer(ProducerConsumer pc) { this.pc = pc; }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 20; i++) {
                pc.consume();
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Important: Always use while loops with wait(), not if
// Spurious wakeups are possible
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }
    // Process
}
```

### Volatile

```java
// Volatile ensures visibility across threads
class VolatileExample {
    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    public void run() {
        while (running) {
            // Do work
        }
    }
}

// Use cases for volatile:
// 1. Simple flags
volatile boolean shutdownRequested = false;

// 2. Published immutable objects
volatile ImmutableObject config = new ImmutableObject();

// 3. happens-before guarantee

// Volatile does NOT provide atomicity for compound actions
class VolatileCounter {
    private volatile int count = 0;  // NOT thread-safe!

    // This is NOT atomic
    public void increment() {
        count++;  // Read, modify, write - not atomic
    }
}

// For compound operations, use synchronized or atomic classes
```

---

## Lock Interface

### ReentrantLock

```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();  // Always unlock in finally
        }
    }

    // Try lock with timeout
    public boolean tryIncrement() {
        if (lock.tryLock()) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    // Try lock with timeout
    public boolean tryIncrement(long timeout, TimeUnit unit) throws InterruptedException {
        if (lock.tryLock(timeout, unit)) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    // Interruptible lock
    public void interruptibleIncrement() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    // Check if lock is held
    public boolean isLocked() {
        return lock.isLocked();
    }

    // Check if current thread holds lock
    public boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }

    // Get queue length
    public int getQueueLength() {
        return lock.getQueueLength();
    }
}

// Fair lock (threads acquire in FIFO order)
ReentrantLock fairLock = new ReentrantLock(true);

// Unfair lock (default, can be faster)
ReentrantLock unfairLock = new ReentrantLock(false);
```

### ReentrantReadWriteLock

```java
class ReadWriteLockExample {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

    private Map<String, String> data = new HashMap<>();

    public String get(String key) {
        readLock.lock();
        try {
            return data.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(String key, String value) {
        writeLock.lock();
        try {
            data.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    // Multiple readers can read simultaneously
    public void readAll() {
        readLock.lock();
        try {
            data.forEach((k, v) -> System.out.println(k + ": " + v));
        } finally {
            readLock.unlock();
        }
    }

    // Only one writer can write
    public void clear() {
        writeLock.lock();
        try {
            data.clear();
        } finally {
            writeLock.unlock();
        }
    }
}

// Downgrading from write lock to read lock
public void downgrade() {
    writeLock.lock();
    try {
        // Write operation
        data.put("key", "value");
        // Downgrade
        readLock.lock();
    } finally {
        writeLock.unlock();
    }
    try {
        // Read operation
        System.out.println(data.get("key"));
    } finally {
        readLock.unlock();
    }
}
```

### StampedLock

```java
class StampedLockExample {
    private final StampedLock lock = new StampedLock();
    private double x, y;

    public void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();
        double currentX = x, currentY = y;
        if (!lock.validate(stamp)) {
            // Fall back to read lock
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    // Read lock can be upgraded to write lock
    public void moveIfAtOrigin(double newX, double newY) {
        long stamp = lock.readLock();
        try {
            while (x == 0 && y == 0) {
                long ws = lock.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }
}
```

### Condition Variables

```java
class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();  // Wait for space
            }
            queue.add(item);
            notEmpty.signal();  // Signal consumers
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();  // Wait for items
            }
            T item = queue.remove();
            notFull.signal();  // Signal producers
            return item;
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(T item, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.size() == capacity) {
                if (nanos <= 0) return false;
                nanos = notFull.awaitNanos(nanos);
            }
            queue.add(item);
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Concurrent Collections

### ConcurrentHashMap

```java
// Thread-safe HashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Basic operations (thread-safe)
map.put("one", 1);
Integer value = map.get("one");

// Atomic operations
map.putIfAbsent("two", 2);           // Only if absent
map.replace("one", 1, 10);            // Replace if value matches
map.remove("two", 2);                 // Remove if value matches

// Compute operations (atomic)
map.compute("key", (k, v) -> v == null ? 1 : v + 1);
map.computeIfAbsent("key", k -> 1);
map.computeIfPresent("key", (k, v) -> v + 1);

// Merge (atomic)
map.merge("key", 1, Integer::sum);

// forEach (parallel)
map.forEach(2, (k, v) -> System.out.println(k + "=" + v));

// Search
Map.Entry<String, Integer> entry = map.searchEntries(1, e -> e.getValue() > 5 ? e : null);

// Reduce
Integer sum = map.reduceValues(1, Integer::sum);

// Bulk operations
Map<String, Integer> other = new HashMap<>();
map.putAll(other);

ConcurrentHashMap.KeySetView<String, Integer> keys = map.keySet();
ConcurrentHashMap.Values<Integer> values = map.values();
ConcurrentHashMap.EntrySet<String, Integer> entries = map.entrySet();
```

### Concurrent Linked Queues

```java
// Non-blocking concurrent queue
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

queue.offer("A");  // Add (returns false if failure)
queue.offer("B");
queue.offer("C");

String peek = queue.peek();     // Look at head without removing
String poll = queue.poll();     // Remove and return head
boolean removed = queue.remove("B");

// ConcurrentLinkedDeque (double-ended)
ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<>();

deque.addFirst("A");
deque.addLast("Z");
String first = deque.peekFirst();
String last = deque.peekLast();
```

### Blocking Queues

```java
// ArrayBlockingQueue (bounded)
BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(10);
arrayQueue.put("A");  // Blocks if full
String element = arrayQueue.take();  // Blocks if empty

// LinkedBlockingQueue (optionally bounded)
BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(100);
BlockingQueue<String> unbounded = new LinkedBlockingQueue<>();

// PriorityBlockingQueue
BlockingQueue<Integer> priorityQueue = new PriorityBlockingQueue<>();
priorityQueue.put(5);
priorityQueue.put(2);
priorityQueue.put(8);
Integer smallest = priorityQueue.take();  // 2

// DelayQueue
class DelayedTask implements Delayed {
    private final String name;
    private final long startTime;

    DelayedTask(String name, long delayMs) {
        this.name = name;
        this.startTime = System.currentTimeMillis() + delayMs;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.startTime, ((DelayedTask) other).startTime);
    }
}

BlockingQueue<DelayedTask> delayQueue = new DelayQueue<>();
delayQueue.put(new DelayedTask("Task1", 1000));
delayQueue.put(new DelayedTask("Task2", 500));

DelayedTask task = delayQueue.take();  // Blocks until delay expires

// Producer-Consumer pattern
class Producer implements Runnable {
    private final BlockingQueue<String> queue;

    Producer(BlockingQueue<String> queue) { this.queue = queue; }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                queue.put("Item " + i);
            }
            queue.put("POISON_PILL");  // Signal to stop
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final BlockingQueue<String> queue;

    Consumer(BlockingQueue<String> queue) { this.queue = queue; }

    @Override
    public void run() {
        try {
            while (true) {
                String item = queue.take();
                if (item.equals("POISON_PILL")) {
                    break;
                }
                process(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(String item) {
        System.out.println("Processing: " + item);
    }
}
```

### ConcurrentSkipListMap and ConcurrentSkipListSet

```java
// ConcurrentSkipListMap (sorted concurrent map)
ConcurrentSkipListMap<String, Integer> sortedMap = new ConcurrentSkipListMap<>();
sortedMap.put("C", 3);
sortedMap.put("A", 1);
sortedMap.put("B", 2);

// Iteration follows natural ordering
sortedMap.forEach((k, v) -> System.out.println(k + ": " + v));  // A:1, B:2, C:3

// Range operations
ConcurrentNavigableMap<String, Integer> subMap = sortedMap.subMap("A", "C");
String firstKey = sortedMap.firstKey();
String lastKey = sortedMap.lastKey();

// ConcurrentSkipListSet (sorted concurrent set)
ConcurrentSkipListSet<String> sortedSet = new ConcurrentSkipListSet<>();
sortedSet.add("C");
sortedSet.add("A");
sortedSet.add("B");

// Iteration follows natural ordering
sortedSet.forEach(System.out::println);  // A, B, C

// Range operations
NavigableSet<String> subset = sortedSet.subSet("A", "C");
String first = sortedSet.first();
String last = sortedSet.last();
```

---

## Atomic Classes

### Basic Atomic Classes

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// AtomicInteger
AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();  // Returns new value
counter.getAndIncrement();  // Returns old value
counter.decrementAndGet();  // Returns new value
counter.getAndDecrement();  // Returns old value

counter.addAndGet(5);      // Add and return new value
counter.getAndAdd(5);       // Add and return old value

int value = counter.get();
counter.set(10);
boolean wasSet = counter.compareAndSet(10, 20);  // Atomically set if expected

// AtomicLong
AtomicLong longCounter = new AtomicLong(0);
longCounter.incrementAndGet();
longCounter.getAndAdd(100L);

// AtomicBoolean
AtomicBoolean flag = new AtomicBoolean(false);
flag.set(true);
boolean wasSet = flag.getAndSet(false);  // Get and set atomically
boolean wasSwapped = flag.compareAndSet(false, true);

// AtomicReference
AtomicReference<String> ref = new AtomicReference<>("Initial");
ref.set("New");
String old = ref.getAndSet("Another");
boolean wasSet = ref.compareAndSet("Another", "Final");

// Custom object
class User {
    private final String name;
    private final int age;

    User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

AtomicReference<User> userRef = new AtomicReference<>(new User("John", 30));
User oldUser = userRef.getAndSet(new User("Jane", 25));
```

### Atomic Array Classes

```java
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

// AtomicIntegerArray
AtomicIntegerArray array = new AtomicIntegerArray(10);
array.set(0, 100);
array.addAndGet(0, 50);  // array[0] += 50
int value = array.get(0);
boolean wasSet = array.compareAndSet(0, 150, 200);

// AtomicReferenceArray
AtomicReferenceArray<String> strArray = new AtomicReferenceArray<>(5);
strArray.set(0, "Hello");
String old = strArray.getAndSet(0, "World");
```

### Atomic Field Updaters

```java
class MyClass {
    volatile int counter;
    volatile String name;
}

class FieldUpdaterDemo {
    public static void main(String[] args) {
        // Create updaters
        AtomicIntegerFieldUpdater<MyClass> counterUpdater =
            AtomicIntegerFieldUpdater.newUpdater(MyClass.class, "counter");

        AtomicReferenceFieldUpdater<MyClass, String> nameUpdater =
            AtomicReferenceFieldUpdater.newUpdater(MyClass.class, String.class, "name");

        MyClass obj = new MyClass();

        // Atomic operations
        counterUpdater.incrementAndGet(obj);
        nameUpdater.compareAndSet(obj, null, "John");

        System.out.println("Counter: " + counterUpdater.get(obj));
        System.out.println("Name: " + nameUpdater.get(obj));
    }
}
```

### AtomicStampedReference

```java
// Solve ABA problem
AtomicStampedReference<String> ref = new AtomicStampedReference<>("Initial", 0);

int[] stampHolder = new int[1];
String value = ref.get(stampHolder);  // value and stamp

boolean wasSet = ref.compareAndSet(
    "Initial",  // Expected value
    "New",      // New value
    0,          // Expected stamp
    1           // New stamp
);

// Update with new stamp
ref.set("Another", 2);
int stamp = ref.getStamp();

// Attempt update with wrong stamp
boolean failed = ref.compareAndSet("Another", "Final", 1, 3);  // false
```

### LongAdder and DoubleAdder

```java
// For high contention scenarios
LongAdder adder = new LongAdder();

adder.add(5);
adder.increment();

// Sum when needed
long sum = adder.sum();

// Reset
adder.reset();

// For statistics
LongAdder counter = new LongAdder();
counter.increment();
counter.increment();

long count = counter.sum();  // Efficient for many threads

// DoubleAdder for double values
DoubleAdder doubleAdder = new DoubleAdder();
doubleAdder.add(3.14);
doubleAdder.add(2.86);

double total = doubleAdder.sum();
```

---

## Executor Framework

### Thread Pools

```java
import java.util.concurrent.*;

// Fixed thread pool
ExecutorService fixedPool = Executors.newFixedThreadPool(4);

// Cached thread pool (creates threads as needed)
ExecutorService cachedPool = Executors.newCachedThreadPool();

// Single thread executor
ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

// Scheduled thread pool
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

// Work stealing pool (Java 8+)
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// Manual thread pool creation
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    4,                      // Core pool size
    10,                     // Maximum pool size
    60L,                    // Keep alive time
    TimeUnit.SECONDS,      // Time unit
    new LinkedBlockingQueue<>(100),  // Work queue
    Executors.defaultThreadFactory(), // Thread factory
    new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy
);

// Submit tasks
executor.submit(() -> System.out.println("Task 1"));
executor.submit(() -> System.out.println("Task 2"));

// Submit Callable
Future<String> future = executor.submit(() -> "Result");
try {
    String result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Submit multiple tasks
List<Callable<String>> tasks = List.of(
    () -> "Task 1",
    () -> "Task 2",
    () -> "Task 3"
);

List<Future<String>> futures = executor.invokeAll(tasks);

// Shutdown executor
executor.shutdown();  // Graceful shutdown

try {
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();  // Force shutdown
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}

// Immediate shutdown
List<Runnable> pending = executor.shutdownNow();
```

### Rejection Policies

```java
// Rejection policies when queue is full and pool is full

// AbortPolicy (default) - throws RejectedExecutionException
ThreadPoolExecutor abortPolicy = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1),
    new ThreadPoolExecutor.AbortPolicy()
);

// CallerRunsPolicy - runs task in calling thread
ThreadPoolExecutor callerRunsPolicy = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1),
    new ThreadPoolExecutor.CallerRunsPolicy()
);

// DiscardPolicy - silently discards task
ThreadPoolExecutor discardPolicy = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1),
    new ThreadPoolExecutor.DiscardPolicy()
);

// DiscardOldestPolicy - discards oldest task in queue
ThreadPoolExecutor discardOldestPolicy = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1),
    new ThreadPoolExecutor.DiscardOldestPolicy()
);

// Custom rejection handler
ThreadPoolExecutor customPolicy = new ThreadPoolExecutor(
    1, 1, 0, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1),
    new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("Task rejected: " + r);
            // Custom handling: log, retry, queue elsewhere, etc.
        }
    }
);
```

### Scheduled Executor

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// Schedule one-time task
ScheduledFuture<?> future = scheduler.schedule(
    () -> System.out.println("One-time task"),
    5, TimeUnit.SECONDS
);

// Schedule fixed-rate task (starts at fixed intervals)
ScheduledFuture<?> fixedRate = scheduler.scheduleAtFixedRate(
    () -> System.out.println("Fixed rate: " + System.currentTimeMillis()),
    0, 1, TimeUnit.SECONDS
);

// Schedule fixed-delay task (starts after previous completes)
ScheduledFuture<?> fixedDelay = scheduler.scheduleWithFixedDelay(
    () -> {
        System.out.println("Fixed delay: " + System.currentTimeMillis());
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    },
    0, 1, TimeUnit.SECONDS
);

// Cancel scheduled task
fixedRate.cancel(false);  // false = don't interrupt if running

// Shutdown scheduler
scheduler.shutdown();
```

### ForkJoinPool

```java
// ForkJoinPool for recursive tasks
ForkJoinPool forkJoinPool = new ForkJoinPool();

// Recursive task that returns a result
class SumTask extends RecursiveTask<Long> {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 10_000;

    SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            int mid = (start + end) >>> 1;
            SumTask left = new SumTask(array, start, mid);
            SumTask right = new SumTask(array, mid, end);
            left.fork();  // Execute left task asynchronously
            long rightResult = right.compute();  // Compute right task
            long leftResult = left.join();  // Wait for left task
            return leftResult + rightResult;
        }
    }
}

int[] array = new int[100_000];
// Fill array...
SumTask task = new SumTask(array, 0, array.length);
long sum = forkJoinPool.invoke(task);

// Recursive action (no result)
class PrintTask extends RecursiveAction {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 10_000;

    PrintTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            for (int i = start; i < end; i++) {
                System.out.println(array[i]);
            }
        } else {
            int mid = (start + end) >>> 1;
            invokeAll(
                new PrintTask(array, start, mid),
                new PrintTask(array, mid, end)
            );
        }
    }
}

PrintTask printTask = new PrintTask(array, 0, array.length);
forkJoinPool.invoke(printTask);

// Common ForkJoinPool
ForkJoinPool commonPool = ForkJoinPool.commonPool();
int parallelism = commonPool.getParallelism();  // Usually number of CPUs
```

---

## CompletableFuture

### Basic Operations

```java
import java.util.concurrent.CompletableFuture;

// Create and complete future
CompletableFuture<String> future = new CompletableFuture<>();
future.complete("Result");  // Manually complete

// Create from supplier
CompletableFuture<String> supplyFuture = CompletableFuture.supplyAsync(
    () -> {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        return "Async result";
    }
);

// Run without return value
CompletableFuture<Void> runFuture = CompletableFuture.runAsync(
    () -> System.out.println("Running async")
);

// Get result
String result = supplyFuture.get();  // Blocking
String resultWithTimeout = supplyFuture.get(2, TimeUnit.SECONDS);

// Get result with default
String orElse = supplyFuture.getNow("Default if not complete");

// Join (unchecked exception)
String joined = supplyFuture.join();

// Complete exceptionally
CompletableFuture<String> failedFuture = new CompletableFuture<>();
failedFuture.completeExceptionally(new RuntimeException("Error"));

// Handle exception
CompletableFuture<String> handled = failedFuture.exceptionally(ex -> {
    System.out.println("Exception: " + ex.getMessage());
    return "Fallback value";
});

// Create already completed futures
CompletableFuture<String> completed = CompletableFuture.completedFuture("Done");
CompletableFuture<String> failed = CompletableFuture.failedFuture(new RuntimeException("Failed"));
```

### Chaining and Composition

```java
// thenApply - transform value
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<Integer> length = future.thenApply(String::length);

// thenAccept - consume value (no return)
CompletableFuture<Void> print = future.thenAccept(s -> System.out.println(s));

// thenRun - execute action (no value)
CompletableFuture<Void> action = future.thenRun(() -> System.out.println("Done"));

// thenApplyAsync - async transform
CompletableFuture<Integer> asyncLength = future.thenApplyAsync(String::length);

// thenCompose - flatMap (async transformation)
CompletableFuture<String> composed = future.thenCompose(s ->
    CompletableFuture.supplyAsync(() -> s + " World")
);

// thenCombine - combine two futures
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World");
CompletableFuture<String> combined = f1.thenCombine(f2, (s1, s2) -> s1 + " " + s2);

// thenAcceptBoth - consume two futures
CompletableFuture<Void> consumeBoth = f1.thenAcceptBoth(f2, (s1, s2) ->
    System.out.println(s1 + " " + s2)
);

// allOf - wait for all futures to complete
CompletableFuture<Void> allOf = CompletableFuture.allOf(f1, f2);
allOf.thenRun(() -> System.out.println("All complete"));

// anyOf - wait for any future to complete
CompletableFuture<Object> anyOf = CompletableFuture.anyOf(f1, f2);
anyOf.thenAccept(result -> System.out.println("First complete: " + result));
```

### Error Handling

```java
// exceptionally - handle exception
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("Error");
    return "Success";
});

CompletableFuture<String> recovered = future.exceptionally(ex -> {
    return "Recovered from: " + ex.getMessage();
});

// handle - handle both success and failure
CompletableFuture<String> handled = future.handle((result, ex) -> {
    if (ex != null) {
        return "Error: " + ex.getMessage();
    }
    return "Success: " + result;
});

// whenComplete - side effect after completion
CompletableFuture<String> logged = future.whenComplete((result, ex) -> {
    if (ex != null) {
        System.err.println("Exception occurred");
    } else {
        System.out.println("Completed: " + result);
    }
});

// Recovery chains
CompletableFuture<String> chain = CompletableFuture.supplyAsync(() -> {
        throw new RuntimeException("Error");
    })
    .exceptionally(ex -> {
        System.out.println("First recovery");
        throw new RuntimeException("Another error");
    })
    .exceptionally(ex -> {
        System.out.println("Second recovery");
        return "Final recovery";
    });
```

### Multiple Futures

```java
// Wait for multiple futures
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C");

// Collect all results
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
all.thenRun(() -> {
    try {
        System.out.println(f1.get() + f2.get() + f3.get());
    } catch (Exception e) {
        e.printStackTrace();
    }
});

// Wait for any future
CompletableFuture<Object> any = CompletableFuture.anyOf(f1, f2, f3);
any.thenAccept(result -> System.out.println("First: " + result));

// Wait for first successful future
CompletableFuture<String> firstSuccessful = f1
    .applyToEither(f2, r -> r)
    .applyToEither(f3, r -> r);
firstSuccessful.thenAccept(r -> System.out.println("First success: " + r));

// Combine multiple futures
List<CompletableFuture<String>> futures = List.of(f1, f2, f3);
CompletableFuture<Void> allFutures = CompletableFuture.allOf(
    futures.toArray(new CompletableFuture[0])
);

// Convert list of futures to future of list
CompletableFuture<List<String>> listOfResults = allFutures.thenApply(v ->
    futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList())
);
```

### Advanced Patterns

```java
// Timeout pattern
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(5000); } catch (InterruptedException e) {}
    return "Result";
});

CompletableFuture<String> timeout = future.orTimeout(2, TimeUnit.SECONDS);
CompletableFuture<String> withTimeout = future.completeOnTimeout("Timeout", 2, TimeUnit.SECONDS);

// Retry pattern
public static <T> CompletableFuture<T> retry(
    Supplier<CompletableFuture<T>> supplier,
    int maxRetries
) {
    return supplier.get().exceptionallyCompose(ex -> {
        if (maxRetries > 0) {
            return retry(supplier, maxRetries - 1);
        }
        return CompletableFuture.failedFuture(ex);
    });
}

// Circuit breaker pattern
class CircuitBreaker {
    private int failureCount = 0;
    private final int threshold;
    private final long timeoutMillis;
    private long lastFailureTime = 0;

    CircuitBreaker(int threshold, long timeoutMillis) {
        this.threshold = threshold;
        this.timeoutMillis = timeoutMillis;
    }

    public <T> CompletableFuture<T> call(Supplier<CompletableFuture<T>> supplier) {
        if (failureCount >= threshold) {
            if (System.currentTimeMillis() - lastFailureTime < timeoutMillis) {
                return CompletableFuture.failedFuture(new CircuitBreakerOpenException());
            } else {
                failureCount = 0;  // Reset
            }
        }

        return supplier.get().exceptionally(ex -> {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            throw new CompletionException(ex);
        });
    }
}

// Caching with CompletableFuture
class AsyncCache {
    private final Map<String, CompletableFuture<String>> cache = new ConcurrentHashMap<>();

    public CompletableFuture<String> get(String key, Function<String, String> loader) {
        return cache.computeIfAbsent(key, k ->
            CompletableFuture.supplyAsync(() -> loader.apply(k))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        cache.remove(k);  // Remove on failure
                    }
                })
        );
    }
}

// Parallel execution
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(1000); } catch (InterruptedException e) {}
    return "A";
});

CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(500); } catch (InterruptedException e) {}
    return "B";
});

CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(200); } catch (InterruptedException e) {}
    return "C";
});

// All complete in parallel time (about 1 second)
CompletableFuture<List<String>> all = CompletableFuture.allOf(f1, f2, f3)
    .thenApply(v -> List.of(f1.join(), f2.join(), f3.join()));
```

---

## Advanced Patterns

### Producer-Consumer

```java
class ProducerConsumerPattern {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private volatile boolean running = true;

    public void start() {
        // Producer
        executor.submit(() -> {
            int i = 0;
            while (running) {
                try {
                    String item = "Item-" + i++;
                    queue.put(item);
                    System.out.println("Produced: " + item);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // Multiple consumers
        for (int j = 0; j < 3; j++) {
            executor.submit(() -> {
                while (running || !queue.isEmpty()) {
                    try {
                        String item = queue.poll(1, TimeUnit.SECONDS);
                        if (item != null) {
                            System.out.println(Thread.currentThread().getName() + " Consumed: " + item);
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void stop() {
        running = false;
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### Future Chain

```java
// Pipeline of async operations
CompletableFuture<String> pipeline = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApplyAsync(s -> s + " World")
    .thenApplyAsync(String::toUpperCase)
    .thenAcceptAsync(System.out::println);

// Wait for completion
pipeline.join();

// Parallel dependent operations
CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> getUser());
CompletableFuture<String> orderFuture = userFuture.thenComposeAsync(user ->
    CompletableFuture.supplyAsync(() -> getOrders(user))
);
CompletableFuture<String> shippingFuture = orderFuture.thenComposeAsync(orders ->
    CompletableFuture.supplyAsync(() -> getShippingInfo(orders))
);

shippingFuture.thenAccept(shipping -> System.out.println("Shipping: " + shipping));
```

### Thread-Local

```java
// ThreadLocal for thread-specific data
class ThreadLocalExample {
    private static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    private static final ThreadLocal<User> currentUser =
        ThreadLocal.withInitial(() -> User.ANONYMOUS);

    public void process() {
        // Each thread has its own SimpleDateFormat
        SimpleDateFormat df = dateFormat.get();
        String date = df.format(new Date());

        // Set user for current thread
        currentUser.set(new User("Alice"));
        User user = currentUser.get();

        // Clear thread-local when done
        currentUser.remove();
    }
}

// InheritableThreadLocal for child threads
class InheritableThreadLocalExample {
    private static final InheritableThreadLocal<String> context =
        new InheritableThreadLocal<>();

    public static void main(String[] args) {
        context.set("Parent context");

        Thread child = new Thread(() -> {
            System.out.println("Child context: " + context.get());
        });

        child.start();
        child.join();

        context.remove();
    }
}
```

### Semaphore

```java
import java.util.concurrent.Semaphore;

// Limit concurrent access
class ResourcePool {
    private final Semaphore semaphore;
    private final List<Resource> resources;

    public ResourcePool(int size) {
        this.semaphore = new Semaphore(size);
        this.resources = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            resources.add(new Resource());
        }
    }

    public Resource acquire() throws InterruptedException {
        semaphore.acquire();
        // Find available resource
        synchronized (resources) {
            for (Resource resource : resources) {
                if (!resource.isInUse()) {
                    resource.setInUse(true);
                    return resource;
                }
            }
        }
        throw new IllegalStateException("No resource available");
    }

    public void release(Resource resource) {
        resource.setInUse(false);
        semaphore.release();
    }
}

// Counting semaphore
class ConnectionPool {
    private final Semaphore semaphore;
    private final List<Connection> connections;

    public ConnectionPool(int maxConnections) {
        this.semaphore = new Semaphore(maxConnections);
        this.connections = createConnections(maxConnections);
    }

    public Connection getConnection() throws InterruptedException {
        semaphore.acquire();
        Connection conn = getAvailableConnection();
        return new ConnectionWrapper(conn, this);
    }

    public void releaseConnection(Connection conn) {
        conn.close();
        semaphore.release();
    }
}
```

### CountDownLatch

```java
import java.util.concurrent.CountDownLatch;

// Wait for multiple threads to complete
class LatchExample {
    public static void main(String[] args) throws InterruptedException {
        int workerCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(workerCount);

        // Create workers
        for (int i = 0; i < workerCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    startLatch.await();  // Wait for start signal
                    System.out.println("Worker " + id + " started");
                    Thread.sleep(1000);
                    System.out.println("Worker " + id + " finished");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();  // Signal completion
                }
            }).start();
        }

        // Start all workers at once
        System.out.println("Starting workers...");
        startLatch.countDown();

        // Wait for all workers to finish
        endLatch.await();
        System.out.println("All workers finished");
    }
}
```

### CyclicBarrier

```java
import java.util.concurrent.CyclicBarrier;

// Wait for multiple threads to reach a barrier
class BarrierExample {
    public static void main(String[] args) {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println("All parties reached barrier!");
        });

        for (int i = 0; i < parties; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + id + " working...");
                    Thread.sleep(1000);
                    System.out.println("Thread " + id + " waiting at barrier");
                    barrier.await();
                    System.out.println("Thread " + id + " passed barrier");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}

// Barrier with timeout
try {
    barrier.await(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.out.println("Barrier timed out");
}
```

### Phaser

```java
import java.util.concurrent.Phaser;

// More flexible than CountDownLatch/CyclicBarrier
class PhaserExample {
    public static void main(String[] args) {
        int parties = 3;
        Phaser phaser = new Phaser(parties);

        for (int i = 0; i < parties; i++) {
            final int id = i;
            new Thread(() -> {
                for (int phase = 0; phase < 3; phase++) {
                    System.out.println("Thread " + id + " phase " + phase + " working");
                    sleep(1000);
                    System.out.println("Thread " + id + " phase " + phase + " arrived");
                    phaser.arriveAndAwaitAdvance();
                }
                phaser.arriveAndDeregister();
            }).start();
        }
    }

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) {}
    }
}

// Dynamic registration
class DynamicPhaser {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);  // Register main thread

        for (int i = 0; i < 5; i++) {
            phaser.register();  // Register new party
            new Thread(() -> {
                System.out.println("Worker working...");
                sleep(1000);
                System.out.println("Worker arrived");
                phaser.arriveAndAwaitAdvance();
                System.out.println("Worker continuing...");
            }).start();
        }

        System.out.println("Main waiting for workers");
        phaser.arriveAndAwaitAdvance();
        System.out.println("All workers arrived");
    }

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) {}
    }
}
```

### Exchanger

```java
import java.util.concurrent.Exchanger;

// Exchange data between two threads
class ExchangerExample {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        // Thread 1
        new Thread(() -> {
            String data = "Data from thread 1";
            try {
                System.out.println("Thread 1 sending: " + data);
                String received = exchanger.exchange(data);
                System.out.println("Thread 1 received: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Thread 2
        new Thread(() -> {
            String data = "Data from thread 2";
            try {
                System.out.println("Thread 2 sending: " + data);
                String received = exchanger.exchange(data);
                System.out.println("Thread 2 received: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
```

---

## Best Practices

### Thread Pool Configuration

```java
// CPU-bound tasks: pool size = number of cores
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuPool = Executors.newFixedThreadPool(cores);

// I/O-bound tasks: larger pool size
ExecutorService ioPool = Executors.newFixedThreadPool(cores * 2);

// Mixed workload: work stealing pool
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// Custom configuration based on monitoring
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    cores,           // Core size
    cores * 2,       // Max size
    60L,             // Keep alive
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100),
    new ThreadPoolExecutor.CallerRunsPolicy()
);

// Monitor and adjust
executor.setCorePoolSize(8);
executor.setMaximumPoolSize(16);
executor.setKeepAliveTime(120, TimeUnit.SECONDS);
```

### Exception Handling

```java
// Handle exceptions in CompletableFuture
CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Error");
})
.exceptionally(ex -> {
    System.err.println("Exception: " + ex.getMessage());
    return "Fallback";
});

// Handle exceptions in ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> {
    try {
        doWork();
    } catch (Exception e) {
        log.error("Error in task", e);
        // Handle exception
    }
});

// Uncaught exception handler
Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
    System.err.println("Uncaught exception in " + thread.getName());
    ex.printStackTrace();
});
```

### Avoid Common Pitfalls

```java
// DON'T: Call run() instead of start()
Thread thread = new Thread(() -> System.out.println("Hello"));
// thread.run();  // Wrong - runs in current thread
thread.start();   // Correct - runs in new thread

// DON'T: Forget to unlock
lock.lock();
try {
    // Code
} finally {
    lock.unlock();  // Always unlock
}

// DON'T: Use synchronized on mutable objects
synchronized (list) {  // Bad if list reference changes
    list.add(item);
}

// DO: Use final lock object
private final Object lock = new Object();
synchronized (lock) {
    // Safe
}

// DON'T: Interrupt threads abruptly
thread.interrupt();  // Better than thread.stop()

// DON'T: Use deprecated methods
// thread.stop();     // Deprecated - unsafe
// thread.suspend();  // Deprecated - deadlock risk
```

### Thread Safety Guidelines

```java
// 1. Share less, share immutable data
final ImmutableConfig config = new ImmutableConfig("value");

// 2. Use thread-safe collections
ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

// 3. Use atomic operations
AtomicInteger counter = new AtomicInteger(0);

// 4. Use proper synchronization
private final Object lock = new Object();
synchronized (lock) {
    // Critical section
}

// 5. Be careful with double-checked locking
private volatile Singleton instance;

public Singleton getInstance() {
    if (instance == null) {
        synchronized (this) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}

// Or use enum (simpler and safer)
enum Singleton {
    INSTANCE;
}

// 6. Use ExecutorService instead of creating threads directly
ExecutorService executor = Executors.newFixedThreadPool(4);
```

---

## Summary

This reference covers Java concurrency fundamentals:

- **Threads and Runnables**: Creating and managing threads
- **Thread Lifecycle**: Understanding thread states and transitions
- **Thread Synchronization**: synchronized blocks, wait/notify
- **Lock Interface**: ReentrantLock, ReadWriteLock, StampedLock
- **Concurrent Collections**: Thread-safe collection implementations
- **Atomic Classes**: Lock-free thread-safe operations
- **Executor Framework**: Thread pools and task execution
- **CompletableFuture**: Asynchronous programming with futures
- **Fork/Join Framework**: Parallel task execution
- **Advanced Patterns**: Producer-consumer, barriers, semaphores
- **Best Practices**: Thread safety guidelines and patterns

Concurrency in Java provides powerful tools for writing efficient multi-threaded applications, but requires careful design to avoid common pitfalls like race conditions, deadlocks, and performance issues.
