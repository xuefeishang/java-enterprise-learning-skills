/**
 * ConcurrentExample.java
 *
 * Demonstrates Java concurrency features:
 * - Thread creation and management
 * - Synchronization
 * - ExecutorService and thread pools
 * - CompletableFuture
 * - Concurrent collections
 * - Atomic classes
 * - Fork/Join framework
 * - Locks and conditions
 */

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class ConcurrentExample {

    public static void main(String[] args) throws Exception {
        demonstrateBasicThreads();
        demonstrateExecutorService();
        demonstrateCompletableFuture();
        demonstrateConcurrentCollections();
        demonstrateAtomicClasses();
        demonstrateSynchronized();
        demonstrateLocks();
        demonstrateForkJoin();
        demonstrateProducerConsumer();
    }

    // Basic thread creation
    private static void demonstrateBasicThreads() {
        System.out.println("=== Basic Threads ===");

        // Extending Thread class
        Thread t1 = new Thread(new MyThread());
        t1.start();

        // Implementing Runnable
        Thread t2 = new Thread(new MyRunnable());
        t2.start();

        // Lambda expression
        Thread t3 = new Thread(() -> {
            System.out.println("Lambda thread running: " + Thread.currentThread().getName());
        });
        t3.start();

        // Sleep to let threads finish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    // ExecutorService demonstration
    private static void demonstrateExecutorService() throws Exception {
        System.out.println("=== ExecutorService ===");

        // Fixed thread pool
        ExecutorService fixedPool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            fixedPool.submit(() -> {
                System.out.println("Task " + taskId + " running in " +
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        fixedPool.shutdown();
        fixedPool.awaitTermination(5, TimeUnit.SECONDS);

        // Cached thread pool
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        cachedPool.submit(() -> System.out.println("Cached pool task"));

        cachedPool.shutdown();
        cachedPool.awaitTermination(5, TimeUnit.SECONDS);

        // Single thread executor
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

        singleExecutor.submit(() -> System.out.println("Single thread task 1"));
        singleExecutor.submit(() -> System.out.println("Single thread task 2"));

        singleExecutor.shutdown();
        singleExecutor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println();
    }

    // CompletableFuture demonstration
    private static void demonstrateCompletableFuture() throws Exception {
        System.out.println("=== CompletableFuture ===");

        // Simple async computation
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Computing asynchronously...");
            sleep(500);
            return "Result";
        });

        // Then apply transformation
        CompletableFuture<String> transformed = future.thenApply(s -> s + " transformed");

        // Then accept (consume result)
        CompletableFuture<Void> consumed = transformed.thenAccept(s ->
            System.out.println("Consumed: " + s)
        );

        // Then run (no value)
        CompletableFuture<Void> after = consumed.thenRun(() ->
            System.out.println("After consumption")
        );

        after.get();  // Wait for completion

        // Multiple futures
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "Hello";
        });

        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "World";
        });

        // Combine two futures
        CompletableFuture<String> combined = f1.thenCombine(f2, (s1, s2) -> s1 + " " + s2);

        System.out.println("Combined: " + combined.get());

        // AllOf - wait for all futures
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> {
                sleep(200);
                System.out.println("Task 1 done");
            }),
            CompletableFuture.runAsync(() -> {
                sleep(300);
                System.out.println("Task 2 done");
            })
        );

        allOf.get();
        System.out.println("All tasks completed");

        // Exception handling
        CompletableFuture<String> failedFuture = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Error occurred");
        });

        String result = failedFuture.exceptionally(ex -> {
            System.err.println("Exception: " + ex.getMessage());
            return "Fallback value";
        }).get();

        System.out.println("Result: " + result);

        System.out.println();
    }

    // Concurrent collections demonstration
    private static void demonstrateConcurrentCollections() throws Exception {
        System.out.println("=== Concurrent Collections ===");

        // ConcurrentHashMap
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // Multiple threads writing to map
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int index = i;
            executor.submit(() -> {
                map.put("Key" + index, index);
                System.out.println(Thread.currentThread().getName() + " put Key" + index);
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Map size: " + map.size());
        System.out.println("Map contents: " + map);

        // ConcurrentLinkedQueue
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int index = i;
            executor.submit(() -> {
                queue.offer("Item" + index);
                System.out.println(Thread.currentThread().getName() + " added Item" + index);
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Queue size: " + queue.size());

        // CopyOnWriteArrayList
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int index = i;
            executor.submit(() -> {
                list.add("Element" + index);
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("List size: " + list.size());

        System.out.println();
    }

    // Atomic classes demonstration
    private static void demonstrateAtomicClasses() throws Exception {
        System.out.println("=== Atomic Classes ===");

        // AtomicInteger
        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::incrementAndGet);
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Counter: " + counter.get());

        // LongAdder for high contention
        LongAdder adder = new LongAdder();

        executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 1000; i++) {
            executor.submit(adder::increment);
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Adder sum: " + adder.sum());

        // AtomicReference
        AtomicReference<String> ref = new AtomicReference<>("Initial");

        String oldValue = ref.getAndSet("New");
        System.out.println("Old value: " + oldValue);
        System.out.println("New value: " + ref.get());

        // Compare and set
        boolean wasSet = ref.compareAndSet("New", "Final");
        System.out.println("Was set: " + wasSet);

        System.out.println();
    }

    // Synchronized demonstration
    private static void demonstrateSynchronized() throws Exception {
        System.out.println("=== Synchronized ===");

        SynchronizedCounter counter = new SynchronizedCounter();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::increment);
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("Counter: " + counter.getValue());

        System.out.println();
    }

    // Locks demonstration
    private static void demonstrateLocks() throws Exception {
        System.out.println("=== Locks ===");

        LockExample lockExample = new LockExample();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> lockExample.task(taskId));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println();
    }

    // Fork/Join demonstration
    private static void demonstrateForkJoin() throws Exception {
        System.out.println("=== Fork/Join Framework ===");

        ForkJoinPool pool = new ForkJoinPool();

        int[] array = new int[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        SumTask task = new SumTask(array, 0, array.length);
        long sum = pool.invoke(task);

        System.out.println("Sum: " + sum);
        System.out.println("Expected: " + (100 * 101 / 2));

        pool.shutdown();

        System.out.println();
    }

    // Producer-Consumer demonstration
    private static void demonstrateProducerConsumer() throws Exception {
        System.out.println("=== Producer-Consumer ===");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Producer
        executor.submit(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    System.out.println("Producing: " + i);
                    queue.put(i);
                    Thread.sleep(100);
                }
                queue.put(-1);  // Poison pill
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Consumers
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            executor.submit(() -> {
                try {
                    while (true) {
                        Integer value = queue.take();
                        if (value == -1) {
                            // Put back poison pill for other consumer
                            queue.put(-1);
                            break;
                        }
                        System.out.println("Consumer " + consumerId + " consuming: " + value);
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println();
    }

    // Helper method
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Thread implementations
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + getName());
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running: " + Thread.currentThread().getName());
    }
}

// Synchronized counter
class SynchronizedCounter {
    private int value = 0;

    public synchronized void increment() {
        value++;
    }

    public synchronized int getValue() {
        return value;
    }
}

// Lock example
class LockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void task(int taskId) {
        lock.lock();
        try {
            System.out.println("Task " + taskId + " started");
            Thread.sleep(500);
            System.out.println("Task " + taskId + " completed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

// Fork/Join task
class SumTask extends RecursiveTask<Long> {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 20;

    public SumTask(int[] array, int start, int end) {
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
            left.fork();
            long rightResult = right.compute();
            long leftResult = left.join();
            return leftResult + rightResult;
        }
    }
}
