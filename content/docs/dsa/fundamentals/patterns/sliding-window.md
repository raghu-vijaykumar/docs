---
aliases: [""]
weight: 2
bookFlatSection: true
title: "Sliding Window"
draft: false
---

# Sliding Window

{{< markmap >}}

```markmap
# Sliding Window
- Fixed-Size Window → Used in problems with a fixed `k` size.
- Variable-Size Window → Expands or contracts based on constraints.
- Monotonic Deque → Efficiently finds min/max in a sliding window.
- Prefix Sum + Sliding Window → Helps in sum-based constraints.
- Binary Search + Sliding Window → Optimizes window size decisions.
- Two Sliding Windows → Useful when tracking multiple constraints.
- Counter Array + Sliding Window → Optimized frequency tracking.
- Bitmasking + Sliding Window → Rare but useful for some substring problems.
- Two Pointers + Sliding Window → Helps in substring and interval merging problems.
```

{{< /markmap >}}

# Leetcode Problems

| **Level**     | **Problem Name & Link**                                                                                                                      | **Technique Used**             |
| ------------- | -------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------ |
| 🟢 **Easy**   | [121. Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)                                       | Fixed-Size Window              |
| 🟢 **Easy**   | [219. Contains Duplicate II](https://leetcode.com/problems/contains-duplicate-ii/)                                                           | HashMap + Sliding Window       |
| 🟢 **Easy**   | [1838. Frequency of the Most Frequent Element](https://leetcode.com/problems/frequency-of-the-most-frequent-element/)                        | Prefix Sum + Sliding Window    |
| 🟡 **Medium** | [209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/)                                                   | Variable-Size Window           |
| 🟡 **Medium** | [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/)                                                           | Counter Array + Sliding Window |
| 🟡 **Medium** | [1004. Max Consecutive Ones III](https://leetcode.com/problems/max-consecutive-ones-iii/)                                                    | Two Pointers + Variable Window |
| 🟡 **Medium** | [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)                       | Frequency Map + Sliding Window |
| 🟡 **Medium** | [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/)                               | Two Sliding Windows            |
| 🟡 **Medium** | [1423. Maximum Points You Can Obtain from Cards](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/)                    | Binary Search + Sliding Window |
| 🔴 **Hard**   | [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/)                                                         | Monotonic Deque                |
| 🔴 **Hard**   | [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/)                                                      | HashMap + Two Pointers         |
| 🔴 **Hard**   | [30. Substring with Concatenation of All Words](https://leetcode.com/problems/substring-with-concatenation-of-all-words/)                    | HashMap + Sliding Window       |
| 🔴 **Hard**   | [1493. Longest Subarray of 1’s After Deleting One Element](https://leetcode.com/problems/longest-subarray-of-1s-after-deleting-one-element/) | Two Pointers                   |
