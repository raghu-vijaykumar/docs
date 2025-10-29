---
aliases: [""]
weight: 3
bookFlatSection: true
title: "XOR-Based Algorithms"
draft: false
---

# XOR-Based Algorithms

{{< markmap >}}

```markmap
# XOR-Based Algorithms
- **Properties** → XOR is associative, commutative, a ⊕ a = 0, a ⊕ 0 = a
- **Common Use Cases**
  - Find single non-duplicate number
  - Swap variables without temp
  - Check bit differences
  - Encode/decode simple ciphers
- **Tricks**
  - XOR all elements: duplicates cancel out
  - Use partitioning for multiple uniques
```

{{< /markmap >}}

XOR operations are powerful for problems involving duplicates or bit manipulation due to its unique properties (associative, commutative, inverse).

## Theory

Key properties:
- a ⊕ a = 0 (XOR same gives 0)
- a ⊕ 0 = a (XOR identity)
- a ⊕ b = c ⇒ a ⊕ c = b (XOR is reversible)
- In array with even duplicates except one: XOR all elements gives the unique one.

## Common XOR Problems

### 1. Single Number (XOR all elements)
Input: [4,1,2,1,2], Output: 4

```java
public int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) result ^= num;
    return result;
}
```

### 2. Two Unique Numbers (Divide by rightmost set bit)
Input: [1,2,1,3,2,5], Output: [3,5]

```java
public int[] singleNumberIII(int[] nums) {
    int xor = 0;
    for (int num : nums) xor ^= num;

    int mask = xor & -xor; // Rightmost set bit

    int a = 0, b = 0;
    for (int num : nums) {
        if ((num & mask) != 0) a ^= num;
        else b ^= num;
    }
    return new int[]{a, b};
}
```

### 3. Swapping Variables
No temp swap: a ^= b; b ^= a; a ^= b;

```java
// Swap a and b using XOR
void swapXor(int a, int b) {
    a ^= b;
    b ^= a;
    a ^= b;
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                  | Technique Used   |
| -------- | -------------------------------------------------------------------------------------------------------------------- | ---------------- |
| 🟢 Easy   | [136. Single Number](https://leetcode.com/problems/single-number/)                                                   | XOR All Elements |
| 🟡 Medium | [260. Single Number III](https://leetcode.com/problems/single-number-iii/)                                           | XOR Partitioning |
| 🟡 Medium | [389. Find the Difference](https://leetcode.com/problems/find-the-difference/)                                       | XOR Difference   |
| 🔴 Hard   | [421. Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/) | Trie for XOR     |
