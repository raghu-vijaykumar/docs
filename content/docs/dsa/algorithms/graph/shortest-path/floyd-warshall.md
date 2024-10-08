---
weight: 5
bookFlatSection: true
title: "Floyd Warshall - Shortest Paths"
draft: false
---

# Graph Algorithms: Floyd-Warshall Algorithm

## Introduction

The **Floyd-Warshall Algorithm** is a **shortest-path algorithm** that computes the shortest paths between all pairs of vertices in a weighted graph. Unlike algorithms like Dijkstra or Bellman-Ford, which calculate shortest paths from a single source vertex, Floyd-Warshall is designed for **all-pairs shortest paths**. It can handle graphs with **negative edge weights** but **cannot** handle graphs with **negative weight cycles**.

## Key Characteristics of Floyd-Warshall Algorithm

- **All-pairs shortest path:** Computes the shortest path between every pair of vertices in the graph.
- **Dynamic programming approach:** The algorithm uses a bottom-up dynamic programming technique to solve the problem.
- **Handles negative edge weights:** It can compute shortest paths even when the graph contains negative weights.
- **Negative cycle detection:** If a negative weight cycle exists, the algorithm can detect it by observing if the distance of any vertex to itself becomes negative.
- **Time complexity:** The time complexity is **O(V³)**, where V is the number of vertices.

## Algorithm Overview

The Floyd-Warshall algorithm works by iteratively improving the shortest path between any two vertices. The algorithm considers each vertex as an intermediate vertex in the path between two other vertices, and it checks whether a path through this intermediate vertex offers a shorter route.

### Steps

1. **Initialize the distance matrix:**

   - Create a matrix `dist[][]` where `dist[i][j]` is the shortest distance from vertex `i` to vertex `j`. Initialize the matrix with the weights of the edges between the vertices. If no direct edge exists between two vertices, initialize the distance as infinity (`∞`), except for the diagonal elements (`dist[i][i] = 0`).

2. **Relax the edges:**

   - For each vertex `k` (considered as an intermediate vertex), for each pair of vertices `i` and `j`, update the distance from `i` to `j` to the minimum of the current distance and the distance through `k`. The formula is: `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])`

3. **Detect negative weight cycles**:
   After processing all vertices, if any diagonal element of the distance matrix (`dist[i][i]`) becomes negative, it indicates the presence of a negative weight cycle.

## Floyd-Warshall Algorithm: Pseudocode

```python
def floyd_warshall(graph):
# Step 1: Initialize the distance matrix
V = len(graph)
dist = [[float('infinity')] * V for _ in range(V)]

for i in range(V):
    for j in range(V):
        if i == j:
            dist[i][j] = 0
        elif graph[i][j] != 0:  # assuming graph[i][j] is non-zero if there is an edge
            dist[i][j] = graph[i][j]

# Step 2: Update distances using Floyd-Warshall Algorithm
for k in range(V):
    for i in range(V):
        for j in range(V):
            dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])

# Step 3: Check for negative weight cycles
for i in range(V):
    if dist[i][i] < 0:
        raise ValueError("Graph contains a negative-weight cycle")

return dist
```

**Example**

```python
# Example graph with positive and negative weights

graph = [
[0, 3, float('inf'), 5],
[2, 0, float('inf'), 4],
[float('inf'), 1, 0, float('inf')],
[float('inf'), float('inf'), 2, 0]
]

distances = floyd_warshall(graph)
for row in distances:
print(row)
```

**Explanation**
In this example:

- The algorithm starts with an initial distance matrix based on the given graph.
- It iteratively updates the matrix to reflect the shortest paths between all pairs of vertices by considering intermediate vertices.
- The final matrix contains the shortest distances between all pairs.

## Time Complexity

The time complexity of Floyd-Warshall is:

`O(V³)`, where V is the number of vertices. The algorithm has three nested loops iterating over the number of vertices. It is less efficient for large graphs compared to other algorithms designed for specific shortest path tasks.

## Space Complexity

The space complexity is `O(V²)`, which is required to store the distance matrix of size `VxV`.

## Floyd-Warshall Algorithm: Key Use Cases

- **All-Pairs Shortest Paths**: It is suitable when we need to calculate the shortest paths between all pairs of vertices, making it ideal for dense graphs where this information is needed.
- **Detecting Negative Cycles**: Floyd-Warshall can detect negative weight cycles by observing the diagonal of the distance matrix.
- **Transitive Closure of a Graph**: The algorithm can also be modified to compute the transitive closure of a graph, which helps in understanding reachability in a graph.
- **Routing Algorithms**: The algorithm is applied in routing protocols to find the shortest path between every pair of nodes, such as in computer networks or geographical mapping systems.

## Floyd-Warshall vs Other Algorithms

| **Feature**                  | **Floyd-Warshall**                              | **Dijkstra's Algorithm**                  | **Bellman-Ford**                              |
| ---------------------------- | ----------------------------------------------- | ----------------------------------------- | --------------------------------------------- |
| **Purpose**                  | All-pairs shortest paths                        | Single-source shortest paths              | Single-source shortest paths                  |
| **Graph Type**               | Can handle graphs with negative weights         | Only works with non-negative edge weights | Can handle graphs with negative weights       |
| **Time Complexity**          | O(V³)                                           | O((V + E) log V) with a priority queue    | O(V \* E)                                     |
| **Negative Cycle Detection** | Can detect negative cycles by checking diagonal | Cannot detect negative cycles             | Can detect negative cycles                    |
| **Typical Use Case**         | Dense graphs with many vertices and edges       | Sparse graphs                             | Sparse graphs, especially with negative edges |

### When to Use Floyd-Warshall

- **All-Pairs Shortest Paths**: If you need the shortest path between all pairs of vertices in a dense graph, Floyd-Warshall is appropriate.
- **Graph with Negative Edges**: It works well in graphs with negative weights and can detect negative weight cycles.
- **Transitive Closure and Reachability Analysis**: The algorithm can also be adapted for reachability queries, making it versatile for more than just shortest paths.

## Negative Weight Cycles

### What is a Negative Weight Cycle?

A negative weight cycle is a cycle in a graph where the sum of the edge weights in the cycle is negative. If a graph contains such a cycle, there is no well-defined shortest path because the total weight can be reduced indefinitely by traversing the cycle repeatedly.

### How Floyd-Warshall Detects It

The algorithm can detect negative weight cycles by checking the diagonal of the distance matrix after running the algorithm. If any element dist[i][i] < 0, it indicates that there is a negative weight cycle involving vertex i.

## Applications of Floyd-Warshall Algorithm

- **Network Routing**: In computer networks, the algorithm can be used to find the shortest path between any pair of nodes for efficient routing of packets.
- **Flight Scheduling**: In airline networks, Floyd-Warshall can calculate the shortest or fastest route between any two cities, taking into account layovers, flight times, and costs.
- **Transitive Closure**: It is also used in determining the reachability of nodes in a graph, which has applications in database systems, compiler design, and social network analysis.
- **Graph Analysis**: It is useful for analyzing and solving problems in dense graphs, where many vertices are interconnected, and shortest path calculations between all pairs are necessary.

## Conclusion

The Floyd-Warshall Algorithm is a powerful algorithm for computing the all-pairs shortest paths in a graph. It is particularly useful for handling graphs with negative edge weights and detecting negative weight cycles. Although its time complexity of `O(V³)` makes it less efficient for large, sparse graphs, it remains the go-to algorithm for dense graphs and scenarios where all-pairs shortest path information is required.
