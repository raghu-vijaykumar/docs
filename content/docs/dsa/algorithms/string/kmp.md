---
weight: 1
bookFlatSection: true
title: "KMP Search"
draft: false
---

# KMP Search

{{< markmap >}}

```markmap
# KMP (Knuth-Morris-Pratt) Search
- **Algorithm** → Linear time pattern matching
- **Key Concept** → LPS (Longest Proper Prefix Suffix) array
- **Time Complexity** → O(m + n)
- **Space Complexity** → O(m)
- **Use Cases**
  - String searching
  - Pattern matching problems
- **Steps**
  - Build LPS array
  - Use LPS to avoid backtracking
```

{{< /markmap >}}

Knuth-Morris-Pratt (KMP) is an efficient string matching algorithm that runs in linear time O(m + n), where m and n are the lengths of the pattern and text respectively. It avoids naive backtracking by precomputing information about the pattern.

## Theory

KMP improves over naive string matching by using a **Longest Proper Prefix that is also Suffix (LPS) array**. This array helps skip unnecessary comparisons when mismatches occur.

**Key Idea**: When a mismatch happens at position i, instead of comparing from i+1, we know how far to shift the pattern based on the prefix information.

## Code Snippet (Java)

```java
public class KMP {
    // Build LPS array
    public static int[] computeLPSArray(String pat) {
        int M = pat.length();
        int[] lps = new int[M];
        int len = 0;
        int i = 1;

        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static void KMPSearch(String pat, String txt) {
        int N = txt.length();
        int M = pat.length();
        int[] lps = computeLPSArray(pat);

        int i = 0; // index for txt
        int j = 0; // index for pat

        while (i < N) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                System.out.println("Found pattern at index " + (i - j));
                j = lps[j - 1]; // Continue searching
            } else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                                         | Technique Used     |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ |
| 🟡 Medium | [28. Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/) | Substring Search   |
| 🔴 Hard   | [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)                                                              | KMP For Palindrome |
