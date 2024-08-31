package dsajava.linkedlist;

public class LinkedList {

    Node head;
    Node tail;
    int length = 0;

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

    // Clear the linked list
    public void clear() {
        head = null;
        tail = null;
        length = 0;
    }

    // Add an element to linked list
    public void append(int value) {
        Node node = new Node(value);
        if (length == 0) {
            head = node;
            tail = head;
        } else {
            tail.next = node;
            tail = node;
        }
        length++;
    }

    // Remove last element from linked list
    public Node removeLast() {
        if (length == 0)
            return null;
        if (length == 1) {
            Node last = head;
            clear();
            return last;
        }
        Node tmp = head;
        while (tmp.next.next != null) {
            tmp = tmp.next;
        }
        Node last = tmp.next;
        tail = tmp;
        tail.next = null;
        length--;
        return last;
    }

    // Remove first element from the linked list
    public Node removeFirst() {
        if (length == 0)
            return null;

        Node temp = head;
        head = head.next;
        length--;

        if (length == 0)
            tail = null;

        return temp;
    }

    // Get an element at index
    public Node get(int index) {
        if (index > length || index <= 0) {
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds the size of linkedList is " + String.valueOf(length));
        } else {
            Node temp = head;
            for (int i = 2; i <= index; i++) {
                temp = temp.next;
            }
            return temp;
        }
    }

    // Set an element at index
    public void set(int index, int value) {
        if (index > length) {
            throw new IndexOutOfBoundsException(
                    String.valueOf(index) + " is out of bounds. Cannot Set Value.");
        } else {
            Node newNode = new Node(value);
            if (index == 1 && length == 0) {

                head = newNode;
                tail = newNode;
            } else {
                Node temp = head;
                for (int i = 2; i < index; i++) {
                    temp = temp.next;
                }
                newNode.next = temp.next.next;
                temp.next = newNode;
            }
        }
    }

    // Print the elements of linked list
    public String toString() {
        if (length == 0)
            return "";
        Node temp = head;
        StringBuffer buffer = new StringBuffer();
        buffer.append("[" + temp.value + ", ");
        while (temp.next != null) {
            buffer.append(temp.value);
            if (temp.next != null)
                buffer.append(", ");
            temp = temp.next;
        }
        buffer.append("]" + "(" + length + ")");
        return buffer.toString();
    }
}