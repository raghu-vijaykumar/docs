---
weight: 1
bookCollapseSection: true
title: "Basics"
draft: false
---

# Basics

{{< markmap >}}

```markmap
# Algorithm Basics
- **Time Complexity**
  - Big O Notation
  - Big Ω (Omega)
  - Big θ (Theta)
  - Common complexities (O(1), O(log n), O(n), O(n log n), O(n²))
- **Space Complexity**
  - Auxiliary space vs total space
  - In-place algorithms
- **Algorithm Analysis**
  - Best, average, worst case analysis
  - Amortized analysis
- **Problem Solving Approach**
  - Understand the problem
  - Choose a data structure(s)
  - Design the algorithm
  - Analyze complexity
  - Implement and test
```

{{< /markmap >}}

## Introduction to Algorithms

An **algorithm** is a finite sequence of well-defined instructions to solve a computational problem. The goal is to design algorithms that are efficient in terms of time and space.

### Why Algorithm Analysis Matters?

- Predicts how algorithm will scale with input size
- Helps choose the best algorithm for specific constraints
- Essential for optimizing performance in real-world applications

## Time Complexity Analysis

### Big O Notation

Big O notation describes the **upper bound** or *worst-case* scenario of an algorithm's growth rate.

| Notation   | Name         | Example                                  |
| ---------- | ------------ | ---------------------------------------- |
| O(1)       | Constant     | Array access, hash table lookup          |
| O(log n)   | Logarithmic  | Binary search, BST operations            |
| O(n)       | Linear       | Linear search, single pass traversals    |
| O(n log n) | Linearithmic | Sorting algorithms (merge, quick, heap)  |
| O(n²)      | Quadratic    | Bubble sort, nested loops                |
| O(2^n)     | Exponential  | Brute force subsets, recursive factorial |

### Common Rules for Analysis

1. **Simplify**: Keep only the dominant term
2. **Ignore constants**: O(2n) = O(n)
3. **Nested loops**: Multiplication of complexities
4. **Consecutive statements**: Take the maximum complexity

### Example Analysis

```java
public int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {        // O(n)
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;                                     // O(1)
}
// Total: O(n) + O(1) = O(n)
```

## Space Complexity Analysis

Space complexity measures the amount of memory an algorithm uses relative to input size.

### Types

- **Auxiliary Space**: Extra space used besides input
- **Total Space**: Input space + auxiliary space

### Examples

- **In-place**: O(1) extra space (Bubble Sort, Selection Sort)
- **Out-of-place**: O(n) extra space (Merge Sort)

## Problem Solving Framework

### 1. Understand the Problem
- Read carefully, multiple times if needed
- Identify inputs, outputs, constraints
- Ask clarifying questions

### 2. Choose Data Structures
- Array: Fast access, O(n) search
- HashMap: O(1) lookup, but space intensive
- LinkedList: Dynamic size, O(1) insert/delete
- Stack/Queue: Specific access patterns
- Tree/Graph: Hierarchical/relational data

### 3. Design the Algorithm
- Break problem into smaller subproblems
- Consider edge cases (empty input, duplicates, etc.)
- Think about multiple approaches
- Choose based on constraints

### 4. Analyze Complexity
- Time: O(?)
- Space: O(?)
- Trade-offs between time and space

### 5. Implement and Test
- Write clean, readable code
- Handle edge cases
- Test with small examples first
- Debug systematically

## Common Algorithm Categories

### Divide and Conquer
- Divide problem into subproblems
- Solve recursively
- Combine solutions
- Examples: Merge Sort, Quick Sort, Binary Search

### Greedy Algorithms
- Make locally optimal choice at each step
- Hope for globally optimal solution
- Examples: Huffman Coding, Dijkstra's, Kruskal's

### Dynamic Programming
- Break into subproblems
- Store solutions (memoization/tabulation)
- Avoid recomputation
- Examples: Fibonacci, Knapsack, Longest Common Subsequence

## LeetCode Practice Problems

### Easy - Foundation Building
- [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/)
- [704. Binary Search](https://leetcode.com/problems/binary-search/)

### Medium - Application
- [75. Sort Colors](https://leetcode.com/problems/sort-colors/)
- [148. Sort List](https://leetcode.com/problems/sort-list/)

## Key Takeaways

1. **Time > Space**: Usually prioritize time complexity over space
2. **Worst-case first**: Always consider worst-case scenario
3. **Understand before coding**: Clear planning prevents bugs
4. **Practice regularly**: Algorithm design is a skill that improves with practice
