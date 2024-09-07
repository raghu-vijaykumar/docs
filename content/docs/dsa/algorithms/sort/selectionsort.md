---
weight: 2
bookFlatSection: true
title: "Selection Sort"
draft: false
---
# Selection Sort

Selection Sort is a simple comparison-based sorting algorithm. It works by repeatedly finding the minimum (or maximum, depending on sorting order) element from the unsorted portion of the array and moving it to the beginning (or end) of the sorted portion. This algorithm is easy to understand and implement but is not very efficient for large data sets.

## Code and Inline Comments

```java
public class SelectionSort {
    public static void sort(int[] array) {
        // Iterate over each element in the array except the last one
        for (int i = 0; i < array.length; i++) {
            // Assume the current index is the minimum
            int minIndex = i;

            // Find the index of the minimum element in the unsorted portion of the array
            for (int j = i + 1; j < array.length; j++) {
                // Update minIndex if a smaller element is found
                if (array[j] < array[minIndex])
                    minIndex = j;
            }

            // If minIndex has changed, swap the minimum element found with the element at the current index
            if (minIndex != i) {
                int temp = array[minIndex];
                array[minIndex] = array[i];
                array[i] = temp;
            }
        }
    }
}
```

## Space and Time Complexity

- **Time Complexity**:
  - Best Case: O(n²) – Selection Sort always performs the same number of comparisons, regardless of the initial order of the array.
  - Average Case: O(n²) – The algorithm performs a quadratic number of comparisons on average.
  - Worst Case: O(n²) – The algorithm performs a quadratic number of comparisons in the worst case as well.
- **Space Complexity**: O(1) – Selection Sort is an in-place sorting algorithm, requiring a constant amount of extra space.

## When Do We Use That

- **Small Data Sets**: Useful for small arrays where the simplicity of the algorithm is advantageous.
- **Educational Purposes**: Often used for teaching sorting algorithms due to its straightforward implementation.
- **When Memory Usage Is Critical**: Suitable when memory space is limited, as it requires only a constant amount of additional space.

## Advantages and Disadvantages

**Advantages**:

- **In-Place**: Does not require additional storage beyond the input array.
- **Predictable Performance**: Always performs a fixed number of operations regardless of input.

**Disadvantages**:

- **Inefficient for Large Arrays**: Has a quadratic time complexity, making it inefficient for large data sets compared to more advanced algorithms like Merge Sort or Quick Sort.
- **Not Stable**: Does not preserve the relative order of equal elements.
- **More Comparisons**: Performs many comparisons and swaps compared to algorithms with better time complexity.