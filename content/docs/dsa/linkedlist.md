---
weight: 1
bookFlatSection: true
title: "Linked List"
draft: false
---

## LinkedList

A LinkedList is a linear data structure where elements, known as nodes, are linked using pointers. Each node contains two fields:

- value: Holds the data of the node.
- next: A reference to the next node in the sequence.
  Unlike arrays, linked lists do not store elements contiguously in memory. Instead, they use pointers to establish a chain of nodes, providing dynamic memory allocation. In Java, a LinkedList can be implemented using a custom `Node` class.

**Constructor Example**:

```java
public class LinkedList {
    Node head;
    Node tail;
    int length;

    class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public LinkedList(int value) {
        Node newNode = new Node(value);
        head = newNode;
        tail = newNode;
        length = 1;
    }
}
```

In this constructor, the linked list is initialized with a node, and both the head and tail are set to this node. The `length` tracks the number of nodes in the list.

## Complexity Table

| Operation    | Space Complexity | Time Complexity |
| ------------ | ---------------- | --------------- |
| Clear        | O(1)             | O(1)            |
| Append       | O(1)             | O(1)            |
| Prepend      | O(1)             | O(1)            |
| Remove Last  | O(1)             | O(n)            |
| Remove First | O(1)             | O(1)            |
| Get          | O(1)             | O(n)            |
| Set          | O(1)             | O(n)            |
| Reverse      | O(1)             | O(n)            |
| To String    | O(n)             | O(n)            |


## LinkedList Implementation in Java
```java
package dsajava.linkedlist;

public class LinkedList {

    // The head node of the linked list
    Node head;
    // The tail node of the linked list
    Node tail;
    // The number of nodes in the linked list
    int length = 0;

    // Inner class representing a node in the linked list
    class Node {
        int value;  // Value stored in the node
        Node next;  // Reference to the next node

        // Node constructor to initialize a node with a value
        public Node(int value) {
            this.value = value;
        }
    }

    // Constructor to create a linked list with one initial node
    public LinkedList(int value) {
        Node newNode = new Node(value);  // Create a new node with the given value
        head = newNode;  // Set both head and tail to the new node
        tail = newNode;
        length = 1;  // Set the length of the list to 1
    }

    // Clear the linked list by removing all nodes
    public void clear() {
        head = null;  // Set head to null
        tail = null;  // Set tail to null
        length = 0;   // Reset length to 0
    }

    // Add an element to the end of the linked list
    public void append(int value) {
        Node node = new Node(value);  // Create a new node with the given value
        if (length == 0) {  // If the list is empty
            head = node;  // Set head to the new node
            tail = head;  // Set tail to the new node
        } else {
            tail.next = node;  // Set the current tail's next to the new node
            tail = node;  // Update tail to the new node
        }
        length++;  // Increment the length of the list
    }

    // Add an element to the beginning of the linked list
    public void prepend(int value) {
        Node newNode = new Node(value);  // Create a new node with the given value
        if (length == 0) {  // If the list is empty
            head = newNode;  // Set head to the new node
            tail = head;  // Set tail to the new node
        } else {
            newNode.next = head;  // Set the new node's next to the current head
            head = newNode;  // Update head to the new node
        }
        length++;  // Increment the length of the list
    }

    // Remove the last element from the linked list
    public Node removeLast() {
        if (length == 0)  // If the list is empty
            return null;
        if (length == 1) {  // If the list has only one element
            Node last = head;  // Save the last node
            clear();  // Clear the list
            return last;  // Return the last node
        }
        Node temp = head;  // Start from the head
        // Traverse to the second-to-last node
        while (temp.next.next != null) {
            temp = temp.next;
        }
        Node last = temp.next;  // The last node
        tail = temp;  // Update tail to the second-to-last node
        tail.next = null;  // Set the new tail's next to null
        length--;  // Decrement the length of the list
        return last;  // Return the removed last node
    }

    // Remove the first element from the linked list
    public Node removeFirst() {
        if (length == 0)  // If the list is empty
            return null;

        Node temp = head;  // Save the first node
        head = head.next;  // Update head to the next node
        length--;  // Decrement the length of the list

        if (length == 0)  // If the list is now empty
            tail = null;  // Set tail to null

        return temp;  // Return the removed first node
    }

    // Get the element at a specific index
    public Node get(int index) {
        if (index > length || index <= 0) {  // Check for out-of-bounds index
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds the size of linkedList is " + String.valueOf(length));
        } else {
            Node temp = head;  // Start from the head
            // Traverse to the specified index
            for (int i = 2; i <= index; i++) {
                temp = temp.next;
            }
            return temp;  // Return the node at the specified index
        }
    }

    // Set the element at a specific index
    public void set(int index, int value) {
        if (index > length) {  // Check for out-of-bounds index
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds. Cannot Set Value.");
        } else {
            Node newNode = new Node(value);  // Create a new node with the given value
            if (index == 1 && length == 0) {  // If setting the first element in an empty list
                head = newNode;  // Set head to the new node
                tail = newNode;  // Set tail to the new node
            } else {
                Node temp = head;  // Start from the head
                // Traverse to the node just before the specified index
                for (int i = 2; i < index; i++) {
                    temp = temp.next;
                }
                newNode.next = temp.next.next;  // Update the new node's next reference
                temp.next = newNode;  // Update the previous node's next reference to the new node
            }
        }
    }

    // Reverse the linked list
    public void reverse() {
        if (length <= 1)  // If the list is empty or has only one element
            return;
        Node prev = null;  // Previous node
        Node current = head;  // Current node
        tail = head;  // Update tail to the current head
        Node next;  // Next node
        // Traverse the list and reverse the links
        while (current != null) {
            next = current.next;  // Save the next node
            current.next = prev;  // Reverse the link
            prev = current;  // Move prev to current
            current = next;  // Move current to next
        }
        head = prev;  // Update head to the last processed node
    }

    // Print the elements of the linked list
    public String toString() {
        if (length == 0)  // If the list is empty
            return "";
        Node temp = head;  // Start from the head
        StringBuilder buffer = new StringBuilder();  // StringBuilder to construct the string
        buffer.append("[").append(temp.value).append(", ");  // Append the first node's value
        // Traverse the list and append each node's value
        while (temp.next != null) {
            buffer.append(temp.next.value);
            if (temp.next.next != null)
                buffer.append(", ");
            temp = temp.next;
        }
        buffer.append("]").append("(").append(length).append(")");  // Append the length of the list
        return buffer.toString();  // Return the constructed string
    }
}
```

## When Do We Use LinkedLists?

- **Dynamic memory allocation**: LinkedLists allow dynamic resizing, ideal for situations where the number of elements is unknown.
- **Insertion/Deletion**: They are efficient for frequent insertions/deletions (especially at the beginning or end) where arrays would require shifting elements.
- **Implementation**: They are used in various abstract data types like stacks, queues, and graphs.

## Advantages

- Dynamic size, unlike arrays with fixed capacity.
- Efficient insertions and deletions, especially at the start or end of the list.
- No memory waste, as nodes are allocated as needed.

## Disadvantages

- **Memory overhead**: Each node requires extra memory for the pointer/reference.
- **Slow access time**: LinkedLists have O(n) access time for elements due to sequential traversal.
- **Cache inefficiency**: As elements are not stored contiguously in memory, they may result in more cache misses compared to arrays.
