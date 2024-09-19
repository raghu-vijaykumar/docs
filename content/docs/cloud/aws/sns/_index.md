+++
title= "SNS"
tags = [ "sns", "aws", "cloud" ]
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


Amazon SNS Documentation
Overview
Amazon SNS (Simple Notification Service) is a fully managed messaging service that allows you to send messages and notifications to a variety of endpoints, including email, SMS, mobile push, and HTTP/S endpoints. It supports pub/sub (publish/subscribe) messaging and helps you decouple and scale microservices, distributed systems, and serverless applications.

Key Features:
Pub/Sub Messaging: Allows you to publish messages to topics and deliver them to multiple subscribers.
Scalable: Automatically scales to accommodate high throughput of messages.
Flexible: Supports various message formats and protocols, including SMS, email, and push notifications.
Secure: Provides encryption and access control mechanisms to ensure secure messaging.
Core Concepts
1. Topics
Definition: Logical access points for publishing messages. Topics are used to broadcast messages to multiple subscribers.

Usage: Publishers send messages to a topic, and all subscribers to that topic receive the message.

Example:

bash
Copy code
aws sns create-topic --name MyTopic
2. Subscriptions
Definition: Endpoints that receive messages from a topic. Subscribers can be email addresses, phone numbers, HTTP/S endpoints, or AWS services like Lambda.

Subscription Confirmation: For protocols like email, subscribers must confirm their subscription before they start receiving messages.

Example:

bash
Copy code
aws sns subscribe --topic-arn <topic-arn> --protocol email --notification-endpoint example@example.com
3. Messages
Definition: Payloads sent to topics that are delivered to all subscribed endpoints.
Message Attributes: Optional metadata associated with messages that can be used for filtering or routing.
4. Message Filtering
Definition: Allows subscribers to receive only messages that match specific criteria defined by message attributes.

Usage: Reduces the volume of messages sent to subscribers by filtering out irrelevant messages.

Example:

json
Copy code
{
  "FilterPolicy": {
    "Color": ["Red"]
  }
}
5. Publishing Messages
Definition: The process of sending messages to a topic, which are then distributed to all subscribed endpoints.

Delivery Formats: Messages can be formatted in JSON or plain text, depending on the protocol.

Example:

bash
Copy code
aws sns publish --topic-arn <topic-arn> --message "Hello, World!"
6. Dead-Letter Queues (DLQs)
Definition: Queues that store messages that could not be successfully delivered to subscribers after multiple attempts.
Usage: Helps in troubleshooting and handling undeliverable messages.
7. SMS Messaging
Definition: Allows you to send text messages to phone numbers.

Features: Supports delivery to global destinations, and allows setting message attributes like message type and sender ID.

Example:

bash
Copy code
aws sns publish --phone-number +1234567890 --message "Hello, this is a test message!"
8. Mobile Push Notifications
Definition: Sends push notifications to mobile devices through platforms like Apple APNs and Google GCM.

Usage: Integrates with mobile applications to send notifications to users.

Example:

bash
Copy code
aws sns publish --target-arn <platform-endpoint-arn> --message "Your message here"
9. HTTP/S Endpoints
Definition: Allows sending messages to web servers or applications over HTTP or HTTPS protocols.

Usage: Useful for integrating SNS with web services or custom applications.

Example:

bash
Copy code
aws sns subscribe --topic-arn <topic-arn> --protocol https --notification-endpoint https://example.com/endpoint
Security and Access Control
1. IAM Policies
Definition: Define permissions for accessing and managing SNS resources using AWS Identity and Access Management (IAM) roles and policies.

Example Policy:

json
Copy code
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "sns:Publish",
      "Resource": "arn:aws:sns:region:account-id:topic-name"
    }
  ]
}
2. Topic Policies
Definition: Policies attached to SNS topics to control access and permissions for publishing and subscribing.

Example Topic Policy:

json
Copy code
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "SNS:Subscribe",
      "Resource": "arn:aws:sns:region:account-id:topic-name",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "192.168.1.1/32"
        }
      }
    }
  ]
}
3. Encryption
Server-Side Encryption (SSE): Encrypts messages at rest using AWS Key Management Service (KMS) or Amazon S3-Managed Keys (SSE-S3).

Example Configuration:

json
Copy code
{
  "KmsMasterKeyId": "alias/aws/sns"
}
4. Access Logs
AWS CloudTrail: Records API calls made to SNS for auditing and compliance purposes.
Pricing
1. Request Costs
Charges: Based on the number of requests (e.g., Publish, Subscribe) and the number of messages sent.
2. Data Transfer Costs
Charges: Based on the amount of data transferred to and from SNS topics.
3. Additional Costs
SMS Messaging: Costs based on the number of SMS messages sent and the destination country.
Mobile Push Notifications: Typically included in SNS pricing but may incur additional costs with the push notification service providers.
Best Practices
Use Topics for Pub/Sub Messaging: Leverage SNS topics to broadcast messages to multiple subscribers and decouple components.
Implement Message Filtering: Use message attributes and filtering policies to target specific subscribers and reduce noise.
Monitor and Audit: Use AWS CloudWatch and CloudTrail to monitor SNS usage and troubleshoot issues.
Secure Your Topics: Apply appropriate IAM policies and topic policies to restrict access and ensure data protection.
Handle Failed Deliveries: Utilize dead-letter queues to capture and analyze undeliverable messages.
Amazon SNS provides a flexible and scalable messaging service, allowing you to send notifications and messages to various endpoints efficiently. It is suitable for applications requiring reliable message delivery and integration with multiple communication channels.