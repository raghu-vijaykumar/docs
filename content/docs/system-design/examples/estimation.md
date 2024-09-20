+++
title= "Estimation"
tags = [ "system-design", "software-architecture", "interview", "estimation" ]
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
bookFlatSection= true
+++

# Back-of-the-envelope Estimation

In system design interviews, you may be asked to estimate performance requirements or system capacity. These are typically done using thought experiments and common performance numbers. Jeff Dean, a Google Senior Fellow, emphasizes the importance of understanding these mechanisms.

## Power of Two

When dealing with large data volumes, calculations rely on the basics of the power of two. Here are some common data units:

- 2^10 = ~1000 = 1KB
- 2^20 = ~1 million = 1MB
- 2^30 = ~1 billion = 1GB
- 2^40 = ~1 trillion = 1TB
- 2^50 = ~1 quadrillion = 1PB

These approximations are essential when estimating data sizes and storage needs.

## Latency Numbers Every Programmer Should Know

A well-known table, created by Jeff Dean, outlines the duration of typical computer operations. These numbers may be slightly outdated due to hardware improvements, but they still serve as good relative measures among operations:

| Operation                              | Latency         | Description                                                   |
| -------------------------------------- | --------------- | ------------------------------------------------------------- |
| L1 cache reference                     | 0.5ns           | Accessing data from the L1 cache                              |
| Branch mispredict                      | 5ns             | Time taken for a CPU branch misprediction                     |
| L2 cache reference                     | 7ns             | Accessing data from the L2 cache                              |
| Mutex lock/unlock                      | 100ns           | Time to lock and unlock a mutex                               |
| Main memory reference                  | 100ns           | Accessing data from main memory                               |
| Compress 1KB                           | 10,000ns = 10µs | Time to compress 1KB of data                                  |
| Send 2KB over 1Gbps network            | 20,000ns = 20µs | Time to send 2KB of data over a 1Gbps network                 |
| Read 1MB sequentially from memory      | 250µs           | Time to read 1MB of data sequentially from memory             |
| Round trip within the same data center | 500µs           | Time for a round trip within the same data center             |
| Disk seek                              | 10ms            | Time for a disk seek operation                                |
| Read 1MB sequentially from network     | 10ms            | Time to read 1MB of data sequentially from network            |
| Read 1MB sequentially from disk        | 30ms            | Time to read 1MB of data sequentially from disk               |
| Send packet CA -> Netherlands -> CA    | 150ms           | Time to send a packet from California to Netherlands and back |

### Visualization of Latency Numbers

![latency-numbers-visu](../images/latency-numbers-visu.png)

### Key Takeaways

- **Memory is fast, disk is slow**: Minimize disk access when possible.
- **Avoid disk seeks**: Disk seeks are expensive, optimize for sequential reads.
- **Compression is fast**: Compress data before transmission.
- **Data center round trips are costly**: Reduce them where possible to improve latency.

## Availability Numbers

High availability is the ability of a system to be continuously operational, aiming to minimize downtime. Services typically aim for availability between 99% and 100%.

A Service Level Agreement (SLA) defines the level of uptime required for a service. Cloud providers, such as AWS EC2, typically offer SLAs of 99.99%.

### Downtime Based on SLAs

Here's a breakdown of allowed downtime for various SLAs:

| Availability | Downtime Allowed per Month | Downtime Allowed per Year |
| ------------ | -------------------------- | ------------------------- |
| 99.9%        | 7.19 hours                 | 87.6 hours                |
| 99.99%       | 43.2 minutes               | 5.2 hours                 |
| 99.999%      | 4.3 minutes                | 52.6 minutes              |
| 99.9999%     | 25.9 seconds               | 2.43 minutes              |

## Example: Estimating Twitter QPS and Storage Requirements

Let's estimate the queries per second (QPS) and storage needs for Twitter based on some assumptions:

### Assumptions

- 300 million Monthly Active Users (MAU)
- 50% of users use Twitter daily
- Users post 2 tweets per day on average
- 10% of tweets contain media
- Data is stored for 5 years

### Estimations

#### Write Requests Per Second (RPS)
- 150 million * 2 tweets per day / 24 hours / 60 minutes / 60 seconds = 3400-3600 tweets per second
- Peak = 7000 TPS

#### Media Storage Per Day
- 300 million users * 10% = 30 million media posts per day
- Assuming 1MB per media file, 30 million * 1MB = 30TB per day
- Over 5 years: 30TB * 365 days * 5 years = 55PB

#### Tweet Storage Estimation
- 1 tweet = 64-byte ID + 140 bytes text + 1000 bytes metadata
- 3500 tweets/second * 60 seconds * 60 minutes * 24 hours = 302MB per day
- Over 5 years: 302MB * 365 days * 5 years = 551GB

## Estimation Table
Here’s a table showing the minimum and maximum ranges for different components.
| **Component**               | **Min**                        | **Max**                                 |
| --------------------------- | ------------------------------ | --------------------------------------- |
| **HDD (Storage)**           | 500 GB (consumer-level HDD)    | 20 TB (high-capacity enterprise HDD)    |
| **SSD (Storage)**           | 128 GB (consumer SSD)          | 30 TB (enterprise-level NVMe SSD)       |
| **Network (Throughput)**    | 100 Mbps (low-speed broadband) | 100 Gbps (high-performance data center) |
| **RAM (Memory)**            | 2 GB (small consumer devices)  | 12 TB (high-end servers with RDIMMs)    |
| **CPU (Cores)**             | 2 vCPUs (basic consumer CPUs)  | 128 vCPUs (high-performance cloud VMs)  |
| **Disk IOPS (HDD)**         | 75 - 100 (consumer HDD)        | 15,000+ (high-end enterprise HDDs)      |
| **Disk IOPS (SSD)**         | 5,000 (consumer SSD)           | 1,000,000+ (enterprise NVMe SSDs)       |
| **Latency (HDD)**           | ~5 – 10 ms (consumer HDD)      | ~2 – 5 ms (high-end enterprise HDD)     |
| **Latency (SSD)**           | <1 ms (consumer SSD)           | ~100 µs (high-end NVMe SSD)             |
| **Power Consumption (HDD)** | ~6 watts (consumer HDD)        | ~12+ watts (enterprise HDDs)            |
| **Power Consumption (SSD)** | ~2 watts (consumer SSD)        | ~10 watts (enterprise SSD)              |
| **Network Latency**         | ~10 ms (home network)          | <1 ms (high-performance data center)    |
| **Storage Latency**         | ~5-10 ms (HDD)                 | ~100 µs (NVMe SSD)                      |
| **Bandwidth**               | 10 Mbps (low-end)              | 400 Gbps (high-end networks)            |

Here is an example table for different use cases:

| **Use Case**                      | **Data Volume (per day)** | **Per Second Processing Volume** | **Network Throughput**                    | **Disk IOPS/Throughput**                          | **RAM Requirements**                               | **SQL DB Nodes** | **SQL DB Node Spec (CPU/RAM/Storage)**   | **NoSQL DB Nodes** | **NoSQL DB Node Spec (CPU/RAM/Storage)**          |
| --------------------------------- | ------------------------- | -------------------------------- | ----------------------------------------- | ------------------------------------------------- | -------------------------------------------------- | ---------------- | ---------------------------------------- | ------------------ | ------------------------------------------------- |
| **Simple Web Application**        | 100 MB – 1 GB             | 1.2 KB - 12 KB per second        | 10 Mbps - 100 Mbps                        | Moderate IOPS (~500 - 1,000)                      | 2 – 4 GB (General-purpose VMs)                     | 1 – 2 nodes      | 2 – 4 vCPUs, 4 – 8 GB RAM, 100 GB SSD    | 1 – 2 nodes        | 2 – 4 vCPUs, 8 GB RAM, 100 GB SSD                 |
| **Small Database (e.g., MySQL)**  | 1 GB – 10 GB              | 12 KB - 120 KB per second        | 100 Mbps - 1 Gbps                         | Moderate IOPS (~1,000 - 5,000)                    | 4 – 8 GB (Memory-optimized VMs)                    | 1 – 3 nodes      | 4 – 8 vCPUs, 8 – 16 GB RAM, 500 GB SSD   | 3 – 5 nodes        | 4 – 8 vCPUs, 16 GB RAM, 500 GB SSD                |
| **File Storage/Backup**           | 100 GB – 1 TB             | 1.2 MB - 12 MB per second        | 500 Mbps - 2 Gbps                         | Lower IOPS (~500 - 1,000)                         | 4 – 16 GB (More RAM not critical)                  | 2 – 4 nodes      | 4 vCPUs, 8 GB RAM, 1 TB HDD              | 3 – 5 nodes        | 4 vCPUs, 16 GB RAM, 1 TB HDD                      |
| **Video Streaming**               | 100 GB – 1 TB             | 1.2 MB - 12 MB per second        | 1 Gbps - 10 Gbps                          | High throughput (~100 MB/s or higher)             | 16 – 32 GB (For large buffers)                     | 3 – 5 nodes      | 8 vCPUs, 16 – 32 GB RAM, 2 TB SSD        | 5 – 10 nodes       | 8 vCPUs, 32 GB RAM, 2 TB SSD                      |
| **E-commerce System**             | 10 GB – 500 GB            | 120 KB - 6 MB per second         | 1 Gbps - 10 Gbps                          | Moderate IOPS (~5,000 - 10,000)                   | 16 – 64 GB (For session management and DB caching) | 3 – 5 nodes      | 16 vCPUs, 32 GB RAM, 1 – 2 TB SSD        | 5 – 10 nodes       | 16 – 32 vCPUs, 64 GB RAM, 2 TB SSD                |
| **Analytics/Batch Processing**    | 500 GB – 10 TB            | 6 MB - 120 MB per second         | 1 Gbps - 40 Gbps (parallel processing)    | High IOPS (~10,000 - 50,000)                      | 64 – 128 GB (Depends on dataset size and caching)  | 5 – 10 nodes     | 16 – 32 vCPUs, 64 GB RAM, 2 – 4 TB SSD   | 10 – 20 nodes      | 16 – 32 vCPUs, 64 GB RAM, 2 – 4 TB SSD            |
| **Big Data (e.g., Hadoop/Spark)** | 1 TB – 100 TB             | 12 MB - 1.2 GB per second        | 10 Gbps - 100 Gbps (distributed clusters) | High IOPS (50,000+) or high sequential throughput | 128 GB+ (RAM for in-memory processing and caching) | 10 – 50 nodes    | 32 – 64 vCPUs, 128 GB RAM, 4 – 10 TB HDD | 50 – 100 nodes     | 32 – 64 vCPUs, 128 – 256 GB RAM, 4 – 10 TB SSD    |
| **In-memory DB (e.g., Redis)**    | 100 GB – 1 TB             | 1.2 MB - 12 MB per second        | 1 Gbps - 10 Gbps                          | High IOPS (~100,000+)                             | 512 GB - 1 TB (To store data in memory)            | N/A (NoSQL only) | N/A                                      | 10 – 20 nodes      | 64 – 128 vCPUs, 512 GB RAM, SSD optional          |
| **AI/ML Model Training**          | 1 TB – 10 TB              | 12 MB - 120 MB per second        | 10 Gbps - 40 Gbps                         | High IOPS (~50,000+)                              | 256 GB - 1 TB (For in-memory computations)         | 5 – 10 nodes     | 32 vCPUs, 128 GB RAM, 4 TB SSD           | 10 – 20 nodes      | 32 – 64 vCPUs, 256 GB RAM, SSD/HDD mix            |
| **Financial Trading**             | 100 GB – 1 TB             | 1.2 MB - 12 MB per second        | 10 Gbps - 100 Gbps (ultra-low latency)    | Very high IOPS (100,000+)                         | 128 – 256 GB (For rapid processing)                | 10 – 20 nodes    | 32 – 64 vCPUs, 128 GB RAM, 2 – 4 TB SSD  | 20 – 50 nodes      | 32 – 64 vCPUs, 128 GB RAM, SSD (high-performance) |


## Tips for Back-of-the-envelope Estimation

Back-of-the-envelope estimations are more about the problem-solving process than arriving at exact results. Interviewers are usually assessing your approach and reasoning.

### Key Tips

- **Rounding and Approximation**: Simplify calculations by rounding. For example, instead of calculating 99,987 / 9.1, approximate to 100,000 / 10.
- **State Assumptions**: Write down any assumptions you are making before starting your estimations.
- **Label Units**: Always specify units. For example, write 5MB instead of just 5.
- **Common Estimations**: Focus on estimating queries per second (QPS), peak QPS, storage needs, cache requirements, and the number of servers required.


