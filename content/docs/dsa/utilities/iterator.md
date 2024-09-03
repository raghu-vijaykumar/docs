---
weight: 1
bookFlatSection: true
title: "Iterator"
draft: false
---

## Introduction to Iterators

An iterator is an object that enables sequential access to elements within a data structure, without exposing the underlying structure's details. Iterators provide a standardized way to traverse through elements one at a time, regardless of the container's internal organization (whether it be a list, tree, graph, or any other collection).

The key idea behind iterators is to abstract the process of element traversal, making it easier to work with various data structures without needing to know their internal workings.

## Core Characteristics of Iterators

- **Sequential Access**: An iterator provides a way to access elements one after another. It "remembers" its position in the traversal so that it can retrieve the next element each time it's called.
- **Stateful**: Iterators maintain state, which is typically the current position in the data structure. Each time an element is accessed, the iterator moves to the next position.
- **Interface**: In most programming languages, iterators expose a standardized interface, such as `hasNext()` and `next()` in Java or `__iter__()` and `__next__()` in Python.
- **Generality**: The same iterator concept can be applied to different data structures (e.g., arrays, linked lists, trees), making it a powerful abstraction in handling various collections of data.

## Advantages of Using Iterators

- **Abstraction**: Iterators decouple the iteration logic from the underlying structure. This allows developers to focus on the "what" (elements to be accessed) rather than the "how" (how those elements are organized).
- **Uniformity**: Iterators provide a uniform way of accessing different types of collections. Whether you are traversing a list, a set, or a custom data structure, the interface for iteration remains consistent.
- **Efficiency**: Iterators can be optimized for the specific data structure, allowing efficient access to elements without needing to rewrite traversal logic for each structure.

## Iterators in Practice

When working with an iterator, you generally follow a specific pattern:

- **Initialization**: Create or obtain an iterator for the collection.
- **Traversal**: Repeatedly fetch elements from the collection using the iterator until all elements have been accessed.
- **Termination**: The iteration process ends when the iterator signals that there are no more elements to traverse.

## Iterators in Java

In Java, an iterator is an object that allows traversal through a collection, typically one element at a time. Iterators provide a way to access elements of a collection sequentially without exposing the underlying structure. Java iterators are provided by implementing the Iterator<E> interface, which includes three methods:

- `boolean hasNext()`: Returns true if the iteration has more elements.
- `E next()`: Returns the next element in the iteration.
- `void remove()`: Removes from the underlying collection the last element returned by the iterator (optional operation).

### Implementing an Iterator for a Linked List

Below is an example of how to implement an iterator for a singly linked list in Java.

```java
import java.util.Iterator;

/**
 * A simple implementation of a singly linked list that supports iteration.
 * @param <T> the type of elements in this linked list
 */
public class LinkedList<T> implements Iterable<T> {
    // The head node of the linked list
    private Node<T> head;

    /**
     * Represents a node in the linked list.
     * @param <T> the type of element stored in this node
     */
    private static class Node<T> {
        T data;           // The data stored in the node
        Node<T> next;     // Reference to the next node in the list

        /**
         * Constructs a node with the given data.
         * @param data the data to be stored in the node
         */
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Adds a new element to the end of the linked list.
     * @param data the data to be added to the list
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);  // Create a new node with the given data
        if (head == null) {  // If the list is empty, set the new node as the head
            head = newNode;
        } else {
            Node<T> temp = head;  // Start from the head of the list
            while (temp.next != null) {  // Traverse to the end of the list
                temp = temp.next;
            }
            temp.next = newNode;  // Add the new node at the end of the list
        }
    }

    /**
     * Returns an iterator over the elements in this linked list.
     * @return an iterator for this linked list
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();  // Return a new iterator for the linked list
    }

    /**
     * An iterator for the linked list.
     */
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head;  // The current node in the iteration

        /**
         * Checks if there are more elements in the list.
         * @return true if there are more elements, false otherwise
         */
        @Override
        public boolean hasNext() {
            return current != null;  // There are more elements if the current node is not null
        }

        /**
         * Returns the next element in the iteration.
         * @return the next element
         * @throws IllegalStateException if there are no more elements
         */
        @Override
        public T next() {
            if (!hasNext()) {  // If no more elements are available, throw an exception
                throw new IllegalStateException("No more elements");
            }
            T data = current.data;  // Store the data of the current node
            current = current.next;  // Move to the next node
            return data;  // Return the data of the current node
        }

        /**
         * This operation is not supported in this implementation.
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }

    /**
     * Main method to test the LinkedList class.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();  // Create a new linked list of integers
        list.add(1);  // Add elements to the list
        list.add(2);
        list.add(3);

        // Iterate over the list and print each element
        for (int value : list) {
            System.out.println(value);
        }
    }
}
```

**Key Points**:

- **`Iterable` Interface**: The `LinkedList<T>` class implements the `Iterable<T>` interface, which requires the `iterator()` method to be overridden. This method returns an instance of the LinkedListIterator.
- **`LinkedListIterator` Class**: This inner class implements the `Iterator<T>` interface, which requires implementing the `hasNext()`, `next()`, and optionally the `remove()` method. The `hasNext()` method checks if the next element exists, and the `next()` method advances the iterator to the next element.
- **Iteration**: You can use the enhanced for-each loop with the LinkedList because it implements the Iterable interface.

**Usage Example**:

```java
LinkedList<Integer> list = new LinkedList<>();
list.add(10);
list.add(20);
list.add(30);

for (int value : list) {
    System.out.println(value);
}
```

## Iterators in Python

In Python, an iterator is an object that contains a countable number of values and can be iterated upon. An iterator in Python must implement two special methods: `__iter__()` and `__next__()`.

- `__iter__()`: Returns the iterator object itself.
- `__next__()`: Returns the next item in the sequence. When no more items are left, it raises a `StopIteration` exception.

### Implementing an Iterator for a Linked List

Below is an example of how to implement an iterator for a singly linked list in Python.

```python
class Node:
    def __init__(self, data):
        """
        Initializes a node with the given data.
        
        :param data: The data to be stored in the node.
        """
        self.data = data  # Data stored in the node
        self.next = None  # Reference to the next node in the list


class LinkedList:
    def __init__(self):
        """
        Initializes an empty linked list.
        """
        self.head = None  # The head of the linked list

    def add(self, data):
        """
        Adds a new element to the end of the linked list.
        
        :param data: The data to be added to the list.
        """
        new_node = Node(data)  # Create a new node with the given data
        if not self.head:  # If the list is empty, set the new node as the head
            self.head = new_node
        else:
            current = self.head  # Start from the head of the list
            while current.next:  # Traverse to the end of the list
                current = current.next
            current.next = new_node  # Add the new node at the end of the list

    def __iter__(self):
        """
        Returns an iterator for the linked list.
        
        :return: An instance of LinkedListIterator.
        """
        return LinkedListIterator(self.head)  # Return an iterator starting at the head


class LinkedListIterator:
    def __init__(self, head):
        """
        Initializes the iterator with the starting head of the list.
        
        :param head: The head node of the linked list.
        """
        self.current = head  # The current node in the iteration

    def __iter__(self):
        """
        Returns the iterator instance itself.
        
        :return: The iterator instance.
        """
        return self

    def __next__(self):
        """
        Returns the next element in the iteration.
        
        :return: The data of the next node.
        :raises StopIteration: If there are no more elements.
        """
        if self.current is None:  # If there are no more elements, raise StopIteration
            raise StopIteration
        data = self.current.data  # Get the data from the current node
        self.current = self.current.next  # Move to the next node
        return data  # Return the data of the current node


if __name__ == "__main__":
    # Test the LinkedList class and its iterator
    ll = LinkedList()  # Create a new linked list
    ll.add(1)  # Add elements to the list
     ll.add(2)
    ll.add(3)

    # Iterate over the linked list and print each element
    for value in ll:
        print(value)
```

**Key Points**:

- **`Iterable` Object**: The LinkedList class has an **iter**() method that returns an instance of LinkedListIterator. This allows the linked list to be used in a for loop or any other Python iterator context.

- **Iterator Object**: The LinkedListIterator class implements the iterator protocol by defining **iter**() (which just returns the iterator itself) and **next**() (which returns the next node's data and advances the pointer).
- **StopIteration**: When there are no more nodes, the **next**() method raises a StopIteration exception to signal the end of the iteration.

**Usage Example**:

```python
ll = LinkedList()
ll.add(10)
ll.add(20)
ll.add(30)

for value in ll:
    print(value)
```

**Output**:

```sh
10
20
30
```

## Comparison of Java and Python Iterators:

- **Interface and Protocol**: In Java, iterators are implemented by following the `Iterator<E>` interface, while in Python, iterators follow the protocol by implementing the `__iter__()` and `__next__()` methods.
- **StopIteration**: Python uses exceptions (`StopIteration`) to signal the end of iteration, whereas Java uses the `hasNext()` method to check for more elements.
- **Usage**: Both Java and Python iterators allow the usage of a for-each loop to iterate over elements, making them functionally similar despite the differences in their implementation.
