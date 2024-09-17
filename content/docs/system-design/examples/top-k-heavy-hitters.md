+++
title= "Top K Heavy Hitters"
tags = [ "system-design", "software-architecture", "interview", "top-k-heavy-hitters" ]
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
weight= 26
bookFlatSection= true
+++

# Designing a System for Identifying Top K Heavy Hitters

This document provides a summary of the system design for identifying the top K heavy hitters (i.e., the most frequent items) in a stream of data. The system needs to handle high-volume requests and process data in real-time, suitable for use cases like identifying the most viewed videos, frequently searched keywords, or top liked posts across platforms like Google, YouTube, or Facebook.

## 2. Problem Definition

We need to design a system that:
- Returns the top K most frequent items (heavy hitters) within a specified time interval.
- Supports real-time data processing with high throughput, handling millions of requests per second.
- Ensures scalability, availability, performance, and accuracy.
- Supports both bounded (limited size) and unbounded (streaming) data sets.

## 3. Functional Requirements

- **Top K Items**: The system should provide the K most frequent items.
- **Time Interval**: The system should allow users to specify a time interval for calculating the top K items (e.g., last 5 minutes, 1 hour).
  
## 4. Non-Functional Requirements

- **Scalability**: The system should handle increasing data volumes and maintain performance.
- **Availability**: The system must be available even in case of hardware failures or network partitions.
- **Performance**: The system should retrieve the top K heavy hitters within tens of milliseconds.
- **Accuracy**: The solution must ensure accurate counting of events without relying on approximations unless explicitly discussed (e.g., through data sampling).

## 5. Design Ideas and Approaches

### 5.1 Single Host Approach
1. **Hash Table**: 
   - Store frequency counts of items in a hash table.
   - Retrieve the top K items using either sorting (O(n log n)) or a heap (O(n log K)) for more efficiency.
2. **Scalability Issue**: 
   - This approach does not scale well due to memory constraints and processing limits.

### 5.2 Distributed Approach

#### 5.2.1 Parallel Processing with Load Balancer
- **Load Balancer**: Distribute incoming data (events) across multiple processors to handle high data volume.
- **Processor Hosts**: Each processor maintains its own list of K heavy hitters.
- **Storage Host**: Data from multiple processors is merged into a final list of top K heavy hitters.

#### 5.2.2 Data Partitioning
- **Data Partitioning**: Partition the data into smaller subsets to reduce memory usage and improve scalability. Each processor handles only a subset of data.
- **Merging Results**: Merge the K heavy hitters from each processor to form the final list. This problem of merging sorted lists is a classic coding problem.

### 5.3 Stream Processing for Unbounded Data
- **Stream Processing**: Since the data is continuous and infinite (e.g., streaming video views), the processors accumulate data for a short period (e.g., 1 minute) and then flush the top K heavy hitters to storage.
- **Challenges**: 
   - Calculating the top K heavy hitters for longer periods (e.g., 1 hour or 1 day) requires processing entire data sets, which cannot be stored in memory.

### 5.4 Batch Processing with MapReduce
- **MapReduce**: For longer time intervals, store data on disk and use a batch processing framework like MapReduce to calculate the top K heavy hitters. This approach allows handling larger data sets that cannot be processed in real time.

## 6. Key Challenges and Trade-offs

1. **Memory Usage**: Storing large volumes of data in memory is infeasible for systems with billions of events.
2. **Accuracy vs. Scalability**: To maintain accuracy while scaling, careful partitioning and aggregation strategies are necessary.
3. **Real-Time vs. Batch Processing**: Real-time systems may lose precision due to limited data retention, while batch systems require more resources and are slower to respond.

## 7. Optimizing Data Processing for Top K Heavy Hitters Using Count-Min Sketch

### 1. **Count-Min Sketch**
A Count-Min Sketch is a probabilistic data structure used for frequency estimation. It offers memory efficiency by using a fixed-size two-dimensional array to approximate the count of elements, with tunable error bounds.

- **Structure**: A 2D array where the width represents a large number (thousands) and the height corresponds to the number of hash functions (e.g., 5).
- **Insertion**: When a new element is added, it updates multiple cells using different hash functions.
- **Retrieval**: The minimum value across all hashed cells is considered as the approximate count to mitigate overestimation caused by hash collisions.
- **Tuning**: The width and height can be calculated using known formulas based on desired accuracy and error probabilities.

### 2. **API Gateway Log Aggregation**
The system starts by capturing user interactions (e.g., video views) through an API Gateway, which logs every request. Logs can be used to track video views for further aggregation.

- **Data Aggregation at Gateway**: Pre-aggregates video views in memory using a hash table. This pre-aggregation reduces the volume of data sent for downstream processing.
- **Buffering**: Data is either flushed when the buffer is full or at regular time intervals.
- **Optimization**: Optionally, raw view events can be sent directly for processing without aggregation.

### 3. **Fast Path: Approximate Top K Calculation**
The fast path provides approximate results in near real-time using the Count-Min Sketch. Data flows as follows:

- **Log Processing**: Log entries from API Gateway are sent to a distributed messaging system like **Apache Kafka**.
- **Fast Processor**: A service that reads messages from Kafka, updates the Count-Min Sketch for a short time interval, and stores the sketch in memory.
- **Data Replication**: Not required since the approximate nature of the Count-Min Sketch allows for occasional data loss in case of hardware failure.
- **Storage**: The final aggregated sketch is periodically flushed to storage, where it holds the top k heavy hitters for short intervals (e.g., 1 minute or 5 minutes).

### 4. **Slow Path: Precise Top K Calculation**
The slow path ensures accurate top k results by processing data through **MapReduce** jobs.

- **Data Partitioning**: A **Data Partitioner** reads batches of events and assigns each video view to a partition in Kafka or Kinesis. Partitioning ensures uniform distribution of the data for processing, preventing hot partitions.
- **Partition Processors**: These components aggregate data for their respective partitions over a predefined period (e.g., 5 minutes), batch it, and store it in a distributed file system (e.g., HDFS or S3).
- **MapReduce Jobs**: Two jobs are run:
  - **Frequency Count Job**: Aggregates the counts for each video.
  - **Top K Calculation Job**: Computes the final top k list based on aggregated counts.

### 5. **Trade-offs**
- **Fast Path**: Provides rapid, approximate results with minimal resource usage but at the cost of accuracy.
- **Slow Path**: Ensures precise results but introduces latency due to the complexity of MapReduce jobs.
- **Data Replication**: Can be omitted in the fast path, simplifying the architecture, while the slow path maintains full accuracy.

## Data Flow

1. **User Clicks**: User views are logged by API Gateway and captured in log files.
2. **API Gateway Aggregation**: Pre-aggregation of view counts happens at the API Gateway level. When buffer thresholds are met, data is flushed and sent to the messaging system.
3. **Kafka Messaging**: Data is partitioned across Kafka topics to distribute load and ensure scalability.
4. **Fast Processor**: Quickly processes data with the Count-Min Sketch and stores approximate top k results in memory. Data is periodically flushed to storage.
5. **Slow Path Processing**: The Data Partitioner ensures even distribution of messages across partitions, and Partition Processors handle in-memory aggregation before data is written to distributed storage.
6. **MapReduce Jobs**: Process the data stored in the distributed file system to generate precise top k heavy hitters.

## Retrieval and Data Processing Pipeline

### 1. **Data Ingestion**
- **API Gateway** handles incoming data, including functionalities like authentication, SSL termination, rate limiting, and request routing.
- **Log Aggregation**: API Gateway hosts may offload log files to a separate cluster for parsing and aggregation, ensuring performance isolation.

### 2. **Top K MapReduce Process**
- **Partitioning**: The data is split into partitions, and each partition calculates its own Top K list.
- **Final Top K Calculation**: The individual top k lists from each partition are merged together by a reducer to form the final Top K list.
  
### 3. **Data Retrieval**
- **API Gateway Exposure**: A `topK` operation is exposed by the API Gateway, which routes data retrieval requests to the **Storage Service**.
- **Storage Service**: Retrieves data from an underlying database, with two data paths:
  - **Fast Path**: Returns approximate results by merging multiple short-interval (e.g., 1-minute) Top K lists.
  - **Slow Path**: Provides precise Top K lists for longer intervals (e.g., 1-hour).

### 4. **Handling Different Time Intervals**
- **Approximate Results**: For shorter time intervals (e.g., the last 5 minutes), the system merges multiple 1-minute Top K lists, returning approximate results.
- **Precise Results**: For predefined longer intervals (e.g., 1-hour), the system provides precomputed precise Top K lists.
- **Merging Larger Intervals**: For custom intervals (e.g., 2 hours), multiple precise lists (e.g., two 1-hour lists) are merged, though the result may no longer be perfectly accurate.

### 5. **Challenges and Trade-offs**
- **Capacity and Performance**: The API Gateway hosts may lack the capacity for background data aggregation, necessitating offloading to a separate log processing cluster.
- **Value of K**: The size of K (e.g., number of top heavy hitters) plays a role in performance, especially when merging large lists in real-time or handling network bandwidth and storage requirements for high values of K.
- **System Complexity**: Introducing a fast and slow path increases system complexity (as seen in **Lambda Architecture**), which balances real-time performance with accuracy but comes with added development and operational costs.

### 6. **Lambda Architecture**
- **Definition**: An architectural approach that handles both batch and real-time data processing by combining batch systems (e.g., MapReduce) and stream processing systems.
- **Trade-offs**: While effective for obtaining accurate and timely results, it introduces complexity. Alternatives like using **Kafka + Spark** offer simpler solutions for certain workloads, though they rely on the same principles of data partitioning and aggregation.

## Alternatives to Count-Min Sketch
Several algorithms offer alternatives for calculating heavy hitters, such as:
- **Lossy Counting**
- **Space Saving**
- **Sticky Sampling**

Modifications of the **Count-Min Sketch** algorithm are also available, depending on specific needs.

## Practical Use Cases
The top k heavy hitters design is applicable across various domains:
- **Trending Services**: Google Trends, Twitter Trends, etc., which compute popular items based on user activity.
- **DDoS Protection**: Identifying heavy hitter IP addresses that generate the most requests and blocking them.
- **Stock Trading**: Finding the most actively traded stocks in real-time by identifying the top k stocks with the highest trade volumes.

## Conclusion
The design of a system for calculating top k heavy hitters, though complex, applies to a wide range of use cases, including trending services, fraud detection, and real-time analytics. The architecture leverages both approximate and precise methods to provide scalable solutions, balancing performance with accuracy depending on the use case.

