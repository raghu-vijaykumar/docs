package dsajava.stack;

import java.util.ArrayList;

public class StackAsArrayList {
    ArrayList<Integer> stack = new ArrayList<>();

    public boolean push(int value) {
        return stack.add(value);
    }

    public int pop() {
        return stack.removeLast();
    }

    public int peek() {
        return stack.getLast();
    }

    public int size() {
        return stack.size();
    }

}
