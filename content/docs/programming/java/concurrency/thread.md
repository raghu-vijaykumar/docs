+++
title= "Thread"
tags = [ "thread", "java", "concurrency" ]
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

# Thread

A thread is a lightweight process that allows for concurrent execution of tasks. In Java, the `Thread` class and the `Runnable` interface are used to create and manage threads.

**Example**:

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running.");
    }
}

public class ThreadExample {
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start(); // Starts the thread, which invokes the run() method
    }
}
```
**Explanation**:

- MyThread extends Thread and overrides the run() method, which contains the code to be executed by the thread.
- The start() method initiates the thread and calls the run() method.

## Multithreading
Multithreading is the concept of running multiple threads in parallel, which can improve the performance and responsiveness of an application. Threads in Java can be managed using the Thread class or implementing the Runnable interface.

**Example**:

```java
class Task implements Runnable {
    private String taskName;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(taskName + ": " + i);
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class MultithreadingExample {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Task("Task 1"));
        Thread thread2 = new Thread(new Task("Task 2"));

        thread1.start(); // Start the first thread
        thread2.start(); // Start the second thread
    }
}
```

**Explanation**:

- Task implements Runnable, and its run() method contains the code to be executed.
- Two threads are created with different tasks and started simultaneously, running in parallel.

## Thread Lifecycle
The lifecycle of a thread includes several states, each representing a stage in the thread's execution.

**New**: The thread is created but not yet started.

**Example**:

```java
Thread thread = new Thread(() -> System.out.println("Thread in New state"));
```

**Runnable**: The thread is ready to run and waiting for CPU resources.

**Example**:

```java
Thread thread = new Thread(() -> {
    System.out.println("Thread is running.");
});
thread.start(); // Moves the thread to the Runnable state
```

**Blocked/Waiting**: The thread is waiting for resources or another thread.

**Example**:

```java
class BlockedTask implements Runnable {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void run() {
        synchronized (lock1) {
            try {
                Thread.sleep(100); // Simulate some work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock2) {
                // Critical section
            }
        }
    }
}
```

**Explanation**:

- If another thread holds lock2, this thread will be blocked while waiting for the lock.

**Timed Waiting**: The thread is waiting for a specific period.

**Example**:

```java
class TimedWaitingTask implements Runnable {
    public void run() {
        try {
            Thread.sleep(2000); // Timed waiting for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread finished waiting.");
    }
}
```

**Explanation**:

- Thread.sleep(2000) puts the thread in the Timed Waiting state for 2 seconds.

**Terminated**: The thread has completed its execution.

**Example**:

```java
class TerminatedTask implements Runnable {
    public void run() {
        System.out.println("Thread is running.");
    }
}

public class TerminatedStateExample {
    public static void main(String[] args) {
        Thread thread = new Thread(new TerminatedTask());
        thread.start();
        try {
            thread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread has terminated.");
    }
}
```

**Explanation**:

- `thread.join()` waits for the thread to finish executing, transitioning it to the Terminated state.
