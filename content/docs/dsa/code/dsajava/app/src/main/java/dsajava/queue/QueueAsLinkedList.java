package dsajava.queue;

public class QueueAsLinkedList {

    Node start;
    Node end;

    public class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public QueueAsLinkedList() {
    }

    public void enqueue(int value) {
        Node node = new Node(value);
        if (start == null) {
            start = node;
            end = node;
        } else {
            end.next = node;
            end = node;
        }
    }

    public Node dequeue() {
        if (start == null)
            return null;
        Node temp = start;
        start = temp.next;
        return temp;
    }

    public Node peek() {
        return start;
    }

}
