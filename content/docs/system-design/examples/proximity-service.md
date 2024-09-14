+++
title= "Proximity Service"
tags = [ "system-design", "software-architecture", "interview", "proximity-service" ]
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
weight= 14
bookFlatSection= true
+++

# Proximity Service

A proximity service enables you to discover nearby places such as restaurants, hotels, theatres, etc.

## Step 1 - Understand the Problem and Establish Design Scope

### Sample Questions to Understand the Problem Better
- **C:** Can a user specify a search radius? What if there are not enough businesses within the search area?
- **I:** We only care about businesses within a certain area. If time permits, we can discuss enhancing the functionality.
- **C:** What's the max radius allowed? Can I assume it's 20km?
- **I:** Yes, that is a reasonable assumption.
- **C:** Can a user change the search radius via the UI?
- **I:** Yes, let's say we have the options - 0.5km, 1km, 2km, 5km, 20km.
- **C:** How is business information modified? Do we need to reflect changes in real-time?
- **I:** Business owners can add/delete/update a business. Assume changes are going to be propagated on the next day.
- **C:** How do we handle search results while the user is moving?
- **I:** Let's assume we don't need to constantly update the page since users are moving slowly.

### Functional Requirements
- Return all businesses based on the user's location.
- Business owners can add/delete/update a business. Information is not reflected in real-time.
- Customers can view detailed information about a business.

### Non-Functional Requirements
- **Low latency:** Users should be able to see nearby businesses quickly.
- **Data privacy:** Location info is sensitive data and we should take this into consideration in order to comply with regulations.
- **High availability and scalability requirements:** We should ensure the system can handle a spike in traffic during peak hours in densely populated areas.

### Back-of-the-Envelope Calculation
- Assuming 100 million daily active users and 200 million businesses.
- Search QPS = 100 million * 5 (average searches per day) / 10^5 (seconds in day) = 5000.

## Step 2 - Propose High-Level Design and Get Buy-In

### API Design
We'll use a RESTful API convention to design a simplified version of the APIs.

**GET /v1/search/nearby**

This endpoint returns businesses based on search criteria, paginated.

**Request parameters:** latitude, longitude, radius

**Example response:**
{
  "total": 10,
  "businesses": [{business object}]
}

The endpoint returns everything required to render a search results page, but a user might require additional details about a particular business, fetched via other endpoints.

Here's some other business APIs we'll need:
- `GET /v1/businesses/{:id}` - Return business detailed info.
- `POST /v1/businesses` - Create a new business.
- `PUT /v1/businesses/{:id}` - Update business details.
- `DELETE /v1/businesses/{:id}` - Delete a business.

### Data Model
In this problem, the read volume is high because these features are commonly used:
- Search for nearby businesses.
- View the detailed information of a business.

On the other hand, write volume is low because we rarely change business information. Hence for a read-heavy workflow, a relational database such as MySQL is ideal.

In terms of schema, we'll need one main `business` table which holds information about a business:
![business-table](../images/business-table.png)

We'll also need a geo-index table so that we efficiently process spatial operations. This table will be discussed later when we introduce the concept of geohashes.

### High-Level Design
Here's a high-level overview of the system:
![high-level-design](../images/high-level-deisgn.png)

- The load balancer automatically distributes incoming traffic across multiple services. A company typically provides a single DNS entry point and internally routes API calls to appropriate services based on URL paths.
- Location-based service (LBS) - Read-heavy, stateless service, responsible for serving read requests for nearby businesses.
- Business service - Supports CRUD operations on businesses.
- Database cluster - Stores business information and replicates it in order to scale reads. This leads to some inconsistency for LBS to read business information, which is not an issue for our use-case.
- Scalability of business service and LBS - Since both services are stateless, we can easily scale them horizontally.

### Algorithms to Fetch Nearby Businesses
In real life, one might use a geospatial database, such as Geohash in Redis or Postgres with PostGIS extension.

Let's explore how these databases work and what other alternative algorithms there are for this type of problem.

#### Two-Dimensional Search
The most intuitive and naive approach to solving this problem is to draw a circle around the person and fetch all businesses within the circle's radius:
![2d-search](../images/2d-search.png)

This can easily be translated to a SQL query:
SELECT business_id, latitude, longitude
FROM business
WHERE (latitude BETWEEN {:my_lat} - radius AND {:my_lat} + radius) AND
      (longitude BETWEEN {:my_long} - radius AND {:my_long} + radius)

This query is not efficient because we need to query the whole table. An alternative is to build an index on the longitude and latitude columns but that won't improve performance by much.

This is because we still need to subsequently filter a lot of data regardless of whether we index by long or lat:
![2d-query-problem](../images/2d-query-problem.png)

We can, however, build 2D indexes and there are different approaches to that:
![2d-index-options](../images/2d-index-options.png)

We'll discuss the ones highlighted in purple - geohash, quadtree, and google S2 are the most popular approaches.

#### Evenly Divided Grid
Another option is to divide the world into small grids:
![evenly-divided-grid](../images/evenly-divided-grid.png)

The major flaw with this approach is that business distribution is uneven as there are a lot of businesses concentrated in New York and close to zero in the Sahara Desert.

#### Geohash
Geohash works similarly to the previous approach, but it recursively divides the world into smaller and smaller grids, where each two bits correspond to a single quadrant:
![geohash-example](../images/geohash-example.png)

Geohashes are typically represented in base32. Here's the example geohash of Google headquarters:
1001 10110 01001 10000 11011 11010 (base32 in binary) → 9q9hvu (base32)

It supports 12 levels of precision, but we only need up to 6 levels for our use-case:
![geohash-precision](../images/geohash-precision.png)

Geohashes enable us to quickly locate neighboring regions based on a substring of the geohash:
![geohash-substring](../images/geohash-substrint.png)

However, one issue with geohashes is that there can be places which are very close to each other but don't share any prefix, because they're on different sides of the equator or meridian:
![boundary-issue-geohash](../images/boundary-issue-geohash.png)

Another issue is that two businesses can be very close but not share a common prefix because they're in different quadrants:
![geohash-boundary-issue-2](../images/geohash-boundary-issue-2.png)

This can be mitigated by fetching neighboring geohashes, not just the geohash of the user.

A benefit of using geohashes is that we can use them to easily implement the bonus problem of increasing search radius in case insufficient businesses are fetched via query:
![geohash-expansion](../images/geohash-expansion.png)

This can be done by removing the last letter of the target geohash to increase radius.

#### Quadtree
A quadtree is a data structure, which recursively subdivides quadrants as deep as it needs to, based on business needs:
![quadtree-example](../images/quadtree-example.png)

This is an in-memory solution which can't easily be implemented in a database.

Here's how it might look conceptually:
![quadtree-concept](../images/quadtree-concept.png)

**Example pseudocode to build a quadtree:**
public void buildQuadtree(TreeNode node) {
    if (countNumberOfBusinessesInCurrentGrid(node) > 100) {
        node.subdivide();
        for (TreeNode child : node.getChildren()) {
            buildQuadtree(child);
        }
    }
}

In a leaf node, we store:
- Top-left, bottom-right coordinates to identify the quadrant dimensions.
- List of business IDs in the grid.

In an internal node, we store:
- Top-left, bottom-right coordinates of quadrant dimensions.
- 4 pointers to children.

The total memory to represent the quadtree is calculated as ~1.7GB in the book if we assume that we operate with 200 million businesses.

Hence, a quadtree can be stored in a single server, in-memory, although we can of course replicate it for redundancy and load balancing purposes.

One consideration to take into account if this approach is adopted - startup time of the server can be a couple of minutes while the quadtree is being built.

Hence, this should be taken into account during the deployment process. For example, a health check endpoint can be exposed and queried to signal when the quadtree build is finished.

Another consideration is how to update the quadtree. Given our requirements, a good option would be to update it every night using a nightly job due to our commitment of reflecting changes at the start of the next day.

It is nevertheless possible to update the quadtree on the fly, but that would complicate the implementation significantly.

**Example quadtree search:**
In order to search for businesses in a radius of 5km from the user:
- Traverse to the root node, then recursively traverse the children.
- Only fetch children nodes that overlap with the search radius.

#### Google S2
The S2 geometry library, developed by Google, is a library for spatial indexing and querying.

It works similarly to Geohash but in spherical coordinates:
![google-s2](../images/google-s2.png)

The library handles issues related to boundaries and non-rectangular regions, ensuring even results.

**Here are a few advantages of S2:**
- Handles spherical geometry, boundaries and overlaps better.
- Flexible and reliable.

**Some disadvantages:**
- Less popular, fewer tools and libraries support it.
- More complex implementation.

### Tradeoffs

| Algorithm     | Pros                                         | Cons                                                       |
| ------------- | -------------------------------------------- | ---------------------------------------------------------- |
| **Geohash**   | Easy to use, widely supported                | Boundary issues, doesn't handle spherical coordinates well |
| **Quadtree**  | Efficient search, handles boundaries well    | Memory usage, complex updates                              |
| **Google S2** | Handles spherical coordinates well, accurate | Complex implementation, less popular                       |

## Step 3 - Discuss Tradeoffs and Considerations

### Alternative Approaches
- **Spatial Indexing:** PostGIS, MongoDB, Elasticsearch, Redis Geo.
- **Real-time Updates:** Consider using WebSockets or Pub/Sub patterns to push updates to clients.

### Scalability
- **Horizontal Scaling:** Distribute load across multiple servers for the location-based service and business service.
- **Database Scaling:** Use read replicas and sharding strategies to handle high read volumes.

### Data Consistency
- **Eventual Consistency:** Accept some degree of eventual consistency for business updates.

## Step 4 - Consider Testing and Monitoring

### Testing
- **Unit Testing:** Test individual components for correctness.
- **Integration Testing:** Ensure that different components work together as expected.
- **Load Testing:** Simulate high traffic scenarios to test the system’s performance and scalability.

### Monitoring
- **Metrics:** Monitor request latency, error rates, and system resource usage.
- **Alerts:** Set up alerts for critical issues such as high error rates or system downtimes.

## Summary

Designing a proximity service involves understanding the problem, proposing a high-level design, evaluating algorithms, and considering tradeoffs. Choosing the right algorithm depends on factors such as data distribution, system requirements, and complexity. Testing and monitoring are crucial to ensure the system performs well under various conditions.
