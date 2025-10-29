---
weight: 2
bookCollapseSection: true
title: "Application layer protocols"
draft: false
---

# Application Layer Protocols

The **Application Layer** (Layer 7) in the OSI model provides network services directly to end users and applications.

## Common Application Layer Protocols

| **Protocol**   | **Full Form**                         | **Port(s)**              | **Function**                                                                    |
| -------------- | ------------------------------------- | ------------------------ | ------------------------------------------------------------------------------- |
| **HTTP**       | Hypertext Transfer Protocol           | 80                       | Transfers web pages and resources from web servers to browsers.                 |
| **HTTPS**      | Hypertext Transfer Protocol Secure    | 443                      | Secure version of HTTP using **TLS encryption** to protect data.                |
| **FTP**        | File Transfer Protocol                | 21 (Control), 20 (Data)  | Transfers files between client and server.                                      |
| **SFTP**       | Secure File Transfer Protocol         | 22                       | Secure version of FTP using **SSH encryption**.                                 |
| **SMTP**       | Simple Mail Transfer Protocol         | 25, 465 (SSL), 587 (TLS) | Sends emails from client to mail servers.                                       |
| **IMAP**       | Internet Message Access Protocol      | 143, 993 (SSL)           | Retrieves emails from the mail server while keeping them stored.                |
| **POP3**       | Post Office Protocol v3               | 110, 995 (SSL)           | Retrieves emails and **downloads them locally** (removes them from the server). |
| **DNS**        | Domain Name System                    | 53 (UDP/TCP)             | Resolves domain names to IP addresses.                                          |
| **DHCP**       | Dynamic Host Configuration Protocol   | 67 (Server), 68 (Client) | Assigns **IP addresses** dynamically to devices.                                |
| **SNMP**       | Simple Network Management Protocol    | 161 (Agent), 162 (Trap)  | Monitors and manages network devices.                                           |
| **Telnet**     | -                                     | 23                       | Remote login to another system **without encryption** (not secure).             |
| **SSH**        | Secure Shell                          | 22                       | Secure remote login and command execution.                                      |
| **RDP**        | Remote Desktop Protocol               | 3389                     | GUI-based remote access for Windows systems.                                    |
| **NTP**        | Network Time Protocol                 | 123 (UDP)                | Synchronizes time between computers.                                            |
| **LDAP**       | Lightweight Directory Access Protocol | 389, 636 (SSL)           | Manages authentication and access control (e.g., **Active Directory**).         |
| **MQTT**       | Message Queuing Telemetry Transport   | 1883, 8883 (SSL)         | Lightweight messaging protocol for **IoT devices**.                             |
| **CoAP**       | Constrained Application Protocol      | 5683, 5684 (DTLS)        | Similar to MQTT, optimized for **low-power IoT devices**.                       |
| **BitTorrent** | -                                     | Dynamic Ports            | Peer-to-peer (P2P) file sharing.                                                |
| **SMB**        | Server Message Block                  | 445                      | File and printer sharing in Windows environments.                               |
| **TFTP**       | Trivial File Transfer Protocol        | 69 (UDP)                 | Lightweight FTP version used for **network booting and firmware updates**.      |
| **Gopher**     | -                                     | 70                       | Early web-like document retrieval system (rarely used today).                   |

## Quick Insights
- **Web Browsing** → HTTP (80), HTTPS (443)
- **File Transfers** → FTP (21, 20), SFTP (22), TFTP (69)
- **Email Services** → SMTP (25, 465, 587), IMAP (143, 993), POP3 (110, 995)
- **Network Services** → DNS (53), DHCP (67, 68), SNMP (161, 162)
- **Remote Access** → SSH (22), RDP (3389), Telnet (23)
- **IoT & Messaging** → MQTT (1883, 8883), CoAP (5683, 5684)