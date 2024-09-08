+++
title= "Message Delivery"
tags = [ "system-design", "software-architecture", "distributed-systems", "communication" , "message-delivery"]
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
bookFlatSection= true
+++

# Message Delivery Semantics in Distributed Systems

In distributed systems, message delivery is prone to various failure scenarios, leading to unpredictability in request-response transactions. To manage this, we define delivery semantics between client and server, ensuring that the system behaves predictably in the event of failure. The two key delivery semantics are **at most once** and **at least once**. Additionally, we examine the importance of idempotent operations in such systems.

## Common Failure Scenarios

During a typical HTTP transaction between a client and server, various failure scenarios can occur:
1. **Successful Transaction**: The server receives the request, executes the action, and responds successfully to the client.
2. **Network Failure**: The server successfully performs the requested action but fails to send the acknowledgment due to network issues.
3. **Server Crash (Before Response)**: The server performs the action but crashes before sending the response.
4. **Server Crash (Before Execution)**: The server crashes before the requested action is executed.
5. **Request Never Received**: The server never receives the client's request.

From the client's perspective, all of these scenarios appear identical, making it difficult to determine the appropriate action.

## Delivery Semantics

### 1. **At Most Once Delivery Semantics**
- **Definition**: The client sends a request to the server only once. If the server does not receive the request or crashes before responding, the client will **not resend** the request.
- **Outcome**: 
  - In the worst case, the requested action may never be performed.
  - In the best case, the action will be performed **no more than once**.
- **Use Cases**:
  - **Logging or Monitoring**: Occasional loss of logs or metrics is acceptable.
  - **Promotional Emails or Notifications**: If a user misses one email, it's fine, but sending multiple notifications could lead to user frustration.

### 2. **At Least Once Delivery Semantics**
- **Definition**: If the client does not receive a response from the server, it will **retransmit** the request until a response is received.
- **Outcome**: If the server has already performed the action but failed to respond, the action may be performed **multiple times**.
- **Idempotency**: This semantics works well for **idempotent operations**, which yield the same result when performed multiple times.

#### Examples of Idempotent Operations:
- **Reading the First Line of a File**
- **Updating User Status to Active**
- **Deleting a Record by ID**

These operations have the same result regardless of how many times they are performed.

#### Examples of Non-Idempotent Operations:
- **Appending a Line to a File**
- **Incrementing a Metric in a Database**
- **Withdrawing Money from a User's Account**

Performing non-idempotent operations multiple times leads to adverse effects, such as incorrect state or duplicate withdrawals.

## Artificial Idempotency for Non-Idempotent Operations

For non-idempotent operations, artificially introducing idempotency can help ensure correct behavior. For example, in a **user billing** scenario:
1. **Sequence Key**: The order service generates a unique, monotonically increasing key for each billing request.
2. **Retry Flag**: A boolean flag indicates whether the request is a retry.
3. **Database Check**: Before executing the operation, the billing service checks the sequence key in the database. If the sequence key matches, the operation has already been performed, and no further action is taken.

This strategy ensures that non-idempotent operations are not inadvertently executed multiple times due to retransmissions.

### Example Flow:
1. **Initial Request**: The client sends a request with a unique sequence key.
2. **No Response**: If the client does not receive a response, it retries the request with the same sequence key and a retry flag set to `true`.
3. **Sequence Check**: The server checks the stored sequence key. If it matches the request, no further action is taken; otherwise, the server performs the operation.

## Conclusion

In distributed systems, handling message delivery failures is critical to ensuring predictable behavior. Two primary delivery semantics—**at most once** and **at least once**—provide options depending on the requirements of the system. For idempotent operations, **at least once** semantics are safe. For non-idempotent operations, strategies such as **artificial idempotency** using sequence keys and retries must be employed to ensure correct behavior. This complexity is an inherent trade-off for building scalable, loosely coupled systems.
