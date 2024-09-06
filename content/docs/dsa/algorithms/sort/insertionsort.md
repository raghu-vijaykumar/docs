---
weight: 3
bookFlatSection: true
title: "Insertion Sort"
draft: false
---
# Insertion Sort

Insertion Sort is a simple comparison-based sorting algorithm. It builds the final sorted array one item at a time by repeatedly picking the next item from the unsorted portion and inserting it into its correct position within the sorted portion. This algorithm is intuitive and similar to the way you might sort a hand of playing cards.

## Code and Inline Explanation
```java
public class InsertionSort {
    public static void sort(int[] array) {
        // Start from the second element (index 1) since the first element (index 0) is trivially sorted.
        for (int i = 1; i < array.length; i++) {
            // Store the current element to be inserted into the sorted portion.
            int temp = array[i];
            // Initialize j to be the index just before the current element.
            int j = i - 1;

            // Shift elements of the sorted portion to the right until the correct position for temp is found.
            while (j > -1 && temp < array[j]) {
                // Move the element at array[j] to the next position.
                array[j + 1] = array[j];
                // Decrement j to continue shifting elements.
                j--;
            }
            // Insert the temp element into its correct position in the sorted portion.
            array[j + 1] = temp;
        }
    }
}
```
## Space and Time Complexity

- **Time Complexity**:
  - Best Case: O(n) – Occurs when the array is already sorted; only a single pass is required.
  - Average Case: O(n²) – Requires quadratic time due to nested loops where each element might be compared with every other element.
  - Worst Case: O(n²) – Occurs when the array is sorted in reverse order, leading to maximum comparisons and shifts.
- **Space Complexity**: O(1) – Insertion Sort is an in-place sorting algorithm, meaning it requires a constant amount of additional space beyond the input array.

## When Do We Use That

- **Small Data Sets**: It is efficient for small data sets or arrays that are nearly sorted.
- **Online Sorting**: Useful when elements are added one by one and need to be sorted continuously.
- **Simple Implementation**: Suitable when a simple and easy-to-implement sorting algorithm is needed.

## Advantages and Disadvantages

- **Advantages**:
  - Simplicity: Easy to understand and implement.
  - Efficient for Small Arrays: Works well with small or nearly sorted arrays.
  - Stable: Maintains the relative order of equal elements.
  - In-Place: Does not require additional storage beyond the input array.
- **Disadvantages**:
  - Inefficiency for Large Arrays: Not suitable for large arrays due to its O(n²) time complexity.
  - More Comparisons and Shifts: Compared to more advanced algorithms like Quick Sort or Merge Sort, it performs more comparisons and data shifts.