+++
title= "Search Autocomplete"
tags = [ "system-design", "software-architecture", "interview", "search-autocomplete" ]
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
weight= 10
bookFlatSection= true
+++

# Design a Search Autocomplete System

Search autocomplete is a feature offered by many platforms, such as Amazon and Google. When you start typing in the search bar, it suggests possible completions based on your input:

![google-search](../images/google-search.png)

## Step 1 - Understand the Problem and Establish Design Scope

- **C:** Is the matching only supported at the beginning of a search term or, e.g., in the middle?
  - **I:** Only at the beginning.
- **C:** How many autocompletion suggestions should the system return?
  - **I:** 5.
- **C:** Which suggestions should the system choose?
  - **I:** Determined by popularity based on historical query frequency.
- **C:** Does the system support spell check?
  - **I:** Spell check or auto-correct is not supported.
- **C:** Are search queries in English?
  - **I:** Yes, if time allows, we can discuss multi-language support.
- **C:** Is capitalization and special characters supported?
  - **I:** We assume all queries use lowercase characters.
- **C:** How many users use the product?
  - **I:** 10 million DAU (Daily Active Users).

**Summary:**

- **Fast Response Time:** Suggestions should be returned with a delay of at most 100ms to avoid stuttering.
- **Relevant:** Autocomplete suggestions should be relevant to the search term.
- **Sorted:** Suggestions should be sorted by popularity.
- **Scalable:** System should handle high traffic volumes.
- **Highly Available:** System should be operational even if parts of it are unresponsive.

## Back of the Envelope Estimation

- Assume 10 million DAU.
- On average, each person performs 10 searches per day.
- Total searches per day: 10 million * 10 = 100 million.
- Searches per second: 100,000,000 / 86,400 = 1,200 QPS (Queries Per Second).
- Given an average search term length of 4 words of 5 characters each: 1,200 * 20 = 24,000 QPS. Peak QPS = 48,000 QPS.
- 20% of daily queries are new: 100 million * 0.2 = 20 million new searches * 20 bytes = 400 MB new data per day.

## Step 2 - Propose High-Level Design and Get Buy-In

The system has two main components:

1. **Data Gathering Service:** Gathers user input queries and aggregates them in real-time.
2. **Query Service:** Given a search query, returns the top 5 suggestions.

### Data Gathering Service

This service maintains a frequency table:

![frequency-table](../images/frequency-table.png)

### Query Service

This service returns the top 5 suggestions based on the frequency table:

![query-service-example](../images/query-service-example.png)

Querying the dataset might involve running the following SQL query:

![query-service-sql-query](../images/query-service-sql-query.png)

This method is practical for small datasets but becomes less efficient for larger ones.

## Step 3 - Design Deep Dive

We'll delve into several components to optimize the initial high-level design.

### Trie Data Structure

A trie is a suitable data structure for fast string prefix retrieval:

- It is a tree-like structure.
- The root represents the empty string.
- Each node has 26 children, representing possible next characters. Empty links are not stored to save space.
- Each node represents a single word or prefix.
- For this problem, we store frequencies at each leaf:

![trie-example-with-frequency](../images/trie-example-with-frequency.png)

**Algorithm:**

1. Find the node representing the prefix (time complexity: O(p), where p = length of prefix).
2. Traverse the subtree to find all leaves (time complexity: O(c), where c = total children).
3. Sort the retrieved children by their frequencies (time complexity: O(c log c), where c = total children).

**Optimizations:**

- **Limit the Max Length of Prefix:** Set a maximum prefix length (e.g., 50 characters) to reduce time complexity from `O(p) + O(c) + O(c log c)` to `O(1) + O(c) + O(c log c)`.

- **Cache Top Search Queries at Each Node:** Cache the top k most frequently accessed words in each node to reduce time complexity to `O(1)`:

![caching-top-search-results](../images/caching-top-search-results.png)

### Data Gathering Service

Previously, user searches updated data in real-time, which is impractical at a larger scale due to:

- Billions of queries per day.
- Top suggestions may not change much once the trie is built.

Instead, we update the trie asynchronously based on analytics data:

![data-gathering-service](../images/data-gathering-service.png)

Analytics logs contain raw search data with timestamps:

![analytics-log](../images/analytics-log.png)

Aggregators map analytics data into a suitable format and aggregate it into fewer records.

**Aggregation Cadence:**

- For real-time data (e.g., Twitter search), aggregate every 30 minutes.
- For less frequent updates (e.g., Google search), aggregate once per week.

Example weekly aggregated data:

![weekly-aggregated-data](../images/weekly-aggregated-data.png)

Workers build the trie from aggregated data and store it in a database.

**Trie Storage Options:**

- **Document Store (e.g., MongoDB):** Periodically build and store the trie.
- **Key-Value Store (e.g., DynamoDB):** Store the trie in a hashmap format:

![trie-as-hashmap](../images/trie-as-hashmap.png)

### Query Service

The query service fetches top suggestions from the Trie Cache or falls back to the Trie DB on cache miss:

![query-service-improved](../images/query-service-improved.png)

**Additional Optimizations:**

- **AJAX Requests:** Prevent page refreshes.
- **Data Sampling:** Log a sample of requests to reduce log volume.
- **Browser Caching:** Leverage browser cache to avoid unnecessary backend calls:

![google-browser-caching](../images/google-browser-caching.png)

### Trie Operations

**Create:** The trie is created by workers using aggregated data from analytics logs.

**Update:**

- **Reconstruct the Trie:** Rebuild the trie periodically if real-time updates are not needed.
- **Update Individual Nodes:** Avoid this due to slow performance; updating a node requires updating all parent nodes:

![update-trie](../images/update-trie.png)

**Delete:** To remove unwanted content (e.g., hateful content), add a filter between the trie cache and the API servers:

![filter-layer](../images/filter-layer.png)

The database is asynchronously updated to remove undesirable content.

### Scale the Storage

As the trie grows, it may not fit on a single server. Sharding is needed:

- **Sharding by Alphabet:** E.g., `a-m` on one shard, `n-z` on another. This approach may be uneven due to letter frequency differences.

- **Dedicated Shard Mapper:** Use a smart sharding algorithm to handle uneven data distribution:

![sharding](../images/sharding.png)

## Step 4 - Wrap Up

**Other Considerations:**

- **Multi-Language Support:** Store Unicode characters in trie nodes instead of ASCII.
- **Country-Specific Queries:** Build different tries per country and use CDNs to improve response time.
- **Trending Queries:** Support real-time queries by reducing the working data set via sharding, adjusting the ranking model, or using stream processing technologies (e.g., Hadoop, Apache Spark, Apache Storm, Apache Kafka).

