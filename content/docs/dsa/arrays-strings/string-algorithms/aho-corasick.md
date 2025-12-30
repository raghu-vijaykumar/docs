---
aliases: [""]
weight: 2
bookFlatSection: true
title: "Aho-Corasick Algorithm"
draft: false
---

# Aho-Corasick Algorithm

{{< markmap >}}

```markmap
# Aho-Corasick Algorithm
- **Concept** → Multi-pattern string matching automaton
- **Structure** → Trie with failure links
- **Time Complexity** → O(n + m + z) where z is output size
- **Space Complexity** → O(m)
- **Use Cases**
  - Multiple pattern search in text
  - Text search engines
- **Key Components**
  - Goto function (trie)
  - Failure function
  - Output function
```

{{< /markmap >}}

Aho-Corasick builds a finite automaton from multiple patterns for efficient multi-pattern matching in text. It combines a trie with failure links, allowing simultaneous search for all patterns.

## Theory

1. **Build Trie**: Insert all patterns.
2. **Failure Links**: For each node, link to longest proper suffix in trie.
3. **Search**: Traverse text with automaton, report matches using output links.

## Code Snippet (Java)

High-level implementation (full implementation is complex):

```java
// Simplified Aho-Corasick for demonstration
class AhoCorasickNode {
    Map<Character, AhoCorasickNode> children = new HashMap<>();
    AhoCorasickNode fail = null;
    List<String> output = new ArrayList<>();
}

public class AhoCorasick {
    private final AhoCorasickNode root = new AhoCorasickNode();

    public void insert(String word) {
        AhoCorasickNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new AhoCorasickNode());
        }
        node.output.add(word);
    }

    public void buildFailureLinks() {
        Queue<AhoCorasickNode> queue = new LinkedList<>();
        for (AhoCorasickNode child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            AhoCorasickNode current = queue.poll();
            for (Map.Entry<Character, AhoCorasickNode> entry : current.children.entrySet()) {
                char c = entry.getKey();
                AhoCorasickNode child = entry.getValue();
                AhoCorasickNode fail = current.fail;
                while (fail != null && !fail.children.containsKey(c)) fail = fail.fail;
                child.fail = fail != null ? fail.children.get(c) : root;
                child.output.addAll(child.fail.output);
                queue.add(child);
            }
        }
    }

    public List<String> search(String text) {
        List<String> result = new ArrayList<>();
        AhoCorasickNode current = root;
        for (char c : text.toCharArray()) {
            while (current != null && !current.children.containsKey(c)) current = current.fail;
            if (current == null) current = root;
            else current = current.children.get(c);
            result.addAll(current.output);
        }
        return result;
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                                          | Technique Used      |
| -------- | ---------------------------------------------------------------------------------------------------------------------------- | ------------------- |
| 🟡 Medium | [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Trie with Wildcards |
