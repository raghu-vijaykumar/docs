---
weight: 5
bookFlatSection: true
title: "Heap"
draft: false
---
# Heap 

A heap is a specialized tree-based data structure that satisfies the heap property. It is commonly used to implement priority queues and is crucial in algorithms like heapsort. A heap can be visualized as a complete binary tree, where every node follows a specific ordering property relative to its children.

## Types of Heaps
- **Max-Heap**: In a max-heap, the key at the root node is greater than or equal to the keys of its children. This property must be true for all nodes in the heap. Therefore, the largest key is at the root.
- **Min-Heap**: In a min-heap, the key at the root node is less than or equal to the keys of its children. This property must be true for all nodes in the heap. Thus, the smallest key is at the root.

## Heap Properties
- **Complete Binary Tree**: A heap is a complete binary tree, meaning all levels are fully filled except possibly for the last level, which is filled from left to right.
- **Heap Order Property**:
  - **Max-Heap**: For every node i, the value of i is greater than or equal to the values of its children.
  - **Min-Heap**: For every node i, the value of i is less than or equal to the values of its children.

## Heap Operations
- **Insertion**: Add a new element to the heap while maintaining the heap property. This is typically done by adding the element to the end of the tree and then "bubbling up" or "sifting up" to restore the heap property.
- **Deletion**: Typically involves removing the root (the maximum element in a max-heap or the minimum element in a min-heap) and then reordering the heap to maintain the heap property. This is done by replacing the root with the last element and "bubbling down" or "sifting down."
- **Peek**: Retrieve the root element (maximum or minimum) without removing it.
- **Heapify**: Convert an arbitrary array into a heap. This operation can be performed efficiently using the "heapify" process.

## Implementation
Heaps are commonly implemented using arrays. The parent-child relationship is maintained using array indices:

For a node at index i:
- The left child is at index 2i + 1.
- The right child is at index 2i + 2.
- The parent is at index (i - 1) / 2.

```java
package dsajava.datastructures.heap;

import java.util.ArrayList;
import java.util.List;

public class MaxHeap {

    // List to store the elements of the heap
    List<Integer> heap = new ArrayList<>();

    // Returns the index of the left child for a given node index
    public int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    // Returns the index of the right child for a given node index
    public int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    // Returns the index of the parent for a given node index
    public int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    // Swaps the elements at the specified indices
    public void swap(int index1, int index2) {
        if (index1 > heap.size() - 1 || index2 > heap.size() - 1)
            return;
        int temp = heap.get(index2);
        heap.set(index2, heap.get(index1));
        heap.set(index1, temp);
    }

    // Inserts a new value into the heap and maintains the heap property
    public void insert(int value) {
        heap.add(value); // Add the new value to the end of the heap
        int currentIndex = heap.size() - 1; // Index of the newly added value
        int parentIndex = getParentIndex(currentIndex); // Get the index of the parent node

        // Perform heapification (bubble-up) to maintain the heap property
        while (currentIndex != 0 && heap.get(currentIndex) > heap.get(parentIndex)) {
            swap(currentIndex, parentIndex); // Swap with parent if heap property is violated
            currentIndex = parentIndex; // Move to the parent index
            parentIndex = getParentIndex(currentIndex); // Update the parent index for the next iteration
        }
    }

    // Removes and returns the root element from the heap
    public Integer remove() {
        if (heap.size() == 0)
            return null;
        if (heap.size() == 1)
            return heap.remove(0);

        Integer rootValue = heap.get(0); // Get the root value
        heap.set(0, heap.remove(heap.size() - 1)); // Replace root with the last element and remove it
        sinkdown(0); // Restore the heap property
        return rootValue;
    }

    // Moves down the element at the given index to restore the heap property
    private void sinkdown(int index) {
        int maxIndex = index; // Initialize the index of the largest element
        while (true) {
            int leftIndex = getLeftChildIndex(maxIndex); // Index of the left child
            int rightIndex = getRightChildIndex(maxIndex); // Index of the right child

            // Check if the left child exists and is larger than the current largest element
            if (leftIndex < heap.size() && heap.get(leftIndex) > heap.get(maxIndex)) {
                maxIndex = leftIndex;
            }

            // Check if the right child exists and is larger than the current largest element
            if (rightIndex < heap.size() && heap.get(rightIndex) > heap.get(maxIndex)) {
                maxIndex = rightIndex;
            }

            // If the largest element is not at the current index, swap and continue
            if (maxIndex != index) {
                swap(maxIndex, index);
                index = maxIndex; // Move to the index of the largest element
            } else {
                return; // Heap property is restored
            }
        }
    }
}
```

## Space and Time Complexity

| Operation | Time Complexity | Space Complexity |
| --------- | --------------- | ---------------- |
| Insertion | O(log n)        | O(n)             |
| Deletion  | O(log n)        | O(n)             |
| Peek      | O(1)            | O(n)             |
| Heapify   | O(n)            | O(n)             |

## When to Use a Heap
- **Priority Queues**: Heaps are ideal for implementing priority queues where the highest (or lowest) priority element needs to be accessed quickly.
- **Heapsort**: A comparison-based sorting algorithm that sorts an array by first building a heap and then repeatedly extracting the maximum (or minimum) element.
- **Graph Algorithms**: Used in algorithms like Dijkstra's shortest path algorithm to efficiently retrieve the minimum-weight edge.


## Advantages
- **Efficient Priority Queue Operations**: Provides O(log n) time complexity for insertion and deletion, making it suitable for priority queue implementations.
- **In-place Sorting**: Heapsort sorts an array in place with O(n log n) time complexity and does not require additional space beyond the input array.

## Disadvantages
- **Not Stable**: Heapsort is not a stable sort, meaning it does not preserve the relative order of equal elements.
- **Slower Than Other Algorithms**: For practical use cases, heapsort is often slower than algorithms like quicksort or mergesort due to higher constant factors and less efficient cache usage.