---
aliases: [""]
weight: 5
bookFlatSection: true
title: "Z-Algorithm"
draft: false
---

# Z-Algorithm

{{< markmap >}}

```markmap
# Z-Algorithm
- **Purpose** → Construct Z-array for prefix matching
- **Z[i]** → Length of longest substring starting at i that matches prefix
- **Time Complexity** → O(n)
- **Space Complexity** → O(n)
- **Applications**
  - String matching
  - Pattern searching
  - Substring problems
```

{{< /markmap >}}

The Z-algorithm constructs an array Z where Z[i] contains the length of the longest substring starting at position i that matches the prefix of the string.

## Theory

For string S, Z[i] is longest substring S[i..] that matches S[0..] as prefix.

Efficiently computed in O(n) using a window [l,r] outside which z values are reset.

## Code Snippet (Java)

```java
public class ZAlgorithm {
    public static int[] computeZArray(String S) {
        int n = S.length();
        int[] Z = new int[n];
        int L = 0, R = 0;

        for (int i = 1; i < n; i++) {
            if (i < R) {
                Z[i] = Z[i - L];
                if (i + Z[i - L] > R) Z[i] = R - i;
            }
            while (i + Z[i] < n && S.charAt(Z[i]) == S.charAt(i + Z[i])) {
                Z[i]++;
            }
            if (i + Z[i] > R) {
                L = i;
                R = i + Z[i];
            }
        }
        return Z;
    }

    // Find all occurrences of pattern in text
    public static List<Integer> searchPattern(String text, String pattern) {
        String concat = pattern + "$" + text;
        int[] Z = computeZArray(concat);
        List<Integer> indices = new ArrayList<>();
        for (int i = pattern.length() + 1; i < concat.length(); i++) {
            if (Z[i] == pattern.length()) indices.add(i - pattern.length() - 1);
        }
        return indices;
    }
}
```

## Leetcode Problems

| Level  | Problem Name & Link                                                               | Technique Used  |
| ------ | --------------------------------------------------------------------------------- | --------------- |
| 🟢 Easy | [14. Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/) | Prefix Matching |
