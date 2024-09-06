---
weight: 1
bookFlatSection: true
title: "Bubble Sort"
draft: false
---
# Bubble Sort

## Overview

Bubble Sort is a simple comparison-based sorting algorithm. It repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. This process is repeated until the list is sorted. The algorithm is named for the way smaller elements "bubble" to the top of the list.

## Algorithm

1. **Compare Adjacent Elements**: Starting from the first element, compare each pair of adjacent elements.
2. **Swap If Necessary**: If the current element is greater than the next element, swap them.
3. **Repeat**: Continue this process for each pair of adjacent elements until the end of the list is reached. After each pass, the largest unsorted element is placed in its correct position.
4. **Reduce the Range**: Reduce the range of the list being considered in each pass as the largest elements are already in their correct positions.
5. **Terminate**: Repeat the process until no swaps are needed, indicating that the list is sorted.

## Complexity

- **Time Complexity**:
  - Best Case: O(n) when the array is already sorted (optimized with a flag to detect no swaps).
  - Average Case: O(n²) for a random array.
  - Worst Case: O(n²) when the array is sorted in reverse order.
- **Space Complexity**: O(1) as Bubble Sort is an in-place sorting algorithm.

## Code

```java
package dsajava.algorithms.sorting;

public class BubbleSort {
    public static void sort(int[] array) {
        // Traverse the array from the end to the beginning
        for (int i = array.length - 1; i > 0; i--) {
            // Traverse the array from the start to the current end position
            for (int j = 0; j < i; j++) {
                // Compare adjacent elements
                if (array[j] > array[j + 1]) {
                    // Swap if the current element is greater than the next
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
}
```

## Explanation

- **Outer Loop**: The outer loop iterates from the end of the array to the start, reducing the effective size of the unsorted portion of the array with each pass.
- **Inner Loop**: The inner loop performs comparisons and swaps adjacent elements if they are in the wrong order.
- **Swap Operation**: Uses a temporary variable to swap the elements.

## Use Cases

Bubble Sort is mainly used for educational purposes to illustrate sorting concepts. It is not suitable for large datasets due to its inefficient time complexity compared to more advanced sorting algorithms like Quick Sort or Merge Sort.

## Advantages

- **Simplicity**: Easy to understand and implement.
- **In-Place**: Requires no additional storage beyond the original array.

## Disadvantages

- **Inefficiency**: Poor performance on large datasets due to its O(n²) time complexity.
- **Not Stable**: Bubble Sort does not preserve the relative order of equal elements unless specifically implemented to do so.