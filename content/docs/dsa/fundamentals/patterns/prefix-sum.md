---
aliases: [""]
weight: 3
bookFlatSection: true
title: "Prefix Sum"
draft: false
---

# Prefix Sum

{{< markmap >}}

```markmap
# Prefix Sum
- Cumulative Sum → Used to compute subarray sums efficiently.
- Prefix Sum with HashMap → Used for problems involving sum constraints.
- Prefix Sum + Binary Search → Helps in sum-based optimizations.
- Prefix Sum + Difference Array → Used to modify array ranges efficiently.
- Prefix Sum + Modulo → Helps in problems related to divisibility and remainders.
- Prefix Sum + Sliding Window → Optimized sum-based problems.
- **2D Prefix Sum** → Used for sum queries in matrices.
  - **2D Cumulative Sum** → Efficiently calculates rectangular subarray sums.
  - **2D Difference Array** → Handles matrix updates efficiently.
```

{{< /markmap >}}

# Leetcode Problems

| **Level**     | **Problem Name & Link**                                                                                                                       | **Technique Used**            |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| 🟢 **Easy**   | [303. Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)                                                  | Cumulative Sum                |
| 🟢 **Easy**   | [724. Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)                                                                      | Prefix Sum                    |
| 🟡 **Medium** | [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)                                                            | Prefix Sum with HashMap       |
| 🟡 **Medium** | [238. Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/)                                              | Prefix Product                |
| 🟡 **Medium** | [930. Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)                                                    | Prefix Sum + HashMap          |
| 🟡 **Medium** | [1524. Number of Subarrays With Odd Sum](https://leetcode.com/problems/number-of-subarrays-with-odd-sum/)                                     | Prefix Sum + Modulo           |
| 🟡 **Medium** | [2090. K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)                                                 | Prefix Sum + Sliding Window   |
| 🟡 **Medium** | [1074. Number of Submatrices That Sum to Target](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/)                     | 2D Prefix Sum + HashMap       |
| 🟡 **Medium** | [304. Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)                                            | 2D Cumulative Sum             |
| 🔴 **Hard**   | [327. Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/)                                                                  | Prefix Sum + Binary Search    |
| 🔴 **Hard**   | [1171. Remove Zero Sum Consecutive Nodes from Linked List](https://leetcode.com/problems/remove-zero-sum-consecutive-nodes-from-linked-list/) | Prefix Sum + HashMap          |
| 🔴 **Hard**   | [1685. Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)           | Prefix Sum                    |
| 🔴 **Hard**   | [363. Max Sum of Rectangle No Larger Than K](https://leetcode.com/problems/max-sum-of-rectangle-no-larger-than-k/)                            | 2D Prefix Sum + Binary Search |
