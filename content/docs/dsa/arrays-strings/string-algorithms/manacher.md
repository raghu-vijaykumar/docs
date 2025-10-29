---
aliases: [""]
weight: 3
bookFlatSection: true
title: "Manacher’s Algorithm"
draft: false
---

# Manacher’s Algorithm

{{< markmap >}}

```markmap
# Manacher’s Algorithm
- **Problem** → Find longest palindromic substring
- **Time Complexity** → O(n)
- **Space Complexity** → O(n)
- **Technique** → Expand around centers with optimization
- **Use Cases**
  - Palindrome detection
  - Text analysis problems
- **Key Insight** → Avoid redundant palindrome lengths
```

{{< /markmap >}}

Manacher’s Algorithm efficiently finds the longest palindromic substring in linear time by expanding around potential centers and using a radius array to avoid redundant checks for symmetric positions.

## Theory

1. Preprocess string with delimiters (e.g., '#' between chars) to handle both odd and even length palindromes.
2. Use an array P to store palindrome radii.
3. Use mirror properties to speed up calculations via center/right expansion.

## Code Snippet (Java)

```java
public class Manacher {
    public static String longestPalindrome(String s) {
        if (s == null || s.length() < 1) return "";

        String transformed = preprocess(s);
        int[] P = new int[transformed.length()];
        int C = 0, R = 0;

        for (int i = 1; i < transformed.length() - 1; i++) {
            int iMirror = 2 * C - i;

            if (R > i) P[i] = Math.min(P[iMirror], R - i);

            while (transformed.charAt(i + P[i] + 1) == transformed.charAt(i - P[i] - 1)) {
                P[i]++;
            }

            if (i + P[i] > R) {
                C = i;
                R = i + P[i];
            }
        }

        int maxIndex = 0;
        for (int i = 1; i < P.length; i++) {
            if (P[i] > P[maxIndex]) maxIndex = i;
        }

        return s.substring((maxIndex - P[maxIndex]) / 2, (maxIndex + P[maxIndex]) / 2);
    }

    private static String preprocess(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('^');
        for (char c : s.toCharArray()) {
            sb.append('#').append(c);
        }
        sb.append("#$");
        return sb.toString();
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                              | Technique Used       |
| -------- | ------------------------------------------------------------------------------------------------ | -------------------- |
| 🟡 Medium | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | Manacher’s Algorithm |
