---
weight: 4
bookFlatSection: true
title: "Queue"
draft: false
---

## Queue

A queue is a First-In-First-Out (FIFO) data structure that allows elements to be added to the back (`enqueue`) and removed from the front (`dequeue`). The `QueueAsLinkedList` implementation uses a singly linked list where nodes represent elements.

**Constructor:**
- `QueueAsLinkedList()`: Initializes an empty queue with `start` and `end` set to `null`.

## Implementataion 
```java
package dsajava.queue;

public class QueueAsLinkedList {

    // The start node of the queue (front)
    Node start;

    // The end node of the queue (rear)
    Node end;

    // Inner class representing a node in the queue
    public class Node {
        int value;    // Value stored in the node
        Node next;    // Reference to the next node in the queue

        // Constructor to initialize a node with a given value
        public Node(int value) {
            this.value = value;
        }
    }

    // Constructor to initialize an empty queue
    public QueueAsLinkedList() {
    }

    // Method to add an element to the end of the queue
    public void enqueue(int value) {
        Node node = new Node(value);  // Create a new node with the given value
        if (start == null) {          // If the queue is empty
            start = node;            // Set the new node as the start
            end = node;              // Set the new node as the end
        } else {                     
            end.next = node;         // Link the new node to the end of the queue
            end = node;              // Update the end to the new node
        }
    }

    // Method to remove and return the element from the front of the queue
    public Node dequeue() {
        if (start == null)             // If the queue is empty
            return null;              // Return null (or handle as needed)
        Node temp = start;            // Store the start node to be removed
        start = temp.next;            // Move the start to the next node
        return temp;                  // Return the removed node
    }

    // Method to return the element at the front of the queue without removing it
    public Node peek() {
        return start;                 // Return the start node (front of the queue)
    }

}
```

## Operations, Time, and Space Complexities

| Operation            | Time Complexity | Space Complexity | Description                                           |
| -------------------- | --------------- | ---------------- | ----------------------------------------------------- |
| `enqueue(int value)` | O(1)            | O(1)             | Adds an element to the end of the queue.              |
| `dequeue()`          | O(1)            | O(1)             | Removes and returns the element from the front.       |
| `peek()`             | O(1)            | O(1)             | Returns the element at the front without removing it. |

## When to Use
- **Queue** is used in scenarios where elements need to be processed in the order they are added. Common use cases include:
  - Task scheduling.
  - Managing requests in a system.
  - Implementing breadth-first search algorithms.
  - Buffering data.

## Advantages
- **Efficient Operations**: Both `enqueue` and `dequeue` operations are performed in constant time O(1).
- **Dynamic Size**: The queue size can grow dynamically with the number of elements, limited only by available memory.

## Disadvantages
- **Memory Overhead**: Each element requires additional memory for the node pointers (`next`), which adds overhead compared to arrays.
- **Node Traversal**: Accessing an element by position is not efficient; only the front and end elements are directly accessible.

## Queue as ArrayList 

**Not Recommended**

```java
import java.util.ArrayList;

public class QueueAsArrayList {
    private ArrayList<Integer> list = new ArrayList<>();

    // Adds an element to the end of the queue
    public void enqueue(int value) {
        list.add(value);
    }

    // Removes and returns the element from the front of the queue
    public Integer dequeue() {
        if (list.isEmpty()) {
            return null; // or throw an exception
        }
        return list.remove(0);
    }

    // Returns the element at the front of the queue without removing it
    public Integer peek() {
        if (list.isEmpty()) {
            return null; // or throw an exception
        }
        return list.get(0);
    }

    // Returns the number of elements in the queue
    public int size() {
        return list.size();
    }

    // Checks if the queue is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
```

Disadvantages
- **Inefficiency in Dequeue Operation**: Removing an element from the front of an ArrayList requires shifting all subsequent elements, which makes the dequeue operation O(N) in time complexity.
- **Potentially High Memory Usage**: ArrayList can use more memory than necessary if elements are frequently added and removed, due to resizing and shifting operations.