package dsajava.stack;

public class StackAsLinkedList {
    Node top;
    int size;

    public class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public StackAsLinkedList() {

    }

    public void push(int value) {
        Node newNode = new Node(value);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public Node pop() {
        if (size == 0)
            return null;
        Node popped = top;
        top = top.next;
        size--;
        return popped;
    }

    public Node peek() {
        return top;
    }

    public int size() {
        return size;
    }

}
