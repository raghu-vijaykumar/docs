---
weight: 2
bookCollapseSection: true
title: "Data Structures"
draft: false
---

# Data Structures

**Data structures** are organized and specialized formats for storing, managing, and manipulating data. They define a particular way to organize and store data in a computer so that it can be used efficiently. The choice of an appropriate data structure allows algorithms to run faster and use less memory, which is crucial for developing efficient software.

## Categories of Data Structures

### 1. Primitive Data Structures
- Basic types of data structures that are directly operated upon by the machine instructions.
- **Examples**: Integers, Floats, Characters, Boolean.

### 2. Non-Primitive Data Structures
- More complex data structures that are derived from primitive data types.
- Further classified into:
  - **Linear Data Structures**: Data elements are arranged sequentially.
    - **Examples**: Arrays, Linked Lists, Stacks, Queues.
  - **Non-Linear Data Structures**: Data elements are arranged in a hierarchical or graph-like relationship.
    - **Examples**: Trees, Graphs, Heaps, Hash Tables.

## Common Data Structures

### 1. Array
- A collection of elements, identified by index or key.
- Elements are stored in contiguous memory locations.

### 2. Linked List
- A linear collection of data elements called nodes, where each node points to the next node.
- **Types**: Singly Linked List, Doubly Linked List, Circular Linked List.

### 3. Stack
- A linear data structure that follows a Last-In-First-Out (LIFO) principle.
- Used for recursive algorithms, expression evaluation, and backtracking.

### 4. Queue
- A linear data structure that follows a First-In-First-Out (FIFO) principle.
- Used in scheduling, buffering, and resource management tasks.

### 5. Tree
- A hierarchical data structure consisting of nodes, where each node can have child nodes.
- **Types**: Binary Tree, Binary Search Tree, AVL Tree, Red-Black Tree.

### 6. Graph
- A collection of nodes (vertices) connected by edges.
- **Types**: Directed, Undirected, Weighted, Unweighted.

### 7. Hash Table
- A data structure that maps keys to values using a hash function.
- Used for efficient searching, insertion, and deletion.

### 8. Heap
- A specialized tree-based data structure that satisfies the heap property.
- **Types**: Max Heap, Min Heap.

## Advanced Data Structures

### 1. Trie (Prefix Tree)
- A tree-like data structure used to store dynamic sets of strings where keys are usually strings.
- Useful for implementing dictionaries, autocomplete features, and spell-checking algorithms.

### 2. Segment Tree
- A tree used for storing intervals or segments. It allows querying which of the stored segments contain a given point efficiently.
- Mainly used in applications that require fast queries and updates of array ranges.

### 3. Fenwick Tree (Binary Indexed Tree)
- A data structure used to efficiently update elements and calculate prefix sums in a table of numbers.
- Often used in range query problems.

### 4. B-Tree
- A self-balancing search tree in which nodes can have multiple children.
- Commonly used in databases and file systems where large blocks of data are read and written.

### 5. Suffix Tree
- A compressed trie of all the suffixes of a given text. It allows for fast pattern matching.
- Useful in string matching algorithms like substring search, longest repeated substring, and more.

### 6. KD-Tree (K-Dimensional Tree)
- A space-partitioning data structure for organizing points in a k-dimensional space.
- Used in applications like nearest neighbor search and range search in multiple dimensions.

### 7. Skip List
- A probabilistic data structure that allows fast search within an ordered sequence of elements.
- Provides a middle ground between linked lists and balanced trees with logarithmic search times.

### 8. Red-Black Tree
- A self-balancing binary search tree that guarantees O(log n) time complexity for search, insertion, and deletion.
- Used in the implementation of many associative containers, such as `TreeMap` in Java and `std::map` in C++.

### 9. Disjoint Set (Union-Find)
- A data structure that keeps track of a partition of a set into disjoint subsets.
- Used in algorithms such as Kruskal's for finding the Minimum Spanning Tree and for network connectivity problems.

## Importance of Data Structures

- **Efficiency**: Properly chosen data structures make programs efficient in terms of processing time and memory usage.
- **Modularity**: Data structures can simplify complex programs by breaking down tasks into smaller, manageable operations.
- **Reusability**: Data structures allow code to be more adaptable and reusable, making the software more scalable.

## Implementations 

| **Data Structure**     | **Python Implementation**     | **Java Implementation**              |
| ---------------------- | ----------------------------- | ------------------------------------ |
| **Array**              | `list`                        | `Array` / `ArrayList`                |
| **Linked List**        | `Node`, `LinkedList`          | `Node`, `LinkedList`                 |
| **Stack**              | `list` / `collections.deque`  | `Stack` / `ArrayDeque`               |
| **Queue**              | `collections.deque` / `Queue` | `Queue` / `LinkedList`               |
| **Deque**              | `collections.deque`           | `ArrayDeque`                         |
| **Hash Table**         | `dict`                        | `HashMap`, `Hashtable`               |
| **Set**                | `set`                         | `HashSet`, `TreeSet`                 |
| **Tree**               | Custom `Node`, `Tree` class   | `TreeNode`, `BinaryTree`             |
| **Binary Search Tree** | Custom `Node`, `BST` class    | `TreeNode`, `BinarySearchTree`       |
| **Graph**              | Custom `Graph` class          | Custom `Graph`, `AdjacencyListGraph` |
| **Trie**               | Custom `TrieNode`, `Trie`     | Custom `TrieNode`, `Trie`            |
| **Heap**               | `heapq`                       | `PriorityQueue`                      |
| **HashSet**            | `set`                         | `HashSet`                            |



## Conclusion

Data structures are foundational to computer science and programming. Choosing the right data structure is essential for optimizing program performance, ensuring efficient memory usage, and solving computational problems effectively. Understanding different types of data structures, from basic arrays to advanced structures like tries and B-trees, is a key skill for any software developer or computer scientist.
