---
weight: 3
bookCollapseSection: true
title: "Disjoint Set Union (DSU)"
draft: false
---

# Disjoint Set Union (DSU) / Union Find

{{< markmap >}}

```markmap
# Disjoint Set Union (DSU)
- **Operations**
  - Find: Find root/representative of a set
  - Union: Merge two sets
  - Make Set: Create new singleton set
- **Optimizations**
  - Union by Rank: Balance tree height
  - Path Compression: Flatten tree structure
- **Applications**
  - Connected Components in Undirected Graph
  - Cycle Detection in Undirected Graph
  - Kruskal's MST Algorithm
  - Image Segmentation
  - Network Connectivity
```

{{< /markmap >}}

## Introduction

Disjoint Set Union (DSU), also known as Union-Find, is a data structure that tracks a set of elements partitioned into a number of disjoint (non-overlapping) subsets.

### Key Operations

- **Find**: Find which subset a particular element is in (find the representative/root)
- **Union**: Join two subsets into a single subset
- **Make Set**: Create a new set containing a single element

### Performance Characteristics

- **Without optimizations**: Find O(n), Union O(n)
- **With Union by Rank + Path Compression**: Nearly O(1) amortized

## Basic Implementation (Without Optimizations)

```java
class DisjointSet {
    private int[] parent;

    public DisjointSet(int size) {
        parent = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i; // Initially, each element is its own parent
        }
    }

    // Find with recursion
    public int find(int x) {
        if (parent[x] != x) {
            return find(parent[x]); // Recurse until root
        }
        return x;
    }

    // Union by setting one parent to another
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootX] = rootY; // Make one root point to another
        }
    }

    // Check if two elements are in same set
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
```

### Problems with Basic Implementation

- **Trees can become linked lists**: Find operations become O(n)
- **No balancing**: Worst case linear structure

## Optimized Implementation (Union by Rank + Path Compression)

```java
class DisjointSetOptimized {
    private int[] parent;
    private int[] rank; // Height of each tree

    public DisjointSetOptimized(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Find with path compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    // Union by rank
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            // Attach smaller rank tree under larger rank
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                // Same rank, attach one to other and increment rank
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
```

### Optimizations Explained

#### Path Compression
- Makes the tree flatter by pointing each node directly to the root
- Happens during find operations: `parent[x] = find(parent[x])`
- Significantly reduces future find operation times

#### Union by Rank
- Ensures shorter trees are attached to taller trees
- Rank represents the upper bound on tree height
- Prevents formation of linked list structures

## Applications

### 1. Connected Components (Graph Theory)
Determine which nodes are connected in an undirected graph.

```java
public int countComponents(int n, int[][] edges) {
    DisjointSet ds = new DisjointSet(n);

    for (int[] edge : edges) {
        ds.union(edge[0], edge[1]);
    }

    Set<Integer> components = new HashSet<>();
    for (int i = 0; i < n; i++) {
        components.add(ds.find(i));
    }

    return components.size();
}
```

### 2. Cycle Detection in Undirected Graphs
Connect components and check if edge endpoints are already in same component.

```java
public boolean hasCycle(int[][] edges, int n) {
    DisjointSet ds = new DisjointSet(n);

    for (int[] edge : edges) {
        int x = edge[0], y = edge[1];
        if (ds.connected(x, y)) {
            return true; // Edge between same component = cycle
        }
        ds.union(x, y);
    }
    return false;
}
```

### 3. Kruskal's Minimum Spanning Tree Algorithm

Used in Kruskal's algorithm to detect cycles while adding minimum weight edges.

### 4. Dynamic Connectivity Queries
Track connections as edges are added/removed (with additional optimizations for deletions).

## Advanced Concepts

### Weighted Quick Union with Path Compression
Combines both optimizations for ~O(1) amortized operations.

### Union-Find with Undo Operations
For applications needing reversibility (persistent DSU).

### Applications in Image Processing
- Image segmentation using connected components
- Region labeling and analysis

## Time Complexity Analysis

- **Without optimizations**: O(n) for find and union operations
- **With Union by Rank**: O(log n) for find and union
- **With Path Compression**: Amortized nearly O(1)
- **Combined (Union by Rank + Path Compression)**: Amortized O(α(n)) where α is inverse Ackermann function (nearly constant)

## Practice Problems

### Easy
- [323. Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/)

### Medium
- [684. Redundant Connection](https://leetcode.com/problems/redundant-connection/)
- [685. Redundant Connection II](https://leetcode.com/problems/redundant-connection-ii/)
- [695. Max Area of Island](https://leetcode.com/problems/max-area-of-island/)

### Hard
- [721. Accounts Merge](https://leetcode.com/problems/accounts-merge/)

## Key Takeaways

1. **Use optimized version**: Always implement with Union by Rank and Path Compression
2. **Graph applications**: Perfect for connectivity queries in graphs
3. **Near-linear performance**: Very efficient for large datasets
4. **Offline processing**: Great for batch operations where all unions are done first
