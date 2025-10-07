---
weight: 2
bookFlatSection: true
title: "Sieve of Eratosthenes"
draft: false
---

# Sieve of Eratosthenes

{{< markmap >}}

```markmap
# Sieve of Eratosthenes
- **Purpose** → Find all prime numbers up to n
- **Algorithm** → Eliminate multiples of each prime
- **Time Complexity** → O(n log log n)
- **Space Complexity** → O(n)
- **Use Cases**
  - Generating prime numbers
  - Prime checking in range
- **Optimizations**
  - Start from i*i instead of 2*i
  - Odd numbers only
```

{{< /markmap >}}

Sieve of Eratosthenes is an ancient algorithm to find all primes up to n efficiently. It marks composites by iterating over primes.

## Theory

1. Initialize boolean array isPrime[0..n] = true
2. Mark 0,1 as false
3. For each prime i from 2 to sqrt(n):
   - If isPrime[i], mark all multiples of i as false (start from i*i to optimize)

## Code Snippet (Java)

```java
public class SieveOfEratosthenes {
    public static boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;

        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                // Mark multiples starting from i*i
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        return isPrime;
    }

    // Count primes <= n
    public static int countPrimes(int n) {
        boolean[] isPrime = sieve(n);
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) count++;
        }
        return count;
    }

    // Get list of primes
    public static List<Integer> getPrimes(int n) {
        boolean[] isPrime = sieve(n);
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes;
    }
}
```

## Leetcode Problems

| Level  | Problem Name & Link                                              | Technique Used       |
| ------ | ---------------------------------------------------------------- | -------------------- |
| 🟢 Easy | [204. Count Primes](https://leetcode.com/problems/count-primes/) | Sieve Implementation |
