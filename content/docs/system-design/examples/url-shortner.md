+++
title= "URL Shortner"
tags = [ "system-design", "software-architecture", "interview", "url-shortner" ]
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

# URL Shortening Service - Merged Approach

This document outlines the design of a scalable, efficient, and reliable URL shortening service similar to TinyURL by combining concepts from both Doc 1 and Doc 2.

## Step 1 - Understand the Problem and Establish Design Scope

### Functional Requirements (FR)
1. **Shorten URL**: Given a long URL, return a shortened URL.
2. **Redirect**: When a short URL is accessed, redirect the user to the original long URL.

### Non-Functional Requirements (NFR)
1. **High Availability**: The service must ensure continuous operation.
2. **Low Latency**: The service should respond quickly to shorten URL requests and redirection.

### Traffic Volume
- 100 million URLs are generated per day, which translates to ~1200 URLs per second.
- Assuming a read-to-write ratio of 10:1, we expect 12,000 reads per second.
- The service must support 365 billion records over 10 years.

### URL Length
The shortened URL should use alphanumeric characters (`[A-Z], [a-z], [0-9]`), providing 62 possible characters. We calculate the length of the short URL based on the expected number of URLs.

### Short URL Length Calculation
Using the formula `n = log62(Y)` where `Y` is the total number of unique URLs, we find that:
- With a 7-character short URL (`62^7`), we can support 3.5 trillion unique URLs, which is more than enough for large-scale use.

---

## Step 2 - High-Level Design and API Endpoints

We'll design a REST API with two main endpoints:
1. `POST /api/v1/data/shorten`: Accepts a long URL and returns a short one.
2. `GET /api/v1/{shortURL}`: Returns the long URL for HTTP redirection.

### URL Redirecting
When a user accesses a short URL, it can be redirected to the long URL using HTTP 301 (permanently moved) or 302 (temporarily moved) status codes. 

![tinyurl-example](../images/tinyurl-example.png)
- **301 (Permanent Redirect)**: Tells the browser to bypass the URL shortening service in subsequent requests, reducing server load.
- **302 (Temporary Redirect)**: Retains tracking information by ensuring the browser always contacts the shortening service.

To improve performance, we use caching to handle read-heavy traffic.

---

## Step 3 - Deep Dive into System Components

### Components:
1. **UI**: Takes a long URL as input and provides a shortened URL as output.
2. **Short URL Service**: Handles the core logic of shortening the URL, storing the mapping, and redirecting on access.
3. **Database**: Stores the mappings between long and short URLs.
4. **Token Service**: Manages unique number ranges to avoid URL collisions.

### Architecture Flow:
1. A request to shorten a URL is sent to the Short URL Service.
2. The service generates a unique ID, converts it to Base62, and returns the short URL.
3. The long and short URLs are stored in a database.
4. When a short URL is accessed, the Short URL Service fetches the long URL and redirects the user.

---

### URL Collision Prevention
Multiple instances of the service could generate the same short URL, leading to collisions. Two main strategies are discussed:

1. **Base62 Encoding**: Each instance generates a unique numeric ID, which is converted to a Base62 string. This approach avoids collisions but requires a distributed unique ID generator.
2. **Hashing with Collision Detection**: Using a hash function (like MD5 or SHA256) to generate the short URL. To handle collisions, rehashing is done until a unique value is found.

We recommend **Base62 encoding** because it avoids collisions entirely by relying on unique numeric IDs.

### Token Service
A **Token Service** ensures that each instance of the Short URL Service generates unique IDs by assigning distinct token ranges. Each instance receives a range of numbers to convert into Base62 strings, guaranteeing no overlap across instances.

---

### Hash Function
The short URL consists of characters `[0-9a-zA-Z]`, and we calculate that 7 characters are sufficient to support 365 billion URLs over 10 years.

#### Base62 Conversion
Base62 encoding converts numeric IDs into alphanumeric short URLs. Using a unique numeric ID allows us to easily generate and track the next available short URL, which is ideal for distributed systems.

```python
def encode_base62(num):
    characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    base = len(characters)
    encoded = []
    
    while num > 0:
        num, rem = divmod(num, base)
        encoded.append(characters[rem])
    
    return ''.join(reversed(encoded))

# Example usage
unique_id = 123456789
short_url = encode_base62(unique_id)
print(short_url)  # Output: "8M0kX"
```

### Database Selection
We recommend using **Cassandra** due to its scalability, high availability, and ability to handle a high volume of writes across multiple data centers.

---

### Handling Failures
If a service instance crashes, the token ranges allocated to that instance may be lost. However, with trillions of possible unique URLs, this is not a significant issue. The system continues to function effectively despite these occasional losses.

---

## Step 4 - Scaling and Optimization

### Caching
To handle read-heavy workloads, we introduce a cache for URL lookups. When a short URL is accessed:
- The service first checks the cache for the long URL.
- If found, the service redirects immediately.
- If not, the service queries the database, stores the result in the cache, and redirects the user.

### Scaling the System
- **Web Tier Scaling**: The stateless design allows easy scaling by adding more service instances behind a load balancer.
- **Database Scaling**: Use replication and sharding to scale the database as the number of URLs grows.

---

## Step 5 - Additional Considerations

### Rate Limiting
A rate limiter can be introduced to prevent malicious users from overloading the service with excessive URL shortening requests.

### Analytics
We can integrate analytics tracking to provide insights on the number of times a short URL was accessed and other useful metrics for clients.

---

## Conclusion
The merged design provides a scalable, reliable, and highly available URL shortening service. By combining Base62 encoding, caching, and distributed token generation, we ensure low latency, collision-free URL generation, and efficient handling of high traffic.
