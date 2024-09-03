package dsajava.utilities.iterator;

import java.util.Iterator;

/**
 * A simple implementation of a singly linked list.
 * 
 * @param <T> the type of elements in this list
 */
public class LinkedList<T> implements Iterable<T> {
    // The head node of the linked list
    private Node<T> head;

    /**
     * Represents a node in the linked list.
     * 
     * @param <T> the type of element stored in this node
     */
    private static class Node<T> {
        T data; // The data stored in the node
        Node<T> next; // Reference to the next node in the list

        /**
         * Constructs a node with the given data.
         * 
         * @param data the data to be stored in the node
         */
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Adds a new element to the end of the linked list.
     * 
     * @param data the data to be added to the list
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data); // Create a new node with the given data
        if (head == null) { // If the list is empty, set the new node as the head
            head = newNode;
        } else {
            Node<T> temp = head; // Start from the head of the list
            while (temp.next != null) { // Traverse to the end of the list
                temp = temp.next;
            }
            temp.next = newNode; // Add the new node at the end of the list
        }
    }

    /**
     * Returns an iterator over the elements in this list.
     * 
     * @return an iterator for this linked list
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator(); // Return a new iterator for the linked list
    }

    /**
     * An iterator for the linked list.
     */
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head; // The current node in the iteration

        /**
         * Checks if there are more elements in the list.
         * 
         * @return true if there are more elements, false otherwise
         */
        @Override
        public boolean hasNext() {
            return current != null; // There are more elements if the current node is not null
        }

        /**
         * Returns the next element in the iteration.
         * 
         * @return the next element
         */
        @Override
        public T next() {
            if (current == null) // If no more elements are available, return null
                return null;
            Node<T> temp = current; // Store the current node
            current = current.next; // Move to the next node
            return temp.data; // Return the data of the current node
        }
    }
}
