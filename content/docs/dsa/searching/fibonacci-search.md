---
aliases: [""]
weight: 5
bookFlatSection: true
title: "Fibonacci Search"
draft: false
---

# Fibonacci Search

{{< markmap >}}

```markmap
# Fibonacci Search
- **Concept** → Division based on Fibonacci numbers instead of midpoint
- **Advantage** → Fewer divisions, efficient on linked lists
- **Algorithm**
  - Generate Fibonacci numbers
  - Use fib(k) to find search range
  - Narrow based on comparisons
- **Time Complexity** → O(log n)
- **Space Complexity** → O(1)
- **Use Cases**
  - CPU-friendly (fewer arithmetic operations)
  - Good for memory-constrained environments
```

{{< /markmap >}}

Fibonacci Search is a searching algorithm that uses Fibonacci numbers to divide the array, similar to how binary search uses the midpoint. It's particularly useful in environments where division is expensive or on sequential access devices likefibona linked lists.

## Theory

Instead of dividing by 2, Fibonacci search divides by Fibonacci ratios. Find the smallest Fibonacci number >= array length, then search by comparing with elements at Fibonacci positions.

Efficient because it requires only addition/subtraction operations, no division.

## Code Snippet (Java)

```java
public class FibonacciSearch {
    // Generate Fibonacci number >= n
    private static int getFib(int n) {
        if (n <= 1) return 1;
        int a = 0, b = 1;
        while (b < n) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    public static int fibonacciSearch(int[] arr, int target) {
        int n = arr.length;
        int fib = getFib(n);

        int offset = -1; // offset for fib placement

        int fibM2 = fib - fib;  // fib-2, actually 0
        int fibM1 = fib;       // fib-1
        int fibM = fib;        // fib

        while (fibM > 1) {
            int i = Math.min(offset + fibM2, n - 1);

            if (arr[i] < target) {
                fib = fibM;
                fibM1 = fibM1 - fibM;
                fibM2 = fibM - fib - fibM1;
                fibM = fib;
            } else if (arr[i] > target) {
                fib = fibM1;
                fibM = fib - fibM1;
                fibM1 = fibM1 - fibM;
                fibM2 = fibM1 - fibM;
            } else {
                return i;
            }
        }

        if (fibM1 == 1 && offset + 1 < n && arr[offset + 1] == target) {
            return offset + 1;
        }

        return -1;
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                   | Technique Used  |
| -------- | ------------------------------------------------------------------------------------- | --------------- |
| 🟡 Medium | [1095. Find in Mountain Array](https://leetcode.com/problems/find-in-mountain-array/) | Search Variants |
