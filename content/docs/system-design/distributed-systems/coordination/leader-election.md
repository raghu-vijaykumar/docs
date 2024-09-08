+++
title= "Leader Election"
tags = [ "system-design", "software-architecture", "distributed-systems", "leader-election" ]
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

# Leader Election with Zookeeper

Leader election is a fundamental problem in distributed systems, where a group of nodes must elect a single leader among themselves. The leader typically coordinates tasks, manages resources, and makes critical decisions for the system. Zookeeper, a distributed coordination service, provides powerful primitives that make implementing leader election both robust and straightforward.

This documentation provides a detailed explanation of how to implement leader election using Zookeeper, ensuring fault tolerance and high availability in distributed systems.

## Prerequisites

To understand leader election with Zookeeper, it is essential to be familiar with the following concepts:

- Zookeeper: A distributed service for maintaining configuration information, naming, providing distributed synchronization, and providing group services.
- ZNodes: Nodes in Zookeeper’s hierarchical namespace. There are two types of ZNodes:
  - Persistent ZNodes: Remain in Zookeeper until explicitly deleted.
  - Ephemeral ZNodes: Automatically deleted when the session that created them terminates.
  - Sequential ZNodes: A ZNode with an appended sequence number, useful for leader election.

## Leader Election Strategy with Zookeeper

### Core Concept

Zookeeper's leader election works by having each node in a cluster create an ephemeral, sequential ZNode. The node that creates the ZNode with the lowest sequence number is elected as the leader. This design ensures:

- Fault Tolerance: If the leader node fails, its ZNode is automatically deleted, triggering the election of a new leader.
- Scalability: The algorithm scales horizontally, handling additional nodes and load without a bottleneck.

### Steps for Leader Election

1. **Start a Zookeeper Client**: Each node in the distributed system starts by connecting to a Zookeeper ensemble.

2. **Create an Ephemeral Sequential ZNode**: Every node that wants to participate in the leader election creates an ephemeral, sequential ZNode. The path might look like:

```bash
/election/node-00000001
/election/node-00000002
/election/node-00000003
```

3. **Determine the Leader**: After creating its ZNode, each node queries Zookeeper for the list of children in the /election directory. The node with the smallest sequence number is considered the leader. For example, if a node created /election/node-00000001, it becomes the leader because it has the lowest number.

4. **Set a Watcher on the Predecessor Node**: Nodes that are not elected as the leader set a watcher on their predecessor's ZNode (the node that has the next lowest sequence number). For instance, if a node created /election/node-00000002, it will set a watcher on /election/node-00000001.

5. **Handle Leader Failure**: If the leader node fails (due to a crash or disconnection), its ephemeral ZNode is automatically deleted by Zookeeper. The node watching the leader's ZNode receives a notification and re-runs the leader election process. The node that now has the smallest ZNode sequence number becomes the new leader.

### Implementation Details

1. Creating Ephemeral Sequential ZNodes

Nodes create an ephemeral sequential ZNode when they connect to the Zookeeper ensemble:

```java
String path = zk.create("/election/node-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
```

This creates a node with a unique sequential identifier such as node-00000001, node-00000002, etc.

2. Electing the Leader
   Once a ZNode is created, the node checks for the ZNode with the lowest sequence number to determine whether it is the leader:

```java
List<String> children = zk.getChildren("/election", false);
List<String> children = zk.getChildren("/election", false);
Collections.sort(children); // Sort the children to find the smallest
if (path.endsWith(children.get(0))) {
    System.out.println("I am the leader");
} else {
    System.out.println("I am not the leader");
}
```

3. Watching the Predecessor Node
   Non-leader nodes need to monitor the ZNode that precedes their own ZNode. This is done by setting a watcher on the predecessor’s ZNode:

```java
int index = children.indexOf(myNode);
String predecessorNode = children.get(index - 1); // Get predecessor's ZNode
Stat stat = zk.exists("/election/" + predecessorNode, new Watcher() {
    public void process(WatchedEvent event) {
        if (event.getType() == EventType.NodeDeleted) {
            // Trigger re-election
        }
    }
});
```

4. Handling Node Failures
   Zookeeper's ephemeral ZNodes are automatically deleted when the session of the node that created the ZNode expires. This behavior is crucial for ensuring fault tolerance in leader election:

- If the leader node crashes, its ZNode is deleted.
- The next node in the sequence (based on its Watcher notification) detects the deletion and triggers a re-election.
- This ensures that a new leader is quickly and automatically elected without manual intervention.

### Edge Cases and Considerations

1. Race Conditions
   Race conditions can arise during the election process, particularly when setting watchers or when ZNodes are deleted between operations. To handle these cases, the leader election process is typically repeated until a new leader is elected or a valid predecessor is found.

2. Scalability
   The algorithm scales efficiently for large clusters. Even with hundreds or thousands of nodes, the election process remains quick, as only a small subset of nodes need to interact directly with Zookeeper (i.e., each node only watches its immediate predecessor).

3. Rejoining the Cluster
   Nodes that recover from failure can rejoin the cluster by creating a new ZNode. They are assigned a new sequential number and will once again participate in the election process.

4. Session Timeouts
   Configuring appropriate session timeouts in Zookeeper is crucial. The timeout should be long enough to account for temporary network issues but short enough to ensure that failed nodes do not linger in the cluster.

### Advantages of Using Zookeeper for Leader Election

1. Fault Tolerance: The system remains operational as long as at least one node is alive.
2. Automatic Failover: Zookeeper handles node failures by automatically promoting the next node in line as the new leader.
3. Simplicity: Zookeeper provides a simple and reliable mechanism for leader election, with built-in primitives for handling ephemeral nodes and watches.
   Scalability: The system can scale horizontally, handling thousands of nodes without degradation in performance.

### Use Cases

Leader election with Zookeeper is commonly used in:

1. Distributed Databases: To ensure that one node coordinates database reads and writes.
2. Task Scheduling: To assign a master node for scheduling tasks across worker nodes.
3. Consensus Systems: To ensure a single node is responsible for decision-making in consensus algorithms like Paxos or Raft.

### Conclusion

Zookeeper provides an elegant solution to the leader election problem in distributed systems. By leveraging ephemeral sequential ZNodes and watchers, we can implement a fault-tolerant and highly available system that automatically elects a new leader when failures occur. This mechanism is essential for ensuring continuous operation in large-scale distributed architectures.
