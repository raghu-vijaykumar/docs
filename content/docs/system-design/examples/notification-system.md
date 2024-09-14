+++
title= "Notification System"
tags = [ "system-design", "software-architecture", "interview", "notification-system" ]
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
weight= 7
bookFlatSection= true
+++

# Design a Notification System

Notification systems are a popular feature in many applications, alerting users about important news, product updates, events, etc. They can be categorized into several types:

- Mobile push notifications
- SMS
- Email

## Step 1 - Understand the Problem and Establish Design Scope

### Key Questions

- **C**: What types of notifications does the system support?  
  **I**: Push notifications, SMS, Email

- **C**: Is it a real-time system?  
  **I**: Soft real-time. Notifications should be delivered as soon as possible, but delays are acceptable if the system is under high load.

- **C**: What are the supported devices?  
  **I**: iOS devices, Android devices, laptops/desktops.

- **C**: What triggers notifications?  
  **I**: Notifications can be triggered by client applications or on the server-side.

- **C**: Will users be able to opt-out?  
  **I**: Yes

- **C**: How many notifications per day?  
  **I**: 10 million mobile push, 1 million SMS, 5 million emails

## Step 2 - Propose High-Level Design and Get Buy-In

This section explores the high-level design of the notification system.

### Different Types of Notifications

#### iOS Push Notification
![ios-push-notifications](../images/ios-push-notifications.png)

- **Provider**: Builds and sends notification requests to Apple Push Notification Service (APNS). Needs:
  - **Device Token**: Unique identifier used for sending push notifications
  - **Payload**: JSON payload for the notification, e.g.:
    ```json
    {
       "aps": {
          "alert": {
             "title": "Game Request",
             "body": "Bob wants to play chess",
             "action-loc-key": "PLAY"
          },
          "badge": 5
       }
    }
    ```

- **APNS**: Service provided by Apple for sending mobile push notifications
- **iOS Device**: End client that receives the push notifications

#### Android Push Notification
Android uses a similar approach. Firebase Cloud Messaging is a common alternative to APNS:
![android-push-notifications](../images/android-push-notifications.png)

#### SMS Message
For SMS, third-party providers like Twilio are available:
![sms-messages](../images/sms-messages.png)

#### Email
Clients can set up their own mail servers or use third-party services like Mailchimp:
![email-sending](../images/email-sending.png)

Here's the final design after including all notification providers:
![notification-providers-design](../images/notification-providers-design.png)

### Contact Info Gathering Form

To send notifications, we need to gather user inputs at signup:
![contact-info-gathering](../images/contact-info-gathering.png)

Example database tables for storing contact info:
![contact-info-db](../images/contact-info-db.png)

### Notification Sending/Receiving Flow

Here's the high-level design of our notification system:
![high-level-design](../images/notification-system-hld.png)

- **Service 1 to N**: Other services or cron jobs that trigger notification sending events.
- **Notification System**: Accepts notification sending messages and propagates them to the correct provider.
- **Third-Party Services**: Responsible for delivering messages via the appropriate medium. This part should be built with extensibility in case we change third-party service providers in the future.
- **iOS, Android, SMS, Email**: Users receive notifications on their devices.

#### Potential Issues

- **Single Point of Failure**: Only a single notification service
- **Scalability**: Hard to scale since the notification system handles everything
- **Performance Bottleneck**: Handling everything in one system can be a bottleneck, especially for resource-intensive tasks

## High-Level Design (Improved)

Here are some changes from the original naive design:

- **Move database & cache out of the notification service**
- **Add more notification servers & set up autoscaling & load balancing**
- **Introduce message queues to decouple system components**

![high-level-design-improved](../images/notification-system-hld-improved.png)

### Components

- **Service 1 to N:** Services that send notifications within our system.
- **Notification Servers:** Provide APIs for sending notifications, visible to internal services or verified clients. Perform basic validation, fetch notification templates from the database, and put notification data into message queues for parallel processing.
- **Cache:** Stores user info, device info, and notification templates.
- **DB:** Stores data about users, notifications, settings, etc.
- **Message Queues:** Decouple components by serving as buffers for notifications. Each notification provider has a separate message queue to prevent outages in one provider from affecting others.
- **Workers:** Pull notification events from message queues and send them to the corresponding third-party services.
- **Third-Party Services:** Already covered in the initial design.
- **iOS, Android, SMS, Email:** Already covered in the initial design.

### Example API Call to Send an Email

```json
{
   "to":[
      {
         "user_id":123456
      }
   ],
   "from": {
      "email":"from_address@example.com"
   },
   "subject":"Hello World!",
   "content":[
      {
         "type":"text/plain",
         "value":"Hello, World!"
      }
   ]
}
```

### Example Lifecycle of a Notification

- Service makes a call to create a notification.
- Notification Service fetches metadata (user info, settings, etc.) from the database/cache.
- The Notification Event is sent to the corresponding queue for processing for each third-party provider.
- Workers pull notifications from the message queues and send them to third-party services.
- Third-Party Services deliver notifications to end users.

## Step 3 - Design Deep Dive

### Reliability

Questions to consider:

- What happens in the event of data loss?
- Will recipients receive notifications exactly once?

To avoid data loss, notifications can be persisted in a notification log database on the workers, which retry them if a notification doesn't go through:
![notification-log-db](../images/notification-log-db.png)

#### Handling Duplicate Notifications

Occasional duplicates may occur as exact-once delivery cannot always be guaranteed. Implement a deduplication mechanism to discard events with already seen IDs.

### Additional Components and Considerations

#### Notification Templates

To avoid building every notification from scratch, use notification templates:
```plaintext
BODY:
You dreamed of it. We dared it. [ITEM NAME] is back — only until [DATE].

CTA:
Order Now. Or, Save My [ITEM NAME]
```

#### Notification Settings
Before sending any notification, we first check if user has opted in for the given communication channel via this database table:
```
user_id bigInt
channel varchar # push notification, email or SMS
opt_in boolean # opt-in to receive notification
```

### Rate Limiting

To avoid overwhelming users with too many notifications, we can introduce client-side rate limiting. This ensures that users don't opt out of notifications immediately after being bombarded.

### Retry Mechanism

If a third-party provider fails to send a notification, it will be placed into a retry queue. If the problem persists, developers will be notified.

### Security in Push Notifications

Only verified and authenticated clients are allowed to send push notifications through our APIs. This is achieved by requiring an `appKey` and `appSecret`, inspired by Android and Apple notification servers.

### Monitor Queued Notifications

A critical metric to monitor is the number of queued notifications. If the queue grows too large, additional workers may be needed:
![notifications-queue](../images/notifications-queue.png)

### Events Tracking

Tracking certain events related to notifications, such as open rates and click rates, is important. This is typically done by integrating with an analytics service:
![notification-events](../images/notification-events.png)

## Updated Design

Here's the final design incorporating all improvements:
![final-design](../images/final-design.png)

### Added Features

- **Notification Servers**: Equipped with authentication and rate limiting.
- **Retry Mechanism**: Added to handle notification failures.
- **Notification Templates**: Added for a coherent notification experience.
- **Monitoring and Tracking**: Systems implemented to monitor system health and facilitate future improvements.

# Step 4 - Wrap Up

We have introduced a robust notification system that supports push notifications, SMS, and email. The system now uses message queues to decouple components.

### Key Improvements

- **Reliability**: Robust retry mechanism added to handle failures.
- **Security**: `appKey` and `appSecret` ensure that only verified clients can send notifications.
- **Tracking and Monitoring**: Implemented to monitor important statistics.
- **Respect User Settings**: Users can opt out of receiving notifications. The service checks user settings before sending notifications.
- **Rate Limiting**: Prevents bombarding users with excessive notifications all at once.
