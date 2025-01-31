---
weight: 1
bookFlatSection: true
title: "Tarjan's Algorithm"
draft: true
---

# Tarjan's Algorithm

{{< markmap >}}

```markmap
# Tarjan's Algorithm

- **Strongly Connected Components (SCC)** → Finds SCCs in a directed graph.
  - **Tarjan’s SCC Algorithm** → Uses DFS and a stack to find SCCs.
  - **Kosaraju’s SCC Algorithm** → Alternative approach using two DFS passes.
- **Lowest Common Ancestor (LCA) using Offline Queries** → Finds LCA efficiently using DSU.
  - **Tarjan’s LCA Algorithm** → Uses Disjoint Set Union (DSU) and DFS.
  - **Binary Lifting for LCA** → Alternative technique for LCA.
- **Bridges in Graph (Critical Connections)** → Finds edges whose removal disconnects the graph.
- **Articulation Points (Cut Vertices)** → Finds vertices whose removal increases components.
```

{{< /markmap >}}

# Leetcode Problems

| **Level**     | **Problem Name & Link**                                                                                             | **Technique Used**                  |
| ------------- | ------------------------------------------------------------------------------------------------------------------- | ----------------------------------- |
| 🟡 **Medium** | [1192. Critical Connections in a Network](https://leetcode.com/problems/critical-connections-in-a-network/)         | Tarjan’s Algorithm for Bridges      |
| 🟡 **Medium** | [310. Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/)                                    | Tarjan’s Algorithm for LCA          |
| 🟡 **Medium** | [685. Redundant Connection II](https://leetcode.com/problems/redundant-connection-ii/)                              | Tarjan’s SCC Algorithm              |
| 🟡 **Medium** | [1129. Shortest Path with Alternating Colors](https://leetcode.com/problems/shortest-path-with-alternating-colors/) | Tarjan’s Algorithm + Graph          |
| 🔴 **Hard**   | [Tarjan’s LCA Problem (SPOJ)](https://www.spoj.com/problems/LCA/)                                                   | Tarjan’s LCA Algorithm              |
| 🔴 **Hard**   | [Tarjan’s SCC Problem (SPOJ)](https://www.spoj.com/problems/SCONNECT/)                                              | Tarjan’s SCC Algorithm              |
| 🔴 **Hard**   | [851. Loud and Rich](https://leetcode.com/problems/loud-and-rich/)                                                  | Tarjan’s Algorithm for SCC          |
| 🔴 **Hard**   | [924. Minimize Malware Spread](https://leetcode.com/problems/minimize-malware-spread/)                              | Tarjan’s Algorithm + DSU            |
| 🔴 **Hard**   | [Tarjan’s Bridges Problem (SPOJ)](https://www.spoj.com/problems/BRIDGE/)                                            | Tarjan’s Algorithm for Bridges      |
| 🔴 **Hard**   | [Articulation Points Problem (SPOJ)](https://www.spoj.com/problems/CUT/)                                            | Tarjan’s Algorithm for Cut Vertices |
