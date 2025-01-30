+++
title = "Databases"
tags = [ "database" ]
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
+++

# Databases

{{< markmap >}}

```markmap
# Database Internals
  - **Database Architecture**
    - Database Management Systems (DBMS) Overview
    - Components of DBMS
      - Query Processor
      - Storage Manager
      - Transaction Manager
    - Client-Server Architecture
    - Multi-Tier Architecture

  - **Storage Structures**
    - Data Files & Tables
      - Heap Files
      - Indexed Files
      - Clustered Files
    - Data Pages & Blocks
    - Record Storage & Layout
    - Variable-Length vs Fixed-Length Records

  - **Indexing**
    - Types of Indexes
      - Single-Level vs Multi-Level Indexes
      - B-Tree, B+Tree, & AVL Tree Indexes
      - Hash Indexes
      - Bitmap Indexes
      - Full-text Indexes
    - Index Maintenance & Updates
    - Sparse vs Dense Indexes

  - **Query Execution**
    - Query Parsing & Optimization
      - Query Parsing (Lexical, Syntax, Semantic Analysis)
      - Query Optimization (Cost-based vs Rule-based Optimization)
      - Execution Plan Generation
    - Join Algorithms
      - Nested Loop Join
      - Merge Join
      - Hash Join
    - Query Execution Plan (QEP) & Cost Estimation

  - **Transactions**
    - ACID Properties (Atomicity, Consistency, Isolation, Durability)
    - Isolation Levels (Read Uncommitted, Read Committed, Repeatable Read, Serializable)
    - Transaction Logs (Write-Ahead Logging, Log-Based Recovery)
    - Locking Mechanisms (Pessimistic vs Optimistic Locking)
    - Deadlock Detection & Resolution
    - Two-Phase Locking (2PL)

  - **Concurrency Control**
    - Locking Protocols
      - Shared and Exclusive Locks
      - Lock Granularity (Row-level, Page-level, Table-level)
    - Timestamp Ordering Protocol
    - Optimistic Concurrency Control
    - Serializable Scheduling & Views
    - Multi-Version Concurrency Control (MVCC)

  - **Database Caching**
    - Buffer Pool Management
    - Cache Replacement Algorithms (LRU, MRU, LFU)
    - Write-Through vs Write-Back Caching
    - Prefetching Data
    - Read-Ahead & Write-Ahead Techniques

  - **Data Integrity & Constraints**
    - Entity Integrity
    - Referential Integrity
    - Domain Constraints
    - Foreign Key Constraints
    - Check Constraints
    - Trigger Mechanisms

  - **Data Recovery**
    - Backup & Restore
      - Full, Incremental, and Differential Backups
    - Recovery Techniques
      - Log-Based Recovery
      - Shadow Paging
      - Checkpointing
    - Point-in-Time Recovery

  - **Distributed Databases**
    - Data Distribution & Replication
      - Horizontal Partitioning (Sharding)
      - Vertical Partitioning
      - Data Replication (Master-Slave, Multi-Master)
    - Distributed Query Processing
    - Distributed Transactions & Two-Phase Commit
    - CAP Theorem in Distributed Databases

  - **NoSQL Databases**
    - Key-Value Stores (Redis, DynamoDB)
    - Document Stores (MongoDB, CouchDB)
    - Columnar Stores (Cassandra, HBase)
    - Graph Databases (Neo4j, ArangoDB)
    - Eventual Consistency vs Strong Consistency

  - **Performance Tuning**
    - Query Optimization Techniques
      - Index Usage & Optimization
      - Join Ordering & Optimization
      - Query Rewrite Rules
    - Database Design for Performance
      - Normalization & Denormalization
      - Partitioning & Sharding
    - Analyzing Execution Plans & Bottlenecks
    - Load Balancing & Replication for Scaling

  - **Database Security**
    - User Authentication & Authorization
    - SQL Injection & Mitigation
    - Encryption (Data-at-Rest, Data-in-Transit)
    - Database Auditing & Access Control
    - Role-Based Access Control (RBAC)

```

{{< /markmap >}}
