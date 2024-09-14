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

# Design a URL Shortener

We're tackling a classical system design problem - designing a URL shortening service like tinyurl.

## Step 1 - Understand the problem and establish design scope

- **C**: Can you give an example of how a URL shortening service works?
- **I**: Given URL `https://www.systeminterview.com/q=chatsystem&c=loggedin&v=v3&l=long` and alias `https://tinyurl.com/y7keocwj`. You open the alias and get to the original URL.
- **C**: What is the traffic volume?
- **I**: 100 million URLs are generated per day.
- **C**: How long is the shortened URL?
- **I**: As short as possible.
- **C**: What characters are allowed?
- **I**: Numbers and letters.
- **C**: Can shortened URLs be updated or deleted?
- **I**: For simplicity, let's assume they can't.

Other functional requirements: high availability, scalability, fault tolerance.

---

## Back of the envelope calculation

- 100 million URLs per day → ~1200 URLs per second.
- Assuming a read-to-write ratio of 10:1 → 12,000 reads per second.
- Assuming the URL shortener will run for 10 years, we need to support 365 billion records.
- Average URL length is 100 characters.
- Storage requirements for 10 years: 36.5 TB.

---

## Step 2 - Propose high-level design and get buy-in

### API Endpoints

We'll make a REST API.

A URL shortening service needs two endpoints:

- `POST api/v1/data/shorten` - Accepts a long URL and returns a short one.
- `GET api/v1/shortURL` - Returns the long URL for HTTP redirection.

### URL Redirecting

How it works:

![tinyurl-example](../images/tinyurl-example.png)

What's the difference between 301 and 302 statuses?

- **301 (Permanently moved)**: Indicates that the URL permanently points to the new URL. This instructs the browser to bypass the tinyurl service on subsequent calls.
- **302 (Temporarily moved)**: Indicates that the URL is temporarily moved to the new URL. The browser will not bypass the tinyurl service on future calls.

Choose **301** to avoid extra server load. Choose **302** if tracking analytics is important.

The easiest way to implement the URL redirection is to store the `<shortURL, longURL>` pair in an in-memory hash table.

---

### URL Shortening

To support URL shortening, we need to find a suitable hash function. It should support hashing long URLs to short URLs and mapping them back. Details are discussed in the deep dive section.

---

## Step 3 - Design Deep Dive

We'll explore the data model, hash function, URL shortening, and redirection.

### Data Model

In the simplified version, we're storing the URLs in a hash table. This is problematic as we'll run out of memory, and in-memory data doesn't persist across server reboots.

To handle this, we can use a simple relational table instead:

![url-table](../images/url-table.png)

---

### Hash Function

The hash value consists of characters `[0-9a-zA-Z]`, giving a max of 62 characters.

To figure out the smallest hash value we can use, we need to calculate **n** in `62^n >= 365 billion`. This results in `n=7`, which can support ~3.5 trillion URLs.

For the hash function itself, we can either use `base62 conversion` or `hash + collision detection`.

In the latter case, we can use something like MD-5 or SHA256, but only take the first 7 characters. To resolve collisions, we can rehash with some padding until no collision occurs:

![hash-collision-mechanism](../images/hash-collision-mechanism.png)

A problem with this method is that we need to query the database to detect collisions. **Bloom filters** could help here.

Alternatively, we can use **base62 conversion**, which converts an arbitrary ID into a string of 62 characters.

---

### Hashing Approach Comparison

| Hash + Collision Resolution                                           | Base 62 Conversion                                                                                           |
| --------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| Fixed short URL length.                                               | Short URL length increases with the ID.                                                                      |
| Does not require a unique ID generator.                               | Depends on a unique ID generator.                                                                            |
| Collision is possible and must be resolved.                           | Collision is not possible since the ID is unique.                                                            |
| Cannot determine the next available short URL since it's independent. | Can easily determine the next available short URL if the ID increments by 1. This can be a security concern. |

---

## URL Shortening Deep Dive

To keep our service simple, we'll use **base62 encoding** for URL shortening.

Here's the workflow:

![url-shortening-deep-dive](../images/url-shortening-deep-dive.png)

To ensure our ID generator works in a distributed environment, we can use **Twitter's snowflake algorithm**.

---

## URL Redirection Deep Dive

We've introduced a cache to improve read performance as there are more reads than writes:

![url-redirection-deep-dive](../images/url-redirection-deep-dive.png)

- User clicks the short URL.
- Load balancer forwards the request to one of the service instances.
- If the short URL is in cache, return the long URL directly.
- Otherwise, fetch the long URL from the database and store it in cache. If not found, the short URL doesn't exist.

---

## Step 4 - Wrap up

We discussed:

- API design
- Data model
- Hash function
- URL shortening
- URL redirecting

---

## Additional Talking Points

- **Rate Limiter**: Introduce a rate limiter to protect against malicious actors making too many URL shortening requests.
- **Web Server Scaling**: Easily scale the web tier by introducing more service instances as it is stateless.
- **Database Scaling**: Replication and sharding are common approaches to scaling the data layer.
- **Analytics**: Integrate analytics tracking in the URL shortener to provide insights for clients, such as "how many users clicked the link".
- **Availability, Consistency, Reliability**: At the core of distributed systems. We would leverage concepts already discussed. 
