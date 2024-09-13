+++
title= "Lambda Expressions"
tags = [ "java", "lambda" ]
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

# Lambda Expressions in Java

Lambda expressions, introduced in Java 8, provide a clear and concise way to represent a method interface using an expression. They enable functional programming in Java, allowing the creation of anonymous functions that can be treated as a first-class citizen in the language.

## Syntax of Lambda Expressions
A lambda expression has the following syntax:

```java
(parameters) -> expression
or
(parameters) -> { statements; }
```

- **Parameters**: Optional; if a method takes no parameters, an empty `()` is used.
- **Arrow** (`->`): Separates parameters from the body.
- **Body**: Contains the expression or statements. Single expressions don't need braces, while multiple statements must be enclosed within `{}`.
Examples:
A simple lambda with no parameters:

```java
() -> System.out.println("Hello World");
```
A lambda with one parameter:

```java
(x) -> System.out.println(x);
```
A lambda with multiple parameters:

```java
(a, b) -> a + b;
```
A lambda with multiple statements:

```java
(x, y) -> {
    int sum = x + y;
    System.out.println("Sum: " + sum);
};
```

## Functional Interfaces
Lambda expressions can be used only in the context of functional interfaces. A functional interface is an interface that has exactly one abstract method. These interfaces may contain multiple default or static methods but must contain only one abstract method to qualify as a functional interface.

The annotation `@FunctionalInterface` is used to indicate that an interface is functional.

```java
@FunctionalInterface
interface MyFunctionalInterface {
    void performTask();
}
```
The `java.util.function` package contains many built-in functional interfaces such as `Predicate`, `Function`, `Consumer`, and `Supplier`.

## How Lambda Expressions Work
Lambda expressions enable you to pass behavior as a parameter to methods, or treat code as data. Instead of creating an anonymous inner class, you can use a lambda expression.

Example without Lambda:
```java
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Running in thread");
    }
}).start();
```
Example with Lambda:
```java
new Thread(() -> System.out.println("Running in thread")).start();
```

## Using Lambda Expressions with Common Functional Interfaces
`Predicate<T>`: Represents a boolean-valued function of one argument.

```java
Predicate<Integer> isEven = (x) -> x % 2 == 0;
System.out.println(isEven.test(4));  // true
```
`Function<T, R>`: Represents a function that accepts one argument and produces a result.

```java
Function<String, Integer> length = (s) -> s.length();
System.out.println(length.apply("Lambda"));  // 6
```
`Consumer<T>`: Represents an operation that accepts a single input and returns no result.

```java
Consumer<String> print = (s) -> System.out.println(s);
print.accept("Hello, World!");  // Hello, World!
```
`Supplier<T>`: Represents a function that supplies a result without any input.

```java
Supplier<Double> randomValue = () -> Math.random();
System.out.println(randomValue.get());
```

## Capturing Variables in Lambdas
Lambda expressions can capture local variables from the enclosing scope. However, these variables must be either final or effectively final.

Example:
```java
int a = 10;
Runnable r = () -> System.out.println(a);
```
In the above example, the variable a must not be modified after being used in the lambda expression.

## Method References
Method references are a shorthand notation for lambda expressions that invoke an existing method.

**Static method reference:**

```java
Function<Integer, String> converter = String::valueOf;
```
**Instance method reference:**

```java
Consumer<String> printer = System.out::println;
```
**Constructor reference:**

```java
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
```

## Lambda with Streams API
Lambda expressions are widely used with Java Streams for operations like filtering, mapping, and reducing.

Example:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evenNumbers = numbers.stream()
                                   .filter(n -> n % 2 == 0)
                                   .collect(Collectors.toList());
System.out.println(evenNumbers);  // [2, 4]
```

## Advantages of Lambda Expressions
- **Concise Code**: Lambdas reduce boilerplate code and eliminate the need for anonymous inner classes.
- **Readability**: The intent of the code becomes clearer, especially in cases of single-method functionality.
- **Functional Programming**: Enables a functional approach to problem-solving in Java, improving expressiveness.

## Limitations of Lambda Expressions
- **Debugging**: Since lambdas are anonymous functions, debugging can be harder compared to named classes and methods.
- **Code Readability**: Overuse of lambdas can lead to overly concise code that might be difficult to understand for complex operations.

## Conclusion
Lambda expressions revolutionize how developers write code in Java, making it more concise and expressive, especially when combined with functional interfaces and the Streams API. They provide a way to pass behavior, which can result in cleaner and more maintainable code.