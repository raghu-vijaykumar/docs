+++
title= "JVM Internals"
tags = [ "jvm", "internals", "programming" ]
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
weight= 8
bookCollapseSection= true
+++

# JVM (Java Virtual Machine) Internals
The Java Virtual Machine (JVM) is the engine that drives Java applications. It provides a runtime environment in which Java bytecode is executed. Understanding the JVM internals is crucial for tuning performance, debugging, and optimizing Java applications. Below is a deep dive into the key components and inner workings of the JVM.

##  JVM Architecture
The JVM consists of three main components:

- ClassLoader Subsystem
- Runtime Data Areas
- Execution Engine

Here’s a high-level architecture diagram of the JVM:

```
  ----------------------------------------
 |            JVM Architecture            |
  ----------------------------------------
 | ClassLoader Subsystem                    |
 | ---------------------------------------- |
 | Runtime Data Areas                       |
 | - Method Area                            |
 | - Heap                                   |
 | - Stack                                  |
 | - Program Counter (PC) Register          |
 | - Native Method Stacks                   |
 | ---------------------------------------- |
 | Execution Engine                         |
 | - Interpreter                            |
 | - Just-In-Time (JIT) Compiler            |
 | - Garbage Collector                      |
  ----------------------------------------
```

### ClassLoader Subsystem
The ClassLoader is responsible for loading class files into memory. It follows a three-step process:

- Loading: Reads `.class` files and stores bytecode in the method area.
- Linking:
  - Verification: Ensures that the bytecode follows JVM specifications.
  - Preparation: Allocates memory for static variables.
  - Resolution: Converts symbolic references (e.g., variable names) into direct references (e.g., memory addresses).
- Initialization: Initializes static variables and executes the static block of the class.

JVM uses three types of class loaders:
- Bootstrap ClassLoader: Loads core Java classes (from `rt.jar`).
- Extension ClassLoader: Loads classes from the Java extensions directory.
- Application ClassLoader: Loads classes from the classpath specified by the user.

### Runtime Data Areas
The JVM has several memory areas allocated during runtime. These areas are crucial for the lifecycle of a Java application.

- Method Area
  - Stores class-level data such as bytecode, static variables, method code, and runtime constant pool.
  - Shared among all threads.
  - Part of the JVM heap.
- Heap
  - The heap is where objects and instance variables are allocated.
  - It is shared among all threads.
  - Divided into:
    - Young Generation: Where new objects are created. It is further divided into:
      - Eden Space: New objects are allocated here.
      - Survivor Spaces (S0 and S1): Objects that survive the first round of garbage collection are moved here.
    - Old Generation (Tenured Space): Objects that have survived multiple garbage collections are moved here.
- Stack
  - Each thread has its own stack.
  - Stores frames that contain local variables, operand stacks, and frame data.
  - Frames: Created when a method is invoked and destroyed when the method exits.
- Program Counter (PC) Register
  - Each thread has its own PC register.
  - It holds the address of the JVM instruction currently being executed.
- Native Method Stacks
  - Native method stacks are used for executing native (non-Java) methods using languages like C or C++.
  - Each thread has its own native method stack.

### Execution Engine
The Execution Engine is responsible for executing the bytecode loaded by the ClassLoader. It has several key components:

- Interpreter
  - The interpreter executes Java bytecode line by line.
  - It's simple but slower than compiled execution since it interprets the same instructions repeatedly.
- Just-In-Time (JIT) Compiler
  - Converts frequently executed bytecode into native machine code to improve performance.
  - It uses hotspot detection to compile only the “hot” methods (frequently executed methods) into machine code.
- Adaptive Optimization
  - JIT dynamically optimizes the code based on runtime profiling.

### Garbage Collector (GC)
Garbage Collection is the process of automatically reclaiming memory by removing unused objects from the heap.
Different GC algorithms:
- **Serial GC**: Single-threaded, suitable for small applications.
- **Parallel GC**: Multiple threads are used for GC, suited for multi-core systems.
- **CMS (Concurrent Mark-Sweep) GC**: A low-latency collector that minimizes GC pauses.
- **G1 (Garbage-First) GC**: Aimed at both latency-sensitive and large heap applications.
Garbage Collection Phases:
- **Mark**: Identifies which objects are still in use.
- **Sweep**: Removes objects that are no longer needed.
- **Compact**: Reorganizes memory to reduce fragmentation.

### JVM Execution Model
When you execute a Java program, the JVM follows these steps:

- **Compilation**: The Java source code is compiled into bytecode by the Java compiler (javac), which produces .class files.
- **Class Loading**: The ClassLoader subsystem loads the class files and puts them into the method area.
- **Bytecode Execution**: The Execution Engine (Interpreter or JIT) executes the bytecode.
- **Garbage Collection**: The GC reclaims memory by cleaning up unused objects from the heap.

### JVM Optimization Techniques
To improve JVM performance, several optimizations are made:

- **Inlining**: The JIT compiler can inline frequently called methods to avoid the overhead of method invocation.
- **Escape Analysis**: Determines if an object can be allocated on the stack instead of the heap.
- **Dead Code Elimination**: Unreachable or unnecessary code is eliminated during optimization.
- **Tiered Compilation**: Combines interpreted execution with JIT compilation to balance start-up time and peak performance.

### JVM Parameters and Tuning
JVM provides a set of parameters to control its behavior and optimize performance:

- **Heap Size**:
  - `-Xms`: Sets the initial heap size.
  - `-Xmx`: Sets the maximum heap size.
- **Garbage Collector**:
  - `-XX:+UseG1GC`: Uses the G1 Garbage Collector.
  - `-XX:+UseConcMarkSweepGC`: Uses the CMS Garbage Collector.
- **JIT Compiler**:
  - `-XX:+TieredCompilation`: Enables tiered compilation (mix of interpreted and JIT-compiled code).
  - `-XX:CompileThreshold`: Sets the number of method invocations before it is JIT compiled.

## Conclusion
Understanding the JVM internals is essential for optimizing Java applications. By knowing how the JVM works, you can make informed decisions about memory management, garbage collection, and other performance-related aspects.