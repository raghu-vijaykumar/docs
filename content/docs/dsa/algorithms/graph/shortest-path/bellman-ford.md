---
weight: 4
bookFlatSection: true
title: "Bellman Ford - Shortest Path"
draft: false
---

# SSSP: Bellman-Ford Algorithm

The **Bellman-Ford Algorithm** is a popular algorithm used to compute the **shortest paths** from a single source vertex to all other vertices in a weighted graph. Unlike Dijkstra’s Algorithm, **Bellman-Ford** can handle graphs with **negative weight edges**, making it more versatile but generally slower. The algorithm also detects the presence of **negative weight cycles**, which Dijkstra’s algorithm cannot.

## Key Characteristics of Bellman-Ford Algorithm

- **Single-source shortest path algorithm:** It computes the shortest paths from a single source vertex to all other vertices.
- **Handles negative weights:** It can work with graphs that contain negative edge weights.
- **Detects negative weight cycles:** The algorithm can detect if a graph contains a cycle whose total weight is negative.
- **Dynamic Programming approach:** Bellman-Ford works by relaxing edges repeatedly, updating the shortest path estimates.

## Algorithm Overview

### Steps

1. **Initialization:**

   - Set the distance for the source vertex to `0` and all other vertices to infinity (`∞`).
   - Create a list or array to store the shortest distance from the source to every other vertex.

2. **Edge Relaxation:**

   - For each edge, if the distance to the destination vertex can be shortened by taking the edge, update the shortest distance. This process is repeated for **V - 1 times** (where V is the number of vertices).

3. **Negative Cycle Detection:**
   - After the edge relaxation, perform one more pass over all edges. If any distance can still be shortened, it means there is a **negative weight cycle** in the graph.

## Bellman-Ford Algorithm: Pseudocode

```python
def bellman_ford(graph, source):
    # Step 1: Initialize distances from source to all vertices as infinite except source itself
    distance = {vertex: float('infinity') for vertex in graph}
    distance[source] = 0

    # Step 2: Relax all edges |V| - 1 times
    for _ in range(len(graph) - 1):
        for u in graph:
            for v, weight in graph[u].items():
                if distance[u] + weight < distance[v]:
                    distance[v] = distance[u] + weight

    # Step 3: Check for negative-weight cycles
    for u in graph:
        for v, weight in graph[u].items():
            if distance[u] + weight < distance[v]:
                raise ValueError("Graph contains a negative-weight cycle")

    return distance
```

**Example**

```python
# Example graph with negative weight
graph = {
    'A': {'B': -1, 'C': 4},
    'B': {'C': 3, 'D': 2, 'E': 2},
    'C': {},
    'D': {'B': 1, 'C': 5},
    'E': {'D': -3}
}

distances = bellman_ford(graph, 'A')
print(distances)  # Output: {'A': 0, 'B': -1, 'C': 2, 'D': -2, 'E': 1}
```

**Explanation**
In this example:

- Starting from vertex A, the algorithm finds the shortest path to all other vertices, even though there are negative edge weights.
- The algorithm ensures that no negative weight cycle exists by verifying all edges in the final pass.

## Time Complexity

The time complexity of the Bellman-Ford algorithm is:

`O(V * E)`, where `V` is the number of vertices and `E` is the number of edges. This complexity arises because the algorithm relaxes all edges for `V - 1` iterations, and in each iteration, it processes all edges.

## Space Complexity

The space complexity of the algorithm is `O(V)`, where `V` is the number of vertices. This space is used to store the shortest distance estimates for each vertex.

## Bellman-Ford vs Dijkstra's Algorithm

| **Feature**                  | **Bellman-Ford**               | **Dijkstra's Algorithm**                  |
| ---------------------------- | ------------------------------ | ----------------------------------------- |
| **Edge Weights**             | Supports negative weights      | Assumes non-negative weights only         |
| **Negative Cycle Detection** | Detects negative weight cycles | Cannot detect negative cycles             |
| **Efficiency**               | Slower `O(V \* E)`             | Faster `O((V + E) log V)` with a min-heap |
| **Approach**                 | Dynamic Programming            | Greedy                                    |

### Key Differences

- **Bellman-Ford** is used when the graph contains negative weight edges or when we need to detect negative weight cycles.
- **Dijkstra's Algorithm** is more efficient but cannot handle negative edge weights.

## Use Cases of Bellman-Ford Algorithm

- **Detecting Negative Weight Cycles**: Bellman-Ford is commonly used to detect negative weight cycles in graphs. This is crucial in certain applications like currency arbitrage where negative cycles can represent opportunities for profit.
- **Routing Algorithms**: Bellman-Ford is used in networking for routing algorithms like the Distance Vector Routing Protocol (e.g., RIP - Routing Information Protocol).
- **Finding Shortest Path in General Graphs**: In scenarios where graphs may contain negative weights, Bellman-Ford is a safer alternative to Dijkstra’s Algorithm, providing correct results even in the presence of such weights.

## Negative Weight Cycles

### What is a Negative Weight Cycle?

A negative weight cycle is a cycle in a graph where the sum of the edge weights in the cycle is negative. If a negative weight cycle exists, there is no well-defined shortest path because the total weight can be reduced indefinitely by traveling around the cycle multiple times.

### How Bellman-Ford Detects It:

After V - 1 relaxations, the Bellman-Ford algorithm performs one more iteration over the edges. If any distance can still be reduced, a negative weight cycle exists, because the shortest path distances should have stabilized after V - 1 iterations.

**Example of a Negative Weight Cycle**
Consider the following graph:

```text
A --(-2)--> B
^ /
\ /
+--(-3)
```

In this graph:

The cycle `A -> B -> A` has a total weight of -5, which is a negative weight cycle. If the algorithm doesn't detect and prevent this, the shortest path distances will keep decreasing.

## Applications of Bellman-Ford Algorithm

- **Currency Arbitrage**: In financial systems, Bellman-Ford can detect arbitrage opportunities by modeling exchange rates as graph edges with weights, where a negative cycle indicates an arbitrage opportunity.
- **Telecommunication Networks**: Bellman-Ford is used in Distance Vector Routing protocols for routing messages across networks, where it helps determine the shortest path between nodes and detects issues like routing loops.
- **Graph Analysis**: In any domain where negative weights or cycles need to be handled, Bellman-Ford is the go-to algorithm. Examples include evaluating delays in a weighted network, finding optimal strategies in game theory, or risk management in economic models.

## Conclusion

The Bellman-Ford Algorithm is a versatile and powerful graph algorithm for solving the single-source shortest path problem, especially in graphs with negative edge weights. Its ability to detect negative weight cycles and handle negative weights sets it apart from other algorithms like Dijkstra. While it is less efficient in terms of time complexity, it remains essential in scenarios where graph structures are more complex.
