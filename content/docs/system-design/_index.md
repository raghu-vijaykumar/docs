+++
title = "System Design"
tags = [ "system-design", "architecture", "scalability", "reliability", "performance", "patterns" ]
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
# Preserve existing ordering/collapse behavior from the old page
weight = 1
bookCollapseSection = true
+++

# System Design

{{< markmap >}}

```markmap
# System Design
  - **Fundamentals**
    - Requirements & Use Cases
      - Actors, Use Cases, User Flows
      - Sequence Diagrams & API Identification
    - Quality Attributes (NFRs)
      - Performance, Scalability, Availability
      - Maintainability, Security, Cost
    - System Constraints
      - Technical, Business, Legal
    - SLOs, SLIs, SLAs
  - **Architecture Styles**
    - Monolith
    - Layered Architecture
    - Client–Server
    - Microservices
    - Service-Oriented Architecture (SOA)
    - Event-Driven Architecture (EDA)
    - Peer-to-Peer (P2P)
    - Space-Based Architecture
  - **Communication**
    - RPC (gRPC, Thrift)
    - REST
    - GraphQL
    - Messaging (Queues, Pub/Sub)
    - WebSockets/Streaming
  - **Data**
    - Relational, NoSQL, Object Storage, File Systems
    - Indexing
    - Replication
    - Partitioning/Sharding
    - Caching & Materialized Views
  - **Reliability & Operations**
    - Fault Tolerance & Redundancy
    - Resilience (Circuit Breaker, Retry, Timeouts, Bulkhead)
    - Observability (Metrics, Logs, Tracing)
    - Deployment (Blue/Green, Canary, Rolling)
  - **Patterns & Anti-patterns**
    - Architectural Patterns (Scalability, Reliability, Performance)
    - Design Patterns (GoF)
    - Anti-Patterns
    - SOLID Principles
  - **Examples**
    - Business Systems
    - Cloud Architectures
    - Data Replication
    - AI Agents
```

{{< /markmap >}}

## Overview and Navigation

This System Design section is the entry point for pragmatic, production-focused guidance across fundamentals, architecture styles, communication, data, reliability, patterns, and worked examples. Use the map above to orient, then dive into focused guides below.

- Architectural Patterns: scalability, reliability, performance, and deployment tactics — [Go to architectural patterns](architectural-patterns/)
- Design Patterns (GoF): proven object-level patterns with runnable examples — [Design patterns](design-patterns/)
- SOLID Principles: foundational principles for extensible, maintainable design — [SOLID](solid-principles/)
- Anti-Patterns: what to avoid; common pitfalls and remediation strategies — [Anti-patterns](anti-patterns/)
- Examples: end-to-end designs across business, cloud, data replication, AI agents — [Examples](examples/)

Implementation note
- Where relevant, pages here cross-link to Distributed Systems, Databases, and Networking sections to avoid duplication and keep this area focused on trade-offs, patterns, and system-wide design decisions.

Next steps
- Start with Architectural Patterns and Anti-Patterns to establish strong defaults, then explore Design Patterns and SOLID for code-level extensibility before reviewing end-to-end Examples.
