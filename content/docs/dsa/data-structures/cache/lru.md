---
weight: 1
bookFlatSection: true
title: "LRU Cache"
draft: false
---

# LRU Cache

{{< markmap >}}

```markmap
# LRU Cache Implementation
- **Data Structures**
  - HashMap for O(1) key access
  - Doubly Linked List for order maintenance
- **Operations**
  - get(key): O(1) - move to front
  - put(key, value): O(1) - add/update and evict if needed
  - remove(key): O(1)
- **Edge Cases**
  - Capacity = 0
  - Duplicate keys
  - Cache full on insert
```

{{< /markmap >}}

## Detailed LRU Cache Implementation

LRU (Least Recently Used) Cache is a popular caching strategy that evicts the least recently accessed items when the cache is full.

### Design Requirements

1. **Fixed Capacity**: Maximum number of items the cache can hold
2. **O(1) Operations**: All operations (get, put) must be constant time
3. **Order Maintenance**: Track access order to identify LRU item

### Node Structure

```java
static class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
```

### Doubly Linked List Management

```java
static class DoublyLinkedList<K, V> {
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public DoublyLinkedList() {
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    // Add node after head (most recently used position)
    public void addToFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    // Remove node from list
    public void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Move existing node to front
    public void moveToFront(Node<K, V> node) {
        remove(node);
        addToFront(node);
    }

    // Remove and return least recently used node
    public Node<K, V> removeLast() {
        if (head.next == tail) return null;
        Node<K, V> lru = tail.prev;
        remove(lru);
        return lru;
    }

    public boolean isEmpty() {
        return head.next == tail;
    }
}
```

### Complete LRU Cache Implementation

```java
import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> list;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.list = new DoublyLinkedList<>();
    }

    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null; // Cache miss
        }

        // Move to front (most recently used)
        list.moveToFront(node);
        return node.value;
    }

    public void put(K key, V value) {
        if (capacity == 0) return;

        if (cache.containsKey(key)) {
            // Update existing value
            Node<K, V> node = cache.get(key);
            node.value = value;
            list.moveToFront(node);
        } else {
            // Add new entry
            if (cache.size() >= capacity) {
                // Evict least recently used
                Node<K, V> lru = list.removeLast();
                if (lru != null) {
                    cache.remove(lru.key);
                }
            }

            // Add new node
            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            list.addToFront(newNode);
        }
    }

    public void remove(K key) {
        Node<K, V> node = cache.get(key);
        if (node != null) {
            list.remove(node);
            cache.remove(key);
        }
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public int size() {
        return cache.size();
    }

    public int capacity() {
        return capacity;
    }

    public void clear() {
        cache.clear();
        // Reinitialize list
        // In real implementation, you'd reset the list
    }

    // For debugging - get keys in access order (MRU to LRU)
    public java.util.List<K> getAccessOrder() {
        java.util.List<K> order = new java.util.ArrayList<>();
        Node<K, V> current = list.head.next;
        while (current != list.tail) {
            order.add(current.key);
            current = current.next;
        }
        return order;
    }
}
```

### Time Complexity Analysis

| Operation          | Time Complexity | Explanation                          |
| ------------------ | --------------- | ------------------------------------ |
| `get(key)`         | O(1)            | HashMap lookup + list moveToFront    |
| `put(key, value)`  | O(1)            | HashMap operations + list operations |
| `remove(key)`      | O(1)            | HashMap lookup + list remove         |
| `containsKey(key)` | O(1)            | HashMap lookup                       |
| `size()`           | O(1)            | Cache size tracking                  |
| `capacity()`       | O(1)            | Return stored capacity               |

### Space Complexity
- **O(capacity)**: HashMap + Doubly linked list nodes
- **Overhead**: Each node has prev/next pointers (extra space)

### Test Cases and Edge Cases

```java
public void testLRUCache() {
    LRUCache<Integer, String> cache = new LRUCache<>(3);

    // Basic operations
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");

    assert cache.get(1).equals("one"); // Should be at front now

    // Add fourth item - evicts LRU (2)
    cache.put(4, "four");

    assert cache.get(2) == null; // 2 was evicted
    assert cache.get(1).equals("one"); // Still present
    assert cache.get(3).equals("three"); // Still present
    assert cache.get(4).equals("four"); // New item

    // Update existing item
    cache.put(1, "updated_one");
    assert cache.get(1).equals("updated_one");

    // Capacity = 0 edge case
    LRUCache<Integer, String> zeroCache = new LRUCache<>(0);
    zeroCache.put(1, "one");
    assert zeroCache.get(1) == null;
}
```

### Variations and Extensions

#### LRU with TTL (Time To Live)

```java
class LRUCacheWithTTL<K, V> extends LRUCache<K, V> {
    private final long ttlMillis;

    public LRUCacheWithTTL(int capacity, long ttlMillis) {
        super(capacity);
        this.ttlMillis = ttlMillis;
    }

    // Override get to check expiration
    @Override
    public V get(K key) {
        V value = super.get(key);
        if (value != null) {
            // Check if expired (would need timestamp tracking)
            // Implementation depends on how timestamps are stored
        }
        return value;
    }
}
```

#### Thread-Safe LRU Cache

```java
public class ThreadSafeLRUCache<K, V> extends LRUCache<K, V> {
    private final Object lock = new Object();

    public ThreadSafeLRUCache(int capacity) {
        super(capacity);
    }

    @Override
    public V get(K key) {
        synchronized (lock) {
            return super.get(key);
        }
    }

    @Override
    public void put(K key, V value) {
        synchronized (lock) {
            super.put(key, value);
        }
    }

    // Add similar synchronization for other methods...
}
```

### Common Implementation Mistakes

1. **Wrong Order**: Forgetting to move accessed items to front
2. **HashMap Values vs Keys**: Storing nodes as values, not keys
3. **Removing From Middle**: Incorrect list manipulation in doubly linked list
4. **Capacity Check**: Off-by-one errors in capacity management
5. **Null Handling**: Not handling null values properly

### Performance Optimizations

1. **Lazy Cleanup**: Clean expired entries only when accessed
2. **Pre-allocation**: Pre-allocate hashmap and node capacity
3. **Memory Pools**: Reuse evicted nodes to reduce GC pressure
4. **Access Time Optimization**: Use timestamp instead of list movement when appropriate

### Applications

- **Database Query Caching**: Cache frequently executed SQL queries
- **Web Page Caching**: Browser cache, CDN caching
- **File System Caching**: Operating system page cache
- **API Response Caching**: REST API endpoints
- **ML Model Caching**: Cache predictions for repeated inputs

### Comparison with Other Caches

| Feature           | LRU    | LFU  | FIFO |
| ----------------- | ------ | ---- | ---- |
| Access Time Track | Yes    | No   | No   |
| Frequency Track   | No     | Yes  | No   |
| Insertion Order   | No     | No   | Yes  |
| Complexity        | Medium | High | Low  |
| Memory Usage      | Medium | High | Low  |

## LeetCode Problems

- [146. LRU Cache](https://leetcode.com/problems/lru-cache/) - Design LRU Cache
- [460. LFU Cache](https://leetcode.com/problems/lfu-cache/) - Compare with LFU

## Key Implementation Points

1. **HashMap + Doubly Linked List**: Essential for O(1) operations
2. **Move to Front**: Critical for maintaining access order
3. **Eviction Logic**: Remove from both hashmap and list
4. **Node Management**: Careful handling of prev/next pointers
5. **Thread Safety**: Add synchronization if concurrent access needed
