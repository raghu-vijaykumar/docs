+++
title= "Synchronization & Locking"
tags = [ "synchronization", "locking", "java", "concurrency" ]
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
bookFlatSection= true
+++

# Synchronization & Locking

## Synchronization
Synchronization in Java is a mechanism that ensures that multiple threads do not interfere with each other when accessing shared resources. It helps to prevent issues like race conditions, where the outcome of a computation depends on the order of thread execution.

### Purpose of Synchronization
When multiple threads access shared resources, such as variables or objects, there's a risk that one thread might change the resource while another thread is using it, leading to inconsistent or corrupt data. Synchronization helps to prevent such scenarios by ensuring that only one thread can access the resource at a time.

### The `synchronized` Keyword
The synchronized keyword in Java can be used to lock a method or a block of code. It ensures that only one thread can execute the synchronized block or method at a time.

Example of Synchronizing a Method:
```java
class Counter {
    private int count = 0;

    // Synchronized method
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}

public class SynchronizedMethodExample {
    public static void main(String[] args) {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final count: " + counter.getCount());
    }
}
```

**Explanation**:

- The `increment()` method is synchronized, ensuring that only one thread can execute it at a time.
- The Counter class safely increments the count from multiple threads.

## Synchronized Blocks
Synchronized blocks are used to lock a specific part of code instead of the entire method. This provides finer control over synchronization.

Example of Synchronized Block:

```java
public void increment() {
    synchronized (lock) {
        count++;
    }
    }
```

**Explanation**:

The increment() method contains a synchronized block that locks on the lock object.
This approach limits the scope of synchronization, potentially improving performance if other methods of the class do not need synchronization.

## Locks
In Java, locks are used to control access to shared resources and provide mechanisms for synchronizing thread execution. The java.util.concurrent.locks package offers advanced locking constructs that provide more flexibility than the traditional synchronized keyword. Two key lock types are ReentrantLock and ReadWriteLock.

### ReentrantLock
ReentrantLock is a type of lock that allows the same thread to acquire the lock multiple times without causing a deadlock. This feature is useful in scenarios where a thread might need to acquire the lock in nested method calls or when the lock is held by multiple parts of code.

**Key Features**:

- **Reentrant**: A thread that holds the lock can reacquire it without blocking itself.
- **Fairness**: Can be configured to be fair, meaning that threads acquire the lock in the order they requested it.
- **Interruptible Lock Acquisition**: Threads can be interrupted while waiting for the lock.
- **Try-Lock**: Allows threads to attempt to acquire the lock without blocking indefinitely.

**Example**:

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock(); // ReentrantLock object

    public void increment() {
        lock.lock(); // Acquire the lock
        try {
            count++;
        } finally {
            lock.unlock(); // Ensure the lock is released
        }
    }

    public int getCount() {
        return count;
    }
}

public class ReentrantLockExample {
    public static void main(String[] args) {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final count: " + counter.getCount());
    }
}
```

**Explanation**:

- `lock.lock()` acquires the lock, allowing only one thread to execute the critical section at a time.
- `lock.unlock()` releases the lock, allowing other threads to acquire it.
- The try-finally block ensures the lock is released even if an exception occurs.

### ReadWriteLock
ReadWriteLock is designed to allow multiple threads to read a shared resource concurrently while ensuring that only one thread can write to it at a time. This is useful for scenarios where read operations are more frequent than write operations, improving performance by reducing contention.

**Key Features**:

- **Read Lock**: Allows multiple threads to acquire the lock simultaneously for reading.
- **Write Lock**: Allows only one thread to acquire the lock for writing, blocking other threads from reading or writing.
- **Fairness**: Can be configured to be fair, ensuring threads acquire the locks in the order they requested.

**Example**:

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SharedResource {
    private String data = "Initial Data";
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void writeData(String newData) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            data = newData;
            System.out.println("Data written: " + newData);
        } finally {
            writeLock.unlock();
        }
    }

    public String readData() {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            System.out.println("Data read: " + data);
            return data;
        } finally {
            readLock.unlock();
        }
    }
}

public class ReadWriteLockExample {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        Runnable readTask = () -> {
            for (int i = 0; i < 10; i++) {
                resource.readData();
                try {
                    Thread.sleep(100); // Simulate time between reads
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable writeTask = () -> {
            for (int i = 0; i < 5; i++) {
                resource.writeData("New Data " + i);
                try {
                    Thread.sleep(200); // Simulate time between writes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread reader1 = new Thread(readTask);
        Thread reader2 = new Thread(readTask);
        Thread writer = new Thread(writeTask);

        reader1.start();
        reader2.start();
        writer.start();

        try {
            reader1.join();
            reader2.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

**Explanation**:

- `ReadWriteLock` provides `readLock()` and `writeLock()` methods to acquire read and write locks, respectively.
- Multiple threads can read concurrently, but write operations are exclusive and block all readers and other writers.
- The try-finally blocks ensure that locks are properly released.

### StampedLock
StampedLock is a lock introduced in Java 8 that provides a combination of read, write, and optimistic read locking. It is designed to offer higher performance for read-heavy workloads by supporting optimistic reads that don’t block.

**Key Features**:

- **Optimistic Read Lock**: Allows threads to perform read operations without blocking, assuming that no write locks are active.
- **Write Lock**: Provides exclusive access for writing.
- **Read Lock**: Allows multiple threads to read concurrently.

**Example**:

```java
import java.util.concurrent.locks.StampedLock;

class SharedResource {
    private String data = "Initial Data";
    private final StampedLock lock = new StampedLock();

    public void writeData(String newData) {
        long stamp = lock.writeLock();
        try {
            data = newData;
            System.out.println("Data written: " + newData);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public String readData() {
        long stamp = lock.tryOptimisticRead();
        String currentData = data;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentData = data;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        System.out.println("Data read: " + currentData);
        return currentData;
    }
}

public class StampedLockExample {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        Runnable readTask = () -> {
            for (int i = 0; i < 10; i++) {
                resource.readData();
                try {
                    Thread.sleep(100); // Simulate time between reads
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable writeTask = () -> {
            for (int i = 0; i < 5; i++) {
                resource.writeData("New Data " + i);
                try {
                    Thread.sleep(200); // Simulate time between writes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread reader1 = new Thread(readTask);
        Thread reader2 = new Thread(readTask);
        Thread writer = new Thread(writeTask);

        reader1.start();
        reader2.start();
        writer.start();

        try {
            reader1.join();
            reader2.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### Condition Variables
Condition objects are used with ReentrantLock to provide more advanced thread coordination. They allow threads to wait for specific conditions to become true and to be signaled when conditions change.

**Key Features**:

- **Await**: Blocks the current thread until signaled.
- **Signal**: Wakes up one or all waiting threads.
- **SignalAll**: Wakes up all waiting threads.

**Example**:

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer {
    private final int[] buffer;
    private int count, putIndex, takeIndex;
    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public BoundedBuffer(int capacity) {
        buffer = new int[capacity];
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            while (count == buffer.length) {
                notFull.await();
            }
            buffer[putIndex] = value;
            if (++putIndex == buffer.length) putIndex = 0;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            int value = buffer[takeIndex];
            if (++takeIndex == buffer.length) takeIndex = 0;
            count--;
            notFull.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }
}

public class ConditionVariableExample {
    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer(10);

        Runnable producer = () -> {
            try {
                for (int i = 0; i < 100; i++) {
                    buffer.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable consumer = () -> {
            try {
                for (int i = 0; i < 100; i++) {
                    int value = buffer.take();
                    System.out.println("Consumed: " + value);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}
```
### Lock Support Utility Methods
The LockSupport class provides static utility methods to block and unblock threads in a more flexible manner compared to traditional wait() and notify() methods. It is used in conjunction with various synchronization utilities.

**Key Features**:

- **Park**: Blocks the current thread.
- **Unpark**: Unblocks a blocked thread.

**Example**:

```java
import java.util.concurrent.locks.LockSupport;

class LockSupportExample {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Thread waiting...");
            LockSupport.park(); // Block the thread
            System.out.println("Thread resumed!");
        });

        thread.start();

        try {
            Thread.sleep(2000); // Allow time for the thread to block
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Unblocking the thread...");
        LockSupport.unpark(thread); // Unblock the thread
    }
}
```

### Semaphore
A Semaphore controls access to a shared resource by maintaining a set of permits. Threads acquire permits before accessing the resource and release them when done.

**Key Features**:

- **Acquire**: Blocks until a permit is available.
- **Release**: Releases a permit, potentially unblocking other threads.

**Example**:

```java
import java.util.concurrent.Semaphore;

class SemaphoreExample {
    private static final Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) {
        Runnable task = () -> {
            try {
                semaphore.acquire(); // Acquire a permit
                System.out.println(Thread.currentThread().getName() + " acquired the permit.");
                Thread.sleep(2000); // Simulate work
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); // Release the permit
                System.out.println(Thread.currentThread().getName() + " released the permit.");
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();
    }
}
```

## Conclusion
Java offers a variety of locking and synchronization mechanisms to handle different concurrency scenarios. Each type of lock or synchronization aid provides specific features and advantages:

- **ReentrantLock**: Flexible, reentrant locking with additional features.
- **ReadWriteLock**: Optimized for scenarios with frequent reads and infrequent writes.
- **StampedLock**: High-performance locking with support for optimistic reads.
- **Condition Variables**: Advanced thread coordination with ReentrantLock.
- **LockSupport**: Utility methods for blocking and unblocking threads.
- **Semaphore**: Controls access to a limited number of permits.