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

# Design Google Drive

Google Drive is a cloud file storage product that allows users to store and access documents, videos, and other files from any device. Users can also share files with friends and family.

## Step 1 - Understand the Problem and Establish Design Scope

### Key Considerations
- **C**: What are the most important features?
  - **I**: Upload/download files, file synchronization, and notifications.

- **C**: Mobile or web?
  - **I**: Both.

- **C**: What are the supported file formats?
  - **I**: Any file type.

- **C**: Do files need to be encrypted?
  - **I**: Yes, files in storage need to be encrypted.

- **C**: Is there a file size limit?
  - **I**: Yes, files need to be 10 GB or smaller.

- **C**: How many users does the app have?
  - **I**: 10 million DAU (Daily Active Users).

### Features to Focus On
- Adding files
- Downloading files
- Syncing files across devices
- Viewing file revisions
- Sharing files with friends
- Sending notifications when files are edited, deleted, or shared

### Features Not Discussed
- Collaborative editing

### Non-Functional Requirements
- **Reliability**: Data loss is unacceptable.
- **Fast sync speed**: Ensure quick file synchronization.
- **Bandwidth usage**: Minimize network traffic and battery consumption.
- **Scalability**: Handle a high volume of traffic.
- **High availability**: Ensure system usability even when some services are down.

## Back-of-the-Envelope Estimation
- Assume 50 million sign-ups and 10 million DAU.
- Users get 10 GB of free space.
- Users upload 2 files per day, with an average size of 500 KB.
- 1:1 read-write ratio.
- **Total space allocated**: 50 million * 10 GB = 500 PB.
- **QPS for upload API**: 10 million * 2 uploads / 24 hours / 3600 seconds = ~240.
- **Peak QPS**: 480.

## Step 2 - Propose High-Level Design and Get Buy-In

### Design Overview
We'll start from a single server and scale out from there:

- **Web Server**: Handles file uploads and downloads.
- **Database**: Manages metadata, including user data, login info, and file info.
- **Storage System**: Stores the files.

**Example Storage**:
![Storage Example](../images/storage-example.png)

### APIs

- **Upload File**: `https://api.example.com/files/upload?uploadType=resumable`
- **Download File**: `https://api.example.com/files/download?fileId=1234567890`
- **Get File Revision History**: `https://api.example.com/files/revisions?fileId=1234567890`

### Moving Away from Single Server

As file uploads increase, the capacity of a single server will eventually be reached. To address this challenge, consider scaling your storage solution by implementing data sharding. This involves distributing each user's data across multiple servers:

**Sharding Example**:
![Sharding Example](../images/sharding-example.png)

While sharding helps manage increased storage needs, it introduces concerns about potential data loss. To mitigate these risks, consider using a robust off-the-shelf solution like Amazon S3, which provides replication options (same-region or cross-region) to ensure data durability:

**Amazon S3 Example**:
![Amazon S3](../images/amazon-s3.png)

Additional improvements to consider include:

- **Load Balancing**: Implement load balancing to evenly distribute network traffic across web server replicas.
- **More Web Servers**: Increase the number of web servers as needed, utilizing a load balancer to manage traffic distribution.
- **Metadata Database**: Move the database away from the server to avoid single points of failure. Employ replication and sharding to meet scalability requirements.
- **File Storage**: Use Amazon S3 for file storage, ensuring availability and durability by replicating files across multiple geographical regions.

**Updated Design**:
![Updated Simple Design](../images/updated-simple-design.png)

### Sync Conflicts

As the user base grows, sync conflicts become inevitable. To handle these conflicts, you can implement a strategy where the first modification to a file is considered the authoritative version:

**Sync Conflict Strategy**:
![Sync Conflict](../images/sync-conflict.png)

When a conflict arises, a second version of the file is created to represent the alternative file version. It is then up to the user to merge the conflicting versions:

**Sync Conflict Example**:
![Sync Conflict Example](../images/sync-conflict-example.png)

### High-Level Design

The high-level design of Google Drive involves several components working together to manage file storage and synchronization:

**High-Level Design**:
![High-Level Design](../images/high-level-design.png)

- **User Access**: Users interact with the application through a browser or mobile app.
- **Block Servers**: These servers handle file uploads and downloads. Files are split into blocks and stored in cloud storage. For example, Dropbox stores blocks of size 4 MB.
- **Cloud Storage**: Files are stored in the cloud, split into blocks for efficiency.
- **Cold Storage**: Used for storing inactive files that are infrequently accessed.
- **Load Balancer**: Distributes requests evenly among API servers to manage network traffic.
- **API Servers**: Handle tasks beyond file uploads, such as authentication, user profile management, and updating file metadata.
- **Metadata Database**: Stores information about files uploaded to cloud storage.
- **Metadata Cache**: Caches some metadata for faster retrieval.
- **Notification Service**: A publisher/subscriber system that notifies users of file updates, edits, or removals, allowing them to pull the latest changes.
- **Offline Backup Queue**: Queues file changes for users who are offline, enabling them to retrieve updates when they come back online.

### Step 3 - Design Deep Dive

Let's explore the components in greater detail:

#### Block Servers

Handling large files efficiently requires optimizing how files are sent and stored. Two key optimizations are:

- **Delta Sync**: Only the modified blocks of a file are sent to the block servers, rather than the entire file.
- **Compression**: Blocks are compressed to reduce data size. Different algorithms are used based on file type, such as gzip or bzip2 for text files.

Block servers also handle encryption before storing files:

**Block Servers Deep Dive**:
![Block Servers Deep Dive](../images/block-servers-deep-dive.png)

**Delta Sync Example**:
![Delta Sync](../images/delta-sync.png)

#### High Consistency Requirement

Our system demands strong consistency to avoid showing different versions of a file to different users. To achieve this:

- **Cache Consistency**: Ensure cache master and replicas are consistent and invalidate caches on database writes.
- **Database Consistency**: Use a relational database with ACID properties to ensure strong consistency.

#### Metadata Database

The metadata database schema includes:

**Metadata DB Deep Dive**:
![Metadata DB Deep Dive](../images/metadata-db-deep-dive.png)

- **User Table**: Contains basic user information.
- **Device Table**: Stores device information and push IDs for notifications.
- **Namespace**: Represents the root directory of a user.
- **File Table**: Stores file-related information.
- **File Version Table**: Maintains version history for files.
- **Block Table**: Contains information about file blocks, which can be reconstructed into file versions.

#### Upload Flow

**Upload Flow**:
![Upload Flow](../images/upload-flow.png)

The upload flow involves two parallel requests:

1. **Add File Metadata**:
   - Client sends a request to update file metadata.
   - Metadata is stored with a status of "pending."
   - Notification service is informed of the new file.
   - Users are notified of the new file upload.

2. **Upload Files to Cloud Storage**:
   - Client uploads file contents to block servers.
   - Block servers split, compress, encrypt, and upload blocks to cloud storage.
   - Upload completion triggers a callback to API servers.
   - Metadata DB status is updated to "uploaded."
   - Notification service updates clients about the new file.

#### Download Flow

**Download Flow**:
![Download Flow](../images/download-flow.png)

When a file is added or edited:

- **Notification**: Clients are notified of changes.
- **File Metadata**: Clients fetch file metadata from API servers.
- **Download Blocks**: Clients request and download file blocks from block servers.

#### Notification Service

The notification service informs clients of file changes using:

- **Long Polling**: Clients send requests that stay open until a change is received or a timeout occurs. This method is preferred for its simplicity in handling infrequent notifications.
- **Web Sockets**: Provides persistent, bidirectional communication, though not ideal for this use case.

#### Save Storage Space

To optimize storage usage:

- **De-duplicate Data Blocks**: Store identical blocks only once.
- **Intelligent Backup Strategy**: Limit version history and aggregate frequent edits.
- **Cold Storage**: Move infrequently accessed data to cheaper storage options like Amazon S3 Glacier.

#### Failure Handling

Handling typical failures:

- **Load Balancer Failure**: Secondary load balancer takes over.
- **Block Server Failure**: Other replicas handle the load.
- **Cloud Storage Failure**: Data is replicated across regions, and traffic is redirected.
- **API Server Failure**: Load balancer redirects traffic to other instances.
- **Metadata Cache Failure**: Replicated cache servers handle requests.
- **Metadata DB Failure**: Promote a slave to master if the master fails.
- **Notification Service Failure**: Clients reconnect to a different service replica.
- **Offline Backup Queue Failure**: Replicated queues ensure availability.

### Step 4 - Wrap Up

In summary, the Google Drive system design is characterized by:

- **Strong Consistency**: Ensures users see the same file version.
- **Low Network Bandwidth**: Efficient data transfer.
- **Fast Sync**: Quick synchronization across devices.

The design covers file upload and sync flows, with the possibility of discussing alternative approaches, such as direct block uploads to cloud storage, which might offer speed benefits but come with challenges like implementing chunking, compression, and encryption across different platforms.

Consider also separating online/offline logic into a dedicated service to allow other services to reuse it and support additional functionalities.
