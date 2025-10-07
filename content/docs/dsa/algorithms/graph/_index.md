---
weight: 5
bookCollapseSection: true
title: "Graph Algorithms"
draft: false
---

# Graph Algorithms

{{< markmap >}}

```markmap
# Graph Algorithms Roadmap
- **Basics**
  - Graph Representations (Adjacency List/Matrix)
  - BLEVISITED Traversal (BFS, DFS)
  - Connected Components
- **Classical Problems**
  - Topological Sort [](/docs/dsa/algorithms/graph/topological-sort/)
  - Cycle Detection
  - Eulerian/Path Circuit [](/docs/dsa/algorithms/graph/eulerian-path/)
  - Hamiltonian Path/Circuit [](/docs/dsa/algorithms/graph/hamiltonian-path/)
- **Shortest Path**
  - Dijkstra [](/docs/dsa/algorithms/graph/shortest-path/dijkstra/)
  - Bellman-Ford [](/docs/dsa/algorithms/graph/shortest-path/bellman-ford/)
  - Floyd-Warshall [](/docs/dsa/algorithms/graph/shortest-path/floyd-warshall/)
- **Minimum Spanning Tree**
  - Kruskal [](/docs/dsa/algorithms/graph/mst/kruskal/)
  - Prim [](/docs/dsa/algorithms/graph/mst/prim/)
- **Advanced Graph**
  - Kosaraju (SCC) [](/docs/dsa/algorithms/graph/kosaraju/)
- **Tree Algorithms**
  - Lowest Common Ancestor (LCA) [](/docs/dsa/algorithms/graph/tree/lca/)
  - Tree Diameter [](/docs/dsa/algorithms/graph/tree/tree-diameter/)
  - Centroid Decomposition [](/docs/dsa/algorithms/graph/tree/centroid-decomp/)
  - Heavy-Light Decomposition [](/docs/dsa/algorithms/graph/tree/heavy-light-decomp/)
  - Persistent Segment Tree [](/docs/dsa/algorithms/graph/tree/persistent-segment-tree/)
- **Union-Find/DSU**
  - Basic Operations [](/docs/dsa/data-structures/disjoint-set/)
  - Tarjan's Algorithm (Applications) [](/docs/dsa/algorithms/graph/dsu/tarjan-algorithm/)
```

{{< /markmap >}}

Graphs are fundamental data structures representing networks of interconnected entities. Graph algorithms solve problems involving paths, connections, cycles, and optimizations in these networks.

## Graph Representations

### Adjacency Matrix
- 2D array where matrix[i][j] = 1 if edge between i and j
- Space O(V²), good for dense graphs
- Quick edge existence check

### Adjacency List
- Array of lists where list[i] contains neighbors of i
- Space O(V + E), good for sparse graphs
- Efficient traversal

## Core Algorithms Categories

### Traversal
- **BFS**: Level-order, shortest path in unweighted graphs
- **DFS**: Depth-first, detects cycles, topological sort

### Connectivity
- Connected components, strongly connected components (Kosaraju)
- Biconnected components, articulation points

### Optimization
- Shortest paths (Dijkstra, Bellman-Ford, Floyd-Warshall)
- Minimum spanning tree (Kruskal, Prim)

### Special Problems
- Topological sorting on DAGs
- Eulerian/Hamiltonian paths and circuits
- Maximum flow/minimum cut

## Practice Recommendations

1. **Start with Basics**: Implement BFS, DFS, adjacency lists
2. **Path Finding**: Learn Dijkstra for weighted shortest paths
3. **Tree Algorithms**: Focus on LCA, tree traversals
4. **Advanced**: MST, SCC, Eulerian paths

## Leetcode Problem Sets by Difficulty

### Easy (Graph Basics)
- [200. Number of Islands](https://leetcode.com/problems/number-of-islands/)
- [463. Island Perimeter](https://leetcode.com/problems/island-perimeter/)

### Medium (Core Algorithms)
- [207. Course Schedule](https://leetcode.com/problems/course-schedule/) (Topology/Cycle)
- [210. Course Schedule II](https://leetcode.com/problems/course-schedule-ii/)
- [994. Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) (BFS)
- [743. Network Delay Time](https://leetcode.com/problems/network-delay-time/) (Dijkstra)
- [847. Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) (State DP)

### Hard (Advanced)
- [785. Is Graph Bipartite?](https://leetcode.com/problems/is-graph-bipartite)
- [1584. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) (MST)
- [1334. Find the City With the Smallest Number of Neighbors](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) (Floyd-Warshall)
