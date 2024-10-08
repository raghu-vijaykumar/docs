---
weight: 3
bookFlatSection: true
title: "Dijkstra - Shortest Path"
draft: false
---

# Graph Algorithms: Dijkstra's Algorithm

**Dijkstra's Algorithm** is one of the most well-known algorithms for finding the shortest path between nodes in a graph, especially useful for graphs with **non-negative edge weights**. It is a **greedy algorithm** that solves the **single-source shortest path** problem, i.e., it finds the shortest path from a given source vertex to all other vertices in the graph.

## Key Characteristics of Dijkstra's Algorithm

- **Single-source shortest path algorithm:** Finds the shortest path from a source vertex to all other vertices in the graph.
- **Non-negative weights:** The algorithm assumes that all edge weights are non-negative.
- **Greedy approach:** Dijkstra's Algorithm uses a greedy approach by selecting the vertex with the smallest known distance to expand the search.
- **Priority Queue (Heap) based implementation:** Dijkstra’s algorithm can be efficiently implemented using a priority queue (min-heap).

## Algorithm Overview

### Steps

1. **Initialization:**

   - Set the distance of the source vertex to 0 and all other vertices to infinity (∞).
   - Mark all vertices as unvisited. Set the source vertex as the current node.
   - Create a priority queue (min-heap) to hold all vertices and their current distances.

2. **Exploration:**

   - For the current vertex, consider all its unvisited neighbors. Calculate their tentative distances through the current vertex.
   - If a shorter path to a neighbor is found, update the shortest distance.

3. **Update:**

   - After examining all neighbors of the current vertex, mark it as visited.
   - Pick the next vertex with the smallest tentative distance from the unvisited vertices, and repeat the process until all vertices have been visited.

4. **Termination:**
   - The algorithm terminates when all vertices have been visited or when the shortest path to all reachable vertices has been found.

---

## Dijkstra's Algorithm: Pseudocode

```python
import heapq

def dijkstra(graph, start):
    # Initialize distances with infinity and set the distance for the start node as 0
    distances = {vertex: float('infinity') for vertex in graph}
    distances[start] = 0

    # Priority queue to select the next vertex with the smallest distance
    priority_queue = [(0, start)]

    while priority_queue:
        current_distance, current_vertex = heapq.heappop(priority_queue)

        # If the popped vertex distance is larger than the current known distance, skip it
        if current_distance > distances[current_vertex]:
            continue

        # Explore neighbors of the current vertex
        for neighbor, weight in graph[current_vertex].items():
            distance = current_distance + weight

            # Only consider this path if it's better than the previously known path
            if distance < distances[neighbor]:
                distances[neighbor] = distance
                heapq.heappush(priority_queue, (distance, neighbor))

    return distances
```

**Example**

```python
# Example graph represented as an adjacency list
graph = {
    'A': {'B': 1, 'C': 4},
    'B': {'A': 1, 'C': 2, 'D': 5},
    'C': {'A': 4, 'B': 2, 'D': 1},
    'D': {'B': 5, 'C': 1}
}

distances = dijkstra(graph, 'A')
print(distances)  # Output: {'A': 0, 'B': 1, 'C': 3, 'D': 4}
```

**Explanation**
In this example:

- Starting from vertex A, the shortest path to B is 1 (A -> B).
- The shortest path to C is 3 (A -> B -> C).
- The shortest path to D is 4 (A -> B -> C -> D).

## Time Complexity

The time complexity of Dijkstra's Algorithm depends on the implementation of the priority queue:

- Using a simple array-based priority queue: `O(V^2)`, where V is the number of vertices.
- Using a binary heap (min-heap): `O((V + E) log V)`, where V is the number of vertices and E is the number of edges. This is the most efficient implementation in practice, particularly for sparse graphs.

## Space Complexity

- Space Complexity: `O(V + E)`, where `V` is the number of vertices and `E` is the number of edges. This accounts for the storage of the graph, distance array, and the priority queue.

## Limitations of Dijkstra’s Algorithm

- **Non-negative Weights**: Dijkstra’s Algorithm does not work correctly with graphs that have negative edge weights. If negative weights are present, algorithms like Bellman-Ford should be used instead.
  **Single-Source Only**: Dijkstra's algorithm finds the shortest path from one source vertex. If multiple sources are needed, other algorithms such as Floyd-Warshall may be better suited.
  **Inefficiency for Dense Graphs**: For dense graphs, where the number of edges (E) is close to the square of the number of vertices `V`, Dijkstra’s time complexity can become inefficient.

## Optimizations

1. **Priority Queue Optimization:**
   The most common and efficient way to implement Dijkstra’s Algorithm is by using a min-heap (binary heap) as the priority queue, which reduces the time complexity to O((V + E) log V).

2. **Fibonacci Heap:**
   In theoretical settings, Dijkstra’s Algorithm can be implemented using a Fibonacci heap, which offers better amortized time complexity of `O(V log V + E)`.

## Applications of Dijkstra's Algorithm

1. **Routing in Network Communication**: Dijkstra's Algorithm is often used in routing algorithms to find the shortest path between nodes in a computer network. It is the foundation of the Open Shortest Path First (OSPF) routing protocol.

2. **Google Maps and GPS Navigation:** In navigation systems, Dijkstra’s Algorithm is used to find the shortest path between two locations on a map, factoring in the distance between road segments.

3. **Flight Scheduling:** Airline flight scheduling systems use Dijkstra’s Algorithm to find the shortest travel times or least-cost routes between airports.

4. **Telecommunications:** In telecom networks, Dijkstra’s Algorithm is used to find the shortest paths between various communication towers or to optimize the design of networks.

## Variations of Dijkstra's Algorithm

1. **Bidirectional Dijkstra:**
   In scenarios where you need to find the shortest path between two specific vertices, a bidirectional Dijkstra can be used. This variant runs two simultaneous Dijkstra searches: one from the source and one from the target. The algorithm stops when the two searches meet.

2. **A Algorithm:\***
   The A algorithm\* is a variant of Dijkstra's Algorithm that uses a heuristic to speed up the search. It combines the greedy approach of Dijkstra with a heuristic function that estimates the distance to the goal, making it useful for real-time pathfinding.

3. **Bellman-Ford Algorithm:**
   While Dijkstra's Algorithm is efficient, it cannot handle negative edge weights. The Bellman-Ford Algorithm is a more flexible option that can handle negative weights but is slower than Dijkstra's.

## Conclusion

Dijkstra's Algorithm is a fundamental algorithm for solving the single-source shortest path problem in graphs with non-negative weights. Its efficiency and simplicity make it widely used in various applications such as routing, navigation, and network optimization. However, for graphs with negative weights or very dense graphs, other algorithms may be more appropriate.
