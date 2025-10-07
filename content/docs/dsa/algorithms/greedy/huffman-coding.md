---
weight: 1
bookFlatSection: true
title: "Huffman Coding"
draft: false
---

# Huffman Coding

{{< markmap >}}

```markmap
# Huffman Coding
- **Algorithm Overview**
  - Greedy approach for optimal prefix codes
  - Builds binary tree based on frequency
  - Variable-length codes (shorter for frequent chars)
- **Applications**
  - File compression (ZIP, JPEG, MP3)
  - Data transmission optimization
  - Text encoding
- **Time Complexity**
  - O(n log n) for tree construction
  - O(n) for encoding/decoding
- **Space Complexity**
  - O(n) for frequency map and codes
```

{{< /markmap >}}

## Introduction to Huffman Coding

Huffman coding is a greedy algorithm that constructs an optimal prefix code for data compression. It assigns shorter codes to more frequent characters, achieving better compression than fixed-length codes.

### Why Huffman Coding?

- **Optimal Compression**: Produces shortest possible average code length
- **Prefix-Free**: No code is prefix of another (unambiguous decoding)
- **Adaptive**: Builds tree based on actual data frequency

## Core Concept

### Frequency-Based Encoding
Instead of using 8 bits per character (ASCII), use fewer bits for frequent characters and more bits for rare ones.

Example:
- Character "a" appears 10 times → 2 bits
- Character "z" appears 1 time → 5 bits
- Average bits per character < 8

## Algorithm Steps

```java
public class HuffmanCoding {

    // Node class for Huffman Tree
    static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left, right;

        Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
            this.left = this.right = null;
        }

        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }

    public static Node buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Create leaf nodes for each character
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Build the Huffman Tree
        while (pq.size() > 1) {
            // Remove two nodes with smallest frequencies
            Node left = pq.poll();
            Node right = pq.poll();

            // Create internal node with sum frequency
            Node internal = new Node('\0', left.frequency + right.frequency);
            internal.left = left;
            internal.right = right;

            // Add back to priority queue
            pq.add(internal);
        }

        // Root of Huffman Tree
        return pq.poll();
    }

    // Generate Huffman codes by traversing the tree
    public static void generateCodes(Node root, String code, Map<Character, String> huffmanCode) {
        if (root == null) return;

        // Leaf node
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.character, code);
            return;
        }

        // Traverse left (add '0')
        generateCodes(root.left, code + "0", huffmanCode);

        // Traverse right (add '1')
        generateCodes(root.right, code + "1", huffmanCode);
    }
}
```

## Example Walkthrough

### Input Text: "ABRACADABRA"

Frequency:
- A: 5
- B: 2
- R: 2
- C: 1
- D: 1

### Huffman Tree Construction

1. Initial priority queue: A(5), B(2), R(2), C(1), D(1)
2. Combine C(1) + D(1) → CD(2)
3. Queue: A(5), B(2), R(2), CD(2)
4. Combine B(2) + CD(2) → BCD(4)
5. Queue: A(5), R(2), BCD(4)
6. Combine R(2) + BCD(4) → RBCD(6)
7. Queue: A(5), RBCD(6)
8. Combine A(5) + RBCD(6) → Root(11)

### Generated Codes
- A: 0
- B: 100
- R: 101
- C: 110
- D: 111

### Compression
Original: ABRACADABRA (11 chars × 8 bits = 88 bits)
Compressed: 0 100 101 0 110 0 111 0 100 101 0
Bits: 1+3+3+1+3+1+3+1+3+3+1 = 24 bits (72% compression)

## Encoding and Decoding

### Encoding Text
```java
public static String encode(String text, Map<Character, String> huffmanCode) {
    StringBuilder encoded = new StringBuilder();

    for (char c : text.toCharArray()) {
        encoded.append(huffmanCode.get(c));
    }

    return encoded.toString();
}
```

### Decoding Compressed Data
```java
public static String decode(String encoded, Node root) {
    StringBuilder decoded = new StringBuilder();
    Node current = root;

    for (char bit : encoded.toCharArray()) {
        if (bit == '0') {
            current = current.left;
        } else {
            current = current.right;
        }

        // Reached leaf node
        if (current.left == null && current.right == null) {
            decoded.append(current.character);
            current = root; // Reset for next character
        }
    }

    return decoded.toString();
}
```

## Time Complexity Analysis

| Operation                | Time Complexity | Explanation                                      |
| ------------------------ | --------------- | ------------------------------------------------ |
| **Build Priority Queue** | O(n)            | Insert n characters                              |
| **Build Tree**           | O(n log n)      | Priority queue operations                        |
| **Generate Codes**       | O(n)            | Traverse all nodes                               |
| **Encoding**             | O(m)            | Process each character in text (m = text length) |
| **Decoding**             | O(m)            | Process each bit                                 |

Where n = number of unique characters, m = text length

## Space Complexity
- **Frequency Map**: O(n)
- **Priority Queue**: O(n)
- **Huffman Tree**: O(n)
- **Huffman Codes**: O(n)

## Properties of Huffman Codes

### 1. Prefix-Free
No code is prefix of another, allowing unambiguous decoding.

### 2. Optimally Efficient
Achieves minimum average code length for given frequencies.

### 3. Uniquely Decodable
The code can be uniquely decoded without separators.

## Variations and Extensions

### Adaptive Huffman Coding
- **Dynamic**: Updates frequencies as characters are processed
- **Better for streaming**: No need for full text analysis first
- **Vitter Algorithm**: More efficient adaptive version

### Huffman Coding with Run-Length Encoding
- Combine with RLE for repeated sequences
- Good for images and binary data

### Canonical Huffman Codes
- Store only code lengths, not full codes
- More compact representation
- Faster to transmit/store

## Applications in Real World

### File Compression
- **ZIP files**: Uses DEFLATE (LZ77 + Huffman)
- **JPEG images**: Huffman coding for DCT coefficients
- **MP3 audio**: Huffman coding for audio data

### Data Transmission
- **HTTP/2**: Huffman coding for header compression
- **Wireless Networks**: Bandwidth optimization

### Database Systems
- **Index Compression**: Compress database indexes
- **Bitmap Indexes**: Huffman coding for sparse bitmaps

## Comparison with Other Compression

| Algorithm             | Huffman | LZW       | Run-Length    | Arithmetic    |
| --------------------- | ------- | --------- | ------------- | ------------- |
| **Type**              | Static  | Adaptive  | RLE           | Probabilistic |
| **Compression Ratio** | Good    | Excellent | Good for runs | Best          |
| **Speed**             | Fast    | Medium    | Fast          | Slow          |
| **Memory Usage**      | Low     | Medium    | Low           | High          |

## Practice Problems

### Easy
- Basic Huffman tree construction and traversal

### Medium
- [1048. Longest String Chain](https://leetcode.com/problems/longest-string-chain/) (Indirect Huffman application)

### Hard
- Design and implement Huffman coding (LeetCode-style problem)

## Implementation Considerations

1. **Priority Queue**: Must be min-heap for frequencies
2. **Character Handling**: Support for extended character sets
3. **Memory Efficiency**: For large alphabets, consider canonical codes
4. **Error Handling**: Invalid encoded data detection

## Key Takeaways

1. **Greedy Choice**: Always combine two lowest frequency nodes
2. **Prefix Property**: Essential for unambiguous decoding
3. **Optimal**: No other prefix code can do better for given frequencies
4. **Build Once, Encode Fast**: Tree construction is O(n log n), encoding is O(m)
5. **Applications**: Fundamental to modern compression algorithms

Huffman coding demonstrates how greedy algorithms can achieve globally optimal solutions through locally optimal choices.
