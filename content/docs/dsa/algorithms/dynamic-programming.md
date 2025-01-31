---
weight: 3
bookFlatSection: true
title: "Dynamic Programming"
draft: false
---

# Dynamic Programming

{{< markmap >}}

```markmap
# Dynamic Programming (DP)
- **Memoization** → Top-down approach, storing results to avoid recomputation.
- **Tabulation** → Bottom-up approach, filling up a table iteratively.
- **State Compression** → Optimizing space by storing only essential states.
- **Knapsack Problem** → Classic optimization problem with different variants.
  - **0/1 Knapsack** → Can take or leave an item.
  - **Unbounded Knapsack** → Can take an item multiple times.
- **Longest Common Subsequence (LCS)** → Finds the longest subsequence present in both strings.
- **Longest Increasing Subsequence (LIS)** → Finds the longest subsequence in a sequence that is strictly increasing.
- **Fibonacci Sequence** → A basic DP problem to compute Fibonacci numbers.
- **Matrix Chain Multiplication** → Optimizes the order of matrix multiplications.
- **Coin Change Problem** → Finds the minimum number of coins required to make a certain amount.
- **Edit Distance** → Calculates the minimum number of operations to convert one string into another.
- **Subset Sum** → Determines if there is a subset that adds up to a given sum.
- **Optimal Binary Search Tree** → Find the optimal way to build a binary search tree.
- **DP on Trees** → Solves tree-based problems using dynamic programming.
```

{{< /markmap >}}

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

# Leetcode Problems

| **Level**     | **Problem Name & Link**                                                                                               | **Technique Used**                     |
| ------------- | --------------------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| 🟢 **Easy**   | [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/)                                                 | Simple 1D DP                           |
| 🟢 **Easy**   | [198. House Robber](https://leetcode.com/problems/house-robber/)                                                      | Simple 1D DP                           |
| 🟡 **Medium** | [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/)                         | LCS (Dynamic Programming)              |
| 🟡 **Medium** | [62. Unique Paths](https://leetcode.com/problems/unique-paths/)                                                       | 2D DP on Grid                          |
| 🟡 **Medium** | [121. Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)                | 1D DP (with state transitions)         |
| 🟡 **Medium** | [198. House Robber II](https://leetcode.com/problems/house-robber-ii/)                                                | Circular 1D DP                         |
| 🟡 **Medium** | [322. Coin Change](https://leetcode.com/problems/coin-change/)                                                        | 1D DP (0/1 Knapsack)                   |
| 🟡 **Medium** | [51. N-Queens](https://leetcode.com/problems/n-queens/)                                                               | Backtracking + 2D DP                   |
| 🟡 **Medium** | [300. Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)                  | 1D DP (Longest Increasing Subsequence) |
| 🟡 **Medium** | [464. Can I Win](https://leetcode.com/problems/can-i-win/)                                                            | Game Theory + 1D DP                    |
| 🟡 **Medium** | [279. Perfect Squares](https://leetcode.com/problems/perfect-squares/)                                                | 1D DP (Minimum Steps)                  |
| 🟡 **Medium** | [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/)                                    | 1D DP (Subsequence Counting)           |
| 🟡 **Medium** | [138. Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/)                    | 1D DP with Linked List                 |
| 🟡 **Medium** | [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/)                              | 1D DP (Max Product Subarray)           |
| 🟡 **Medium** | [300. Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)                  | 1D DP (LIS)                            |
| 🟡 **Medium** | [72. Edit Distance](https://leetcode.com/problems/edit-distance/)                                                     | 1D DP (Edit Distance)                  |
| 🟡 **Medium** | [1027. Longest Arithmetic Subsequence](https://leetcode.com/problems/longest-arithmetic-subsequence/)                 | 1D DP (Arithmetic Subsequence)         |
| 🔴 **Hard**   | [188. Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)          | 1D DP (Stock Buy and Sell)             |
| 🔴 **Hard**   | [123. Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/)        | 1D DP (Stock Buy and Sell)             |
| 🔴 **Hard**   | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)                      | 1D DP (Palindrome)                     |
| 🔴 **Hard**   | [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/)                         | 2D DP (Regex Matching)                 |
| 🔴 **Hard**   | [44. Wildcard Matching](https://leetcode.com/problems/wildcard-matching/)                                             | 2D DP (Wildcard Matching)              |
| 🔴 **Hard**   | [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)                                        | 1D DP (Palindrome)                     |
| 🔴 **Hard**   | [96. Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees/)                           | 1D DP (BST)                            |
| 🔴 **Hard**   | [240. Search a 2D Matrix II](https://leetcode.com/problems/search-a-2d-matrix-ii/)                                    | 2D DP (Matrix Search)                  |
| 🔴 **Hard**   | [312. Burst Balloons](https://leetcode.com/problems/burst-balloons/)                                                  | 1D DP (Max Coins)                      |
| 🔴 **Hard**   | [132. Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/)                          | 1D DP (Palindrome Partitioning)        |
| 🔴 **Hard**   | [343. Integer Break](https://leetcode.com/problems/integer-break/)                                                    | 1D DP (Integer Partition)              |
| 🔴 **Hard**   | [137. Single Number II](https://leetcode.com/problems/single-number-ii/)                                              | 1D DP (Bitmasking + DP)                |
| 🔴 **Hard**   | [240. Search a 2D Matrix II](https://leetcode.com/problems/search-a-2d-matrix-ii/)                                    | 2D DP (Matrix Search)                  |
| 🔴 **Hard**   | [472. Concatenated Words](https://leetcode.com/problems/concatenated-words/)                                          | 1D DP + Trie                           |
| 🔴 **Hard**   | [372. Super Pow](https://leetcode.com/problems/super-pow/)                                                            | 1D DP (Matrix Exponentiation)          |
| 🔴 **Hard**   | [313. Super Ugly Number](https://leetcode.com/problems/super-ugly-number/)                                            | 1D DP + Priority Queue                 |
| 🔴 **Hard**   | [1039. Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | 1D DP (Polygon Triangulation)          |
| 🔴 **Hard**   | [1370. Increasing Decreasing String](https://leetcode.com/problems/increasing-decreasing-string/)                     | 1D DP (String Manipulation)            |
