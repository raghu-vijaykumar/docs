---
weight: 1
bookFlatSection: true
title: "Linear Search"
draft: false
---

# Linear Search

{{< markmap >}}

```markmap
# Linear Search
- **Concept** → Sequential traversal from start to end
- **Time Complexity** → O(n)
- **Space Complexity** → O(1)
- **Advantages** → Simple, works on unsorted data
- **Disadvantages** → Inefficient for large datasets
- **Use Cases** → Small arrays, unsorted data, when array size is unknown
```

{{< /markmap >}}

Linear Search is the simplest searching algorithm that checks every element until the target is found or the end is reached.

## Theory

Start from the first element, compare with target, move to next. Continue until found or end.

Best for small arrays or when data is unsorted.

## Code Snippet (Java)

```java
public class LinearSearch {
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }
}
```

## Leetcode Problems

| Level  | Problem Name & Link                                                | Technique Used |
| ------ | ------------------------------------------------------------------ | -------------- |
| 🟢 Easy | [1. Two Sum](https://leetcode.com/problems/two-sum/)               | Linear Search  |
| 🟢 Easy | [136. Single Number](https://leetcode.com/problems/single-number/) | Linear Scan    |
