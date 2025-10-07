---
weight: 1
bookCollapseSection: true
title: "Minimum Spanning Tree"
draft: false
---

# Minimum Spanning Tree (MST)

{{< markmap >}}

```markmap
# Minimum Spanning Tree (MST)
- **Definition**
  - Spanning Tree: Connected acyclic subgraph
  - Minimum: Minimum total edge weight
- **Properties**
  - Connects all vertices
  - No cycles
  - Minimum possible sum of edge weights
  - Unique for some graphs, not for others
- **Algorithms**
  - Kruskal's Algorithm (Greedy, sort edges)
  - Prim's Algorithm (Greedy, grow from vertex)
  - Boruvka's Algorithm (Historical)
- **Applications**
  - Network design (computers, roads)
  - Clustering analysis
  - Image segmentation
  - Approximation algorithms
```

{{< /markmap >}}

## Introduction

A Minimum Spanning Tree (MST) of a connected, undirected, weighted graph is a spanning tree whose total edge weight is minimal.

### Properties of MST

1. **Spanning Tree**: Connects all vertices without cycles
2. **Minimum Weight**: Among all possible spanning trees, has the smallest sum of edge weights
3. **Unique**: May not be unique in graphs with equal weight edges
4. **Size**: Always contains |V| - 1 edges for |V| vertices

### Applications

- **Network Design**: Designing efficient computer/telephone networks with minimum cost
- **Road Networks**: Connecting cities with minimum road length
- **Clustering**: MST can be used in hierarchical clustering algorithms
- **Image Processing**: Segmenting images into regions

## Kruskal's Algorithm

### Approach
1. Sort all edges in increasing order of weight
2. Add edges to MST if they don't create a cycle
3. Use Union-Find data structure to detect cycles
4. Continue until MST has V-1 edges

### Implementation

```java
class KruskalsMST {
    static class Edge implements Comparable<Edge> {
        int src, dest, weight;

        public Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        public int compareTo(Edge compareEdge) {
            return this.weight - compareEdge.weight;
        }
    }

    // Union-Find structure for cycle detection
    static class DisjointSet {
        int[] parent, rank;

        DisjointSet(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        void union(int x, int y) {
            int px = find(x), py = find(y);
            if (px != py) {
                if (rank[px] < rank[py]) parent[px] = py;
                else if (rank[px] > rank[py]) parent[py] = px;
                else { parent[py] = px; rank[px]++; }
            }
        }
    }

    public static void kruskal(List<Edge> edges, int n) {
        Collections.sort(edges);

        DisjointSet ds = new DisjointSet(n);
        List<Edge> mst = new ArrayList<>();

        for (Edge edge : edges) {
            int src = edge.src, dest = edge.dest;

            if (ds.find(src) != ds.find(dest)) {
                mst.add(edge);
                ds.union(src, dest);
            }

            if (mst.size() == n - 1) break; // MST complete
        }

        // Print MST
        System.out.println("MST edges:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " - " + edge.dest + " : " + edge.weight);
        }
    }
}
```

### Time Complexity
- **Sorting edges**: O(E log E)
- **Union-Find operations**: Nearly O(E α(V))
- **Overall**: O(E log E) where E is number of edges

### Advantages
- Simple to implement
- Works well when edges are sparse
- Easy to add new edges

## Prim's Algorithm

### Approach
1. Start with an arbitrary vertex
2. Find the minimum weight edge connecting current MST to remaining vertices
3. Add that edge and vertex to MST
4. Repeat until all vertices are included

### Implementation

```java
class PrimsMST {
    static int prim(int[][] graph) {
        int V = graph.length;
        int[] key = new int[V];     // Minimum weight to connect vertex
        int[] parent = new int[V];  // Parent in MST
        boolean[] mstSet = new boolean[V]; // Vertex included in MST

        // Initialize all keys as INFINITY
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        key[0] = 0; // Start from vertex 0

        for (int count = 0; count < V - 1; count++) {
            // Pick minimum key vertex not yet included in MST
            int u = minKey(key, mstSet);

            mstSet[u] = true;

            // Update key and parent for adjacent vertices
            for (int v = 0; v < V; v++) {
                if (graph[u][v] != 0 && !mstSet[v] &&
                    graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }

        // Calculate total weight
        int totalWeight = 0;
        for (int i = 1; i < V; i++) {
            totalWeight += key[i];
        }

        return totalWeight;
    }

    static int minKey(int[] key, boolean[] mstSet) {
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (int v = 0; v < key.length; v++) {
            if (!mstSet[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }

        return minIndex;
    }
}
```

### Time Complexity
- **Naive implementation**: O(V²)
- **With Binary Heap (Fibonacci)**: O(E log V)
- **With adjacency list + priority queue**: O((V + E) log V)

### When to Use Which Algorithm

| Aspect          | Kruskal's              | Prim's                          |
| --------------- | ---------------------- | ------------------------------- |
| Graph Density   | Sparse (E << V²)       | Dense (E ≈ V²)                  |
| Data Structure  | Edge list + Union-Find | Priority Queue + Adjacency list |
| Implementation  | Simpler                | More complex                    |
| Time Complexity | O(E log E)             | O((V + E) log V)                |

## Cycle Detection in MST

### Using MST Properties
- If adding an edge creates a cycle, it's not added
- In Kruskal's: Cycle detection via Union-Find
- In Prim's: Only consider vertices not in current MST

## Properties and Theorems

### Cut Property
For any partition of vertices into two non-empty sets, the minimum weight edge crossing the cut is in some MST.

### Cycle Property
For any cycle in the graph, the maximum weight edge in that cycle is not in any MST.

### Uniqueness
MST is unique if all edge weights are distinct. May have multiple MSTs if some edges have equal weights.

## Advanced Applications

### Minimum Bottleneck Spanning Tree
Find spanning tree where maximum edge weight is minimized.

### Steiner Tree Problem
Find minimum tree connecting a subset of vertices (NP-hard).

### Approximation Algorithms
- Traveling Salesman Problem approximation
- Steiner tree approximations

## Practice Problems

### Easy
- [1584. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/)

### Medium
- [1135. Connecting Cities With Minimum Cost](https://leetcode.com/problems/connecting-cities-with-minimum-cost/)
- [1168. Optimize Water Distribution in a Village](https://leetcode.com/problems/optimize-water-distribution-in-a-village/)

### Hard
- [1489. Find Critical and Pseudo-Critical Edges in Minimum Spanning Tree](https://leetcode.com/problems/find-critical-and-pseudo-critical-edges-in-minimum-spanning-tree/)

## Key Takeaways

1. **Kruskal's vs Prim's**: Use Kruskal's for sparse graphs, Prim's for dense graphs
2. **Greedy Choice**: Both algorithms make greedy choices and produce optimal results
3. **Union-Find is key**: Essential for Kruskal's algorithm's efficiency
4. **Applications**: Beyond graphs - network design, clustering, image processing
5. **Properties**: Understanding cut and cycle properties helps prove correctness
