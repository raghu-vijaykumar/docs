+++
title= "HTTP"
tags = [ "system-design", "software-architecture", "distributed-systems", "http" ]
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

# HTTP Protocol Fundamentals for Distributed Systems

## Overview

The HTTP (Hypertext Transfer Protocol) is a widely used protocol for communication between computers in distributed systems. It facilitates easy interaction and message exchange between nodes by operating at the application layer. This document provides an overview of HTTP request and response structures, methods, headers, and status codes.

## HTTP Request Structure

An HTTP request sent from a client to a server consists of the following five parts:

1. **Method**:
   - Specifies the action the server should perform.
   - Common methods include:
     - **GET**: Retrieves data from the server without side effects. It is safe and idempotent.
     - **POST**: Sends data to the server, which may result in side effects or complex computations.
  

2. **Relative Path**:
   - Part of the URL after the host and port, used for routing requests to the appropriate handler.
   - May include a query string with additional parameters.

3. **HTTP Protocol Version**:
   - Specifies the version of HTTP being used (e.g., HTTP/1.1 or HTTP/2).
   - Affects how connections and data are managed.

4. **Headers**:
   - Key-value pairs providing additional information about the request.
   - Examples include `Content-Type`, `Content-Encoding`, and custom headers for debugging or testing.
     - `Content-Type`: Specifies the MIME type of the body.
     - `Content-Encoding`: Specifies the encoding used on the body.
     - `Content-Length`: Specifies the length of the body in bytes.
     - `Accept`: Specifies the MIME types the client can understand.
     

5. **Message Body**:
   - Contains the data sent with the request, if applicable.
   - Can include complex data objects.

### HTTP Methods

- **GET**: Used for data retrieval. Safe and idempotent, with no message body.
- **POST**: Used for submitting data to the server, which can have side effects and include a message body.
- **PUT**: Updates or creates a resource on the server.
- **DELETE**: Removes a resource from the server.
- **PATCH**: Updates a resource on the server.
- **OPTIONS**: Describes the communication options for the target resource.
- **CONNECT**: Establishes a tunnel to the server identified by the target resource.
- **TRACE**: Performs a message loop-back test along the path to the target resource.
- **HEAD**: Retrieves the headers of a resource without the body.

### HTTP Versions

- **HTTP/1.1**:
  - Establishes a new TCP connection for each request-response pair.
  - Has limitations on the number of concurrent connections, leading to potential bottlenecks.
  - Requests and responses are sent sequentially over these connections, which can result in head-of-line blocking issues.
  - Requires techniques like domain sharding and inlining to improve performance due to limitations in parallelism.
  - Does not support header compression, leading to higher overhead in data transmission.
  - Lacks server push functionality, where the server can proactively send resources to the client before they are requested.

- **HTTP/2**:
  - Allows multiple requests and responses to be multiplexed over a single connection.
  - Supports header compression, reducing overhead and improving data transmission efficiency.
  - Solves the head-of-line blocking problem by enabling concurrent streams within a single connection.
  - Introduces server push, enabling servers to push resources to clients without waiting for a request.
  - Prioritizes requests, allowing more critical resources to be delivered first.
  - Enhances security by requiring the use of TLS encryption.
  - Overall, HTTP/2 offers significant performance improvements over HTTP/1.1 by optimizing resource utilization, reducing latency, and enhancing the overall browsing experience for users.

## HTTP Response Structure

An HTTP response consists of:

1. **Status Code and Message**:
   - Indicates the outcome of the request.
   - Status codes are grouped into categories:
     - **200 OK**: Success.
     - **400 Bad Request**: Client error.
     - **500 Internal Server Error**: Server error.

2. **Headers**:
   - Provide metadata about the response, similar to request headers.

3. **Message Body**:
   - Contains the data sent back to the client, if applicable.

## HTTP Server Implementation in Java

This documentation covers the implementation of a basic HTTP server using Java's standard libraries. The server can handle GET and POST requests and has two endpoints:
- `/status`: Provides health check information about the server.
- `/task`: Accepts a POST request with a list of numbers and returns the product of those numbers.

The server also supports custom headers (`X-Test` and `X-Debug`) that control server behavior for testing and debugging purposes.

### Key Components
- **HTTP Server**: Built using `HttpServer` class from the `com.sun.net.httpserver` package.
- **Endpoints**: `/status` for health checks and `/task` for processing a list of integers.
- **Custom Headers**: `X-Test` and `X-Debug` headers are used for controlling test mode and debugging mode, respectively.

### Project Structure
- **Main Class**: `WebServer` initializes and starts the HTTP server.
- **Methods**:
  - `startServer()`: Sets up the server and defines the endpoint handlers.
  - `handleStatusCheckRequest(HttpExchange exchange)`: Handles the `/status` endpoint by returning a health check response.
  - `handleTaskRequest(HttpExchange exchange)`: Handles the `/task` endpoint, processes the incoming data, and returns the result of the computation.
  - `calculateResponse(byte[] requestBytes)`: Processes the task request by multiplying the numbers and returns the result.
  - `sendResponse(byte[] responseBytes, HttpExchange exchange)`: Sends the HTTP response back to the client.

### HTTP Endpoints

#### `/status`
- **Method**: GET
- **Description**: Provides the health status of the server.
- **Response**: `"Server is alive\n"` with an HTTP status code of 200 (OK).

#### `/task`
- **Method**: POST
- **Description**: Accepts a list of numbers in the request body, multiplies them, and returns the result.
- **Request Body**: Comma-separated string of numbers (e.g., `"2,3,4"`).
- **Response**: Returns the result of the multiplication (e.g., `"Result of the multiplication is 24\n"`).

### Custom Headers
- **X-Test Header**: 
  - When set to `true`, the server returns a dummy response instead of performing the actual calculation.
  - Example Response: `"123\n"`.
  
- **X-Debug Header**: 
  - When set to `true`, the server calculates the task and returns additional timing information in the response headers.
  - Example Response Header: `"X-Debug-Info: Operation took 12345678 ns"`.

### Key Code Components

#### Server Initialization

```java
public void startServer() {
    try {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (IOException e) {
        e.printStackTrace();
        return;
    }

    HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
    HttpContext taskContext = server.createContext(TASK_ENDPOINT);

    statusContext.setHandler(this::handleStatusCheckRequest);
    taskContext.setHandler(this::handleTaskRequest);

    server.setExecutor(Executors.newFixedThreadPool(8));
    server.start();
}
```
This method sets up the server and assigns request handlers for the /status and /task endpoints.

#### Task Request Handling

```java
private void handleTaskRequest(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
        exchange.close();
        return;
    }

    Headers headers = exchange.getRequestHeaders();
    if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
        String dummyResponse = "123\n";
        sendResponse(dummyResponse.getBytes(), exchange);
        return;
    }

    boolean isDebugMode = headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true");

    long startTime = System.nanoTime();

    byte[] requestBytes = exchange.getRequestBody().readAllBytes();
    byte[] responseBytes = calculateResponse(requestBytes);

    long finishTime = System.nanoTime();

    if (isDebugMode) {
        String debugMessage = String.format("Operation took %d ns", finishTime - startTime);
        exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
    }

    sendResponse(responseBytes, exchange);
}
```
This method processes POST requests to the /task endpoint. It handles optional custom headers for test mode and debug mode and performs the calculation of the product of the provided numbers.

#### Response Calculation

```java
private byte[] calculateResponse(byte[] requestBytes) {
    String bodyString = new String(requestBytes);
    String[] stringNumbers = bodyString.split(",");

    BigInteger result = BigInteger.ONE;

    for (String number : stringNumbers) {
        BigInteger bigInteger = new BigInteger(number);
        result = result.multiply(bigInteger);
    }

    return String.format("Result of the multiplication is %s\n", result).getBytes();
}
```
This method performs the multiplication of the numbers provided in the POST request and returns the result.

#### Status Check Request Handling

```java
private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
        exchange.close();
        return;
    }

    String responseMessage = "Server is alive\n";
    sendResponse(responseMessage.getBytes(), exchange);
}
```
This method processes GET requests to the /status endpoint and returns a simple health check message.

#### Response Sending

```java
private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(200, responseBytes.length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(responseBytes);
    outputStream.flush();
    outputStream.close();
    exchange.close();
}
```
This utility method sends a response to the client, writing the provided data to the response body and closing the transaction.

### Usage

#### Starting the Server
To start the server, run the WebServer class. You can optionally pass a port number as an argument; otherwise, it defaults to 8080.

```bash
java WebServer 8081
```

#### Testing with cURL
You can test the HTTP server using the curl command-line tool.

Test the /status endpoint:
    
```bash
curl -X GET http://localhost:8081/status
```

Test the /task endpoint with data:

```bash
curl -X POST -d "2,3,4" http://localhost:8081/task
```

Test with X-Test header:

```bash
curl -X POST -H "X-Test: true" -d "2,3,4" http://localhost:8081/task
```

Test with X-Debug header:

```bash
curl -X POST -H "X-Debug: true" -d "2,3,4" http://localhost:8081/task
```

## HTTP Client with Task Aggregation

This documentation provides an overview of the implementation of an HTTP client in Java using the JDK 11 built-in `HttpClient`. The client asynchronously sends tasks to multiple worker instances and aggregates their responses. The functionality is also tested using Wireshark to analyze network traffic and confirm connection pooling.

## Features

- **Asynchronous HTTP Requests**: The HTTP client sends requests asynchronously to prevent blocking the thread while waiting for responses. The responses are handled by `CompletableFuture` which allows the results to be processed after all tasks have been sent.
- **Connection Pooling**: The HTTP client reuses existing connections to reduce the overhead of establishing new connections for every transaction. This is particularly useful when communicating with the same server multiple times.
- **Task Aggregation**: The implementation sends tasks to multiple worker nodes in parallel, retrieves the results, and aggregates them.

## Implementation Details

### 1. WebClient Class

The `WebClient` class is responsible for sending asynchronous HTTP requests. It uses `HttpClient` from JDK 11 to create and send HTTP requests.

```java
public class WebClient {
    private HttpClient client;

    public WebClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
```

**Key Components**:
- `HttpClient.newBuilder()`: Builds an HTTP client that uses HTTP/1.1.
- `sendAsync()`: Sends an HTTP request asynchronously, returning a CompletableFuture for the response.
- `POST Request`: Sends the task as a byte array to the provided URL.

### 2. Aggregator Class

The `Aggregator` class aggregates tasks and sends them to multiple worker servers, then collects the results using `CompletableFuture`.

```java
public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        for (int i = 0; i < workersAddresses.size(); i++) {
            String workerAddress = workersAddresses.get(i);
            String task = tasks.get(i);

            byte[] requestPayload = task.getBytes();
            futures[i] = webClient.sendTask(workerAddress, requestPayload);
        }

        List<String> results = Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());

        return results;
    }
}
```

**Key Components**:
- `sendTasksToWorkers()`: Sends tasks to multiple workers and aggregates their responses using CompletableFuture.
- `CompletableFuture.join()`: Waits for the task to complete and returns the result.

### 3. Application Class
The Application class is the entry point of the program. It defines the worker addresses and tasks, sends the tasks to workers, and prints the results.

```java
public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        String task1 = "10,200";
        String task2 = "123456789,100000000000000,700000002342343";

        List<String> results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                Arrays.asList(task1, task2));

        for (String result : results) {
            System.out.println(result);
        }
    }
}
```
**Key Components**:
- `WORKER_ADDRESS_1` and `WORKER_ADDRESS_2`: The addresses of two worker servers are specified, each listening on different ports.
- Tasks: Two tasks are defined and sent to the worker nodes for computation.
- Results: The results from the workers are printed after aggregation.

## Performance Considerations

- Asynchronous Communication: By using asynchronous requests (sendAsync()), the implementation avoids blocking the thread, allowing tasks to be sent to workers in parallel.
- Connection Pooling: Reusing connections reduces the overhead of establishing and closing connections for each HTTP transaction. HTTP/1.1 connection pooling is enabled by default in the JDK's HTTP client.
