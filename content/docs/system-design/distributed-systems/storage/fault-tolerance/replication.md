+++
title= "Replication"
tags = [ "system-design", "software-architecture", "distributed-systems", "fault-tolerance", "storage", "replication" ]
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

# Database Replication

Database replication involves creating multiple copies of data across different machines to ensure redundancy and availability in distributed databases.

## Motivation for Replication

### High Availability
Replication ensures that data remains accessible even if the primary database instance fails. By keeping copies of the data on separate machines, the system can continue to operate even if the master database becomes unavailable.

### Fault Tolerance
Replication provides a backup to protect against data loss due to hardware failures or other issues. This redundancy ensures that data is not lost if a single instance fails.

### Scalability and Performance
Replication enhances read performance by distributing read operations across multiple replicas. This allows the system to handle higher read throughput and concurrency.

## Replicated Database Architectures

### Master-Slave Architecture
- **Description**: All write operations are directed to the master node, while read operations are handled by slave nodes. The master propagates changes to the slaves.
- **Failover**: In case of master failure, a slave can be promoted to master to ensure continued operation.

### Master-Master Architecture
- **Description**: All nodes can perform both read and write operations. Writes are propagated to other nodes to maintain consistency.
- **Scalability**: Allows for horizontal scaling by adding more nodes, with each node able to handle both read and write operations.

## Conclusion

Database replication is essential for maintaining high availability, fault tolerance, and improved performance in distributed systems. Different replication architectures provide flexibility in balancing these requirements.
