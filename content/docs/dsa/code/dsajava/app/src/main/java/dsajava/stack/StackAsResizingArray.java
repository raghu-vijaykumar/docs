package dsajava.stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class StackAsResizingArray {
    int[] stack = new int[1];
    int size = 0;

    public StackAsResizingArray() {

    }

    private void resize() {
        stack = Arrays.copyOf(stack, stack.length * 2);
    }

    private boolean isFull() {
        if (size == stack.length)
            return true;
        return false;
    }

    public void push(int value) {
        if (isFull())
            resize();
        stack[size] = value;
        size++;
    }

    public int pop() {
        if (size == 0)
            throw new NoSuchElementException("Stack is empty");
        int popped = stack[size - 1];
        size--;
        return popped;
    }

    public int peek() {
        if (size == 0)
            throw new NoSuchElementException("Stack is empty");
        return stack[size - 1];
    }

    public int size() {
        return size;
    }
}
