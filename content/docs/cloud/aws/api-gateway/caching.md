+++
title= "Caching"
tags = [ "api-gateway", "aws", "cloud", "caching" ]
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
weight= 5
bookFlatSection= true
+++

# Caching in Amazon API Gateway

**Caching** in **Amazon API Gateway** is a powerful feature that allows you to store and reuse the responses from your API calls, improving performance and reducing the load on your backend services. By enabling caching, you can reduce latency and limit the number of requests that hit your backend systems, leading to improved efficiency and cost savings.

---

## Key Concepts

### 1. **API Gateway Cache**
API Gateway cache stores the responses to API requests for a configurable time, known as the **Time-To-Live (TTL)**. When a client makes a request that matches a cached response, the response is served from the cache rather than the backend service.

### 2. **Cache Keys**
API Gateway uses **cache keys** to determine whether a request is cacheable. By default, the cache key includes:
- The request URL (method and path).
- Query string parameters.

You can configure additional cache key components such as headers, request body, or certain path parameters to influence cache hits.

### 3. **Time-To-Live (TTL)**
**TTL** defines how long the cached response will be retained. You can set the TTL between 0 seconds (no caching) and 3600 seconds (1 hour). After the TTL expires, the cached response is invalidated and the next request will hit the backend service.

---

## Benefits of API Gateway Caching

1. **Reduced Latency**: Cached responses can be served to the client without going to the backend service, leading to faster response times.
2. **Backend Offloading**: Caching can offload repeated, identical requests from your backend, reducing server load.
3. **Cost Efficiency**: By reducing the number of requests that reach your backend, caching helps lower costs, particularly in scenarios where backends are charged per request or computation.
4. **Scalability**: For APIs that experience heavy traffic, caching improves scalability by reducing the amount of work backend systems must handle.

---

## Cache Implementation Example

### Scenario:
You have an API endpoint `GET /products/{productId}` that retrieves product information from a database. To optimize performance, you want to cache product details so that repeated requests for the same product don't hit the database.

### Step-by-Step Cache Configuration:

#### 1. **Create an API in Amazon API Gateway**
- Create a REST API with the endpoint `GET /products/{productId}`.
- Integrate the API with your backend service (e.g., a Lambda function or HTTP endpoint that queries the product database).

#### 2. **Enable API Caching**
- In the **Stages** section of the API Gateway console, select your API stage (e.g., `prod` or `dev`).
- Enable **Caching** for the stage. You can choose a cache size depending on your requirements (e.g., 0.5 GB, 1 GB, etc.).

#### 3. **Set the Cache TTL**
- Set the **TTL** for the cache. For example, set TTL to 300 seconds (5 minutes) so that responses are cached for 5 minutes.
  
#### 4. **Configure Cache Key Parameters**
- Go to the **Method Request** section for `GET /products/{productId}`.
- Choose **Caching** and configure the cache key. By default, the `productId` path parameter will be included in the cache key. This ensures that each product has its own cache entry.

#### 5. **Deploy the API**
- Deploy the API to your stage. The API now caches the responses for product requests.

### Example Request:

1. The first request to `GET /products/123` will hit the backend service, retrieve the product information, and store the response in the cache.
   
2. Subsequent requests to `GET /products/123` within the next 5 minutes will return the cached response directly from the API Gateway cache without contacting the backend.

---

## Cache Invalidation

Sometimes, cached data becomes outdated (e.g., if the product details are updated). In such cases, API Gateway provides mechanisms to invalidate the cache and fetch fresh data.

### Methods to Invalidate Cache:

1. **Manually Clear the Cache**:
   - In the API Gateway console, you can manually invalidate all cached responses by selecting the **Invalidate Cache** option for a particular stage.

2. **Invalidate Cache per Request**:
   - You can force cache invalidation for specific API requests by adding the `Cache-Control: max-age=0` header in the client request. This instructs API Gateway to bypass the cache and fetch fresh data from the backend.

---

## Example: Selective Caching

In some cases, you may want to cache only certain responses based on specific query parameters or headers.

### Scenario:
You have an API endpoint `GET /products?category=electronics` to fetch products by category. You want to cache the responses based on the category, but not for other query parameters (e.g., sorting).

### Step-by-Step:

1. **Enable Caching** for the `GET /products` method.
2. **Configure Cache Keys**:
   - Go to **Method Request** and specify that the `category` query parameter should be part of the cache key. This way, requests with different `category` values (e.g., electronics, furniture) will have separate cache entries.
   - You can exclude other query parameters (like sorting options) from the cache key to ensure they do not affect the cache hits.

---

## Caching Considerations and Best Practices

### 1. **Caching Time-to-Live (TTL) Strategy**:
   - Choose an appropriate TTL based on the nature of your data. For highly dynamic data (e.g., stock prices), you may want shorter TTLs (e.g., 30 seconds). For static or less frequently updated data (e.g., product descriptions), longer TTLs (e.g., 10 minutes) are more suitable.
  
### 2. **Cache Size**:
   - The cache size you select (ranging from 0.5 GB to 237 GB) should depend on the expected volume of traffic and the size of the cached responses. Monitor cache hit/miss metrics to optimize cache usage.

### 3. **Monitor Cache Performance**:
   - Use **Amazon CloudWatch** to monitor cache hits and misses. A high cache hit rate indicates that the cache is effectively serving responses, while a low cache hit rate might indicate that requests are bypassing the cache too frequently.
   
### 4. **Use Cache Invalidation When Necessary**:
   - Implement cache invalidation logic where appropriate. For example, if a product update occurs in your backend, invalidate the cache for that product to ensure clients receive the updated information.

### 5. **Limit Cacheable Responses**:
   - Not all responses should be cached. For instance, you may want to avoid caching user-specific or personalized data (e.g., user profiles). You can configure API Gateway to selectively enable or disable caching based on request headers or query parameters.

---

## Example of Cache Metrics in CloudWatch

Amazon API Gateway provides the following CloudWatch metrics to monitor cache performance:

1. **CacheHitCount**: The number of times a response was served from the cache.
2. **CacheMissCount**: The number of times a request was forwarded to the backend because no valid response was found in the cache.

### Monitoring Example:

```shell
CacheHitCount: 250 (High hit count means the cache is being utilized effectively)
CacheMissCount: 50 (Low miss count indicates that the cache is efficiently serving requests)
```
Setting up CloudWatch Alarms based on cache metrics can help you optimize TTL settings or modify the caching strategy to maximize performance.


Conclusion
Caching in Amazon API Gateway is a highly effective way to reduce latency, protect backend services from overuse, and optimize API performance. By using cache keys, TTL settings, and selective caching mechanisms, you can fine-tune your API's cache behavior to suit your specific needs. Monitoring cache performance through CloudWatch ensures that you get the most out of the caching functionality.