+++
title= "Key Value Store"
tags = [ "system-design", "software-architecture", "interview", "key-value-store" ]
author = "Me"
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight= 5
bookFlatSection= true
+++

# Key-Value Store

Key-value stores are a type of non-relational databases:
- Each unique identifier is stored as a key with a value associated with it.
- Keys must be unique and can be plain text or hashes.
- Performance-wise, shorter keys work better.

Example keys:
- Plain-text: `"last_logged_in_at"`
- Hashed key: `253DDEC4`

We're now about to design a key-value store that supports:
- `put(key, value)` - inserts a `value` associated with a `key`.
- `get(key)` - retrieves the `value` associated with the `key`.

## Understand the Problem and Establish Design Scope

There is always a trade-off between read/write performance and memory usage, as well as between consistency and availability.

Key characteristics to achieve:
- Small key-value pair size (10kb).
- Capability to store a large amount of data.
- High availability: system responds quickly, even during failures.
- High scalability: system can support large data sets.
- Automatic scaling: addition/deletion of servers happens automatically based on traffic.
- Tunable consistency.
- Low latency.

## Single Server Key-Value Store

A single-server key-value store is easy to develop. We can maintain an in-memory hash map to store key-value pairs.

However, memory can be a bottleneck as not everything can fit in memory. To scale, we can:
- Compress data.
- Store frequently used data in-memory, with the rest stored on disk.

Even with optimizations, a single server can reach its capacity quickly.

## Distributed Key-Value Store

A distributed key-value store uses a distributed hash table to spread keys across many nodes.

When developing a distributed data store, the **CAP Theorem** is important to consider:

### CAP Theorem

The CAP theorem states that a data store cannot provide more than two of the following guarantees:
1. **Consistency**: All clients see the same data at the same time, regardless of which node they're connected to.
2. **Availability**: Every request receives a response, regardless of node connection.
3. **Partition tolerance**: The system remains operational despite a network partition (where some nodes cannot communicate).

![CAP Theorem](../images/cap-theorem.png)

In an ideal world, consistency and availability can coexist. In the real world, however, network failures make partition tolerance a necessity.

- If we prioritize **consistency**, all write operations block during a network partition.
- If we prioritize **availability**, the system continues to accept reads/writes, risking some clients receiving stale data.

What to prioritize is something to clarify with the interviewer, as each option has its own trade-offs.

## System Components

### Data Partitioning

For a large enough data set, it's impractical to maintain it on a single server. We can split the data into partitions and distribute them across multiple nodes.

Challenges:
- Distribute data evenly.
- Minimize data movement when resizing the cluster.

**Consistent hashing** addresses these problems:
- Servers are mapped to a hash ring.
- Keys are hashed and assigned to the closest server in a clockwise direction.

![Consistent Hashing](../images/consistent-hashing.png)

Advantages:
- Automatic scaling: Servers can be added/removed with minimal impact.
- Heterogeneity: Servers with higher capacity are assigned more virtual nodes.

### Data Replication

To ensure high availability and reliability, data is replicated across multiple nodes.

Replication is achieved by assigning a key to multiple nodes on the hash ring.

![Data Replication](../images/data-replication.png)

Care must be taken to avoid assigning replicas to virtual nodes mapped to the same physical node.

Data should also be replicated across multiple data centers for additional resilience.

### Consistency

Since data is replicated, it must be synchronized. **Quorum consensus** ensures consistency for both reads and writes:
- **N**: number of replicas.
- **W**: write quorum, i.e., the number of nodes required to acknowledge a write.
- **R**: read quorum, i.e., the number of nodes required to acknowledge a read.

![Write Quorum Example](../images/write-quorum-example.png)

Configuration of `W` and `R` is a trade-off between latency and consistency:
- **W = 1, R = 1** → Low latency, eventual consistency.
- **W + R > N** → Strong consistency, higher latency.

Other configurations:
- **R = 1, W = N** → Strong consistency, fast reads, slow writes.
- **R = N, W = 1** → Strong consistency, fast writes, slow reads.

### Consistency Models

Different consistency models can be tuned for:
- **Strong consistency**: Reads always return the most up-to-date data.
- **Weak consistency**: Reads might not see the most recent updates.
- **Eventual consistency**: Reads may return stale data, but eventually, all replicas converge to the latest state.

In most distributed systems, **eventual consistency** is preferred, as it allows for high availability while the system gradually converges to the latest state (e.g., DynamoDB and Cassandra).

### Inconsistency Resolution: Versioning

Replication can lead to data inconsistencies across replicas. This can be resolved using **vector clocks** to track version changes.

Example:
![Inconsistency Example](../images/inconsistency-example.png)

Using versioning with vector clocks, conflicts can be detected and resolved. While this increases complexity, it ensures data consistency in distributed systems.

## Handling Failures

Failures are inevitable in distributed systems. It is essential to define error detection and recovery strategies.

### Failure Detection

Distributed systems cannot assume a node is down just because it isn't responding. A **gossip protocol** can be used for decentralized failure detection.

![Gossip Protocol](../images/gossip-protocol.png)

Each node periodically shares its heartbeat with random nodes, which propagate it further. If a heartbeat isn't received for a threshold period, the node is marked as offline.

### Handling Temporary Failures

- **Hinted handoff** is used to maintain availability during temporary failures.
- When a server (node) fails temporarily:
  - A healthy server takes over and stores incoming writes meant for the failed server as **hints**.
  - The healthy server keeps these hints locally until the failed server recovers.
- Once the failed server is back online:
  - The healthy server forwards the stored hints to the recovered server.
  - The recovered server applies these hints to ensure no data is lost.
- This approach ensures continued write operations and data availability during short outages.

### Handling Permanent Failures

- For permanent failures or inconsistent replicas, an **anti-entropy protocol** using **Merkle trees** ensures data consistency.
- **Merkle trees** work as follows:
  - Each node stores a hash-based binary tree where leaf nodes represent hashes of individual data blocks.
  - Parent nodes contain hashes of their child nodes, culminating in a **root hash** that summarizes all data in the tree.
- To compare data across nodes:
  - Nodes first exchange their root hashes.
  - If the root hashes match, the data is identical.
  - If the root hashes differ, nodes recursively compare child nodes to identify the specific data blocks that differ.
- This method minimizes data transfer by synchronizing only the differing data blocks, ensuring efficient and consistent data replication.


![Merkle Tree](../images/merkle-tree.png)

### Handling Data Center Outage

To ensure resiliency, replicate data across multiple data centers, protecting against catastrophic failures like natural disasters.

## System Architecture

![Architecture Diagram](../images/architecture-diagram.png)

Key features:
- Clients communicate via a simple API.
- A coordinator serves as a proxy between clients and the key-value store.
- Nodes are distributed using consistent hashing.
- The system is decentralized, enabling dynamic scaling and data replication.
- There is no single point of failure.

Each node handles:
![Node Responsibilities](../images/node-responsibilities.png)

### Write Path

![Write Path](../images/write-pth.png)
- Write requests are first recorded in a **commit log**, ensuring durability.
- The data is then stored in a **memory cache** (usually a memtable) for faster access.
- Once the memory cache reaches a threshold, data is flushed to disk as an **SSTable** (Sorted String Table). SSTables are immutable and optimized for fast reads.

### Read Path

#### When Data is in Memory:
![Read Path in Memory](../images/read-path-in-memory.png)
- If the requested data is in the **memory cache**, it is retrieved directly from the cache (memtable), providing very fast reads.

#### When Data is Not in Memory:
![Read Path Not in Memory](../images/read-path-not-in-memory.png)
- If the data is not found in memory, the system checks the **SSTables** stored on disk.
- **Bloom filters** are employed to quickly determine if a given key **might** exist in the SSTable. Bloom filters are probabilistic data structures that can say "the data might be here" or "the data is definitely not here." This reduces the number of unnecessary disk reads.
- Once located via the bloom filter, the SSTable is scanned to retrieve the exact record. Since SSTables are sorted by keys, retrieval is efficient.

### Additional Details:
- **Sorted String Table (SSTable):** SSTables are on-disk data structures where keys are stored in sorted order, which allows for efficient searches and merging during compaction.
- **Bloom Filters:** By using bloom filters, the system avoids reading from disk when the key is guaranteed to not exist in an SSTable. This reduces the number of I/O operations, speeding up the read process.

## Summary

Here's a recap of key goals and techniques:

| Goal/Problems               | Technique                                             |
| --------------------------- | ----------------------------------------------------- |
| Ability to store big data   | Use consistent hashing to spread load across servers  |
| High availability for reads | Data replication, multi-data center setup             |
| Highly available writes     | Versioning and conflict resolution with vector clocks |
| Dataset partitioning        | Consistent hashing                                    |
| Incremental scalability     | Consistent hashing                                    |
| Heterogeneity               | Consistent hashing                                    |
| Tunable consistency         | Quorum consensus                                      |
| Handling temporary failures | Sloppy quorum and hinted handoff                      |
| Handling permanent failures | Merkle trees                                          |
| Handling data center outage | Cross-data center replication                         |
