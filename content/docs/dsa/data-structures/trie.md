---
weight: 6
bookFlatSection: true
title: "Trie"
draft: false
---

# Trie

A Trie (pronounced as "try") is a type of search tree, also known as a prefix tree or digital tree, used to store a dynamic set or associative array where the keys are usually strings. Tries are especially useful for situations involving prefix-based queries like searching, insertion, and deletion of strings, which makes them suitable for tasks such as autocomplete or spell-checking.

## Characteristics of a Trie:
- **Nodes**: Each node in the Trie represents a character of the stored strings.
- **Edges**: Each edge represents the path from one character to the next.
- **Root Node**: The root node is always empty and represents the starting point of all strings.
- **Leaf Nodes**: These indicate the end of a valid string in the Trie.
- **Efficiency**: Time complexity for search, insert, and delete operations is proportional to the length of the key (O(m), where m is the length of the word).
- **Space Usage**: While tries can use more space than a binary search tree (due to pointers in each node), they provide fast lookups and memory optimization through common prefixes.

## Trie Node Structure
A basic structure for a Trie node in pseudocode might look like this:

```java
class TrieNode {
    Map<Character, TrieNode> children;  // Stores child nodes for each character
    boolean isEndOfWord;                // Marks if this node represents the end of a word

    TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}
```

## Basic Trie Operations
### Insertion
To insert a string into a Trie, we follow the characters of the string from the root, traversing through nodes representing each character. If a node for a character doesn't exist, a new node is created. After processing all characters, the final node is marked as the end of the word.

Example: Inserting the words cat and can into a Trie.

```css
        root
       /    \
      c      ...
     /      
    a        
   / \
  t   n
```
Java Code for Insertion:
```java
    // Insert a word into the Trie
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }
```
In this code:

- The `insert()` method adds a word to the Trie, creating new nodes if necessary.
- After the last character is inserted, the node is marked as `isEndOfWord = true` to denote the end of the word.

### Search
To search for a word in a Trie, we start from the root and move through the Trie by following the edges that match the characters of the word. If all characters are found and the final node is marked as the end of a word, the search is successful.

Example: To search for the word can:
- Start at the root, follow the c edge, then a, then n.
- If the node for n is marked as the end of the word, the word exists in the Trie.

```java
    // Search for a word in the Trie
    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false; // Word not found
            }
            current = node;
        }
        return current.isEndOfWord; // Return true if it's the end of a valid word
    }
```

Explanation:
- The `search()` method looks for a word in the Trie by iterating through each character.
- If any character is missing in the Trie, the method returns `false`.
- If all characters are found and the node representing the last character is marked as `isEndOfWord`, the word exists in the Trie, and the method returns `true`.

## Deletion
To delete a word, we first need to confirm that the word exists in the Trie. Then, we remove nodes starting from the end of the word, but only if the node does not have any other children (i.e., it is not part of another word).

Example: Deleting the word can from the Trie leaves cat intact.

```css
        root
       /
      c
     /
    a        
   /
  t  
```
Java Code for Deletion:

```java
    // Delete a word from the Trie
    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            // If the end of the word is reached
            if (!current.isEndOfWord) {
                return false; // Word doesn't exist
            }
            current.isEndOfWord = false; // Unmark the end of the word

            // If the node has no children, we can delete this node
            return current.children.isEmpty();
        }

        char ch = word.charAt(index);
        TrieNode node = current.children.get(ch);
        if (node == null) {
            return false; // Word doesn't exist
        }

        boolean shouldDeleteCurrentNode = delete(node, word, index + 1);

        // If the child node should be deleted
        if (shouldDeleteCurrentNode) {
            current.children.remove(ch);

            // If the current node is not the end of another word and has no children, it should be deleted
            return current.children.isEmpty() && !current.isEndOfWord;
        }

        return false;
    }
```
Explanation:
- The `delete()` method is a recursive function that traverses the Trie to find the word.
- When it reaches the end of the word, it unmarks `isEndOfWord` and starts deleting nodes if they have no children and are not part of another word.
- If any part of the word is shared with other words (like the prefix ca for both cat and can), it stops deleting beyond that point, ensuring other words are preserved.

4. **Prefix Search**
One of the primary advantages of a Trie is efficient prefix searches. Given a prefix, we can easily retrieve all words that start with that prefix by traversing the tree up to the last character of the prefix, then performing a depth-first search to collect all complete words.

Example: For the prefix ca, we would collect cat, can, etc.


## Time and Space Complexity
| Operation     | Time Complexity | Space Complexity |
| ------------- | --------------- | ---------------- |
| Insertion     | O(m)            | O(m * n)         |
| Search        | O(m)            | O(1)             |
| Deletion      | O(m)            | O(1)             |
| Prefix Search | O(p)            | O(1)             |

- m: Length of the word to insert/search/delete.
- n: Number of words in the Trie.
- p: Length of the prefix.

## Advantages of Trie
- **Fast Lookup**: Trie provides an efficient search, insert, and delete operation with time complexity proportional to the length of the word.
- **Prefix Matching**: The trie structure naturally supports prefix matching, which is highly useful in applications like autocomplete.
- **Memory Optimization**: It optimizes memory usage by storing common prefixes only once.
## Disadvantages of Trie
- **Space Complexity**: Tries can use a significant amount of memory compared to other data structures like hash tables, especially when many words have unique prefixes.
**Construction Time**: Building a trie can be slower than other structures, especially for a large dataset.


## Applications of Trie
- **Autocomplete**: Tries are widely used in autocomplete functionality to suggest possible word completions based on a prefix.
- **Spell Checking**: Searching for words in a Trie is fast, making it useful for spell checkers.
- **IP Routing**: Tries are used in network routers for storing IP addresses due to their hierarchical nature.
- **Word Games**: Games like Boggle and Scrabble use tries to validate words quickly.
- **Search Engines**: Used for keyword matching and search suggestion features.

## Conclusion

The Trie is a powerful data structure that offers an efficient way to store and search for strings, especially in scenarios that involve prefix matching or dictionary-related operations. Despite its higher memory usage compared to hash-based structures, the trade-off is worth it for tasks where speed and prefix operations are important.