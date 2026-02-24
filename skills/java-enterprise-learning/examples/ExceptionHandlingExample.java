/**
 * ExceptionHandlingExample.java
 *
 * Demonstrates Java exception handling:
 * - Try-catch-finally
 * - Try-with-resources
 * - Custom exceptions
 * - Exception chaining
 * - Multiple catch blocks
 * - Re-throwing exceptions
 * - Try-catch with resources
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ExceptionHandlingExample {

    public static void main(String[] args) {
        demonstrateBasicTryCatch();
        demonstrateMultipleCatch();
        demonstrateTryWithResources();
        demonstrateFinally();
        demonstrateCustomExceptions();
        demonstrateExceptionChaining();
        demonstrateNestedTryCatch();
        demonstrateThrowVsThrows();
        demonstrateFinallyWithReturn();
    }

    // Basic try-catch
    private static void demonstrateBasicTryCatch() {
        System.out.println("=== Basic Try-Catch ===");

        try {
            int result = 10 / 0;  // ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("Caught arithmetic exception: " + e.getMessage());
        }

        try {
            int[] array = new int[5];
            array[10] = 100;  // ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught array index exception: " + e.getMessage());
        }

        try {
            String str = null;
            int length = str.length();  // NullPointerException
        } catch (NullPointerException e) {
            System.out.println("Caught null pointer exception: " + e.getMessage());
        }

        // Generic exception
        try {
            riskyOperation();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        System.out.println();
    }

    // Multiple catch blocks
    private static void demonstrateMultipleCatch() {
        System.out.println("=== Multiple Catch Blocks ===");

        try {
            // Can throw different exceptions
            int value = (int) (Math.random() * 3);
            switch (value) {
                case 0:
                    throw new ArithmeticException("Division by zero");
                case 1:
                    throw new NullPointerException("Null value");
                case 2:
                    throw new IllegalArgumentException("Invalid argument");
            }
        } catch (ArithmeticException | NullPointerException e) {
            System.out.println("Caught runtime exception: " + e.getClass().getSimpleName());
        } catch (IllegalArgumentException e) {
            System.out.println("Caught illegal argument: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Caught generic exception: " + e.getMessage());
        }

        System.out.println();
    }

    // Try-with-resources
    private static void demonstrateTryWithResources() {
        System.out.println("=== Try-With-Resources ===");

        // Single resource
        try (FileReader reader = new FileReader("test.txt")) {
            int data;
            while ((data = reader.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }

        // Multiple resources
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }

        // Custom resource
        try (MyResource resource = new MyResource()) {
            resource.doWork();
        } catch (Exception e) {
            System.out.println("Error with resource: " + e.getMessage());
        }

        System.out.println();
    }

    // Finally block
    private static void demonstrateFinally() {
        System.out.println("=== Finally Block ===");

        try {
            System.out.println("In try block");
            // int result = 10 / 0;  // Uncomment to test exception
        } catch (ArithmeticException e) {
            System.out.println("In catch block");
        } finally {
            System.out.println("In finally block - always executes");
        }

        System.out.println();

        // Finally with exception
        try {
            throw new RuntimeException("Error in try");
        } catch (RuntimeException e) {
            System.out.println("In catch: " + e.getMessage());
        } finally {
            System.out.println("In finally - even with exception");
        }

        System.out.println();
    }

    // Custom exceptions
    private static void demonstrateCustomExceptions() {
        System.out.println("=== Custom Exceptions ===");

        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
        }

        try {
            validateAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
        }

        System.out.println();
    }

    // Exception chaining
    private static void demonstrateExceptionChaining() {
        System.out.println("=== Exception Chaining ===");

        try {
            processData();
        } catch (ProcessingException e) {
            System.out.println("Processing error: " + e.getMessage());

            // Print chain of causes
            Throwable cause = e.getCause();
            while (cause != null) {
                System.out.println("Caused by: " + cause.getClass().getSimpleName() +
                    ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }

        System.out.println();
    }

    // Nested try-catch
    private static void demonstrateNestedTryCatch() {
        System.out.println("=== Nested Try-Catch ===");

        try {
            System.out.println("Outer try block");

            try {
                System.out.println("Inner try block");
                int result = 10 / 0;
            } catch (ArithmeticException e) {
                System.out.println("Inner catch: " + e.getMessage());
                throw new RuntimeException("Re-throwing from inner catch");
            }

        } catch (RuntimeException e) {
            System.out.println("Outer catch: " + e.getMessage());
        }

        System.out.println();
    }

    // Throw vs throws
    private static void demonstrateThrowVsThrows() {
        System.out.println("=== Throw vs Throws ===");

        try {
            methodWithThrows();
        } catch (CustomCheckedException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        }

        try {
            methodWithThrow();
        } catch (RuntimeException e) {
            System.out.println("Caught unchecked exception: " + e.getMessage());
        }

        System.out.println();
    }

    // Finally with return
    private static void demonstrateFinallyWithReturn() {
        System.out.println("=== Finally with Return ===");

        int result = methodWithFinallyReturn();
        System.out.println("Result: " + result);  // Always 100 from finally

        System.out.println();
    }

    // Helper methods

    private static void riskyOperation() throws Exception {
        throw new Exception("Something went wrong");
    }

    private static void validateAge(int age) throws InvalidAgeException {
        if (age < 0) {
            throw new InvalidAgeException("Age cannot be negative: " + age);
        }
        if (age < 18) {
            throw new InvalidAgeException("Age must be at least 18: " + age);
        }
        System.out.println("Valid age: " + age);
    }

    private static void processData() throws ProcessingException {
        try {
            readData();
        } catch (IOException e) {
            throw new ProcessingException("Failed to process data", e);
        }
    }

    private static void readData() throws IOException {
        throw new IOException("Failed to read data");
    }

    private static void methodWithThrows() throws CustomCheckedException {
        throw new CustomCheckedException("Checked exception thrown");
    }

    private static void methodWithThrow() {
        throw new RuntimeException("Unchecked exception thrown");
    }

    private static int methodWithFinallyReturn() {
        try {
            return 50;
        } catch (Exception e) {
            return 75;
        } finally {
            return 100;  // This overwrites previous returns
        }
    }
}

// Custom exceptions
class InvalidAgeException extends Exception {
    public InvalidAgeException(String message) {
        super(message);
    }
}

class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}

class ProcessingException extends Exception {
    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Custom resource implementing AutoCloseable
class MyResource implements AutoCloseable {
    public MyResource() {
        System.out.println("Resource created");
    }

    public void doWork() throws Exception {
        System.out.println("Resource doing work");
        // throw new Exception("Error during work");
    }

    @Override
    public void close() throws Exception {
        System.out.println("Resource closed");
    }
}

// Alternative: Using Closeable
class MyCloseableResource implements Closeable {
    public MyCloseableResource() {
        System.out.println("Closeable resource created");
    }

    public void doWork() throws IOException {
        System.out.println("Closeable resource doing work");
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closeable resource closed");
    }
}

// Resource with suppressed exceptions
class ResourceWithException implements AutoCloseable {
    public ResourceWithException() {
        System.out.println("Resource with exception created");
    }

    public void doWork() throws Exception {
        throw new Exception("Error during work");
    }

    @Override
    public void close() throws Exception {
        throw new Exception("Error during close");
    }
}

// Demonstration of suppressed exceptions
class SuppressedExceptionsDemo {
    public static void main(String[] args) {
        try (ResourceWithException resource = new ResourceWithException()) {
            resource.doWork();
        } catch (Exception e) {
            System.out.println("Main exception: " + e.getMessage());

            // Get suppressed exceptions
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                System.out.println("Suppressed exception: " + t.getMessage());
            }
        }
    }
}
