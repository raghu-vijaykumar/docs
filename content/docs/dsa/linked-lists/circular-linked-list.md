---
aliases: [""]
weight: 1
bookFlatSection: true
title: "Circular Linked List"
draft: false
---

# Circular Linked List

{{< markmap >}}

```markmap
# Circular Linked List
- **Structure**
  - Last node points to first node
  - Can be singly or doubly linked
  - No NULL termination
- **Types**
  - Singly Circular: One-way traversal
  - Doubly Circular: Two-way traversal
- **Operations**
  - Insertion: O(1) at beginning/end, O(n) at middle
  - Deletion: Similar complexity
  - Traversal: Start from head, continue until back to head
- **Applications**
  - Round-robin scheduling
  - Music playlist
  - Clock applications
```

{{< /markmap >}}

## Introduction

A Circular Linked List is a variation of the linked list where the last node points back to the first node (head), forming a circle. This eliminates the NULL termination and allows continuous traversal.

## Types of Circular Linked Lists

### 1. Singly Circular Linked List
Each node has a `next` pointer, and the last node points to the head.

```java
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}
```

### 2. Doubly Circular Linked List
Each node has `prev` and `next` pointers, with appropriate circular references.

```java
class DoublyNode {
    int data;
    DoublyNode prev, next;

    DoublyNode(int data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}
```

## Operations on Circular Linked Lists

### Traversal
```java
public void traverseCircular(Node head) {
    if (head == null) return;

    Node current = head;
    do {
        System.out.print(current.data + " ");
        current = current.next;
    } while (current != head);

    System.out.println();
}
```

### Insertion

#### Insertion at Beginning
```java
public Node insertAtBeginning(Node head, int data) {
    Node newNode = new Node(data);

    if (head == null) {
        newNode.next = newNode; // Point to itself
        return newNode;
    }

    // Find last node
    Node last = head;
    while (last.next != head) {
        last = last.next;
    }

    // Insert new node
    newNode.next = head;
    last.next = newNode;

    return newNode; // New head
}
```

#### Insertion at End
```java
public Node insertAtEnd(Node head, int data) {
    Node newNode = new Node(data);

    if (head == null) {
        newNode.next = newNode;
        return newNode;
    }

    // Find last node
    Node last = head;
    while (last.next != head) {
        last = last.next;
    }

    // Insert at end
    last.next = newNode;
    newNode.next = head;

    return head; // Head remains same
}
```

#### Insertion at Specific Position
```java
public Node insertAtPosition(Node head, int data, int position) {
    if (position < 1) return head;

    Node newNode = new Node(data);

    if (position == 1) {
        return insertAtBeginning(head, data);
    }

    Node current = head;
    int count = 1;

    // Find node before insertion point
    while (count < position - 1 && current.next != head) {
        current = current.next;
        count++;
    }

    // If position is beyond list length, insert at end
    if (current.next == head && position > count + 1) {
        return insertAtEnd(head, data);
    }

    newNode.next = current.next;
    current.next = newNode;

    return head;
}
```

### Deletion

#### Deletion from Beginning
```java
public Node deleteFromBeginning(Node head) {
    if (head == null || head.next == head) {
        return null; // Empty list or single node
    }

    // Find last node
    Node last = head;
    while (last.next != head) {
        last = last.next;
    }

    // Delete head
    Node newHead = head.next;
    last.next = newHead;

    return newHead;
}
```

#### Deletion from End
```java
public Node deleteFromEnd(Node head) {
    if (head == null || head.next == head) {
        return null; // Empty list or single node
    }

    // Find second last node
    Node current = head;
    while (current.next.next != head) {
        current = current.next;
    }

    // Delete last node
    current.next = head;

    return head;
}
```

#### Deletion at Specific Position
```java
public Node deleteAtPosition(Node head, int position) {
    if (head == null || position < 1) return head;

    if (position == 1) {
        return deleteFromBeginning(head);
    }

    Node current = head;
    int count = 1;

    // Find node before the one to delete
    while (count < position - 1 && current.next != head) {
        current = current.next;
        count++;
    }

    // If position doesn't exist
    if (current.next == head) {
        return head; // No deletion
    }

    // Delete node
    current.next = current.next.next;

    return head;
}
```

## Doubly Circular Linked List

### Node Structure
```java
class DoublyCircularNode {
    int data;
    DoublyCircularNode prev, next;

    DoublyCircularNode(int data) {
        this.data = data;
        this.prev = this.next = this;
    }
}
```

### Advantages over Singly Circular
- Bidirectional traversal
- Easier deletion (can delete any node with direct reference)
- More efficient operations for certain use cases

### Implementation
The implementation is more complex due to maintaining both prev and next pointers correctly during insertions and deletions.

## Time Complexity Analysis

| Operation                 | Singly Circular | Doubly Circular                |
| ------------------------- | --------------- | ------------------------------ |
| **Traversal**             | O(n)            | O(n)                           |
| **Insert at beginning**   | O(1)            | O(1) if we have tail reference |
| **Insert at end**         | O(n)            | O(1)                           |
| **Insert at position**    | O(n)            | O(n) (need traversal)          |
| **Delete from beginning** | O(1)            | O(1)                           |
| **Delete from end**       | O(n)            | O(1)                           |
| **Delete at position**    | O(n)            | O(n)                           |
| **Search**                | O(n)            | O(n)                           |

## Space Complexity
- **Singly Circular**: O(n) for data, plus O(n) for pointers
- **Doubly Circular**: O(n) for data, plus O(2n) for pointers

## Applications

### 1. Round-Robin Scheduling
Operating systems use circular linked lists for process scheduling where each process gets equal CPU time in a circular manner.

### 2. Music Playlist with Repeat
Media players implement playlists where after the last song, it goes back to the first song.

### 3. Clock Applications
Clock hands and timers often use circular linked lists conceptually.

### 4. Multiplayer Games
Player turns in games can be implemented using circular linked lists.

### 5. Buffer Management
Circular buffers for data streams.

## Advantages of Circular Linked Lists

1. **Endless Traversal**: Can traverse continuously without stopping
2. **Memory Efficient**: No NULL pointers needed
3. **Dynamic Size**: Can grow and shrink dynamically
4. **Queue Implementation**: Efficient for circular queues

## Disadvantages

1. **Complexity**: More complex to implement than linear lists
2. **Traversal Issues**: Risk of infinite loops if not careful
3. **No Quick End Detection**: Can't detect end like linear lists with NULL

## Common Operations Implementation

### Finding Length
```java
public int getLength(Node head) {
    if (head == null) return 0;

    int length = 0;
    Node current = head;

    do {
        length++;
        current = current.next;
    } while (current != head);

    return length;
}
```

### Detecting Loop (Though Circular by Design)
Circular lists always have loops, but you might want to detect if there's an issue.

### Splitting Circular List
```java
public Node[] splitCircularList(Node head) {
    if (head == null) return new Node[]{null, null};

    // Find middle and last nodes
    Node slow = head, fast = head;
    while (fast.next != head && fast.next.next != head) {
        slow = slow.next;
        fast = fast.next.next;
    }

    Node secondHalf = slow.next;
    slow.next = head; // First half ends at slow

    // Find end of second half
    Node last = secondHalf;
    while (last.next != head) {
        last = last.next;
    }
    last.next = secondHalf; // Second half circular

    return new Node[]{head, secondHalf};
}
```

## Practice Problems

### Easy
- Implement basic circular linked list operations
- Convert linear linked list to circular

### Medium
- [Josephus Problem](https://en.wikipedia.org/wiki/Josephus_problem) using circular linked list
- Implement circular queue using circular linked list

### Hard
- Implement LRU cache using circular doubly linked list
- Detect and remove loops (though circular lists have intentional loops)

## Key Takeaways

1. **Circular references**: Always be careful with traversal to avoid infinite loops
2. **Performance**: Choose singly or doubly based on usage patterns
3. **Applications**: Perfect for round-robin and cyclic operations
4. **Implementation**: Carefully maintain the circular nature during insertions/deletions
5. **Memory**: Efficient use with no NULL pointers, but additional complexity

Circular linked lists are particularly useful when you need continuous traversal or cyclic behavior in your data structure operations.
