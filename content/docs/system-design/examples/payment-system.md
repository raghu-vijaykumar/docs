+++
title= "Payment System"
tags = [ "system-design", "software-architecture", "interview", "payment-system" ]
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
weight= 25
bookFlatSection= true
+++

# Technical Documentation: Implementing a Reliable and Scalable Payment System

## Overview

In recent years, e-commerce has experienced significant growth. E-commerce involves trading goods and services over the internet in exchange for monetary payments. The payment system, operating in the background, is crucial for facilitating these transactions. Implementing a reliable and scalable payment system is challenging due to the need for high availability, reliability, and compliance with various regulations.

## Payment System Components

![pay-in-flow-high-level](../images/payin-flow-high-level.png)

### Payment Gateway

- **Function**: Validates financial credentials and transfers them to the merchant’s bank account.
- **Compliance**: Must adhere to standards like PCI DSS and GDPR.
- **Additional Functions**: Risk and fraud prevention through external verification services.

### Payment Service Provider (PSP)

- **Definition**: A third-party company assisting businesses in processing payments safely and securely.
- **Services**: Risk management, reconciliation tools, order management.
- **Role**: Can act as the acquiring bank or collaborate with acquiring banks to facilitate transactions.

### Acquiring Bank

- **Function**: Processes card payments on behalf of the merchant.
- **Responsibilities**: Captures transaction information, performs basic validation, and routes requests to the cardholder's issuing bank for approval.

### Cardholder Issuing Bank

- **Function**: Receives transaction information, checks validity, account balance, and approves or declines the transaction.
- **Role**: Sends the transaction status back through the network to the merchant.

## System Design Considerations

### Key Requirements

1. **High Availability**: Critical to avoid revenue loss due to downtime.
2. **Reliability**: Ensure the system can handle high volumes of transactions and recover from failures.
3. **Compliance**: Adherence to industry standards and regulations to ensure secure transactions.

### Communication Patterns

- **Synchronous Communication**:
  - **Description**: One service waits for a response from another before proceeding.
  - **Drawbacks**: Susceptible to failure and latency issues, which can affect system availability and cause cascading failures.
  - **Use Case**: Real-time authorization scenarios, such as physical store payments.

- **Asynchronous Communication**:
  - **Description**: Services do not wait for a response; instead, they continue with their execution and handle responses at intervals or through notifications.
  - **Benefits**: More resilient to failures and latency, better suited for large-scale systems with complex business logic.
  - **Use Case**: Suitable for online payments, fraud detection, and analytics. Utilizes persistent queues like Kafka to manage traffic spikes and ensure system stability.

## System Implementation

1. **Payment Event Handling**:
   - Generate a payment event when a user places an order.
   - Store the event in the database.
   - Call an external PSP to process the payment.

2. **Payment Page**:
   - Use a PSP-provided form page to collect payment details.
   - Building an in-house payment page is uncommon and requires stringent compliance measures.

3. **Transaction Processing**:
   - PSP sends card details to banks or card schemes.
   - Upon successful processing, the wallet service updates the merchant’s account balance.
   - Update The Ledger with financial transactions for auditing and revenue calculation.

4. **Service Communication**:
   - Prefer asynchronous communication for better fault tolerance and handling large transaction volumes.
   - Use persistent queues to buffer and manage traffic spikes.

## Handling Issues in Payment Systems

### 1. Common Issues in Payment Systems

1. **System Failures**
   - **Network Failures**: Disruptions in network connectivity.
   - **Server Failures**: Issues with server availability or performance.

2. **Poison Pill Errors**
   - Errors arising when an inbound message cannot be processed or consumed.

3. **Functional Bugs**
   - Instances where the system behaves correctly from a technical standpoint but produces invalid results.

### 2. Ensuring Reliability in Payment Systems

1. **Message Persistence with Kafka**
   - **Role of Kafka**: Kafka can be used to persist communication messages to ensure they are not lost, even when the system encounters issues.
   - **Message Handling**: Messages are stored in Kafka until processed by the consumer service, ensuring messages are delivered and not lost. 
   - **Consumer Responsibilities**: Consumers must mark messages as consumed only after successful processing and storage in the database.

2. **Retry Mechanisms**
   - **Simple Retry**: Retry immediately after a failure.
   - **Fixed Interval Retry**: Retry at fixed intervals to avoid overwhelming the system.
   - **Incremental Interval Retry**: Retry at gradually increasing intervals to allow systems to recover.
   - **Exponential Backoff**: Increase the waiting time between retries exponentially to prevent hammering the server.
   - **Jitter**: Introduce randomness in retry intervals to avoid synchronized retries from multiple clients.

3. **Timeout Management**
   - **Purpose**: Prevent requests from waiting indefinitely for a response.
   - **Challenges**: Determining appropriate timeout values to balance between allowing slow responses and avoiding indefinitely stalled requests.
   - **Handling Timeouts**: Aborting operations when response times exceed the timeout limit.

4. **Fallback Strategies**
   - **Fallback Values**: Use predefined values or alternate processes when certain services fail (e.g., allowing transactions of small amounts to proceed despite service failures).
   - **Persistent Failures**: Store failed transactions in a persistent queue for processing once the service is back up.

5. **Idempotency and Hidden Potency**
   - **Idempotent Operations**: Ensure that repeating an operation has no additional effect (e.g., retrying a payment request).
   - **Hidden Potency Key**: Use a unique identifier to recognize and prevent duplicate transactions. Commonly implemented with UUIDs.

6. **Distributed Systems**
   - **Benefits**: Redundancy, workload distribution, fault tolerance, and scalability.
   - **Challenges**: Data inconsistency, replication lag, and communication failures between nodes.

7. **Data Protection**
   - **Encryption**: Encrypt data at rest and in transit using tools such as VPNs, TLS, and HTTPS.
   - **Access Control**: Restrict access to authorized users and use methods like two-factor authentication.
   - **Software Updates**: Regularly update software libraries and operating systems to address vulnerabilities.
   - **Data Backup**: Implement regular backups to recover data in case of loss or damage.

8. **Monitoring and Integrity**
   - **Data Integrity**: Use cryptographic checksums to monitor data changes and detect potential tampering.
   - **Resource Efficiency**: Focus monitoring efforts on high-risk data to manage resources effectively.

### Conclusion

To build a reliable payment system, it is crucial to handle system failures, implement effective retry mechanisms, manage timeouts, and use fallback strategies. Employing distributed systems, ensuring data protection, and monitoring data integrity further enhance system reliability and fault tolerance.
