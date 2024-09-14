+++
title= "DynamoDB"
tags = [ "dynamodb", "aws", "cloud" ]
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
weight= 1
bookCollapseSection= true
+++

# DynamoDB Documentation

Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability. It is designed to handle high-traffic applications with low-latency responses, offering both key-value and document data structures.

## Key Features:

- **Managed NoSQL database**: AWS handles database provisioning, setup, replication, and backups.
- **Automatic scaling**: DynamoDB can automatically adjust throughput capacity based on traffic.
- **Low-latency reads/writes**: Suitable for high-performance, real-time applications.
- **Flexible data model**: Supports key-value pairs and document-based data storage.
- **Global tables**: Multi-region replication for highly available global applications.

## Core Concepts of DynamoDB

1. **Tables**
   A table is a collection of data where each item is uniquely identified by a primary key.

Similar to tables in a relational database but without predefined schema for columns (flexible schema).

**Example:**

```plaintext
Table: Employees
| EmployeeID | Name | Department | Age |
| ---------- | ---- | ---------- | --- |
| 123        | John | Sales      | 34  |
| 456        | Jane | IT         | 29  |
```

2. **Items**

- An item is a single data record in a table, analogous to a row in relational databases.
- Each item is uniquely identifiable using a primary key.
- Items are composed of attributes (name-value pairs).

3. **Attributes**

- Attributes are the individual data elements in an item, like columns in a row.
- They can store different types of data such as strings, numbers, binary data, or sets.

4. **Primary Key**

- The primary key uniquely identifies each item in the table.
- Partition Key (Hash Key): A single attribute used to distribute data across partitions.
- Composite Key: Combines a Partition Key and Sort Key to allow multiple items with the same partition key but different sort keys.

5. **Partitions**

- DynamoDB automatically partitions data across multiple servers to handle large-scale workloads.
- The partition key determines where the data is stored.

### **Indexing in DynamoDB**

DynamoDB supports indexing to enhance query performance.

1. **Global Secondary Index (GSI)**

- Allows querying based on non-primary key attributes.
- Can have different partition keys or sort keys from the base table.

**Example:** You can create a GSI on an "email" attribute to query by email, even if the primary key is based on "userID."

2. **Local Secondary Index (LSI)**

- Allows querying with a different sort key while using the same partition key.
- Must be defined at the time of table creation.

### **Data Access**

1. **Read Operations**
   DynamoDB supports GetItem, Query, and Scan operations for retrieving data.

- **GetItem:** Retrieves a single item by its primary key.
- **Query:** Retrieves multiple items based on partition key and optional sort key conditions.
- **Scan:** Examines all items in the table or a subset based on filters (slower than Query).

2. **Write Operations**
   DynamoDB provides PutItem, UpdateItem, and DeleteItem for writing data.

- **PutItem:** Adds or replaces an item in the table.
- **UpdateItem:** Updates specific attributes of an item.
- **DeleteItem:** Deletes an item from the table.

3. **Condition Expressions**
   Allow you to perform read/write operations only if specified conditions are met.
   Update item only if the current value of an attribute equals "John".

### Performance & Throughput

DynamoDB provides two capacity modes to handle database throughput:

1. **Provisioned Mode**
   You manually specify the number of read and write capacity units (RCUs, WCUs).
   DynamoDB scales according to your defined limits and charges accordingly.
2. **On-Demand Mode**
   DynamoDB automatically adjusts capacity to match your application’s traffic.
   You are only charged for the actual read and write requests, with no need for manual provisioning.
3. **DynamoDB Accelerator (DAX)**
   A fully managed in-memory cache for DynamoDB.
   Improves performance for read-heavy workloads by caching frequently accessed items.
   Provides microsecond response times.

### Data Consistency Models

DynamoDB supports two consistency models for read operations:

1. **Eventually Consistent Reads (default)**
   - Data might not reflect recent write operations immediately but will be consistent eventually.
   - Offers the highest performance and scalability.
2. **Strongly Consistent Reads**
   - Guarantees that the returned data reflects all successful write operations.
   - Can result in slightly higher latency and lower throughput.
3. **Transactions**
   - DynamoDB supports ACID-compliant transactions for multiple read/write operations across one or more tables.

### Key Features:

- Ensures all-or-nothing execution of a series of operations.
- Useful for scenarios like order processing, where atomicity is crucial.

### Security

1. **Access Control**
   - DynamoDB integrates with AWS Identity and Access Management (IAM) to control access to resources.
   - You can define fine-grained permissions at the table, item, or attribute level.
2. **Encryption**
   - Data at rest is automatically encrypted using AWS Key Management Service (KMS).
   - You can also enable encryption in transit using TLS/SSL.

### Backup and Recovery

1. **Point-in-Time Recovery (PITR)**
   - Allows restoring tables to any point within the past 35 days, providing disaster recovery for accidental writes/deletes.
2. **On-Demand Backups**
   - You can create on-demand backups of your tables at any time, retaining them indefinitely.

### Use Cases

- **Real-time Applications**: DynamoDB is often used in scenarios where low-latency, high-throughput data access is required, such as real-time bidding, gaming leaderboards, and financial transactions.
- **IoT Applications**: Given its scalability and real-time response, it’s also used for IoT data collection and monitoring systems.
- **Mobile and Web Applications**: DynamoDB is commonly used for applications needing scalable back-end services for millions of users (e.g., chat applications, social media platforms).

### Pricing Model

1. **Pay-per-request (On-Demand Mode)**
   Charges are based on the actual read and write requests performed.
2. **Provisioned Mode**
   Charges are based on the allocated read/write capacity. Additional charges apply for exceeding the allocated capacity.
3. **Additional Costs**
   - Data storage: Charged per GB of stored data.
   - DAX and Global Tables: Charged separately based on their usage.

### Best Practices

- **Use Query over Scan**: Prefer using Query with partition keys over Scan to avoid reading the entire table.
- **Choose Composite Keys**: Design your primary key schema wisely to distribute load evenly across partitions.
- **Limit Data Access**: Use indexes (GSI/LSI) to avoid large scans and improve query performance.
- **Use DAX for caching**: For read-heavy applications, enable DynamoDB Accelerator (DAX) to cache frequently accessed data.
- **Optimize Costs**: Choose on-demand capacity mode for unpredictable workloads, and provisioned capacity mode for predictable usage patterns.
