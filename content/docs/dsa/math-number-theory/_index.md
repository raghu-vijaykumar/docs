---
aliases: [""]
weight: 10
bookCollapseSection: true
title: "Math"
draft: false
---

# Math Algorithms

{{< markmap >}}

```markmap
# Math Algorithms
- **Number Theory**
  - Prime Numbers (Sieve of Eratosthenes)
  - GCD & LCM
  - Modular Arithmetic
  - Chinese Remainder Theorem [](/docs/dsa/math-number-theory/chinese-remainder-theorem/)
- **Combinatorics**
  - Factorials
  - Binomial Coefficients
  - Permutations/Combinations
  - Stars and Bars
- **Numerical Methods**
  - Fast Exponentiation
  - Matrix Operations
  - Continued Fractions
  - Babylonian Square Root
- **Probability & Statistics**
  - Basic Probability
  - Expected Value
  - Variance & Standard Deviation
- **Game Theory**
  - Grundy Numbers (Impartial Games)
  - Sprague-Grundy Theorem
```

{{< /markmap >}}

## Introduction

Mathematical algorithms form the foundation of many computer science problems. They involve number theory, combinatorics, and numerical computations that are essential for solving algorithmic challenges.

## Number Theory

### Prime Numbers and Related Algorithms

#### Sieve of Eratosthenes
Finds all prime numbers up to a limit efficiently.

```java
public boolean[] sieveOfEratosthenes(int n) {
    boolean[] isPrime = new boolean[n + 1];
    Arrays.fill(isPrime, true);

    isPrime[0] = isPrime[1] = false;

    for (long i = 2; i * i <= n; i++) {
        if (isPrime[(int) i]) {
            for (long j = i * i; j <= n; j += i) {
                isPrime[(int) j] = false;
            }
        }
    }

    return isPrime;
}
```

**Time Complexity**: O(n log log n)
**Space Complexity**: O(n)

### Greatest Common Divisor (GCD)

#### Euclidean Algorithm
```java
public int gcd(int a, int b) {
    while (b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}
```

**Recursive Version**:
```java
public int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
}
```

### Extended Euclidean Algorithm
Solves Bezout's identity: ax + by = gcd(a,b)

```java
public static int[] extendedGcd(int a, int b) {
    if (b == 0) {
        return new int[]{1, 0, a}; // x, y, gcd
    }

    int[] vals = extendedGcd(b, a % b);
    int x = vals[1];
    int y = vals[0] - (a / b) * vals[1];
    int gcd = vals[2];

    return new int[]{x, y, gcd};
}
```

### Modular Arithmetic

#### Modular Inverse
Finding x such that (a * x) % m = 1

```java
// Using Extended Euclidean Algorithm
public int modInverse(int a, int m) {
    int[] vals = extendedGcd(a, m);
    if (vals[2] != 1) return -1; // No inverse if not coprime

    return (vals[0] % m + m) % m; // Ensure positive
}

// Using Fermat's Little Theorem (if m is prime)
public int modInversePrime(int a, int p) {
    return modPow(a, p - 2, p);
}
```

#### Fast Modular Exponentiation
Computes (base^exponent) % mod efficiently

```java
public long modPow(long base, long exp, long mod) {
    long result = 1;
    base %= mod;

    while (exp > 0) {
        if ((exp & 1) == 1) { // If exp is odd
            result = (result * base) % mod;
        }
        base = (base * base) % mod;
        exp >>= 1; // Divide exp by 2
    }

    return result;
}
```

## Combinatorics

### Factorials and P&C

```java
class Combinatorics {
    long MOD = 1000000007;

    // Factorial with modulo
    long[] fact;

    public void computeFactorials(int maxN) {
        fact = new long[maxN + 1];
        fact[0] = 1;
        for (int i = 1; i <= maxN; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }
    }

    // nCr % MOD
    public long nCr(int n, int r) {
        if (r > n || r < 0) return 0;
        if (r == 0 || r == n) return 1;

        long numerator = fact[n];
        long denominator = (fact[r] * fact[n - r]) % MOD;

        return (numerator * modInverse(denominator, MOD)) % MOD;
    }

    // nPr
    public long nPr(int n, int r) {
        if (r > n || r < 0) return 0;
        return (fact[n] * modInverse(fact[n - r], MOD)) % MOD;
    }

    // Modular inverse using Fermat's Little Theorem
    private long modInverse(long a, long m) {
        return modPow(a, m - 2, m);
    }

    private long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }
}
```

### Stars and Bars Theorem
Number of ways to distribute n identical items into k distinct groups (each group can be empty): C(n+k-1, k-1)

### Inclusion-Exclusion Principle
|A ∪ B ∪ C| = |A| + |B| + |C| - |A∩B| - |A∩C| - |B∩C| + |A∩B∩C|

## Expected Value and Probability

### Expected Value
For discrete random variables: E[X] = Σ(x * P(X = x))

### Variance: Var(X) = E[X²] - (E[X])²
Standard Deviation = √Var(X)

### Linearity of Expectation
E[X + Y] = E[X] + E[Y] (even when X and Y are dependent)

## Game Theory Basics

### Sprague-Grundy Theorem
Every impartial game can be assigned a Grundy number (nimber).

- Position with Grundy number 0 is losing position
- XOR of position Grundy numbers determines overall game Grundy number

## Practice Problems

### Easy
- [50. Pow(x, n)](https://leetcode.com/problems/powx-n/) - Modular exponentiation
- [204. Count Primes](https://leetcode.com/problems/count-primes/) - Sieve application
- [172. Factorial Trailing Zeroes](https://leetcode.com/problems/factorial-trailing-zeroes/)

### Medium
- [372. Super Pow](https://leetcode.com/problems/super-pow/) - Big exponentiation
- [793. Preimage Size of Factorial Zeroes Function](https://leetcode.com/problems/preimage-size-of-factorial-zeroes-function/)
- [878. Nth Magical Number](https://leetcode.com/problems/nth-magical-number/) - Chinese Remainder Theorem application

### Hard
- [780. Reaching Points](https://leetcode.com/problems/reaching-points/) - Number theory
- [913. Cat and Mouse](https://leetcode.com/problems/cat-and-mouse/) - Game theory with DP

## Key Takeaways

1. **Modular Arithmetic**: Essential for large numbers - understand inverse and exponentiation
2. **GCD & LCM**: Foundation for many number theory problems
3. **Combinatorics**: Learn to compute C(n,k) and P(n,k) efficiently with memoization/modulo
4. **Sieve of Eratosthenes**: Most efficient way to find primes up to reasonable limits
5. **Probability**: Understand expected value and variance for randomized algorithms
6. **Practice**: Many problems involve combining multiple mathematical concepts
