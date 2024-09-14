+++
title= "Scaling From Zero To Millions Of Users"
tags = [ "system-design", "software-architecture", "interview", "scaling-0-to-millions" ]
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
weight= 1
bookFlatSection= true
+++
 
# Scale From Zero to Millions of Users

Here, we're building a system that supports a few users & gradually scales it to support millions.

## Single Server Setup

To start off, we're going to put everything on a single server - web app, database, cache, etc.

![single-server-setup](../images/single-server-setup.png)

### What's the Request Flow?

- User asks DNS server for the IP of my site (i.e. `api.mysite.com -> 15.125.23.214`). Usually, DNS is provided by third-parties instead of hosting it yourself.
- HTTP requests are sent directly to server (via its IP) from your device.
- Server returns HTML pages or JSON payloads, used for rendering.

Traffic to the web server comes from either a web application or a mobile application:

- Web applications use a combo of server-side languages (i.e. Java, Python) to handle business logic & storage. Client-side languages (i.e. HTML, JS) are used for presentation.
- Mobile apps use the HTTP protocol for communication between mobile & the web server. JSON is used for formatting transmitted data.

## Database

As the user base grows, storing everything on a single server is insufficient. We can separate our database on another server so that it can be scaled independently from the web tier.

![database-separate-from-web](../images/database-separate-from-web.png)

### Which Databases to Use?

You can choose either a traditional relational database or a non-relational (NoSQL) one.

- Most popular relational DBs: MySQL, Oracle, PostgreSQL.
- Most popular NoSQL DBs: CouchDB, Neo4J, Cassandra, HBase, DynamoDB.

Relational databases represent & store data in tables & rows. You can join different tables to represent aggregate objects. NoSQL databases are grouped into four categories: key-value stores, graph stores, column stores & document stores.

For most use cases, relational databases are the best option. If not suitable, explore NoSQL databases, which might be better if:

- Application requires super-low latency.
- Data is unstructured or you don't need any relational data.
- You only need to serialize/deserialize data (JSON, XML, YAML, etc.).
- You need to store a massive amount of data.

## Vertical Scaling vs. Horizontal Scaling

- **Vertical scaling** (scale up): Adding more power to your servers (CPU, RAM, etc.).
- **Horizontal scaling** (scale out): Adding more servers to your pool of resources.

### Advantages of Horizontal Scaling

- Can handle larger traffic volumes.
- Avoids the hard limits of vertical scaling.
- Provides failover and redundancy.

## Load Balancer

A load balancer evenly distributes incoming traffic among web servers in a load-balanced set.

![load-balancer-example](../images/load-balancer-example.png)

### How It Works?

- If one server goes down, all traffic is routed to another server.
- More servers can be added to handle spikes in traffic.

## Database Replication

Database replication is usually achieved via master/slave replication (nowadays called primary/secondary replication). A master database supports writes, while slave databases store copies and support read operations.

![master-slave-replication](../images/master-slave-replication.png)

### Advantages

- **Better performance:** Enables more read queries to be processed in parallel.
- **Reliability:** If one database fails, data is still preserved.
- **High availability:** Data remains accessible as long as one instance is operational.

If a master or slave database goes offline, the system promotes a new master and adjusts slaves accordingly.

![master-slave-db-replication](../images/master-slave-db-replication.png)

### Updated Request Lifecycle

1. User gets the IP address of the load balancer from DNS.
2. User connects to the load balancer via IP.
3. HTTP request is routed to server 1 or server 2.
4. Web server reads user data from a slave database or routes data modifications to the master database.

## Cache

A cache is a temporary storage that stores frequently accessed data or results of expensive computations. In our web application, caching can reduce the need for expensive database queries.

### Cache Tier

The cache tier is a temporary storage layer that can be scaled independently from the database.

![cache-tier](../images/cache-tier.png)

### Considerations for Using Cache

- When to use: Useful when data is read frequently but modified infrequently.
- Expiration policy: Controls when cached data expires. Too short leads to frequent DB queries, too long risks stale data.
- Consistency: Ensures the cache is in sync with the database.
- Mitigating failures: Provision servers with extra memory or set them up in multiple locations.
- Eviction policy: Determines what happens when the cache is full. Common policies are LRU, LFU, FIFO.

## Content Delivery Network (CDN)

A CDN is a network of geographically dispersed servers, used for delivering static content (images, HTML, CSS, JS files).

Whenever a user requests static content, the CDN server closest to the user serves it.

![cdn](../images/cdn.png)

### Considerations for Using CDN

- **Cost:** CDNs are managed by third parties, so be mindful of cost.
- **Cache expiry:** Setting an appropriate cache expiry to balance request frequency and data staleness.
- **CDN fallback:** Clients should have a way to fall back if there's a temporary outage.
- **Invalidation:** Use APIs or object versioning to invalidate cache.

## Stateless Web Tier

To scale our web tier, we need to make it stateless by storing session data in persistent storage (relational database or NoSQL).

### Stateful Architecture

Stateful servers remember client data across different requests, making them less flexible.

![stateful-servers](../images/stateful-servers.png)

### Stateless Architecture

Stateless servers don't store user data, allowing HTTP requests to be served by any server.

![stateless-architecture](../images/stateless-architecture.png)

## Data Centers

Clients are geo-routed to the nearest data center based on their IP address.

![data-centers](../images/data-centers.png)

In the event of an outage, traffic is rerouted to a healthy data center.

![data-center-failover](../images/data-center-failover.png)

## Message Queues

Message queues enable asynchronous communication and decouple producers from consumers.

![message-queue](../images/message-queue.png)

Example use-case: Photo processing tasks.

![photo-processing-queue](../images/photo-processing-queue.png)

## Logging, Metrics, and Automation

As the web application grows, monitoring tooling becomes essential.

- **Logging:** Error logs can be emitted to a data store.
- **Metrics:** Collect various types of metrics to monitor system health.
- **Automation:** Continuous integration and deployment detect problems early and improve productivity.

## Database Scaling

There are two approaches to database scaling:

### Vertical Scaling

Adding more resources (CPU, RAM, etc.) to your database nodes. This approach has hardware limits and can be expensive.

### Horizontal Scaling

Add more database nodes instead of upgrading a single one. Sharding is a common horizontal scaling technique.

![database-sharding](../images/database-sharding.png)

In this setup, data is distributed across shards using a partition key.

![user-data-in-shards](../images/user-data-in-shards.png)

## Millions of Users and Beyond

Scaling a system is iterative. Here are key takeaways:

- Keep the web tier stateless.
- Build redundancy at every layer.
- Cache frequently accessed data.
- Support multiple data centers.
- Host static assets in CDNs.
- Scale your data tier via sharding.
- Split your big application into multiple services.
- Monitor your system & use automation.
