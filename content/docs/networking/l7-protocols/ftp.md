---
weight: 2
bookFlatSection: true
title: "FTP & SFTP"
draft: false
---


# FTP & SFTP Protocol Documentation

## Overview
FTP (File Transfer Protocol) and SFTP (Secure File Transfer Protocol) are used for transferring files between a client and a server.
- **FTP** is a standard protocol used for transferring files over a network but lacks security.
- **SFTP** is a secure version of FTP that operates over **SSH (Secure Shell)**, ensuring encryption and secure file transfers.

## Features of FTP & SFTP
- **FTP**
  - Uses two separate channels: **Command Channel** (for sending commands) and **Data Channel** (for transferring files).
  - Supports **Active** and **Passive** modes for data transmission.
  - Authentication is done using plaintext usernames and passwords (can be encrypted using FTPS).
- **SFTP**
  - Uses a **single encrypted channel** for both commands and data.
  - Provides **strong encryption** using SSH keys or password authentication.
  - Supports **resuming interrupted transfers** and **file integrity checks**.

## FTP & SFTP Sequence Diagram

{{< mermaid >}}

sequenceDiagram
    participant Client
    participant FTP_Server
    participant SFTP_Server

    Client->>FTP_Server: FTP Request (AUTH, USER, PASS)
    FTP_Server-->>Client: FTP Response (230 Login Successful)
    Client->>FTP_Server: File Transfer (STOR, RETR)
    FTP_Server-->>Client: Transfer Complete

    Client->>SFTP_Server: SFTP Request (SSH Authentication)
    SFTP_Server-->>Client: Authentication Success
    Client->>SFTP_Server: Secure File Transfer
    SFTP_Server-->>Client: Transfer Complete

{{< /mermaid >}}

## Common FTP & SFTP Commands
| Command    | Description                      |
| ---------- | -------------------------------- |
| USER       | Send username for authentication |
| PASS       | Send password for authentication |
| LIST       | List files in the directory      |
| RETR       | Retrieve a file from the server  |
| STOR       | Upload a file to the server      |
| DELE       | Delete a file on the server      |
| MKD        | Create a new directory           |
| RMD        | Remove a directory               |
| PWD        | Print working directory          |
| SFTP GET   | Download a file securely         |
| SFTP PUT   | Upload a file securely           |
| SFTP RM    | Delete a file securely           |
| SFTP CHMOD | Change file permissions securely |

## FTP vs SFTP
| Feature         | FTP                              | SFTP                         |
| --------------- | -------------------------------- | ---------------------------- |
| Security        | No encryption                    | Encrypted with SSH           |
| Port            | 21 (control), 20 (data)          | 22                           |
| Authentication  | Plaintext                        | SSH keys or password         |
| Data Protection | Vulnerable to MITM attacks       | Secure against eavesdropping |
| Performance     | Faster due to lack of encryption | Slower due to encryption     |

## Conclusion
FTP and SFTP are essential for file transfers, but SFTP is the **preferred** choice for secure environments due to its encryption and security mechanisms. Organizations handling sensitive data should use **SFTP over FTP** to ensure data protection and integrity.
