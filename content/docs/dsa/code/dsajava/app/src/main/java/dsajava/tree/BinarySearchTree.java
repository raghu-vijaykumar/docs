package dsajava.tree;

public class BinarySearchTree {

    Node root;

    public class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    public boolean insert(int value) {
        Node node = new Node(value);
        if (root == null) {
            root = node;
            return true;
        }

        Node temp = root;
        while (true) {
            if (temp.value == value) {
                return false;
            }

            if (value < temp.value) {
                if (temp.left == null) {
                    temp.left = node;
                    return true;
                } else {
                    temp = temp.left;
                }
            }

            else {
                if (temp.right == null) {
                    temp.right = node;
                    return true;
                } else {
                    temp = temp.right;
                }
            }

        }
    }

    public boolean contains(int value) {
        Node temp = root;
        while (temp != null) {
            if (temp.value == value)
                return true;
            if (value < temp.value)
                temp = temp.left;
            else
                temp = temp.right;
        }
        return false;
    }

}
