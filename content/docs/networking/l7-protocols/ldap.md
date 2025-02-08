---
weight: 4
bookFlatSection: true
title: "LDAP"
draft: false
---

# LDAP (Lightweight Directory Access Protocol) Documentation

## Overview
LDAP (Lightweight Directory Access Protocol) is a protocol used to access and manage directory services over a network. It is widely used for authentication, authorization, and directory lookups.

## Features of LDAP
- **Hierarchical Structure**: Uses a tree-like structure to organize directory entries.
- **Efficient Searches**: Optimized for searching user and resource information.
- **Authentication & Authorization**: Supports user authentication and role-based access.
- **Standardized Protocol**: Works with various directory services like OpenLDAP and Microsoft Active Directory.

## LDAP Structure
LDAP directories use a **tree hierarchy**, known as the **Directory Information Tree (DIT)**, which consists of:
- **Root Entry**: The base of the directory tree.
- **Organizational Units (OUs)**: Groups of related objects.
- **Entries**: Individual records such as users, groups, and devices.

## LDAP Operations
| Operation | Description                                |
| --------- | ------------------------------------------ |
| BIND      | Authenticate a client with the LDAP server |
| SEARCH    | Query directory entries based on filters   |
| ADD       | Add a new entry to the directory           |
| MODIFY    | Update an existing entry                   |
| DELETE    | Remove an entry from the directory         |
| UNBIND    | Close the connection to the server         |

## LDAP Authentication Sequence
{{< mermaid >}}
sequenceDiagram
    participant Client
    participant LDAP_Server
    Client->>LDAP_Server: BIND (Authenticate)
    LDAP_Server-->>Client: Success/Failure Response
    Client->>LDAP_Server: SEARCH (Find User Info)
    LDAP_Server-->>Client: User Details
    Client->>LDAP_Server: UNBIND (Close Connection)
{{< /mermaid >}}

## Active Directory and LDAP
**Active Directory (AD)** is Microsoft's implementation of LDAP with additional functionality. It is commonly used for enterprise authentication and identity management.

### Key Features of Active Directory:
- **Domain Controllers (DCs)**: Central servers managing authentication and directory services.
- **Group Policy Management**: Allows administrators to enforce security and configuration policies.
- **Single Sign-On (SSO)**: Enables users to log in once and access multiple resources.
- **Kerberos & NTLM Authentication**: Provides secure authentication mechanisms.
- **Integration with Windows Environments**: Seamlessly works with Windows-based networks.

### Active Directory Authentication Sequence
{{< mermaid >}}
sequenceDiagram
    participant User
    participant Client
    participant Domain_Controller
    participant LDAP_Server
    User->>Client: Enter Credentials
    Client->>Domain_Controller: Authenticate using Kerberos/NTLM
    Domain_Controller-->>Client: Authentication Success
    Client->>LDAP_Server: Search User Information
    LDAP_Server-->>Client: User Details
    Client->>User: Access Granted
{{< /mermaid >}}


## LDAP vs Active Directory
| Feature        | LDAP                                    | Active Directory                                        |
| -------------- | --------------------------------------- | ------------------------------------------------------- |
| Protocol       | Open standard                           | Microsoft-specific implementation of LDAP               |
| Authentication | Supports simple and SASL authentication | Uses Kerberos and NTLM                                  |
| Structure      | Hierarchical (DIT)                      | Hierarchical with additional features like Group Policy |
| Platform       | Cross-platform                          | Primarily Windows-based                                 |
| Use Case       | Authentication & directory lookups      | Enterprise identity management                          |

## Conclusion
LDAP is a widely used protocol for directory services, enabling efficient authentication and authorization. Microsoft Active Directory extends LDAP with enterprise features, making it the preferred choice for Windows-based networks.
