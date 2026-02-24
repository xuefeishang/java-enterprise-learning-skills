# Java Collections Framework Reference Guide

## Table of Contents

1. [Collection Hierarchy](#collection-hierarchy)
2. [List Implementations](#list-implementations)
3. [Set Implementations](#set-implementations)
4. [Map Implementations](#map-implementations)
5. [Queue Implementations](#queue-implementations)
6. [Utility Classes](#utility-classes)
7. [Stream API](#stream-api)
8. [Performance Considerations](#performance-considerations)
9. [Best Practices](#best-practices)

---

## Collection Hierarchy

```
Collection (Interface)
├── List (Interface)
│   ├── ArrayList
│   ├── LinkedList
│   ├── Vector (Legacy)
│   └── Stack (Legacy)
├── Set (Interface)
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet
├── Queue (Interface)
│   ├── PriorityQueue
│   ├── ArrayDeque
│   └── Deque (Interface)
│       ├── ArrayDeque
│       └── LinkedList
└── SortedSet (Interface)
    └── TreeSet

Map (Interface - separate hierarchy)
├── HashMap
├── LinkedHashMap
├── TreeMap
├── WeakHashMap
├── ConcurrentHashMap
├── Hashtable (Legacy)
└── SortedMap (Interface)
    └── TreeMap
```

---

## List Implementations

### ArrayList

```java
// Basic usage
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");

// Initial capacity
List<Integer> numbers = new ArrayList<>(100);

// From other collection
List<String> copy = new ArrayList<>(names);

// Access elements
String first = names.get(0);         // "Alice"
String last = names.get(names.size() - 1);  // "Charlie"

// Modify elements
names.set(1, "Robert");  // Replace "Bob" with "Robert"

// Remove elements
names.remove(0);           // Remove first element
names.remove("Charlie");  // Remove by value
names.removeAll(Arrays.asList("Alice", "Bob"));  // Remove multiple

// Contains
boolean hasAlice = names.contains("Alice");
boolean hasAll = names.containsAll(Arrays.asList("Alice", "Bob"));

// Size and empty
int size = names.size();
boolean empty = names.isEmpty();

// Clear
names.clear();

// Convert to array
String[] array = names.toArray(new String[0]);

// Iteration
for (String name : names) {
    System.out.println(name);
}

// With index
for (int i = 0; i < names.size(); i++) {
    System.out.println(i + ": " + names.get(i));
}

// Iterator
Iterator<String> iterator = names.iterator();
while (iterator.hasNext()) {
    String name = iterator.next();
    if (name.length() > 5) {
        iterator.remove();  // Safe removal during iteration
    }
}

// ListIterator (bi-directional)
ListIterator<String> listIterator = names.listIterator();
while (listIterator.hasNext()) {
    System.out.println(listIterator.next());
}
while (listIterator.hasPrevious()) {
    System.out.println(listIterator.previous());
}

// Sublist (view, not copy)
List<String> sub = names.subList(0, 2);  // Elements at indices 0 and 1
sub.set(0, "Modified");  // Also modifies original list

// Sort
names.sort(Comparator.naturalOrder());
names.sort(Comparator.reverseOrder());
names.sort(Comparator.comparingInt(String::length));

// Binary search (requires sorted list)
names.sort(String::compareTo);
int index = Collections.binarySearch(names, "Bob");

// Replace all
names.replaceAll(String::toUpperCase);
names.replaceAll(s -> s.replace("A", "@"));

// Remove if
names.removeIf(s -> s.startsWith("A"));

// Performance characteristics
// - O(1) access by index
// - O(1) insertion at end (amortized)
// - O(n) insertion at arbitrary position (needs shifting)
// - O(1) removal from end
// - O(n) removal from arbitrary position
```

### LinkedList

```java
// Basic usage
Deque<String> deque = new LinkedList<>();
deque.addFirst("First");
deque.addLast("Last");
deque.push("Top");      // Equivalent to addFirst
deque.offer("Offered"); // Equivalent to addLast

// Access
String first = deque.getFirst();
String last = deque.getLast();
String peek = deque.peek();        // Returns null if empty
String peekFirst = deque.peekFirst();
String peekLast = deque.peekLast();

// Remove
String removed = deque.removeFirst();      // Throws exception if empty
String removedLast = deque.removeLast();
String poll = deque.poll();                // Returns null if empty
String pollFirst = deque.pollFirst();
String pollLast = deque.pollLast();

// Stack operations
String pop = deque.pop();      // Removes and returns first element
deque.push("New Top");

// Queue operations
String element = deque.element();  // Throws exception if empty
boolean offered = deque.offer("New");  // Returns false if full (never full)

// Convert to list view
List<String> list = new LinkedList<>();

// Performance characteristics
// - O(n) access by index
// - O(1) insertion at beginning or end
// - O(1) removal from beginning or end
// - O(n) insertion or removal at arbitrary position

// When to use LinkedList over ArrayList
// - Frequent insertions/deletions at beginning
// - Need for queue/stack operations
// - Memory efficiency for many small elements
```

### Vector and Stack (Legacy)

```java
// Vector - thread-safe but generally use CopyOnWriteArrayList instead
Vector<String> vector = new Vector<>();
vector.add("Element");
String element = vector.get(0);

// Stack - extends Vector, LIFO
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
stack.push("C");

String top = stack.peek();   // "C"
String popped = stack.pop(); // "C" removed
boolean empty = stack.empty();

// NOTE: Prefer Deque implementations (ArrayDeque, LinkedList) over Stack
Deque<String> modernStack = new ArrayDeque<>();
modernStack.push("A");
modernStack.push("B");
String top2 = modernStack.pop();
```

---

## Set Implementations

### HashSet

```java
// Basic usage
Set<String> names = new HashSet<>();
names.add("Alice");
names.add("Bob");
names.add("Alice");  // Duplicate, ignored

// Initial capacity and load factor
Set<Integer> numbers = new HashSet<>(100, 0.75f);

// From collection
Set<String> copy = new HashSet<>(names);

// Contains
boolean hasAlice = names.contains("Alice");

// Remove
names.remove("Bob");

// Set operations
Set<String> setA = new HashSet<>(Arrays.asList("A", "B", "C"));
Set<String> setB = new HashSet<>(Arrays.asList("B", "C", "D"));

// Union
Set<String> union = new HashSet<>(setA);
union.addAll(setB);  // [A, B, C, D]

// Intersection
Set<String> intersection = new HashSet<>(setA);
intersection.retainAll(setB);  // [B, C]

// Difference
Set<String> difference = new HashSet<>(setA);
difference.removeAll(setB);  // [A]

// Symmetric difference
Set<String> symmetric = new HashSet<>(setA);
symmetric.addAll(setB);  // Union
Set<String> temp = new HashSet<>(setA);
temp.retainAll(setB);  // Intersection
symmetric.removeAll(temp);  // [A, D]

// Iteration
for (String name : names) {
    System.out.println(name);
}

// Remove if
names.removeIf(s -> s.length() < 3);

// Performance characteristics
// - O(1) add, remove, contains (average case)
// - O(n) in worst case (rare, hash collisions)
// - No ordering guarantee
// - Allows one null element
// - Not thread-safe
```

### LinkedHashSet

```java
// Maintains insertion order
Set<String> orderedNames = new LinkedHashSet<>();
orderedNames.add("Charlie");
orderedNames.add("Alice");
orderedNames.add("Bob");

// Iteration follows insertion order
for (String name : orderedNames) {
    System.out.println(name);  // Charlie, Alice, Bob
}

// Access by insertion position (simulated)
List<String> list = new ArrayList<>(orderedNames);
String second = list.get(1);  // "Alice"

// Performance characteristics
// - O(1) add, remove, contains (average case)
// - Maintains insertion order
// - Slightly slower than HashSet due to linked list maintenance
// - More memory than HashSet
```

### TreeSet

```java
// Sorted set (natural ordering)
Set<String> sortedNames = new TreeSet<>();
sortedNames.add("Charlie");
sortedNames.add("Alice");
sortedNames.add("Bob");

// Iteration follows natural ordering
for (String name : sortedNames) {
    System.out.println(name);  // Alice, Bob, Charlie (alphabetical)
}

// Custom comparator
Set<String> reverseOrder = new TreeSet<>(Comparator.reverseOrder());
reverseOrder.addAll(sortedNames);

// TreeSet-specific operations
TreeSet<Integer> numbers = new TreeSet<>();
numbers.add(5);
numbers.add(2);
numbers.add(8);
numbers.add(1);

Integer first = numbers.first();      // 1
Integer last = numbers.last();        // 8

// Subset view
SortedSet<Integer> subset = numbers.subSet(2, 8);  // [2, 5] (exclusive of 8)
NavigableSet<Integer> head = numbers.headSet(5);  // [1, 2]
NavigableSet<Integer> tail = numbers.tailSet(5);  // [5, 8]

// Navigation methods
Integer lower = numbers.lower(5);   // 2 (strictly less than)
Integer floor = numbers.floor(5);    // 5 (less than or equal)
Integer ceiling = numbers.ceiling(3); // 5 (greater than or equal)
Integer higher = numbers.higher(2);  // 5 (strictly greater)

// Poll methods
Integer pollFirst = numbers.pollFirst();  // Remove and return first
Integer pollLast = numbers.pollLast();    // Remove and return last

// Descending iterator
Iterator<Integer> descending = numbers.descendingIterator();
while (descending.hasNext()) {
    System.out.println(descending.next());  // 8, 5, 2, 1
}

// Performance characteristics
// - O(log n) add, remove, contains
// - Elements are sorted
// - No null elements (for natural ordering)
// - More memory than HashSet
```

---

## Map Implementations

### HashMap

```java
// Basic usage
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);

// Initial capacity and load factor
Map<String, String> config = new HashMap<>(100, 0.75f);

// From another map
Map<String, Integer> copy = new HashMap<>(scores);

// Access values
Integer aliceScore = scores.get("Alice");
Integer unknownScore = scores.get("Unknown");  // null

// getOrDefault
Integer score = scores.getOrDefault("Unknown", 0);

// putIfAbsent (only put if key absent)
scores.putIfAbsent("Alice", 100);  // Not added, Alice exists
scores.putIfAbsent("David", 85);   // Added

// Replace operations
scores.replace("Alice", 96);           // Replace if exists
scores.replace("Bob", 87, 88);        // Replace if value matches
scores.replaceAll((k, v) -> v + 5);   // Add 5 to all scores

// Compute operations
scores.compute("Alice", (k, v) -> v == null ? 0 : v + 5);
scores.computeIfAbsent("Eve", k -> 70);  // Compute and put if absent
scores.computeIfPresent("Bob", (k, v) -> v + 2);  // Compute and put if present

// Merge
scores.merge("Alice", 5, Integer::sum);  // Add 5 to Alice's score

// Contains
boolean hasAlice = scores.containsKey("Alice");
boolean has95 = scores.containsValue(95);

// Remove
scores.remove("Charlie");
scores.remove("Bob", 87);  // Only remove if value matches

// Size and empty
int size = scores.size();
boolean empty = scores.isEmpty();

// Clear
scores.clear();

// Entry set
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    String name = entry.getKey();
    int score = entry.getValue();
    System.out.println(name + ": " + score);
}

// Key set
Set<String> names = scores.keySet();
for (String name : names) {
    System.out.println(name);
}

// Values collection
Collection<Integer> allScores = scores.values();

// For-each on entries
scores.forEach((name, score) -> System.out.println(name + ": " + score));

// Performance characteristics
// - O(1) get, put, containsKey (average case)
// - O(n) in worst case (hash collisions)
// - No ordering guarantee
// - Allows one null key and multiple null values
// - Not thread-safe
```

### LinkedHashMap

```java
// Maintains insertion order
Map<String, Integer> orderedMap = new LinkedHashMap<>();
orderedMap.put("C", 3);
orderedMap.put("A", 1);
orderedMap.put("B", 2);

// Iteration follows insertion order
orderedMap.forEach((k, v) -> System.out.println(k + ": " + v));  // C:3, A:1, B:2

// Access order (LRU cache)
Map<String, Integer> lruMap = new LinkedHashMap<>(16, 0.75f, true);
lruMap.put("A", 1);
lruMap.put("B", 2);
lruMap.put("C", 3);

lruMap.get("A");  // Access A, moves to end
lruMap.get("B");  // Access B, moves to end

// Iteration: C, A, B (access order)
```

### TreeMap

```java
// Sorted map (natural ordering)
Map<String, Integer> sortedMap = new TreeMap<>();
sortedMap.put("Charlie", 3);
sortedMap.put("Alice", 1);
sortedMap.put("Bob", 2);

// Iteration follows natural ordering
sortedMap.forEach((k, v) -> System.out.println(k + ": " + v));  // Alice:1, Bob:2, Charlie:3

// Custom comparator
Map<String, Integer> reverseMap = new TreeMap<>(Comparator.reverseOrder());
reverseMap.putAll(sortedMap);

// TreeMap-specific operations
TreeMap<Integer, String> tree = new TreeMap<>();
tree.put(5, "Five");
tree.put(2, "Two");
tree.put(8, "Eight");
tree.put(1, "One");

Integer firstKey = tree.firstKey();    // 1
Integer lastKey = tree.lastKey();      // 8
Map.Entry<Integer, String> firstEntry = tree.firstEntry();   // 1="One"
Map.Entry<Integer, String> lastEntry = tree.lastEntry();     // 8="Eight"

// Submap views
SortedMap<Integer, String> subMap = tree.subMap(2, 8);  // Keys 2 to 7 (inclusive start, exclusive end)
NavigableMap<Integer, String> headMap = tree.headMap(5);  // Keys less than 5
NavigableMap<Integer, String> tailMap = tree.tailMap(5);  // Keys greater than or equal to 5

// Navigation methods
Integer lowerKey = tree.lowerKey(5);   // 2 (strictly less than)
Integer floorKey = tree.floorKey(5);    // 5 (less than or equal)
Integer ceilingKey = tree.ceilingKey(3); // 5 (greater than or equal)
Integer higherKey = tree.higherKey(2); // 5 (strictly greater)

// Poll methods
Map.Entry<Integer, String> pollFirst = tree.pollFirstEntry();  // Remove and return first
Map.Entry<Integer, String> pollLast = tree.pollLastEntry();    // Remove and return last

// Descending map
NavigableMap<Integer, String> descending = tree.descendingMap();
descending.forEach((k, v) -> System.out.println(k + ": " + v));  // 8:8, 5:5, 2:2, 1:1

// Performance characteristics
// - O(log n) get, put, containsKey
// - Keys are sorted
// - No null keys (for natural ordering)
// - More memory than HashMap
```

### ConcurrentHashMap

```java
// Thread-safe map
ConcurrentMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("Alice", 95);
concurrentMap.put("Bob", 87);

// Thread-safe operations
concurrentMap.putIfAbsent("Alice", 100);  // Only if absent
concurrentMap.replace("Alice", 95, 96);    // Replace if value matches
concurrentMap.replace("Bob", 88);          // Replace if exists

// Atomic operations
concurrentMap.computeIfAbsent("Charlie", k -> 92);
concurrentMap.computeIfPresent("Bob", (k, v) -> v + 5);
concurrentMap.merge("Alice", 5, Integer::sum);

// Remove atomic
concurrentMap.remove("Alice", 95);  // Only if value matches

// forEach with parallelism
concurrentMap.forEach(2, (k, v) -> System.out.println(k + ": " + v));

// search
Map.Entry<String, Integer> entry = concurrentMap.searchEntries(1, e -> e.getValue() > 90 ? e : null);

// reduce
Integer sum = concurrentMap.reduceValues(2, Integer::sum);

// Performance characteristics
// - O(1) get, put (average case), lock-free reads
// - Thread-safe without synchronization
// - Better performance than synchronized HashMap
// - Does not allow null keys or values
```

### WeakHashMap

```java
// Keys are weak references, garbage collected when no strong references
Map<String, Integer> weakMap = new WeakHashMap<>();

String key = new String("WeakKey");
weakMap.put(key, 100);

key = null;  // Remove strong reference
System.gc();  // May trigger garbage collection

// The entry may be removed automatically
System.out.println(weakMap.size());

// Use cases:
// - Caching with automatic cleanup
// - Metadata for objects
// - Canonicalizing maps
```

---

## Queue Implementations

### PriorityQueue

```java
// Min-heap by default (smallest first)
Queue<Integer> minHeap = new PriorityQueue<>();
minHeap.add(5);
minHeap.add(2);
minHeap.add(8);
minHeap.add(1);

System.out.println(minHeap.poll());  // 1 (smallest)
System.out.println(minHeap.poll());  // 2
System.out.println(minHeap.poll());  // 5

// Max-heap
Queue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.add(5);
maxHeap.add(2);
maxHeap.add(8);
maxHeap.add(1);

System.out.println(maxHeap.poll());  // 8 (largest)

// Custom comparator (by string length)
Queue<String> lengthQueue = new PriorityQueue<>(Comparator.comparingInt(String::length));
lengthQueue.add("Hello");
lengthQueue.add("Hi");
lengthQueue.add("Hey there");

System.out.println(lengthQueue.poll());  // "Hi"
System.out.println(lengthQueue.poll());  // "Hello"
System.out.println(lengthQueue.poll());  // "Hey there"

// Priority queue with objects
class Task implements Comparable<Task> {
    String name;
    int priority;

    Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}

Queue<Task> taskQueue = new PriorityQueue<>();
taskQueue.add(new Task("Low priority", 3));
taskQueue.add(new Task("High priority", 1));
taskQueue.add(new Task("Medium priority", 2));

Task next = taskQueue.poll();  // Highest priority (lowest number)
```

### ArrayDeque

```java
// Double-ended queue
Deque<Integer> deque = new ArrayDeque<>();

// Add to ends
deque.addFirst(1);
deque.addLast(2);
deque.push(3);  // Equivalent to addFirst
deque.offer(4);  // Equivalent to addLast

// Access from ends
Integer first = deque.getFirst();   // Throws exception if empty
Integer last = deque.getLast();
Integer peekFirst = deque.peekFirst();  // Returns null if empty
Integer peekLast = deque.peekLast();

// Remove from ends
Integer removedFirst = deque.removeFirst();
Integer removedLast = deque.removeLast();
Integer polledFirst = deque.pollFirst();  // Returns null if empty
Integer polledLast = deque.pollLast();

// Stack operations
deque.push(10);
Integer popped = deque.pop();
Integer peeked = deque.peek();

// Capacity (no capacity constraint)
// ArrayDeque is resizable

// Performance characteristics
// - O(1) add, remove from both ends
// - No capacity limit
// - Faster than LinkedList as a stack
// - Not thread-safe
```

### BlockingQueue Implementations

```java
// ArrayBlockingQueue - bounded, FIFO
BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(10);
try {
    arrayQueue.put("Element");  // Blocks if full
    String element = arrayQueue.take();  // Blocks if empty
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

// LinkedBlockingQueue - optionally bounded
BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(100);
BlockingQueue<String> unbounded = new LinkedBlockingQueue<>();

// PriorityBlockingQueue - unbounded priority queue
BlockingQueue<Integer> priorityQueue = new PriorityBlockingQueue<>();
priorityQueue.put(5);
priorityQueue.put(2);
priorityQueue.put(8);

Integer first = priorityQueue.take();  // 2

// ConcurrentLinkedQueue - non-blocking concurrent queue
Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
concurrentQueue.offer("Element");
String element = concurrentQueue.poll();

// DelayQueue - elements become available after delay
class DelayedTask implements Delayed {
    String name;
    long delay;

    DelayedTask(String name, long delayMs) {
        this.name = name;
        this.delay = System.currentTimeMillis() + delayMs;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.delay, ((DelayedTask) other).delay);
    }
}

BlockingQueue<DelayedTask> delayQueue = new DelayQueue<>();
delayQueue.put(new DelayedTask("Task1", 1000));
delayQueue.put(new DelayedTask("Task2", 500));

DelayedTask task = delayQueue.take();  // Blocks until delay expires
```

---

## Utility Classes

### Collections Class

```java
List<Integer> list = Arrays.asList(5, 2, 8, 1, 9);

// Sorting
Collections.sort(list);  // Natural order
Collections.sort(list, Comparator.reverseOrder());
Collections.sort(list, Comparator.comparingInt(Integer::intValue).reversed());

// Binary search (list must be sorted)
Collections.sort(list);
int index = Collections.binarySearch(list, 5);  // Returns index or (-(insertion point) - 1)

// Shuffling
Collections.shuffle(list);
Collections.shuffle(list, new Random(42));  // With specific seed

// Reversing
Collections.reverse(list);

// Rotating
Collections.rotate(list, 2);  // Rotate right by 2
Collections.rotate(list, -2); // Rotate left by 2

// Swapping
Collections.swap(list, 0, list.size() - 1);

// Filling
Collections.fill(list, 0);  // Fill all elements with 0

// Copy
List<Integer> dest = new ArrayList<>(Arrays.asList(new Integer[list.size()]));
Collections.copy(dest, list);  // Copy from list to dest

// Min and max
Integer min = Collections.min(list);
Integer max = Collections.max(list);
Integer customMin = Collections.min(list, Comparator.comparingInt(i -> i % 10));

// Frequency
int count = Collections.frequency(list, 5);  // Count occurrences of 5

// Disjoint
List<Integer> list2 = Arrays.asList(10, 20, 30);
boolean noCommon = Collections.disjoint(list, list2);  // true if no common elements

// AddAll
Collections.addAll(list, 10, 20, 30);  // Add multiple elements

// Unmodifiable views
List<Integer> unmodifiable = Collections.unmodifiableList(list);
Set<Integer> unmodifiableSet = Collections.unmodifiableSet(new HashSet<>(list));
Map<String, Integer> unmodifiableMap = Collections.unmodifiableMap(new HashMap<>());
// unmodifiable.add(100);  // Throws UnsupportedOperationException

// Synchronized views
List<Integer> syncList = Collections.synchronizedList(list);
Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
// Must synchronize on the object when iterating
synchronized (syncList) {
    for (Integer i : syncList) {
        System.out.println(i);
    }
}

// Empty collections
List<String> emptyList = Collections.emptyList();
Set<String> emptySet = Collections.emptySet();
Map<String, String> emptyMap = Collections.emptyMap();

// Singleton collections
List<String> singletonList = Collections.singletonList("Only");
Set<String> singletonSet = Collections.singleton("Only");

// Checked collections (type-safe at runtime)
List<String> checkedList = Collections.checkedList(new ArrayList<>(), String.class);
// checkedList.add(123);  // Throws ClassCastException

// Enumeration
Enumeration<Integer> enumeration = Collections.enumeration(list);
while (enumeration.hasMoreElements()) {
    System.out.println(enumeration.nextElement());
}
```

### Arrays Class

```java
// Sorting
int[] numbers = {5, 2, 8, 1, 9};
Arrays.sort(numbers);

// Parallel sorting (for large arrays)
int[] largeArray = new int[1_000_000];
Arrays.parallelSort(largeArray);

// Binary search
int index = Arrays.binarySearch(numbers, 5);  // Returns index or (-(insertion point) - 1)

// Equality
int[] array1 = {1, 2, 3};
int[] array2 = {1, 2, 3};
boolean equal = Arrays.equals(array1, array2);  // true

// Deep equality for multi-dimensional arrays
int[][] matrix1 = {{1, 2}, {3, 4}};
int[][] matrix2 = {{1, 2}, {3, 4}};
boolean deepEqual = Arrays.deepEquals(matrix1, matrix2);  // true

// Filling
int[] array = new int[10];
Arrays.fill(array, 0);  // Fill all with 0
Arrays.fill(array, 0, 5, 1);  // Fill indices 0-4 with 1

// Copy
int[] copy = Arrays.copyOf(array, array.length);
int[] longer = Arrays.copyOf(array, array.length * 2);
int[] range = Arrays.copyOfRange(array, 0, 5);  // Copy elements from index 0 to 4

// Converting to list (fixed-size, backed by array)
String[] strings = {"A", "B", "C"};
List<String> list = Arrays.asList(strings);
// list.add("D");  // UnsupportedOperationException - fixed size

// Hash code
int hashCode = Arrays.hashCode(array);
int deepHash = Arrays.deepHashCode(matrix1);

// toString
String string = Arrays.toString(array);  // "[1, 2, 3]"
String deepString = Arrays.deepToString(matrix1);  // "[[1, 2], [3, 4]]"

// Set all
Arrays.setAll(array, i -> i * i);  // array[i] = i * i

// Parallel set all
Arrays.parallelSetAll(largeArray, i -> i);

// Prefix sum (Java 8+)
Arrays.parallelPrefix(array, Integer::sum);
```

---

## Stream API

### Stream Basics

```java
// Creating streams
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// From collection
Stream<Integer> stream = numbers.stream();
Stream<Integer> parallelStream = numbers.parallelStream();

// From values
Stream<String> valueStream = Stream.of("A", "B", "C");

// From array
String[] array = {"A", "B", "C"};
Stream<String> arrayStream = Arrays.stream(array);

// From builder
Stream<String> builderStream = Stream.<String>builder()
    .add("A")
    .add("B")
    .build();

// Generate
Stream<Double> randomStream = Stream.generate(Math::random).limit(10);

// Iterate
Stream<Integer> iterateStream = Stream.iterate(0, n -> n + 1).limit(10);

// Range (int and long)
IntStream intStream = IntStream.range(0, 10);      // 0-9
IntStream rangeClosed = IntStream.rangeClosed(0, 10);  // 0-10

// From strings
"Hello World".chars();  // IntStream of characters

// Basic operations
List<Integer> result = numbers.stream()
    .filter(n -> n % 2 == 0)    // Filter
    .map(n -> n * n)            // Map
    .distinct()                 // Remove duplicates
    .sorted()                   // Sort
    .limit(5)                   // Limit elements
    .skip(1)                    // Skip elements
    .collect(Collectors.toList());

// Terminal operations
// forEach
numbers.stream().forEach(System.out::println);

// collect
List<Integer> list = numbers.stream().collect(Collectors.toList());
Set<Integer> set = numbers.stream().collect(Collectors.toSet());
String joined = numbers.stream().map(Object::toString).collect(Collectors.joining(", "));

// reduce
int sum = numbers.stream().reduce(0, Integer::sum);
Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);

// count
long count = numbers.stream().filter(n -> n > 2).count();

// anyMatch, allMatch, noneMatch
boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
boolean allPositive = numbers.stream().allMatch(n -> n > 0);
boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

// findFirst, findAny
Optional<Integer> first = numbers.stream().findFirst();
Optional<Integer> any = numbers.stream().findAny();

// min, max
Optional<Integer> min = numbers.stream().min(Comparator.naturalOrder());
Optional<Integer> max = numbers.stream().max(Comparator.naturalOrder());

// toArray
Integer[] array = numbers.stream().toArray(Integer[]::new);

// Primitive streams
IntStream intStream = numbers.stream().mapToInt(Integer::intValue);
LongStream longStream = numbers.stream().mapToLong(Integer::longValue);
DoubleStream doubleStream = numbers.stream().mapToDouble(Integer::doubleValue);

// Primitive operations
int sum = intStream.sum();
double average = intStream.average().orElse(0);
OptionalInt min = intStream.min();
OptionalInt max = intStream.max();
long count = intStream.count();
IntSummaryStatistics stats = intStream.summaryStatistics();
```

### Advanced Stream Operations

```java
// Collecting with grouping
List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");

// Group by length
Map<Integer, List<String>> byLength = words.stream()
    .collect(Collectors.groupingBy(String::length));

// Group by with counting
Map<Integer, Long> countByLength = words.stream()
    .collect(Collectors.groupingBy(String::length, Collectors.counting()));

// Group by with mapping
Map<Integer, Set<Character>> firstChars = words.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.mapping(s -> s.charAt(0), Collectors.toSet())
    ));

// Partitioning
Map<Boolean, List<String>> partitioned = words.stream()
    .collect(Collectors.partitioningBy(s -> s.length() > 5));

// Joining
String joined = words.stream()
    .collect(Collectors.joining(", "));

String joinedWithPrefixSuffix = words.stream()
    .collect(Collectors.joining(", ", "[", "]"));

// Reducing
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sum using reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);

// Collecting to specific collection
LinkedList<String> linkedList = words.stream()
    .collect(Collectors.toCollection(LinkedList::new));

TreeSet<String> treeSet = words.stream()
    .collect(Collectors.toCollection(TreeSet::new));

// Collecting to map
Map<String, Integer> wordLengths = words.stream()
    .collect(Collectors.toMap(
        Function.identity(),  // Key mapper
        String::length,       // Value mapper
        (old, newVal) -> old, // Merge function for duplicate keys
        TreeMap::new          // Map factory
    ));

// Collecting to concurrent map
ConcurrentMap<String, Integer> concurrentMap = words.parallelStream()
    .collect(Collectors.toConcurrentMap(
        Function.identity(),
        String::length
    ));

// Flat map
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> flattened = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

// Optional handling
Optional<String> optional = words.stream()
    .filter(s -> s.startsWith("z"))
    .findFirst();

optional.ifPresent(System.out::println);
String result = optional.orElse("Default");
String computed = optional.orElseGet(() -> "Computed default");

// Parallel streams
List<Integer> largeList = IntStream.range(0, 1_000_000)
    .boxed()
    .collect(Collectors.toList());

// Use parallel for CPU-bound operations on large datasets
int sumParallel = largeList.parallelStream()
    .mapToInt(Integer::intValue)
    .sum();
```

### Custom Collectors

```java
// Creating custom collector
Collector<String, ?, Long> countingCollector = Collector.of(
    () -> new long[1],                    // Supplier
    (acc, str) -> acc[0] += str.length(), // Accumulator
    (acc1, acc2) -> {                     // Combiner (for parallel)
        acc1[0] += acc2[0];
        return acc1;
    },
    acc -> acc[0]                          // Finisher
);

long totalLength = words.stream().collect(countingCollector);

// Collecting to immutable collections
List<String> immutableList = words.stream()
    .collect(Collectors.toUnmodifiableList());

Set<String> immutableSet = words.stream()
    .collect(Collectors.toUnmodifiableSet());

Map<String, Integer> immutableMap = words.stream()
    .collect(Collectors.toUnmodifiableMap(
        Function.identity(),
        String::length
    ));

// Teeing collector (Java 12+)
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
String result = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.summingInt(Integer::intValue),
        Collectors.averagingInt(Integer::intValue),
        (sum, avg) -> String.format("Sum: %d, Avg: %.2f", sum, avg)
    ));
```

---

## Performance Considerations

### Time Complexity Comparison

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| Add (end) | O(1)* | O(1) | O(1)* | O(log n) | O(1)* | O(log n) |
| Add (index) | O(n) | O(n) | N/A | N/A | N/A | N/A |
| Remove (index) | O(n) | O(n) | O(1)* | O(log n) | O(1)* | O(log n) |
| Get (index) | O(1) | O(n) | N/A | N/A | N/A | N/A |
| Contains | O(n) | O(n) | O(1)* | O(log n) | O(1)* | O(log n) |
| Find | O(n) | O(n) | N/A | O(log n) | O(1)* | O(log n) |

*Amortized/average case

### Space Complexity

| Collection | Space per Element | Notes |
|------------|-------------------|-------|
| ArrayList | ~12 bytes | Contiguous memory, minimal overhead |
| LinkedList | ~24 bytes | Node overhead (prev, next, data) |
| HashSet | ~20 bytes | Hash table overhead |
| TreeSet | ~24 bytes | Tree node overhead |
| HashMap | ~24 bytes | Entry overhead |
| TreeMap | ~24 bytes | Tree node overhead |

### Choosing the Right Collection

```java
// When to use ArrayList
// - Frequent access by index
// - Iteration in order
// - More reads than writes
List<Integer> frequentAccess = new ArrayList<>();

// When to use LinkedList
// - Frequent insertions/deletions at beginning or middle
// - Need queue/stack operations
Deque<String> queueOperations = new LinkedList<>();

// When to use HashSet
// - Need fast lookup
// - Don't care about order
// - Need unique elements
Set<String> uniqueElements = new HashSet<>();

// When to use LinkedHashSet
// - Need fast lookup
// - Need to maintain insertion order
Set<String> orderedUnique = new LinkedHashSet<>();

// When to use TreeSet
// - Need sorted elements
// - Need range operations
Set<Integer> sortedElements = new TreeSet<>();

// When to use HashMap
// - Need fast key lookup
// - Don't care about order
Map<String, User> userMap = new HashMap<>();

// When to use LinkedHashMap
// - Need fast key lookup
// - Need to maintain insertion/access order
Map<String, User> userCache = new LinkedHashMap<>();

// When to use TreeMap
// - Need sorted keys
// - Need range operations
Map<String, User> sortedUserMap = new TreeMap<>();

// When to use ConcurrentHashMap
// - Need thread-safe map
// - High concurrency
Map<String, User> concurrentMap = new ConcurrentHashMap<>();
```

### Initial Capacity Tuning

```java
// For known size, set initial capacity to avoid resizing
int expectedSize = 1000;

// ArrayList: initial capacity
List<String> list = new ArrayList<>(expectedSize);

// HashMap: capacity and load factor
// Formula: capacity = expectedSize / loadFactor + 1
int capacity = (int) (expectedSize / 0.75) + 1;
Map<String, String> map = new HashMap<>(capacity);

// HashSet: similar to HashMap
Set<String> set = new HashSet<>(capacity);
```

---

## Best Practices

### Collection Declaration

```java
// DO: Use interface types for declarations
List<String> names = new ArrayList<>();  // Good
ArrayList<String> namesList = new ArrayList<>();  // Too specific

Set<Integer> numbers = new HashSet<>();
Map<String, User> users = new HashMap<>();

// Diamond operator (Java 7+)
List<String> names = new ArrayList<>();  // Diamond inferred
```

### Empty vs Null Collections

```java
// DO: Return empty collection instead of null
public List<String> getNames() {
    if (names == null || names.isEmpty()) {
        return Collections.emptyList();  // or Collections.emptySet(), emptyMap()
    }
    return new ArrayList<>(names);
}

// DO: Initialize collections to empty
private List<String> names = new ArrayList<>();

// DON'T: Return null
public List<String> getBadNames() {
    return null;  // Bad - callers must check for null
}
```

### Immutability

```java
// DO: Return unmodifiable views
public List<String> getReadOnlyNames() {
    return Collections.unmodifiableList(new ArrayList<>(names));
}

// DO: Use immutable collections (Java 9+)
List<String> immutable = List.of("A", "B", "C");
Set<String> immutableSet = Set.of("A", "B", "C");
Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2);

// DO: Create defensive copies
private final List<String> items;

public MyClass(List<String> items) {
    this.items = new ArrayList<>(items);  // Defensive copy
}

public List<String> getItems() {
    return new ArrayList<>(items);  // Defensive copy on return
}
```

### Concurrent Modifications

```java
// DON'T: Modify collection during iteration
List<String> names = Arrays.asList("A", "B", "C");
for (String name : names) {
    // names.remove(name);  // ConcurrentModificationException!
}

// DO: Use Iterator.remove()
Iterator<String> iterator = names.iterator();
while (iterator.hasNext()) {
    String name = iterator.next();
    if (shouldRemove(name)) {
        iterator.remove();  // Safe
    }
}

// DO: Use removeIf()
names.removeIf(this::shouldRemove);

// DO: Use concurrent collections for multi-threading
ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
map.forEach((k, v) -> {
    map.compute(k, (key, val) -> val + 1);  // Safe in multi-threaded
});
```

### Stream vs Loop

```java
// Use Stream for:
// - Declarative, readable code
// - Complex transformations
// - Parallel processing (when beneficial)

List<String> result = items.stream()
    .filter(Item::isValid)
    .map(Item::getName)
    .distinct()
    .sorted(Comparator.comparing(String::length))
    .collect(Collectors.toList());

// Use traditional loop for:
// - Simple operations (performance)
// - Need early termination
// - Exception handling in loop

List<String> result = new ArrayList<>();
for (Item item : items) {
    if (item.isValid()) {
        String name = item.getName();
        if (!result.contains(name)) {
            result.add(name);
        }
    }
}
```

### equals() and hashCode()

```java
// When using collections with custom objects:
class Person {
    private String name;
    private int age;

    // MUST override equals() if using contains(), remove()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    // MUST override hashCode() if using HashSet, HashMap, etc.
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

// Or use records (Java 14+)
record Person(String name, int age) {
    // equals(), hashCode(), toString() auto-generated
}
```

---

## Summary

This reference covers the Java Collections Framework:

- **Collection Hierarchy**: Interface hierarchy and relationships
- **List Implementations**: ArrayList, LinkedList, Vector, Stack
- **Set Implementations**: HashSet, LinkedHashSet, TreeSet
- **Map Implementations**: HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap
- **Queue Implementations**: PriorityQueue, ArrayDeque, BlockingQueue
- **Utility Classes**: Collections, Arrays
- **Stream API**: Functional-style operations on collections
- **Performance Considerations**: Time/space complexity, choosing the right collection
- **Best Practices**: Code style, immutability, concurrency, streams

The collections framework provides a unified architecture for representing and manipulating collections of objects, with implementations optimized for different use cases.
