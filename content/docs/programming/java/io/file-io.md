+++
title= "File IO"
tags = [ "java", "io", "file-io"]
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
weight= 2
bookCollapseSection= true
+++

# Java I/O File Operations

Java I/O (`java.io`) provides a traditional way to handle file operations, including reading, writing, and manipulating files. It primarily uses streams and readers/writers for handling file data.

## Reading Files

To read files using Java I/O:

### Using `FileInputStream`

```java
import java.io.FileInputStream;
import java.io.IOException;

public class FileInputStreamExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("example.txt")) {
            int data;
            while ((data = fis.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Using `FileReader`

```java
import java.io.FileReader;
import java.io.IOException;

public class FileReaderExample {
    public static void main(String[] args) {
        try (FileReader fr = new FileReader("example.txt")) {
            int data;
            while ((data = fr.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## Writing Files

To write files using Java I/O:

### Using `FileOutputStream`

```java
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOutputStreamExample {
    public static void main(String[] args) {
        try (FileOutputStream fos = new FileOutputStream("example.txt")) {
            fos.write("Hello, World!".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
### Using `FileWriter`

```java
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterExample {
    public static void main(String[] args) {
        try (FileWriter fw = new FileWriter("example.txt")) {
            fw.write("Hello, World!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## File Position
File position is managed implicitly by the streams. For example, FileInputStream and FileOutputStream automatically handle the current position in the file.

**Skipping Bytes**
```java
import java.io.FileInputStream;
import java.io.IOException;

public class FileSkipExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("example.txt")) {
            fis.skip(10); // Skip first 10 bytes
            int data;
            while ((data = fis.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## File Copying

To copy a file using Java I/O:

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("source.txt");
             FileOutputStream fos = new FileOutputStream("target.txt")) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## File Deletion

To delete a file using Java I/O:

```java
import java.io.File;

public class FileDeletionExample {
    public static void main(String[] args) {
        File file = new File("example.txt");
        if (file.delete()) {
            System.out.println("File deleted.");
        } else {
            System.out.println("Failed to delete the file.");
        }
    }
}
```
## Directory Operations

### Creating a Directory

```java
import java.io.File;

public class DirectoryCreationExample {
    public static void main(String[] args) {
        File dir = new File("new_directory");
        if (dir.mkdir()) {
            System.out.println("Directory created.");
        } else {
            System.out.println("Failed to create directory.");
        }
    }
}
```
### Listing Files in a Directory

```java
import java.io.File;

public class DirectoryListingExample {
    public static void main(String[] args) {
        File dir = new File("existing_directory");
        if (dir.isDirectory()) {
            String[] files = dir.list();
            if (files != null) {
                for (String file : files) {
                    System.out.println(file);
                }
            }
        }
    }
}
```
### Deleting a Directory

```java
import java.io.File;

public class DirectoryDeletionExample {
    public static void main(String[] args) {
        File dir = new File("new_directory");
        if (dir.delete()) {
            System.out.println("Directory deleted.");
        } else {
            System.out.println("Failed to delete directory.");
        }
    }
}
```

## Summary

Java I/O provides fundamental methods for handling file operations using streams and readers/writers. While it's simpler and straightforward for basic tasks, it lacks some advanced features like non-blocking I/O and memory-mapped files that are present in Java NIO. The `FileInputStream`, `FileOutputStream`, `FileReader`, and `FileWriter` classes are central to file operations in Java I/O, providing essential functionality for reading, writing, copying, and deleting files.