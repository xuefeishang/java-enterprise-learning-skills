# Java IO and NIO Reference Guide

## Table of Contents

1. [Traditional IO](#traditional-io)
2. [File IO](#file-io)
3. [Stream Classes](#stream-classes)
4. [Reader/Writer Classes](#readerwriter-classes)
5. [Buffering](#buffering)
6. [NIO Overview](#nio-overview)
7. [Buffers](#buffers)
8. [Channels](#channels)
9. [Selectors](#selectors)
10. [Asynchronous IO](#asynchronous-io)
11. [File System Operations](#file-system-operations)
12. [Best Practices](#best-practices)

---

## Traditional IO

### IO Class Hierarchy

```
InputStream (abstract)
├── FileInputStream
├── ByteArrayInputStream
├── BufferedInputStream
├── DataInputStream
├── ObjectInputStream
└── PushbackInputStream

OutputStream (abstract)
├── FileOutputStream
├── ByteArrayOutputStream
├── BufferedOutputStream
├── DataOutputStream
├── ObjectOutputStream
└── PrintStream

Reader (abstract)
├── FileReader
├── StringReader
├── BufferedReader
├── CharArrayReader
└── InputStreamReader

Writer (abstract)
├── FileWriter
├── StringWriter
├── BufferedWriter
├── CharArrayWriter
└── OutputStreamWriter
```

### Basic InputStream Operations

```java
// Reading byte by byte
try (InputStream in = new FileInputStream("input.txt")) {
    int byteData;
    while ((byteData = in.read()) != -1) {
        System.out.print((char) byteData);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading into byte array
try (InputStream in = new FileInputStream("input.txt")) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        System.out.write(buffer, 0, bytesRead);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading specific number of bytes
try (InputStream in = new FileInputStream("input.txt")) {
    byte[] buffer = new byte[100];
    int bytesRead = in.read(buffer, 0, buffer.length);
    System.out.println("Read " + bytesRead + " bytes");
} catch (IOException e) {
    e.printStackTrace();
}

// ByteArrayInputStream from memory
byte[] data = {65, 66, 67, 68, 69};  // ABCDE
try (InputStream in = new ByteArrayInputStream(data)) {
    int byteData;
    while ((byteData = in.read()) != -1) {
        System.out.print((char) byteData);
    }
}
```

### Basic OutputStream Operations

```java
// Writing byte by byte
try (OutputStream out = new FileOutputStream("output.txt")) {
    String text = "Hello, World!";
    byte[] bytes = text.getBytes();
    out.write(bytes);
} catch (IOException e) {
    e.printStackTrace();
}

// Writing byte array with offset
try (OutputStream out = new FileOutputStream("output.txt")) {
    byte[] bytes = "Hello, World!".getBytes();
    out.write(bytes, 0, 5);  // Write first 5 bytes: "Hello"
} catch (IOException e) {
    e.printStackTrace();
}

// ByteArrayOutputStream to memory
try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    out.write("Hello".getBytes());
    out.write(" ".getBytes());
    out.write("World".getBytes());

    byte[] result = out.toByteArray();
    String text = out.toString();  // "Hello World"
    System.out.println(text);
} catch (IOException e) {
    e.printStackTrace();
}
```

---

## File IO

### FileInputStream/FileOutputStream

```java
// Reading from file
try (FileInputStream fis = new FileInputStream("input.txt")) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
        processBytes(buffer, bytesRead);
    }
} catch (FileNotFoundException e) {
    System.err.println("File not found: " + e.getMessage());
} catch (IOException e) {
    System.err.println("IO error: " + e.getMessage());
}

// Writing to file
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    String content = "Hello, World!";
    fos.write(content.getBytes());
} catch (IOException e) {
    e.printStackTrace();
}

// Appending to file
try (FileOutputStream fos = new FileOutputStream("output.txt", true)) {
    fos.write("\nAppended content".getBytes());
} catch (IOException e) {
    e.printStackTrace();
}

// Binary file operations
try (FileInputStream fis = new FileInputStream("data.bin");
     FileOutputStream fos = new FileOutputStream("copy.bin")) {

    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
        fos.write(buffer, 0, bytesRead);
    }
}
```

### FileReader/FileWriter

```java
// Reading text file
try (FileReader reader = new FileReader("input.txt")) {
    int charData;
    while ((charData = reader.read()) != -1) {
        System.out.print((char) charData);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading into char array
try (FileReader reader = new FileReader("input.txt")) {
    char[] buffer = new char[1024];
    int charsRead;
    while ((charsRead = reader.read(buffer)) != -1) {
        System.out.print(new String(buffer, 0, charsRead));
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Writing text file
try (FileWriter writer = new FileWriter("output.txt")) {
    writer.write("Hello, World!\n");
    writer.write("This is a text file.");
} catch (IOException e) {
    e.printStackTrace();
}

// Appending to text file
try (FileWriter writer = new FileWriter("output.txt", true)) {
    writer.write("\nAppended text");
} catch (IOException e) {
    e.printStackTrace();
}

// With character encoding
try (FileReader reader = new FileReader("input.txt", StandardCharsets.UTF_8);
     FileWriter writer = new FileWriter("output.txt", StandardCharsets.UTF_8)) {

    char[] buffer = new char[1024];
    int charsRead;
    while ((charsRead = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, charsRead);
    }
}
```

### RandomAccessFile

```java
// Read and write at arbitrary positions
try (RandomAccessFile raf = new RandomAccessFile("data.dat", "rw")) {
    // Write data
    raf.writeUTF("Hello");
    raf.writeInt(42);
    raf.writeDouble(3.14);

    // Move to beginning and read
    raf.seek(0);
    String str = raf.readUTF();
    int num = raf.readInt();
    double dbl = raf.readDouble();

    System.out.println(str + ", " + num + ", " + dbl);

    // Get current position
    long position = raf.getFilePointer();

    // Get file length
    long length = raf.length();

    // Jump to specific position
    raf.seek(0);
} catch (IOException e) {
    e.printStackTrace();
}

// Mode options:
// "r"   - Read only
// "rw"  - Read and write
// "rws" - Read/write, sync on every write
// "rwd" - Read/write, sync on content writes only
```

---

## Stream Classes

### BufferedInputStream/BufferedOutputStream

```java
// Buffered reading
try (InputStream in = new BufferedInputStream(new FileInputStream("input.txt"))) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        processBytes(buffer, bytesRead);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Custom buffer size
try (InputStream in = new BufferedInputStream(
    new FileInputStream("input.txt"), 8192)) {

    // Read operations
} catch (IOException e) {
    e.printStackTrace();
}

// Buffered writing
try (OutputStream out = new BufferedOutputStream(new FileOutputStream("output.txt"))) {
    byte[] data = "Hello, World!".getBytes();
    out.write(data);
    out.flush();  // Ensure data is written
} catch (IOException e) {
    e.printStackTrace();
}

// Buffered copy operation
try (BufferedInputStream in = new BufferedInputStream(new FileInputStream("input.bin"));
     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("output.bin"))) {

    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
    }
}
```

### DataInputStream/DataOutputStream

```java
// Writing primitive types
try (DataOutputStream dos = new DataOutputStream(
    new BufferedOutputStream(new FileOutputStream("data.bin")))) {

    dos.writeByte(127);
    dos.writeShort(32767);
    dos.writeInt(2147483647);
    dos.writeLong(9223372036854775807L);
    dos.writeFloat(3.14f);
    dos.writeDouble(3.14159265359);
    dos.writeChar('A');
    dos.writeBoolean(true);
    dos.writeUTF("Hello, World!");

} catch (IOException e) {
    e.printStackTrace();
}

// Reading primitive types
try (DataInputStream dis = new DataInputStream(
    new BufferedInputStream(new FileInputStream("data.bin")))) {

    byte b = dis.readByte();
    short s = dis.readShort();
    int i = dis.readInt();
    long l = dis.readLong();
    float f = dis.readFloat();
    double d = dis.readDouble();
    char c = dis.readChar();
    boolean bool = dis.readBoolean();
    String str = dis.readUTF();

    System.out.printf("%d, %d, %d, %d, %.2f, %.2f, %c, %b, %s%n",
        b, s, i, l, f, d, c, bool, str);

} catch (IOException e) {
    e.printStackTrace();
}
```

### ObjectInputStream/ObjectOutputStream

```java
// Serializable class
class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    transient String password;  // Not serialized

    public User(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + "}";
    }
}

// Writing objects
try (ObjectOutputStream oos = new ObjectOutputStream(
    new BufferedOutputStream(new FileOutputStream("users.dat")))) {

    User user1 = new User("Alice", 30, "secret123");
    User user2 = new User("Bob", 25, "password456");

    oos.writeObject(user1);
    oos.writeObject(user2);

    // Write collection
    List<User> users = Arrays.asList(user1, user2);
    oos.writeObject(users);

} catch (IOException e) {
    e.printStackTrace();
}

// Reading objects
try (ObjectInputStream ois = new ObjectInputStream(
    new BufferedInputStream(new FileInputStream("users.dat")))) {

    User user1 = (User) ois.readObject();
    User user2 = (User) ois.readObject();

    @SuppressWarnings("unchecked")
    List<User> users = (List<User>) ois.readObject();

    System.out.println(user1);
    System.out.println(user2);
    System.out.println(users);

} catch (IOException | ClassNotFoundException e) {
    e.printStackTrace();
}

// Custom serialization
class CustomUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private transient String password;

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Encrypt password before writing
        oos.writeObject(encrypt(password));
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Decrypt password after reading
        this.password = decrypt((String) ois.readObject());
    }

    private String encrypt(String password) {
        // Encryption logic
        return password;  // Simplified
    }

    private String decrypt(String encrypted) {
        // Decryption logic
        return encrypted;  // Simplified
    }
}
```

### PrintStream/PrintWriter

```java
// PrintStream for output
try (PrintStream ps = new PrintStream(new FileOutputStream("output.txt"))) {
    ps.println("Hello, World!");
    ps.printf("Name: %s, Age: %d%n", "Alice", 30);
    ps.print("Multiple ");
    ps.print("words ");
    ps.println("on one line");

    // Print boolean
    ps.println(true);

    // Print numbers
    ps.println(42);
    ps.println(3.14);

    // Print objects
    User user = new User("Bob", 25, "pass");
    ps.println(user);

} catch (FileNotFoundException e) {
    e.printStackTrace();
}

// PrintWriter with auto-flush
try (PrintWriter pw = new PrintWriter(new FileWriter("output.txt"))) {
    pw.println("Line 1");
    pw.println("Line 2");
    pw.println("Line 3");

    // Format strings
    pw.printf("Price: $%.2f%n", 19.99);

    // Check errors
    if (pw.checkError()) {
        System.err.println("Error occurred");
    }

} catch (IOException e) {
    e.printStackTrace();
}

// PrintWriter with auto-flush (true parameter)
try (PrintWriter pw = new PrintWriter(new FileWriter("console.txt"), true)) {
    pw.println("Auto-flushed line 1");
    pw.println("Auto-flushed line 2");
    // Auto-flushes after each println

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## Reader/Writer Classes

### BufferedReader/BufferedWriter

```java
// Reading lines
try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading all lines (Java 8+)
try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
    List<String> lines = reader.lines().collect(Collectors.toList());
    lines.forEach(System.out::println);
} catch (IOException e) {
    e.printStackTrace();
}

// Reading with custom buffer size
try (BufferedReader reader = new BufferedReader(
    new FileReader("input.txt"), 8192)) {

    // Read operations

} catch (IOException e) {
    e.printStackTrace();
}

// Writing lines
try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
    writer.write("Line 1");
    writer.newLine();  // Platform-independent line separator
    writer.write("Line 2");
    writer.newLine();
    writer.write("Line 3");

} catch (IOException e) {
    e.printStackTrace();
}

// Copy file with character encoding
try (BufferedReader reader = new BufferedReader(new InputStreamReader(
    new FileInputStream("input.txt"), StandardCharsets.UTF_8));
     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
    new FileOutputStream("output.txt"), StandardCharsets.UTF_8))) {

    String line;
    while ((line = reader.readLine()) != null) {
        writer.write(line);
        writer.newLine();
    }

} catch (IOException e) {
    e.printStackTrace();
}
```

### InputStreamReader/OutputStreamWriter

```java
// Reading bytes as characters with encoding
try (InputStreamReader reader = new InputStreamReader(
    new FileInputStream("input.txt"), StandardCharsets.UTF_8)) {

    int charData;
    while ((charData = reader.read()) != -1) {
        System.out.print((char) charData);
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Writing characters as bytes with encoding
try (OutputStreamWriter writer = new OutputStreamWriter(
    new FileOutputStream("output.txt"), StandardCharsets.UTF_8)) {

    writer.write("Hello, 世界!");  // Unicode characters

} catch (IOException e) {
    e.printStackTrace();
}

// Detecting encoding
try (InputStreamReader reader = new InputStreamReader(
    new FileInputStream("input.txt"), "windows-1252")) {

    // Read with specific encoding

} catch (UnsupportedEncodingException e) {
    System.err.println("Unsupported encoding: " + e.getMessage());
} catch (IOException e) {
    e.printStackTrace();
}
```

### StringReader/StringWriter

```java
// Reading from string
String text = "Hello, World!";
try (StringReader reader = new StringReader(text)) {
    int charData;
    while ((charData = reader.read()) != -1) {
        System.out.print((char) charData);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading into char array
try (StringReader reader = new StringReader(text)) {
    char[] buffer = new char[10];
    int charsRead = reader.read(buffer);
    System.out.println(new String(buffer, 0, charsRead));
} catch (IOException e) {
    e.printStackTrace();
}

// Writing to string
try (StringWriter writer = new StringWriter()) {
    writer.write("Hello");
    writer.write(", ");
    writer.write("World!");

    String result = writer.toString();
    System.out.println(result);  // "Hello, World!"

    // Get buffer directly
    StringBuilder buffer = writer.getBuffer();

} catch (IOException e) {
    e.printStackTrace();
}

// Building string with StringWriter
try (StringWriter writer = new StringWriter()) {
    writer.write("Line 1\n");
    writer.write("Line 2\n");
    writer.write("Line 3\n");

    String result = writer.toString();
    String[] lines = result.split("\n");

} catch (IOException e) {
    e.printStackTrace();
}
```

### CharArrayReader/CharArrayWriter

```java
// Reading from char array
char[] chars = {'H', 'e', 'l', 'l', 'o'};
try (CharArrayReader reader = new CharArrayReader(chars)) {
    int charData;
    while ((charData = reader.read()) != -1) {
        System.out.print((char) charData);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Reading with offset and length
char[] largeChars = "Hello, World!".toCharArray();
try (CharArrayReader reader = new CharArrayReader(largeChars, 7, 5)) {
    // Reads from index 7, 5 characters: "World"
    int charData;
    while ((charData = reader.read()) != -1) {
        System.out.print((char) charData);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Writing to char array
try (CharArrayWriter writer = new CharArrayWriter()) {
    writer.write("Hello");
    writer.write(", ");
    writer.write("World!");

    char[] result = writer.toCharArray();
    System.out.println(new String(result));  // "Hello, World!"

    String stringResult = writer.toString();

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## Buffering

### Buffer Size Considerations

```java
// Small buffer (more I/O operations)
try (BufferedInputStream in = new BufferedInputStream(
    new FileInputStream("large.dat"), 512)) {

    // Many small reads

} catch (IOException e) {
    e.printStackTrace();
}

// Large buffer (fewer I/O operations)
try (BufferedInputStream in = new BufferedInputStream(
    new FileInputStream("large.dat"), 65536)) {

    // Fewer large reads

} catch (IOException e) {
    e.printStackTrace();
}

// Recommended buffer sizes:
// - Disk I/O: 8KB - 64KB
// - Network I/O: 4KB - 16KB
// - Memory-mapped I/O: Page size (usually 4KB)
```

### Manual Buffering

```java
// Read entire file into memory (small files)
try (InputStream in = new FileInputStream("small.txt")) {
    byte[] data = in.readAllBytes();  // Java 9+
    System.out.println(new String(data));
} catch (IOException e) {
    e.printStackTrace();
}

// Read with manual buffer
try (InputStream in = new FileInputStream("medium.txt")) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int bytesRead;

    while ((bytesRead = in.read(buffer)) != -1) {
        baos.write(buffer, 0, bytesRead);
    }

    byte[] data = baos.toByteArray();
    String content = new String(data);

} catch (IOException e) {
    e.printStackTrace();
}

// Write with manual buffer
byte[] largeData = generateLargeData();
try (OutputStream out = new FileOutputStream("large.txt")) {
    int offset = 0;
    int bufferSize = 8192;

    while (offset < largeData.length) {
        int chunkSize = Math.min(bufferSize, largeData.length - offset);
        out.write(largeData, offset, chunkSize);
        offset += chunkSize;
    }

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## NIO Overview

### NIO vs IO

| Feature | Traditional IO | NIO |
|---------|---------------|-----|
| Model | Stream-oriented | Buffer-oriented |
| Blocking | Blocking only | Blocking and non-blocking |
| Performance | Good for simple operations | Better for high throughput |
| Scalability | One thread per connection | Selector for many connections |
| Operations | Read/Write streams | Channels, Buffers, Selectors |

### NIO Core Components

```java
// Basic NIO file reading
try (FileChannel channel = FileChannel.open(
    Paths.get("input.txt"),
    StandardOpenOption.READ)) {

    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(buffer);

    while (bytesRead != -1) {
        buffer.flip();  // Switch from write to read mode
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }
        buffer.clear();  // Clear for next read
        bytesRead = channel.read(buffer);
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Basic NIO file writing
try (FileChannel channel = FileChannel.open(
    Paths.get("output.txt"),
    StandardOpenOption.WRITE,
    StandardOpenOption.CREATE)) {

    String content = "Hello, World!";
    ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
    channel.write(buffer);

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## Buffers

### Buffer Basics

```java
// Creating buffers
ByteBuffer buffer = ByteBuffer.allocate(1024);
ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);

// Wrapping existing array
byte[] array = new byte[1024];
ByteBuffer wrapped = ByteBuffer.wrap(array);

// Buffer properties
int capacity = buffer.capacity();   // Total capacity
int position = buffer.position();   // Current position
int limit = buffer.limit();         // Read/write limit
boolean hasRemaining = buffer.hasRemaining();

// Buffer operations
buffer.clear();    // position=0, limit=capacity
buffer.flip();     // position=0, limit=previous position
buffer.rewind();   // position=0, limit unchanged
buffer.compact();  // Move remaining to beginning

// Reading/writing
buffer.put((byte) 65);       // Write byte
byte b = buffer.get();      // Read byte

buffer.putInt(42);          // Write int
int i = buffer.getInt();    // Read int

buffer.putDouble(3.14);     // Write double
double d = buffer.getDouble();  // Read double
```

### Buffer Types

```java
// ByteBuffer
ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
byteBuffer.put((byte) 1);
byteBuffer.putInt(42);
byteBuffer.putDouble(3.14);

// CharBuffer
CharBuffer charBuffer = CharBuffer.allocate(1024);
charBuffer.put('H');
charBuffer.put("ello");
charBuffer.put("World");

// ShortBuffer
ShortBuffer shortBuffer = ShortBuffer.allocate(1024);
shortBuffer.put((short) 100);
shortBuffer.put((short) 200);

// IntBuffer
IntBuffer intBuffer = IntBuffer.allocate(1024);
intBuffer.put(42);
intBuffer.put(100);

// LongBuffer
LongBuffer longBuffer = LongBuffer.allocate(1024);
longBuffer.put(1000000000L);
longBuffer.put(2000000000L);

// FloatBuffer
FloatBuffer floatBuffer = FloatBuffer.allocate(1024);
floatBuffer.put(3.14f);
floatBuffer.put(2.71f);

// DoubleBuffer
DoubleBuffer doubleBuffer = DoubleBuffer.allocate(1024);
doubleBuffer.put(3.14159265359);
doubleBuffer.put(2.71828182846);

// Buffer views
ByteBuffer bb = ByteBuffer.allocate(1024);
IntBuffer ib = bb.asIntBuffer();      // View as int buffer
LongBuffer lb = bb.asLongBuffer();    // View as long buffer
```

### Buffer Operations

```java
// Bulk operations
ByteBuffer buffer = ByteBuffer.allocate(1024);

// Write array
byte[] source = "Hello, World!".getBytes();
buffer.put(source);

// Write portion of array
buffer.put(source, 0, 5);

// Read to array
buffer.flip();
byte[] dest = new byte[buffer.remaining()];
buffer.get(dest);

// Read portion
byte[] partial = new byte[5];
buffer.get(partial, 0, partial.length);

// Mark and reset
buffer.reset();  // Return to marked position
buffer.mark();   // Mark current position

// Duplicate buffer (shares data, independent position)
ByteBuffer duplicate = buffer.duplicate();

// Slice buffer (view of portion of buffer)
ByteBuffer slice = buffer.slice();

// Direct vs heap buffers
ByteBuffer heapBuffer = ByteBuffer.allocate(1024);  // Heap-based
ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);  // Direct memory

// Direct buffer advantages:
// - Better for I/O operations
// - No JVM heap overhead
// - Can be used with memory-mapped files
// - Disadvantages:
// - Slower allocation/deallocation
// - Not subject to GC
```

---

## Channels

### FileChannel

```java
// Reading with FileChannel
try (FileChannel channel = FileChannel.open(
    Paths.get("input.txt"),
    StandardOpenOption.READ)) {

    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(buffer);

    while (bytesRead != -1) {
        buffer.flip();
        processBuffer(buffer);
        buffer.clear();
        bytesRead = channel.read(buffer);
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Writing with FileChannel
try (FileChannel channel = FileChannel.open(
    Paths.get("output.txt"),
    StandardOpenOption.WRITE,
    StandardOpenOption.CREATE)) {

    String content = "Hello, World!";
    ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
    channel.write(buffer);

} catch (IOException e) {
    e.printStackTrace();
}

// Appending with FileChannel
try (FileChannel channel = FileChannel.open(
    Paths.get("output.txt"),
    StandardOpenOption.WRITE,
    StandardOpenOption.APPEND)) {

    String content = "\nAppended line";
    ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
    channel.write(buffer);

} catch (IOException e) {
    e.printStackTrace();
}

// File position operations
try (FileChannel channel = FileChannel.open(
    Paths.get("data.txt"),
    StandardOpenOption.READ,
    StandardOpenOption.WRITE)) {

    // Get current position
    long position = channel.position();
    System.out.println("Position: " + position);

    // Set position
    channel.position(100);

    // Get file size
    long size = channel.size();

    // Truncate file
    channel.truncate(1000);

    // Write at specific position
    ByteBuffer buffer = ByteBuffer.wrap("Insert".getBytes());
    channel.write(buffer, 50);

    // Read from specific position
    ByteBuffer readBuffer = ByteBuffer.allocate(10);
    channel.read(readBuffer, 100);

} catch (IOException e) {
    e.printStackTrace();
}

// File transfer (fast copy)
try (FileChannel source = FileChannel.open(
    Paths.get("source.txt"),
    StandardOpenOption.READ);
     FileChannel dest = FileChannel.open(
    Paths.get("dest.txt"),
    StandardOpenOption.WRITE,
    StandardOpenOption.CREATE)) {

    // Transfer from position 0, up to source.size()
    long transferred = source.transferTo(0, source.size(), dest);
    System.out.println("Transferred: " + transferred + " bytes");

} catch (IOException e) {
    e.printStackTrace();
}

// File locking
try (FileChannel channel = FileChannel.open(
    Paths.get("locked.txt"),
    StandardOpenOption.READ,
    StandardOpenOption.WRITE)) {

    // Exclusive lock
    FileLock lock = channel.lock();

    try {
        // Perform operations with lock
        ByteBuffer buffer = ByteBuffer.wrap("Locked data".getBytes());
        channel.write(buffer);
    } finally {
        lock.release();
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Shared lock (for reading)
try (FileChannel channel = FileChannel.open(
    Paths.get("shared.txt"),
    StandardOpenOption.READ)) {

    FileLock lock = channel.lock(0, Long.MAX_VALUE, true);  // Shared lock

    try {
        // Read operations
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
    } finally {
        lock.release();
    }

} catch (IOException e) {
    e.printStackTrace();
}
```

### SocketChannel

```java
// Client connection
try (SocketChannel channel = SocketChannel.open()) {
    // Connect to server
    channel.connect(new InetSocketAddress("localhost", 8080));

    // Send data
    String message = "Hello, Server!";
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
    channel.write(buffer);

    // Read response
    ByteBuffer response = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(response);

    if (bytesRead != -1) {
        response.flip();
        byte[] data = new byte[response.remaining()];
        response.get(data);
        System.out.println("Response: " + new String(data));
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Non-blocking mode
try (SocketChannel channel = SocketChannel.open()) {
    channel.configureBlocking(false);
    channel.connect(new InetSocketAddress("localhost", 8080));

    // Continue while connecting
    while (!channel.finishConnect()) {
        // Do other work
    }

    // Non-blocking write
    String message = "Non-blocking message";
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
    while (buffer.hasRemaining()) {
        channel.write(buffer);  // May write 0 bytes
    }

    // Non-blocking read
    ByteBuffer response = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(response);

    if (bytesRead == 0) {
        // No data available yet
    } else if (bytesRead == -1) {
        // Connection closed
    } else {
        // Process data
        response.flip();
        processResponse(response);
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Server socket channel
try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
    serverChannel.bind(new InetSocketAddress(8080));

    while (true) {
        // Accept connection (blocking)
        SocketChannel clientChannel = serverChannel.accept();

        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = clientChannel.read(buffer);

            if (bytesRead != -1) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                System.out.println("Received: " + new String(data));

                // Send response
                String response = "Echo: " + new String(data);
                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                clientChannel.write(responseBuffer);
            }

        } finally {
            clientChannel.close();
        }
    }

} catch (IOException e) {
    e.printStackTrace();
}
```

### DatagramChannel

```java
// UDP client
try (DatagramChannel channel = DatagramChannel.open()) {
    channel.bind(null);  // Bind to any available port

    String message = "Hello, UDP Server!";
    InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9999);
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

    channel.send(buffer, serverAddress);

    // Receive response
    ByteBuffer response = ByteBuffer.allocate(1024);
    InetSocketAddress sender = (InetSocketAddress) channel.receive(response);

    response.flip();
    byte[] data = new byte[response.remaining()];
    response.get(data);
    System.out.println("From " + sender + ": " + new String(data));

} catch (IOException e) {
    e.printStackTrace();
}

// UDP server
try (DatagramChannel channel = DatagramChannel.open()) {
    channel.bind(new InetSocketAddress(9999));

    ByteBuffer buffer = ByteBuffer.allocate(1024);

    while (true) {
        buffer.clear();
        InetSocketAddress sender = (InetSocketAddress) channel.receive(buffer);

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println("From " + sender + ": " + new String(data));

        // Send response
        String response = "Echo: " + new String(data);
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        channel.send(responseBuffer, sender);
    }

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## Selectors

### Selector Basics

```java
// Non-blocking server with selector
try (Selector selector = Selector.open();
     ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

    serverChannel.bind(new InetSocketAddress(8080));
    serverChannel.configureBlocking(false);

    // Register server channel with selector
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
        // Wait for events (timeout of 1 second)
        int readyChannels = selector.select(1000);

        if (readyChannels == 0) {
            continue;  // No events
        }

        // Get selected keys
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isAcceptable()) {
                // Accept new connection
                handleAccept(key);
            } else if (key.isReadable()) {
                // Read data
                handleRead(key);
            } else if (key.isWritable()) {
                // Write data
                handleWrite(key);
            }

            keyIterator.remove();
        }
    }

} catch (IOException e) {
    e.printStackTrace();
}

// Handle accept
private static void handleAccept(SelectionKey key) throws IOException {
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverChannel.accept();

    if (clientChannel != null) {
        clientChannel.configureBlocking(false);

        // Register client channel for read
        Selector selector = key.selector();
        clientChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }
}

// Handle read
private static void handleRead(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    int bytesRead = clientChannel.read(buffer);

    if (bytesRead == -1) {
        // Connection closed
        clientChannel.close();
        key.cancel();
    } else if (bytesRead > 0) {
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println("Received: " + new String(data));

        // Register for write
        key.interestOps(SelectionKey.OP_WRITE);
        key.attach(ByteBuffer.wrap(("Echo: " + new String(data)).getBytes()));
    }
}

// Handle write
private static void handleWrite(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = (ByteBuffer) key.attachment();

    clientChannel.write(buffer);

    if (!buffer.hasRemaining()) {
        // All data written
        key.interestOps(SelectionKey.OP_READ);
    }
}
```

### Interest Operations

```java
// Interest ops combinations
int ops = SelectionKey.OP_ACCEPT | SelectionKey.OP_READ;

// Register with multiple interest ops
channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

// Check specific interest ops
SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

if ((key.interestOps() & SelectionKey.OP_READ) != 0) {
    // Interested in read
}

if ((key.readyOps() & SelectionKey.OP_READ) != 0) {
    // Ready for read
}

// Modify interest ops
key.interestOps(SelectionKey.OP_WRITE);

// Add interest ops
key.interestOps(key.interestOps() | SelectionKey.OP_READ);

// Remove interest ops
key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
```

---

## Asynchronous IO

### AsynchronousFileChannel

```java
// Asynchronous file read
try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Paths.get("input.txt"),
    StandardOpenOption.READ)) {

    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Future<Integer> operation = channel.read(buffer, 0);

    // Can do other work here while reading
    System.out.println("Reading in background...");

    // Wait for completion
    int bytesRead = operation.get();

    if (bytesRead != -1) {
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println("Read: " + new String(data));
    }

} catch (IOException | InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Asynchronous file write
try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Paths.get("output.txt"),
    StandardOpenOption.WRITE,
    StandardOpenOption.CREATE)) {

    String content = "Hello, World!";
    ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
    Future<Integer> operation = channel.write(buffer, 0);

    Integer bytesWritten = operation.get();
    System.out.println("Written: " + bytesWritten + " bytes");

} catch (IOException | InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Asynchronous with completion handler
try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Paths.get("input.txt"),
    StandardOpenOption.READ)) {

    ByteBuffer buffer = ByteBuffer.allocate(1024);

    channel.read(buffer, 0, null, new CompletionHandler<Integer, Void>() {
        @Override
        public void completed(Integer bytesRead, Void attachment) {
            System.out.println("Read completed: " + bytesRead + " bytes");
            buffer.flip();
            processBuffer(buffer);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            System.err.println("Read failed: " + exc.getMessage());
        }
    });

    // Can do other work here
    Thread.sleep(1000);  // Wait for demo

} catch (IOException | InterruptedException e) {
    e.printStackTrace();
}

// Large file operations with callback
try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Paths.get("large.txt"),
    StandardOpenOption.READ)) {

    long fileSize = channel.size();
    long position = 0;
    int chunkSize = 1024 * 1024;  // 1MB chunks

    readInChunks(channel, position, chunkSize, fileSize);

} catch (IOException e) {
    e.printStackTrace();
}

private static void readInChunks(AsynchronousFileChannel channel,
                                 long position, int chunkSize, long fileSize) {
    if (position >= fileSize) {
        return;  // Done
    }

    int currentChunkSize = (int) Math.min(chunkSize, fileSize - position);
    ByteBuffer buffer = ByteBuffer.allocate(currentChunkSize);

    channel.read(buffer, position, position, new CompletionHandler<Integer, Long>() {
        @Override
        public void completed(Integer bytesRead, Long pos) {
            if (bytesRead != -1) {
                buffer.flip();
                processChunk(buffer);
                // Read next chunk
                readInChunks(channel, pos + bytesRead, chunkSize, fileSize);
            }
        }

        @Override
        public void failed(Throwable exc, Long pos) {
            System.err.println("Error at position " + pos + ": " + exc.getMessage());
        }
    });
}
```

### AsynchronousSocketChannel

```java
// Asynchronous client
try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {

    // Connect to server
    Void connectResult = client.connect(
        new InetSocketAddress("localhost", 8080)
    ).get();

    // Send data
    String message = "Hello, Server!";
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
    Future<Integer> writeResult = client.write(buffer);
    writeResult.get();

    // Read response
    ByteBuffer response = ByteBuffer.allocate(1024);
    Future<Integer> readResult = client.read(response);
    int bytesRead = readResult.get();

    if (bytesRead != -1) {
        response.flip();
        byte[] data = new byte[response.remaining()];
        response.get(data);
        System.out.println("Response: " + new String(data));
    }

} catch (IOException | InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Asynchronous server
try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
    server.bind(new InetSocketAddress(8080));

    System.out.println("Server started on port 8080");

    server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
        @Override
        public void completed(AsynchronousSocketChannel client, Void attachment) {
            // Accept next connection
            server.accept(null, this);

            // Handle current connection
            handleClient(client);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            System.err.println("Accept failed: " + exc.getMessage());
        }
    });

    // Keep server running
    Thread.sleep(Long.MAX_VALUE);

} catch (IOException | InterruptedException e) {
    e.printStackTrace();
}

private static void handleClient(AsynchronousSocketChannel client) {
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    client.read(buffer, null, new CompletionHandler<Integer, Void>() {
        @Override
        public void completed(Integer bytesRead, Void attachment) {
            if (bytesRead == -1) {
                // Connection closed
                try { client.close(); } catch (IOException e) {}
                return;
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            System.out.println("Received: " + new String(data));

            // Send response
            String response = "Echo: " + new String(data);
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());

            client.write(responseBuffer, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer bytesWritten, Void attachment) {
                    if (!responseBuffer.hasRemaining()) {
                        // Response sent, read next message
                        buffer.clear();
                        client.read(buffer, null, this);
                    } else {
                        // Continue writing
                        client.write(responseBuffer, null, this);
                    }
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    System.err.println("Write failed: " + exc.getMessage());
                }
            });
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            System.err.println("Read failed: " + exc.getMessage());
        }
    });
}
```

---

## File System Operations

### Path Operations

```java
// Creating paths
Path path1 = Paths.get("directory/file.txt");
Path path2 = Path.of("directory", "file.txt");  // Java 11+
Path path3 = Paths.get("C:\\Users\\user\\file.txt");
Path path4 = Paths.get("/home/user/file.txt");

// Absolute and relative paths
Path absolutePath = path1.toAbsolutePath();
Path relativePath = path1.relativize(path2);
Path normalizedPath = path1.normalize();

// Path components
Path path = Paths.get("/home/user/documents/file.txt");
Path parent = path.getParent();        // /home/user/documents
Path fileName = path.getFileName();    // file.txt
Path root = path.getRoot();            // /

int nameCount = path.getNameCount();   // 4
Path name2 = path.getName(2);          // documents

// Path operations
Path resolved = path.resolve("subdir/file.txt");
Path subpath = path.subpath(0, 2);     // /home/user

// Path comparison
Path p1 = Paths.get("file.txt");
Path p2 = Paths.get("./file.txt");
boolean equals = p1.equals(p2);       // false (different representations)
boolean sameFile = Files.isSameFile(p1, p2);  // true (same file)

// Path starts with/ends with
boolean startsWith = path.startsWith("/home");
boolean endsWith = path.endsWith("file.txt");

// Path conversion
String pathString = path.toString();
File file = path.toFile();
URI uri = path.toUri();
```

### Files Operations

```java
// Check file properties
Path path = Paths.get("file.txt");
boolean exists = Files.exists(path);
boolean notExists = Files.notExists(path);
boolean isRegularFile = Files.isRegularFile(path);
boolean isDirectory = Files.isDirectory(path);
boolean isReadable = Files.isReadable(path);
boolean isWritable = Files.isWritable(path);
boolean isExecutable = Files.isExecutable(path);
boolean isHidden = Files.isHidden(path);

// File attributes
long size = Files.size(path);
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
FileTime lastModifiedTime = attrs.lastModifiedTime();
FileTime lastAccessTime = attrs.lastAccessTime();
FileTime creationTime = attrs.creationTime();
boolean isDirectory = attrs.isDirectory();
boolean isRegularFile = attrs.isRegularFile();
boolean isSymbolicLink = attrs.isSymbolicLink();
long size = attrs.size();

// Copy file
Path source = Paths.get("source.txt");
Path target = Paths.get("target.txt");

// Copy with options
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.copy(source, target,
    StandardCopyOption.COPY_ATTRIBUTES,
    StandardCopyOption.REPLACE_EXISTING);

// Move file
Path oldPath = Paths.get("old.txt");
Path newPath = Paths.get("new.txt");

Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

// Delete file
Path fileToDelete = Paths.get("delete.txt");
Files.delete(fileToDelete);  // Throws if file doesn't exist
boolean deleted = Files.deleteIfExists(fileToDelete);  // Returns boolean

// Create directories
Path dir = Paths.get("newdir");
Files.createDirectory(dir);  // Create single directory
Files.createDirectories(dir.resolve("sub1/sub2"));  // Create parent directories

// Create file
Path newFile = Paths.get("newfile.txt");
Files.createFile(newFile);

// Create symbolic link
Path link = Paths.get("link.txt");
Path target = Paths.get("target.txt");
Files.createSymbolicLink(link, target);

// Create hard link
Files.createLink(link, target);

// Read/write small files
Path file = Paths.get("small.txt");

// Read all bytes
byte[] bytes = Files.readAllBytes(file);
List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

// Write all bytes
String content = "Hello, World!";
Files.write(file, content.getBytes());
Files.write(file, content.getBytes(), StandardOpenOption.APPEND);

// Write all lines
List<String> lineList = Arrays.asList("Line 1", "Line 2", "Line 3");
Files.write(file, lineList, StandardCharsets.UTF_8);

// Buffered read/write
try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

try (BufferedWriter writer = Files.newBufferedWriter(
    file, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {

    writer.write("New line");
}

// File permissions
Path filePath = Paths.get("file.txt");
Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(filePath);
Files.setPosixFilePermissions(filePath, permissions);

// Add permission
permissions.add(PosixFilePermission.OWNER_EXECUTE);
Files.setPosixFilePermissions(filePath, permissions);

// File owner
UserPrincipal owner = Files.getOwner(filePath);
UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
UserPrincipal newOwner = lookupService.lookupPrincipalByName("newuser");
Files.setOwner(filePath, newOwner);

// File store
FileStore store = Files.getFileStore(filePath);
long totalSpace = store.getTotalSpace();
long usableSpace = store.getUsableSpace();
long unallocatedSpace = store.getUnallocatedSpace();
String type = store.type();
```

### Directory Operations

```java
// List directory contents
Path dir = Paths.get(".");
try (Stream<Path> stream = Files.list(dir)) {
    stream.forEach(System.out::println);
}

// Walk directory tree (recursive)
try (Stream<Path> stream = Files.walk(dir)) {
    stream.filter(Files::isRegularFile)
          .forEach(System.out::println);
}

// Walk with depth limit
try (Stream<Path> stream = Files.walk(dir, 2)) {
    stream.forEach(System.out::println);
}

// Find files
try (Stream<Path> stream = Files.find(
    dir,
    Integer.MAX_VALUE,
    (path, attrs) -> path.toString().endsWith(".txt"))) {

    stream.forEach(System.out::println);
}

// NewDirectoryStream
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
    for (Path entry : stream) {
        System.out.println(entry.getFileName());
    }
}

// Glob pattern matching
try (DirectoryStream<Path> stream = Files.newDirectoryStream(
    dir, "*.txt")) {

    for (Path entry : stream) {
        System.out.println(entry.getFileName());
    }
}

// Create directory
Path newDir = Paths.get("newdir");
Files.createDirectory(newDir);

// Create directories
Path nestedDir = Paths.get("newdir/sub1/sub2");
Files.createDirectories(nestedDir);

// Delete directory (must be empty)
Files.deleteDirectory(newDir);

// Delete directory tree (recursive)
Files.walkFileTree(nestedDir, new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
});
```

### File Watching

```java
// Watch service for directory changes
try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
    Path dir = Paths.get(".");
    dir.register(watcher,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_DELETE,
        StandardWatchEventKinds.ENTRY_MODIFY);

    while (true) {
        WatchKey key = watcher.take();

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }

            Path filename = (Path) event.context();
            Path fullPath = dir.resolve(filename);

            System.out.printf("%s: %s%n",
                kind.name(), fullPath);

            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                System.out.println("File created: " + fullPath);
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                System.out.println("File deleted: " + fullPath);
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                System.out.println("File modified: " + fullPath);
            }
        }

        boolean valid = key.reset();
        if (!valid) {
            break;
        }
    }

} catch (IOException | InterruptedException e) {
    e.printStackTrace();
}
```

---

## Best Practices

### Resource Management

```java
// DO: Use try-with-resources
try (InputStream in = new FileInputStream("input.txt");
     OutputStream out = new FileOutputStream("output.txt")) {

    // Copy file
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
    }

} catch (IOException e) {
    e.printStackTrace();
}

// DON'T: Manual resource management
InputStream in = null;
try {
    in = new FileInputStream("input.txt");
    // ... process ...
} finally {
    if (in != null) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Buffer Selection

```java
// For small files (< 1MB)
byte[] data = Files.readAllBytes(Paths.get("small.txt"));

// For medium files (1MB - 100MB)
try (InputStream in = new BufferedInputStream(
    new FileInputStream("medium.txt"), 8192)) {

    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        process(buffer, bytesRead);
    }
}

// For large files (> 100MB)
try (FileChannel channel = FileChannel.open(
    Paths.get("large.txt"),
    StandardOpenOption.READ)) {

    MappedByteBuffer mapped = channel.map(
        FileChannel.MapMode.READ_ONLY, 0, channel.size());

    // Process mapped buffer

} catch (IOException e) {
    e.printStackTrace();
}
```

### Encoding

```java
// DO: Always specify encoding
try (BufferedReader reader = new BufferedReader(new InputStreamReader(
    new FileInputStream("input.txt"), StandardCharsets.UTF_8))) {

    // Read file

} catch (IOException e) {
    e.printStackTrace();
}

// DON'T: Use default encoding
try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
    // Uses platform default encoding - not portable
} catch (IOException e) {
    e.printStackTrace();
}
```

### Error Handling

```java
// Handle specific exceptions
try {
    Files.readAllBytes(Paths.get("file.txt"));
} catch (NoSuchFileException e) {
    System.err.println("File not found");
} catch (AccessDeniedException e) {
    System.err.println("Access denied");
} catch (IOException e) {
    System.err.println("IO error: " + e.getMessage());
}

// Check before operation
Path path = Paths.get("file.txt");
if (Files.exists(path) && Files.isReadable(path)) {
    // Safe to read
}
```

### Performance Tips

```java
// Use buffered I/O for most operations
try (BufferedInputStream in = new BufferedInputStream(
    new FileInputStream("file.txt"), 8192);
     BufferedOutputStream out = new BufferedOutputStream(
    new FileOutputStream("output.txt"), 8192)) {

    // Fast copy

} catch (IOException e) {
    e.printStackTrace();
}

// Use NIO for large file operations
try (FileChannel in = FileChannel.open(
    Paths.get("source.txt"), StandardOpenOption.READ);
     FileChannel out = FileChannel.open(
    Paths.get("dest.txt"), StandardOpenOption.WRITE,
    StandardOpenOption.CREATE)) {

    // Fast transfer
    in.transferTo(0, in.size(), out);

} catch (IOException e) {
    e.printStackTrace();
}

// Use memory-mapped files for random access
try (FileChannel channel = FileChannel.open(
    Paths.get("data.dat"),
    StandardOpenOption.READ,
    StandardOpenOption.WRITE)) {

    MappedByteBuffer mapped = channel.map(
        FileChannel.MapMode.READ_WRITE, 0, channel.size());

    // Random access to file content
    mapped.put(0, (byte) 65);  // Write at position 0
    byte b = mapped.get(0);   // Read from position 0

} catch (IOException e) {
    e.printStackTrace();
}
```

---

## Summary

This reference covers Java IO and NIO fundamentals:

- **Traditional IO**: Stream-based input/output operations
- **File IO**: Reading and writing files
- **Stream Classes**: Buffered, Data, Object streams
- **Reader/Writer Classes**: Character-based operations
- **Buffering**: Improving I/O performance
- **NIO Overview**: Channel-based operations
- **Buffers**: Efficient data containers
- **Channels**: File, Socket, Datagram channels
- **Selectors**: Non-blocking multiplexed I/O
- **Asynchronous IO**: Non-blocking I/O with callbacks
- **File System Operations**: Path, Files, directory operations
- **Best Practices**: Resource management, encoding, performance

Java IO provides both traditional stream-based I/O and modern NIO for handling input/output operations efficiently, with support for both blocking and non-blocking operations.
