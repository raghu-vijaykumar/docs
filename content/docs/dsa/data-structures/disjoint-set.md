---
weight: 7
bookFlatSection: true
title: "Disjoint Set (Union Find)"
draft: false
---

# Disjoint Set (Union-Find)

{{< markmap "Disjoint Set (Union-Find)" >}}

```markmap
# Disjoint Set Union (DSU)
## 1. Connected Components in a Graph/Grid: Find components by merging connected nodes (applies to both graphs and 2D grids).
## 2. Cycle Detection in an Undirected Graph: Detect cycles by checking if two vertices belong to the same component.
## 3. Kruskal’s Algorithm for MST: Build MST by greedily adding edges while avoiding cycles.
## 4. Dynamic Connectivity & Network Merging: Check if two nodes belong to the same set, used in networks, social graphs, and dynamic connections.
## 5. LCA (Lowest Common Ancestor) using Tarjan’s Algorithm: Find LCA efficiently by processing queries offline with DSU.
## 6. Number of Distinct Groups (Clustering): Track dynamic groups by merging related people or objects.
## 7. Equations Possible (Union-Find with Constraints): Validate equations like `"a == b"` and `"c != d"` by merging equivalence classes.
## 8. Percolation Theory: Model fluid flow in a grid by tracking connectivity between open cells.
## 9. Word Groups (String Matching & Clustering): Group words based on transformation rules using DSU.
```

{{< /markmap >}}

## Overview

The **Disjoint Set**, also known as **Union-Find** or **Merge-Find** structure, is a data structure that tracks a set of elements partitioned into a number of disjoint (non-overlapping) subsets. It supports two primary operations:

- **Find:** Determine which subset a particular element is in. This can be used to check if two elements belong to the same subset.
- **Union:** Merge two subsets into a single subset.

The **Disjoint Set** is primarily used in scenarios involving equivalence relations, such as connected components in graphs, network connectivity, and image processing.

## Key Operations

1. **Find (x):** Returns the representative (or root) of the set containing element `x`. This operation is often used to determine whether two elements are in the same set.
2. **Union (x, y):** Merges the sets that contain elements `x` and `y` into a single set.
3. **Connected (x, y):** Returns `True` if `x` and `y` are in the same set, i.e., if `Find(x) == Find(y)`.

## Data Structure Representation

A Disjoint Set is typically represented by two arrays:

- **Parent Array (`parent[i]`):** Stores the parent of each element. If `parent[i] == i`, then `i` is a root node.
- **Rank/Size Array (`rank[i]` or `size[i]`):** Stores the rank (or size) of the tree for balancing the union operation.

## Concepts

### 1. **Path Compression**

Path compression is a technique used during the **Find** operation.

- Flattens the tree during `find(x)`, making future queries fast.
- Ensures most nodes point directly to the root.
- Works **over time**, not immediately.

{{< tabs "Path Compression" >}}
{{< tab "Python" >}}

```python
def find(parent, x):
    if parent[x] != x:

        parent[x] = find(parent, parent[x])  # Path Compression
    return parent[x]
```

{{< /tab >}}

{{< tab "Java" >}}

// Add Java code here if needed

{{< /tab >}}

{{< /tabs >}}

This effectively flattens the tree, resulting in faster lookups.

### 2. **Union by Rank / Size**

When performing the **Union** operation, we want to keep the tree as flat as possible.

- Ensures the **smaller tree always joins the larger one**.
- Prevents tree height from growing unnecessarily.
- Helps keep the structure balanced **before path compression takes effect**.

{{< tabs "Union by Rank" >}}
{{< tab "Python" >}}

```python
def union(parent, rank, x, y):
    rootX = find(parent, x)

    rootY = find(parent, y)

    if rootX != rootY:
        # Union by Rank
        if rank[rootX] > rank[rootY]:
            parent[rootY] = rootX
        elif rank[rootX] < rank[rootY]:
            parent[rootX] = rootY
        else:
            parent[rootY] = rootX
            rank[rootX] += 1
```

{{< /tab >}}

{{< tab "Java" >}}

// Add Java code here if needed

{{< /tab >}}

{{< /tabs >}}

This helps maintain a logarithmic height for the trees, improving the efficiency of the operations.

### 3. **Connected Components**

The Disjoint Set is often used to find **connected components** in a graph, where each connected component is a disjoint set. Two nodes belong to the same connected component if there is a path between them.

{{< tabs "DSU" >}}
{{< tab "Python" >}}

```python
class DisjointSet:
    def __init__(self, n):
        self.parent = [i for i in range(n)]
        self.rank = [0] * n

    def find(self, x):
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])  # Path Compression
        return self.parent[x]

    def union(self, x, y):
        rootX = self.find(x)
        rootY = self.find(y)

        if rootX != rootY:
            # Union by Rank
            if self.rank[rootX] > self.rank[rootY]:
                self.parent[rootY] = rootX
            elif self.rank[rootX] < self.rank[rootY]:
                self.parent[rootX] = rootY
            else:
                self.parent[rootY] = rootX
                self.rank[rootX] += 1

    def connected(self, x, y):
        return self.find(x) == self.find(y)

# Example Usage
ds = DisjointSet(5)
ds.union(0, 1)
ds.union(1, 2)
print(ds.connected(0, 2))  # Output: True
print(ds.connected(0, 3))  # Output: False
```

{{< /tab >}}

{{< tab "Java" >}}

// Add Java code here if needed

{{< /tab >}}

{{< /tabs >}}

### 🔹 Why Use Both Union by Rank and Path Compression Together?

1. **Path compression alone doesn't control merging order**, leading to suboptimal initial structures.
2. **Union by rank prevents deep trees from forming**, making path compression even more effective.
3. **Best time complexity**: `O(α(n))` (inverse Ackermann function, nearly constant).

## Applications of Disjoint Set

- **Kruskal's Algorithm for Minimum Spanning Tree (MST)**: The Disjoint Set is used in Kruskal's algorithm to efficiently check whether two vertices are in the same connected component and to union them if they are not. Example, Detecting cycles while adding edges in an MST.
- **Connected Components in Graphs**: The Disjoint Set is used to identify connected components in a graph. Each connected component forms a disjoint set.
- **Cycle Detection in Graphs**: The Disjoint Set can be used to detect cycles in an undirected graph. If two vertices belong to the same set before performing a union, a cycle exists.
- **Dynamic Connectivity Problem**: In scenarios where the connectivity between nodes is dynamically updated (e.g., network connections being added or removed), the Disjoint Set allows for efficient queries on whether two nodes are connected.
- **Image Processing**: Used in the Union-Find algorithm for labeling connected components in binary images.

## Time Complexity

- **Find (with path compression)**: Amortized time complexity is O(α(n)), where α(n) is the inverse Ackermann function, which grows extremely slowly and is nearly constant for practical inputs.
- **Union (with union by rank/size)**: Amortized time complexity is also O(α(n)).
  Overall, both Find and Union operations have nearly constant time complexity for practical purposes.

## Leetcode Problems

{{< expand "684. Redundant Connection" "Cycle Detection" >}}

[684. Redundant Connection](https://leetcode.com/problems/redundant-connection/)

- In a disjoint set if both already belong to the same root then its redundant connection.

```java
class Solution {

    class DisjointSet {

        int[] parent;
        int[] rank;

        public DisjointSet(int n) {
            parent = new int[n];
            rank = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }

        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY)
                return;

            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootY] > rank[rootX]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

        }

        public boolean connected(int x, int y) {
            return find(x) == find(y);
        }
    }

    public int[] findRedundantConnection(int[][] edges) {
        // in a disjoint Set if both already belong to the same root then its redundan
        // connection
        DisjointSet ds = new DisjointSet(1001);
        for (int[] edge : edges) {
            if (ds.connected(edge[0], edge[1]))
                return edge;
            else {
                ds.union(edge[0], edge[1]);
            }
        }
        return new int[2];
    }
}
```

{{< /expand >}}

{{< expand "547. Number of Provinces" "Cluster Count" >}}

[547. Number of Provinces](https://leetcode.com/problems/number-of-provinces/)

Another way to solve this problem is to use the DFS with visited array. which is a very simlar to [Number of islands](https://leetcode.com/problems/number-of-islands/description/) problem.

- Start with province 1 and add every other province connected to one is visited set, and increment count by 1
- Start again with province n (if not visited) and add every other province connected to n is visited set, and increment count by 1
- Continue this process until all provinces are visited.

```java
class Solution {

    int[] parent;
    int[] rank;

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        parent = new int[n];
        rank = new int[n];
        int count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1 && !isConnected(i, j)) {
                    union(i, j);
                    count--;
                }
            }
        }
        return count;
    }

    public void union(int a, int b) {
        int parentA = find(a);
        int parentB = find(b);

        if (rank[parentA] < rank[parentB]) {
            parent[parentA] = parentB;
        } else if (rank[parentA] > rank[parentB]) {
            parent[parentB] = parentA;
        } else {
            parent[parentA] = parentB;
            rank[parentB]++;
        }

    }

    public boolean isConnected(int a, int b) {
        return find(a) == find(b);
    }

    public int find(int a) {
        if (parent[a] != a)
            parent[a] = find(parent[a]);
        return parent[a];
    }
}
```

{{< /expand >}}

{{< expand "1319. Number of Operations to Make Network Connected" "Cluster Count" >}}

[1319. Number of Operations to Make Network Connected](https://leetcode.com/problems/number-of-operations-to-make-network-connected/)

- Keep track of connected components and extra connections.
- If extra connections are less than the number of components - 1, then it is not possible to connect all the computers.

```java
class Solution {
    int[] parent;
    int[] rank;

    public int makeConnected(int n, int[][] connections) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;

        int connectedComponetCount = n;
        int extraConnections = 0;
        for (int i = 0; i < connections.length; i++) {
            if (isConnected(connections[i][0], connections[i][1]))
                extraConnections++;
            else {
                union(connections[i][0], connections[i][1]);
                connectedComponetCount--;
            }
        }

        return connectedComponetCount - 1 > extraConnections ? -1 : connectedComponetCount - 1;

    }

    public int find(int a) {
        if (parent[a] != a)
            parent[a] = find(parent[a]);
        return parent[a];
    }

    public boolean isConnected(int a, int b) {
        return find(a) == find(b);
    }

    public void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
    }
}
```

{{< /expand >}}

{{< expand "721. Accounts Merge" "Connected Components" >}}

[721. Accounts Merge](https://leetcode.com/problems/accounts-merge/)

```java

```

{{< /expand >}}

{{< expand "1192. Critical Connections in a Network" "Connected Components" >}}

[1192. Critical Connections in a Network](https://leetcode.com/problems/critical-connections-in-a-network/)

```java

```

{{< /expand >}}

## Conclusion

The Disjoint Set (Union-Find) is an efficient and flexible data structure for solving problems related to partitioning sets and determining connectivity. By using techniques like path compression and union by rank, it ensures that both Find and Union operations are nearly constant time. This makes it extremely useful in graph algorithms, network connectivity, and other dynamic systems.
