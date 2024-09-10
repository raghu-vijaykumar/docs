+++
title= "Message Brokers"
tags = [ "system-design", "software-architecture", "distributed-systems", "message-brokers" ]
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

# Introduction to Message Brokers and Apache Kafka

This document provides an overview of message brokers, their use cases, and an introduction to Apache Kafka. It highlights the problems with direct network communication, the advantages of message brokers, and the key concepts of Apache Kafka including topics, partitions, and consumer groups.

## Direct Network Communication Challenges

**Synchronous Nature**
- Direct network communication is inherently synchronous, requiring both sender and receiver to maintain a connection throughout the transaction.
- **Example Scenario:** In a financial transaction pipeline, a series of services (e.g., online store, billing service, inventory service, and shipping service) need to confirm each step sequentially. This can lead to delays and potential issues if any service fails or experiences a delay.

**Scalability Issues**
- Direct communication struggles to scale when broadcasting messages to multiple services. The more services involved, the more connections the publisher server must maintain.
- High traffic scenarios, such as a social media event with millions of comments, can overwhelm the system if not managed properly.

## Message Brokers

**Definition and Functionality**
- A message broker is an intermediary software component that passes messages between senders and receivers. It can also provide data transformation, validation, queuing, and routing.
- **Key Benefits:**
  - **Decoupling:** Senders and receivers do not need to be online at the same time.
  - **Scalability:** Message brokers handle load balancing and can scale horizontally.
  - **Asynchronous Communication:** Enables decoupling of synchronous systems into asynchronous ones.

**Publish/Subscribe Paradigm**
- Allows a sender to publish messages to multiple subscribers through a single message broker.
- Receivers can subscribe to specific topics or queues and receive messages from multiple publishers.

**Pull-Based Messaging**
- Receivers have control over the rate at which they consume messages, as opposed to direct network communication where the server passively listens for incoming messages.

**Scalability and Fault Tolerance**
- Message brokers need to be scalable and fault-tolerant to avoid becoming a bottleneck or single point of failure.

**Use Cases**
- **Distributed Queue:** Facilitates message passing from a single producer to a single consumer.
- **Publish/Subscribe:** Delivers messages from a single publisher to multiple subscribers.

## Apache Kafka

**Introduction**
- Apache Kafka is a distributed streaming platform that provides both distributed queuing and publish/subscribe capabilities.
- It is designed to handle real-time message exchanges between services and is known for its scalability and fault tolerance.

**Kafka Abstraction Layer**
- **Producer Record:** A message in Kafka is called a record, which includes a key, value, and timestamp.
- **Topic:** A category for messages where producers publish records, and consumers subscribe to receive records.

**Partitions**
- Topics are partitioned into multiple logs, each maintaining ordered records. Partitions allow Kafka to scale horizontally and manage high volumes of messages.
- Records are assigned an offset within a partition, which is a sequence number.

**Consumer Groups**
- Consumers belong to consumer groups. Messages from a topic are load-balanced among consumers in a group.
- For publish/subscribe scenarios, each consumer group receives a copy of each message.

**Key Concepts**
- **Partitions:** Allow parallel processing and high performance. Ordering within partitions is maintained, but not across partitions.
- **Consumer Groups:** Facilitate load balancing and broadcasting of messages.

## Apache Kafka Architecture and Configuration

This document provides an overview of Apache Kafka's architecture, focusing on how Kafka achieves scalability, performance, and fault tolerance. It also covers the configuration and setup of a Kafka cluster, including partitioning, replication, and handling failures.

### Kafka Scalability and Performance

**Partitioning**
- Kafka topics can be divided into multiple partitions.
- **Single Partition Limitation:** A single topic managed on one machine limits scalability and performance. It can only utilize the resources of one machine and is a single point of failure.
- **Multiple Partitions:** By distributing a topic across multiple partitions and brokers, Kafka can scale horizontally and increase throughput. Each partition is managed by different brokers, allowing for parallel processing of messages.
- **Trade-offs:** While partitioning improves scalability, it sacrifices global ordering of messages. Ordering is maintained within each partition but not across partitions.

**Parallelism**
- **Publishing:** The number of partitions determines the maximum unit of parallelism for message publishing. More partitions enable higher throughput.
- **Consumption:** Partitions are distributed among consumers within a consumer group. Adding more consumers reduces the load on each consumer, allowing for parallel message consumption.

### Kafka Fault Tolerance

**Replication**
- Each topic can be configured with a replication factor. This factor indicates how many copies of each partition exist across brokers.
- **Leader and Followers:** Each partition has one leader broker that handles all reads and writes, and several follower brokers that replicate the leader’s data. Followers are ready to take over if the leader fails.
- **Example:** With a replication factor of 2, each partition is replicated on two brokers. If one broker fails, the data remains accessible through the other broker.

**Fault Tolerance Mechanisms**
- **Data Persistence:** Kafka persists messages to disk, allowing data recovery and replay even after a broker failure.
- **Message Retention:** Messages are retained in Kafka partitions for a configurable period, enabling new consumers to access older messages and allowing for retries in case of consumer failures.
- **Broker Recovery:** Failed brokers can recover and catch up with the cluster’s state quickly, minimizing downtime.

### Kafka Configuration and Setup

**Single Broker Setup**
- **Configuration Files:** Kafka requires a configuration file for both the broker and Zookeeper. The broker configuration file (`server.properties`) includes settings for broker ID, port, log directory, and log retention.
- **Starting Kafka:** Start Zookeeper and Kafka brokers using the provided scripts. Ensure proper configuration of paths and ports.

**3.2 Multi-Broker Setup**
- **Configuring Additional Brokers:** Duplicate the broker configuration file for each additional broker, incrementing broker IDs and ports as needed.
- **Creating Topics:** Topics are created with a specified replication factor and number of partitions. For example, a topic with 3 partitions and a replication factor of 3 will be distributed across 3 brokers.
- **Topic Management:** Use Kafka scripts to create, list, and describe topics. This helps in verifying the topic configuration and partition distribution.

**3.3 Testing Fault Tolerance**
- **Simulating Failures:** Shut down one broker and observe how Kafka handles the failure. The remaining brokers should continue to function, and data should remain accessible.
- **Recovery and Replication:** Kafka’s replication ensures that data is not lost even if a broker fails. New messages continue to be published and consumed seamlessly.


## Kafka Producer with Java

This document provides a detailed guide on building a Kafka producer using Kafka's Java API. The focus is on configuring the producer and sending records to a distributed Kafka topic.

### Setup

**Kafka Cluster Configuration:**
- Single Zookeeper Instance: Set up a single Zookeeper instance.
- Three Kafka Brokers: Launch three Kafka brokers for a fully distributed Kafka cluster.
- Topic Creation:
    - Topic Name: events
    - Replication Factor: 2
    - Partitions: 3
- Topic Structure:
    - Each partition has a leader and is replicated by one more broker.

**Maven Project Setup**
Add Kafka clients and logging libraries to the Maven pom.xml:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
```

**Kafka Producer Implementation**
1. `createKafkaProducer` Method
This method configures and returns a Kafka producer instance.

```java
public static Producer<Long, String> createKafkaProducer(String bootstrapServers) {
    Properties properties = new Properties();

    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    return new KafkaProducer<>(properties);
}
```

**Bootstrap Servers:** Comma-separated list of Kafka broker addresses.
**Client ID:** A human-readable name for the producer.
**Key Serializer:** LongSerializer
**Value Serializer:** StringSerializer

2. `produceMessages` Method
This method sends a specified number of messages to a Kafka topic.

```java
public static void produceMessages(int numberOfMessages, Producer<Long, String> kafkaProducer) throws ExecutionException, InterruptedException {
    int partition = 1;

    for (int i = 0; i < numberOfMessages; i++) {
        long key = i;
        String value = String.format("event %d", i);

        long timeStamp = System.currentTimeMillis();

        ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, partition, timeStamp, key, value);

        RecordMetadata recordMetadata = kafkaProducer.send(record).get();

        System.out.println(String.format("Record with (key: %s, value: %s), was sent to (partition: %d, offset: %d",
                record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset()));
    }
}
```

**Partition:** Explicitly specifies the partition for the messages.
**ProducerRecord:** Defines the Kafka topic, partition, timestamp, key, and value.
**RecordMetadata:** Provides metadata about the sent record (partition and offset).

3. `main` Method
The entry point of the application, which creates a Kafka producer and sends messages.

```java
public static void main(String[] args) {
    Producer<Long, String> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);

    try {
        produceMessages(10, kafkaProducer);
    } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
    } finally {
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
```

**Bootstrap Servers:** Uses all server addresses to ensure connectivity.
**Message Production:** Sends 10 messages to the events topic.

### Partitioning Strategies

**Explicit Partition:**
- Messages are sent to a specified partition.
- Ensures ordering within the partition.

**Key-Based Partitioning:**
- Kafka's hash function determines the partition based on the key.
- Simplifies producer code by relying on Kafka's partitioning logic.

**Round-Robin:**
- Messages are sent to partitions in a round-robin fashion when no key is provided.
- Useful for distributing messages evenly across partitions.


**Example Code**
```java
/**
 * Apache Kafka - Kafka Producer with Java
 */
public class Application {
    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        Producer<Long, String> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);

        try {
            produceMessages(10, kafkaProducer);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    public static void produceMessages(int numberOfMessages, Producer<Long, String> kafkaProducer) throws ExecutionException, InterruptedException {
        int partition = 1;

        for (int i = 0; i < numberOfMessages; i++) {
            long key = i;
            String value = String.format("event %d", i);

            long timeStamp = System.currentTimeMillis();

            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, partition, timeStamp, key, value);

            RecordMetadata recordMetadata = kafkaProducer.send(record).get();

            System.out.println(String.format("Record with (key: %s, value: %s), was sent to (partition: %d, offset: %d",
                    record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset()));
        }
    }

    public static Producer<Long, String> createKafkaProducer(String bootstrapServers) {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }
```

## Kafka Consumer with Java

This document describes how to build and configure a Kafka consumer using Kafka's Java API. It covers key aspects such as partition load-balancing, publish/subscribe pattern configuration, and handling consumer failures.

### Dependencies
Ensure the following dependencies are added to the pom.xml file for a Maven project:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
```

### Kafka Consumer Configuration

**Method: createKafkaConsumer**
This method initializes a Kafka consumer with the following configuration:

- Bootstrap Servers: List of Kafka server addresses.
- Group ID: The consumer group the consumer belongs to.
- Key Deserializer: Deserializes the keys from binary format to Long.
- Value Deserializer: Deserializes the values from binary format to String.
- Enable Auto Commit: Set to false to manually commit offsets and handle message reprocessing in case of failures.

**Code:**

```java
public static Consumer<Long, String> createKafkaConsumer(String bootstrapServers, String consumerGroup) {
    Properties properties = new Properties();

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    return new KafkaConsumer<>(properties);
}
```

**Method: consumeMessages**
This method subscribes to a topic and consumes messages:

- Subscription: The consumer subscribes to the specified Kafka topic.
- Polling: The poll method retrieves records from Kafka, blocking for a specified duration.
- Processing: Iterates over the retrieved records and prints out the details.
- Commit: The commitAsync method is used to commit the offsets asynchronously.

**Code:**

```java
public static void consumeMessages(String topic, Consumer<Long, String> kafkaConsumer) {
    kafkaConsumer.subscribe(Collections.singletonList(topic));

    while (true) {
        ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

        if (consumerRecords.isEmpty()) {
            // Optional: Perform alternative tasks if no records are received
        }

        for (ConsumerRecord<Long, String> record : consumerRecords) {
            System.out.println(String.format("Received record (key: %d, value: %s, partition: %d, offset: %d",
                    record.key(), record.value(), record.partition(), record.offset()));
        }

        // Optional: Process the records

        kafkaConsumer.commitAsync();
    }
}
```

### Testing Kafka Consumer

**Single Consumer Test**
- **Produce Messages:** Use Kafka console producer to send messages to the topic.
- **Start Consumer:** Launch the Java consumer to consume messages from the topic.
- **Observe Behavior:** Verify that the consumer processes messages and handles partitions as expected.

**Consumer Group and Load Balancing**
- **Multiple Consumers:** Start additional consumers within the same consumer group.
- **Observe Partition Distribution:** Messages should be distributed among consumers based on partition load balancing.
- **Failure Handling:** Simulate consumer failures and observe Kafka’s rebalancing behavior.

**Publish/Subscribe Pattern**
- **Separate Consumer Groups:** Run each consumer instance in a different consumer group.
- **Publish Messages:** Send messages to the topic.
- **Verify:** Each consumer in different groups should receive all messages, demonstrating a publish/subscribe pattern.

**Handling Consumer Failures**
- **Simulate Failure:** Comment out the commitAsync method call to simulate a failure.
- **Observe Redelivery:** Produce new messages and restart the consumer to check if Kafka redelivers uncommitted messages.

**Code Example for Consumer Application:**

```java
public class Application {
    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        String consumerGroup = "defaultConsumerGroup";
        if (args.length == 1) {
            consumerGroup = args[0];
        }

        System.out.println("Consumer is part of consumer group " + consumerGroup);

        Consumer<Long, String> kafkaConsumer = createKafkaConsumer(BOOTSTRAP_SERVERS, consumerGroup);

        consumeMessages(TOPIC, kafkaConsumer);
    }

    public static Consumer<Long, String> createKafkaConsumer(String bootstrapServers, String consumerGroup) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(properties);
    }

    public static void consumeMessages(String topic, Consumer<Long, String> kafkaConsumer) {
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        while (true) {
            ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            if (consumerRecords.isEmpty()) {
                // Optional: Perform alternative tasks if no records are received
            }

            for (ConsumerRecord<Long, String> record : consumerRecords) {
                System.out.println(String.format("Received record (key: %d, value: %s, partition: %d, offset: %d",
                        record.key(), record.value(), record.partition(), record.offset()));
            }

            // Optional: Process the records

            kafkaConsumer.commitAsync();
        }
    }
}
```