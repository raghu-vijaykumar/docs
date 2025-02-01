---
weight: 5
bookFlatSection: true
title: "Binary Search Tree"
draft: false
---

# Binary Search Tree

A Binary Search Tree (BST) is a type of binary tree that maintains its elements in a sorted order, allowing for efficient search, insertion, and deletion operations. Each node in a BST has at most two children, referred to as the left and right child.

## Key Properties

1. **Node Structure:**
   - Each node in a BST contains:
     - **Key (or Value):** The value stored in the node.
     - **Left Child:** A reference to the left subtree (all nodes in the left subtree have keys less than the node’s key).
     - **Right Child:** A reference to the right subtree (all nodes in the right subtree have keys greater than the node’s key).

2. **Binary Search Tree Property:**
   - For each node:
     - All keys in the left subtree are less than the node’s key.
     - All keys in the right subtree are greater than the node’s key.

## Operations

1. **Search:**
   - Start at the root and recursively traverse the left or right subtree based on the comparison of the search key with the current node’s key.

2. **Insertion:**
   - Insert a new node by recursively finding the appropriate position where the new node should be placed to maintain the BST property.

3. **Deletion:**
   - Remove a node while preserving the BST property. This involves handling three cases:
     - Node with no children (leaf node): Simply remove the node.
     - Node with one child: Replace the node with its child.
     - Node with two children: Find the node’s successor (or predecessor), replace the node with the successor’s key, and then remove the successor node.

4. **Traversal:**
   - In-order Traversal: Visit the left subtree, the node, and then the right subtree (results in sorted order of keys).
   - Pre-order Traversal: Visit the node, then the left subtree, and finally the right subtree.
   - Post-order Traversal: Visit the left subtree, then the right subtree, and finally the node.

## Use Cases
- BSTs are used in scenarios requiring dynamic data insertion and deletion with efficient searching. They are suitable for applications like dictionary implementations, priority queues, and maintaining ordered collections.

## Implementation of Binary Search Tree (BST)

The following code provides an implementation of a Binary Search Tree (BST) with various operations including insertion, search, and different types of tree traversal.

```java
package dsajava.datastructures.tree;

import java.util.ArrayList;
import java.util.LinkedList;

public class BinarySearchTree {

    // Root node of the binary search tree
    Node root;

    // Class representing a node in the binary search tree
    public class Node {
        int value; // Value of the node
        Node left; // Left child of the node
        Node right; // Right child of the node

        // Constructor to initialize a node with a value
        public Node(int value) {
            this.value = value;
        }
    }

    // Insert a value into the binary search tree
    public boolean insert(int value) {
        // Create a new node with the provided value
        Node node = new Node(value);

        // If the tree is empty, set the new node as the root
        if (root == null) {
            root = node;
            return true;
        }

        // Start traversing the tree from the root
        Node temp = root;

        // Traverse the tree until the correct position is found
        while (true) {
            // If the value already exists in the tree, return false
            if (temp.value == value) {
                return false;
            }

            // If the value is smaller, go to the left subtree
            if (value < temp.value) {
                if (temp.left == null) {
                    temp.left = node; // Insert as left child
                    return true;
                } else {
                    temp = temp.left; // Keep traversing the left subtree
                }
            }
            // If the value is larger, go to the right subtree
            else {
                if (temp.right == null) {
                    temp.right = node; // Insert as right child
                    return true;
                } else {
                    temp = temp.right; // Keep traversing the right subtree
                }
            }
        }
    }

    // Check if a value is present in the tree
    public boolean contains(int value) {
        // Start traversing from the root
        Node temp = root;

        // Traverse the tree until we either find the value or reach a leaf
        while (temp != null) {
            if (temp.value == value)
                return true; // Value found
            if (value < temp.value)
                temp = temp.left; // Move to the left subtree
            else
                temp = temp.right; // Move to the right subtree
        }

        // Return false if value not found
        return false;
    }

    // Perform a Breadth-First Search (BFS) traversal of the tree
    public ArrayList<Integer> BFS() {
        // List to store the BFS traversal result
        ArrayList<Integer> results = new ArrayList<>();

        // Queue to assist with level-order traversal
        LinkedList<Node> queue = new LinkedList<>();

        // Start BFS if the tree is not empty
        if (root != null) {
            // Add the root node to the queue
            queue.add(root);

            // Traverse the tree level by level
            while (!queue.isEmpty()) {
                // Remove the front node from the queue
                Node node = queue.remove();

                // Add the node's value to the result list
                results.add(node.value);

                // Add left child to the queue if it exists
                if (node.left != null) {
                    queue.add(node.left);
                }

                // Add right child to the queue if it exists
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
        }

        // Return the result of BFS traversal
        return results;
    }

    // Perform a pre-order traversal (root -> left -> right)
    public ArrayList<Integer> preOrder() {
        // List to store the traversal result
        ArrayList<Integer> results = new ArrayList<>();

        // Nested class to handle the recursive traversal
        class Traverse {
            public Traverse(Node node) {
                if (node != null) {
                    results.add(node.value); // Visit the root node
                    new Traverse(node.left); // Traverse the left subtree
                    new Traverse(node.right); // Traverse the right subtree
                }
            }
        }

        // Start the traversal from the root
        new Traverse(root);
        return results;
    }

    // Perform an in-order traversal (left -> root -> right)
    public ArrayList<Integer> inOrder() {
        // List to store the traversal result
        ArrayList<Integer> results = new ArrayList<>();

        // Helper method to perform in-order traversal
        inOrderTraversal(root, results);

        // Return the result of in-order traversal
        return results;
    }

    // Helper method for in-order traversal
    private void inOrderTraversal(Node node, ArrayList<Integer> results) {
        if (node == null)
            return; // Base case: if node is null, return

        // Traverse the left subtree
        inOrderTraversal(node.left, results);

        // Visit the root node
        results.add(node.value);

        // Traverse the right subtree
        inOrderTraversal(node.right, results);
    }

    // Perform a post-order traversal (left -> right -> root)
    public ArrayList<Integer> postOrder() {
        // List to store the traversal result
        ArrayList<Integer> results = new ArrayList<>();

        // Helper method to perform post-order traversal
        postOrderTraversal(root, results);

        // Return the result of post-order traversal
        return results;
    }

    // Helper method for post-order traversal
    private void postOrderTraversal(Node node, ArrayList<Integer> results) {
        if (node == null)
            return; // Base case: if node is null, return

        // Traverse the left subtree
        postOrderTraversal(node.left, results);

        // Traverse the right subtree
        postOrderTraversal(node.right, results);

        // Visit the root node
        results.add(node.value);
    }
}
```
## Space and Time Complexity

| Operation  | Time Complexity | Space Complexity |
| ---------- | --------------- | ---------------- |
| Insertion  | O(h)            | O(1)             |
| Search     | O(h)            | O(1)             |
| BFS        | O(n)            | O(n)             |
| Pre-order  | O(n)            | O(n)             |
| In-order   | O(n)            | O(n)             |
| Post-order | O(n)            | O(n)             |

- h is the height of the tree.
- n is the number of nodes in the tree.

## Advantages
- **Efficiency:** Provides efficient search, insertion, and deletion operations, generally O(log n) in balanced BSTs.
- **Ordered Structure:** Maintains elements in a sorted order, making in-order traversal straightforward for sorting and retrieval.

## Disadvantages
- **Unbalanced Trees:** Performance can degrade to O(n) in the worst case if the tree becomes unbalanced (e.g., skewed tree).
- **Complex Operations:** Requires balancing mechanisms (e.g., AVL trees, Red-Black trees) to maintain efficient operations in practice.

