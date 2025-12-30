---
title: "Least Privilege & Zero Trust"
draft: false
---

# Least Privilege & Zero Trust

Least privilege minimizes blast radius by granting only the permissions required, for only the duration needed, to only the identities that need them. Zero Trust extends this idea across the network and application layers: never trust by default; continuously verify identity, device posture, and context before authorizing every action.

This guide focuses on concrete implementation patterns for human and service access, policy design, automation, and operational guardrails.

---

## Core Principles

- Minimum necessary permissions
  - Scope access to the smallest resource set and action set that achieves the job.
- Time-bounded access (JIT)
  - Temporary elevation with automatic expiry; avoid standing privileges.
- Explicit approvals and auditable trails
  - Dual control for sensitive actions; immutable logs for all grants/denials.
- Continuous verification
  - Evaluate authZ on each request using identity + device posture + context (network, risk signals).
- Defense in depth
  - Combine identity-aware policy with network segmentation, mTLS, and application-level checks.

```mermaid
flowchart LR
  I[Identity (User/Service)] --> P[Policy Engine]
  D[Device Posture] --> P
  C[Context (Network/Time/Risk)] --> P
  P --> Z[Access Proxy / Gateway]
  Z --> S[Service / Data]
  S -.-> L[Logs / SIEM]
```

---

## Human Access Patterns

Just-in-time (JIT) elevation
- Self-service requests with approver workflows (Slack/JIRA chatops, access portals).
- Broker issues short-lived credentials/roles (e.g., 15–60 minutes) and auto-revokes.
- Scope elevation to specific resources (database X, namespace Y) and actions (read-only vs admin).

Privileged Access Management (PAM)
- Use PAM vaults for legacy systems; prefer ephemeral certs/credentials over static passwords.
- Enforce session recording and command restrictions for admin sessions.

Single Sign-On + MFA
- Enforce phishing-resistant MFA (WebAuthn/security keys) for all interactive admin access.
- Step-up MFA for high-risk operations (e.g., production data exports).

Device posture checks
- Gate access on MDM enrollment, disk encryption, screen lock, OS/patch level.
- Fail closed: deny or degrade access if posture signals are missing or stale.

---

## Service-to-Service Patterns

mTLS identity
- Assign each workload a verifiable identity (SPIFFE ID in X.509 SAN).
- Terminate/validate at the mesh or gateway; authorize based on identity, not IP.

Externalized authorization
- Use a centralized/pulled policy decision (OPA/Rego, Cedar) with per-request evaluation.
- Cache decisions with short TTL and include context (tenant, environment, time-of-day).

Short-lived tokens
- Prefer 5–15 minute access tokens with automated rotation (STS, OIDC).
- Use token exchange (RFC 8693) to mint scoped, audience-bound tokens; avoid passing end-user tokens downstream.

Network segmentation
- Default deny east–west; allow-list by identity-aware gateways; avoid implicit trust via private networks.
- Egress control and NAT gateways with domain allow-lists for sensitive services.

---

## Policy Design

Start with “deny-by-default.” Grant only the needed permissions explicitly. Partition policies by resource and tenant to avoid accidental cross-tenant access.

OPA/Rego example
```rego
package orders.authz

default allow = false

# Context includes user, device, and request attributes.
allow {
  input.request.method == "POST"
  input.request.path == ["v1", "orders"]
  input.user.scopes[_] == "orders:create"
  input.user.tenant == input.request.body.tenant
  input.device.compliant == true
}
```

Cedar example (Amazon Verified Permissions style)
```cedar
permit(
  principal in User,
  action in [Action::"orders.create"],
  resource in Order
)
when {
  principal.tenant == resource.tenant &&
  context.device.compliant == true
};
```

AWS IAM least-privilege snippet
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "ReadOnlySpecificBucketPrefix",
    "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:ListBucket"],
    "Resource": [
      "arn:aws:s3:::tenant-data",
      "arn:aws:s3:::tenant-data/acme/*"
    ],
    "Condition": { "StringEquals": { "aws:PrincipalTag/tenant": "acme" } }
  }]
}
```

---

## Zero Trust Access (ZTA) Architecture

Components
- Identity Provider (IdP): SSO, MFA, token issuance, device binding.
- Access Proxy/Gateway: Enforces policy at the edge (HTTP, TCP, SSH), injects identity to backends.
- Policy Decision Point (PDP): Evaluates rules (OPA/Rego, Cedar), returns allow/deny with reason.
- Policy Enforcement Points (PEP): API gateways, service mesh sidecars, DB proxies.
- Device Posture Service: Signals (MDM compliance, attestation, OS/patch).
- Telemetry: Centralized logs for decisions, failures, and anomalies.

Practices
- Authenticate every request; avoid trusted subnet exceptions.
- Bind sessions to device posture; re-evaluate on posture changes.
- Prefer application-layer authorization with identity claims over SG/NSG-only rules.

---

## Just-in-Time (JIT) Implementation

Workflow
1) User requests access with scope (resource, actions, TTL) and justification.
2) Approver approves (dual control for high risk).
3) Broker mints short-lived role/token/cert and grants time-boxed access.
4) Automatic expiry; revoke on incident signal. Full audit trail maintained.

Tooling options
- AWS: IAM Identity Center, Access Analyzer, STS AssumeRole with session tags; identity-aware proxies (Boundary, Teleport).
- GCP: IAM Conditions, Workload Identity Federation; IAP for application access.
- Azure: PIM (Privileged Identity Management), Managed Identities.

SSH with short-lived certs
- Use OpenSSH CA: sign user keys for minutes; restrict principals/commands via cert extensions.
- Enforce via AuthorizedPrincipalsFile and TrustedUserCAKeys.

---

## Device Posture Integration

Signals to consider
- MDM enrollment, disk encryption (FileVault/BitLocker), screen lock, firewall on.
- OS version and patch level; EDR running; secure/verified boot.
- Attestation: TPM-backed measurements (Windows/Linux), Secure Enclave (macOS/iOS).

Enforcement patterns
- Include device_id and compliant=true in session context and pass to PDP.
- Deny or downgrade (read-only) when posture is unknown or noncompliant.

---

## Observability and Operations

- Log all allow/deny decisions with inputs (redacted) and reasons.
- Alert on anomalies: privilege escalations, mass grants, access outside change windows.
- Periodic access reviews: expired roles removed; unused grants cleaned; simulate policies with “dry-run.”
- Game days: test JIT flows, CA rollover, PDP outages, and break-glass procedures.

---

## Common Pitfalls

- Standing admin privileges; no TTL or approvals.
- IP-based trust within “trusted” networks; no identity verification.
- Device posture not enforced for admins/contractors.
- Overly broad wildcard policies (“Action:*”, “Resource:*”) to “unblock” teams.
- Silent policy changes without review and versioning.

---

## Checklist

- [ ] Deny-by-default across services; explicit, minimal grants only.
- [ ] MFA for all interactive users; phishing-resistant for admins.
- [ ] JIT elevation with expiry and approvals; session recording for admin access.
- [ ] Workload identities and mTLS; per-request policy with PDP/PEP pattern.
- [ ] Short-lived tokens/credentials; automated rotation; token exchange for scoped access.
- [ ] Device posture checks enforced; access downgraded/blocked on noncompliance.
- [ ] Immutable logs of authZ decisions; anomaly detection and regular access reviews.
- [ ] Policy versioning, testing, and simulation before rollout.
