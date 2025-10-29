---
title: "Netstat and Nmap"
weight: 3
description: "System network statistics and port scanning tools"
---

# Netstat and Nmap

Netstat and Nmap are essential command-line tools for network analysis. Netstat provides detailed information about network connections, routing tables, and interface statistics, while Nmap offers comprehensive network discovery and port scanning capabilities.

## Netstat (Network Statistics)

### Basic Connection Information

**Show all connections:**
```bash
# Linux
netstat -a

# Windows
netstat -a
```

**Output includes:**
- **Proto:** Protocol (TCP/UDP)
- **Recv-Q/Send-Q:** Bytes in receive/send queues
- **Local Address:** Local IP and port
- **Foreign Address:** Remote IP and port
- **State:** Connection state

### Active Network Connections

**TCP connections only:**
```bash
# Linux
netstat -t

# Windows
netstat -p tcp
```

**UDP connections:**
```bash
netstat -u
```

**UNIX domain sockets:**
```bash
netstat -x
```

### Listening Ports

**Show listening ports:**
```bash
# Linux - numeric ports
netstat -ln

# Linux - resolve service names
netstat -l

# Windows
netstat -an | findstr LISTENING
```

**Find specific service:**
```bash
# Check if SSH is running
netstat -an | grep :22

# Check web servers
netstat -an | grep :80
netstat -an | grep :443
```

### Routing Table

**Display routing information:**
```bash
# Linux
netstat -r
route -n

# Windows
netstat -r
route print
```

**Route analysis:**
- **Destination:** Network destination
- **Gateway:** Next hop router
- **Genmask:** Network mask
- **Flags:** Route status (U=up, G=gateway, H=host route)
- **Iface:** Network interface

### Network Interface Statistics

**Interface information:**
```bash
# Linux
netstat -i

# Windows
netstat -e
```

**Detailed statistics:**
```bash
# Linux
netstat -s

# Windows
netstat -s | more
```

### Connection States

**TCP state meanings:**
| State         | Description                                 |
| ------------- | ------------------------------------------- |
| `LISTEN`      | Waiting for connection                      |
| `SYN_SENT`    | Sent SYN, waiting for SYN-ACK               |
| `SYN_RECV`    | Received SYN, sent SYN-ACK                  |
| `ESTABLISHED` | Connection established                      |
| `FIN_WAIT1`   | Sent FIN, waiting for FIN-ACK               |
| `FIN_WAIT2`   | Received FIN-ACK, waiting for FIN           |
| `CLOSE_WAIT`  | Received FIN, waiting for application close |
| `CLOSING`     | Both sides sent FIN simultaneously          |
| `TIME_WAIT`   | Waiting for network to clear connection     |
| `CLOSED`      | Connection closed                           |

### Security Analysis with Netstat

**Suspicious connections:**
```bash
# Check for unusual listening ports
netstat -lnp | grep -v -E '(127\.0\.0\.1|::1)'

# Find connections to suspicious IPs
netstat -anp | grep ESTABLISHED | grep -v 127.0.0.1

# Check for backdoors (listening on high ports)
netstat -lnp | awk 'NR>2 {print $7}' | grep -v "-" | sort -u
```

## Nmap (Network Mapper)

### Basic Host Discovery

**Simple ping scan:**
```bash
# Ping sweep
nmap -sn 192.168.1.0/24

# Disable port scan, just discover hosts
nmap -sn -PE 10.0.0.0/8
```

### Port Scanning

**TCP SYN scan (default stealth scan):**
```bash
nmap -sS target.com
```

**TCP connect scan:**
```bash
nmap -sT target.com
```

**UDP scan:**
```bash
nmap -sU target.com
```

**Common port ranges:**
```bash
# Scan top 1000 ports
nmap -sS --top-ports 1000 target.com

# Scan specific ports
nmap -p 20-25,80,443 target.com

# Scan all ports
nmap -p- target.com
```

### Service and Version Detection

**Service version detection:**
```bash
nmap -sV target.com
```

**Operating system detection:**
```bash
nmap -O target.com
```

**Aggressive scanning:**
```bash
nmap -A target.com
```

**Output includes:**
- Open ports
- Service names and versions
- OS details
- Traceroute information

### Advanced Scanning Techniques

**TCP SYN scan with timing:**
```bash
# Fast scanning
nmap -sS -T4 target.com

# Sneaky (slow and quiet)
nmap -sS -T2 target.com

# Paranoid (very slow)
nmap -sS -T1 target.com
```

**Firewall evasion techniques:**
```bash
# Fragment packets
nmap -f target.com

# Use decoy IPs
nmap -D decoy1,decoy2 target.com

# Spoof source IP
nmap -S spoofed_ip -e interface target.com
```

**Idle scan (bounces off zombie host):**
```bash
nmap -sI zombie_host target.com
```

### NSE (Nmap Scripting Engine)

**Script categories:**
```bash
# Vulnerability scanning
nmap --script vuln target.com

# Auth testing
nmap --script auth target.com

# Discovery scripts
nmap --script discovery target.com

# Run specific scripts
nmap --script http-enum,ftp-anon target.com
```

**Popular NSE scripts:**
```bash
# Heartbleed vulnerability
nmap -p 443 --script ssl-heartbleed target.com

# SMB vulnerabilities
nmap --script smb-vuln* target.com

# HTTP information gathering
nmap --script http-headers,http-methods target.com
```

### Output Formats

**Normal output:**
```bash
nmap -oN scan_results.txt target.com
```

**XML output (for parsing):**
```bash
nmap -oX scan_results.xml target.com
```

**Grepable output:**
```bash
nmap -oG scan_results.grep target.com
```

**All formats:**
```bash
nmap -oA basename target.com
```

### Subnet and Range Scanning

**CIDR notation:**
```bash
nmap -sn 192.168.1.0/24
```

**IP ranges:**
```bash
nmap 192.168.1.1-10
```

**Multiple targets:**
```bash
nmap target1.com target2.com target3.com

# Or from file
nmap -iL targets.txt
```

### Host Discovery Methods

**ARP discovery (local network):**
```bash
nmap -PR 192.168.1.0/24
```

**TCP SYN ping:**
```bash
nmap -PS80,443 target.com
```

**UDP ping:**
```bash
nmap -PU53 target.com
```

**ICMP echo:**
```bash
nmap -PE target.com
```

### Security Auditing

**Common audit scans:**
```bash
# Basic security check
nmap -sS -O -sV --script auth,vuln target.com > security_audit.txt

# Web server audit
nmap -p80,443 --script http-enum,http-vuln* target.com

# Database scan
nmap -p1433,3306 --script mysql-info,ms-sql-info target.com
```

### Performance Optimization

**Timing controls:**
```bash
# Minimum packet rate (packets per second)
nmap --min-rate 300 target.com

# Maximum RTT timeout
nmap --max-rtt-timeout 100ms target.com

# Initial RTT timeout
nmap --initial-rtt-timeout 50ms target.com

# Host timeout
nmap --host-timeout 15m target.com
```

## Network Troubleshooting Workflows

### Connection Issues

**Step 1: Check local network**
```bash
# Verify interface is up
ip addr show

# Check gateway reachability
ping 192.168.1.1

# Check DNS resolution
nslookup google.com
```

**Step 2: Check remote connectivity**
```bash
# Test port availability
nmap -p 80,443 google.com

# Check firewall rules
iptables -L
netsh advfirewall show currentprofile
```

### Service Discovery

**Find running services:**
```bash
# Linux
netstat -lnp | grep LISTEN

# Windows
netstat -ano | findstr LISTENING

# Cross-platform network scan
nmap -sS -sV localhost
```

### Performance Monitoring

**Ongoing connection monitoring:**
```bash
# Linux
watch -n 1 'netstat -t | wc -l'

# Show established connections
netstat -ant | awk '/ESTABLISHED/ {print $5}' | cut -d: -f1 | sort | uniq -c | sort -nr
```

### Security Monitoring

**Detect network anomalies:**
```bash
# Check for suspicious outbound connections
netstat -antp | grep ESTABLISHED | grep -v ':22\|:80\|:443'

# Scan for open ports that shouldn't be open
nmap -p- --open localhost | grep open
```

## Comparative Analysis

| Tool                  | Purpose                  | Netstat | Nmap                    |
| --------------------- | ------------------------ | ------- | ----------------------- |
| **Primary Use**       | Display connections      | ✓       | Port scanning           |
| **Port Info**         | Listening/specific ports | ✓       | All ports               |
| **Host Discovery**    | Local routing            | ✓       | Network sweeping        |
| **Service Detection** | Basic PID/Program        | Basic   | Advanced fingerprinting |
| **Security Scanning** | Basic anomaly detection  | Limited | Comprehensive           |
| **Invocation**        | Passive monitoring       | ✓       | Active scanning         |

Netstat provides real-time visibility into system network state, while Nmap offers proactive network reconnaissance and vulnerability assessment. Both tools are essential for comprehensive network administration and troubleshooting.
