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

> TODO: Add diagrams

# Instant Messaging Platform Design

This document outlines the design considerations for a highly scalable instant messaging platform intended for real-time communication among millions of users. The platform must handle various communication types, ensure high availability, and deliver messages with minimal latency.

## Functional Requirements

### Communication Types
1. **Direct 1-to-1 Messaging**
   - Users can send messages directly to each other.
   - Only the two participating users can read and respond to the messages.

2. **Group Messaging**
   - Users can create group chats with multiple participants.
   - All users within the group can read and respond to messages.

3. **Dedicated Channels**
   - Users can create channels for specific topics.
   - Other users can join, leave, and invite others to these channels.

### Media and Persistence
- **Text Messages Only**: Initially, the system will support text-based communication without media sharing or video calls.
- **Message Persistence**: Messages should be retained and accessible to users when they return online, ensuring they receive all missed messages.

### Additional Features
- **User Presence**: Display whether a user is currently online when their profile is accessed.
- **Typing Indicators, Editing, and Deleting Messages**: These features are out of scope for the current design phase.

## Non-Functional Requirements

### Scalability
- **User Base**: Support millions of daily active users with long connection durations.
- **Message Throughput**: Handle hundreds of thousands of messages per second, with each message up to 10,000 characters.
- **Group and Channel Size**: Support groups and channels with up to hundreds of thousands of participants.

### High Availability
- Aim for 99.99% availability, especially for paying customers, to ensure uninterrupted service.

### Performance
- **Latency**: 
  - Aim for 1 second at the 99th percentile for message delivery to online users.
  - Aim for 2 seconds for offline users to receive missed messages upon returning online.

## System API and Use Cases

### User Registration and Authentication
- **Sign Up**: Users provide name, email, password, public name/username, and optionally a profile image.
  - **Response**: Authentication token and user ID.

- **Login**: Users authenticate via mobile or web app to receive an authentication token and user ID.

### Messaging
- **Direct Messaging**:
  - **Search**: Find the user ID of the target recipient.
  - **Send Message**: Include target user IDs and message text.
  - **System Response**: New chat ID or group ID, message persistence, and delivery to online users.

- **Group Messaging**:
  - **Create Group**: Users find and add target user IDs.
  - **Send Message**: New chat ID is created and returned.
  - **System Response**: Message delivery to all group participants.

- **Channel Messaging**:
  - **Create Channel**: Users receive a channel ID and URL.
  - **Join Channel**: Other users can join via the provided URL.
  - **Send Message**: Message stored, then delivered to online users in the channel.

## System Design

### Communication Topology

1. **Peer-to-Peer Communication**
   - **Pros**: 
     - Scales efficiently for 1-to-1 communication.
     - Reduces latency since messages are sent directly between users.
   - **Cons**: 
     - Inefficient for group messaging and channels due to high connection and data transmission requirements.
     - Does not support message persistence for offline users.

2. **Centralized Communication**
   - **Approach**: 
     - All communication is routed through centralized servers.
     - Centralized servers manage message delivery, which simplifies handling group chats and channels.
   - **Benefits**: 
     - Supports message persistence for offline users.
     - Reduces client-side complexity by handling message distribution centrally.
   - **Challenges**: 
     - Requires efficient server infrastructure to handle high message throughput and maintain low latency.

### Network Protocol

1. **Polling (Naive Approach)**
   - **Description**: Users periodically send requests to the server to check for new messages.
   - **Issues**:
     - Overwhelms the system with frequent requests.
     - Inefficient and costly due to unnecessary requests when there are no new messages.

2. **Bidirectional Communication (Preferred Approach)**
   - **Description**: Utilizes a protocol that supports full duplex communication, allowing both the server and client to send messages independently.
   - **Benefits**:
     - Reduces server load by maintaining persistent connections.
     - Efficiently handles high message throughput with low resource usage when connections are idle.
   - **Implementation**: 
     - Establish a bidirectional connection for real-time message exchange.
     - Ensure minimal latency and efficient message delivery.

### System Components

1. **User Service**
   - **Function**: Manages user registration, authentication, and profile information.
   - **Database**: Stores user credentials, profile information, and links to profile images in an object store.

2. **Web Application Service**
   - **Function**: Handles user interaction for sign-up and login via web browsers.

3. **Mobile Application Service**
   - **Function**: Provides user interfaces and interactions for mobile users.

4. **Messaging Service**
   - **Function**: Manages real-time message delivery to users.
   - **Bidirectional Connection**: Maintains active connections for sending and receiving messages.

5. **Groups and Channels Service**
   - **Function**: Manages group and channel creation, and maintains participant lists.
   - **Database**:
     - **Groups Table**: Stores group ID and creation time.
     - **Channels Table**: Stores channel ID, name, creation time, owner, and URL.
     - **Group Participants Table**: Connects users to groups.
     - **Channel Subscriptions Table**: Connects users to channels.

6. **Chat History Service**
   - **Function**: Stores message history for retrieval and persistence.
   - **Database**: 
     - **Message Table**: Stores message ID, group/channel ID, sender ID, timestamp, and message text.

### Workflow

1. **User Signup and Login**
   - Users sign up or log in through the web or mobile application.
   - User credentials are verified by the User Service.
   - Successful login establishes a bidirectional connection with the Messaging Service.

2. **Sending Messages**
   - Direct Messages: User sends a search request to find the recipient's user ID, then sends the message through the Messaging Service.
   - Group Messages: User creates a group, and messages are sent to the Messaging Service for distribution to group participants.
   - Channel Messages: User creates or joins a channel, and messages are sent to the Messaging Service for distribution to channel subscribers.

3. **Receiving Messages**
   - Messages are delivered to users in real-time via the Messaging Service.
   - Offline users retrieve missed messages upon reconnecting.

4. **Handling Offline Users**
   - When a user goes offline and reconnects, the system retrieves recent messages from the Chat History Service.


### Scalability

1. **Service Scaling**
   - **Stateless Services**: Most services are stateless and follow the client-service model, making horizontal scaling straightforward. Each service is run as a group of instances behind a load balancer.
   - **Messaging Service**: Needs special consideration due to the nature of maintaining active connections between users.
     - **Partitioned Messaging**: If users are divided into partitions (e.g., by organization or topic), each partition can be managed by separate servers. Each server maintains an in-memory mapping of user IDs to connections.
     - **Non-Partitioned Messaging**: Requires distributing connection loads across all servers. A Connection Management Service (CMS) with a key-value store maps user IDs to messaging server addresses. 

2. **Load Distribution**
   - **Connection Management Service**: Manages user connections and mappings to servers, providing scalability by balancing loads.
   - **Message Broker**: Introduced between the messaging service and the CMS to handle incoming messages asynchronously and absorb traffic spikes.
   - **Publish-Subscribe Pattern**: Messaging services subscribe to events about connected users to ensure efficient message delivery.

3. **Database Sharding**
   - **Data Distribution**: Implement database sharding to distribute data across multiple instances, ensuring efficient data management and scalability.

4. **API Gateway**
   - **Rate Limiting**: Introduces an API gateway with rate limiting to manage message traffic and protect against high-frequency requests.

### High Availability

1. **Redundancy**: Achieved through redundant service instances and database replication.
2. **Database Replication**: Ensures data availability and reliability.

### Performance Optimization

1. **Indexing**
   - **User Table**: Create an index on the username column for fast lookups during login and message sending.
   - **Chat History Database**: Create compound indexes on group ID and message ID, and channel ID and message ID for efficient message retrieval.
   - **Groups and Channels Service**: Add hash indexes on group ID and channel ID in the respective tables for fast lookups.

2. **Caching**
   - **Recent Messages**: Use an in-memory cache to store frequently queried messages, improving retrieval performance.

### Summary

The platform design incorporates:
- **Bidirectional Communication Protocol** for enhanced scalability.
- **Connection Management Service** and **Message Broker** for efficient load handling and message delivery.
- **Database Sharding** and **API Gateway** for scalability and traffic management.
- **Indexing** and **Caching** to optimize query performance and user experience.

This design ensures a scalable, high-availability, and performant instant messaging system capable of handling millions of users effectively.
