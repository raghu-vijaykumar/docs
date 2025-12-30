---
aliases: [""]
weight: 1
bookFlatSection: true
title: "Hamiltonian Path & Circuit"
draft: false
---

# Hamiltonian Path & Circuit

{{< markmap >}}
- Hamiltonian Path & Circuit
  - Definition
    - Hamiltonian Path: Visits each vertex exactly once
    - Hamiltonian Cycle: Returns to starting vertex
  - NP-Complete Problem
    - No efficient polynomial solution known
    - Brute force approaches
  - Algorithms
    - Backtracking
    - Dynamic Programming (TSP-related)
    - Heuristics for approximation
  - Applications
    - Graph theory problems
    - Routing optimization
    - Genome sequencing
{{< /markmap >}}

## Problem Definition / Concept Overview

A **Hamiltonian path** in a graph is a path that visits each vertex exactly once. A **Hamiltonian cycle** (or circuit) is a cycle that visits each vertex exactly once and returns to the starting vertex.

**Key characteristics:**
- **NP-complete**: Determining whether a graph contains a Hamiltonian path/cycle is NP-complete
- **Decision vs. Construction**: The decision problem is NP-complete; finding the actual path is harder
- **Contrast with Eulerian paths**: Eulerian paths visit each edge once; Hamiltonian paths visit each vertex once

## Intuition / Core Idea

The core challenge is finding a path that visits every vertex exactly once. Unlike Eulerian paths which depend on vertex degrees (even degrees for cycle), Hamiltonian paths have no simple degree-based conditions.

**Fundamental insight:** The problem is notoriously difficult because any subset of vertices must have edges connecting them in a way that allows the path to continue.

## Variants / Use Cases

1. **Hamiltonian Path**: Linear sequence visiting all vertices once
2. **Hamiltonian Cycle**: Closed loop visiting all vertices once
3. **Directed vs. Undirected**: Both variants exist
4. **Weighted graphs**: Often used for optimization problems (TSP)

**Real-world applications:**
- Traveling salesman problem (approximations)
- Genome assembly in bioinformatics
- Circuit board design
- Network routing optimization

## Operations / Algorithm Steps

### Backtracking Approach
1. Start with an empty path
2. For each vertex, try adding it to the path if:
   - It's not already visited
   - It's connected to the last vertex in the path (or first vertex)
3. Recursively continue until all vertices are visited
4. If a full path is found, record it
5. Backtrack and try alternative vertices

### Dynamic Programming (TSP-based)
For complete graphs, use bitmask DP similar to TSP:
1. Use state: current position + visited vertices bitmap
2. DP[state][current] = minimum cost to reach current with visited state
3. Transition: move to unvisited neighbors

## Pseudocode / Implementation

### Backtracking Solution (C++)
```cpp
class HamiltonianPath {
private:
    vector<vector<int>> graph;
    vector<bool> visited;
    vector<int> path;
    int vertices;

    bool dfs(int current, int count) {
        if (count == vertices) return true;

        for (int next : graph[current]) {
            if (!visited[next]) {
                visited[next] = true;
                path.push_back(next);

                if (dfs(next, count + 1)) return true;

                path.pop_back();
                visited[next] = false;
            }
        }
        return false;
    }

public:
    vector<int> findHamiltonianPath(vector<vector<int>>& adj) {
        graph = adj;
        vertices = adj.size();
        visited.assign(vertices, false);
        path.clear();

        // Try starting from each vertex
        for (int start = 0; start < vertices; ++start) {
            visited.assign(vertices, false);
            path = {start};
            visited[start] = true;

            if (dfs(start, 1)) return path;
        }
        return {}; // No path found
    }
};
```

### Cycle Detection
Add cycle check: `if (count == vertices && graph[current].count(start)) return true;`

## Complexity Analysis

| Algorithm                 | Time Complexity  | Space Complexity |
| ------------------------- | ---------------- | ---------------- |
| Naive Backtracking        | O(N!)            | O(N)             |
| Backtracking with pruning | O(2^N * N) worst | O(N)             |
| DP (TSP)                  | O(2^N * N^2)     | O(2^N * N)       |

**Space considerations:**
- Backtracking: O(N) for recursion stack and visited array
- DP: Exponential space due to subset states
- Practical limit: ~20-25 vertices for backtracking, ~18-20 for DP

## Example Walkthrough

**Graph:**
```
A -- B -- C
|    |    |
D -- E -- F
```

**Hamiltonian Path example:**
Start at A: A → D → E → B → C → F

**Visual steps:**
1. From A, choose adjacent vertex (possible: B, D)
2. Choose D: Path = [A, D]
3. From D, choose unvisited neighbor (possible: E)
4. Continue until all vertices visited or backtrack

**Backtracking trace:**
- A → D → E → B (C,F unvisited) → backtrack
- A → D → E → F → C → B (complete path!)

## Common Pitfalls & Edge Cases

1. **Disconnected graphs**: No Hamiltonian path possible
2. **Degree constraints**: Insufficient minimum degree (Dirac/Niere condition)
3. **Time limits**: Exponential time - need early termination
4. **Multiple solutions**: Algorithm may find one valid path, not all
5. **Start vertex choice**: Some graphs require specific starting points
6. **Stack overflow**: Deep recursion on large graphs

**Edge case examples:**
- **Single vertex**: Always has Hamiltonian path (empty path or self-loop)
- **Complete graph K_N**: Many Hamiltonian paths exist
- **Linear graph**: Only two Hamiltonian paths possible

## Extensions / Related Topics

1. **Traveling Salesman Problem**: Hamiltonian cycle with minimum weight
2. **Longest Path Problem**: NP-complete variant
3. **Graph Coloring**: Another NP-complete graph problem
4. **Approximation Algorithms**: For TSP variants
5. **Probabilistic Methods**: Finding Hamiltonian paths in random graphs
6. **Directed Acyclic Graphs**: Applications in scheduling

## Interview Tips / Talking Points

**Key discussion points:**
- NP-completeness proves solving is inherently difficult
- Backtracking works but scales poorly (N! time)
- Mention special cases: complete graphs, bipartite graphs
- Contrast with polynomial Eulerian path detection
- Discuss approximation algorithms for weighted variants

**Common interview questions:**
- "Why is this problem hard?"
- "What's the difference between Hamiltonian and Eulerian paths?"
- "How would you optimize the backtracking?"

**Red flags to avoid:**
- Claiming polynomial time solution
- Ignoring NP-completeness
- Not mentioning backtracking basics

## Leetcode Problems

| Problem                                                                                     | Difficulty | Technique             |
| ------------------------------------------------------------------------------------------- | ---------- | --------------------- |
| [Find if Path Exists in Graph](https://leetcode.com/problems/find-if-path-exists-in-graph/) | Easy       | DFS/BFS               |
| [Valid Arrangement of Pairs](https://leetcode.com/problems/valid-arrangement-of-pairs/)     | Hard       | Eulerian Path         |
| Nearest Neighbor (TSP approximation)                                                        | Medium     | Greedy                |
| [Word Ladder II](https://leetcode.com/problems/word-ladder-ii/)                             | Hard       | BFS with backtracking |

**Note:** Direct Hamiltonian path problems are rare due to NP-completeness; focus on variants and related graph problems.

## References

- [Wikipedia: Hamiltonian Path](https://en.wikipedia.org/wiki/Hamiltonian_path)
- "Computers and Intractability" by Garey and Johnson
- Sedgewick Algorithms 4th Edition
- CLRS Introduction to Algorithms
