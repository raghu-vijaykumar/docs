+++
title= "Sharding"
tags = [ "system-design", "software-architecture", "distributed-systems", "storage", "sharding" ]
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
weight= 2
bookCollapseSection= true
+++

# Database Sharding

## Overview

Sharding is a database partitioning technique that divides a large dataset into smaller, more manageable pieces called shards. This approach enables horizontal scaling, reduces latency, and improves throughput by distributing data across multiple database instances.

## Sharding Basics

- **Definition**: Sharding involves splitting a large database into smaller chunks, which are then distributed across different machines.
- **Benefits**:
  - **Lower Latency**: Smaller datasets can fit into memory, speeding up data access.
  - **Higher Throughput**: Parallelism is achieved as transactions operate on different shards.
  - **Scalability**: Adding more database instances allows for horizontal scaling as data volume grows.
  - **Availability**: The failure of one shard does not affect others, ensuring continued data access.

## Sharding Methods

### SQL Databases

1. **Vertical Sharding**:
   - **Description**: Splits a table into smaller tables, each containing fewer columns.
   - **Use Case**: Useful for scaling by distributing the table's columns.

2. **Horizontal Sharding**:
   - **Description**: Divides a table's rows into separate chunks, each residing on different shards.
   - **Use Case**: More adaptable and scalable compared to vertical sharding.

### NoSQL Databases

- **Description**: Records are naturally disjoint, allowing for straightforward division into shards.
- **Implementation**: Groups of records are placed in different shards based on their characteristics.

## Sharding Strategies

1. **Hash-Based Sharding**:
   - **Description**: Applies a hash function to the record's key to determine the shard.
   - **Example**: With a hash function and modulus operation, user IDs are distributed across shards.
   - **Advantages**: Even distribution of records with good hash functions.
   - **Disadvantages**: Range-based queries may require multiple shards due to non-sequential key distribution.

2. **Range-Based Sharding**:
   - **Description**: Divides the key space into continuous ranges, assigning records within a range to the same shard.
   - **Example**: User records are distributed based on alphabetical ranges of names.
   - **Advantages**: Efficient range-based queries as records with similar keys are in the same shard.
   - **Disadvantages**: Uneven data distribution if the key space is not well balanced.

## Challenges and Disadvantages

- **Complex Operations**: Operations involving records across multiple shards become complex, requiring distributed transactions or locks.
- **Concurrency Control**: Maintaining data consistency and handling concurrency is more difficult and costly in a sharded setup.
- **Performance Penalties**: Distributed locks and transactions introduce performance overheads.
- **Relational vs. NoSQL Databases**: NoSQL databases are more suited for sharding compared to relational databases.


## Consistent Hashing for Sharding

Consistent hashing is a sophisticated algorithm designed to address the limitations of traditional hash-based sharding strategies in distributed databases. It enhances the scalability and flexibility of distributed systems by minimizing the impact of adding or removing nodes and addressing capacity imbalances.

### Issues with Hash-Based Sharding

1. **Rebalancing on Node Addition/Removal**:
   - **Problem**: When adding or removing nodes, the hash-based strategy requires reshuffling a significant portion of the records. This results in extensive data movement and potential downtime as records are redistributed across the new set of nodes.
   - **Impact**: All records must be reassigned based on the updated hash formula, leading to increased operational complexity and potential performance degradation.

2. **Capacity Imbalance**:
   - **Problem**: Standard hashing does not account for differences in node capacities (e.g., CPU, memory). This can result in uneven data distribution, where more powerful nodes are underutilized while weaker nodes are overloaded.
   - **Impact**: Inefficient utilization of resources and potential bottlenecks in the system.

### Consistent Hashing Algorithm

Consistent hashing improves upon traditional hashing methods by mapping both data records and nodes to a shared hash space, structured as a continuous ring.

#### Key Concepts

1. **Hashing Nodes and Records**:
   - **Description**: Nodes and records are hashed to the same continuous hash space (ring). Each node and record is assigned a position on this ring.
   - **Assignment**: A record is assigned to the node whose position on the ring follows immediately after the record's position.

2. **Dynamic Node Management**:
   - **Adding Nodes**: When adding a new node, only a portion of the records needs to be redistributed. The records that fall between the new node’s position and the previous node’s position on the ring are moved to the new node.
   - **Removing Nodes**: To remove a node, its records are reassigned to the adjacent node on the ring, minimizing data movement.

3. **Addressing Capacity Imbalances**:
   - **Virtual Nodes**: Nodes are mapped to multiple virtual nodes based on their capacities. More powerful nodes are assigned multiple virtual nodes, while less capable nodes are assigned fewer virtual nodes.
   - **Example**: A powerful node might be mapped to three virtual nodes, while a weaker node is mapped to only one, helping to balance the load according to node capabilities.

4. **Load Distribution**:
   - **Problem**: A single hash function may lead to uneven load distribution, where some nodes handle disproportionately more keys.
   - **Solution**: Using multiple hash functions to map nodes to multiple positions on the ring. This technique distributes the keys more evenly across nodes, reducing the likelihood of bottlenecks.

### Benefits of Consistent Hashing

- **Scalability**: Facilitates easy scaling of the database by adding or removing nodes with minimal impact on existing records.
- **Flexibility**: Allows for dynamic adjustments to node capacities and efficient handling of varying hardware capabilities.
- **Load Balancing**: Using multiple hash functions improves load distribution and prevents performance bottlenecks.

### Summary

Consistent hashing is a powerful algorithm for managing data distribution in sharded databases. It addresses the limitations of traditional hash-based sharding by providing mechanisms for dynamic scaling, balancing node capacities, and improving load distribution. This approach is essential for maintaining performance and efficiency in large-scale distributed systems.

