---
weight: 4
bookFlatSection: true
title: "Kadane's Algorithm"
draft: false
---

# Kadane's Algorithm

**Kadane's Algorithm** is an efficient way to find the **maximum sum of a contiguous subarray** within a one-dimensional array of numbers. It solves the **Maximum Subarray Problem** with a time complexity of **O(n)**.

## Problem Statement
Given an array of integers (both positive and negative), find the contiguous subarray (containing at least one number) which has the largest sum and return that sum.

### Example:
- Input: `[-2, 1, -3, 4, -1, 2, 1, -5, 4]`
- Output: `6` (Subarray: `[4, -1, 2, 1]`)

## Algorithm Explanation
Kadane’s Algorithm works by iterating through the array and, at each position, keeping track of two values:
1. **Current Subarray Sum (`current_max`)**: The maximum sum of the subarray ending at the current position.
2. **Global Maximum Sum (`global_max`)**: The overall maximum sum found so far.

The key insight is that at each step, the maximum subarray sum either includes the current element or starts afresh at the current element.

### Steps:
1. Initialize `current_max` and `global_max` to the first element of the array.
2. Iterate through the array starting from the second element.
3. For each element, update the `current_max` to the maximum of either the current element or the sum of `current_max + current element`. This checks if adding the current element improves the sum or if it's better to start a new subarray.
4. Update `global_max` if `current_max` is greater than `global_max`.
5. At the end of the iteration, `global_max` will contain the maximum sum of the contiguous subarray.

## Kadane's Algorithm in Python

```python
def kadane(arr):
    # Initialize current_max and global_max to the first element of the array
    current_max = global_max = arr[0]
    
    # Loop through the array starting from the second element
    for num in arr[1:]:
        # Update current_max to the maximum of (num) or (current_max + num)
        current_max = max(num, current_max + num)
        
        # Update global_max if current_max is larger
        global_max = max(global_max, current_max)
    
    return global_max
```

**Example Usage**:
```python
arr = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
print(kadane(arr))  # Output: 6
```
**Dry Run**:
For the array `[-2, 1, -3, 4, -1, 2, 1, -5, 4]`:

Start with `current_max = global_max = -2`

Process element 1:
- `current_max = max(1, -2 + 1) = 1`
- `global_max = max(-2, 1) = 1`

Process element -3:
- `current_max = max(-3, 1 + -3) = -2`
- `global_max = max(1, -2) = 1`

Process element 4:
- `current_max = max(4, -2 + 4) = 4`
- `global_max = max(1, 4) = 4`

Process element -1:
- `current_max = max(-1, 4 + -1) = 3`
- `global_max = max(4, 3) = 4`

Process element 2:
- `current_max = max(2, 3 + 2) = 5`
- `global_max = max(4, 5) = 5`

Process element 1:
- `current_max = max(1, 5 + 1) = 6`
- `global_max = max(5, 6) = 6`

Process element -5:
- `current_max = max(-5, 6 + -5) = 1`
- `global_max = max(6, 1) = 6`

Process element 4:
- `current_max = max(4, 1 + 4) = 5`
- `global_max = max(6, 5) = 6`

Final result: `global_max = 6`.

## Time Complexity
- **Time Complexity**: O(n), where n is the size of the array. The array is traversed once.
- **Space Complexity**: O(1), because only a few extra variables are used (no additional space proportional to the input size).

Kadane’s Algorithm is highly efficient and widely used for solving the Maximum Subarray Problem in various real-world applications, including stock price analysis and signal processing.