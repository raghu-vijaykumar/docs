---
weight: 7
bookFlatSection: true
title: "Disjoint Set (Union Find)"
draft: false
---

# Disjoint Set (Union-Find)

## Overview

The **Disjoint Set**, also known as **Union-Find** or **Merge-Find** structure, is a data structure that tracks a set of elements partitioned into a number of disjoint (non-overlapping) subsets. It supports two primary operations:

- **Find:** Determine which subset a particular element is in. This can be used to check if two elements belong to the same subset.
- **Union:** Merge two subsets into a single subset.

The **Disjoint Set** is primarily used in scenarios involving equivalence relations, such as connected components in graphs, network connectivity, and image processing.

## Key Operations

1. **Find (x):** Returns the representative (or root) of the set containing element `x`. This operation is often used to determine whether two elements are in the same set.
2. **Union (x, y):** Merges the sets that contain elements `x` and `y` into a single set.
3. **Connected (x, y):** Returns `True` if `x` and `y` are in the same set, i.e., if `Find(x) == Find(y)`.

## Concepts

### 1. **Path Compression**

Path compression is a technique used during the **Find** operation to make future queries faster. When finding the representative of a set, the nodes in the path leading to the root are directly connected to the root. This effectively flattens the tree, resulting in faster lookups.

### 2. **Union by Rank / Size**

When performing the **Union** operation, we want to keep the tree as flat as possible. Union by rank (or by size) ensures that the smaller tree is always merged under the root of the larger tree. This helps maintain a logarithmic height for the trees, improving the efficiency of the operations.

- **Union by Rank:** Attach the tree with a smaller rank (height) under the tree with a larger rank.
- **Union by Size:** Attach the smaller set under the root of the larger set.

### 3. **Connected Components**

The Disjoint Set is often used to find **connected components** in a graph, where each connected component is a disjoint set. Two nodes belong to the same connected component if there is a path between them.

## Data Structure Representation

A Disjoint Set is typically represented by two arrays:

- **Parent Array (`parent[i]`):** Stores the parent of each element. If `parent[i] == i`, then `i` is a root node.
- **Rank/Size Array (`rank[i]` or `size[i]`):** Stores the rank (or size) of the tree for balancing the union operation.

## Algorithms

### Find with Path Compression

The **Find** operation is optimized with **path compression**, which ensures that nodes are directly linked to the root after the initial query, making future operations faster.

```python
def find(parent, x):
    if parent[x] != x:
        parent[x] = find(parent, parent[x])  # Path Compression
    return parent[x]
```

### Union by Rank/Size

The Union operation is optimized with union by rank or union by size, which ensures that smaller trees are merged under the root of larger trees to keep the structure balanced.

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

Example Code (Python)
The following code implements the Disjoint Set data structure using path compression and union by rank.

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

## Applications of Disjoint Set

- **Kruskal’s Algorithm for Minimum Spanning Tree (MST)**: The Disjoint Set is used in Kruskal's algorithm to efficiently check whether two vertices are in the same connected component and to union them if they are not. Example, Detecting cycles while adding edges in an MST.
- **Connected Components in Graphs**: The Disjoint Set is used to identify connected components in a graph. Each connected component forms a disjoint set.
- **Cycle Detection in Graphs**: The Disjoint Set can be used to detect cycles in an undirected graph. If two vertices belong to the same set before performing a union, a cycle exists.
- **Dynamic Connectivity Problem**: In scenarios where the connectivity between nodes is dynamically updated (e.g., network connections being added or removed), the Disjoint Set allows for efficient queries on whether two nodes are connected.
- **Image Processing**: Used in the Union-Find algorithm for labeling connected components in binary images.

## Time Complexity

- **Find (with path compression)**: Amortized time complexity is O(α(n)), where α(n) is the inverse Ackermann function, which grows extremely slowly and is nearly constant for practical inputs.
- **Union (with union by rank/size)**: Amortized time complexity is also O(α(n)).
  Overall, both Find and Union operations have nearly constant time complexity for practical purposes.

## Conclusion

The Disjoint Set (Union-Find) is an efficient and flexible data structure for solving problems related to partitioning sets and determining connectivity. By using techniques like path compression and union by rank, it ensures that both Find and Union operations are nearly constant time. This makes it extremely useful in graph algorithms, network connectivity, and other dynamic systems.
