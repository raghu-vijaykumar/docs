+++
title= "Generics"
tags = [ "java", "generics" ]
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

# Java Generics

**Java Generics** allow you to create classes, interfaces, and methods that operate on types specified by the user, while maintaining type safety. Generics enable you to write flexible, reusable code without the need to cast objects explicitly. Generics in Java let you parameterize types in your code, making it type-safe at compile-time. This means that errors can be caught during compilation rather than at runtime, which reduces bugs and increases readability.

**Example:**
```java
List<String> stringList = new ArrayList<>();
stringList.add("Hello");
// stringList.add(123);  // Compilation error: incompatible types
```

Without generics, we would need to manually cast objects, which can lead to runtime exceptions:

```java
List list = new ArrayList();
list.add("Hello");
String s = (String) list.get(0); // Cast needed
```

## Generic Classes
A generic class defines one or more type parameters. The type parameter is specified within angle brackets (<>) and can be used to define the class's fields, methods, and constructors.

**Syntax:**
```java
class Box<T> {
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
```

**Example of using the generic class:**
```java
Box<String> stringBox = new Box<>("Hello");
System.out.println(stringBox.getValue());  // Output: Hello

Box<Integer> intBox = new Box<>(123);
System.out.println(intBox.getValue());  // Output: 123
```
Here, `T` acts as a placeholder for the type of data. When you instantiate `Box<String>`, `T` is replaced with `String`.

## Generic Methods
A generic method allows you to parameterize a method within a class, even if the class itself is not generic.

**Syntax:**
```java
public class GenericMethodExample {
    public <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
}

**Example:**
```java
GenericMethodExample example = new GenericMethodExample();
String[] stringArray = {"A", "B", "C"};
Integer[] intArray = {1, 2, 3};

example.<String>printArray(stringArray);  // Output: A, B, C
example.<Integer>printArray(intArray);    // Output: 1, 2, 3
```
Here, the type parameter `<T>` is declared before the return type of the method. You can call the method for different types by specifying the type or let Java infer it.

## Bounded Type Parameters
Generics allow you to specify bounds for the type parameters using the extends keyword. This restricts the types that can be used as arguments.

**Syntax:**
```java
class NumberBox<T extends Number> {
    private T value;

    public NumberBox(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
```
**Example:**
```java
NumberBox<Integer> intBox = new NumberBox<>(123);
NumberBox<Double> doubleBox = new NumberBox<>(45.67);

// NumberBox<String> stringBox = new NumberBox<>("Hello");  // Compilation error
```
In this example, `T extends Number` means that only classes that are subclasses of Number (like `Integer`, `Double`, etc.) can be used as type arguments. String cannot be used because it does not extend Number.

## Wildcards in Generics
Generics offer wildcards to allow more flexibility in the type arguments. Wildcards are represented by the `?` symbol.

### Unbounded Wildcard
`?` can represent any type.
**Example:**
```java
public void printList(List<?> list) {
    for (Object elem : list) {
        System.out.println(elem);
    }
}
```
You can pass a List of any type to this method:

```java
List<String> stringList = Arrays.asList("A", "B", "C");
List<Integer> intList = Arrays.asList(1, 2, 3);


printList(stringList);  // Output: A, B, C
printList(intList);     // Output: 1, 2, 3
```

### Upper Bounded Wildcard
`<? extends T>` allows a method to accept arguments of type T or its subclasses.
**Example:**
```java
public void printNumbers(List<? extends Number> list) {
    for (Number num : list) {
        System.out.println(num);
    }
}
```
You can pass lists of any subclass of Number:

```java
List<Integer> intList = Arrays.asList(1, 2, 3);
List<Double> doubleList = Arrays.asList(1.1, 2.2, 3.3);

printNumbers(intList);    // Output: 1, 2, 3
printNumbers(doubleList); // Output: 1.1, 2.2, 3.3
```

### Lower Bounded Wildcard
`<? super T>` allows a method to accept arguments of type T or its superclasses.
**Example:**
```java
public void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```
You can pass a list of Integer or any of its superclasses (Number, Object):

```java
List<Number> numberList = new ArrayList<>();
addNumbers(numberList);  // Valid
```

## Type Erasure
Java implements generics using a process called type erasure. This means that generic type information is only used at compile-time for type safety, but is erased at runtime. As a result:

The JVM doesn't retain generic type information at runtime.
The compiler replaces generic types with their bounds or Object if no bounds are specified.

**Example:**
```java
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();
```
At runtime, both stringList and intList become List<Object>, so they appear identical.

**Implications of Type Erasure:**
- No instanceof checks: You can't do instanceof checks with generic types:

```java
if (list instanceof List<String>) {  // Compilation error
}
```
- No arrays of generic types: You can't create arrays of generic types:

```java
List<String>[] listArray = new List<String>[10];  // Compilation error
```

## Generic Interfaces
You can define generic interfaces similarly to generic classes.

**Syntax:**
```java
interface Container<T> {
    void add(T item);
    T get();
}
```
**Example:**
```java
class StringContainer implements Container<String> {
    private String value;

    @Override
    public void add(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }
}
```

## Benefits of Generics
- **Type Safety:** Generics enforce compile-time type checking, reducing the risk of ClassCastException at runtime.
- **Code Reusability:** Generics allow the same code to work with different data types, improving flexibility and reducing redundancy.
- **Elimination of Casts:** With generics, you don’t need to cast objects when retrieving them from collections.

## Limitations of Generics
- **Primitive Types Not Allowed:** Generics work only with reference types (e.g., `Integer`, `String`) and not with primitive types (`int`, `double`).

```java
List<int> intList = new ArrayList<>();  // Compilation error
List<int> intList = new ArrayList<>();  // Compilation error
```
- **No Instantiation of Generic Types:** You cannot instantiate a generic type parameter:

```java
class Test<T> {
    T obj = new T();  // Compilation error
}
```