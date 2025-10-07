---
weight: 4
bookFlatSection: true
title: "Rabin-Karp Search"
draft: false
---

# Rabin-Karp Search

{{< markmap >}}

```markmap
# Rabin-Karp Search
- **Concept** → Rolling hash for efficient string matching
- **Hash Function** → Uses polynomial rolling hash
- **Time Complexity** → Average O(m + n), Worst O(mn)
- **Space Complexity** → O(1) extra space
- **Advantages**
  - Multiple pattern search efficient
  - Better for large alphabets
- **Steps**
  - Compute pattern hash
  - Compute text substrings hashes
  - Compare only when hashes match
```

{{< /markmap >}}

Rabin-Karp is a string searching algorithm that uses hashing to find substrings. It computes hashes of the pattern and sliding windows of the text, comparing hashes first (fast) before character-by-character check.

## Theory

**Rolling Hash**: Instead of recomputing hash each time, updates hash in O(1) by subtracting old character and adding new one, multiplied by base powers.

Reduces spurious hits (hash matches but strings differ) by checking characters when hashes match.

## Code Snippet (Java)

```java
public class RabinKarp {
    private static final int PRIME = 101; // Prime modulus

    public static void search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        long textHash = 0, patternHash = 0, h = 1;

        // h = pow(BASE, m-1) % PRIME
        for (int i = 0; i < m - 1; i++)
            h = (h * 256) % PRIME;

        // Calculate initial hash for pattern and first window
        for (int i = 0; i < m; i++) {
            textHash = (256 * textHash + text.charAt(i)) % PRIME;
            patternHash = (256 * patternHash + pattern.charAt(i)) % PRIME;
        }

        // Slide window
        for (int i = 0; i <= n - m; i++) {
            // Check hash match
            if (textHash == patternHash) {
                // Character check to avoid spurious hits
                int j;
                for (j = 0; j < m; j++)
                    if (text.charAt(i + j) != pattern.charAt(j))
                        break;
                if (j == m) System.out.println("Pattern at index " + i);
            }

            // Calculate next hash
            if (i < n - m) {
                textHash = (256 * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;
                if (textHash < 0) textHash += PRIME; // Make positive
            }
        }
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                                         | Technique Used |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| 🟡 Medium | [28. Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/) | String Search  |
| 🟡 Medium | [1044. Longest Duplicate Substring](https://leetcode.com/problems/longest-duplicate-substring/)                                             | Rolling Hash   |
