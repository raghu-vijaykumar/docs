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

Chinese Remainder Theorem states that if we have a system of simultaneous congruences with pairwise coprime moduli, then there exists a unique solution modulo the product of moduli.

## Theory

Given: x ≡ a₁ (mod m₁), x ≡ a₂ (mod m₂), ..., x ≡ aₙ (mod mₙ), where gcd(mᵢ, mⱼ) = 1 for i ≠ j.

Then, there is a unique x mod M, where M = m₁ × m₂ × ... × mₙ.

To find x, we can use modular inverses. For each equation, find yᵢ such that yᵢ * M/mᵢ * aᵢ where M/mᵢ has inverse mod mᵢ.

## Code Snippet (Java)

### Basic CRT for two congruences

```java
// Extended Euclidean Algorithm for inverse
public static long[] extendedGcd(long a, long b) {
    if (b == 0) return new long[]{a, 1, 0};
    long[] res = extendedGcd(b, a % b);
    long gcd = res[0], x = res[2], y = res[1] - (a / b) * res[2];
    return new long[]{gcd, x, y};
}

public static long modInverse(long a, long m) {
    long[] res = extendedGcd(a, m);
    if (res[0] != 1) return -1; // No inverse
    return (res[1] % m + m) % m;
}

public static long chineseRemainder(long[] a, long[] m) {
    int n = a.length;
    long M = 1;
    for (long mi : m) M *= mi;

    long x = 0;
    for (int i = 0; i < n; i++) {
        long Mi = M / m[i];
        long inv = modInverse(Mi, m[i]);
        x = (x + a[i] * Mi % M * inv % M) % M;
    }
    return x;
}
```

Example: x ≡ 2 (mod 3), x ≡ 3 (mod 5), x ≡ 2 (mod 7)
a = {2,3,2}, m = {3,5,7}, result x = 23 mod 105.

# Leetcode Problems

| Level    | Problem Name & Link                                                                          | Technique Used               |
| -------- | -------------------------------------------------------------------------------------------- | ---------------------------- |
| 🟡 Medium | [1492. The kth Factor of n](https://leetcode.com/problems/the-kth-factor-of-n/)              | Modular Arithmetic           |
| 🟡 Medium | [1201. Ugly Number III](https://leetcode.com/problems/ugly-number-iii/)                      | CRT + Binary Search          |
| 🔴 Hard   | [365. Water and Jug Problem](https://leetcode.com/problems/water-and-jug-problem/)           | Extended Euclidean Algorithm |
| 🔴 Hard   | [927. Three Equal Parts](https://leetcode.com/problems/three-equal-parts/)                   | Modular Arithmetic + CRT     |
| 🔴 Hard   | [372. Super Pow](https://leetcode.com/problems/super-pow/)                                   | Modular Exponentiation + CRT |
| 🔴 Hard   | [479. Largest Palindrome Product](https://leetcode.com/problems/largest-palindrome-product/) | Number Theory + CRT          |
