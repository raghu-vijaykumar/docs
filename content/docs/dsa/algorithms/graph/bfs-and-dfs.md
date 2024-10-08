---
weight: 1
bookFlatSection: true
title: "BFS & DFS"
draft: false
---

# Graph Algorithms: BFS & DFS

## Introduction

Graphs are widely used to represent real-world systems such as social networks, transportation networks, and web page links. Two fundamental algorithms used for exploring and traversing graphs are **Breadth-First Search (BFS)** and **Depth-First Search (DFS)**.

Both **BFS** and **DFS** are important for solving various graph problems, including:

- Pathfinding
- Cycle detection
- Connected components detection
- Topological sorting
- Shortest path in unweighted graphs

## 1. Breadth-First Search (BFS)

### Overview

**Breadth-First Search (BFS)** is a graph traversal algorithm that explores the vertices layer by layer. It starts from a source vertex and explores all neighboring vertices before moving to the next layer of vertices. This makes BFS an ideal algorithm for finding the shortest path in unweighted graphs.

### Key Characteristics

- **Explores nodes in layers:** It explores all nodes at the present depth before moving on to the nodes at the next depth level.
- **Queue-based:** BFS uses a queue to keep track of the vertices to be explored.
- **Unweighted shortest path:** BFS finds the shortest path in terms of the number of edges in an unweighted graph.

### Algorithm

1. Start from a given source vertex `s`.
2. Mark `s` as visited and enqueue it.
3. While the queue is not empty:
   - Dequeue a vertex `v`.
   - For each unvisited neighbor `u` of `v`, mark it as visited and enqueue `u`.

### BFS Pseudocode

```python
from collections import deque

def bfs(graph, start):
    visited = set()
    queue = deque([start])
    visited.add(start)

    while queue:
        vertex = queue.popleft()
        print(vertex, end=' ')

        for neighbor in graph[vertex]:
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)
```

Example

```python
# Example graph represented as an adjacency list
graph = {
    'A': ['B', 'C'],
    'B': ['D', 'E'],
    'C': ['F'],
    'D': [],
    'E': ['F'],
    'F': []
}

bfs(graph, 'A')  # Output: A B C D E F
```

### Time and Space Complexity

- Time Complexity: `O(V + E)`, where V is the number of vertices and E is the number of edges.
- Space Complexity: `O(V)`, for storing the queue and visited set.

### Applications of BFS

- **Shortest Path in Unweighted Graphs**: BFS finds the shortest path between two vertices in terms of the number of edges.
- **Connected Components**: BFS can be used to find all vertices in a connected component.
- **Level-Order Traversal of Trees**: In tree-like structures, BFS traverses nodes level by level.

## Depth-First Search (DFS)

Depth-First Search (DFS) is a graph traversal algorithm that explores as far as possible along a branch before backtracking. It uses a recursive or stack-based approach to traverse the graph. DFS is widely used for problems related to connectivity, pathfinding, and graph cycle detection.

### Key Characteristics

- Explores deep into the graph: DFS dives into the graph by following edges as far as possible before backtracking.
- Stack-based: DFS can be implemented using recursion or a manual stack.
- Useful for backtracking problems: DFS can be used to explore all possible paths and is effective for problems where backtracking is required.

### Algorithm

- Start from a given source vertex `s`.
- Mark `s` as visited.
- Recursively visit all unvisited neighbors of `s`.

### DFS Pseudocode (Recursive)

```python
def dfs(graph, vertex, visited=None):
if visited is None:
visited = set()

    visited.add(vertex)
    print(vertex, end=' ')

    for neighbor in graph[vertex]:
        if neighbor not in visited:
            dfs(graph, neighbor, visited)
```

### DFS Pseudocode (Iterative)

```python
def dfs_iterative(graph, start):
visited = set()
stack = [start]

    while stack:
        vertex = stack.pop()
        if vertex not in visited:
            print(vertex, end=' ')
            visited.add(vertex)
            stack.extend(reversed(graph[vertex]))  # Reverse to maintain order similar to recursion
```

**Example**

```python
# Example graph represented as an adjacency list

graph = {
'A': ['B', 'C'],
'B': ['D', 'E'],
'C': ['F'],
'D': [],
'E': ['F'],
'F': []
}

dfs(graph, 'A') # Output: A B D E F C
```

### Time and Space Complexity

- **Time Complexity**: `O(V + E)`, where V is the number of vertices and E is the number of edges.
- **Space Complexity**: `O(V)`, for storing the recursion stack or explicit stack and the visited set.

### Applications of DFS

- **Cycle Detection**: DFS can detect cycles in a graph. If a back edge is encountered, it indicates a cycle.
- **Topological Sorting**: In Directed Acyclic Graphs (DAGs), DFS can be used to perform a topological sort.
- **Connected Components**: DFS can be used to find all vertices in a connected component.
- **Path Finding**: DFS explores all possible paths, making it useful in scenarios requiring exploration of all paths, like in maze solving.
- **Graph Coloring and Bipartiteness**: DFS can be used to solve problems related to graph coloring and detecting if a graph is bipartite.

## BFS vs DFS

| **Feature**                           | **BFS**                                                    | **DFS**                                           |
| ------------------------------------- | ---------------------------------------------------------- | ------------------------------------------------- |
| **Traversal Approach**                | Layer by layer (breadth-wise)                              | Depth-wise (deep into branches)                   |
| **Data Structure**                    | Queue                                                      | Stack (or recursion)                              |
| **Shortest Path in Unweighted Graph** | Yes                                                        | No                                                |
| **Memory Usage**                      | More (stores all neighbors)                                | Less (stacks only current path)                   |
| **Use Case**                          | Shortest path, level-order traversal, connected components | Pathfinding, cycle detection, topological sorting |

## Use Cases and Applications

1. **Shortest Path in Unweighted Graphs (BFS)**
   BFS guarantees the shortest path in terms of edges in unweighted graphs. It is widely used in navigation systems, finding the shortest route in transportation networks, or solving simple puzzles like the shortest path in a maze.

2. **Cycle Detection (DFS)**
   DFS is useful in detecting cycles in both directed and undirected graphs. In directed graphs, if a back edge is encountered (an edge that points to an ancestor in the DFS tree), then a cycle exists.

3. **Topological Sorting (DFS)**
   DFS is the basis for topological sorting in Directed Acyclic Graphs (DAGs). By performing a DFS and pushing nodes to a stack after all their neighbors are visited, the stack gives the topologically sorted order.

4. **Connected Components (BFS/DFS)**
   Both BFS and DFS can be used to explore all the nodes in a connected component, and thus can be used to count the number of connected components in a graph.

5. **Flood Fill Algorithm (DFS/BFS)**
   Flood fill is a classic problem (e.g., in image processing, or painting programs) where both BFS and DFS are used to "fill" connected regions starting from a point. The same algorithm can be applied to maze solving or finding connected areas in a 2D grid.

## Conclusion

BFS and DFS are foundational graph traversal algorithms used to explore vertices and edges in various ways. BFS is ideal for finding the shortest path in unweighted graphs, while DFS is well-suited for problems that require deep exploration, such as cycle detection, topological sorting, and backtracking problems. Understanding the trade-offs between BFS and DFS is key to selecting the right algorithm for a given graph problem.
