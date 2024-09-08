+++
title= "Service Registry"
tags = [ "system-design", "software-architecture", "distributed-systems", "service-registry" ]
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

# Service Registry with Zookeeper

A service registry is a crucial component in distributed systems, facilitating dynamic service discovery and management. Zookeeper, a distributed coordination service, can be used to implement a robust and scalable service registry. In this documentation, we explore how to build a service registry using Zookeeper and how to integrate it with leader election to ensure high availability and efficient service management.

## Prerequisites
To effectively implement a service registry with Zookeeper, you should be familiar with:

- **Zookeeper Basics**: Understanding of Zookeeper nodes (ZNodes), ephemeral and persistent ZNodes, and how Zookeeper maintains its hierarchical structure.
- **Service Discovery**: The process of dynamically locating services in a distributed system.
- **Leader Election**: Mechanism to ensure a single leader node is elected to coordinate tasks in a distributed system.

## Service Registry Architecture

### Core Concepts
- **Service Registration**: Services register themselves with the service registry by creating ephemeral ZNodes under a specific directory. This ensures that if a service fails or disconnects, its ZNode is automatically removed.
- **Service Discovery**: Services or clients query the service registry to discover available services. This involves retrieving the list of ZNodes under a specific directory and using their paths to connect to the appropriate services.
- **Leader Node**: In some cases, a leader node may be responsible for managing or orchestrating service registrations and queries. Leader election ensures that there is a single leader responsible for these tasks, preventing inconsistencies and conflicts.

## Implementing a Service Registry with Zookeeper

### Service Registration
When a service starts, it registers itself with Zookeeper by creating an ephemeral ZNode under a designated directory, such as `/services`.

- **Define the Service Path**: Each service creates a unique ZNode under the `/services` directory. For example:

```bash
/services/service1-instance1
/services/service1-instance2
/services/service2-instance1
```

- **Create an Ephemeral ZNode**: Services create an ephemeral ZNode to ensure that their registration is automatically removed if they fail or disconnect:

```java
String path = zk.create("/services/service1-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
```

- **Handle ZNode Path**: The service uses the returned path to update its status or metadata if needed.

### Service Discovery
Clients or other services query Zookeeper to discover registered services.

- **List Available Services**: Retrieve the list of child nodes under the /services directory to get available service instances:

```java
List<String> services = zk.getChildren("/services", false);
```

- **Sort and Filter**: Optionally, sort or filter the list of service instances based on criteria like service version or geographic location.

- **Connect to Service**: Use the ZNode paths to connect to the appropriate service instances.

### Integrating Leader Election
In some scenarios, it is beneficial to have a leader node manage the service registry, especially in complex or large-scale systems.

- **Implement Leader Election**: Use Zookeeper’s leader election mechanism to elect a leader node among the nodes managing the service registry. The leader is responsible for:

  - Coordinating service registrations.
  - Managing service queries and updates.
  - Handling failures and re-elections.

- **Service Registration by Leader**: The leader node performs the service registration, ensuring consistency and reducing conflicts.

- **Leader Failover**: If the leader fails, Zookeeper will trigger a new leader election, ensuring that service management continues without interruption.

### Example Integration

#### Leader Election Setup
- **Initialize Zookeeper Client**: Each node connects to Zookeeper and participates in leader election.

- **Create Ephemeral Sequential ZNodes for Leadership**: Nodes create ephemeral sequential ZNodes to participate in leader election:

```java
String leaderPath = zk.create("/election/leader-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
```

- **Determine Leader**: Nodes determine the leader by finding the ZNode with the smallest sequence number.

- **Register Services**: The elected leader node registers services by creating ephemeral ZNodes under the /services directory.

- **Handle Failures**: If the leader fails, the new leader takes over and continues managing the service registry.

#### Service Registration by Leader
- Leader Node Registers Services: The leader creates ephemeral ZNodes for each service:

```java
String servicePath = zk.create("/services/myService-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
```

- **Monitor and Update**: The leader monitors service status and updates ZNodes as needed.

#### Handling Failures and Recovery
- **Service Failure**: If a service fails, its ephemeral ZNode is deleted, and clients are notified of the change.

- **Leader Failure**: Zookeeper automatically elects a new leader if the current leader fails. The new leader will pick up service management and re-register services if necessary.

- **Service Re-registration**: When a failed service recovers and re-registers, it creates a new ephemeral ZNode.

#### Advantages of Using Zookeeper for Service Registry
- **Fault Tolerance**: Automatic removal of ephemeral ZNodes ensures that failed services do not remain registered.
- **Consistency**: Leader election provides a single point of coordination for service registration and discovery.
- **Dynamic Discovery**: Services can be dynamically discovered and queried by clients.
- **Scalability**: The system can scale horizontally by adding more nodes and handling service registrations efficiently.

## Conclusion
Using Zookeeper for service registry and integrating it with leader election ensures a robust, fault-tolerant, and scalable solution for managing services in a distributed system. By leveraging Zookeeper’s capabilities for ephemeral nodes and leader election, you can build a resilient service registry that adapts to changes and failures in your distributed environment.