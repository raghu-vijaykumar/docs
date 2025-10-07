---
weight: 3
bookFlatSection: true
title: "Exponential Search"
draft: false
---

# Exponential Search

{{< markmap >}}

```markmap
# Exponential Search
- **Concept** → Find range exponentially, then binary search
- **Use Case** → Unbounded or infinite arrays, unknown size
- **Algorithm**
  - Double bound until element <= target
  - Binary search in found range
- **Time Complexity** → O(log n)
- **Space Complexity** → O(1)
- **Comparison**
  - Efficient for large/infinite ranges
  - Slower than binary if size known
```

{{< /markmap >}}

Exponential Search is useful for finding elements in unbounded arrays (where the size is unknown or potentially infinite). It minimizes the number of checks by finding a suitable range exponentially and then applying binary search within that range.

## Theory

Exponential Search works in two phases:
1. **Find Range**: Start from index 1 and double (exponential) until finding an index where arr[i] >= target or i >= array length.
2. **Binary Search**: Perform binary search from previous index/2 to current index.

This is particularly useful when the array is sorted but unbounded (server logs, streams).

## Code Snippet (Java)

```java
public class ExponentialSearch {
    // Binary search helper
    private static int binarySearch(int[] arr, int left, int right, int target) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    public static int exponentialSearch(int[] arr, int target) {
        if (arr[0] == target) return 0;

        int i = 1;
        while (i < arr.length && arr[i] <= target) {
            i *= 2;  // exponential jump
        }

        // Binary search in range [i/2, min(i, arr.length - 1)]
        return binarySearch(arr, i/2, Math.min(i, arr.length - 1), target);
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                   | Technique Used     |
| -------- | ------------------------------------------------------------------------------------- | ------------------ |
| 🟡 Medium | [1095. Find in Mountain Array](https://leetcode.com/problems/find-in-mountain-array/) | Exponential Search |
