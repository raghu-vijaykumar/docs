+++
title= "Rate Limiter"
tags = [ "system-design", "software-architecture", "interview", "rate-limiter" ]
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
weight= 3
bookFlatSection= true
+++

# Design a Rate Limiter

The rate limiter's purpose in a distributed system is to control the rate of traffic sent from clients to a given server. It controls the maximum number of requests allowed in a given time period. If the number of requests exceeds the threshold, the extra requests are dropped by the rate limiter.

### Examples
* Users can write no more than 2 posts per second.
* You can create a maximum of 10 accounts per day from the same IP.
* You can claim rewards a maximum of 10 times per week.

Almost all APIs have some sort of rate limiting - for example, Twitter allows a maximum of 300 tweets per 3 hours.

### Benefits of Using a Rate Limiter
* **Prevents DoS attacks.**
* **Reduces cost** - fewer servers are allocated to lower-priority APIs. Additionally, there might be downstream dependencies that charge on a per-call basis (e.g., making a payment, retrieving health records, etc.).
* **Prevents servers from getting overloaded.**

---

## Step 1 - Understand the Problem and Establish Design Scope

There are multiple techniques to implement a rate limiter, each with its pros and cons.

### Example Candidate-Interviewer Conversation
* **Candidate**: What kind of rate limiter are we designing? Client-side or server-side?
* **Interviewer**: Server-side.
* **Candidate**: Does the rate limiter throttle API requests based on IP, user ID, or something else?
* **Interviewer**: The system should be flexible enough to support different throttling rules.
* **Candidate**: What's the scale of the system? Startup or big company?
* **Interviewer**: It should handle a large number of requests.
* **Candidate**: Will the system work in a distributed environment?
* **Interviewer**: Yes.
* **Candidate**: Should it be a separate service or a library?
* **Interviewer**: Up to you.
* **Candidate**: Do we need to inform throttled users?
* **Interviewer**: Yes.

### Summary of Requirements
* Accurately limit excess requests.
* Low latency & minimal memory usage.
* Distributed rate limiting.
* Exception handling.
* High fault tolerance - if the cache server goes down, the rate limiter should continue functioning.

---

## Step 2 - Propose High-Level Design and Get Buy-In

We'll stick with a simple client-server model for simplicity.

### Where to Put the Rate Limiter?
It can be implemented either client-side, server-side, or as middleware.

#### Client-Side
Unreliable, because client requests can easily be forged by malicious actors. Additionally, we might not have control over the client implementation.

#### Server-Side
![Server-Side Rate Limiter](../images/server-side-rate-limiter.png)

#### As Middleware Between Client and Server
![Middleware Rate Limiter](../images/middleware-rate-limiter.png)

How it works, assuming 2 requests per second are allowed:
![Middleware Rate Limiter Example](../images/middleware-rate-limiter-example.png)

In cloud microservices, rate limiting is usually implemented in the API Gateway. This service supports rate limiting, SSL termination, authentication, IP whitelisting, serving static content, etc.

### Server-Side vs. API Gateway
Where should the rate limiter be implemented? On the server-side or in the API gateway?

It depends on several factors:
* **Current tech stack** - if implemented server-side, your language should be sufficient enough to support it.
* **Control** - server-side rate limiting gives more control over the algorithm.
* **Existing API Gateway** - if one exists, it might be easier to add rate limiting there.
* **Third-party solutions** - consider using an off-the-shelf solution if building your own takes too much time or resources.

---

## Algorithms for Rate Limiting

There are multiple algorithms for rate limiting, each with its pros and cons.

### Token Bucket Algorithm
A simple, well-understood algorithm commonly used by companies like Amazon and Stripe for throttling their APIs.
![Token Bucket Algorithm](../images/token-bucket-algo.png)

**How it works:**
* A container with predefined capacity holds tokens.
* Tokens are periodically added to the bucket.
* Once full, no more tokens are added.
* Each request consumes one token.
* If no tokens are left, the request is dropped.

![Token Bucket Algorithm Explained](../images/token-bucket-algo-explained.png)

**Parameters:**
* **Bucket size** - the maximum number of tokens allowed in the bucket.
* **Refill rate** - the number of tokens added to the bucket every second.

**Pros:**
* Easy to implement.
* Memory efficient.
* Supports short bursts of traffic.

**Cons:**
* Parameters might be difficult to tune properly.

---

### Leaking Bucket Algorithm
Similar to the token bucket algorithm but processes requests at a fixed rate.

**How it works:**
* When a request arrives, the system checks if the queue is full. If not, the request is added to the queue; otherwise, it is dropped.
* Requests are pulled from the queue and processed at regular intervals.

![Leaking Bucket Algorithm](../images/leaking-bucket-algo.png)

**Pros:**
* Memory efficient.
* Ensures stable request processing.

**Cons:**
* Bursts of traffic fill the queue with old requests, delaying newer ones.

---

### Fixed Window Counter Algorithm
This algorithm divides time into fixed windows and maintains a counter for each window.

**How it works:**
* Each request increments the counter.
* Once the counter reaches the threshold, subsequent requests in that window are dropped.

![Fixed Window Counter Algorithm](../images/fixed-window-counter-algo.png)

**Pros:**
* Memory efficient.
* Simple and easy to understand.

**Cons:**
* Bursts of traffic at the edges can allow more requests than permitted.

---

### Sliding Window Log Algorithm
This resolves the issues of the fixed window algorithm by using a sliding time window.

**How it works:**
* The algorithm keeps track of request timestamps. When a request comes in, it removes outdated timestamps and adds the new one.
* If the number of timestamps exceeds the threshold, the request is rejected.

![Sliding Window Log Algorithm](../images/sliding-window-log-algo.png)

**Pros:**
* Highly accurate.

**Cons:**
* High memory footprint.

---

### Sliding Window Counter Algorithm
A hybrid approach combining fixed and sliding window algorithms.

**How it works:**
* Maintains a counter for each time window.
* Derives a sliding window counter based on overlap between previous and current windows.

![Sliding Window Counter Algorithm](../images/sliding-window-counter-algo.png)

**Pros:**
* Smooths out traffic spikes.
* Memory efficient.

**Cons:**
* Not 100% accurate but error rate is extremely low (~0.003%).

---

## High-Level Architecture

We'll use an in-memory cache (e.g., Redis) as it's more efficient than a database for storing rate-limiting buckets.
![High-Level Architecture](../images/high-level-architecture.png)

---

## Step 3 - Design Deep Dive

### Rate Limiting Rules
Example of rate limiting rules used by Lyft for sending marketing messages:
![Lyft Rate Limiting Rules](../images/lyft-rate-limiting-rules.png)

---

### Exceeding the Rate Limit
When a request is rate limited, a `429 Too Many Requests` error code is returned. Optionally, rate-limited requests can be enqueued for future processing.

We can also include additional HTTP headers to provide metadata:
```http
X-Ratelimit-Remaining: The remaining number of allowed requests within the window.
X-Ratelimit-Limit: The maximum number of calls the client can make per time window.
X-Ratelimit-Retry-After: The number of seconds to wait before making another request.
```

## Rate Limiter in a Distributed Environment

How will we scale the rate limiter beyond a single server?  
There are several challenges to consider, including race conditions and synchronization issues.

### Race Condition
In a distributed setup, counters might not be updated correctly when multiple instances are mutating them at the same time, leading to inconsistencies.

#### Solution: Use Locks or Redis
Locks are a typical way to solve this issue, but they are costly and might impact performance. Alternatively, using Lua scripts or Redis sorted sets can solve race conditions efficiently by ensuring that all instances operate on shared counters in a synchronized manner.

![race-condition](../images/race-condition.png)

### Synchronization Issue
If user information is stored in the application memory, the rate limiter becomes stateful. This would require sticky sessions to ensure that all requests from the same user are handled by the same rate limiter instance. This can complicate scaling and performance.

![synchronization-issue](../images/synchronization-issue.png)

#### Solution: Centralized Data Store
One way to avoid synchronization problems is to use a centralized data store like Redis. By storing rate limiter counters in Redis, the rate limiter instances can remain stateless. This simplifies scaling, as requests can be handled by any instance without the need for sticky sessions.

![redis-centralized-data-store](../images/redis-centralized-data-store.png)


## Performance Optimization
- Multi-data center setup - allows users to interact with instances closer to their location.
- Eventual consistency - avoid excessive locking by using eventual consistency as a synchronization model.

## Monitoring
After deployment, we need to monitor:
- The effectiveness of the rate-limiting algorithm.
- The effectiveness of the rate-limiting rules.
If too many requests are dropped, it may be necessary to tune the rules or algorithm parameters.

## Step 4 - Wrap Up
We discussed several rate-limiting algorithms:

- Token Bucket - supports traffic bursts.
- Leaking Bucket - ensures consistent request processing.
- Fixed Window - divides time into explicit windows.
- Sliding Window Log - highly accurate but memory intensive.
- Sliding Window Counter - balances accuracy and memory efficiency.

## Additional Topics
If time permits:

- Hard vs. soft rate limiting:
  - Hard - requests cannot exceed the specified threshold.
  - Soft - requests can exceed the threshold for a limited time.
- Rate limiting at different layers (L7 vs. L3).
- Client-side measures to avoid being rate limited:
  - Cache responses.
  - Add back-off and retry logic.
