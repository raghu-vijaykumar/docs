---
weight: 1
bookCollapseSection: true
title: "Tree"
draft: false
---

# Tree Data Structures

{{< markmap >}}

```markmap
# Tree Data Structures
- **Basic Trees**
  - Binary Tree [](/docs/dsa/data-structures/tree/binarytree/)
  - Binary Search Tree (BST)
- **Balanced Trees**
  - AVL Tree [](/docs/dsa/data-structures/tree/avl-trees/)
  - Red-Black Tree [](/docs/dsa/data-structures/tree/rb-tree/)
  - B-Tree [](/docs/dsa/data-structures/tree/b-trees/)
- **Specialized Trees**
  - Segment Tree [](/docs/dsa/data-structures/tree/segment-tree/)
  - Fenwick Tree [](/docs/dsa/data-structures/tree/fenwick-tree/)
  - KD-Tree [](/docs/dsa/data-structures/tree/kd-trees/)
  - Suffix Tree [](/docs/dsa/data-structures/tree/suffix-trees/)
- **Tree Operations**
  - Traversal (Inorder, Preorder, Postorder, Level-order)
  - Searching, Insertion, Deletion
  - Balancing, Rotation operations
- **Applications**
  - Databases, File systems
  - Expression evaluation, Parsers
  - Networking, Routing algorithms
```

{{< /markmap >}}

## Introduction to Trees

Trees are hierarchical data structures that consist of nodes connected by edges. Each node contains a value and may have child nodes. Trees are fundamental data structures with widespread applications in computer science.

### Key Tree Properties

- **Root**: Topmost node with no parent
- **Parent**: Node directly above another node
- **Child**: Node directly below a parent node
- **Leaf**: Node with no children
- **Height**: Longest path from root to a leaf
- **Depth**: Distance from root to a node
- **Subtree**: Tree formed by a node and its descendants

## Binary Trees

### Definition and Structure

A binary tree is a tree where each node has at most two children, typically called left and right children.

```java
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
        this.left = this.right = null;
    }
}
```

### Types of Binary Trees

1. **Full Binary Tree**: Every node has 0 or 2 children
2. **Complete Binary Tree**: All levels filled except possibly the last, and last level filled from left to right
3. **Perfect Binary Tree**: All internal nodes have exactly 2 children, all leaves at same level
4. **Balanced Binary Tree**: Height difference between left and right subtrees is at most 1

## Tree Traversal Algorithms

### Depth-First Traversals

#### Inorder Traversal (Left, Root, Right)
```java
public void inorder(TreeNode node) {
    if (node == null) return;

    inorder(node.left);
    System.out.print(node.val + " ");
    inorder(node.right);
}
```

For BSTs, inorder gives sorted order.

#### Preorder Traversal (Root, Left, Right)
```java
public void preorder(TreeNode node) {
    if (node == null) return;

    System.out.print(node.val + " ");
    preorder(node.left);
    preorder(node.right);
}
```

Used for creating copies of trees.

#### Postorder Traversal (Left, Right, Root)
```java
public void postorder(TreeNode node) {
    if (node == null) return;

    postorder(node.left);
    postorder(node.right);
    System.out.print(node.val + " ");
}
```

Used for deleting trees or evaluation.

### Breadth-First Traversal (Level Order)
```java
public void levelOrder(TreeNode root) {
    if (root == null) return;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);

    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        System.out.print(node.val + " ");

        if (node.left != null) queue.add(node.left);
        if (node.right != null) queue.add(node.right);
    }
}
```

## Binary Search Trees (BSTs)

### Properties
- Left subtree contains values less than root
- Right subtree contains values greater than root
- Both subtrees are also BSTs

### Basic Operations

#### Search in BST
```java
public TreeNode search(TreeNode root, int key) {
    if (root == null || root.val == key) return root;

    if (key < root.val) {
        return search(root.left, key);
    } else {
        return search(root.right, key);
    }
}
```

Time Complexity: O(h) where h is height (worst case O(n) for skewed tree)

#### Insert in BST
```java
public TreeNode insert(TreeNode root, int val) {
    if (root == null) {
        return new TreeNode(val);
    }

    if (val < root.val) {
        root.left = insert(root.left, val);
    } else if (val > root.val) {
        root.right = insert(root.right, val);
    }

    return root;
}
```

#### Delete from BST
```java
public TreeNode deleteNode(TreeNode root, int key) {
    if (root == null) return null;

    if (key < root.val) {
        root.left = deleteNode(root.left, key);
    } else if (key > root.val) {
        root.right = deleteNode(root.right, key);
    } else {
        // Node to delete found

        // Case 1: No child or one child
        if (root.left == null) return root.right;
        if (root.right == null) return root.left;

        // Case 2: Two children
        // Find inorder successor (smallest in right subtree)
        TreeNode successor = findMin(root.right);
        root.val = successor.val;
        root.right = deleteNode(root.right, successor.val);
    }

    return root;
}

private TreeNode findMin(TreeNode node) {
    while (node.left != null) node = node.left;
    return node;
}
```

## Self-Balancing Trees

### AVL Trees
AVL trees maintain balance by ensuring the height difference between left and right subtrees is at most 1.

#### Rotations
- **Left Rotation**: When right subtree becomes too tall
- **Right Rotation**: When left subtree becomes too tall
- **Left-Right Rotation**: Double rotation for RL case
- **Right-Left Rotation**: Double rotation for LR case

### Red-Black Trees
Red-Black trees maintain balance using coloring properties:
1. Every node is either red or black
2. Root is black
3. No two consecutive red nodes
4. Black height property

Used in implementations like Java's TreeMap and TreeSet.

### B-Trees
B-trees are self-balancing search trees optimized for systems that read and write large blocks of data.

- Used in databases and file systems
- Minimize disk I/O operations
- Variable number of children per node

## Advanced Tree Structures

### Segment Trees
- Efficient range queries and updates (sum, min, max)
- Used for array range queries

### Fenwick Trees (Binary Indexed Trees)
- Efficient prefix sum queries and point updates
- Space efficient alternative to segment trees for sum queries

### Suffix Trees
- Compress all suffixes of a string
- Used for pattern matching, longest common substring

### KD-Trees
- Multi-dimensional binary search trees
- Used for nearest neighbor searches in 2D/3D space

## Tree Applications

### Database Systems
- B-trees for database indexing
- Efficient range queries and updates

### File Systems
- Directory structures
- Hierarchical organization of files

### Compilers and Parsers
- Expression trees for mathematical expressions
- Abstract Syntax Trees (ASTs) for programming languages

### Networking
- Routing tables in networks
- Spanning trees in network topology

### Artificial Intelligence
- Decision trees for classification
- Game trees for minimax algorithms

## Common Tree Problems

### Tree Construction
- Build tree from traversals (inorder + preorder/postorder)
- Construct BST from sorted array

### Tree Queries
- Maximum depth, minimum depth
- Check if balanced, check if BST
- Lowest Common Ancestor (LCA)

### Tree Modifications
- Invert binary tree
- Flatten to linked list
- Convert to sum tree

## Tree Interview Questions

### Easy
- [104. Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/)
- [100. Same Tree](https://leetcode.com/problems/same-tree/)
- [226. Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/)

### Medium
- [102. Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/)
- [236. Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)
- [114. Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/)

### Hard
- [124. Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/)
- [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)

## Time and Space Complexity

| Operation  | Binary Tree | BST  | AVL      | Red-Black | B-Tree   |
| ---------- | ----------- | ---- | -------- | --------- | -------- |
| **Search** | O(n)        | O(h) | O(log n) | O(log n)  | O(log n) |
| **Insert** | O(n)        | O(h) | O(log n) | O(log n)  | O(log n) |
| **Delete** | O(n)        | O(h) | O(log n) | O(log n)  | O(log n) |

## Key Takeaways

1. **Traversal Types**: Understand inorder, preorder, postorder, and level-order traversals
2. **BST Properties**: Left ≤ root ≤ right for efficient searching
3. **Balancing**: AVL and Red-Black trees provide logarithmic guarantees
4. **Applications**: Choose appropriate tree based on use case (database, file system, etc.)
5. **Operations**: Master insertion, deletion, and search algorithms
6. **Variations**: Learn specialized trees (segment, fenwick, suffix) for specific problems

Trees are versatile data structures essential for hierarchical data representation and efficient searching, with applications ranging from data storage to AI algorithms.
