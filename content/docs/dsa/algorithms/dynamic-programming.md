---
weight: 3
bookCollapseSection: true
title: "Dynamic Programming"
draft: false
---

# Dynamic Programming
Dynamic Programming (DP) is an optimization technique used to solve complex problems by breaking them down into simpler subproblems. The core idea of dynamic programming is to store the results of subproblems to avoid recomputing them, leading to more efficient algorithms.

Dynamic programming is particularly useful for problems that exhibit two main properties:

- **Optimal Substructure**: A problem has optimal substructure if an optimal solution to the problem can be constructed from the optimal solutions of its subproblems.
- **Overlapping Subproblems**: A problem has overlapping subproblems if the same subproblems are solved multiple times during the course of solving the main problem.

## Key Concepts
- **Memoization**: This is a top-down approach in which you recursively solve the problem and store the results of the subproblems in a table (or cache). If the same subproblem is encountered again, the solution is retrieved from the table instead of being recomputed.
- **Tabulation**: This is a bottom-up approach where you iteratively solve smaller subproblems first and use their solutions to build up the solution to the larger problem. It typically involves filling out a table with solutions to the subproblems, starting from the smallest.

## Example: Fibonacci Sequence
A classic example of dynamic programming is calculating the Fibonacci sequence, where each number is the sum of the two preceding ones.

### Recursive Approach (Without DP)

```java
public int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);  // Recomputing subproblems repeatedly
}
```

The above solution recomputes the Fibonacci numbers multiple times, resulting in an exponential time complexity O(2^n).

### Dynamic Programming Approach (With Memoization)

```java
public int fib(int n) {
    int[] memo = new int[n + 1];
    return fibMemo(n, memo);
}

private int fibMemo(int n, int[] memo) {
    if (n <= 1) return n;
    if (memo[n] == 0) {  // Check if result is already computed
        memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
    }
    return memo[n];
}
```
In this approach, we store the results of subproblems in an array memo so that we don’t recompute them. This reduces the time complexity to O(n).

### Dynamic Programming Approach (With Tabulation)

```java
public int fib(int n) {
    if (n <= 1) return n;
    int[] dp = new int[n + 1];
    dp[0] = 0;
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}
```
This approach builds the Fibonacci sequence iteratively and is also O(n) in time complexity, with O(n) space complexity.

## When to Use Dynamic Programming
- Optimization Problems: Problems that require finding the best or optimal solution (e.g., maximum, minimum, longest, shortest).
- Problems with Overlapping Subproblems: Problems where the same subproblems are solved multiple times, such as recursive algorithms without pruning.
- Problems Exhibiting Optimal Substructure: Problems where the optimal solution can be constructed efficiently from the optimal solutions of subproblems.

## Examples of Dynamic Programming Problems
- Knapsack Problem: Maximizing the total value of items that can be put into a knapsack of a fixed weight capacity.
- Longest Common Subsequence (LCS): Finding the longest subsequence common to two sequences.
- Matrix Chain Multiplication: Finding the most efficient way to multiply a chain of matrices.
- Shortest Path Algorithms: Dynamic programming is used in algorithms like Floyd-Warshall to find the shortest path between all pairs of vertices in a graph.

## Advantages and Disadvantages
- **Advantages**:
  - **Efficiency**: DP optimizes recursive algorithms by storing and reusing results, reducing time complexity from exponential to polynomial in many cases.
  - **Wide Applicability**: It is a versatile technique used across a wide range of problems, including optimization, graph theory, and combinatorics.
- **Disadvantages**:
  - **Memory Overhead**: Storing intermediate results can lead to increased space complexity.
  - **Complexity**: Developing dynamic programming solutions can be challenging because it requires identifying subproblems, formulating recurrences, and deciding the optimal way to store results.
