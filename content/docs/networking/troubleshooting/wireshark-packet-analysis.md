---
title: "Wireshark Packet Analysis"
weight: 2
description: "Using Wireshark for packet capture and network protocol analysis"
---

# Wireshark Packet Analysis

Wireshark is a powerful open-source packet analyzer that captures network traffic in real-time and displays detailed protocol information. It enables deep inspection of network communications for troubleshooting, security analysis, and protocol development.

## Wireshark Fundamentals

### Installation and Setup

**On Ubuntu/Debian:**
```bash
# Install Wireshark with GUI
sudo apt update
sudo apt install wireshark

# Add user to wireshark group for non-root capture
sudo usermod -a -G wireshark $USER

# Re-login or run:
newgrp wireshark
```

**On Windows:**
```powershell
# Use official installer from wireshark.org
# Or via Chocolatey:
choco install wireshark
```

### Interface Selection

**List available interfaces:**
```bash
# Linux
sudo tshark -D

# Output:
1. eth0
2. wlan0
3. any
4. lo
5. vnet0

# Windows
tcpdump -D
```

### Basic Capture Start

**GUI Method:**
1. Launch Wireshark
2. Select network interface (e.g., eth0, Wi-Fi)
3. Click "Start" or press Ctrl+E
4. Generate some network traffic
5. Click "Stop" or press Ctrl+E

**Command Line (TShark):**
```bash
# Capture on eth0 interface
sudo tshark -i eth0

# Capture with output file
sudo tshark -i wlan0 -w capture.pcap

# Capture specific number of packets
sudo tshark -i eth0 -c 100 -w capture.pcap
```

## Packet Display and Filtering

### Display Filters

Display filters hide unwanted packets while maintaining the capture file:

```wireshark
# Common display filters
tcp                    # Show only TCP packets
udp                    # Show only UDP packets
http                   # Show HTTP traffic
dns                    # Show DNS queries/responses
ip.addr == 192.168.1.1 # Packets to/from specific IP
tcp.port == 80        # Packets on TCP port 80
tcp.stream eq 0       # Packets in TCP stream 0
```

### Capture Filters

Capture filters reduce what gets captured (more efficient):

```bash
# BPF (Berkeley Packet Filter) syntax
sudo tshark -i eth0 -f "port 80"          # Port 80 traffic only
sudo tshark -i eth0 -f "host 8.8.8.8"     # Traffic to/from 8.8.8.8 only
sudo tshark -i eth0 -f "tcp portrange 20-21"  # FTP ports
sudo tshark -i eth0 -f "not port 22"      # Exclude SSH
```

### Advanced Filter Examples

**Complex display filters:**
```wireshark
# HTTP traffic with GET requests
http.request and http.request.method == "GET"

# Failed TCP connections
tcp.flags.reset == 1 && tcp.flags.ack == 1

# DNS queries but not responses
dns.flags.response == 0

# Large packets (possible MTU issues)
frame.len > 1500

# Slow network traffic
tcp.time_delta > 0.1

# Encrypted traffic (port patterns)
tcp.port == 443 or tcp.port == 993 or tcp.port == 995
```

### Color Rules

Wireshark uses colors to highlight packet types:

**Default Color Rules:**
- **Light Purple:** TCP SYN, FIN, RST packets
- **Light Blue:** UDP packets
- **Light Green:** HTTP packets
- **Black/Green:** TCP packets (black outline = RST)
- **Red/Yellow:** Bad TCP (red = error, yellow/turquoise = retransmission)

## Protocol Analysis

### TCP Analysis

**Handshake Analysis:**
```wireshark
# Filter for three-way handshake
tcp.flags.syn == 1 or tcp.flags.syn == 1 and tcp.flags.ack == 1

# Identify specific connection
ip.src == 192.168.1.100 and ip.dst == 10.0.0.1 and tcp.port == 80
```

**Common TCP Issues:**
- **Retransmissions:** High counts indicate packet loss
- **Zero Window:** Receiver buffer full
- **Duplicate ACKs:** Missing packets detected
- **Reset Connections:** Abnormal termination

**TCP Stream Analysis:**
- Right-click a TCP packet → Follow → TCP Stream
- View complete conversation in ASCII/HEX
- Analyze HTTP requests, FTP commands, etc.

### HTTP Analysis

**HTTP Request/Response Patterns:**
```wireshark
# Successful responses
http.response.code >= 200 and http.response.code < 400

# Error responses
http.response.code >= 400

# Slow loading pages
http.time > 1.0

# Large downloads
http.content_length > 1000000
```

**HTTP Request Details:**
- Host, User-Agent, Content-Type headers
- Cookies and authentication tokens
- POST data and file uploads

### DNS Analysis

**DNS Query/Response Matching:**
```wireshark
# DNS queries
dns.flags.response == 0

# DNS responses
dns.flags.response == 1

# NXDOMAIN responses (not found)
dns.flags.rcode == 3

# Slow DNS responses
dns.time > 0.5
```

**DNS Troubleshooting:**
- Query types: A, AAAA, CNAME, MX, TXT
- Response codes: NOERROR, NXDOMAIN, SERVFAIL
- Recursive vs Iterative resolution patterns

### SSL/TLS Analysis

**Connection Analysis:**
```wireshark
# SSL handshake
ssl.handshake.type == 1 (Client Hello)
ssl.handshake.type == 2 (Server Hello)

# Certificate details
Right-click → Follow → SSL Stream

# TLS version negotiation
ssl.handshake.version
```

**Note:** Can't decrypt encrypted traffic without keys

## Statistical Analysis

### Protocol Hierarchy Statistics

**View → Statistics → Protocol Hierarchy**
- Shows percentage of traffic by protocol
- Identifies dominant protocols on network
- Helps spot unusual protocol usage

**Sample Output:**
```
Ethernet: 100% (1,234 frames)
  Internet Protocol Version 4: 95% (1,175 frames)
    Transmission Control Protocol: 85% (1,050 frames)
      Hypertext Transfer Protocol: 45% (556 frames)
      Secure Socket Layer: 30% (370 frames)
    User Datagram Protocol: 10% (124 frames)
      Domain Name System: 8% (99 frames)
  Address Resolution Protocol: 5% (62 frames)
```

### Conversations Window

**View → Statistics → Conversations**
- Top talkers on the network
- Port usage statistics
- Protocol distribution by endpoint

### I/O Graphs

**View → Statistics → I/O Graphs**
- Traffic patterns over time
- Identify peak usage periods
- Spot network bottlenecks

**Graph Types:**
```wireshark
# TCP issues over time
tcp.analysis.retransmission

# HTTP transfers
http.request.method == "GET"

# All traffic (bits per second)
frame.len * 8 / frame.time_delta
```

## Troubleshooting Common Issues

### Slow Network Performance

**Step-by-step analysis:**
```wireshark
# 1. Check overall traffic
# Use I/O graph for throughput patterns

# 2. Identify high-volume connections
# Statistics → Conversations → Sort by Bytes

# 3. Check for retransmissions
tcp.analysis.retransmission

# 4. Look for buffer issues
tcp.window_size == 0

# 5. DNS resolution problems
dns.time > 1.0
```

### Connectivity Problems

**TCP Connection Issues:**
```wireshark
# SYN packets with no response
tcp.flags.syn == 1 and not tcp.flags.ack == 1

# RST packets (connection resets)
tcp.flags.reset == 1

# Firewalled ports
tcp.flags.reset == 1 and tcp.ack == tcp.seq + 1
```

### Security Analysis

**Suspicious Traffic Patterns:**
```wireshark
# Horizontal scanning (brute force)
tcp.flags.syn == 1 and ip.ttl < 64

# Port scanning
tcp.flags.syn == 1 and tcp.window_size == 1024

# Data exfiltration
tcp.len > 1000 and small_packets_every_second
```

### Wireless Analysis

**Wi-Fi Troubleshooting:**
```wireshark
# Beacon frames
wlan.fc.type_subtype == 0x08

# Authentication frames
wlan.fc.type_subtype == 0x0b

# Deauthentication attacks
wlan.fc.type_subtype == 0x0c

# Signal strength analysis
wlan_radio.signal_dbm
```

## Command Line Analysis

### TShark for Automated Analysis

**Extract statistics:**
```bash
# Protocol breakdown
tshark -r capture.pcap -q -z io,phs > protocol_stats.txt

# Top talkers
tshark -r capture.pcap -T fields -e ip.src -e ip.dst | sort | uniq -c | sort -nr

# HTTP user agents
tshark -r capture.pcap -Y "http contains User-Agent" -T fields -e http.user_agent
```

### Shell Scripting with TShark

**Automated capture and analysis:**
```bash
#!/bin/bash
# Capture 60 seconds of traffic
timeout 60 tshark -i eth0 -w capture.pcap

# Extract HTTP requests with response times
tshark -r capture.pcap -Y "http.request" \
    -T fields -e frame.time_epoch -e http.request.uri -e http.request.method \
    > http_requests.csv
```

## Advanced Features

### Follow Streams

**Follow entire conversation:**
- Right-click packet → Follow → TCP Stream
- Right-click packet → Follow → HTTP Stream
- Useful for viewing complete transactions

### Expert Information

**View → Expert Information**
- **Errors:** Packet dissection errors
- **Warnings:** Protocol violations
- **Notes:** Informational messages
- **Chats:** Protocol request/response pairs

### Packet Reassembly

**Preferences → Protocols → TCP**
- Allow subdissector to reassemble TCP streams
- Enable analysis of segmented payloads

### Custom Columns

**Add custom display columns:**
- Right-click column header → Column Preferences
- Add columns like: tcp.stream, http.content_type, dns.qry.name

## Export and Reporting

### Export Packet Dissections

```bash
# Export to XML
tshark -r capture.pcap -T pdml > packets.xml

# Export to JSON
tshark -r capture.pcap -T json > packets.json

# Export specific fields to CSV
tshark -r capture.pcap -T fields \
    -e frame.number -e frame.time -e ip.src -e ip.dst -e tcp.port \
    -E header=y -E separator=, > packets.csv
```

### Generate Reports

**Create summary report:**
```bash
#!/bin/bash
echo "Packet Capture Report" > report.txt
echo "====================" >> report.txt
echo "Total packets: $(tshark -r capture.pcap -r 2>/dev/null | wc -l)" >> report.txt
echo "Date range: $(tshark -r capture.pcap -T fields -e frame.time 2>/dev/null | head -1) to $(tshark -r capture.pcap -T fields -e frame.time 2>/dev/null | tail -1)" >> report.txt
tshark -r capture.pcap -q -z conv,ip >> report.txt
```

Wireshark's extensive packet analysis capabilities make it indispensable for network engineers, security analysts, and developers debugging network issues. Its deep protocol dissection and filtering options provide the visibility needed to understand complex network behaviors.
