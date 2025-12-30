---
title: "Firewalls: Packet Filter, Stateful, NGFW, and WAF"
draft: false
---

# Firewalls: Packet Filter, Stateful, NGFW, and WAF

Firewalls enforce policy on traffic flows. Effective deployments start with default‑deny, precise allow‑lists, layered inspection (L3–L7), strong logging, and automated governance to avoid rule creep and shadowed rules. This guide covers packet filters, stateful firewalls, next‑gen firewalls (NGFW), and how they differ from web application firewalls (WAF).

Cross‑refs:
- Segmentation & Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)
- IDS/IPS: [/docs/security/network-security/ids-ips.md](/docs/security/network-security/ids-ips.md)
- VPNs: [/docs/security/network-security/vpns.md](/docs/security/network-security/vpns.md)
- DDoS Mitigation: [/docs/security/network-security/ddos-mitigation.md](/docs/security/network-security/ddos-mitigation.md)
- API Security & WAF context: [/docs/security/application-security/api-security.md](/docs/security/application-security/api-security.md)

---

## Models and Capabilities

- Packet filtering (stateless, L3/L4)
  - Evaluates individual packets against ACLs (IP, port, protocol). Fast, simple; no connection tracking.
  - Use for very high‑throughput or simple deterministic rules (e.g., infrastructure control planes).
- Stateful firewalls (L3/L4 with connection tracking)
  - Track flows and allow return traffic dynamically (e.g., ephemeral ports). Default for host and cloud firewalls.
  - Safer by default (allow outbound, track state for inbound responses).
- Next‑Gen Firewalls (NGFW)
  - Add L7 application awareness, user identity, IPS features, TLS inspection, URL categorization.
  - Useful for egress control (domain categories), east‑west inspection, and threat prevention.
- Web Application Firewall (WAF)
  - L7 HTTP/HTTPS protections (OWASP rules, bot mitigation, virtual patching). Not a network firewall.
  - Deployed at edge or per‑app ingress; complements network firewalls.

Key principle
- Layered controls: perimeter allow‑listing + internal segmentation + host firewalls + WAF/IPS as needed. Avoid single choke point assumptions.

---

## Policy Design

- Default‑deny, explicit allow
  - Inbound: allow only specific apps/ports from specific sources. Outbound: constrain egress to needed destinations or domains.
- Least privilege and segmentation
  - Group resources by sensitivity/tenant/environment; define inter‑segment rules explicitly.
- Identity/context
  - Where available, bind rules to workload identity, tags, or security groups rather than IPs to reduce drift.
- Change control and recertification
  - Every rule has owner, ticket, expiry or review cadence; remove unused rules (detect via logs).
- Logging and evidence
  - Log accepts/denies at useful aggregation points; ship to SIEM; build coverage dashboards.

Common pitfalls
- Any‑any rules for “temporary fixes” that never expire.
- Over‑reliance on security through obscurity (random ports, NAT only).
- No egress control → data exfiltration paths and C2 possible.
- Shadowed rules and rule bloat causing unexpected exposure or outages.

---

## Cloud Firewalls (Concepts and Examples)

- AWS Security Groups (SG) and NACLs
  - SGs: stateful, attached to ENIs (instances, load balancers). Reference other SGs for identity‑like grouping.
  - NACLs: stateless, subnet‑level, ordered rules.
- GCP VPC Firewalls
  - Stateful, priority‑based allow/deny with tags/service accounts as selectors; hierarchical policies at org/folder.
- Azure NSGs and Azure Firewall
  - NSGs: stateful L3/L4 rules on subnets/NICs. Azure Firewall (managed NGFW) adds L7, FQDN/application rules.

Examples

AWS SG (allow only ALB to app on 8080)
```text
# Inbound (app-sg)
Type: TCP 8080
Source: sg-alb
Description: Allow traffic only from ALB

# Outbound (app-sg)
Type: TCP 443
Destination: 0.0.0.0/0
Description: Egress only HTTPS (restrict further via proxy where possible)
```

GCP firewall (allow only specific service account to DB)
```yaml
name: db-allow-app
direction: INGRESS
priority: 1000
targetServiceAccounts: ["app-sa@project.iam.gserviceaccount.com"]
sourceTags: ["app-tier"]
allowed:
- IPProtocol: tcp
  ports: ["5432"]
```

Azure NSG (deny all inbound except AppGW, allow required egress)
```text
Inbound:
  100 Allow_TCP_8080_From_AppGW  TCP 8080  Source: AppGWSubnet
  4096 Deny_All                   *   *     Source: *

Outbound:
  200 Allow_HTTPS_Out             TCP 443   Dest: Internet
  4096 Deny_All                   *   *
```

---

## Host Firewalls (Linux nftables/iptables, Windows Defender Firewall)

Linux nftables (preferred modern stack)
```bash
# Default deny inbound, allow established/related, allow SSH from mgmt, allow app port
nft add table inet filter
nft add chain inet filter input  '{ type filter hook input priority 0 ; policy drop ; }'
nft add rule inet filter input ct state established,related accept
nft add rule inet filter input iif lo accept
nft add rule inet filter input tcp dport 22 ip saddr 10.0.0.0/16 accept
nft add rule inet filter input tcp dport 8080 accept
```

Windows Defender Firewall (concept)
- Block inbound by default; allow only required ports; enforce per‑profile (Domain/Private/Public).
- Use GPO/Intune for centralized policy; log drops and successful connections.

Use cases
- Defense in depth: even if network layer misconfigures, host continues to enforce constraints.
- Narrow ports for management, agent communications, and egress to proxies.

---

## Egress Control

Why it matters
- Prevents data exfiltration and command‑and‑control (C2).
- Limits blast radius during compromise.

Techniques
- Allow‑list domain/FQDNs via NGFW or egress proxy; use TLS SNI/HTTP CONNECT inspection where policy allows.
- Private endpoints to cloud services (AWS PrivateLink, GCP PSC, Azure Private Endpoint) to avoid Internet routes.
- DNS policies: restrict to internal resolvers; monitor and block suspicious domains.

---

## TLS Inspection and Privacy

- TLS decryption on NGFW enables L7 inspection but introduces privacy, performance, and key management challenges.
- Prefer not to decrypt end‑user personal traffic; for egress from workloads, consider validation rather than bulk decryption (e.g., mTLS to pinned endpoints, certificate pinning).
- If used, segregate inspection zones, audit access, and maintain strong governance over trust stores.

---

## WAF vs Network Firewalls

- WAF focuses on HTTP semantics and application attacks (SQLi/XSS/CSRF patterns, virtual patching, bot defenses).
- Network firewalls enforce network flows (L3/L4) and may add L7 application ID but not full HTTP semantic analysis.
- Use both appropriately: WAF at edge or ingress; network firewalls at perimeters and segmentation boundaries.

---

## Change Management and Testing

- Staged rollout
  - Test rules in non‑prod; in prod use “log/alert” modes where supported before enforcement.
- Shadow rules
  - Analyze rule order and hits; remove duplicates or never‑hit rules.
- Synthetic testing
  - Run canaries to confirm required flows still pass; monitor latency and drops.

---

## Detections and Telemetry

- Collect: accept/deny logs, session duration, bytes counts, threats/IPS events (on NGFW), rule matches.
- Detect: new any‑any rules, sudden surge of denies on critical flows, unexpected new destinations, blocked exfil attempts.
- Feed: SIEM with context (rule id, app, owner) and dashboards for coverage and drift.

---

## Common Pitfalls

- Flat allow‑lists that grow unchecked (no owners/expiry).
- Inbound allows from 0.0.0.0/0 on admin ports (22/3389/5432/9200).
- No egress controls; workloads reach arbitrary Internet services.
- Missing logging or sampling too low to be actionable.
- TLS inspection deployed without governance or user consent where required.

---

## Checklist

- [ ] Default‑deny inbound at perimeter and host; explicit allow‑lists by app/tenant/env.
- [ ] Egress restricted to required domains/destinations; private endpoints to cloud services.
- [ ] Use identity/tag‑based rules where available (SG refs, service accounts, tags).
- [ ] Rule owners, tickets, and expiry dates tracked; unused rules removed regularly.
- [ ] Logging of accepts/denies shipped to SIEM; detections for risky changes and exfil attempts.
- [ ] WAF for HTTP/HTTPS apps; NGFW/IPS for egress and east‑west as needed.
- [ ] Host firewalls enabled; Kubernetes NetworkPolicies in place for pods.

---

## Cross‑References

- Segmentation & Zero Trust: [/docs/security/network-security/segmentation-zero-trust.md](/docs/security/network-security/segmentation-zero-trust.md)
- IDS/IPS: [/docs/security/network-security/ids-ips.md](/docs/security/network-security/ids-ips.md)
- VPNs: [/docs/security/network-security/vpns.md](/docs/security/network-security/vpns.md)
- DDoS: [/docs/security/network-security/ddos-mitigation.md](/docs/security/network-security/ddos-mitigation.md)
- API Security/WAF: [/docs/security/application-security/api-security.md](/docs/security/application-security/api-security.md)
