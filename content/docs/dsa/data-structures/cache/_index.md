---
weight: 1
bookCollapseSection: true
title: "Cache"
draft: false
---

# Cache Data Structures

{{< markmap >}}

```markmap
# Cache Eviction Policies
- **LRU (Least Recently Used)**
  - Evicts least recently accessed items
  - Uses doubly-linked list + hash map for O(1) operations
- **LFU (Least Frequently Used)**
  - Evicts least frequently accessed items
  - Uses frequency count + same frequency lists
  - More complex implementation but better for some patterns
- **Other Policies**
  - FIFO (First In First Out)
  - Random eviction
  - MRU (Most Recently Used)
```

{{< /markmap >}}

## Introduction to Caches

Caches are temporary storage areas that store frequently accessed data for faster retrieval. Cache eviction policies determine which items to remove when the cache is full.

### Why Cache Eviction Matters?

- **Memory Constraints**: Limited cache size requires removal decisions
- **Performance**: Good eviction policies maximize cache hit ratios
- **Access Patterns**: Different policies work better for different usage patterns

## Fundamental Cache Operations

```java
interface Cache<K, V> {
    V get(K key);           // Get value, may trigger eviction logic
    void put(K key, V value); // Insert/update value, may evict if full
    void remove(K key);     // Remove specific key
    int size();             // Current number of items
    int capacity();         // Maximum capacity
    void clear();           // Clear all items
}
```

## LRU Cache (Least Recently Used)

### Design Approach
- **Data Structures**: HashMap + Doubly Linked List
- **HashMap**: Key → List Node (for O(1) access)
- **Doubly Linked List**: Maintains order of usage (MRU to LRU)

### Implementation

```java
class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> list;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.list = new DoublyLinkedList<>();
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }

        Node<K, V> node = cache.get(key);
        list.moveToFront(node); // Most recently used
        return node.value;
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            // Update existing
            Node<K, V> node = cache.get(key);
            node.value = value;
            list.moveToFront(node);
        } else {
            // Add new
            if (cache.size() >= capacity) {
                Node<K, V> lru = list.removeLast();
                cache.remove(lru.key);
            }

            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            list.addToFront(newNode);
        }
    }

    // Node and DoublyLinkedList classes...
}
```

### Time Complexity
- **Get**: O(1)
- **Put**: O(1)
- **Space**: O(capacity)

### When LRU Works Well
- **Temporal Locality**: Recently accessed items are likely to be accessed again
- **Progressive Access**: Sequential or repeating access patterns
- **Cache Conscious Algorithms**: Algorithms that benefit from keeping recent data

## LFU Cache (Least Frequently Used)

### Design Approach
- **Multiple Data Structures**:
  - HashMap: Key → Node (frequency info)
  - HashMap: Frequency → DoublyLinkedList of nodes
  - Maintain minimum frequency for eviction decisions

### Key Challenges
- **Frequency Tracking**: Each item tracks access count
- **Same Frequency Grouping**: Items with same frequency in lists
- **Minimum Frequency Tracking**: For fast eviction decisions

### Implementation

```java
class LFUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Map<Integer, DoublyLinkedList<K, V>> freqMap;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 0;
    }

    public V get(K key) {
        if (!cache.containsKey(key)) return null;

        Node<K, V> node = cache.get(key);
        updateFrequency(node);
        return node.value;
    }

    public void put(K key, V value) {
        if (capacity == 0) return;

        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.value = value;
            updateFrequency(node);
        } else {
            if (cache.size() >= capacity) {
                evictLeastFrequent();
            }

            Node<K, V> newNode = new Node<>(key, value, 1);
            cache.put(key, newNode);

            freqMap.computeIfAbsent(1, k -> new DoublyLinkedList<>()).addToFront(newNode);
            minFreq = 1;
        }
    }

    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.freq;
        node.freq++;

        // Remove from old frequency list
        freqMap.get(oldFreq).remove(node);

        // Add to new frequency list
        freqMap.computeIfAbsent(node.freq, k -> new DoublyLinkedList<>()).addToFront(node);

        // Update minFreq if needed
        if (oldFreq == minFreq && freqMap.get(oldFreq).isEmpty()) {
            minFreq++;
        }
    }

    private void evictLeastFrequent() {
        DoublyLinkedList<K, V> minFreqList = freqMap.get(minFreq);
        Node<K, V> lfu = minFreqList.removeLast();
        cache.remove(lfu.key);

        if (minFreqList.isEmpty()) {
            freqMap.remove(minFreq);
        }
    }
}
```

### Time Complexity
- **Get**: O(1)
- **Put**: O(1)
- **Space**: O(capacity)

### When LFU Works Well
- **Frequency Patterns**: Some items accessed much more than others
- **Long-term Access Patterns**: Better for systems with stable access frequencies
- **Zipfian Distributions**: Power-law access patterns in real systems

## LRU vs LFU Comparison

| Aspect              | LRU                               | LFU                             |
| ------------------- | --------------------------------- | ------------------------------- |
| **Policy**          | Time-based recency                | Frequency-based                 |
| **Complexity**      | Simpler implementation            | More complex                    |
| **Memory Overhead** | Lower                             | Higher (frequency tracking)     |
| **Best For**        | Recent access patterns            | Stable frequency patterns       |
| **Cache Misses**    | Higher for frequency-based misses | Lower for high-frequency items  |
| **Adaptive**        | Adapts to recent changes quickly  | Takes time to learn frequencies |

## Other Eviction Policies

### FIFO (First In First Out)
- Evicts oldest item
- Simple Queue implementation
- Good for: Understanding data flow, simple caching

### Random Eviction
- Random item removal
- Very simple, no tracking overhead
- Good for: Approximate caching, low memory systems

### MRU (Most Recently Used)
- Evicts most recently accessed item
- Opposite of LRU
- Rare, but useful for specific replacement needs

## Applications in System Design

### Database Caching
- LRU for query result caching
- LFU for frequently accessed table rows

### Web Caching
- CDN edge servers use LRU-like policies
- Browser caches use combinations

### Operating Systems
- Page replacement algorithms (LRU approximation)
- File system caches

## Practice Implementation Problems

### Easy
- [146. LRU Cache](https://leetcode.com/problems/lru-cache/)

### Medium
- [460. LFU Cache](https://leetcode.com/problems/lfu-cache/)
- [432. All O'one Data Structure](https://leetcode.com/problems/all-oone-data-structure/)

### Hard
- [379. Design Phone Directory](https://leetcode.com/problems/design-phone-directory/)

## Performance Considerations

1. **Thread Safety**: Add synchronization for concurrent access
2. **Memory Efficiency**: Consider object overhead in linked lists
3. **Eviction Callbacks**: Allow cleanup when items are evicted
4. **Size Calculation**: Custom size calculations for complex objects

## Key Takeaways

1. **LRU is most common**: Simple, effective, widely used
2. **LFU for frequency patterns**: Better when access frequency matters more than recency
3. **Choose based on patterns**: Analyze your system's access patterns
4. **Implementation details matter**: Node management, frequency tracking precision
5. **Space-time trade-offs**: More sophisticated policies have higher overhead
