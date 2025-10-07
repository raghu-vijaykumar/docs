---
weight: 1
bookFlatSection: true
title: "Binary Tree"
draft: false
---

# Binary Tree

{{< markmap >}}

```markmap
# Binary Tree
- **Basic Concepts**
  - Node Structure
  - Tree Properties (Height, Depth, Size)
  - Types (Full, Complete, Perfect, Balanced)
- **Tree Construction**
  - From Array (Level Order)
  - From Traversals (Inorder + Preorder/Postorder)
  - Balanced Constructions
- **Traversal Algorithms**
  - Depth-First: Inorder, Preorder, Postorder
  - Breadth-First: Level Order
  - Iterative vs Recursive
- **Common Operations**
  - Size, Height, Diameter
  - Leaf Count, Node Counts
  - Mirror Tree, Invert Tree
  - Path Operations
```

{{< /markmap >}}

## Introduction to Binary Trees

A **Binary Tree** is a hierarchical data structure in which each node has at most two children, referred to as the left child and the right child. It's one of the most fundamental tree structures used in computer science.

### Node Structure

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}
```

## Tree Properties and Terminology

### Key Properties

- **Root**: The topmost node (no parent)
- **Parent**: Node directly above another node
- **Child**: Node directly below a parent (left/right child)
- **Leaf**: Node with no children
- **Internal Node**: Node with at least one child
- **Height**: Longest path from root to leaf (number of edges)
- **Depth**: Distance from root to a node
- **Level**: Nodes at same depth

### Tree Size and Structure Metrics

```java
// Calculate size (number of nodes)
public int size(TreeNode root) {
    if (root == null) return 0;
    return 1 + size(root.left) + size(root.right);
}

// Calculate height
public int height(TreeNode root) {
    if (root == null) return -1; // or 0 depending on convention
    return 1 + Math.max(height(root.left), height(root.right));
}
```

## Types of Binary Trees

### 1. Full Binary Tree
Every node has 0 or 2 children.

**Properties**: Maximum nodes at each level, used for efficient representation.

### 2. Complete Binary Tree
All levels filled except possibly the last, and last level filled from left to right.

**Properties**: Can be represented as array efficiently.

### 3. Perfect Binary Tree
All internal nodes have exactly 2 children, all leaves at same level.

**Properties**: Symmetric structure, predictable number of nodes.

### 4. Balanced Binary Tree
Height difference between left and right subtrees ≤ 1 for all nodes.

**Properties**: Ensures logarithmic height, efficient operations.

## Tree Construction

### From Array (Level Order)

```java
public TreeNode buildTree(int[] arr) {
    if (arr == null || arr.length == 0) return null;

    TreeNode root = new TreeNode(arr[0]);
    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);

    int i = 1;
    while (!queue.isEmpty() && i < arr.length) {
        TreeNode current = queue.poll();

        // Left child
        if (arr[i] != -1) { // -1 represents null
            current.left = new TreeNode(arr[i]);
            queue.add(current.left);
        }
        i++;

        // Right child
        if (i < arr.length && arr[i] != -1) {
            current.right = new TreeNode(arr[i]);
            queue.add(current.right);
        }
        i++;
    }

    return root;
}
```

### From Inorder and Preorder Traversals

```java
public TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderIndex = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderIndex.put(inorder[i], i);
    }

    return buildTreeHelper(preorder, 0, preorder.length - 1,
                          inorder, 0, inorder.length - 1, inorderIndex);
}

private TreeNode buildTreeHelper(int[] preorder, int preStart, int preEnd,
                                int[] inorder, int inStart, int inEnd,
                                Map<Integer, Integer> inorderIndex) {

    if (preStart > preEnd || inStart > inEnd) return null;

    int rootVal = preorder[preStart];
    TreeNode root = new TreeNode(rootVal);

    int rootIndex = inorderIndex.get(rootVal);
    int leftSubtreeSize = rootIndex - inStart;

    root.left = buildTreeHelper(preorder, preStart + 1, preStart + leftSubtreeSize,
                               inorder, inStart, rootIndex - 1, inorderIndex);

    root.right = buildTreeHelper(preorder, preStart + leftSubtreeSize + 1, preEnd,
                                inorder, rootIndex + 1, inEnd, inorderIndex);

    return root;
}
```

## Tree Traversal Algorithms

### Depth-First Traversals

#### Inorder Traversal (Left, Root, Right)
```java
public void inorder(TreeNode root) {
    if (root == null) return;
    inorder(root.left);
    System.out.print(root.val + " ");
    inorder(root.right);
}
```

**Iterative Version:**
```java
public void inorderIterative(TreeNode root) {
    Stack<TreeNode> stack = new Stack<>();
    TreeNode current = root;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }

        current = stack.pop();
        System.out.print(current.val + " ");
        current = current.right;
    }
}
```

#### Preorder Traversal (Root, Left, Right)
```java
public void preorder(TreeNode root) {
    if (root == null) return;
    System.out.print(root.val + " ");
    preorder(root.left);
    preorder(root.right);
}
```

#### Postorder Traversal (Left, Right, Root)
```java
public void postorder(TreeNode root) {
    if (root == null) return;
    postorder(root.left);
    postorder(root.right);
    System.out.print(root.val + " ");
}
```

### Breadth-First (Level Order) Traversal
```java
public void levelOrder(TreeNode root) {
    if (root == null) return;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            System.out.print(node.val + " ");

            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        System.out.println(); // Next level
    }
}
```

### Boundary Traversal
Print nodes in order: left boundary, leaves, right boundary.

```java
public void boundaryTraversal(TreeNode root) {
    if (root == null) return;

    System.out.print(root.val + " ");

    // Print left boundary
    printLeftBoundary(root.left);

    // Print leaves
    printLeaves(root.left);
    printLeaves(root.right);

    // Print right boundary bottom-up
    printRightBoundary(root.right);
}

private void printLeftBoundary(TreeNode node) {
    if (node == null) return;
    if (node.left != null || node.right != null) {
        System.out.print(node.val + " ");
    }
    if (node.left != null) printLeftBoundary(node.left);
    else printLeftBoundary(node.right);
}

private void printLeaves(TreeNode node) {
    if (node == null) return;
    if (node.left == null && node.right == null) {
        System.out.print(node.val + " ");
        return;
    }
    printLeaves(node.left);
    printLeaves(node.right);
}

private void printRightBoundary(TreeNode node) {
    if (node == null) return;
    if (node.right != null) printRightBoundary(node.right);
    else printRightBoundary(node.left);
    if (node.left != null || node.right != null) {
        System.out.print(node.val + " ");
    }
}
```

## Common Binary Tree Operations

### Diameter of Binary Tree
```java
private int diameter = 0;

public int diameterOfBinaryTree(TreeNode root) {
    height(root);
    return diameter;
}

private int height(TreeNode node) {
    if (node == null) return 0;

    int leftHeight = height(node.left);
    int rightHeight = height(node.right);

    diameter = Math.max(diameter, leftHeight + rightHeight);

    return 1 + Math.max(leftHeight, rightHeight);
}
```

### Check if Balanced
```java
public boolean isBalanced(TreeNode root) {
    return checkBalance(root) != -1;
}

private int checkBalance(TreeNode node) {
    if (node == null) return 0;

    int leftHeight = checkBalance(node.left);
    if (leftHeight == -1) return -1;

    int rightHeight = checkBalance(node.right);
    if (rightHeight == -1) return -1;

    if (Math.abs(leftHeight - rightHeight) > 1) return -1;

    return 1 + Math.max(leftHeight, rightHeight);
}
```

### Invert Binary Tree
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;

    TreeNode temp = root.left;
    root.left = root.right;
    root.right = temp;

    invertTree(root.left);
    invertTree(root.right);

    return root;
}
```

### Count Leaves
```java
public int countLeaves(TreeNode root) {
    if (root == null) return 0;
    if (root.left == null && root.right == null) return 1;
    return countLeaves(root.left) + countLeaves(root.right);
}
```

## Path Problems

### Root to Leaf Paths
```java
public void printPaths(TreeNode root) {
    List<Integer> path = new ArrayList<>();
    printPathsUtil(root, path);
}

private void printPathsUtil(TreeNode node, List<Integer> path) {
    if (node == null) return;

    path.add(node.val);

    if (node.left == null && node.right == null) {
        System.out.println(path);
    } else {
        printPathsUtil(node.left, path);
        printPathsUtil(node.right, path);
    }

    path.remove(path.size() - 1);
}
```

### Check if Path Sum Exists
```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;

    targetSum -= root.val;

    if (root.left == null && root.right == null) {
        return targetSum == 0;
    }

    return hasPathSum(root.left, targetSum) ||
           hasPathSum(root.right, targetSum);
}
```

### Find All Paths with Sum
```java
public void printPathsWithSum(TreeNode root, int sum) {
    List<Integer> path = new ArrayList<>();
    printPathsWithSumUtil(root, sum, path, 0);
}

private void printPathsWithSumUtil(TreeNode node, int sum,
                                  List<Integer> path, int currentSum) {
    if (node == null) return;

    path.add(node.val);
    currentSum += node.val;

    if (node.left == null && node.right == null && currentSum == sum) {
        System.out.println(path);
    }

    printPathsWithSumUtil(node.left, sum, path, currentSum);
    printPathsWithSumUtil(node.right, sum, path, currentSum);

    path.remove(path.size() - 1);
}
```

## Tree Serialization and Deserialization

### Serialize (Preorder)
```java
public String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serializeHelper(root, sb);
    return sb.toString();
}

private void serializeHelper(TreeNode node, StringBuilder sb) {
    if (node == null) {
        sb.append("null,");
        return;
    }

    sb.append(node.val).append(",");
    serializeHelper(node.left, sb);
    serializeHelper(node.right, sb);
}
```

### Deserialize
```java
public TreeNode deserialize(String data) {
    String[] nodes = data.split(",");
    return deserializeHelper(new int[]{0}, nodes);
}

private TreeNode deserializeHelper(int[] index, String[] nodes) {
    if (index[0] >= nodes.length || nodes[index[0]].equals("null")) {
        index[0]++;
        return null;
    }

    TreeNode node = new TreeNode(Integer.parseInt(nodes[index[0]++]));
    node.left = deserializeHelper(index, nodes);
    node.right = deserializeHelper(index, nodes);

    return node;
}
```

## Tree Queries and Checks

### Check if Binary Search Tree
```java
public boolean isBST(TreeNode root) {
    return isBSTUtil(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean isBSTUtil(TreeNode node, long min, long max) {
    if (node == null) return true;

    if (node.val <= min || node.val >= max) return false;

    return isBSTUtil(node.left, min, node.val) &&
           isBSTUtil(node.right, node.val, max);
}
```

### Lowest Common Ancestor (LCA)
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;

    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);

    if (left != null && right != null) return root;
    return left != null ? left : right;
}
```

## Applications of Binary Trees

### Expression Trees
Used for mathematical expression evaluation.

### Decision Trees
Used in machine learning for classification.

### Game Trees
Used in game AI (minimax algorithm).

### Storage Systems
- Database indexes
- File systems

## Practice Problems

### Easy
- [100. Same Tree](https://leetcode.com/problems/same-tree/)
- [101. Symmetric Tree](https://leetcode.com/problems/symmetric-tree/)
- [104. Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/)
- [226. Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/)

### Medium
- [102. Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/)
- [105. Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)
- [236. Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)
- [543. Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/)

### Hard
- [124. Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/)
- [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)

## Time Complexities

| Operation | Time Complexity                        |
| --------- | -------------------------------------- |
| Insertion | O(1) - O(log n) depending on position  |
| Deletion  | O(1) - O(log n) depending on position  |
| Search    | O(n) - O(log n) depending on structure |
| Traversal | O(n) for all types                     |
| Height    | O(n)                                   |
| Diameter  | O(n)                                   |

## Key Takeaways

1. **Traversal Order**: Master inorder, preorder, postorder, and level-order traversals
2. **Recursive Patterns**: Most problems use recursive solutions with base cases
3. **Edge Cases**: Handle null nodes, single nodes, leaf nodes properly
4. **Path Problems**: Use backtracking with path lists or sum tracking
5. **Tree Construction**: Understand building trees from arrays and traversals
6. **Serialization**: Preorder/inorder traversals for tree reconstruction

Binary trees form the foundation for more advanced tree structures and are essential for understanding hierarchical data and recursive algorithms.
