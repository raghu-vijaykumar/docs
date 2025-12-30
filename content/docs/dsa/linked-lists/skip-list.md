---
aliases: [""]
weight: 4
bookFlatSection: true
title: "Skip List"
draft: false
---

# Skip List

{{< markmap >}}

```markmap
# Skip List
- **Purpose** → Searchable linked list with multiple layers
- **Structure** → Multi-level linked list with fast lanes
- **Operations**
  - Search: O(log n)
  - Insert: O(log n)
  - Delete: O(log n)
- **Space** → O(n)
- **Use Cases**
  - Alternative to balanced trees
  - Ordered set/map implementations
  - Redis sorted sets
```

{{< /markmap >}}

Skip List is a data structure that implements ordered elements by layering linked lists with "express lanes" for faster searching.

## Theory

Like a linked list but with multiple levels: bottom level has all elements, upper levels skip some for faster traversal.

Coin flip decides height during insertion.

Operations similar to binary search trees but easier to implement.

## Code Snippet (Java)

### Basic Skip List Implementation

```java
import java.util.Random;

public class SkipListNode {
    int value;
    SkipListNode[] forward;

    public SkipListNode(int value, int level) {
        this.value = value;
        this.forward = new SkipListNode[level];
    }
}

public class SkipList {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5; // Probability
    private SkipListNode head;
    private int level;
    private Random random;

    public SkipList() {
        head = new SkipListNode(Integer.MIN_VALUE, MAX_LEVEL);
        level = 0;
        random = new Random();
    }

    private int randomLevel() {
        int lev = 1;
        while (random.nextDouble() < P && lev < MAX_LEVEL) lev++;
        return lev;
    }

    public void insert(int value) {
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode current = head;

        // Find insert position
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].value < value) {
                current = current.forward[i];
            }
            update[i] = current;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) update[i] = head;
            level = newLevel;
        }

        SkipListNode newNode = new SkipListNode(value, newLevel);
        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    public boolean search(int value) {
        SkipListNode current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].value < value) {
                current = current.forward[i];
            }
        }
        current = current.forward[0];
        return current != null && current.value == value;
    }
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                     | Technique Used        |
| -------- | ----------------------------------------------------------------------- | --------------------- |
| 🟡 Medium | [146. LRU Cache](https://leetcode.com/problems/lru-cache/)              | Similar caching ideas |
| 🟡 Medium | [1206. Design Skiplist](https://leetcode.com/problems/design-skiplist/) | Skip List             |
