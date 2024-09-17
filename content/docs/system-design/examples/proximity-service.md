+++
title= "Proximity Service"
tags = [ "system-design", "software-architecture", "interview", "proximity-service" ]
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
weight= 14
bookFlatSection= true
+++

# Proximity Service Design

## Overview

This document outlines the design of a proximity service, typically used in applications like Yelp for finding nearby businesses or in mapping apps for locating nearby points of interest. The focus is on designing a service to handle business searches based on user location within a specified radius.

## Functional Requirements

1. **Search Functionality**: Given a user's location and a search radius, return all businesses within that radius.
2. **Business Management**: Business owners can create, update, or delete business listings. Updates are not required to appear in real-time but should reflect by the next day.
3. **Business Details**: Users can view detailed information about a business.

## Non-Functional Requirements

1. **Low Latency**: The system should ensure quick response times for business searches.
2. **High Availability**: The service should handle traffic spikes and be resilient to failures.

### Estimated Scale

- **Daily Active Users (DAU)**: 100 million
- **Businesses**: 200 million
- **Search Queries**: Approximately 5,000 queries per second (5 queries per DAU).

## High-Level Design

### API Design

1. **Search API**:
   - **Endpoint**: `GET /search`
   - **Inputs**: Latitude, longitude, and optional search radius.
   - **Response**: List of businesses within the radius and total count.
   - **Notes**: Pagination is recommended but omitted in this design for simplicity.

2. **Business Management API**:
   - **CRUD Operations**: Endpoints for creating, reading, updating, and deleting businesses.

### Data Schema

1. **Business Table**:
   - **Purpose**: Stores detailed information about businesses.
   - **Primary Key**: Business ID.
   - **Schema**: Stores business details; size estimated at 1-10 KB per business, totaling in the low terabyte range.

2. **Location Table**:
   - **Purpose**: Supports fast searches for nearby businesses.
   - **Schema**: Stores business ID, latitude, and longitude (8 bytes each), totaling approximately 5 GB.
   - **Considerations**: Efficient indexing of location data for quick searches.

### Storage and Database Design

1. **Storage Requirements**:
   - **Business Table**: Low terabyte range.
   - **Location Table**: Approximately 5 GB, which allows for potential in-memory solutions.

2. **Database Architecture**:
   - **Primary-Secondary Setup**: 
     - **Primary Database**: Handles all write requests.
     - **Read Replicas**: Handle high read requests.
     - **Replication**: Data is replicated from the primary database to replicas, allowing for high read throughput with acceptable replication delay.

### Service Components

1. **Load Balancer**:
   - Distributes incoming traffic between the location-based and business services.
   - Stateless services can be easily scaled horizontally.

2. **Location-Based Service (LBS)**:
   - **Characteristics**: Read-heavy, stateless, and designed for high query per second (QPS) rates.
   - **Function**: Quickly finds nearby businesses based on location and radius.

3. **Business Service**:
   - **Characteristics**: Handles CRUD operations with lower QPS for writes but potentially high QPS for reads during peak times.
   - **Caching**: Consideration for caching to handle high read load effectively.

# Geospatial Indexing for Location-Based Search


Geospatial indexing is a crucial component for efficiently handling location-based searches, especially when managing large datasets of businesses or points of interest (POI). Two main approaches are commonly used for geospatial indexing: **hash-based** and **tree-based**. This document outlines the key considerations for building a geospatial index, focusing on Geohash-based indexing and its application in a relational database.

## Challenges with Traditional Indexing

Using traditional indexing methods, such as latitude and longitude indexes, for geospatial searches is inefficient due to the two-dimensional nature of location data. Fetching results within a search radius would require finding the intersection of longitude and latitude ranges, resulting in large datasets that are expensive to process. To solve this, we explore ways to map two-dimensional data into a one-dimensional index.

## Hash-Based Geospatial Indexing

### Geohash Overview

**Geohash** is a hash-based solution for geospatial indexing. It works by reducing two-dimensional location data (latitude and longitude) into a one-dimensional string of characters and digits. Geohash divides the world into a grid, and each grid cell is represented by a unique string. The precision of the geohash string determines the grid size.

- **Grid subdivision**: The world is divided into four quadrants, with each grid being subdivided further into smaller grids, each represented by additional bits in the geohash.
- **Geohash length**: The length of the geohash string determines the grid size. For example, a geohash length of 5 covers a grid of about 2km in size.
- **Use case**: Geohashes between lengths 4 to 6 are typically used in location-based services, providing a grid size suitable for proximity searches ranging from 0.5km to 20km.

### Handling Edge Cases

Geohash works well but has some edge cases:
- **Shared prefix issue**: Two geohashes with a long shared prefix are geographically close, but the reverse is not always true. Two nearby locations could have completely different geohashes, especially near the prime meridian or the equator.
- **Boundary problem**: Some geohashes may straddle grid boundaries. To address this, neighboring geohashes (eight neighbors) must also be queried.

### Geospatial Index Table Schema

The geospatial index table schema consists of two key columns:
- **Geohash**: The geohash string representing the business location at a specific precision (length 4 to 6).
- **Business ID**: A unique identifier for the business.

This schema allows fast lookups of businesses within a specific geohash region. Geohashes with the same prefix can be fetched using the SQL `LIKE` operator.

## Performance Considerations

### Table Size and Scalability

With an estimated table size of 6GB for 200M businesses, modern hardware can handle this easily. However, to manage high read queries per second (QPS), read replicas are recommended instead of sharding. Sharding introduces complexity in the application layer, while read replicas are simpler to maintain and scale.

### Caching

Caching is not necessarily beneficial for the geospatial index due to its small size. However, for larger datasets like the business table, caching frequently accessed data can significantly reduce the load on the database. Monitoring system performance helps in deciding whether to add a cache layer in the future.

## Tree-Based Geospatial Indexing

While not implemented in the design, tree-based solutions like **Quadtree** and **Google S2** offer another method for indexing geospatial data. Quadtrees recursively subdivide space into quadrants until certain criteria (e.g., number of businesses per grid) are met. Tree-based indexing operates as an in-memory data structure and may impose additional operational constraints compared to hash-based solutions.

## Final Design Overview

### Query Lifecycle

1. **Request**: The client sends a location and search radius (e.g., 500 meters) to the load balancer.
2. **Geohash Precision**: The service calculates the geohash precision corresponding to the search radius (e.g., a geohash length of 6 for a 500-meter search radius).
3. **Neighboring Geohashes**: The service fetches the geohash and its eight neighbors.
4. **Database Query**: A query is executed against the geospatial index table to retrieve business IDs and their lat/lng coordinates.
5. **Distance Calculation**: The service calculates the distance between the user and businesses, ranks the results, and returns them to the client.

### Scaling Strategy

The design initially relies on a single database with read replicas to handle read loads. As the application scales, monitoring will inform whether to add more read replicas, introduce a caching layer, or consider sharding the business table.

## Conclusion

Geohash-based indexing is a powerful tool for building scalable, efficient location-based search services. By using a simple table structure and leveraging the `LIKE` operator in SQL, a relational database can efficiently handle proximity searches. Future scalability can be achieved by adding read replicas and, if necessary, caching frequently accessed business data.


