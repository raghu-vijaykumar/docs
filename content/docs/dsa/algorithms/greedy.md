# Greedy Algorithms

Greedy algorithms are a class of algorithms that make decisions step by step, always choosing the best option at each step with the hope of finding an optimal solution at the end. The greedy approach doesn't reconsider decisions once made, which can be a limitation in some cases. However, greedy algorithms are particularly useful for problems where local optimal choices lead to a global optimal solution.

## Key Concepts

1. **Greedy Choice Property**: The algorithm builds a solution by making the locally optimal choice at each step. This property means that a globally optimal solution can be obtained by selecting the best possible option at each step, without needing to go back and reconsider previous decisions.

2. **Optimal Substructure**: A problem exhibits optimal substructure if an optimal solution to the problem contains optimal solutions to its subproblems. This is crucial for greedy algorithms because it ensures that local choices lead to a global solution.

## Greedy Algorithm Design Process

1. **Identify the Greedy Choice**: At each step, decide what the best possible choice is.
2. **Prove that the Greedy Choice is Safe**: Show that choosing the greedy option will not prevent the algorithm from finding the optimal solution.
3. **Develop a Recursive or Iterative Solution**: Implement the solution based on repeated greedy choices.
4. **Prove Optimal Substructure**: Verify that the problem has optimal substructure, meaning that the problem can be solved by breaking it down into smaller problems.

## Examples of Greedy Algorithms

### 1. Activity Selection Problem

Given a set of activities with their start and end times, the goal is to select the maximum number of activities that don't overlap.

**Greedy Strategy**: Always select the activity with the earliest finish time. This choice ensures that more activities can be selected in the remaining time.

**Algorithm**:

- Sort the activities by their finish times.
- Select the first activity and continue selecting the next activity that starts after the previous one finishes.

```java
class ActivitySelection {
    static void selectActivities(int[] start, int[] end, int n) {
        int i = 0;
        System.out.println("Selected activities: " + i);

        for (int j = 1; j < n; j++) {
            if (start[j] >= end[i]) {
                System.out.println("Selected activities: " + j);
                i = j;
            }
        }
    }
}
```

### 2. Fractional Knapsack Problem

Given a set of items, each with a weight and a value, the goal is to maximize the total value in the knapsack. The knapsack can carry fractional parts of items.

Greedy Strategy: Select items based on the highest value-to-weight ratio and take as much as possible until the knapsack is full.

Algorithm:

- Calculate the value-to-weight ratio for each item.
- Sort items by this ratio.
- Take as much of each item as possible until the knapsack is full.

```java
class KnapsackItem {
    int weight, value;
    double ratio;

    public KnapsackItem(int weight, int value) {
        this.weight = weight;
        this.value = value;
        this.ratio = (double) value / weight;
    }
}

class FractionalKnapsack {
    public static double getMaxValue(int W, KnapsackItem[] items) {
        Arrays.sort(items, Comparator.comparingDouble(i -> -i.ratio));
        double maxValue = 0;

        for (KnapsackItem item : items) {
            if (W >= item.weight) {
                W -= item.weight;
                maxValue += item.value;
            } else {
                maxValue += item.ratio * W;
                break;
            }
        }
        return maxValue;
    }
}
```

### 3. Huffman Coding

Huffman Coding is a popular algorithm for lossless data compression. The goal is to minimize the total length of the encoded message by using shorter codes for more frequent characters.

Greedy Strategy: Combine the two least frequent characters at each step to build the Huffman tree.

Algorithm:

- Create a priority queue where each character's frequency is a node.
- Remove the two nodes with the smallest frequencies and combine them into a new node.
- Repeat the process until one node is left. This node is the root of the Huffman tree.

```java
class HuffmanNode {
    int frequency;
    char data;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }
}

class HuffmanCoding {
    public static HuffmanNode buildTree(char[] charArray, int[] charFreq) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));

        for (int i = 0; i < charArray.length; i++) {
            queue.add(new HuffmanNode(charArray[i], charFreq[i]));
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            HuffmanNode newNode = new HuffmanNode('-', left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            queue.add(newNode);
        }

        return queue.poll();
    }
}
```

## When to Use Greedy Algorithms

Greedy algorithms are best used when the problem exhibits both the greedy choice property and optimal substructure. They are especially useful for problems where:

- Local decisions lead to globally optimal solutions.
- There's a need for efficiency and simplicity over complex, exhaustive search methods like dynamic programming.

## Advantages of Greedy Algorithms

- **Efficiency**: Greedy algorithms typically run in O(n log n) time, where sorting or other selection processes are required. This makes them faster compared to dynamic programming solutions.
- **Simplicity**: Greedy algorithms are often easier to implement than more complex algorithms like dynamic programming.
- **Memory Usage**: They use less memory compared to other approaches like dynamic programming that require memoization tables or recursion.

## Limitations of Greedy Algorithms

- **Non-optimal for All Problems**: Greedy algorithms do not always guarantee an optimal solution. In some problems, local choices do not lead to the best global solution.
- **Proof of Correctness Required**: It is necessary to prove that the greedy choice leads to the correct and optimal solution for a particular problem. Not all problems are suited for greedy solutions.

## Common Problems Solved by Greedy Algorithms

- **Job Scheduling**: Maximizing the number of non-overlapping jobs.
- **Minimum Spanning Tree (MST)**: Using algorithms like Prim’s and Kruskal’s to find the MST of a graph.
- **Dijkstra's Algorithm**: Finding the shortest path from a source to all other nodes in a weighted graph.
- **Set Cover Problem**: Selecting a minimum number of sets such that their union covers all elements.

## Conclusion

Greedy algorithms provide an elegant and efficient way to solve optimization problems when the problem adheres to the greedy choice property and optimal substructure. While they do not work for every problem, they are invaluable tools in cases where they can lead to correct and optimal solutions, making them highly useful in a variety of applications such as scheduling, compression, graph algorithms, and more.
