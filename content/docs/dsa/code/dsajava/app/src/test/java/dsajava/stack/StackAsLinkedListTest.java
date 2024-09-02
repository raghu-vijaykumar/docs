package dsajava.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StackAsLinkedListTest {

    // Test pushing a single element to the stack
    @Test
    public void testPush() {
        StackAsLinkedList stack = new StackAsLinkedList();
        stack.push(10);
        assertEquals(1, stack.size(), "Size should be 1 after 1 push operation");
        assertEquals(10, stack.peek().value, "The top element should be 10 after push");
    }

    // Test pushing multiple elements to the stack
    @Test
    public void testMultiplePush() {
        StackAsLinkedList stack = new StackAsLinkedList();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertEquals(3, stack.size(), "Size should be 3 after 3 push operations");
        assertEquals(30, stack.peek().value, "The top element should be 30 after multiple pushes");
    }

    // Test popping an element from the stack
    @Test
    public void testPop() {
        StackAsLinkedList stack = new StackAsLinkedList();
        stack.push(10);
        stack.push(20);
        StackAsLinkedList.Node popped = stack.pop();
        assertEquals(20, popped.value, "The popped value should be the last pushed value (20)");
        assertEquals(1, stack.size(), "Size should be 1 after 1 pop operation");
    }

    // Test popping from an empty stack
    @Test
    public void testPopOnEmptyStack() {
        StackAsLinkedList stack = new StackAsLinkedList();
        assertNull(stack.pop(), "Pop on empty stack should return null");
    }

    // Test peeking the top element of the stack
    @Test
    public void testPeek() {
        StackAsLinkedList stack = new StackAsLinkedList();
        stack.push(10);
        stack.push(20);
        StackAsLinkedList.Node top = stack.peek();
        assertEquals(20, top.value, "The top element should be 20 after 2 pushes");
        assertEquals(2, stack.size(), "Size should remain unchanged after peek operation");
    }

    // Test peeking from an empty stack
    @Test
    public void testPeekOnEmptyStack() {
        StackAsLinkedList stack = new StackAsLinkedList();
        assertNull(stack.peek(), "Peek on empty stack should return null");
    }

    // Test stack size after various operations
    @Test
    public void testSize() {
        StackAsLinkedList stack = new StackAsLinkedList();
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
        StackAsLinkedList stack = new StackAsLinkedList();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.pop();
        stack.pop();
        stack.pop();
        assertEquals(0, stack.size(), "Size should be 0 after popping all elements");
        assertNull(stack.pop(), "Pop on empty stack after all elements are removed should return null");
    }
}
