---
aliases: [""]
weight: 1
bookFlatSection: true
title: "Brian Kernighan’s Algorithm"
draft: false
---

# Brian Kernighan’s Algorithm

{{< markmap >}}

```markmap
# Brian Kernighan’s Algorithm
- **Purpose** → Count number of set bits (1s) in bit representation
- **Key Trick** → n & (n-1) flips lowest set bit to 0
- **Time Complexity** → O(k) where k is number of set bits
- **Space Complexity** → O(1)
- **Use Cases**
  - Bit counting problems
  - Hamming weight
  - Population count in numbers
```

{{< /markmap >}}

Brian Kernighan's Algorithm performs bitwise operation to count the number of 1 bits in an integer. It repeatedly flips the lowest set bit to 0 using n & (n-1), counting each flip.

## Theory

For n = 13 (1101 in binary):
- 13 & 12 = 12 (1100), count=1
- 12 & 11 = 8 (1000), count=2  
- 8 & 7 = 0 (0000), count=3

Each iteration removes rightmost set bit. Stops after k operations where k is set bits.

## Code Snippet (Java)

```java
public class BrianKernighan {
    // Count number of 1 bits
    public static int countSetBits(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1); // Flip lowest set bit
            count++;
        }
        return count;
    }

    // Check if power of 2 (n > 0)
    public static boolean isPowerOf2(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    // Find lowest set bit position (0-based)
    public static int lowestSetBit(int n) {
        return n & -n; // Isolates LSB
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                      | Technique Used      |
| -------- | ------------------------------------------------------------------------ | ------------------- |
| 🟢 Easy   | [191. Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) | Bit Counting        |
| 🟢 Easy   | [342. Power of Four](https://leetcode.com/problems/power-of-four/)       | Power Check         |
| 🟡 Medium | [231. Power of Two](https://leetcode.com/problems/power-of-two/)         | Bitwise Power Check |
