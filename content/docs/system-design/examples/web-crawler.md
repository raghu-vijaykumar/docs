+++
title= "Web Crawler"
tags = [ "system-design", "software-architecture", "interview", "web-crawler" ]
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
weight= 13
bookFlatSection= true
+++

# Web Crawler Design 

A Web Crawler is a system that fetches content from the web by visiting URLs, extracting data, and processing it. It is a fundamental component of search engines, news aggregators, and web scraping applications. This documentation covers key design principles and considerations for building a scalable web crawler.

## Key Components of a Web Crawler

### 1. **Input URL**
   - The crawler begins by accepting a URL, usually pointing to a main webpage or a list of seed URLs.
   - It then fetches the contents of this page to extract further URLs for continued crawling.

### 2. **Fetching Content**
   - The crawler fetches the contents of the web page, either storing the entire content or just extracting URLs.
   - Content can be stored for indexing or processed for specific data like metadata, links, etc.

### 3. **Processing Extracted URLs**
   - Once URLs are extracted, the crawler repeats the process by fetching these new URLs.
   - To avoid duplication, URLs need to be normalized and checked to determine if they've been crawled before.

## Crawler Design Considerations

### 1. **Scope of Crawling**
   - **Single Site Crawling:** In simpler cases, crawlers are designed to visit only the pages within a specific domain.
   - **Entire Web Crawling:** In more complex cases, crawlers are designed to traverse the entire web, continuously fetching new data.

### 2. **Seed URLs**
   - Seed URLs represent the starting points for crawling.
   - They can be predefined major websites or specific links provided by the user.

### 3. **Termination**
   - The crawler can either terminate after visiting all relevant URLs or run continuously, such as in the case of a search engine that must always be up-to-date.

### 4. **Storing Results**
   - Simple crawlers may store only URLs.
   - More complex crawlers may store full-page content for indexing or analysis.

## Core Design Elements

### 1. **Queue for URL Processing**
   - URLs are maintained in a queue where they are fetched one by one for content extraction.
   - Technologies like **Kafka** or **RabbitMQ** can be used as a robust external queue system.

### 2. **Content Fetching Service**
   - A service (e.g., Content Fetcher) is responsible for obtaining page content from URLs.
   - The service handles domain name resolution and integrates with DNS for quicker lookups.
   - **Headless Browsers** like Chrome without a user interface can be used to fetch content for JavaScript-heavy websites.

### 3. **Handling Duplicate URLs**
   - Normalizing URLs is necessary to avoid fetching the same URL multiple times.
   - Components like **Bloom Filters** or **Redis** can be used to check if a URL has been processed.
     - **Bloom Filters:** Memory-efficient but may have false positives.
     - **Redis:** Key-value store to manage large datasets effectively. URLs can be stored as keys with optional metadata (e.g., last crawled date).

### 4. **Page Change Detection**
   - Content at the same URL may change over time.
   - Crawlers should track the rate of change for each page or domain, adjusting the frequency of crawls accordingly.

# How to Distribute Crawl Jobs to Reduce Latency

Distributing crawl jobs to reduce latency involves efficient allocation of tasks across multiple workers (crawlers) to maximize parallel processing, reduce network delays, and ensure resource optimization. Here's a breakdown of strategies to achieve this:

## 1. Job Queue with Distributed Workers
- **Use a distributed message queue** (e.g., Kafka, RabbitMQ, Amazon SQS) to manage URLs.
- Multiple workers subscribe to the queue, each picking the next available URL.
  
**Advantages:**
- Scalability: Add more workers for higher throughput.
- Parallel Processing: Multiple domains/URLs are processed concurrently.

## 2. Sharding the URL Space
- **Divide the URL space into shards** based on domain name, URL hash, or geography.
- Each shard is assigned to a specific worker or group of workers.

**Example:**
- For 10 million URLs with 100 workers, assign 100,000 URLs per worker.

**Advantages:**
- Prevents duplication.
- Workload is evenly distributed.

## 3. Geographical Distribution
- **Deploy crawlers in geographically distributed regions** (using AWS, GCP, Azure).
- Assign URLs based on geographic proximity to reduce network latency.

**Advantages:**
- Reduced latency due to proximity.
- Optimized bandwidth and faster response times.

## 4. Task Prioritization and Dynamic Scheduling
- **Assign priorities to URLs** based on importance or likelihood of changes.
- Implement **dynamic scheduling** for high-priority or frequently updated URLs.

**Advantages:**
- Focus on high-priority content.
- Reduces overall latency for critical URLs.

## 5. Politeness and Rate-Limiting Distribution
- **Enforce politeness policies** (delay between requests) to avoid overloading a domain.
- Distribute requests across workers to balance the load.

**Advantages:**
- Prevents workers from being rate-limited or blocked.
- Efficiently distributes the load across multiple domains.

## 6. Caching DNS Lookups
- **Cache DNS resolutions** to reduce repeated DNS lookups.
  
**Advantages:**
- Faster resolution times for frequently crawled domains.
- Reduces latency due to DNS lookups.

## 7. Headless Browsers for JavaScript-Heavy Pages
- Use **headless browsers** (e.g., Puppeteer, Selenium) for JavaScript-heavy pages.
- Assign simple HTML pages to lighter workers, saving resources.

**Advantages:**
- Optimizes resource allocation based on page complexity.
- Reduces bottlenecks for JavaScript-heavy pages.

## 8. Load Balancing Across Workers
- Use a **load balancer** (e.g., HAProxy, NGINX) to distribute jobs based on worker load.
  
**Advantages:**
- Prevents overloading any single worker.
- Ensures optimal distribution across workers.

## 9. Adaptive Timeouts and Retries
- Set **adaptive timeouts** for different page complexities.
- Implement **retry mechanisms** with exponential backoff for failed requests.

**Advantages:**
- Reduces latency for simple pages.
- Allows more time for complex pages without overwhelming the site.

## 10. Monitoring and Autoscaling
- Use **monitoring systems** (e.g., Prometheus, Grafana) to track performance.
- Implement **autoscaling** to dynamically add/remove workers based on demand.

**Advantages:**
- Minimizes latency during high demand periods.
- Scales resources as needed to maintain performance.

## Workflow Summary:
1. **URL Submission**: URLs are added to a distributed message queue (e.g., Kafka).
2. **Worker Pool**: Workers pick URLs from the queue and fetch the content.
3. **Job Assignment**: Jobs are assigned based on sharding, priority, or geography.
4. **Caching & Politeness**: Workers enforce DNS caching and politeness policies.
5. **Dynamic Scaling**: Autoscaling ensures more workers are added when needed.
6. **Content Processing**: Workers process content and store or index the results.

## Tools & Technologies:
- **Distributed Queue**: Kafka, RabbitMQ, AWS SQS
- **Headless Browsers**: Puppeteer, Selenium
- **Load Balancing**: NGINX, HAProxy, Cloud Load Balancers
- **Data Stores**: Bloom Filter (for deduplication), Redis (for deduplication), Sharded Databases (for scalability)
- **Monitoring**: Prometheus, Grafana
- **Autoscaling**: Kubernetes Horizontal Pod Autoscaler, AWS/GCP autoscaling

This approach ensures efficient distribution of crawl jobs, minimizes latency, and scales seamlessly under load.

## Detect and Avoid Problematic Content

Common problematic content types:
- **Redundant Content**: Use hashes/checksums to avoid processing duplicate pages.
- **Spider Traps**: Avoid infinite loops by specifying max URL lengths and manually blacklisting problematic sites.
- **Data Noise**: Filter out low-value content such as ads, spam, etc.

## Conclusion
Designing a web crawler requires careful consideration of scalability, efficiency, and resource utilization. It involves balancing memory consumption, ensuring politeness to target websites, and effectively handling duplicate URLs. While different data structures and systems (Bloom Filter, Redis, RDBMS) offer varying trade-offs, the solution chosen must align with the needs of the crawler in terms of throughput, consistency, and availability.
