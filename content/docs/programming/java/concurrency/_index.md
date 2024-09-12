
## Concurrency in Java

Concurrency in Java refers to the ability of a program to execute multiple tasks simultaneously. It involves managing the execution of threads to perform tasks in parallel, improving the efficiency and responsiveness of applications.

### Key Concepts of Concurrency

1. **Thread**: 
   - The smallest unit of a process in Java.
   - Allows multiple tasks to run concurrently.

2. **Multithreading**: 
   - Core concept where multiple threads run in parallel.
   - Each thread executes independently but may share resources.

3. **Thread Lifecycle**:
   - **New**: Thread is created but not yet started.
   - **Runnable**: Thread is ready to run and waiting for CPU resources.
   - **Blocked/Waiting**: Thread is waiting for resources or another thread.
   - **Timed Waiting**: Thread is waiting for a specific time period.
   - **Terminated**: Thread has finished executing.

4. **Synchronization**:
   - Ensures that threads do not interfere with each other when accessing shared resources.
   - Uses the `synchronized` keyword and locks.

5. **Executor Framework**:
   - Part of `java.util.concurrent` package.
   - Manages thread pools, schedules tasks, and handles concurrency more effectively.

6. **Locks**:
   - **ReentrantLock**: A lock that can be acquired multiple times by the same thread.
   - **ReadWriteLock**: Allows multiple threads to read but only one thread to write at a time.
   - **StampedLock**: High-performance locking with support for optimistic reads.
   - **Condition Variables**: Allows threads to wait until a condition is met.
   - **LockSupport**: Utility methods for blocking and unblocking threads.
   - **Semaphore**: Controls access to a limited number of permits.
   - **CountDownLatch**: Allows threads to wait until a set of operations are completed.
   - **CyclicBarrier**: Synchronization point where threads wait for each other to reach a common barrier.


8. **Volatile Keyword**:
   - Ensures that changes made by one thread to a variable are visible to other threads.

9. **Atomic Operations**:
   - Uses atomic classes like `AtomicInteger` for thread-safe operations on shared data.

10. **Deadlock**:
   - Occurs when two or more threads are blocked forever, each waiting for the other to release a resource.

11. **Concurrency Utilities**:
    - **Concurrent Collections**: Thread-safe collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList`.
    - **Exchanger**: Allows threads to exchange objects.
    - **SynchronousQueue**: Simple queue for transferring data between threads.
    - 
    - **CountDownLatch**: Allows threads to wait until a set of operations are completed.
    - **CyclicBarrier**: Synchronization point where threads wait for each other to reach a common barrier.


