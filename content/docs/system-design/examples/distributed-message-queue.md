+++
title= "Distributed Message Queue"
tags = [ "system-design", "software-architecture", "interview", "distributed-message-queue" ]
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
weight= 18
bookFlatSection= true
+++

# Distributed Message Queue System Design

## Overview

This document outlines the design of a distributed message queue system, covering the core components, their functions, and considerations for scaling and high availability.

## System Components

### 1. Virtual IP (VIP)

- **Purpose**: Acts as a symbolic hostname (e.g., `myWebService.domain.com`) that resolves to a load balancer.
- **Function**: Directs client requests to one of the load balancers.

### 2. Load Balancer

- **Function**: Routes client requests across multiple servers.
- **High Availability**:
  - **Primary and Secondary Nodes**: Ensure failover if the primary node fails.
- **Scalability**:
  - **Multiple VIPs**: Use multiple VIPs for partitioning requests across several load balancers.
  - **Data Center Distribution**: Spread load balancers across different data centers to enhance availability and performance.

### 3. FrontEnd Web Service

- **Function**: Handles initial request processing, including:
  - **Request Validation**: Ensures requests meet the necessary criteria before processing.
  - **Authentication and Authorization**: Verifies user identity and permissions.
  - **SSL Termination**: Decrypts SSL/TLS encrypted traffic at the load balancer.
  - **Server-Side Data Encryption**: Encrypts data at rest using strong encryption algorithms.
  - **Caching**: Stores metadata about frequently used queues and user identity information.
  - **Rate Limiting**: Protects against request overload, commonly implemented using algorithms like Leaky Bucket.
  - **Request Dispatching**: Routes requests to appropriate Backend nodes.
  - **Request Deduplication**: Ensures messages are not processed more than once, especially crucial for 'exactly once' delivery semantics.
  - **Usage Data Collection**: Gathers metrics and usage data for analytics and billing.

### 4. Metadata Service

- **Function**: Stores information about queues and acts as a caching layer between FrontEnd and persistent storage.
- **Data Organization**:
  - **Full Data Set on Nodes**: For smaller caches, where all nodes contain the same information.
  - **Sharding**: For larger data sets, partition data into chunks (shards), either with FrontEnd knowing shard locations or using a hashing ring.
  - **Load Balancer**: Optional for directing requests to Metadata service nodes.

### 5. Backend Web Service

- **Function**: Manages message persistence and processing.

## Considerations

### Functional Requirements

- **Core APIs**: 
  - Send Message
  - Receive Message
  - Additional APIs may include Create/Delete Queue and Delete Message.
- **Specific Requirements**: 
  - Avoid duplicate submissions
  - Security and ordering guarantees
  - SLA (Service Level Agreement) for throughput and cost-effectiveness

### Non-Functional Requirements

- **Scalability**: Handle load increases.
- **High Availability**: Tolerate hardware and network failures.
- **Performance**: Ensure fast send and receive operations.
- **Durability**: Persist data once submitted to the queue.

## Key Considerations

### 1. Data Storage

- **Database Storage**: Using a traditional database is not ideal due to high throughput requirements. A database capable of handling high throughput would be necessary, making the problem similar to building a high-performance database.
  
- **Alternative Storage Options**: 
  - **Memory**: Suitable for short-term storage of newly arrived messages.
  - **File System**: Useful for more durable storage but not as resilient as local disks.
  - **Local Disk**: Recommended for storing messages over longer periods (days or weeks).

### 2. Data Replication

- **Replication Methods**:
  - **Synchronous Replication**: Ensures high durability by waiting for data to be replicated across all hosts before acknowledging receipt.
  - **Asynchronous Replication**: Returns acknowledgment immediately after storing the message on a single host, with later replication to other hosts. This method is more performant but less durable.

### 3. Backend Host Management

- **Leader-Based Architecture**:
  - **Leader Election**: Each backend instance acts as a leader for specific queues. The leader is responsible for handling requests and data replication.
  - **In-Cluster Manager**: Manages leader election and queue-to-leader assignments. Needs to be reliable, scalable, and performant.

- **Cluster-Based Architecture**:
  - **Out-Cluster Manager**: Manages queue-to-cluster assignments without the need for leader election. It tracks cluster health and utilization.
  - **Partitioning**: For large queues, the in-cluster manager splits the queue into partitions, each with a leader. The out-cluster manager may distribute partitions across multiple clusters.

### 4. Queue Management

- **Queue Creation and Deletion**:
  - **Auto-Creation**: Queues can be auto-created when the first message arrives.
  - **API-Based Creation**: Provides better control over queue configuration.
  - **Deletion**: Should be executed cautiously, possibly through command line utilities rather than public APIs.

- **Message Deletion**:
  - **Delayed Deletion**: Consumers are responsible for deleting consumed messages. This method maintains message order and offsets.
  - **Invisible Messages**: Similar to Amazon SQS, messages are marked invisible until explicitly deleted by consumers.

### 5. Delivery Guarantees

- **Types of Guarantees**:
  - **At Most Once**: Messages may be lost but are never redelivered.
  - **At Least Once**: Messages are never lost but may be redelivered.
  - **Exactly Once**: Each message is delivered exactly once, though this is challenging to achieve in practice.

### 6. Message Delivery Models

- **Pull Model**: Consumers continuously request messages. Easier to implement but requires more work from consumers.
- **Push Model**: Consumers are notified when new messages arrive. More efficient but harder to implement.

### 7. Order and Security

- **FIFO Ordering**: Ensuring strict order is challenging in distributed systems. Many systems relax this guarantee or limit throughput to maintain order.
- **Security**: Use SSL over HTTPS for message encryption in transit. Messages can also be encrypted at rest.

### 8. Monitoring

- **Components to Monitor**:
  - **FrontEnd Service**
  - **Metadata Service**
  - **Backend Services**

- **Metrics and Alerts**:
  - Emission of metrics and log data by services.
  - Creation of dashboards and alerts for both operators and customers.

### 9. Non-Functional Requirements

- **Scalability**: System components can be scaled horizontally by adding more resources.
- **High Availability**: Redundancy across data centers ensures continued operation despite individual failures.
- **Performance**: Dependent on implementation, hardware, and network setup.
- **Durability**: Ensured through data replication and robust storage practices.

## Conclusion

This architecture outlines a comprehensive approach to designing a distributed message queue system, addressing storage, replication, management, and operational aspects to ensure performance, reliability, and scalability.

