---
weight: 8
bookFlatSection: true
title: "Ternary Search"
draft: false
---

# Ternary Search

{{< markmap >}}

```markmap
# Ternary Search
- **Concept** → Divide array into 3 parts, search in one of two possible regions
- **Types**
  - Iterative → Loop with 2 midpoints
  - Recursive → Divide and conquer
- **Time Complexity** → O(log₃ n)
- **Space Complexity** → O(1) for iterative, O(log n) for recursive
- **Use Cases**
  - Finding maximum/minimum in unimodal functions
  - Searching in 3-partitioned arrays
- **Comparison** → Slower than binary but for ternary regions
```

{{< /markmap >}}

Ternary Search divides the search space into three parts instead of two, checking two midpoints to decide which third to continue searching in. It's most useful for finding the maximum or minimum of a unimodal function (increases then decreases).

## Theory

For arrays:
- Set two midpoints: m1 = low + (high-low)/3, m2 = high - (high-low)/3
- Compare target with arr[m1] and arr[m2] to decide which third to search

For functions (continuous search):
- Minimize/maximize by narrowing the range.

Works on unimodal arrays (strictly increasing then decreasing).

## Code Snippet (Java)

### Iterative Ternary Search on Array

```java
public class TernarySearch {
    public static int ternarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid1 = left + (right - left) / 3;
            int mid2 = right - (right - left) / 3;

            if (arr[mid1] == target) return mid1;
            if (arr[mid2] == target) return mid2;

            if (target < arr[mid1]) right = mid1 - 1;
            else if (target > arr[mid2]) left = mid2 + 1;
            else {
                left = mid1 + 1;
                right = mid2 - 1;
            }
        }
        return -1;
    }
}
```

### Function Minimization (Unimodal Function)

```java
// For minimizing a unimodal function f(x)
public static double ternarySearchMin(double left, double right, double precision) {
    while (right - left > precision) {
        double mid1 = left + (right - left) / 3;
        double mid2 = right - (right - left) / 3;

        if (f(mid1) < f(mid2)) right = mid2;  // Drop right third
        else left = mid1;  // Drop left third
    }
    return (left + right) / 2;
}

// Assume f is unimodal
private static double f(double x) {
    return (x - 5) * (x - 5) + 3;  // Example: minimum at x=5
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                    | Technique Used         |
| -------- | ---------------------------------------------------------------------------------------------------------------------- | ---------------------- |
| 🟡 Medium | [154. Find Minimum in Rotated Sorted Array II](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/) | Ternary Search Variant |
