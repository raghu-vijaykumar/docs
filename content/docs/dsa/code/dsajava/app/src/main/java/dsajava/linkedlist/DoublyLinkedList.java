package dsajava.linkedlist;

public class DoublyLinkedList {
    Node head;
    Node tail;
    int length;

    public class Node {
        Node prev;
        Node next;
        int value;

        public Node(int value) {
            this.value = value;
        }
    }

    public DoublyLinkedList(int value) {
        Node newNode = new Node(value);
        head = newNode;
        tail = newNode;
        length = 1;
    }

    // Append an item into doubly linked list
    public void append(int value) {
        Node newNode = new Node(value);
        if (length == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        length++;
    }

    // Prepend an item
    public void prepend(int value) {
        Node newNode = new Node(value);
        if (length == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        length++;
    }

    // Remove last item from the DLL
    public Node removeLast() {
        if (length == 0)
            return null;
        Node temp = tail;
        if (length == 1) {
            tail = null;
            head = null;
        } else {
            temp.prev.next = null;
            tail = temp.prev;
            temp.prev = null;
        }
        length--;
        return temp;
    }

    // Remove first item from the DLL

    // Get an item from DLL 1-index
    public Node get(int index) {
        if (index < 0 || index > length)
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds the size of DLL is " + String.valueOf(length));
        Node temp = head;
        for (int i = 1; i < index; i++) {
            temp = temp.next;
        }
        return temp;
    }

    // Set an item at an index 1-index
    public Boolean set(int index, int value) {

        return false;
    }
}
