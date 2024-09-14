# Design a Web Crawler

In this document, we'll focus on designing a web crawler, a classical system design problem. Web crawlers (or robots) are used to discover new or updated content on the web, such as articles, videos, PDFs, etc.

![web-crawler-example](../images/web-crawler-example.png)

## Use-Cases

- **Search Engine Indexing**: For creating a local index of a search engine, e.g., Google's Googlebot.
- **Web Archiving**: Collect data from the web and preserve it for future use.
- **Web Mining**: Used for data mining, e.g., finding important insights such as shareholder meetings for trading firms.
- **Web Monitoring**: Monitor the internet for copyright infringements or internal information leaks.

The complexity of building a web crawler varies based on the target scale, from a simple student project to a multi-year project maintained by a dedicated team.

## Step 1 - Understand the Problem and Establish Design Scope

### High-Level Overview

A web crawler works as follows:
1. Given a set of URLs, download all the pages these URLs point to.
2. Extract URLs from the web pages.
3. Add the new URLs to the list of URLs to be traversed.

### Key Questions

1. **C**: What's the main purpose of the web crawler? Search engine indexing, data mining, or something else?  
   **I**: Search engine indexing.

2. **C**: How many web pages does it collect per month?  
   **I**: 1 billion.

3. **C**: What content types are included? HTML, PDF, images?  
   **I**: HTML only.

4. **C**: Should we consider newly added/edited content?  
   **I**: Yes.

5. **C**: Do we need to persist the crawled web pages?  
   **I**: Yes, for 5 years.

6. **C**: What do we do with pages with duplicate content?  
   **I**: Ignore them.

Other characteristics of a good web crawler:
- **Scalable**: It should be extremely efficient.
- **Robust**: Handle edge cases such as bad HTML, infinite loops, server crashes, etc.
- **Polite**: Avoid making too many requests to a server within a short time interval.
- **Extensible**: It should be easy to add support for new types of content, e.g., images in the future.

### Back of the Envelope Estimation

- **Given**: 1 billion pages per month → ~400 pages per second.
- **Peak QPS**: 800 pages per second.
- **Given**: Average web page size is 500KB → 500 TB per month → 30 PB for 5 years.

## Step 2 - Propose High-Level Design and Get Buy-In

![high-level-design](../images/high-level-design.png)

### Components

- **Seed URLs**: Starting points for crawlers. It's important to select seed URLs well to traverse the web appropriately.
- **URL Frontier**: Stores the URLs to be downloaded in a FIFO queue.
- **HTML Downloader**: Downloads HTML pages from URLs in the frontier.
- **DNS Resolver**: Resolves the IP for a given URL's domain.
- **Content Parser**: Validates that the web page is OK. Malformed pages are discarded.
- **Content Seen?**: Eliminates pages that have already been processed. Compares content using web page hashes.
- **Content Storage**: Storage system for HTML documents. Most content is stored on disk with popular ones in memory for fast retrieval.
- **URL Extractor**: Extracts links from an HTML document.
- **URL Filter**: Excludes invalid URLs, unsupported content types, blacklisted URLs, etc.
- **URL Seen?**: Keeps track of visited URLs to avoid traversing them again. Bloom filters are used for efficient implementation.
- **URL Storage**: Stores already visited URLs.

### Workflow

![web-crawler-workflow](../images/web-crawler-workflow.png)

1. Add Seed URLs to URL Frontier.
2. HTML Downloader fetches a list of URLs from the frontier.
3. Match URLs to IP addresses via the DNS resolver.
4. Parse HTML pages and discard if malformed.
5. Once validated, content is passed to "Content Seen?".
6. Check if the HTML page is already in storage. If yes - discard. If no - process.
7. Extract links from the HTML page.
8. Pass extracted links to URL Filter.
9. Pass filtered links to "URL Seen?" component.
10. If the URL is in storage - discard. Otherwise - process.
11. If the URL has not been processed before, it is added to URL Frontier.

## Step 3 - Design Deep Dive

Let's explore some of the most important mechanisms in the web crawler:

- **DFS vs. BFS**
- **URL Frontier**
- **HTML Downloader**
- **Robustness**
- **Extensibility**
- **Detect and Avoid Problematic Content**

### DFS vs. BFS

The web is a directed graph, where the links in a web page are the edges to other pages (the nodes). Two common approaches for traversing this data structure are:

- **DFS (Depth-First Search)**: Not ideal due to potential deep traversal depth.
- **BFS (Breadth-First Search)**: Typically preferable as it uses a FIFO queue to traverse URLs in order of encountering them. 

However, traditional BFS has issues with:
- Backlinks to the same domain causing excessive requests.
- Lack of URL priority consideration.

### URL Frontier

The URL Frontier helps address problems related to politeness and prioritization.

#### Politeness

A web crawler should avoid sending too many requests to the same host in a short time frame to prevent excessive traffic.

- **Queue Router**: Ensures each queue contains URLs from the same host.
- **Mapping Table**: Maps each host to a queue.
- **FIFO Queues**: Maintain URLs belonging to the same host.
- **Queue Selector**: Each worker thread processes URLs from its assigned FIFO queue with a delay between tasks.

#### Priority

URLs are prioritized based on usefulness, which can be determined by PageRank, web traffic, update frequency, etc.

- **Prioritizer**: Calculates priority for URLs.
- **Queue Selector**: Randomly chooses queues with a bias towards high-priority ones.

#### Freshness

Web pages are updated constantly. We need to periodically recrawl updated content based on web page update history or priority.

#### Storage for URL Frontier

Due to the large number of URLs, a hybrid approach is used:
- Most URLs are stored on disk.
- An in-memory buffer maintains URLs currently being processed, periodically flushed to disk.

### HTML Downloader

The HTML Downloader component retrieves HTML pages using HTTP. 

- **Robots Exclusion Protocol**: Use `robots.txt` files to communicate which web pages are OK to crawl and which should be skipped.

Example `robots.txt` file:

```
User-agent: Googlebot 
Disallow: /creatorhub/* 
Disallow: /rss/people//reviews 
Disallow: /gp/pdp/rss//reviews 
Disallow: /gp/cdp/member-reviews/ 
Disallow: /gp/aw/cr/
```

**Performance Optimization**:
- **Distributed Crawl**: Parallelize crawl jobs across multiple machines and threads.
- **Cache DNS Resolver**: Maintain a DNS cache to reduce resolver requests.
- **Locality**: Distribute crawl jobs geographically to lower latency.
- **Short Timeout**: Add timeouts for unresponsive servers to prevent long waits.

### Robustness

Ensure robustness through:
- **Consistent Hashing**: For easy rescaling of workers and crawlers.
- **Save Crawl State and Data**: Store intermediary results to recover from server crashes.
- **Exception Handling**: Handle exceptions gracefully without crashing.
- **Data Validation**: Prevent system errors.

### Extensibility

Ensure the crawler is extendable to support new content types in the future:

![extendable-crawler](../images/extendable-crawler.png)

Example extensions:
- **PNG Downloader**: To crawl PNG images.
- **Web Monitor**: To monitor for copyright infringements.

### Detect and Avoid Problematic Content

Common problematic content types:
- **Redundant Content**: Use hashes/checksums to avoid processing duplicate pages.
- **Spider Traps**: Avoid infinite loops by specifying max URL lengths and manually blacklisting problematic sites.
- **Data Noise**: Filter out low-value content such as ads, spam, etc.

## Step 4 - Wrap Up

Characteristics of a good crawler: scalable, polite, extensible, and robust.

Additional talking points:
- **Server-Side Rendering**: For dynamically generated HTML.
- **Filter Out Unwanted Pages**: Anti-spam components for filtering low-quality pages.
- **Database Replication and Sharding**: Improve data layer availability, scalability, reliability.
- **Horizontal Scaling**: Keep servers stateless to enable scaling.
- **Availability, Consistency, Reliability**: Core concepts for system success.
- **Analytics**: Collect and analyze data to fine-tune the system.
