---
weight: 1
bookCollapseSection: true
title: "Utilities"
draft: false
---


## Common Utilities

- **Iterator**
  - **Description**: Provides a way to access elements of a collection sequentially without exposing its underlying structure.
  - **Java**: `Iterator` interface and `Iterable` interface
  - **Python**: `iter()` and iterator objects

- **Comparable/Comparator**
  - **Description**: Used to define natural ordering or custom sorting for objects.
  - **Java**: `Comparable` interface, `Comparator` interface
  - **Python**: `__lt__`, `__le__`, `__gt__`, `__ge__`

- **ListIterator**
  - **Description**: Allows bidirectional iteration and modification of a list.
  - **Java**: `ListIterator` interface (extends `Iterator`)
  - **Python**: No direct equivalent

- **Stream**
  - **Description**: Provides functional-style operations on collections of elements.
  - **Java**: `Stream` API (Java 8+)
  - **Python**: `itertools`, list comprehensions, generator expressions

- **Spliterator**
  - **Description**: Allows parallel processing of data sources.
  - **Java**: `Spliterator` (for traversing and splitting elements)
  - **Python**: No direct equivalent

- **Concurrent Collections**
  - **Description**: Provides thread-safe collections for concurrent programming.
  - **Java**: `ConcurrentHashMap`, `CopyOnWriteArrayList`, etc.
  - **Python**: `concurrent.futures`, `queue.Queue`

- **Future/CompletableFuture**
  - **Description**: Represents a result of an asynchronous computation.
  - **Java**: `Future`, `CompletableFuture` (asynchronous computations)
  - **Python**: `concurrent.futures.Future`, `asyncio.Future`

- **ForkJoinPool**
  - **Description**: Manages and works with parallel tasks.
  - **Java**: `ForkJoinPool` (parallelism for recursive tasks)
  - **Python**: `multiprocessing.Pool`, `concurrent.futures.ProcessPoolExecutor`

- **FileLock**
  - **Description**: Provides a mechanism to lock files for concurrent access.
  - **Java**: `FileLock` (locking files)
  - **Python**: `fcntl`, `portalocker` (3rd party)

- **Atomic Classes**
  - **Description**: Provides atomic operations on single variables to ensure thread safety.
  - **Java**: `AtomicInteger`, `AtomicBoolean`, etc. (atomic operations)
  - **Python**: `multiprocessing.Value`, `threading.Atomic` (3rd party)

- **ThreadLocal**
  - **Description**: Provides thread-local storage, where each thread has its own value.
  - **Java**: `ThreadLocal` (thread-local variables)
  - **Python**: `threading.local()`

- **ReentrantReadWriteLock**
  - **Description**: Lock that allows multiple readers or a single writer.
  - **Java**: `ReentrantReadWriteLock` (separate read/write locks)
  - **Python**: `threading.RLock()`

- **Scanner**
  - **Description**: Utility for parsing input and reading from various sources.
  - **Java**: `Scanner` (simple text input parsing)
  - **Python**: `input()` (basic), `argparse` (CLI parsing)

- **NIO Channels**
  - **Description**: Provides non-blocking I/O operations and efficient data transfer.
  - **Java**: `FileChannel`, `SocketChannel`, `ServerSocketChannel`
  - **Python**: `io` module (`io.BufferedReader`, `io.StringIO`)

- **Semaphore**
  - **Description**: Controls access to a shared resource by multiple threads.
  - **Java**: `Semaphore` (used for controlling access to resources)
  - **Python**: `threading.Semaphore`

- **Lambda**
  - **Description**: Provides a way to create small anonymous functions inline.
  - **Java**: `Lambda` expressions (inline anonymous functions)
  - **Python**: `lambda` expressions

- **Optional**
  - **Description**: Provides a way to avoid `null` references and handle the absence of values more gracefully.
  - **Java**: `Optional` (handling absent values)
  - **Python**: No direct equivalent
