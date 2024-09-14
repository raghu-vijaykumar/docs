+++
title= "Nearby Friends"
tags = [ "system-design", "software-architecture", "interview", "nearby-friends" ]
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
weight= 16
bookFlatSection= true
+++

# Nearby Friends

This chapter focuses on designing a scalable backend for an application that enables users to share their location and discover friends who are nearby. The major difference with the proximity chapter is that in this problem, locations constantly change, whereas in that one, business addresses more or less stay the same.

## Step 1 - Understand the Problem and Establish Design Scope

Some questions to drive the interview:
- **C:** How geographically close is considered to be "nearby"?
  - **I:** 5 miles, this number should be configurable
- **C:** Is distance calculated as straight-line distance vs. taking into consideration, e.g., a river in-between friends?
  - **I:** Yes, that is a reasonable assumption
- **C:** How many users does the app have?
  - **I:** 1 billion users, with 10% of them using the nearby friends feature
- **C:** Do we need to store location history?
  - **I:** Yes, it can be valuable for, e.g., machine learning
- **C:** Can we assume inactive friends will disappear from the feature in 10 minutes?
  - **I:** Yes
- **C:** Do we need to worry about GDPR, etc.?
  - **I:** No, for simplicity's sake

### Functional Requirements
- Users should be able to see nearby friends on their mobile app. Each friend has a distance and timestamp, indicating when the location was updated.
- The nearby friends list should be updated every few seconds.

### Non-functional Requirements
- **Low Latency:** It's important to receive location updates without too much delay.
- **Reliability:** Occasional data point loss is acceptable, but the system should be generally available.
- **Eventual Consistency:** The location data store doesn't need strong consistency. A few seconds' delay in receiving location data in different replicas is acceptable.

### Back-of-the-Envelope
Some estimations to determine potential scale:
- Nearby friends are friends within a 5-mile radius.
- Location refresh interval is 30 seconds. Human walking speed is slow, hence, no need to update location too frequently.
- On average, 100 million users use the feature every day with 10% concurrent users, i.e., 10 million.
- On average, a user has 400 friends, all of them use the nearby friends feature.
- The app displays 20 nearby friends per page.
- Location Update QPS = 10 million / 30 ≈ ~334k updates per second.

## Step 2 - Propose High-Level Design and Get Buy-In

Before exploring API and data model design, we'll study the communication protocol we'll use as it's less ubiquitous than the traditional request-response communication model.

### High-Level Design
At a high level, we want to establish effective message passing between peers. This can be done via a peer-to-peer protocol, but that's not practical for a mobile app with flaky connection and tight power consumption constraints.

A more practical approach is to use a shared backend as a fan-out mechanism towards friends you want to reach:
![fan-out-backend](../images/fan-out-backend.png)

What does the backend do?
- Receives location updates from all active users.
- For each location update, find all active users who should receive it and forward it to them.
- Do not forward location data if the distance between friends is beyond the configured threshold.

This sounds simple, but the challenge is to design the system for the scale we're operating with.

We'll start with a simpler design at first and discuss a more advanced approach in the deep dive:
![simple-high-level-design](../images/simple-high-level-design.png)

- The load balancer spreads traffic across REST API servers and bidirectional WebSocket servers.
- The REST API servers handle auxiliary tasks such as managing friends, updating profiles, etc.
- The WebSocket servers are stateful servers, which forward location update requests to respective clients. They also manage seeding the mobile client with nearby friends' locations at initialization (discussed in detail later).
- Redis location cache is used to store the most recent location data for each active user. There is a TTL set on each entry in the cache. When the TTL expires, the user is no longer active, and their data is removed from the cache.
- User database stores user and friendship data. Either a relational or NoSQL database can be used for this purpose.
- Location history database stores a history of user location data, not necessarily used directly within the nearby friends feature but instead used to track historical data for analytical purposes.
- Redis Pub/Sub is used as a lightweight message bus, which enables different topics for each user channel for location updates.
![redis-pubsub-usage](../images/redis-pubsub-usage.png)

In the above example, WebSocket servers subscribe to channels for the users connected to them and forward location updates whenever they receive them to appropriate users.

### Periodic Location Update
Here's how the periodic location update flow works:
![periodic-location-update](../images/periodic-location-update.png)

- Mobile client sends a location update to the load balancer.
- Load balancer forwards location update to the WebSocket server's persistent connection for that client.
- WebSocket server saves location data to the location history database.
- Location data is updated in the location cache. The WebSocket server also saves location data in-memory for subsequent distance calculations for that user.
- WebSocket server publishes location data in the user's channel via Redis Pub/Sub.
- Redis Pub/Sub broadcasts location update to all subscribers for that user channel, i.e., servers responsible for the friends of that user.
- Subscribed WebSocket servers receive the location update, calculate which users the update should be sent to, and send it.

Here's a more detailed version of the same flow:
![detailed-periodic-location-update](../images/detailed-periodic-location-update.png)

On average, there are going to be 40 location updates to forward as a user has 400 friends on average and 10% of them are online at a time.

### API Design
WebSocket Routines we'll need to support:
- **Periodic Location Update:** User sends location data to the WebSocket server.
- **Client Receives Location Update:** Server sends friend location data and timestamp.
- **WebSocket Client Initialization:** Client sends user location; server sends back nearby friends' location data.
- **Subscribe to a New Friend:** WebSocket server sends a friend ID; the mobile client is supposed to track e.g., when a friend appears online for the first time.
- **Unsubscribe a Friend:** WebSocket server sends a friend ID; the mobile client is supposed to unsubscribe from due to e.g., a friend going offline.

HTTP API - traditional request/response payloads for auxiliary responsibilities.

### Data Model
- The location cache will store a mapping between `user_id` and `lat, long, timestamp`. Redis is a great choice for this cache as we only care about current location and it supports TTL eviction, which we need for our use case.
- Location history table stores the same data but in a relational table with the four columns stated above. Cassandra can be used for this data as it is optimized for write-heavy loads.

## Step 3 - Design Deep Dive

Let's discuss how we scale the high-level design so that it works at the scale we're targeting.

### How Well Does Each Component Scale?
- **API Servers:** Can be easily scaled via autoscaling groups and replicating server instances.
- **WebSocket Servers:** We can easily scale out the WebSocket servers, but we need to ensure we gracefully shut down existing connections when tearing down a server. For example, we can mark a server as "draining" in the load balancer and stop sending connections to it before being finally removed from the server pool.
- **Client Initialization:** When a client first connects to a server, it fetches the user's friends, subscribes to their channels on Redis Pub/Sub, fetches their location from the cache, and finally forwards it to the client.
- **User Database:** We can shard the database based on `user_id`. It might also make sense to expose user/friends data via a dedicated service and API, managed by a dedicated team.
- **Location Cache:** We can shard the cache easily by spinning up several Redis nodes. Also, the TTL puts a limit on the maximum memory we could have taken up at a time. But we still want to handle the large write load.
- **Redis Pub/Sub Server:** We leverage the fact that no memory is consumed if there are channels initialized but not in use. Hence, we can pre-allocate channels for all users who use the nearby friends feature to avoid having to deal with e.g., bringing up a new channel when a user comes online and notifying active WebSocket servers.

### Scaling Deep-Dive on Redis Pub/Sub Component
We will need around 200 GB of memory to maintain all Pub/Sub channels. This can be achieved by using 2 Redis servers with 100 GB each.

Given that we need to push ~14 million location updates per second, we will, however, need at least 140 Redis servers to handle that amount of load, assuming that a single server can handle ~100k pushes per second.

Hence, we'll need a distributed Redis server cluster to handle the intense CPU load.

In order to support a distributed Redis cluster, we'll need to utilize a service discovery component, such as Zookeeper or etcd, to keep track of which servers are alive.

What we need to encode in the service discovery component is this data:
![channel-distribution-data](../images/channel-distribution-data.png)

WebSocket servers use that encoded data, fetched from Zookeeper, to determine where a particular channel lives. For efficiency, the hash ring data can be cached in-memory on each WebSocket server.

In terms of scaling the server cluster up or down, we can set up a daily job to scale the cluster as needed based on historical traffic data. We can also over-provision the cluster to handle the unexpected spikes in usage.

The redis cluster can be treated as a stateful storage server as there is some state maintained for the channels and there is a need for coordination with subscribers so that they hand-off to newly provisioned nodes in the cluster.

We have to be mindful of some potential issues during scaling operations:
- There will be a lot of resubscription requests from the web socket servers due to channels being moved around
- Some location updates might be missed from clients during the operation, which is acceptable for this problem, but we should still minimize it from happening. Consider doing such operation when traffic is at lowest point of the day.
- We can leverage consistent hashing to minimize amount of channels moved in the event of adding/removing servers
![consistent-hashing-nearby-friends](../images/consistent-hashing-nearby-friends.png)

## Adding/removing friends
Whenever a friend is added/removed, websocket server responsible for affected user needs to subscribe/unsubscribe from the friend's channel.

Since the "nearby friends" feature is part of a larger app, we can assume that a callback on the mobile client side can be registered whenever any of the events occur and the client will send a message to the websocket server to do the appropriate action.

## Users with many friends
We can put a cap on the total number of friends one can have, eg facebook has a cap of 5000 max friends.

The websocket server handling the "whale" user might have a higher load on its end, but as long as we have enough web socket servers, we should be okay.

## Nearby random person
What if the interviewer wants to update the design to include a feature where we can occasionally see a random person pop up on our nearby friends map?

One way to handle this is to define a pool of pubsub channels, based on geohash:
![geohash-pubsub](../images/geohash-pubsub.png)

Anyone within the geohash subscribes to the appropriate channel to receive location updates for random users:
![location-updates-geohash](../images/location-updates-geohash.png)

We could also subscribe to several geohashes to handle cases where someone is close but in a bordering geohash:
![geohash-borders](../images/geohash-borders.png)

## Alternative to Redis pub/sub
An alternative to using Redis for pub/sub is to leverage Erlang - a general programming language, optimized for distributed computing applications.

With it, we can spawn millions of small, erland processes which communicate with each other. We can handle both websocket connections and pub/sub channels within the distributed erlang application.

A challenge with using Erlang, though, is that it's a niche programming language and it could be hard to source strong erlang developers.

### Fault-Tolerance
- **WebSocket Servers:** We need to handle WebSocket server failures gracefully. We'll have to make sure that connections are properly managed and transferred to a new server when a server fails.
- **Cache Failures:** If Redis fails, the application should be able to handle stale data and fall back on periodic full cache updates.
- **Redis Pub/Sub:** We should use multiple Redis servers to handle high volumes of data. Failures of individual Redis instances should not affect the overall system.

### Security Considerations
- **Data Privacy:** Ensure encrypted transmission of data between servers and clients.
- **Authentication and Authorization:** Use secure tokens for client-to-server authentication and authorization.
- **Rate Limiting:** Protect the servers from abuse by implementing rate limits on the API endpoints.

### Monitoring and Maintenance
- **Monitoring:** Use monitoring tools to keep track of system health, such as Datadog or Prometheus.
- **Alerts:** Set up alerts for server performance, error rates, and other key metrics to address issues proactively.
- **Logging:** Implement comprehensive logging for debugging and troubleshooting.

In conclusion, by leveraging scalable components like Redis, distributed systems principles, and robust monitoring, we can build an efficient and reliable nearby friends feature for large-scale applications.
