---
aliases: [""]
weight: 5
bookFlatSection: true
title: "Segment Tree"
draft: false
---

# Segment Tree

{{< markmap >}}

```markmap
# Segment Tree
- **Purpose** → Efficient range queries (sum, min, max) on arrays
- **Structure** → Full binary tree with leaves as array elements
- **Operations**
  - Build: O(n)
  - Query: O(log n)
  - Update: O(log n)
- **Space** → O(n)
- **Use Cases**
  - Range sum/min/max queries
  - Point updates with range queries
  - Lazy propagation for range updates
```

{{< /markmap >}}

A Segment Tree is a data structure used for efficient range queries and updates on arrays. It can answer queries like "what is the sum/min/max in this range?" in O(log n) time.

## Theory

Built as a full binary tree where each node represents a range of the array. Root is [0..n-1], leaves are individual elements.

For range queries:
1. Break query range into O(log n) node ranges
2. Combine results

For updates:
1. Update leaves and propagate up

Supports lazy propagation for range updates.

## Code Snippet (Java)

### Basic Range Sum Query

```java
public class SegmentTree {
    private int[] tree;
    private int n;

    public SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n]; // 4*n space for worst case
        build(arr, 0, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
            return;
        }
        int mid = (start + end) / 2;
        build(arr, 2 * node + 1, start, mid);
        build(arr, 2 * node + 2, mid + 1, end);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2]; // Sum
    }

    public void update(int index, int value) {
        update(0, 0, n - 1, index, value);
    }

    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            tree[node] = value;
            return;
        }
        int mid = (start + end) / 2;
        if (index <= mid) {
            update(2 * node + 1, start, mid, index, value);
        } else {
            update(2 * node + 2, mid + 1, end, index, value);
        }
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2]; // Recompute
    }

    public int query(int left, int right) {
        return query(0, 0, n - 1, left, right);
    }

    private int query(int node, int start, int end, int left, int right) {
        if (right < start || end < left) return 0; // No overlap
        if (left <= start && end <= right) return tree[node]; // Complete overlap
        int mid = (start + end) / 2;
        int leftSum = query(2 * node + 1, start, mid, left, right);
        int rightSum = query(2 * node + 2, mid + 1, end, left, right);
        return leftSum + rightSum;
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                            | Technique Used  |
| -------- | -------------------------------------------------------------------------------------------------------------- | --------------- |
| 🔴 Hard   | [307. Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/)                       | Segment Tree    |
| 🟡 Medium | [315. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) | Fenwick/Segment |
| 🔴 Hard   | [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/)                                             | Segment Tree    |
