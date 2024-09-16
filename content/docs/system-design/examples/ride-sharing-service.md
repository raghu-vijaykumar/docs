+++
title= "Ride Sharing Service"
tags = [ "system-design", "software-architecture", "interview", "ride-sharing" ]
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
weight= 13
bookFlatSection= true
+++
> TODO: Add diagrams

# Ridesharing Service

This document outlines the design and requirements for a highly scalable ridesharing service that matches users with drivers, handles payments, and provides journey records.

## Functional Requirements

1. **User and Driver Registration**:
   - Users can register and log in to the service.
   - Drivers are manually registered and go through a background check and vehicle inspection.

2. **Ride Matching**:
   - Drivers can be matched with multiple riders if both riders agree and the driver has sufficient vehicle capacity.
   - The system should efficiently find and match users with the nearest available drivers to minimize wait times and travel distances.

3. **Pricing and Payments**:
   - The ride fare includes a base fee, duration, and distance traveled.
   - The formula for calculating the fare is subject to change.
   - Payment processing is managed via third-party services for credit card payments and driver bank transfers.

4. **Tracking and ETA**:
   - Track driver locations every 5 to 10 seconds.
   - Use external services for real-time ETA calculations based on current traffic conditions.

5. **User Experience**:
   - Fast login with error messages for incorrect usernames should be provided within 100 milliseconds.
   - Ride matching should be completed within 5 seconds at the 50th percentile and 10 seconds at the 99th percentile.

## Non-Functional Requirements

1. **Scalability**:
   - Handle millions of users and hundreds of thousands of drivers globally.
   - Scale out during peak hours and scale in during low demand periods.

2. **High Availability**:
   - Aim for at least four nines (99.99%) availability, striving for zero downtime.
   - Ensure the system can gracefully handle traffic spikes and outages.

3. **Performance**:
   - Fast user login and prompt ride matching to reduce user wait times and driver idle times.

## System Components and Workflow

1. **Authentication**:
   - Drivers receive an authentication token upon login.
   - Users receive an authentication token after registration and login.

2. **Driver Availability**:
   - Drivers join the pool of available drivers and send location updates every 5 seconds.
   - A bi-directional communication protocol is used for location updates.

3. **Ride Request**:
   - Users request rides by sending their location (latitude and longitude).
   - The system matches the user with the nearest available driver considering distance and traffic.

4. **Ride Execution**:
   - The driver receives user details and the trip's destination.
   - Both the driver and rider receive ongoing location updates until the ride starts.

5. **Ride Completion**:
   - The driver initiates the ride start, sending a trip start request.
   - Location updates continue until the ride is completed or manually ended.
   - The system calculates fare, processes payments, and sends receipts with journey maps to both users and drivers.

## API and Sequence of Events

1. **Driver API**:
   - Login: Receives an authentication token.
   - Join Pool: Adds to the pool of available drivers.
   - Location Updates: Sent every 5 seconds.

2. **User API**:
   - Registration: Captures username, password, and payment details.
   - Login: Receives an authentication token.
   - Ride Request: Sends current location for ride matching.

3. **Ride Matching**:
   - System identifies nearby drivers and matches based on proximity and traffic.
   - Notifies both driver and user of ride details and updates.

## State Diagram for Drivers

1. **States**:
   - **Logged Out**: Driver is not available for rides.
   - **Available for Rides**: Driver is ready to pick up riders but not currently earning.
   - **In Trip**: Driver is engaged in a ride and earning.
   - **Transition**: Drivers may transition between states based on ride status and availability.

2. **Transitions**:
   - From Logged Out to Available for Rides upon login.
   - From Available for Rides to In Trip when matched with a user.
   - From In Trip to Available for Rides after completing a ride.
   - From Available for Rides to Logged Out when opting to stop taking rides.

## Components

### 1. **User and Driver Services**
- **User Service**:
  - **Purpose**: Manages user registration, login, and authentication.
  - **Database**:
    - **User Table**: Stores user ID, name, credentials, age, and profile image link.
  - **External Storage**:
    - **Profile Images**: Stored in an object store.
  - **Credit Card and Billing Information**: Managed by a separate Payment Service.
  
- **Driver Service**:
  - **Purpose**: Manages driver registration and login.
  - **Database**:
    - **Driver Table**: Stores driver name, credentials, driver's license, vehicle information, and profile image link.
  - **External Storage**:
    - **Profile Images**: Stored in an object store.
  - **Bank Information**: Managed by a separate Payment Service.

### 2. **Payment Service**
- **Purpose**: Handles billing and payments.
- **Database**: Stores credit card and bank information.
- **Integration**:
  - **Credit Card Processing**: Interfaces with external APIs for processing payments.
  - **Bank Integration**: Interfaces with banks for driver payments.

### 3. **Driver Service**
- **Purpose**: Manages driver status, location updates, and ride assignments.
- **Connection**: Long and bidirectional connection for real-time updates.
- **Location Service**:
  - **Purpose**: Stores and manages driver locations and states.
  - **Database**: NoSQL high-performance database for storing the latest driver location.
  - **Event-Driven Model**: Uses a message broker to handle location updates.

### 4. **Rider Service**
- **Purpose**: Manages ride requests and provides updates to riders.
- **Connection**: Long and bidirectional connection for real-time updates.

### 5. **Matching Service**
- **Purpose**: Matches riders with drivers based on proximity and estimated time of arrival (ETA).
- **Process**:
  - Retrieves driver locations from the Location Service.
  - Requests ETA from an external service based on driver and rider locations.
  - Chooses the best driver based on ETA and other factors.

### 6. **Trip Service**
- **Purpose**: Manages trip details and status.
- **Database**: Stores trip start time, end time, distance, and fare calculations.
- **Process**:
  - Creates trip entries when a match is made.
  - Updates the trip status based on driver actions and location updates.

### 7. **Post-Trip Activities**
- **Purpose**: Handles billing, payment, and trip data processing.
- **Tasks**:
  - Generates trip maps.
  - Sends receipts and emails.
  - Updates payment and billing information.

### Post-Trip Processing

After a trip is completed, several critical actions are required:

1. **Fare Calculation**:
   - Compute the total fare for the trip.
   - Determine the driver's share, accounting for the company's revenue cut.

2. **Generate and Send Trip Information**:
   - **Trip Map**: Create a visual map of the trip using historical location data.
   - **User Notification**: Email the trip map and receipt to the user.
   - **Driver Notification**: Email the trip map and payment confirmation to the driver.

3. **Asynchronous Processing**:
   - The Trip Service calculates fare and generates events for the Payment Service.
   - The Payment Service processes payments and communicates with third-party APIs.
   - Upon confirmation, the Payment Service triggers the Trip Map Generator Service to create and cache the trip map.
   - The Notification Service sends out emails and push notifications to the user and driver.

### System Scalability Enhancements

1. **Load Balancing and Scaling**:
   - Use load balancers for services exposed via direct web APIs (User Service, Driver Service).
   - Implement dynamic scaling for backend services (Trip Map Generator Service, Notification Service) that do not handle direct web traffic.

2. **Connection Management**:
   - Deploy a Connection Manager Service with an in-memory Key-Value Store to efficiently map user and driver connections.
   - The Connection Manager Service helps in locating the correct service instance for real-time updates.

3. **Data Store Management**:
   - Utilize partitioning and sharding to scale databases.
   - Add replication to databases and message brokers to enhance high availability.

### Performance Optimization

1. **Fast User Login**:
   - Implement a Bloom Filter to efficiently check username existence and minimize database queries.
   - The Bloom Filter is more space-efficient than a hash table, reducing memory usage while maintaining performance.

2. **Handling Collisions**:
   - Use multiple hash functions in the Bloom Filter to minimize false positives and improve accuracy.

3. **Connection Management with Bloom Filters**:
   - A Bloom Filter allows for quick user lookup and reduces unnecessary database load, especially as the number of users grows.

### Performance Bottleneck

The core challenge in the rideshare service is efficiently matching users requesting rides with available drivers. This involves several steps:
1. **Identify Nearby Drivers**: Create a virtual circle around the user and identify all drivers within this radius.
2. **Calculate ETA**: Make external API calls to calculate the Estimated Time of Arrival (ETA) for each driver.
3. **Select Driver**: Choose the most suitable driver based on the ETA and business logic.

The significant bottleneck is determining which drivers are within a specified radius of the user. This process requires:
- **Distance Calculation**: Using the latitude and longitude of both the driver and the user to calculate distance.
- **Efficiency**: Performing this calculation across potentially hundreds of thousands of drivers.

## Solution Approach

### Distance Calculation

To calculate the distance between two points on Earth's surface, use the Haversine formula, which accounts for Earth's spherical shape. The formula is:

{{< katex display = "true" >}}
d = 2 \cdot R \cdot \text{asin}\left(\sqrt{\text{sin}^2\left(\frac{\Delta \phi}{2}\right) + \text{cos}(\phi_1) \cdot \text{cos}(\phi_2) \cdot \text{sin}^2\left(\frac{\Delta \lambda}{2}\right)}\right)
{{< /katex >}}

Where:
- \( \phi_1 \) and \( \phi_2 \) are the latitudes of the two points in radians.
- \( \Delta \phi \) and \( \Delta \lambda \) are the differences in latitude and longitude, respectively.
- \( R \) is the Earth's radius.

### Optimized Approach: Geohashing

To address performance challenges, employ Geohashing:
1. **Earth Division**: Divide Earth's surface into cells using Geohash.
2. **Cell Mapping**: Each cell is assigned a unique identifier. As drivers update their locations, the corresponding cell ID is updated in the database.
3. **Cell Querying**: When a user requests a ride, identify cells within the virtual circle and query the database for drivers within those cells.

#### Benefits of Geohashing:
- **Efficiency**: Simplifies and speeds up the query process.
- **Indexing**: Cell IDs can be indexed, allowing for rapid searches.
- **Sharding**: Facilitates data sharding across multiple database instances for scalability.

### Implementation Steps

1. **Location Update**: Calculate the Geohash for each driver's location and store it in the database.
2. **Ride Request**: Determine the cells within the virtual circle around the user.
3. **Driver Query**: Fetch drivers from the identified cells.
4. **ETA Calculation**: Send coordinates of selected drivers to the external ETA service and proceed with driver selection.

## Summary

- **Challenge**: Calculating distances between users and drivers efficiently.
- **Solution**: Use Geohashing to divide Earth into manageable cells.
- **Implementation**: Update driver locations with Geohash, query relevant cells, and calculate ETAs for nearby drivers.

By adopting this approach, the system can handle high volumes of ride requests and driver updates efficiently, ensuring a scalable and performant ridesharing service.


