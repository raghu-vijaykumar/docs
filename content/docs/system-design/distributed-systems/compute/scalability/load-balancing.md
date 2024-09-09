+++
title= "Load Balancing"
tags = [ "system-design", "software-architecture", "distributed-systems", "load-balancing" ]
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

# Load Balancing in Distributed Systems

## Overview

Load balancing is a critical component in distributed systems, designed to improve system reliability, scalability, and performance. This document provides a summary of the need for load balancing, its benefits, and various strategies used to achieve effective load distribution.

## Need for Load Balancers

### Single Point of Failure
In a distributed system, having a single user-facing server or search coordinator can create a single point of failure. If the server goes down, users lose access to the system. As the system scales, the single server may not handle increasing loads efficiently.

### Scalability Issues
As user numbers grow, the load on a search coordinator or any central component increases. This can lead to performance bottlenecks and may exceed operating system limits due to the need for handling more connections and data.

### Solutions
To mitigate these issues:
- **Multiple Coordinators**: Implement multiple search coordinators to share the load, using a service registry to manage them.
- **Load Balancer**: Introduce a load balancer to distribute traffic evenly among multiple servers, improving reliability and allowing for scaling without exposing implementation details to users.

## Benefits of Load Balancers

1. **Performance**: Distributes network traffic to prevent any single server from becoming a bottleneck.
2. **Reliability**: Monitors server health and directs traffic only to functional servers, enhancing system availability.
3. **Scalability**: Allows for easy addition or removal of servers based on load, with auto-scaling capabilities in cloud environments.
4. **Transparency**: Hides the complexity of the underlying infrastructure from users, presenting a unified interface.

## Types of Load Balancers

1. **Hardware Load Balancers**
   - **Description**: Dedicated devices optimized for load balancing.
   - **Advantages**: High performance, reliability, and capable of handling large numbers of servers.
   - **Drawbacks**: Typically more expensive and less flexible.

2. **Software Load Balancers**
   - **Description**: Load balancing programs running on general-purpose hardware.
   - **Advantages**: Cost-effective, easier to configure and upgrade, and often available as open-source solutions (e.g., HAProxy, Nginx).
   - **Drawbacks**: May have performance limitations compared to hardware load balancers.

## Load Balancing Strategies

1. **Round-Robin**
   - **Description**: Distributes requests evenly across all servers.
   - **Use Case**: Suitable for scenarios where servers have similar capabilities and the load is evenly distributed.

2. **Weighted Round-Robin**
   - **Description**: Assigns weights to servers based on their capacity or importance.
   - **Use Case**: Useful for scenarios with varying server capacities or when gradually rolling out new versions of software.

3. **Source IP Hash**
   - **Description**: Routes requests based on a hash of the user's IP address.
   - **Use Case**: Ensures that a user's session is consistently handled by the same server, providing session stickiness.

4. **Least Connections**
   - **Description**: Directs traffic to servers with the fewest open connections.
   - **Use Case**: Effective when server load varies significantly.

5. **Weighted Response Time**
   - **Description**: Uses response time from periodic health checks to adjust server weights.
   - **Use Case**: Useful for adapting to servers with varying response times.

6. **Agent-Based Real-Time Metrics**
   - **Description**: Utilizes agents on servers to report real-time metrics such as CPU utilization, network traffic, and memory usage.
   - **Use Case**: Provides the most accurate load balancing by considering real-time server performance data.

## Networking Layers for Load Balancing

Load balancers can operate at different networking layers to manage and distribute incoming traffic to application servers. This document outlines the two primary networking layers on which load balancers function: the transport layer and the application layer.

### Transport Layer Load Balancing (Layer 4)

#### Description
- **Layer**: Transport Layer (Layer 4) in the OSI model.
- **Function**: Forwards TCP packets based on source and destination IP addresses and ports.
- **Operation**:
  - **Incoming Traffic**: Replaces source address with its own and forwards traffic to the backend server.
  - **Outgoing Traffic**: Replaces source address with its own and sends traffic back to the client.

#### Characteristics
- **Low Overhead**: Does not inspect the content of TCP packets beyond the initial headers.
- **Transparency**: The client remains unaware of the backend server details, enhancing security.
- **Best Use**: Ideal for scenarios requiring minimal routing logic and where performance is prioritized.

### Application Layer Load Balancing (Layer 7)

#### Description
- **Layer**: Application Layer (Layer 7) in the OSI model.
- **Function**: Makes routing decisions based on HTTP message headers and content.
- **Operation**:
  - **Incoming Traffic**: Inspects HTTP headers to make routing decisions based on URL, HTTP method, or cookies.
  - **Routing Examples**:
    - **General Web Requests**: Route to a general web server cluster using round-robin.
    - **Search Requests**: Route to a search compute cluster, possibly using least connections.
    - **Streaming Requests**: Route to a dedicated video cluster, potentially using session stickiness strategies like cookies and IP hashing.

#### Characteristics
- **Advanced Routing**: Allows for more granular and intelligent routing decisions.
- **Overhead**: Slightly higher due to additional inspection of HTTP headers, but generally acceptable in most systems.
- **Best Use**: Preferred for systems requiring detailed routing control and where overhead is manageable.

##  HAProxy Configuration and Use

HAProxy is a high-performance, open-source load balancer used for distributing network traffic across multiple servers. It operates on both TCP (Layer 4) and HTTP (Layer 7) layers and is well-regarded for its reliability and feature set. This document provides an overview of configuring HAProxy, using it for load balancing, high availability, and advanced routing.

### HAProxy Introduction

- **Type**: Free and open-source software load balancer.
- **Supported Layers**: TCP (Layer 4) and HTTP (Layer 7).
- **Platform**: Primarily for Linux, but can be run on other platforms like Windows and macOS using additional tools.

### Configuration Structure

HAProxy configuration consists of two main sections:

1. **Global Section**: Defines parameters for the entire HAProxy instance, such as the maximum number of connections.
2. **Proxy Section**: Contains the routing logic, including:
   - **Default Section**: Applies default parameters to all proxies.
   - **Frontend Section**: Manages incoming requests and specifies listening ports.
   - **Backend Section**: Defines the set of backend servers and load balancing strategies.

### Example Configuration

```plaintext
global
    maxconn 500

defaults
    mode http
    timeout connect 10s
    timeout client 50s
    timeout server 50s

frontend http_in
    bind *:80
    default_backend app_nodes

backend app_nodes
    server server1 127.0.0.1:9001
    server server2 127.0.0.1:9002
    server server3 127.0.0.1:9003
```

## Load Balancing Strategies

1. **Round-Robin**
   - **Description**: Distributes requests evenly among servers.
```plaintext
backend app_nodes
    server server1 127.0.0.1:9001
    server server2 127.0.0.1:9002
    server server3 127.0.0.1:9003
```
2. **Weighted Round-Robin**
   - **Description**: Assigns weights to servers to adjust request distribution based on server capacity.
```plaintext
backend app_nodes
    server server1 127.0.0.1:9001 weight 1
    server server2 127.0.0.1:9002 weight 2
    server server3 127.0.0.1:9003 weight 3
```
3. **High Availability**
   - **Description**: HAProxy can perform regular health checks on backend servers to ensure traffic is only routed to healthy servers.
- **Configuration for Health Checks**
  - **HTTP Check**: Sends GET requests to a status endpoint.
  - **Response Verification**: Can include expected strings to validate server health.

Example Health Check Configuration
```plaintext
backend app_nodes
    server server1 127.0.0.1:9001 check
    server server2 127.0.0.1:9002 check
    server server3 127.0.0.1:9003 check
```

1. **Advanced Routing**
   - **Description**: Access Control Lists (ACLs) allow dynamic routing based on request content.
   - **Example Configuration**:
     - **Define ACLs for routing based on URL paths.**
     - **Route requests to different backend clusters based on ACL matches.**

```plaintext
frontend http_in
    bind *:80
    acl is_even path_beg /even
    acl is_odd path_beg /odd
    use_backend even_app_nodes if is_even
    use_backend odd_app_nodes if is_odd

backend even_app_nodes
    server server2 127.0.0.1:9002

backend odd_app_nodes
    server server1 127.0.0.1:9001
    server server3 127.0.0.1:9003
```

1. **Monitoring and Admin Page**
   - **Stats Page**: Provides an overview of frontend and backend server statistics.
   - **Configuration for Stats Page**:
     - Define a frontend for stats access and enable the stats page.

```plaintext
frontend stats
    bind *:83
    mode http
    stats uri /stats
    stats realm Haproxy\ Statistics
    stats auth admin:password
```

## Layer 4 vs. Layer 7 Load Balancing

- **Layer 4 (TCP)**: Handles load balancing based on TCP connections without inspecting application-level content. It routes traffic based on IP addresses and ports.
- **Layer 7 (HTTP)**: Provides advanced load balancing by inspecting HTTP headers and routing based on request content.

### Comparison
- **Layer 4**: Simpler, lower overhead, but less granular control.
- **Layer 7**: More complex, higher overhead, but allows detailed routing and content-based decisions.

## Conclusion
HAProxy is a versatile and powerful tool for managing and distributing network traffic. It supports various load balancing strategies, provides high availability through health checks, and allows advanced routing based on request content. The choice between Layer 4 and Layer 7 load balancing depends on the specific requirements for performance and control.
