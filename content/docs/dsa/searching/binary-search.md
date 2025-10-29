---
aliases: [""]
weight: 2
bookFlatSection: true
title: "Binary Search"
draft: false
---

# Binary Search

{{< markmap >}}

```markmap
# Binary Search
- **Concept** → Searching a sorted array by repeatedly dividing the search interval in half
- **Conditions** → Array must be sorted
- **Time Complexity** → O(log n)
- **Space Complexity** → O(1)
- **Variants**
  - Lower Bound → First position >= target
  - Upper Bound → First position > target
  - Rotated Array Binary Search → Handle rotation/pivot
- **Applications**
  - Find insertion point
  - Search in sorted data
  - Optimization problems
```

{{< /markmap >}}

Binary Search is an efficient algorithm for finding an element in a sorted array. It works by repeatedly dividing the search interval in half, comparing the middle element with the target value, and narrowing down the search space.

## Theory

1. **Algorithm Steps**:
   - Start with low = 0, high = n-1
   - While low <= high:
     - mid = low + (high - low)/2
     - If arr[mid] == target, return mid
     - If arr[mid] < target, search right: low = mid + 1
     - If arr[mid] > target, search left: high = mid - 1
   - If not found, return -1

2. **Conditions**: The array must be sorted.

3. **Variants**:
   - **Lower Bound**: Find the first index where arr[index] >= target
   - **Upper Bound**: Find the first index where arr[index] > target

## Code Snippets

### Basic Binary Search (Java)

```java
public class BinarySearch {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }
}
```

### Lower Bound (Java)

```java
public int lowerBound(int[] nums, int target) {
    int left = 0, right = nums.length;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (nums[mid] < target) left = mid + 1;
        else right = mid;
    }
    return left;
}
```

### Rotated Array Search (Java)
For rotated sorted arrays (e.g., [4,5,6,7,0,1,2])

```java
public int searchRotated(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (nums[mid] == target) return mid;
        if (nums[left] <= nums[mid]) { // left half is sorted
            if (target >= nums[left] && target < nums[mid]) right = mid - 1;
            else left = mid + 1;
        } else { // right half is sorted
            if (target > nums[mid] && target <= nums[right]) left = mid + 1;
            else right = mid - 1;
        }
    }
    return -1;
}
```

## Leetcode Problems

| Level    | Problem Name & Link                                                                                              | Technique Used              |
| -------- | ---------------------------------------------------------------------------------------------------------------- | --------------------------- |
| 🟢 Easy   | [704. Binary Search](https://leetcode.com/problems/binary-search/)                                               | Basic Binary Search         |
| 🟢 Easy   | [374. Guess Number Higher or Lower](https://leetcode.com/problems/guess-number-higher-or-lower/)                 | Basic Binary Search         |
| 🟡 Medium | [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/)              | Rotated Array Binary Search |
| 🟡 Medium | [81. Search in Rotated Sorted Array II](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/)        | Rotated Array (duplicates)  |
| 🟡 Medium | [162. Find Peak Element](https://leetcode.com/problems/find-peak-element/)                                       | Peak Finding (Variant)      |
| 🟡 Medium | [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Boundary Search             |
| 🔴 Hard   | [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/)                     | Advanced Binary Search      |
