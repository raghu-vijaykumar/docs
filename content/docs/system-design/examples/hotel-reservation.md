+++
title= "Hotel Reservation"
tags = [ "system-design", "software-architecture", "interview", "hotel-reservation" ]
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
weight= 21
bookFlatSection= true
+++

# Hotel Booking System Design

## 1. Overview

This document outlines the high-level architecture of a hotel booking system similar to platforms like Booking.com or Airbnb. It covers functional and non-functional requirements, system design, and data flow.

## 2. Functional Requirements

### 2.1. Hotel Management
- **Onboarding**: Hotels must be able to join the platform.
- **Property Management**: Hotels should update property details, including adding rooms, changing pricing, and updating images.
- **Booking Insights**: Hotels need to view bookings and access revenue data.

### 2.2. User Functionality
- **Search**: Users should be able to search for hotels based on location, price range, and other criteria.
- **Booking**: Users must be able to book hotels and view their bookings.

## 3. Non-Functional Requirements

- **Low Latency**: The system must operate with minimal delay.
- **High Availability**: The system should be consistently available.
- **High Consistency**: Booking data should be up-to-date and visible immediately.

## 4. System Design

### 4.1. High-Level Architecture

#### 4.1.1. User Interface
- **Hotel Management UI**: Used by hotel managers for onboarding, property updates, and booking insights.
- **Search and Booking UI**: Used by users to search for and book hotels.

#### 4.1.2. Backend Services
- **Hotel Service**: Manages hotel data including onboarding and property updates. Uses a clustered MySQL database with a CDN for storing images.
- **Search Service**: Handles search queries using Elasticsearch for fuzzy search capabilities. It consumes data from Kafka for indexing and search operations.
- **Booking Service**: Manages bookings and payment processing. Uses a separate MySQL database for bookings and interacts with a Payment Service.
- **Archival Service**: Moves completed or canceled bookings to a Cassandra cluster for archival.
- **Notification Service**: Sends notifications to users and hotels regarding booking status and updates.

### 4.2. Data Flow

1. **Hotel Management**: Data from the hotel management UI is processed by the Hotel Service, stored in MySQL, and images are managed via CDN.
2. **Search Operations**: Modifications and updates are published to Kafka, processed by the Search Consumer, and indexed in Elasticsearch for search operations.
3. **Booking Process**: Booking requests are handled by the Booking Service, stored in MySQL, and processed for payment. Booking data is also sent to Kafka for updating search availability.
4. **Archival**: Booking records are archived in Cassandra for completed or canceled bookings.
5. **Notification**: Notifications about bookings and updates are managed by the Notification Service.
6. **Booking Management**: Provides a read-only view of bookings by accessing both MySQL (for active bookings) and Cassandra (for historical data), with Redis used as a cache to optimize performance.
7. **Analytics**: Data is collected from Kafka and processed in a Hadoop cluster using Spark Streaming for reporting and analysis.

## 5. Scalability and Performance

- **Horizontal Scaling**: Components like Hotel Service, Search Service, and Elasticsearch can be scaled horizontally to handle increased load.
- **Caching**: Redis is used to cache frequently accessed data to reduce the load on MySQL.
- **Archival**: Cassandra is used for handling large volumes of historical booking data efficiently.

## Hotel Service

### Functionality

The Hotel Service is a CRUD service responsible for managing hotel data. It serves as the source of truth for hotel information and supports basic operations: Create, Read, Update, and Delete.

### API Endpoints

1. **Create Hotel**
   - **POST** `/hotels`
   - Description: Adds a new hotel to the system.

2. **Get Hotel Information**
   - **GET** `/hotel/{hotel_id}`
   - Description: Retrieves details of a specific hotel.

3. **Update Hotel Information**
   - **PUT** `/hotel/{hotel_id}`
   - Description: Updates information for a specified hotel.

4. **Update Room Information**
   - **PUT** `/hotel/{hotel_id}/room/{room_id}`
   - Description: Updates or creates room information for a specific hotel.

### Database Schema

1. **Hotel Table**
   - `id` (Primary Key): Unique identifier for the hotel.
   - `name`: Name of the hotel.
   - `locality_id` (Foreign Key): References the locality table.
   - `description`: Description of the hotel.
   - `original_images`: Store original images uploaded.
   - `display_images`: Compressed or optimized versions of the images.
   - `is_active`: Soft delete flag to indicate if the hotel is active.

2. **Rooms Table**
   - `room_id` (Primary Key): Unique identifier for the room.
   - `hotel_id` (Foreign Key): References the hotel table.
   - `display_name`: Identifier for the room type.
   - `is_active`: Soft delete flag to indicate if the room is active.
   - `quantity`: Number of rooms of this type available.
   - `price_min`: Minimum price for the room.
   - `price_max`: Maximum price for the room.

3. **Facilities Table**
   - `facility_id` (Primary Key): Unique identifier for facilities.
   - `facility_name`: Name of the facility.

4. **Hotels_Facilities Table**
   - `hotel_id` (Foreign Key): References the hotel table.
   - `facility_id` (Foreign Key): References the facilities table.
   - `is_active`: Soft delete flag.

5. **Rooms_Facilities Table**
   - `room_id` (Foreign Key): References the rooms table.
   - `facility_id` (Foreign Key): References the facilities table.
   - `is_active`: Soft delete flag.

### Cache Considerations

- Redis Cache: Not utilized for Hotel Service due to non-critical path usage and cost-benefit trade-off. Redis cache is employed for the Booking Service instead.

## Booking Service

### Functionality

The Booking Service manages room bookings and checks availability. It interacts with the Hotel Service to handle room reservations and cancellations.

### Database Schema

1. **Available_Rooms Table**
   - `room_id` (Foreign Key): References the room table.
   - `date`: Date for which the availability is tracked.
   - `initial_quantity`: Initial number of rooms available.
   - `available_quantity`: Number of rooms remaining.

2. **Booking Table**
   - `booking_id` (Primary Key): Unique identifier for the booking.
   - `room_id` (Foreign Key): References the room table.
   - `user_id`: Identifier for the user making the booking.
   - `start_date`: Start date of the booking.
   - `end_date`: End date of the booking.
   - `number_of_rooms`: Number of rooms booked.
   - `status`: Booking status (`reserved`, `booked`, `cancelled`, `completed`).
   - `invoice_id`: Identifier for the invoice, if generated.

### Booking Flow

1. **Check Availability**
   - Query the `Available_Rooms` table to ensure sufficient room availability.

2. **Create Booking Record**
   - Insert a record into the `Booking` table with status `reserved`.

3. **Update Availability**
   - Decrement the `available_quantity` in the `Available_Rooms` table.

4. **Payment Handling**
   - If payment is successful, update the booking status to `booked` and generate an invoice.
   - If payment fails or booking times out, revert the booking status to `cancelled` and update availability.

### Cache and TTL Management

- **TTL with Redis**: Utilize Redis TTL for temporary room holds. Expiration handling includes updating booking status and availability based on payment success or failure.

### Optimizations

- **Key Eviction**: Remove expired keys proactively if the payment status is known to avoid unnecessary TTL callbacks.

## Scalability

All components are horizontally scalable. For handling traffic spikes, increase the number of nodes in individual services, databases, Kafka, and Hadoop clusters as required.

## Database Choices

### Relational Databases

- **Alternatives**: 
  - **PostgreSQL**
  - **SQL Server**
- **Criteria**: Any relational database providing ACID guarantees is suitable.

### Caching Mechanisms

- **Redis**: Chosen for its scalability and performance.
- **Alternatives**:
  - **Memcached**: An option for in-memory caching.

### Distributed Data Stores

- **Cassandra**: Preferred for its suitability in handling sharded data with minimal operational overhead.
- **Alternatives**:
  - **HBase**: A viable option but with higher operational complexity.

## Messaging Systems

- **Kafka**: Selected for its scalability and performance in message queuing.
- **Alternatives**:
  - **ActiveMQ**
  - **RabbitMQ**
  - **Amazon SQS**: Another potential option.

## Monitoring and Alerting

- **Tools**: 
  - **Grafana**: For monitoring CPU, memory, disk usage, and other metrics.
- **Practices**:
  - **Alerts**: Set thresholds for key metrics to notify the team of potential issues.
  - **Goal**: Maintain latency and high availability by addressing spikes and system overloads.

## Geographic Distribution

### Data Center Topology

- **Simple Approach**: 
  - **Primary**: DC1 
  - **Secondaries**: DC2, DC3, DC4
  - **Replication**: Data is replicated in near real-time.
- **Drawback**: Only 25% of capacity is actively used.

### Improved Approach

- **Regional Distribution**:
  - **Region 1**: DC1 (Primary) and DC2 (Secondary)
  - **Region 2**: DC3 (Primary) and DC4 (Secondary)
- **Benefits**:
  - **Low Latency**: Clients connect to the nearest data center.
  - **High Availability**: Failover between data centers within the same region.
  - **Geographic Segmentation**: Data is separated by geography, e.g., hotels in India vs. the US.

### Further Optimization

- **Additional Segmentation**: Potentially divide regions into smaller parts to further reduce latency and increase availability. For most hotel booking systems, a two-region setup is typically sufficient.

## Conclusion

The proposed design and alternatives provide a robust framework for handling data distribution, caching, messaging, and geographic redundancy, aiming to balance performance, scalability, and operational efficiency.

