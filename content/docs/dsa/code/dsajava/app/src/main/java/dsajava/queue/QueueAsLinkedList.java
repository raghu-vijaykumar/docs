package dsajava.queue;

public class QueueAsLinkedList {

    // The start node of the queue (front)
    Node start;

    // The end node of the queue (rear)
    Node end;

    // Inner class representing a node in the queue
    public class Node {
        int value; // Value stored in the node
        Node next; // Reference to the next node in the queue

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
        Node node = new Node(value); // Create a new node with the given value
        if (start == null) { // If the queue is empty
            start = node; // Set the new node as the start
            end = node; // Set the new node as the end
        } else {
            end.next = node; // Link the new node to the end of the queue
            end = node; // Update the end to the new node
        }
    }

    // Method to remove and return the element from the front of the queue
    public Node dequeue() {
        if (start == null) // If the queue is empty
            return null; // Return null (or handle as needed)
        Node temp = start; // Store the start node to be removed
        start = temp.next; // Move the start to the next node
        return temp; // Return the removed node
    }

    // Method to return the element at the front of the queue without removing it
    public Node peek() {
        return start; // Return the start node (front of the queue)
    }

}
