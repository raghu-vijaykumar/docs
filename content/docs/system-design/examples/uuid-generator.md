+++
title= "UUID Generator"
tags = [ "system-design", "software-architecture", "interview", "uuid-generator" ]
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
weight= 6
bookFlatSection= true
+++

# UUID Generator Design

We need to design a unique ID generator suitable for distributed systems. Using a primary key with `auto_increment` is not viable, as generating IDs across multiple database servers introduces significant latency.

## Step 1 - Problem Understanding and Design Scope

### Questions and Answers
- **Q:** What characteristics should the unique IDs have?
  - **A:** The IDs should be unique and sortable.
- **Q:** Should the ID increment by 1 for each record?
  - **A:** IDs increment by time but not necessarily by 1.
- **Q:** Should the IDs only contain numerical values?
  - **A:** Yes.
- **Q:** What is the required ID length?
  - **A:** 64 bits (not bytes).
- **Q:** What scale must the system support?
  - **A:** It should generate 10,000 IDs per second.

## Step 2 - High-Level Design Options

### Options Considered
1. **Multi-master replication**
2. **Universally Unique Identifiers (UUIDs)**
3. **Ticket Server**
4. **Twitter Snowflake**

### Multi-master Replication
This approach leverages the database’s `auto_increment` feature but modifies it to increment by `K`, where `K` equals the number of servers.

- ![multi-master-replication](../images/multi-master-replication.png)

- **Pros:**
  - Scalability is achieved by limiting ID generation to a single server.
- **Cons:**
  - Difficult to scale across multiple data centers.
  - IDs may not increment in order across different servers.
  - Adding or removing servers disrupts the ID generation mechanism.

### Universally Unique Identifiers (UUID)
UUIDs are 128-bit unique identifiers. The likelihood of a collision is extremely low.

- **Pros:**
  - UUIDs can be generated independently across servers without synchronization.
  - Easy to scale across systems.
- **Cons:**
  - 128-bit length does not fit the 64-bit requirement.
  - UUIDs are not sortable by time.
  - Non-numeric format.

Example UUID - `09c93e62-50b4-468d-bf8a-c07e1040bfb2`.

### Ticket Server
A centralized server generates unique IDs across distributed services.

- ![ticket-server](../images/ticket-server.png)

- **Pros:**
  - Generates numeric IDs.
  - Simple to implement and suitable for small- to medium-scale applications.
- **Cons:**
  - Centralized server creates a single point of failure.
  - Adds latency due to network communication with the ticket server.

### Twitter Snowflake
The **Twitter Snowflake** algorithm generates unique 64-bit numeric IDs, sortable by time and suitable for distributed systems.

- ![twitter-snowflake](../images/twitter-snowflake.png)

- **Structure:**
  - **Sign bit (1 bit):** Always `0`, reserved for future use.
  - **Timestamp (41 bits):** Time in milliseconds since the epoch, allowing for up to 69 years of IDs.
  - **Datacenter ID (5 bits):** Supports up to 32 data centers.
  - **Machine ID (5 bits):** Supports up to 32 machines per data center.
  - **Sequence number (12 bits):** Sequence of IDs generated within the same millisecond, resetting to 0 for the next millisecond.

- **Pros:**
  - IDs are sortable by time.
  - IDs are 64 bits, fitting the requirement.
  - Can be generated independently by servers.
- **Cons:**
  - Clock synchronization must be ensured across servers to prevent ID collisions.

## Step 3 - Detailed Design

We’ll adopt **Twitter's Snowflake algorithm** as it meets the requirements of being time-sorted, 64-bit in length, and easily distributable across servers.

### Design Considerations:
- **Datacenter and Machine IDs** are chosen at server startup.
- The **timestamp** and **sequence number** are generated at runtime.

### Key Features:
- **Time-sorted IDs:** The timestamp ensures that newer records have larger IDs.
- **Concurrency handling:** The sequence number ensures that multiple IDs can be generated within the same millisecond.
- **Scalability:** The system supports distributed ID generation across data centers and machines.

## Step 4 - Conclusion

We explored several approaches to generating unique IDs, including multi-master replication, UUIDs, ticket servers, and the Twitter Snowflake algorithm. After evaluating the pros and cons, we selected **Twitter’s Snowflake algorithm** for its ability to generate 64-bit, time-sorted, numeric IDs with minimal coordination across servers.

### Additional Considerations:
- **Clock Synchronization:** Ensure all machines maintain accurate clocks using Network Time Protocol (NTP) to prevent ID collisions.
- **Bit Tuning:** Adjust the number of bits assigned to timestamps and sequence numbers depending on concurrency and system lifespan.
- **High Availability:** The ID generator must be highly available, as it is a critical component in distributed systems.
