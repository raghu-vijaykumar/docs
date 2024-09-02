package dsajava.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class StackAsResizingArrayTest {

    // Test pushing an element to the stack
    @Test
    public void testPush() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        assertEquals(1, stack.size(), "Size should be 1 after 1 push operation");
        assertEquals(10, stack.peek(), "The top element should be 10 after push");
    }

    // Test pushing multiple elements and check resizing
    @Test
    public void testMultiplePush() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        stack.push(20);
        stack.push(30); // This should trigger resizing
        assertEquals(3, stack.size(), "Size should be 3 after 3 push operations");
        assertEquals(30, stack.peek(), "The top element should be 30 after multiple pushes");
    }

    // Test popping an element from the stack
    @Test
    public void testPop() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        stack.push(20);
        int poppedValue = stack.pop();
        assertEquals(20, poppedValue, "The popped value should be the last pushed value (20)");
        assertEquals(1, stack.size(), "Size should be 1 after 1 pop operation");
    }

    // Test popping from an empty stack
    @Test
    public void testPopOnEmptyStack() {
        StackAsResizingArray stack = new StackAsResizingArray();
        assertThrows(NoSuchElementException.class, () -> stack.pop(), "Pop on empty stack should throw exception");
    }

    // Test peeking the top element of the stack
    @Test
    public void testPeek() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        stack.push(20);
        int topValue = stack.peek();
        assertEquals(20, topValue, "The top element should be 20 after 2 pushes");
        assertEquals(2, stack.size(), "Size should remain unchanged after peek operation");
    }

    // Test peeking from an empty stack
    @Test
    public void testPeekOnEmptyStack() {
        StackAsResizingArray stack = new StackAsResizingArray();
        assertThrows(NoSuchElementException.class, () -> stack.peek(), "Peek on empty stack should throw exception");
    }

    // Test stack resizing
    @Test
    public void testResizing() {
        StackAsResizingArray stack = new StackAsResizingArray();
        // Initially, the stack has size 1. Add more than one element to trigger
        // resizing.
        stack.push(10);
        stack.push(20); // This should trigger a resize.
        stack.push(30);
        stack.push(40);
        assertEquals(4, stack.size(), "Size should be 4 after 4 pushes and a resize");
        assertEquals(40, stack.peek(), "The top element should be 40 after resizing and multiple pushes");
    }

    // Test stack size after operations
    @Test
    public void testSize() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertEquals(3, stack.size(), "Size should be 3 after 3 pushes");
        stack.pop();
        assertEquals(2, stack.size(), "Size should be 2 after 1 pop operation");
    }

    // Test popping all elements from the stack
    @Test
    public void testPopAllElements() {
        StackAsResizingArray stack = new StackAsResizingArray();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.pop();
        stack.pop();
        stack.pop();
        assertEquals(0, stack.size(), "Size should be 0 after popping all elements");
        assertThrows(NoSuchElementException.class, () -> stack.pop(),
                "Pop on empty stack after all elements are removed should throw exception");
    }

}
