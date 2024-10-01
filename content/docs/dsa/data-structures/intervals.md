---
weight: 7
bookFlatSection: true
title: "Intervals"
draft: false
---

# Intervals in Data Structures

Intervals are an important concept in data structures, representing a continuous range of values between two endpoints. Intervals are commonly used in problems such as scheduling, range queries, and managing overlapping ranges. Various data structures are designed to handle intervals efficiently, enabling operations like insertion, deletion, and querying for overlaps.

## Definition

An **interval** is defined by two values, typically represented as:

`Interval = [start, end]`

Where `start` is the beginning of the interval, and `end` is the endpoint. The interval represents all values between `start` and `end`, inclusive.

## Types of Intervals

1. **Open Interval**

   - Represented as `(start, end)`
   - Does not include the `start` and `end` values.
   - Example: `(2, 5)` includes all values greater than 2 and less than 5.

2. **Closed Interval**

   - Represented as `[start, end]`
   - Includes both the `start` and `end` values.
   - Example: `[2, 5]` includes all values from 2 to 5, inclusive.

3. **Half-Open Interval**
   - Represented as `[start, end)` or `(start, end]`
   - Includes one of the endpoints but not the other.
   - Example: `[2, 5)` includes 2 but not 5.

## Operations on Intervals

- **Insertion**: Inserting a new interval into a set of existing intervals, ensuring that any overlapping intervals are merged.
- **Deletion**: Removing an interval from a set, potentially splitting or merging remaining intervals to maintain consistency.
- **Overlap Check**: Checking whether two intervals overlap (i.e., whether they share any common values).
- **Merging Intervals**: Combining overlapping intervals into a single, larger interval that covers the union of their ranges.
- **Querying for Overlaps**: Given an interval, finding all intervals that overlap with it in a set.

## Data Structures for Intervals

### Interval Trees

**Interval Trees** are specialized binary search trees designed for efficiently storing intervals and supporting operations such as overlap queries.

- **Structure**: Each node in an interval tree represents an interval, and the tree is arranged based on the start value of intervals.
- **Operations**:

  - **Insertion**: Insert a new interval into the tree based on its start value.
  - **Deletion**: Remove an interval from the tree.
  - **Query**: Given an interval, find all intervals that overlap with it.

- **Use Cases**:

  - Finding overlapping intervals in scheduling problems.
  - Range queries in 1D space.

- **Time Complexity**:
  - Insertion: `O(log n)`
  - Deletion: `O(log n)`
  - Overlap Query: `O(k + log n)`, where `k` is the number of overlapping intervals.

```java
class IntervalTreeNode {
    int start, end;
    IntervalTreeNode left, right;

    public IntervalTreeNode(int start, int end) {
        this.start = start;
        this.end = end;
        this.left = this.right = null;
    }
}

public class IntervalTree {
    private IntervalTreeNode root;

    public void insert(int start, int end) {
        root = insert(root, start, end);
    }

    private IntervalTreeNode insert(IntervalTreeNode node, int start, int end) {
        if (node == null) return new IntervalTreeNode(start, end);

        if (start < node.start) {
            node.left = insert(node.left, start, end);
        } else {
            node.right = insert(node.right, start, end);
        }
        return node;
    }

    // Overlap query and other operations can be added similarly
}
```

### Segment Trees

A Segment Tree is a tree data structure used for storing intervals or segments. It allows querying which intervals a given point or range falls into, making it useful for range-based problems.

- **Structure**: A segment tree breaks down a range of values into smaller subranges and stores them in a binary tree.

- **Operations**

  - **Range Queries**: Efficiently find the sum or minimum/maximum of elements within a specific range.
  - **Updates**: Modify elements within a specific range.

- **Use Cases**

  - Dynamic range queries.
  - Range sum, range minimum, or maximum queries.

- **Time Complexity**

  - Query: `O(log n)`
  - Update: `O(log n)`

```java
class SegmentTree {
    int[] tree;
    int n;

    public SegmentTree(int[] nums) {
        n = nums.length;
        tree = new int[2 * n];
        buildTree(nums);
    }

    private void buildTree(int[] nums) {
        for (int i = 0; i < n; i++) tree[n + i] = nums[i];
        for (int i = n - 1; i > 0; i--) tree[i] = tree[i * 2] + tree[i * 2 + 1];
    }

    public void update(int pos, int value) {
        pos += n;
        tree[pos] = value;
        while (pos > 0) {
            int left = pos, right = pos;
            if (pos % 2 == 0) right = pos + 1;
            else left = pos - 1;
            tree[pos / 2] = tree[left] + tree[right];
            pos /= 2;
        }
    }

    public int query(int left, int right) {
        left += n;
        right += n;
        int sum = 0;
        while (left <= right) {
            if (left % 2 == 1) sum += tree[left++];
            if (right % 2 == 0) sum += tree[right--];
            left /= 2;
            right /= 2;
        }
        return sum;
    }
}
```

### Balanced Binary Search Trees (BST)

Balanced binary search trees like AVL trees or Red-Black trees can also be adapted to store intervals. These structures maintain sorted order while keeping tree height balanced, ensuring efficient insertion, deletion, and lookup operations.

**Operations**:

- Insertion and deletion of intervals.
- Querying for intervals that overlap with a given range.

**Use Cases**

- Handling dynamic sets of intervals where frequent insertions and deletions occur.

**Time Complexity**

- Insertion: `O(log n)`
- Deletion: `O(log n)`
- Query: `O(log n)` for point queries.

### Applications of Intervals in Data Structures

- **Scheduling**: In scheduling problems, intervals represent the time spans of tasks or events. You can use interval trees or balanced BSTs to check for conflicting tasks or find available slots.
- **Range Queries**: Segment trees and interval trees are often used for range queries, where you need to find all intervals that fall within a specific range or to determine the sum of values within a given range.
- **Collision Detection**: In computational geometry or gaming, intervals represent bounding boxes or line segments. Interval trees can efficiently check for overlapping objects.
- **Time-Based Data Storage**: When handling logs or time-series data, intervals can represent time ranges, and you can use data structures like interval trees to perform fast range-based queries.

## Conclusion

Intervals are a crucial concept in data structures, enabling the management and manipulation of ranges of values. Whether through Interval Trees, Segment Trees, or Balanced Binary Search Trees, the efficient handling of intervals is important in applications such as scheduling, range queries, and collision detection. Understanding the appropriate data structure to use based on the operation is key to solving interval-based problems efficiently.
