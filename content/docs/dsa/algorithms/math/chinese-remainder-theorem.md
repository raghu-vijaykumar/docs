---
weight: 1
bookFlatSection: true
title: "Chinese Remainder Theorem"
draft: false
---

# Chinese Remainder Theorem (CRT)

{{< markmap >}}

```markmap
# Chinese Remainder Theorem (CRT)
- **Definition**
  - Solves systems of congruences with pairwise coprime moduli.
- **Use Cases**
  - Efficient modular arithmetic.
  - Solving large modular equations.
  - Cryptography (RSA, ElGamal).
- **Mathematical Formulation**
  - Given x ≡ a₁ (mod m₁), x ≡ a₂ (mod m₂), ..., x ≡ aₙ (mod mₙ).
  - Solution exists if moduli are pairwise coprime.
- **Methods**
  - **Naive Method** → Brute force checking for smallest x.
  - **Constructive Method** → Using modular inverses.
  - **Extended Euclidean Algorithm** → Finding modular inverse.
- **Implementation**
  - Iterative approach.
  - Using Garner’s Algorithm.
  - Applying with Modular Exponentiation.
```

{{< /markmap >}}

# Leetcode Problems

| Level     | Problem Name & Link                                                                          | Technique Used               |
| --------- | -------------------------------------------------------------------------------------------- | ---------------------------- |
| 🟡 Medium | [1492. The kth Factor of n](https://leetcode.com/problems/the-kth-factor-of-n/)              | Modular Arithmetic           |
| 🟡 Medium | [1201. Ugly Number III](https://leetcode.com/problems/ugly-number-iii/)                      | CRT + Binary Search          |
| 🔴 Hard   | [365. Water and Jug Problem](https://leetcode.com/problems/water-and-jug-problem/)           | Extended Euclidean Algorithm |
| 🔴 Hard   | [927. Three Equal Parts](https://leetcode.com/problems/three-equal-parts/)                   | Modular Arithmetic + CRT     |
| 🔴 Hard   | [372. Super Pow](https://leetcode.com/problems/super-pow/)                                   | Modular Exponentiation + CRT |
| 🔴 Hard   | [479. Largest Palindrome Product](https://leetcode.com/problems/largest-palindrome-product/) | Number Theory + CRT          |
