---
aliases: [""]
weight: 7
bookFlatSection: true
title: "Jump Search"
draft: false
---

# Jump Search

{{< markmap >}}

```markmap
# Jump Search
- **Concept** → Search in blocks of size √n
- **Algorithm**
  - Jump forward by √n steps
  - Linear search in found block
- **Time Complexity** → O(√n)
- **Space Complexity** → O(1)
- **Advantages**
  - Simpler than binary search
  - Good for small arrays or linked lists
- **Comparison** → Slower than binary but easier implementation
```

{{< /markmap >}}

Jump Search is a searching algorithm that works by jumping ahead a fixed number of steps (block size, typically √n) and then performing linear search within the block where the element might be.

## Theory

1. Determine block size m = √n
2. Jump m steps ahead until you find a block where arr[i] >= target or end of array
3. Perform linear search in the previous block from (i - m) to i

Better than ternary search in terms of cache performance due to fewer cache misses.

## Code Snippet (Java)

```java
import java.lang.Math;

public class JumpSearch {
    public static int jumpSearch(int[] arr, int target) {
        int n = arr.length;
        int step = (int) Math.floor(Math.sqrt(n));  // Block size
        int prev = 0;

        // Find block containing target
        while (arr[Math.min(step, n) - 1] < target) {
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;  // Not found
        }

        // Linear search in block
        while (arr[prev] < target) {
            prev++;
            if (prev == Math.min(step, n)) return -1;  // End of block
        }

        if (arr[prev] == target) return prev;
        return -1;
    }
}
```

## Leetcode Problems

| Level  | Problem Name & Link                                                                                        | Technique Used      |
| ------ | ---------------------------------------------------------------------------------------------------------- | ------------------- |
| 🟢 Easy | [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Jump Search Variant |
