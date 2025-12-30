---
aliases: [""]
weight: 1
bookFlatSection: true
title: "Eulerian Path & Circuit"
draft: false
---

# Eulerian Path & Circuit

## 1. Markmap

{{</* markmap 4 */>}}

- Eulerian Path & Circuit
  - Graph Theory Fundamentals
    - Undirected/Directed Graphs
    - Degree Concepts
  - Path Classification
    - Eulerian Path: Traverse each edge exactly once
    - Eulerian Circuit: Closed Eulerian Path
  - Existence Conditions
    - Undirected: ≤2 vertices with odd degree
    - Directed: Exactly one in/out degree difference
  - Algorithms
    - Hierholzer's Algorithm
    - Fleury's Algorithm
  - Applications
    - DNA Sequencing
    - Route Planning
    - Puzzle Solving

{{</* /markmap */>}}

## 2. Title

Eulerian Path and Circuit

## 3. Problem Definition / Concept Overview

An Eulerian path is a path in a graph that visits each edge exactly once. An Eulerian circuit is a cyclic Eulerian path that starts and ends at the same vertex.

### Key Definitions:
- **Eulerian Path**: Sequence using each edge exactly once
- **Eulerian Circuit**: Closed Eulerian path (cycle)
- **Eulerian Graph**: Graph containing an Eulerian circuit

## 4. Intuition / Core Idea

Named after Leonhard Euler's 1736 solution to the Königsberg Bridge Problem, the core idea is that traversing each edge exactly once depends purely on vertex degrees:

- In undirected graphs: Most vertices must have even degree
- In directed graphs: Balance of incoming/outgoing edges is crucial

The algorithm uses the concept that after traversing a path, any remaining subgraph must also be Eulerian.

## 5. Variants / Use Cases

### Variants:
- **Eulerian Path**: Open walk (ends at different vertices)
- **Eulerian Circuit**: Closed walk (same start/end vertex)
- **Semi-Eulerian**: Contains Eulerian path but no circuit

### Use Cases:
- **DNA Fragment Assembly**: Reconstructing genome sequences
- **Route Optimization**: Chinese Postman Problem variants
- **Game Theory**: Knight's tour, maze solving
- **Network Analysis**: Infrastructure routing optimization
- **Chemistry**: Molecular structure analysis

## 6. Operations / Algorithm Steps

### Hierholzer's Algorithm (Efficient):
1. Check existence conditions
2. Choose starting vertex (vertex with odd degree for path)
3. Maintain a stack for backtracking
4. DFS traversal: remove edges as traversed
5. When stuck, backtrack and append vertices
6. Continue until all edges used

### Fleury's Algorithm (Brute Force):
1. Start from vertex meeting conditions
2. Choose unused edge (avoid bridges until necessary)
3. Remove edge and move to adjacent vertex
4. Repeat until path complete
5. For circuit, return to start vertex

## 7. Pseudocode / Implementation

### Hierholzer's Algorithm (Undirected Graph):
```python
def findEulerianPath(graph, start):
    path = []
    stack = [start]

    while stack:
        u = stack[-1]
        if graph[u]:  # Has unused edges
            v = graph[u].pop()
            # Remove reverse edge for undirected graph
            graph[v].remove(u)
            stack.append(v)
        else:
            path.append(stack.pop())

    return path[::-1]  # Reverse to get correct order
```

### Existence Check:
```python
def hasEulerianPath(graph):
    odd_count = 0
    for vertex in graph:
        if len(graph[vertex]) % 2 == 1:
            odd_count += 1
            start = vertex
    return odd_count == 0 or odd_count == 2
```

## 8. Complexity Analysis

| Algorithm    | Time Complexity              | Space Complexity |
| ------------ | ---------------------------- | ---------------- |
| Hierholzer's | O(V + E)                     | O(V + E)         |
| Fleury's     | O(V + E) with bridge finding | O(V + E)         |

- **V**: Number of vertices
- **E**: Number of edges
- DFS traversal dominates complexity
- Additional O(E) for existence checking

## 9. Example Walkthrough

### Example 1: Eulerian Circuit
```
Graph: A-B-C-D-A-B-D
Edges: A-B, B-C, C-D, D-A, A-B, B-D

Starting at A:
1. Traverse to B (via A-B), remove edge
2. From B to C (B-C), remove
3. From C to D (C-D), remove
4. From D to A (D-A), remove
5. From A to B (A-B), remove
6. From B to D (B-D), remove

Result: A-B-C-D-A-B-D (back to A, circuit found)
```

### Example 2: Eulerian Path
```
Graph with odd degrees: A-B, A-C, B-C
Odd degree vertices: A(1), B(2), C(1)

Start at A or C:
1. A to B, remove A-B
2. B to C, remove B-C
3. C to A, but A-C already used

Result: A-B-C (path, not circuit)
```

## 10. Common Pitfalls & Edge Cases

### Pitfalls:
- Forgetting to remove vertices with zero degree from consideration
- Not handling disconnected graphs properly
- Mistaking Eulerian circuit conditions for path conditions

### Edge Cases:
- **Single vertex with loop**: Always has Eulerian circuit
- **Two vertices with multiple edges**: May create Eulerian circuit/path
- **Disconnected graph**: Must check each component separately
- **Empty graph**: Degenerate case, handle gracefully
- **Graph with all even degrees**: Has circuit if connected
- **Graph with exactly 2 odd degrees**: Has path but not circuit

## 11. Extensions / Related Topics

### Related Concepts:
- **Chinese Postman Problem**: Find shortest closed walk covering all edges
- **Hamiltonian Path**: Visit each vertex exactly once (NP-complete)
- **Traveling Salesman Problem**: Hamiltonian path optimization
- **Bridge Detection**: Important for Fleury's algorithm
- **Graph Connectivity**: Strongly/Weakly connected components

### Advanced Variants:
- **De Bruijn Sequences**: Eulerian paths in sequence graphs
- **Genome Assembly**: Practical application in bioinformatics
- **Route Planning**: Urban planning and logistics

## 12. Interview Tips / Talking Points

### Key Questions:
- "What's the difference between Eulerian path and circuit?"
- "How do degree conditions determine existence?"
- "Why is Hierholzer's more efficient than Fleury's?"
- "How would you handle directed graphs?"

### Talking Points:
- Historical context (Königsberg Bridge Problem)
- Applications in real-world problems
- Comparison with Hamiltonian paths
- Implementation considerations
- Edge case handling

### Follow-up Questions:
- "Implementation details for different graph representations?"
- "How to modify for finding all possible Eulerian paths?"
- "Performance on sparse vs dense graphs?"

## 13. LeetCode Problems

| Problem                                                                                              | Difficulty | Technique                               |
| ---------------------------------------------------------------------------------------------------- | ---------- | --------------------------------------- |
| [332. Reconstruct Itinerary](https://leetcode.com/problems/reconstruct-itinerary/)                   | Medium     | Eulerian Path in Directed Graph         |
| [2097. Valid Arrangement of Pairs](https://leetcode.com/problems/valid-arrangement-of-pairs/)        | Hard       | Eulerian Circuit with Pair Constraints  |
| [753. Cracking the Safe](https://leetcode.com/problems/cracking-the-safe/)                           | Hard       | De Bruijn Sequence (Eulerian Path)      |
| [334. Increasing Triplet Subsequence](https://leetcode.com/problems/increasing-triplet-subsequence/) | Medium     | Non-graph variant, but similar thinking |

## 14. References

- **Original Paper**: Euler, L. (1736). "Solutio problematis ad geometriam situs pertinentis"
- **CLRS Book**: Chapter on Graph Algorithms
- **Wikipedia**: Eulerian Path article
- **Introduction to Algorithms** by Cormen et al.
