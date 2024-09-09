+++
title= "Consistency"
tags = [ "system-design", "software-architecture", "distributed-systems", "consistency", "storage" ]
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

# Database Consistency Models

## Introduction

Consistency models define how a distributed system manages the visibility of data across different nodes, ensuring that the data is accurate and up-to-date.

## Consistency Models

### Eventual Consistency
- **Description**: Guarantees that, given enough time, all replicas will converge to the same value. Temporary inconsistencies are possible.
- **Use Cases**: Suitable for applications where immediate consistency is not critical, such as social media posts or analytics.

### Strict Consistency
- **Description**: Ensures that all readers see the most recent write. Writes are only acknowledged once all replicas have been updated.
- **Use Cases**: Essential for systems requiring up-to-date data, such as financial transactions or inventory systems.

## Quorum Consensus Algorithm

### Concept
- **Quorum**: A quorum is a majority of nodes required to achieve a consistent state. It is defined by read (`R`) and write (`W`) quorums.
- **Guarantee**: To ensure strict consistency, the sum of `R` and `W` must be greater than the total number of nodes (`N`).

### Example
- For a cluster with 5 nodes:
  - If `R = 3` and `W = 3`, then `R + W = 6`, which is greater than `N = 5`, ensuring strict consistency.
  - If `R = 2` and `W = 3`, then `R + W = 5`, which is not strictly greater than `N`, leading to eventual consistency.

### Optimization
- **Read Optimization**: Choosing smaller `R` and larger `W` values can optimize for read performance while maintaining consistency.
- **Write Optimization**: Larger `R` and smaller `W` values can optimize for write performance but may impact consistency and availability.

## Conclusion

Consistency models are crucial for managing data accuracy and availability in distributed systems. The quorum consensus algorithm helps achieve strict consistency by ensuring appropriate overlap between read and write operations. Different configurations allow for optimization based on specific workload requirements.
