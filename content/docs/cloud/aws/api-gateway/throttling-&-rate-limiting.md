+++
title= "Throttling & Rate Limiting"
tags = [ "api-gateway", "aws", "cloud", "throttling-&-rate-limiting" ]
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
weight= 4
bookFlatSection= true
+++


# Throttling and Rate Limiting in Amazon API Gateway

**Throttling** and **rate limiting** are essential mechanisms provided by **Amazon API Gateway** to control traffic and protect APIs from abuse, such as Denial of Service (DoS) attacks, and to ensure fair usage across clients.

These features allow you to limit the number of API requests from clients, ensuring backend services remain reliable, scalable, and cost-efficient under load.

## Key Concepts

### Throttling
**Throttling** controls how many API requests can be processed over a short period. This can be configured globally or at the API method level. Throttling is designed to:
- **Prevent API Overuse**: Stops clients from overwhelming your API with too many requests.
- **Protect Backend Services**: Shields your backend from excessive traffic and potential performance degradation.
- **Control API Costs**: Prevents over-consumption, which could lead to unexpectedly high bills.

### Rate Limiting
**Rate limiting** involves setting a maximum number of requests that a client can make to your API over a longer period (e.g., per second, minute, or day). It helps manage client access to ensure fairness.

## Throttling Mechanism in API Gateway

Amazon API Gateway provides throttling limits at two levels:
- **Global (Account-level) Limits**: AWS sets default account-level limits that apply across all APIs in your account. This can be increased by submitting a request to AWS Support.
- **Usage Plan Limits (Client-level)**: API Gateway allows you to define usage plans for specific API keys, enabling you to enforce throttling and quotas on a per-client basis.

### Throttling Metrics

- **Rate (requests per second)**: Defines the steady-state rate of requests the API can handle.
- **Burst (number of requests in a short period)**: Allows for brief spikes in request traffic beyond the rate limit.

## Throttling with Usage Plans

A **usage plan** enables API Gateway to associate specific API clients (identified via **API keys**) with rate limits and quotas. You can configure throttling per **client** based on their usage plan.

### Example of Usage Plan with Throttling

#### Scenario:
You have a **free-tier** and a **premium-tier** client. You want to limit the free-tier client to:
- **100 requests per second** (rate limit).
- **1000 burst limit** (maximum spike requests).

For the premium-tier client, you allow:
- **500 requests per second** (rate limit).
- **5000 burst limit** (maximum spike requests).

#### Step-by-Step Setup

1. **Create API Gateway**:
   - Create an API with methods (e.g., GET `/items`) in API Gateway.
   
2. **Create API Keys**:
   - Generate separate API keys for **free-tier** and **premium-tier** clients.

3. **Define Usage Plans**:
   - In the API Gateway console, create two usage plans, one for **free-tier** and one for **premium-tier**.
   
4. **Set Throttling Limits**:
   - For the **free-tier** usage plan:
     - Set rate limit to 100 requests per second.
     - Set burst limit to 1000.
   - For the **premium-tier** usage plan:
     - Set rate limit to 500 requests per second.
     - Set burst limit to 5000.
     
5. **Associate API Keys**:
   - Attach the **free-tier** API key to the free-tier usage plan.
   - Attach the **premium-tier** API key to the premium-tier usage plan.

## Rate Limiting with Quotas

In addition to throttling, **quotas** define the maximum number of requests a client can make over a period (e.g., per day, per week, or per month). 

### Example of Quota

#### Scenario:
For the **free-tier** clients, you want to set a daily quota of **10,000 requests**.

1. **Modify Usage Plan**:
   - In the usage plan for free-tier clients, set the quota to 10,000 requests per day.

2. **Monitor Quota**:
   - API Gateway automatically tracks the number of requests made by each API key and enforces the quota.

Once the client exceeds their quota, API Gateway returns a **429 Too Many Requests** error until the quota resets.

## Global Throttling Example

Global throttling is applied when no specific usage plan is defined, or clients don't provide API keys. API Gateway defines default limits, such as:
- **10,000 requests per second** at the account level.
- **5,000 burst limit** for short spikes.

### Example:

If you create an API without associating it with a usage plan or API key, the API will inherit the default account-level throttling settings.

#### Steps:
1. **Deploy an API** without a usage plan.
2. **Test Request Volume**:
   - If you exceed the global limit, API Gateway responds with **429 Too Many Requests**.

## Custom Throttling Per Method

You can apply throttling limits at the **method level**, controlling how many requests specific methods (e.g., GET, POST) can handle independently.

### Example:

You want to limit `POST /items` to **50 requests per second**, but `GET /items` can handle **100 requests per second**.

1. **Configure Method Throttling**:
   - In the method settings of API Gateway, for `POST /items`, set the **throttling burst** to 500 and **rate** to 50 requests per second.
   - For `GET /items`, set the **burst** to 1000 and **rate** to 100 requests per second.
   
2. **Deploy API** and test.

If a user sends more than 50 `POST` requests per second, they will receive a **429 Too Many Requests** response for that method, while `GET` requests can continue at the higher limit.

## Error Responses for Throttling and Rate Limiting

When clients exceed the defined throttling or quota limits, API Gateway returns an HTTP status code **429 Too Many Requests** along with a customizable message.

### Example Response:
```json
{
  "message": "Rate limit exceeded. Try again in a few seconds."
}
```
The response headers may also include information about how long the client should wait before retrying.

- **Retry-After**: This header can tell the client how long to wait before making another request.

## Best Practices for Throttling and Rate Limiting

1. **Use Throttling to Protect Backend**
   - Define appropriate throttling limits to protect your backend services from traffic spikes and overuse. Set burst limits for short-term spikes and rate limits for sustained traffic.

2. **Create Usage Plans for Different Client Tiers**
   - Assign usage plans with throttling and quotas to distinguish between free, premium, or internal clients. This allows you to allocate resources fairly and ensure premium clients receive priority access.

3. **Monitor Throttling Metrics**
   - Use Amazon CloudWatch to monitor throttling and rate-limiting metrics. Set up alarms for excessive 429 Too Many Requests responses, which might indicate misconfigured limits or an unusual spike in traffic.

4. **Handle 429 Too Many Requests Gracefully**
   - Ensure your clients handle the 429 Too Many Requests error appropriately by implementing retry logic with exponential backoff, rather than hammering the API.

## Conclusion
Amazon API Gateway’s throttling and rate limiting features provide robust control over API traffic. By configuring appropriate limits, usage plans, and quotas, you can protect your backend services, ensure fair usage across clients, and prevent abuse. These tools are essential for scaling APIs and maintaining performance under heavy traffic.