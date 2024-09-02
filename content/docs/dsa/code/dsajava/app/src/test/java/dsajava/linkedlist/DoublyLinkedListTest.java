package dsajava.linkedlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dsajava.linkedlist.DoublyLinkedList.Node;

public class DoublyLinkedListTest {

    @Test
    public void testDoublyLinkedListInitialization() {
        DoublyLinkedList dList = new DoublyLinkedList(0);
        assertEquals(dList.head.value, 0);
        assertEquals(dList.head.next, null);
        assertEquals(dList.head.prev, null);
        assertEquals(dList.head, dList.tail);
        assertEquals(dList.length, 1);
    }

    @Test
    public void testAppend() {
        DoublyLinkedList dList = new DoublyLinkedList(0);
        dList.append(1);
        assertEquals(dList.head.value, 0);
        assertEquals(dList.tail.value, 1);
        assertEquals(dList.head.prev, null);
        assertEquals(dList.head.next, dList.tail);
        assertEquals(dList.length, 2);
    }

    @Test
    public void testPrepend() {
        DoublyLinkedList dList = new DoublyLinkedList(0);
        dList.prepend(-1);
        assertEquals(dList.head.value, -1);
        assertEquals(dList.tail.value, 0);
        assertEquals(dList.head.prev, null);
        assertEquals(dList.head.next, dList.tail);
        assertEquals(dList.length, 2);
    }

    @Test
    public void testRemoveLast() {
        DoublyLinkedList dList = new DoublyLinkedList(0);
        dList.prepend(-1);
        Node last = dList.removeLast();
        assertEquals(last.value, 0);
        assertEquals(last.prev, null);
        assertEquals(last.next, null);
        assertEquals(dList.length, 1);
        last = dList.removeLast();
        assertEquals(last.value, -1);
        assertEquals(last.prev, null);
        assertEquals(last.next, null);
        assertEquals(dList.head, null);
        assertEquals(dList.tail, null);
        assertEquals(dList.length, 0);
    }

    @Test
    public void testGet() {
        DoublyLinkedList dList = new DoublyLinkedList(0);
        dList.prepend(-1);
        dList.append(1);
        Node n = dList.get(2);
        assertEquals(n.value, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dList.get(4);
        });
    }

}
