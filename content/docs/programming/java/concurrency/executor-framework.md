+++
title= "Executor Framework"
tags = [ "java", "concurrency", "executor-framework" ]
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

# Executor Framework

The Executor Framework, introduced in Java 5 as part of the `java.util.concurrent` package, provides a powerful and flexible mechanism for managing threads and tasks. It simplifies concurrent programming by abstracting thread management and offering various utility classes for task scheduling and execution.

## Core Concepts
The Executor Framework provides a high-level API for managing and controlling threads, focusing on the following core concepts:

Executor: The root interface that represents an object capable of executing submitted tasks.
ExecutorService: An extension of the Executor interface that provides methods for managing and controlling the lifecycle of tasks, including shutdown and task scheduling.
ScheduledExecutorService: An extension of ExecutorService that supports scheduling tasks to run after a delay or periodically.
2. Key Interfaces and Classes
a. Executor Interface

The Executor interface defines a single method for executing tasks:

java
Copy code
public interface Executor {
    void execute(Runnable command);
}
Purpose: To submit tasks for execution.
Example Usage: Basic task execution without returning results.
b. ExecutorService Interface

The ExecutorService interface extends Executor and adds methods for managing tasks and their execution:

java
Copy code
public interface ExecutorService extends Executor {
    void shutdown();
    List<Runnable> shutdownNow();
    boolean isShutdown();
    boolean isTerminated();
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
    <T> Future<T> submit(Callable<T> task);
    <T> Future<T> submit(Runnable task, T result);
    Future<?> submit(Runnable task);
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;
    <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException;
}
Purpose: To manage the lifecycle of tasks, including submitting tasks, shutting down the executor, and waiting for tasks to complete.
Example Usage: More advanced task management with support for callable tasks and task results.
c. ScheduledExecutorService Interface

The ScheduledExecutorService interface extends ExecutorService and adds methods for scheduling tasks:

java
Copy code
public interface ScheduledExecutorService extends ExecutorService {
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
Purpose: To schedule tasks with fixed-rate or fixed-delay execution policies.
Example Usage: Tasks that need to be executed periodically or after a specific delay.
3. Common Implementations
a. ThreadPoolExecutor

ThreadPoolExecutor is the core implementation of ExecutorService. It provides a flexible thread pool that can be customized with different parameters for managing threads and tasks.

Key Parameters:

Core Pool Size: The number of core threads to keep in the pool.
Maximum Pool Size: The maximum number of threads allowed in the pool.
Keep Alive Time: The time for which idle threads are kept alive.
Blocking Queue: The queue used to hold tasks before they are executed.
Thread Factory: A factory for creating new threads.
Rejected Execution Handler: A handler for tasks that cannot be executed.
Example:

java
Copy code
import java.util.concurrent.*;

public class ThreadPoolExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(
            2, // core pool size
            4, // maximum pool size
            60, // keep-alive time
            TimeUnit.SECONDS, // time unit for keep-alive time
            new LinkedBlockingQueue<>(), // work queue
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // Simulate task
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
    }
}
b. ScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor is an implementation of ScheduledExecutorService that supports scheduling tasks with fixed-rate or fixed-delay execution policies.

Example:

java
Copy code
import java.util.concurrent.*;

public class ScheduledThreadPoolExecutorExample {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fixed-rate task running at " + System.currentTimeMillis());
        }, 0, 2, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Fixed-delay task running at " + System.currentTimeMillis());
        }, 0, 3, TimeUnit.SECONDS);
    }
}
c. Executors Factory Methods

The Executors class provides factory methods for creating common types of executor services:

newFixedThreadPool(int nThreads): Creates a fixed-size thread pool.
newCachedThreadPool(): Creates a thread pool that creates new threads as needed but reuses previously constructed threads when they are available.
newSingleThreadExecutor(): Creates an executor that uses a single worker thread.
newScheduledThreadPool(int corePoolSize): Creates a thread pool that supports scheduled and periodic execution of tasks.
Example:

java
Copy code
import java.util.concurrent.*;

public class ExecutorsFactoryMethodsExample {
    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            fixedThreadPool.submit(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // Simulate task
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        fixedThreadPool.shutdown();
    }
}
4. Handling Task Results
Tasks submitted to ExecutorService can return results via Future objects:

Future.get(): Blocks until the task completes and returns the result.
Future.cancel(boolean mayInterruptIfRunning): Attempts to cancel the execution of the task.
Future.isDone(): Checks if the task is complete.
Example:

java
Copy code
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(2000); // Simulate long-running task
            return 42;
        });

        try {
            Integer result = future.get(); // Blocks until task completes
            System.out.println("Task result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
Conclusion
The Executor Framework simplifies concurrent programming by abstracting thread management and providing robust tools for task scheduling and execution. Key components include:

Executor: Basic interface for task execution.
ExecutorService: Manages task lifecycle and results.
ScheduledExecutorService: Supports scheduling and periodic tasks.
ThreadPoolExecutor: Flexible and customizable thread pool.
ScheduledThreadPoolExecutor: Scheduled tasks with fixed-rate and fixed-delay.
Executors Factory Methods: Convenient methods for creating common executor types.
By using these abstractions and utilities, you can manage complex concurrency scenarios more effectively and with less boilerplate code.

Beyond the core concepts and implementations of the Executor Framework, there are additional features and utilities that can further enhance concurrency management in Java. Here are some advanced topics and additional utilities within the java.util.concurrent package:

1. CompletableFuture
CompletableFuture is a powerful class introduced in Java 8 that allows for non-blocking asynchronous programming. It provides a way to write asynchronous code in a more readable and maintainable way using a fluent API.

Key Features:

Asynchronous Computations: Chain and compose asynchronous tasks.
Completion: Manually complete futures and handle results or exceptions.
Combining Futures: Combine multiple futures and handle their results.
Example:

java
Copy code
import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 1 is running");
            return "Result from Task 1";
        }).thenApply(result -> {
            System.out.println("Task 2 is running");
            return result + " processed";
        }).thenAccept(result -> {
            System.out.println("Final result: " + result);
        }).exceptionally(ex -> {
            System.err.println("Exception: " + ex.getMessage());
            return null;
        });

        // Keep main thread alive to see the result
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
2. ForkJoinPool
ForkJoinPool is designed for parallel processing of tasks that can be recursively divided into smaller tasks. It is used primarily for tasks that can be divided into sub-tasks (forking) and then combined (joining).

Key Features:

Work Stealing: Threads can "steal" tasks from other threads' queues to improve efficiency.
RecursiveTask and RecursiveAction: Abstract classes for tasks that return a result or do not return a result, respectively.
Example:

```java
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

class FibonacciTask extends RecursiveTask<Integer> {
    private final int n;

    FibonacciTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }
        FibonacciTask f1 = new FibonacciTask(n - 1);
        FibonacciTask f2 = new FibonacciTask(n - 2);
        f1.fork(); // Fork the first task
        return f2.compute() + f1.join(); // Compute the second task and join the result from the first task
    }
}

public class ForkJoinPoolExample {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        FibonacciTask task = new FibonacciTask(10);
        Integer result = pool.invoke(task);
        System.out.println("Fibonacci result: " + result);
    }
}
```