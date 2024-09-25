---
weight: 5
bookFlatSection: true
title: "Backtracking"
draft: false
---

# Backtracking 

Backtracking is a general algorithmic technique used to solve problems incrementally, by trying to build a solution piece by piece. When an invalid solution is detected, the algorithm removes the last piece and backtracks to the previous step. It is often used for solving combinatorial problems where a large search space needs to be explored.

Backtracking is akin to depth-first search (DFS) in tree or graph traversal, where decisions are made and if any leads to a dead-end, the algorithm backtracks and tries other possibilities.

## Key Concepts

### 1. **Decision Tree**:
In backtracking, the solution space is often visualized as a decision tree where:
- Each node represents a partial solution.
- Each edge represents a decision that leads to the next node.
- A solution is found by exploring these nodes using recursion or an iterative stack-based approach.

### 2. **Base Case**:
A base case defines the conditions under which the recursion or iterative process terminates. This is typically when:
- A valid solution is found (e.g., finding a path in a maze).
- All possible options have been exhausted (backtracking terminates).

### 3. **Pruning**:
Pruning is the process of eliminating certain paths early in the backtracking process to avoid unnecessary work. It reduces the search space and improves performance by skipping paths that are guaranteed to be invalid or suboptimal.

### 4. **Backtracking Tree**:
The backtracking tree is the implicit tree structure formed by the recursive calls (or iterations). It branches out at each decision point and backtracks when invalid paths are encountered.

### 5. **Backtracking Strategy**:
The key steps of backtracking are:
- **Choose**: Select a candidate for the next step.
- **Explore**: Recursively proceed to explore this candidate.
- **Un-choose (Backtrack)**: If the candidate doesn’t lead to a solution, backtrack by undoing the last step and try a different candidate.

---

## Example Problems Using Backtracking

### 1. **N-Queens Problem**:
The problem is to place `N` queens on an `N x N` chessboard such that no two queens threaten each other. The backtracking algorithm explores each possibility of placing a queen and backtracks when placing a queen leads to a conflict.

#### Pseudo Code:
```python
def solveNQueens(board, row):
    if row == N:  # Base case: all queens placed
        return True
    for col in range(N):  # Try placing queen in each column
        if isSafe(board, row, col):
            placeQueen(board, row, col)
            if solveNQueens(board, row + 1):  # Explore next row
                return True
            removeQueen(board, row, col)  # Backtrack
    return False  # No valid position found

def isSafe(board, row, col):
    # Check column, left diagonal, right diagonal for conflicts
    ...
```

### 2. Sudoku Solver:
Given a 9x9 Sudoku grid, the problem is to fill empty cells such that each row, column, and 3x3 sub-grid contains digits 1-9 without repetition. Backtracking explores possible numbers for each empty cell, and if a solution is invalid, it backtracks.

### 3. Subset Sum Problem:
Given a set of integers and a target sum, the problem is to find all subsets of the set that sum up to the target. Backtracking explores each subset by deciding whether to include each element in the subset or not.

## Iterative vs Recursive Backtracking
Backtracking is commonly implemented recursively, but it can also be implemented iteratively using an explicit stack (like in depth-first search). In the recursive version, the call stack maintains the backtracking state, while in the iterative version, we use an explicit data structure (e.g., Stack or Deque) to track decisions.

Recursive Example:
```java
boolean solve(int row, int col) {
    if (isSolution(row, col)) {
        return true;
    }
    for (nextMove : possibleMoves) {
        if (isValid(nextMove)) {
            makeMove(nextMove);
            if (solve(nextRow, nextCol)) {
                return true;
            }
            unMakeMove(nextMove);  // Backtrack
        }
    }
    return false;  // No valid moves
}
```
Iterative Example (using a stack):
```java
Stack<State> stack = new Stack<>();
stack.push(initialState);

while (!stack.isEmpty()) {
    State current = stack.pop();
    if (isSolution(current)) {
        return true;
    }
    for (nextMove : possibleMoves(current)) {
        if (isValid(nextMove)) {
            stack.push(nextState(nextMove));  // Explore next move
        }
    }
}
```
## When to Use Backtracking
Backtracking is used when:

- The problem involves exploring all potential solutions (e.g., permutations, combinations, or paths).
- The solution space is too large to explore exhaustively, but can be pruned by eliminating infeasible solutions early.
- You want to find one or all solutions, possibly optimizing for time or space by avoiding unnecessary exploration.

## Examples of Problems That Suit Backtracking:
- Puzzle solving (e.g., Sudoku, N-Queens)
- Pathfinding (e.g., Maze-solving)
- Generating combinations, permutations, or subsets
- Graph coloring problems
- String pattern matching

## Time Complexity of Backtracking
The time complexity of backtracking depends on:

- **Branching Factor**: The number of decisions or choices at each step.
- **Depth of the Search Tree**: The depth to which the algorithm explores the decision tree.
- **Pruning Efficiency**: How early the algorithm can prune branches.

In the worst case, backtracking explores all possible combinations, leading to exponential time complexity `O(b^d)`, where `b` is the branching factor, and `d` is the depth of the decision tree.

## Common Optimizations
- **Memoization**: Memoization can be used to store intermediate results and avoid recomputation, particularly when the same subproblems are encountered repeatedly.

- **Constraint Propagation**: In problems like Sudoku, constraint propagation can be used to reduce the number of choices at each step by narrowing down the possible values early.

- **Heuristics**: In some cases, heuristics like the Minimum Remaining Value (MRV) or Least Constraining Value (LCV) can be applied to choose the best option first, which may reduce the need for backtracking.

## Conclusion
Backtracking is a powerful algorithmic technique for solving complex problems by exploring all possible solutions incrementally. By making decisions and backtracking when necessary, it efficiently searches through the solution space, especially when combined with pruning techniques. However, its performance can degrade in problems with large solution spaces, and optimizations like memoization, constraint propagation, and heuristics are crucial for improving efficiency.