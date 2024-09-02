package dsajava.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class StackAsArrayListTest {

    // Test push operation on an empty stack
    @Test
    public void testPush() {
        StackAsArrayList stack = new StackAsArrayList();
        assertTrue(stack.push(10), "Push operation failed for value 10");
        assertEquals(1, stack.size(), "Size should be 1 after 1 push operation");
    }

    // Test multiple push operations
    @Test
    public void testMultiplePush() {
        StackAsArrayList stack = new StackAsArrayList();
        assertTrue(stack.push(10), "Push operation failed for value 10");
        assertTrue(stack.push(20), "Push operation failed for value 20");
        assertTrue(stack.push(30), "Push operation failed for value 30");
        assertEquals(3, stack.size(), "Size should be 3 after 3 push operations");
    }

    // Test pop operation
    @Test
    public void testPop() {
        StackAsArrayList stack = new StackAsArrayList();
        stack.push(10);
        stack.push(20);
        int poppedValue = stack.pop();
        assertEquals(20, poppedValue, "Pop operation did not return the last pushed value");
        assertEquals(1, stack.size(), "Size should be 1 after 1 pop operation");
    }

    // Test pop operation when stack is empty
    @Test
    public void testPopOnEmptyStack() {
        StackAsArrayList stack = new StackAsArrayList();
        assertThrows(NoSuchElementException.class, () -> stack.pop(), "Pop on empty stack should throw exception");
    }

    // Test peek operation
    @Test
    public void testPeek() {
        StackAsArrayList stack = new StackAsArrayList();
        stack.push(10);
        stack.push(20);
        int topValue = stack.peek();
        assertEquals(20, topValue, "Peek operation did not return the correct top value");
        assertEquals(2, stack.size(), "Size should remain unchanged after peek operation");
    }

    // Test peek operation when stack is empty
    @Test
    public void testPeekOnEmptyStack() {
        StackAsArrayList stack = new StackAsArrayList();
        assertThrows(NoSuchElementException.class, () -> stack.peek(), "Peek on empty stack should throw exception");
    }

    // Test size operation on empty stack
    @Test
    public void testSizeOnEmptyStack() {
        StackAsArrayList stack = new StackAsArrayList();
        assertEquals(0, stack.size(), "Size of an empty stack should be 0");
    }

    // Test size operation after multiple pushes and pops
    @Test
    public void testSizeAfterOperations() {
        StackAsArrayList stack = new StackAsArrayList();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertEquals(3, stack.size(), "Size should be 3 after 3 push operations");
        stack.pop();
        assertEquals(2, stack.size(), "Size should be 2 after 1 pop operation");
    }
}
