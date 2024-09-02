package dsajava.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueueAsLinkedListTest {

    private QueueAsLinkedList queue;

    @BeforeEach
    void setUp() {
        queue = new QueueAsLinkedList();
    }

    @Test
    void testEnqueueSingleElement() {
        queue.enqueue(10);
        assertEquals(10, queue.peek().value, "Peek should return the first enqueued element.");
    }

    @Test
    void testEnqueueMultipleElements() {
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        assertEquals(10, queue.peek().value, "Peek should return the front of the queue.");
    }

    @Test
    void testDequeueEmptyQueue() {
        assertNull(queue.dequeue(), "Dequeue should return null for an empty queue.");
    }

    @Test
    void testDequeueSingleElement() {
        queue.enqueue(10);
        assertEquals(10, queue.dequeue().value, "Dequeue should return the only element in the queue.");
        assertNull(queue.peek(), "Peek should return null after dequeuing the only element.");
    }

    @Test
    void testDequeueMultipleElements() {
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        assertEquals(10, queue.dequeue().value, "Dequeue should return the front element of the queue.");
        assertEquals(20, queue.peek().value, "Peek should return the next element after dequeue.");
    }

    @Test
    void testPeekEmptyQueue() {
        assertNull(queue.peek(), "Peek should return null for an empty queue.");
    }

    @Test
    void testPeekAfterEnqueue() {
        queue.enqueue(10);
        queue.enqueue(20);
        assertEquals(10, queue.peek().value, "Peek should return the front element of the queue.");
    }

    @Test
    void testMultipleDequeueOperations() {
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        queue.dequeue();
        queue.dequeue();
        assertEquals(30, queue.peek().value, "Peek should return the last remaining element after multiple dequeues.");
    }

}
