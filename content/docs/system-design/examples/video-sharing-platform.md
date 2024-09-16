+++
title= "Video Sharing Platform"
tags = [ "system-design", "software-architecture", "interview", "video-sharing" ]
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
bookFlatSection= true
+++

> TODO: Add diagrams

# Scalable Video on Demand Streaming Platform

This is sample design for any kind of video sharing platform like **youtube, netflix, prime video, hotstar** etc.

## Overview
This document describes the architecture for a highly scalable video on demand (VoD) streaming platform. The platform allows content creators to upload large video files and provides users the ability to search for and stream videos across various devices. The system supports millions of users with strict performance, availability, and scalability requirements.

## Functional Requirements

### 1. **Content Creator Features**
- Content creators can upload any type of video file, regardless of format or codec (live streaming is out of scope).
- Videos can be deleted but not modified after upload.
- Metadata for each video includes:
  - Title
  - Author
  - Description
  - Optional list of categories or tags (all fields can be updated anytime).
- An email notification is sent to the content creator once their video is ready for streaming.

### 2. **User Features**
- Registered and paid users can search for videos using free text queries based on video attributes (title, description, etc.).
- Users can watch videos on any device (web browser or mobile app) under various network conditions.
- Payment processes are out of scope for this design.

## Non-Functional Requirements

### 1. **Content Creators**
- **Scale**: Thousands of content creators, each uploading new videos weekly.
- **Video Size**: Average video size is 50GB, potentially high-resolution (up to 60 FPS), and possibly not optimally compressed.
- **Availability**: The system must provide high availability with an SLA of **three nines (99.9%)** for content creators.
- **Performance**: Guarantee **500ms page load time** for content creator UI at the 99th percentile.
- **Video Processing**: Videos should be available to users within a few hours after upload.
- **Consistency**: Prioritize consistency over availability in case of a network partition to ensure creators see the latest information about their videos.

### 2. **Users**
- **Scale**: Hundreds of millions of users browsing, searching, and watching videos.
- **Availability**: Guarantee **four nines (99.99%)** of uptime for users to ensure no interruptions during video streaming.
- **Performance**:
  - Search results should load within **500ms** at the 99th percentile.
  - **Zero buffering** is the goal for video playback, regardless of device or network conditions.
- **Consistency**: Prioritize availability over consistency for video search results, ensuring users can always retrieve some results, even if slightly outdated.

## Sequence of Operations

### 1. **Content Creator Workflow**
1. Content creator registers and logs into the system.
2. They access the upload page, providing video metadata (title, description, author, tags).
3. The system processes the video and metadata asynchronously.
4. An email notification is sent when the video is available for users to stream.

### 2. **User Workflow**
1. User logs into the system and accesses the home page.
2. User searches for videos using a query, which returns a list of video titles and IDs sorted by relevance (with pagination).
3. User selects a video, triggering a request for a video player page or a video URL.
4. User clicks the play button, and the system streams video and audio content.

## Key Architectural Considerations

- **API Pagination**: For search queries, results are paginated to reduce response size and latency.
- **Asynchronous Processing**: Video ingestion and email notifications for content creators are processed asynchronously to ensure performance.
- **Streaming Protocols**: The platform ensures optimal video streaming performance, supporting various devices and network conditions.
- **Fault Tolerance**: High fault tolerance and availability for both content creators and users to minimize downtime and ensure reliability.

It focuses on two primary functional requirements:
1. **Content Upload Flexibility**: Content creators can upload videos in various formats or codecs.
2. **Cross-Device Video Playback**: Users can watch videos on any device or browser, under varying network conditions.

The design addresses challenges in handling different video formats, transcoding, adaptive bitrate streaming, and packaging content for delivery over various protocols, while ensuring efficient video storage, seamless playback, and content protection.

---

## Key Concepts

### Video File Formats and Codecs
- **Containers**: Input video files can come in different containers (formats), holding multiple streams (e.g., video, audio, subtitles). Containers also store metadata such as codec, bitrate, resolution, and frame rate.
- **Codecs**: Algorithms used to encode/decode media streams. Initially, videos are often encoded in lossless formats (for editing), which are large and unsuitable for streaming. Thus, **transcoding** (converting to a lossy format) is necessary to reduce storage size.

### Transcoding for Multiple Formats
- To cater to different devices, we transcode videos into multiple output formats, resolutions, and bitrates. This ensures compatibility across a variety of:
  - **Devices**: Different computing power and screen sizes.
  - **Network Conditions**: Varying internet speeds and connection stability.

### Adaptive Bitrate Streaming
- **Adaptive Streaming**: Breaks videos into short segments (5-10 seconds) and creates a manifest file listing streams with their respective bitrates, resolutions, and codecs.
- **Dynamic Adaptation**: The user's video player selects segments based on real-time analysis of bandwidth and playback performance, adjusting the video quality dynamically to avoid buffering and optimize video experience.

### Packaging and DRM
- **Packaging**: Transcoded videos are packaged into different streaming protocols for various platforms (e.g., web, mobile). 
- **Digital Rights Management (DRM)**: Ensures that only authenticated users can access the videos via encryption, protecting against unauthorized access.

---

## Architecture

### Uploading and Storing Content
1. **API Gateway**: Handles authentication and authorization for content creators.
2. **Web Application Service**: Provides the user interface for uploading videos.
3. **Object Store**: Stores the uploaded video files.
4. **Video Data Service**: Stores metadata about the video (title, description, tags).

### Transcoding and Packaging
1. **Pipes and Filters Pattern**: 
   - After uploading, the **Object Store** publishes a notification to a **Message Broker**.
   - The **Transcoding Service** consumes this event, converts the video into multiple bitrates, and generates segments and a manifest file.
   - A notification is sent to the **Video Packaging Service**, which adds DRM encryption and packages the video into different protocols.
   - Packaged files and manifests are stored in the **Object Store**.

2. **Video Availability Notification**: 
   - The **Video Data Service** updates its database, marking the video as ready for streaming.
   - The **Email Notification Service** informs content creators that their video is available.

### User Experience and Streaming
1. **Search Service**: 
   - Optimized with a NoSQL database for efficient video search.
   - After videos are ready for streaming, metadata is indexed for search functionality.

2. **Video Streaming**:
   - Users can search for videos, select them, and request the appropriate stream URL from the **Video Data Service**.
   - The stream URL directs the user’s device to download the manifest file and start streaming video from the **Object Store**.

---

# Technical Documentation: Non-Functional Requirements for Scalable Video On-Demand Streaming Platform

## Overview

This document outlines the strategies and solutions for addressing non-functional requirements in the scalable video on-demand (VoD) streaming platform. These requirements include scalability, high availability, and performance for both content creators and users.

---

## Content Creator Requirements

### Scalability
- **Load Balancing**: Place a load balancer in front of the web application and video data services to handle content creator requests. 
- **Optimized Upload Flow**: Use **Signed URLs** (pre-signed URLs) to allow direct uploads to the object store, bypassing the API gateway and web application services. This reduces network and computing load on the system.

### High Availability
- **Redundancy**: Ensure high availability by using redundancy in services and a cloud-managed object store.
- **Database Replication**: Implement replication for the video data service database to prevent data loss in case of instance failures.

### Performance
- **Parallel Processing**: Implement parallelism in the video processing pipeline to handle simultaneous uploads and maintain service levels.
- **Consistency Over Availability**: Choose a database technology or configuration for the video data service to provide consistency and partition tolerance.

---

## User Requirements

### Scalability
- **Search Service**: Place the search service behind a load balancer and run it with multiple identical instances. 
- **Database Replication**: Replicate the search database to distribute requests across multiple instances.

### High Availability
- **Geographical Distribution**: Run the system across multiple isolation zones and geographical locations to ensure high availability.

### Performance
- **Content Delivery Network (CDN)**: Integrate a CDN with a push model into the video processing pipeline. Update the video data service with CDN locations to speed up video delivery. The CDN fetches content from the nearest edge server, improving initial segment load times and overall streaming performance.
- **Adaptive Bitrate Streaming**: Continue using adaptive bitrate streaming to minimize buffering and optimize video quality based on network conditions.

### Consistency Over Availability
- **Event-Driven Consistency**: The search service uses an event-driven pattern for data synchronization with the video data service, ensuring eventual consistency. Configure the search database to prioritize availability over consistency.

