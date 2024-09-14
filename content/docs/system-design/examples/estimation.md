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

- L1 cache reference = 0.5ns
- Branch mispredict = 5ns
- L2 cache reference = 7ns
- Mutex lock/unlock = 100ns
- Main memory reference = 100ns
- Compress 1KB = 10,000ns = 10µs
- Send 2KB over 1Gbps network = 20,000ns = 20µs
- Read 1MB sequentially from memory = 250µs
- Round trip within the same data center = 500µs
- Disk seek = 10ms
- Read 1MB sequentially from network = 10ms
- Read 1MB sequentially from disk = 30ms
- Send packet CA -> Netherlands -> CA = 150ms

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

![sla-chart](../images/sla-chart.png)

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

## Tips for Back-of-the-envelope Estimation

Back-of-the-envelope estimations are more about the problem-solving process than arriving at exact results. Interviewers are usually assessing your approach and reasoning.

### Key Tips

- **Rounding and Approximation**: Simplify calculations by rounding. For example, instead of calculating 99,987 / 9.1, approximate to 100,000 / 10.
- **State Assumptions**: Write down any assumptions you are making before starting your estimations.
- **Label Units**: Always specify units. For example, write 5MB instead of just 5.
- **Common Estimations**: Focus on estimating queries per second (QPS), peak QPS, storage needs, cache requirements, and the number of servers required.


