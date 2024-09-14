+++
title= "Video Sharing"
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

# Design YouTube

This chapter is about designing a video-sharing platform such as YouTube. The solution can also be applied to designing platforms like Netflix and Hulu.

## Step 1 - Understand the Problem and Establish Design Scope

### Questions and Insights

* **What features are important?**
  * Upload video + watch video
  
* **What clients do we need to support?**
  * Mobile apps, web apps, smart TVs
  
* **How many DAUs do we have?**
  * 5 million
  
* **Average time per day spent on YouTube?**
  * 30 minutes
  
* **Do we need to support international users?**
  * Yes
  
* **What video resolutions do we need to support?**
  * Most of them
  
* **Is encryption required?**
  * Yes
  
* **File size requirement for videos?**
  * Max file size is 1GB
  
* **Can we leverage existing cloud infrastructure from Google, Amazon, Microsoft?**
  * Yes, building everything from scratch is not advisable.

### Features to Focus On

* Upload videos quickly
* Smooth video streaming
* Ability to change video quality
* Low infrastructure cost
* High availability, scalability, reliability
* Supported clients - web, mobile, smart TV

### Back of the Envelope Estimation

* Assume the product has 5 million DAUs
* Users watch 5 videos per day
* 10% of users upload 1 video per day
* Average video size is 300MB
* Daily storage cost needed: `5 million * 10% * 300MB = 150TB`
* CDN Cost, assuming $0.02 per GB: `5 million * 5 videos * 0.3GB * $0.02 = USD 150K per day`

## Step 2 - Propose High-Level Design and Get Buy-In

As previously discussed, we won't be building everything from scratch.

### Why?

* In a system design interview, choosing the right technology is more important than explaining how the technology works.
* Building scalable blob storage over CDN is complex and costly. Even big tech companies don't build everything from scratch. For instance, Netflix uses AWS and Facebook uses Akamai's CDN.

### High-Level System Design

![High-Level System Design](../images/high-level-sys-design.png)

* **Client**: You can watch YouTube on web, mobile, and TV.
* **CDN**: Videos are stored in CDN.
* **API Servers**: Everything else, except video streaming, goes through the API servers. This includes feed recommendation, generating video URLs, updating metadata DB and cache, and user signup.

### High-Level Design of Video Streaming and Uploading

#### Video Uploading Flow

![Video Uploading Flow](../images/video-uploading-flow.png)

* Users watch videos on a supported client.
* Load balancer evenly distributes requests across API servers.
* All user requests go through API servers, except video streaming.
* Metadata DB: Sharded and replicated to meet performance and availability requirements.
* Metadata Cache: For better performance, video metadata and user objects are cached.
* A blob storage system is used to store the actual videos.
* Transcoding/Encoding Servers: Transform videos to various formats (e.g., MPEG, HLS) suitable for different devices and bandwidths.
* Transcoded Storage: Stores result files from transcoding.
* Videos are cached in CDN - clicking play streams the video from CDN.
* Completion Queue: Stores events about video transcoding results.
* Completion Handler: A set of workers pulls event data from the completion queue and updates metadata cache and database.

#### Video Uploading Flow (Detailed)

![Video Uploading Flow](../images/video-uploading-flow.png)

* Videos are uploaded to original storage.
* Transcoding servers fetch videos from storage and start transcoding.
* Once transcoding is complete, two steps are executed in parallel:
  * Transcoded videos are sent to transcoded storage and distributed to CDN.
  * Transcoding completion events are queued in the completion queue. Workers pick up the events and update metadata database and cache.
* API servers inform users that uploading is complete.

#### Metadata Update Flow

![Metadata Update Flow](../images/metadata-update-flow.png)

* While the file is being uploaded, the user sends a request to update the video metadata (e.g., file name, size, format).
* API servers update metadata database and cache.

#### Video Streaming Flow

![Video Streaming Flow](../images/video-streaming-flow.png)

Users do not download the whole video at once. Instead, they download a small part and start watching it while downloading the rest. This is referred to as streaming. The stream is served from the closest CDN server for the lowest latency.

Some popular streaming protocols include:

* MPEG-DASH (Moving Picture Experts Group - Dynamic Adaptive Streaming over HTTP)
* Apple HLS (HTTP Live Streaming)
* Microsoft Smooth Streaming
* Adobe HTTP Dynamic Streaming (HDS)

Choosing the right streaming protocol is important to support our use case.

## Step 3 - Design Deep Dive

In this part, we'll deep dive into the video uploading and streaming flows.

### Video Transcoding

Video transcoding is important for several reasons:

* Raw video consumes a lot of storage space.
* Many browsers have constraints on the types of videos they support. Encoding a video ensures compatibility.
* To ensure good UX, serve HD videos to users with a good network connection and lower-quality formats to those with slower connections.
* Network conditions can change, especially on mobile. Automatically switching video formats at runtime is important for smooth UX.

**Transcoding Formats**:

* **Container**: The basket which contains the video file. Recognized by the file extension (e.g., .avi, .mov, .mp4).
* **Codecs**: Compression and decompression algorithms that reduce video size while preserving quality. Popular ones include H.264, VP9, HEVC.

### Directed Acyclic Graph (DAG) Model

Transcoding video is computationally expensive and time-consuming. Different creators have different inputs—some provide thumbnails, others do not; some upload HD videos, others do not.

To support video processing pipelines, dev customizations, and high parallelism, we adopt a DAG model:

![DAG Model](../images/dag-model.png)

Some tasks applied to a video file:

* Ensure video has good quality and is not malformed.
* Encode the video to support different resolutions, codecs, bitrates, etc.
* Automatically add a thumbnail if not specified by the user.
* Add a watermark if specified by the creator.

![Video Encodings](../images/video-encodings.png)

### Video Transcoding Architecture

![Video Transcoding Architecture](../images/video-transcoding-architecture.png)

#### Preprocessor

![Preprocessor](../images/preprocessor.png)

Responsibilities:

* **Video Splitting**: Splits video into groups of pictures (GOP) alignment—arranged groups of chunks that can be played independently.
* **Cache**: Stores intermediary steps in persistent storage to retry on failure.
* **DAG Generation**: Generates DAG based on config files specified by programmers.

Example DAG configuration with two steps:

![DAG Config Example](../images/dag-config-example.png)

#### DAG Scheduler

![DAG Scheduler](../images/dag-scheduler.png)

Splits a DAG into stages of tasks and puts them in a task queue, managed by a resource manager:

![DAG Split Example](../images/dag-split-example.png)

In this example, a video is split into video, audio, and metadata stages, which are processed in parallel.

#### Resource Manager

![Resource Manager](../images/resource-manager.png)

Responsible for optimizing resource allocation:

![Resource Manager Deep Dive](../images/resource-manager-deep-dive.png)

* **Task Queue**: A priority queue of tasks to be executed.
* **Worker Queue**: A queue of available workers and worker utilization info.
* **Running Queue**: Contains info about currently running tasks and which workers they're assigned to.

**How It Works**:

* Task scheduler gets the highest-priority task from the queue.
* Task scheduler selects the optimal worker to run the task.
* Task scheduler instructs the worker to start working on the task.
* Task scheduler binds the worker to the task and updates the running queue.
* Task scheduler removes the job from the running queue once the job is done.

#### Task Workers

![Task Workers](../images/task-workers.png)

Execute tasks in the DAG. Different workers handle different tasks and can be scaled independently.

![Task Workers Example](../images/task-workers-example.png)

#### Temporary Storage

![Temporary Storage](../images/temporary-storage.png)

Multiple storage systems are used for different types of data. For example, temporary images/video/audio are stored in blob storage, and metadata is stored in an in-memory cache as data size is small. Data is freed up once processing is complete.

#### Encoded Video

![Encoded Video](../images/encoded-video.png)

The final output of the DAG. Example output: `funny_720p.mp4`.

## System Optimizations

### Speed Optimization - Parallelize Video Uploading

Split video uploading into separate units via GOP alignment:

![Video Uploading Optimization](../images/video-uploading-optimization.png)

This enables fast resumable uploads if something goes wrong. Splitting the video file is done by the client.

### Speed Optimization - Place Upload Centers Close to Users

![Upload Centers](../images/upload-centers.png)

This can be achieved by leveraging CDNs.

### Speed Optimization - Parallelism Everywhere

Build a loosely coupled system and enable high parallelism.

Current components rely on inputs from previous components to produce outputs:

![No Parallelism Components](../images/no-parralelism-components.png)

Introduce message queues so components can start their tasks independently once events are available:

![Parallelism Components](../images/parralelism-components.png)

### Safety Optimization - Pre-Signed Upload URL

To avoid unauthorized users from uploading videos, introduce pre-signed upload URLs:

![Presigned Upload URL](../images/presigned-upload-url.png)

## Monitoring

Monitoring metrics include:

* **Video Uploading**: Number of requests per second, upload success rate, average upload time.
* **Video Transcoding**: Number of tasks processed, average time for transcoding, worker utilization, success/failure rate.
* **Video Streaming**: Number of streams, average streaming time, buffering rate, error rate.

### Monitoring Tools

Consider using tools like Prometheus for real-time monitoring, Grafana for visualization, and centralized logging tools like ELK (Elasticsearch, Logstash, Kibana) for log management.

## Summary

Designing a video-sharing platform like YouTube involves a range of features including video uploading, streaming, transcoding, and metadata management. It requires careful consideration of scalability, reliability, and cost. Leveraging existing cloud services and CDNs can significantly simplify the implementation and improve performance. Advanced techniques such as DAG for transcoding, parallel processing, and efficient monitoring are essential to handle large volumes of data and maintain a high-quality user experience.
