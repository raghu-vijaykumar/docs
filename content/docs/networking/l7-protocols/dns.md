---
weight: 3
bookFlatSection: true
title: "DNS & DoH"
draft: false
---

# DNS & DNS over HTTPS (DoH) Documentation

## Overview
DNS (Domain Name System) is a hierarchical system that translates human-readable domain names (e.g., `example.com`) into IP addresses (e.g., `192.0.2.1`). DNS over HTTPS (DoH) enhances security by encrypting DNS queries using HTTPS.

## How DNS Works
1. A client requests the IP address of a domain (e.g., `example.com`).
2. The request is sent to a **Recursive DNS Resolver**.
3. The resolver queries the **Root DNS Server**.
4. The request is passed down to **TLD (Top-Level Domain) Server** (e.g., `.com`).
5. The request reaches the **Authoritative DNS Server**.
6. The server returns the corresponding IP address to the client.

## DNS Record Types
| Record Type | Description                                          |
| ----------- | ---------------------------------------------------- |
| A           | Maps a domain to an IPv4 address                     |
| AAAA        | Maps a domain to an IPv6 address                     |
| CNAME       | Alias for another domain                             |
| MX          | Specifies mail servers for the domain                |
| TXT         | Stores arbitrary text data (e.g., SPF records)       |
| NS          | Lists authoritative name servers for the domain      |
| PTR         | Maps an IP address to a domain (reverse lookup)      |
| SOA         | Provides administrative information about the domain |

## DNS Query Sequence Diagram
{{< mermaid >}}
sequenceDiagram
    participant Client
    participant Resolver
    participant Root_Server
    participant TLD_Server
    participant Auth_Server

    Client->>Resolver: Request IP for example.com
    Resolver->>Root_Server: Query example.com
    Root_Server-->>Resolver: Refer to .com TLD Server
    Resolver->>TLD_Server: Query example.com
    TLD_Server-->>Resolver: Refer to Authoritative Server
    Resolver->>Auth_Server: Query example.com
    Auth_Server-->>Resolver: IP address 192.0.2.1
    Resolver-->>Client: IP address 192.0.2.1
{{< /mermaid >}}

## What is DNS over HTTPS (DoH)?
DoH encrypts DNS queries using HTTPS, preventing DNS spoofing and eavesdropping.

### How DoH Works
1. A client sends a DNS request over an **HTTPS connection** instead of plain UDP.
2. The request is sent to a **DoH-compatible resolver** (e.g., Google, Cloudflare).
3. The resolver fetches the response via standard DNS mechanisms.
4. The encrypted response is sent back to the client.

### DNS vs DoH
| Feature     | DNS                        | DNS over HTTPS (DoH)              |
| ----------- | -------------------------- | --------------------------------- |
| Encryption  | No                         | Yes (TLS/HTTPS)                   |
| Protocol    | UDP/TCP                    | HTTPS (TCP port 443)              |
| Privacy     | Vulnerable to ISP tracking | Secure against eavesdropping      |
| Performance | Faster (UDP-based)         | Slightly slower due to encryption |

## DoH Sequence Diagram
{{< mermaid >}}

sequenceDiagram
    participant Client
    participant DoH_Resolver
    participant DNS_Servers

    Client->>DoH_Resolver: Encrypted DNS Query (HTTPS)
    DoH_Resolver->>DNS_Servers: Standard DNS Query
    DNS_Servers-->>DoH_Resolver: DNS Response
    DoH_Resolver-->>Client: Encrypted DNS Response (HTTPS)

{{< /mermaid >}}

## Conclusion
DNS is critical for resolving domain names, but it is susceptible to interception and manipulation. DoH mitigates these risks by encrypting DNS queries, enhancing privacy and security. While DoH is more secure, traditional DNS remains faster for large-scale applications.
