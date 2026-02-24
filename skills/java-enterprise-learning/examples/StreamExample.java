/**
 * StreamExample.java
 *
 * Demonstrates Java Stream API features:
 * - Stream creation
 * - Intermediate operations
 * - Terminal operations
 * - Collecting
 * - Grouping and partitioning
 * - Parallel streams
 * - Optional operations
 */

import java.util.*;
import java.util.stream.*;

public class StreamExample {

    public static void main(String[] args) {
        demonstrateStreamCreation();
        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateCollecting();
        demonstrateGrouping();
        demonstrateParallelStreams();
        demonstrateOptional();
        demonstrateAdvancedExamples();
    }

    // Stream creation
    private static void demonstrateStreamCreation() {
        System.out.println("=== Stream Creation ===");

        // From collection
        List<String> list = Arrays.asList("a", "b", "c");
        list.stream().forEach(System.out::println);

        // From values
        Stream.of("a", "b", "c").forEach(System.out::println);

        // From array
        String[] array = {"a", "b", "c"};
        Arrays.stream(array).forEach(System.out::println);

        // From builder
        Stream.Builder<String> builder = Stream.builder();
        builder.add("a").add("b").add("c");
        builder.build().forEach(System.out::println);

        // Generate infinite stream (limited)
        Stream.generate(() -> "value").limit(3).forEach(System.out::println);

        // Iterate
        Stream.iterate(0, n -> n + 1).limit(5).forEach(System.out::println);

        // IntStream range
        IntStream.range(0, 5).forEach(System.out::println);

        // String.chars
        "hello".chars().forEach(c -> System.out.println((char) c));

        System.out.println();
    }

    // Intermediate operations
    private static void demonstrateIntermediateOperations() {
        System.out.println("=== Intermediate Operations ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Filter
        System.out.println("Even numbers:");
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Map
        System.out.println("Squared:");
        numbers.stream()
            .map(n -> n * n)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // FlatMap
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4),
            Arrays.asList(5, 6)
        );

        System.out.println("Flattened:");
        nested.stream()
            .flatMap(List::stream)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Distinct
        List<Integer> duplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
        System.out.println("Distinct:");
        duplicates.stream()
            .distinct()
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Sorted
        System.out.println("Sorted:");
        numbers.stream()
            .sorted(Comparator.reverseOrder())
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Limit and skip
        System.out.println("Limit 5, skip 2:");
        numbers.stream()
            .skip(2)
            .limit(5)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Peek
        System.out.println("With peek:");
        numbers.stream()
            .peek(n -> System.out.println("Processing: " + n))
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.println();
    }

    // Terminal operations
    private static void demonstrateTerminalOperations() {
        System.out.println("=== Terminal Operations ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // ForEach
        System.out.println("For each:");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Collect
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());

        System.out.println("Even numbers: " + evenNumbers);

        // Reduce
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("Sum: " + sum);

        Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);
        System.out.println("Product: " + product.orElse(0));

        // Count
        long count = numbers.stream().filter(n -> n > 5).count();
        System.out.println("Count > 5: " + count);

        // Min and Max
        Optional<Integer> min = numbers.stream().min(Comparator.naturalOrder());
        Optional<Integer> max = numbers.stream().max(Comparator.naturalOrder());

        System.out.println("Min: " + min.orElse(0));
        System.out.println("Max: " + max.orElse(0));

        // AnyMatch, AllMatch, NoneMatch
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

        System.out.println("Any even: " + anyEven);
        System.out.println("All positive: " + allPositive);
        System.out.println("None negative: " + noneNegative);

        // FindFirst and FindAny
        Optional<Integer> first = numbers.stream().findFirst();
        Optional<Integer> any = numbers.stream().findAny();

        System.out.println("First: " + first.orElse(0));
        System.out.println("Any: " + any.orElse(0));

        // ToArray
        Integer[] array = numbers.stream().toArray(Integer[]::new);
        System.out.println("Array length: " + array.length);

        System.out.println();
    }

    // Collecting examples
    private static void demonstrateCollecting() {
        System.out.println("=== Collecting ===");

        List<String> words = Arrays.asList(
            "apple", "banana", "cherry", "date", "elderberry"
        );

        // ToList
        List<String> longWords = words.stream()
            .filter(w -> w.length() > 5)
            .collect(Collectors.toList());

        System.out.println("Long words: " + longWords);

        // ToSet
        Set<String> uniqueWords = words.stream()
            .collect(Collectors.toSet());

        System.out.println("Unique words: " + uniqueWords);

        // ToMap
        Map<String, Integer> wordLengths = words.stream()
            .collect(Collectors.toMap(
                w -> w,
                String::length
            ));

        System.out.println("Word lengths: " + wordLengths);

        // Joining
        String joined = words.stream()
            .collect(Collectors.joining(", "));

        System.out.println("Joined: " + joined);

        // Grouping
        Map<Integer, List<String>> byLength = words.stream()
            .collect(Collectors.groupingBy(String::length));

        System.out.println("Grouped by length: " + byLength);

        // Partitioning
        Map<Boolean, List<String>> partitioned = words.stream()
            .collect(Collectors.partitioningBy(w -> w.length() > 5));

        System.out.println("Partitioned: " + partitioned);

        // Counting
        Map<String, Long> counts = words.stream()
            .collect(Collectors.groupingBy(
                w -> w.substring(0, 1),
                Collectors.counting()
            ));

        System.out.println("Counts by first letter: " + counts);

        // Summing
        int totalLength = words.stream()
            .collect(Collectors.summingInt(String::length));

        System.out.println("Total length: " + totalLength);

        // Averaging
        double avgLength = words.stream()
            .collect(Collectors.averagingDouble(String::length));

        System.out.println("Average length: " + avgLength);

        System.out.println();
    }

    // Grouping and partitioning
    private static void demonstrateGrouping() {
        System.out.println("=== Grouping and Partitioning ===");

        List<Person> people = Arrays.asList(
            new Person("Alice", 30, "Engineering"),
            new Person("Bob", 25, "Engineering"),
            new Person("Charlie", 35, "Marketing"),
            new Person("David", 28, "Engineering"),
            new Person("Eve", 40, "Marketing")
        );

        // Group by department
        Map<String, List<Person>> byDept = people.stream()
            .collect(Collectors.groupingBy(Person::department));

        System.out.println("Grouped by department:");
        byDept.forEach((dept, persons) -> {
            System.out.println("  " + dept + ": " + persons);
        });

        // Group by and count
        Map<String, Long> countsByDept = people.stream()
            .collect(Collectors.groupingBy(
                Person::department,
                Collectors.counting()
            ));

        System.out.println("Counts by department: " + countsByDept);

        // Group by and calculate average age
        Map<String, Double> avgAgeByDept = people.stream()
            .collect(Collectors.groupingBy(
                Person::department,
                Collectors.averagingInt(Person::age)
            ));

        System.out.println("Average age by department: " + avgAgeByDept);

        // Partition by age > 30
        Map<Boolean, List<Person>> byAge = people.stream()
            .collect(Collectors.partitioningBy(p -> p.age() > 30));

        System.out.println("Partitioned by age > 30:");
        System.out.println("  Young: " + byAge.get(false));
        System.out.println("  Old: " + byAge.get(true));

        // Multi-level grouping
        Map<String, Map<String, List<Person>>> multiLevel = people.stream()
            .collect(Collectors.groupingBy(
                Person::department,
                Collectors.groupingBy(p -> p.age() > 30 ? "Senior" : "Junior")
            ));

        System.out.println("Multi-level grouping: " + multiLevel);

        System.out.println();
    }

    // Parallel streams
    private static void demonstrateParallelStreams() {
        System.out.println("=== Parallel Streams ===");

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 10_000_000; i++) {
            numbers.add(i);
        }

        // Sequential stream
        long start = System.nanoTime();
        long sumSequential = numbers.stream()
            .mapToLong(i -> (long) i * i)
            .sum();
        long sequentialTime = System.nanoTime() - start;

        System.out.println("Sequential sum: " + sumSequential);
        System.out.println("Sequential time: " + sequentialTime / 1_000_000 + " ms");

        // Parallel stream
        start = System.nanoTime();
        long sumParallel = numbers.parallelStream()
            .mapToLong(i -> (long) i * i)
            .sum();
        long parallelTime = System.nanoTime() - start;

        System.out.println("Parallel sum: " + sumParallel);
        System.out.println("Parallel time: " + parallelTime / 1_000_000 + " ms");

        // Parallel stream with custom operations
        System.out.println("Parallel processing:");
        numbers.parallelStream()
            .limit(10)
            .forEach(n -> System.out.println(
                n + " in " + Thread.currentThread().getName()
            ));

        System.out.println();
    }

    // Optional operations
    private static void demonstrateOptional() {
        System.out.println("=== Optional ===");

        // Create Optional
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);

        System.out.println("Present: " + present);
        System.out.println("Empty: " + empty);
        System.out.println("Nullable: " + nullable);

        // ifPresent
        present.ifPresent(s -> System.out.println("Value: " + s));

        // orElse
        String value1 = present.orElse("Default");
        String value2 = empty.orElse("Default");

        System.out.println("orElse - present: " + value1);
        System.out.println("orElse - empty: " + value2);

        // orElseGet
        String value3 = empty.orElseGet(() -> {
            System.out.println("Computing default value");
            return "Computed Default";
        });

        System.out.println("orElseGet: " + value3);

        // orElseThrow
        try {
            present.orElseThrow();
            // empty.orElseThrow();  // Throws NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("Empty optional threw exception");
        }

        // map
        Optional<Integer> length = present.map(String::length);
        System.out.println("Length: " + length.orElse(0));

        // filter
        Optional<String> startsWithH = present.filter(s -> s.startsWith("H"));
        Optional<String> startsWithW = present.filter(s -> s.startsWith("W"));

        System.out.println("Starts with H: " + startsWithH.orElse("No"));
        System.out.println("Starts with W: " + startsWithW.orElse("No"));

        // flatMap
        Optional<String> upper = present.flatMap(s ->
            s.isEmpty() ? Optional.empty() : Optional.of(s.toUpperCase())
        );

        System.out.println("FlatMap result: " + upper.orElse("Empty"));

        // Stream of Optionals
        List<Optional<String>> optionals = Arrays.asList(
            Optional.of("a"),
            Optional.empty(),
            Optional.of("b"),
            Optional.empty(),
            Optional.of("c")
        );

        List<String> values = optionals.stream()
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

        System.out.println("Values from optionals: " + values);

        System.out.println();
    }

    // Advanced examples
    private static void demonstrateAdvancedExamples() {
        System.out.println("=== Advanced Examples ===");

        // FlatMap example
        List<List<Integer>> matrix = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5, 6),
            Arrays.asList(7, 8, 9)
        );

        List<Integer> flattened = matrix.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        System.out.println("Flattened matrix: " + flattened);

        // Collect to Map with merge function
        List<Person> people = Arrays.asList(
            new Person("Alice", 30, "Engineering"),
            new Person("Alice", 25, "Marketing")  // Same name, different person
        );

        Map<String, Integer> nameToAge = people.stream()
            .collect(Collectors.toMap(
                Person::name,
                Person::age,
                (old, newAge) -> old  // Keep first occurrence
            ));

        System.out.println("Name to age: " + nameToAge);

        // Custom collector
        String commaSeparated = people.stream()
            .map(Person::name)
            .collect(Collectors.joining(", ", "[", "]"));

        System.out.println("Comma separated: " + commaSeparated);

        // Collector of
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int sum = numbers.stream()
            .collect(Collectors.summingInt(Integer::intValue));

        System.out.println("Sum: " + sum);

        // Summary statistics
        IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();

        System.out.println("Summary statistics:");
        System.out.println("  Count: " + stats.getCount());
        System.out.println("  Sum: " + stats.getSum());
        System.out.println("  Min: " + stats.getMin());
        System.out.println("  Max: " + stats.getMax());
        System.out.println("  Average: " + stats.getAverage());

        // Stream of arrays
        int[] array = {1, 2, 3, 4, 5};
        int sumArray = Arrays.stream(array).sum();

        System.out.println("Array sum: " + sumArray);

        System.out.println();
    }
}

// Helper class
record Person(String name, int age, String department) {}
