+++
title= "Apache Kafka"
tags = [ "kafka", "message-broker" ]
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
weight= 11
+++


## What is Apache Kafka?

Apache Kafka is a **distributed streaming platform** that enables real-time processing of data streams. It provides two primary functionalities:

1. **Creation of real-time data streams:** Kafka allows for the continuous flow of data in real time, with latencies as low as milliseconds.
2. **Processing of real-time data streams:** Kafka facilitates the real-time processing of incoming data, enabling immediate actions based on predefined conditions.

### Example Use Case
Imagine a smart electricity meter generating data every minute. This data is sent to a Kafka server, which continuously receives information from multiple meters across the city, forming a continuous data stream. Applications can process this stream in real-time, such as sending alerts when electricity load exceeds a threshold, ensuring timely actions within seconds or minutes.

## How Does Apache Kafka Work?

Kafka adopts the **Publish/Subscribe (Pub/Sub) messaging architecture** typical of enterprise messaging systems. It consists of three core components:

1. **Producer:** A client application that sends data records (messages).
2. **Message Broker (Kafka Server):** Receives and stores messages from producers.
3. **Consumer:** Client applications that read and process messages from the broker.

### Example Workflow
- The smart meter acts as a **Producer**, sending data to the Kafka **Message Broker**.
- The Kafka server acts as a **Message Broker**, storing the data.
- An application acts as a **Consumer**, processing the data and triggering actions such as sending alerts when necessary.

Kafka serves as a **middleman** between producers and consumers, efficiently handling streams of data between them.

## Origins of Apache Kafka

Kafka was originally developed by LinkedIn to address their **data integration problem**. As various applications generated and consumed data across LinkedIn’s infrastructure, a need arose to simplify and streamline the process of transferring data between systems.

### Solution
Kafka introduced a Pub/Sub messaging architecture that centralized the flow of data. With Kafka as a **Message Broker**, applications became **Producers** and **Consumers**, reducing the complexity and maintenance challenges associated with direct data pipelines between systems.

## Evolution of Apache Kafka

Kafka evolved from a **data integration tool** into a full-fledged **real-time streaming platform**. Initially, Kafka consisted of:

1. **Kafka Broker:** The central server system acting as a message broker.
2. **Kafka Client API:** Producer and Consumer APIs for sending and receiving messages.

Over time, Kafka added three more components to expand its capabilities:

1. **Kafka Connect:** A tool to facilitate **data integration**, addressing the problem Kafka was initially designed to solve.
2. **Kafka Streams:** A library for creating real-time stream processing applications.
3. **KSQL:** A tool that transforms Kafka into a real-time database with SQL-like querying capabilities (offered commercially by Confluent Inc.).

## Kafka's Role in Enterprise Applications

Kafka occupies a **central position** in an organization's data integration and processing ecosystem. It serves as a **circulatory system** for data, decoupling data producers from consumers:

1. **Producers:** Send data to Kafka as soon as business events occur.
2. **Consumers:** Retrieve and process the data from Kafka in real-time, ensuring immediate action.

### Decoupling Benefits
- Producers and consumers are **independent** of each other.
- Kafka ensures **low-latency** data flow between producers and consumers, often in milliseconds.
- Kafka's architecture enables **scalability** and **flexibility**, allowing for the easy addition, removal, or modification of producers and consumers as business needs evolve.

## Apache Kafka Core Concepts

This technical documentation outlines the fundamental concepts associated with Apache Kafka, a distributed streaming platform for handling real-time data streams.

### Producer
- **Producer**: An application that sends data to Kafka. The data, often called messages or records, is sent as an array of bytes.
- **Example**: A producer can send each line of a data file or database table row as a message to Kafka.

### Consumer
- **Consumer**: An application that receives data from Kafka. Consumers do not directly communicate with producers but retrieve messages from the Kafka server.
- **Example**: A consumer application requests messages (such as lines of text) from Kafka, processes them, and may request more messages in a loop.

### Broker
- **Broker**: The Kafka server acts as a broker, facilitating the exchange of messages between producers and consumers. Producers send data to the broker, and consumers request data from it.

### Cluster
- **Cluster**: A group of computers running Kafka instances, designed to act together for a common purpose. Kafka is a distributed system that runs on a cluster of brokers, allowing for scalability.

### Topic
- **Topic**: A unique name given to a data stream. Producers send messages to a specific topic, and consumers request data from that topic.
- **Example**: Topics can be used to organize data from different producers, such as creating separate topics for current load, consumed units, and input fluctuations.

### Partitions
- **Partition**: Kafka breaks topics into smaller, independent parts called partitions to manage large datasets. Partitions allow for distributed storage across multiple machines in a Kafka cluster.
- **Design Decision**: The number of partitions is a design decision made during topic creation, based on the expected data volume.

### Offset
- **Offset**: A unique sequence ID assigned to each message within a partition. Offsets are immutable and indicate the order of arrival of messages within a partition.
- **Local Scope**: Offsets are local to partitions, meaning there is no global ordering across partitions.

### Consumer Group
- **Consumer Group**: A group of consumers working together to share the workload of processing messages from a Kafka topic. Each consumer within a group is assigned one or more partitions to process.
- **Scalability**: The number of consumers in a group is limited by the number of partitions in the topic. Kafka ensures that only one consumer reads from a partition at a time to prevent double reading of messages.

### Scalability and Distribution
- **Partitions for Scalability**: Kafka partitions play a crucial role in distributing workload across the Kafka cluster. Partitions enable Kafka to scale horizontally, with producers and consumers processing data across multiple machines.
- **Consumer Group Scalability**: Multiple consumers in a group can read from different partitions simultaneously, dividing the work and improving processing efficiency.

These core concepts form the foundation for working with Apache Kafka and are crucial to understanding how Kafka manages real-time data streams in a distributed system.

## Kafka Connect 

Kafka Connect is a distributed data integration framework within Apache Kafka, designed to move large amounts of data between Kafka and external systems efficiently. It enables organizations to manage data flows between a variety of systems, without writing custom code, using reusable connectors. 

### Key Concepts

Kafka Connect helps solve the **data integration problem**, allowing data to flow from various source systems to Kafka and from Kafka to various target systems, including databases, data warehouses, and analytics platforms.

### Kafka Connect Architecture

Kafka Connect is composed of **workers**, **connectors**, and **tasks**:

1. **Workers**: Kafka Connect runs as a cluster of worker processes. These workers are fault-tolerant, self-managed, and use the same mechanism as Kafka consumer groups. If a worker fails or stops, other workers in the cluster will take over its connectors and tasks.

2. **Connectors**: Connectors define how to connect Kafka to external systems. There are two types of connectors:
   - **Source Connectors**: Pull data from an external system into Kafka.
   - **Sink Connectors**: Send data from Kafka to an external system.

3. **Tasks**: Tasks perform the actual data integration work. They connect to the source/target system, fetch or send data, and hand it over to workers. Tasks run in parallel, enabling scalability.

### Kafka Connect Data Flow

1. **Source Connectors**: These connectors pull data from external systems into Kafka. They interact with source databases, cloud storage systems, or other external data sources using source-specific APIs. Each connector creates **tasks**, which read and send data to Kafka.

2. **Sink Connectors**: Sink connectors move data from Kafka to external systems like databases or data lakes. Tasks consume Kafka records and write them to the target system using APIs specific to the target.

3. **Parallelism and Tasks**: Connectors split work into parallel tasks, enabling load distribution across workers. For example, a source connector might pull data from multiple tables, with each task handling a single table.

4. **Workers and Reliability**: Workers are responsible for running connectors and their associated tasks. The framework is highly resilient; if a worker fails, the remaining workers automatically redistribute the tasks. Kafka Connect supports dynamic scaling by adding or removing workers without downtime.

### Kafka Connect Features

- **Single Message Transformations (SMTs)**: Kafka Connect supports basic transformations of data records on the fly, such as adding fields, filtering, renaming fields, and routing messages to different topics. These transformations are applied within connectors before records are sent to Kafka or external systems.

- **Scalability**: Kafka Connect scales horizontally by adding more workers. Each worker runs one or more tasks, allowing parallel processing of data.

- **Fault Tolerance**: Kafka Connect provides built-in fault tolerance, where if a worker fails, tasks are rebalanced across the remaining workers without loss of data.

### Connector Framework

The **Kafka Connect Framework** simplifies the process of writing connectors. Developers implement the following Java classes:
- **SourceConnector/SinkConnector**: These define the logic for pulling or pushing data.
- **SourceTask/SinkTask**: These implement the logic for handling data interaction with the source or target system.

The framework handles the rest, including scalability, fault tolerance, error handling, and communication with Kafka. Connectors are typically distributed as JARs or ZIP files and installed in the Kafka Connect cluster.

### Example Use Case: Data Integration

An example scenario where Kafka Connect is used:
- **Source**: A relational database (RDBMS) with several tables.
- **Target**: A data warehouse (e.g., Snowflake).

Using Kafka Connect, a JDBC source connector is configured to pull data from the RDBMS into Kafka topics. On the other side, a Snowflake sink connector is configured to move data from the Kafka topics to Snowflake. Kafka Connect handles all the data flow, parallelism, and fault tolerance.

## Kafka Streams Overview

Kafka Streams is a library within Apache Kafka, designed to build real-time stream processing applications and microservices. These applications process data streams, continuously reading from Kafka topics and performing real-time analytics or actions. Kafka Streams allows you to scale your applications effortlessly, providing fault tolerance, and it can be deployed on various environments, from virtual machines to Kubernetes clusters.

### Key Concepts of Real-Time Stream Processing

1. **Data Streams**: Streams represent unbounded, infinite sequences of data generated continuously. They can come from sources like sensors, log entries, clickstreams, transactions, or social media feeds.
   
2. **Stream Processing vs. Traditional Processing**:
   - **Request-Response Processing**: Queries are performed on stored data, returning answers to specific questions.
   - **Batch Processing**: Processes data in bulk, running periodic jobs to analyze large datasets.
   - **Stream Processing**: Unlike batch processing, stream processing deals with continuous data flows, ensuring real-time updates and insights as new data arrives.

### Kafka Streams Architecture

Kafka Streams continuously reads data from one or more Kafka topics, processes it in real-time, and outputs the results. Key aspects of Kafka Streams architecture include:

- **Tasks and Partitions**: Kafka Streams divides the work into logical tasks. Each task consumes data from one or more partitions of Kafka topics. The number of tasks is determined by the number of partitions.
- **Threads and Scalability**: Kafka Streams allows multi-threaded processing. Tasks are assigned to threads, and scaling out is done by adding more instances of the application, which will automatically distribute the tasks across the available resources.
- **Task Re-assignment and Fault Tolerance**: When a new instance is added, Kafka Streams automatically reassigns tasks to balance the load. If an instance fails, tasks are re-assigned to other running instances to ensure no data is lost, providing built-in fault tolerance.

### Features and Capabilities

Kafka Streams provides several powerful capabilities designed for stream processing:

- **Stream-Table Interoperability**: Kafka Streams allows interaction between streams and tables. It can convert streams to tables and vice versa, enabling more flexible data modeling.
- **Aggregations**: Kafka Streams supports continuously updating aggregates from grouped streams.
- **Joins**: Kafka Streams allows joining between streams, between tables, or between a combination of both.
- **Stateful Processing**: Kafka Streams provides fault-tolerant local state stores, which are essential for windowing and time-based operations.
- **Windowing**: Kafka Streams supports creating time windows for stream data, handling complexities such as event time, processing time, and dealing with late-arriving data.
- **Interactive Queries**: Kafka Streams provides an interactive query interface, enabling real-time querying of the state within your streams application.
- **Fault Tolerance and Scalability**: Kafka Streams offers inherent fault tolerance and scalability, allowing dynamic scaling of stream processing applications by adding more threads or instances as needed.
- **Testing and Extensibility**: Kafka Streams includes tools for unit testing applications, along with flexible DSLs and options to extend with custom processors.

### Kafka Streams in Action

Kafka Streams allows you to scale out real-time applications while ensuring reliability:

- **Task Distribution**: When consuming from Kafka topics, Kafka Streams automatically creates tasks based on the number of partitions. These tasks are then assigned to threads within the application.
- **Scaling Out**: Kafka Streams makes it easy to scale out by adding more instances of the application. When more instances are deployed, tasks are automatically rebalanced and reassigned without manual intervention or downtime.
- **Failover Handling**: If an instance or thread fails, Kafka Streams reassigns the tasks to other available instances, ensuring seamless recovery and processing continuation.

### Conclusion

Kafka Streams is a powerful, scalable, and fault-tolerant library designed for real-time stream processing. It allows developers to build stream processing applications with minimal effort, leveraging the full capabilities of Kafka, including parallelism, scalability, fault tolerance, and real-time analytics. Kafka Streams can be deployed on various environments, making it highly versatile for real-world streaming use cases.

## Kafka SQL Overview 

### Introduction to KSQL
KSQL is a SQL interface for Kafka Streams, designed to enable scalable and fault-tolerant stream processing workloads without needing to write code in Java or Scala. KSQL supports two operating modes:

- **Interactive Mode**: Allows the user to submit KSQL commands via a command-line interface (CLI) or a web-based UI for immediate response, suitable for development environments.
- **Headless Mode**: Designed for production environments, this non-interactive mode executes predefined KSQL files on the KSQL server.

### KSQL Components
KSQL comprises three core components:
- **KSQL Engine**: The main component responsible for processing KSQL statements and queries by building a Kafka Streams topology and executing them as stream tasks.
- **REST Interface**: Powers the KSQL clients and interacts with the KSQL Engine to execute commands.
- **KSQL CLI/UI**: The interface through which users submit and execute commands.

These components form the KSQL server, which can operate in interactive or headless mode. Multiple KSQL servers can be deployed to form a scalable KSQL cluster.

### Capabilities of KSQL
KSQL enables SQL-like operations on Kafka topics, such as:
- Grouping and aggregating over time windows
- Filtering and joining multiple topics
- Storing query results into new topics

By using KSQL, Kafka topics can be treated like tables, and complex queries can be run over streaming data, turning Kafka into a real-time data warehouse.

## Kafka Ecosystem Application Patterns

Kafka's ecosystem supports various application patterns, including data integration, real-time stream processing in microservice architectures, and real-time stream processing for data lakes or data warehouses.

#### 1. Data Integration Pattern
This pattern focuses on data integration among multiple independent systems using Kafka. The components used in this pattern include:
- **Kafka Broker**: Provides a shared infrastructure for data exchange.
- **Kafka Client APIs**: Used for implementing Kafka producers and consumers.
- **Kafka Connect**: Facilitates data integration with commercial-off-the-shelf (COTS) products.

In this architecture, Kafka brokers handle data sharing between systems, while producers and consumers manage data generation and consumption.

#### 2. Microservice Architecture for Stream Processing
This pattern involves real-time stream processing using a microservice architecture. Key Kafka components used here include:
- **Kafka Broker**: Provides backbone infrastructure for microservices.
- **Kafka Producers**: Generate and share data streams.
- **Kafka Streams**: Handle business logic and real-time stream processing.

Kafka Streams are favored over Kafka Consumers for stream processing because of their additional capabilities such as handling state, joins, and windowing.

#### 3. Real-Time Streaming in Data Warehouse and Data Lakes
In this pattern, Kafka serves as a data ingestion layer, streaming data from various sources into a data lake. Once in the data lake, the data can be processed using batch or stream processing tools like Spark Structured Streaming.

For real-time querying, KSQL can be utilized to generate up-to-date summaries and reports. This extends Kafka's use case beyond data ingestion into the realm of real-time data warehousing.

