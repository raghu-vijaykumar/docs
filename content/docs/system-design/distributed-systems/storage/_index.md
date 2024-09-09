+++
title= "Storage"
tags = [ "system-design", "software-architecture", "distributed-systems", "storage" ]
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

# Distributed Storage and Databases

In distributed systems, a key component is the storage layer where both temporary and long-term business data are stored. This document explores the importance of distributed storage, available storage options, and the fundamentals of distributed databases.

## Storage Options

### Filesystem Storage

- **Description**: A lower-level, general-purpose method for storing data of any format, structure, or size.
- **Use Cases**: Suitable for unstructured data like video, audio, and text files.
- **Characteristics**: Allows for the storage of any type of data but lacks advanced data management capabilities.

### Database Storage

- **Description**: A higher-level abstraction that may utilize the filesystem for underlying storage but provides additional functionalities.
- **Capabilities**:
  - **Query Language**: Enables complex queries and data manipulation.
  - **Caching and Optimization**: Enhances performance based on data structure.
  - **Data Constraints**: Enforces data structure, relationships, and format.
  - **ACID Transactions**: Ensures atomicity, consistency, isolation, and durability of transactions.

## Types of Databases

### Relational Databases

- **Structure**: Data is organized in tables with rows and columns.
- **Schema**: Predefined structure allows for SQL queries and data analysis.
- **Primary Key**: Uniquely identifies each record in a table.

### Non-Relational Databases

- **Structure**: Data can be key-value pairs, documents, or graphs, and does not require a fixed schema.
- **Flexibility**: Records are independent, facilitating easier scaling compared to relational databases.
- **Types**:
  - **Key-Value Stores**
  - **Document Stores**
  - **Graph Databases**

## Challenges with Centralized Databases

- **Single Point of Failure**: Losing a centralized database instance risks data loss and affects business continuity.
- **Performance Bottleneck**: Limited by the machine's capabilities (CPU, connections, network card, and memory).
- **Latency**: Performance can be impacted by the geographical location of the data source.

## Benefits of Distributed Databases

- **Availability**: Distributing data across multiple nodes ensures system availability even if individual nodes fail.
- **Scalability**: Enables horizontal scaling by adding more nodes to handle increased load.
- **Fault-Tolerance**: Enhances reliability by replicating data and distributing it to prevent data loss and ensure continuous operation.

## Summary

- **Motivation**: The need for distributed databases arises from the limitations of centralized systems, including single points of failure, performance bottlenecks, and latency issues.
- **Focus**: Understanding the types of storage and databases, and the importance of having a reliable storage solution to mitigate risks associated with data loss.

In upcoming sections, we will delve deeper into distributed databases, exploring the challenges and solutions in creating scalable and fault-tolerant storage systems.
