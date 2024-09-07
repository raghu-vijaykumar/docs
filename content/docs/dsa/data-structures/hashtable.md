---
weight: 5
bookFlatSection: true
title: "Hash Table"
draft: false
---

# Hash Table

A hash table is a data structure that provides efficient insertion, deletion, and retrieval operations by mapping keys to values through a hash function. It is designed to optimize access to data by using an array of buckets or slots, where each bucket holds data that hashes to the same index.

## Key Concepts

- **Hash Function:** The hash function is a crucial component of a hash table. It takes an input (a key) and returns an index in the hash table where the corresponding value is stored. A good hash function distributes keys uniformly across the array to minimize collisions.
- **Buckets:** Buckets are the slots in the hash table where data is stored. Each bucket can store one or more key-value pairs, depending on the collision resolution strategy employed.
- **Load Factor:** The load factor of a hash table is the ratio of the number of elements to the number of buckets. It helps in determining when to resize the hash table to maintain efficient operations.
- **Collision:** A collision occurs when two or more keys hash to the same index. Hash tables use various strategies to handle collisions and ensure that each key-value pair can be retrieved efficiently.

## Operations

- **Insertion:** Adding a new key-value pair to the hash table involves computing the hash of the key to find the appropriate bucket and then placing the key-value pair in the bucket. If the bucket already contains other pairs (due to collisions), the collision resolution strategy determines how the new pair is added.
- **Deletion:** Removing a key-value pair involves locating the bucket using the hash of the key and then removing the pair from the bucket. The collision resolution strategy also affects how the pair is removed.
- **Search:** To retrieve a value associated with a key, the hash table computes the hash of the key to find the corresponding bucket and then searches within the bucket for the key. If the key is found, the associated value is returned.

## Hash Collisions

A hash collision occurs when two or more keys hash to the same index in a hash table. Since a hash table relies on hash functions to map keys to indices, a collision indicates that multiple keys have the same hash value, resulting in multiple entries being placed in the same bucket or slot of the hash table.

### Why Collisions Occur

- **Limited Number of Slots:** The hash table has a finite number of slots, while the number of possible keys is much larger.
- **Hash Function Limitations:** Even with a well-designed hash function, collisions can occur due to the pigeonhole principle, which states that if more items are placed into fewer containers, at least one container must contain more than one item.

### Handling Hash Collisions

Several strategies exist to handle hash collisions, each with its own advantages and disadvantages. Here are the most common methods:

#### Separate Chaining

Separate chaining involves storing collided elements in a linked list (or another data structure) at each index of the hash table. Each slot in the hash table contains a reference to a linked list of entries that hash to the same index.

**Implementation:**

- **Insert:** Add the new element to the head (or end) of the linked list at the index.
- **Search:** Traverse the linked list at the index to find the required element.
- **Delete:** Traverse the linked list to remove the element if found.

**Advantages:**

- Can handle a large number of collisions since linked lists can grow dynamically.

**Disadvantages:**

- Requires additional memory for pointers in the linked list.
- Performance may degrade if many collisions occur and the lists become long.

#### Open Addressing

Open addressing resolves collisions by probing for the next available slot within the hash table. When a collision occurs, the algorithm searches for the next free slot using a probing sequence.

**Common Probing Techniques:**

- **Linear Probing:** Increment the index by a fixed amount (usually 1) and try the next slot. For example, if a collision occurs at index `i`, try `i+1`, `i+2`, etc.
- **Quadratic Probing:** Use a quadratic function to determine the next slot. For example, try `i+1^2`, `i+2^2`, etc.
- **Double Hashing:** Use a second hash function to determine the step size for probing. For example, if the first hash function is `h1(key)`, then use `h2(key)` to determine the step size.

**Advantages:**

- More memory-efficient than separate chaining since no extra data structures are needed.
- Can be more cache-friendly due to contiguous memory access.

**Disadvantages:**

- Performance degrades as the hash table becomes full.
- Requires a good probing strategy to minimize clustering (concentration of collisions).

#### Rehashing

Rehashing involves creating a new, larger hash table and re-inserting all existing elements into the new table. This is typically done when the load factor (number of elements / table size) exceeds a certain threshold.

**Implementation:**

- Resize: Create a new hash table with a larger size (usually a prime number).
- Rehash: Recalculate the hash for each existing element and insert it into the new table.

**Advantages:**

- Reduces the load factor, which can significantly improve performance.
- Reduces the likelihood of collisions.

**Disadvantages:**

- Rehashing can be time-consuming and computationally expensive since it requires re-inserting all elements.
- Temporary performance degradation during the rehashing process.

## Code and In-Line Comments

This code uses separate chaining to handle collisions.

```java
package dsajava.datastructures.hashtable;

public class HashTable {

    // Define the size of the hash table
    int length = 7;
    Node[] dataMap; // Array of Node references to store the hash table entries

    // Inner class to represent each node in the hash table
    public class Node {
        String key; // Key for the node
        int value; // Value associated with the key
        Node next; // Reference to the next node (for handling collisions)

        public Node(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    // Constructor to initialize the hash table
    public HashTable() {
        dataMap = new Node[length]; // Initialize the dataMap array with the specified length
    }

    // Hash function to compute the index for a given key
    private int hash(String key) {
        int hash = 0;
        char[] keyChars = key.toCharArray(); // Convert the key to a character array
        for (int i = 0; i < keyChars.length; i++) {
            // Compute hash by adding ASCII value of each character multiplied by 37
            hash += (keyChars[i] * 37) % dataMap.length;
        }
        return hash; // Return the computed hash value
    }

    // Method to add a key-value pair to the hash table
    public void put(String key, int value) {
        int hash = hash(key); // Compute the hash for the given key
        Node newNode = new Node(key, value); // Create a new node with the provided key and value
        if (dataMap[hash] == null) { // Check if there's no collision at the computed index
            dataMap[hash] = newNode; // Add the new node directly to the dataMap
        } else {
            // Handle collisions using chaining
            Node temp = dataMap[hash];
            // Traverse to the end of the linked list at this index
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode; // Append the new node to the end of the linked list
        }
    }

    // Method to retrieve a value by key from the hash table
    public int get(String key) {
        int hash = hash(key); // Compute the hash for the given key
        Node temp = dataMap[hash]; // Start at the head of the linked list at the computed index
        // Traverse the linked list to find the node with the matching key
        while (temp != null) {
            if (key.equals(temp.key)) { // Compare keys (use .equals() for string comparison)
                return temp.value; // Return the value if the key matches
            }
            temp = temp.next; // Move to the next node in the list
        }
        return 0; // Return 0 if the key is not found in the hash table
    }

}
```

## Space and Time Complexity

| Operation | Time Complexity (Average Case) | Time Complexity (Worst Case) | Space Complexity |
| --------- | ------------------------------ | ---------------------------- | ---------------- |
| put       | `O(1)`                         | `O(n)`                       | `O(n)`           |
| get       | `O(1)`                         | `O(n)`                       | `O(n)`           |
| hash      | `O(k)`                         | `O(k)`                       | `O(1)`           |
| Node      | `O(1)`                         | `O(1)`                       | `O(n)`           |

**Where:**

- n is the number of elements in the hash table.
- k is the length of the key (number of characters).

## When to Use

- Hash Tables are used when you need efficient data retrieval, insertion, and deletion operations.
- They are particularly useful for scenarios where fast lookups are required, such as implementing a cache, dictionary, or database indexing.


## Advantages

- **Efficient Lookup:** Provides average-case `O(1)` time complexity for search operations.
- **Dynamic Resizing:** Can handle large datasets by resizing, though this implementation does not include resizing logic.
- **Collision Handling:** Handles collisions using separate chaining, which allows for flexible and dynamic growth.

## Disadvantages

- **Space Complexity**: Requires additional space for handling collisions (linked lists) and may lead to higher memory usage.
- **Worst-Case Performance**: Can degrade to `O(n)` in the worst-case scenario when many collisions occur and all keys hash to the same index.
- **Hash Function Dependency**: The performance heavily depends on the quality of the hash function. Poor hash functions can lead to frequent collisions and decreased performance.