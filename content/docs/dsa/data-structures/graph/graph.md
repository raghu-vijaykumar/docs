---
weight: 5
bookFlatSection: true
title: "Graph"
draft: false
---


# Graph 

A graph is a fundamental data structure used in computer science to model and solve problems involving relationships between entities. It consists of a set of vertices (or nodes) and a set of edges (or links) that connect pairs of vertices. Graphs are versatile and can represent various types of real-world scenarios, such as social networks, transportation systems, and organizational hierarchies.

## Key Components
- **Vertices (Nodes)**: The fundamental units of a graph, representing entities or objects. For example, in a social network graph, each vertex could represent a user.
- **Edges (Links)**: Connections between pairs of vertices. Edges can be directed (one-way) or undirected (two-way). In a social network, an edge might represent a friendship between two users.

## Types of Graphs
- **Directed Graph (Digraph)**: In this graph, edges have a direction, indicating the relationship flows from one vertex to another. For instance, in a website link graph, a directed edge from vertex A to vertex B implies that there is a link from page A to page B.
- **Undirected Graph**: In this graph, edges do not have a direction. An edge between two vertices signifies a mutual relationship. For example, in a graph representing friendships, if A and B are friends, there is an undirected edge between them.
- **Weighted Graph**: Each edge has an associated weight, representing the cost, distance, or any other measure of the relationship. For instance, in a transportation network, weights could represent the distance between two locations.
- **Unweighted Graph**: Edges have no weights; the relationship is simply represented by the presence of an edge between vertices.
- **Cyclic Graph**: Contains at least one cycle, which is a path that starts and ends at the same vertex.
- **Acyclic Graph**: Contains no cycles. A special case is the Directed Acyclic Graph (DAG), where edges are directed and no cycles are present.
- **Connected Graph**: In an undirected graph, a graph is connected if there is a path between any pair of vertices.
- **Disconnected Graph**: A graph in which not all vertices are connected to each other.

## Graph Representation
Graphs can be represented using various data structures, depending on the needs of the application:

- **Adjacency Matrix**: A 2D array where the entry at [i][j] represents the presence or weight of an edge between vertex i and vertex j.
- **Adjacency List**: A list where each vertex has a list of adjacent vertices. This representation is often used to efficiently store sparse graphs.
- **Edge List**: A list of edges, where each edge is a pair (or tuple) of vertices. This is less commonly used for efficient graph operations but is useful for simple representations.

## Applications of Graphs
Graphs are used in a wide range of applications:

- Social Networks: Modeling relationships and interactions between users.
- Web Crawling: Representing web pages and links between them.
- Route Planning: Finding shortest paths in transportation networks.
- Recommendation Systems: Suggesting items based on user behavior and preferences.
- Network Design: Optimizing and analyzing network connections in various domains.

## Adjacency List vs. Adjacency Matrix
Graphs can be represented in various ways, but two of the most common data structures used for representing graphs are Adjacency Lists and Adjacency Matrices. Both have their own advantages and disadvantages depending on the graph's density and the operations needed.

### Adjacency List
An Adjacency List represents the graph as a list of lists (or a hashmap of lists in Java). Each vertex in the graph has a list of all vertices to which it is directly connected by an edge. For undirected graphs, each edge appears in two lists (one for each vertex), while for directed graphs, each edge appears only in the origin vertex's list.

#### Example
For the following graph:

```css
A -- B
|    |
C -- D
```
The adjacency list would look like:

```css
A -> B, C
B -> A, D
C -> A, D
D -> B, C
```

#### Space Complexity
O(V + E) where V is the number of vertices and E is the number of edges.

#### Time Complexity for Operations
- Adding an Edge: O(1)
- Removing an Edge: O(V) (need to search for the vertex in the list)
- Checking for an Edge: O(V) (need to search for the vertex in the list)

#### When to Use
- When the graph is sparse (few edges compared to the number of vertices).
- When the graph is dynamic, meaning vertices and edges are frequently added or removed.
- When efficient edge traversal is necessary, such as during BFS or DFS.

#### Advantages
- **Space-Efficient**: For sparse graphs, it uses less memory compared to an adjacency matrix.
- **Faster Edge Operations**: Adding edges is efficient.

#### Disadvantages
- **Slow Edge Checking**: Checking for the existence of an edge between two vertices requires linear search within the vertex’s adjacency list, which could be slower for dense graphs.
- **Not Suitable for Dense Graphs**: As graphs become denser, the advantage in space diminishes compared to adjacency matrices.

### Adjacency Matrix
An Adjacency Matrix is a 2D array (matrix) where each row and column represent a vertex. The value at the position [i][j] is 1 if there is an edge between vertex i and vertex j, otherwise, it is 0. For weighted graphs, the value could represent the weight of the edge.

#### Example
For the following graph:

```css
A -- B
|    |
C -- D
```
The adjacency matrix would look like:

```css
    A  B  C  D
A [ 0, 1, 1, 0 ]
B [ 1, 0, 0, 1 ]
C [ 1, 0, 0, 1 ]
D [ 0, 1, 1, 0 ]
```

#### Space Complexity
`O(V²)` where V is the number of vertices. Every possible pair of vertices must be accounted for, even if there is no edge between them.
#### Time Complexity for Operations
- Adding an Edge: `O(1)`
- Removing an Edge: `O(1)`
- Checking for an Edge: `O(1)`

#### When to Use
- When the graph is dense (i.e., the number of edges is close to V²).
- When fast edge lookups are required, e.g., checking if an edge exists between two vertices.
- When graph algorithms benefit from matrix operations, such as in dynamic programming or matrix exponentiation methods.

#### Advantages
- **Fast Edge Checking**: Checking for the presence of an edge between two vertices is a constant-time operation, `O(1)`.
- **Easier for Dense Graphs**: Suitable for dense graphs where the number of edges is large.

#### Disadvantages
- **Space-Inefficient for Sparse Graphs**: Uses `O(V²)` space even if the graph has few edges.
- **Slower for Edge Iteration**: Traversing all edges connected to a vertex requires checking all V columns in the row, even if only a few edges exist.

## Undirected Unweighted Graph

This `UndirectedUnWightedGraph` class is a basic implementation of an undirected, unweighted graph using an adjacency list. The graph is stored as a HashMap, where each key represents a vertex, and the value is a list of neighboring vertices. This class allows for the addition and removal of vertices and edges, ensuring no duplicate vertices, edges, or self-loops. This type of graph is commonly used in algorithms for pathfinding, connectivity, and graph traversal in various domains like networking, geography, and social networks.

### Code and Inline Comments
```java
package dsajava.datastructures.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class UndirectedUnWightedGraph {

    // HashMap to store adjacency list for each vertex.
    // Key is the vertex, and value is a list of connected vertices.
    HashMap<String, ArrayList<String>> adjacencyList = new HashMap<>();

    // Adds a vertex to the graph. Returns false if the vertex already exists.
    public boolean addVertex(String vertex) {
        if (adjacencyList.containsKey(vertex)) // Check if vertex already exists.
            return false;
        adjacencyList.put(vertex, new ArrayList<>()); // Add vertex with an empty adjacency list.
        return true;
    }

    // Adds an undirected edge between two vertices. Returns false if either vertex is not found.
    public boolean addEdge(String vertex1, String vertex2) {
        if (vertex1.equals(vertex2)) // Prevent self-loops.
            return false;
        if (!adjacencyList.containsKey(vertex1) || !adjacencyList.containsKey(vertex2)) // Check if both vertices exist.
            return false;
        adjacencyList.get(vertex1).add(vertex2); // Add vertex2 to vertex1's adjacency list.
        adjacencyList.get(vertex2).add(vertex1); // Add vertex1 to vertex2's adjacency list.
        return true;
    }

    // Removes a vertex and its associated edges from the graph. Returns false if the vertex is not found.
    public boolean removeVertex(String vertex) {
        if (!adjacencyList.containsKey(vertex)) // Check if vertex exists.
            return false;

        // Iterate over the adjacency list of the vertex and remove the edges from each connected vertex.
        for (String otherVertex : adjacencyList.get(vertex)) {
            adjacencyList.get(otherVertex).remove(vertex); // Remove vertex from otherVertex's adjacency list.
        }
        adjacencyList.remove(vertex); // Finally, remove the vertex itself from the adjacency list.
        return true;
    }

    // Removes an undirected edge between two vertices. Returns false if either vertex is not found.
    public boolean removeEdge(String vertex1, String vertex2) {
        if (vertex1.equals(vertex2)) // Prevent self-loops.
            return false;
        if (!adjacencyList.containsKey(vertex1) || !adjacencyList.containsKey(vertex2)) // Check if both vertices exist.
            return false;

        // Check if the edge exists between vertex1 and vertex2.
        if (!adjacencyList.get(vertex1).contains(vertex2) || !adjacencyList.get(vertex2).contains(vertex1)) {
            return false;
        }

        // Remove the edge between vertex1 and vertex2 from their respective adjacency lists.
        adjacencyList.get(vertex1).remove(vertex2);
        adjacencyList.get(vertex2).remove(vertex1);
        return true;
    }

}
```    
### Space and Time Complexity

| Operation      | Time Complexity | Space Complexity | Description                                                                                                                                     |
| -------------- | --------------- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| addVertex()    | `O(1)`          | `O(V)`           | Inserting a new vertex into the adjacency list is a constant-time operation.                                                                    |
| addEdge()      | `O(1)`          | `O(E)`           | Adding an edge involves inserting elements into two lists, which takes constant time.                                                           |
| removeVertex() | `O(V)`          | `O(V + E)`       | Removing a vertex involves iterating over the adjacency list of connected vertices, taking linear time with respect to the number of neighbors. |
| removeEdge()   | `O(1)`          | `O(1)`           | Removing an edge is done by removing the element from two lists, which is a constant-time operation.                                            |

### When Do We Use This?
This graph structure is used when we need to model undirected and unweighted graphs. Common scenarios include:

- **Social networks**: Representing friendships or connections.
- **Roadmaps**: Representing locations and roads between them.
- **Communication networks**: Representing systems where each node is a device, and the edges represent connections.
This structure is typically used for:

- **Graph traversal algorithms**: Such as Depth-First Search (DFS), Breadth-First Search (BFS), and Dijkstra's algorithm.
- **Pathfinding**: Finding paths in an unweighted graph.

### Advantages
- **Space Efficient**: Adjacency lists are space-efficient for sparse graphs where the number of edges is much smaller than the square of the number of vertices.
- **Easy Edge Operations**: Adding or removing an edge is quick and straightforward since it only involves list insertions or deletions.

### Disadvantages
- **Inefficient for Dense Graphs**: For dense graphs (graphs where the number of edges is close to `V^2`), adjacency lists can be inefficient in terms of time, as searching for specific edges requires a linear search through the vertex’s adjacency list.
- **No Weight Support**: This implementation does not support weighted graphs, so it’s unsuitable for applications where edge weights are necessary (e.g., Dijkstra's shortest path algorithm for weighted graphs).