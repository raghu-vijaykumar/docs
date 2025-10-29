---
aliases: [""]
weight: 9
bookCollapseSection: true
title: "Bit Manipulation"
draft: false
---

# Bit Manipulation

{{< markmap >}}

```markmap
# Bit Manipulation
- **Basic Concepts**
  - AND (`&`), OR (`|`), XOR (`^`), NOT (`~`)
  - Left Shift (`<<`), Right Shift (`>>`)
- **Counting Bits**
  - Hamming Weight
  - Brian Kernighan’s Algorithm
- **Bit Masking**
  - Checking if a bit is set
  - Setting, clearing, toggling bits
- **Power of Two & Parity**
  - Checking if a number is a power of two
  - Counting set bits efficiently
- **Swapping & Arithmetic Tricks**
  - Swapping two numbers without extra space
  - Finding the single non-repeating element
- **Subsets & Combinations using Bits**
  - Generating all subsets using bitmasking
  - Dynamic Programming with bitmasking
- **Gray Code**
  - Generating Gray code sequence
  - Converting between binary and Gray code
```

{{< /markmap >}}

# Leetcode Problems

| Level     | Problem Name & Link                                                                                      | Technique Used                   |
| --------- | -------------------------------------------------------------------------------------------------------- | -------------------------------- |
| 🟢 Easy   | [136. Single Number](https://leetcode.com/problems/single-number/)                                       | XOR Trick                        |
| 🟢 Easy   | [190. Reverse Bits](https://leetcode.com/problems/reverse-bits/)                                         | Bit Manipulation                 |
| 🟢 Easy   | [191. Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/)                                 | Hamming Weight, Kernighan’s Algo |
| 🟡 Medium | [78. Subsets](https://leetcode.com/problems/subsets/)                                                    | Bitmasking for Subsets           |
| 🟡 Medium | [137. Single Number II](https://leetcode.com/problems/single-number-ii/)                                 | Bit Manipulation                 |
| 🟡 Medium | [260. Single Number III](https://leetcode.com/problems/single-number-iii/)                               | XOR Trick, Bitmasking            |
| 🟡 Medium | [338. Counting Bits](https://leetcode.com/problems/counting-bits/)                                       | Dynamic Programming, Bit Count   |
| 🟡 Medium | [201. Bitwise AND of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range/)         | Bit Manipulation                 |
| 🟡 Medium | [231. Power of Two](https://leetcode.com/problems/power-of-two/)                                         | Checking Bit Set                 |
| 🟡 Medium | [393. UTF-8 Validation](https://leetcode.com/problems/utf-8-validation/)                                 | Bit Masking, Bitwise Operations  |
| 🔴 Hard   | [1542. Find Longest Awesome Substring](https://leetcode.com/problems/find-longest-awesome-substring/)    | Bit Manipulation + Hashing       |
| 🔴 Hard   | [465. Optimal Account Balancing](https://leetcode.com/problems/optimal-account-balancing/)               | Dynamic Programming + Bitmasking |
| 🔴 Hard   | [847. Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) | Bitmasking + BFS                 |
| 🔴 Hard   | [996. Number of Squareful Arrays](https://leetcode.com/problems/number-of-squareful-arrays/)             | Backtracking + Bitmasking        |
| 🔴 Hard   | [1349. Maximum Students Taking Exam](https://leetcode.com/problems/maximum-students-taking-exam/)        | Bitmasking + DP                  |
