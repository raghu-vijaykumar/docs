+++
title= "Search Autocomplete"
tags = [ "system-design", "software-architecture", "interview", "search-autocomplete" ]
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
weight= 10
bookFlatSection= true
+++

# Type-Ahead/Autocomplete System Design

## Overview

The goal is to design a type-ahead or autocomplete system for a search engine. This feature suggests popular queries as users type, improving search efficiency and user experience. The design focuses on providing suggestions based on user input, optimizing performance, and handling large-scale data efficiently.

## Functional Requirements

1. **Query Suggestions Criteria**:
   - Provide suggestions based on recent trending queries.
   - Suggestions derived from the most popular queries in the last 24 hours.
   - Support up to 10 suggestions per query prefix.
   - No support for spell checking or correction of common typos.

2. **User Input Specifications**:
   - English alphabet support.
   - Maximum prefix length: 60 characters.
   - Case insensitive suggestions (convert all input to lowercase).

## Non-Functional Requirements

1. **Scale**:
   - Handle billions of search queries daily.
   
2. **Performance**:
   - Maximum response time for suggestions: 240 milliseconds.
   - Suggestions can be up to 1 hour out of date.
   
3. **Consistency**:
   - Eventual consistency is acceptable.
   - No requirement for exact same suggestions or order for all users simultaneously.

## API Design

1. **Autocomplete Endpoint**:
   - **Request**: `GET /complete?prefix=<prefix>`
   - **Response**: JSON array of autocomplete suggestions, sorted by popularity.

2. **Search Endpoint**:
   - **Request**: `GET /search?query=<query>`
   - **Response**: Search results for the given query.

   - Note: URL encoding required for special characters and spaces.

## Data Structures

### Lexical Prefix Tree (Trie)

- **Structure**:
  - A tree where each node represents a letter in the alphabet.
  - Paths from the root to terminal nodes represent strings.
  
- **Pros**:
  - Efficiently provides suggestions for fixed prefixes.

- **Cons**:
  - Large size: With billions of queries and a 60-character prefix, the trie can become very large and memory-intensive.
  - Performance: Traversing and sorting branches for long prefixes can be slow.

## Alternative Approach

1. **Command and Query Responsibility Segregation (CQRS)**:
   - Use separate data representations for read (autocomplete) and write (ranking updates) operations.
   - Optimize for fast read operations and handle updates in a batch processing model.

2. **Batch Processing for Updates**:
   - Update autocomplete suggestions in batch processes, allowing a lag of up to one hour.

## System Architecture

### Microservices

1. **Autocomplete Service**
   - **Function**: Handles autocomplete requests from users typing in the search box.
   - **Data Storage**: Maintains a database of autocomplete suggestions for quick retrieval.
   - **Data Structure**: Uses a key-value store where keys are prefixes and values are sorted lists of suggestions.

2. **Autocomplete Updater Service**
   - **Function**: Updates the autocomplete suggestions based on recent search queries.
   - **Data Storage**: Stores user search queries and timestamps for batch processing.

### Data Flow

1. **Autocomplete Requests**:
   - **Request**: Users type a prefix into the search box.
   - **Process**: The Autocomplete Service retrieves suggestions from its database based on the prefix.
   - **Response**: Returns a sorted list of up to 10 autocomplete suggestions.

2. **Search Queries**:
   - **Request**: Users submit search queries.
   - **Process**: The Autocomplete Updater Service records each query and timestamp, storing them in a distributed file system.

### Batch Processing

1. **Map Stage**:
   - **Process**: Iterates over recorded queries to extract prefixes and emit key-value pairs where the key is the prefix and the value is the query.
   - **Filter**: Ignores queries older than 24 hours.

2. **Reduce Stage**:
   - **Process**: Aggregates key-value pairs by prefix and sorts them to find the top 10 suggestions.
   - **Output**: Generates a new set of key-value pairs with prefixes and corresponding top 10 suggestions.

3. **Data Storage**:
   - **Process**: Stores the processed mappings in a file system or database.

### Data Synchronization

1. **Change Data Capture (CDC)**:
   - **Function**: Monitors changes in the database and publishes updates to a message broker.
   - **Process**: The Autocomplete Service subscribes to these updates and refreshes its in-memory database.

2. **Update Frequency**:
   - **Process**: The batch processing pipeline runs on a scheduled basis (e.g., every 30 minutes to an hour).

## Key Considerations

1. **Scalability**:
   - Utilize distributed systems and databases to manage large volumes of data.
   - Employ batch processing to handle updates efficiently.

2. **Performance**:
   - Ensure autocomplete responses are provided within 240 milliseconds.
   - Use efficient data structures and indexing to minimize retrieval time.

3. **Consistency**:
   - Allow for eventual consistency with up-to-one-hour lag in suggestions.

## System Components

### 1. Autocomplete Service
- **Function**: Handles autocomplete requests and returns a sorted list of suggestions for given prefixes.
- **Deployment**: Runs behind a load balancer with multiple instances to manage high traffic.

### 2. Autocomplete Updater Service
- **Function**: Updates autocomplete records using a big data processing pipeline.
- **Deployment**: Runs behind a load balancer with multiple instances to handle high query volumes.

## Data Storage and Management

### Key-Value Store
- **Data Storage**: Uses a sharded key-value store to manage mappings between prefixes and suggestions.
- **Sharding**: Distributes data across multiple instances to fit within memory limits and avoid performance bottlenecks.
- **Load Balancing**: Addresses hotspots by employing database replication to balance load across multiple replicas.
- **Dynamic Replication**:
  - **Shard Manager**: Manages dynamic addition or removal of read replicas based on traffic load.
  - **Metrics**: Monitors network traffic and CPU utilization to adjust replication dynamically.

## Big Data Processing Pipeline

### MapReduce Framework
- **Map Stage**:
  - **Function**: Processes search queries, extracts prefixes, and emits key-value pairs.
  - **Optimization**: Implements sampling to reduce the volume of data processed.
- **Reduce Stage**:
  - **Function**: Aggregates key-value pairs, performs sorting to determine top 10 suggestions, and writes results to storage.
- **Parallel Processing**:
  - **Map and Reduce Instances**: Run on different computers to process data in parallel, improving scalability and performance.
  - **Data Partitioning**: Ensures results are stored across multiple computers.

### Change Data Capture (CDC)
- **Function**: Monitors database changes and publishes updates to a message broker.
- **Process**: Only changed records are sent to the Autocomplete Service, ensuring efficient updates without duplication.

## Geographic Distribution
- **Deployment**: Runs in multiple geographical locations and data centers to enhance high availability and reduce latency.
