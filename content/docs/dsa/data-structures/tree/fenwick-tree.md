---
weight: 6
bookFlatSection: true
title: "Fenwick Tree"
draft: false
---

# Fenwick Tree

{{< markmap >}}

```markmap
# Fenwick Tree (Binary Indexed Tree)
- **Purpose** → Efficient prefix sum queries and updates
- **Structure** → Array based binary representation
- **Operations**
  - Build: O(n log n)
  - Query prefix: O(log n)
  - Update: O(log n)
- **Space** → O(n)
- **Use Cases**
  - Frequency counts
  - Prefix sum calculations
  - Range queries with updates
- **Advantage** → Simpler than segment tree for prefix sums
```

{{< /markmap >}}

Fenwick Tree (Binary Indexed Tree) is a data structure for efficient prefix sum calculations and point updates. It's simpler than segment tree while maintaining O(log n) operations.

## Theory

Uses binary indexing to store prefix sums. Each tree[i] stores sum of elements from i - 2^r + 1 to i, where r is trailing zeros in binary.

Update propagates through ancestors, query accumulates from index to parents.

## Code Snippet (Java)

### Prefix Sum Fenwick Tree

```java
public class FenwickTree {
    private int[] tree;
    private int n;

    public FenwickTree(int size) {
        n = size;
        tree = new int[n + 1]; // 1-based indexing
    }

    public void update(int index, int delta) {
        index++; // Convert to 1-based
        while (index <= n) {
            tree[index] += delta;
            index += index & -index; // Add least significant bit
        }
    }

    public int query(int index) { // Prefix sum [1..index]
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= index & -index; // Remove least significant bit
        }
        return sum;
    }

    public int queryRange(int left, int right) {
        return query(right + 1) - (left > 0 ? query(left) : 0);
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                            | Technique Used                        |
| -------- | -------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| 🟡 Medium | [315. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) | Fenwick Tree                          |
| 🟡 Medium | [327. Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/)                                   | Fenwick Tree + Coordinate Compression |
| 🔴 Hard   | [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/)                                             | Binary Search + Fenwick               |
