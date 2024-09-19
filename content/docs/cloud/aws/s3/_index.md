+++
title= "S3"
tags = [ "s3", "aws", "cloud" ]
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
weight= 1
bookCollapseSection= true
+++


Amazon S3 Documentation
Overview
Amazon S3 (Simple Storage Service) is a scalable object storage service that provides high availability, durability, and security for storing and retrieving any amount of data from anywhere on the web. S3 is designed to handle vast amounts of data with minimal management overhead.

Key Features:
Scalable: Automatically scales to accommodate growing data needs.
Durable: Provides 99.999999999% (11 9's) durability by replicating data across multiple facilities.
Available: Designed for high availability with a Service Level Agreement (SLA) of 99.9% uptime.
Secure: Provides data encryption and access control features.
Core Concepts
1. Buckets
Definition: Containers for storing objects in S3. Each bucket has a globally unique name and is associated with a specific AWS region.

Bucket Policy: JSON-based policies that define permissions for accessing the bucket and its objects.

Example Bucket Policy:

json
Copy code
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    }
  ]
}
2. Objects
Definition: Files stored in S3. Each object consists of data, metadata, and a unique identifier (key).

Key: The unique name for an object within a bucket. Keys can be hierarchical, allowing for organizational structures similar to directories.

Example Key: images/photo.jpg

3. Storage Classes
Standard: Designed for frequently accessed data. Offers low latency and high throughput.
Intelligent-Tiering: Moves objects between two access tiers (frequent and infrequent) based on changing access patterns.
One Zone-IA: Lower-cost option for infrequently accessed data that does not require multiple availability zone resilience.
Glacier: For archival storage with retrieval times ranging from minutes to hours.
Glacier Deep Archive: The lowest-cost storage class for long-term archival with retrieval times of 12 hours.
4. Versioning
Definition: Enables the preservation of, retrieval of, and restoration of every version of every object stored in a bucket.
Usage: Protects against accidental deletions and overwrites.
5. Lifecycle Policies
Definition: Rules that automate the transition of objects between storage classes and the expiration of objects.
Example: Move objects to Glacier after 30 days and delete them after 365 days.
6. Access Control
Access Control Lists (ACLs): Define permissions at the object and bucket level.
Bucket Policies: JSON-based policies for fine-grained access control.
IAM Policies: Control access to S3 resources using IAM roles and policies.
7. Encryption
Server-Side Encryption (SSE): Encrypts data at rest using keys managed by AWS (SSE-S3), AWS Key Management Service (SSE-KMS), or customer-provided keys (SSE-C).
Client-Side Encryption: Encrypt data before uploading it to S3 using your own encryption methods.
8. Event Notifications
Definition: Notify other services or applications when objects are created, deleted, or modified in a bucket.
Examples: Trigger an AWS Lambda function, send a message to an SQS queue, or publish a message to an SNS topic.
Data Management
1. Upload and Download
Single Upload: For objects smaller than 5 GB.
Multipart Upload: For objects larger than 5 GB. Allows for parallel uploads and resuming interrupted uploads.
2. Data Transfer Acceleration
Definition: Speeds up the transfer of files to and from S3 using CloudFront’s globally distributed edge locations.
3. Cross-Region Replication (CRR)
Definition: Automatically replicates objects across different AWS regions for compliance, lower latency, or disaster recovery.
4. Cross-Account Access
Definition: Allows you to grant access to your S3 resources from accounts other than your own.
5. Data Consistency
Strong Consistency: New objects and updates to existing objects are immediately visible for read operations.
Security
1. Identity and Access Management (IAM)
IAM Roles: Define permissions for accessing S3 resources.
IAM Policies: Attach policies to users, groups, or roles to control access.
2. Bucket Policies and ACLs
Bucket Policies: Use JSON to specify access rules at the bucket level.
ACLs: Set permissions on individual objects or buckets.
3. Encryption
Server-Side Encryption: Encrypts data stored in S3 using various methods.
Client-Side Encryption: Encrypt data before uploading.
4. Audit Logging
AWS CloudTrail: Records API calls made to S3 for auditing and compliance purposes.
Pricing
1. Storage Costs
Charges: Based on the amount of data stored and the storage class used.
2. Data Transfer Costs
Charges: Based on the amount of data transferred out of S3 to the internet or other AWS regions.
3. Request Costs
Charges: Based on the number of requests made (e.g., GET, PUT, DELETE).
4. Additional Features
Data Transfer Acceleration: Additional costs for accelerated data transfers.
Cross-Region Replication: Costs associated with replication data transfer.
Best Practices
Use Lifecycle Policies: Manage costs by automatically transitioning objects to cheaper storage classes or deleting them.
Enable Versioning: Protect against accidental data loss by keeping multiple versions of objects.
Use Encryption: Ensure data security by encrypting data at rest and in transit.
Implement Access Controls: Use IAM roles, bucket policies, and ACLs to control access to your S3 resources.
Monitor and Audit: Use CloudWatch and CloudTrail to monitor access and changes to your S3 data.
Amazon S3 provides a robust, scalable, and secure solution for object storage, making it suitable for a wide range of applications from data backup and recovery to big data analytics and content distribution.