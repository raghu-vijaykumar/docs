package dsajava.linkedlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(n.value, 1);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dList.get(4);
        });
    }

    @Test
    void testGetValidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        DoublyLinkedList.Node node = dll.get(1); // Getting the second element (20)
        assertEquals(20, node.value, "get() should return the correct node at a valid index.");
    }

    @Test
    void testGetInvalidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dll.get(3); // Out of bounds index
        }, "get() should throw IndexOutOfBoundsException for an invalid index.");
    }

    @Test
    void testSetValidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        assertTrue(dll.set(1, 100), "set() should return true for valid index.");
        assertEquals(100, dll.get(1).value, "set() should correctly update the node value at the specified index.");
    }

    @Test
    void testSetInvalidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dll.set(5, 100); // Out of bounds index
        }, "set() should throw IndexOutOfBoundsException for an invalid index.");
    }

    @Test
    void testRemoveFirst() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        DoublyLinkedList.Node first = dll.removeFirst();
        assertEquals(10, first.value, "removeFirst() should remove and return the first element.");
        assertEquals(20, dll.head.value, "After removeFirst(), the new head should be the second element.");
    }

    @Test
    void testRemoveFirstWhenEmpty() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.removeFirst(); // Remove the only element
        assertNull(dll.removeFirst(), "removeFirst() should return null when list is empty.");
    }

    @Test
    void testInsertAtBeginning() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        assertTrue(dll.insert(0, 5), "insert() should return true when inserting at the beginning.");
        assertEquals(5, dll.head.value, "After insert(0), the new head should have the inserted value.");
        assertEquals(10, dll.get(1).value, "The original head should now be at index 1.");
    }

    @Test
    void testInsertAtMiddle() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        assertTrue(dll.insert(1, 15), "insert() should return true when inserting at the middle.");
        assertEquals(15, dll.get(1).value, "The new element should be correctly inserted at the middle.");
    }

    @Test
    void testInsertAtEnd() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        assertTrue(dll.insert(2, 30), "insert() should return true when inserting at the end.");
        assertEquals(30, dll.tail.value, "The new element should be correctly inserted at the end.");
    }

    @Test
    void testRemoveValidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        dll.append(40);
        DoublyLinkedList.Node removed = dll.remove(2); // Remove the third element (30)
        assertEquals(30, removed.value, "remove() should return the removed node.");
        assertEquals(40, dll.get(2).value, "After removal, the subsequent elements should shift correctly.");
    }

    @Test
    void testRemoveInvalidIndex() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dll.remove(5); // Out of bounds index
        }, "remove() should throw IndexOutOfBoundsException for an invalid index.");
    }

    @Test
    void testRemoveLastWhenEmpty() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.removeLast(); // Remove the only element
        assertNull(dll.removeLast(), "removeLast() should return null when the list is empty.");
    }

    @Test
    void testRemoveLastWithMultipleNodes() {
        DoublyLinkedList dll = new DoublyLinkedList(10);
        dll.append(20);
        dll.append(30);
        dll.removeLast(); // Remove last element (30)
        assertEquals(20, dll.tail.value, "After removeLast(), the new tail should be the second last element.");
        assertEquals(2, dll.length, "After removeLast(), the length should decrease.");
    }

}
