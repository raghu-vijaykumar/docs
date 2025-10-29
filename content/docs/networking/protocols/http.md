---
weight: 1
bookFlatSection: true
title: "HTTP/s"
draft: false
---


# HTTP & HTTPS Protocol

## Overview
HTTP (Hypertext Transfer Protocol) and HTTPS (Hypertext Transfer Protocol Secure) are the foundations of communication on the web. 
- **HTTP** is a **stateless**, **request-response** protocol used by web browsers and servers to exchange data.
- **HTTPS** is the secure version of HTTP, which encrypts data using **TLS (Transport Layer Security)** to protect against interception and attacks.

## Features of HTTP & HTTPS
- **Stateless**: Each request is independent, and the server does not retain information about previous interactions.
- **Connectionless**: The client initiates a request, and after receiving the response, the connection is closed.
- **Media Independence**: HTTP/HTTPS can transfer various data formats like HTML, JSON, XML, images, and videos.
- **Security (HTTPS only)**: Encrypts data using **TLS** to prevent eavesdropping and ensure secure communication.
- **Methods**: Defines request methods such as GET, POST, PUT, DELETE, etc.
- **Status Codes**: Responses include status codes to indicate success, errors, or redirection (e.g., 200 OK, 404 Not Found, 500 Internal Server Error).

## HTTP Request Structure
An HTTP/HTTPS request consists of:
- **Request Line**: Contains the method, resource URL, and HTTP version.
- **Headers**: Provide metadata such as content type, user agent, and authorization.
- **Body**: Contains the actual data (used in POST/PUT requests).

## HTTP Response Structure
An HTTP/HTTPS response consists of:
- **Status Line**: Contains the HTTP version, status code, and status message.
- **Headers**: Provide metadata such as content type and server details.
- **Body**: Contains the actual response data (HTML, JSON, etc.).

## HTTP & HTTPS Sequence Diagram

{{< mermaid >}}

sequenceDiagram
    participant Client
    participant HTTP_Server
    participant HTTPS_Server

    Client->>HTTP_Server: HTTP Request (GET /index.html)
    HTTP_Server-->>Client: HTTP Response (200 OK, HTML content)

    Client->>HTTPS_Server: HTTPS Request (GET /secure-data)
    HTTPS_Server-->>Client: HTTPS Response (200 OK, Encrypted content)
{{< /mermaid >}}

## Common HTTP Methods
| Method  | Description                                  |
| ------- | -------------------------------------------- |
| GET     | Retrieve data from the server                |
| POST    | Send data to the server to create a resource |
| PUT     | Update an existing resource                  |
| DELETE  | Remove a resource from the server            |
| PATCH   | Partially update a resource                  |
| HEAD    | Retrieve headers without body                |
| OPTIONS | Retrieve allowed HTTP methods                |

## HTTP vs HTTPS
| Feature         | HTTP                  | HTTPS                                                |
| --------------- | --------------------- | ---------------------------------------------------- |
| Security        | No encryption         | Encrypted using TLS                                  |
| Port            | 80                    | 443                                                  |
| Data Protection | Vulnerable to attacks | Secure from MITM attacks                             |
| SEO Ranking     | Lower                 | Higher (preferred by search engines)                 |
| Trust Level     | Less trusted          | More trusted (indicated by padlock icon in browsers) |

## HTTPS Versions and Improvements
| Version            | Key Improvements                                                                                                                                          |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **TLS 1.0 (1999)** | Introduced as a replacement for SSL 3.0, but vulnerable to BEAST attack and deprecated in modern systems.                                                 |
| **TLS 1.1 (2006)** | Added protection against CBC attacks and improved security, but still considered weak and deprecated.                                                     |
| **TLS 1.2 (2008)** | Introduced support for stronger encryption algorithms, perfect forward secrecy, and authenticated encryption (AEAD). Still widely used today.             |
| **TLS 1.3 (2018)** | Major performance improvements with faster handshakes, removal of weak ciphers, enhanced privacy with 0-RTT resumption, and stronger security by default. |


## Conclusion
HTTP and HTTPS are the backbone of web communication, providing mechanisms for data transfer between clients and servers. While HTTP is still in use, HTTPS is the **preferred and recommended** protocol for secure communication, with TLS 1.3 offering the best security and performance.