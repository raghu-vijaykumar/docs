+++
title= "Google Drive"
tags = [ "system-design", "software-architecture", "interview", "google-drive" ]
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
weight= 12
bookFlatSection= true
+++

# Cloud Storage Solution Design

This documentation outlines the design of a cloud storage solution similar to Google Drive or Dropbox. The key focus areas include file storage, synchronization across devices, scalability, and fault tolerance. The design takes into consideration the complexities of managing large volumes of files, distributing them efficiently, and ensuring the system can scale to handle millions of users.

## User Flow Overview
- A user has an account on the cloud storage service and uses multiple devices (e.g., mobile, desktop).
- A synchronization app is installed on both devices, allowing files to be uploaded to the cloud from any device.
- Once a file is uploaded to the cloud, it is synchronized across all devices.
- Files deleted from one device are also removed from the other devices.

## Basic System Design

### Components
1. **User Devices (Mobile, Desktop):**
   - The user can upload and delete files.
2. **Cloud Storage Backend:**
   - Files are uploaded to a cloud storage service, where they are stored persistently.
   - Metadata for each file is saved in a database to track the file's ownership, name, size, and status.

### File Storage
- Storing files in a relational database is inefficient for binary data, so files are stored in the **file system**.
- **File metadata** is stored in a relational database for tracking purposes.

### File Upload Process
1. The user uploads a file.
2. The system stores the file's metadata (e.g., file name, user, size) in the database.
3. The file is saved to the file system.
4. The metadata is updated to reflect the successful upload, and the user is notified.

## Key Design Considerations

### Scalability
- With millions of users and billions of files, storage and scalability are critical.
- Cloud storage services must support scaling both in terms of user requests and the amount of data stored.

### File Distribution
- Files are distributed across multiple storage instances to handle large data volumes.
- The system must determine which storage instance should hold a file, taking into account:
  - Disk space availability.
  - Balancing load across storage nodes.

### Storage Estimation
- For example, if there are 100 million users with 1GB of space each, the system must support at least **100PB** of data.
- Storage instances typically hold **100TB** of data, requiring **1,000 instances** to support the total data volume.

### Load Balancing and Fault Tolerance
- To manage the load and ensure system reliability, a **file control service** is introduced.
- This service abstracts away the complexity of assigning storage instances and handles:
  - Selecting the correct storage node for file uploads.
  - Monitoring storage capacity and adding more instances when needed.
  - Redistributing files if a storage node fails or reaches capacity.

## Advanced Design

### Controller Pattern
- The **controller pattern** is used for managing storage instances, where a controller node manages a pool of worker nodes (storage instances).
- The controller node:
  - Tracks the status of each storage instance (e.g., space available).
  - Adds new storage instances when needed.
  - Rebalances files across storage instances when required.

### Consistent Hashing
- **Consistent hashing** can be used to assign files to storage nodes efficiently.
  - If a node is near capacity, the system can create a new node and redistribute files using consistent hashing to minimize data movement.

## Key Components

### 1. **Storage Architecture**
   - **Storage Nodes**: A large number of storage nodes are used to handle the actual file storage.
   - **Controller Nodes**: A small number of controller nodes manage the coordination and access to these storage nodes.

### 2. **File Metadata Design**
   - **Metadata Requirements**: The system stores metadata about each file, potentially up to **100 billion rows** (based on 100 million users and 1000 files per user on average).
   - **Metadata Storage Options**:
     - Store the **absolute path** of each file, with the root being the user's directory.
     - Store the **file name** and its parent directory separately.
   - **Chosen Approach**: The simpler method of storing the absolute path was selected, requiring **one entry per file**.
   - **Sharding Strategy**: To handle a large database (~10 terabytes), metadata is sharded using the **user ID hash** as the shard key to improve scalability.

### 3. **Client-Side Optimizations**
   - **Protocol**: **HTTPS** is used for secure file transfer, as alternatives like FTP, SFTP, and SCP were ruled out due to security and scalability concerns.
   
   - **File Compression**:
     - Client-side file compression reduces **network bandwidth** and **storage requirements**.
     - **Gzip** is the most common algorithm, but **Brotli** provides better compression.
     - **Drawbacks**: Compression can increase battery consumption on mobile devices or laptops.
   
   - **File Sync and Version Control**:
     - Sync issues (e.g., detecting changes between files with the same name but different content) can be addressed by using **checksums** or **Cyclic Redundancy Check (CRC)**.
     - CRC checks can detect differences in files based on content rather than just size.

   - **Delta Copying**:
     - To optimize the transfer of large files, only modified portions of the file (blocks) are uploaded instead of the entire file.
     - Each file is split into smaller blocks, and only modified blocks are transferred, reducing bandwidth and improving performance.
     - **Disadvantages**: Increased metadata storage, as **CRC for each block** must be stored along with the overall file CRC.

### 4. **Synchronization Between Clients**
   - **Synchronization Protocols**:
     - **Polling** and **Long Polling** are viable for file sync, with **Long Polling** providing a balance between responsiveness and reduced server load.
     - **WebSockets** were discarded due to the lack of a need for bidirectional communication and fast notification.
   - **Process Flow**:
     - Clients (e.g., a desktop) make **GET** requests to check for updates.
     - The server responds with updates (e.g., when a file is uploaded on a mobile device), prompting the client to download the new or updated files.

## System Design Summary

The proposed cloud storage system incorporates the following:
1. **Sharded Metadata Database**: Efficiently manages metadata for files using a sharded architecture.
2. **Optimized File Transfer**:
   - File compression to reduce storage and bandwidth.
   - Use of CRC for file content verification.
   - Delta copying to transfer only modified parts of files.
3. **Client Synchronization**: Long polling ensures efficient synchronization across multiple clients, with updates being fetched only when necessary.
4. **Scalability**: Designed to handle storage needs for up to **100 million users**, with provisions for **sharding** and **client-side optimizations** to ensure efficient performance at scale.

This system provides a robust and scalable solution for modern cloud storage needs, balancing **storage efficiency**, **data transfer optimization**, and **user experience** through effective client-server interactions.

## Further Improvements
- **Delta Sync Optimization**: Investigating more advanced methods of identifying and transferring minimal changes to large files.
- **Metadata Storage Enhancements**: Exploring more efficient storage and retrieval mechanisms for metadata as the system scales.
- **Mobile Device Battery Impact**: Further analysis of how client-side compression and syncing affect battery life on mobile devices and potential mitigations.



