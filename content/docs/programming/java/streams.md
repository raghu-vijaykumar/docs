+++
title= "Java Streams"
tags = [ "java", "streams" ]
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
weight= 7
bookFlatSection= true
+++

# Java Streams

Java Streams, introduced in Java 8, provide a modern way to process sequences of elements (e.g., collections) in a functional programming style. They enable more readable and concise code for operations such as filtering, mapping, and reducing.

## Key Concepts
- **Stream**: Represents a sequence of elements supporting sequential and parallel aggregate operations. Streams do not store data; they are used to perform operations on data.
- **Pipeline**: A series of transformations (operations) on a stream. A pipeline consists of a source, zero or more intermediate operations, and a terminal operation.
- **Intermediate Operations**: Operations that transform a stream into another stream. They are lazy and do not execute until a terminal operation is invoked. Examples include `filter()`, `map()`, and `sorted()`.
- **Terminal Operations**: Operations that produce a result or a side-effect and terminate the stream pipeline. Examples include `collect()`, `forEach()`, `reduce()`, and `count()`.

## Creating Streams
Streams can be created from various data sources:

From Collections:

```java
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();
```
From Arrays:

```java
String[] array = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(array);
```
From Static Methods:

```java
Stream<String> stream = Stream.of("a", "b", "c");
```
From Files:

```java
try (Stream<String> lines = Files.lines(Paths.get("file.txt"))) {
    lines.forEach(System.out::println);
} catch (IOException e) {
    e.printStackTrace();
}
```

## Intermediate Operations

`filter(Predicate<? super T> predicate)`: Filters elements based on a condition.

```java
Stream<String> filtered = stream.filter(s -> s.startsWith("a"));
```

`map(Function<? super T, ? extends R> mapper)`: Transforms each element.

```java
Stream<Integer> lengths = stream.map(String::length);
```

`sorted(Comparator<? super T> comparator)`: Sorts elements.

```java
Stream<String> sorted = stream.sorted();
```

`distinct()`: Removes duplicate elements.

```java
Stream<String> distinct = stream.distinct();
```

`limit(long maxSize)`: Limits the number of elements.

```java
Stream<String> limited = stream.limit(2);
```

`skip(long n)`: Skips the first n elements.

```java
Stream<String> skipped = stream.skip(2);
```

## Terminal Operations

`forEach(Consumer<? super T> action)`: Performs an action for each element.

```java
stream.forEach(System.out::println);
```

`collect(Collector<? super T, A, R> collector)`: Collects elements into a collection or other container.

```java
List<String> list = stream.collect(Collectors.toList());
```

`reduce(T identity, BinaryOperator<T> accumulator)`: Reduces elements to a single result.

```java
String concatenated = stream.reduce("", (a, b) -> a + b);
```

`count()`: Counts the number of elements.

```java
long count = stream.count();
```

`anyMatch(Predicate<? super T> predicate)`: Checks if any elements match a condition.

```java
boolean hasA = stream.anyMatch(s -> s.equals("a"));
```

`allMatch(Predicate<? super T> predicate)`: Checks if all elements match a condition.

```java
boolean allStartWithA = stream.allMatch(s -> s.startsWith("a"));
```

`noneMatch(Predicate<? super T> predicate)`: Checks if no elements match a condition.

```java
boolean noneStartWithB = stream.noneMatch(s -> s.startsWith("b"));
```

`findFirst()`: Finds the first element.

```java
Optional<String> first = stream.findFirst();
```

`findAny()`: Finds any element.

```java
Optional<String> any = stream.findAny();
```

## Parallel Streams

Streams can be processed in parallel using the `parallel()` method, which can improve performance on multi-core processors.

```java
Stream<String> parallelStream = stream.parallel();
```

## Common Pitfalls

- **Streams are Single-Use**: Once a terminal operation is called, a stream cannot be reused.
- **Avoid Side-Effects**: Intermediate operations should not have side effects to ensure correct behavior.
- **Performance Considerations**: Be mindful of parallel streams and their overhead; use them when operations are computationally expensive.

## Conclusion

Java Streams provide a powerful way to process collections and other sequences of data in a functional and declarative style. By leveraging streams, you can write more readable and maintainable code for data manipulation.