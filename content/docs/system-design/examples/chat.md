+++
title= "Chat"
tags = [ "system-design", "software-architecture", "interview", "chat" ]
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
weight= 9
bookFlatSection= true
+++


# Design a Chat System

We'll be designing a chat system similar to Messenger, WhatsApp, etc. It's crucial to define the exact requirements since chat systems can vary significantly, such as those focused on group chats vs. one-on-one conversations.

## Step 1 - Understand the Problem and Establish Design Scope

- **C:** What kind of chat app should we design? One-on-one conversations or group chat?
- **I:** It should support both cases.
- **C:** Mobile app, web app, or both?
- **I:** Both.
- **C:** What's the app scale? Startup or massive application?
- **I:** It should support 50 million DAU (Daily Active Users).
- **C:** For group chat, what is the member limit?
- **I:** 100 people.
- **C:** What features are important? For example, attachments?
- **I:** One-on-one and group chats, online indicator, text messages only.
- **C:** Is there a message size limit?
- **I:** Text length is less than 100,000 characters.
- **C:** Is end-to-end encryption required?
- **I:** Not required, but will discuss if time permits.
- **C:** How long should chat history be stored?
- **I:** Forever.

**Summary of Features We’ll Focus On:**

- One-on-one chat with low delivery latency.
- Small group chats (100 people).
- Online presence.
- Same account can be logged in via multiple devices.
- Push notifications.
- Scale of 50 million DAU.

## Step 2 - Propose High-Level Design and Get Buy-In

### Communication Between Clients and Servers

In this system, clients can be mobile devices or web browsers. They don't connect to each other directly but are connected to a server.

**Main Functions the Chat Service Should Support:**

- Receive messages from clients.
- Find the right recipients for a message and relay it.
- If the recipient is not online, hold messages for them until they get back online.

![store-relay-message](../images/store-relay-message.png)

When clients connect to the server, they can do it via one or more network protocols. One option is HTTP, which is okay for the sender-side but not for the receiver-side.

### Protocol Options for Server-Initiated Messages

#### Polling

Polling requires the client to periodically ask the server for status updates:
![polling](../images/polling.png)

This is easy to implement but can be costly due to many requests, which often yield no results.

#### Long Polling

With long polling, clients hold the connection open while waiting for an event to occur on the server-side. This method is more efficient than polling but still has some wasted requests if users don't chat much.

![long-polling](../images/long-polling.png)

Other caveats:
- Server has no good way to determine if the client is disconnected.
- Senders and receivers might be connected to different servers.

#### WebSocket

The most common approach for bidirectional communication:
![web-sockets](../images/web-sockets.png)

The connection is initiated by the client and starts as HTTP but can be upgraded after handshake. Both clients and servers can initiate messages. 

**Caveat:** Web sockets are persistent, making the servers stateful. Efficient connection management is necessary.

### High-Level Design

Although web sockets are useful for exchanging messages, most other standard features of our chat can use the normal request/response protocol over HTTP.

Given this, our service can be broken down into three parts: stateless API, stateful WebSocket API, and third-party integration for notifications:
![high-level-design](../images/high-level-design.png)

#### Stateless Services

Traditional public-facing request/response services manage login, signup, user profile, etc. These services sit behind a load balancer, which distributes requests across a set of service replicas.

#### Stateful Service

The only stateful service is our chat service, which maintains a persistent connection with clients. Service discovery coordinates closely with chat services to avoid overload.

#### Third-Party Integration

Push notifications are essential for chat applications to notify users when they receive a message. This component is covered in the [Design a notification system chapter](../chapter11).

### Scalability

On a small scale, everything can fit on a single server. With 1 million concurrent users, assuming each connection takes 10k memory, a single server will need 10GB of memory to service them all. 

However, a single-server setup has a single point of failure, which is a red flag. It’s fine to start with a single-server design and extend it later.

![refined-high-level-design](../images/refined-high-level-design.png)

- Clients maintain a persistent WebSocket connection with a chat server for real-time messaging.
- Chat servers facilitate message sending/receiving.
- Presence servers manage online/offline status.
- API servers handle traditional request/response-based responsibilities (login, signup, profile changes, etc.).
- Notification servers manage push notifications.
- Key-value store is used for storing chat history. Offline users will see their chat history and missed messages when they go online.

### Storage

Deciding between SQL or NoSQL databases depends on read/write access patterns.

**Relational Databases:**
- Suitable for traditional data like user profiles, settings, friends list.
- Replication and sharding help meet scalability needs.

**NoSQL Databases:**
- Chat history data has specific patterns:
  - Large data volume (e.g., Facebook and WhatsApp process 60 billion messages per day).
  - Recent chats are accessed frequently.
  - Searching within chat history is necessary.
  - Read-to-write ratio is 1:1.

**Recommendation:** Use a key-value store:
- Allows easy horizontal scaling.
- Provides low latency access to data.
- Handles long-tail data better than relational databases.
- Widely adopted for chat systems (e.g., Facebook uses HBase, Discord uses Cassandra).

## Data Models

### One-on-One Chat Table

![one-on-one-chat-table](../images/one-on-one-chat-table.png)

Use `message_id` instead of `created_at` to determine message sequence since messages can be sent simultaneously.

### Group Chat Table

![group-chat-table](../images/group-chat-table.png)

In this table, `(channel_id, message_id)` is the primary key, with `channel_id` also serving as the sharding key.

**Message ID Generation:**
- IDs must be unique and sortable by time.
- Options include `auto_increment` (for relational databases), Snowflake (Twitter's algorithm for generating sortable IDs), or a local sequence number generator.

## Step 3 - Design Deep-Dive

### Service Discovery

Service discovery selects the best server based on criteria like geographic location or server capacity.

Apache Zookeeper is a popular solution for service discovery:
![service-discovery](../images/service-discovery.png)

- User A logs in to the app.
- The load balancer sends the request to API servers.
- Service discovery chooses the best chat server (e.g., chat server 2).
- User A connects to chat server 2 via WebSocket protocol.

### Message Flows

#### One-on-One Chat Flow

![one-on-one-chat-flow](../images/one-on-one-chat-flow.png)

- User A sends a message to chat server 1.
- Chat server 1 obtains a `message_id` from the ID generator.
- Chat server 1 sends the message to the "message sync" queue.
- The message is stored in a key-value store.
- If User B is online, the message is forwarded to chat server 2, where User B is connected.
- If offline, a push notification is sent via the push notification servers.
- Chat server 2 forwards the message to User B.

#### Message Synchronization Across Devices

![message-sync](../images/message-sync.png)

- When User A logs in via phone, a WebSocket is established with chat server 1.
- Each device maintains a variable called `cur_max_message_id`, tracking the latest message received on the device.
- Messages with a `message_id` greater than `cur_max_message_id` and intended for the logged-in user are considered new.

#### Small Group Chat Flow

Group chats are more complex:
![group-chat-flow](../images/group-chat-flow.png)

- User A’s message is copied to each message queue of group participants (e.g., User B and C).
- This approach is feasible for small groups as it simplifies message synchronization and is manageable for small numbers of participants.

For larger groups, consider:
- Fetching presence status when a user enters a group or refreshes the members list.

### Online Presence

Presence servers manage online/offline status in chat applications:
![user-login-online](../images/user-login-online.png)
![user-logout-offline](../images/user-logout-offline.png)

**Handling Disconnections:**
- Naive approach: Mark user as "offline" on disconnect, which is poor if users frequently reconnect.
- Improved approach: Use a heartbeat mechanism, where clients periodically send a heartbeat to presence servers. If no heartbeat is received within a timeframe, the user is marked offline:
![user-heartbeat](../images/user-heartbeat.png)

**Fanout Mechanism:**
- Presence status changes are sent to the respective queues of each friend pair:
![presence-status-fanout](../images/presence-status-fanout.png)

This method is effective for small groups. For larger groups, consider fetching status only when a user joins a group or refreshes the member list.

## Step 4 - Wrap Up

We built a chat system that supports both one-on-one and group chats. 

**System Components:**
- Chat servers (real-time messages)
- Presence servers (online/offline status)
- Push notification servers
- Key-value stores for chat history
- API servers for additional functions (login, signup, etc.)

**Additional Talking Points:**
- Extend chat app features (e.g., voice/video calls)
- Data replication and backups
- Security and compliance considerations (if encryption was added)

---
This design is a starting point. Depending on additional requirements or constraints, specific components and implementations may evolve.
