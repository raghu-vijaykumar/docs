---
title: "Segmentation & Zero Trust"
draft: false
---

# Segmentation & Zero Trust

Segmentation reduces blast radius by limiting where traffic can go. Zero Trust enforces that every request is authenticated and authorized regardless of network location. Combined, they turn flat networks into least‑privilege access graphs governed by identity and context rather than implicit trust.

This guide covers macro‑ and micro‑segmentation, identity‑aware policy, service mesh, and practical configs across cloud and Kubernetes.

---

## Why Segmentation Still Matters

- Limits lateral movement after initial compromise.
- Reduces scope of misconfigurations and credential leaks.
- Enables defense‑in‑depth: network controls complement identity and application controls.
- Provides natural choke points for detection and response.

Zero Trust extends segmentation
- “Never trust, always verify” for every hop.
- Decisions include identity (user/service), device posture, and context (time, risk, network).
- Network location alone is not a grant of access.

```mermaid
flowchart LR
  U[User/Service Identity] --> P[Policy Engine]
  D[Device Posture] --> P
  C[Context] --> P
  P --> A[Access Proxy / Gateway]
  A --> S1[Frontend Segment]
  A --> S2[Services Segment]
  S2 --> M[Service Mesh (mTLS + AuthZ)]
  M --> D1[(DB Segment)]
  M --> Q[(Queue Segment)]
```

---

## Macro‑Segmentation

Account/Project/Subscription and VPC/VNet boundaries
- Separate dev/stage/prod and tenants into different accounts/projects/subscriptions.
- Each environment has its own VPC/VNet with peering or transit gateways where necessary.

Subnet tiers
- Public edge (ALBs/ELBs/ingress), private services, and data subnets.
- Default‑deny routing between tiers; allow only specific flows (L4) with explicit rules.

Egress control
- Centralize egress via NAT or forward proxy.
- Allow‑list domains for sensitive workloads; block direct Internet where possible.

Example (AWS Security Groups)
```hcl
# Service SG allows inbound only from ALB SG on 443
resource "aws_security_group" "svc" {
  name = "svc"
  ingress {
    protocol  = "tcp"
    from_port = 443
    to_port   = 443
    security_groups = [aws_security_group.alb.id]
  }
  egress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Prefer egress via NAT + proxy allow-lists
  }
}
```

Example (Azure NSG)
```json
{
  "name": "allow-alb-to-svc-443",
  "properties": {
    "priority": 100,
    "access": "Allow",
    "direction": "Inbound",
    "protocol": "Tcp",
    "sourcePortRange": "*",
    "destinationPortRange": "443",
    "sourceApplicationSecurityGroups": ["asg-alb"],
    "destinationApplicationSecurityGroups": ["asg-svc"]
  }
}
```

---

## Micro‑Segmentation

Host‑level firewalls
- Linux nftables/iptables; Windows Defender Firewall.
- Default‑deny inbound; explicit per‑process/per‑port allow rules; log denies.

Cloud instance/workload identity
- Prefer identity‑based controls via service mesh or identity‑aware proxies over IP‑based “allow lists.”
- Bind rules to SPIFFE IDs or principal claims instead of brittle IPs.

Service Mesh (mTLS + AuthZ)
- mTLS between workloads with automatic certificate rotation.
- Per‑endpoint policies in the mesh using identity and attributes.

Istio/Envoy concept (DENY by default, allow specific identities)
```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: allow-orders-from-frontend
spec:
  selector:
    matchLabels:
      app: orders
  action: ALLOW
  rules:
  - from:
    - source:
        principals: ["spiffe://example.local/ns/prod/sa/frontend"]
    to:
    - operation:
        paths: ["/v1/orders"]
        methods: ["POST","GET"]
```

Kubernetes NetworkPolicy (default deny + explicit allow)
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: orders-deny-by-default
  namespace: prod
spec:
  podSelector:
    matchLabels:
      app: orders
  policyTypes: ["Ingress","Egress"]
  ingress: [] # deny all by default
  egress:   [] # deny all by default
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: orders-allow-frontend
  namespace: prod
spec:
  podSelector:
    matchLabels:
      app: orders
  policyTypes: ["Ingress"]
  ingress:
  - from:
    - namespaceSelector: { matchLabels: { name: "prod" } }
      podSelector: { matchLabels: { app: "frontend" } }
    ports:
    - protocol: TCP
      port: 8080
```

---

## Identity‑Aware Access Proxies

Access at the edge
- Place an identity‑aware proxy in front of internal web apps/APIs (e.g., BeyondCorp‑style proxies).
- Enforce user/service identity, device posture, and risk signals before allowing through.

Patterns
- OIDC/OAuth 2.1 for end‑users; mTLS for services.
- Externalized authorization via PDP (OPA/Cedar) with per‑request evaluation.

---

## Practical Design Rules

- Default‑deny everywhere (Security Groups/NSGs, NetworkPolicy, host firewalls, mesh policies).
- Authorize by identity and context; avoid long allow‑lists of IPs that drift.
- Keep perimeter rules simple; push fine‑grained authN/Z into the application/mesh layer.
- Centralize egress with DNS/domain allow‑lists for sensitive workloads.
- Monitor denies and unexpected allows; treat them as detection signals.

---

## Rollout Strategy

1) Inventory current flows
   - Capture L4 flows at gateways and host firewalls; build a baseline of required communications.
2) Establish defaults
   - Set default‑deny in non‑critical environments; add explicit allows iteratively.
3) Introduce identity
   - Deploy mTLS in the mesh for high‑risk paths; migrate policies from IPs to identities.
4) Progressive hardening
   - Tighten egress; restrict Internet access; enable DLP/logging on choke points.
5) Validate and monitor
   - Synthetic tests and canaries for critical paths; dashboards for denies and error rates.

---

## Observability and Incident Response

- Log allow/deny at all layers (cloud SG/NSG, mesh, gateway) with principal identity where possible.
- Alert on new, unexpected flows and on deny spikes to sensitive services.
- Maintain runbooks to quickly open temporary, auditable exceptions (time‑boxed).

---

## Common Pitfalls

- Relying solely on private networks as “trusted” zones.
- Exception creep: accumulating permanent rules for temporary needs.
- IP pinning in dynamic environments (autoscaling, Kubernetes).
- Lack of egress control allowing data exfiltration and C2 channels.
- Mesh policies that permit identity but ignore HTTP method/path (over‑broad).

---

## Checklist

- [ ] Separate accounts/projects and VPC/VNets per environment/tenant.
- [ ] Subnets by tier; default‑deny rules between tiers with explicit allows.
- [ ] Centralized egress via NAT/proxy with domain allow‑lists for sensitive apps.
- [ ] Host firewalls default‑deny; only necessary ports/services allowed.
- [ ] Service mesh enabling mTLS and identity‑aware authorization.
- [ ] Kubernetes NetworkPolicies (deny‑all + allow‑lists) on all namespaces.
- [ ] Identity‑aware edge proxy for internal apps; PDP/PEP for per‑request decisions.
- [ ] Logging of allow/deny with identity; alerts on anomalies and deny spikes.
- [ ] Process for time‑boxed exceptions with approvals and audits.

---

## Cross‑References

- Transport security: [TLS/SSL Configuration](/docs/security/cryptography/tls-ssl/)
- IAM foundations: [Least Privilege & Zero Trust](/docs/security/iam/least-privilege-zero-trust.md)
- Network overview: [Network Security](/docs/security/network-security/)
