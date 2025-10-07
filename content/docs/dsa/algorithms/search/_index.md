---
weight: 3
bookCollapseSection: true
title: "Search Algorithms"
draft: false
---

# Search Algorithms

{{< markmap >}}

```markmap
# Search Algorithms
- **Linear Search**
  - Sequential search in arrays
  - O(n) time complexity
- **Binary Search**
  - Works on sorted arrays
  - Logarithmic time O(log n)
  - Variants: First/Last occurrence [](/docs/dsa/algorithms/search/binary-search/)
- **Ternary Search**
  - Divides array into three parts
  - Good for unimodal functions [](/docs/dsa/algorithms/search/ternary-search/)
- **Exponential Search**
  - Finds range then binary search
  - Good for unbounded arrays [](/docs/dsa/algorithms/search/exponential-search/)
- **Interpolation Search**
  - Uses position estimation formula
  - Better for uniformly distributed data [](/docs/dsa/algorithms/search/interpolation-search/)
- **Fibonacci Search**
  - Uses Fibonacci numbers
  - Division-based like golden ratio [](/docs/dsa/algorithms/search/fibonacci-search/)
- **Jump Search**
  - Jumps ahead by fixed steps
  - Square root decomposition [](/docs/dsa/algorithms/search/jump-search/)
```

{{< /markmap >}}

## Introduction to Searching

Searching algorithms are fundamental operations that locate elements within data structures. The choice of algorithm depends on data structure organization, size, and access patterns.

## Linear Search

### Simple Sequential Search

```java
public static int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i; // Element found
        }
    }
    return -1; // Element not found
}
```

**Time Complexity**: O(n)
**Best Case**: O(1) - element at first position
**Worst Case**: O(n) - element at last position or not present
**Space Complexity**: O(1)

### When to Use Linear Search?
- Small arrays
- Unsorted data
- Frequency of search operations is low
- Simple implementation needed

## Binary Search

### Iterative Implementation

```java
public static int binarySearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;

    while (left <= right) {
        int mid = left + (right - left) / 2; // Prevents integer overflow

        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return -1; // Element not found
}
```

### Recursive Implementation

```java
public static int binarySearchRecursive(int[] arr, int target, int left, int right) {
    if (left > right) {
        return -1;
    }

    int mid = left + (right - left) / 2;

    if (arr[mid] == target) {
        return mid;
    } else if (arr[mid] < target) {
        return binarySearchRecursive(arr, target, mid + 1, right);
    } else {
        return binarySearchRecursive(arr, target, left, mid - 1);
    }
}
```

### Finding First and Last Occurrence

```java
// First occurrence
public static int findFirstOccurrence(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    int result = -1;

    while (left <= right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            result = mid;
            right = mid - 1; // Continue searching left
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return result;
}

// Last occurrence
public static int findLastOccurrence(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    int result = -1;

    while (left <= right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            result = mid;
            left = mid + 1; // Continue searching right
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return result;
}
```

**Time Complexity**: O(log n)
**Space Complexity**: O(1) iterative, O(log n) recursive
**Requirements**: Sorted array

## Other Search Algorithms

### Ternary Search
Divides the array into three parts instead of two.

```java
public static int ternarySearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;

    while (left <= right) {
        int mid1 = left + (right - left) / 3;
        int mid2 = right - (right - left) / 3;

        if (arr[mid1] == target) return mid1;
        if (arr[mid2] == target) return mid2;

        if (target < arr[mid1]) {
            right = mid1 - 1;
        } else if (target > arr[mid2]) {
            left = mid2 + 1;
        } else {
            left = mid1 + 1;
            right = mid2 - 1;
        }
    }

    return -1;
}
```

**Best for**: Unimodal functions (functions that increase then decrease)

### Exponential Search
1. Find range where element might exist by repeated doubling
2. Use binary search in that range

```java
public static int exponentialSearch(int[] arr, int target) {
    if (arr[0] == target) return 0;

    // Find range: 1, 2, 4, 8, ...
    int bound = 1;
    while (bound < arr.length && arr[bound] <= target) {
        bound *= 2;
    }

    // Binary search in range [bound/2, min(bound, length-1)]
    int left = bound / 2;
    int right = Math.min(bound, arr.length - 1);

    return binarySearchInRange(arr, target, left, right);
}
```

**Use when**: Array is unbounded or very large, and binary search alone isn't possible

### Jump Search
Jumps ahead by fixed step size (√n), then does linear search in block.

```java
public static int jumpSearch(int[] arr, int target) {
    int n = arr.length;
    int blockSize = (int) Math.sqrt(n); // Block size

    int step = blockSize;
    int prev = 0;

    // Jump to find the block
    while (step < n && arr[step] < target) {
        prev = step;
        step += blockSize;

        if (step > n) step = n;
    }

    // Linear search in the block
    for (int i = prev; i < step; i++) {
        if (arr[i] == target) {
            return i;
        }
    }

    return -1;
}
```

**Time Complexity**: O(√n)

### Interpolation Search
Estimates position using interpolation formula.

```java
public static int interpolationSearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;

    while (left <= right && arr[left] <= target && target <= arr[right]) {
        // Estimate position
        int pos = left + ((target - arr[left]) * (right - left) / (arr[right] - arr[left]));

        if (pos < left || pos > right) {
            return -1; // Invalid position
        }

        if (arr[pos] == target) {
            return pos;
        } else if (arr[pos] < target) {
            left = pos + 1;
        } else {
            right = pos - 1;
        }
    }

    return -1;
}
```

**Best for**: Uniformly distributed sorted arrays

## Comparison of Search Algorithms

| Algorithm            | Time Complexity  | Prerequisites                | Best Use Case                                  |
| -------------------- | ---------------- | ---------------------------- | ---------------------------------------------- |
| Linear Search        | O(n)             | None                         | Small arrays, unsorted data                    |
| Binary Search        | O(log n)         | Sorted array                 | Large sorted arrays                            |
| Ternary Search       | O(log₃ n)        | Sorted, unimodal             | Function optimization                          |
| Jump Search          | O(√n)            | Sorted array                 | Alternative to binary search                   |
| Interpolation Search | O(log log n) avg | Sorted, uniform distribution | Special cases where data is evenly distributed |
| Fibonacci Search     | O(log n)         | Sorted array                 | Division-based searching                       |

## When to Choose Which Algorithm?

- **Linear Search**: Always work as fallback, simple to implement
- **Binary Search**: Standard choice for sorted arrays, most widely used
- **Interpolation Search**: When data is uniformly distributed (rare in practice)
- **Exponential Search**: When array can be unbounded
- **Jump Search**: When jumping through large arrays with predictable access patterns
- **Ternary Search**: Rarely used for arrays, more for function optimization

## Practice Problems

### Easy
- [704. Binary Search](https://leetcode.com/problems/binary-search/)
- [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/)
- [278. First Bad Version](https://leetcode.com/problems/first-bad-version/)

### Medium
- [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/)
- [81. Search in Rotated Sorted Array II](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/)
- [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)
- [162. Find Peak Element](https://leetcode.com/problems/find-peak-element/)

### Hard
- [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/)
- [65. Valid Number](https://leetcode.com/problems/valid-number/)

## Key Takeaways

1. **Binary Search is King**: Most practical and widely used search algorithm
2. **Understand Prerequisites**: Most algorithms require sorted data
3. **Trade-offs**: Consider edge cases, data distribution, and constraints
4. **Implementation Details**: Watch for integer overflow in midpoint calculation
5. **Variants Matter**: Learn to find first/last occurrences, insertion points
6. **Practice Variations**: Different search problems often require algorithm modifications
