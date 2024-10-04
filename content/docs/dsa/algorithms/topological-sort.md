# Topological Sort

## Overview

Topological Sort is a linear ordering of vertices in a **Directed Acyclic Graph (DAG)**, where for every directed edge `U -> V`, vertex `U` comes before vertex `V` in the ordering. This algorithm is useful for tasks that involve dependency resolution, such as build systems, course prerequisite chains, and task scheduling.

## Characteristics

- **DAG (Directed Acyclic Graph):** Topological sort can only be applied to a DAG, as the presence of cycles would make it impossible to determine a valid ordering.
- **Ordering:** It provides an order of vertices such that for every edge `U -> V`, vertex `U` comes before `V`.
- **Non-uniqueness:** A graph can have multiple valid topological sorts.

## Applications

- **Course Scheduling:** Determining the order in which courses should be taken given prerequisite requirements.
- **Task Scheduling:** Resolving dependencies between tasks in a project.
- **Build Systems:** Compiling a list of files to be built in the correct order, considering dependencies among source files.

## Algorithms for Topological Sort

There are two main approaches to finding the topological sort of a DAG:

### 1. Kahn’s Algorithm (BFS Approach)

#### Steps:

1. **In-degree Calculation:** Calculate the in-degree (number of incoming edges) for each vertex.
2. **Initialize Queue:** Add all vertices with in-degree `0` to a queue (these vertices do not depend on any other vertices).
3. **Process Vertices:**
   - Remove a vertex from the queue, add it to the topological sort result.
   - Decrease the in-degree of all its adjacent vertices.
   - If any adjacent vertex’s in-degree becomes `0`, add it to the queue.
4. **Repeat** until the queue is empty.

#### Time Complexity:

- **O(V + E)**, where `V` is the number of vertices and `E` is the number of edges.

#### Example Code (Python):

```python
from collections import deque, defaultdict

def kahn_topological_sort(graph, n):
    in_degree = [0] * n
    for u in range(n):
        for v in graph[u]:
            in_degree[v] += 1

    queue = deque([u for u in range(n) if in_degree[u] == 0])
    topo_order = []

    while queue:
        u = queue.popleft()
        topo_order.append(u)

        for v in graph[u]:
            in_degree[v] -= 1
            if in_degree[v] == 0:
                queue.append(v)

    if len(topo_order) == n:
        return topo_order
    else:
        return []  # If cycle exists

# Example usage
graph = {0: [1], 1: [2], 2: [], 3: [0, 1]}
n = 4
print(kahn_topological_sort(graph, n))
```

### 2. Depth-First Search (DFS) Approach

Steps:

- **Perform DFS**: Starting from an unvisited vertex, recursively visit all adjacent vertices.
- **Post-order Insertion**: Once a vertex has no more adjacent vertices to visit, add it to the result stack.
- **Repeat**: Continue the process for all unvisited vertices.
- **Reverse the Stack**: The vertices will be in reverse topological order in the stack.

#### Time Complexity:

`O(V + E)`, where `V` is the number of vertices and `E` is the number of edges.
Example Code (Python):

```python
def dfs_topological_sort(graph, n):
    visited = [False] * n
    stack = []

    def dfs(u):
        visited[u] = True
        for v in graph[u]:
            if not visited[v]:
                dfs(v)
        stack.append(u)

    for u in range(n):
        if not visited[u]:
            dfs(u)

    return stack[::-1]  # Return the reversed stack

# Example usage
graph = {0: [1], 1: [2], 2: [], 3: [0, 1]}
n = 4
print(dfs_topological_sort(graph, n))
```

### Detecting Cycles in a Directed Graph

Since topological sort is only applicable to DAGs, it is important to check if the graph contains a cycle.

**Kahn’s Algorithm**: If the number of vertices in the topological order is less than V (number of vertices in the graph), then the graph contains a cycle.
**DFS Approach**: If during DFS a vertex is visited again before the recursive call completes (i.e., if it's part of the current recursion stack), then the graph contains a cycle.

**Example (Cycle Detection using DFS)**

```python
def is_cyclic(graph, n):
    visited = [False] * n
    recursion_stack = [False] * n

    def dfs(u):
        visited[u] = True
        recursion_stack[u] = True

        for v in graph[u]:
            if not visited[v] and dfs(v):
                return True
            elif recursion_stack[v]:
                return True

        recursion_stack[u] = False
        return False

    for u in range(n):
        if not visited[u]:
            if dfs(u):
                return True
    return False

# Example usage
graph = {0: [1], 1: [2], 2: [0], 3: []}
n = 4
print(is_cyclic(graph, n))  # Output: True (cycle detected)
```

## Conclusion

Topological sort is a fundamental algorithm used for ordering tasks that depend on each other. It requires a DAG and can be implemented using either Kahn's algorithm (BFS) or a DFS approach. The algorithm is efficient, with a time complexity of O(V + E), and has widespread applications in real-world problems such as task scheduling, build systems, and more.
