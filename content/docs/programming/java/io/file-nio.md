+++
title= "File NIO"
tags = [ "java", "io", "file-operations", "java.nio" ]
author = "Me"
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight= 1
bookCollapseSection= true
+++

# Java NIO File Operations

Java NIO (New I/O) provides advanced file handling capabilities that differ from the traditional Java I/O (`java.io`). It includes classes for efficient file operations, including non-blocking I/O, memory-mapped files, and asynchronous file operations.

## Reading Files

To read files in Java NIO, use `FileChannel` with a `ByteBuffer`:

```java
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FileReadExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## Writing Files
To write files using Java NIO:

```java
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FileWriteExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("Hello, World!".getBytes());
            buffer.flip();
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## File Position
To manage file positions:

```java
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FilePositionExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            channel.position(10); // Move position to 10 bytes
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## File Mapping
To create a memory-mapped file:

```java
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FileMappingExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## Non-Blocking I/O
To use non-blocking I/O with FileChannel:

```java
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class NonBlockingIOExample {
    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             FileChannel channel = FileChannel.open(Paths.get("example.txt"), StandardOpenOption.READ)) {
            
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            
            // Use selector to handle non-blocking operations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## Asynchronous I/O
To perform asynchronous file operations:

```java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.io.IOException;

public class AsynchronousIOExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> result = channel.read(buffer, 0);
            // Wait for the read operation to complete
            result.get();
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (IOException | InterruptedException | java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```
## Path and Directory Operations
To manage paths and directories:

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class PathOperationsExample {
    public static void main(String[] args) {
        Path path = Paths.get("example.txt");
        
        // Check if file exists
        if (Files.exists(path)) {
            System.out.println("File exists.");
        }

        // Copy file
        Path targetPath = Paths.get("copy_example.txt");
        try {
            Files.copy(path, targetPath);
            System.out.println("File copied.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete file
        try {
            Files.delete(targetPath);
            System.out.println("File deleted.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Java NIO provides a modern approach to file operations with features like non-blocking I/O, memory-mapped files, and asynchronous operations. The FileChannel, ByteBuffer, and Path classes are central to NIO file operations, offering efficient ways to handle files compared to the traditional Java I/O classes.