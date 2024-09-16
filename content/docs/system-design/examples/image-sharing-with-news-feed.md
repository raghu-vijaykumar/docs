+++
title= "Image Sharing with News Feed"
tags = [ "system-design", "software-architecture", "interview", "news-feed" ]
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
weight= 8
bookFlatSection= true
+++

> TODO: Update this later to handle text and video posts, with relavant diagrams.

# Image Sharing Social Media Platform Design

This is sample design for any kind of social media platform like Instagram, Pinterest, Facebook, tiktok, etc.

## 1. System Overview

This document outlines the design of a highly scalable image-sharing social media platform. The system is designed to support billions of users globally, with a focus on high availability, fault tolerance, and low-latency performance.

## 2. Functional Requirements

### 2.1 User Registration
- **User Data**: Users must provide a first name, last name, email, username, password, and profile image.
- **Optional Data**: Age, location, and other fields (future expansion).
  
### 2.2 User Interactions
- **Post Images**: Users can upload and share images.
- **User Search**: Users can search for other users using first name, last name, username, or other attributes.
- **View Public Profiles**: Users can view the public profile and images of other users.
- **Follow/Unfollow**: Users can follow and unfollow other users.

### 2.3 News Feed
- **Personalized Timeline**: Users will see a timeline of images posted by the accounts they follow.
- **Sorting**: Images in the timeline will be sorted by the timestamp of when they were posted, with the most recent images appearing first.

## 3. Non-Functional Requirements

### 3.1 Scalability
- Designed to handle billions of users and process petabytes of new data daily.

### 3.2 Availability
- Targeting **99.99% availability** to avoid downtime that could lead to revenue loss through ads or real-time event sharing.

### 3.3 Performance
- **General Interactions**: End-to-end latency of 500 milliseconds at the 99th percentile.
- **News Feed**: Response time under 1000 milliseconds at the 99th percentile for loading the user's timeline.

## 4. System API

### 4.1 User Registration
- **GET /register**: Loads the registration page.
- **POST /register**: Submits user data (first name, last name, email, username, password, profile image).

### 4.2 Login
- **POST /login**: Submits username and password for authentication.

### 4.3 Image Posting
- **POST /user/{userId}/post**: Allows a user to upload an image.

### 4.4 User Search
- **GET /search**: Search for users by first name, last name, or username (with pagination).

### 4.5 Follow/Unfollow
- **POST /user/{userId}/follow**: Allows one user to follow another.
- **POST /user/{userId}/unfollow**: Allows one user to unfollow another.

### 4.6 News Feed
- **GET /user/{userId}/newsfeed**: Retrieves the personalized news feed for the user, showing images from followed users (with pagination).

## 5. Quality Attributes

### 5.1 Scalability
- System needs to scale horizontally to handle billions of users.

### 5.2 High Availability
- **99.99% uptime** achieved through redundancy, failover mechanisms, and globally distributed data centers.

### 5.3 Low Latency
- **500ms latency** for most user interactions and **1000ms response time** for loading the news feed.

### 5.4 API Pagination
- All endpoints that could return large datasets (such as user search and news feed) will implement **pagination** to ensure fast response times.

## Large Scale Image Sharing Social Media Platform Architecture

### 1. **Web Application Service**
   - Handles initial user requests.
   - Determines whether to send a login or registration page based on the user's authentication status.

### 2. **User Service**
   - Stores and manages user data in a NoSQL document store.
   - Data includes both public (username, profile image, etc.) and private (email, password, phone) information.
   - Profile images are stored in an **Object Store** with a URL reference in the database.
   - Handles:
     - **User Registration**: Stores image in object store, generates a user ID, and authentication token.
     - **User Login**: Authenticates the user and returns the user ID and token.

### 3. **Object Store**
   - Stores binary objects like profile and post images.
   - Users' database stores only the URL pointing to the image in the object store.

### 4. **Post Service**
   - Manages posts (currently images) in a SQL database.
   - Each post contains:
     - Post ID
     - User ID
     - Image URL
     - Timestamp
     - Post type (for future extensibility)
   - Upon image upload, the post service stores the image in the object store and records it in the post database.

### 5. **Search Service**
   - Dedicated to handling user searches (by username, first name, etc.).
   - Optimized text search via a NoSQL database with a search engine.
   - Keeps search data in sync with the user database using an **event-driven architecture**.
     - User updates and registrations are published as events and consumed by the search service.

### 6. **Follow Service**
   - Tracks user follow and unfollow relationships in the user database.
   - Stores pairs of follower-user ID and target-user ID.

### 7. **Timeline Service**
   - Stores a pre-sorted timeline of posts for each user.
   - Follows a **Materialized View Pattern** to cache and manage the timeline, ensuring efficient access.
   - Timeline is maintained as an in-memory key-value store, which maps user IDs to a list of posts from followed users.

### 8. **Message Broker**
   - Facilitates communication between services.
   - Publishes events like new posts, which are consumed by services like the **Timeline Service** to update user timelines asynchronously.

## API Calls and Data Flow

### 1. **User Registration**
   - User sends registration request to **Web Application Service**.
   - **User Service** stores profile image in the **Object Store**, generates a user ID, and returns an authentication token.

### 2. **User Login**
   - User sends login request to **Web Application Service**.
   - **User Service** verifies the username and password, returning the user ID and authentication token.

### 3. **Posting an Image**
   - User sends image upload request to **Post Service**.
   - **Post Service** uploads the image to the **Object Store**, stores post data in the database, and returns confirmation.

### 4. **Searching for Users**
   - User sends a search request to the **Search Service**.
   - **Search Service** queries its own NoSQL database optimized for text search.

### 5. **Follow/Unfollow Users**
   - User sends follow/unfollow request to **Web Application Service**, which forwards it to **User Service**.
   - **User Service** updates the follow/unfollow status in the database.

### 6. **Fetching Timeline**
   - User sends a timeline request to **Timeline Service**.
   - **Timeline Service** retrieves pre-sorted posts from its in-memory store and returns post URLs to the user.

## Key Design Patterns

### 1. **Event-Driven Architecture**
   - Used to keep services like **Search Service** and **Timeline Service** in sync with updates from other services.
   - Facilitated by the **Message Broker**.

### 2. **Materialized View Pattern**
   - Used by the **Timeline Service** to maintain pre-sorted, ready-to-serve timelines for each user.

### 3. **Seekers Pattern**
   - Used to efficiently pull and display timeline data by pre-building and caching user timelines.


## 7. Scalability

### 7.1 Application Scaling
- **Load Balancers**: All services, including the web application and user services, will be placed behind load balancers. This allows for dynamic scaling by running multiple instances of each service based on user demand.
- **User Data Storage**: 
  - **NoSQL Document Store**: User data is stored in a distributed NoSQL database.
  - **Sharding**: The database is sharded using a hash strategy on the user ID to evenly distribute user data across multiple instances. Shards can be added dynamically as more users join the platform.
- **Followers Collection**: The same sharding strategy (hashing by follower user ID) is applied to efficiently manage user-follow relationships.

### 7.2 Timeline and Posts Scaling
- **Timeline Service**: 
  - The timeline service uses a key-value store for user timelines.
  - Sharding is applied using a hash function on post IDs to distribute data across database instances.
- **Post Partitioning**: Post data is partitioned by post ID in a SQL database that supports partitioning.

### 7.3 Image Storage
- **Object Store**: Images are stored in a scalable object store optimized for high-volume data.
- **Image Compression Pipeline**: 
  - An asynchronous pipeline compresses uploaded images, reducing file size, resolution, and storage costs.
  - This optimization significantly reduces the amount of storage needed while maintaining acceptable image quality for mobile and web displays.

### 7.4 API Gateway
- **API Gateway**: All client requests are routed through an API gateway to abstract and decouple frontend code from backend services, allowing for easy service updates without impacting the client.

## 8. High Availability & Fault Tolerance

### 8.1 Service Redundancy
- **Multiple Replicas**: Each service runs as multiple instances behind a load balancer, ensuring redundancy and fault tolerance.

### 3.2 Database Replication
- **Replication**: Each database shard is replicated to prevent data loss and allow read/write operations to continue seamlessly in case of failure.

### 8.3 Multi-Zone Deployment
- **Isolation Zones**: The system is deployed across multiple isolation zones and geographical regions to mitigate the risk of regional outages.
- **Global Load Balancing**: A global load balancer directs users to the closest available data center, ensuring low-latency performance and fault tolerance.

## 4. Performance Optimization

### 9.1 Response Time Optimization
- **CDN**: A Content Delivery Network (CDN) is used to distribute static content (HTML, CSS, JavaScript) and images to edge servers, reducing response time and latency for users.

### 9.2 Timeline Service Optimization
- **Materialized View**: Cached materialized views of user timelines are used to minimize query load and improve response times.
- **Influencer Post Optimization**:
  - **Threshold for Influencers**: Users with a large number of followers are marked as influencers.
  - **Influencer Post Collection**: Instead of updating millions of timelines when an influencer posts, a separate collection stores influencer posts, which are merged with the user's timeline during read operations.


