---
weight: 1
bookFlatSection: true
title: "Kruskal's Algorithm - MST"
draft: false
---

# Graph Algorithms: Kruskal's Algorithm

**Kruskal's Algorithm** is a **greedy algorithm** used to find the **Minimum Spanning Tree (MST)** of a graph. The MST is a subset of the edges that connects all the vertices together without forming any cycles and with the minimum possible total edge weight.

Kruskal’s Algorithm works by selecting the smallest edge from the graph and adding it to the spanning tree, ensuring no cycles are formed. It continues adding edges in this manner until all vertices are connected.

## Key Characteristics of Kruskal's Algorithm

- **Greedy Approach:** It picks the smallest available edge at each step.
- **Spanning Tree:** The algorithm constructs a **Minimum Spanning Tree** (MST), a subset of edges that connects all vertices with minimal total edge weight.
- **Disjoint Set Data Structure:** To efficiently detect cycles, Kruskal’s Algorithm uses the **Disjoint Set Union (DSU)**, also known as **Union-Find**.
- **Graph Type:** Works on both connected and disconnected graphs. If the graph is disconnected, Kruskal’s will find a **Minimum Spanning Forest**.
- **Time Complexity:** O(E log E), where E is the number of edges, due to sorting the edges by weight.

## Prerequisites

- **Graph:** The graph can be undirected and must be weighted. It should contain `V` vertices and `E` edges.
- **Minimum Spanning Tree (MST):** A spanning tree is a subgraph that includes all vertices and is a tree (i.e., acyclic and connected). A **Minimum Spanning Tree** is the spanning tree with the smallest total edge weight.

## Algorithm Overview

### Steps of Kruskal's Algorithm:

1. **Sort all edges by weight:** Start by sorting the edges in non-decreasing order based on their weights.
2. **Pick the smallest edge:** Add the smallest edge to the MST, as long as it does not form a cycle.
3. **Cycle Detection:** To detect cycles, use the **Disjoint Set Union (DSU)**, a data structure that helps keep track of which vertices are already connected.
4. **Repeat:** Continue adding edges until all vertices are connected or until the MST contains exactly `V-1` edges (where `V` is the number of vertices).

### Greedy Choice

Kruskal's algorithm follows a **greedy approach** where the algorithm picks the smallest available edge at each step, ensuring that a minimal cost tree is formed.

## Kruskal’s Algorithm: Pseudocode

```python
# Helper function to find the root of a set
def find(parent, i):
    if parent[i] == i:
        return i
    return find(parent, parent[i])

# Function to perform the union of two sets
def union(parent, rank, x, y):
    root_x = find(parent, x)
    root_y = find(parent, y)

    # Attach smaller rank tree under the root of the higher rank tree
    if rank[root_x] < rank[root_y]:
        parent[root_x] = root_y
    elif rank[root_x] > rank[root_y]:
        parent[root_y] = root_x
    else:
        parent[root_y] = root_x
        rank[root_x] += 1

def kruskal(graph, V):
    # Step 1: Sort all edges in non-decreasing order of their weight
    result = []  # Stores the resulting MST
    i = 0  # Index variable for sorted edges
    e = 0  # Number of edges in the MST

    graph = sorted(graph, key=lambda item: item[2])  # Sort edges by weight

    parent = []
    rank = []

    # Step 2: Initialize the parent and rank of each vertex
    for node in range(V):
        parent.append(node)
        rank.append(0)

    # Step 3: Pick the smallest edge and check if it forms a cycle
    while e < V - 1:
        # Pick the smallest edge (u, v, w)
        u, v, w = graph[i]
        i = i + 1
        x = find(parent, u)
        y = find(parent, v)

        # If including this edge does not cause a cycle, include it in the result
        if x != y:
            e = e + 1
            result.append([u, v, w])
            union(parent, rank, x, y)

    # Return the MST
    return result
```

Example
Input Graph
Consider the following weighted graph with 5 vertices:

```lua
   1
0-----1
| \   |
|  2  | 3
|     |
2-----3
 \   /
  3  4
```

```python
# Edges of the graph represented as (u, v, weight)
graph = [
    [0, 1, 1],
    [0, 2, 2],
    [1, 3, 3],
    [2, 3, 3],
    [2, 4, 3],
    [3, 4, 4]
]

V = 5  # Number of vertices
mst = kruskal(graph, V)

for u, v, weight in mst:
    print(f"Edge: {u} -- {v}, weight: {weight}")
```

Output

```yaml
Edge: 0 -- 1, weight: 1
Edge: 0 -- 2, weight: 2
Edge: 1 -- 3, weight: 3
Edge: 2 -- 4, weight: 3
```

This is the Minimum Spanning Tree with total weight of 9.

## Time Complexity

The time complexity of Kruskal’s Algorithm is dominated by the sorting step:

- **Sorting the edges**: Sorting all edges takes O(E log E), where E is the number of edges.
- **Union-Find Operations**: Each union and find operation takes nearly constant time due to path compression and union by rank, leading to an amortized time complexity of `O(E log V)`, where `V` is the number of vertices.

Therefore, the overall time complexity is: `O(E log E)` or `O(E log V)` (since `E <= V²`).

## Space Complexity

The space complexity is `O(V + E)`:

- `O(V)` for the parent and rank arrays used in the union-find data structure.
- `O(E)` for storing the graph's edges.

## Kruskal’s Algorithm: Key Use Cases

- **Network Design**: Kruskal’s Algorithm can be applied to design network infrastructures like computer networks or telecommunication systems where connecting all devices with minimal cable length or cost is important.
- **Approximating NP-Hard Problems**: Kruskal’s Algorithm can be used as a heuristic to solve or approximate NP-hard problems like the Traveling Salesman Problem (TSP).
- **Clustering Algorithms**: Kruskal’s can be adapted in clustering methods like Single-Linkage Clustering, where a graph is gradually partitioned into clusters.
- **Cycle Detection using Union-Find**: To detect cycles in Kruskal’s Algorithm, the Disjoint Set Union (DSU), or Union-Find data structure, is used:

  - **Find Operation**: Determines the root or representative of a vertex.
  - **Union Operation**: Connects two vertices, ensuring they belong to the same set.

This helps efficiently manage the sets of connected components during the construction of the MST. If two vertices belong to the same set, adding an edge between them would form a cycle.

## Kruskal's Algorithm vs Prim's Algorithm

| **Feature**         | **Kruskal's Algorithm**                  | **Prim's Algorithm**                       |
| ------------------- | ---------------------------------------- | ------------------------------------------ |
| **Approach**        | Greedy approach, selects edges by weight | Greedy approach, selects edges from a tree |
| **Graph Type**      | Works well with sparse graphs            | Works well with dense graphs               |
| **Data Structure**  | Uses Union-Find for cycle detection      | Uses a priority queue or heap              |
| **Time Complexity** | O(E log E)                               | O(E log V) with a priority queue           |
| **Cycle Detection** | Uses Union-Find                          | No explicit cycle detection is required    |

## When to Use Kruskal's Algorithm

- **Sparse Graphs**: Kruskal’s is preferable when the graph is sparse (i.e., few edges compared to the number of vertices).
- **Edge List Representation**: If the input graph is in edge list format, Kruskal's algorithm is easier to implement.

## Conclusion

Kruskal’s Algorithm is a fundamental greedy algorithm for finding the Minimum Spanning Tree (MST) of a graph. It uses the Disjoint Set Union (Union-Find) data structure to efficiently detect cycles while adding the smallest edges, ensuring the MST is constructed in an optimal manner. Kruskal’s is best suited for sparse graphs and provides a clean, simple way to compute the MST.
