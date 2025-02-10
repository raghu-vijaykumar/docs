---
weight: 3
bookFlatSection: true
title: "Stack"
draft: false
---

# Stack

A Stack is a linear data structure that follows the LIFO (Last In, First Out) principle, where the last element added is the first one to be removed. Stacks have two primary operations: push, which adds an element to the top, and pop, which removes the top element.

There are three common ways to implement a stack:

- Using ArrayList (dynamic array).
- Using a Linked List (dynamic structure of nodes).
- Using a Resizing Array (dynamic array with resizing capabilities).

## Stack Implementations

### Stack as ArrayList

```java
public class StackAsArrayList {
    ArrayList<Integer> stack = new ArrayList<>();

    public boolean push(int value) {
        return stack.add(value);  // Adds an element to the end of the list
    }

    public int pop() {
        return stack.remove(stack.size() - 1);  // Removes the last element
    }

    public int peek() {
        return stack.get(stack.size() - 1);  // Returns the last element without removing
    }

    public int size() {
        return stack.size();  // Returns the size of the stack
    }
}
```

### Stack as Linked List

```java
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

    public void push(int value) {
        Node newNode = new Node(value);  // Creates a new node
        newNode.next = top;  // Sets the next reference to the current top
        top = newNode;  // Updates the top to the new node
        size++;
    }

    public Node pop() {
        if (size == 0) return null;  // Stack underflow check
        Node popped = top;  // Get the top element
        top = top.next;  // Update top to the next element
        size--;
        return popped;
    }

    public Node peek() {
        return top;  // Returns the top node without removing
    }

    public int size() {
        return size;  // Returns the current size
    }
}
```

### Stack as Resizing Array

```java
public class StackAsResizingArray {
    int[] stack = new int[1];  // Start with an initial capacity of 1
    int size = 0;

    public void push(int value) {
        if (isFull()) resize();  // Resize the array if it's full
        stack[size++] = value;  // Add the value and increment size
    }

    public int pop() {
        if (size == 0) throw new NoSuchElementException("Stack is empty");
        return stack[--size];  // Decrement size and return the popped element
    }

    public int peek() {
        if (size == 0) throw new NoSuchElementException("Stack is empty");
        return stack[size - 1];  // Return the top element without removing
    }

    public int size() {
        return size;  // Return the current size
    }

    private boolean isFull() {
        return size == stack.length;  // Check if the stack is full
    }

    private void resize() {
        stack = Arrays.copyOf(stack, stack.length * 2);  // Double the array size
    }
}
```

## Operations, Time, and Space Complexities

| Operation  | ArrayList Stack | LinkedList Stack | Resizing Array Stack | Time Complexity | Space Complexity |
| ---------- | --------------- | ---------------- | -------------------- | --------------- | ---------------- |
| `push()`   | O(1)            | O(1)             | O(1) (amortized)     | O(1)            | O(N)             |
| `pop()`    | O(1)            | O(1)             | O(1)                 | O(1)            | O(N)             |
| `peek()`   | O(1)            | O(1)             | O(1)                 | O(1)            | O(1)             |
| `size()`   | O(1)            | O(1)             | O(1)                 | O(1)            | O(1)             |
| `resize()` | N/A             | N/A              | O(N)                 | O(N)            | O(N)             |

- **ArrayList Stack**: Uses dynamic arrays, efficient with most operations, but resizing can be costly.
- **LinkedList Stack**: Uses nodes and is flexible with dynamic memory usage but has higher memory overhead due to the node structure.
- **Resizing Array Stack**: Combines dynamic array properties with resizing to ensure efficient use of space.

## When to Use a Stack

Stacks are useful in scenarios where you need to maintain a history or order of operations that follow the Last In, First Out (LIFO) principle. Some common uses of stacks include:

- Expression evaluation (e.g., postfix evaluation).
- Backtracking problems (e.g., solving mazes).
- Function call management (e.g., recursion).
- Undo operations in text editors or browsers.

## Advantages and Disadvantages

| Implementation   | Advantages                                   | Disadvantages                         |
| ---------------- | -------------------------------------------- | ------------------------------------- |
| ArrayList Stack  | Dynamic resizing; O(1) access time.          | Resizing can be costly (O(N)).        |
| LinkedList Stack | Efficient memory use for dynamic data.       | Higher memory overhead for pointers.  |
| Resizing Array   | Amortized constant time for most operations. | Resizing incurs time overhead (O(N)). |

## Leetcode 

| **Level**    | **Problem Name & Link**                                                                                                   | **Technique Used**                |
| ------------ | ------------------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| 🟢 **Easy**   | [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/)                                                 | Stack (LIFO)                      |
| 🟢 **Easy**   | [155. Min Stack](https://leetcode.com/problems/min-stack/)                                                                | Stack with Auxiliary Min Tracking |
| 🟢 **Easy**   | [232. Implement Queue using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/)                          | Two Stacks                        |
| 🟢 **Easy**   | [682. Baseball Game](https://leetcode.com/problems/baseball-game/)                                                        | Stack for Score Calculation       |
| 🟢 **Easy**   | [844. Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/)                                  | Stack for String Processing       |
| 🟡 **Medium** | [71. Simplify Path](https://leetcode.com/problems/simplify-path/)                                                         | Stack for Path Navigation         |
| 🟡 **Medium** | [150. Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/)                  | Stack for Expression Evaluation   |
| 🟡 **Medium** | [394. Decode String](https://leetcode.com/problems/decode-string/)                                                        | Stack for Nested Processing       |
| 🟡 **Medium** | [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/)                                              | Monotonic Stack                   |
| 🟡 **Medium** | [901. Online Stock Span](https://leetcode.com/problems/online-stock-span/)                                                | Monotonic Stack                   |
| 🟡 **Medium** | [1021. Remove Outermost Parentheses](https://leetcode.com/problems/remove-outermost-parentheses/)                         | Stack for Parentheses Tracking    |
| 🟡 **Medium** | [1047. Remove All Adjacent Duplicates In String](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/) | Stack for Character Removal       |
| 🟡 **Medium** | [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/)                                              | Monotonic Stack                   |
| 🔴 **Hard**   | [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)                       | Monotonic Stack                   |
| 🔴 **Hard**   | [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/)                                                 | Stack for 2D Histogram            |
| 🔴 **Hard**   | [316. Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/)                                  | Stack + Greedy                    |
| 🔴 **Hard**   | [895. Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack/)                                    | Stack with Frequency Map          |
| 🔴 **Hard**   | [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/)                                             | Stack for Water Collection        |

