---
weight: 2
bookCollapseSection: true
title: "Sorting Algorithms"
draft: false
---

# Sorting Algorithms

{{< markmap >}}

```markmap
# Sorting Algorithms Overview
- **Comparison Based (O(n log n) avg)**
  - Bubble Sort (O(n²))
  - Insertion Sort (O(n²))
  - Selection Sort (O(n²))
  - Merge Sort (O(n log n))
  - Quick Sort (O(n log n) avg, O(n²) worst)
  - Heap Sort (O(n log n))
  - Shell Sort (O(n log n))
  - Tim Sort (O(n log n))
- **Non-Comparison Based**
  - Counting Sort (O(n + k))
  - Radix Sort (O(n*k))
  - Bucket Sort (O(n + k))
  - Pigeonhole Sort (O(n + k))
- **Special Purpose**
  - Bitonic Sort (O(log² n))
  - Topological Sort
  - Pancake Sorting
```

{{< /markmap >}}

Sorting algorithms organize data in a specific order (ascending or descending). They vary in complexity, stability, and use cases.

## Categories

### Based on Comparison
- Use element comparisons to sort
- Lower bound O(n log n) for comparison sorts
- **Stable**: Bubble, Insertion, Merge, Tim
- **In-place**: Quick, Heap, Bubble, Insertion, Selection

### Non-Comparison
- Depend on key ranges or key characteristics
- Can achieve better than O(n log n) for special cases
- Stable: Counting, Bucket, Pigeonhole

### Hybrid
- Combine multiple strategies
- Tim Sort combines merge and insertion

## Common Complexity Comparison

| Algorithm | Best       | Average    | Worst      | Space    | Stable |
| --------- | ---------- | ---------- | ---------- | -------- | ------ |
| Bubble    | O(n)       | O(n²)      | O(n²)      | O(1)     | Yes    |
| Insertion | O(n)       | O(n²)      | O(n²)      | O(1)     | Yes    |
| Selection | O(n²)      | O(n²)      | O(n²)      | O(1)     | No     |
| Merge     | O(n log n) | O(n log n) | O(n log n) | O(n)     | Yes    |
| Quick     | O(n log n) | O(n log n) | O(n²)      | O(log n) | No     |
| Heap      | O(n log n) | O(n log n) | O(n log n) | O(1)     | No     |
| Counting  | O(n+k)     | O(n+k)     | O(n+k)     | O(n+k)   | Yes    |

## When to Use Which Algorithm?

- **Small datasets (<100 elements)**: Insertion, Bubble, Selection
- **Large random datasets**: Quick (with good pivot), Merge, Heap
- **Limited memory**: In-place sorts (Quick, Heap)
- **Stable sort needed**: Merge, Bubble, Insertion
- **Pre-sorted data**: Insertion, Bubble (optimized versions)
- **Limited range keys**: Counting, Radix, Bucket
- **Data types**: Primitive types (Quick), Objects (Merge for stability)

## Leetcode Problem Patterns

| Pattern                  | Problems                                                                                                                   |
| ------------------------ | -------------------------------------------------------------------------------------------------------------------------- |
| **Sort Arrays**          | [912. Sort an Array](https://leetcode.com/problems/sort-an-array/)                                                         |
| **Sort + Search**        | [34. Find First and Last Position](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) |
| **Sort + Two Pointers**  | [15. 3Sum](https://leetcode.com/problems/3sum/), [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/)           |
| **Sort + Prefix Sum**    | [325. Maximum Size Subarray Sum Equals K](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)               |
| **Sort Objects/Lambda**  | [179. Largest Number](https://leetcode.com/problems/largest-number/)                                                       |
| **Counting Sort Tricks** | [274. H-Index](https://leetcode.com/problems/h-index/)                                                                     |
| **Bucket Sort**          | [451. Sort Characters By Frequency](https://leetcode.com/problems/sort-characters-by-frequency/)                           |
| **Radix Sort**           | [164. Maximum Gap](https://leetcode.com/problems/maximum-gap/)                                                             |

## Implementation Tips

1. **Java Arrays.sort()**: Uses Tim Sort (hybrid merge + insertion)
2. **Java Collections.sort()**: Merge sort derivative
3. **Choose based on constraints**: Stability, space, time
4. **Avoid unstable sorts** when relative order matters
