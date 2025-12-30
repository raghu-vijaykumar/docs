---
title: "Network Security"
weight: 3
bookCollapseSection: true
draft: false
---

# Network Security

Network security hardens connectivity layers so that only intended traffic can traverse, is inspected appropriately, and is resilient against abuse. This section focuses on practical architectures, control placement, and configuration patterns that complement application and identity layers.

Use these guides to build defense‑in‑depth: segmentation to reduce blast radius, secure protocols to protect data in transit, detection/prevention at choke points, and DDoS resilience at the edge.

---

## Core Topics

- Firewalls
  - L3/L4 packet filters vs stateful inspection vs Next‑Gen Firewalls (NGFW).
  - Placement: Internet edge, DC/Cloud edge, east‑west, workload firewalls.
  - Cross‑reference device fundamentals: see [Networking › Devices › Firewalls](/docs/networking/devices/firewalls/).
- IDS/IPS
  - Signature and anomaly detection; inline prevention vs out‑of‑band detection.
  - Tuning to reduce false positives; logging to SIEM with clear triage runbooks.
- VPN
  - IPsec vs TLS VPN; site‑to‑site vs remote access; split‑tunnel trade‑offs.
  - Device posture, MFA, and just‑in‑time access for zero trust alignment.
- Segmentation & Zero Trust
  - Macro‑segmentation (VPC/VNET, subnets) and micro‑segmentation (SGs/NSGs, host firewalls, service mesh).
  - Policy by identity and context, not only IPs; continuous verification.
- Secure Protocols
  - SSH, HTTPS/TLS, SFTP/SCP; cipher and version hardening; certificate hygiene.
- DDoS Mitigation
  - Edge scrubbing (CDN/Anti‑DDoS), rate limiting, autoscaling, connection limits, and upstream coordination.

---

## Architecture Overview

```mermaid
flowchart LR
  U[Users / Clients] -->|HTTPS| E[Edge (CDN/WAF/DDoS)]
  E --> G[Gateway / Load Balancer]
  G --> Z[Zero Trust / Access Proxy]
  Z --> S1[Segmentation: Frontend Subnet]
  Z --> S2[Segmentation: Services Subnet]
  S2 --> M[Service Mesh / mTLS]
  M --> D[(Data Stores)]
  E -.->|Telemetry| SIEM[SIEM/Logs]
  G -.->|Metrics| MON[Monitoring]
```

Key points
- Terminate TLS at trusted edges with modern configs; prefer end‑to‑end TLS with mTLS internally.
- Apply least‑privilege network ACLs between tiers; default‑deny east‑west.
- Centralize egress with controlled NAT/proxies and allow‑list destinations for sensitive workloads.

---

## Practical Guidance

Segmentation
- Start with macro segments per sensitivity level; add micro‑segmentation for workloads handling secrets or critical operations.
- Use identity‑aware policies (e.g., service accounts, SPIFFE IDs) via service mesh to avoid IP sprawl brittleness.

Perimeter and internal firewalls
- Default deny; explicitly allow ports/protocols; log denies with sufficient metadata (src/dst, port, proto, user/role if available).
- Keep rule sets small and auditable; group by application and ownership; automate validation tests.

VPN and remote access
- Enforce MFA and device posture checks.
- Prefer application‑layer access brokers (zero trust) over network‑wide VPN access where feasible.

Secure protocols
- Follow [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/) for cipher and version guidance.
- SSH: key‑only auth, disable root login and password auth, restrict with ForceCommand/Match blocks, rotate keys regularly.

DDoS mitigation
- Use provider‑grade protection (AWS Shield Advanced, Cloud Armor, Azure DDoS Protection) and CDN fronting.
- Implement request‑level rate limits and circuit breakers; plan for graceful degradation.

---

## Cross‑References

- Firewalls device fundamentals: [Networking › Devices › Firewalls](/docs/networking/devices/firewalls/)
- Load balancers and DDoS angles: [Networking › Devices › Load Balancers](/docs/networking/devices/load-balancers/)
- Cryptography and TLS: [Security › Cryptography › TLS/SSL](/docs/security/cryptography/tls-ssl/)
- Identity‑aware micro‑segmentation: see forthcoming [Segmentation & Zero Trust](segmentation-zero-trust.md)

---

## Where to Go Next

- Firewalls: [firewalls.md](firewalls.md)
- IDS/IPS: [ids-ips.md](ids-ips.md)
- VPNs: [vpn.md](vpn.md)
- Segmentation & Zero Trust: [segmentation-zero-trust.md](segmentation-zero-trust.md)
- Secure Protocols: [secure-protocols.md](secure-protocols.md)
- DDoS Mitigation: [ddos-mitigation.md](ddos-mitigation.md)
