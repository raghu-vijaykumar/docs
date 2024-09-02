---
weight: 1
bookFlatSection: true
title: "Doubly LinkedList"
draft: true
---

# DoublyLinkedList

A Doubly Linked List (DLL) is a type of linked list in which each node has two pointers:

- One pointing to the next node.
- One pointing to the previous node.
  This allows traversal in both directions (forward and backward). The list maintains pointers to both the head (first node) and tail (last node) of the list.

To create a doubly linked list, you define a node class with the necessary pointers and then a list class with operations such as append, prepend, insert, remove, and more. Each operation modifies the head, tail, or both, as well as the links between the nodes.

Example Constructor:

```java
public class DoublyLinkedList {
    // Pointers to the head and tail of the list and a variable to track the length
    // of the list.
    Node head;
    Node tail;
    int length;

    // Inner class representing each node in the doubly linked list.
    public class Node {
        Node prev; // Pointer to the previous node
        Node next; // Pointer to the next node
        int value; // The value stored in the node

        // Constructor to create a new node with the given value
        public Node(int value) {
            this.value = value;
        }
    }

    // Constructor to initialize a new doubly linked list with a single value
    public DoublyLinkedList(int value) {
        Node newNode = new Node(value); // Create a new node
        head = newNode; // Set the head to the new node
        tail = newNode; // Set the tail to the new node (since there's only one node)
        length = 1; // Set the length of the list to 1
    }
}
```

## Operations, Time and Space Complexity:

| **Operation**                       | **Time Complexity** | **Space Complexity** | **Description**                                                            |
| ----------------------------------- | ------------------- | -------------------- | -------------------------------------------------------------------------- |
| Append (Insert at End)              | O(1)                | O(1)                 | Adds a node at the end of the list.                                        |
| Prepend (Insert at Start)           | O(1)                | O(1)                 | Adds a node at the beginning of the list.                                  |
| Insert (At Specific Index)          | O(N)                | O(1)                 | Inserts a node at the given index.                                         |
| Remove Last (Delete from End)       | O(1)                | O(1)                 | Removes the node at the end of the list.                                   |
| Remove First (Delete from Start)    | O(1)                | O(1)                 | Removes the node at the beginning of the list.                             |
| Remove (Delete from Specific Index) | O(N)                | O(1)                 | Removes the node at the specified index.                                   |
| Get (Fetch Node by Index)           | O(N)                | O(1)                 | Retrieves the node at the specified index.                                 |
| Set (Update Node Value by Index)    | O(N)                | O(1)                 | Updates the value of the node at the given index.                          |
| Traverse (Forward/Backward)         | O(N)                | O(1)                 | Traverses the list from head to tail (forward) or tail to head (backward). |

## Java Implementataion 

```java
package dsajava.linkedlist;

public class DoublyLinkedList {
    // Pointers to the head and tail of the list and a variable to track the length
    // of the list.
    Node head;
    Node tail;
    int length;

    // Inner class representing each node in the doubly linked list.
    public class Node {
        Node prev; // Pointer to the previous node
        Node next; // Pointer to the next node
        int value; // The value stored in the node

        // Constructor to create a new node with the given value
        public Node(int value) {
            this.value = value;
        }
    }

    // Constructor to initialize a new doubly linked list with a single value
    public DoublyLinkedList(int value) {
        Node newNode = new Node(value); // Create a new node
        head = newNode; // Set the head to the new node
        tail = newNode; // Set the tail to the new node (since there's only one node)
        length = 1; // Set the length of the list to 1
    }

    // Method to append an item to the end of the doubly linked list
    public void append(int value) {
        Node newNode = new Node(value); // Create a new node with the provided value
        if (length == 0) { // If the list is empty
            head = newNode; // Set the head to the new node
            tail = newNode; // Set the tail to the new node
        } else {
            newNode.prev = tail; // Link the new node to the current tail
            tail.next = newNode; // Set the current tail's next pointer to the new node
            tail = newNode; // Update the tail to the new node
        }
        length++; // Increment the length of the list
    }

    // Method to prepend an item to the beginning of the doubly linked list
    public void prepend(int value) {
        Node newNode = new Node(value); // Create a new node with the provided value
        if (length == 0) { // If the list is empty
            head = newNode; // Set the head to the new node
            tail = newNode; // Set the tail to the new node
        } else {
            newNode.next = head; // Link the new node to the current head
            head.prev = newNode; // Set the current head's previous pointer to the new node
            head = newNode; // Update the head to the new node
        }
        length++; // Increment the length of the list
    }

    // Method to remove the last item from the doubly linked list
    public Node removeLast() {
        if (length == 0) // If the list is empty, return null
            return null;
        Node temp = tail; // Store the current tail in a temporary variable
        if (length == 1) { // If there's only one node
            head = null; // Set the head to null
            tail = null; // Set the tail to null
        } else {
            tail = tail.prev; // Move the tail back to the previous node
            tail.next = null; // Remove the link to the old tail
            temp.prev = null; // Remove the link from the old tail to the previous node
        }
        length--; // Decrease the length of the list
        return temp; // Return the removed node
    }

    // Method to remove the first item from the doubly linked list
    public Node removeFirst() {
        if (length == 0) // If the list is empty, return null
            return null;
        Node first = head; // Store the current head in a temporary variable
        if (length == 1) { // If there's only one node
            head = null; // Set the head to null
            tail = null; // Set the tail to null
        } else {
            head = head.next; // Move the head forward to the next node
            head.prev = null; // Remove the link to the old head
            first.next = null; // Remove the link from the old head to the next node
        }
        length--; // Decrease the length of the list
        return first; // Return the removed node
    }

    // Method to get a node by its index in the list (1-based indexing)
    public Node get(int index) {
        // Check if the index is out of bounds
        if (index < 0 || index > length)
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds. The size of DLL is " + String.valueOf(length));

        Node temp = head; // Start at the head
        // Traverse the list until reaching the desired index
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp; // Return the node at the specified index
    }

    // Method to set the value of a node at a specific index
    public Boolean set(int index, int value) {
        Node node = get(index); // Get the node at the specified index
        node.value = value; // Update the node's value
        return true; // Return true to indicate success
    }

    // Method to insert a node at a specific index
    public boolean insert(int index, int value) {
        // Check if the index is out of bounds
        if (index < 0 || index > length)
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds. The size of DLL is " + String.valueOf(length));

        if (index == 0) { // If the index is at the beginning
            prepend(value); // Prepend the value
            return true;
        }
        if (index == length) { // If the index is at the end
            append(value); // Append the value
            return true;
        }

        // Otherwise, find the nodes before and after the insertion point
        Node after = get(index); // Get the node currently at the insertion index
        Node before = after.prev; // Get the node before the insertion index
        Node temp = new Node(value); // Create the new node to be inserted

        // Link the new node with the nodes before and after
        temp.next = after;
        temp.prev = before;
        after.prev = temp;
        before.next = temp;

        length++; // Increment the length of the list
        return true; // Return true to indicate success
    }

    // Method to remove a node at a specific index
    public Node remove(int index) {
        // Check if the index is out of bounds
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds. The size of DLL is " + String.valueOf(length));

        if (index == 0) { // If the index is at the beginning
            return removeFirst(); // Remove the first node
        }
        if (index == length - 1) { // If the index is at the end
            return removeLast(); // Remove the last node
        }

        // Otherwise, find the node to be removed
        Node node = get(index); // Get the node at the specified index

        // Update the links of the nodes before and after the removed node
        node.prev.next = node.next;
        node.next.prev = node.prev;

        // Remove the node's links to its neighbors
        node.next = null;
        node.prev = null;

        length--; // Decrease the length of the list
        return node; // Return the removed node
    }

    @Override
    public String toString() {
        // If the list is empty (head is null), return the representation for an empty
        // list
        if (head == null) {
            return "[]";
        }

        // StringBuilder is used for efficient string concatenation
        StringBuilder sb = new StringBuilder();

        // Start from the head of the list
        Node temp = head;

        // Append the opening bracket of the list representation
        sb.append("[");

        // Traverse the list
        while (temp != null) {
            // Append the value of the current node
            sb.append(temp.value);

            // If there is a next node, append the separator "<->"
            if (temp.next != null) {
                sb.append(" <-> ");
            }

            // Move to the next node
            temp = temp.next;
        }

        // Append the closing bracket of the list representation
        sb.append("]");

        // Return the constructed string
        return sb.toString();
    }

}
```




## Use Cases

Doubly linked lists are suitable for scenarios where:

- You need to efficiently perform insertion and deletion of nodes from both ends of the list.
- You require bidirectional traversal, i.e., moving both forward and backward through the list.
- You want to maintain a dynamic list that can grow or shrink efficiently.

## Examples include

- Implementing a browser's history navigation (back/forward buttons).
- Creating a music playlist where you can move between songs.
- Task scheduling or undo operations where both backward and forward navigation is required.

## Advantages

- **Bidirectional Traversal**: Can traverse the list in both directions, which provides more flexibility in accessing elements.
- **Efficient Deletion/Inserting**: Allows insertion and deletion at both ends (head and tail) with O(1) time complexity.

## Disadvantages

- **Extra Memory**: Each node requires additional memory for the extra pointer (to the previous node), increasing space complexity.
- **Complexity in Implementation**: More complicated to implement compared to a singly linked list due to managing two pointers for each node.
