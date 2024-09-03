package dsajava.utilities.iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkedListTest {

    private LinkedList<Integer> linkedList;

    @BeforeEach
    public void setUp() {
        linkedList = new LinkedList<>();
    }

    @Test
    public void testAddAndIterator() {
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);

        Iterator<Integer> iterator = linkedList.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testEmptyList() {
        Iterator<Integer> iterator = linkedList.iterator();

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleElement() {
        linkedList.add(10);
        Iterator<Integer> iterator = linkedList.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(10), iterator.next());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testMultipleElements() {
        linkedList.add(5);
        linkedList.add(15);
        linkedList.add(25);
        linkedList.add(35);

        Iterator<Integer> iterator = linkedList.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(15), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(25), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(35), iterator.next());

        assertFalse(iterator.hasNext());
    }

}
