---
weight: 6
bookFlatSection: true
title: "Interpolation Search"
draft: false
---

# Interpolation Search

{{< markmap >}}

```markmap
# Interpolation Search
- **Concept** → Estimating element position based on value distribution
- **Formula** → pos = low + [(key - arr[low]) / (arr[high] - arr[low])] * (high - low)
- **Best For** → Uniformly distributed sorted arrays
- **Time Complexity**
  - Best Case → O(log log n)
  - Worst Case → O(n)
- **Space Complexity** → O(1)
- **Comparison** → Faster than binary when data is uniform
```

{{< /markmap >}}

Interpolation Search is an improved variant of binary search optimized for uniformly distributed sorted arrays. It estimates the position of the target based on the values at the boundaries, potentially finding elements with fewer comparisons.

## Theory

Uses interpolation formula to guess the position proportionate to the target value. Works best when data is uniformly sorted (values are evenly spaced).

Similar to binary search but smarter probes.

## Code Snippet (Java)

```java
public class InterpolationSearch {
    public static int interpolationSearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high && target >= arr[low] && target <= arr[high]) {
            if (low == high) {
                if (arr[low] == target) return low;
                return -1;
            }

            // Estimate position
            int pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low]);

            if (arr[pos] == target) return pos;
            else if (arr[pos] < target) low = pos + 1;
            else high = pos - 1;
        }
        return -1;
    }
}
```

Note: Prevent division by zero if arr[high] == arr[low].

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                    | Technique Used       |
| -------- | ---------------------------------------------------------------------------------------------------------------------- | -------------------- |
| 🟡 Medium | [378. Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | Interpolation Search |
