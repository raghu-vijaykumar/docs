---
weight: 1
bookCollapseSection: true
title: "Data Structures & Algorithms"
draft: false
---

# Data Structures & Algorithms

This section covers comprehensive Data Structures and Algorithms (DSA) study guides, designed as quick references for coding interviews. Each topic follows a structured approach: **Markmap** for overview, **Theory & Code Snippets** for understanding, and **Leetcode Problems** for practice.

{{< markmap >}}

```markmap
# DSA Learning Roadmap
- **DSA Overview**
  - Big O notation
  - Time & Space Complexity
- **Algorithms**
  - Basics
    - Recursion
    - Two Pointers
    - Sliding Window
    - Prefix Sum
  - Sorting
    - Bubble, Merge, Quick, Heap, etc.
  - Searching
    - Linear Search [](/docs/dsa/algorithms/search/linear-search/)
    - Binary Search [](/docs/dsa/algorithms/search/binary-search/)
    - Exponential Search [](/docs/dsa/algorithms/search/exponential-search/)
    - Fibonacci Search [](/docs/dsa/algorithms/search/fibonacci-search/)
    - Interpolation Search [](/docs/dsa/algorithms/search/interpolation-search/)
    - Jump Search [](/docs/dsa/algorithms/search/jump-search/)
    - Ternary Search [](/docs/dsa/algorithms/search/ternary-search/)
  - Dynamic Programming [](/docs/dsa/algorithms/dynamic-programming/)
  - Backtracking [](/docs/dsa/algorithms/backtracking/)
  - Greedy Algorithms [](/docs/dsa/algorithms/greedy/)
  - Graph Algorithms [](/docs/dsa/algorithms/graph/)
    - Shortest Path (Dijkstra, Bellman-Ford, Floyd-Warshall)
    - MST (Kruskal, Prim)
    - Topological Sort
    - Tarjan Algorithm (DSU, SCC, Bridges)
    - Tree Algorithms (LCA, Heavy-Light, Tree Diameter)
  - String Algorithms [](/docs/dsa/algorithms/string/)
    - KMP, Rabin-Karp, Z-Algorithm, Aho-Corasick, Manacher
  - Bit Manipulation [](/docs/dsa/algorithms/bit-manipulation/)
  - Mathematical Algorithms [](/docs/dsa/algorithms/math/)
    - Chinese Remainder Theorem
  - Divide and Conquer
- **Data Structures**
  - Primitive: Arrays, Strings
  - Linear: Linked Lists, Stacks, Queues
  - Nonlinear: Trees (Binary, BST, AVL, Red-Black, Segment, Fenwick)
  - Advanced: Tries, Disjoint Sets, Graphs, Heaps, Hash Tables
  - Cache: LRU, LFU
  - Intervals
- **Utilities**
  - Comparator & Comparable
  - Iterator
  - Recursion

```

{{< /markmap >}}

## Quick Links to All Topics

### Foundations & Basics
- [Big O, Complexity Analysis]({{< relref "docs/dsa/utilities/recursion" >}}) *(within recursion utilities)*
- [Recursion Utilities]({{< relref "docs/dsa/utilities/recursion" >}})
- [Iterators]({{< relref "docs/dsa/utilities/iterator" >}})
- [Comparators]({{< relref "docs/dsa/utilities/comparator" >}})

### Algorithms
- **Searching**
  - [Linear Search]({{< relref "docs/dsa/algorithms/search/linear-search" >}})
  - [Binary Search]({{< relref "docs/dsa/algorithms/search/binary-search" >}})
  - [Exponential Search]({{< relref "docs/dsa/algorithms/search/exponential-search" >}})
  - [Fibonacci Search]({{< relref "docs/dsa/algorithms/search/fibonacci-search" >}})
  - [Interpolation Search]({{< relref "docs/dsa/algorithms/search/interpolation-search" >}})
  - [Jump Search]({{< relref "docs/dsa/algorithms/search/jump-search" >}})
  - [Ternary Search]({{< relref "docs/dsa/algorithms/search/ternary-search" >}})
- [Dynamic Programming]({{< relref "docs/dsa/algorithms/dynamic-programming" >}})
- [Backtracking]({{< relref "docs/dsa/algorithms/backtracking" >}})
- [Greedy Algorithms]({{< relref "docs/dsa/algorithms/greedy" >}})
- **Graphs**: See sub-index below
- **Strings**: See sub-index below
- **Bit Manipulation**: See sub-index below
- **Math**
  - [Chinese Remainder Theorem]({{< relref "docs/dsa/algorithms/math/chinese-remainder-theorem" >}})

### Data Structures
- **Arrays & Strings**: Covered in basics
- **Linear DS**
  - [Linked Lists]({{< relref "docs/dsa/data-structures/linked-list" >}}) (Singly, Doubly, Circular, Skip)
  - [Stacks]({{< relref "docs/dsa/data-structures/stack" >}})
  - [Queues]({{< relref "docs/dsa/data-structures/queue" >}})
- **Trees**
  - [Binary Trees]({{< relref "docs/dsa/data-structures/tree/binarytree" >}})
  - [Binary Search Trees]({{< relref "docs/dsa/data-structures/tree/binarysearchtree" >}})
  - [AVL Trees]({{< relref "docs/dsa/data-structures/tree/avl-trees" >}})
  - [Red-Black Trees]({{< relref "docs/dsa/data-structures/tree/rb-tree" >}})
  - [Segment Trees]({{< relref "docs/dsa/data-structures/tree/segment-tree" >}})
  - [Fenwick Trees]({{< relref "docs/dsa/data-structures/tree/fenwick-tree" >}})
  - [B-Trees]({{< relref "docs/dsa/data-structures/tree/b-trees" >}})
  - [Trie (Prefix Tree)]({{< relref "docs/dsa/data-structures/trie" >}})
- **Graphs**
  - [Graph Representations]({{< relref "docs/dsa/data-structures/graph/graph" >}})
  - [Undirected Graphs]({{< relref "docs/dsa/data-structures/graph/undirected-graph" >}})
  - [Directed Graphs]({{< relref "docs/dsa/data-structures/graph/directed-graphs" >}})
  - [Weighted Graphs]({{< relref "docs/dsa/data-structures/graph/weighted-graphs" >}})
- [Hash Tables]({{< relref "docs/dsa/data-structures/hashtable" >}})
- [Heaps]({{< relref "docs/dsa/data-structures/heap" >}})
- [Disjoint Sets]({{< relref "docs/dsa/data-structures/disjoint-set" >}})
- [Intervals]({{< relref "docs/dsa/data-structures/intervals" >}})
- **Caches**
  - [LRU Cache]({{< relref "docs/dsa/data-structures/cache/lru" >}})
  - [LFU Cache]({{< relref "docs/dsa/data-structures/cache/lfu" >}})

## Studying Path & Recommendations

1. **Start with Basics**: Recursion, Big O, then fundamental sorting (Merge, Quick, Binary Search).
2. **Build Foundations**: Data Structures (Arrays, Linked Lists, Trees, Heaps), then Algorithms (Greedy, DP, Backtracking).
3. **Dive into Specializations**: Graph, String, Bit Manip, Math algorithms.
4. **Practice Daily**: Use the Leetcode problems in each topic to reinforce patterns.

Each individual doc includes:
- **Markmap**: High-level overview
- **Theory & Snippets**: Detailed explanations and code
- **Leetcode Practice**: Suggested problems with difficulty levels

Happy learning! 🚀
