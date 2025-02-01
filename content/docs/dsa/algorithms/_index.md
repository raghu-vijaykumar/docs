---
weight: 3
bookCollapseSection: true
title: "Algorithms"
draft: false
---

# Algorithms

An **algorithm** is a step-by-step, well-defined procedure or set of rules designed to solve a particular problem or perform a computation. It takes an input, processes the input through a series of steps, and produces an output. Algorithms form the foundation of computer science and programming and are essential for performing tasks in an efficient and logical manner.

{{< markmap >}}

```markmap
# Algorithms & Techniques
 - Basics
   - Recursion
   - Two Pointers
   - Sliding Window
   - Prefix Sum
 - Sorting
   - Bubble Sort (Time: `O(n^2)`, Space: `O(1)`)
   - Merge Sort (Time: `O(n log n)`, Space: `O(n)`)
   - Quick Sort (Time: `O(n log n)` average, `O(n^2)` worst, Space: `O(log n)`)
   - Insertion Sort (Time: `O(n^2)`, Space: `O(1)`)
   - Selection Sort (Time: O(n^2), Space: O(1))
   - Heap Sort (Time: O(n log n), Space: O(1))
   - Counting Sort (Time: O(n + k), Space: O(k))
   - Radix Sort (Time: O(nk), Space: O(n + k))
   - Bucket Sort (Time: O(n + k), Space: O(n))
   - Shell Sort (Time: O(n log n) average, O(n^2) worst, Space: O(1))
   - Tim Sort (Time: O(n log n), Space: O(n))
   - Pigeonhole Sort (Time: O(n + k), Space: O(k))
   - Bitonic Sort (Time: O(log^2 n), Space: O(n))
 - Search
   - Linear Search
   - Binary Search
   - Ternary Search
   - Interpolation Search
   - Exponential Search
   - Jump Search
   - Fibonacci Search
 - Pattern Matching
   - KMP Search
   - Rabin-Karp Search
 - Dynamic Programming (DP)
   - Basic DP Problems
      - Fibonacci Sequence
      - Knapsack Problem
      - Coin Change Problem
      - Subset Sum & Partition Problem
   - Sequence-Based DP Problems
      - Longest Common Subsequence (LCS)
      - Longest Increasing Subsequence (LIS)
      - Edit Distance (Levenshtein Distance)
   - Matrix-Based DP Problems
      - Matrix Chain Multiplication
      - Palindromic Subsequence & Partitioning
   - Game Theory & Harder DP Problems
      - Egg Dropping Problem
      - Rod Cutting Problem
      - Catalan Number Problems
   - Divide and Conquer
      - Strassen’s Matrix Multiplication
      - Karatsuba Algorithm
      - Closest Pair of Points
 - Greedy
   - Huffman Coding
   - Kadane's Algorithm
   - Activity Selection Problem
   - Job Sequencing with Deadlines
   - Egyptian Fraction
   - Coin Change Problem (Greedy Approach)
 - Graph
   - Shortest Path
     - Dijkstra's Algorithm
     - Floyd-Warshall Algorithm
     - Bellman-Ford Algorithm
     - Kahn's Algorithm
   - Minimum Spanning Tree
     - Kruskal's Algorithm
     - Prim's Algorithm
   - Topological Sort
   - Kosaraju's Algorithm
   - Cycle Detection
   - Eulerian Path & Circuit
   - Hamiltonian Path & Circuit
 - Disjoint Set
   - Tarjan's Algorithm
   - Bridges & Articulation Points (Tarjan's Algorithm)
   - Strongly Connected Components (SCC)
 - Tree Algorithms
   - Lowest Common Ancestor (LCA) (Binary Lifting)
   - Heavy-Light Decomposition
   - Tree Diameter Calculation
   - Centroid Decomposition
   - Persistent Segment Tree
 - String Algorithms
   - Z-Algorithm
   - Aho-Corasick Algorithm
   - Manacher’s Algorithm
   - Suffix Array & LCP Array
   - Burrows-Wheeler Transform (BWT)
 - Bit Manipulation Algorithms
   - Brian Kernighan’s Algorithm
   - Bitwise Sieve of Eratosthenes
   - XOR-Based Algorithms
 - Mathematical Algorithms
   - Extended Euclidean Algorithm
   - Chinese Remainder Theorem (CRT)
   - Fermat’s Primality Test
   - Miller-Rabin Primality Test
   - Modular Exponentiation
 - Randomized Algorithms
   - Monte Carlo Method
   - Las Vegas Algorithm
   - Reservoir Sampling
```

{{< /markmap >}}

## Characteristics of Algorithms

1. **Definiteness**: Each step of the algorithm must be clear and unambiguous.
2. **Finiteness**: The algorithm must terminate after a finite number of steps.
3. **Input**: The algorithm accepts input values, which are necessary for the process.
4. **Output**: The algorithm produces output or results after processing the inputs.
5. **Effectiveness**: Every step of the algorithm must be effective and feasible, meaning it should be performed within a reasonable amount of time using available resources.

## Types of Algorithms

1. **Sorting Algorithms**: Organize data in a particular order (e.g., ascending or descending).

   - Examples: Bubble Sort, Merge Sort, Quick Sort, Insertion Sort.

2. **Search Algorithms**: Find specific elements or data in a data structure.

   - Examples: Linear Search, Binary Search, Depth-First Search (DFS), Breadth-First Search (BFS).

3. **Dynamic Programming Algorithms**: Solve problems by breaking them down into simpler subproblems and storing their results to avoid redundant computation.

   - Examples: Fibonacci Sequence, Knapsack Problem.

4. **Divide and Conquer Algorithms**: Break down a problem into smaller subproblems, solve them recursively, and combine their solutions.

   - Examples: Merge Sort, Quick Sort, Binary Search.

5. **Greedy Algorithms**: Make local optimal choices at each step with the hope of finding a global optimum.

   - Examples: Dijkstra's Algorithm, Kruskal's Minimum Spanning Tree, Huffman Coding.

6. **Backtracking Algorithms**: Use a trial-and-error approach to solve problems incrementally by building solutions and discarding them if they fail.

   - Examples: N-Queens Problem, Sudoku Solver.

7. **Graph Algorithms**: Solve problems related to graph structures such as shortest paths, minimum spanning trees, or network flows.

   - Examples: Dijkstra’s Algorithm, Prim's Algorithm, Floyd-Warshall Algorithm.

8. **Tree Algorithms**: Solve problems related to tree structures such as finding the lowest common ancestor, calculating the diameter, or decomposing the tree.

   - Examples: Lowest Common Ancestor (LCA) (Binary Lifting), Heavy-Light Decomposition, Tree Diameter Calculation, Centroid Decomposition.

9. **String Algorithms**: Solve problems related to string manipulation and pattern matching.

   - Examples: Z-Algorithm, Aho-Corasick Algorithm, Manacher’s Algorithm, Suffix Array & LCP Array, Burrows-Wheeler Transform (BWT).

10. **Bit Manipulation Algorithms**: Perform operations on individual bits of data to manipulate or extract information.

    - Examples: Brian Kernighan’s Algorithm, Bitwise Sieve of Eratosthenes, XOR-Based Algorithms.

11. **Mathematical Algorithms**: Solve problems related to mathematical operations and number theory.

    - Examples: Extended Euclidean Algorithm, Chinese Remainder Theorem (CRT), Fermat’s Primality Test, Miller-Rabin Primality Test, Modular Exponentiation.
