+++
title= "Functional Programming"
tags = [ "functional", "programming" ]
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

# Functional Programming

Functional programming (FP) is a programming paradigm that treats computation as the evaluation of mathematical functions and emphasizes a declarative style. In FP, functions are first-class citizens, meaning they can be passed as arguments, returned from other functions, and assigned to variables. The core concepts of FP include:

## 1. Pure Functions
- A **pure function** always produces the same output given the same input and has no side effects (e.g., modifying external state or variables).
- Pure functions make programs more predictable and easier to reason about.

## 2. Immutability
- In functional programming, data is **immutable**, meaning that once a value is assigned, it cannot be changed. Instead of modifying data, new data structures are created.
- This helps reduce side effects and makes the program easier to reason about, particularly in concurrent or parallel environments.

## 3. First-Class Functions
- Functions are **first-class citizens**, meaning they can be:
  - Assigned to variables
  - Passed as arguments to other functions
  - Returned from functions
- This enables a high degree of flexibility and reusability in programming.

## 4. Higher-Order Functions
- A **higher-order function** is a function that either:
  - Takes one or more functions as arguments
  - Returns a function as a result
- Common examples include functions like `map`, `reduce`, and `filter`.

## 5. Recursion
- Functional programming often uses **recursion** to handle iteration since it avoids mutable state and loops (like `for` or `while`).
- A recursive function calls itself to break down problems into smaller parts.

## 6. Function Composition
- **Function composition** is the process of combining two or more functions to produce a new function. 
- This allows for creating more complex functions from simpler ones.

## 7. Declarative Programming
- Functional programming focuses on *what* to do rather than *how* to do it. This contrasts with imperative programming, which specifies explicit step-by-step instructions.

## 8. Referential Transparency
- An expression is **referentially transparent** if it can be replaced with its value without changing the program's behavior.
- This property holds when functions are pure, making the program easier to debug and reason about.

## Example in Functional Programming

Here’s an example in Python using **map** and **reduce** to operate on lists:

```python
from functools import reduce

numbers = [1, 2, 3, 4]

# Using map to square each number
squared_numbers = list(map(lambda x: x**2, numbers))

# Using reduce to sum all numbers
sum_of_numbers = reduce(lambda x, y: x + y, numbers)
```

## Benefits of Functional Programming
- Modularity: First-class functions enable more modular code.
- Easier to Test: Pure functions with no side effects are easier to test and debug.
- Concurrency: Immutability and lack of side effects make FP well-suited for concurrent and parallel processing.

Languages like Haskell, Erlang, Scala, and F# are designed for functional programming, while languages like Python, JavaScript, and Java support functional programming features.