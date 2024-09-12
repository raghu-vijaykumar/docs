+++
title= "Java I/O"
tags = [ "java", "io" ]
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

# Java I/O (`java.io`) vs. Java NIO (`java.nio`)

## Java I/O (`java.io`)

### Overview
- **Introduced**: Java 1.0
- **Focus**: Simplicity and ease of use
- **Approach**: Stream-based I/O

### Key Classes and Interfaces
- **File**: Represents a file or directory.
- **InputStream / OutputStream**: Base classes for byte-based I/O.
- **Reader / Writer**: Base classes for character-based I/O.
- **BufferedReader / BufferedWriter**: Provides buffering for performance.
- **PrintWriter**: Convenience methods for formatted output.

### Characteristics
- **Stream-Based**: Data is read or written sequentially.
- **Blocking I/O**: Operations block until complete.
- **Not Asynchronous**: Primarily synchronous.
- **Simplicity**: Easier for basic I/O tasks.

## Java NIO (`java.nio`)

### Overview
- **Introduced**: Java 1.4
- **Focus**: Performance and scalability
- **Approach**: Buffer-based I/O

### Key Components
- **Path**: Represents a path in the file system.
- **Files**: Static methods for file operations.
- **FileChannel**: Allows for efficient file reading and writing.
- **ByteBuffer / CharBuffer**: Buffers for managing byte and character data.
- **Selector**: Enables multiplexing of I/O operations.

### Characteristics
- **Buffer-Based**: Data is managed in chunks using buffers.
- **Non-Blocking I/O**: Channels can operate in non-blocking mode.
- **Asynchronous I/O**: Supports asynchronous operations with `AsynchronousFileChannel`.
- **Performance**: Higher performance for large-scale or concurrent I/O operations.

# Comparison between Java I/O and Java NIO

| **Aspect**                        | **Java I/O (`java.io`)**                                                          | **Java NIO (`java.nio`)**                                     |
| --------------------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| **Primary Classes**               | `FileInputStream`, `FileOutputStream`, `FileReader`, `FileWriter`                 | `FileChannel`, `FileSystems`, `Path`, `ByteBuffer`            |
| **Stream-Based vs. Buffer-Based** | Stream-based, operates on data one byte or character at a time                    | Buffer-based, operates on data in chunks using buffers        |
| **File Reading**                  | `FileInputStream`, `FileReader`                                                   | `FileChannel.read(ByteBuffer)`                                |
| **File Writing**                  | `FileOutputStream`, `FileWriter`                                                  | `FileChannel.write(ByteBuffer)`                               |
| **File Position**                 | `FileInputStream`, `FileOutputStream` manage position implicitly                  | `FileChannel.position(long newPosition)`                      |
| **File Size**                     | Not directly accessible                                                           | `FileChannel.size()`                                          |
| **File Mapping**                  | Not supported                                                                     | `FileChannel.map(MapMode mode, long position, long size)`     |
| **Non-Blocking I/O**              | Not supported                                                                     | `FileChannel` with `Selector` for non-blocking I/O            |
| **Memory-Mapped Files**           | Not supported                                                                     | `FileChannel.map()`                                           |
| **Asynchronous I/O**              | Not supported                                                                     | `AsynchronousFileChannel` for asynchronous operations         |
| **Buffering**                     | `BufferedInputStream`, `BufferedOutputStream`, `BufferedReader`, `BufferedWriter` | `ByteBuffer` for buffering                                    |
| **Error Handling**                | Traditional exception handling                                                    | More advanced exception handling with channels and buffers    |
| **Path Management**               | `File` for file path management                                                   | `Path` for file path management, `Files` utility class        |
| **Directory Operations**          | `File` methods for directory management                                           | `Files` utility class for directory operations                |
| **File Copying**                  | `FileInputStream` with `FileOutputStream`                                         | `Files.copy(Path source, Path target, CopyOption... options)` |
| **File Deletion**                 | `File.delete()`                                                                   | `Files.delete(Path path)`                                     |

## Use Cases

- **`java.io`**: Best for simple file operations where ease of use is preferred.
- **`java.nio`**: Ideal for high-performance applications, large files, and non-blocking I/O needs.
