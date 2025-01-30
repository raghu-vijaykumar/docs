+++
title= "Distrubuted Systems"
tags = [ "system-design", "software-architecture", "distributed-systems" ]
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

# Distributed Systems

A **distributed system** is a collection of independent computers (also known as nodes or machines) that appears to the users of the system as a single coherent system. These nodes communicate and coordinate with each other to achieve a common goal, often by exchanging messages over a network. Distributed systems are designed to support scalable and reliable services across large and geographically dispersed infrastructures, enabling different components to work together seamlessly.

{{< markmap >}}

```markmap
# Distributed Systems
  - **Fundamentals**
    - CAP Theorem (Consistency, Availability, Partition Tolerance)
    - Consistency Models (Strong, Eventual, Causal, etc.)
    - Distributed System Challenges (Latency, Failures, Clock Synchronization)
    - Trade-offs in Distributed Architectures

  - **Consensus & Coordination**
    - Leader Election (Bully Algorithm, Raft, Paxos)
    - Two-Phase Commit (2PC) & Three-Phase Commit (3PC)
    - Vector Clocks & Logical Clocks (Lamport, Hybrid Clocks)
    - Quorum-Based Approaches (Read & Write Quorums)

  - **Data Management**
    - Distributed Databases (NoSQL, NewSQL)
    - Replication Strategies (Master-Slave, Multi-Master, Read Replicas)
    - Sharding & Partitioning Strategies (Hash-based, Range-based, Geographic)
    - Eventual Consistency & Conflict Resolution (CRDTs, Gossip Protocols)

  - **Scalability & Performance**
    - Load Balancing (Round Robin, Least Connections, Weighted)
    - Caching Strategies (CDN, Redis, Memcached, Write-Through, Write-Behind)
    - Rate Limiting & Backpressure
    - High Availability & Failover Mechanisms

  - **Messaging & Event-Driven Architectures**
    - Message Queues (RabbitMQ, Kafka, SQS, Pub/Sub)
    - Event Sourcing & CQRS (Command Query Responsibility Segregation)
    - Streaming & Real-Time Processing (Apache Flink, Spark Streaming)

  - **Networking & Communication**
    - RPC vs REST vs gRPC vs GraphQL
    - API Gateway & Service Mesh (Istio, Linkerd)
    - WebSockets, SSE (Server-Sent Events), WebRTC
    - Asynchronous Messaging & Event-Driven Design

  - **Security & Reliability**
    - Zero Trust Architecture
    - Encryption (TLS, mTLS, End-to-End Encryption)
    - Authentication & Authorization (OAuth2, OIDC, JWT)
    - Fault Tolerance (Retries, Circuit Breakers, Bulkheads)
    - Observability (Tracing, Logging, Metrics with OpenTelemetry, Prometheus, ELK)

  - **Cloud & Infrastructure**
    - Infrastructure as Code (Terraform, CloudFormation, Pulumi)
    - Containerization (Docker, Kubernetes, OpenShift)
    - Serverless & FaaS (AWS Lambda, GCP Cloud Functions)
    - Multi-Cloud & Hybrid Cloud Architectures

  - **Testing & Chaos Engineering**
    - Fault Injection (Netflix Chaos Monkey, Gremlin)
    - Resilience Testing & Failure Recovery
    - Load Testing & Performance Benchmarking
    - Canary Releases & Blue-Green Deployments
```

{{< /markmap >}}
