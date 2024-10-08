---
weight: 1
bookFlatSection: true
title: "Prim's Algorithm - MST"
draft: false
---

# Graph Algorithms: Prim's Algorithm

**Prim's Algorithm** is a **greedy algorithm** used to find the **Minimum Spanning Tree (MST)** of a weighted, undirected graph. A Minimum Spanning Tree is a subset of the edges that connects all the vertices of a graph together without any cycles and with the minimum possible total edge weight.

Prim’s Algorithm builds the MST by starting with a single vertex and expanding it one edge at a time, always choosing the smallest edge that connects a vertex in the MST to a vertex outside the MST.

## Key Characteristics of Prim's Algorithm

- **Greedy Approach:** At each step, Prim's selects the smallest edge that extends the MST to a new vertex.
- **Spanning Tree:** Prim’s constructs a **Minimum Spanning Tree (MST)** which connects all vertices with the minimum edge cost.
- **Graph Type:** Prim’s works on weighted, undirected graphs. It requires the graph to be connected.
- **Priority Queue:** A priority queue (or heap) is typically used to efficiently select the minimum weight edge.
- **Time Complexity:** O(E log V), where E is the number of edges, and V is the number of vertices, using a priority queue.

## Prerequisites

- **Graph:** The graph must be undirected and weighted, containing `V` vertices and `E` edges.
- **Minimum Spanning Tree (MST):** A spanning tree that covers all vertices and has the minimum possible sum of edge weights.

## Algorithm Overview

### Steps of Prim’s Algorithm:

1. **Initialize:** Start with an arbitrary vertex, mark it as part of the MST, and add its adjacent edges to a priority queue.
2. **Choose the Minimum Edge:** From the priority queue, select the smallest edge that connects the current MST to a vertex outside the MST.
3. **Add the Edge to MST:** Add the chosen edge and the new vertex to the MST.
4. **Update Priority Queue:** Add the edges of the newly included vertex to the priority queue.
5. **Repeat:** Continue this process until all vertices are included in the MST.

### Greedy Choice

Prim's algorithm follows a greedy strategy where the algorithm always chooses the smallest possible edge to expand the MST.

## Prim’s Algorithm: Pseudocode

```python
import heapq

# Function to perform Prim's algorithm on a graph
def prim(graph, V):
    # Resulting MST
    mst = []

    # Priority queue to select the smallest edge at each step
    pq = [(0, 0)]  # (cost, vertex), starting from vertex 0

    # Visited vertices
    visited = [False] * V
    total_cost = 0

    while pq:
        # Pick the smallest edge
        cost, u = heapq.heappop(pq)

        if visited[u]:
            continue

        # Mark vertex u as visited
        visited[u] = True
        total_cost += cost
        mst.append((u, cost))

        # Add all edges connected to the newly visited vertex
        for v, weight in graph[u]:
            if not visited[v]:
                heapq.heappush(pq, (weight, v))

    return total_cost, mst
```

**Explanation**:
The graph is represented as an adjacency list.

- A priority queue (pq) is used to efficiently get the smallest edge.
- The visited list keeps track of which vertices are already part of the MST.
- The algorithm starts at an arbitrary vertex (in this case, vertex 0), and continues to grow the MST by choosing the smallest available edge that connects to an unvisited vertex.

**Example**
Input Graph
Consider the following weighted undirected graph:

```lua
    2     3
0-------1-------2
|       |       |
|   6   |   5   |
|       |       |
3-------4-------5
    8     9
```

**Adjacency List Representation**

```python
# Graph represented as an adjacency list (u, v, weight)
graph = {
    0: [(1, 2), (3, 6)],
    1: [(0, 2), (2, 3), (4, 8)],
    2: [(1, 3), (5, 5)],
    3: [(0, 6), (4, 8)],
    4: [(1, 8), (3, 8), (5, 9)],
    5: [(2, 5), (4, 9)]
}

V = 6  # Number of vertices
total_cost, mst = prim(graph, V)

print(f"Total cost of MST: {total_cost}")
print("Edges in MST:")
for u, cost in mst:
    print(f"Vertex: {u}, Cost: {cost}")
```

**Output**

```yaml
Total cost of MST: 24
Edges in MST:
Vertex: 0, Cost: 0
Vertex: 1, Cost: 2
Vertex: 2, Cost: 3
Vertex: 3, Cost: 6
Vertex: 5, Cost: 5
Vertex: 4, Cost: 8
```

This is the Minimum Spanning Tree (MST) with a total cost of 24.

## Time Complexity

The time complexity of Prim's Algorithm is:

`O(E log V)` when using a priority queue, where:

- `E` is the number of edges in the graph.
- `V` is the number of vertices.
  The reason for this complexity is:
- Extracting the minimum edge takes `O(log V)` using a priority queue.
- Inserting all edges into the queue takes `O(E log V)` time.

## Space Complexity

The space complexity of Prim’s Algorithm is `O(V + E)`:

- `O(V)` for storing the visited array.
- `O(E)` for storing the adjacency list of the graph.

## Prim’s Algorithm vs Kruskal’s Algorithm

| **Feature**         | **Kruskal's Algorithm**                  | **Prim's Algorithm**                       |
| ------------------- | ---------------------------------------- | ------------------------------------------ |
| **Approach**        | Greedy approach, selects edges by weight | Greedy approach, selects edges from a tree |
| **Graph Type**      | Works well with sparse graphs            | Works well with dense graphs               |
| **Data Structure**  | Uses Union-Find for cycle detection      | Uses a priority queue or heap              |
| **Time Complexity** | O(E log E)                               | O(E log V) with a priority queue           |
| **Cycle Detection** | Uses Union-Find                          | No explicit cycle detection is required    |

## When to Use Prim's Algorithm

- **Dense Graphs**: Prim’s algorithm is often more efficient for dense graphs because it can directly extend the MST without needing to sort edges.
- **Adjacency List Representation**: When the graph is stored as an adjacency list, Prim’s can be implemented more efficiently with a priority queue.

## Prim’s Algorithm: Key Use Cases

- **Network Design**: Prim’s Algorithm is useful in designing networks, such as laying cables to connect computers, where the goal is to minimize the total length or cost.
- **Approximating NP-Hard Problems**: Like Kruskal’s, Prim’s can be used as a heuristic to solve or approximate solutions to NP-hard problems like the Traveling Salesman Problem (TSP).
- **Clustering Algorithms**: Prim's algorithm can be applied in clustering techniques, where the graph is split into smaller regions.

## Conclusion

Prim’s Algorithm is a fundamental greedy algorithm used to find the Minimum Spanning Tree (MST) of a connected, weighted graph. It uses a priority queue to efficiently select the minimum weight edge at each step, making it particularly well-suited for dense graphs. Prim’s is an efficient method for constructing the MST and is used in various real-world applications such as network design and clustering.
