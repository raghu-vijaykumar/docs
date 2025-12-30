---
aliases: [""]
weight: 6
bookFlatSection: true
title: "Burrows-Wheeler Transform (BWT)"
draft: false
---

# Burrows-Wheeler Transform (BWT)

{{< markmap >}}

```markmap
# Burrows-Wheeler Transform (BWT)
- **Purpose** → Reversible data compression transform
- **BWT Prop. Size** → Rearrange string to group similar chars
- **Incompressible** → Can be applied multiple times
- **Applications**
  - Text compression (bzip2)
  - Dimensional search algorithms
- **Complexity** → Sorting n strings of size n → O(n² log n)
```

{{< /markmap >}}

The Burrows-Wheeler Transform rearranges a string to bring similar characters together, making it compressible. It's used in bzip2 compression.

## Theory

Create all cyclic shifts (rotations), sort them lexicographically, take last column as BWT string.

Example: "banana$" 

Rotations:
banana$
anana$b
nana$ba
ana$ban
na$ bana
a$ banan
$ banana

Sorted, last column: "annb$aa" (BWT)

## Code Snippet (Java)

```java
public class BWT {
    public static String transform(String s) {
        s += '$'; // Sentinel
        int n = s.length();
        String[] rotations = new String[n];

        for (int i = 0; i < n; i++) {
            rotations[i] = s.substring(i) + s.substring(0, i);
        }

        Arrays.sort(rotations);
        StringBuilder bwt = new StringBuilder();
        for (String r : rotations) {
            bwt.append(r.charAt(n - 1));
        }
        return bwt.toString();
    }

    // Inverse BWT (simplified, needs full implementation for production)
    public static String inverse(String bwt) {
        // Requires LF mapping and sorting
        return ""; // Placeholder
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                             | Technique Used |
| -------- | ----------------------------------------------------------------------------------------------- | -------------- |
| 🟡 Medium | [1044. Longest Duplicate Substring](https://leetcode.com/problems/longest-duplicate-substring/) | Suffix Arrays  |
